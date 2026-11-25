package com.budgetbuddies.model;

public class User {
    private String U_id;
    private String U_name;
    private String U_email;
    private String U_password; // hash PBKDF2, BUKAN password asli

    public User() {
    }

    public User(String U_id, String U_name, String U_email, String U_password) {
        this.U_id = U_id;
        this.U_name = U_name;
        this.U_email = U_email;
        this.U_password = U_password;
    }

    public String getU_id() {
        return U_id;
    }

    public void setU_id(String U_id) {
        this.U_id = U_id;
    }

    public String getU_name() {
        return U_name;
    }

    public void setU_name(String U_name) {
        this.U_name = U_name;
    }

    public String getU_email() {
        return U_email;
    }

    public void setU_email(String U_email) {
        this.U_email = U_email;
    }

    public String getU_password() {
        return U_password;
    }

    public void setU_password(String U_password) {
        this.U_password = U_password;
    }
}
