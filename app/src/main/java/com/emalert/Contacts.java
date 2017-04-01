package com.emalert;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

public class Contacts extends AppCompatActivity {

    Button rag,vio,aci,nat,kid;
    SharedPreferences sh;
    RequestQueue rq;
    JsonObjectRequest jor;
    String user;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        rag=(Button)findViewById(R.id.rag);
        vio=(Button)findViewById(R.id.vio);
        aci=(Button)findViewById(R.id.aci);
        nat=(Button)findViewById(R.id.nat);
        kid=(Button)findViewById(R.id.kid);
        sh = getSharedPreferences("emAlert", Context.MODE_PRIVATE);
        user=sh.getString("user",null);

        rag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set_con();
                final Dialog d = new Dialog(Contacts.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(true);
                d.setContentView(R.layout.contacts);
                Button temp = (Button) d.findViewById(R.id.save);
                final EditText rag1, rag2, rag3, rag4;
                rag1 = (EditText) d.findViewById(R.id.pri1);
                rag2 = (EditText) d.findViewById(R.id.pri2);
                rag3 = (EditText) d.findViewById(R.id.pri3);
                rag4 = (EditText) d.findViewById(R.id.pri4);
                Fetch_No("rag",rag1,rag2,rag3,rag4);


                temp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences.Editor ed;
                        ed = sh.edit();
                        ed.putString("rag1", rag1.getText().toString());
                        ed.putString("rag2", rag2.getText().toString());
                        ed.putString("rag3", rag3.getText().toString());
                        ed.putString("rag4", rag4.getText().toString());
                        ed.commit();
                        //finish();
                        setCon("rag");
                        alert("Saved List !!");
                        d.hide();
                    }
                });
                d.show();
                pd = new ProgressDialog(Contacts.this);
                pd.setMessage("Fetching Data...");
                pd.setCancelable(false);
                showpDialog();
            }
        });
        vio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set_con();
                final Dialog d = new Dialog(Contacts.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(true);
                d.setContentView(R.layout.contacts);
                Button temp = (Button) d.findViewById(R.id.save);
                final EditText vio1, vio2, vio3, vio4;
                vio1 = (EditText) d.findViewById(R.id.pri1);
                vio2 = (EditText) d.findViewById(R.id.pri2);
                vio3 = (EditText) d.findViewById(R.id.pri3);
                vio4 = (EditText) d.findViewById(R.id.pri4);
                Fetch_No("vio",vio1,vio2,vio3,vio4);
                temp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences.Editor ed;
                        ed = sh.edit();
                        ed.putString("vio1", vio1.getText().toString());
                        ed.putString("vio2", vio2.getText().toString());
                        ed.putString("vio3", vio3.getText().toString());
                        ed.putString("vio4", vio4.getText().toString());
                        ed.commit();
                        setCon("vio");
                        //finish();
                        alert("Saved List !!");
                        d.hide();
                    }
                });
                d.show();
                pd = new ProgressDialog(Contacts.this);
                pd.setMessage("Fetching Data...");
                pd.setCancelable(false);
                showpDialog();
            }
        });

        aci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set_con();
                final Dialog d = new Dialog(Contacts.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(true);
                d.setContentView(R.layout.contacts);
                Button temp = (Button) d.findViewById(R.id.save);
                final EditText aci1, aci2, aci3, aci4;
                aci1 = (EditText) d.findViewById(R.id.pri1);
                aci2 = (EditText) d.findViewById(R.id.pri2);
                aci3 = (EditText) d.findViewById(R.id.pri3);
                aci4 = (EditText) d.findViewById(R.id.pri4);
                Fetch_No("aci",aci1,aci2,aci3,aci4);
                temp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences.Editor ed;
                        ed = sh.edit();
                        ed.putString("aci1", aci1.getText().toString());
                        ed.putString("aci2", aci2.getText().toString());
                        ed.putString("aci3", aci3.getText().toString());
                        ed.putString("aci4", aci4.getText().toString());
                        ed.commit();
                        //finish();
                        setCon("aci");

                        alert("Saved List !!");
                        d.hide();
                    }
                });
                d.show();
                pd = new ProgressDialog(Contacts.this);
                pd.setMessage("Fetching Data...");
                pd.setCancelable(false);
                showpDialog();
            }
        });

        nat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set_con();
                final Dialog d = new Dialog(Contacts.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(true);
                d.setContentView(R.layout.contacts);
                Button temp = (Button) d.findViewById(R.id.save);
                final EditText nat1, nat2, nat3, nat4;
                nat1 = (EditText) d.findViewById(R.id.pri1);
                nat2 = (EditText) d.findViewById(R.id.pri2);
                nat3 = (EditText) d.findViewById(R.id.pri3);
                nat4 = (EditText) d.findViewById(R.id.pri4);
                Fetch_No("nat",nat1,nat2,nat3,nat4);
                temp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences.Editor ed;
                        ed = sh.edit();
                        ed.putString("nat1", nat1.getText().toString());
                        ed.putString("nat2", nat2.getText().toString());
                        ed.putString("nat3", nat3.getText().toString());
                        ed.putString("nat4", nat4.getText().toString());
                        ed.commit();
                        //finish();
                        setCon("nat");

                        alert("Saved List !!");
                        d.hide();
                    }
                });
                d.show();
                pd = new ProgressDialog(Contacts.this);
                pd.setMessage("Fetching Data...");
                pd.setCancelable(false);
                showpDialog();
            }
        });

        kid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   set_con();

                final Dialog d = new Dialog(Contacts.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(true);
                d.setContentView(R.layout.contacts);
                Button temp = (Button) d.findViewById(R.id.save);
                final EditText kid1, kid2, kid3, kid4;
                kid1 = (EditText) d.findViewById(R.id.pri1);
                kid2 = (EditText) d.findViewById(R.id.pri2);
                kid3 = (EditText) d.findViewById(R.id.pri3);
                kid4 = (EditText) d.findViewById(R.id.pri4);
                Fetch_No("kid",kid1,kid2,kid3,kid4);
                temp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences.Editor ed;
                        ed = sh.edit();
                        ed.putString("kid1", kid1.getText().toString());
                        ed.putString("kid2", kid2.getText().toString());
                        ed.putString("kid3", kid3.getText().toString());
                        ed.putString("kid4", kid4.getText().toString());
                        ed.commit();
                        //finish();
                        setCon("kid");
                        alert("Saved List !!");
                        d.hide();
                    }
                });
                d.show();
                pd = new ProgressDialog(Contacts.this);
                pd.setMessage("Fetching Data...");
                pd.setCancelable(false);
                showpDialog();
            }
        });
    }

    private void alert(String st) {
        Toast.makeText(getApplicationContext(), st, Toast.LENGTH_LONG).show();
    }


    // Log.i("Send SMS ", "");
    private void setCon(String tag)
    {
        rq= Volley.newRequestQueue(getApplicationContext());

        String url="http://emalert.hol.es/index.php?username="+user+
                "&tag="+tag+"&num1="+sh.getString(tag+"1",null)+"&num2="+sh.getString(tag+"2",null)+"&num3="+sh.getString(tag+"3",null)+"&num4="+sh.getString(tag+"4",null);
        alert("URL"+url);
        jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String chk= response.getString("check");
                            if(chk.equals("True"))
                            {
                                alert("all ok");
                                //finish();
                            }
                            else
                                alert("Failed");
                        }
                        catch (JSONException e){
                            alert(e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley",error.toString());
                        alert(error.toString());
                    }
                });

        rq.add(jor);
    }

    public void Fetch_No(String tag, final EditText e1, final EditText e2, final EditText e3, final EditText e4)
    {
        int count=0;

        rq= Volley.newRequestQueue(getApplicationContext());
        String url="http://emalert.hol.es/index.php?username="+user+
                "&tag="+tag;
        alert("URL"+url);
        jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("JsonArray",response.toString());
                            if (response.getString("num1").equals("null"))
                                e1.setText("");
                            else
                            e1.setText(response.getString("num1"));


                            if (response.getString("num2").equals("null"))
                                e2.setText("");
                            else
                            e2.setText(response.getString("num2"));


                            if (response.getString("num3").equals("null"))
                                e1.setText("");
                            else
                            e3.setText(response.getString("num3"));


                            if (response.getString("num4").equals("null"))
                                e1.setText("");
                            else
                                e4.setText(response.getString("num4"));
                        }
                        catch (JSONException e){
                            alert(e.toString());
                        }
                        hidepDialog();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley",error.toString());
                        alert(error.toString());
                    }
                });

        rq.add(jor);
    }
    private void showpDialog() {
        if (!pd.isShowing())
            pd.show();
    }

    private void hidepDialog() {
        if (pd.isShowing())
            pd.dismiss();
    }


}


