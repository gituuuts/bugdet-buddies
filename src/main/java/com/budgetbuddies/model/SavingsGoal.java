package com.budgetbuddies.model;

import java.math.BigDecimal;
import java.sql.Date;

public class SavingsGoal {

    private String SG_id;
    private String SG_name;
    private BigDecimal SG_target_amount;
    private BigDecimal SG_current_amount;
    private Date SG_target_date;
    private String SG_status;
    private String Users_U_id;

    public SavingsGoal() {
    }

    public SavingsGoal(
            String SG_id,
            String SG_name,
            BigDecimal SG_target_amount,
            BigDecimal SG_current_amount,
            Date SG_target_date,
            String SG_status,
            String Users_U_id) {

        this.SG_id = SG_id;
        this.SG_name = SG_name;
        this.SG_target_amount = SG_target_amount;
        this.SG_current_amount = SG_current_amount;
        this.SG_target_date = SG_target_date;
        this.SG_status = SG_status;
        this.Users_U_id = Users_U_id;
    }

    public String getSG_id() {
        return SG_id;
    }

    public void setSG_id(String SG_id) {
        this.SG_id = SG_id;
    }

    public String getSG_name() {
        return SG_name;
    }

    public void setSG_name(String SG_name) {
        this.SG_name = SG_name;
    }

    public BigDecimal getSG_target_amount() {
        return SG_target_amount;
    }

    public void setSG_target_amount(BigDecimal SG_target_amount) {
        this.SG_target_amount = SG_target_amount;
    }

    public BigDecimal getSG_current_amount() {
        return SG_current_amount;
    }

    public void setSG_current_amount(BigDecimal SG_current_amount) {
        this.SG_current_amount = SG_current_amount;
    }

    public Date getSG_target_date() {
        return SG_target_date;
    }

    public void setSG_target_date(Date SG_target_date) {
        this.SG_target_date = SG_target_date;
    }

    public String getSG_status() {
        return SG_status;
    }

    public void setSG_status(String SG_status) {
        this.SG_status = SG_status;
    }

    public String getUsers_U_id() {
        return Users_U_id;
    }

    public void setUsers_U_id(String Users_U_id) {
        this.Users_U_id = Users_U_id;
    }
}
