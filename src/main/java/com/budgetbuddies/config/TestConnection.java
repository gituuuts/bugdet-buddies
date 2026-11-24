package com.budgetbuddies.config;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection connection = DBConnection.getConnection();

            if (connection != null) {
                System.out.println("Koneksi database berhasil!");
                connection.close();
            }

        } catch (Exception e) {
            System.out.println("Koneksi database gagal!");
            e.printStackTrace();
        }
    }
}
