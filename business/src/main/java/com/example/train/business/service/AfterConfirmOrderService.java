package com.example.train.business.service;

import com.example.train.business.domain.ConfirmOrder;
import com.example.train.business.domain.DailyTrainSeat;
import com.example.train.business.domain.DailyTrainTicket;
import com.example.train.business.enums.ConfirmOrderStatusEnum;
//import com.example.train.business.feign.MemberFeign;
import com.example.train.business.feign.MemberFeign;
import com.example.train.business.mapper.ConfirmOrderMapper;
import com.example.train.business.mapper.DailyTrainSeatMapper;
//import com.example.train.business.mapper.cust.DailyTrainTicketMapperCust;
import com.example.train.business.mapper.DailyTrainTicketMapper;
import com.example.train.business.mapper.cust.DailyTrainTicketMapperCust;
import com.example.train.business.req.ConfirmOrderTicketReq;
//import com.example.train.common.req.MemberTicketReq;
import com.example.train.common.req.MemberTicketReq;
import com.example.train.common.resp.CommonResp;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AfterConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    @Resource
    private DailyTrainTicketMapperCust dailyTrainTicketMapperCust;

    @Resource
    private MemberFeign memberFeign;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    /**
     * 选中座位后事务处理：
     *  座位表修改售卖情况sell；
     *  余票详情表修改余票；
     *  为会员增加购票记录
     *  更新确认订单为成功
     */
//     @Transactional
     @GlobalTransactional
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> finalSeatList, List<ConfirmOrderTicketReq> tickets, ConfirmOrder confirmOrder) throws Exception {
        LOG.info("seata全局事务ID: {}", RootContext.getXID());
        for (int j = 0; j < finalSeatList.size(); j++) {
            DailyTrainSeat dailyTrainSeat = finalSeatList.get(j);
//          更新每日座位表
            updateDailyTrainSeat(dailyTrainSeat);

            ConfirmOrderTicketReq confirmOrderTicketReq = tickets.get(j);
//          更新乘客的订单表
            CommonResp<Object> commonResp = saveMemberTicket(dailyTrainTicket, confirmOrder, dailyTrainSeat,confirmOrderTicketReq);
            LOG.info("调用member接口，返回：{}", commonResp);

//          更新订单状态为成功
            String Status = ConfirmOrderStatusEnum.SUCCESS.getCode();
            updateConfirmOrderStatus(confirmOrder,Status);
//          更新每日票数
            dailyTrainTicketMapperCust.updateCountBySell(
                    dailyTrainSeat.getDate(),
                    dailyTrainSeat.getTrainCode(),
                    dailyTrainSeat.getSeatType(),
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex());
//            Thread.sleep(10000);
        }
        LOG.info("成功！！！");

//         throw new Exception("严重异常");
    }
    private void updateConfirmOrderStatus(ConfirmOrder confirmOrder,String Status) {
        ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
        confirmOrderForUpdate.setId(confirmOrder.getId());
        confirmOrderForUpdate.setUpdateTime(new Date());
        confirmOrderForUpdate.setStatus(Status);
        confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderForUpdate);
    }

    private CommonResp<Object> saveMemberTicket(DailyTrainTicket dailyTrainTicket, ConfirmOrder confirmOrder, DailyTrainSeat dailyTrainSeat,ConfirmOrderTicketReq ticket) {
        MemberTicketReq memberTicketReq = new MemberTicketReq();
        memberTicketReq.setMemberId(confirmOrder.getMemberId());
        memberTicketReq.setPassengerId(ticket.getPassengerId());
        memberTicketReq.setPassengerName(ticket.getPassengerName());

        memberTicketReq.setCarriageIndex(dailyTrainSeat.getCarriageIndex());
        memberTicketReq.setSeatRow(dailyTrainSeat.getRow());
        memberTicketReq.setSeatCol(dailyTrainSeat.getCol());
        memberTicketReq.setSeatType(dailyTrainSeat.getSeatType());

        memberTicketReq.setTrainDate(dailyTrainTicket.getDate());
        memberTicketReq.setTrainCode(dailyTrainTicket.getTrainCode());
        memberTicketReq.setStartStation(dailyTrainTicket.getStart());
        memberTicketReq.setStartTime(dailyTrainTicket.getStartTime());
        memberTicketReq.setEndStation(dailyTrainTicket.getEnd());
        memberTicketReq.setEndTime(dailyTrainTicket.getEndTime());

        CommonResp<Object> commonResp = memberFeign.save(memberTicketReq);
        return commonResp;
    }

    private DailyTrainSeat updateDailyTrainSeat(DailyTrainSeat dailyTrainSeat) {
        DailyTrainSeat seatForUpdate = new DailyTrainSeat();
        seatForUpdate.setId(dailyTrainSeat.getId());
        seatForUpdate.setSell(dailyTrainSeat.getSell());
        seatForUpdate.setUpdateTime(new Date());
        dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);
        return seatForUpdate;
    }


    private static void extracted(DailyTrainTicket dailyTrainTicket, DailyTrainSeat seatForUpdate) {
        Integer startIndex = dailyTrainTicket.getStartIndex();
        Integer endIndex = dailyTrainTicket.getEndIndex();
        char[] chars = seatForUpdate.getSell().toCharArray();
        Integer maxStartIndex = endIndex - 1;
        Integer minEndIndex = startIndex + 1;
        Integer minStartIndex = 0;
        for (int i = startIndex - 1; i >= 0; i--) {
            char aChar = chars[i];
            if (aChar == '1') {
                minStartIndex = i + 1;
                break;
            }
        }
        LOG.info("影响出发站区间：" + minStartIndex + "-" + maxStartIndex);

        Integer maxEndIndex = seatForUpdate.getSell().length();
        for (int i = endIndex; i < seatForUpdate.getSell().length(); i++) {
            char aChar = chars[i];
            if (aChar == '1') {
                maxEndIndex = i;
                break;
            }
        }
        LOG.info("影响到达站区间：" + minEndIndex + "-" + maxEndIndex);
    }
}
