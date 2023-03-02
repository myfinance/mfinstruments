package de.hf.myfinance.instruments.api;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.InstrumentService;
import de.hf.myfinance.restapi.InstrumentApi;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import de.hf.framework.utils.ServiceUtil;
import de.hf.myfinance.restmodel.Instrument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class InstrumentApiImpl implements InstrumentApi {
    ServiceUtil serviceUtil;
    InstrumentService instrumentService;

    @Value("${api.common.version}")
    String apiVersion;

    @Autowired
    public InstrumentApiImpl(InstrumentService instrumentService, ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
        this.instrumentService = instrumentService;
    }

    @Override
    public String index() {
        return "Hello my InstrumentService version:"+apiVersion;
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
    public Flux<Instrument> listInstruments() {
        return instrumentService.listInstruments();
    }

    @Override
    public Flux<Instrument> listInstrumentsForTenant(String tenantbusinesskey) {
        return instrumentService.listInstruments(tenantbusinesskey);
    }

    @Override
    public Flux<Instrument> listActiveInstrumentsForTenant(String tenantbusinesskey) {
        return instrumentService.listActiveInstruments(tenantbusinesskey);
    }

    @Override
    public Flux<Instrument> listInstrumentsByType(String tenantbusinesskey, InstrumentType instrumentType) {
        return instrumentService.listInstrumentsByType(tenantbusinesskey, instrumentType);
    }

    @Override
    public Flux<Instrument> listTenants() {
        return instrumentService.listTenants();
    }

    @Override
    public Mono<String> saveInstrument(Instrument instrument) {
        return instrumentService.saveInstrument(instrument);
    }

    private Instrument setServiceAddress(Instrument e) {
        e.setServiceAddress(serviceUtil.getServiceAddress());
        return e;
    }
}