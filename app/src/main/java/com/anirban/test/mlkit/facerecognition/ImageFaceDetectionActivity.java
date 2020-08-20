package com.anirban.test.mlkit.facerecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anirban.test.mlkit.MainActivity;
import com.anirban.test.mlkit.R;

/**
 * This Activity shows up the Image selected by the user along with the face classification results
 */
public class ImageFaceDetectionActivity extends AppCompatActivity implements FaceClassificationListener
{
    private Uri fileUri;

    ImageView ivSelectedImage;
    TextView tvSmilingProbability, tvLeftEyeOpenProbability, tvRightEyeOpenProbability;
    LinearLayout llClassificationStats;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_face_detection);

        Intent intent = getIntent();
        if (intent == null) return;

        fileUri = intent.getData();
        if (fileUri == null) return;

        initViews();

        ivSelectedImage.setImageURI(fileUri);

        //
        // start the face detection process
        //
        FaceRecognition faceRecognition = new FaceRecognition(this, this);
        faceRecognition.detectFacesInImage(fileUri);
    }

    void initViews()
    {
        ivSelectedImage = findViewById(R.id.ivSelectedImage);

        llClassificationStats = findViewById(R.id.llClassificationStats);

        tvSmilingProbability = findViewById(R.id.tvSmilingProbability);
        tvLeftEyeOpenProbability = findViewById(R.id.tvLeftEyeOpenProbability);
        tvRightEyeOpenProbability = findViewById(R.id.tvRightEyeOpenProbability);
    }

    @SuppressLint("SetTextI18n")
    public void setFaceClassificationValues(Float smiling, Float leftEyeOpen, Float rightEyeOpen)
    {
        if (tvSmilingProbability == null || tvLeftEyeOpenProbability == null || tvRightEyeOpenProbability == null) return;

        tvSmilingProbability.setText(smiling + " %");
        tvLeftEyeOpenProbability.setText(leftEyeOpen + " %");
        tvRightEyeOpenProbability.setText(rightEyeOpen + " %");
    }

    public void setFaceFound(boolean faceFound)
    {
        if (faceFound)
        {
            llClassificationStats.setVisibility(View.VISIBLE);
        }
        else
        {
            llClassificationStats.setVisibility(View.INVISIBLE);
        }
    }
}