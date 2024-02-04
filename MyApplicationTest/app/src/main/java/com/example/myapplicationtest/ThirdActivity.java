package com.example.myapplicationtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class ThirdActivity extends AppCompatActivity {
    private String address;
    private String name;
    private TextView leftLine;
    private TextView rightLine;
    private LinearLayout infoLayout;
    private LinearLayout spinner;
    private RequestQueue queue;
    private TextView artistName;
    private TextView artistNation;
    private TextView artistBirth;
    private TextView artistDeath;
    private TextView artistBio;
    private TextView noShow2;
    private String[] geneID;
    private String artworks;

    private LinkedList<HashMap<String,String>> artwork;
    private ThirdActivity.MyAdapter myAdapter2;
    private RecyclerView recyclerView2;
    private RecyclerView.LayoutManager mLayoutManager2;

    private LinearLayout nationLayout;
    private LinearLayout birthLayout;
    private LinearLayout deathLayout;
    private LinearLayout bioLayout;

    private Dialog dialog1;
    private Dialog dialog2;

    private Boolean addOrNot;
    private String nationality;
    private String birthday;


//    private FragmentManager fmgr;
//    private DetailFragment detail;
//    private ArtworkFragment artwork;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        queue = Volley.newRequestQueue(this);

        getSupportActionBar().hide();
        Intent intent = getIntent();
        address = intent.getStringExtra("address");
        name = intent.getStringExtra("name");
        if(intent.getStringExtra("addornot").equals("false")){
            addOrNot = false;
        }
        else{
            addOrNot = true;
        }

        TextView textView = findViewById(R.id.topinput3);
        textView.setText(name);
        Log.v("inthree",address);

        rightLine = findViewById(R.id.rightline);
        rightLine.setVisibility(View.GONE);
        leftLine = findViewById(R.id.leftline);

//        fmgr = getSupportFragmentManager();
//        detail = new DetailFragment();
//        artwork = new ArtworkFragment();
//
//        FragmentTransaction transaction = fmgr.beginTransaction();
//        transaction.add(R.id.frameContainer, detail);
//        transaction.commit();

        //detail.getInfoLayout().setVisibility(View.GONE);
        recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView2.setHasFixedSize(true);
        mLayoutManager2 = new LinearLayoutManager(this);
        recyclerView2.setLayoutManager(mLayoutManager2);

        infoLayout = findViewById(R.id.infoLayout);
        infoLayout.setVisibility(View.GONE);

        spinner = findViewById(R.id.progressBar3);
        artistName = findViewById(R.id.artistName);
        artistNation = findViewById(R.id.artistNation);
        artistBirth = findViewById(R.id.artistBidth);
        artistDeath = findViewById(R.id.artistDeath);
        artistBio = findViewById(R.id.artistBio);
        nationLayout = findViewById(R.id.nationLayout);
        birthLayout = findViewById(R.id.birthLayout);
        deathLayout = findViewById(R.id.deathLayout);
        bioLayout = findViewById(R.id.bioLayout);

        recyclerView2 = findViewById(R.id.recyclerView2);

        recyclerView2.setVisibility(View.GONE);

        noShow2 = findViewById(R.id.noShow2);
        noShow2.setVisibility(View.GONE);

        artwork = new LinkedList<>();

        getDetail(address);

        dialog1 = new Dialog(this);
        dialog2 = new Dialog(this);

        ImageButton star = findViewById(R.id.star);
        if(addOrNot == false){
            star.setImageResource(R.drawable.starwhite);
        }
        else{
            star.setImageResource(R.drawable.starblack);
        }

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                SharedPreferences prefs = getSharedPreferences("transfer", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if(addOrNot==false){
                    star.setImageResource(R.drawable.starblack);
                    Toast.makeText(context,name + " is added to favorites",Toast.LENGTH_SHORT).show();
                    addOrNot = true;

                    int current = prefs.getInt("current", 0);
                    editor.putString("NameOfTransfer" + current , name);
                    editor.putString("BirthOfTransfer" + current , birthday);
                    editor.putString("NationOfTransfer" + current , nationality);
                    editor.putString("AddressOfThirdpage" + current , address);
                    editor.putInt("current", current+1);

                    editor.commit();
                }
                else{
                    star.setImageResource(R.drawable.starwhite);
                    Toast.makeText(context,name + " is removed from favorites",Toast.LENGTH_SHORT).show();
                    addOrNot = false;

                    int current = prefs.getInt("current", 0);
                    editor.putInt("current", current - 1);
                    editor.commit();
                }
            }
        });
    }

    public void turnback(View view){
        finish();
    }

    public void turnleft(View view){
//        FragmentTransaction transaction = fmgr.beginTransaction();
//        transaction.replace(R.id.frameContainer, detail);
//        transaction.commit();
        recyclerView2.setVisibility(View.GONE);
        leftLine.setVisibility(View.VISIBLE);
        rightLine.setVisibility(View.GONE);
        infoLayout.setVisibility(View.VISIBLE);
        noShow2.setVisibility(View.GONE);
    }

    public void turnright(View view){
//        FragmentTransaction transaction = fmgr.beginTransaction();
//        transaction.replace(R.id.frameContainer, artwork);
//        transaction.commit();
        rightLine.setVisibility(View.VISIBLE);
        leftLine.setVisibility(View.GONE);
        infoLayout.setVisibility(View.GONE);
        recyclerView2.setVisibility(View.VISIBLE);
        if(artwork.size()==0) {
            searchArtwork(artworks);
        }

    }

    public void getDetail(String address){
        String url = "https://ktlhw8backend.wl.r.appspot.com/artistEndpointTest?address=" + address;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //convert string to JSONArray and get each element
                        try {
                            JSONObject jsnobject = new JSONObject(response);
                            String name = jsnobject.getString("name");
                            nationality = jsnobject.getString("nationality");
                            birthday = jsnobject.getString("birthday");
                            String deathday = jsnobject.getString("deathday");
                            String biography = jsnobject.getString("biography");
                            artworks = jsnobject.getString("artworks");

                            Log.v("htt3",artworks);

                            artistName.setText(name);
                            artistNation.setText(nationality);
                            artistBirth.setText(birthday);
                            artistDeath.setText(deathday);
                            artistBio.setText(biography);


                            if(nationality.equals("")){
                                nationLayout.setVisibility(View.GONE);
                            }

                            if(birthday.equals("")){
                                birthLayout.setVisibility(View.GONE);
                            }

                            if(deathday.equals("")){
                                deathLayout.setVisibility(View.GONE);
                            }

                            if(biography.equals("")){
                                bioLayout.setVisibility(View.GONE);
                            }

                            spinner.setVisibility(View.GONE);
                            infoLayout.setVisibility(View.VISIBLE);

                            //searchArtwork(artworks);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("htt","That didn't work!");
            }
        });
        queue.add(stringRequest);
    }

    private void searchArtwork(String address){
        String url = "https://ktlhw8backend.wl.r.appspot.com/artworkEndpointTest?address=" + address;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //convert string to JSONArray and get each element
                        try {
                            JSONObject jsnobject = new JSONObject(response);
                            JSONArray jsonArray = jsnobject.getJSONArray("title");
                            JSONArray jsonArrayImg = jsnobject.getJSONArray("image");
                            JSONArray jsonArrayID = jsnobject.getJSONArray("id");
                            if(jsonArray.length()>0){
                                recyclerView2.setVisibility(View.VISIBLE);
                            }
                            else{
                                noShow2.setVisibility(View.VISIBLE);
                            }

                            geneID = new String[jsonArray.length()];

                            for(int i=0; i<jsonArray.length(); i++){
                                HashMap<String,String> name = new HashMap<>();
                                String title = jsonArray.getString(i);
                                String image = jsonArrayImg.getString(i);
                                String id  = jsonArrayID.getString(i);

                                name.put("title", title);
                                name.put("image", image);
                                name.put("id", id);
                                artwork.add(name);
                            }

                            myAdapter2 = new ThirdActivity.MyAdapter(getApplicationContext());
                            recyclerView2.setAdapter(myAdapter2);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("htt","That didn't work!");
            }
        });
        queue.add(stringRequest);
    }

    private class MyAdapter extends RecyclerView.Adapter<ThirdActivity.MyAdapter.MyViewHolder>{

        private Context context;

        public MyAdapter(Context context){
            this.context = context;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            public View itemView2;
            public TextView artworkName;
            public ImageView artworkImg;

            public MyViewHolder(View view) {
                super(view);
                itemView2 = view;

                artworkName = itemView2.findViewById(R.id.artworkName);
                artworkImg = itemView2.findViewById(R.id.artworkView);
            }
        }

        @NonNull
        @Override
        public ThirdActivity.MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView2 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item2, parent, false);
            ThirdActivity.MyAdapter.MyViewHolder vh = new ThirdActivity.MyAdapter.MyViewHolder(itemView2);

            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ThirdActivity.MyAdapter.MyViewHolder holder, int position) {
            //照brad練習
            //holder.firstName.setText(data.get(position).get("title"));
            //holder.lastName.setText(data.get(position).get("date"));
            //照brad練習
            holder.artworkName.setText(artwork.get(position).get("title"));
            Picasso.get().load(artwork.get(position).get("image")).resize(720,580).into(holder.artworkImg);

            holder.itemView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = artwork.get(position).get("id");

                    Log.v("clickID", "" + id);
                    openNormalDialog(id);
                }
            });
        }

        @Override
        public int getItemCount() {
            return artwork.size();
        }
    }

    public void openNormalDialog(String id){
        String url = "https://ktlhw8backend.wl.r.appspot.com/geneEndpointTest?id=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //convert string to JSONArray and get each element
                        try {
                            JSONObject jsnobject = new JSONObject(response);
                            JSONArray jsonArray = jsnobject.getJSONArray("artname");
                            JSONArray jsonArrayImg = jsnobject.getJSONArray("image");
                            JSONArray jsonArrayDes = jsnobject.getJSONArray("description");
                            String title = "";
                            String image = "";
                            String id = "";

                            if(jsonArray.length()>0){
                                title = jsonArray.getString(0);
                                image = jsonArrayImg.getString(0);
                                id  = jsonArrayDes.getString(0);
                                //open dialog1
                                dialog1.setContentView(R.layout.dialog1);
                                TextView dialogTitle = dialog1.findViewById(R.id.dialogTitle);
                                dialogTitle.setText(title);

                                ImageView dialogImage = dialog1.findViewById(R.id.dialogImage);
                                Picasso.get().load(image).resize(300,300).into(dialogImage);

                                TextView dialogDe = dialog1.findViewById(R.id.dialogDe);
                                dialogDe.setText(id);

                                dialog1.show();
                            }
                            else{
                                //open dialog2
                                dialog2.setContentView(R.layout.dialog2);
                                dialog2.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("htt","That didn't work!");
            }
        });
        queue.add(stringRequest);

    }
}