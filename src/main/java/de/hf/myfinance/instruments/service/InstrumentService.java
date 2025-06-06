package de.hf.myfinance.instruments.service;

import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.myfinance.restmodel.LiquidityType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class InstrumentService {
    private final InstrumentFactory instrumentFactory;

    public InstrumentService(InstrumentFactory instrumentFactory){
        this.instrumentFactory = instrumentFactory;
    }

    public Mono<Instrument> getInstrument(String businesskey) {
        return instrumentFactory.loadInstrument(businesskey).map(this::resolveLiquidityType);
    }

    public Mono<String> saveInstrument(Instrument instrument) {
        return instrumentFactory.getInstrumentHandler(instrument).save();
    }

    public Flux<Instrument> listInstruments() {
        return instrumentFactory.listInstruments().map(this::resolveLiquidityType);
    }

    public Flux<Instrument> listTenants(){
        return instrumentFactory.listTenants();
    }

    public Flux<Instrument> listInstruments(String tenantkey){
        return instrumentFactory.getTenantHandler(tenantkey).listInstrumentChilds().map(this::resolveLiquidityType);
    }

    public Flux<Instrument> listActiveInstruments(String tenantkey) {
        return instrumentFactory.getTenantHandler(tenantkey).listActiveInstrumentChilds().map(this::resolveLiquidityType);
    }

    public Flux<Instrument> listInstrumentsByType(String tenantkey, InstrumentType instrumentType) {
        return instrumentFactory.getTenantHandler(tenantkey).listInstrumentChilds(instrumentType, true).map(this::resolveLiquidityType);
    }

    public Flux<Instrument> listAccounts(String tenantkey){
        return instrumentFactory.getTenantHandler(tenantkey).getActiveAccounts().map(this::resolveLiquidityType);
    }

    public Flux<Instrument> listAllAccounts(String tenantkey){
        return instrumentFactory.getTenantHandler(tenantkey).getAccounts().map(this::resolveLiquidityType);
    }

    public Flux<Instrument> listBudgets(String tenantkey){
        return instrumentFactory.getTenantHandler(tenantkey).getActiveBudgets().map(this::resolveLiquidityType);
    }

    public Flux<Instrument> listAllBudgets(String tenantkey){
        return instrumentFactory.getTenantHandler(tenantkey).getBudgets().map(this::resolveLiquidityType);
    }

    public Flux<Instrument> getIncomeBudgets(String tenantbusinesskey) {
        return instrumentFactory.getTenantHandler(tenantbusinesskey).getIncomeBudgets();
    }

    private Instrument resolveLiquidityType(Instrument instrument){
        if(instrument.getLiquidityTypeCalculated()){
            var properties = instrument.getAdditionalProperties();
            if(properties.containsKey(AdditionalProperties.MATURITYDATE)){
                var maturityDateString = properties.get(AdditionalProperties.MATURITYDATE);    
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate maturityDate = LocalDate.parse(maturityDateString, formatter);        
                var monthToMaturity = ChronoUnit.MONTHS.between(LocalDate.now(), maturityDate);
                if(monthToMaturity<3){
                    instrument.setLiquidityType(LiquidityType.SHORTTERM);
                } else if(monthToMaturity<12){
                    instrument.setLiquidityType(LiquidityType.MIDTERM);
                } else {
                    instrument.setLiquidityType(LiquidityType.LONGTERM);
                }
            }
        }
        return instrument;
    }
}
