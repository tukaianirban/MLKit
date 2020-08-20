package com.anirban.test.mlkit.facerecognition;

public interface FaceClassificationListener
{
    public void setFaceClassificationValues(Float smiling, Float leftEyeOpen, Float rightEyeOpen);

    public void setFaceFound(boolean faceFound);
}
