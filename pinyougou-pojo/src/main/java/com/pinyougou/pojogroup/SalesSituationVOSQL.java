package com.pinyougou.pojogroup;

import java.io.Serializable;

public class SalesSituationVOSQL implements Serializable {

    private String startTime;
    private String endTime;
    private Long goodsId;

    public SalesSituationVOSQL(String startTime, String endTime, Long goodsId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.goodsId = goodsId;
    }

    public SalesSituationVOSQL() {
    }

    @Override
    public String toString() {
        return "SalesSituationVOSQL{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", goodsId=" + goodsId +
                '}';
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
