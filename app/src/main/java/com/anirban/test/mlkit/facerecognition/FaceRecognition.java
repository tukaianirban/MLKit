package com.anirban.test.mlkit.facerecognition;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.io.File;
import java.util.List;

public class FaceRecognition
{
    final static String LOGTAG = FaceRecognition.class.getSimpleName();

    final static int[] faceLandmarkTypes = new int[]{
            FaceLandmark.LEFT_EAR,
            FaceLandmark.RIGHT_EAR,
            FaceLandmark.LEFT_EYE,
            FaceLandmark.RIGHT_EYE,
            FaceLandmark.LEFT_CHEEK,
            FaceLandmark.RIGHT_CHEEK,
            FaceLandmark.MOUTH_BOTTOM,
            FaceLandmark.MOUTH_LEFT,
            FaceLandmark.MOUTH_RIGHT,
            FaceLandmark.NOSE_BASE,
    };

    final static String[] faceLandmarkNames = new String[]{
            "LEFT_EAR",
            "RIGHT_EAR",
            "LEFT_EYE",
            "RIGHT_EYE",
            "LEFT_CHEEK",
            "RIGHT_CHEEK",
            "MOUTH_BOTTOM",
            "MOUTH_LEFT",
            "MOUTH_RIGHT",
            "NOSE_BASE",
    };

    private Context context;
    private FaceClassificationListener faceClassificationListener;

    public FaceRecognition(@NonNull Context context, FaceClassificationListener classificationListener)
    {
        this.context = context;
        this.faceClassificationListener = classificationListener;
    }

    @NonNull
    static FaceDetectorOptions getDefaultDetectionOptions()
    {
        // landmark detection, face classification, performance mode, facial contours
        return new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build();
    }

    @Nullable
    static InputImage getImageFile(@NonNull Context context, @NonNull File file)
    {
        try
        {
            return InputImage.fromFilePath(context, Uri.fromFile(file));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    static InputImage getImageFile(@NonNull Context context, @NonNull Uri fileUri)
    {
        try
        {
            return InputImage.fromFilePath(context, fileUri);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    void printFaceDetails(int index, Face face)
    {
        final String PREFIX = "Face(" + index + "): ";

        //
        // print Euler angle of the head
        //
        if (face.getHeadEulerAngleX() > 0.0) Log.d(LOGTAG, PREFIX + "head is tilted up");
        if (face.getHeadEulerAngleX() == 0.0) Log.d(LOGTAG, PREFIX + "head is not tilted up or down");
        if (face.getHeadEulerAngleX() < 0.0) Log.d(LOGTAG, PREFIX + "head is tilted down");

        for (int inx=0; inx<faceLandmarkTypes.length; inx++)
        {
            FaceLandmark faceLandmark = face.getLandmark(faceLandmarkTypes[inx]);
            if (faceLandmark == null)
            {
                Log.e(LOGTAG, PREFIX + "does not have " + faceLandmarkNames[inx]);
                continue;
            }

            Log.d(LOGTAG, PREFIX + "has " + faceLandmarkNames[inx] + " at (x=" + faceLandmark.getPosition().x + ", y=" + faceLandmark.getPosition().y + ")");
        }

        //
        // Classification results
        //
        Float smiling = (face.getSmilingProbability() == null) ? 0.0F : face.getSmilingProbability() * 100F;

        Float leftEyeOpen = (face.getLeftEyeOpenProbability() == null) ? 0.0F : face.getLeftEyeOpenProbability() * 100F;

        Float rightEyeOpen = (face.getRightEyeOpenProbability() == null) ? 0.0F : face.getRightEyeOpenProbability() * 100F;

        faceClassificationListener.setFaceClassificationValues(smiling, leftEyeOpen, rightEyeOpen);
    }

    public void detectFacesInImage(@NonNull Uri fileUri)
    {
        detectFacesInImage(fileUri, getDefaultDetectionOptions());
    }

    public void detectFacesInImage(@NonNull File file)
    {
        detectFacesInImage(file, getDefaultDetectionOptions());
    }

    public void detectFacesInImage(@NonNull Uri fileUri, @NonNull FaceDetectorOptions options)
    {
        InputImage inputImage = getImageFile(context, fileUri);
        if (inputImage == null)
        {
            Log.e(LOGTAG, "Could not retrieve image file");
            return;
        }

        performDetection(options, inputImage);
    }

    /**
     * detect faces in a given image file with defined options
     * @param file file containing image on which face detection is to be done
     * @param options options for face detection processing
     */
    public void detectFacesInImage(@NonNull File file, @NonNull FaceDetectorOptions options)
    {
        InputImage inputImage = getImageFile(context, file);
        if (inputImage == null)
        {
            Log.d(LOGTAG, "Could not retrieve image file");
            return;
        }

        performDetection(options, inputImage);
    }

    void performDetection(@NonNull FaceDetectorOptions options, @NonNull InputImage inputImage)
    {
        final FaceDetector faceDetectorClient = FaceDetection.getClient(options);
        Task<List<Face>> resultTasks = faceDetectorClient.process(inputImage);

        //
        // add listeners for complete, success, failure of face detection
        //
        resultTasks.addOnCompleteListener(
                new OnCompleteListener<List<Face>>()
                {
                    @Override
                    public void onComplete(@NonNull Task<List<Face>> task)
                    {
                        Log.d(LOGTAG, "Face detection complete");
                        faceDetectorClient.close();
                    }
                }
        );

        resultTasks.addOnSuccessListener(
                new OnSuccessListener<List<Face>>()
                {
                    @Override
                    public void onSuccess(List<Face> faces)
                    {
                        Log.d(LOGTAG, "Face detection successful. Found " + faces.size() + " faces");

                        faceClassificationListener.setFaceFound(faces.size() > 0);

                        for (int inx=0; inx<faces.size(); inx++)
                        {
                            printFaceDetails(inx, faces.get(inx));
                        }
                    }
                }
        );

        resultTasks.addOnFailureListener(
                new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.e(LOGTAG, "Face detection failed. Exception = " + e.getMessage());

                        faceClassificationListener.setFaceFound(false);
                    }
                }
        );

        Log.d(LOGTAG, "Face detection started");
    }
}
