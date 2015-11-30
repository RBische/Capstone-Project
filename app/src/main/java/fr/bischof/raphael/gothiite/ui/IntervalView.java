package fr.bischof.raphael.gothiite.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * View that shows how an interval is created
 * Created by biche on 18/09/2015.
 */
public class IntervalView extends View {
    private Paint paintGreen;
    private Paint paintRed;
    private Paint paintGray;
    private int mTimeFromBeginning = 0;
    private int parentHeight;
    private int parentWidth;
    private List<ColorPart> partsToDraw;
    private int summedSize;

    public void setTimeFromBeginning(int timeFromBeginning) {
        this.mTimeFromBeginning = timeFromBeginning;
        invalidate();
    }

    public IntervalView(Context context) {
        super(context);
        initPaint();
    }

    public IntervalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public IntervalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint(){
        // create an empty list of parts to draw
        partsToDraw = new ArrayList<>();
        summedSize = 1;
        // create the Paint and set its color
        paintGreen = new Paint();
        paintGreen.setColor(Color.GREEN);
        // create the Paint and set its color
        paintRed = new Paint();
        paintRed.setColor(Color.RED);
        // create the Paint and set its color
        paintGray = new Paint();
        paintGray.setColor(Color.GRAY);
        paintGray.setAlpha(100);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.DKGRAY);
        int x = 0;
        int y = 0;
        for(ColorPart part:partsToDraw){
            int rectangleWidth = parentWidth*part.getPartLength()/summedSize;
            Rect rectangle = new Rect(x, y, rectangleWidth+x, parentHeight);
            if (part.isRedColor()){
                canvas.drawRect(rectangle, paintRed);
            }else{
                canvas.drawRect(rectangle, paintGreen);
            }
            x+=rectangleWidth;
        }
        int grayRectangleWidth = parentWidth*mTimeFromBeginning/summedSize;
        canvas.drawRect(new Rect(0, 0, grayRectangleWidth, parentHeight), paintGray);
    }

    /**
     * Refresh the display with new parts
     * @param partsToDraw SortedMap with an integer that represents the length of the part and a boolean that represents the color
     */
    public void updateParts(List<ColorPart> partsToDraw){
        this.partsToDraw = partsToDraw;
        summedSize = 0;
        for(ColorPart part:partsToDraw){
            summedSize += part.getPartLength();
        }
        invalidate();
    }
}
