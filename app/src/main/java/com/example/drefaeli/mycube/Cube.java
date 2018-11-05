// Copyright (c) 2018 Lightricks. All rights reserved.
// Created by David Refaeli.
package com.example.drefaeli.mycube;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cube {
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private FloatBuffer colorBuffer;
    private int program;

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 vColor;" +
                    "varying vec4 vColorVarying;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "vColorVarying = vColor;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColorVarying;" +
                    "void main() {" +
                    "  gl_FragColor = vColorVarying;" +
                    "}";

    private static final int COORDS_PER_VERTEX = 3;
    private float cubeCoords[] =
            {
                    -0.5f, 0.5f, 0.5f,   // front top left 0
                    -0.5f, -0.5f, 0.5f,   // front bottom left 1
                    0.5f, -0.5f, 0.5f,   // front bottom right 2
                    0.5f, 0.5f, 0.5f,  // front top right 3
                    -0.5f, 0.5f, -0.5f,   // back top left 4
                    0.5f, 0.5f, -0.5f,   // back top right 5
                    -0.5f, -0.5f, -0.5f,   // back bottom left 6
                    0.5f, -0.5f, -0.5f,  // back bottom right 7
            };

    private short drawOrder[] =
            {
                    0, 1, 2, 0, 2, 3,//front
                    0, 4, 5, 0, 5, 3, //Top
                    0, 1, 6, 0, 6, 4, //left
                    3, 2, 7, 3, 7, 5, //right
                    1, 2, 7, 1, 7, 6, //bottom
                    4, 6, 7, 4, 7, 5 //back
            };

    final float cubeColor[] =
            {
                    1.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    0.5f, 0.2f, 0.8f, 1.0f,
                    1.0f, 1.0f, 1.0f, 1.0f,
            };

    public Cube() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                cubeCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // initialize byte buffer for the color list
        ByteBuffer cb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                cubeColor.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(cubeColor);
        colorBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(program);
        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        int colorHandle = GLES20.glGetAttribLocation(program, "vColor");

        GLES20.glEnableVertexAttribArray(positionHandle);
        int vertexStride = COORDS_PER_VERTEX * 4;
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 16, colorBuffer);

        int MVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the cube
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
        GLES20.glDisableVertexAttribArray(MVPMatrixHandle);
    }
}
