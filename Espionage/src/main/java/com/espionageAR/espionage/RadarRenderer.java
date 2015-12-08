package com.espionageAR.espionage;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.lang.Math;

/**
 * This class takes care of rendering the radar object and its background.
 *
 * What needs to be done:
 *   - Actually draw the radar as concentric circles and then some lines crossing them.
 *   - Expose a method to draw points on the radar.
 *   - Expose a method to specify flashing arcs on the radar.
 *
 * What would be nice to do:
 *   - Try to make the points and arcs animate well. XD
 */
public class RadarRenderer implements GLSurfaceView.Renderer {

    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        // Set the background frame color
        GLES20.glClearColor(2.0f, 2.0f, 2.0f, 1.0f);
        //float[] verts=MakeCircle2d(1,100,0,0);

        //Once I get the circle stuff validated, circle draw goes here for the radar

    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //Do camera view stuff here.
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        //Put some projection code here.

    }

}