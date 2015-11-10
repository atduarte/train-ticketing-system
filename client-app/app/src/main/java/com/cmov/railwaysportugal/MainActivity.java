package com.cmov.railwaysportugal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            token = extras.getString("TOKEN");
        }
        Button mNetworkMapButton = (Button) findViewById(R.id.networkmap);
        mNetworkMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,NetworkMapActivity.class);
                startActivity(i);
            }
        });

        Button mSearchBuy = (Button) findViewById(R.id.searchbuy);
        mSearchBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TimetableActivity.class);
                i.putExtra("TOKEN", token);
                startActivity(i);
            }
        });


        Button mTickets = (Button) findViewById(R.id.yourtickets);
        mTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyTicketsAcitivty.class);
                i.putExtra("TOKEN", token);
                startActivity(i);
            }
        });


    }
    @Override
    public void onBackPressed() {
        Intent i;
        i = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }

}
