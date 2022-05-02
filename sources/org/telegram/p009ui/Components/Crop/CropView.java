package org.telegram.p009ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0890R;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.p009ui.ActionBar.AlertDialog;
import org.telegram.p009ui.BubbleActivity;
import org.telegram.p009ui.Components.Crop.CropAreaView;
import org.telegram.p009ui.Components.Crop.CropGestureDetector;
import org.telegram.p009ui.Components.Paint.Swatch;
import org.telegram.p009ui.Components.Paint.Views.TextPaintView;
import org.telegram.p009ui.Components.PaintingOverlay;
import org.telegram.p009ui.Components.Point;
import org.telegram.p009ui.Components.VideoEditTextureView;

public class CropView extends FrameLayout implements CropAreaView.AreaViewListener, CropGestureDetector.CropGestureListener {
    private CropAreaView areaView;
    private Bitmap bitmap;
    private int bitmapRotation;
    private float bottomPadding;
    private CropTransform cropTransform;
    private CropGestureDetector detector;
    private boolean freeform;
    private boolean hasAspectRatioDialog;
    private ImageView imageView;
    private boolean inBubbleMode;
    private boolean isVisible;
    private CropViewListener listener;
    private float rotationStartScale;
    private CropState state;
    private VideoEditTextureView videoEditTextureView;
    float[] values = new float[9];
    RectF cropRect = new RectF();
    RectF sizeRect = new RectF(0.0f, 0.0f, 1280.0f, 1280.0f);
    private RectF previousAreaRect = new RectF();
    private RectF initialAreaRect = new RectF();
    private Matrix overlayMatrix = new Matrix();
    private CropRectangle tempRect = new CropRectangle();
    private Matrix tempMatrix = new Matrix();
    private boolean animating = false;

    public interface CropViewListener {
        void onAspectLock(boolean z);

        void onChange(boolean z);

        void onTapUp();

        void onUpdate();
    }

    @Override
    public void onFling(float f, float f2, float f3, float f4) {
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    public class CropState {
        private float baseRotation;
        private float height;
        private Matrix matrix;
        private float minimumScale;
        private boolean mirrored;
        private float orientation;
        private float rotation;
        private float scale;
        private float width;
        private float f1019x;
        private float f1020y;

        private CropState(int i, int i2, int i3) {
            this.width = i;
            this.height = i2;
            this.f1019x = 0.0f;
            this.f1020y = 0.0f;
            this.scale = 1.0f;
            this.baseRotation = i3;
            this.rotation = 0.0f;
            this.matrix = new Matrix();
        }

        public void update(int i, int i2, int i3) {
            float f = i;
            this.scale *= this.width / f;
            this.width = f;
            this.height = i2;
            updateMinimumScale();
            this.matrix.getValues(CropView.this.values);
            this.matrix.reset();
            Matrix matrix = this.matrix;
            float f2 = this.scale;
            matrix.postScale(f2, f2);
            Matrix matrix2 = this.matrix;
            float[] fArr = CropView.this.values;
            matrix2.postTranslate(fArr[2], fArr[5]);
            CropView.this.updateMatrix();
        }

        public boolean hasChanges() {
            return Math.abs(this.f1019x) > 1.0E-5f || Math.abs(this.f1020y) > 1.0E-5f || Math.abs(this.scale - this.minimumScale) > 1.0E-5f || Math.abs(this.rotation) > 1.0E-5f || Math.abs(this.orientation) > 1.0E-5f;
        }

        public float getWidth() {
            return this.width;
        }

        public float getHeight() {
            return this.height;
        }

        public float getOrientedWidth() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.height : this.width;
        }

        public float getOrientedHeight() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.width : this.height;
        }

        public void translate(float f, float f2) {
            this.f1019x += f;
            this.f1020y += f2;
            this.matrix.postTranslate(f, f2);
        }

        public float getX() {
            return this.f1019x;
        }

        public float getY() {
            return this.f1020y;
        }

        public void scale(float f, float f2, float f3) {
            this.scale *= f;
            this.matrix.postScale(f, f, f2, f3);
        }

        public float getScale() {
            return this.scale;
        }

        public void rotate(float f, float f2, float f3) {
            this.rotation += f;
            this.matrix.postRotate(f, f2, f3);
        }

        public float getRotation() {
            return this.rotation;
        }

        public float getOrientation() {
            return this.orientation + this.baseRotation;
        }

        public int getOrientationOnly() {
            return (int) this.orientation;
        }

        public float getBaseRotation() {
            return this.baseRotation;
        }

        public void mirror() {
            this.mirrored = !this.mirrored;
        }

        public void reset(CropAreaView cropAreaView, float f, boolean z) {
            this.matrix.reset();
            this.f1019x = 0.0f;
            this.f1020y = 0.0f;
            this.rotation = 0.0f;
            this.orientation = f;
            updateMinimumScale();
            float f2 = this.minimumScale;
            this.scale = f2;
            this.matrix.postScale(f2, f2);
        }

        private void updateMinimumScale() {
            float f = this.orientation;
            float f2 = this.baseRotation;
            float f3 = (f + f2) % 180.0f != 0.0f ? this.height : this.width;
            float f4 = (f + f2) % 180.0f != 0.0f ? this.width : this.height;
            if (CropView.this.freeform) {
                this.minimumScale = CropView.this.areaView.getCropWidth() / f3;
            } else {
                this.minimumScale = Math.max(CropView.this.areaView.getCropWidth() / f3, CropView.this.areaView.getCropHeight() / f4);
            }
        }

        public void getConcatMatrix(Matrix matrix) {
            matrix.postConcat(this.matrix);
        }

        public Matrix getMatrix() {
            Matrix matrix = new Matrix();
            matrix.set(this.matrix);
            return matrix;
        }
    }

    public CropView(Context context) {
        super(context);
        this.inBubbleMode = context instanceof BubbleActivity;
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        addView(this.imageView);
        CropGestureDetector cropGestureDetector = new CropGestureDetector(context);
        this.detector = cropGestureDetector;
        cropGestureDetector.setOnGestureListener(this);
        CropAreaView cropAreaView = new CropAreaView(context);
        this.areaView = cropAreaView;
        cropAreaView.setListener(this);
        addView(this.areaView);
    }

    public boolean isReady() {
        return !this.detector.isScaling() && !this.detector.isDragging() && !this.areaView.isDragging();
    }

    public void setListener(CropViewListener cropViewListener) {
        this.listener = cropViewListener;
    }

    public void setBottomPadding(float f) {
        this.bottomPadding = f;
        this.areaView.setBottomPadding(f);
    }

    public void setAspectRatio(float f) {
        this.areaView.setActualRect(f);
    }

    public void setBitmap(Bitmap bitmap, int i, boolean z, boolean z2, PaintingOverlay paintingOverlay, CropTransform cropTransform, VideoEditTextureView videoEditTextureView, final MediaController.CropState cropState) {
        this.freeform = z;
        this.videoEditTextureView = videoEditTextureView;
        this.cropTransform = cropTransform;
        this.bitmapRotation = i;
        this.bitmap = bitmap;
        this.areaView.setIsVideo(videoEditTextureView != null);
        Bitmap bitmap2 = null;
        if (bitmap == null && videoEditTextureView == null) {
            this.state = null;
            this.imageView.setImageDrawable(null);
            return;
        }
        final int currentWidth = getCurrentWidth();
        final int currentHeight = getCurrentHeight();
        CropState cropState2 = this.state;
        if (cropState2 == null || !z2) {
            this.state = new CropState(currentWidth, currentHeight, 0);
            this.areaView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int i2;
                    int i3;
                    float f;
                    float f2;
                    CropView.this.reset();
                    MediaController.CropState cropState3 = cropState;
                    if (cropState3 != null) {
                        boolean z3 = true;
                        if (cropState3.lockedAspectRatio > 1.0E-4f) {
                            CropView.this.areaView.setLockedAspectRatio(cropState.lockedAspectRatio);
                            if (CropView.this.listener != null) {
                                CropView.this.listener.onAspectLock(true);
                            }
                        }
                        CropView.this.setFreeform(cropState.freeform);
                        float aspectRatio = CropView.this.areaView.getAspectRatio();
                        int i4 = cropState.transformRotation;
                        if (i4 == 90 || i4 == 270) {
                            aspectRatio = 1.0f / aspectRatio;
                            f2 = CropView.this.state.height;
                            f = CropView.this.state.width;
                            i3 = currentHeight;
                            i2 = currentWidth;
                        } else {
                            f2 = CropView.this.state.width;
                            f = CropView.this.state.height;
                            i3 = currentWidth;
                            i2 = currentHeight;
                        }
                        int i5 = cropState.transformRotation;
                        boolean z4 = CropView.this.freeform;
                        if (!CropView.this.freeform || CropView.this.areaView.getLockAspectRatio() <= 0.0f) {
                            CropAreaView cropAreaView = CropView.this.areaView;
                            int currentWidth2 = CropView.this.getCurrentWidth();
                            int currentHeight2 = CropView.this.getCurrentHeight();
                            if ((i5 + CropView.this.state.getBaseRotation()) % 180.0f == 0.0f) {
                                z3 = false;
                            }
                            cropAreaView.setBitmap(currentWidth2, currentHeight2, z3, CropView.this.freeform);
                        } else {
                            CropView.this.areaView.setLockedAspectRatio(1.0f / CropView.this.areaView.getLockAspectRatio());
                            CropView.this.areaView.setActualRect(CropView.this.areaView.getLockAspectRatio());
                            z4 = false;
                        }
                        CropView.this.state.reset(CropView.this.areaView, i5, z4);
                        CropAreaView cropAreaView2 = CropView.this.areaView;
                        MediaController.CropState cropState4 = cropState;
                        cropAreaView2.setActualRect((aspectRatio * cropState4.cropPw) / cropState4.cropPh);
                        CropView.this.state.mirrored = cropState.mirrored;
                        CropView.this.state.rotate(cropState.cropRotate, 0.0f, 0.0f);
                        CropView.this.state.translate(cropState.cropPx * i3 * CropView.this.state.minimumScale, cropState.cropPy * i2 * CropView.this.state.minimumScale);
                        CropView.this.state.scale(cropState.cropScale * (Math.max(CropView.this.areaView.getCropWidth() / f2, CropView.this.areaView.getCropHeight() / f) / CropView.this.state.minimumScale), 0.0f, 0.0f);
                        CropView.this.updateMatrix();
                        if (CropView.this.listener != null) {
                            CropView.this.listener.onChange(false);
                        }
                    }
                    CropView.this.areaView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        } else {
            cropState2.update(currentWidth, currentHeight, i);
        }
        ImageView imageView = this.imageView;
        if (videoEditTextureView == null) {
            bitmap2 = this.bitmap;
        }
        imageView.setImageBitmap(bitmap2);
    }

    public void willShow() {
        this.areaView.setFrameVisibility(true, false);
        this.areaView.setDimVisibility(true);
        this.areaView.invalidate();
    }

    public void setFreeform(boolean z) {
        this.areaView.setFreeform(z);
        this.freeform = z;
    }

    public void onShow() {
        this.isVisible = true;
    }

    public void onHide() {
        this.videoEditTextureView = null;
        this.isVisible = false;
    }

    public void show() {
        updateCropTransform();
        this.areaView.setDimVisibility(true);
        this.areaView.setFrameVisibility(true, true);
        this.areaView.invalidate();
    }

    public void hide() {
        this.imageView.setVisibility(4);
        this.areaView.setDimVisibility(false);
        this.areaView.setFrameVisibility(false, false);
        this.areaView.invalidate();
    }

    public void reset() {
        this.areaView.resetAnimator();
        this.areaView.setBitmap(getCurrentWidth(), getCurrentHeight(), this.state.getBaseRotation() % 180.0f != 0.0f, this.freeform);
        this.areaView.setLockedAspectRatio(this.freeform ? 0.0f : 1.0f);
        this.state.reset(this.areaView, 0.0f, this.freeform);
        this.state.mirrored = false;
        this.areaView.getCropRect(this.initialAreaRect);
        updateMatrix();
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(true);
            this.listener.onAspectLock(false);
        }
    }

    public void updateMatrix() {
        this.overlayMatrix.reset();
        if (this.state.getBaseRotation() == 90.0f || this.state.getBaseRotation() == 270.0f) {
            this.overlayMatrix.postTranslate((-this.state.getHeight()) / 2.0f, (-this.state.getWidth()) / 2.0f);
        } else {
            this.overlayMatrix.postTranslate((-this.state.getWidth()) / 2.0f, (-this.state.getHeight()) / 2.0f);
        }
        this.overlayMatrix.postRotate(this.state.getOrientationOnly());
        this.state.getConcatMatrix(this.overlayMatrix);
        this.overlayMatrix.postTranslate(this.areaView.getCropCenterX(), this.areaView.getCropCenterY());
        if (!this.freeform || this.isVisible) {
            updateCropTransform();
            this.listener.onUpdate();
        }
        invalidate();
    }

    private void fillAreaView(RectF rectF, boolean z) {
        final boolean z2;
        final float f;
        int i = 0;
        final float[] fArr = {1.0f};
        float max = Math.max(rectF.width() / this.areaView.getCropWidth(), rectF.height() / this.areaView.getCropHeight());
        if (this.state.getScale() * max > 30.0f) {
            f = 30.0f / this.state.getScale();
            z2 = true;
        } else {
            f = max;
            z2 = false;
        }
        if (Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode) {
            i = AndroidUtilities.statusBarHeight;
        }
        final float orientedWidth = this.state.getOrientedWidth() * ((rectF.centerX() - (this.imageView.getWidth() / 2)) / this.areaView.getCropWidth());
        final float centerY = ((rectF.centerY() - (((this.imageView.getHeight() - this.bottomPadding) + i) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CropView.this.lambda$fillAreaView$0(f, fArr, orientedWidth, centerY, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (z2) {
                    CropView.this.fitContentInBounds(false, false, true);
                }
            }
        });
        this.areaView.fill(rectF, ofFloat, true);
        this.initialAreaRect.set(rectF);
    }

    public void lambda$fillAreaView$0(float f, float[] fArr, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = (((f - 1.0f) * ((Float) valueAnimator.getAnimatedValue()).floatValue()) + 1.0f) / fArr[0];
        fArr[0] = fArr[0] * floatValue;
        this.state.scale(floatValue, f2, f3);
        updateMatrix();
    }

    private float fitScale(RectF rectF, float f, float f2) {
        float width = rectF.width() * f2;
        float height = rectF.height() * f2;
        float width2 = (rectF.width() - width) / 2.0f;
        float height2 = (rectF.height() - height) / 2.0f;
        float f3 = rectF.left;
        float f4 = rectF.top;
        rectF.set(f3 + width2, f4 + height2, f3 + width2 + width, f4 + height2 + height);
        return f * f2;
    }

    private void fitTranslation(RectF rectF, RectF rectF2, PointF pointF, float f) {
        float f2 = rectF2.left;
        float f3 = rectF2.top;
        float f4 = rectF2.right;
        float f5 = rectF2.bottom;
        float f6 = rectF.left;
        if (f6 > f2) {
            f4 += f6 - f2;
            f2 = f6;
        }
        float f7 = rectF.top;
        if (f7 > f3) {
            f5 += f7 - f3;
            f3 = f7;
        }
        float f8 = rectF.right;
        if (f8 < f4) {
            f2 += f8 - f4;
        }
        float f9 = rectF.bottom;
        if (f9 < f5) {
            f3 += f9 - f5;
        }
        float centerX = rectF2.centerX() - (f2 + (rectF2.width() / 2.0f));
        float centerY = rectF2.centerY() - (f3 + (rectF2.height() / 2.0f));
        double d = f;
        Double.isNaN(d);
        double d2 = 1.5707963267948966d - d;
        double sin = Math.sin(d2);
        double d3 = centerX;
        Double.isNaN(d3);
        double cos = Math.cos(d2);
        Double.isNaN(d3);
        float f10 = (float) (cos * d3);
        Double.isNaN(d);
        double d4 = d + 1.5707963267948966d;
        double cos2 = Math.cos(d4);
        double d5 = centerY;
        Double.isNaN(d5);
        double sin2 = Math.sin(d4);
        Double.isNaN(d5);
        pointF.set(pointF.x + ((float) (sin * d3)) + ((float) (cos2 * d5)), pointF.y + f10 + ((float) (sin2 * d5)));
    }

    public RectF calculateBoundingBox(float f, float f2, float f3) {
        RectF rectF = new RectF(0.0f, 0.0f, f, f2);
        Matrix matrix = new Matrix();
        matrix.postRotate(f3, f / 2.0f, f2 / 2.0f);
        matrix.mapRect(rectF);
        return rectF;
    }

    public float scaleWidthToMaxSize(RectF rectF, RectF rectF2) {
        float width = rectF2.width();
        return ((float) Math.floor((double) ((rectF.height() * width) / rectF.width()))) > rectF2.height() ? (float) Math.floor((rectF2.height() * rectF.width()) / rectF.height()) : width;
    }

    public static class CropRectangle {
        float[] coords = new float[8];

        CropRectangle() {
        }

        void setRect(RectF rectF) {
            float[] fArr = this.coords;
            float f = rectF.left;
            fArr[0] = f;
            float f2 = rectF.top;
            fArr[1] = f2;
            float f3 = rectF.right;
            fArr[2] = f3;
            fArr[3] = f2;
            fArr[4] = f3;
            float f4 = rectF.bottom;
            fArr[5] = f4;
            fArr[6] = f;
            fArr[7] = f4;
        }

        void applyMatrix(Matrix matrix) {
            matrix.mapPoints(this.coords);
        }

        void getRect(RectF rectF) {
            float[] fArr = this.coords;
            rectF.set(fArr[0], fArr[1], fArr[2], fArr[7]);
        }
    }

    public void fitContentInBounds(boolean z, boolean z2, boolean z3) {
        fitContentInBounds(z, z2, z3, false);
    }

    public void fitContentInBounds(final boolean z, final boolean z2, final boolean z3, final boolean z4) {
        float f;
        if (this.state != null) {
            float cropWidth = this.areaView.getCropWidth();
            float cropHeight = this.areaView.getCropHeight();
            float orientedWidth = this.state.getOrientedWidth();
            float orientedHeight = this.state.getOrientedHeight();
            float rotation = this.state.getRotation();
            float radians = (float) Math.toRadians(rotation);
            RectF calculateBoundingBox = calculateBoundingBox(cropWidth, cropHeight, rotation);
            RectF rectF = new RectF(0.0f, 0.0f, orientedWidth, orientedHeight);
            float scale = this.state.getScale();
            this.tempRect.setRect(rectF);
            Matrix matrix = this.state.getMatrix();
            matrix.preTranslate(((cropWidth - orientedWidth) / 2.0f) / scale, ((cropHeight - orientedHeight) / 2.0f) / scale);
            this.tempMatrix.reset();
            this.tempMatrix.setTranslate(rectF.centerX(), rectF.centerY());
            Matrix matrix2 = this.tempMatrix;
            matrix2.setConcat(matrix2, matrix);
            this.tempMatrix.preTranslate(-rectF.centerX(), -rectF.centerY());
            this.tempRect.applyMatrix(this.tempMatrix);
            this.tempMatrix.reset();
            this.tempMatrix.preRotate(-rotation, orientedWidth / 2.0f, orientedHeight / 2.0f);
            this.tempRect.applyMatrix(this.tempMatrix);
            this.tempRect.getRect(rectF);
            PointF pointF = new PointF(this.state.getX(), this.state.getY());
            if (!rectF.contains(calculateBoundingBox)) {
                f = (!z || (calculateBoundingBox.width() <= rectF.width() && calculateBoundingBox.height() <= rectF.height())) ? scale : fitScale(rectF, scale, calculateBoundingBox.width() / scaleWidthToMaxSize(calculateBoundingBox, rectF));
                fitTranslation(rectF, calculateBoundingBox, pointF, radians);
            } else if (!z2 || this.rotationStartScale <= 0.0f) {
                f = scale;
            } else {
                float width = calculateBoundingBox.width() / scaleWidthToMaxSize(calculateBoundingBox, rectF);
                if (this.state.getScale() * width < this.rotationStartScale) {
                    width = 1.0f;
                }
                f = fitScale(rectF, scale, width);
                fitTranslation(rectF, calculateBoundingBox, pointF, radians);
            }
            final float x = pointF.x - this.state.getX();
            final float y = pointF.y - this.state.getY();
            if (z3) {
                final float f2 = f / scale;
                if (Math.abs(f2 - 1.0f) >= 1.0E-5f || Math.abs(x) >= 1.0E-5f || Math.abs(y) >= 1.0E-5f) {
                    this.animating = true;
                    final float[] fArr = {1.0f, 0.0f, 0.0f};
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            CropView.this.lambda$fitContentInBounds$1(x, fArr, y, f2, valueAnimator);
                        }
                    });
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            CropView.this.animating = false;
                            if (!z4) {
                                CropView.this.fitContentInBounds(z, z2, z3, true);
                            }
                        }
                    });
                    ofFloat.setInterpolator(this.areaView.getInterpolator());
                    ofFloat.setDuration(z4 ? 100L : 200L);
                    ofFloat.start();
                    return;
                }
                return;
            }
            this.state.translate(x, y);
            this.state.scale(f / scale, 0.0f, 0.0f);
            updateMatrix();
        }
    }

    public void lambda$fitContentInBounds$1(float f, float[] fArr, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f4 = (f * floatValue) - fArr[1];
        fArr[1] = fArr[1] + f4;
        float f5 = (f2 * floatValue) - fArr[2];
        fArr[2] = fArr[2] + f5;
        this.state.translate(f4 * fArr[0], f5 * fArr[0]);
        float f6 = (((f3 - 1.0f) * floatValue) + 1.0f) / fArr[0];
        fArr[0] = fArr[0] * f6;
        this.state.scale(f6, 0.0f, 0.0f);
        updateMatrix();
    }

    public int getCurrentWidth() {
        VideoEditTextureView videoEditTextureView = this.videoEditTextureView;
        if (videoEditTextureView != null) {
            return videoEditTextureView.getVideoWidth();
        }
        int i = this.bitmapRotation;
        return (i == 90 || i == 270) ? this.bitmap.getHeight() : this.bitmap.getWidth();
    }

    public int getCurrentHeight() {
        VideoEditTextureView videoEditTextureView = this.videoEditTextureView;
        if (videoEditTextureView != null) {
            return videoEditTextureView.getVideoHeight();
        }
        int i = this.bitmapRotation;
        return (i == 90 || i == 270) ? this.bitmap.getWidth() : this.bitmap.getHeight();
    }

    public boolean mirror() {
        CropState cropState = this.state;
        boolean z = false;
        if (cropState == null) {
            return false;
        }
        cropState.mirror();
        updateMatrix();
        if (this.listener != null) {
            float orientation = (this.state.getOrientation() - this.state.getBaseRotation()) % 360.0f;
            CropViewListener cropViewListener = this.listener;
            if (!this.state.hasChanges() && orientation == 0.0f && this.areaView.getLockAspectRatio() == 0.0f && !this.state.mirrored) {
                z = true;
            }
            cropViewListener.onChange(z);
        }
        return this.state.mirrored;
    }

    public boolean rotate90Degrees() {
        if (this.state == null) {
            return false;
        }
        this.areaView.resetAnimator();
        resetRotationStartScale();
        float orientation = ((this.state.getOrientation() - this.state.getBaseRotation()) - 90.0f) % 360.0f;
        boolean z = this.freeform;
        if (!z || this.areaView.getLockAspectRatio() <= 0.0f) {
            this.areaView.setBitmap(getCurrentWidth(), getCurrentHeight(), (this.state.getBaseRotation() + orientation) % 180.0f != 0.0f, this.freeform);
        } else {
            CropAreaView cropAreaView = this.areaView;
            cropAreaView.setLockedAspectRatio(1.0f / cropAreaView.getLockAspectRatio());
            CropAreaView cropAreaView2 = this.areaView;
            cropAreaView2.setActualRect(cropAreaView2.getLockAspectRatio());
            z = false;
        }
        this.state.reset(this.areaView, orientation, z);
        updateMatrix();
        fitContentInBounds(true, false, false);
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(orientation == 0.0f && this.areaView.getLockAspectRatio() == 0.0f && !this.state.mirrored);
        }
        return this.state.getOrientationOnly() != 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.animating || this.areaView.onTouchEvent(motionEvent)) {
            return true;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            onScrollChangeBegan();
        } else if (action == 1 || action == 3) {
            onScrollChangeEnded();
        }
        try {
            return this.detector.onTouchEvent(motionEvent);
        } catch (Exception unused) {
            return false;
        }
    }

    @Override
    public void onAreaChangeBegan() {
        this.areaView.getCropRect(this.previousAreaRect);
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
        }
    }

    @Override
    public void onAreaChange() {
        this.areaView.setGridType(CropAreaView.GridType.MAJOR, false);
        float centerX = this.previousAreaRect.centerX() - this.areaView.getCropCenterX();
        float centerY = this.previousAreaRect.centerY() - this.areaView.getCropCenterY();
        CropState cropState = this.state;
        if (cropState != null) {
            cropState.translate(centerX, centerY);
        }
        updateMatrix();
        this.areaView.getCropRect(this.previousAreaRect);
        fitContentInBounds(true, false, false);
    }

    @Override
    public void onAreaChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fillAreaView(this.areaView.getTargetRectToFill(), false);
    }

    @Override
    public void onDrag(float f, float f2) {
        if (!this.animating) {
            this.state.translate(f, f2);
            updateMatrix();
        }
    }

    @Override
    public void onTapUp() {
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onTapUp();
        }
    }

    public void onScrollChangeBegan() {
        if (!this.animating) {
            this.areaView.setGridType(CropAreaView.GridType.MAJOR, true);
            resetRotationStartScale();
            CropViewListener cropViewListener = this.listener;
            if (cropViewListener != null) {
                cropViewListener.onChange(false);
            }
        }
    }

    public void onScrollChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fitContentInBounds(true, false, true);
    }

    @Override
    public void onScale(float f, float f2, float f3) {
        if (!this.animating) {
            if (this.state.getScale() * f > 30.0f) {
                f = 30.0f / this.state.getScale();
            }
            this.state.scale(f, ((f2 - (this.imageView.getWidth() / 2)) / this.areaView.getCropWidth()) * this.state.getOrientedWidth(), ((f3 - (((this.imageView.getHeight() - this.bottomPadding) - ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight());
            updateMatrix();
        }
    }

    public void onRotationBegan() {
        this.areaView.setGridType(CropAreaView.GridType.MINOR, false);
        if (this.rotationStartScale < 1.0E-5f) {
            this.rotationStartScale = this.state.getScale();
        }
    }

    public void onRotationEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
    }

    private void resetRotationStartScale() {
        this.rotationStartScale = 0.0f;
    }

    @Override
    public void setRotation(float f) {
        this.state.rotate(f - this.state.getRotation(), 0.0f, 0.0f);
        fitContentInBounds(true, true, false);
    }

    public static void editBitmap(Context context, String str, Bitmap bitmap, Canvas canvas, Bitmap bitmap2, Bitmap.CompressFormat compressFormat, Matrix matrix, int i, int i2, float f, float f2, float f3, float f4, boolean z, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z2) {
        float f5 = f3;
        char c = 0;
        if (z2) {
            try {
                bitmap2.eraseColor(0);
            } catch (Throwable th) {
                FileLog.m30e(th);
                return;
            }
        }
        Bitmap decodeFile = bitmap == null ? BitmapFactory.decodeFile(str) : bitmap;
        float max = Math.max(decodeFile.getWidth(), decodeFile.getHeight()) / Math.max(i, i2);
        Matrix matrix2 = new Matrix();
        int i3 = 2;
        matrix2.postTranslate((-decodeFile.getWidth()) / 2, (-decodeFile.getHeight()) / 2);
        if (z) {
            matrix2.postScale(-1.0f, 1.0f);
        }
        float f6 = 1.0f / max;
        matrix2.postScale(f6, f6);
        matrix2.postRotate(f5);
        matrix2.postConcat(matrix);
        matrix2.postScale(f4, f4);
        matrix2.postTranslate(bitmap2.getWidth() / 2, bitmap2.getHeight() / 2);
        canvas.drawBitmap(decodeFile, matrix2, new Paint(2));
        FileOutputStream fileOutputStream = new FileOutputStream(new File(str));
        bitmap2.compress(compressFormat, 87, fileOutputStream);
        fileOutputStream.close();
        if (arrayList != null && !arrayList.isEmpty()) {
            float[] fArr = new float[4];
            float width = f6 * f4 * f * (decodeFile.getWidth() / bitmap2.getWidth());
            TextPaintView textPaintView = null;
            int size = arrayList.size();
            int i4 = 0;
            while (i4 < size) {
                VideoEditedInfo.MediaEntity mediaEntity = arrayList.get(i4);
                fArr[c] = (mediaEntity.f823x * decodeFile.getWidth()) + ((mediaEntity.viewWidth * mediaEntity.scale) / 2.0f);
                fArr[1] = (mediaEntity.f824y * decodeFile.getHeight()) + ((mediaEntity.viewHeight * mediaEntity.scale) / 2.0f);
                fArr[i3] = mediaEntity.textViewX * decodeFile.getWidth();
                fArr[3] = mediaEntity.textViewY * decodeFile.getHeight();
                matrix2.mapPoints(fArr);
                byte b = mediaEntity.type;
                if (b == 0) {
                    int width2 = bitmap2.getWidth() / i3;
                    mediaEntity.viewHeight = width2;
                    mediaEntity.viewWidth = width2;
                } else if (b == 1) {
                    mediaEntity.fontSize = bitmap2.getWidth() / 9;
                    if (textPaintView == null) {
                        textPaintView = new TextPaintView(context, new Point(0.0f, 0.0f), mediaEntity.fontSize, "", new Swatch(-16777216, 0.85f, 0.1f), 0);
                        textPaintView.setMaxWidth(bitmap2.getWidth() - 20);
                    }
                    byte b2 = mediaEntity.subType;
                    textPaintView.setType((b2 & 1) != 0 ? 0 : (b2 & 4) != 0 ? 2 : 1);
                    textPaintView.setText(mediaEntity.text);
                    textPaintView.measure(View.MeasureSpec.makeMeasureSpec(bitmap2.getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(bitmap2.getHeight(), Integer.MIN_VALUE));
                    mediaEntity.viewWidth = textPaintView.getMeasuredWidth();
                    mediaEntity.viewHeight = textPaintView.getMeasuredHeight();
                }
                float f7 = mediaEntity.scale * width;
                mediaEntity.scale = f7;
                c = 0;
                mediaEntity.f823x = (fArr[0] - ((mediaEntity.viewWidth * f7) / 2.0f)) / bitmap2.getWidth();
                mediaEntity.f824y = (fArr[1] - ((mediaEntity.viewHeight * mediaEntity.scale) / 2.0f)) / bitmap2.getHeight();
                mediaEntity.textViewX = fArr[2] / bitmap2.getWidth();
                mediaEntity.textViewY = fArr[3] / bitmap2.getHeight();
                mediaEntity.width = (mediaEntity.viewWidth * mediaEntity.scale) / bitmap2.getWidth();
                mediaEntity.height = (mediaEntity.viewHeight * mediaEntity.scale) / bitmap2.getHeight();
                mediaEntity.textViewWidth = mediaEntity.viewWidth / bitmap2.getWidth();
                mediaEntity.textViewHeight = mediaEntity.viewHeight / bitmap2.getHeight();
                double d = mediaEntity.rotation;
                double d2 = f2 + f5;
                Double.isNaN(d2);
                Double.isNaN(d);
                mediaEntity.rotation = (float) (d - (d2 * 0.017453292519943295d));
                i4++;
                f5 = f3;
                i3 = 2;
            }
        }
        decodeFile.recycle();
    }

    private void updateCropTransform() {
        float f;
        float f2;
        int i;
        float f3;
        if (this.cropTransform != null && this.state != null) {
            this.areaView.getCropRect(this.cropRect);
            int ceil = (int) Math.ceil(scaleWidthToMaxSize(this.cropRect, this.sizeRect));
            int ceil2 = (int) Math.ceil(f / this.areaView.getAspectRatio());
            float cropWidth = ceil / this.areaView.getCropWidth();
            this.state.matrix.getValues(this.values);
            float f4 = this.state.minimumScale * cropWidth;
            int orientationOnly = this.state.getOrientationOnly();
            while (orientationOnly < 0) {
                orientationOnly += 360;
            }
            if (orientationOnly == 90 || orientationOnly == 270) {
                i = (int) this.state.height;
                f2 = this.state.width;
            } else {
                i = (int) this.state.width;
                f2 = this.state.height;
            }
            double d = ceil;
            float f5 = i;
            double ceil3 = Math.ceil(f5 * f4);
            Double.isNaN(d);
            float f6 = (float) (d / ceil3);
            double d2 = ceil2;
            float f7 = (int) f2;
            double ceil4 = Math.ceil(f4 * f7);
            Double.isNaN(d2);
            float f8 = (float) (d2 / ceil4);
            if (f6 > 1.0f || f8 > 1.0f) {
                float max = Math.max(f6, f8);
                f6 /= max;
                f8 /= max;
            }
            float f9 = f6;
            float f10 = f8;
            RectF targetRectToFill = this.areaView.getTargetRectToFill(f5 / f7);
            if (this.freeform) {
                f3 = targetRectToFill.width() / f5;
            } else {
                f3 = Math.max(targetRectToFill.width() / f5, targetRectToFill.height() / f7);
            }
            float f11 = this.state.scale / f3;
            float f12 = this.state.scale / this.state.minimumScale;
            float f13 = (this.values[2] / f5) / this.state.scale;
            float f14 = (this.values[5] / f7) / this.state.scale;
            float f15 = this.state.rotation;
            RectF targetRectToFill2 = this.areaView.getTargetRectToFill();
            this.cropTransform.setViewTransform(this.state.mirrored || this.state.hasChanges() || this.state.getBaseRotation() >= 1.0E-5f, f13, f14, f15, this.state.getOrientationOnly(), f11, f12, this.state.minimumScale / f3, f9, f10, this.areaView.getCropCenterX() - targetRectToFill2.centerX(), this.areaView.getCropCenterY() - targetRectToFill2.centerY(), this.state.mirrored);
        }
    }

    public static String getCopy(String str) {
        File directory = FileLoader.getDirectory(4);
        File file = new File(directory, SharedConfig.getLastLocalId() + "_temp.jpg");
        try {
            AndroidUtilities.copyFile(new File(str), file);
        } catch (Exception e) {
            FileLog.m30e(e);
        }
        return file.getAbsolutePath();
    }

    public void makeCrop(MediaController.MediaEditState mediaEditState) {
        float f;
        int i;
        float f2;
        int i2;
        if (this.state != null) {
            this.areaView.getCropRect(this.cropRect);
            int ceil = (int) Math.ceil(scaleWidthToMaxSize(this.cropRect, this.sizeRect));
            int ceil2 = (int) Math.ceil(f / this.areaView.getAspectRatio());
            float cropWidth = ceil / this.areaView.getCropWidth();
            if (mediaEditState.paintPath != null) {
                Bitmap createBitmap = Bitmap.createBitmap(ceil, ceil2, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                String copy = getCopy(mediaEditState.paintPath);
                if (mediaEditState.croppedPaintPath != null) {
                    new File(mediaEditState.croppedPaintPath).delete();
                    mediaEditState.croppedPaintPath = null;
                }
                mediaEditState.croppedPaintPath = copy;
                ArrayList<VideoEditedInfo.MediaEntity> arrayList = mediaEditState.mediaEntities;
                if (arrayList == null || arrayList.isEmpty()) {
                    mediaEditState.croppedMediaEntities = null;
                } else {
                    mediaEditState.croppedMediaEntities = new ArrayList<>(mediaEditState.mediaEntities.size());
                    int size = mediaEditState.mediaEntities.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        mediaEditState.croppedMediaEntities.add(mediaEditState.mediaEntities.get(i3).copy());
                    }
                }
                editBitmap(getContext(), copy, null, canvas, createBitmap, Bitmap.CompressFormat.PNG, this.state.matrix, getCurrentWidth(), getCurrentHeight(), this.state.scale, this.state.rotation, this.state.getOrientationOnly(), cropWidth, false, mediaEditState.croppedMediaEntities, false);
            }
            if (mediaEditState.cropState == null) {
                mediaEditState.cropState = new MediaController.CropState();
            }
            this.state.matrix.getValues(this.values);
            float f3 = this.state.minimumScale * cropWidth;
            mediaEditState.cropState.transformRotation = this.state.getOrientationOnly();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m33d("set transformRotation = " + mediaEditState.cropState.transformRotation);
            }
            while (true) {
                MediaController.CropState cropState = mediaEditState.cropState;
                i = cropState.transformRotation;
                if (i >= 0) {
                    break;
                }
                cropState.transformRotation = i + 360;
            }
            if (i == 90 || i == 270) {
                i2 = (int) this.state.height;
                f2 = this.state.width;
            } else {
                i2 = (int) this.state.width;
                f2 = this.state.height;
            }
            MediaController.CropState cropState2 = mediaEditState.cropState;
            double d = ceil;
            float f4 = i2;
            double ceil3 = Math.ceil(f4 * f3);
            Double.isNaN(d);
            cropState2.cropPw = (float) (d / ceil3);
            MediaController.CropState cropState3 = mediaEditState.cropState;
            double d2 = ceil2;
            float f5 = (int) f2;
            double ceil4 = Math.ceil(f3 * f5);
            Double.isNaN(d2);
            cropState3.cropPh = (float) (d2 / ceil4);
            MediaController.CropState cropState4 = mediaEditState.cropState;
            float f6 = cropState4.cropPw;
            if (f6 > 1.0f || cropState4.cropPh > 1.0f) {
                float max = Math.max(f6, cropState4.cropPh);
                MediaController.CropState cropState5 = mediaEditState.cropState;
                cropState5.cropPw /= max;
                cropState5.cropPh /= max;
            }
            mediaEditState.cropState.cropScale = this.state.scale * Math.min(f4 / this.areaView.getCropWidth(), f5 / this.areaView.getCropHeight());
            mediaEditState.cropState.cropPx = (this.values[2] / f4) / this.state.scale;
            mediaEditState.cropState.cropPy = (this.values[5] / f5) / this.state.scale;
            mediaEditState.cropState.cropRotate = this.state.rotation;
            mediaEditState.cropState.stateScale = this.state.scale;
            mediaEditState.cropState.mirrored = this.state.mirrored;
            MediaController.CropState cropState6 = mediaEditState.cropState;
            cropState6.scale = cropWidth;
            cropState6.matrix = this.state.matrix;
            MediaController.CropState cropState7 = mediaEditState.cropState;
            cropState7.width = ceil;
            cropState7.height = ceil2;
            cropState7.freeform = this.freeform;
            cropState7.lockedAspectRatio = this.areaView.getLockAspectRatio();
            mediaEditState.cropState.initied = true;
        }
    }

    private void setLockedAspectRatio(float f) {
        this.areaView.setLockedAspectRatio(f);
        RectF rectF = new RectF();
        this.areaView.calculateRect(rectF, f);
        fillAreaView(rectF, true);
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
            this.listener.onAspectLock(true);
        }
    }

    public void showAspectRatioDialog() {
        if (this.state != null && !this.hasAspectRatioDialog) {
            this.hasAspectRatioDialog = true;
            String[] strArr = new String[8];
            final Integer[][] numArr = {new Integer[]{3, 2}, new Integer[]{5, 3}, new Integer[]{4, 3}, new Integer[]{5, 4}, new Integer[]{7, 5}, new Integer[]{16, 9}};
            strArr[0] = LocaleController.getString("CropOriginal", C0890R.string.CropOriginal);
            strArr[1] = LocaleController.getString("CropSquare", C0890R.string.CropSquare);
            int i = 2;
            for (int i2 = 0; i2 < 6; i2++) {
                Integer[] numArr2 = numArr[i2];
                if (this.areaView.getAspectRatio() > 1.0f) {
                    strArr[i] = String.format("%d:%d", numArr2[0], numArr2[1]);
                } else {
                    strArr[i] = String.format("%d:%d", numArr2[1], numArr2[0]);
                }
                i++;
            }
            AlertDialog create = new AlertDialog.Builder(getContext()).setItems(strArr, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i3) {
                    CropView.this.lambda$showAspectRatioDialog$2(numArr, dialogInterface, i3);
                }
            }).create();
            create.setCanceledOnTouchOutside(true);
            create.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public final void onCancel(DialogInterface dialogInterface) {
                    CropView.this.lambda$showAspectRatioDialog$3(dialogInterface);
                }
            });
            create.show();
        }
    }

    public void lambda$showAspectRatioDialog$2(Integer[][] numArr, DialogInterface dialogInterface, int i) {
        this.hasAspectRatioDialog = false;
        if (i == 0) {
            setLockedAspectRatio((this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getHeight() : this.state.getWidth()) / (this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getWidth() : this.state.getHeight()));
        } else if (i != 1) {
            Integer[] numArr2 = numArr[i - 2];
            if (this.areaView.getAspectRatio() > 1.0f) {
                setLockedAspectRatio(numArr2[0].intValue() / numArr2[1].intValue());
            } else {
                setLockedAspectRatio(numArr2[1].intValue() / numArr2[0].intValue());
            }
        } else {
            setLockedAspectRatio(1.0f);
        }
    }

    public void lambda$showAspectRatioDialog$3(DialogInterface dialogInterface) {
        this.hasAspectRatioDialog = false;
    }

    public void updateLayout() {
        CropState cropState;
        float cropWidth = this.areaView.getCropWidth();
        if (cropWidth != 0.0f && (cropState = this.state) != null) {
            this.areaView.calculateRect(this.initialAreaRect, cropState.getWidth() / this.state.getHeight());
            CropAreaView cropAreaView = this.areaView;
            cropAreaView.setActualRect(cropAreaView.getAspectRatio());
            this.areaView.getCropRect(this.previousAreaRect);
            this.state.scale(this.areaView.getCropWidth() / cropWidth, 0.0f, 0.0f);
            updateMatrix();
        }
    }

    public float getCropLeft() {
        return this.areaView.getCropLeft();
    }

    public float getCropTop() {
        return this.areaView.getCropTop();
    }

    public float getCropWidth() {
        return this.areaView.getCropWidth();
    }

    public float getCropHeight() {
        return this.areaView.getCropHeight();
    }

    public RectF getActualRect() {
        this.areaView.getCropRect(this.cropRect);
        return this.cropRect;
    }
}
