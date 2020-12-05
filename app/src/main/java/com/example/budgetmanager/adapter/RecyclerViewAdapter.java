package com.example.budgetmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetmanager.R;

import org.json.JSONArray;
import org.json.JSONObject;
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

        }
        catch(Exception e){}
    }

    // How many items?
    @Override
    public int getItemCount() {
        return arr.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView topic,amount,date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            topic= itemView.findViewById(R.id.topic);
            amount= itemView.findViewById(R.id.amount);
            date= itemView.findViewById(R.id.date);

        }

        @Override
        public void onClick(View view) {
            Log.d("ClickFromViewHolder", "Clicked");

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
}


