package com.budgetbuddies.model;

import java.math.BigDecimal;
import java.sql.Date;

public class RecurringTransaction {

    private String RT_id;
    private String RT_title;
    private String RT_type;
    private BigDecimal RT_amount;
    private String RT_frequency;
    private Date RT_start_date;
    private Date RT_end_date;
    private Date RT_next_date;
    private String RT_status;
    private String Accounts_A_id;
    private String Categories_C_id;

    public RecurringTransaction() {
    }

    public RecurringTransaction(
            String RT_id,
            String RT_title,
            String RT_type,
            BigDecimal RT_amount,
            String RT_frequency,
            Date RT_start_date,
            Date RT_end_date,
            Date RT_next_date,
            String RT_status,
            String Accounts_A_id,
            String Categories_C_id) {

        this.RT_id = RT_id;
        this.RT_title = RT_title;
        this.RT_type = RT_type;
        this.RT_amount = RT_amount;
        this.RT_frequency = RT_frequency;
        this.RT_start_date = RT_start_date;
        this.RT_end_date = RT_end_date;
        this.RT_next_date = RT_next_date;
        this.RT_status = RT_status;
        this.Accounts_A_id = Accounts_A_id;
        this.Categories_C_id = Categories_C_id;
    }

    public String getRT_id() {
        return RT_id;
    }

    public void setRT_id(String RT_id) {
        this.RT_id = RT_id;
    }

    public String getRT_title() {
        return RT_title;
    }

    public void setRT_title(String RT_title) {
        this.RT_title = RT_title;
    }

    public String getRT_type() {
        return RT_type;
    }

    public void setRT_type(String RT_type) {
        this.RT_type = RT_type;
    }

    public BigDecimal getRT_amount() {
        return RT_amount;
    }

    public void setRT_amount(BigDecimal RT_amount) {
        this.RT_amount = RT_amount;
    }

    public String getRT_frequency() {
        return RT_frequency;
    }

    public void setRT_frequency(String RT_frequency) {
        this.RT_frequency = RT_frequency;
    }

    public Date getRT_start_date() {
        return RT_start_date;
    }

    public void setRT_start_date(Date RT_start_date) {
        this.RT_start_date = RT_start_date;
    }

    public Date getRT_end_date() {
        return RT_end_date;
    }

    public void setRT_end_date(Date RT_end_date) {
        this.RT_end_date = RT_end_date;
    }

    public Date getRT_next_date() {
        return RT_next_date;
    }

    public void setRT_next_date(Date RT_next_date) {
        this.RT_next_date = RT_next_date;
    }

    public String getRT_status() {
        return RT_status;
    }

    public void setRT_status(String RT_status) {
        this.RT_status = RT_status;
    }

    public String getAccounts_A_id() {
        return Accounts_A_id;
    }

    public void setAccounts_A_id(String Accounts_A_id) {
        this.Accounts_A_id = Accounts_A_id;
    }

    public String getCategories_C_id() {
        return Categories_C_id;
    }

    public void setCategories_C_id(String Categories_C_id) {
        this.Categories_C_id = Categories_C_id;
    }
}
