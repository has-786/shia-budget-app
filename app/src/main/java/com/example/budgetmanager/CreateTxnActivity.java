package com.example.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class CreateTxnActivity extends AppCompatActivity {
    EditText e1, e2;
    RadioButton r;
    RadioGroup rg;
    CheckBox ch,ch1;
RadioButton debit,credit;
ProgressBar pb;
ConstraintLayout rel;
Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_txn);
        e1 = findViewById(R.id.topic);
        e2 = findViewById(R.id.amount);
        rg = findViewById(R.id.radioGroup);
        credit= findViewById(R.id.radioCredit);
        debit= findViewById(R.id.radioDebit);
         ch=findViewById(R.id.checked);
        ch1=findViewById(R.id.checked1);

        pb=findViewById(R.id.pb);
        rel=findViewById(R.id.rel);
        submit=findViewById(R.id.submit);
        getSupportActionBar().setTitle("Create Transaction");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.purple));

    }

    public void createTxn(View v) {
      final   String topic = e1.getText().toString();
       String amt=e2.getText().toString();
       if(amt.equals(""))amt="0";
        final  Double amount = Double.parseDouble(amt);
        int status1=1;
        int selectedId = rg.getCheckedRadioButtonId();
        r = findViewById(selectedId);
        if (selectedId == -1) {
            Toast.makeText(getApplicationContext(), "Select Credit or Debit", Toast.LENGTH_SHORT).show();
        } else if (topic.equals("") || amount.equals(0)) {
            Toast.makeText(getApplicationContext(), "Please Fill The Places Correctly", Toast.LENGTH_SHORT).show();
        } else {
            r = findViewById(selectedId);
            String s = r.getText().toString();
            //Toast.makeText(this,s,Toast.LENGTH_LONG).show();
            if (s.equals("Debit")) status1 = -1;
            final int status=status1;
            final SharedPreferences sharedPreferences = getSharedPreferences("", Context.MODE_PRIVATE);


            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            final int d = calendar.get(Calendar.DATE);
            final int m = calendar.get(Calendar.MONTH);
            final int y = calendar.get(Calendar.YEAR);
            final int pure = (ch.isChecked() && status==-1) ? 1 : 0;
            final int pure1 = (ch1.isChecked()  && status==1) ? 1 : 0;

           final String timestamp = d + "/" + m + "/" + y;


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure want to submit?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                //    Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_LONG).show();
                                    pbEnable(true);

                                    JsonObjectRequest jsonObjectRequest = null;
                                    RequestQueue requestQueue = null;
                                    JSONObject obj = new JSONObject();
                                    try {
                                        String email = sharedPreferences.getString("email", "");
                                        obj.put("email", email);
                                        obj.put("topic", topic);
                                        obj.put("amount", amount);
                                        obj.put("status", status);
                                        obj.put("timestamp", timestamp);
                                        obj.put("date", d);
                                        obj.put("month", m);
                                        obj.put("year", y);
                                        obj.put("pure",pure);
                                        obj.put("pure1",pure1);

                                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                                        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                                                "http://"+ip+"/createTxn", obj, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                pbEnable(false);

                                                try {
                                                    if (response.getInt("status") == 1) {
                                                        Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                                                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                                        intent.putExtra("name","create");

                                                        if(status==-1)
                                                        {
                                                            intent.putExtra("debit",amount);
                                                            intent.putExtra("credit",0);
                                                        }
                                                        else
                                                        {
                                                                intent.putExtra("debit",0);
                                                                intent.putExtra("credit",amount);
                                                        }

                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);

                                                    } else
                                                        Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();

                                                } catch (Exception e) {
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();

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


    }


    public void check(View v)
    {
        if(debit.isChecked()){
            ch.setAlpha(1);
            ch.setEnabled(true);
            ch1.setAlpha(0);
            ch1.setEnabled(false);
        }
        else {
            ch1.setAlpha(1);
            ch1.setEnabled(true);
            ch.setAlpha(0);
            ch.setEnabled(false);
        }
    }

    public void pbEnable(boolean b)
    {
        if(b) {
            pb.setAlpha(1);
            submit.setEnabled(false);
        }
        else
        {
            pb.setAlpha(0);
            submit.setEnabled(true);
        }
    }


}
