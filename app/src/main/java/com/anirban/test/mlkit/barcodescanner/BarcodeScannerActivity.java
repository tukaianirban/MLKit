package com.anirban.test.mlkit.barcodescanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.anirban.test.mlkit.R;

public class BarcodeScannerActivity extends AppCompatActivity
{
    PreviewView pvBarcodeScanner;
    Button btnCaptureBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        initViews();
    }

    void initViews()
    {
        pvBarcodeScanner = findViewById(R.id.pvBarcodeScanner);

        btnCaptureBarcode = findViewById(R.id.btnCaptureBarcode);
        btnCaptureBarcode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });
    }
}