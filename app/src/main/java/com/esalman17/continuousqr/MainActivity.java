package com.esalman17.continuousqr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainActivity";

    DecoratedBarcodeView dbvScanner;
    TextView tvResult;
    Switch switchInv;
    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.textViewResult);
        dbvScanner = (DecoratedBarcodeView) findViewById(R.id.dbv_barcode);
        requestPermission();

        dbvScanner.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                tvResult.setText("Result: "+ result);
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });


        switchInv = findViewById(R.id.switchInv);
        switchInv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                    integrator.addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.INVERTED_SCAN);
                    Intent inverted = integrator.createScanIntent();
                    dbvScanner.initializeFromIntent(inverted);
                    //integrator.initiateScan();
                }
                else{
                    IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                    Intent normal = integrator.createScanIntent();
                    dbvScanner.initializeFromIntent(normal);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CUSTOMIZED_REQUEST_CODE && requestCode != IntentIntegrator.REQUEST_CODE) {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        switch (requestCode) {
            case CUSTOMIZED_REQUEST_CODE: {
                Toast.makeText(this, "REQUEST_CODE = " + requestCode, Toast.LENGTH_LONG).show();
                break;
            }
            default:
                break;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result.getContents() == null) {
            Log.d("MainActivity", "Cancelled scan");
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        } else {
            Log.d("MainActivity", "Scanned");
            Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!dbvScanner.isActivated()){
            dbvScanner.resume();
        }
        Log.d(TAG, "resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbvScanner.pause();
        Log.d(TAG, "paused");
    }


    void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults.length < 1) {
            requestPermission();
        } else {
            dbvScanner.resume();
        }
    }

}
