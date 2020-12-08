package com.example.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import static com.example.budgetmanager.MainActivity.ip;

public class RegActivity extends AppCompatActivity {
    EditText name,e1,e2,e3,e4,otp,e5;
    CheckBox ch;
    TextView purenote;
    RelativeLayout rel;
    ProgressBar pb,pb1;
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
        e5=findViewById(R.id.editText5);
        ch=findViewById(R.id.checked);
        pb=findViewById(R.id.pb);
        pb1=findViewById(R.id.pb1);
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
                        e5.setAlpha(1);
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
                    Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
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
        final String cpass=e5.getText().toString();

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
        if(!pass.equals(cpass)){
            Toast.makeText(getApplicationContext(),"Password doesn't match with confirm password",Toast.LENGTH_LONG).show();
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

        pb1Enable(true);

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
                    pb1Enable(false);
                    try {
                        if (response.getInt("status")==1){
                            editor.putString("otp", "9999999");
                            editor.putString("name", name1);
                            editor.putString("email", email);
                            editor.putString("mili", "0");
                            editor.putString("alarmDate", "");
                            editor.putString("alarmTime", "");

                            editor.commit();

                            How();
                            Toast.makeText(getApplicationContext(),"Successfully Registered", Toast.LENGTH_LONG).show();
                        }
                        else Toast.makeText(getApplicationContext(),response.getString("name"), Toast.LENGTH_LONG).show();

                    }catch (Exception e){}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d("myapp", "Something went wrong Haha");
                    Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    pb1Enable(false);
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
            verify.setEnabled(false);
        }
        else
        {
            pb.setAlpha(0);
            verify.setEnabled(true);
        }
    }

    public void pb1Enable(boolean b)
    {
        if(b) {
            pb1.setAlpha(1);
            register.setEnabled(false);
        }
        else
        {
            pb.setAlpha(0);
            register.setEnabled(true);
        }
    }

    public  void How(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("How to use");
        alertDialogBuilder.setMessage("A budget management app to save your earnings and expenditures records\n\n" +
                "And see them in Credits and Debits tab respectively\n\n" +
                "It's very useful for Shias as it keeps track of Khums\n\n" +
                "Here Pure Balance is the balance after paying Khums or the amount for which Khums is not applicable\n\n" +
                "So we consider Khums for the balance excluding pure balance\n\n" +
                "You can also set reminder for Khums\n");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
