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
    private float[] identityMatrix = new float[16];
    float angleAroundY = -1f;
    float angleAroundX = -1f;
    float[] previousRotationMatrix = new float[16];
    float scalingTranslation = 20f;
    private float screenRatio;
    private final float[] lightPosition = new float[]{0, 0, 0, 1};

    private static final float MIN_SCALING = -20f;
    private static final float MAX_SCALING = 20f;
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
        Matrix.setIdentityM(identityMatrix, 0);
        cube = new Cube();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        screenRatio = (float) width / height;
        doProjection();
    }

    public void onDrawFrame(GL10 unused) {
        clearColorAndDepth();
        float[] mvmMatrix = setModelViewMatrix();
        float[] mvpMatrix = setModelViewProjectionMatrix(mvmMatrix);
        cube.draw(mvmMatrix, mvpMatrix, lightPosition);
    }

    private float[] setModelViewProjectionMatrix(float[] mvmMatrix) {
        float[] MVPMatrix = multiplyMatrices(projectionMatrix, mvmMatrix);
        return MVPMatrix;
    }

    private void clearColorAndDepth() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    private float[] setModelViewMatrix() {
        float[] rotationMatrix = createRotationMatrix();
        float[] translationMatrix = doTranslation();
        float[] MVMatrix = multiplyMatrices(translationMatrix, rotationMatrix);
        float[] viewMatrix = createCameraPosition();
        MVMatrix = multiplyMatrices(viewMatrix, MVMatrix);
        return MVMatrix;
    }

    private float[] doTranslation() {
        scalingTranslation = Math.min(MAX_SCALING, Math.max(MIN_SCALING, scalingTranslation));
        float[] translatedMatrix = new float[16];
        Matrix.setIdentityM(translatedMatrix, 0);
        Matrix.translateM(translatedMatrix, 0,0,0, scalingTranslation - 20);
        return translatedMatrix;
    }

    private float[] multiplyMatrices(float[] lhs, float[] rhs) {
        float[] result = new float[16];
        Matrix.multiplyMM(result, 0, lhs, 0, rhs, 0);
        return result;
    }

    private float[] createCameraPosition() {
        // Set the camera position (View matrix)
        float[] viewMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 6f, 0f, 0f,
                0f, 0f, 1.0f, 0.0f);
        return viewMatrix;
    }

    private float[] createRotationMatrix() {
        // angles are counter clockwise when looking against the axis
        if (angleAroundX == -1) {
            angleAroundX = 0.5f;
            // a positive x angle means it wants to turn "towards us"
            // a negative x angle means it wants to turn "away from us"
        }
        if (angleAroundY == -1) {
            angleAroundY = 0.5f;
            // a positive y angle means it wants to turn left to right
            // a negative x angle means it wants to turn right to left
        }

        float[] rotationMatrixX = new float[16];
        float[] rotationMatrixY = new float[16];
        Matrix.setRotateM(rotationMatrixX, 0, angleAroundX, 1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(rotationMatrixY, 0, angleAroundY, 0.0f, 1.0f, 0.0f);
        float[] currentRotationMatrix = new float[16];

        if (Arrays.equals(previousRotationMatrix, currentRotationMatrix)) { // i.e. empty matrix
            previousRotationMatrix = identityMatrix;
        }

        Matrix.multiplyMM(currentRotationMatrix, 0, rotationMatrixX, 0, rotationMatrixY, 0);

        float[] rotationMatrix = new float[16];
        Matrix.multiplyMM(rotationMatrix, 0, currentRotationMatrix, 0, previousRotationMatrix, 0);
        previousRotationMatrix = rotationMatrix.clone();

        angleAroundX *= 1 - FRICTION;
        angleAroundY *= 1 - FRICTION;

        return rotationMatrix;
    }

    public void doProjection() {
        Matrix.frustumM(projectionMatrix, 0, -screenRatio, screenRatio, -1, 1, 3, 30);
    }
}
