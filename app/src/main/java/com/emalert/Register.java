package com.emalert;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
/**
 * Created by Sobhini on 10/11/2016.
 */
public class Register extends AppCompatActivity {

    EditText e_user,e_email,e_pass,e_repass,e_phone;
    Button br;
    Spinner e_cat;
    String asv[];
    String role;
    RadioButton b1,b2;
    ProgressDialog p;
    RequestQueue rq;
    JsonObjectRequest jor;
    String item;
    TextView help;
    private AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e_user=(EditText)findViewById(R.id.e_user);
        e_email=(EditText)findViewById(R.id.e_email);
        e_pass=(EditText)findViewById(R.id.e_pass);
        e_repass=(EditText)findViewById(R.id.e_pass);
        e_phone=(EditText)findViewById(R.id.e_phone);
        e_cat=(Spinner)findViewById(R.id.e_cat);
        br=(Button)findViewById(R.id.b_reg);
        help = (TextView)findViewById(R.id.help);
        b1=(RadioButton)findViewById(R.id.rstud);
        b2=(RadioButton)findViewById(R.id.remp);
        awesomeValidation  = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.e_user, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.e_email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.e_phone, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.e_pass,"((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})", R.string.passworderror);
        p = new ProgressDialog(Register.this);
        p.setMessage("Fetching Data...");
        p.setCancelable(false);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(Register.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(true);
                d.setContentView(R.layout.layout);
                TextView t = (TextView) d.findViewById(R.id.textView3);
                t.setText("USERNAME:   Must only contain letters and whitespace." +
                        "It should not conain any special charaters except a period. \n \n" +
                        "EMAIL ID:   Must contain a valid email address (with @ and .)\n \n" +
                        "PHONE NO.:   Only a 10 digit number \n \n" +
                        "PASSWORD:    Starts with a alphabet, it can be upper case and " +
                        "it should contain atleast one special character with atleast one digit");


                d.setTitle("Help :)");
                d.show();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arrayList = new ArrayList<String>();
                asv= new String[]{"1yr","2yr","3yr","4yr"};
                arrayList.add("1st Year");
                arrayList.add("2 Year");
                arrayList.add("3 Year");
                arrayList.add("4 Year");
                ((TextView) v).setTextColor(Color.RED);
                ArrayAdapter<String> ad = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,arrayList);
                ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                e_cat.setAdapter(ad);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> arrayList = new ArrayList<String>();
                asv= new String[]{"GrA","GrB","GrC"};
                arrayList.add("Group A");
                arrayList.add("Group B");
                arrayList.add("Group C");
                ((TextView) v).setTextColor(Color.RED);
                ArrayAdapter<String> ad = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,arrayList);
                ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                e_cat.setAdapter(ad);
            }
        });

        br.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(b1.isChecked())
                {
                    role="stud";
                }

                if(b2.isChecked())
                {
                    role="emp";
                }


                if (awesomeValidation.validate())
                {
                //    Toast.makeText(getApplicationContext(), "Validation Successfull", Toast.LENGTH_LONG).show();
                    fetch();
               //     finish();
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);

                }
                p = new ProgressDialog(Register.this);
                p.setMessage("Please wait...");
                p.setCancelable(false);

            }
        });

        e_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = asv[position];
                Toast.makeText(parent.getContext(),item,Toast.LENGTH_LONG).show();
                //((TextView) parent.getChildAt(0)).setTextColor(Color.RED);
                //((TextView) parent.getChildAt(0)).setTextSize(15);
                ((TextView) view).setTextColor(Color.RED);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void toast(String st)
    {
        Toast.makeText(getApplicationContext(),st,Toast.LENGTH_LONG).show();
    }
    private void fetch()
    {
        showpDialog();
        rq= Volley.newRequestQueue(getApplicationContext());
        String url="http://emalert.hol.es/index.php?user="+e_user.getText().toString()+
        "&pass="+e_pass.getText().toString()+
                "&phone="+e_phone.getText().toString()+
                "&email="+e_email.getText().toString()+
                "&role="+role+ "&cat="+item;
        url = url.replaceAll(" ", "%20");
        //toast("URL"+url);
        jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String chk= response.getString("check");
                            if(chk.equals("True"))
                            {
                                toast("Successfully Registered");
                                toast("Please Login Now...");
                                finish();
                            }
                            else
                                toast("Failed");
                        }
                        catch (JSONException e){
                            toast("Failed to Register");
                        }
                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley",error.toString());
                        toast(error.toString());
                    }
                });

        rq.add(jor);
    }
    private void showpDialog() {
        if (!p.isShowing())
            p.show();
    }

    private void hidepDialog() {
        if (p.isShowing())
            p.dismiss();
    }

}
