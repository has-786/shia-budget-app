package com.example.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

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


public class PureActivity extends AppCompatActivity {
    TextView t1,t2,t3;
    EditText e1;
    SharedPreferences sharedPreferences;
    ProgressBar pb;
    ConstraintLayout rel;
    Double khums;
    Button pay;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pure);
         t1=findViewById(R.id.t1);
         t2=findViewById(R.id.t2);
        t3=findViewById(R.id.t3);
        e1=findViewById(R.id.e1);
        rel=findViewById(R.id.rel);
        pb=findViewById(R.id.pb);
        pay=findViewById(R.id.pay);

        getSupportActionBar().setTitle("Pay Khums");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.purple));
        sharedPreferences=getSharedPreferences("", Context.MODE_PRIVATE);

        JsonObjectRequest jsonObjectRequest = null;
        RequestQueue requestQueue = null;
        JSONObject obj = new JSONObject();
        try {
            String email=sharedPreferences.getString("email","");
            obj.put("email", email);

            requestQueue = Volley.newRequestQueue(getApplicationContext());
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "http://"+ip+"/impure", obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        t1.setText(response.getString("pure"));
                        t2.setText(response.getString("impure"));
                        khums=Math.ceil(Double.parseDouble(response.getString("impure"))/5);
                        t3.setText(khums+"");
                    }catch (Exception e){}
                }}, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("myapp", "Something went wrong Haha");
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
        }
    }

    public void submit(View v)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to submit?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //    Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_LONG).show();
                        pbEnable(true);

                        JsonObjectRequest jsonObjectRequest = null;
                        RequestQueue requestQueue = null;
                        JSONObject obj = new JSONObject();
                        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                        int d = calendar.get(Calendar.DATE);
                        int m = calendar.get(Calendar.MONTH);
                        int y = calendar.get(Calendar.YEAR);
                        String s=e1.getText().toString();
                        if(s.equals(""))s="0";
                        Log.d("myapp3",s);
                        khums+=Double.parseDouble(s);
                        if(khums==0){
                            pbEnable(false);
                            Toast.makeText(getApplicationContext(),"Khums is 0",Toast.LENGTH_LONG).show();
                            return;
                        }
                            String timestamp = d + "/" + m + "/" + y;
                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                        try {
                            String email=sharedPreferences.getString("email","");

                            obj.put("email", email);
                            obj.put("khums",khums);
                            obj.put("timestamp", timestamp);
                            obj.put("date", d);
                            obj.put("month", m);
                            obj.put("year", y);


                            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                                    "http://"+ip+"/paykhums", obj, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    pbEnable(false);

                                    try {
                                        if(response.getInt("status")==0)
                                            Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_LONG).show();
                                        else {
                                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                            intent.putExtra("balance",response.getDouble("balance"));
                                            intent.putExtra("khums",khums);
                                            intent.putExtra("name","pure");

                                            t1.setText(response.getDouble("pure")+"");
                                            t2.setText(response.getDouble("impure")+"");
                                            t3.setText(response.getDouble("balance")+"");

                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);

                                            //  Toast.makeText(getApplicationContext(), response.getString("msg")+" "+response.getDouble("pure"), Toast.LENGTH_LONG).show();
                                        }

                                    }catch (Exception e){}
                                }}, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("myapp", "Something went wrong Haha");
                                    pbEnable(false);
                                }
                            });
                            requestQueue.add(jsonObjectRequest);
                        } catch (Exception e) {
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                //  Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_LONG).show();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    public void pbEnable(boolean b)
    {
        if(b) {
            pb.setAlpha(1);
            pay.setEnabled(false);
        }
        else
        {
            pb.setAlpha(0);
            pay.setEnabled(true);
        }
    }


}
