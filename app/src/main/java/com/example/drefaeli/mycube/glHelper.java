// Copyright (c) 2018 Lightricks. All rights reserved.
// Created by David Refaeli.
package com.example.drefaeli.mycube;

import android.opengl.GLES20;

public class glHelper {

    public static int createProgram(int vertexShader, int fragmentShader, final String[] attributes) {
        String compilationError = "";
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);

//            // Bind attributes
//            if (attributes != null)
//            {
//                final int size = attributes.length;
//                for (int i = 0; i < size; i++)
//                {
//                    GLES20.glBindAttribLocation(program, i, attributes[i]);
//                }
//            }

            GLES20.glLinkProgram(program);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                compilationError = GLES20.glGetProgramInfoLog(program);
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        if (program == 0) {
            throw new RuntimeException(String.format("Error creating program. %s", compilationError));
        }

        return program;
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        String compilationError = "";
        if (shader != 0) {
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                compilationError = GLES20.glGetShaderInfoLog(shader);
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        if (shader == 0)
        {
            throw new RuntimeException(String.format("Error creating vertex shader. %s", compilationError));
        }
        return shader;

    }


}
