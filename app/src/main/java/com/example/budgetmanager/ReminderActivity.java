package com.example.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.ListResourceBundle;

import static com.example.budgetmanager.MainActivity.cancelAlarm;
import static com.example.budgetmanager.MainActivity.ip;
import static com.example.budgetmanager.MyBroadcastReceiver.mp;

public class ReminderActivity extends AppCompatActivity {

    TextView t1,t2,t3;
    Button date,time,submit;
    long mili=0,miliC=0,miliTime=0,miliDate=0;
    String alarmTime="",alarmDate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        getSupportActionBar().setTitle("Khums Reminder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.purple));


        Intent i=getIntent();
        if(i!=null && i.getStringExtra("notification")!=null)
            mp.stop();



        t1=findViewById(R.id.t1);
        t2=findViewById(R.id.t2);
        t3=findViewById(R.id.t3);

        date=findViewById(R.id.date);
        time=findViewById(R.id.time);
        submit=findViewById(R.id.submit);

        final SharedPreferences sharedPreferences=getSharedPreferences("",Context.MODE_PRIVATE);
        alarmTime=sharedPreferences.getString("alarmTime","");
        alarmDate=sharedPreferences.getString("alarmDate","");
        t1.setText("Reminder set for "+alarmDate+" "+sharedPreferences.getString("alarmTime",""));
        t2.setText(alarmDate);
        t3.setText(alarmTime);



        if(alarmTime.equals("") || alarmDate.equals(""))
            t1.setText("Reminder is not yet set");

        miliC= Long.parseLong(sharedPreferences.getString("mili",""));
        if(miliC!=0) {
            String h=alarmTime.substring(0, 2),m=alarmTime.substring(3, 5);
            if(h.charAt(0)=='0')h=h.charAt(1)+"";
            if(m.charAt(0)=='0')m=m.charAt(1)+"";

            miliTime = (Long.parseLong(h) * 3600 + Long.parseLong(m) * 60) * 1000;
            Log.d("myapp4",h+" "+m);
            miliDate = miliC - miliTime;
            Log.d("myapp4","mili "+miliC);
            Log.d("myapp4","miliDate "+miliDate);
        }

    }



    public void handleDateButton(final View v) {
        final Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);
        //final long miliTemp=calendar.getTimeInMillis()+1000;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                String monthS=month+"",dateS=date+"";
                if(month/10==0)monthS="0"+monthS;
                if(date/10==0)dateS="0"+dateS;

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);

                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.YEAR, year);
                calendar2.set(Calendar.MONTH, month);
                calendar2.set(Calendar.DATE, date);

                alarmDate=dateS+"/"+monthS+"/"+year;
                t2.setText(alarmDate);
                Calendar current=Calendar.getInstance();
               // Log.d("myapp6",(calendar1.getTimeInMillis()-calendar2.getTimeInMillis())/1000+"");
                long currentTime=(current.get(Calendar.HOUR_OF_DAY)*3600+current.get(Calendar.MINUTE)*60+current.get(Calendar.SECOND))*1000;
                // String dateText = DateFormat.format("EEEE, MMM d, yyyy", calendar1).toString();
                miliDate=calendar1.getTimeInMillis()-currentTime;
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    public void handleTimeButton(View v) {
        final Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.d("tag", "onTimeSet: " + hour + minute);
                String hourS=hour+"",minuteS=minute+"";
                if(hour/10==0)hourS="0"+hourS;
                if(minute/10==0)minuteS="0"+minuteS;

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR, hour);
                calendar1.set(Calendar.MINUTE, minute);
                miliTime=(hour*3600+minute*60)*1000;
                alarmTime=hourS+":"+minuteS;
                t3.setText(alarmTime);
                Log.d("myapp5",""+mili);


            }
        }, HOUR, MINUTE, true);

        timePickerDialog.show();

    }
    public  void submit(View v)
    {
        if(alarmDate.equals("") || alarmTime.equals("")){
            Toast.makeText(getApplicationContext(),"Set Date & Time",Toast.LENGTH_LONG).show();
            return;
        }
        final Calendar calendar = Calendar.getInstance();
        //final long miliTemp=calendar.getTimeInMillis()+1000;
        int requestCode=2;


        Log.d("myapp5",mili+" "+calendar.get(Calendar.MINUTE));

        mili=miliDate+miliTime;
        Log.d("myapp5",mili+" "+calendar.getTimeInMillis());
        Log.d("myapp4",""+(mili-calendar.getTimeInMillis())/1000);

        if(mili<calendar.getTimeInMillis()){
            Toast.makeText(this,"Please select future date and time",Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent  = new Intent(this, MyBroadcastReceiver.class);
        intent.setAction("SomeAction");
       //  alarmManager.cancel(pendingIntent);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, mili, pendingIntent);

      //  Toast.makeText(this,"Start",Toast.LENGTH_LONG).show();

        final SharedPreferences sharedPreferences=getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("alarmDate",alarmDate);
        editor.putString("alarmTime",alarmTime);
        editor.putString("mili",Long.toString(mili));
        editor.commit();

        t1.setText("Reminder set for "+sharedPreferences.getString("alarmDate","")+" "+sharedPreferences.getString("alarmTime",""));
        storeReminder();
        Snackbar.make(v,"Reminder set for "+alarmDate+" "+alarmTime,Snackbar.LENGTH_LONG).show();

    }





    public void storeReminder()
    {
        JsonObjectRequest jsonObjectRequest=null;
        RequestQueue requestQueue=null;
        JSONObject obj=new JSONObject();
        final SharedPreferences sharedPreferences=getSharedPreferences("",Context.MODE_PRIVATE);
        final String email=sharedPreferences.getString("email","");
        final String alarmDate=sharedPreferences.getString("alarmDate","");
        final String alarmTime=sharedPreferences.getString("alarmTime","");
        try {
            obj.put("email",email);
            obj.put("alarmDate", alarmDate);
            obj.put("alarmTime", alarmTime);
            obj.put("mili",mili+"");

            requestQueue = Volley.newRequestQueue(this);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "http://"+ip+"/storereminder", obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d("myapp",response.getString("name"));
                        //Toast.makeText(getApplicationContext(),response.getString("name"), Toast.LENGTH_LONG).show();

                    }catch (Exception e){}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("myapp", "Something went wrong Haha");
                    //   Toast.makeText(VideoActivity.this, "error", Toast.LENGTH_LONG);
                }
            });
            requestQueue.add(jsonObjectRequest);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }catch (Exception e){}
    }




    public  void clear(View v){
        final SharedPreferences sharedPreferences=getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();
        mili=0;
        alarmDate="";
        alarmTime="";

        editor.putString("alarmDate",alarmDate);
        editor.putString("alarmTime",alarmDate);
        editor.putString("mili",mili+"");
        editor.commit();
        t1.setText("Reminder is not yet set");
        t2.setText(""); t3.setText("");
        storeReminder();
        cancelAlarm(getApplicationContext());
        Toast.makeText(this,"Cleared the reminder",Toast.LENGTH_LONG).show();
    }
}
