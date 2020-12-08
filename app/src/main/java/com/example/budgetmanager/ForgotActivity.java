package com.example.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class ForgotActivity extends AppCompatActivity {
    EditText name,e1,e2,e3,e4,otp;
    ProgressBar pb;
    Button register,verify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        e1=findViewById(R.id.editText1);
        e2=findViewById(R.id.editText2);
        e3=findViewById(R.id.editText3);
        pb=findViewById(R.id.pb);

        register=findViewById(R.id.register);
        verify=findViewById(R.id.verify);
        otp=findViewById(R.id.otp);

        getSupportActionBar().setTitle("Reset password");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.purple));
    }


    public void verify(View v){

        final String email=e1.getText().toString();
        e1.setEnabled(false);

        pb.setAlpha(1);
        verify.setEnabled(false);
        //Toast.makeText(this,email,Toast.LENGTH_LONG).show();

        JsonObjectRequest jsonObjectRequest=null;
        RequestQueue requestQueue=null;
        try {
            JSONObject obj=new JSONObject();
            obj.put("email",email);

            requestQueue = Volley.newRequestQueue(this);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "http://"+ip+"/emailverifyforgotpassword", obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String otp1;
                    pb.setAlpha(0);
                    verify.setEnabled(true);

                    try{
                        if(response.getInt("status")==0){
                            e1.setEnabled(true);
                            Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                              return;
                        }
                        Toast.makeText(getApplicationContext(),"An OTP is sent to your email id",Toast.LENGTH_LONG).show();

                        otp.setAlpha(1);
                        e2.setAlpha(1);
                        e3.setAlpha(1);
                        register.setAlpha(1);
                        register.setEnabled(true);
                        verify.setText("RESEND OTP");
                        otp1=response.getString("otp1");
                        final SharedPreferences sharedPreferences=getSharedPreferences("", Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("otp1",otp1);
                        editor.commit();
                    }
                    catch (Exception e){}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("myapp", "Something went wrong Haha");
                    Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    pb.setAlpha(0);
                    verify.setEnabled(true);

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
        final String email=e1.getText().toString();
        final String pass=e2.getText().toString();
        final String cpass=e3.getText().toString();


        if(pass.equals("")){
            Toast.makeText(getApplicationContext(),"Please Fill The Required Places",Toast.LENGTH_LONG).show();
            return;
        }
         if(!pass.equals(cpass)){
            Toast.makeText(getApplicationContext(),"Password should match with confirm password",Toast.LENGTH_LONG).show();
            return;
        }

        final  SharedPreferences sharedPreferences=getSharedPreferences("", Context.MODE_PRIVATE);
        final  SharedPreferences.Editor editor = sharedPreferences.edit();
        EditText otp=findViewById(R.id.otp);
        if(!otp.getText().toString().equals(sharedPreferences.getString("otp1","9999999")))
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
            obj.put("pass",pass);
            requestQueue = Volley.newRequestQueue(this);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "http://"+ip+"/updatepassword", obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    pbEnable(false);
                    try {
                        if (response.getInt("status")==1){
                            editor.putString("otp1", "9999999");
                            editor.putString("email", email);
                            editor.commit();
                            //startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(),response.getString("name"), Toast.LENGTH_LONG).show();
                        }
                        else Toast.makeText(getApplicationContext(),response.getString("name"), Toast.LENGTH_LONG).show();

                    }catch (Exception e){}
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
