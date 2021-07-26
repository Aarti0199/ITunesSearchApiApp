package com.example.itunessearchapi;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItem> listItems;

    public static final String TAG="MyTag";
    Button searchBtn;
    public String editTextSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItems=new ArrayList<>();


        final EditText textView=(EditText)findViewById(R.id.text);
        searchBtn=findViewById(R.id.searchButton);


        RequestQueue queue=Volley.newRequestQueue(this);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listItems.size()>0){
                    listItems.removeAll(listItems);
                    adapter.notifyDataSetChanged();
                }
                editTextSearch=textView.getText().toString();
                if (editTextSearch.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please write something",Toast.LENGTH_LONG).show();
                }
                else{
                    String url="https://itunes.apple.com/search?term="+editTextSearch+"&entity=musicTrack";

                    ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Loading data...");
                    progressDialog.show();


                    StringRequest stringRequest=new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject o = jsonArray.getJSONObject(i);
                                            ListItem item = new ListItem(
                                                    o.getString("artistName"),
                                                    o.getString("artistId")
                                            );
                                            listItems.add(item);
                                        }
                                        adapter = new MyAdapter(listItems, getApplicationContext());
                                        recyclerView.setAdapter(adapter);

                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            textView.setText("error");
                        }
                    });


                    stringRequest.setTag(TAG);

                    queue.add(stringRequest);

                }




            }
        });

        if(queue!=null) {
        queue.cancelAll(TAG);

        }


    }
}