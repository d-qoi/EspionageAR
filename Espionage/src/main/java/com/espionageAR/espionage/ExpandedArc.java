package com.espionageAR.espionage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * This expanded arc class is capable of making the draw buffers for any sort of circle or arc that
 * your beautiful little heart desires. Note that trig functions are costly so we do a snazzy arc
 * draw based on tangent lines that only requires one trig evaluation at the beginning.
 *
 * Need to modify class so it can apply projections and camera transformations,
 * as well as draw the actual shape.
 *
 * Note that right now I've been using sample code that requires 3-space, but my coordinates are
 * generated in 2-space. I should look up how to 2-d in Android.
 */
public class ExpandedArc {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 2;
    static float arcCoords[];


    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private int vertexCount;

    //Three overloaded constructors exist. One draws a circle, one draws a circle with a defined
    //number of line segments, and the last draws an arc from a start angle to a finish angle with
    //a defined number of line segments.

    //Default to drawing 100 segments
    public ExpandedArc(float cx, float cy, float r) {
        // create requested arc
        vertexCount = 100;
        arcCoords=MakeCircle(cx,cy,r,100);
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                arcCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(arcCoords);
        vertexBuffer.position(0);

        //Establish a draw order here or things will break.

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
    }

    //Draw a circle with X segments
    public ExpandedArc(float cx, float cy, float r, int num_segments) {
        //create requested arc
        vertexCount = num_segments;
        arcCoords=MakeCircle(cx,cy,r,num_segments);
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                arcCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(arcCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
    }

    //Draw an arc with X segments
    public ExpandedArc(float cx, float cy, float r, float s_angle, float f_angle, int num_segments) {
        //create requested arc
        vertexCount = num_segments;
        arcCoords=MakeArc(cx,cy,r,s_angle,f_angle,num_segments);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                arcCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(arcCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
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
