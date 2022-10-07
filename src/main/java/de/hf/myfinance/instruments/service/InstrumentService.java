package de.hf.myfinance.instruments.service;

import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class InstrumentService {
    private final InstrumentFactory instrumentFactory;

    @Autowired
    public InstrumentService(InstrumentFactory instrumentFactory){
        this.instrumentFactory = instrumentFactory;
    }

    public Mono<Instrument> getInstrument(String businesskey) {
        var instrumentHandler = instrumentFactory.getInstrumentHandlerForExistingInstrument(businesskey);
        return instrumentHandler.loadInstrument();
    }

    public Mono<String> addInstrument(Instrument instrument) {

        var instrumentHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(instrument.getInstrumentType(), instrument.getDescription(), instrument.getParentBusinesskey(), instrument.getBusinesskey());
        instrumentHandler.setValues(instrument);
        return instrumentHandler.save();
    }

    public Flux<Instrument> listInstruments() {
        return instrumentFactory.listInstruments();
    }

    public Flux<Instrument> listTenants(){
        return instrumentFactory.listTenants();
    }

    public Flux<Instrument> listInstruments(String tenantkey){
        return instrumentFactory.getTenantHandler(tenantkey).listInstrumentChilds();
    }

    public Flux<Instrument> listActiveInstruments(String tenantkey) {
        return instrumentFactory.getTenantHandler(tenantkey).listActiveInstrumentChilds();
    }

    public Flux<Instrument> listInstrumentsByType(String tenantkey, InstrumentType instrumentType) {
        return instrumentFactory.getTenantHandler(tenantkey).listInstrumentChilds(instrumentType, true);
    }
}
