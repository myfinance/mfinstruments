package de.hf.myfinance.instruments.api;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.InstrumentService;
import de.hf.myfinance.restapi.InstrumentApi;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import de.hf.framework.utils.ServiceUtil;
import de.hf.myfinance.restmodel.Instrument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Flux<Instrument> listInstruments() {
        return instrumentService.listInstruments();
    }

    @Override
    public Flux<Instrument> listInstrumentsForTenant(String businesskey) {
        return instrumentService.listInstruments(businesskey);
    }

    @Override
    public Flux<Instrument> listTenants() {
        return instrumentService.listTenants();
    }

    @Override
    public Mono<Instrument> saveInstrument(Instrument instrument) {
        return instrumentService.addInstrument(instrument).map(e -> setServiceAddress(e));
    }

    private Instrument setServiceAddress(Instrument e) {
        e.setServiceAddress(serviceUtil.getServiceAddress());
        return e;
    }
}