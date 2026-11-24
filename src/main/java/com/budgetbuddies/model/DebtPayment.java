package com.budgetbuddies.model;

import java.math.BigDecimal;
import java.sql.Date;

public class DebtPayment {
    private String DP_id;
    private BigDecimal DP_amount;
    private Date DP_payment_date;
    private String DP_notes;
    private String Debts_D_id;

    public DebtPayment() {
    }

    public DebtPayment(String DP_id, BigDecimal DP_amount,
                       Date DP_payment_date, String DP_notes,
                       String Debts_D_id) {
        this.DP_id = DP_id;
        this.DP_amount = DP_amount;
        this.DP_payment_date = DP_payment_date;
        this.DP_notes = DP_notes;
        this.Debts_D_id = Debts_D_id;
    }

    public String getDP_id() {
        return DP_id;
    }

    public void setDP_id(String DP_id) {
        this.DP_id = DP_id;
    }

    public BigDecimal getDP_amount() {
        return DP_amount;
    }

    public void setDP_amount(BigDecimal DP_amount) {
        this.DP_amount = DP_amount;
    }

    public Date getDP_payment_date() {
        return DP_payment_date;
    }

    public void setDP_payment_date(Date DP_payment_date) {
        this.DP_payment_date = DP_payment_date;
    }

    public String getDP_notes() {
        return DP_notes;
    }

    public void setDP_notes(String DP_notes) {
        this.DP_notes = DP_notes;
    }

    public String getDebts_D_id() {
        return Debts_D_id;
    }

    public void setDebts_D_id(String Debts_D_id) {
        this.Debts_D_id = Debts_D_id;
    }
}
