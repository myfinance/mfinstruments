package de.hf.myfinance.instruments.api;

import de.hf.myfinance.restapi.InstrumentApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import de.hf.framework.utils.ServiceUtil;
import de.hf.myfinance.restmodel.Instrument;

@RestController
public class InstrumentApiImpl implements InstrumentApi {
    ServiceUtil serviceUtil;

    @Autowired
    public InstrumentApiImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public String index() {
        return "Hello my InstrumentService";
    }

    @Override
    public Instrument getInstrument(int instrumentId) {
        return new Instrument(instrumentId, "name-" + instrumentId, serviceUtil.getServiceAddress());
    }

}