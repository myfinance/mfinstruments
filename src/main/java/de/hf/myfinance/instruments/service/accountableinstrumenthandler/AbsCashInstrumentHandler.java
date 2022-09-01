package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraph;


public abstract class AbsCashInstrumentHandler extends AbsAccountableInstrumentHandler {

    protected AbsCashInstrumentHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String description, String parentBusinesskey, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, parentBusinesskey, businesskey, isNewInstrument);
    }

    //@Override
    protected void validateInstrument4Inactivation() {
        /*if( valueService.getValue(instrumentId, LocalDate.MAX)!=0.0 ){
            throw new MFException(MFMsgKey.NO_VALID_INSTRUMENT_FOR_DEACTIVATION, "instrument with id:"+instrumentId + " not deactivated. The current value is not 0");
        }
        validateRecurrentTransactions4InstrumentInactivation();*/
    }


    /*private void validateRecurrentTransactions4InstrumentInactivation() {
        for (RecurrentTransaction r : recurrentTransactionDao.listRecurrentTransactions()) {

            if( r.getInstrumentByInstrumentid1().getInstrumentid() == instrumentId ||
                    r.getInstrumentByInstrumentid2().getInstrumentid() == instrumentId ) {
                throw new MFException(MFMsgKey.NO_VALID_INSTRUMENT_FOR_DEACTIVATION, "instrument with id:"+
                        instrumentId + " not deactivated. There are still recurrent transactions for this instrument");
            }
        }
    }*/
}