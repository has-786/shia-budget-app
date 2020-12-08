package com.example.budgetmanager.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetmanager.PassbookActivity;
import com.example.budgetmanager.R;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import static com.example.budgetmanager.MainActivity.ip;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    static TextView t1,t2,t3;
    static ProgressBar pb;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
          t1=root.findViewById(R.id.t1);
          t2 = root.findViewById(R.id.t2);
          t3 = root.findViewById(R.id.t3);
          pb=root.findViewById(R.id.pb);

          Intent i=getActivity().getIntent();
          if(i!=null)
          {
              String iname=i.getStringExtra("name");
              if(iname==null){}
              else {
                  if (iname.equals("pure")){
                      Double khums = i.getDoubleExtra("khums", 0);
                      Double balance = 0.0, exp = 0.0;
                      if (!t1.getText().toString().equals(""))
                          balance = Double.parseDouble(t1.getText().toString());
                      if (!t3.getText().toString().equals(""))
                          exp = Double.parseDouble(t3.getText().toString());

                      t1.setText((balance - khums) + "");
                      t3.setText((exp + khums) + "");
                  }
              else if (i.getStringExtra("name").equals("create")) {
                      Double debitN = i.getDoubleExtra("debit", 0);
                      Double creditN = i.getDoubleExtra("credit", 0);
                      Double balance = 0.0, credit = 0.0, debit = 0.0;
                      if (!t1.getText().toString().equals(""))
                          balance = Double.parseDouble(t1.getText().toString());
                      if (!t2.getText().toString().equals(""))
                          credit = Double.parseDouble(t2.getText().toString());
                      if (!t3.getText().toString().equals(""))
                          debit = Double.parseDouble(t3.getText().toString());

                      t1.setText((balance + creditN - debitN) + "");
                      t2.setText((credit + creditN) + "");
                      t3.setText((debit - debitN) + "");

                  }
              }

          }

        getDataForHome(getContext());
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    public static void getDataForHome(final Context context)
    {
        pb.setAlpha(1);
        final SharedPreferences sharedPreferences = context.getSharedPreferences("",Context.MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int d=calendar.get(Calendar.DATE);
        int m=calendar.get(Calendar.MONTH);
        int y=calendar.get(Calendar.YEAR);

        JsonObjectRequest jsonObjectRequest = null;
        RequestQueue requestQueue = null;
        JSONObject obj = new JSONObject();
        try {
            String email=sharedPreferences.getString("email","");
            obj.put("email", email);
            obj.put("date", d);
            obj.put("month", m);
            obj.put("year", y);

            requestQueue = Volley.newRequestQueue(context);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "http://"+ip+"/home", obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  try {
                      t1.setText(response.getString("balance"));
                      t2.setText(response.getString("credit"));
                      t3.setText(response.getString("debit"));
                      pb.setAlpha(0);

                   //  Toast.makeText(getContext(), response.getString("balance")+"", Toast.LENGTH_LONG).show();

                  }catch (Exception e){}
            }}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                     Log.d("myapp", "Something went wrong Haha");
                        pb.setAlpha(0);

                        Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
        }
    }





}