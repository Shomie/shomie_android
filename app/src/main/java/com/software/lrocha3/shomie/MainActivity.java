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
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

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
                SendMessageToUser(v);
            }
            case R.id.mysql_connect: {
                new MySQLAsync().execute("");
            }
            break;
        }
    }

    public void SendMessageToUser(View v) {
        Activity host = (Activity) v.getContext();
        if (ContextCompat.checkSelfPermission(host, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(host, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {

            String messageToSend = "[Shomie] Hello !";
            String phone_number = "+351 917674816";

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


    public void MySqlClose(Connection connect) {
        try {
            /* TODO: Clear statement and result sets here as well if null */

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean MySqlConnection() {
        boolean result = false;
        Connection connect = null;
        try {
            connect = DriverManager.getConnection("jdbc:mysql://lrocha3.no-ip.org:3306/Development", "dev", "development");

            if (connect != null) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySqlClose(connect);
        }
        return result;
    }

    private class MySQLAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            boolean result = MySqlConnection();

            if (result == true) {
                return "[MySQL] OK";
            } else {
                return "[MySQL] NOK";
            }
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
