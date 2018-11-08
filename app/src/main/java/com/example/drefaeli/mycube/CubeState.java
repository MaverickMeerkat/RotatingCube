package com.example.drefaeli.mycube;

public class CubeState {
    private float[] previousRotationMatrix;
    private final float angleAroundX;
    private final float angleAroundY;
    private final float scalingTranslation;
    private static final float MIN_SCALING = -20f;
    private static final float MAX_SCALING = 20f; // 20 - 20 = 0; i.e. max scaling is original position

    public CubeState(){
        this(new float[16], -1f, -1f, 20f);
    }

    public CubeState(float[] previousRotationMatrix, float angleAroundX, float angleAroundY, float scalingTranslation){
        this.previousRotationMatrix = previousRotationMatrix;
        this.angleAroundX = angleAroundX;
        this.angleAroundY = angleAroundY;
        this.scalingTranslation = Math.min(MAX_SCALING, Math.max(MIN_SCALING, scalingTranslation));
    }

    void setPreviousRotationMatrix(float[] previousRotationMatrix){
        this.previousRotationMatrix = previousRotationMatrix.clone();
    }

    float getAngleAroundX(){
        return angleAroundX;
    }

    float getAngleAroundY(){
        return angleAroundY;
    }

    float getScalingTranslation(){
        return scalingTranslation;
    }

    float[] getPreviousRotationMatrix(){
        return previousRotationMatrix;
    }
}
