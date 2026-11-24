package com.budgetbuddies.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Debt {
    private String D_id;
    private String D_name;
    private String D_type;
    private BigDecimal D_total_amount;
    private BigDecimal D_remaining_amount;
    private Date D_due_date;
    private String D_status;
    private String Users_U_id;

    public Debt() {
    }

    public Debt(String D_id, String D_name, String D_type,
                BigDecimal D_total_amount,
                BigDecimal D_remaining_amount,
                Date D_due_date, String D_status,
                String Users_U_id) {
        this.D_id = D_id;
        this.D_name = D_name;
        this.D_type = D_type;
        this.D_total_amount = D_total_amount;
        this.D_remaining_amount = D_remaining_amount;
        this.D_due_date = D_due_date;
        this.D_status = D_status;
        this.Users_U_id = Users_U_id;
    }

    public String getD_id() {
        return D_id;
    }

    public void setD_id(String D_id) {
        this.D_id = D_id;
    }

    public String getD_name() {
        return D_name;
    }

    public void setD_name(String D_name) {
        this.D_name = D_name;
    }

    public String getD_type() {
        return D_type;
    }

    public void setD_type(String D_type) {
        this.D_type = D_type;
    }

    public BigDecimal getD_total_amount() {
        return D_total_amount;
    }

    public void setD_total_amount(BigDecimal D_total_amount) {
        this.D_total_amount = D_total_amount;
    }

    public BigDecimal getD_remaining_amount() {
        return D_remaining_amount;
    }

    public void setD_remaining_amount(BigDecimal D_remaining_amount) {
        this.D_remaining_amount = D_remaining_amount;
    }

    public Date getD_due_date() {
        return D_due_date;
    }

    public void setD_due_date(Date D_due_date) {
        this.D_due_date = D_due_date;
    }

    public String getD_status() {
        return D_status;
    }

    public void setD_status(String D_status) {
        this.D_status = D_status;
    }

    public String getUsers_U_id() {
        return Users_U_id;
    }

    public void setUsers_U_id(String Users_U_id) {
        this.Users_U_id = Users_U_id;
    }
}
