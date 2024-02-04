package com.example.myapplicationtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private RequestQueue queue;

    private LinkedList<HashMap<String,String>> artistName;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainActivity.MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#112B3C"));
        actionBar.setBackgroundDrawable(colorDrawable);

//        actionBar.setTitle(Html.fromHtml("<font color=\"white\">" + getString(R.string.app_name) + "</font>"));

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        String[] splitDate = currentDate.split(",");
        String[] splitDate2 = splitDate[0].split(" ");

        TextView textViewDate = findViewById(R.id.view_date);
        textViewDate.setText(splitDate2[1]);

        TextView textViewMonth = findViewById(R.id.view_month);
        textViewMonth.setText(splitDate2[0]);

        TextView textViewYear = findViewById(R.id.view_year);
        textViewYear.setText(splitDate[1]);

        TextView artlink = (TextView) findViewById(R.id.powerby);
        artlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.artsy.net/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        //doData();

//        SharedPreferences prefs = getSharedPreferences("transfer", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putInt("current", 0);
//        editor.commit();
//        int current = prefs.getInt("current",0);
//        Log.v("abcdefg",current + "");

        queue = Volley.newRequestQueue(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search_action);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search...");


//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                CharSequence input = searchView.getQuery();
                String userInput = input.toString();

                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                intent.putExtra("input",userInput);
                startActivity(intent);

                //Log.v("Johnny",userInput);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TextView getnamefromthird = findViewById(R.id.getnamefromthird);
        SharedPreferences prefs = getSharedPreferences("transfer", MODE_PRIVATE);
        int current = prefs.getInt("current",0);
        Log.v("abcdefg",current + "");

        artistName = new LinkedList<>();
        if(current>0){
//            artistName = new LinkedList<>();
            for(int i=0; i<current; i++){
                HashMap<String,String> row = new HashMap<>();
                String name = prefs.getString("NameOfTransfer" + i ,"");
                String birth = prefs.getString("BirthOfTransfer" + i,"");
                String nation = prefs.getString("NationOfTransfer" + i ,"");
                String address = prefs.getString("AddressOfThirdpage" + i ,"");
                Log.v("abcdefg", name);
                row.put("name",name);
                row.put("year",birth);
                row.put("nation",nation);
                row.put("address",address);
                artistName.add(row);
            }
            myAdapter = new MainActivity.MyAdapter(getApplicationContext());
            recyclerView.setAdapter(myAdapter);
        }
        else{
            myAdapter = new MainActivity.MyAdapter(getApplicationContext());
            recyclerView.setAdapter(myAdapter);
        }
        //String testit = prefs.getString("NameOfTransfer","");
//        if (!testit.equals("")) {
//            getnamefromthird.setText(testit);
//        }
//        Log.v("see",testit);
//        Log.v("see",prefs.getString("NationOfTransfer",""));
//        Log.v("see",prefs.getString("BirthOfTransfer",""));
//        Log.v("see","" + prefs.getInt("current",0));
    }


    private class MyAdapter extends RecyclerView.Adapter<MainActivity.MyAdapter.MyViewHolder>{

        private Context context;

        public MyAdapter(Context context){
            this.context = context;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            public View itemViewf;
            public TextView returnName, returnYear,returnNation;
            public ImageButton imgButton;

            public MyViewHolder(View view) {
                super(view);
                itemViewf = view;

                returnName = itemViewf.findViewById(R.id.returnName);
                returnYear = itemViewf.findViewById(R.id.returnYear);
                returnNation = itemViewf.findViewById(R.id.returnNation);
                imgButton = itemViewf.findViewById(R.id.imgButton);

            }
        }

        @NonNull
        @Override
        public MainActivity.MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.itemf, parent, false);
            MainActivity.MyAdapter.MyViewHolder vh = new MainActivity.MyAdapter.MyViewHolder(itemView);

            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MainActivity.MyAdapter.MyViewHolder holder, int position) {
            holder.returnName.setText(artistName.get(position).get("name"));
            holder.returnYear.setText(artistName.get(position).get("year"));
            holder.returnNation.setText(artistName.get(position).get("nation"));


            holder.imgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String address = artistName.get(position).get("address");
                    Log.v("address", address);
                    String name = artistName.get(position).get("name");
                    Intent intent = new Intent(MainActivity.this,ThirdActivity.class);
                    intent.putExtra("address",address);
                    intent.putExtra("name",name);
                    intent.putExtra("addornot","true");
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return artistName.size();
        }
    }

    //    public void test1(View view){
//        final TextView textView = (TextView) findViewById(R.id.mesg);
//        String url = "https://www.google.com";
//        StringRequest stringRequest = new StringRequest(Request.Method.GET,
//                url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        //Log.v("brag",response);
//                        textView.setText("Response is: " + response.substring(0,500));
//                    }
//                }, new Response.ErrorListener() {
//                   @Override
//                   public void onErrorResponse(VolleyError error) {
//                      //Log.v("brag","That didn't work!");
//                       textView.setText("That didn't work!");
//                   }
//                });
//        queue.add(stringRequest);
//    }
    private void doData(){
        artistName = new LinkedList<>();
        HashMap<String,String> row = new HashMap<>();
        row.put("name","Pablo Picasso");
        row.put("year","1881");
        row.put("nation","Spanish");
        artistName.add(row);

        HashMap<String,String> row2 = new HashMap<>();
        row2.put("name","Claude Monet");
        row2.put("year","1840");
        row2.put("nation","French");
        artistName.add(row2);

        myAdapter = new MainActivity.MyAdapter(getApplicationContext());
        recyclerView.setAdapter(myAdapter);

    }
}