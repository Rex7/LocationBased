package com.example.regischarles.locationbased;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity implements View.OnClickListener {
    Button Sub;
    public EditText user, pass;
    RequestQueue requestQueue;
    SessionManage sessionManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        user = findViewById(R.id.PhoneNo);
        pass = findViewById(R.id.PassWord);
        Sub = findViewById(R.id.Login);
        Sub.setOnClickListener(this);
        sessionManage = new SessionManage(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_register)
            startActivity(new Intent(getApplicationContext(), Register.class));
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Login:
                final String phoneno = user.getText().toString().trim();
                String password = pass.getText().toString().trim();
                if (phoneno.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter Your PhoneNo", Toast.LENGTH_LONG).show();

                }
                if (password.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter Your password", Toast.LENGTH_LONG).show();

                } else {
                    StringRequest stringRequest;
                    requestQueue = VolleySingle.getInstance().getRequestQueue();
                    stringRequest = new StringRequest(Request.Method.POST, Urls.URL_LOGIN, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            String message;
                            try {
                                jsonObject = new JSONObject(response);
                                message = jsonObject.getString("message");
                                if (message.equals("successfull")) {
                                    sessionManage.createSession(phoneno);
                                    startActivity(new Intent(getApplicationContext(), ControlActivity.class));
                                }
                                Log.v("UniqueTag", "Message " + message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("phone", phoneno);
                            data.put("password", pass.getText().toString());


                            return data;

                        }
                    };


                    requestQueue.add(stringRequest);


                }
                break;
        }
    }
}
