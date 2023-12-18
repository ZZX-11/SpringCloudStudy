package com.example.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.example.train.business.domain.*;
import com.example.train.business.enums.ConfirmOrderStatusEnum;
import com.example.train.business.enums.SeatColEnum;
import com.example.train.business.enums.SeatTypeEnum;
import com.example.train.business.mapper.ConfirmOrderMapper;
import com.example.train.business.req.ConfirmOrderDoReq;
import com.example.train.business.req.ConfirmOrderQueryReq;
import com.example.train.business.req.ConfirmOrderSaveReq;
import com.example.train.business.req.ConfirmOrderTicketReq;
import com.example.train.business.resp.ConfirmOrderQueryResp;
import com.example.train.common.exception.BusinessException;
import com.example.train.common.exception.BusinessExceptionEnum;
import com.example.train.common.resp.PageResp;
import com.example.train.common.util.SnowUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfirmOrderService1 {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService1.class);

    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;


    @Transactional
    @SentinelResource(value = "doConfirm", blockHandler = "doConfirmBlock")
    public void doConfirm(ConfirmOrderDoReq req) {
//      省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过
//      保存确认订单表，状态初始
         DateTime now = DateTime.now();

         List<ConfirmOrderTicketReq> passengerTickets = req.getTickets();

        ConfirmOrder confirmOrder = getAndInsertConfirmOrder(req, now, passengerTickets);
//      查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(req);
        if(dailyTrainTicket == null){
//           所选车次已经卖完
            throw new BusinessException(BusinessExceptionEnum.Ticket_Not_Exist);
        }else{
            LOG.info("查出余票记录：{}", dailyTrainTicket);
        }
//     每一次选座 仅支持同一种座位类型
        ConfirmOrderTicketReq ticketReq0 = passengerTickets.get(0);
        String seatTypeCode = ticketReq0.getSeatTypeCode();
        if (StrUtil.isNotBlank(ticketReq0.getSeat())) {
            LOG.info("有选座！！");
        }else {
            LOG.info("无选座！！");
//          随机挑两个座位即可
            List<DailyTrainSeat> dailyTrainSeats = selectForNoneChoose(req, passengerTickets, seatTypeCode, dailyTrainTicket);
            LOG.info("选中座位:{}", dailyTrainSeats);
            try {
                afterConfirmOrderService.afterDoConfirm(dailyTrainTicket,dailyTrainSeats,passengerTickets,confirmOrder);
            } catch (Exception e) {
                LOG.error("保存购票信息失败", e);
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
            }
        }
    }

    public void doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOG.info("购票请求被限流：{}", req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }

    private ConfirmOrder getAndInsertConfirmOrder(ConfirmOrderDoReq req, DateTime now, List<ConfirmOrderTicketReq> tickets) {
        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();

        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setMemberId(req.getMemberId());

        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);

        confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setTickets(JSON.toJSONString(tickets));
        confirmOrderMapper.insert(confirmOrder);
        return confirmOrder;
    }

    private List<DailyTrainSeat> selectForNoneChoose(ConfirmOrderDoReq req, List<ConfirmOrderTicketReq> PassengerTickets, String seatType, DailyTrainTicket dailyTrainTicket){
//      随机选中合适的位置
//      对于所有车厢的所有座位
        List<DailyTrainSeat> getSeatList = new ArrayList<>();
        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode,seatType);
        int size = PassengerTickets.size();
        int flag = 0;
//          对每一个票都选座
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            LOG.info("开始从车厢{}选座", dailyTrainCarriage.getIndex());

            List<DailyTrainSeat> dailyTrainSeats = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOG.info("车厢{}的座位数：{}", dailyTrainCarriage.getIndex(), dailyTrainSeats.size());

            for (DailyTrainSeat dailyTrainSeat:dailyTrainSeats){
                String sell = dailyTrainSeat.getSell();
                String sellPart = getSubstring(sell,dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
                if(! sellPart.contains("1") && flag < size ){
//                 没人买过，选中该座位，加入list待买
//                 所有随机选坐都一定能成功
                    String Sold = replaceInRange(sell, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
                    dailyTrainSeat.setSell(Sold);
                    getSeatList.add(dailyTrainSeat);
                    flag=flag+1;
                }
            }
        }
//      买的不够，抛出异常
        if (getSeatList.isEmpty() || getSeatList.size() != PassengerTickets.size()){
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
        }

        return getSeatList;
    }

    private List<DailyTrainSeat> selectForChoose(ConfirmOrderDoReq req, ConfirmOrderTicketReq PassengerTicket, String seatType, DailyTrainTicket dailyTrainTicket){
//      随机选中合适的位置
//      对于所有车厢的所有座位
        List<DailyTrainSeat> getSeatList = new ArrayList<>();
        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        List<String> seatList = req.getTickets().stream()
                .map(ticket -> ticket.getSeat())
                .collect(Collectors.toList());
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode,seatType);
//      根据seat 和seat type判断 seat_index 应该余多少
//      因为可选座位仅能在两排的范围内选，所以 先看第一
//        for(String seat:seatList){
////            if (seat)
//        }

        return getSeatList;
    }


    public static String getSubstring(String str, int start, int end) {
        if (start >= 0  && start <= end) {
            return str.substring(start-1, end-1);
        } else {
            return "位置输入无效";
        }
    }

    public static String replaceInRange(String str, int start, int end) {
//        StringBuilder replacedString = new StringBuilder(str);
        String str1="";
        int length = str.length();
        String str2 = str.substring(0, start-1);
        String str3 = str.substring(end-1, length);

        str1 = getSubstring(str, start, end).replaceAll(".","1");
        return str2+str1+str3;
    }
}
