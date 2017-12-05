package com.example.iceauror.adba;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView logoView = (ImageView)findViewById(R.id.logoView);
        Button send_request = (Button)findViewById(R.id.send_request);
    }
    public void sendRequest(View view) {
        TextView command_text = (TextView) findViewById(R.id.command);
        String Server_IP = command_text.getText().toString();
        Intent intent = new Intent(this, ServerConnect.class);
        intent.putExtra("Server_IP", Server_IP);
        startActivity(intent);
    }
}


