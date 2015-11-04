package com.cmov.railwaysportugal;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

public class NetworkMapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_map);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
