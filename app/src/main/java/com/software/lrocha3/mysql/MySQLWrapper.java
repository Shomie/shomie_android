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
                this.connect = DriverManager.getConnection("jdbc:mysql://45.77.89.232:3306/shomie", "root", "password");
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
                    .executeQuery("SELECT communication.id, communication.property_id, communication.visit_date, communication.visit_time, properties.landlord_id, landlords.phone_number FROM communication INNER JOIN landlords INNER JOIN properties WHERE communication.sms_status = 0 AND communication.state = 0 AND properties.id = communication.property_id AND properties.landlord_id = landlords.id");
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
