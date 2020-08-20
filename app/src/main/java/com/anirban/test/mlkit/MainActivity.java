package com.anirban.test.mlkit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.anirban.test.mlkit.facerecognition.ImageFaceDetectionActivity;

public class MainActivity extends AppCompatActivity
{
    private static final String LOGTAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_FACE_RECOGNITION_IMAGEFILE = 100;

    Button btnDetectFacesImageFile, btnDetectFacesCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasPermissions())
        {
            getPermissions();
            return;
        }

        initViews();
    }

    void getPermissions()
    {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    boolean hasPermissions()
    {
        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (hasPermissions()) initViews();
    }

    void initViews()
    {
        btnDetectFacesImageFile = findViewById(R.id.btnDetectFacesImageFile);
        btnDetectFacesImageFile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showImageFileChooser();
            }
        });

        btnDetectFacesCamera = findViewById(R.id.btnDetectFacesCamera);
        btnDetectFacesCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // todo: implement face detection from camera feed
            }
        });
    }

    void showImageFileChooser()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try
        {
            startActivityForResult(Intent.createChooser(intent, "Select image file"),
                    REQUEST_CODE_FACE_RECOGNITION_IMAGEFILE);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(LOGTAG, "File chooser missing in system...");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_FACE_RECOGNITION_IMAGEFILE:
                if (resultCode != RESULT_OK)
                {
                    Log.e(LOGTAG, "image file was not chosen");
                    return;
                }

                if (data == null)
                {
                    Log.e(LOGTAG, "no data received from file chooser");
                    return;
                }

                Uri fileUri = data.getData();
                if (fileUri == null)
                {
                    Log.e(LOGTAG, "file Uri is null !");
                    return;
                }

                //
                // open face detection activity with this image
                //
                Intent intent = new Intent(this, ImageFaceDetectionActivity.class);
                intent.setData(fileUri);
                startActivity(intent);

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}