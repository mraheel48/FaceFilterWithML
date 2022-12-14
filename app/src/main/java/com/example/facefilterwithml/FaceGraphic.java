package com.example.facefilterwithml;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.example.facefilterwithml.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;

public class FaceGraphic extends GraphicOverlay.Graphic {

    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float GENERIC_POS_OFFSET = 20.0f;
    private static final float GENERIC_NEG_OFFSET = -20.0f;

    private static final float BOX_STROKE_WIDTH = 0f;

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;
    private Bitmap bitmap;
    private Bitmap op;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        /*mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);*/
        bitmap = BitmapFactory.decodeResource(getOverlay().getContext().getResources(), R.drawable.snap);
        op = bitmap;
    }

    void setId(int id) {
        mFaceId = id;
    }

    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        op = Bitmap.createScaledBitmap(bitmap, (int) scaleX(face.getWidth()), (int) scaleY(((bitmap.getHeight() * face.getWidth()) / bitmap.getWidth())), false);
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);

        float deltaX, deltaY;

        deltaX = 1.3f * scaleX(mFace.getWidth() / 2);
        deltaY = 1.3f * scaleY(mFace.getHeight() / 2);

        int left = (int) (x - deltaX);
        int top = (int) (y - deltaY);
        int right = (int) (x + deltaX);
        int bottom = (int) (y + deltaY);

        Rect destBounds = new Rect(left, top, right, bottom);
        canvas.drawBitmap(bitmap, null, destBounds, null);

        // Draws a circle at the position of the detected face, with the face's track id below.
        /*float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;

        canvas.drawRect(left, top, right, bottom, mBoxPaint);
        canvas.drawBitmap(op, left, top, new Paint());*/
    }

    private float getNoseAndMouthDistance(PointF nose, PointF mouth) {
        return (float) Math.hypot(mouth.x - nose.x, mouth.y - nose.y);
    }
}
