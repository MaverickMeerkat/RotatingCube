// Copyright (c) 2018 Lightricks. All rights reserved.
// Created by David Refaeli.
package com.example.drefaeli.mycube;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Cube cube;
    private final float[] projectionMatrix = new float[16];
    private float[] identityRotationMatrix = new float[16];
    float angleAroundY = -1f;
    float angleAroundX = -1f;
    float[] previousRotationMatrix = new float[16];
    float scalingProjection = -1f;
    private float screenRatio;

    private static final float MIN_SCALING = 0.5f;
    private static final float MAX_SCALING = 6f;
    private static final float FRICTION = 0.01f;

    void setAngleAroundX(float angleAroundX) {
        this.angleAroundX = angleAroundX;
    }

    void setAngleAroundY(float angleAroundY) {
        this.angleAroundY = angleAroundY;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        clearColorAndDepth();
        Matrix.setIdentityM(identityRotationMatrix, 0);
        cube = new Cube();
    }

    public void onDrawFrame(GL10 unused) {
        clearColorAndDepth();
        float[] mvpMatrix = setModelViewProjectionMatrix();
        cube.draw(mvpMatrix);
    }

    private void clearColorAndDepth() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable( GLES20.GL_DEPTH_TEST );
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );
    }

    private float[] setModelViewProjectionMatrix() {
        float[] viewMatrix = createCameraPosition();
        float[] rotationMatrix = createRotationMatrix();
        float[] MVPMatrix = multiplyMatrices(projectionMatrix, viewMatrix);
        MVPMatrix = multiplyMatrices(MVPMatrix, rotationMatrix);
        return MVPMatrix;
    }

    private float[] multiplyMatrices(float[] lhs, float[] rhs) {
        float[] result = new float[16];
        Matrix.multiplyMM(result, 0,lhs , 0,  rhs, 0);
        return result;
    }

    private float[] createCameraPosition() {
        // Set the camera position (View matrix)
        float[] viewMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 6, 0f, 0f,
                0f, 0f, 1.0f, 0.0f);
        return viewMatrix;
    }

    private float[] createRotationMatrix() {
        // angles are counter clockwise when looking against the axis
        if (angleAroundX == -1){
            angleAroundX = 0.5f;
            // a positive x angle means it wants to turn "towards us"
            // a negative x angle means it wants to turn "away from us"
        }
        if(angleAroundY == -1){
            angleAroundY = 0.5f;
            // a positive y angle means it wants to turn left to right
            // a negative x angle means it wants to turn right to left
        }

        float[] rotationMatrixX = new float[16];
        float[] rotationMatrixY = new float[16];
        Matrix.setRotateM(rotationMatrixX, 0, angleAroundX, 1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(rotationMatrixY, 0, angleAroundY, 0.0f, 1.0f, 0.0f);
        float[] currentRotationMatrix = new float[16];

        if (Arrays.equals(previousRotationMatrix,currentRotationMatrix)){ // i.e. empty matrix
            previousRotationMatrix = identityRotationMatrix;
        }

        Matrix.multiplyMM(currentRotationMatrix, 0, rotationMatrixX , 0, rotationMatrixY , 0);
        Matrix.multiplyMM(currentRotationMatrix, 0, rotationMatrixX , 0, rotationMatrixY , 0);

        float[] rotationMatrix = new float[16];
        Matrix.multiplyMM(rotationMatrix, 0, currentRotationMatrix , 0, previousRotationMatrix, 0);
        previousRotationMatrix = rotationMatrix.clone();

        angleAroundX *= 1 - FRICTION;
        angleAroundY *= 1 - FRICTION;

        return rotationMatrix;
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        screenRatio = (float) width / height;
        doProjection();
    }

    public void doProjection() {
        if (scalingProjection == -1){
            scalingProjection = 3;
        } else {
            scalingProjection = Math.min(MAX_SCALING, Math.max(MIN_SCALING, scalingProjection));
        }
        Matrix.frustumM(projectionMatrix, 0, -screenRatio, screenRatio, -1, 1, scalingProjection, 7);
    }

    static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
