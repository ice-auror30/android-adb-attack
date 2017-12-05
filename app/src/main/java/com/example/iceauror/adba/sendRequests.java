/**
 * TODO: 1. Set the log in the textView
 * DONE
 * TODO: 2. Set the video in the videoView
 * DONE
 */
package com.example.iceauror.adba;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class sendRequests extends Activity implements View.OnClickListener {
    String url;
    String deviceID;
    String log;
    String requestCode;
    TextView logView;
    VideoView videoView;
    ImageView imageView;
    MediaController vidControl;
    String fileName;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_requests);
        //UI objects
        logView = (TextView) findViewById(R.id.logView);
        videoView = (VideoView) findViewById(R.id.videoView);
        imageView = (ImageView) findViewById(R.id.imageView);
        Button logRequest = (Button) findViewById(R.id.logRequest);
        logRequest.setOnClickListener(this);
        Button videoRequest = (Button) findViewById(R.id.videoRequest);
        videoRequest.setOnClickListener(this);
        //Getting information from the intents
        Intent intent = getIntent();
        vidControl = new MediaController(this);
        url = intent.getStringExtra("URL");
        log = intent.getStringExtra("LOG");
        deviceID = intent.getStringExtra("ID");
        //Verify the app read and write permissions
        verifyStoragePermissions(this);
    }

    /**
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logRequest:
                requestCode = "log"; //Request Code for Video Log
                sendRequest(url, requestCode);
                break;
            case R.id.videoRequest:
                Toast.makeText(this, "Sending Request", Toast.LENGTH_SHORT).show();
                requestCode = "video";
                Log.d("VIDEO_REQUEST", "Requesting Video");
                sendVideoRequest(url); //request for the video URL
                //request for the Video
                fileName = "video1.mp4";
                break;
            /*case R.id.imageRequest:
                requestCode = "image";
                sendImageRequest(url);
                break;*/
        }
    }


    /**
     * Asynchronous Request for connection to the Server
     */
    private class RequestTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.d("sendRequestClass", "Log/Video Request");
            try {
                sendRequest(url, requestCode);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
            return null;
        }
    }

    /**
     * Sending Requests through Volley
     */
    public void sendRequest(String url, final String requestCode) {
        //RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        //Request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(sendRequests.this, "Request Sent", Toast.LENGTH_SHORT).show();
                        Log.d("RESPONSE IN sendRequest", response);
                        switch (requestCode) {
                            case ("log"):
                                videoView.setVisibility(View.INVISIBLE);
                                imageView.setVisibility(View.INVISIBLE);
                                logView.setVisibility(View.VISIBLE);
                                setlogViewText(response);
                                Log.d("FUCK!!", response);
                                break;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.getMessage());
                Toast.makeText(sendRequests.this, "Ain't Connected sendRequest", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ID", deviceID);
                params.put("code", requestCode);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     *
     * @param s
     */
    public void setlogViewText(final String s)
    {
        logView.setText(" ");
        final String logText = log.concat("\n").concat(s);
        final int[] i = new int[1];
        i[0] = 0;
        final int length = logText.length();
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(logText.isEmpty())
                    return;
                char c= logText.charAt(i[0]);
                logView.append(String.valueOf(c));
                i[0]++;
            }
        };

        final Timer timer = new Timer();
        TimerTask taskEverySplitSecond = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
                if (i[0] == length - 1) {
                    timer.cancel();
                }
            }
        };
        timer.schedule(taskEverySplitSecond, 1, 50);
    }


    /**
     * Sending Image Requests
     * Not using currently
     */
    public void sendImageRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                logView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(response);
            }
        }, 0, 0, null, null);
        // Add the request to the RequestQueue.
        queue.add(imageRequest);
    }

    /**
     * Video Requests
     */
    public void sendVideoRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                try {
                    if (response != null) {
                        FileOutputStream outputStream;
                        File file = new File(Environment.getExternalStorageDirectory(), fileName);
                        if(!file.exists())
                            file.createNewFile();
                        try{
                            outputStream = new FileOutputStream(file);
                            outputStream.write(response);
                            outputStream.flush();
                            outputStream.close();
                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                        Log.d("INSIDE", "OutputStream Closed");
                        Toast.makeText(getApplicationContext(), "Downloaded", Toast.LENGTH_SHORT).show();
                        logView.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoPath(Environment.getExternalStorageDirectory().getPath().concat("/"+fileName));
                        videoView.start();
                        //add playback controls
                        vidControl.setAnchorView(videoView);
                        videoView.setMediaController(vidControl);
                    }
                } catch (Exception e) {
                    Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ID", deviceID);
                params.put("code", requestCode);
                return params;
            }
        };
        queue.add(request);
    }
    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}