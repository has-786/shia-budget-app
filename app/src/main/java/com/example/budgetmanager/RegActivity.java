package com.example.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import static com.example.budgetmanager.MainActivity.ip;

public class RegActivity extends AppCompatActivity {
    EditText name,e1,e2,e3,e4,otp;
    CheckBox ch;
    TextView purenote;
    RelativeLayout rel;
    ProgressBar pb;
    Button register,verify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        name=findViewById(R.id.name);
        e1=findViewById(R.id.editText1);
        e2=findViewById(R.id.editText2);
        e3=findViewById(R.id.editText3);
        e4=findViewById(R.id.editText4);
        ch=findViewById(R.id.checked);
        pb=findViewById(R.id.pb);
        rel=findViewById(R.id.rel);
        register=findViewById(R.id.register);
        verify=findViewById(R.id.verify);;
        purenote=findViewById(R.id.purenote);
        otp=findViewById(R.id.otp);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.purple));
    }



    public void login(View v) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();

    }

    public void verify(View v){


        e1.setEnabled(false);

        final String email=e1.getText().toString();

        //Toast.makeText(this,email,Toast.LENGTH_LONG).show();
        pbEnable(true);

        JsonObjectRequest jsonObjectRequest=null;
        RequestQueue requestQueue=null;
        try {
            JSONObject obj=new JSONObject();
            obj.put("email",email);

            requestQueue = Volley.newRequestQueue(this);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "http://"+ip+"/emailverify", obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    pbEnable(false);
                    String otp1;
                    try{
                        if(response.getInt("status")==0)
                        {
                            e1.setEnabled(true);
                            Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        otp.setAlpha(1);
                        name.setAlpha(1);
                        e2.setAlpha(1);
                        e3.setAlpha(1);
                        e4.setAlpha(1);
                        purenote.setAlpha(1);
                        register.setAlpha(1);
                        register.setEnabled(true);
                        verify.setText("RESEND OTP");

                        Toast.makeText(getApplicationContext(),"An OTP is sent to your email id",Toast.LENGTH_LONG).show();
                        otp1=response.getString("otp");
                        final SharedPreferences sharedPreferences=getSharedPreferences("", Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("otp",otp1);
                        editor.commit();
                    }
                    catch (Exception e){}
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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        }catch (Exception e){}
    }


    public void register(View v){
        final String name1=name.getText().toString();
        final String email=e1.getText().toString();
        final String pass=e2.getText().toString();
        String amt1=e3.getText().toString();
        if(amt1.equals(""))amt1="0";
        String amt2=e4.getText().toString();
        if(amt2.equals(""))amt2="0";
        final Double balance=Double.parseDouble(amt1);
        final Double pure=Double.parseDouble(amt2);


        if(name1.equals("") || email.equals("")  || pass.equals("")){
            Toast.makeText(getApplicationContext(),"Please Fill The Required Places",Toast.LENGTH_LONG).show();
            return;

        }
        if(pure>balance){
            Toast.makeText(this,"Pure balance can't be greater",Toast.LENGTH_LONG).show();
            return;
        }
        final  SharedPreferences sharedPreferences=getSharedPreferences("", Context.MODE_PRIVATE);
        final  SharedPreferences.Editor editor = sharedPreferences.edit();
        EditText otp=findViewById(R.id.otp);
        if(!otp.getText().toString().equals(sharedPreferences.getString("otp","9999999")))
        {
            Toast.makeText(this,"Incorrect OTP",Toast.LENGTH_LONG).show();
            return;
        }

        pbEnable(true);

        JsonObjectRequest jsonObjectRequest=null;
        RequestQueue requestQueue=null;
        JSONObject obj=new JSONObject();
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int d = calendar.get(Calendar.DATE);
        int m = calendar.get(Calendar.MONTH);
        int y = calendar.get(Calendar.YEAR);

        String timestamp = d + "/" + m + "/" + y;
        try {
            obj.put("email",email);
            obj.put("name",name1);
            obj.put("pass",pass);
            obj.put("balance",balance);
            obj.put("pure",pure);
            obj.put("timestamp", timestamp);
            obj.put("date", d);
            obj.put("month", m);
            obj.put("year", y);

            requestQueue = Volley.newRequestQueue(this);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "http://"+ip+"/register", obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    pbEnable(false);
                    try {
                        if (response.getInt("status")==1){
                            editor.putString("otp", "9999999");
                            editor.putString("name", name1);
                            editor.putString("email", email);
                            editor.putString("mili", "0");
                            editor.putString("alarmDate", "");
                            editor.putString("alarmTime", "");

                            editor.commit();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(),"Successfully Registered", Toast.LENGTH_LONG).show();
                        }
                        else Toast.makeText(getApplicationContext(),response.getString("name"), Toast.LENGTH_LONG).show();

                    }catch (Exception e){}
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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }catch (Exception e){}
    }


    public void pbEnable(boolean b)
    {
        if(b) {
            pb.setAlpha(1);
            register.setEnabled(false);
        }
        else
        {
            pb.setAlpha(0);
            register.setEnabled(true);
        }
    }


}
