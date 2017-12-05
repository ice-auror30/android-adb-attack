/**
 * TODO: 1. Parse the response to update the ListView with the device id's
 * DONE
 * TODO: 2. Parse the response for different information received
 * DONE
 * TODO: 3. Implement onPause, onStop, onDelete?
 * NOT NEEDED
 */
package com.example.iceauror.adba;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerConnect extends ListActivity {
    //Global String for IP Address of Server
    String url= "HTTP://10.26.53.238:8000/test";
    String responseString;
    ArrayList<String> id_list= new ArrayList<String>();
    ArrayAdapter<String> adapter;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        url = intent.getStringExtra("Server_IP");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, id_list);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        new RequestTask().execute("");
        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.touchEvent:
                Intent touchEventIntent = new Intent(this, TouchEventRequest.class);
                touchEventIntent.putExtra("URL", url);
                touchEventIntent.putExtra("ID", (String)getListAdapter().getItem(info.position));
                startActivity(touchEventIntent);
                break;
            case R.id.sendRequests:
                Intent RequestIntent= new Intent(this, sendRequests.class);
                RequestIntent.putExtra("URL", url);
                RequestIntent.putExtra("LOG", filterResponse(responseString, (String)getListAdapter().getItem(info.position)));
                RequestIntent.putExtra("ID", (String)getListAdapter().getItem(info.position));
                startActivity(RequestIntent);
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     *
     * @param
     * @param
     * @param
     * @param
     */

    public void sendRequest() {
        //RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        //final TextView textView = (TextView) findViewById(R.id.textView);
        //Request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE FOR CODE 0",response);
                        responseString = response;
                        getDeviceID(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't Work!");
                Log.d("ERROR", error.getMessage());
                Toast.makeText(ServerConnect.this, "Ain't Connected", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("code", "0");
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private class RequestTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.d("537", "doing");
            for (int i = 0; i < 1; ) {
                try {
                    sendRequest();
                    publishProgress();
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    public void getDeviceID(String response)
    {
        adapter.clear();
        if(response != null) {
            String response_array[] = response.split("\n");
            for (int i = 0; i < response_array.length; i++) {
                if (response_array[i].contains("ID"))
                    adapter.add(response_array[i].substring(3));
            }
            Log.d("LIST_OF_ID", id_list.toString());
        }
    }
    public String filterResponse(String response, String ID)
    {
        Log.d("RESPONSEEEE", response);
        Log.d("IDDDD", ID);
        String filteredResponse;
        filteredResponse = response.substring(response.indexOf(ID), response.indexOf("videoAvailable"));
        Log.d("FILTERED_RESPONSE", filteredResponse);
        return filteredResponse;
    }

}