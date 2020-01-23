package com.codingblocks.cbonlineapp.util.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.VectorDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.appcompat.graphics.drawable.DrawableWrapper;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

/**
 * @author aggarwalpulkit596
 */
public class SvgRatingBar extends AppCompatRatingBar {

    private Bitmap sampleTile;

    public SvgRatingBar(Context context) {
        this(context, null);
    }

    public SvgRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.ratingBarStyle);
    }

    public SvgRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayerDrawable drawable = (LayerDrawable) createTile(getProgressDrawable(), false);
        setProgressDrawable(drawable);
    }

    /**
     * Converts a drawable to a tiled version of itself. It will recursively
     * traverse layer and state list drawables.
     */
    @SuppressLint("RestrictedApi")
    private Drawable createTile(Drawable drawable, boolean clip) {
        if (drawable instanceof DrawableWrapper) {
            Drawable inner = ((DrawableWrapper) drawable).getWrappedDrawable();
            if (inner != null) {
                inner = createTile(inner, clip);
                ((DrawableWrapper) drawable).setWrappedDrawable(inner);
            }
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable background = (LayerDrawable) drawable;
            final int n = background.getNumberOfLayers();
            Drawable[] outDrawables = new Drawable[n];

            for (int i = 0; i < n; i++) {
                int id = background.getId(i);
                outDrawables[i] = createTile(background.getDrawable(i),
                        (id == android.R.id.progress || id == android.R.id.secondaryProgress));
            }
            LayerDrawable newBg = new LayerDrawable(outDrawables);

            for (int i = 0; i < n; i++) {
                newBg.setId(i, background.getId(i));
            }

            return newBg;

        } else if (drawable instanceof BitmapDrawable) {
            final BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            final Bitmap tileBitmap = bitmapDrawable.getBitmap();
            if (sampleTile == null) {
                sampleTile = tileBitmap;
            }

            final ShapeDrawable shapeDrawable = new ShapeDrawable(getDrawableShape());
            final BitmapShader bitmapShader = new BitmapShader(tileBitmap,
                    Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
            shapeDrawable.getPaint().setShader(bitmapShader);
            shapeDrawable.getPaint().setColorFilter(bitmapDrawable.getPaint().getColorFilter());
            return (clip) ? new ClipDrawable(shapeDrawable, Gravity.START,
                    ClipDrawable.HORIZONTAL) : shapeDrawable;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && drawable instanceof VectorDrawable) {
            return createTile(getBitmapDrawableFromVectorDrawable(drawable), clip);
        } else if (drawable instanceof VectorDrawableCompat) {
            // API 19 support.
            return createTile(getBitmapDrawableFromVectorDrawable(drawable), clip);
        }
        return drawable;
    }

    private BitmapDrawable getBitmapDrawableFromVectorDrawable(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return new BitmapDrawable(getResources(), bitmap);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (sampleTile != null) {
            final int width = sampleTile.getWidth() * getNumStars();
            setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0),
                    getMeasuredHeight());
        }
    }

    private Shape getDrawableShape() {
        final float[] roundedCorners = new float[]{5, 5, 5, 5, 5, 5, 5, 5};
        return new RoundRectShape(roundedCorners, null, null);
    }
}
