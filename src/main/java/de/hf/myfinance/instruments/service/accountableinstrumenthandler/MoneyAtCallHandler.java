package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.myfinance.restmodel.LiquidityType;
import reactor.core.publisher.Mono;

public class MoneyAtCallHandler extends GiroHandler {

    public MoneyAtCallHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.MONEYATCALL;
    }

    @Override
    protected Mono<Instrument> setLiquidityType(Instrument instrument) {
        instrument.setLiquidityType(LiquidityType.SHORTTERM);
        return Mono.just(instrument);
    }

}