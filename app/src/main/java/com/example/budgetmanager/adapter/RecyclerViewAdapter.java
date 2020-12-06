package com.example.budgetmanager.adapter;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetmanager.MainActivity;
import com.example.budgetmanager.R;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.budgetmanager.MainActivity.ip;

public  class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {
    private Context context;
    private JSONArray constArr;
    private JSONArray arr;


    public RecyclerViewAdapter(Context context,JSONArray arr) {
        this.context = context;
        this.constArr=arr;
        this.arr=arr;


    }

    // Where to get the single card as viewholder Object
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    // What will happen after we create the viewholder object
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        try {
            Log.d("myapp2",arr.getJSONObject(position)+"");
            JSONObject obj = arr.getJSONObject(position);
            holder.topic.setText(obj.getString("topic"));
            holder.amount.setText("Rs. "+obj.getString("amount"));
            holder.date.setText(obj.getString("timestamp"));
            holder.id.setText(obj.getString("_id"));

        }
        catch(Exception e){}
    }

    // How many items?
    @Override
    public int getItemCount() {
        return arr.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView topic,amount,date,id;
        Button delete;
        ProgressBar pb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            topic= itemView.findViewById(R.id.topic);
            amount= itemView.findViewById(R.id.amount);
            date= itemView.findViewById(R.id.date);
            id= itemView.findViewById(R.id.id);
            delete= itemView.findViewById(R.id.del);
            pb= itemView.findViewById(R.id.pb);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertBeforeDel(id.getText().toString(),pb);

                }
            });

        }

        @Override
        public void onClick(View view) {
            Log.d("ClickFromViewHolder", "Clicked");
            if(view.getId()==R.id.del)
            {
                delete(id.getText().toString(),pb);
            }

        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            JSONArray filteredList = new JSONArray();

            if (constraint == null || constraint.length() == 0) {
                filteredList=constArr;
            } else {
                try{  String filterPattern = constraint.toString().toLowerCase().trim();
                    String[] p=filterPattern.split(" ");
                    for (int j=0;j<p.length;j++)
                    {
                        for (int i=0;i<constArr.length();i++) {

                            if (constArr.getJSONObject(i).getString("topic").toLowerCase().contains(p[j])
                                    || constArr.getJSONObject(i).getString("timestamp").toLowerCase().contains(p[j])) {
                                filteredList.put(constArr.getJSONObject(i));}
                        }
                    }

                }catch (Exception e){}
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arr=new JSONArray();
            arr=(JSONArray) results.values;
            notifyDataSetChanged();
        }
    };



    public void delete(final String id,final ProgressBar pb)
    {
        final SharedPreferences sharedPreferences=context.getSharedPreferences("",Context.MODE_PRIVATE);
        String email=sharedPreferences.getString("email","");

        JsonObjectRequest jsonObjectRequest = null;
        RequestQueue requestQueue = null;
        JSONObject obj = new JSONObject();
        try {
            obj.put("email", email);
            obj.put("id", id);
            obj.put("status", arr.getJSONObject(0).getInt("status"));

            requestQueue = Volley.newRequestQueue(context);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "http://" + ip + "/deleteTxn", obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    pb.setAlpha(1);

                    try {
                        //     Toast.makeText(getApplicationContext(),response+"",Toast.LENGTH_LONG).show();
                        if (response.getInt("status") == 1) {

                            Log.d("myapp",response+"");
                            // Toast.makeText(getApplicationContext(),response.getString("alarmDate")+" "+response.getString("alarmTime"),Toast.LENGTH_LONG).show();
                            arr=response.getJSONArray("txn");
                            notifyDataSetChanged();
                            Toast.makeText(context, response.getString("msg"), Toast.LENGTH_LONG).show();
                            pb.setAlpha(0);

                        } else
                            Toast.makeText(context,  response.getString("msg"), Toast.LENGTH_LONG).show();
                            pb.setAlpha(0);


                    } catch (Exception e) {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("myapp", "Something went wrong Haha");
                    //   Toast.makeText(VideoActivity.this, "error", Toast.LENGTH_LONG);
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                    pb.setAlpha(0);

                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
        }


    }




    public  void alertBeforeDel(final String id,final ProgressBar pb){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Deleting this will delete all the transactions made after this. Do you still want to delete?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        delete(id,pb);
                    }
                });
        alertDialogBuilder.setNegativeButton("no",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}


