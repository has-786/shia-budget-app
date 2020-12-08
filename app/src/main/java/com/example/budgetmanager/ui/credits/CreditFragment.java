package com.example.budgetmanager.ui.credits;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetmanager.R;
import com.example.budgetmanager.adapter.RecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.budgetmanager.MainActivity.ip;

public class CreditFragment extends Fragment {

    private CreditViewModel creditViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        creditViewModel =
                ViewModelProviders.of(this).get(CreditViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_credits, container, false);


        creditViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        SharedPreferences sharedPreferences=this.getActivity().getSharedPreferences("",Context.MODE_PRIVATE);
        final RecyclerView recyclerView=root.findViewById(R.id.recycle1);
        final ProgressBar pb=root.findViewById(R.id.pb);

        //Toast.makeText(QuizActivity.this,"Hi Syed",Toast.LENGTH_LONG).show();;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        JsonArrayRequest  jsonArrayRequest = null;
        RequestQueue requestQueue = null;
        try {
            pb.setAlpha(1);
            String email=sharedPreferences.getString("email","");
            JSONArray arr=new JSONArray();
            JSONObject obj=new JSONObject();
            obj.put("email",email);
            arr.put(obj);
            requestQueue = Volley.newRequestQueue(getContext());
            jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,
                    "http://"+ip+"/creditAll",arr , new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        Log.d("myapp1",response.length()+"");

                        final RecyclerViewAdapter  recyclerViewAdapter = new RecyclerViewAdapter(getContext(),response);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        searchbar(root,recyclerViewAdapter);
                        pb.setAlpha(0);


                    }catch (Exception e){}
                }}, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("myapp", "Something went wrong Haha");
                    pb.setAlpha(0);
                    Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();


                    //   Toast.makeText(VideoActivity.this, "error", Toast.LENGTH_LONG);
                }
            });
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
        }






        return root;
    }






    public void searchbar(View root,final RecyclerViewAdapter recyclerViewAdapter){
        final SearchView searchView = root.findViewById(R.id.search1);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setClickable(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (recyclerViewAdapter == null) return false;
                recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }

        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

    }
    }




