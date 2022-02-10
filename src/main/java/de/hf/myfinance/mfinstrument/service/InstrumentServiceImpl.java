package de.hf.myfinance.mfinstrument.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import de.hf.framework.utils.ServiceUtil;
import de.hf.myfinance.restapi.InstrumentService;
import de.hf.myfinance.restmodel.Instrument;

@RestController
public class InstrumentServiceImpl implements InstrumentService {
    ServiceUtil serviceUtil;

    @Autowired
    public InstrumentServiceImpl(ServiceUtil serviceUtil) {
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