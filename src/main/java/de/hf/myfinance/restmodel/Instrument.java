package de.hf.myfinance.restmodel;


public class Instrument {
    private final Integer instrumentid;
    private final String description;
    private final String serviceAddress;

    public Instrument(Integer instrumentid, String description, String serviceAddress){
        this.instrumentid = instrumentid;
        this.description = description;
        this.serviceAddress =serviceAddress;
    }


    public Integer getInstrumentid() {
        return instrumentid;
    }

    public String getDescription() {
        return description;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

}