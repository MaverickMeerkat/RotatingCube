package com.example.drefaeli.mycube;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MainActivity extends AppCompatActivity {
    private CubeGLSurfaceView glSurfaceView;

    private final static String ROTATION_MATRIX_TAG = "rotationMatrix";
    private final static String ANGLE_X_TAG = "angleX";
    private final static String ANGLE_Y_TAG = "angleY";
    private final static String SCALE_TAG = "scale";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new CubeGLSurfaceView(this);
        setContentView(glSurfaceView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloatArray(ROTATION_MATRIX_TAG, glSurfaceView.renderer.getCubeState().getPreviousRotationMatrix());
        outState.putFloat(ANGLE_X_TAG, glSurfaceView.renderer.getCubeState().getAngleAroundX());
        outState.putFloat(ANGLE_Y_TAG, glSurfaceView.renderer.getCubeState().getAngleAroundY());
        outState.putFloat(SCALE_TAG, glSurfaceView.renderer.getCubeState().getScalingTranslation());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        glSurfaceView.queueEvent(() -> glSurfaceView.renderer.UpdateState(new CubeState(savedInstanceState.getFloatArray(ROTATION_MATRIX_TAG),
                savedInstanceState.getFloat(ANGLE_X_TAG),
                savedInstanceState.getFloat(ANGLE_Y_TAG),
                savedInstanceState.getFloat(SCALE_TAG))));
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

    class CubeGLSurfaceView extends GLSurfaceView {
        public final CubeGLRenderer renderer;

        private final float TOUCH_SCALE_FACTOR = 180.0f / 1020;

        public CubeGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            renderer = new CubeGLRenderer();
            setRenderer(renderer);


            ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    CubeState prevState = renderer.getCubeState();
                    CubeState newState = new CubeState(prevState.getPreviousRotationMatrix(),
                            prevState.getAngleAroundX(),
                            prevState.getAngleAroundY(),
                            slowDownScaling(detector.getScaleFactor()) * prevState.getScalingTranslation());
                    queueEvent(() -> renderer.UpdateState(newState));
                    return super.onScale(detector);
                }

                private float slowDownScaling(float newScale) {
                    if (newScale > 1) { // zoom in
                        newScale = 1 + (newScale - 1) / 6;
                    } else { // zoom out
                        newScale = 1 - (1 - newScale) / 6;
                    }
                    return newScale;
                }
            });

            GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    CubeState prevState = renderer.getCubeState();
                    CubeState newState = new CubeState(prevState.getPreviousRotationMatrix(),
                            -distanceY * TOUCH_SCALE_FACTOR,
                            -distanceX * TOUCH_SCALE_FACTOR,
                            prevState.getScalingTranslation());
                    queueEvent(() -> renderer.UpdateState(newState));
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    CubeState prevState = renderer.getCubeState();
                    CubeState newState = new CubeState(prevState.getPreviousRotationMatrix(),
                            0,
                            0,
                            prevState.getScalingTranslation());
                    queueEvent(() -> renderer.UpdateState(newState));
                    return super.onDown(e);
                }
            });

            setOnTouchListener((v, event) -> {
                        gestureDetector.onTouchEvent(event);
                        scaleGestureDetector.onTouchEvent(event);
                        return true;
                    }
            );

            // Render the view only when there is a change in the drawing data
//            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
    }
}
