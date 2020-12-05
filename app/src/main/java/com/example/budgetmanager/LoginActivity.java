package com.example.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Calendar;

import static com.example.budgetmanager.MainActivity.ip;

public class LoginActivity extends AppCompatActivity {

    EditText name, e1, e2;
    RelativeLayout rel;
    Button login;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        e1 = findViewById(R.id.editText1);
        e2 = findViewById(R.id.editText2);
        rel = findViewById(R.id.rel);
        pb = findViewById(R.id.pb);
        login = findViewById(R.id.login);

        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.purple));

        final SharedPreferences sharedPreferences = getSharedPreferences("", Context.MODE_PRIVATE);
        if (!sharedPreferences.getString("name", "").equals("")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void register(View v) {
        Intent i = new Intent(this, RegActivity.class);
        startActivity(i);
        finish();
    }

    public void login(View v) {
        final String email = e1.getText().toString();
        final String pass = e2.getText().toString();
        final SharedPreferences sharedPreferences = getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        pbEnable(true);

        JsonObjectRequest jsonObjectRequest = null;
        RequestQueue requestQueue = null;
        JSONObject obj = new JSONObject();
        try {
            obj.put("email", email);
            obj.put("pass", pass);

            requestQueue = Volley.newRequestQueue(this);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "http://" + ip + "/login", obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    pbEnable(false);

                    try {
                   //     Toast.makeText(getApplicationContext(),response+"",Toast.LENGTH_LONG).show();
                        if (response.getInt("status") == 1) {
                            editor.putString("name", response.getString("name"));
                            editor.putString("email", email);
                            editor.putString("mili", response.getString("mili"));
                            editor.putString("alarmDate", response.getString("alarmDate"));
                            editor.putString("alarmTime", response.getString("alarmTime"));

                            editor.commit();
                            Log.d("myapp",response+"");
                           // Toast.makeText(getApplicationContext(),response.getString("alarmDate")+" "+response.getString("alarmTime"),Toast.LENGTH_LONG).show();
                            Log.d("myapp","mili "+response.getString("mili"));
                            callAlarm(Long.parseLong(response.getString("mili")));
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(), "Welcome " + response.getString("name"), Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(getApplicationContext(), response.getString("name"), Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("myapp", "Something went wrong Haha");
                    //   Toast.makeText(VideoActivity.this, "error", Toast.LENGTH_LONG);
                    pbEnable(false);

                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
        }

    }


    public void pbEnable(boolean b) {
        if (b) {
            pb.setAlpha(1);
            login.setEnabled(false);
        } else {
            pb.setAlpha(0);
            login.setEnabled(true);
        }
    }

    public void forgotPassword(View v) {
        startActivity(new Intent(this, ForgotActivity.class));
    }


    public  void callAlarm(Long mili)
    {
        if(Calendar.getInstance().getTimeInMillis()>mili)return;
        Log.d("myapp2",mili+"");
        Intent intent  = new Intent(this, MyBroadcastReceiver.class);
        intent.setAction("SomeAction");
        //  alarmManager.cancel(pendingIntent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, mili, pendingIntent);

    }






}
