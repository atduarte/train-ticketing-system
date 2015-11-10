package com.cmov.railwaysportugalback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cmov.railwaysportugalback.QRCode.QRCodeReader;
import com.cmov.railwaysportugalback.Ticket.Ticket;
import com.cmov.railwaysportugalback.Ticket.TicketParser;
import com.cmov.railwaysportugalback.Ticket.TicketValidator;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private final TicketParser ticketParser;
    private TicketValidator ticketValidator;

    public MainActivity() {
        this.ticketParser = new TicketParser();
        try {
            this.ticketValidator = new TicketValidator(
                Security.generatePublicKey("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAO3VWP2LVsIX81GR7lVyVI2Regsms0Xg\nEeqk8RVV8Dp9gbhIbrK7YwXuEtaHj/lsE73uXY81ODQARoKrGapnYk0CAwEAAQ==")
            );
        } catch (Exception ignored) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QRCodeReader qrCodeReader = new QRCodeReader();
        qrCodeReader.start(this);

        setContentView(R.layout.activity_main);
        findViewById(R.id.read_button).setOnClickListener(new QRCodeReader());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        String message;
        Ticket ticket;

        if (scanResult != null) {
            try {
                ticket = ticketParser.parse(scanResult.getContents());

                if (ticketValidator == null || !ticketValidator.validate(ticket)) {
                    throw new Exception();
                }

                message = ticket.getFrom() + " - " + ticket.getTo();
            } catch (Exception e) {
                message = "Invalid Ticket";
            }

            TextView view = (TextView)this.findViewById(R.id.ticket_info);
            view.setText(message);
            view.invalidate();
        }
    }

}
