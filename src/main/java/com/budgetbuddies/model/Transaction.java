package com.budgetbuddies.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Transaction {
    private String T_id;
    private String T_title;
    private String T_description;
    private String T_type;
    private BigDecimal T_amount;
    private Date T_transaction_date;
    private String Accounts_A_id;
    private String Categories_C_id;

    public Transaction() {
    }

    public Transaction(String T_id, String T_title,
                       String T_description, String T_type,
                       BigDecimal T_amount,
                       Date T_transaction_date,
                       String Accounts_A_id,
                       String Categories_C_id) {
        this.T_id = T_id;
        this.T_title = T_title;
        this.T_description = T_description;
        this.T_type = T_type;
        this.T_amount = T_amount;
        this.T_transaction_date = T_transaction_date;
        this.Accounts_A_id = Accounts_A_id;
        this.Categories_C_id = Categories_C_id;
    }

    public String getT_id() {
        return T_id;
    }

    public void setT_id(String T_id) {
        this.T_id = T_id;
    }

    public String getT_title() {
        return T_title;
    }

    public void setT_title(String T_title) {
        this.T_title = T_title;
    }

    public String getT_description() {
        return T_description;
    }

    public void setT_description(String T_description) {
        this.T_description = T_description;
    }

    public String getT_type() {
        return T_type;
    }

    public void setT_type(String T_type) {
        this.T_type = T_type;
    }

    public BigDecimal getT_amount() {
        return T_amount;
    }

    public void setT_amount(BigDecimal T_amount) {
        this.T_amount = T_amount;
    }

    public Date getT_transaction_date() {
        return T_transaction_date;
    }

    public void setT_transaction_date(Date T_transaction_date) {
        this.T_transaction_date = T_transaction_date;
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
