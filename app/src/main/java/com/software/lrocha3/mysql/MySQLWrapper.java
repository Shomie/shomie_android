package com.software.lrocha3.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by luigi on 14-01-2018.
 */

public class MySQLWrapper {

    private Connection connect = null;
    private ResultSet resultSet = null;
    private Statement statement = null;

    public MySQLWrapper() {
    /* Initialize stuff here */
    }

    public boolean ConnectionState() {
        if (this.connect != null) {
            System.out.println("TRUE CONNECTED");

            return true;
        } else {
            System.out.println("TRUE not CONNECTED");

            return false;
        }
    }

    public void Open() {
        try {
            if (this.connect == null) {
                this.connect = DriverManager.getConnection("jdbc:mysql://lrocha3.no-ip.org:3306/shomie", "dev", "development");
            }
        } catch (SQLException e) {
            System.out.println("\n\nups\n\n");

            e.printStackTrace();
        }
    }

    public void Close() {
        try {
            if (this.connect != null) {
                this.connect.close();
            }

            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public ResultSet GetAllVisitRequests() {

        try {
            statement = this.connect.createStatement();
            resultSet = statement
                    .executeQuery("SELECT * FROM communication");
            System.out.println("SELECT EXECUTES");

            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
