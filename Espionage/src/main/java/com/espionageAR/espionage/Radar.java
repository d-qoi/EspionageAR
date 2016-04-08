package com.espionageAR.espionage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.Arrays;

//Class to draw a rudimentary radar. Animation is very basic.

public class Radar extends View {

    private int fps = 30;
    private boolean showCircles = true;
    private int maxDots = 100;
    private int maxArcs = 20;

    //Set up a flasher for the dots
    private double flashDuration = .5;
    private double flashCounter = 0;
    private boolean drawDots = false;

    //Draw enemy wedge directions instead of just arcs:
    boolean drawWedge=false;

    //Set up a cooldown timer for showing enemy arcs
    private int calmTimer = 0;
    private int calmSeconds = 10;

    //Separate timer for showing search dots. This one is set only on initialization:
    private int searchTimer = 0;
    private int searchSeconds = 10;


    private Point pointArray[];
    private long arcArray[];

    //Set up the paint styles.
    //Localpaint is the background
    //PointPaint is the enemy points
    //ArcPaint is the enemy detection arcs
    Paint localPaint = new Paint();
    Paint pointPaint = new Paint();
    Paint arcPaint = new Paint();

    public Radar(Context context) {
        this(context, null);
    }

    public Radar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Radar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        localPaint.setColor(Color.GREEN);
        localPaint.setAntiAlias(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(1.0F);
        localPaint.setAlpha(0);

        pointPaint.setColor(Color.RED);
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setStrokeWidth(1.5F);
        pointPaint.setAlpha(0);

        //We might want to change this draw type, maybe make it translucent or fading at edge.
        arcPaint.setColor(Color.RED);
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(3.0F);
        arcPaint.setAlpha(0);
    }

    android.os.Handler mHandler = new android.os.Handler();
    Runnable mTick = new Runnable() {
        @Override
        public void run() {
            invalidate();
            mHandler.postDelayed(this, 1000 / fps);
        }
    };

    //These are holdovers from the original scanning radar code, but we can use them to stop
    //Animation while there is no action. This may be efficient.
    public void startAnimation() {
        mHandler.removeCallbacks(mTick);
        mHandler.post(mTick);
    }

    public void stopAnimation() {
        mHandler.removeCallbacks(mTick);
    }

    //Does what it says on the tin.
    public void setFrameRate(int fps) { this.fps = fps; }
    public int getFrameRate() { return this.fps; }

    //Set up bounding box for arc draw.
    RectF arcBox = new RectF(100,1,100,1);

    //This is the main animation loop.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int width = getWidth();
        int height = getHeight();


        int D = Math.min(width,height);

        //Draw a background rectangle
        //canvas.drawRect(0, 0, getWidth(), getHeight(), localPaint);

        //Scale the draw radius down 5%
        int r = D / 2 * 95/100;
        int j = r - 1;

        //Draw the bulls-eye.
        if (showCircles) {
            canvas.drawCircle(D/2, D/2, j, localPaint);
            canvas.drawCircle(D/2, D/2, j, localPaint);
            canvas.drawCircle(D/2, D/2, j * 3 / 4, localPaint);
            canvas.drawCircle(D/2, D/2, j >> 1, localPaint);
            canvas.drawCircle(D/2, D/2, j >> 2, localPaint);
        }

        //If enemies are off but listed, count up and check to turn them back on.
        if (pointArray!=null&&!drawDots&&++flashCounter>=(int)flashDuration*fps){
            drawDots=true;
        }
        //If enemies are on, count down and check to turn them back off.
        else if (pointArray!=null&&drawDots){
            int maxiter = Math.min(pointArray.length,maxDots);
            for (int i = maxiter; i > 0; i--) {
                Point point = pointArray[i-1];
                if (point != null) {
                    canvas.drawPoint(point.x,point.y,pointPaint);
                }
            }
            if(--flashCounter<=0){
                drawDots=false;
            }
        }

        //Set bounding box size for arc draw. Undersize it a bit so things fit on the canvas.
        arcBox.set(height-1,1,width-1,1);

        //Draw all available arc ranges if on:
        if (arcArray!=null)
        {
            int maxiter=Math.min(arcArray.length-1,maxArcs);
            for (int i = 0; i<maxiter; i+=2) {
                canvas.drawArc(arcBox,arcArray[i],arcArray[i+1],drawWedge,arcPaint);
            }
        }

        //If things are to be calm, null the draw lists.
        //That first condition is to prevent counting infinitely and overflowing the integer (unlikely but possible).
        if (calmTimer!=-1 && --calmTimer<0){
            arcArray=null;
            calmTimer=-1;
        }
        if (searchTimer!=-1 && --searchTimer<0){
            pointArray=null;
            searchTimer=-1;
        }
    }


    //These two methods allow for input of new draw arcs/points.
    //JUST APPENDING ALLOWS FOR LOTS OF COLLISION. SHOULD THERE BE A SEARCH?

    //Points are passed in as [ID, X-pos, Y-pos]
    public void setPointArray(long[] rawPoints){
       if (pointArray == null){
           //As long as the array isn't full, we create an array of associated points from raw data.
           //TO DO: Grab the player ID and draw that as well.
           pointArray = new Point[rawPoints.length/3];
           for(int i=0;i<rawPoints.length-2;i+=3){
               pointArray[i/3]=new Point((int)rawPoints[i],(int)rawPoints[i+1]);
           }
        }
       else {
           //If point array is populated, concatenate. :)
           Point [] tempArray = new Point[rawPoints.length/3 + pointArray.length];

           System.arraycopy(pointArray,0,tempArray,0,pointArray.length);
           for(int i=0;i<rawPoints.length-2;i+=3){
               tempArray[i/3+pointArray.length]=new Point((int)rawPoints[i],(int)rawPoints[i+1]);
           }
           pointArray = Arrays.copyOf(tempArray,tempArray.length);
       }

       //Set the display timer and the flash counter.
       searchTimer=searchSeconds*fps;
       flashCounter=(int)(flashDuration*fps);
    }

    //Arcs are passed as [Angle Start, Angle Duration]
    public void setArcArray(long[] rawArc){
        //If arc array is full, concatenate, otherwise just make a new list.
        if (arcArray == null){
            arcArray = Arrays.copyOf(rawArc,rawArc.length);
        }
        else {
            long [] tempArray = new long[rawArc.length + arcArray.length];

            System.arraycopy(arcArray,0,tempArray,0,arcArray.length);
            System.arraycopy(rawArc,0,tempArray,arcArray.length,rawArc.length);

            arcArray = Arrays.copyOf(tempArray,tempArray.length);
        }

        //Set the hostile event display timer.
        calmTimer=calmSeconds*fps;
    }
}