package de.hf.myfinance.instruments.api;

import de.hf.myfinance.instruments.service.InstrumentService;
import de.hf.myfinance.restapi.InstrumentApi;
import de.hf.myfinance.restmodel.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import de.hf.framework.utils.ServiceUtil;
import de.hf.myfinance.restmodel.Instrument;

import java.time.LocalDateTime;

@RestController
public class InstrumentApiImpl implements InstrumentApi {
    ServiceUtil serviceUtil;
    InstrumentService instrumentService;

    @Autowired
    public InstrumentApiImpl(InstrumentService instrumentService, ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
        this.instrumentService = instrumentService;
    }

    @Override
    public String index() {
        return "Hello my InstrumentService";
    }

    @Override
    public Instrument getInstrument(int instrumentId) {
        var tenant = new Tenant("testkey", "desc", true);
        tenant.setServiceAddress(serviceUtil.getServiceAddress());
        tenant.setTreelastchanged(LocalDateTime.now());
        return tenant;
    }

    @Override
    public void saveTenant(Tenant tenant) {
        instrumentService.saveTenant(tenant);
    }

}