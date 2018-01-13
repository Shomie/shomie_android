package com.software.lrocha3.shomie;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;
    private Connection connect = null;
    private ResultSet resultSet = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Asks permissions to send_sms and read_phone_state in newer android versions */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {

                requestPermissions(new String[]{
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.READ_PHONE_STATE},
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        }

        Button send_button = (Button) findViewById(R.id.send);
        send_button.setOnClickListener(this);

        Button mysql_button = (Button) findViewById(R.id.mysql_connect);
        mysql_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send: {
                String phone_number = "+351 917674816";
                String messageToSend = "[Shomie] Hello !";

                SendMessageToUser(v, messageToSend, phone_number);
            }
            case R.id.mysql_connect: {
                new MySQLAsync().execute(v);
            }
            break;
        }
    }

    public void SendMessageToUser(View v, String messageToSend, String phone_number) {
        Activity host = (Activity) v.getContext();
        if (ContextCompat.checkSelfPermission(host, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(host, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {

            boolean valid_phone = false;
            if (!TextUtils.isEmpty(phone_number)) {
                valid_phone = Patterns.PHONE.matcher(phone_number).matches();
            }

            if (valid_phone == true) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone_number, null, messageToSend, null, null);
                    System.out.println("Valid number");
                            /* TODO: Verify if the sms was sent successfully  */
                    Snackbar.make(v, "Sent new message.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                        /* Number is not valid. Warn the user via database account. */
                System.out.println("Invalid number");
            }
        } else {
            Snackbar.make(v, "All the permissions needed were not granted.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void MySqlClose() {
        try {
            /* TODO: Clear statement and result sets here as well if null */

            if (this.connect != null) {
                this.connect.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean MySqlConnect() {
        boolean result = false;
        try {
            this.connect = DriverManager.getConnection("jdbc:mysql://lrocha3.no-ip.org:3306/shomie", "dev", "development");

            if (this.connect != null) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean MySqlGetAllData(View v) {
        boolean result = false;

        try {
            Statement statement = this.connect.createStatement();
            ResultSet resultSet = statement
                    .executeQuery("SELECT * FROM communication");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String state = resultSet.getString("state");
                String property_id = resultSet.getString("property_id");
                String user_id = resultSet.getString("user_id");
                String visit_date = resultSet.getString("visit_date");
                String visit_time = resultSet.getString("visit_time");
                String sms_status = resultSet.getString("sms_status");

                System.out.println("id: " +  Integer.toString(id));
                System.out.println("state: " + state);
                System.out.println("property_id: " + property_id);
                System.out.println("user_id: " + user_id);
                System.out.println("visit_date: " + visit_date);
                System.out.println("visit_time: " + visit_time);
                System.out.println("sms_status: " + sms_status);

                String phone_number = "+351 916563335";
                String messageToSend = "[Shomie] Hello! The visit to the property " +
                        property_id + " will be in the day " + visit_date
                        + " at " + visit_time + ". Cheers.";

                SendMessageToUser(v, messageToSend, phone_number);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                phone_number = "+351 917674816";

                SendMessageToUser(v, messageToSend, phone_number);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
    }


    /*
    *   // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // Result set get the result of the SQL query
            resultSet = statement
                    .executeQuery("select * from feedback.comments");
            writeResultSet(resultSet);
    * */

    private class MySQLAsync extends AsyncTask<View, Void, String> {

        @Override
        protected String doInBackground(View... params) {

            View v = params[0];
            boolean result = MySqlConnect();
           String data = "[MySQL] NOK";


            if (result == true) {
                MySqlGetAllData(v);

                data = "[MySQL] OK";
            } else {

            }

            MySqlClose();
            return data;

        }

        @Override
        protected void onPostExecute(String result) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, result, duration);
            toast.show();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


}
