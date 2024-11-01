package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.myfinance.restmodel.LiquidityType;
import reactor.core.publisher.Mono;

public class DepotHandler extends AbsCashInstrumentHandler {

    public DepotHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, requestedInstrument.getDescription(), InstrumentType.DEPOT, true, ts);
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.ACCOUNTPORTFOLIO;
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.DEPOT;
    }

    @Override
    protected Mono<Instrument> setLiquidityType(Instrument instrument) {
        instrument.setLiquidityType(LiquidityType.MIDTERM);
        return Mono.just(instrument);
    }
}