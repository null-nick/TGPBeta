package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.collection.LongSparseArray;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EmojiTabsStrip;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.Premium.PremiumLockIconView;
import org.telegram.ui.Components.Reactions.HwEmojis;
public class EmojiTabsStrip extends ScrollableHorizontalScrollView {
    private final int accentColor;
    public boolean animateAppear;
    private int animatedEmojiCacheType;
    private ValueAnimator appearAnimation;
    private int appearCount;
    private int currentType;
    private EmojiTabsView emojiTabs;
    boolean first;
    private boolean forceTabsShow;
    private boolean includeAnimated;
    private Runnable onSettingsOpenRunnable;
    private int packsIndexStart;
    private float paddingLeftDp;
    private int recentDrawableId;
    private boolean recentFirstChange;
    private boolean recentIsShown;
    public EmojiTabButton recentTab;
    private HashMap<View, android.graphics.Rect> removingViews;
    private Theme.ResourcesProvider resourcesProvider;
    private float selectAnimationT;
    private ValueAnimator selectAnimator;
    private float selectT;
    private int selected;
    private int settingsDrawableId;
    private EmojiTabButton settingsTab;
    private boolean showSelected;
    private AnimatedFloat showSelectedAlpha;
    public EmojiTabButton toggleEmojiStickersTab;
    public boolean updateButtonDrawables;
    private boolean wasDrawn;
    private int wasIndex;
    private static int[] emojiTabsDrawableIds = {R.drawable.msg_emoji_smiles, R.drawable.msg_emoji_cat, R.drawable.msg_emoji_food, R.drawable.msg_emoji_activities, R.drawable.msg_emoji_travel, R.drawable.msg_emoji_objects, R.drawable.msg_emoji_other, R.drawable.msg_emoji_flags};
    private static int[] emojiTabsAnimatedDrawableIds = {R.raw.msg_emoji_smiles, R.raw.msg_emoji_cat, R.raw.msg_emoji_food, R.raw.msg_emoji_activities, R.raw.msg_emoji_travel, R.raw.msg_emoji_objects, R.raw.msg_emoji_other, R.raw.msg_emoji_flags};

    protected boolean allowEmojisForNonPremium() {
        return false;
    }

    protected boolean doIncludeFeatured() {
        return true;
    }

    protected boolean onTabClick(int i) {
        return true;
    }

    protected void onTabCreate(EmojiTabButton emojiTabButton) {
    }

    public EmojiTabsStrip(Context context, Theme.ResourcesProvider resourcesProvider, boolean z, boolean z2, boolean z3, int i, Runnable runnable) {
        this(context, resourcesProvider, z, z2, z3, i, runnable, Theme.getColor(Theme.key_windowBackgroundWhiteBlueIcon, resourcesProvider));
    }

    public EmojiTabsStrip(Context context, Theme.ResourcesProvider resourcesProvider, boolean z, boolean z2, final boolean z3, int i, Runnable runnable, int i2) {
        super(context);
        this.recentDrawableId = R.drawable.msg_emoji_recent;
        this.settingsDrawableId = R.drawable.smiles_tab_settings;
        this.forceTabsShow = !UserConfig.getInstance(UserConfig.selectedAccount).isPremium();
        this.showSelected = true;
        this.removingViews = new HashMap<>();
        this.selectT = 0.0f;
        this.selectAnimationT = 0.0f;
        this.selected = 0;
        this.wasIndex = 0;
        this.animateAppear = true;
        this.animatedEmojiCacheType = 6;
        this.updateButtonDrawables = true;
        this.recentFirstChange = true;
        this.recentIsShown = true;
        this.first = true;
        this.paddingLeftDp = 11.0f;
        this.includeAnimated = z3;
        this.resourcesProvider = resourcesProvider;
        this.onSettingsOpenRunnable = runnable;
        this.currentType = i;
        this.accentColor = i2;
        LinearLayout linearLayout = new LinearLayout(context) {
            private final LongSparseArray<Integer> lastX = new LongSparseArray<>();
            private Paint paint = new Paint(1);
            private RectF from = new RectF();
            private RectF to = new RectF();
            private RectF rect = new RectF();
            private Path path = new Path();

            @Override
            protected void onLayout(boolean z4, int i3, int i4, int i5, int i6) {
                int i7 = (i6 - i4) / 2;
                if (z3) {
                    int paddingLeft = getPaddingLeft() - (!EmojiTabsStrip.this.recentIsShown ? AndroidUtilities.dp(33.0f) : 0);
                    for (int i8 = 0; i8 < getChildCount(); i8++) {
                        View childAt = getChildAt(i8);
                        if (childAt != EmojiTabsStrip.this.settingsTab && !EmojiTabsStrip.this.removingViews.containsKey(childAt) && childAt != null) {
                            childAt.layout(paddingLeft, i7 - (childAt.getMeasuredHeight() / 2), childAt.getMeasuredWidth() + paddingLeft, (childAt.getMeasuredHeight() / 2) + i7);
                            boolean z5 = childAt instanceof EmojiTabButton;
                            Long id = z5 ? ((EmojiTabButton) childAt).id() : childAt instanceof EmojiTabsView ? Long.valueOf(((EmojiTabsView) childAt).id) : null;
                            if (EmojiTabsStrip.this.animateAppear && z5) {
                                EmojiTabButton emojiTabButton = (EmojiTabButton) childAt;
                                if (emojiTabButton.newly) {
                                    emojiTabButton.newly = false;
                                    childAt.setScaleX(0.0f);
                                    childAt.setScaleY(0.0f);
                                    childAt.setAlpha(0.0f);
                                    childAt.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(HwEmojis.isHwEnabledOrPreparing() ? 0L : 200L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
                                }
                            }
                            if (id != null) {
                                Integer num = this.lastX.get(id.longValue());
                                if (num != null && num.intValue() != paddingLeft && Math.abs(num.intValue() - paddingLeft) < AndroidUtilities.dp(45.0f)) {
                                    childAt.setTranslationX(num.intValue() - paddingLeft);
                                    childAt.animate().translationX(0.0f).setDuration(250L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
                                }
                                this.lastX.put(id.longValue(), Integer.valueOf(paddingLeft));
                            }
                            paddingLeft += childAt.getMeasuredWidth() + AndroidUtilities.dp(3.0f);
                        }
                    }
                    if (EmojiTabsStrip.this.settingsTab != null) {
                        int dp = paddingLeft + (EmojiTabsStrip.this.recentIsShown ? 0 : AndroidUtilities.dp(33.0f));
                        Long l = EmojiTabsStrip.this.settingsTab.id;
                        if (EmojiTabsStrip.this.settingsTab.getMeasuredWidth() + dp + getPaddingRight() <= EmojiTabsStrip.this.getMeasuredWidth()) {
                            EmojiTabButton emojiTabButton2 = EmojiTabsStrip.this.settingsTab;
                            int i9 = i5 - i3;
                            int paddingRight = (i9 - getPaddingRight()) - EmojiTabsStrip.this.settingsTab.getMeasuredWidth();
                            emojiTabButton2.layout(paddingRight, i7 - (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2), i9 - getPaddingRight(), i7 + (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2));
                            dp = paddingRight;
                        } else {
                            EmojiTabsStrip.this.settingsTab.layout(dp, i7 - (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2), EmojiTabsStrip.this.settingsTab.getMeasuredWidth() + dp, i7 + (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2));
                        }
                        if (l != null) {
                            if (this.lastX.get(l.longValue()) != null && this.lastX.get(l.longValue()).intValue() != dp) {
                                EmojiTabsStrip.this.settingsTab.setTranslationX(this.lastX.get(l.longValue()).intValue() - dp);
                                EmojiTabsStrip.this.settingsTab.animate().translationX(0.0f).setDuration(350L).start();
                            }
                            this.lastX.put(l.longValue(), Integer.valueOf(dp));
                            return;
                        }
                        return;
                    }
                    return;
                }
                int childCount = getChildCount() - (!EmojiTabsStrip.this.recentIsShown ? 1 : 0);
                int paddingLeft2 = (int) (((((i5 - i3) - getPaddingLeft()) - getPaddingRight()) - (AndroidUtilities.dp(30.0f) * childCount)) / Math.max(1, childCount - 1));
                int paddingLeft3 = getPaddingLeft();
                while (r11 < childCount) {
                    View childAt2 = getChildAt((!EmojiTabsStrip.this.recentIsShown ? 1 : 0) + r11);
                    if (childAt2 != null) {
                        childAt2.layout(paddingLeft3, i7 - (childAt2.getMeasuredHeight() / 2), childAt2.getMeasuredWidth() + paddingLeft3, (childAt2.getMeasuredHeight() / 2) + i7);
                        paddingLeft3 += childAt2.getMeasuredWidth() + paddingLeft2;
                    }
                    r11++;
                }
            }

            @Override
            protected void onMeasure(int i3, int i4) {
                int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(99999999, Integer.MIN_VALUE);
                int paddingLeft = (getPaddingLeft() + getPaddingRight()) - ((int) (EmojiTabsStrip.this.recentIsShown ? 0.0f : EmojiTabsStrip.this.recentTab.getAlpha() * AndroidUtilities.dp(33.0f)));
                for (int i5 = 0; i5 < getChildCount(); i5++) {
                    View childAt = getChildAt(i5);
                    if (childAt != null) {
                        childAt.measure(makeMeasureSpec, i4);
                        paddingLeft += childAt.getMeasuredWidth() + (i5 + 1 < getChildCount() ? AndroidUtilities.dp(3.0f) : 0);
                    }
                }
                if (!z3) {
                    setMeasuredDimension(View.MeasureSpec.getSize(i3), View.MeasureSpec.getSize(i4));
                } else {
                    setMeasuredDimension(Math.max(paddingLeft, View.MeasureSpec.getSize(i3)), View.MeasureSpec.getSize(i4));
                }
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                Paint paint;
                Paint paint2;
                Paint paint3;
                for (Map.Entry entry : EmojiTabsStrip.this.removingViews.entrySet()) {
                    View view = (View) entry.getKey();
                    if (view != null) {
                        android.graphics.Rect rect = (android.graphics.Rect) entry.getValue();
                        canvas.save();
                        canvas.translate(rect.left, rect.top);
                        canvas.scale(view.getScaleX(), view.getScaleY(), rect.width() / 2.0f, rect.height() / 2.0f);
                        view.draw(canvas);
                        canvas.restore();
                    }
                }
                if (EmojiTabsStrip.this.showSelectedAlpha == null) {
                    EmojiTabsStrip.this.showSelectedAlpha = new AnimatedFloat(this, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
                }
                float f = EmojiTabsStrip.this.showSelectedAlpha.set(EmojiTabsStrip.this.showSelected ? 1.0f : 0.0f);
                int floor = (int) Math.floor(EmojiTabsStrip.this.selectT);
                getChildBounds(floor, this.from);
                getChildBounds((int) Math.ceil(EmojiTabsStrip.this.selectT), this.to);
                AndroidUtilities.lerp(this.from, this.to, EmojiTabsStrip.this.selectT - floor, this.rect);
                float clamp = EmojiTabsStrip.this.emojiTabs != null ? MathUtils.clamp(1.0f - Math.abs(EmojiTabsStrip.this.selectT - 1.0f), 0.0f, 1.0f) : 0.0f;
                float f2 = EmojiTabsStrip.this.selectAnimationT * 4.0f * (1.0f - EmojiTabsStrip.this.selectAnimationT);
                float width = (this.rect.width() / 2.0f) * ((0.3f * f2) + 1.0f);
                float height = (this.rect.height() / 2.0f) * (1.0f - (f2 * 0.05f));
                RectF rectF = this.rect;
                rectF.set(rectF.centerX() - width, this.rect.centerY() - height, this.rect.centerX() + width, this.rect.centerY() + height);
                float dp = AndroidUtilities.dp(AndroidUtilities.lerp(8.0f, 16.0f, clamp));
                this.paint.setColor(EmojiTabsStrip.this.selectorColor());
                if (EmojiTabsStrip.this.forceTabsShow) {
                    this.paint.setAlpha((int) (paint3.getAlpha() * f * (1.0f - (clamp * 0.5f))));
                } else {
                    this.paint.setAlpha((int) (paint.getAlpha() * f));
                }
                this.path.rewind();
                this.path.addRoundRect(this.rect, dp, dp, Path.Direction.CW);
                canvas.drawPath(this.path, this.paint);
                if (EmojiTabsStrip.this.forceTabsShow) {
                    this.path.rewind();
                    getChildBounds(1, this.rect);
                    this.path.addRoundRect(this.rect, AndroidUtilities.dpf2(16.0f), AndroidUtilities.dpf2(16.0f), Path.Direction.CW);
                    this.paint.setColor(EmojiTabsStrip.this.selectorColor());
                    this.paint.setAlpha((int) (paint2.getAlpha() * 0.5f));
                    canvas.drawPath(this.path, this.paint);
                }
                if (EmojiTabsStrip.this.emojiTabs != null) {
                    this.path.addCircle(EmojiTabsStrip.this.emojiTabs.getLeft() + AndroidUtilities.dp(15.0f), (EmojiTabsStrip.this.emojiTabs.getTop() + EmojiTabsStrip.this.emojiTabs.getBottom()) / 2.0f, AndroidUtilities.dp(15.0f), Path.Direction.CW);
                }
                super.dispatchDraw(canvas);
                EmojiTabsStrip.this.wasDrawn = true;
            }

            @Override
            protected boolean drawChild(Canvas canvas, View view, long j) {
                if (view == EmojiTabsStrip.this.emojiTabs) {
                    canvas.save();
                    canvas.clipPath(this.path);
                    boolean drawChild = super.drawChild(canvas, view, j);
                    canvas.restore();
                    return drawChild;
                }
                return super.drawChild(canvas, view, j);
            }

            private void getChildBounds(int i3, RectF rectF) {
                View childAt = getChildAt(MathUtils.clamp(i3, 0, getChildCount() - 1));
                if (childAt == null) {
                    return;
                }
                rectF.set(childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom());
                rectF.set(rectF.centerX() - ((rectF.width() / 2.0f) * childAt.getScaleX()), rectF.centerY() - ((rectF.height() / 2.0f) * childAt.getScaleY()), rectF.centerX() + ((rectF.width() / 2.0f) * childAt.getScaleX()), rectF.centerY() + ((rectF.height() / 2.0f) * childAt.getScaleY()));
            }
        };
        this.contentView = linearLayout;
        linearLayout.setClipToPadding(false);
        this.contentView.setOrientation(0);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        addView(this.contentView);
        if (i == 4) {
            LinearLayout linearLayout2 = this.contentView;
            EmojiTabButton emojiTabButton = new EmojiTabButton(context, R.drawable.msg_emoji_stickers, false, false);
            this.toggleEmojiStickersTab = emojiTabButton;
            linearLayout2.addView(emojiTabButton);
        }
        if (i == 3) {
            this.recentDrawableId = R.drawable.msg_emoji_smiles;
        }
        if (i == 6) {
            this.recentDrawableId = R.drawable.emoji_love;
        }
        if (z) {
            LinearLayout linearLayout3 = this.contentView;
            EmojiTabButton emojiTabButton2 = new EmojiTabButton(context, this.recentDrawableId, false, false);
            this.recentTab = emojiTabButton2;
            linearLayout3.addView(emojiTabButton2);
            this.recentTab.id = Long.valueOf(-934918565);
        }
        if (z3) {
            if (z2) {
                LinearLayout linearLayout4 = this.contentView;
                EmojiTabsView emojiTabsView = new EmojiTabsView(context);
                this.emojiTabs = emojiTabsView;
                linearLayout4.addView(emojiTabsView);
                this.emojiTabs.id = 3552126;
            }
            this.packsIndexStart = this.contentView.getChildCount();
            if (runnable != null) {
                LinearLayout linearLayout5 = this.contentView;
                EmojiTabButton emojiTabButton3 = new EmojiTabButton(context, this.settingsDrawableId, false, true);
                this.settingsTab = emojiTabButton3;
                linearLayout5.addView(emojiTabButton3);
                this.settingsTab.id = Long.valueOf(1434631203);
                this.settingsTab.setAlpha(0.0f);
            }
            updateClickListeners();
            return;
        }
        int i3 = 0;
        while (true) {
            int[] iArr = emojiTabsDrawableIds;
            if (i3 < iArr.length) {
                this.contentView.addView(new EmojiTabButton(context, iArr[i3], false, i3 == 0));
                i3++;
            } else {
                updateClickListeners();
                return;
            }
        }
    }

    public void showRecentTabStub(boolean z) {
        EmojiTabButton emojiTabButton = this.recentTab;
        if (emojiTabButton == null) {
            return;
        }
        if (z) {
            emojiTabButton.setBackground(new StabDrawable(selectorColor()));
        } else {
            emojiTabButton.setBackground(null);
        }
    }

    public void showSelected(boolean z) {
        this.showSelected = z;
        this.contentView.invalidate();
    }

    public void showRecent(boolean z) {
        if (this.recentIsShown == z) {
            return;
        }
        this.recentIsShown = z;
        if (this.recentFirstChange) {
            this.recentTab.setAlpha(z ? 1.0f : 0.0f);
        } else {
            this.recentTab.animate().alpha(z ? 1.0f : 0.0f).setDuration(200L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
        }
        if ((!z && this.selected == 0) || (z && this.selected == 1)) {
            select(0, !this.recentFirstChange);
        }
        this.contentView.requestLayout();
        this.recentFirstChange = false;
    }

    private TLRPC$Document getThumbDocument(TLRPC$StickerSet tLRPC$StickerSet, ArrayList<TLRPC$Document> arrayList) {
        if (tLRPC$StickerSet == null) {
            return null;
        }
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$Document tLRPC$Document = arrayList.get(i);
                if (tLRPC$Document.id == tLRPC$StickerSet.thumb_document_id) {
                    return tLRPC$Document;
                }
            }
        }
        if (arrayList == null || arrayList.size() < 1) {
            return null;
        }
        return arrayList.get(0);
    }

    private static class StabDrawable extends Drawable {
        private final Paint paint;
        private final RectF rectF;

        @Override
        public int getOpacity() {
            return -3;
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public StabDrawable(int i) {
            Paint paint = new Paint();
            this.paint = paint;
            this.rectF = new RectF();
            paint.setAlpha(45);
            paint.setColor(i);
        }

        @Override
        public void draw(Canvas canvas) {
            this.rectF.set(0.0f, 0.0f, AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
            canvas.drawRoundRect(this.rectF, AndroidUtilities.dpf2(8.0f), AndroidUtilities.dpf2(8.0f), this.paint);
        }

        @Override
        public void setAlpha(int i) {
            this.paint.setAlpha(i);
        }
    }

    protected boolean isInstalled(EmojiView.EmojiPack emojiPack) {
        return emojiPack.installed;
    }

    public void updateEmojiPacks(ArrayList<EmojiView.EmojiPack> arrayList) {
        EmojiView.EmojiPack emojiPack;
        int i;
        Boolean bool;
        if (this.includeAnimated) {
            if (!this.first || MediaDataController.getInstance(UserConfig.selectedAccount).areStickersLoaded(5)) {
                this.first = false;
                if (arrayList == null) {
                    return;
                }
                int childCount = (this.contentView.getChildCount() - this.packsIndexStart) - (this.settingsTab != null ? 1 : 0);
                if (childCount == 0 && arrayList.size() > 0 && this.appearCount != arrayList.size()) {
                    boolean z = this.wasDrawn;
                }
                Boolean bool2 = null;
                if (this.appearAnimation != null && this.appearCount != arrayList.size()) {
                    this.appearAnimation.cancel();
                    this.appearAnimation = null;
                }
                this.appearCount = arrayList.size();
                doIncludeFeatured();
                boolean z2 = UserConfig.getInstance(UserConfig.selectedAccount).isPremium() || allowEmojisForNonPremium();
                ArrayList arrayList2 = new ArrayList();
                int i2 = 0;
                while (i2 < Math.max(arrayList.size(), childCount)) {
                    EmojiTabButton emojiTabButton = i2 < childCount ? (EmojiTabButton) this.contentView.getChildAt(this.packsIndexStart + i2) : bool2;
                    EmojiView.EmojiPack emojiPack2 = i2 < arrayList.size() ? arrayList.get(i2) : bool2;
                    if (emojiPack2 == null) {
                        if (emojiTabButton != null) {
                            this.contentView.removeView(emojiTabButton);
                        }
                    } else if (emojiPack2.resId == 0) {
                        boolean z3 = emojiPack2.free;
                        TLRPC$Document thumbDocument = getThumbDocument(emojiPack2.set, emojiPack2.documents);
                        if (emojiTabButton == null) {
                            emojiPack = emojiPack2;
                            i = i2;
                            EmojiTabButton emojiTabButton2 = new EmojiTabButton(getContext(), thumbDocument, z3, false, false);
                            onTabCreate(emojiTabButton2);
                            this.contentView.addView(emojiTabButton2, this.packsIndexStart + i);
                            emojiTabButton = emojiTabButton2;
                        } else {
                            emojiPack = emojiPack2;
                            i = i2;
                            emojiTabButton.setAnimatedEmojiDocument(thumbDocument);
                        }
                        emojiTabButton.updateSelect(this.selected == i, false);
                        int i3 = this.currentType;
                        if (i3 == 4 || i3 == 6) {
                            bool = null;
                        } else if (i3 == 5 || i3 == 7) {
                            bool = null;
                        } else {
                            if (!z2 && !z3) {
                                emojiTabButton.setLock(Boolean.TRUE, false);
                            } else if (!isInstalled(emojiPack)) {
                                emojiTabButton.setLock(Boolean.FALSE, false);
                            } else {
                                bool = null;
                                emojiTabButton.setLock(null, false);
                                i2 = i + 1;
                                bool2 = bool;
                            }
                            bool = null;
                            i2 = i + 1;
                            bool2 = bool;
                        }
                        emojiTabButton.setLock(bool, false);
                        i2 = i + 1;
                        bool2 = bool;
                    } else if (emojiTabButton == null) {
                        EmojiTabButton emojiTabButton3 = new EmojiTabButton(getContext(), emojiPack2.resId, false, false);
                        onTabCreate(emojiTabButton3);
                        this.contentView.addView(emojiTabButton3, this.packsIndexStart + i2);
                    } else {
                        emojiTabButton.setDrawable(getResources().getDrawable(emojiPack2.resId).mutate());
                        emojiTabButton.updateColor();
                        emojiTabButton.setLock(bool2, false);
                    }
                    i = i2;
                    bool = bool2;
                    i2 = i + 1;
                    bool2 = bool;
                }
                EmojiTabButton emojiTabButton4 = this.settingsTab;
                if (emojiTabButton4 != null) {
                    emojiTabButton4.bringToFront();
                    if (this.settingsTab.getAlpha() < 1.0f) {
                        this.settingsTab.animate().alpha(1.0f).setDuration(HwEmojis.isHwEnabledOrPreparing() ? 0L : 200L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    }
                }
                for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                    ((EmojiTabButton) arrayList2.get(i4)).keepAttached = false;
                    ((EmojiTabButton) arrayList2.get(i4)).updateAttachState();
                }
                updateClickListeners();
            }
        }
    }

    public void updateClickListeners() {
        int i = 0;
        final int i2 = 0;
        while (i < this.contentView.getChildCount()) {
            View childAt = this.contentView.getChildAt(i);
            if (childAt instanceof EmojiTabsView) {
                EmojiTabsView emojiTabsView = (EmojiTabsView) childAt;
                int i3 = 0;
                while (i3 < emojiTabsView.contentView.getChildCount()) {
                    emojiTabsView.contentView.getChildAt(i3).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            EmojiTabsStrip.this.lambda$updateClickListeners$0(i2, view);
                        }
                    });
                    i3++;
                    i2++;
                }
                i2--;
            } else if (childAt != null) {
                childAt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        EmojiTabsStrip.this.lambda$updateClickListeners$1(i2, view);
                    }
                });
            }
            i++;
            i2++;
        }
        EmojiTabButton emojiTabButton = this.settingsTab;
        if (emojiTabButton != null) {
            emojiTabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    EmojiTabsStrip.this.lambda$updateClickListeners$2(view);
                }
            });
        }
    }

    public void lambda$updateClickListeners$0(int i, View view) {
        onTabClick(i);
    }

    public void lambda$updateClickListeners$1(int i, View view) {
        onTabClick(i);
    }

    public void lambda$updateClickListeners$2(View view) {
        Runnable runnable = this.onSettingsOpenRunnable;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void setPaddingLeft(float f) {
        this.paddingLeftDp = f;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        this.contentView.setPadding(AndroidUtilities.dp(this.paddingLeftDp), 0, AndroidUtilities.dp(11.0f), 0);
        super.onMeasure(i, i2);
    }

    public void updateColors() {
        EmojiTabButton emojiTabButton = this.recentTab;
        if (emojiTabButton != null) {
            emojiTabButton.updateColor();
        }
    }

    public void select(int i) {
        select(i, true);
    }

    public void select(int i, boolean z) {
        int i2;
        int i3;
        boolean z2 = z && !this.first;
        EmojiTabButton emojiTabButton = this.toggleEmojiStickersTab;
        if (emojiTabButton != null) {
            i++;
        }
        if (!this.recentIsShown || emojiTabButton != null) {
            i = Math.max(1, i);
        }
        int i4 = this.selected;
        int i5 = 0;
        int i6 = 0;
        while (i5 < this.contentView.getChildCount()) {
            View childAt = this.contentView.getChildAt(i5);
            if (childAt instanceof EmojiTabsView) {
                EmojiTabsView emojiTabsView = (EmojiTabsView) childAt;
                int i7 = i6;
                int i8 = 0;
                while (i8 < emojiTabsView.contentView.getChildCount()) {
                    View childAt2 = emojiTabsView.contentView.getChildAt(i8);
                    if (childAt2 instanceof EmojiTabButton) {
                        ((EmojiTabButton) childAt2).updateSelect(i == i7, z2);
                    }
                    i8++;
                    i7++;
                }
                i3 = i7 - 1;
            } else {
                if (childAt instanceof EmojiTabButton) {
                    ((EmojiTabButton) childAt).updateSelect(i == i6, z2);
                }
                i3 = i6;
            }
            if (i >= i6 && i <= i3) {
                this.selected = i5;
            }
            i5++;
            i6 = i3 + 1;
        }
        if (i4 != this.selected) {
            ValueAnimator valueAnimator = this.selectAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            final float f = this.selectT;
            final float f2 = this.selected;
            if (z2) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.selectAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        EmojiTabsStrip.this.lambda$select$3(f, f2, valueAnimator2);
                    }
                });
                this.selectAnimator.setDuration(350L);
                this.selectAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.selectAnimator.start();
            } else {
                this.selectAnimationT = 1.0f;
                this.selectT = AndroidUtilities.lerp(f, f2, 1.0f);
                this.contentView.invalidate();
            }
            EmojiTabsView emojiTabsView2 = this.emojiTabs;
            if (emojiTabsView2 != null) {
                emojiTabsView2.show(this.selected == 1 || this.forceTabsShow, z2);
            }
            View childAt3 = this.contentView.getChildAt(this.selected);
            if (this.selected >= 2) {
                scrollToVisible(childAt3.getLeft(), childAt3.getRight());
            } else {
                scrollTo(0);
            }
        }
        if (this.wasIndex != i) {
            EmojiTabsView emojiTabsView3 = this.emojiTabs;
            if (emojiTabsView3 != null && this.selected == 1 && i >= 1 && i <= emojiTabsView3.contentView.getChildCount() + 1) {
                this.emojiTabs.scrollToVisible(AndroidUtilities.dp(((i - 1) * 36) - 6), AndroidUtilities.dp(i2 + 30));
            }
            this.wasIndex = i;
        }
    }

    public void lambda$select$3(float f, float f2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.selectAnimationT = floatValue;
        this.selectT = AndroidUtilities.lerp(f, f2, floatValue);
        this.contentView.invalidate();
    }

    protected ColorFilter getEmojiColorFilter() {
        return Theme.getAnimatedEmojiColorFilter(this.resourcesProvider);
    }

    public int selectorColor() {
        int i = this.currentType;
        if (i == 5 || i == 7) {
            return Theme.multAlpha(this.accentColor, 0.09f);
        }
        return Theme.multAlpha(Theme.getColor(Theme.key_chat_emojiPanelIcon, this.resourcesProvider), 0.18f);
    }

    public void setAnimatedEmojiCacheType(int i) {
        this.animatedEmojiCacheType = i;
    }

    public class EmojiTabButton extends ViewGroup {
        AnimatedEmojiDrawable animatedEmoji;
        TLRPC$Document animatedEmojiDocument;
        boolean attached;
        private boolean forceSelector;
        public Long id;
        private ImageView imageView;
        private boolean isAnimatedEmoji;
        private boolean isVisible;
        public boolean keepAttached;
        private ValueAnimator lockAnimator;
        private float lockT;
        private PremiumLockIconView lockView;
        private RLottieDrawable lottieDrawable;
        public boolean newly;
        private boolean round;
        private ValueAnimator selectAnimator;
        private float selectT;
        private boolean selected;

        public Long id() {
            Long l = this.id;
            if (l != null) {
                return l;
            }
            TLRPC$Document tLRPC$Document = this.animatedEmojiDocument;
            if (tLRPC$Document != null) {
                return Long.valueOf(tLRPC$Document.id);
            }
            return null;
        }

        public EmojiTabButton(Context context, int i, int i2, boolean z, boolean z2) {
            super(context);
            this.round = z;
            this.forceSelector = z2;
            if (z) {
                setBackground(Theme.createCircleSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 0, 0));
            } else if (z2) {
                setBackground(Theme.createRadSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 8, 8));
            }
            if (Build.VERSION.SDK_INT >= 23) {
                RLottieDrawable rLottieDrawable = new RLottieDrawable(i2, BuildConfig.APP_CENTER_HASH + i2, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), false, null);
                this.lottieDrawable = rLottieDrawable;
                rLottieDrawable.setBounds(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(27.0f), AndroidUtilities.dp(27.0f));
                this.lottieDrawable.setMasterParent(this);
                this.lottieDrawable.setAllowDecodeSingleFrame(true);
                this.lottieDrawable.start();
            } else {
                ImageView imageView = new ImageView(context);
                this.imageView = imageView;
                imageView.setImageDrawable(context.getResources().getDrawable(i).mutate());
                addView(this.imageView);
            }
            setColor(Theme.getColor(Theme.key_chat_emojiPanelIcon, EmojiTabsStrip.this.resourcesProvider));
        }

        public EmojiTabButton(Context context, int i, boolean z, boolean z2) {
            super(context);
            this.round = z;
            this.forceSelector = z2;
            if (z) {
                setBackground(Theme.createCircleSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 0, 0));
            } else if (z2) {
                setBackground(Theme.createRadSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 8, 8));
            }
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setImageDrawable(context.getResources().getDrawable(i).mutate());
            setColor(Theme.getColor(Theme.key_chat_emojiPanelIcon, EmojiTabsStrip.this.resourcesProvider));
            addView(this.imageView);
        }

        public EmojiTabButton(Context context, TLRPC$Document tLRPC$Document, boolean z, boolean z2, boolean z3) {
            super(context);
            this.newly = true;
            this.round = z2;
            this.forceSelector = z3;
            if (z2) {
                setBackground(Theme.createCircleSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 0, 0));
            } else if (z3) {
                setBackground(Theme.createRadSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 8, 8));
            }
            ImageView imageView = new ImageView(context, EmojiTabsStrip.this) {
                @Override
                protected void onDraw(Canvas canvas) {
                }

                @Override
                public void invalidate() {
                    if (HwEmojis.grab(this)) {
                        return;
                    }
                    super.invalidate();
                    EmojiTabButton.this.updateLockImageReceiver();
                }

                @Override
                public void invalidate(int i, int i2, int i3, int i4) {
                    if (HwEmojis.grab(this)) {
                        return;
                    }
                    super.invalidate(i, i2, i3, i4);
                }

                @Override
                protected void dispatchDraw(Canvas canvas) {
                    Drawable drawable = getDrawable();
                    if (drawable != null) {
                        drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                        drawable.setAlpha(255);
                        drawable.draw(canvas);
                    }
                }

                @Override
                public void setImageDrawable(Drawable drawable) {
                    super.setImageDrawable(drawable);
                }
            };
            this.imageView = imageView;
            this.animatedEmojiDocument = tLRPC$Document;
            this.isAnimatedEmoji = true;
            imageView.setColorFilter(EmojiTabsStrip.this.getEmojiColorFilter());
            addView(this.imageView);
            PremiumLockIconView premiumLockIconView = new PremiumLockIconView(this, context, PremiumLockIconView.TYPE_STICKERS_PREMIUM_LOCKED, EmojiTabsStrip.this.resourcesProvider, EmojiTabsStrip.this) {
                @Override
                public void invalidate() {
                    if (HwEmojis.grab(this)) {
                        return;
                    }
                    super.invalidate();
                }

                @Override
                public void invalidate(int i, int i2, int i3, int i4) {
                    if (HwEmojis.grab(this)) {
                        return;
                    }
                    super.invalidate(i, i2, i3, i4);
                }
            };
            this.lockView = premiumLockIconView;
            premiumLockIconView.setAlpha(0.0f);
            this.lockView.setScaleX(0.0f);
            this.lockView.setScaleY(0.0f);
            updateLockImageReceiver();
            addView(this.lockView);
            setColor(Theme.getColor(Theme.key_chat_emojiPanelIcon, EmojiTabsStrip.this.resourcesProvider));
        }

        @Override
        public void invalidate() {
            if (HwEmojis.grab(this)) {
                return;
            }
            super.invalidate();
        }

        @Override
        public void invalidate(int i, int i2, int i3, int i4) {
            if (HwEmojis.grab(this)) {
                return;
            }
            super.invalidate(i, i2, i3, i4);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            RLottieDrawable rLottieDrawable = this.lottieDrawable;
            if (rLottieDrawable == null || !this.isVisible) {
                return;
            }
            rLottieDrawable.draw(canvas);
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (this.isVisible) {
                return super.drawChild(canvas, view, j);
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.isVisible) {
                super.onDraw(canvas);
            }
        }

        @Override
        public boolean performClick() {
            playAnimation();
            return super.performClick();
        }

        public void setDrawable(Drawable drawable) {
            setAnimatedEmojiDocument(null);
            this.imageView.setImageDrawable(drawable);
        }

        public void setAnimatedEmojiDocument(TLRPC$Document tLRPC$Document) {
            TLRPC$Document tLRPC$Document2 = this.animatedEmojiDocument;
            if (tLRPC$Document2 == null || tLRPC$Document == null || tLRPC$Document2.id != tLRPC$Document.id) {
                AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmoji;
                if (animatedEmojiDrawable != null) {
                    animatedEmojiDrawable.removeView(this.imageView);
                    this.animatedEmoji = null;
                    this.imageView.setImageDrawable(null);
                }
                this.animatedEmojiDocument = tLRPC$Document;
                updateAttachState();
            }
        }

        private void playAnimation() {
            ImageReceiver imageReceiver;
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmoji;
            if (animatedEmojiDrawable == null || (imageReceiver = animatedEmojiDrawable.getImageReceiver()) == null) {
                return;
            }
            if (imageReceiver.getAnimation() != null) {
                imageReceiver.getAnimation().seekTo(0L, true);
            }
            imageReceiver.startAnimation();
        }

        private void stopAnimation() {
            ImageReceiver imageReceiver;
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmoji;
            if (animatedEmojiDrawable == null || (imageReceiver = animatedEmojiDrawable.getImageReceiver()) == null) {
                return;
            }
            if (imageReceiver.getLottieAnimation() != null) {
                imageReceiver.getLottieAnimation().setCurrentFrame(0);
                imageReceiver.getLottieAnimation().stop();
            } else if (imageReceiver.getAnimation() != null) {
                imageReceiver.getAnimation().stop();
            }
        }

        public void updateVisibilityInbounds(boolean z, boolean z2) {
            RLottieDrawable rLottieDrawable;
            if (!this.isVisible && z && (rLottieDrawable = this.lottieDrawable) != null && !rLottieDrawable.isRunning() && !z2) {
                this.lottieDrawable.setProgress(0.0f);
                this.lottieDrawable.start();
            }
            if (this.isVisible != z) {
                this.isVisible = z;
                if (z) {
                    invalidate();
                    PremiumLockIconView premiumLockIconView = this.lockView;
                    if (premiumLockIconView != null) {
                        premiumLockIconView.invalidate();
                    }
                    initLock();
                    ImageView imageView = this.imageView;
                    if (imageView != null) {
                        imageView.invalidate();
                    }
                } else {
                    stopAnimation();
                }
                updateAttachState();
            }
        }

        private void initLock() {
            AnimatedEmojiDrawable animatedEmojiDrawable;
            ImageReceiver imageReceiver;
            if (this.lockView == null || (animatedEmojiDrawable = this.animatedEmoji) == null || (imageReceiver = animatedEmojiDrawable.getImageReceiver()) == null) {
                return;
            }
            this.lockView.setImageReceiver(imageReceiver);
        }

        public void setLock(Boolean bool, boolean z) {
            if (this.lockView == null) {
                return;
            }
            if (bool == null) {
                updateLock(false, z);
                return;
            }
            updateLock(true, z);
            if (bool.booleanValue()) {
                this.lockView.setImageResource(R.drawable.msg_mini_lockedemoji);
                return;
            }
            Drawable mutate = getResources().getDrawable(R.drawable.msg_mini_addemoji).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            this.lockView.setImageDrawable(mutate);
        }

        private void updateLock(final boolean z, boolean z2) {
            ValueAnimator valueAnimator = this.lockAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (Math.abs(this.lockT - (z ? 1.0f : 0.0f)) < 0.01f) {
                return;
            }
            if (z2) {
                this.lockView.setVisibility(0);
                float[] fArr = new float[2];
                fArr[0] = this.lockT;
                fArr[1] = z ? 1.0f : 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.lockAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        EmojiTabsStrip.EmojiTabButton.this.lambda$updateLock$0(valueAnimator2);
                    }
                });
                this.lockAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (z) {
                            return;
                        }
                        EmojiTabButton.this.lockView.setVisibility(8);
                    }
                });
                this.lockAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.lockAnimator.setDuration(HwEmojis.isHwEnabledOrPreparing() ? 0L : 200L);
                this.lockAnimator.start();
                return;
            }
            float f = z ? 1.0f : 0.0f;
            this.lockT = f;
            this.lockView.setScaleX(f);
            this.lockView.setScaleY(this.lockT);
            this.lockView.setAlpha(this.lockT);
            this.lockView.setVisibility(z ? 0 : 8);
        }

        public void lambda$updateLock$0(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.lockT = floatValue;
            this.lockView.setScaleX(floatValue);
            this.lockView.setScaleY(this.lockT);
            this.lockView.setAlpha(this.lockT);
        }

        public void updateLockImageReceiver() {
            PremiumLockIconView premiumLockIconView = this.lockView;
            if (premiumLockIconView == null || premiumLockIconView.ready() || !(getDrawable() instanceof AnimatedEmojiDrawable)) {
                return;
            }
            if (((AnimatedEmojiDrawable) getDrawable()).canOverrideColor()) {
                this.lockView.setImageReceiver(null);
                this.lockView.setColor(EmojiTabsStrip.this.accentColor);
                return;
            }
            ImageReceiver imageReceiver = ((AnimatedEmojiDrawable) getDrawable()).getImageReceiver();
            if (imageReceiver != null) {
                this.lockView.setImageReceiver(imageReceiver);
                this.lockView.invalidate();
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
            ImageView imageView = this.imageView;
            if (imageView != null) {
                imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), 1073741824));
            }
            PremiumLockIconView premiumLockIconView = this.lockView;
            if (premiumLockIconView != null) {
                premiumLockIconView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), 1073741824));
            }
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            ImageView imageView = this.imageView;
            if (imageView != null) {
                int i5 = (i3 - i) / 2;
                int i6 = (i4 - i2) / 2;
                imageView.layout(i5 - (imageView.getMeasuredWidth() / 2), i6 - (this.imageView.getMeasuredHeight() / 2), i5 + (this.imageView.getMeasuredWidth() / 2), i6 + (this.imageView.getMeasuredHeight() / 2));
            }
            PremiumLockIconView premiumLockIconView = this.lockView;
            if (premiumLockIconView != null) {
                int i7 = i3 - i;
                int i8 = i4 - i2;
                premiumLockIconView.layout(i7 - premiumLockIconView.getMeasuredWidth(), i8 - this.lockView.getMeasuredHeight(), i7, i8);
            }
        }

        public Drawable getDrawable() {
            ImageView imageView = this.imageView;
            if (imageView != null) {
                return imageView.getDrawable();
            }
            return null;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
            updateAttachState();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
            updateAttachState();
        }

        public void updateAttachState() {
            ImageView imageView = this.imageView;
            if (imageView == null) {
                return;
            }
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmoji;
            if (animatedEmojiDrawable != null && this.animatedEmojiDocument == null) {
                animatedEmojiDrawable.removeView(imageView);
                this.animatedEmoji = null;
                this.imageView.setImageDrawable(null);
            } else if (this.attached && this.isVisible) {
                if (animatedEmojiDrawable == null && this.animatedEmojiDocument != null) {
                    AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, EmojiTabsStrip.this.animatedEmojiCacheType, this.animatedEmojiDocument);
                    this.animatedEmoji = make;
                    make.addView(this.imageView);
                    this.imageView.setImageDrawable(this.animatedEmoji);
                }
            } else if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.removeView(imageView);
                this.animatedEmoji = null;
                this.imageView.setImageDrawable(null);
            }
            updateLockImageReceiver();
        }

        public void updateSelect(final boolean z, boolean z2) {
            ImageView imageView = this.imageView;
            if ((imageView == null || imageView.getDrawable() != null) && this.selected != z) {
                this.selected = z;
                ValueAnimator valueAnimator = this.selectAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.selectAnimator = null;
                }
                if (!z) {
                    stopAnimation();
                }
                if (z2) {
                    float[] fArr = new float[2];
                    fArr[0] = this.selectT;
                    fArr[1] = z ? 1.0f : 0.0f;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                    this.selectAnimator = ofFloat;
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            EmojiTabsStrip.EmojiTabButton.this.lambda$updateSelect$1(valueAnimator2);
                        }
                    });
                    this.selectAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            EmojiTabButton emojiTabButton = EmojiTabButton.this;
                            if (!EmojiTabsStrip.this.updateButtonDrawables || emojiTabButton.round) {
                                return;
                            }
                            if (z || EmojiTabButton.this.forceSelector) {
                                if (EmojiTabButton.this.getBackground() == null) {
                                    EmojiTabButton emojiTabButton2 = EmojiTabButton.this;
                                    emojiTabButton2.setBackground(Theme.createRadSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 8, 8));
                                    return;
                                }
                                return;
                            }
                            EmojiTabButton.this.setBackground(null);
                        }
                    });
                    this.selectAnimator.setDuration(HwEmojis.isHwEnabledOrPreparing() ? 0L : 350L);
                    this.selectAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.selectAnimator.start();
                    return;
                }
                this.selectT = z ? 1.0f : 0.0f;
                updateColor();
            }
        }

        public void lambda$updateSelect$1(ValueAnimator valueAnimator) {
            this.selectT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_chat_emojiPanelIcon, EmojiTabsStrip.this.resourcesProvider), Theme.getColor(Theme.key_chat_emojiPanelIconSelected, EmojiTabsStrip.this.resourcesProvider), this.selectT));
        }

        public void updateColor() {
            Theme.setSelectorDrawableColor(getBackground(), EmojiTabsStrip.this.selectorColor(), false);
            setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_chat_emojiPanelIcon, EmojiTabsStrip.this.resourcesProvider), Theme.getColor(Theme.key_chat_emojiPanelIconSelected, EmojiTabsStrip.this.resourcesProvider), this.selectT));
        }

        private void setColor(int i) {
            if (EmojiTabsStrip.this.currentType == 5 || EmojiTabsStrip.this.currentType == 7) {
                i = EmojiTabsStrip.this.accentColor;
            }
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY);
            ImageView imageView = this.imageView;
            if (imageView != null && !this.isAnimatedEmoji) {
                imageView.setColorFilter(porterDuffColorFilter);
                this.imageView.invalidate();
            }
            RLottieDrawable rLottieDrawable = this.lottieDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setColorFilter(porterDuffColorFilter);
                invalidate();
            }
        }
    }

    public class EmojiTabsView extends ScrollableHorizontalScrollView {
        public long id;
        private float showT;
        private boolean shown;

        public EmojiTabsView(Context context) {
            super(context);
            this.shown = EmojiTabsStrip.this.forceTabsShow;
            this.showT = EmojiTabsStrip.this.forceTabsShow ? 1.0f : 0.0f;
            setSmoothScrollingEnabled(true);
            setHorizontalScrollBarEnabled(false);
            setVerticalScrollBarEnabled(false);
            if (Build.VERSION.SDK_INT >= 21) {
                setNestedScrollingEnabled(true);
            }
            LinearLayout linearLayout = new LinearLayout(context, EmojiTabsStrip.this) {
                @Override
                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int paddingLeft = getPaddingLeft();
                    int i5 = (i4 - i2) / 2;
                    for (int i6 = 0; i6 < getChildCount(); i6++) {
                        View childAt = getChildAt(i6);
                        if (childAt != EmojiTabsStrip.this.settingsTab && childAt != null) {
                            childAt.layout(paddingLeft, i5 - (childAt.getMeasuredHeight() / 2), childAt.getMeasuredWidth() + paddingLeft, (childAt.getMeasuredHeight() / 2) + i5);
                            paddingLeft += childAt.getMeasuredWidth() + AndroidUtilities.dp(2.0f);
                        }
                    }
                }

                @Override
                protected void onMeasure(int i, int i2) {
                    super.onMeasure(Math.max(View.MeasureSpec.getSize(i), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(EmojiTabsView.this.contentView.getChildCount() * 32), 1073741824)), i2);
                }
            };
            this.contentView = linearLayout;
            linearLayout.setOrientation(0);
            addView(this.contentView, new FrameLayout.LayoutParams(-2, -1));
            for (int i = 0; i < EmojiTabsStrip.emojiTabsDrawableIds.length; i++) {
                this.contentView.addView(new EmojiTabButton(context, EmojiTabsStrip.emojiTabsDrawableIds[i], EmojiTabsStrip.emojiTabsAnimatedDrawableIds[i], true, false, EmojiTabsStrip.this) {
                    {
                        EmojiTabsStrip emojiTabsStrip = EmojiTabsStrip.this;
                    }

                    @Override
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        EmojiTabsView.this.intercept(motionEvent);
                        return super.onTouchEvent(motionEvent);
                    }
                });
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.lerp(AndroidUtilities.dp(30.0f), maxWidth(), this.showT), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), 1073741824));
        }

        public int maxWidth() {
            return AndroidUtilities.dp(Math.min(5.7f, this.contentView.getChildCount()) * 32.0f);
        }

        public void intercept(MotionEvent motionEvent) {
            if (!this.shown || this.scrollingAnimation) {
                return;
            }
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action == 1) {
                    this.touching = false;
                    return;
                } else if (action != 2) {
                    return;
                }
            }
            this.touching = true;
            if (!this.scrollingAnimation) {
                resetScrollTo();
            }
            EmojiTabsStrip.this.requestDisallowInterceptTouchEvent(true);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            intercept(motionEvent);
            return super.onTouchEvent(motionEvent);
        }

        public void show(boolean z, boolean z2) {
            if (z == this.shown) {
                return;
            }
            this.shown = z;
            if (!z) {
                scrollTo(0);
            }
            ValueAnimator valueAnimator = this.showAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (z2) {
                float[] fArr = new float[2];
                fArr[0] = this.showT;
                fArr[1] = z ? 1.0f : 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.showAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        EmojiTabsStrip.EmojiTabsView.this.lambda$show$0(valueAnimator2);
                    }
                });
                this.showAnimator.setDuration(475L);
                this.showAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.showAnimator.start();
                return;
            }
            this.showT = z ? 1.0f : 0.0f;
            invalidate();
            requestLayout();
            updateButtonsVisibility();
            EmojiTabsStrip.this.contentView.invalidate();
        }

        public void lambda$show$0(ValueAnimator valueAnimator) {
            this.showT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
            requestLayout();
            updateButtonsVisibility();
            EmojiTabsStrip.this.contentView.invalidate();
        }
    }
}
