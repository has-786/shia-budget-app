package com.example.budgetmanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    public static String ip="192.168.0.7:8080/shiabudget";
//    public static String ip="thelasthope.site";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.purple));
        final SharedPreferences sharedPreferences=getSharedPreferences("",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();
        Double mili=Double.parseDouble(sharedPreferences.getString("mili","0"));
        if(mili!=0 && mili<Calendar.getInstance().getTimeInMillis()-60000){
            cancelAlarm(getApplicationContext());
            editor.putString("mili","0");
            editor.putString("alarmDate","");
            editor.putString("alarmTime","");
            editor.commit();
            storeReminder();
        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
    public void logout()
    {
        final SharedPreferences sharedPreferences=getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        storeReminder();
        editor.putString("name","");
        editor.putString("email","");
        editor.putString("alarmDate","");
        editor.putString("alarmTime","");
        editor.putString("mili","0");

        editor.commit();

        cancelAlarm(getApplicationContext());


        startActivity(new Intent(this,LoginActivity.class));
        finish();

    }

    public void pure(View v)
    {
        //startActivity(new Intent(getApplicationContext(),PureActivity.class));
        Intent i = new Intent(this, PureActivity.class);
        startActivity(i);
    }
    public void create(View v)
    {

        Intent i = new Intent(this, CreateTxnActivity.class);
        startActivity(i);
    }

    public  void setReminder(View v)
    {
        startActivity(new Intent(this,ReminderActivity.class));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.example_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){

            case R.id.item4:
                startActivity(new Intent(this,ProfileActivity.class));
                return true;

            case R.id.item1:
                How();
                return true;

            case R.id.item2:
                Intent email= new Intent(Intent.ACTION_SENDTO);
                email.setData(Uri.parse("mailto:shiabudget@gmail.com"));
                startActivity(email);

                return true;

            case R.id.item3:
                logout();
                return  true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


public  static void cancelAlarm(Context context)
{
    Intent intent  = new Intent(context, MyBroadcastReceiver.class);
    intent.setAction("SomeAction");
    //  alarmManager.cancel(pendingIntent);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    if(pendingIntent!=null)alarmManager.cancel(pendingIntent);


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
    final String mili=sharedPreferences.getString("mili","");
    //Toast.makeText(this,alarmDate+" "+alarmTime,Toast.LENGTH_LONG).show();

    try {
        obj.put("email",email);
        obj.put("alarmDate", alarmDate);
        obj.put("alarmTime", alarmTime);
        obj.put("mili", mili);

        requestQueue = Volley.newRequestQueue(this);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://"+ip+"/storereminder", obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status")==1){
                       // Toast.makeText(getApplicationContext(),"Successfully Registered", Toast.LENGTH_LONG).show();
                    }
                    else {}
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



    public  void How(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("How to use");
        alertDialogBuilder.setMessage("A budget management app to save your earnings and expenditures records\n\n" +
                "And see them in Credits and Debits tab respectively\n\n" +
                "It's very useful for Shias as it keeps track of Khums\n\n" +
                "Here Pure Balance is the balance after paying Khums or the amount for which Khums is not applicable\n\n" +
                "So we consider Khums for the balance excluding pure balance\n\n" +
                "You can also set reminder for Khums\n");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
