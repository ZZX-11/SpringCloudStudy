package com.example.train.business.mapper.cust;

import java.util.Date;

public interface DailyTrainTicketMapperCust {

    void updateCountBySell(Date date
            , String trainCode
            , String seatTypeCode
            , Integer trainStart
            , Integer trainEnd);
}
