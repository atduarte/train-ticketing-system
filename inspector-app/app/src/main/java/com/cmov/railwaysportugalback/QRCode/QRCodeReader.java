package com.cmov.railwaysportugalback.QRCode;

import android.app.Activity;
import android.view.View;

import com.cmov.railwaysportugalback.MainActivity;
import com.google.zxing.integration.android.IntentIntegrator;

public class QRCodeReader implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        MainActivity activity = (MainActivity) v.getContext();
        start(activity);
    }

    public void start(Activity activity) {
        IntentIntegrator intentIntegrator = new IntentIntegrator(activity);
        intentIntegrator.initiateScan();
    }
}
