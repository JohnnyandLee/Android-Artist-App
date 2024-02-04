package com.example.myapplicationtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SecondActivity extends AppCompatActivity {
    private RequestQueue queue;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private LinkedList<HashMap<String,String>> data;
    private MyAdapter myAdapter;

    private int numberOfArtist;
    private LinkedList<HashMap<String,String>> artistName;

    //private ProgressBar spinner;
    private LinearLayout spinner;
    private String[] artistEnd;
    private TextView noShow;
    private String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        queue = Volley.newRequestQueue(this);

        getSupportActionBar().hide();
        Intent intent = getIntent();
        input = intent.getStringExtra("input").toUpperCase();

        TextView textView = findViewById(R.id.topinput);
        textView.setText(input);
        searchEndpoint(input);

        Log.v("Johnny", input);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        doData();

        //myAdapter = new MyAdapter();
        //recyclerView.setAdapter(myAdapter);

        //spinner = (ProgressBar)findViewById(R.id.progressBar2);
        spinner = findViewById(R.id.progressBar2);
        recyclerView.setVisibility(View.GONE);

        noShow = findViewById(R.id.noShow);
        noShow.setVisibility(View.GONE);
    }

    private void doData(){
        data = new LinkedList<>();
        for(int i=0; i<5;i++){
            HashMap<String,String> row = new HashMap<>();
            int random = (int)(Math.random()*100);
            row.put("title","Title: " + random);
            row.put("date","Date: " + random);
            data.add(row);
        }
        Log.v("cat",""+artistName.size());
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        private Context context;

        public MyAdapter(Context context){
            this.context = context;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            public View itemView;
            public TextView firstName, lastName;
            public ImageView artistImg;

            public MyViewHolder(View view) {
                super(view);
                itemView = view;

                firstName = itemView.findViewById(R.id.firstName);
                lastName = itemView.findViewById(R.id.lastName);
                artistImg = itemView.findViewById(R.id.imageView);
            }
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
            MyViewHolder vh = new MyViewHolder(itemView);

            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
             //照brad練習
             //holder.firstName.setText(data.get(position).get("title"));
             //holder.lastName.setText(data.get(position).get("date"));
             //照brad練習
            holder.firstName.setText(artistName.get(position).get("first"));
            holder.lastName.setText(artistName.get(position).get("last"));
            Picasso.get().load(artistName.get(position).get("url")).resize(720,1080).into(holder.artistImg);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String wholeName = artistName.get(position).get("first") + " " + artistName.get(position).get("last");
                    Log.v("click", "" + artistEnd[position]);
                    thirdPage(artistEnd[position], wholeName);
                }
            });
        }

        @Override
        public int getItemCount() {
            //return data.size(); //照brad練習
            //return 5;
            return artistName.size();
        }
    }


    public void turnback(View view){
        finish();
    }

    public void thirdPage(String address, String name){
        Intent intent = new Intent(SecondActivity.this,ThirdActivity.class);
        intent.putExtra("address",address);
        intent.putExtra("name",name);
        intent.putExtra("addornot","false");
        startActivity(intent);
    }

    private void searchEndpoint(String input){
        artistName = new LinkedList<>();
        String url = "https://ktlhw8backend.wl.r.appspot.com/show?name=" + input;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //convert string to JSONArray and get each element
                        try {
                            spinner.setVisibility(View.GONE);
                            JSONObject jsnobject = new JSONObject(response);
                            JSONArray jsonArray = jsnobject.getJSONArray("title");
                            JSONArray jsonArrayImg = jsnobject.getJSONArray("img");
                            JSONArray jsonArrayArEnd = jsnobject.getJSONArray("artistEnd");
                            if(jsonArray.length()>0){
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            else{
                                noShow.setVisibility(View.VISIBLE);
                            }

                            artistEnd = new String[jsonArray.length()];
                            //String test = jsonArray.getString(0);
                            //Log.v("htt", test);
                            int num = 0;
                            for(int i=0; i<jsonArray.length(); i++){
                                HashMap<String,String> name = new HashMap<>();
                                String wholeName = jsonArray.getString(i);
                                String imgurl = jsonArrayImg.getString(i);
                                if(wholeName.contains(" ")) {
                                    String[] splitName = wholeName.split(" ");
                                    name.put("first", splitName[0]);
                                    name.put("last", splitName[1]);
                                    name.put("url", imgurl);
                                    artistName.add(name);

                                    artistEnd[num] = jsonArrayArEnd.getString(i);
                                    //Log.v("artistend",artistEnd[num]);
                                    num++;
                                }
                            }
                            numberOfArtist = num;
                            Log.v("htt", "" + numberOfArtist);
                            myAdapter = new MyAdapter(getApplicationContext());
                            recyclerView.setAdapter(myAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //Log.v("htt", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("htt","That didn't work!");
            }
        });
        //{
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String,String>();
//                params.put("name", input);
//                return params;
//            }
        //};
        queue.add(stringRequest);
    }
}