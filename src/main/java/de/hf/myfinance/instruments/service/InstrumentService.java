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

    public Mono<String> saveInstrument(Instrument instrument) {
        return instrumentFactory.getInstrumentHandler(instrument).save();
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

    public Flux<Instrument> listAccounts(String tenantkey){
        return instrumentFactory.getTenantHandler(tenantkey).getAccounts();
    }

    public Flux<Instrument> listBudgets(String tenantkey){
        return instrumentFactory.getTenantHandler(tenantkey).getBudgets();
    }
}
