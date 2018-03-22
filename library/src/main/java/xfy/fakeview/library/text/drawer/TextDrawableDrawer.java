package xfy.fakeview.library.text.drawer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import xfy.fakeview.library.text.param.ImmutableParams;
import xfy.fakeview.library.text.param.VariableParams;

/**
 * Created by XiongFangyu on 2018/3/2.
 */
public class TextDrawableDrawer {

    private volatile static TextDrawableDrawer drawableDrawer;

    private static Context context;
    public static void init(Context c) {
        context = c.getApplicationContext();
    }

    protected TextDrawableDrawer() {
    }

    public static TextDrawableDrawer getDrawableDrawer() {
        if (drawableDrawer == null) {
            synchronized (TextDrawableDrawer.class) {
                if (drawableDrawer == null) {
                    drawableDrawer = new TextDrawableDrawer();
                }
            }
        }
        return drawableDrawer;
    }

    /**
     * 绘制一个Drawable resource
     * @param canvas
     * @param res
     * @param variableParams
     * @param immutableParams
     * @return resource对应的drawable，若此drawable可复制，则返回复制的drawable；反之返回获取到的drawable
     */
    public Drawable drawResource(@NonNull Canvas canvas, @DrawableRes int res,
                                        @NonNull VariableParams variableParams, @NonNull ImmutableParams immutableParams) {
        Drawable drawable = getSpecialDrawable(res, immutableParams.drawableHeight);
        return drawDrawable(canvas, drawable, variableParams, immutableParams);
    }

    /**
     * 绘制一个drawable
     * @param canvas
     * @param drawable
     * @param variableParams
     * @param immutableParams
     * @return 若此drawable可复制，则返回复制的drawable；反之返回获取到的drawable
     */
    public Drawable drawDrawable(Canvas canvas, Drawable drawable,
                                    @NonNull VariableParams variableParams, @NonNull ImmutableParams immutableParams) {
        drawable = getSpecialDrawable(drawable, immutableParams.drawableHeight);
        drawSpecialDrawable(canvas, drawable, variableParams, immutableParams);
        return drawable;
    }

    public Drawable getSpecialDrawable(@DrawableRes int res, int drawableHeight) {
        return getSpecialDrawable(getResources().getDrawable(res), drawableHeight);
    }

    public Drawable getSpecialDrawable(Drawable drawable, int drawableHeight) {
//        Drawable.ConstantState state = drawable.getConstantState();
//        if (state != null) {
//            Drawable newDrawable = state.newDrawable();
//            if (newDrawable != null) {
//                drawable = newDrawable;
//            }
//        }

        Rect bounds = drawable.getBounds();
        if (bounds.height() != drawableHeight) {
            int dw;
            final int diw = drawable.getIntrinsicWidth();
            final float dih = drawable.getIntrinsicHeight();
            if (dih != 0) {
                dw = (int) (diw * drawableHeight / dih);
            } else {
                dw = drawableHeight;
            }
            drawable.setBounds(0, 0, dw, drawableHeight);
        }
        return drawable;
    }

    public void drawSpecialDrawable(Canvas canvas, Drawable drawable,
                                           @NonNull VariableParams variableParams, @NonNull ImmutableParams immutableParams) {
        int dh = immutableParams.drawableHeight;
        Rect bounds = drawable.getBounds();
        int dw = bounds.width();
        if (bounds.height() != dh) {
            final int diw = drawable.getIntrinsicWidth();
            final float dih = drawable.getIntrinsicHeight();
            if (dih != 0) {
                dw = (int) (diw * dh / dih);
            } else {
                dw = dh;
            }
            drawable.setBounds(0, 0, dw, dh);
        }

        int maxWidth = TextDrawer.getDrawMaxWidthFronNow(variableParams, immutableParams);
        if (maxWidth < 0) {
            if (-maxWidth < dw) {
                TextDrawer.drawEllipsize(canvas, variableParams, immutableParams);
                return;
            }
        }

        canvas.save();
        canvas.translate(variableParams.currentLeft, variableParams.currentTop);
        drawable.draw(canvas);
        canvas.restore();
        variableParams.currentLeft += dw;
    }

    protected Resources getResources() {
        return context.getResources();
    }
}