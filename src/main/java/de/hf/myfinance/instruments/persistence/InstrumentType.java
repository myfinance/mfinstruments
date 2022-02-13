/** ----------------------------------------------------------------------------
 *
 * ---          HF - Application Development                       ---
 *              Copyright (c) 2014, ... All Rights Reserved
 *
 *
 *  Project     : dac
 *
 *  File        : InstrumentType.java
 *
 *  Author(s)   : hf
 *
 *  Created     : 02.02.2018
 *
 * ----------------------------------------------------------------------------
 */

package de.hf.myfinance.instruments.persistence;

public enum InstrumentType {
    GIRO(Integer.valueOf(1)),
    MONEYATCALL(Integer.valueOf(2)),
    TIMEDEPOSIT(Integer.valueOf(3)),
    BUILDINGSAVINGACCOUNT(Integer.valueOf(4)),
    BUDGET(Integer.valueOf(5)),
    TENANT(Integer.valueOf(6)),
    ACCOUNTPORTFOLIO(Integer.valueOf(8)),
    ARTIFICALPORTFOLIO(Integer.valueOf(9)),
    BUDGETGROUP(Integer.valueOf(10)),
    DEPOT(Integer.valueOf(11)),
    BUILDINGSAVING(Integer.valueOf(12)),
    CURRENCY(Integer.valueOf(13)),
    EQUITY(Integer.valueOf(14)),
    FONDS(Integer.valueOf(15)),
    ETF(Integer.valueOf(16)),
    INDEX(Integer.valueOf(17)),
    BOND(Integer.valueOf(18)),
    LIFEINSURANCE(Integer.valueOf(19)),
    DEPRECATIONOBJECT(Integer.valueOf(20)),
    REALESTATE(Integer.valueOf(21)),
    LOAN(Integer.valueOf(22)),
    BUDGETPORTFOLIO(Integer.valueOf(23)),
    UNKNOWN(Integer.valueOf(99));

    public static final String GIRO_IDSTRING = "1";
    public static final String MONEYATCALL_IDSTRING = "2";
    public static final String TIMEDEPOSIT_IDSTRING = "3";
    public static final String BUILDINGSAVINGACCOUNT_IDSTRING = "4";
    public static final String BUDGET_IDSTRING = "5";
    public static final String TENANT_IDSTRING = "6";
    public static final String ACCOUNTPORTFOLIO_IDSTRING = "8";
    public static final String ARTIFICIALPORTFOLIO_IDSTRING = "9";
    public static final String BUDGETGROUP_IDSTRING = "10";
    public static final String DEPOT_IDSTRING = "11";
    public static final String BUILDINGSAVING_IDSTRING = "12";
    public static final String CURRENCY_IDSTRING = "13";
    public static final String EQUITY_IDSTRING = "14";
    public static final String FONDS_IDSTRING = "15";
    public static final String ETF_IDSTRING = "16";
    public static final String INDEX_IDSTRING = "17";
    public static final String BOND_IDSTRING = "18";
    public static final String LIFEINSURANCE_IDSTRING = "19";
    public static final String DEPRECATIONOBJECT_IDSTRING = "20";
    public static final String REALESTATE_IDSTRING = "21";
    public static final String LOAN_IDSTRING = "22";
    public static final String BUDGETPORTFOLIO_IDSTRING = "23";

    private final Integer value;

    InstrumentType(final Integer newValue) {
        value = newValue;
    }

    public Integer getValue() { return value; }

    public InstrumentTypeGroup getTypeGroup(){
        switch(value){
            case 6: return InstrumentTypeGroup.TENANT;
            case 8: return InstrumentTypeGroup.PORTFOLIO;
            case 9: return InstrumentTypeGroup.PORTFOLIO;
            case 10: return InstrumentTypeGroup.PORTFOLIO;
            case 11: return InstrumentTypeGroup.DEPOT;
            case 12: return InstrumentTypeGroup.PORTFOLIO;
            case 13: return InstrumentTypeGroup.SECURITY;
            case 14: return InstrumentTypeGroup.SECURITY;
            case 15: return InstrumentTypeGroup.SECURITY;
            case 16: return InstrumentTypeGroup.SECURITY;
            case 17: return InstrumentTypeGroup.SECURITY;
            case 18: return InstrumentTypeGroup.SECURITY;
            case 19: return InstrumentTypeGroup.LIVEINSURANCE;
            case 20: return InstrumentTypeGroup.DEPRECATIONOBJECT;
            case 21: return InstrumentTypeGroup.REALESTATE;
            case 22: return InstrumentTypeGroup.LOAN;
            case 23: return InstrumentTypeGroup.PORTFOLIO;
            default: return InstrumentTypeGroup.CASHACCOUNT;
        }
    }

    public static InstrumentType getInstrumentTypeById(int instrumenttypeId){
        switch(instrumenttypeId){
            case 1: return InstrumentType.GIRO;
            case 2: return InstrumentType.MONEYATCALL;
            case 3: return InstrumentType.TIMEDEPOSIT;
            case 4: return InstrumentType.BUILDINGSAVINGACCOUNT;
            case 5: return InstrumentType.BUDGET;
            case 6: return InstrumentType.TENANT;
            //case 7: return InstrumentType.BudgetGroupPortfolio;
            case 8: return InstrumentType.ACCOUNTPORTFOLIO;
            case 9: return InstrumentType.ARTIFICALPORTFOLIO;
            case 10: return InstrumentType.BUDGETGROUP;
            case 11: return InstrumentType.DEPOT;
            case 12: return InstrumentType.BUILDINGSAVING;
            case 13: return InstrumentType.CURRENCY;
            case 14: return InstrumentType.EQUITY;
            case 15: return InstrumentType.FONDS;
            case 16: return InstrumentType.ETF;
            case 17: return InstrumentType.INDEX;
            case 18: return InstrumentType.BOND;
            case 19: return InstrumentType.LIFEINSURANCE;
            case 20: return InstrumentType.DEPRECATIONOBJECT;
            case 21: return InstrumentType.REALESTATE;
            case 22: return InstrumentType.LOAN;
            case 23: return InstrumentType.BUDGETPORTFOLIO;
            default: return InstrumentType.UNKNOWN;
        }
    }

     public LiquidityType getLiquidityType(){
        switch(value){
            case 1: return LiquidityType.LIQUIDE;
            case 2: return LiquidityType.LIQUIDE;
            case 3: return LiquidityType.CALCULATED;
            case 4: return LiquidityType.MIDTERM;
            case 11: return LiquidityType.MIDTERM;
            case 12: return LiquidityType.CALCULATED;
            case 19: return LiquidityType.CALCULATED;
            case 20: return LiquidityType.MIDTERM;
            case 21: return LiquidityType.LONGTERM;
            case 22: return LiquidityType.CALCULATED;
            default: return LiquidityType.UNKNOWN;
        }
    }
}


