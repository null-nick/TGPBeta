package org.telegram.ui.bots;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.util.Consumer;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotApp;
import org.telegram.tgnet.TLRPC$InputInvoice;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$PaymentForm;
import org.telegram.tgnet.TLRPC$PaymentReceipt;
import org.telegram.tgnet.TLRPC$TL_appWebViewResultUrl;
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputBotAppID;
import org.telegram.tgnet.TLRPC$TL_messages_prolongWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestAppWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestSimpleWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestWebView;
import org.telegram.tgnet.TLRPC$TL_messages_sendWebViewData;
import org.telegram.tgnet.TLRPC$TL_messages_toggleBotInAttachMenu;
import org.telegram.tgnet.TLRPC$TL_payments_paymentFormStars;
import org.telegram.tgnet.TLRPC$TL_simpleWebViewResultUrl;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$TL_webViewResultUrl;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheetTabs;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OverlayActionBarLayoutDialog;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.TopicsFragment;
import org.telegram.ui.bots.BotWebViewAttachedSheet;
import org.telegram.ui.bots.BotWebViewContainer;
import org.telegram.ui.bots.BotWebViewMenuContainer;
import org.telegram.ui.bots.ChatAttachAlertBotWebViewLayout;

public class BotWebViewAttachedSheet implements NotificationCenter.NotificationCenterDelegate, BaseFragment.AttachedSheet {
    private static final SimpleFloatPropertyCompat<BotWebViewAttachedSheet> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", new SimpleFloatPropertyCompat.Getter() {
        @Override
        public final float get(Object obj) {
            float f;
            f = ((BotWebViewAttachedSheet) obj).actionBarTransitionProgress;
            return f;
        }
    }, new SimpleFloatPropertyCompat.Setter() {
        @Override
        public final void set(Object obj, float f) {
            BotWebViewAttachedSheet.lambda$static$2((BotWebViewAttachedSheet) obj, f);
        }
    }).setMultiplier(100.0f);
    private ActionBar actionBar;
    private int actionBarColor;
    private boolean actionBarIsLight;
    private Drawable actionBarShadow;
    private BackDrawable backDrawable;
    private long botId;
    private String buttonText;
    private int currentAccount;
    private boolean dismissed;
    private boolean forceExpnaded;
    private final BaseFragment fragment;
    private boolean ignoreLayout;
    private TextView mainButton;
    private VerticalPositionAutoAnimator mainButtonAutoAnimator;
    private boolean mainButtonProgressWasVisible;
    private boolean mainButtonWasVisible;
    private int navBarColor;
    private boolean needCloseConfirmation;
    private Runnable onDismissListener;
    private boolean overrideBackgroundColor;
    private Activity parentActivity;
    private long peerId;
    private ChatAttachAlertBotWebViewLayout.WebProgressView progressView;
    private long queryId;
    private VerticalPositionAutoAnimator radialProgressAutoAnimator;
    private RadialProgressView radialProgressView;
    private int replyToMsgId;
    private WebViewRequestProps requestProps;
    private Theme.ResourcesProvider resourcesProvider;
    private ActionBarMenuSubItem settingsItem;
    public boolean showExpanded;
    private float shown;
    private ValueAnimator shownAnimator;
    private boolean silent;
    private SpringAnimation springAnimation;
    private Boolean wasLightStatusBar;
    private BotWebViewContainer webViewContainer;
    public final WindowView windowView;
    private float actionBarTransitionProgress = 0.0f;
    private Paint linePaint = new Paint(1);
    private Paint dimPaint = new Paint();
    private Paint backgroundPaint = new Paint(1);
    private int actionBarColorKey = -1;
    private Paint actionBarPaint = new Paint(1);
    private Runnable pollRunnable = new Runnable() {
        @Override
        public final void run() {
            BotWebViewAttachedSheet.this.lambda$new$5();
        }
    };
    private int lineColor = Theme.getColor(Theme.key_sheet_scrollUp);
    private ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(getContext()) {
        @Override
        protected void onMeasure(int r5, int r6) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotWebViewAttachedSheet.AnonymousClass1.onMeasure(int, int):void");
        }

        @Override
        public void requestLayout() {
            if (BotWebViewAttachedSheet.this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }
    };

    @Override
    public boolean isFullyVisible() {
        return false;
    }

    @Override
    public void setKeyboardHeightFromParent(int i) {
    }

    @Override
    public boolean showDialog(Dialog dialog) {
        return false;
    }

    public void showJustAddedBulletin() {
        TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot;
        final String formatString;
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
        Iterator<TLRPC$TL_attachMenuBot> it = MediaDataController.getInstance(this.currentAccount).getAttachMenuBots().bots.iterator();
        while (true) {
            if (!it.hasNext()) {
                tLRPC$TL_attachMenuBot = null;
                break;
            } else {
                tLRPC$TL_attachMenuBot = it.next();
                if (tLRPC$TL_attachMenuBot.bot_id == this.botId) {
                    break;
                }
            }
        }
        if (tLRPC$TL_attachMenuBot == null) {
            return;
        }
        boolean z = tLRPC$TL_attachMenuBot.show_in_side_menu;
        if (z && tLRPC$TL_attachMenuBot.show_in_attach_menu) {
            formatString = LocaleController.formatString("BotAttachMenuShortcatAddedAttachAndSide", R.string.BotAttachMenuShortcatAddedAttachAndSide, user.first_name);
        } else if (z) {
            formatString = LocaleController.formatString("BotAttachMenuShortcatAddedSide", R.string.BotAttachMenuShortcatAddedSide, user.first_name);
        } else {
            formatString = LocaleController.formatString("BotAttachMenuShortcatAddedAttach", R.string.BotAttachMenuShortcatAddedAttach, user.first_name);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$showJustAddedBulletin$0(formatString);
            }
        }, 200L);
    }

    public void lambda$showJustAddedBulletin$0(String str) {
        BulletinFactory.of(this.windowView, this.resourcesProvider).createSimpleBulletin(R.raw.contact_check, AndroidUtilities.replaceTags(str)).setDuration(5000).show(true);
    }

    public static void lambda$static$2(BotWebViewAttachedSheet botWebViewAttachedSheet, float f) {
        botWebViewAttachedSheet.actionBarTransitionProgress = f;
        botWebViewAttachedSheet.windowView.invalidate();
        botWebViewAttachedSheet.actionBar.setAlpha(f);
        botWebViewAttachedSheet.updateLightStatusBar();
    }

    @Override
    public WindowView getWindowView() {
        return this.windowView;
    }

    public BottomSheetTabs.WebTabData saveState() {
        BottomSheetTabs.WebTabData webTabData = new BottomSheetTabs.WebTabData();
        webTabData.actionBarColor = this.actionBarColor;
        webTabData.actionBarColorKey = this.actionBarColorKey;
        webTabData.overrideActionBarColor = this.overrideBackgroundColor;
        webTabData.backgroundColor = this.backgroundPaint.getColor();
        webTabData.props = this.requestProps;
        BotWebViewContainer botWebViewContainer = this.webViewContainer;
        boolean z = true;
        webTabData.ready = botWebViewContainer != null && botWebViewContainer.isPageLoaded();
        webTabData.themeIsDark = Theme.isCurrentThemeDark();
        BotWebViewContainer botWebViewContainer2 = this.webViewContainer;
        webTabData.lastUrl = botWebViewContainer2 != null ? botWebViewContainer2.getUrlLoaded() : null;
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
        if ((webViewSwipeContainer == null || webViewSwipeContainer.getSwipeOffsetY() >= 0.0f) && !this.forceExpnaded) {
            z = false;
        }
        webTabData.expanded = z;
        BotWebViewContainer botWebViewContainer3 = this.webViewContainer;
        BotWebViewContainer.MyWebView webView = botWebViewContainer3 == null ? null : botWebViewContainer3.getWebView();
        if (webView != null) {
            this.webViewContainer.preserveWebView();
            webTabData.webView = webView;
            BotWebViewContainer botWebViewContainer4 = this.webViewContainer;
            webTabData.webViewProxy = botWebViewContainer4 != null ? botWebViewContainer4.getProxy() : null;
            webTabData.webViewWidth = webView.getWidth();
            webTabData.webViewHeight = webView.getHeight();
            webView.onPause();
        }
        return webTabData;
    }

    public boolean restoreState(BaseFragment baseFragment, BottomSheetTabs.WebTabData webTabData) {
        int i;
        if (webTabData == null || webTabData.props == null) {
            return false;
        }
        if (webTabData.overrideActionBarColor) {
            setBackgroundColor(webTabData.backgroundColor, false);
        }
        if (webTabData.overrideActionBarColor) {
            i = webTabData.actionBarColor;
        } else {
            int i2 = webTabData.actionBarColorKey;
            if (i2 < 0) {
                i2 = Theme.key_windowBackgroundWhite;
            }
            i = Theme.getColor(i2, this.resourcesProvider);
        }
        setActionBarColor(i, webTabData.overrideActionBarColor, false);
        this.showExpanded = webTabData.expanded;
        BotWebViewContainer.MyWebView myWebView = webTabData.webView;
        if (myWebView != null) {
            myWebView.onResume();
            this.webViewContainer.replaceWebView(webTabData.webView, webTabData.webViewProxy);
            this.webViewContainer.setState(webTabData.ready, webTabData.lastUrl);
            if (Theme.isCurrentThemeDark() != webTabData.themeIsDark) {
                if (this.webViewContainer.getWebView() != null) {
                    this.webViewContainer.getWebView().animate().cancel();
                    this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                }
                this.progressView.setLoadProgress(0.0f);
                this.progressView.setAlpha(1.0f);
                this.progressView.setVisibility(0);
                this.webViewContainer.setBotUser(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId)));
                this.webViewContainer.loadFlickerAndSettingsItem(this.currentAccount, this.botId, this.settingsItem);
                this.webViewContainer.setState(false, null);
                if (this.webViewContainer.getWebView() != null) {
                    this.webViewContainer.getWebView().loadUrl("about:blank");
                }
                WebViewRequestProps webViewRequestProps = webTabData.props;
                webViewRequestProps.response = null;
                webViewRequestProps.responseTime = 0L;
            }
        } else {
            WebViewRequestProps webViewRequestProps2 = webTabData.props;
            webViewRequestProps2.response = null;
            webViewRequestProps2.responseTime = 0L;
        }
        requestWebView(baseFragment, webTabData.props);
        return true;
    }

    public void lambda$new$5() {
        if (this.dismissed || this.queryId == 0) {
            return;
        }
        TLRPC$TL_messages_prolongWebView tLRPC$TL_messages_prolongWebView = new TLRPC$TL_messages_prolongWebView();
        tLRPC$TL_messages_prolongWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
        tLRPC$TL_messages_prolongWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.peerId);
        tLRPC$TL_messages_prolongWebView.query_id = this.queryId;
        tLRPC$TL_messages_prolongWebView.silent = this.silent;
        if (this.replyToMsgId != 0) {
            tLRPC$TL_messages_prolongWebView.reply_to = SendMessagesHelper.getInstance(this.currentAccount).createReplyInput(this.replyToMsgId);
            tLRPC$TL_messages_prolongWebView.flags |= 1;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_prolongWebView, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                BotWebViewAttachedSheet.this.lambda$new$4(tLObject, tLRPC$TL_error);
            }
        });
    }

    public void lambda$new$4(TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$new$3(tLRPC$TL_error);
            }
        });
    }

    public void lambda$new$3(TLRPC$TL_error tLRPC$TL_error) {
        if (this.dismissed) {
            return;
        }
        if (tLRPC$TL_error != null) {
            dismiss();
        } else {
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000L);
        }
    }

    public Context getContext() {
        return this.fragment.getContext();
    }

    public BotWebViewAttachedSheet(BaseFragment baseFragment) {
        this.fragment = baseFragment;
        this.resourcesProvider = baseFragment.getResourceProvider();
        Context context = getContext();
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        int i = Theme.key_windowBackgroundWhite;
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(context, resourcesProvider, getColor(i)) {
            @Override
            public void onWebViewCreated() {
                super.onWebViewCreated();
                BotWebViewAttachedSheet.this.swipeContainer.setWebView(BotWebViewAttachedSheet.this.webViewContainer.getWebView());
            }
        };
        this.webViewContainer = botWebViewContainer;
        botWebViewContainer.setDelegate(new AnonymousClass3());
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(4.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.dimPaint.setColor(1073741824);
        this.actionBarColor = getColor(i);
        this.navBarColor = getColor(Theme.key_windowBackgroundGray);
        checkNavBarColor();
        WindowView windowView = new WindowView(getContext());
        this.windowView = windowView;
        windowView.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() {
            @Override
            public final void onSizeChanged(int i2, boolean z) {
                BotWebViewAttachedSheet.this.lambda$new$6(i2, z);
            }
        });
        windowView.addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f, 49, 0.0f, 24.0f, 0.0f, 0.0f));
        TextView textView = new TextView(this, getContext()) {
            @Override
            protected void onMeasure(int i2, int i3) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    Point point = AndroidUtilities.displaySize;
                    i2 = View.MeasureSpec.makeMeasureSpec((int) (Math.min(point.x, point.y) * 0.8f), 1073741824);
                }
                super.onMeasure(i2, i3);
            }
        };
        this.mainButton = textView;
        textView.setVisibility(8);
        this.mainButton.setAlpha(0.0f);
        this.mainButton.setSingleLine();
        this.mainButton.setGravity(17);
        this.mainButton.setTypeface(AndroidUtilities.bold());
        int dp = AndroidUtilities.dp(16.0f);
        this.mainButton.setPadding(dp, 0, dp, 0);
        this.mainButton.setTextSize(1, 14.0f);
        this.mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                BotWebViewAttachedSheet.this.lambda$new$7(view);
            }
        });
        windowView.addView(this.mainButton, LayoutHelper.createFrame(-1, 48, 81));
        this.mainButtonAutoAnimator = VerticalPositionAutoAnimator.attach(this.mainButton);
        RadialProgressView radialProgressView = new RadialProgressView(this, getContext()) {
            @Override
            protected void onMeasure(int i2, int i3) {
                super.onMeasure(i2, i3);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    float dp2 = AndroidUtilities.dp(10.0f);
                    Point point = AndroidUtilities.displaySize;
                    marginLayoutParams.rightMargin = (int) (dp2 + (Math.min(point.x, point.y) * 0.1f));
                    return;
                }
                marginLayoutParams.rightMargin = AndroidUtilities.dp(10.0f);
            }
        };
        this.radialProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(18.0f));
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(8);
        windowView.addView(this.radialProgressView, LayoutHelper.createFrame(28, 28.0f, 85, 0.0f, 0.0f, 10.0f, 10.0f));
        this.radialProgressAutoAnimator = VerticalPositionAutoAnimator.attach(this.radialProgressView);
        this.actionBarShadow = ContextCompat.getDrawable(getContext(), R.drawable.header_shadow).mutate();
        ActionBar actionBar = new ActionBar(this, getContext(), this.resourcesProvider) {
            @Override
            public void onMeasure(int i2, int i3) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    Point point = AndroidUtilities.displaySize;
                    i2 = View.MeasureSpec.makeMeasureSpec((int) (Math.min(point.x, point.y) * 0.8f), 1073741824);
                }
                super.onMeasure(i2, i3);
            }
        };
        this.actionBar = actionBar;
        actionBar.setBackgroundColor(0);
        ActionBar actionBar2 = this.actionBar;
        BackDrawable backDrawable = new BackDrawable(false);
        this.backDrawable = backDrawable;
        actionBar2.setBackButtonDrawable(backDrawable);
        this.backDrawable.setAnimationTime(200.0f);
        this.backDrawable.setColorFilter(null);
        this.backDrawable.setRotation(1.0f, false);
        updateActionBarColors();
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    BotWebViewAttachedSheet.this.onCheckDismissByUser();
                }
            }
        });
        this.actionBar.setAlpha(0.0f);
        windowView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2, 49));
        ChatAttachAlertBotWebViewLayout.WebProgressView webProgressView = new ChatAttachAlertBotWebViewLayout.WebProgressView(this, getContext(), this.resourcesProvider) {
            @Override
            protected void onMeasure(int i2, int i3) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    Point point = AndroidUtilities.displaySize;
                    i2 = View.MeasureSpec.makeMeasureSpec((int) (Math.min(point.x, point.y) * 0.8f), 1073741824);
                }
                super.onMeasure(i2, i3);
            }
        };
        this.progressView = webProgressView;
        windowView.addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
        this.webViewContainer.setWebViewProgressListener(new Consumer() {
            @Override
            public final void accept(Object obj) {
                BotWebViewAttachedSheet.this.lambda$new$9((Float) obj);
            }
        });
        if (this.springAnimation == null) {
            this.springAnimation = new SpringAnimation(this, ACTION_BAR_TRANSITION_PROGRESS_VALUE).setSpring(new SpringForce().setStiffness(1200.0f).setDampingRatio(1.0f));
        }
        this.swipeContainer.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setScrollListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$new$10();
            }
        });
        this.swipeContainer.setScrollEndListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$new$11();
            }
        });
        this.swipeContainer.setDelegate(new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.Delegate() {
            @Override
            public final void onDismiss() {
                BotWebViewAttachedSheet.this.lambda$new$12();
            }
        });
        this.swipeContainer.setTopActionBarOffsetY((ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f));
        this.swipeContainer.setIsKeyboardVisible(new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                Boolean lambda$new$13;
                lambda$new$13 = BotWebViewAttachedSheet.this.lambda$new$13((Void) obj);
                return lambda$new$13;
            }
        });
    }

    public class AnonymousClass3 implements BotWebViewContainer.Delegate {
        private boolean sentWebViewData;

        @Override
        public void onWebAppReady() {
            BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
        }

        AnonymousClass3() {
        }

        @Override
        public void onCloseRequested(Runnable runnable) {
            BotWebViewAttachedSheet.this.dismiss(false, runnable);
        }

        @Override
        public void onWebAppSetupClosingBehavior(boolean z) {
            BotWebViewAttachedSheet.this.needCloseConfirmation = z;
        }

        @Override
        public void onSendWebViewData(String str) {
            if (BotWebViewAttachedSheet.this.queryId != 0 || this.sentWebViewData) {
                return;
            }
            this.sentWebViewData = true;
            TLRPC$TL_messages_sendWebViewData tLRPC$TL_messages_sendWebViewData = new TLRPC$TL_messages_sendWebViewData();
            tLRPC$TL_messages_sendWebViewData.bot = MessagesController.getInstance(BotWebViewAttachedSheet.this.currentAccount).getInputUser(BotWebViewAttachedSheet.this.botId);
            tLRPC$TL_messages_sendWebViewData.random_id = Utilities.random.nextLong();
            tLRPC$TL_messages_sendWebViewData.button_text = BotWebViewAttachedSheet.this.buttonText;
            tLRPC$TL_messages_sendWebViewData.data = str;
            ConnectionsManager.getInstance(BotWebViewAttachedSheet.this.currentAccount).sendRequest(tLRPC$TL_messages_sendWebViewData, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewAttachedSheet.AnonymousClass3.this.lambda$onSendWebViewData$0(tLObject, tLRPC$TL_error);
                }
            });
        }

        public void lambda$onSendWebViewData$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject instanceof TLRPC$TL_updates) {
                MessagesController.getInstance(BotWebViewAttachedSheet.this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
            }
            final BotWebViewAttachedSheet botWebViewAttachedSheet = BotWebViewAttachedSheet.this;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    BotWebViewAttachedSheet.this.dismiss();
                }
            });
        }

        @Override
        public void onWebAppSetActionBarColor(int i, int i2, boolean z) {
            BotWebViewAttachedSheet.this.actionBarColorKey = i;
            BotWebViewAttachedSheet.this.setActionBarColor(i2, z, true);
        }

        @Override
        public void onWebAppSetBackgroundColor(int i) {
            BotWebViewAttachedSheet.this.setBackgroundColor(i, true);
        }

        @Override
        public void onSetBackButtonVisible(boolean z) {
            BotWebViewAttachedSheet.this.backDrawable.setRotation(z ? 0.0f : 1.0f, true);
        }

        @Override
        public void onSetSettingsButtonVisible(boolean z) {
            if (BotWebViewAttachedSheet.this.settingsItem != null) {
                BotWebViewAttachedSheet.this.settingsItem.setVisibility(z ? 0 : 8);
            }
        }

        @Override
        public void onWebAppOpenInvoice(TLRPC$InputInvoice tLRPC$InputInvoice, final String str, TLObject tLObject) {
            PaymentFormActivity paymentFormActivity;
            BaseFragment lastFragment = ((LaunchActivity) BotWebViewAttachedSheet.this.parentActivity).getActionBarLayout().getLastFragment();
            if (tLObject instanceof TLRPC$TL_payments_paymentFormStars) {
                AndroidUtilities.hideKeyboard(BotWebViewAttachedSheet.this.windowView);
                final AlertDialog alertDialog = new AlertDialog(BotWebViewAttachedSheet.this.getContext(), 3);
                alertDialog.showDelayed(150L);
                StarsController.getInstance(BotWebViewAttachedSheet.this.currentAccount).openPaymentForm(null, tLRPC$InputInvoice, (TLRPC$TL_payments_paymentFormStars) tLObject, new Runnable() {
                    @Override
                    public final void run() {
                        AlertDialog.this.dismiss();
                    }
                }, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        BotWebViewAttachedSheet.AnonymousClass3.this.lambda$onWebAppOpenInvoice$2(str, (String) obj);
                    }
                });
                return;
            }
            if (tLObject instanceof TLRPC$PaymentForm) {
                TLRPC$PaymentForm tLRPC$PaymentForm = (TLRPC$PaymentForm) tLObject;
                MessagesController.getInstance(BotWebViewAttachedSheet.this.currentAccount).putUsers(tLRPC$PaymentForm.users, false);
                paymentFormActivity = new PaymentFormActivity(tLRPC$PaymentForm, str, lastFragment);
            } else {
                paymentFormActivity = tLObject instanceof TLRPC$PaymentReceipt ? new PaymentFormActivity((TLRPC$PaymentReceipt) tLObject) : null;
            }
            if (paymentFormActivity != null) {
                BotWebViewAttachedSheet.this.swipeContainer.stickTo((-BotWebViewAttachedSheet.this.swipeContainer.getOffsetY()) + BotWebViewAttachedSheet.this.swipeContainer.getTopActionBarOffsetY());
                AndroidUtilities.hideKeyboard(BotWebViewAttachedSheet.this.windowView);
                final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(BotWebViewAttachedSheet.this.getContext(), BotWebViewAttachedSheet.this.resourcesProvider);
                overlayActionBarLayoutDialog.show();
                paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() {
                    @Override
                    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                        BotWebViewAttachedSheet.AnonymousClass3.this.lambda$onWebAppOpenInvoice$3(overlayActionBarLayoutDialog, str, invoiceStatus);
                    }
                });
                paymentFormActivity.setResourcesProvider(BotWebViewAttachedSheet.this.resourcesProvider);
                overlayActionBarLayoutDialog.addFragment(paymentFormActivity);
            }
        }

        public void lambda$onWebAppOpenInvoice$2(String str, String str2) {
            BotWebViewAttachedSheet.this.webViewContainer.onInvoiceStatusUpdate(str, str2);
        }

        public void lambda$onWebAppOpenInvoice$3(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, String str, PaymentFormActivity.InvoiceStatus invoiceStatus) {
            if (invoiceStatus != PaymentFormActivity.InvoiceStatus.PENDING) {
                overlayActionBarLayoutDialog.dismiss();
            }
            BotWebViewAttachedSheet.this.webViewContainer.onInvoiceStatusUpdate(str, invoiceStatus.name().toLowerCase(Locale.ROOT));
        }

        @Override
        public void onWebAppExpand() {
            if (BotWebViewAttachedSheet.this.swipeContainer.isSwipeInProgress()) {
                return;
            }
            BotWebViewAttachedSheet.this.swipeContainer.stickTo((-BotWebViewAttachedSheet.this.swipeContainer.getOffsetY()) + BotWebViewAttachedSheet.this.swipeContainer.getTopActionBarOffsetY());
        }

        @Override
        public void onWebAppSwitchInlineQuery(final TLRPC$User tLRPC$User, final String str, List<String> list) {
            if (list.isEmpty()) {
                if (BotWebViewAttachedSheet.this.parentActivity instanceof LaunchActivity) {
                    BaseFragment lastFragment = ((LaunchActivity) BotWebViewAttachedSheet.this.parentActivity).getActionBarLayout().getLastFragment();
                    if (lastFragment instanceof ChatActivity) {
                        ((ChatActivity) lastFragment).getChatActivityEnterView().setFieldText("@" + UserObject.getPublicUsername(tLRPC$User) + " " + str);
                        BotWebViewAttachedSheet.this.dismiss();
                        return;
                    }
                    return;
                }
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("dialogsType", 14);
            bundle.putBoolean("onlySelect", true);
            bundle.putBoolean("allowGroups", list.contains("groups"));
            bundle.putBoolean("allowMegagroups", list.contains("groups"));
            bundle.putBoolean("allowLegacyGroups", list.contains("groups"));
            bundle.putBoolean("allowUsers", list.contains("users"));
            bundle.putBoolean("allowChannels", list.contains("channels"));
            bundle.putBoolean("allowBots", list.contains("bots"));
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            AndroidUtilities.hideKeyboard(BotWebViewAttachedSheet.this.windowView);
            final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(BotWebViewAttachedSheet.this.getContext(), BotWebViewAttachedSheet.this.resourcesProvider);
            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                @Override
                public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, TopicsFragment topicsFragment) {
                    boolean lambda$onWebAppSwitchInlineQuery$4;
                    lambda$onWebAppSwitchInlineQuery$4 = BotWebViewAttachedSheet.AnonymousClass3.this.lambda$onWebAppSwitchInlineQuery$4(tLRPC$User, str, overlayActionBarLayoutDialog, dialogsActivity2, arrayList, charSequence, z, topicsFragment);
                    return lambda$onWebAppSwitchInlineQuery$4;
                }
            });
            overlayActionBarLayoutDialog.show();
            overlayActionBarLayoutDialog.addFragment(dialogsActivity);
        }

        public boolean lambda$onWebAppSwitchInlineQuery$4(TLRPC$User tLRPC$User, String str, OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, TopicsFragment topicsFragment) {
            long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (DialogObject.isEncryptedDialog(j)) {
                bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j));
            } else if (DialogObject.isUserDialog(j)) {
                bundle.putLong("user_id", j);
            } else {
                bundle.putLong("chat_id", -j);
            }
            bundle.putString("start_text", "@" + UserObject.getPublicUsername(tLRPC$User) + " " + str);
            if (BotWebViewAttachedSheet.this.parentActivity instanceof LaunchActivity) {
                BaseFragment lastFragment = ((LaunchActivity) BotWebViewAttachedSheet.this.parentActivity).getActionBarLayout().getLastFragment();
                if (MessagesController.getInstance(BotWebViewAttachedSheet.this.currentAccount).checkCanOpenChat(bundle, lastFragment)) {
                    overlayActionBarLayoutDialog.dismiss();
                    BotWebViewAttachedSheet.this.dismissed = true;
                    AndroidUtilities.cancelRunOnUIThread(BotWebViewAttachedSheet.this.pollRunnable);
                    BotWebViewAttachedSheet.this.webViewContainer.destroyWebView();
                    NotificationCenter.getInstance(BotWebViewAttachedSheet.this.currentAccount).removeObserver(BotWebViewAttachedSheet.this, NotificationCenter.webViewResultSent);
                    NotificationCenter.getGlobalInstance().removeObserver(BotWebViewAttachedSheet.this, NotificationCenter.didSetNewTheme);
                    BotWebViewAttachedSheet.this.release();
                    lastFragment.presentFragment(new INavigationLayout.NavigationParams(new ChatActivity(bundle)).setRemoveLast(true));
                }
            }
            return true;
        }

        @Override
        public void onSetupMainButton(final boolean z, boolean z2, String str, int i, int i2, final boolean z3) {
            BotWebViewAttachedSheet.this.mainButton.setClickable(z2);
            BotWebViewAttachedSheet.this.mainButton.setText(str);
            BotWebViewAttachedSheet.this.mainButton.setTextColor(i2);
            BotWebViewAttachedSheet.this.mainButton.setBackground(BotWebViewContainer.getMainButtonRippleDrawable(i));
            if (z != BotWebViewAttachedSheet.this.mainButtonWasVisible) {
                BotWebViewAttachedSheet.this.mainButtonWasVisible = z;
                BotWebViewAttachedSheet.this.mainButton.animate().cancel();
                if (z) {
                    BotWebViewAttachedSheet.this.mainButton.setAlpha(0.0f);
                    BotWebViewAttachedSheet.this.mainButton.setVisibility(0);
                }
                BotWebViewAttachedSheet.this.mainButton.animate().alpha(z ? 1.0f : 0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            BotWebViewAttachedSheet.this.mainButton.setVisibility(8);
                        }
                        BotWebViewAttachedSheet.this.swipeContainer.requestLayout();
                    }
                }).start();
            }
            BotWebViewAttachedSheet.this.radialProgressView.setProgressColor(i2);
            if (z3 != BotWebViewAttachedSheet.this.mainButtonProgressWasVisible) {
                BotWebViewAttachedSheet.this.mainButtonProgressWasVisible = z3;
                BotWebViewAttachedSheet.this.radialProgressView.animate().cancel();
                if (z3) {
                    BotWebViewAttachedSheet.this.radialProgressView.setAlpha(0.0f);
                    BotWebViewAttachedSheet.this.radialProgressView.setVisibility(0);
                }
                BotWebViewAttachedSheet.this.radialProgressView.animate().alpha(z3 ? 1.0f : 0.0f).scaleX(z3 ? 1.0f : 0.1f).scaleY(z3 ? 1.0f : 0.1f).setDuration(250L).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (z3) {
                            return;
                        }
                        BotWebViewAttachedSheet.this.radialProgressView.setVisibility(8);
                    }
                }).start();
            }
        }

        @Override
        public boolean isClipboardAvailable() {
            return MediaDataController.getInstance(BotWebViewAttachedSheet.this.currentAccount).botInAttachMenu(BotWebViewAttachedSheet.this.botId);
        }
    }

    public void lambda$new$6(int i, boolean z) {
        if (i > AndroidUtilities.dp(20.0f)) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((-webViewSwipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY());
        }
    }

    public void lambda$new$7(View view) {
        this.webViewContainer.onMainButtonPressed();
    }

    public void lambda$new$9(Float f) {
        this.progressView.setLoadProgressAnimated(f.floatValue());
        if (f.floatValue() == 1.0f) {
            ValueAnimator duration = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewAttachedSheet.this.lambda$new$8(valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    BotWebViewAttachedSheet.this.progressView.setVisibility(8);
                }
            });
            duration.start();
        }
    }

    public void lambda$new$8(ValueAnimator valueAnimator) {
        this.progressView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void lambda$new$10() {
        if (this.swipeContainer.getSwipeOffsetY() > 0.0f) {
            this.dimPaint.setAlpha((int) ((1.0f - MathUtils.clamp(this.swipeContainer.getSwipeOffsetY() / this.swipeContainer.getHeight(), 0.0f, 1.0f)) * 64.0f));
        } else {
            this.dimPaint.setAlpha(64);
        }
        this.windowView.invalidate();
        this.webViewContainer.invalidateViewPortHeight();
        if (this.springAnimation != null) {
            float f = (1.0f - (Math.min(this.swipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY()) > 0.5f ? 1 : 0) * 100.0f;
            if (this.springAnimation.getSpring().getFinalPosition() != f) {
                this.springAnimation.getSpring().setFinalPosition(f);
                this.springAnimation.start();
            }
        }
        float max = Math.max(0.0f, this.swipeContainer.getSwipeOffsetY());
        this.mainButtonAutoAnimator.setOffsetY(max);
        this.radialProgressAutoAnimator.setOffsetY(max);
        System.currentTimeMillis();
    }

    public void lambda$new$11() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    public void lambda$new$12() {
        dismiss(true, null);
    }

    public Boolean lambda$new$13(Void r2) {
        return Boolean.valueOf(this.windowView.getKeyboardHeight() >= AndroidUtilities.dp(20.0f));
    }

    public float getContainerTop() {
        return AndroidUtilities.isTablet() ? AndroidUtilities.lerp(this.swipeContainer.getTranslationY() + AndroidUtilities.dp(12.0f), AndroidUtilities.statusBarHeight / 2.0f, this.actionBarTransitionProgress) : AndroidUtilities.lerp(this.swipeContainer.getTranslationY(), AndroidUtilities.statusBarHeight + (ActionBar.getCurrentActionBarHeight() / 2.0f), AndroidUtilities.isTablet() ? 0.0f : this.actionBarTransitionProgress) + AndroidUtilities.dp(12.0f);
    }

    public void attachInternal() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
        if (this.fragment.getLayoutContainer() != null) {
            this.fragment.getLayoutContainer().addView(this.windowView);
        }
    }

    @Override
    public void release() {
        BotWebViewContainer botWebViewContainer = this.webViewContainer;
        if (botWebViewContainer != null) {
            botWebViewContainer.destroyWebView();
        }
        ArrayList<BaseFragment.AttachedSheet> arrayList = this.fragment.sheetsStack;
        if (arrayList != null) {
            arrayList.remove(this);
        }
        WindowView windowView = this.windowView;
        if (windowView == null || !(windowView.getParent() instanceof ViewGroup)) {
            return;
        }
        ((ViewGroup) this.windowView.getParent()).removeView(this.windowView);
    }

    @Override
    public boolean attachedToParent() {
        return this.windowView != null;
    }

    public void setParentActivity(Activity activity) {
        this.parentActivity = activity;
    }

    private void updateActionBarColors() {
        if (this.overrideBackgroundColor) {
            return;
        }
        BackDrawable backDrawable = this.backDrawable;
        int i = Theme.key_windowBackgroundWhiteBlackText;
        backDrawable.setColor(getColor(i));
        this.backDrawable.setRotatedColor(getColor(i));
        this.actionBar.setTitleColor(getColor(i));
        this.actionBar.setItemsColor(getColor(i), false);
        this.actionBar.setItemsBackgroundColor(getColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setPopupBackgroundColor(getColor(Theme.key_actionBarDefaultSubmenuBackground), false);
        this.actionBar.setPopupItemsColor(getColor(Theme.key_actionBarDefaultSubmenuItem), false, false);
        this.actionBar.setPopupItemsColor(getColor(Theme.key_actionBarDefaultSubmenuItemIcon), true, false);
        this.actionBar.setPopupItemsSelectorColor(getColor(Theme.key_dialogButtonSelector), false);
    }

    private void updateLightStatusBar() {
        boolean z;
        if (this.overrideBackgroundColor) {
            z = !this.actionBarIsLight;
        } else {
            z = !AndroidUtilities.isTablet() && ColorUtils.calculateLuminance(Theme.getColor(Theme.key_windowBackgroundWhite, null, true)) >= 0.7210000157356262d && this.actionBarTransitionProgress >= 0.85f;
        }
        Boolean bool = this.wasLightStatusBar;
        if (bool == null || bool.booleanValue() != z) {
            this.wasLightStatusBar = Boolean.valueOf(z);
            if (Build.VERSION.SDK_INT >= 23) {
                int systemUiVisibility = this.windowView.getSystemUiVisibility();
                this.windowView.setSystemUiVisibility(z ? systemUiVisibility | LiteMode.FLAG_ANIMATED_EMOJI_REACTIONS_NOT_PREMIUM : systemUiVisibility & (-8193));
            }
        }
    }

    public static JSONObject makeThemeParams(Theme.ResourcesProvider resourcesProvider) {
        try {
            JSONObject jSONObject = new JSONObject();
            int color = Theme.getColor(Theme.key_dialogBackground, resourcesProvider);
            jSONObject.put("bg_color", color);
            jSONObject.put("section_bg_color", Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider));
            jSONObject.put("secondary_bg_color", Theme.getColor(Theme.key_windowBackgroundGray, resourcesProvider));
            jSONObject.put("text_color", Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
            jSONObject.put("hint_color", Theme.getColor(Theme.key_windowBackgroundWhiteHintText, resourcesProvider));
            jSONObject.put("link_color", Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, resourcesProvider));
            jSONObject.put("button_color", Theme.getColor(Theme.key_featuredStickers_addButton, resourcesProvider));
            jSONObject.put("button_text_color", Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider));
            jSONObject.put("header_bg_color", Theme.getColor(Theme.key_actionBarDefault, resourcesProvider));
            jSONObject.put("accent_text_color", Theme.blendOver(color, Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4, resourcesProvider)));
            jSONObject.put("section_header_text_color", Theme.blendOver(color, Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader, resourcesProvider)));
            jSONObject.put("subtitle_text_color", Theme.blendOver(color, Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider)));
            jSONObject.put("destructive_text_color", Theme.blendOver(color, Theme.getColor(Theme.key_text_RedRegular, resourcesProvider)));
            return jSONObject;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public void requestWebView(int i, long j, long j2, String str, String str2, int i2, int i3, boolean z, int i4) {
        requestWebView(null, WebViewRequestProps.of(i, j, j2, str, str2, i2, i3, z, null, false, null, null, i4));
    }

    public void requestWebView(int i, long j, long j2, String str, String str2, int i2, int i3, boolean z) {
        requestWebView(null, WebViewRequestProps.of(i, j, j2, str, str2, i2, i3, z, null, false, null, null, 0));
    }

    public void requestWebView(int i, long j, long j2, String str, String str2, int i2, int i3, boolean z, BaseFragment baseFragment, TLRPC$BotApp tLRPC$BotApp, boolean z2, String str3, TLRPC$User tLRPC$User) {
        requestWebView(baseFragment, WebViewRequestProps.of(i, j, j2, str, str2, i2, i3, z, tLRPC$BotApp, z2, str3, tLRPC$User, 0));
    }

    public static class WebViewRequestProps {
        public boolean allowWrite;
        public TLRPC$BotApp app;
        public long botId;
        public TLRPC$User botUser;
        public String buttonText;
        public String buttonUrl;
        public int currentAccount;
        public int flags;
        public long peerId;
        public int replyToMsgId;
        public TLObject response;
        public long responseTime;
        public boolean silent;
        public String startParam;
        public int type;

        public static WebViewRequestProps of(int i, long j, long j2, String str, String str2, int i2, int i3, boolean z, TLRPC$BotApp tLRPC$BotApp, boolean z2, String str3, TLRPC$User tLRPC$User, int i4) {
            WebViewRequestProps webViewRequestProps = new WebViewRequestProps();
            webViewRequestProps.currentAccount = i;
            webViewRequestProps.peerId = j;
            webViewRequestProps.botId = j2;
            webViewRequestProps.buttonText = str;
            webViewRequestProps.buttonUrl = str2;
            webViewRequestProps.type = i2;
            webViewRequestProps.replyToMsgId = i3;
            webViewRequestProps.silent = z;
            webViewRequestProps.app = tLRPC$BotApp;
            webViewRequestProps.allowWrite = z2;
            webViewRequestProps.startParam = str3;
            webViewRequestProps.botUser = tLRPC$User;
            webViewRequestProps.flags = i4;
            return webViewRequestProps;
        }

        public void applyResponse(TLObject tLObject) {
            this.response = tLObject;
            this.responseTime = System.currentTimeMillis();
        }
    }

    public void requestWebView(BaseFragment baseFragment, WebViewRequestProps webViewRequestProps) {
        TLRPC$InputPeer inputPeer;
        this.requestProps = webViewRequestProps;
        int i = webViewRequestProps.currentAccount;
        this.currentAccount = i;
        this.peerId = webViewRequestProps.peerId;
        this.botId = webViewRequestProps.botId;
        this.replyToMsgId = webViewRequestProps.replyToMsgId;
        this.silent = webViewRequestProps.silent;
        this.buttonText = webViewRequestProps.buttonText;
        CharSequence userName = UserObject.getUserName(MessagesController.getInstance(i).getUser(Long.valueOf(this.botId)));
        try {
            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(AndroidUtilities.dp(20.0f));
            userName = Emoji.replaceEmoji(userName, textPaint.getFontMetricsInt(), false);
        } catch (Exception unused) {
        }
        this.actionBar.setTitle(userName);
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.removeAllViews();
        TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot = null;
        Iterator<TLRPC$TL_attachMenuBot> it = MediaDataController.getInstance(this.currentAccount).getAttachMenuBots().bots.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            TLRPC$TL_attachMenuBot next = it.next();
            if (next.bot_id == this.botId) {
                tLRPC$TL_attachMenuBot = next;
                break;
            }
        }
        createMenu.addItem(R.id.menu_collapse_bot, R.drawable.arrow_more);
        ActionBarMenuItem addItem = createMenu.addItem(0, R.drawable.ic_ab_other);
        addItem.addSubItem(R.id.menu_open_bot, R.drawable.msg_bot, LocaleController.getString(R.string.BotWebViewOpenBot));
        ActionBarMenuSubItem addSubItem = addItem.addSubItem(R.id.menu_settings, R.drawable.msg_settings, LocaleController.getString(R.string.BotWebViewSettings));
        this.settingsItem = addSubItem;
        addSubItem.setVisibility(8);
        addItem.addSubItem(R.id.menu_reload_page, R.drawable.msg_retry, LocaleController.getString(R.string.BotWebViewReloadPage));
        if (tLRPC$TL_attachMenuBot != null && MediaDataController.getInstance(this.currentAccount).canCreateAttachedMenuBotShortcut(tLRPC$TL_attachMenuBot.bot_id)) {
            addItem.addSubItem(R.id.menu_add_to_home_screen_bot, R.drawable.msg_home, LocaleController.getString(R.string.AddShortcut));
        }
        addItem.addSubItem(R.id.menu_tos_bot, R.drawable.menu_intro, LocaleController.getString(R.string.BotWebViewToS));
        if (tLRPC$TL_attachMenuBot != null && (tLRPC$TL_attachMenuBot.show_in_side_menu || tLRPC$TL_attachMenuBot.show_in_attach_menu)) {
            addItem.addSubItem(R.id.menu_delete_bot, R.drawable.msg_delete, LocaleController.getString(R.string.BotWebViewDeleteBot));
        }
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass10());
        JSONObject makeThemeParams = makeThemeParams(this.resourcesProvider);
        this.webViewContainer.setBotUser(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId)));
        this.webViewContainer.loadFlickerAndSettingsItem(this.currentAccount, this.botId, this.settingsItem);
        preloadShortcutBotIcon(webViewRequestProps.botUser, tLRPC$TL_attachMenuBot);
        if (webViewRequestProps.response != null) {
            loadFromResponse(true);
            return;
        }
        int i2 = webViewRequestProps.type;
        if (i2 == 0) {
            TLRPC$TL_messages_requestWebView tLRPC$TL_messages_requestWebView = new TLRPC$TL_messages_requestWebView();
            tLRPC$TL_messages_requestWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.peerId);
            tLRPC$TL_messages_requestWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
            tLRPC$TL_messages_requestWebView.platform = "android";
            String str = webViewRequestProps.buttonUrl;
            if (str != null) {
                tLRPC$TL_messages_requestWebView.url = str;
                tLRPC$TL_messages_requestWebView.flags |= 2;
            }
            if (this.replyToMsgId != 0) {
                tLRPC$TL_messages_requestWebView.reply_to = SendMessagesHelper.getInstance(this.currentAccount).createReplyInput(this.replyToMsgId);
                tLRPC$TL_messages_requestWebView.flags |= 1;
            }
            if (makeThemeParams != null) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestWebView.theme_params = tLRPC$TL_dataJSON;
                tLRPC$TL_dataJSON.data = makeThemeParams.toString();
                tLRPC$TL_messages_requestWebView.flags |= 4;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_requestWebView, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewAttachedSheet.this.lambda$requestWebView$19(tLObject, tLRPC$TL_error);
                }
            });
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.webViewResultSent);
            return;
        }
        if (i2 == 1) {
            TLRPC$TL_messages_requestSimpleWebView tLRPC$TL_messages_requestSimpleWebView = new TLRPC$TL_messages_requestSimpleWebView();
            tLRPC$TL_messages_requestSimpleWebView.from_switch_webview = (webViewRequestProps.flags & 1) != 0;
            tLRPC$TL_messages_requestSimpleWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
            tLRPC$TL_messages_requestSimpleWebView.platform = "android";
            tLRPC$TL_messages_requestSimpleWebView.from_side_menu = (webViewRequestProps.flags & 2) != 0;
            if (makeThemeParams != null) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON2 = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestSimpleWebView.theme_params = tLRPC$TL_dataJSON2;
                tLRPC$TL_dataJSON2.data = makeThemeParams.toString();
                tLRPC$TL_messages_requestSimpleWebView.flags |= 1;
            }
            if (!TextUtils.isEmpty(webViewRequestProps.buttonUrl)) {
                tLRPC$TL_messages_requestSimpleWebView.flags |= 8;
                tLRPC$TL_messages_requestSimpleWebView.url = webViewRequestProps.buttonUrl;
            }
            if (!TextUtils.isEmpty(webViewRequestProps.startParam)) {
                tLRPC$TL_messages_requestSimpleWebView.start_param = webViewRequestProps.startParam;
                tLRPC$TL_messages_requestSimpleWebView.flags |= 16;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_requestSimpleWebView, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewAttachedSheet.this.lambda$requestWebView$17(tLObject, tLRPC$TL_error);
                }
            });
            return;
        }
        if (i2 == 2) {
            TLRPC$TL_messages_requestWebView tLRPC$TL_messages_requestWebView2 = new TLRPC$TL_messages_requestWebView();
            tLRPC$TL_messages_requestWebView2.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
            tLRPC$TL_messages_requestWebView2.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.botId);
            tLRPC$TL_messages_requestWebView2.platform = "android";
            tLRPC$TL_messages_requestWebView2.url = webViewRequestProps.buttonUrl;
            tLRPC$TL_messages_requestWebView2.flags |= 2;
            if (makeThemeParams != null) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON3 = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestWebView2.theme_params = tLRPC$TL_dataJSON3;
                tLRPC$TL_dataJSON3.data = makeThemeParams.toString();
                tLRPC$TL_messages_requestWebView2.flags |= 4;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_requestWebView2, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewAttachedSheet.this.lambda$requestWebView$15(tLObject, tLRPC$TL_error);
                }
            });
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.webViewResultSent);
            return;
        }
        if (i2 != 3) {
            return;
        }
        TLRPC$TL_messages_requestAppWebView tLRPC$TL_messages_requestAppWebView = new TLRPC$TL_messages_requestAppWebView();
        TLRPC$TL_inputBotAppID tLRPC$TL_inputBotAppID = new TLRPC$TL_inputBotAppID();
        TLRPC$BotApp tLRPC$BotApp = webViewRequestProps.app;
        tLRPC$TL_inputBotAppID.id = tLRPC$BotApp.id;
        tLRPC$TL_inputBotAppID.access_hash = tLRPC$BotApp.access_hash;
        tLRPC$TL_messages_requestAppWebView.app = tLRPC$TL_inputBotAppID;
        tLRPC$TL_messages_requestAppWebView.write_allowed = webViewRequestProps.allowWrite;
        tLRPC$TL_messages_requestAppWebView.platform = "android";
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            inputPeer = chatActivity.getCurrentUser() != null ? MessagesController.getInputPeer(chatActivity.getCurrentUser()) : MessagesController.getInputPeer(chatActivity.getCurrentChat());
        } else {
            inputPeer = MessagesController.getInputPeer(webViewRequestProps.botUser);
        }
        tLRPC$TL_messages_requestAppWebView.peer = inputPeer;
        if (!TextUtils.isEmpty(webViewRequestProps.startParam)) {
            tLRPC$TL_messages_requestAppWebView.start_param = webViewRequestProps.startParam;
            tLRPC$TL_messages_requestAppWebView.flags |= 2;
        }
        if (makeThemeParams != null) {
            TLRPC$TL_dataJSON tLRPC$TL_dataJSON4 = new TLRPC$TL_dataJSON();
            tLRPC$TL_messages_requestAppWebView.theme_params = tLRPC$TL_dataJSON4;
            tLRPC$TL_dataJSON4.data = makeThemeParams.toString();
            tLRPC$TL_messages_requestAppWebView.flags |= 4;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_requestAppWebView, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                BotWebViewAttachedSheet.this.lambda$requestWebView$21(tLObject, tLRPC$TL_error);
            }
        }, 66);
    }

    public class AnonymousClass10 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass10() {
        }

        @Override
        public void onItemClick(int i) {
            if (i == -1) {
                if (BotWebViewAttachedSheet.this.webViewContainer.onBackPressed()) {
                    return;
                }
                BotWebViewAttachedSheet.this.onCheckDismissByUser();
                return;
            }
            if (i == R.id.menu_open_bot) {
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", BotWebViewAttachedSheet.this.botId);
                if (BotWebViewAttachedSheet.this.parentActivity instanceof LaunchActivity) {
                    ((LaunchActivity) BotWebViewAttachedSheet.this.parentActivity).lambda$runLinkRequest$88(new ChatActivity(bundle));
                }
                BotWebViewAttachedSheet.this.dismiss();
                return;
            }
            if (i == R.id.menu_tos_bot) {
                Browser.openUrl(BotWebViewAttachedSheet.this.getContext(), LocaleController.getString(R.string.BotWebViewToSLink));
                return;
            }
            if (i == R.id.menu_reload_page) {
                if (BotWebViewAttachedSheet.this.webViewContainer.getWebView() != null) {
                    BotWebViewAttachedSheet.this.webViewContainer.getWebView().animate().cancel();
                    BotWebViewAttachedSheet.this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                }
                BotWebViewAttachedSheet.this.progressView.setLoadProgress(0.0f);
                BotWebViewAttachedSheet.this.progressView.setAlpha(1.0f);
                BotWebViewAttachedSheet.this.progressView.setVisibility(0);
                BotWebViewAttachedSheet.this.webViewContainer.setBotUser(MessagesController.getInstance(BotWebViewAttachedSheet.this.currentAccount).getUser(Long.valueOf(BotWebViewAttachedSheet.this.botId)));
                BotWebViewAttachedSheet.this.webViewContainer.loadFlickerAndSettingsItem(BotWebViewAttachedSheet.this.currentAccount, BotWebViewAttachedSheet.this.botId, BotWebViewAttachedSheet.this.settingsItem);
                BotWebViewAttachedSheet.this.webViewContainer.reload();
                return;
            }
            if (i == R.id.menu_settings) {
                BotWebViewAttachedSheet.this.webViewContainer.onSettingsButtonPressed();
                return;
            }
            if (i == R.id.menu_delete_bot) {
                BotWebViewAttachedSheet.deleteBot(BotWebViewAttachedSheet.this.currentAccount, BotWebViewAttachedSheet.this.botId, new Runnable() {
                    @Override
                    public final void run() {
                        BotWebViewAttachedSheet.AnonymousClass10.this.lambda$onItemClick$0();
                    }
                });
                return;
            }
            if (i == R.id.menu_add_to_home_screen_bot) {
                MediaDataController.getInstance(BotWebViewAttachedSheet.this.currentAccount).installShortcut(BotWebViewAttachedSheet.this.botId, MediaDataController.SHORTCUT_TYPE_ATTACHED_BOT);
            } else if (i == R.id.menu_collapse_bot) {
                BotWebViewAttachedSheet.this.forceExpnaded = true;
                BotWebViewAttachedSheet.this.dismiss(true, null);
            }
        }

        public void lambda$onItemClick$0() {
            BotWebViewAttachedSheet.this.dismiss();
        }
    }

    public void lambda$requestWebView$15(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$requestWebView$14(tLRPC$TL_error, tLObject);
            }
        });
    }

    public void lambda$requestWebView$14(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tLRPC$TL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse(false);
        }
    }

    public void lambda$requestWebView$17(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$requestWebView$16(tLRPC$TL_error, tLObject);
            }
        });
    }

    public void lambda$requestWebView$16(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tLRPC$TL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse(false);
        }
    }

    public void lambda$requestWebView$19(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$requestWebView$18(tLRPC$TL_error, tLObject);
            }
        });
    }

    public void lambda$requestWebView$18(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tLRPC$TL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse(false);
        }
    }

    public void lambda$requestWebView$21(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$requestWebView$20(tLRPC$TL_error, tLObject);
            }
        });
    }

    public void lambda$requestWebView$20(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        WebViewRequestProps webViewRequestProps;
        if (tLRPC$TL_error == null && (webViewRequestProps = this.requestProps) != null) {
            webViewRequestProps.applyResponse(tLObject);
            loadFromResponse(false);
        }
    }

    private void loadFromResponse(boolean z) {
        if (this.requestProps == null) {
            return;
        }
        long max = Math.max(0L, 60000 - (System.currentTimeMillis() - this.requestProps.responseTime));
        String str = null;
        TLObject tLObject = this.requestProps.response;
        if (tLObject instanceof TLRPC$TL_appWebViewResultUrl) {
            this.queryId = 0L;
            str = ((TLRPC$TL_appWebViewResultUrl) tLObject).url;
        } else if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
            str = tLRPC$TL_webViewResultUrl.url;
        } else if (tLObject instanceof TLRPC$TL_simpleWebViewResultUrl) {
            this.queryId = 0L;
            str = ((TLRPC$TL_simpleWebViewResultUrl) tLObject).url;
        }
        if (str != null && !z) {
            this.webViewContainer.loadUrl(this.currentAccount, str);
        }
        AndroidUtilities.runOnUIThread(this.pollRunnable, max);
    }

    private void preloadShortcutBotIcon(TLRPC$User tLRPC$User, TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        if (tLRPC$TL_attachMenuBot == null || !tLRPC$TL_attachMenuBot.show_in_side_menu || MediaDataController.getInstance(this.currentAccount).isShortcutAdded(this.botId, MediaDataController.SHORTCUT_TYPE_ATTACHED_BOT)) {
            return;
        }
        if (tLRPC$User == null) {
            tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
        }
        if (tLRPC$User == null || tLRPC$User.photo == null || FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$User.photo.photo_small, true).exists()) {
            return;
        }
        MediaDataController.getInstance(this.currentAccount).preloadImage(ImageLocation.getForUser(tLRPC$User, 1), 0);
    }

    public static void deleteBot(final int i, final long j, final Runnable runnable) {
        final TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot;
        Iterator<TLRPC$TL_attachMenuBot> it = MediaDataController.getInstance(i).getAttachMenuBots().bots.iterator();
        while (true) {
            if (!it.hasNext()) {
                tLRPC$TL_attachMenuBot = null;
                break;
            }
            TLRPC$TL_attachMenuBot next = it.next();
            if (next.bot_id == j) {
                tLRPC$TL_attachMenuBot = next;
                break;
            }
        }
        if (tLRPC$TL_attachMenuBot == null) {
            return;
        }
        new AlertDialog.Builder(LaunchActivity.getLastFragment().getContext()).setTitle(LocaleController.getString(R.string.BotRemoveFromMenuTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BotRemoveFromMenu", R.string.BotRemoveFromMenu, tLRPC$TL_attachMenuBot.short_name))).setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                BotWebViewAttachedSheet.lambda$deleteBot$24(i, j, tLRPC$TL_attachMenuBot, runnable, dialogInterface, i2);
            }
        }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).show();
    }

    public static void lambda$deleteBot$24(final int i, long j, TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, Runnable runnable, DialogInterface dialogInterface, int i2) {
        TLRPC$TL_messages_toggleBotInAttachMenu tLRPC$TL_messages_toggleBotInAttachMenu = new TLRPC$TL_messages_toggleBotInAttachMenu();
        tLRPC$TL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(i).getInputUser(j);
        tLRPC$TL_messages_toggleBotInAttachMenu.enabled = false;
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                BotWebViewAttachedSheet.lambda$deleteBot$23(i, tLObject, tLRPC$TL_error);
            }
        }, 66);
        tLRPC$TL_attachMenuBot.show_in_side_menu = false;
        NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.attachMenuBotsDidLoad, new Object[0]);
        MediaDataController.getInstance(i).uninstallShortcut(j, MediaDataController.SHORTCUT_TYPE_ATTACHED_BOT);
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$deleteBot$23(final int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.lambda$deleteBot$22(i);
            }
        });
    }

    public static void lambda$deleteBot$22(int i) {
        MediaDataController.getInstance(i).loadAttachMenuBots(false, true);
    }

    public int getColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void show() {
        show(false);
    }

    public void show(boolean z) {
        if (AndroidUtilities.isSafeToShow(getContext())) {
            this.windowView.setAlpha(0.0f);
            this.windowView.addOnLayoutChangeListener(new AnonymousClass11(z));
            attachInternal();
        }
    }

    public class AnonymousClass11 implements View.OnLayoutChangeListener {
        final boolean val$lowBounce;

        AnonymousClass11(boolean z) {
            this.val$lowBounce = z;
        }

        @Override
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            view.removeOnLayoutChangeListener(this);
            BotWebViewAttachedSheet.this.swipeContainer.setSwipeOffsetY(this.val$lowBounce ? BotWebViewAttachedSheet.this.swipeContainer.getHeight() * 0.5f : BotWebViewAttachedSheet.this.swipeContainer.getHeight());
            BotWebViewAttachedSheet.this.windowView.setAlpha(1.0f);
            final AnimationNotificationsLocker animationNotificationsLocker = new AnimationNotificationsLocker();
            animationNotificationsLocker.lock();
            BotWebViewAttachedSheet.this.updateShownAnimated(true);
            BotWebViewAttachedSheet botWebViewAttachedSheet = BotWebViewAttachedSheet.this;
            if (botWebViewAttachedSheet.showExpanded) {
                botWebViewAttachedSheet.swipeContainer.stickTo((-BotWebViewAttachedSheet.this.swipeContainer.getOffsetY()) + BotWebViewAttachedSheet.this.swipeContainer.getTopActionBarOffsetY(), new Runnable() {
                    @Override
                    public final void run() {
                        AnimationNotificationsLocker.this.unlock();
                    }
                });
            } else {
                new SpringAnimation(BotWebViewAttachedSheet.this.swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0.0f).setSpring(new SpringForce(0.0f).setDampingRatio(this.val$lowBounce ? 1.0f : 0.75f).setStiffness(this.val$lowBounce ? 800.0f : 500.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                    @Override
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                        AnimationNotificationsLocker.this.unlock();
                    }
                }).start();
            }
        }
    }

    public void updateShownAnimated(final boolean z) {
        ValueAnimator valueAnimator = this.shownAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float[] fArr = new float[2];
        fArr[0] = this.shown;
        fArr[1] = z ? 1.0f : 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        this.shownAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                BotWebViewAttachedSheet.this.lambda$updateShownAnimated$25(valueAnimator2);
            }
        });
        this.shownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                BotWebViewAttachedSheet.this.shown = z ? 1.0f : 0.0f;
                BotWebViewAttachedSheet.this.checkNavBarColor();
            }
        });
        this.shownAnimator.setDuration(200L);
        this.shownAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.shownAnimator.start();
    }

    public void lambda$updateShownAnimated$25(ValueAnimator valueAnimator) {
        this.shown = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        checkNavBarColor();
    }

    @Override
    public boolean isShown() {
        return !this.dismissed;
    }

    @Override
    public int getNavigationBarColor(int i) {
        return ColorUtils.blendARGB(i, this.navBarColor, this.shown);
    }

    @Override
    public boolean onBackPressed() {
        if (this.webViewContainer.onBackPressed()) {
            return true;
        }
        dismiss(true, null);
        return true;
    }

    @Override
    public void dismiss() {
        dismiss(false, null);
    }

    public boolean onCheckDismissByUser() {
        if (this.needCloseConfirmation) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
            AlertDialog create = new AlertDialog.Builder(getContext()).setTitle(user != null ? ContactsController.formatName(user.first_name, user.last_name) : null).setMessage(LocaleController.getString(R.string.BotWebViewChangesMayNotBeSaved)).setPositiveButton(LocaleController.getString(R.string.BotWebViewCloseAnyway), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    BotWebViewAttachedSheet.this.lambda$onCheckDismissByUser$26(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
            create.show();
            ((TextView) create.getButton(-1)).setTextColor(getColor(Theme.key_text_RedBold));
            return false;
        }
        dismiss();
        return true;
    }

    public void lambda$onCheckDismissByUser$26(DialogInterface dialogInterface, int i) {
        dismiss();
    }

    @Override
    public void setOnDismissListener(Runnable runnable) {
        this.onDismissListener = runnable;
    }

    public void dismiss(boolean z, final Runnable runnable) {
        LaunchActivity launchActivity;
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        updateShownAnimated(false);
        if (z && ((launchActivity = LaunchActivity.instance) == null || launchActivity.getBottomSheetTabsOverlay() == null)) {
            z = false;
        }
        if (z) {
            SpringAnimation springAnimation = this.springAnimation;
            if (springAnimation != null) {
                springAnimation.getSpring().setFinalPosition(0.0f);
                this.springAnimation.start();
            }
            LaunchActivity.instance.getBottomSheetTabsOverlay().dismissSheet(this);
            return;
        }
        this.webViewContainer.destroyWebView();
        this.swipeContainer.stickTo(r3.getHeight() + this.windowView.measureKeyboardHeight(), new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.this.lambda$dismiss$27(runnable);
            }
        });
    }

    public void lambda$dismiss$27(Runnable runnable) {
        release();
        if (runnable != null) {
            runnable.run();
        }
        Runnable runnable2 = this.onDismissListener;
        if (runnable2 != null) {
            runnable2.run();
            this.onDismissListener = null;
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webViewResultSent) {
            if (this.queryId == ((Long) objArr[0]).longValue()) {
                dismiss();
                return;
            }
            return;
        }
        if (i == NotificationCenter.didSetNewTheme) {
            this.windowView.invalidate();
            this.webViewContainer.updateFlickerBackgroundColor(getColor(Theme.key_windowBackgroundWhite));
            updateActionBarColors();
            updateLightStatusBar();
        }
    }

    public static int navigationBarColor(int i) {
        return Theme.adaptHSV(i, 0.35f, -0.1f);
    }

    public void checkNavBarColor() {
        LaunchActivity launchActivity;
        if (!attachedToParent() || (launchActivity = LaunchActivity.instance) == null) {
            return;
        }
        launchActivity.checkSystemBarColors(true, true, true, false);
    }

    public class WindowView extends SizeNotifierFrameLayout {
        private final Path clipPath;
        private boolean drawingFromOverlay;
        private final Paint navbarPaint;
        private final RectF rect;

        public WindowView(Context context) {
            super(context);
            this.navbarPaint = new Paint(1);
            this.rect = new RectF();
            this.clipPath = new Path();
            setWillNotDraw(false);
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            if (this.drawingFromOverlay) {
                return;
            }
            super.dispatchDraw(canvas);
            this.navbarPaint.setColor(BotWebViewAttachedSheet.this.navBarColor);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, getHeight() - getPaddingBottom(), getWidth(), getHeight() + AndroidUtilities.navigationBarHeight);
            canvas.drawRect(rectF, this.navbarPaint);
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (this.drawingFromOverlay) {
                return false;
            }
            return super.drawChild(canvas, view, j);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.drawingFromOverlay) {
                return;
            }
            super.onDraw(canvas);
            if (!BotWebViewAttachedSheet.this.overrideBackgroundColor) {
                BotWebViewAttachedSheet.this.backgroundPaint.setColor(BotWebViewAttachedSheet.this.getColor(Theme.key_windowBackgroundWhite));
            }
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, getWidth(), getHeight());
            canvas.drawRect(rectF, BotWebViewAttachedSheet.this.dimPaint);
            BotWebViewAttachedSheet.this.actionBarPaint.setColor(BotWebViewAttachedSheet.this.actionBarColor);
            float dp = AndroidUtilities.dp(16.0f) * (AndroidUtilities.isTablet() ? 1.0f : 1.0f - BotWebViewAttachedSheet.this.actionBarTransitionProgress);
            rectF.set(BotWebViewAttachedSheet.this.swipeContainer.getLeft(), AndroidUtilities.lerp(BotWebViewAttachedSheet.this.swipeContainer.getTranslationY(), 0.0f, BotWebViewAttachedSheet.this.actionBarTransitionProgress), BotWebViewAttachedSheet.this.swipeContainer.getRight(), BotWebViewAttachedSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f) + dp);
            canvas.drawRoundRect(rectF, dp, dp, BotWebViewAttachedSheet.this.actionBarPaint);
            rectF.set(BotWebViewAttachedSheet.this.swipeContainer.getLeft(), BotWebViewAttachedSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), BotWebViewAttachedSheet.this.swipeContainer.getRight(), getHeight());
            canvas.drawRect(rectF, BotWebViewAttachedSheet.this.backgroundPaint);
        }

        @Override
        public void draw(Canvas canvas) {
            if (this.drawingFromOverlay) {
                return;
            }
            super.draw(canvas);
            float f = AndroidUtilities.isTablet() ? 0.0f : BotWebViewAttachedSheet.this.actionBarTransitionProgress;
            BotWebViewAttachedSheet.this.linePaint.setColor(BotWebViewAttachedSheet.this.lineColor);
            BotWebViewAttachedSheet.this.linePaint.setAlpha((int) (BotWebViewAttachedSheet.this.linePaint.getAlpha() * (1.0f - (Math.min(0.5f, f) / 0.5f))));
            canvas.save();
            float f2 = 1.0f - f;
            float containerTop = BotWebViewAttachedSheet.this.getContainerTop();
            canvas.scale(f2, f2, getWidth() / 2.0f, containerTop);
            canvas.drawLine((getWidth() / 2.0f) - AndroidUtilities.dp(16.0f), containerTop, (getWidth() / 2.0f) + AndroidUtilities.dp(16.0f), containerTop, BotWebViewAttachedSheet.this.linePaint);
            canvas.restore();
            BotWebViewAttachedSheet.this.actionBarShadow.setAlpha((int) (BotWebViewAttachedSheet.this.actionBar.getAlpha() * 255.0f));
            float y = BotWebViewAttachedSheet.this.actionBar.getY() + BotWebViewAttachedSheet.this.actionBar.getTranslationY() + BotWebViewAttachedSheet.this.actionBar.getHeight();
            BotWebViewAttachedSheet.this.actionBarShadow.setBounds(0, (int) y, getWidth(), (int) (y + BotWebViewAttachedSheet.this.actionBarShadow.getIntrinsicHeight()));
            BotWebViewAttachedSheet.this.actionBarShadow.draw(canvas);
        }

        @Override
        @SuppressLint({"ClickableViewAccessibility"})
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.drawingFromOverlay) {
                return false;
            }
            if (motionEvent.getAction() == 0 && (motionEvent.getY() <= AndroidUtilities.lerp(BotWebViewAttachedSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), 0.0f, BotWebViewAttachedSheet.this.actionBarTransitionProgress) || motionEvent.getX() > BotWebViewAttachedSheet.this.swipeContainer.getRight() || motionEvent.getX() < BotWebViewAttachedSheet.this.swipeContainer.getLeft())) {
                BotWebViewAttachedSheet.this.dismiss(true, null);
                return true;
            }
            return super.onTouchEvent(motionEvent);
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            Bulletin.addDelegate(this, new Bulletin.Delegate(this) {
                @Override
                public boolean allowLayoutChanges() {
                    return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
                }

                @Override
                public boolean bottomOffsetAnimated() {
                    return Bulletin.Delegate.CC.$default$bottomOffsetAnimated(this);
                }

                @Override
                public boolean clipWithGradient(int i) {
                    return Bulletin.Delegate.CC.$default$clipWithGradient(this, i);
                }

                @Override
                public int getBottomOffset(int i) {
                    return Bulletin.Delegate.CC.$default$getBottomOffset(this, i);
                }

                @Override
                public void onBottomOffsetChange(float f) {
                    Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
                }

                @Override
                public void onHide(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onHide(this, bulletin);
                }

                @Override
                public void onShow(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onShow(this, bulletin);
                }

                @Override
                public int getTopOffset(int i) {
                    return AndroidUtilities.statusBarHeight;
                }
            });
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Bulletin.removeDelegate(this);
        }

        public void setDrawingFromOverlay(boolean z) {
            if (this.drawingFromOverlay != z) {
                this.drawingFromOverlay = z;
                invalidate();
            }
        }

        public float drawInto(Canvas canvas, RectF rectF, float f, RectF rectF2) {
            this.rect.set(BotWebViewAttachedSheet.this.swipeContainer.getLeft(), BotWebViewAttachedSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), BotWebViewAttachedSheet.this.swipeContainer.getRight(), getHeight());
            AndroidUtilities.lerpCentered(this.rect, rectF, f, rectF2);
            canvas.save();
            this.clipPath.rewind();
            float lerp = AndroidUtilities.lerp(AndroidUtilities.dp(16.0f) * (AndroidUtilities.isTablet() ? 1.0f : 1.0f - BotWebViewAttachedSheet.this.actionBarTransitionProgress), AndroidUtilities.dp(10.0f), f);
            this.clipPath.addRoundRect(rectF2, lerp, lerp, Path.Direction.CW);
            canvas.clipPath(this.clipPath);
            canvas.drawPaint(BotWebViewAttachedSheet.this.backgroundPaint);
            if (BotWebViewAttachedSheet.this.swipeContainer != null) {
                canvas.save();
                canvas.translate(BotWebViewAttachedSheet.this.swipeContainer.getX(), Math.max(BotWebViewAttachedSheet.this.swipeContainer.getY(), rectF2.top) + (f * AndroidUtilities.dp(51.0f)));
                BotWebViewAttachedSheet.this.swipeContainer.draw(canvas);
                canvas.restore();
            }
            canvas.restore();
            return lerp;
        }
    }

    public void setBackgroundColor(final int i, boolean z) {
        final int color = this.backgroundPaint.getColor();
        if (z) {
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewAttachedSheet.this.lambda$setBackgroundColor$28(color, i, valueAnimator);
                }
            });
            duration.start();
            return;
        }
        this.backgroundPaint.setColor(i);
        updateActionBarColors();
        this.windowView.invalidate();
    }

    public void lambda$setBackgroundColor$28(int i, int i2, ValueAnimator valueAnimator) {
        this.backgroundPaint.setColor(ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue()));
        updateActionBarColors();
        this.windowView.invalidate();
    }

    public void setActionBarColor(final int i, boolean z, boolean z2) {
        final int i2 = this.actionBarColor;
        final int i3 = this.navBarColor;
        final int navigationBarColor = navigationBarColor(i);
        final BotWebViewMenuContainer.ActionBarColorsAnimating actionBarColorsAnimating = new BotWebViewMenuContainer.ActionBarColorsAnimating();
        actionBarColorsAnimating.setFrom(this.overrideBackgroundColor ? this.actionBarColor : 0, this.resourcesProvider);
        this.overrideBackgroundColor = z;
        this.actionBarIsLight = ColorUtils.calculateLuminance(i) < 0.7210000157356262d;
        actionBarColorsAnimating.setTo(this.overrideBackgroundColor ? i : 0, this.resourcesProvider);
        if (z2) {
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewAttachedSheet.this.lambda$setActionBarColor$29(i2, i, i3, navigationBarColor, actionBarColorsAnimating, valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    BotWebViewAttachedSheet.this.actionBarColor = ColorUtils.blendARGB(i2, i, 1.0f);
                    BotWebViewAttachedSheet.this.navBarColor = ColorUtils.blendARGB(i3, navigationBarColor, 1.0f);
                    BotWebViewAttachedSheet.this.checkNavBarColor();
                    BotWebViewAttachedSheet.this.windowView.invalidate();
                    BotWebViewAttachedSheet.this.actionBar.setBackgroundColor(BotWebViewAttachedSheet.this.actionBarColor);
                    actionBarColorsAnimating.updateActionBar(BotWebViewAttachedSheet.this.actionBar, 1.0f);
                    BotWebViewAttachedSheet.this.lineColor = actionBarColorsAnimating.getColor(Theme.key_sheet_scrollUp);
                    BotWebViewAttachedSheet.this.windowView.invalidate();
                }
            });
            duration.start();
        } else {
            this.actionBarColor = i;
            this.navBarColor = navigationBarColor;
            checkNavBarColor();
            this.windowView.invalidate();
            this.actionBar.setBackgroundColor(this.actionBarColor);
            actionBarColorsAnimating.updateActionBar(this.actionBar, 1.0f);
            this.lineColor = actionBarColorsAnimating.getColor(Theme.key_sheet_scrollUp);
            this.windowView.invalidate();
        }
        updateLightStatusBar();
    }

    public void lambda$setActionBarColor$29(int i, int i2, int i3, int i4, BotWebViewMenuContainer.ActionBarColorsAnimating actionBarColorsAnimating, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.actionBarColor = ColorUtils.blendARGB(i, i2, floatValue);
        this.navBarColor = ColorUtils.blendARGB(i3, i4, floatValue);
        checkNavBarColor();
        this.windowView.invalidate();
        this.actionBar.setBackgroundColor(this.actionBarColor);
        actionBarColorsAnimating.updateActionBar(this.actionBar, floatValue);
        this.lineColor = actionBarColorsAnimating.getColor(Theme.key_sheet_scrollUp);
        this.windowView.invalidate();
    }
}