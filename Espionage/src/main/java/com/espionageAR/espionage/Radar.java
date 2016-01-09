package com.espionageAR.espionage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

public class Radar extends View {

    private final String LOG = "RadarView";
    private final int POINT_ARRAY_SIZE = 25;

    private int fps = 75;
    private boolean showCircles = true;

    float alpha = 0;
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
    //Animation while there is no action. This may be more efficient.
    public void startAnimation() {
        mHandler.removeCallbacks(mTick);
        mHandler.post(mTick);
    }

    public void stopAnimation() {
        mHandler.removeCallbacks(mTick);
    }

    public void setFrameRate(int fps) { this.fps = fps; }
    public int getFrameRate() { return this.fps; }

    public void setShowCircles(boolean showCircles) { this.showCircles = showCircles; }

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

        //Draw all available enemies
        for (int i = pointArray.length; i > 0; i--) {
            Point point = pointArray[i-1];
            if (point != null) {
                canvas.drawPoint(point.x,point.y,pointPaint);
            }
        }
        //Now that the array is used, clean it out:
        pointArray=null;

        //Set up bounding box for arc draw. Undersize it a bit so things fit on the canvas.
        RectF arcBox = new RectF(height-1,1,width-1,1);

        boolean drawWedge=false;

        //Draw all available arc ranges
        for (int i = 0; i<arcArray.length-1; i+=2) {
            canvas.drawArc(arcBox,arcArray[i],arcArray[i+1],drawWedge,arcPaint);
        }
        //Now that the array is used, clean it out. This will allow
        arcArray=null;

    }

    //These methods allow for input of new draw arcs/points. They are polite and wait for current drawing to finish if necessary.
    //This may not be the best way to do this though.

    public boolean setPointArray(long[] rawPoints){
       if (pointArray == null||pointArray.length==0){
           //Good housekeeping, make sure the array is cleared.
           pointArray=null;

           //As long as the array isn't full, we create an array of associated points from raw data.
           //TO DO: Grab the player ID and draw that as well.
           pointArray = new Point[rawPoints.length/3];
           for(int i=0;i<rawPoints.length-1;i+=3){
               pointArray[i/3]=new Point((int)rawPoints[i],(int)rawPoints[i+1]);
           }

           return true;
       }
       else {
           return false;
           }
    }

    public boolean setArcArray(long[] rawArc){
        if (arcArray == null||arcArray.length==0){
            arcArray = Arrays.copyOf(rawArc,rawArc.length);
            return true;
        }
        else {
            return false;
        }
    }

}