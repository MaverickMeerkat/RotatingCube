// Copyright (c) 2018 Lightricks. All rights reserved.
// Created by David Refaeli.
package com.example.drefaeli.mycube;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Lighter {
    // Define a simple shader program for our point.
    final String pointVertexShader =
            "uniform mat4 uMVPMatrix;"
                    +	"attribute vec4 vPosition;"
                    + "void main()"
                    + "{ "
                    + "   gl_Position = uMVPMatrix"
                    + "               * vPosition;"
                    + "   gl_PointSize = 5.0;"
                    + "}";

    final String pointFragmentShader =
            "precision mediump float;"
                    + "void main()"
                    + "{"
                    + "   gl_FragColor = vec4(1.0,"
                    + "   1.0, 1.0, 1.0);"
                    + "}";

    private final int program;


    public Lighter(){
        final int pointVertexShaderHandle = glHelper.loadShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = glHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
        program = glHelper.createProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                new String[] {"vPosition"});

    }

    /**
     * Draws a point representing the position of the light.
     */
    void draw(float[] mvpMatrix, float[] lightPosInModelSpace)
    {
        GLES20.glUseProgram(program);

        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(program, "vPosition");

        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2]);


        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

        GLES20.glDisableVertexAttribArray(pointPositionHandle);
        GLES20.glDisableVertexAttribArray(pointMVPMatrixHandle);
    }
}
