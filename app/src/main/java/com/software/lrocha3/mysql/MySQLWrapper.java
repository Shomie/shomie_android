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
    @Override
    public void finalize() {
        Close();
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
                    .executeQuery("SELECT * FROM communication WHERE sms_status = 0 AND state = 0");
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void SetSmsStatusToTrue(String myStatement) {

        try {
            statement = this.connect.createStatement();
            statement.executeUpdate(myStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
