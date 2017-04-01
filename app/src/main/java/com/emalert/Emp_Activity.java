package com.emalert;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Emp_Activity extends AppCompatActivity {

    ListView lv;
    ProgressDialog p;
    RequestQueue rq;
    String user;
    SharedPreferences sh;

    JsonObjectRequest jor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_);
        lv = (ListView) findViewById(R.id.listView2);
        sh = getSharedPreferences("emAlert", Context.MODE_PRIVATE);
        user = sh.getString("user", null);
        fetch();

    }

    private void toast(String st) {
        Toast.makeText(getApplicationContext(), st, Toast.LENGTH_LONG).show();
    }

    public void fetch() {

        rq = Volley.newRequestQueue(getApplicationContext());
        String url = "http://emalert.hol.es/index.php?emp_name="+user;
        toast(url);
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            final ArrayList<Stu_req_data> datalist = new ArrayList<Stu_req_data>();
                            datalist.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jresponse = response.getJSONObject(i);
                                Stu_req_data datafetcher = new Stu_req_data();
                                datafetcher.setUser(jresponse.getString("stu_name"));
                                datafetcher.setMsg(jresponse.getString("msg"));
                                datalist.add(datafetcher);
                            }
                            Stu_req_data_adapter stu_req_data_adapter = new Stu_req_data_adapter(Emp_Activity.this,datalist);
                            lv.setAdapter(stu_req_data_adapter);
                        } catch (JSONException e) {
                            toast(e.toString());
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                        toast(error.toString());
                    }
                });

        rq.add(jar);
    }
}