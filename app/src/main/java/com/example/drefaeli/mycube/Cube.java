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
    private FloatBuffer normalsBuffer;
    private int program;

    private final String vertexShaderCode =
            "uniform mat4 uMVMatrix;" +
            "uniform mat4 uMVPMatrix;" +
                    "uniform vec4 uLightPos;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 vColor;" +
                    "attribute vec3 vNormal;" +
                    "varying vec4 vNormalVarying;" +
                    "varying vec4 vColorVarying;" +
                    "varying vec4 vLightPosVarying;" +
                    "varying vec4 vVertexPositionVarying; " +
                    "void main()" +
                    "{" +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "   vVertexPositionVarying = uMVMatrix * vPosition;" +
                    "   vColorVarying = vColor;" +
                    "   vLightPosVarying = uLightPos;" +
                    "   vec4 v4Normal = vec4(vNormal, 0.0);" +
                    "   vNormalVarying = uMVMatrix * v4Normal;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColorVarying;" +
                    "varying vec4 vLightPosVarying;" +
                    "varying vec4 vVertexPositionVarying;" +
                    "varying vec4 vNormalVarying;" +
                    "void main() {" +
                    "   vec4 normalizedLightVector = normalize(vLightPosVarying - vVertexPositionVarying);" +
                    "   float normalDiffuse = max(dot(vNormalVarying, normalizedLightVector), 0.4);" +
                    "   float distance = length(vLightPosVarying - vVertexPositionVarying);" +
                    "   float distanceDiffuse = 1.0 / " +
                    "       (1.0 + 0.00 * distance + 0.015 * distance * distance); " +
                    "  float diffuse = normalDiffuse * distanceDiffuse;" +
                    "  gl_FragColor = vColorVarying * diffuse;" +
                    "}";

    private static final int COORDS_PER_VERTEX = 3;
//    private float cubeCoords[] =
//            {
//                    -0.5f, 0.5f, 0.5f,   // front top left 0
//                    -0.5f, -0.5f, 0.5f,   // front bottom left 1
//                    0.5f, -0.5f, 0.5f,   // front bottom right 2
//                    0.5f, 0.5f, 0.5f,  // front top right 3
//                    -0.5f, 0.5f, -0.5f,   // back top left 4
//                    0.5f, 0.5f, -0.5f,   // back top right 5
//                    -0.5f, -0.5f, -0.5f,   // back bottom left 6
//                    0.5f, -0.5f, -0.5f,  // back bottom right 7
//            };

//    private short drawOrder[] =
//            {
//                    0, 1, 2, 0, 2, 3,//front
//                    0, 4, 5, 0, 5, 3, //Top
//                    0, 1, 6, 0, 6, 4, //left
//                    3, 2, 7, 3, 7, 5, //right
//                    1, 2, 7, 1, 7, 6, //bottom
//                    4, 6, 7, 4, 7, 5 //back
//            };

//    final float cubeColor[] =
//            {
//                    1.0f, 0.0f, 0.0f, 1.0f,
//                    0.0f, 1.0f, 0.0f, 1.0f,
//                    0.0f, 0.0f, 1.0f, 1.0f,
//                    1.0f, 1.0f, 0.0f, 1.0f,
//                    0.0f, 1.0f, 1.0f, 1.0f,
//                    1.0f, 0.0f, 1.0f, 1.0f,
//                    0.5f, 0.2f, 0.8f, 1.0f,
//                    1.0f, 1.0f, 1.0f, 1.0f,
//            };


    final float[] cubeCoords =
            {
                    // Front face
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    -0.5f, -0.5f, 0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,

                    // Right face
                    0.5f, 0.5f, 0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,

                    // Back face
                    0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    -0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, 0.5f, -0.5f,

                    // Left face
                    -0.5f, 0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,

                    // Top face
                    -0.5f, 0.5f, -0.5f,
                    -0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, -0.5f,
                    -0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, -0.5f,

                    // Bottom face
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                    -0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, -0.5f, -0.5f,
            };

    final float[] cubeColor =
            {
                    // Front face (red)
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,

                    // Right face (green)
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,

                    // Back face (blue)
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,

                    // Left face (yellow)
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,

                    // Top face (cyan)
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,

                    // Bottom face (magenta)
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f
            };


    final float[] cubeNormalData =
            {
                    // Front face
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,

                    // Right face
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,

                    // Back face
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,

                    // Left face
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,

                    // Top face
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,

                    // Bottom face
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f
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
//        ByteBuffer dlb = ByteBuffer.allocateDirect(
//                // (# of coordinate values * 2 bytes per short)
//                drawOrder.length * 2);
//        dlb.order(ByteOrder.nativeOrder());
//        drawListBuffer = dlb.asShortBuffer();
//        drawListBuffer.put(drawOrder);
//        drawListBuffer.position(0);

        // initialize byte buffer for the color list
        ByteBuffer cb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                cubeColor.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(cubeColor);
        colorBuffer.position(0);

        ByteBuffer nb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                cubeNormalData.length * 4);
        nb.order(ByteOrder.nativeOrder());
        normalsBuffer = nb.asFloatBuffer();
        normalsBuffer.put(cubeNormalData);
        normalsBuffer.position(0);

        int vertexShader = glHelper.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = glHelper.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        program = glHelper.createProgram(vertexShader, fragmentShader, new String[] {"vPosition",  "vColor", "vNormal"});
    }


    public void draw(float[] mvmMatrix, float[] mvpMatrix, float[] lightPosInEyeSpace) {
        GLES20.glUseProgram(program);

        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        int colorHandle = GLES20.glGetAttribLocation(program, "vColor");
        int lightPosHandle = GLES20.glGetUniformLocation(program, "uLightPos");
        int normalHandle = GLES20.glGetAttribLocation(program, "vNormal");

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);

        int vertexStride = COORDS_PER_VERTEX * 4;
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 16, colorBuffer);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false,
                0, normalsBuffer);

        int MVMatrixHandle = GLES20.glGetUniformLocation(program, "uMVMatrix");
        GLES20.glUniformMatrix4fv(MVMatrixHandle, 1, false, mvmMatrix, 0);

        int MVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Pass in the light position in eye space.
        GLES20.glUniform4f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2], lightPosInEyeSpace[3]);

        // Draw the cube
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(colorHandle);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glDisableVertexAttribArray(MVPMatrixHandle);
    }
}
