package ru.inno.db;

import java.sql.*;
import java.util.Scanner;

public class DbDemo {

    public static void main(String[] args) throws SQLException {

        String connectionString = "jdbc:postgresql://dpg-cqsr9ulumphs73c2q40g-a.frankfurt-postgres.render.com/x_clients_db_fxd0";
        String username = "x_clients_user";
        String password = "95PM5lQE0NfzJWDQmLjbZ45ewrz1fLYa";

//        String SELECT_ALL = "select id, \"name\", is_active  from company";
//        String SELECT_COUNT = "select count(*) from company ";
//
//
        Connection connection = DriverManager.getConnection(connectionString, username, password);
//
//
//        ResultSet resultSet1 = connection.createStatement().executeQuery(SELECT_COUNT);
//
//        resultSet1.next();
//        int count = resultSet1.getInt(1);
//        System.out.println(count);
//
//
//        ResultSet resultSet = connection.createStatement().executeQuery(SELECT_ALL);
//
//        while (resultSet.next()) {
//            System.out.println(resultSet.getInt("id"));
//            System.out.println(resultSet.getBoolean("is_active"));
//            System.out.println(resultSet.getString("name"));
//        }
//
//        System.out.println("Конец");


        //select id, "name", is_active  from company limit 10 offset 10*2

        ResultSet companyInfo = getCompanyInfo(connection, 5773);

        if (companyInfo.next()){
            System.out.println(companyInfo.getString("name"));
        }

        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();

        companyInfo = getCompanyInfoByName(connection, name);
        if (companyInfo.next()){
            System.out.println(companyInfo.getInt("id"));
            System.out.println(companyInfo.getString("name"));
            System.out.println(companyInfo.getString("description"));
        }

    }

    private static ResultSet getCompanyInfo(Connection connection, int id) throws SQLException {
            String sql = "select * from company where id = " + id;

        return connection.createStatement().executeQuery(sql);
    }

    private static ResultSet getCompanyInfoByName(Connection connection, String name) throws SQLException {
        String sql = "select * from company c where \"name\" = ? and id > ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        statement.setInt(2, 5500);

        ResultSet set =  statement.executeQuery();

        System.out.println(statement);
        return set;
    }
}

//
// []
// []
// []
// []
// []
// []
// []
// []
//
