package com.budgetbuddies.model;

public class Category {
    private String C_id;
    private String C_name;
    private String C_type;
    private String Users_U_id;

    public Category() {
    }

    public Category(String C_id, String C_name,
                    String C_type, String Users_U_id) {
        this.C_id = C_id;
        this.C_name = C_name;
        this.C_type = C_type;
        this.Users_U_id = Users_U_id;
    }

    public String getC_id() {
        return C_id;
    }

    public void setC_id(String C_id) {
        this.C_id = C_id;
    }

    public String getC_name() {
        return C_name;
    }

    public void setC_name(String C_name) {
        this.C_name = C_name;
    }

    public String getC_type() {
        return C_type;
    }

    public void setC_type(String C_type) {
        this.C_type = C_type;
    }

    public String getUsers_U_id() {
        return Users_U_id;
    }

    public void setUsers_U_id(String Users_U_id) {
        this.Users_U_id = Users_U_id;
    }
}
