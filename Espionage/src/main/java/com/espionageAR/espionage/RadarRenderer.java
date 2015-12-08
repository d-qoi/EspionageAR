package com.espionageAR.espionage;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.lang.Math;

/**
 * Created by Louis on 12/7/15.
 */
public class RadarRenderer implements GLSurfaceView.Renderer {

    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        // Set the background frame color
        GLES20.glClearColor(2.0f, 2.0f, 2.0f, 1.0f);
        //float[] verts=MakeCircle2d(1,100,0,0);

    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    //Class to make a set of line segments for a circle.
    private float[] MakeCircle(float cx, float cy, float r, int num_segments)
    {
        float[] verts=new float[num_segments*2+2];
        float theta =(float)(2 * 3.1415926 / num_segments);
        float tangetial_factor = (float)Math.tan(theta);//calculate the tangential factor
        float radial_factor = (float)Math.cos(theta);//calculate the radial factor
        float x = r;//we start at angle = 0

        float y = 0;

        for(int ii = 0; ii < num_segments; ii++)
        {
            verts[ii*2]=(x+cx);
            verts[ii*2+1]=(y+cy);

            //calculate the tangential vector
            //remember, the radial vector is (x, y)
            //to get the tangential vector we flip those coordinates and negate one of them

            float tx = -y;
            float ty = x;

            //add the tangential vector

            x += tx * tangetial_factor;
            y += ty * tangetial_factor;

            //correct using the radial factor

            x *= radial_factor;
            y *= radial_factor;
        }
        return verts;
    }

    //Class to make a set of line segments for an arc. Input angles are in degrees.
    private float[] MakeArc(float cx, float cy, float r, float s_ang, float f_ang, int num_segments)
    {
        float[] verts=new float[num_segments*2+2];
        float theta =(float)(2 * 3.1415926*(f_ang-s_ang) / num_segments);
        float tangetial_factor = (float)Math.tan(theta);//calculate the tangential factor
        float radial_factor = (float)Math.cos(theta);//calculate the radial factor

        float x = r * (float)Math.sin(s_ang);//we now start at the start angle
        float y = r * (float)Math.cos(s_ang);

        for(int ii = 0; ii < num_segments; ii++)
        {
            verts[ii*2]=(x+cx);
            verts[ii*2+1]=(y+cy);

            //calculate the tangential vector
            //remember, the radial vector is (x, y)
            //to get the tangential vector we flip those coordinates and negate one of them

            float tx = -y;
            float ty = x;

            //add the tangential vector

            x += tx * tangetial_factor;
            y += ty * tangetial_factor;

            //correct using the radial factor

            x *= radial_factor;
            y *= radial_factor;
        }
        return verts;
    }
}