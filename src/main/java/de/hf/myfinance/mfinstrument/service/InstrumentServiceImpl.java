package de.hf.myfinance.mfinstrument.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import de.hf.myfinance.restapi.InstrumentService;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.utils.ServiceUtil;

@RestController
public class InstrumentServiceImpl implements InstrumentService {
    ServiceUtil serviceUtil;

    @Autowired
    public InstrumentServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public String index() {
        return "Hello my spring";
    }

    @Override
    public Instrument getProduct(int instrumentId) {
        return new Instrument(instrumentId, "name-" + instrumentId, serviceUtil.getServiceAddress());
    }

}