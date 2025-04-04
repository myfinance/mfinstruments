package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.myfinance.restmodel.LiquidityType;
import reactor.core.publisher.Mono;

public class BuildingsavingAcoountHandler  extends GiroHandler {

    public BuildingsavingAcoountHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUILDINGSAVINGACCOUNT;
    }

    @Override
    protected Mono<Instrument> setLiquidityType(Instrument instrument) {
        instrument.setLiquidityType(LiquidityType.MIDTERM);
        return Mono.just(instrument);
    }

}