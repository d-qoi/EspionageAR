package com.espionageAR.espionage;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * This class is here solely to make OpenGL play nice. You have to set up the renderer
 * within a SurfaceView.
 */
class RadarSurfaceView extends GLSurfaceView {

    private final RadarRenderer mRenderer;

    public RadarSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new RadarRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
    }
}

