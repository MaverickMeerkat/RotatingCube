package com.example.drefaeli.mycube;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {
    private MyGLSurfaceView glSurfaceView;

    private final static String ROTATION_MATRIX_TAG = "rotationMatrix";
    private final static String ANGLE_X_TAG = "angleX";
    private final static String ANGLE_Y_TAG = "angleY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new MyGLSurfaceView(this);
        setContentView(glSurfaceView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloatArray(ROTATION_MATRIX_TAG, glSurfaceView.renderer.previousRotationMatrix);
        outState.putFloat(ANGLE_X_TAG, glSurfaceView.renderer.angleAroundX);
        outState.putFloat(ANGLE_Y_TAG, glSurfaceView.renderer.angleAroundY);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        glSurfaceView.renderer.previousRotationMatrix = savedInstanceState.getFloatArray(ROTATION_MATRIX_TAG);
        glSurfaceView.renderer.angleAroundX = savedInstanceState.getFloat(ANGLE_X_TAG);
        glSurfaceView.renderer.angleAroundY = savedInstanceState.getFloat(ANGLE_Y_TAG);
    }

    class MyGLSurfaceView extends GLSurfaceView {

        public final MyGLRenderer renderer;

        public MyGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            renderer = new MyGLRenderer();
            setRenderer(renderer);

            // Render the view only when there is a change in the drawing data
//            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        private final float TOUCH_SCALE_FACTOR = 180.0f / 1020;
        private float previousX;
        private float previousY;

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float dx = x - previousX;
                    float dy = y - previousY;
                    renderer.setAngleAroundX(dy * TOUCH_SCALE_FACTOR);
                    renderer.setAngleAroundY(dx * TOUCH_SCALE_FACTOR);
                    requestRender();
                    break;
                case MotionEvent.ACTION_DOWN:
                    renderer.setAngleAroundX(0);
                    renderer.setAngleAroundY(0);
                    break;

                case MotionEvent.ACTION_UP:
//                    renderer.setAngleAroundX(0.5f);
//                    renderer.setAngleAroundY(0.5f);
                    break;
            }

            previousX = x;
            previousY = y;
            return true;
        }
    }
}
