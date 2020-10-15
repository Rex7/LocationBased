package com.example.regischarles.locationbased;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity implements View.OnClickListener {
    EditText User,pass,email,phone,passcheck;
    String userName,passCode,email_id,phoneNumber;
    Button bsubmit;
    RequestQueue requestQueue;
    int rand,random;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_mainpage);
        User= findViewById(R.id.editText);
        pass=  findViewById(R.id.editText2);
        email=  findViewById(R.id.editText3);
        phone= findViewById(R.id.editText4);
        passcheck=findViewById(R.id.PasswordCheck);
        bsubmit=findViewById(R.id.button);


        bsubmit.setOnClickListener(this);









    }
    public void check(int ran) {
        String pattern="[a-zA-Z_0-9]+[.|_|*]?[a-zA-Z_0-9]@[a-zA-Z]+([.][a-zA-Z]+)+";


        userName = User.getText().toString();
        passCode = pass.getText().toString();
        email_id = email.getText().toString();
        phoneNumber = phone.getText().toString();
        random = ran;
        StringRequest stringRequest;

       Toast.makeText(getApplicationContext(),"Email Validation"+email_id.matches(pattern),Toast.LENGTH_SHORT).show();
        String pass1=pass.getText().toString().trim();
        String pass2=passcheck.getText().toString().trim();
        if (userName.length()!=0&&pass1.equals(pass2)) {



            requestQueue=VolleySingle.getInstance().getRequestQueue();
            stringRequest= new StringRequest(Request.Method.POST, Urls.URL_REGISTER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject jsonObject;
                    String message;
                    try {
                        jsonObject = new JSONObject(response);
                        message = jsonObject.getString("message");
                        if(message.equals("User already registered")){
                            Toast.makeText(getApplicationContext(),"User already registered",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MainPage.class));
                        }
                        else if(message.equals("User registered successfully")){
                            Toast.makeText(getApplicationContext(),"User  registered",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MainPage.class));

                        }

                        Log.v("UniqueTag","Message "+message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.v("response ","data "+response);
                }
            },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> data = new HashMap<>();
                    data.put("phone", phoneNumber);
                    data.put("password", pass.getText().toString());
                    data.put("username",userName);
                    data.put("email",email_id);


                    return data;

                }
            };


            requestQueue.add(stringRequest);







        } else {
            Toast.makeText(getApplicationContext(), "Enter Details   properly", Toast.LENGTH_LONG).show();

        }


    }




    @Override
    public void onClick(View v) {
        switch (v.getId())
        {

            case R.id.button:

                check(rand);
                break;

        }

    }
}
