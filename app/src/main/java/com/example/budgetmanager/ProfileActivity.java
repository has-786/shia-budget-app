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

import static com.example.budgetmanager.MainActivity.cancelAlarm;
import static com.example.budgetmanager.MainActivity.ip;

public class ProfileActivity extends AppCompatActivity {

    Button delete;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("My Profile"); // for set actionbar title
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.purple));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar

        final TextView name=findViewById(R.id.name);
        final TextView email=findViewById(R.id.email);
        final TextView t1=findViewById(R.id.t1);
        final TextView t2=findViewById(R.id.t2);
        delete=findViewById(R.id.delete);
        pb=findViewById(R.id.pb);
        t1.setText("Name");
        t2.setText("Email");

        SharedPreferences sharedPreferences=getSharedPreferences("", Context.MODE_PRIVATE);
        name.setText(sharedPreferences.getString("name",""));
        email.setText(sharedPreferences.getString("email",""));

    }

    public void delete(View v)
    {

        final SharedPreferences sharedPreferences=getSharedPreferences("", Context.MODE_PRIVATE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure want to delete your account permanently?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //    Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_LONG).show();
                        pbEnable(true);

                        JsonObjectRequest jsonObjectRequest = null;
                        RequestQueue requestQueue = null;
                        JSONObject obj = new JSONObject();
                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                        try {
                            String email=sharedPreferences.getString("email","");
                            obj.put("email", email);

                            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                                    "http://"+ip+"/delete", obj, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    pbEnable(false);

                                    try {
                                        Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                                        if(response.getInt("status")==1){
                                            final SharedPreferences sharedPreferences=getSharedPreferences("", Context.MODE_PRIVATE);
                                            final SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("name","");
                                            editor.putString("email","");
                                            editor.putString("alarmDate","");
                                            editor.putString("alarmTime","");
                                            editor.putString("mili","0");
                                            editor.commit();

                                            cancelAlarm(getApplicationContext());
                                            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }

                                    }catch (Exception e){}
                                }}, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("myapp", "Something went wrong Haha");
                                    Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

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
            delete.setEnabled(false);
        }
        else
        {
            pb.setAlpha(0);
            delete.setEnabled(true);
        }
    }







}
