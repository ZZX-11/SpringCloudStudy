package com.example.train.business.req;

import com.example.train.common.req.PageReq;

public class TrainStationQueryReq extends PageReq {
    private String trainCode;

    public TrainStationQueryReq(String trainCode) {
        this.trainCode = trainCode;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    @Override
    public String toString() {
        return "TrainStationQueryReq{" +
                "trainCode='" + trainCode + '\'' +
                "} " + super.toString();
    }
}
