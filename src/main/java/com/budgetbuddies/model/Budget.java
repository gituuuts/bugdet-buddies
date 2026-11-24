package com.budgetbuddies.model;

import java.math.BigDecimal;

public class Budget {
    private String B_id;
    private BigDecimal B_amount_limit;
    private int B_month;
    private int B_year;
    private String Categories_C_id;
    private String Users_U_id;

    public Budget() {
    }

    public Budget(String B_id, BigDecimal B_amount_limit,
                  int B_month, int B_year,
                  String Categories_C_id, String Users_U_id) {
        this.B_id = B_id;
        this.B_amount_limit = B_amount_limit;
        this.B_month = B_month;
        this.B_year = B_year;
        this.Categories_C_id = Categories_C_id;
        this.Users_U_id = Users_U_id;
    }

    public String getB_id() {
        return B_id;
    }

    public void setB_id(String B_id) {
        this.B_id = B_id;
    }

    public BigDecimal getB_amount_limit() {
        return B_amount_limit;
    }

    public void setB_amount_limit(BigDecimal B_amount_limit) {
        this.B_amount_limit = B_amount_limit;
    }

    public int getB_month() {
        return B_month;
    }

    public void setB_month(int B_month) {
        this.B_month = B_month;
    }

    public int getB_year() {
        return B_year;
    }

    public void setB_year(int B_year) {
        this.B_year = B_year;
    }

    public String getCategories_C_id() {
        return Categories_C_id;
    }

    public void setCategories_C_id(String Categories_C_id) {
        this.Categories_C_id = Categories_C_id;
    }

    public String getUsers_U_id() {
        return Users_U_id;
    }

    public void setUsers_U_id(String Users_U_id) {
        this.Users_U_id = Users_U_id;
    }
}
