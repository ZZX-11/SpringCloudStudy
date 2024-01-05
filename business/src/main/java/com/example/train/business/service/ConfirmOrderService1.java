package com.example.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.example.train.business.domain.*;
import com.example.train.business.enums.ConfirmOrderStatusEnum;
import com.example.train.business.enums.RedisKeyPreEnum;
import com.example.train.business.enums.SeatColEnum;
import com.example.train.business.enums.SeatTypeEnum;
import com.example.train.business.mapper.ConfirmOrderMapper;
import com.example.train.business.req.ConfirmOrderDoReq;
import com.example.train.business.req.ConfirmOrderQueryReq;
import com.example.train.business.req.ConfirmOrderSaveReq;
import com.example.train.business.req.ConfirmOrderTicketReq;
import com.example.train.business.resp.ConfirmOrderQueryResp;
import com.example.train.common.context.LoginMemberContext;
import com.example.train.common.exception.BusinessException;
import com.example.train.common.exception.BusinessExceptionEnum;
import com.example.train.common.resp.PageResp;
import com.example.train.common.util.SnowUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SkTokenService skTokenService;

//    @Autowired
//    private RedissonClient redissonClient;
    @Transactional
//    @SentinelResource(value = "doConfirm", blockHandler = "doConfirmBlock")
    public void doConfirm(ConfirmOrderDoReq req) {
//      省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过
//      保存确认订单表，状态初始

//      校验令牌余量 令牌大闸
//      令牌大闸有两个作用，1.帮我们提前校验库存。2.防机器人刷票   LoginMemberContext.getId()  一个人一段时间只能拿一次令牌
//        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), LoginMemberContext.getId());
//        if (validSkToken) {
//            LOG.info("令牌校验通过");
//        } else {
//            LOG.info("令牌校验不通过");
//            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
//        }


//      分布式锁
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(req.getDate()) + "-" + req.getTrainCode();
//      setIfAbsent就是对应redis的setnx
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 10, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(setIfAbsent)) {
            LOG.info("恭喜，抢到锁了！lockKey：{}", lockKey);
        } else {
            // 只是没抢到锁，并不知道票抢完了没，所以提示稍候再试
             LOG.info("很遗憾，没抢到锁！lockKey：{}", lockKey);

             LOG.info("没抢到锁，有其它消费线程正在出票，不做任何处理");
             throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
//            return;
        }

//         RLock lock = null;
//         // 使用redisson，自带看门狗
//         lock = redissonClient.getLock(lockKey);
//         // 不带看门狗
//         // boolean tryLock = lock.tryLock(30, 10, TimeUnit.SECONDS);
//         // 申请不到锁就会等待30s，申请到了锁就会锁10s。
//
//        boolean tryLock = false; // 带看门狗
//        try {
//            tryLock = lock.tryLock(0, TimeUnit.SECONDS);
//        // 等待时间为0，锁时长不配了，因为自带看门狗，根据线程的结束而释放锁
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        if (tryLock) {
//             LOG.info("恭喜，抢到锁了！");
//             // 可以把下面这段放开，只用一个线程来测试，看看redisson的看门狗效果
//             // for (int i = 0; i < 30; i++) {
//             //     Long expire = redisTemplate.opsForValue().getOperations().getExpire(lockKey);
//             //     LOG.info("锁过期时间还有：{}", expire);
//             //     Thread.sleep(1000);
//             // }
//        } else {
//             // 只是没抢到锁，并不知道票抢完了没，所以提示稍候再试
//             LOG.info("很遗憾，没抢到锁");
//             throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
//        }
        try {
            DateTime now = DateTime.now();

            List<ConfirmOrderTicketReq> passengerTickets = req.getTickets();

            ConfirmOrder confirmOrder = getAndInsertConfirmOrder(req, now, passengerTickets);
//          查出余票记录，需要得到真实的库存
            DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(req);
            if (dailyTrainTicket == null) {
//           所选车次已经卖完
                throw new BusinessException(BusinessExceptionEnum.Ticket_Not_Exist);
            } else {
                LOG.info("查出余票记录：{}", dailyTrainTicket);
            }

//          余票预扣减
            reduceTickets(req,dailyTrainTicket);
//     每一次选座 仅支持同一种座位类型
            ConfirmOrderTicketReq ticketReq0 = passengerTickets.get(0);
            String seatTypeCode = ticketReq0.getSeatTypeCode();
            if (StrUtil.isNotBlank(ticketReq0.getSeat())) {
                LOG.info("有选座！！");
            } else {
                LOG.info("无选座！！");
//              随机挑几个座位即可
                List<DailyTrainSeat> dailyTrainSeats = selectForNoneChoose(req, passengerTickets, seatTypeCode, dailyTrainTicket);
                LOG.info("选中座位:{}", dailyTrainSeats);
                try {
                    afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, dailyTrainSeats, passengerTickets, confirmOrder);
                } catch (Exception e) {
                    LOG.error("保存购票信息失败", e);
                    throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
                }
            }
        } finally {
            LOG.info("购票流程结束，释放锁！lockKey：{}", lockKey);
            redisTemplate.delete(lockKey);
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

    private static void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        int Ticket_nums = req.getTickets().size();
//        for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
        String seatTypeCode = req.getTickets().get(0).getSeatTypeCode();
        SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
        switch (seatTypeEnum) {
            case YDZ -> {
                int countLeft = dailyTrainTicket.getYdz() - Ticket_nums;
                if (countLeft < 0) {
                    throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                }
                dailyTrainTicket.setYdz(countLeft);
            }
            case EDZ -> {
                int countLeft = dailyTrainTicket.getEdz() - Ticket_nums;
                if (countLeft < 0) {
                    throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                }
                dailyTrainTicket.setEdz(countLeft);
            }
            case RW -> {
                int countLeft = dailyTrainTicket.getRw() - Ticket_nums;
                if (countLeft < 0) {
                    throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                }
                dailyTrainTicket.setRw(countLeft);
            }
            case YW -> {
                int countLeft = dailyTrainTicket.getYw() - Ticket_nums;
                if (countLeft < 0) {
                    throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                }
                dailyTrainTicket.setYw(countLeft);
            }
        }
//        }
    }

}
