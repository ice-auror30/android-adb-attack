package com.example.iceauror.adba;

import android.app.Activity;
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
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class TouchEventRequest extends Activity implements View.OnClickListener {
    String request;
    String url;
    String ID;
    String code;
    EditText touchInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_event_request);
        touchInput = (EditText) findViewById(R.id.touchE);
        //Log.d("TAP", request);
        Button touchGo = (Button) findViewById(R.id.sendtouchEvent);
        touchGo.setOnClickListener(this);
        Button sabotage1 = (Button) findViewById(R.id.sabotageEvent1);
        sabotage1.setOnClickListener(this);
        Button sabotage2 = (Button) findViewById(R.id.sabotageEvent2);
        sabotage2.setOnClickListener(this);
        Intent intent = getIntent();
        url = intent.getStringExtra("URL");
        ID = intent.getStringExtra("ID");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sendtouchEvent:
                request = touchInput.getText().toString();
                Log.d("TAPTAPTAPTPA", request);
                sendRequest(url, "tap ".concat(request));
                break;
            case R.id.sabotageEvent1:
                code = "sabotage1";
                sendRequest(url, code);
                break;
            case R.id.sabotageEvent2:
                code = "sabotage2";
                sendRequest(url, code);
                break;
        }
    }
    public void sendRequest(String url, final String requestCode) {
        //RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        //Request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                        Log.d("RESPONSE IN sendRequest", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.getMessage());
                Toast.makeText(getApplicationContext(), "Ain't Connected sendRequest", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ID", ID);
                params.put("code", requestCode);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
