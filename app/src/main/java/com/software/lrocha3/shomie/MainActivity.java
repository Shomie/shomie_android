package com.software.lrocha3.shomie;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.software.lrocha3.mysql.MySQLWrapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;
    private Connection connect = null;
    private ResultSet resultSet = null;
    private MySQLWrapper mySQLWrapper = null;
    Handler handler = new Handler();

    private Runnable runnableCode = new Runnable() {

        @Override
        public void run() {

            new MySQLAsync().execute("");
            handler.postDelayed(this, 60000);
        }
    };

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

        mySQLWrapper = new MySQLWrapper();
        mySQLWrapper.Open();


        boolean permissionsGranted = AreSMSPermissionsGranted();

        if (permissionsGranted == true) {
            handler.post(runnableCode);
        } else {
            System.out.println("All permissions are not granted");
            Snackbar.make(toolbar, "All the permissions needed were not granted.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }


    public boolean AreSMSPermissionsGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void SendMessageToUser(String messageToSend, String phone_number) {
        boolean sms_sent = false;

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
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
                        /* Number is not valid. Warn the user via database account. */
            System.out.println("Invalid number");
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

    public void ProcessVisitRequests() {

        try {
            ResultSet resultSet = mySQLWrapper.GetAllVisitRequests();
            if(resultSet != null){
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String property_id = resultSet.getString("property_id");
                String visit_date = resultSet.getString("visit_date");
                String visit_time = resultSet.getString("visit_time");
                String landlord_id = resultSet.getString("landlord_id");
                String phone_number = resultSet.getString("phone_number");

                System.out.println("id: " + Integer.toString(id));
                System.out.println("landlord_id: " + property_id);
                System.out.println("property_id: " + property_id);
                System.out.println("visit_date: " + visit_date);
                System.out.println("visit_time: " + visit_time);

                String messageToSend = "[Shomie] Novo pedido para visitar a sua propriedade " +
                        property_id + " no dia " + visit_date
                        + " as " + visit_time + ". Dirija-se a www.shomie.io para aceitar ou rejeitar. Cumprimentos.";

                SendMessageToUser(messageToSend, phone_number);
                mySQLWrapper.SetSmsStatusToTrue("UPDATE communication SET sms_status = 1 WHERE id = " + Integer.toString(id));

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class MySQLAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            mySQLWrapper.Open();
            boolean connectionState = mySQLWrapper.ConnectionState();
            String data = "[MySQL] NOK";


            if (connectionState == true) {
                data = "[MySQL] OK";
                ProcessVisitRequests();
            }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
