package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.myfinance.restmodel.LiquidityType;
import reactor.core.publisher.Mono;

public class BudgetHandler extends AbsAccountHandler {

    public BudgetHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.BUDGETGROUP;
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUDGET;
    }

    @Override
    protected Mono<Instrument> setLiquidityType(Instrument instrument) {
        if(requestedInstrument.getLiquidityType()!=null && requestedInstrument.getLiquidityType()!=LiquidityType.UNKNOWN){
            instrument.setLiquidityType(requestedInstrument.getLiquidityType());
        }else {
            instrument.setLiquidityType(LiquidityType.LIQUIDE);
        }
        return Mono.just(instrument);
    }
}