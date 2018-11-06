package com.example.drefaeli.mycube;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

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

    // A GLSurfaceView must be notified when to pause and resume rendering.
    // GLSurfaceView clients are required to call onPause() when the activity stops and onResume()
    // when the activity starts. These calls allow GLSurfaceView to pause and resume the rendering
    // thread, and also allow GLSurfaceView to release and recreate the OpenGL display.
    @Override
    public void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    class MyGLSurfaceView extends GLSurfaceView {
        public final MyGLRenderer renderer;

        private final float TOUCH_SCALE_FACTOR = 180.0f / 1020;

        public MyGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            renderer = new MyGLRenderer();
            setRenderer(renderer);


            ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    return super.onScale(detector);
                }
            });

            GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    renderer.setAngleAroundX(-distanceY * TOUCH_SCALE_FACTOR);
                    renderer.setAngleAroundY(-distanceX * TOUCH_SCALE_FACTOR);
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    renderer.setAngleAroundX(0);
                    renderer.setAngleAroundY(0);
                    return super.onDown(e);
                }
            });

            setOnTouchListener((v, event) -> {
                gestureDetector.onTouchEvent(event);
                scaleGestureDetector.onTouchEvent(event);
                return true;}
            );

            // Render the view only when there is a change in the drawing data
//            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
    }
}
