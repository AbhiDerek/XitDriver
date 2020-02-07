package com.app.xit.home.signature;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.app.xit.utill.AppUtill;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Abhishek on 1/8/2020.
 */

public class Signature extends View {

    private static final float STROKE_WIDTH = 5f;
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
    private final Button mGetSign;
    private final RectF dirtyRect = new RectF();
    private Paint mPaint;
    private Path mPath;
    private float lastTouchX;
    private float lastTouchY;
    private Bitmap bitmap;


    public Signature(Context context, AttributeSet attrs, Button getSign) {
        super(context, attrs);
        mPaint = new Paint();
        mPath = new Path();
        mGetSign = getSign;
        mGetSign.setEnabled(false);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(STROKE_WIDTH);

        // Method to create Directory, if the Directory doesn't exists
    }

    public Signature(Context context, AttributeSet attrs, Button getSign, Path path) {
        super(context, attrs);
        mPaint = new Paint();
        mPath = path;

        mGetSign = getSign;
        mGetSign.setEnabled(true);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(STROKE_WIDTH);

        // Method to create Directory, if the Directory doesn't exists
    }

    public void save(View v, String StoredPath) {
        Log.v("log_tag", "Width: " + v.getWidth());
        Log.v("log_tag", "Height: " + v.getHeight());
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        FileOutputStream mFileOutStream = null;
        try {
            // Output the file
            mFileOutStream = new FileOutputStream(StoredPath);
            v.draw(canvas);
            // Convert the output file to Image such as .png
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);

        } catch (Exception e) {
            Log.v("log_tag", e.toString());
        } finally {
            try {
                if (mFileOutStream != null) {
                    mFileOutStream.flush();
                    mFileOutStream.close();
                }
            } catch (IOException e) {
                Log.v("log_tag", e.toString());
            }

        }
    }

    public void clear() {
        mPath.reset();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        mGetSign.setEnabled(true);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;

            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_UP:

                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    mPath.lineTo(historicalX, historicalY);
                }
                mPath.lineTo(eventX, eventY);
                break;

            default:
                debug("Ignored touch event: " + event.toString());
                return false;
        }

        invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

        lastTouchX = eventX;
        lastTouchY = eventY;

        return true;
    }

    private void debug(String string) {

        Log.v("log_tag", string);

    }

    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }

        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    private void resetDirtyRect(float eventX, float eventY) {
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }

    public String getImageString() {

        return "data:image/png;base64," + AppUtill.INSTANCE.bitmapToBase64(bitmap);
    }

    public Path getPath() {
        return mPath;
    }

//    public String getBase64FromBitmap(Bitmap bitmap) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
//        byte[] bytes = byteArrayOutputStream.toByteArray();
//        return Base64.encodeToString(bytes, Base64.DEFAULT);
//    }

}




