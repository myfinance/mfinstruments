package de.hf.myfinance.instruments.api;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.InstrumentService;
import de.hf.myfinance.restapi.InstrumentApi;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import de.hf.framework.utils.ServiceUtil;
import de.hf.myfinance.restmodel.Instrument;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.List;

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
    public Mono<Instrument> getInstrument(String businesskey) {
        try{
            var instrument = instrumentService.getInstrument(businesskey).map(e -> setServiceAddress(e));
            return instrument;
        } catch(MFException e) {
            throw e;
        }
        catch(Exception e) {
            throw new MFException(MFMsgKey.UNSPECIFIED, e.getMessage());
        }
    }

    @Override
    public Instrument getInstrumentBlocking(String businesskey) {
        try{
            var instrument = instrumentService.getInstrument(businesskey).block();
            instrument.setServiceAddress(serviceUtil.getServiceAddress());
            return instrument;
        } catch(MFException e) {
            throw e;
        }
        catch(Exception e) {
            throw new MFException(MFMsgKey.UNSPECIFIED, e.getMessage());
        }
    }

    @Override
    public List<Instrument> listInstruments() {
        return instrumentService.listInstruments();
    }

    @Override
    public List<Instrument> listInstrumentsForTenant(String businesskey) {
        return instrumentService.listInstruments(businesskey);
    }

    @Override
    public List<Instrument> listTenants() {
        return instrumentService.listTenants();
    }

    @Override
    public void addInstrument(Instrument instrument) {
        if(instrument.getInstrumentType().equals(InstrumentType.TENANT)) {
            instrumentService.newTenant(instrument.getDescription());
        }
    }

    @Override
    public void updateInstrument(Instrument instrument) {
        instrumentService.updateInstrument(instrument.getDescription(), instrument.getBusinesskey(), instrument.isIsactive());
    }
    private Instrument setServiceAddress(Instrument e) {
        e.setServiceAddress(serviceUtil.getServiceAddress());
        return e;
    }
}