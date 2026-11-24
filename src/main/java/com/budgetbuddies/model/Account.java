package com.budgetbuddies.model;

import java.math.BigDecimal;

public class Account {
    private String A_id;
    private String A_name;
    private String A_account_type;
    private BigDecimal A_initial_balance;
    private String Users_U_id;

    public Account() {
    }

    public Account(String A_id, String A_name,
                   String A_account_type,
                   BigDecimal A_initial_balance,
                   String Users_U_id) {
        this.A_id = A_id;
        this.A_name = A_name;
        this.A_account_type = A_account_type;
        this.A_initial_balance = A_initial_balance;
        this.Users_U_id = Users_U_id;
    }

    public String getA_id() {
        return A_id;
    }

    public void setA_id(String A_id) {
        this.A_id = A_id;
    }

    public String getA_name() {
        return A_name;
    }

    public void setA_name(String A_name) {
        this.A_name = A_name;
    }

    public String getA_account_type() {
        return A_account_type;
    }

    public void setA_account_type(String A_account_type) {
        this.A_account_type = A_account_type;
    }

    public BigDecimal getA_initial_balance() {
        return A_initial_balance;
    }

    public void setA_initial_balance(BigDecimal A_initial_balance) {
        this.A_initial_balance = A_initial_balance;
    }

    public String getUsers_U_id() {
        return Users_U_id;
    }

    public void setUsers_U_id(String Users_U_id) {
        this.Users_U_id = Users_U_id;
    }
}
