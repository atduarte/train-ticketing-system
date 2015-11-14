package com.cmov.railwaysportugalback.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.cmov.railwaysportugalback.QRCode.QRCodeReader;
import com.cmov.railwaysportugalback.R;
import com.cmov.railwaysportugalback.Security;
import com.cmov.railwaysportugalback.Ticket.Ticket;
import com.cmov.railwaysportugalback.Ticket.TicketManager;
import com.cmov.railwaysportugalback.Ticket.TicketParser;
import com.cmov.railwaysportugalback.Ticket.TicketValidator;
import com.cmov.railwaysportugalback.ApiData;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final TicketParser ticketParser;
    private TicketValidator ticketValidator;
    private TicketManager ticketManager = new TicketManager();
    private PieChart chart;

    protected Integer lineNumber;
    protected Integer departure;
    protected Integer total;

    public MainActivity() {
        this.ticketParser = new TicketParser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineNumber = getIntent().getExtras().getInt("lineNumber");
        departure = getIntent().getExtras().getInt("departure");
        total = getIntent().getExtras().getInt("capacity");

        try {
            this.ticketValidator = new TicketValidator(Security.generatePublicKey(ApiData.publicKey));
        } catch (Exception ignored) {}

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getIntent().getExtras().getString("name"));
            ab.setSubtitle(String.format("%02d", departure / 60) + ":" + String.format("%02d", departure % 60));
        }

        this.chart = (PieChart) findViewById(R.id.chart);
        chart.setDrawHoleEnabled(false);
        chart.getLegend().setEnabled(false);
        drawChart();

        findViewById(R.id.read_button).setOnClickListener(new QRCodeReader());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        String message;
        Ticket ticket;

        do {
            if (scanResult == null) {
                message = "Invalid. Scan failed";
                break;
            }

            try {
                ticket = ticketParser.parse(scanResult.getContents());
            } catch (Exception e) {
                message = "Invalid. Read failed.";
                break;
            }

            if (ticketValidator == null || !ticketValidator.validate(ticket)) {
                message = "Invalid. Validation failed";
                break;
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (!Objects.equals(ticket.getLineNumber(), lineNumber)
                    || !Objects.equals(ticket.getDeparture(), departure)
                    || dateFormat.format(new Date()).equals(ticket.getDate())) {
                message = "Invalid. Wrong trip";
                break;
            }

            message = "Valid. From " + ticket.getFrom() + " to " + ticket.getTo() + ".";

            // Check if used
            if (ticketManager.isUsed(ticket)) {
                message += " Already used";
            } else {
                ticketManager.add(ticket);
            }

            break;
        } while (true);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Ticket");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        alertDialog.show();
        drawChart();
    }

    protected void drawChart()
    {
        Integer ticketsValidated = ticketManager.get().size();
        PieDataSet dataSet = new PieDataSet(new ArrayList<>(Arrays.asList(new Entry(Math.max(total - ticketsValidated, 0), 0), new Entry(ticketsValidated, 1))), "");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);
        chart.setData(new PieData(new ArrayList<>(Arrays.asList("Total", "Validated")), dataSet));
        chart.invalidate();
    }

}
