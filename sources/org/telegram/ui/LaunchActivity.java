package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StatFs;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.arch.core.util.Function;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.common.primitives.Longs;
import j$.util.function.Consumer;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.AutoDeleteMediaTask;
import org.telegram.messenger.BackupAgent;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChannelBoostsController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FingerprintController;
import org.telegram.messenger.FlagSecureReason;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$EmojiStatus;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$LangPackString;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$PaymentForm;
import org.telegram.tgnet.TLRPC$PaymentReceipt;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_resolvedBusinessChatLinks;
import org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode;
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$TL_attachMenuBots;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotsBot;
import org.telegram.tgnet.TLRPC$TL_auth_acceptLoginToken;
import org.telegram.tgnet.TLRPC$TL_auth_sentCode;
import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_channels_getChannels;
import org.telegram.tgnet.TLRPC$TL_channels_getMessages;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$TL_emojiStatusEmpty;
import org.telegram.tgnet.TLRPC$TL_emojiStatusUntil;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_help_appUpdate;
import org.telegram.tgnet.TLRPC$TL_help_deepLinkInfo;
import org.telegram.tgnet.TLRPC$TL_help_getAppUpdate;
import org.telegram.tgnet.TLRPC$TL_help_noAppUpdate;
import org.telegram.tgnet.TLRPC$TL_help_termsOfService;
import org.telegram.tgnet.TLRPC$TL_inputBotAppShortName;
import org.telegram.tgnet.TLRPC$TL_inputChannel;
import org.telegram.tgnet.TLRPC$TL_inputGameShortName;
import org.telegram.tgnet.TLRPC$TL_inputInvoiceSlug;
import org.telegram.tgnet.TLRPC$TL_inputMediaGame;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_langPackLanguage;
import org.telegram.tgnet.TLRPC$TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC$TL_messages_botApp;
import org.telegram.tgnet.TLRPC$TL_messages_chats;
import org.telegram.tgnet.TLRPC$TL_messages_checkHistoryImport;
import org.telegram.tgnet.TLRPC$TL_messages_forumTopics;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachMenuBot;
import org.telegram.tgnet.TLRPC$TL_messages_getBotApp;
import org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage;
import org.telegram.tgnet.TLRPC$TL_messages_historyImportParsed;
import org.telegram.tgnet.TLRPC$TL_messages_toggleBotInAttachMenu;
import org.telegram.tgnet.TLRPC$TL_payments_paymentFormStars;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$ThemeSettings;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.tgnet.TLRPC$WallPaperSettings;
import org.telegram.tgnet.TLRPC$account_Password;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.tgnet.tl.TL_chatlists$TL_chatlists_chatlistInvite;
import org.telegram.tgnet.tl.TL_chatlists$TL_chatlists_chatlistInviteAlready;
import org.telegram.tgnet.tl.TL_chatlists$chatlist_ChatlistInvite;
import org.telegram.tgnet.tl.TL_stories$PeerStories;
import org.telegram.tgnet.tl.TL_stories$StoryItem;
import org.telegram.tgnet.tl.TL_stories$TL_premium_boostsStatus;
import org.telegram.tgnet.tl.TL_stories$TL_stories_getPeerStories;
import org.telegram.tgnet.tl.TL_stories$TL_stories_peerStories;
import org.telegram.tgnet.tl.TL_stories$TL_stories_stories;
import org.telegram.tgnet.tl.TL_stories$TL_storyItemDeleted;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheetTabs;
import org.telegram.ui.ActionBar.BottomSheetTabsOverlay;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DrawerLayoutAdapter;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AttachBotIntroTopView;
import org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticApiModelOutline0;
import org.telegram.ui.Components.BatteryDrawable;
import org.telegram.ui.Components.BlockingUpdateView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.FireworksOverlay;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.FolderBottomSheet;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MediaActivity;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PasscodeViewDialog;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.boosts.BoostPagerBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchTagsList;
import org.telegram.ui.Components.SideMenultItemAnimator;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TermsOfServiceView;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.spoilers.SpoilerEffect2;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.SelectAnimatedEmojiDialog;
import org.telegram.ui.Stars.ISuperRipple;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.SuperRipple;
import org.telegram.ui.Stars.SuperRippleFallback;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.StoryViewer;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.WallpapersListActivity;
import org.telegram.ui.bots.BotWebViewAttachedSheet;
import org.telegram.ui.bots.BotWebViewSheet;
import org.telegram.ui.bots.WebViewRequestProps;
import org.webrtc.voiceengine.WebRtcAudioTrack;

public class LaunchActivity extends BasePermissionsActivity implements INavigationLayout.INavigationLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate {
    public static LaunchActivity instance;
    public static boolean isActive;
    public static boolean isResumed;
    public static Runnable onResumeStaticCallback;
    private static LaunchActivity staticInstanceForAlerts;
    public static boolean systemBlurEnabled;
    public static Runnable whenResumed;
    private ActionBarLayout actionBarLayout;
    private long alreadyShownFreeDiscSpaceAlertForced;
    private SizeNotifierFrameLayout backgroundTablet;
    private BlockingUpdateView blockingUpdateView;
    private BottomSheetTabsOverlay bottomSheetTabsOverlay;
    private boolean checkFreeDiscSpaceShown;
    private ArrayList<TLRPC$User> contactsToSend;
    private Uri contactsToSendUri;
    private int currentConnectionState;
    private ISuperRipple currentRipple;
    private View customNavigationBar;
    private String documentsMimeType;
    private ArrayList<String> documentsOriginalPathsArray;
    private ArrayList<String> documentsPathsArray;
    private ArrayList<Uri> documentsUrisArray;
    private DrawerLayoutAdapter drawerLayoutAdapter;
    public DrawerLayoutContainer drawerLayoutContainer;
    private HashMap<String, String> englishLocaleStrings;
    private Uri exportingChatUri;
    View feedbackView;
    private boolean finished;
    private FireworksOverlay fireworksOverlay;
    private FlagSecureReason flagSecureReason;
    public FrameLayout frameLayout;
    private ArrayList<Parcelable> importingStickers;
    private ArrayList<String> importingStickersEmoji;
    private String importingStickersSoftware;
    private SideMenultItemAnimator itemAnimator;
    private RelativeLayout launchLayout;
    private ActionBarLayout layersActionBarLayout;
    private boolean loadingLocaleDialog;
    private TLRPC$TL_theme loadingTheme;
    private boolean loadingThemeAccent;
    private String loadingThemeFileName;
    private Theme.ThemeInfo loadingThemeInfo;
    private AlertDialog loadingThemeProgressDialog;
    private TLRPC$TL_wallPaper loadingThemeWallpaper;
    private String loadingThemeWallpaperName;
    private Dialog localeDialog;
    private Runnable lockRunnable;
    private ValueAnimator navBarAnimator;
    private boolean navigateToPremiumBot;
    private Runnable navigateToPremiumGiftCallback;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private PasscodeViewDialog passcodeDialog;
    private Intent passcodeSaveIntent;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private ArrayList<SendMessagesHelper.SendingMediaInfo> photoPathsArray;
    private Dialog proxyErrorDialog;
    private ActionBarLayout rightActionBarLayout;
    private View rippleAbove;
    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
    private String sendingText;
    private FrameLayout shadowTablet;
    private FrameLayout shadowTabletSide;
    private RecyclerListView sideMenu;
    private FrameLayout sideMenuContainer;
    private boolean switchingAccount;
    private HashMap<String, String> systemLocaleStrings;
    private boolean tabletFullSize;
    private int[] tempLocation;
    private TermsOfServiceView termsOfServiceView;
    private ImageView themeSwitchImageView;
    private RLottieDrawable themeSwitchSunDrawable;
    private View themeSwitchSunView;
    private IUpdateLayout updateLayout;
    private String videoPath;
    private ActionMode visibleActionMode;
    private String voicePath;
    private boolean wasMutedByAdminRaisedHand;
    private Utilities.Callback<Boolean> webviewShareAPIDoneListener;
    public static final Pattern PREFIX_T_ME_PATTERN = Pattern.compile("^(?:http(?:s|)://|)([A-z0-9-]+?)\\.t\\.me");
    private static final ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    private static final ArrayList<BaseFragment> layerFragmentsStack = new ArrayList<>();
    private static final ArrayList<BaseFragment> rightFragmentsStack = new ArrayList<>();
    public ArrayList<INavigationLayout> sheetFragmentsStack = new ArrayList<>();
    private List<PasscodeView> overlayPasscodeViews = new ArrayList();
    public final ArrayList<Dialog> visibleDialogs = new ArrayList<>();
    private boolean isNavigationBarColorFrozen = false;
    private List<Runnable> onUserLeaveHintListeners = new ArrayList();
    private SparseIntArray requestedPermissions = new SparseIntArray();
    private int requsetPermissionsPointer = 5934;
    private Consumer<Boolean> blurListener = new Consumer<Boolean>() {
        @Override
        public Consumer<Boolean> andThen(Consumer<? super Boolean> consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        @Override
        public void r(Boolean bool) {
            LaunchActivity.systemBlurEnabled = bool.booleanValue();
        }
    };

    public static void lambda$setupActionBarLayout$11(View view) {
    }

    @Override
    public boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, INavigationLayout iNavigationLayout) {
        return INavigationLayout.INavigationLayoutDelegate.CC.$default$needPresentFragment(this, baseFragment, z, z2, iNavigationLayout);
    }

    @Override
    public void onMeasureOverride(int[] iArr) {
        INavigationLayout.INavigationLayoutDelegate.CC.$default$onMeasureOverride(this, iArr);
    }

    public Dialog getVisibleDialog() {
        for (int size = this.visibleDialogs.size() - 1; size >= 0; size--) {
            Dialog dialog = this.visibleDialogs.get(size);
            if (dialog.isShowing()) {
                return dialog;
            }
        }
        return null;
    }

    @Override
    public void onCreate(Bundle bundle) {
        boolean z;
        boolean isBackgroundRestricted;
        ActionBarLayout actionBarLayout;
        Intent intent;
        Uri data;
        int i = 0;
        isActive = true;
        if (BuildVars.DEBUG_VERSION) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy()).detectLeakedClosableObjects().penaltyLog().build());
        }
        instance = this;
        ApplicationLoader.postInitApplication();
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        int i2 = UserConfig.selectedAccount;
        this.currentAccount = i2;
        if (!UserConfig.getInstance(i2).isClientActivated() && (intent = getIntent()) != null && intent.getAction() != null) {
            if ("android.intent.action.SEND".equals(intent.getAction()) || "android.intent.action.SEND_MULTIPLE".equals(intent.getAction())) {
                super.onCreate(bundle);
                finish();
                return;
            } else if ("android.intent.action.VIEW".equals(intent.getAction()) && (data = intent.getData()) != null) {
                String lowerCase = data.toString().toLowerCase();
                if (!lowerCase.startsWith("tg:proxy") && !lowerCase.startsWith("tg://proxy") && !lowerCase.startsWith("tg:socks")) {
                    lowerCase.startsWith("tg://socks");
                }
            }
        }
        requestWindowFeature(1);
        setTheme(R.style.Theme_TMessages);
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                LaunchActivity$$ExternalSyntheticApiModelOutline0.m(this, new ActivityManager.TaskDescription((String) null, (Bitmap) null, Theme.getColor(Theme.key_actionBarDefault) | (-16777216)));
            } catch (Throwable unused) {
            }
            try {
                getWindow().setNavigationBarColor(-16777216);
            } catch (Throwable unused2) {
            }
        }
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        FlagSecureReason flagSecureReason = new FlagSecureReason(getWindow(), new FlagSecureReason.FlagSecureCondition() {
            @Override
            public final boolean run() {
                boolean lambda$onCreate$0;
                lambda$onCreate$0 = LaunchActivity.lambda$onCreate$0();
                return lambda$onCreate$0;
            }
        });
        this.flagSecureReason = flagSecureReason;
        flagSecureReason.attach();
        super.onCreate(bundle);
        int i3 = Build.VERSION.SDK_INT;
        if (i3 >= 24) {
            AndroidUtilities.isInMultiwindow = LaunchActivity$$ExternalSyntheticApiModelOutline3.m(this);
        }
        Theme.createCommonChatResources();
        Theme.createDialogsResources(this);
        if (SharedConfig.passcodeHash.length() != 0 && SharedConfig.appLocked) {
            SharedConfig.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
        }
        AndroidUtilities.fillStatusBarHeight(this, false);
        this.actionBarLayout = new ActionBarLayout(this, true);
        FrameLayout frameLayout = new FrameLayout(this) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                LaunchActivity.this.drawRippleAbove(canvas, this);
            }
        };
        this.frameLayout = frameLayout;
        frameLayout.setClipToPadding(false);
        this.frameLayout.setClipChildren(false);
        char c = 65535;
        setContentView(this.frameLayout, new ViewGroup.LayoutParams(-1, -1));
        if (i3 >= 21) {
            ImageView imageView = new ImageView(this);
            this.themeSwitchImageView = imageView;
            imageView.setVisibility(8);
        }
        AnonymousClass3 anonymousClass3 = new AnonymousClass3(this);
        this.drawerLayoutContainer = anonymousClass3;
        anonymousClass3.setClipChildren(false);
        this.drawerLayoutContainer.setClipToPadding(false);
        this.drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.frameLayout.addView(this.drawerLayoutContainer, LayoutHelper.createFrame(-1, -1.0f));
        if (i3 >= 21) {
            View view = new View(this) {
                @Override
                protected void onDraw(Canvas canvas) {
                    if (LaunchActivity.this.themeSwitchSunDrawable != null) {
                        LaunchActivity.this.themeSwitchSunDrawable.draw(canvas);
                        invalidate();
                    }
                }
            };
            this.themeSwitchSunView = view;
            this.frameLayout.addView(view, LayoutHelper.createFrame(48, 48.0f));
            this.themeSwitchSunView.setVisibility(8);
        }
        FrameLayout frameLayout2 = this.frameLayout;
        BottomSheetTabsOverlay bottomSheetTabsOverlay = new BottomSheetTabsOverlay(this);
        this.bottomSheetTabsOverlay = bottomSheetTabsOverlay;
        frameLayout2.addView(bottomSheetTabsOverlay);
        FrameLayout frameLayout3 = this.frameLayout;
        FireworksOverlay fireworksOverlay = new FireworksOverlay(this) {
            {
                setVisibility(8);
            }

            @Override
            public void start(boolean z2) {
                setVisibility(0);
                super.start(z2);
            }

            @Override
            protected void onStop() {
                super.onStop();
                setVisibility(8);
            }
        };
        this.fireworksOverlay = fireworksOverlay;
        frameLayout3.addView(fireworksOverlay);
        setupActionBarLayout();
        this.sideMenuContainer = new FrameLayout(this);
        RecyclerListView recyclerListView = new RecyclerListView(this) {
            @Override
            public boolean drawChild(Canvas canvas, View view2, long j) {
                int i4;
                if (LaunchActivity.this.itemAnimator != null && LaunchActivity.this.itemAnimator.isRunning() && LaunchActivity.this.itemAnimator.isAnimatingChild(view2)) {
                    i4 = canvas.save();
                    canvas.clipRect(0, LaunchActivity.this.itemAnimator.getAnimationClipTop(), getMeasuredWidth(), getMeasuredHeight());
                } else {
                    i4 = -1;
                }
                boolean drawChild = super.drawChild(canvas, view2, j);
                if (i4 >= 0) {
                    canvas.restoreToCount(i4);
                    invalidate();
                    invalidateViews();
                }
                return drawChild;
            }
        };
        this.sideMenu = recyclerListView;
        SideMenultItemAnimator sideMenultItemAnimator = new SideMenultItemAnimator(recyclerListView);
        this.itemAnimator = sideMenultItemAnimator;
        this.sideMenu.setItemAnimator(sideMenultItemAnimator);
        RecyclerListView recyclerListView2 = this.sideMenu;
        int i4 = Theme.key_chats_menuBackground;
        recyclerListView2.setBackgroundColor(Theme.getColor(i4));
        this.sideMenuContainer.setBackgroundColor(Theme.getColor(i4));
        this.sideMenu.setLayoutManager(new LinearLayoutManager(this, 1, false));
        this.sideMenu.setAllowItemsInteractionDuringAnimation(false);
        RecyclerListView recyclerListView3 = this.sideMenu;
        DrawerLayoutAdapter drawerLayoutAdapter = new DrawerLayoutAdapter(this, this.itemAnimator, this.drawerLayoutContainer);
        this.drawerLayoutAdapter = drawerLayoutAdapter;
        recyclerListView3.setAdapter(drawerLayoutAdapter);
        this.drawerLayoutAdapter.setOnPremiumDrawableClick(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                LaunchActivity.this.lambda$onCreate$1(view2);
            }
        });
        this.sideMenuContainer.addView(this.sideMenu, LayoutHelper.createFrame(-1, -1.0f));
        this.drawerLayoutContainer.setDrawerLayout(this.sideMenuContainer, this.sideMenu);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.sideMenuContainer.getLayoutParams();
        Point realScreenSize = AndroidUtilities.getRealScreenSize();
        layoutParams.width = AndroidUtilities.isTablet() ? AndroidUtilities.dp(320.0f) : Math.min(AndroidUtilities.dp(320.0f), Math.min(realScreenSize.x, realScreenSize.y) - AndroidUtilities.dp(56.0f));
        layoutParams.height = -1;
        this.sideMenuContainer.setLayoutParams(layoutParams);
        this.sideMenu.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view2, int i5) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view2, i5);
            }

            @Override
            public void onDoubleTap(View view2, int i5, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view2, i5, f, f2);
            }

            @Override
            public final void onItemClick(View view2, int i5, float f, float f2) {
                LaunchActivity.this.lambda$onCreate$6(view2, i5, f, f2);
            }
        });
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(3, i) {
            private RecyclerView.ViewHolder selectedViewHolder;

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i5) {
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
                if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                    return false;
                }
                LaunchActivity.this.drawerLayoutAdapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
                return true;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i5) {
                clearSelectedViewHolder();
                if (i5 != 0) {
                    this.selectedViewHolder = viewHolder;
                    View view2 = viewHolder.itemView;
                    LaunchActivity.this.sideMenu.cancelClickRunnables(false);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
                    if (Build.VERSION.SDK_INT >= 21) {
                        ObjectAnimator.ofFloat(view2, "elevation", AndroidUtilities.dp(1.0f)).setDuration(150L).start();
                    }
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                clearSelectedViewHolder();
            }

            private void clearSelectedViewHolder() {
                RecyclerView.ViewHolder viewHolder = this.selectedViewHolder;
                if (viewHolder != null) {
                    final View view2 = viewHolder.itemView;
                    this.selectedViewHolder = null;
                    view2.setTranslationX(0.0f);
                    view2.setTranslationY(0.0f);
                    if (Build.VERSION.SDK_INT >= 21) {
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view2, "elevation", 0.0f);
                        ofFloat.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                view2.setBackground(null);
                            }
                        });
                        ofFloat.setDuration(150L).start();
                    }
                }
            }

            @Override
            public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i5, boolean z2) {
                View view2;
                View view3;
                View view4 = viewHolder.itemView;
                if (LaunchActivity.this.drawerLayoutAdapter.isAccountsShown()) {
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = recyclerView.findViewHolderForAdapterPosition(LaunchActivity.this.drawerLayoutAdapter.getFirstAccountPosition() - 1);
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = recyclerView.findViewHolderForAdapterPosition(LaunchActivity.this.drawerLayoutAdapter.getLastAccountPosition() + 1);
                    if ((findViewHolderForAdapterPosition != null && (view3 = findViewHolderForAdapterPosition.itemView) != null && view3.getBottom() == view4.getTop() && f2 < 0.0f) || (findViewHolderForAdapterPosition2 != null && (view2 = findViewHolderForAdapterPosition2.itemView) != null && view2.getTop() == view4.getBottom() && f2 > 0.0f)) {
                        f2 = 0.0f;
                    }
                }
                view4.setTranslationX(f);
                view4.setTranslationY(f2);
            }
        });
        itemTouchHelper.attachToRecyclerView(this.sideMenu);
        this.sideMenu.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view2, int i5) {
                boolean lambda$onCreate$7;
                lambda$onCreate$7 = LaunchActivity.this.lambda$onCreate$7(itemTouchHelper, view2, i5);
                return lambda$onCreate$7;
            }
        });
        this.updateLayout = ApplicationLoader.applicationLoaderInstance.takeUpdateLayout(this, this.sideMenu, this.sideMenuContainer);
        this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
        this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        this.actionBarLayout.setFragmentStack(mainFragmentsStack);
        this.actionBarLayout.setFragmentStackChangedListener(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$onCreate$8();
            }
        });
        this.actionBarLayout.setDelegate(this);
        Theme.loadWallpaper(true);
        checkCurrentAccount();
        updateCurrentConnectionState(this.currentAccount);
        NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
        int i5 = NotificationCenter.closeOtherAppActivities;
        globalInstance.lambda$postNotificationNameOnUIThread$1(i5, this);
        this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reloadInterface);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needCheckSystemBarColors);
        NotificationCenter.getGlobalInstance().addObserver(this, i5);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.notificationsCountUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.screenStateChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.showBulletin);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.appUpdateAvailable);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.requestPermissions);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.billingConfirmPurchaseError);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        LiteMode.addOnPowerSaverAppliedListener(new LaunchActivity$$ExternalSyntheticLambda21(this));
        if (this.actionBarLayout.getFragmentStack().isEmpty() && ((actionBarLayout = this.layersActionBarLayout) == null || actionBarLayout.getFragmentStack().isEmpty())) {
            if (!UserConfig.getInstance(this.currentAccount).isClientActivated()) {
                this.actionBarLayout.addFragmentToStack(getClientNotActivatedFragment());
                this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            } else {
                DialogsActivity dialogsActivity = new DialogsActivity(null);
                dialogsActivity.setSideMenu(this.sideMenu);
                this.actionBarLayout.addFragmentToStack(dialogsActivity);
                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            }
            if (bundle != null) {
                try {
                    String string = bundle.getString("fragment");
                    if (string != null) {
                        Bundle bundle2 = bundle.getBundle("args");
                        switch (string.hashCode()) {
                            case -1529105743:
                                if (string.equals("wallpapers")) {
                                    c = 5;
                                    break;
                                }
                                break;
                            case -1349522494:
                                if (string.equals("chat_profile")) {
                                    c = 4;
                                    break;
                                }
                                break;
                            case 3052376:
                                if (string.equals("chat")) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case 98629247:
                                if (string.equals("group")) {
                                    c = 2;
                                    break;
                                }
                                break;
                            case 738950403:
                                if (string.equals("channel")) {
                                    c = 3;
                                    break;
                                }
                                break;
                            case 1434631203:
                                if (string.equals("settings")) {
                                    c = 1;
                                    break;
                                }
                                break;
                        }
                        if (c != 0) {
                            if (c == 1) {
                                bundle2.putLong("user_id", UserConfig.getInstance(this.currentAccount).clientUserId);
                                ProfileActivity profileActivity = new ProfileActivity(bundle2);
                                this.actionBarLayout.addFragmentToStack(profileActivity);
                                profileActivity.restoreSelfArgs(bundle);
                            } else if (c != 2) {
                                if (c != 3) {
                                    if (c != 4) {
                                        if (c == 5) {
                                            WallpapersListActivity wallpapersListActivity = new WallpapersListActivity(0);
                                            this.actionBarLayout.addFragmentToStack(wallpapersListActivity);
                                            wallpapersListActivity.restoreSelfArgs(bundle);
                                        }
                                    } else if (bundle2 != null) {
                                        ProfileActivity profileActivity2 = new ProfileActivity(bundle2);
                                        if (this.actionBarLayout.addFragmentToStack(profileActivity2)) {
                                            profileActivity2.restoreSelfArgs(bundle);
                                        }
                                    }
                                } else if (bundle2 != null) {
                                    ChannelCreateActivity channelCreateActivity = new ChannelCreateActivity(bundle2);
                                    if (this.actionBarLayout.addFragmentToStack(channelCreateActivity)) {
                                        channelCreateActivity.restoreSelfArgs(bundle);
                                    }
                                }
                            } else if (bundle2 != null) {
                                GroupCreateFinalActivity groupCreateFinalActivity = new GroupCreateFinalActivity(bundle2);
                                if (this.actionBarLayout.addFragmentToStack(groupCreateFinalActivity)) {
                                    groupCreateFinalActivity.restoreSelfArgs(bundle);
                                }
                            }
                        } else if (bundle2 != null) {
                            ChatActivity chatActivity = new ChatActivity(bundle2);
                            if (this.actionBarLayout.addFragmentToStack(chatActivity)) {
                                chatActivity.restoreSelfArgs(bundle);
                            }
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } else {
            BaseFragment baseFragment = (this.actionBarLayout.getFragmentStack().size() > 0 ? this.actionBarLayout : this.layersActionBarLayout).getFragmentStack().get(0);
            if (baseFragment instanceof DialogsActivity) {
                ((DialogsActivity) baseFragment).setSideMenu(this.sideMenu);
            }
            if (AndroidUtilities.isTablet()) {
                z = this.actionBarLayout.getFragmentStack().size() <= 1 && this.layersActionBarLayout.getFragmentStack().isEmpty();
                if (this.layersActionBarLayout.getFragmentStack().size() == 1 && ((this.layersActionBarLayout.getFragmentStack().get(0) instanceof LoginActivity) || (this.layersActionBarLayout.getFragmentStack().get(0) instanceof IntroActivity))) {
                    z = false;
                }
            } else {
                z = true;
            }
            if (this.actionBarLayout.getFragmentStack().size() == 1 && ((this.actionBarLayout.getFragmentStack().get(0) instanceof LoginActivity) || (this.actionBarLayout.getFragmentStack().get(0) instanceof IntroActivity))) {
                z = false;
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(z, false);
        }
        checkLayout();
        checkSystemBarColors();
        handleIntent(getIntent(), false, bundle != null, false, null, true, true);
        try {
            String str = Build.DISPLAY;
            String str2 = Build.USER;
            String lowerCase2 = str != null ? str.toLowerCase() : "";
            String lowerCase3 = str2 != null ? lowerCase2.toLowerCase() : "";
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("OS name " + lowerCase2 + " " + lowerCase3);
            }
            if ((lowerCase2.contains("flyme") || lowerCase3.contains("flyme")) && Build.VERSION.SDK_INT <= 24) {
                AndroidUtilities.incorrectDisplaySizeFix = true;
                final View rootView = getWindow().getDecorView().getRootView();
                ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
                ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public final void onGlobalLayout() {
                        LaunchActivity.lambda$onCreate$9(rootView);
                    }
                };
                this.onGlobalLayoutListener = onGlobalLayoutListener;
                viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        MediaController.getInstance().setBaseActivity(this, true);
        ApplicationLoader.startAppCenter(this);
        IUpdateLayout iUpdateLayout = this.updateLayout;
        if (iUpdateLayout != null) {
            iUpdateLayout.updateAppUpdateViews(this.currentAccount, false);
        }
        int i6 = Build.VERSION.SDK_INT;
        if (i6 >= 23) {
            FingerprintController.checkKeyReady();
        }
        if (i6 >= 28) {
            isBackgroundRestricted = ((ActivityManager) getSystemService("activity")).isBackgroundRestricted();
            if (isBackgroundRestricted && System.currentTimeMillis() - SharedConfig.BackgroundActivityPrefs.getLastCheckedBackgroundActivity() >= 86400000 && SharedConfig.BackgroundActivityPrefs.getDismissedCount() < 3) {
                AlertsCreator.createBackgroundActivityDialog(this).show();
                SharedConfig.BackgroundActivityPrefs.setLastCheckedBackgroundActivity(System.currentTimeMillis());
            }
        }
        if (i6 >= 31) {
            getWindow().getDecorView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view2) {
                    LaunchActivity.this.getWindowManager().addCrossWindowBlurEnabledListener(Consumer.Wrapper.convert(LaunchActivity.this.blurListener));
                }

                @Override
                public void onViewDetachedFromWindow(View view2) {
                    LaunchActivity.this.getWindowManager().removeCrossWindowBlurEnabledListener(Consumer.Wrapper.convert(LaunchActivity.this.blurListener));
                }
            });
        }
        BackupAgent.requestBackup(this);
        RestrictedLanguagesSelectActivity.checkRestrictedLanguages(false);
    }

    public static boolean lambda$onCreate$0() {
        return SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture;
    }

    public class AnonymousClass3 extends DrawerLayoutContainer {
        private boolean wasPortrait;

        AnonymousClass3(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            setDrawerPosition(getDrawerPosition());
            boolean z2 = i4 - i2 > i3 - i;
            if (z2 != this.wasPortrait) {
                post(new Runnable() {
                    @Override
                    public final void run() {
                        LaunchActivity.AnonymousClass3.this.lambda$onLayout$0();
                    }
                });
                this.wasPortrait = z2;
            }
        }

        public void lambda$onLayout$0() {
            if (LaunchActivity.this.selectAnimatedEmojiDialog != null) {
                LaunchActivity.this.selectAnimatedEmojiDialog.dismiss();
                LaunchActivity.this.selectAnimatedEmojiDialog = null;
            }
        }

        @Override
        public void closeDrawer() {
            super.closeDrawer();
            if (LaunchActivity.this.selectAnimatedEmojiDialog != null) {
                LaunchActivity.this.selectAnimatedEmojiDialog.dismiss();
                LaunchActivity.this.selectAnimatedEmojiDialog = null;
            }
        }

        @Override
        public void closeDrawer(boolean z) {
            super.closeDrawer(z);
            if (LaunchActivity.this.selectAnimatedEmojiDialog != null) {
                LaunchActivity.this.selectAnimatedEmojiDialog.dismiss();
                LaunchActivity.this.selectAnimatedEmojiDialog = null;
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (LaunchActivity.this.actionBarLayout.getParent() == this) {
                LaunchActivity.this.actionBarLayout.parentDraw(this, canvas);
            }
            super.dispatchDraw(canvas);
        }
    }

    public void lambda$onCreate$1(View view) {
        showSelectStatusDialog();
    }

    public void lambda$onCreate$6(View view, int i, float f, float f2) {
        if (this.drawerLayoutAdapter.click(view, i)) {
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        boolean z = true;
        if (i == 0) {
            DrawerProfileCell drawerProfileCell = (DrawerProfileCell) view;
            if (drawerProfileCell.isInAvatar(f, f2)) {
                openSettings(drawerProfileCell.hasAvatar());
                return;
            } else {
                this.drawerLayoutAdapter.setAccountsShown(!r7.isAccountsShown(), true);
                return;
            }
        }
        if (view instanceof DrawerUserCell) {
            switchToAccount(((DrawerUserCell) view).getAccountNumber(), true);
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        Integer num = null;
        if (view instanceof DrawerAddCell) {
            int i2 = 0;
            for (int i3 = 3; i3 >= 0; i3--) {
                if (!UserConfig.getInstance(i3).isClientActivated()) {
                    i2++;
                    if (num == null) {
                        num = Integer.valueOf(i3);
                    }
                }
            }
            if (!UserConfig.hasPremiumOnAccounts()) {
                i2--;
            }
            if (i2 > 0 && num != null) {
                lambda$runLinkRequest$91(new LoginActivity(num.intValue()));
                this.drawerLayoutContainer.closeDrawer(false);
                return;
            } else {
                if (UserConfig.hasPremiumOnAccounts() || this.actionBarLayout.getFragmentStack().size() <= 0) {
                    return;
                }
                BaseFragment baseFragment = this.actionBarLayout.getFragmentStack().get(0);
                LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(baseFragment, this, 7, this.currentAccount, null);
                baseFragment.showDialog(limitReachedBottomSheet);
                limitReachedBottomSheet.onShowPremiumScreenRunnable = new Runnable() {
                    @Override
                    public final void run() {
                        LaunchActivity.this.lambda$onCreate$2();
                    }
                };
                return;
            }
        }
        int id = this.drawerLayoutAdapter.getId(i);
        final TLRPC$TL_attachMenuBot attachMenuBot = this.drawerLayoutAdapter.getAttachMenuBot(i);
        if (attachMenuBot != null) {
            if (attachMenuBot.inactive || attachMenuBot.side_menu_disclaimer_needed) {
                WebAppDisclaimerAlert.show(this, new com.google.android.exoplayer2.util.Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        LaunchActivity.this.lambda$onCreate$5(attachMenuBot, (Boolean) obj);
                    }
                }, null, null);
                return;
            } else {
                showAttachMenuBot(attachMenuBot, null, true);
                return;
            }
        }
        if (id == 2) {
            lambda$runLinkRequest$91(new GroupCreateActivity(new Bundle()));
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        if (id == 3) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlyUsers", true);
            bundle.putBoolean("destroyAfterSelect", true);
            bundle.putBoolean("createSecretChat", true);
            bundle.putBoolean("allowBots", false);
            bundle.putBoolean("allowSelf", false);
            lambda$runLinkRequest$91(new ContactsActivity(bundle));
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        if (id == 4) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            if (!BuildVars.DEBUG_VERSION && globalMainSettings.getBoolean("channel_intro", false)) {
                Bundle bundle2 = new Bundle();
                bundle2.putInt("step", 0);
                lambda$runLinkRequest$91(new ChannelCreateActivity(bundle2));
            } else {
                lambda$runLinkRequest$91(new ActionIntroActivity(0));
                globalMainSettings.edit().putBoolean("channel_intro", true).commit();
            }
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        if (id == 6) {
            Bundle bundle3 = new Bundle();
            bundle3.putBoolean("needFinishFragment", false);
            lambda$runLinkRequest$91(new ContactsActivity(bundle3));
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        if (id == 7) {
            lambda$runLinkRequest$91(new InviteContactsActivity());
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        if (id == 8) {
            openSettings(false);
            return;
        }
        if (id == 9) {
            Browser.openUrl(this, LocaleController.getString(R.string.TelegramFaqUrl));
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        if (id == 10) {
            lambda$runLinkRequest$91(new CallLogActivity());
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        if (id == 11) {
            Bundle bundle4 = new Bundle();
            bundle4.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
            lambda$runLinkRequest$91(new ChatActivity(bundle4));
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        if (id == 12) {
            int i4 = Build.VERSION.SDK_INT;
            if (i4 >= 23 && AudioPlayerAlert$$ExternalSyntheticApiModelOutline0.m(this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                lambda$runLinkRequest$91(new ActionIntroActivity(1));
                this.drawerLayoutContainer.closeDrawer(false);
                return;
            }
            if (i4 >= 28) {
                z = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
            } else {
                try {
                    if (Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) == 0) {
                        z = false;
                    }
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            if (z) {
                lambda$runLinkRequest$91(new PeopleNearbyActivity());
            } else {
                lambda$runLinkRequest$91(new ActionIntroActivity(4));
            }
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        if (id == 13) {
            Browser.openUrl(this, LocaleController.getString(R.string.TelegramFeaturesUrl));
            this.drawerLayoutContainer.closeDrawer(false);
            return;
        }
        if (id == 15) {
            showSelectStatusDialog();
            return;
        }
        if (id == 16) {
            this.drawerLayoutContainer.closeDrawer(true);
            Bundle bundle5 = new Bundle();
            bundle5.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
            bundle5.putBoolean("my_profile", true);
            lambda$runLinkRequest$91(new ProfileActivity(bundle5, null));
            return;
        }
        if (id == 17) {
            this.drawerLayoutContainer.closeDrawer(true);
            Bundle bundle6 = new Bundle();
            bundle6.putLong("dialog_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
            bundle6.putInt("type", 1);
            lambda$runLinkRequest$91(new MediaActivity(bundle6, null));
        }
    }

    public void lambda$onCreate$2() {
        this.drawerLayoutContainer.closeDrawer(false);
    }

    public void lambda$onCreate$5(final TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, Boolean bool) {
        TLRPC$TL_messages_toggleBotInAttachMenu tLRPC$TL_messages_toggleBotInAttachMenu = new TLRPC$TL_messages_toggleBotInAttachMenu();
        tLRPC$TL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(this.currentAccount).getInputUser(tLRPC$TL_attachMenuBot.bot_id);
        tLRPC$TL_messages_toggleBotInAttachMenu.enabled = true;
        tLRPC$TL_messages_toggleBotInAttachMenu.write_allowed = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.this.lambda$onCreate$4(tLRPC$TL_attachMenuBot, tLObject, tLRPC$TL_error);
            }
        }, 66);
    }

    public void lambda$onCreate$4(final TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$onCreate$3(tLRPC$TL_attachMenuBot);
            }
        });
    }

    public void lambda$onCreate$3(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        tLRPC$TL_attachMenuBot.side_menu_disclaimer_needed = false;
        tLRPC$TL_attachMenuBot.inactive = false;
        showAttachMenuBot(tLRPC$TL_attachMenuBot, null, true);
        MediaDataController.getInstance(this.currentAccount).updateAttachMenuBotsInCache();
    }

    public boolean lambda$onCreate$7(ItemTouchHelper itemTouchHelper, View view, int i) {
        Bundle bundle = null;
        if (view instanceof DrawerUserCell) {
            final int accountNumber = ((DrawerUserCell) view).getAccountNumber();
            if (accountNumber == this.currentAccount || AndroidUtilities.isTablet()) {
                itemTouchHelper.startDrag(this.sideMenu.getChildViewHolder(view));
            } else {
                DialogsActivity dialogsActivity = new DialogsActivity(bundle) {
                    @Override
                    public void onTransitionAnimationEnd(boolean z, boolean z2) {
                        super.onTransitionAnimationEnd(z, z2);
                        if (z || !z2) {
                            return;
                        }
                        LaunchActivity.this.drawerLayoutContainer.setDrawCurrentPreviewFragmentAbove(false);
                        LaunchActivity.this.actionBarLayout.getView().invalidate();
                    }

                    @Override
                    public void onPreviewOpenAnimationEnd() {
                        super.onPreviewOpenAnimationEnd();
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        LaunchActivity.this.drawerLayoutContainer.setDrawCurrentPreviewFragmentAbove(false);
                        LaunchActivity.this.switchToAccount(accountNumber, true);
                        LaunchActivity.this.actionBarLayout.getView().invalidate();
                    }
                };
                dialogsActivity.setCurrentAccount(accountNumber);
                this.actionBarLayout.presentFragmentAsPreview(dialogsActivity);
                this.drawerLayoutContainer.setDrawCurrentPreviewFragmentAbove(true);
                return true;
            }
        }
        if (!(view instanceof DrawerActionCell)) {
            return false;
        }
        this.drawerLayoutAdapter.getId(i);
        TLRPC$TL_attachMenuBot attachMenuBot = this.drawerLayoutAdapter.getAttachMenuBot(i);
        if (attachMenuBot == null) {
            return false;
        }
        BotWebViewSheet.deleteBot(this.currentAccount, attachMenuBot.bot_id, null);
        return true;
    }

    public void lambda$onCreate$8() {
        checkSystemBarColors(true, false);
        if (getLastFragment() == null || getLastFragment().getLastStoryViewer() == null) {
            return;
        }
        getLastFragment().getLastStoryViewer().updatePlayingMode();
    }

    public static void lambda$onCreate$9(View view) {
        int measuredHeight = view.getMeasuredHeight();
        FileLog.d("height = " + measuredHeight + " displayHeight = " + AndroidUtilities.displaySize.y);
        if (Build.VERSION.SDK_INT >= 21) {
            measuredHeight -= AndroidUtilities.statusBarHeight;
        }
        if (measuredHeight <= AndroidUtilities.dp(100.0f) || measuredHeight >= AndroidUtilities.displaySize.y) {
            return;
        }
        int dp = AndroidUtilities.dp(100.0f) + measuredHeight;
        Point point = AndroidUtilities.displaySize;
        if (dp > point.y) {
            point.y = measuredHeight;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("fix display size y to " + AndroidUtilities.displaySize.y);
            }
        }
    }

    private void showAttachMenuBot(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, String str, boolean z) {
        this.drawerLayoutContainer.closeDrawer();
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment == null) {
            return;
        }
        int i = this.currentAccount;
        long j = tLRPC$TL_attachMenuBot.bot_id;
        WebViewRequestProps of = WebViewRequestProps.of(i, j, j, tLRPC$TL_attachMenuBot.short_name, null, 1, 0, false, null, false, str, null, 2, false);
        if (getBottomSheetTabs() == null || getBottomSheetTabs().tryReopenTab(of) == null) {
            if (AndroidUtilities.isTablet()) {
                BotWebViewSheet botWebViewSheet = new BotWebViewSheet(this, lastFragment.getResourceProvider());
                botWebViewSheet.setNeedsContext(false);
                botWebViewSheet.setDefaultFullsize(z);
                botWebViewSheet.setParentActivity(this);
                botWebViewSheet.requestWebView(lastFragment, of);
                botWebViewSheet.show();
                return;
            }
            BotWebViewAttachedSheet createBotViewer = (lastFragment.getParentLayout() instanceof ActionBarLayout ? ((ActionBarLayout) lastFragment.getParentLayout()).getSheetFragment() : lastFragment).createBotViewer();
            createBotViewer.setNeedsContext(false);
            createBotViewer.setDefaultFullsize(z);
            createBotViewer.setParentActivity(this);
            createBotViewer.requestWebView(lastFragment, of);
            createBotViewer.show();
        }
    }

    @Override
    public void onThemeProgress(float f) {
        if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().updateThemeColors(f);
        }
        this.drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        if (PhotoViewer.hasInstance()) {
            PhotoViewer.getInstance().updateColors();
        }
    }

    private void setupActionBarLayout() {
        DrawerLayoutContainer drawerLayoutContainer;
        ViewGroup view;
        if (this.drawerLayoutContainer.indexOfChild(this.launchLayout) != -1) {
            drawerLayoutContainer = this.drawerLayoutContainer;
            view = this.launchLayout;
        } else {
            drawerLayoutContainer = this.drawerLayoutContainer;
            view = this.actionBarLayout.getView();
        }
        int indexOfChild = drawerLayoutContainer.indexOfChild(view);
        if (indexOfChild != -1) {
            this.drawerLayoutContainer.removeViewAt(indexOfChild);
        }
        if (AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(16);
            RelativeLayout relativeLayout = new RelativeLayout(this) {
                private boolean inLayout;
                private Path path = new Path();

                @Override
                public void requestLayout() {
                    if (this.inLayout) {
                        return;
                    }
                    super.requestLayout();
                }

                @Override
                protected void onMeasure(int i, int i2) {
                    this.inLayout = true;
                    int size = View.MeasureSpec.getSize(i);
                    int size2 = View.MeasureSpec.getSize(i2);
                    setMeasuredDimension(size, size2);
                    if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                        LaunchActivity.this.tabletFullSize = true;
                        LaunchActivity.this.actionBarLayout.getView().measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                    } else {
                        LaunchActivity.this.tabletFullSize = false;
                        int i3 = (size / 100) * 35;
                        if (i3 < AndroidUtilities.dp(320.0f)) {
                            i3 = AndroidUtilities.dp(320.0f);
                        }
                        LaunchActivity.this.actionBarLayout.getView().measure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                        LaunchActivity.this.shadowTabletSide.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                        LaunchActivity.this.rightActionBarLayout.getView().measure(View.MeasureSpec.makeMeasureSpec(size - i3, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                    }
                    LaunchActivity.this.backgroundTablet.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                    LaunchActivity.this.shadowTablet.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                    LaunchActivity.this.layersActionBarLayout.getView().measure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(530.0f), size - AndroidUtilities.dp(16.0f)), 1073741824), View.MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(16.0f), 1073741824));
                    this.inLayout = false;
                }

                @Override
                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int i5 = i3 - i;
                    int i6 = i4 - i2;
                    if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                        LaunchActivity.this.actionBarLayout.getView().layout(0, 0, LaunchActivity.this.actionBarLayout.getView().getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getView().getMeasuredHeight());
                    } else {
                        int i7 = (i5 / 100) * 35;
                        if (i7 < AndroidUtilities.dp(320.0f)) {
                            i7 = AndroidUtilities.dp(320.0f);
                        }
                        LaunchActivity.this.shadowTabletSide.layout(i7, 0, LaunchActivity.this.shadowTabletSide.getMeasuredWidth() + i7, LaunchActivity.this.shadowTabletSide.getMeasuredHeight());
                        LaunchActivity.this.actionBarLayout.getView().layout(0, 0, LaunchActivity.this.actionBarLayout.getView().getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getView().getMeasuredHeight());
                        LaunchActivity.this.rightActionBarLayout.getView().layout(i7, 0, LaunchActivity.this.rightActionBarLayout.getView().getMeasuredWidth() + i7, LaunchActivity.this.rightActionBarLayout.getView().getMeasuredHeight());
                    }
                    int measuredWidth = (i5 - LaunchActivity.this.layersActionBarLayout.getView().getMeasuredWidth()) / 2;
                    int measuredHeight = ((i6 - LaunchActivity.this.layersActionBarLayout.getView().getMeasuredHeight()) + AndroidUtilities.statusBarHeight) / 2;
                    LaunchActivity.this.layersActionBarLayout.getView().layout(measuredWidth, measuredHeight, LaunchActivity.this.layersActionBarLayout.getView().getMeasuredWidth() + measuredWidth, LaunchActivity.this.layersActionBarLayout.getView().getMeasuredHeight() + measuredHeight);
                    LaunchActivity.this.backgroundTablet.layout(0, 0, LaunchActivity.this.backgroundTablet.getMeasuredWidth(), LaunchActivity.this.backgroundTablet.getMeasuredHeight());
                    LaunchActivity.this.shadowTablet.layout(0, 0, LaunchActivity.this.shadowTablet.getMeasuredWidth(), LaunchActivity.this.shadowTablet.getMeasuredHeight());
                }

                @Override
                protected void dispatchDraw(Canvas canvas) {
                    if (LaunchActivity.this.layersActionBarLayout != null) {
                        LaunchActivity.this.layersActionBarLayout.parentDraw(this, canvas);
                    }
                    super.dispatchDraw(canvas);
                }
            };
            this.launchLayout = relativeLayout;
            if (indexOfChild != -1) {
                this.drawerLayoutContainer.addView(relativeLayout, indexOfChild, LayoutHelper.createFrame(-1, -1.0f));
            } else {
                this.drawerLayoutContainer.addView(relativeLayout, LayoutHelper.createFrame(-1, -1.0f));
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(this) {
                @Override
                protected boolean isActionBarVisible() {
                    return false;
                }
            };
            this.backgroundTablet = sizeNotifierFrameLayout;
            sizeNotifierFrameLayout.setOccupyStatusBar(false);
            this.backgroundTablet.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            this.launchLayout.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            ViewGroup viewGroup = (ViewGroup) this.actionBarLayout.getView().getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.actionBarLayout.getView());
            }
            this.launchLayout.addView(this.actionBarLayout.getView());
            ActionBarLayout actionBarLayout = new ActionBarLayout(this, false);
            this.rightActionBarLayout = actionBarLayout;
            actionBarLayout.setFragmentStack(rightFragmentsStack);
            this.rightActionBarLayout.setDelegate(this);
            this.launchLayout.addView(this.rightActionBarLayout.getView());
            FrameLayout frameLayout = new FrameLayout(this);
            this.shadowTabletSide = frameLayout;
            frameLayout.setBackgroundColor(1076449908);
            this.launchLayout.addView(this.shadowTabletSide);
            FrameLayout frameLayout2 = new FrameLayout(this);
            this.shadowTablet = frameLayout2;
            ArrayList<BaseFragment> arrayList = layerFragmentsStack;
            frameLayout2.setVisibility(arrayList.isEmpty() ? 8 : 0);
            this.shadowTablet.setBackgroundColor(2130706432);
            this.launchLayout.addView(this.shadowTablet);
            this.shadowTablet.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public final boolean onTouch(View view2, MotionEvent motionEvent) {
                    boolean lambda$setupActionBarLayout$10;
                    lambda$setupActionBarLayout$10 = LaunchActivity.this.lambda$setupActionBarLayout$10(view2, motionEvent);
                    return lambda$setupActionBarLayout$10;
                }
            });
            this.shadowTablet.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    LaunchActivity.lambda$setupActionBarLayout$11(view2);
                }
            });
            ActionBarLayout actionBarLayout2 = new ActionBarLayout(this, true);
            this.layersActionBarLayout = actionBarLayout2;
            actionBarLayout2.setRemoveActionBarExtraHeight(true);
            this.layersActionBarLayout.setBackgroundView(this.shadowTablet);
            this.layersActionBarLayout.setUseAlphaAnimations(true);
            this.layersActionBarLayout.setFragmentStack(arrayList);
            this.layersActionBarLayout.setDelegate(this);
            this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
            ViewGroup view2 = this.layersActionBarLayout.getView();
            view2.setBackgroundResource(R.drawable.popup_fixed_alert3);
            view2.setVisibility(arrayList.isEmpty() ? 8 : 0);
            this.launchLayout.addView(view2);
        } else {
            ViewGroup viewGroup2 = (ViewGroup) this.actionBarLayout.getView().getParent();
            if (viewGroup2 != null) {
                viewGroup2.removeView(this.actionBarLayout.getView());
            }
            this.actionBarLayout.setFragmentStack(mainFragmentsStack);
            if (indexOfChild != -1) {
                this.drawerLayoutContainer.addView(this.actionBarLayout.getView(), indexOfChild, new ViewGroup.LayoutParams(-1, -1));
            } else {
                this.drawerLayoutContainer.addView(this.actionBarLayout.getView(), new ViewGroup.LayoutParams(-1, -1));
            }
        }
        FloatingDebugController.setActive(this, SharedConfig.isFloatingDebugActive, false);
    }

    public boolean lambda$setupActionBarLayout$10(View view, MotionEvent motionEvent) {
        if (!this.actionBarLayout.getFragmentStack().isEmpty() && motionEvent.getAction() == 1) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            int[] iArr = new int[2];
            this.layersActionBarLayout.getView().getLocationOnScreen(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            if (!this.layersActionBarLayout.checkTransitionAnimation() && (x <= i || x >= i + this.layersActionBarLayout.getView().getWidth() || y <= i2 || y >= i2 + this.layersActionBarLayout.getView().getHeight())) {
                if (!this.layersActionBarLayout.getFragmentStack().isEmpty()) {
                    while (this.layersActionBarLayout.getFragmentStack().size() - 1 > 0) {
                        ActionBarLayout actionBarLayout = this.layersActionBarLayout;
                        actionBarLayout.removeFragmentFromStack(actionBarLayout.getFragmentStack().get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                }
                return true;
            }
        }
        return false;
    }

    public void addOnUserLeaveHintListener(Runnable runnable) {
        this.onUserLeaveHintListeners.add(runnable);
    }

    public void removeOnUserLeaveHintListener(Runnable runnable) {
        this.onUserLeaveHintListeners.remove(runnable);
    }

    private BaseFragment getClientNotActivatedFragment() {
        if (LoginActivity.loadCurrentState(false, this.currentAccount).getInt("currentViewNum", 0) != 0) {
            return new LoginActivity();
        }
        return new IntroActivity();
    }

    public void showSelectStatusDialog() {
        BaseFragment lastFragment;
        int i;
        View view;
        int i2;
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable;
        WindowInsets rootWindowInsets;
        WindowInsets rootWindowInsets2;
        int stableInsetLeft;
        if (this.selectAnimatedEmojiDialog != null || SharedConfig.appLocked || (lastFragment = this.actionBarLayout.getLastFragment()) == null) {
            return;
        }
        View childAt = this.sideMenu.getChildAt(0);
        final SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[1];
        TLRPC$User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()));
        if (childAt instanceof DrawerProfileCell) {
            DrawerProfileCell drawerProfileCell = (DrawerProfileCell) childAt;
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable emojiStatusDrawable = drawerProfileCell.getEmojiStatusDrawable();
            if (emojiStatusDrawable != null) {
                emojiStatusDrawable.play();
            }
            View emojiStatusDrawableParent = drawerProfileCell.getEmojiStatusDrawableParent();
            if (emojiStatusDrawable != null) {
                boolean z = emojiStatusDrawable.getDrawable() instanceof AnimatedEmojiDrawable;
            }
            Rect rect = AndroidUtilities.rectTmp2;
            drawerProfileCell.getEmojiStatusLocation(rect);
            int dp = (-(childAt.getHeight() - rect.centerY())) - AndroidUtilities.dp(16.0f);
            i = rect.centerX();
            if (Build.VERSION.SDK_INT >= 23 && getWindow() != null && getWindow().getDecorView() != null) {
                rootWindowInsets = getWindow().getDecorView().getRootWindowInsets();
                if (rootWindowInsets != null) {
                    rootWindowInsets2 = getWindow().getDecorView().getRootWindowInsets();
                    stableInsetLeft = rootWindowInsets2.getStableInsetLeft();
                    i -= stableInsetLeft;
                }
            }
            i2 = dp;
            swapAnimatedEmojiDrawable = emojiStatusDrawable;
            view = emojiStatusDrawableParent;
        } else {
            i = 0;
            view = null;
            i2 = 0;
            swapAnimatedEmojiDrawable = null;
        }
        View view2 = view;
        SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = new SelectAnimatedEmojiDialog(lastFragment, this, true, Integer.valueOf(i), 0, null) {
            @Override
            public void onSettings() {
                DrawerLayoutContainer drawerLayoutContainer = LaunchActivity.this.drawerLayoutContainer;
                if (drawerLayoutContainer != null) {
                    drawerLayoutContainer.closeDrawer();
                }
            }

            @Override
            protected void onEmojiSelected(View view3, Long l, TLRPC$Document tLRPC$Document, Integer num) {
                TLRPC$EmojiStatus tLRPC$EmojiStatus;
                int i3;
                if (l == null) {
                    tLRPC$EmojiStatus = new TLRPC$TL_emojiStatusEmpty();
                } else if (num != null) {
                    TLRPC$TL_emojiStatusUntil tLRPC$TL_emojiStatusUntil = new TLRPC$TL_emojiStatusUntil();
                    tLRPC$TL_emojiStatusUntil.document_id = l.longValue();
                    tLRPC$TL_emojiStatusUntil.until = num.intValue();
                    tLRPC$EmojiStatus = tLRPC$TL_emojiStatusUntil;
                } else {
                    TLRPC$TL_emojiStatus tLRPC$TL_emojiStatus = new TLRPC$TL_emojiStatus();
                    tLRPC$TL_emojiStatus.document_id = l.longValue();
                    tLRPC$EmojiStatus = tLRPC$TL_emojiStatus;
                }
                MessagesController.getInstance(LaunchActivity.this.currentAccount).updateEmojiStatus(tLRPC$EmojiStatus);
                TLRPC$User currentUser = UserConfig.getInstance(LaunchActivity.this.currentAccount).getCurrentUser();
                if (currentUser != null) {
                    for (int i4 = 0; i4 < LaunchActivity.this.sideMenu.getChildCount(); i4++) {
                        View childAt2 = LaunchActivity.this.sideMenu.getChildAt(i4);
                        if (childAt2 instanceof DrawerUserCell) {
                            DrawerUserCell drawerUserCell = (DrawerUserCell) childAt2;
                            drawerUserCell.setAccount(drawerUserCell.getAccountNumber());
                        } else if (childAt2 instanceof DrawerProfileCell) {
                            if (l != null) {
                                ((DrawerProfileCell) childAt2).animateStateChange(l.longValue());
                            }
                            ((DrawerProfileCell) childAt2).setUser(currentUser, LaunchActivity.this.drawerLayoutAdapter.isAccountsShown());
                        } else if ((childAt2 instanceof DrawerActionCell) && LaunchActivity.this.drawerLayoutAdapter.getId(LaunchActivity.this.sideMenu.getChildAdapterPosition(childAt2)) == 15) {
                            boolean z2 = DialogObject.getEmojiStatusDocumentId(currentUser.emoji_status) != 0;
                            DrawerActionCell drawerActionCell = (DrawerActionCell) childAt2;
                            String string = LaunchActivity.this.getString(z2 ? R.string.ChangeEmojiStatus : R.string.SetEmojiStatus);
                            if (z2) {
                                i3 = R.drawable.msg_status_edit;
                            } else {
                                i3 = R.drawable.msg_status_set;
                            }
                            drawerActionCell.updateTextAndIcon(string, i3);
                        }
                    }
                }
                if (selectAnimatedEmojiDialogWindowArr[0] != null) {
                    LaunchActivity.this.selectAnimatedEmojiDialog = null;
                    selectAnimatedEmojiDialogWindowArr[0].dismiss();
                }
            }
        };
        if (user != null) {
            selectAnimatedEmojiDialog.setExpireDateHint(DialogObject.getEmojiStatusUntil(user.emoji_status));
        }
        selectAnimatedEmojiDialog.setSelected((swapAnimatedEmojiDrawable == null || !(swapAnimatedEmojiDrawable.getDrawable() instanceof AnimatedEmojiDrawable)) ? null : Long.valueOf(((AnimatedEmojiDrawable) swapAnimatedEmojiDrawable.getDrawable()).getDocumentId()));
        selectAnimatedEmojiDialog.setSaveState(2);
        selectAnimatedEmojiDialog.setScrimDrawable(swapAnimatedEmojiDrawable, view2);
        int i3 = -2;
        SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialogWindow = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow(selectAnimatedEmojiDialog, i3, i3) {
            @Override
            public void dismiss() {
                super.dismiss();
                LaunchActivity.this.selectAnimatedEmojiDialog = null;
            }
        };
        this.selectAnimatedEmojiDialog = selectAnimatedEmojiDialogWindow;
        selectAnimatedEmojiDialogWindowArr[0] = selectAnimatedEmojiDialogWindow;
        selectAnimatedEmojiDialogWindow.showAsDropDown(this.sideMenu.getChildAt(0), 0, i2, 48);
        selectAnimatedEmojiDialogWindowArr[0].dimBehind();
    }

    public FireworksOverlay getFireworksOverlay() {
        return this.fireworksOverlay;
    }

    public BottomSheetTabsOverlay getBottomSheetTabsOverlay() {
        return this.bottomSheetTabsOverlay;
    }

    private void openSettings(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", UserConfig.getInstance(this.currentAccount).clientUserId);
        if (z) {
            bundle.putBoolean("expandPhoto", true);
        }
        lambda$runLinkRequest$91(new ProfileActivity(bundle));
        this.drawerLayoutContainer.closeDrawer(false);
    }

    private void checkSystemBarColors() {
        checkSystemBarColors(false, true, !this.isNavigationBarColorFrozen, true);
    }

    public void checkSystemBarColors(boolean z) {
        checkSystemBarColors(z, true, !this.isNavigationBarColorFrozen, true);
    }

    private void checkSystemBarColors(boolean z, boolean z2) {
        checkSystemBarColors(false, z, z2, true);
    }

    public void checkSystemBarColors(boolean z, boolean z2, boolean z3, boolean z4) {
        boolean z5;
        ArrayList<BaseFragment> arrayList = mainFragmentsStack;
        BaseFragment baseFragment = !arrayList.isEmpty() ? arrayList.get(arrayList.size() - 1) : null;
        if (baseFragment != null && (baseFragment.isRemovingFromStack() || baseFragment.isInPreviewMode())) {
            baseFragment = arrayList.size() > 1 ? arrayList.get(arrayList.size() - 2) : null;
        }
        boolean z6 = baseFragment != null && baseFragment.hasForceLightStatusBar();
        int i = Build.VERSION.SDK_INT;
        if (i >= 23) {
            if (z2) {
                if (baseFragment != null) {
                    z5 = baseFragment.isLightStatusBar();
                    if (baseFragment.getParentLayout() instanceof ActionBarLayout) {
                        ActionBarLayout actionBarLayout = (ActionBarLayout) baseFragment.getParentLayout();
                        if (actionBarLayout.getSheetFragment(false) != null && actionBarLayout.getSheetFragment(false).getLastSheet() != null) {
                            BaseFragment.AttachedSheet lastSheet = actionBarLayout.getSheetFragment(false).getLastSheet();
                            if (lastSheet.isShown()) {
                                z5 = lastSheet.isAttachedLightStatusBar();
                            }
                        } else {
                            ArrayList<BaseFragment.AttachedSheet> arrayList2 = baseFragment.sheetsStack;
                            if (arrayList2 != null && !arrayList2.isEmpty()) {
                                ArrayList<BaseFragment.AttachedSheet> arrayList3 = baseFragment.sheetsStack;
                                BaseFragment.AttachedSheet attachedSheet = arrayList3.get(arrayList3.size() - 1);
                                if (attachedSheet.isShown()) {
                                    z5 = attachedSheet.isAttachedLightStatusBar();
                                }
                            }
                        }
                    }
                } else {
                    z5 = ColorUtils.calculateLuminance(Theme.getColor(Theme.key_actionBarDefault, null, true)) > 0.699999988079071d;
                }
                AndroidUtilities.setLightStatusBar(getWindow(), z5, z6);
            }
            if (i >= 26 && z3 && (!z || baseFragment == null || !baseFragment.isInPreviewMode())) {
                int color = (baseFragment == null || !z) ? Theme.getColor(Theme.key_windowBackgroundGray, null, true) : baseFragment.getNavigationBarColor();
                if (this.actionBarLayout.getSheetFragment(false) != null) {
                    EmptyBaseFragment sheetFragment = this.actionBarLayout.getSheetFragment(false);
                    if (sheetFragment.sheetsStack != null) {
                        for (int i2 = 0; i2 < sheetFragment.sheetsStack.size(); i2++) {
                            BaseFragment.AttachedSheet attachedSheet2 = sheetFragment.sheetsStack.get(i2);
                            if (attachedSheet2.attachedToParent()) {
                                color = attachedSheet2.getNavigationBarColor(color);
                            }
                        }
                    }
                }
                setNavigationBarColor(color, z4);
                setLightNavigationBar(AndroidUtilities.computePerceivedBrightness(color) >= 0.721f);
            }
        }
        if (Build.VERSION.SDK_INT < 21 || !z2) {
            return;
        }
        getWindow().setStatusBarColor(0);
    }

    public FrameLayout getMainContainerFrameLayout() {
        return this.frameLayout;
    }

    public static DialogsActivity lambda$switchToAccount$12(Void r1) {
        return new DialogsActivity(null);
    }

    public void switchToAccount(int i, boolean z) {
        switchToAccount(i, z, new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                DialogsActivity lambda$switchToAccount$12;
                lambda$switchToAccount$12 = LaunchActivity.lambda$switchToAccount$12((Void) obj);
                return lambda$switchToAccount$12;
            }
        });
    }

    public void switchToAccount(int i, boolean z, GenericProvider<Void, DialogsActivity> genericProvider) {
        if (i == UserConfig.selectedAccount || !UserConfig.isValidAccount(i)) {
            return;
        }
        this.switchingAccount = true;
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        UserConfig.selectedAccount = i;
        UserConfig.getInstance(0).saveConfig(false);
        checkCurrentAccount();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.removeAllFragments();
            this.rightActionBarLayout.removeAllFragments();
            if (!this.tabletFullSize) {
                this.shadowTabletSide.setVisibility(0);
                if (this.rightActionBarLayout.getFragmentStack().isEmpty()) {
                    this.backgroundTablet.setVisibility(0);
                }
                this.rightActionBarLayout.getView().setVisibility(8);
            }
            this.layersActionBarLayout.getView().setVisibility(8);
        }
        if (z) {
            this.actionBarLayout.removeAllFragments();
        } else {
            this.actionBarLayout.removeFragmentFromStack(0);
        }
        DialogsActivity provide = genericProvider.provide(null);
        provide.setSideMenu(this.sideMenu);
        this.actionBarLayout.addFragmentToStack(provide, -3);
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        this.actionBarLayout.rebuildFragments(1);
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.rebuildFragments(1);
            this.rightActionBarLayout.rebuildFragments(1);
        }
        if (!ApplicationLoader.mainInterfacePaused) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        }
        if (UserConfig.getInstance(i).unacceptedTermsOfService != null) {
            showTosActivity(i, UserConfig.getInstance(i).unacceptedTermsOfService);
        }
        updateCurrentConnectionState(this.currentAccount);
        this.switchingAccount = false;
    }

    private void switchToAvailableAccountOrLogout() {
        int i = 0;
        while (true) {
            if (i >= 4) {
                i = -1;
                break;
            } else if (UserConfig.getInstance(i).isClientActivated()) {
                break;
            } else {
                i++;
            }
        }
        TermsOfServiceView termsOfServiceView = this.termsOfServiceView;
        if (termsOfServiceView != null) {
            termsOfServiceView.setVisibility(8);
        }
        if (i != -1) {
            switchToAccount(i, true);
            return;
        }
        DrawerLayoutAdapter drawerLayoutAdapter = this.drawerLayoutAdapter;
        if (drawerLayoutAdapter != null) {
            drawerLayoutAdapter.notifyDataSetChanged();
        }
        RestrictedLanguagesSelectActivity.checkRestrictedLanguages(true);
        clearFragments();
        this.actionBarLayout.rebuildLogout();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.rebuildLogout();
            this.rightActionBarLayout.rebuildLogout();
        }
        lambda$runLinkRequest$91(new IntroActivity().setOnLogout());
    }

    public static void clearFragments() {
        Iterator<BaseFragment> it = mainFragmentsStack.iterator();
        while (it.hasNext()) {
            it.next().onFragmentDestroy();
        }
        mainFragmentsStack.clear();
        if (AndroidUtilities.isTablet()) {
            Iterator<BaseFragment> it2 = layerFragmentsStack.iterator();
            while (it2.hasNext()) {
                it2.next().onFragmentDestroy();
            }
            layerFragmentsStack.clear();
            Iterator<BaseFragment> it3 = rightFragmentsStack.iterator();
            while (it3.hasNext()) {
                it3.next().onFragmentDestroy();
            }
            rightFragmentsStack.clear();
        }
    }

    public int getMainFragmentsCount() {
        return mainFragmentsStack.size();
    }

    private void checkCurrentAccount() {
        int i = this.currentAccount;
        if (i != UserConfig.selectedAccount) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.openBoostForUsersDialog);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mainUserInfoChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.attachMenuBotsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowPlayServicesAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.historyImportProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersImportComplete);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatSwithcedToForum);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.storiesEnabledUpdate);
        }
        int i2 = UserConfig.selectedAccount;
        this.currentAccount = i2;
        NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.openBoostForUsersDialog);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.attachMenuBotsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.openArticle);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.hasNewContactsToImport);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowPlayServicesAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadFailed);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.historyImportProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupCallUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersImportComplete);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newSuggestionsAvailable);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserShowLimitReachedDialog);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatSwithcedToForum);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.storiesEnabledUpdate);
    }

    private void checkLayout() {
        if (!AndroidUtilities.isTablet() || this.rightActionBarLayout == null) {
            return;
        }
        if (AndroidUtilities.getWasTablet() == null || AndroidUtilities.getWasTablet().booleanValue() == AndroidUtilities.isTabletForce()) {
            if (!AndroidUtilities.isInMultiwindow && (!AndroidUtilities.isSmallTablet() || getResources().getConfiguration().orientation == 2)) {
                this.tabletFullSize = false;
                List<BaseFragment> fragmentStack = this.actionBarLayout.getFragmentStack();
                if (fragmentStack.size() >= 2) {
                    while (1 < fragmentStack.size()) {
                        BaseFragment baseFragment = fragmentStack.get(1);
                        if (baseFragment instanceof ChatActivity) {
                            ((ChatActivity) baseFragment).setIgnoreAttachOnPause(true);
                        }
                        baseFragment.onPause();
                        baseFragment.onFragmentDestroy();
                        baseFragment.setParentLayout(null);
                        fragmentStack.remove(baseFragment);
                        this.rightActionBarLayout.addFragmentToStack(baseFragment);
                    }
                    PasscodeViewDialog passcodeViewDialog = this.passcodeDialog;
                    if (passcodeViewDialog == null || passcodeViewDialog.passcodeView.getVisibility() != 0) {
                        this.actionBarLayout.rebuildFragments(1);
                        this.rightActionBarLayout.rebuildFragments(1);
                    }
                }
                this.rightActionBarLayout.getView().setVisibility(this.rightActionBarLayout.getFragmentStack().isEmpty() ? 8 : 0);
                this.backgroundTablet.setVisibility(this.rightActionBarLayout.getFragmentStack().isEmpty() ? 0 : 8);
                this.shadowTabletSide.setVisibility(this.actionBarLayout.getFragmentStack().isEmpty() ? 8 : 0);
                return;
            }
            this.tabletFullSize = true;
            List<BaseFragment> fragmentStack2 = this.rightActionBarLayout.getFragmentStack();
            if (!fragmentStack2.isEmpty()) {
                while (fragmentStack2.size() > 0) {
                    BaseFragment baseFragment2 = fragmentStack2.get(0);
                    if (baseFragment2 instanceof ChatActivity) {
                        ((ChatActivity) baseFragment2).setIgnoreAttachOnPause(true);
                    }
                    baseFragment2.onPause();
                    baseFragment2.onFragmentDestroy();
                    baseFragment2.setParentLayout(null);
                    fragmentStack2.remove(baseFragment2);
                    this.actionBarLayout.addFragmentToStack(baseFragment2);
                }
                PasscodeViewDialog passcodeViewDialog2 = this.passcodeDialog;
                if (passcodeViewDialog2 == null || passcodeViewDialog2.passcodeView.getVisibility() != 0) {
                    this.actionBarLayout.rebuildFragments(1);
                }
            }
            this.shadowTabletSide.setVisibility(8);
            this.rightActionBarLayout.getView().setVisibility(8);
            this.backgroundTablet.setVisibility(this.actionBarLayout.getFragmentStack().isEmpty() ? 0 : 8);
        }
    }

    private void showUpdateActivity(int i, TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, boolean z) {
        if (this.blockingUpdateView == null) {
            BlockingUpdateView blockingUpdateView = new BlockingUpdateView(this) {
                @Override
                public void setVisibility(int i2) {
                    super.setVisibility(i2);
                    if (i2 == 8) {
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                }
            };
            this.blockingUpdateView = blockingUpdateView;
            this.drawerLayoutContainer.addView(blockingUpdateView, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.blockingUpdateView.show(i, tLRPC$TL_help_appUpdate, z);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
    }

    private void showTosActivity(int i, TLRPC$TL_help_termsOfService tLRPC$TL_help_termsOfService) {
        if (this.termsOfServiceView == null) {
            TermsOfServiceView termsOfServiceView = new TermsOfServiceView(this);
            this.termsOfServiceView = termsOfServiceView;
            termsOfServiceView.setAlpha(0.0f);
            this.drawerLayoutContainer.addView(this.termsOfServiceView, LayoutHelper.createFrame(-1, -1.0f));
            this.termsOfServiceView.setDelegate(new AnonymousClass15());
        }
        TLRPC$TL_help_termsOfService tLRPC$TL_help_termsOfService2 = UserConfig.getInstance(i).unacceptedTermsOfService;
        if (tLRPC$TL_help_termsOfService2 != tLRPC$TL_help_termsOfService && (tLRPC$TL_help_termsOfService2 == null || !tLRPC$TL_help_termsOfService2.id.data.equals(tLRPC$TL_help_termsOfService.id.data))) {
            UserConfig.getInstance(i).unacceptedTermsOfService = tLRPC$TL_help_termsOfService;
            UserConfig.getInstance(i).saveConfig(false);
        }
        this.termsOfServiceView.show(i, tLRPC$TL_help_termsOfService);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        this.termsOfServiceView.animate().alpha(1.0f).setDuration(150L).setInterpolator(AndroidUtilities.decelerateInterpolator).setListener(null).start();
    }

    public class AnonymousClass15 implements TermsOfServiceView.TermsOfServiceViewDelegate {
        AnonymousClass15() {
        }

        @Override
        public void onAcceptTerms(int i) {
            UserConfig.getInstance(i).unacceptedTermsOfService = null;
            UserConfig.getInstance(i).saveConfig(false);
            LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            if (LaunchActivity.mainFragmentsStack.size() > 0) {
                ((BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1)).onResume();
            }
            LaunchActivity.this.termsOfServiceView.animate().alpha(0.0f).setDuration(150L).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.AnonymousClass15.this.lambda$onAcceptTerms$0();
                }
            }).start();
        }

        public void lambda$onAcceptTerms$0() {
            LaunchActivity.this.termsOfServiceView.setVisibility(8);
        }
    }

    public void showPasscodeActivity(boolean z, boolean z2, int i, int i2, final Runnable runnable, Runnable runnable2) {
        if (this.drawerLayoutContainer == null) {
            return;
        }
        if (this.passcodeDialog == null) {
            this.passcodeDialog = new PasscodeViewDialog(this);
        }
        SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialogWindow = this.selectAnimatedEmojiDialog;
        if (selectAnimatedEmojiDialogWindow != null) {
            selectAnimatedEmojiDialogWindow.dismiss();
            this.selectAnimatedEmojiDialog = null;
        }
        SharedConfig.appLocked = true;
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(false, false);
        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(false, true);
        } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        StoryRecorder.destroyInstance();
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject != null && playingMessageObject.isRoundVideo()) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
        this.passcodeDialog.show();
        this.passcodeDialog.passcodeView.onShow(this.overlayPasscodeViews.isEmpty() && z, z2, i, i2, new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$showPasscodeActivity$13(runnable);
            }
        }, runnable2);
        int i3 = 0;
        while (i3 < this.overlayPasscodeViews.size()) {
            this.overlayPasscodeViews.get(i3).onShow(z && i3 == this.overlayPasscodeViews.size() - 1, z2, i, i2, null, null);
            i3++;
        }
        SharedConfig.isWaitingForPasscodeEnter = true;
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        PasscodeView.PasscodeViewDelegate passcodeViewDelegate = new PasscodeView.PasscodeViewDelegate() {
            @Override
            public final void didAcceptedPassword(PasscodeView passcodeView) {
                LaunchActivity.this.lambda$showPasscodeActivity$14(passcodeView);
            }
        };
        this.passcodeDialog.passcodeView.setDelegate(passcodeViewDelegate);
        Iterator<PasscodeView> it = this.overlayPasscodeViews.iterator();
        while (it.hasNext()) {
            it.next().setDelegate(passcodeViewDelegate);
        }
        try {
            NotificationsController.getInstance(UserConfig.selectedAccount).showNotifications();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$showPasscodeActivity$13(Runnable runnable) {
        this.actionBarLayout.getView().setVisibility(4);
        if (AndroidUtilities.isTablet()) {
            ActionBarLayout actionBarLayout = this.layersActionBarLayout;
            if (actionBarLayout != null && actionBarLayout.getView() != null && this.layersActionBarLayout.getView().getVisibility() == 0) {
                this.layersActionBarLayout.getView().setVisibility(4);
            }
            ActionBarLayout actionBarLayout2 = this.rightActionBarLayout;
            if (actionBarLayout2 != null && actionBarLayout2.getView() != null) {
                this.rightActionBarLayout.getView().setVisibility(4);
            }
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$showPasscodeActivity$14(PasscodeView passcodeView) {
        SharedConfig.isWaitingForPasscodeEnter = false;
        Intent intent = this.passcodeSaveIntent;
        if (intent != null) {
            handleIntent(intent, this.passcodeSaveIntentIsNew, this.passcodeSaveIntentIsRestore, true, null, false, true);
            this.passcodeSaveIntent = null;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        this.actionBarLayout.getView().setVisibility(0);
        this.actionBarLayout.rebuildFragments(1);
        this.actionBarLayout.updateTitleOverlay();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.rebuildFragments(1);
            this.rightActionBarLayout.rebuildFragments(1);
            if (this.layersActionBarLayout.getView().getVisibility() == 4) {
                this.layersActionBarLayout.getView().setVisibility(0);
            }
            this.rightActionBarLayout.getView().setVisibility(0);
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.passcodeDismissed, passcodeView);
        try {
            NotificationsController.getInstance(UserConfig.selectedAccount).showNotifications();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean allowShowFingerprintDialog(PasscodeView passcodeView) {
        PasscodeViewDialog passcodeViewDialog;
        if (!this.overlayPasscodeViews.isEmpty() || (passcodeViewDialog = this.passcodeDialog) == null) {
            List<PasscodeView> list = this.overlayPasscodeViews;
            if (list.get(list.size() - 1) == passcodeView) {
                return true;
            }
        } else if (passcodeView == passcodeViewDialog.passcodeView) {
            return true;
        }
        return false;
    }

    private boolean handleIntent(Intent intent, boolean z, boolean z2, boolean z3) {
        return handleIntent(intent, z, z2, z3, null, true, false);
    }

    @android.annotation.SuppressLint({"Range"})
    private boolean handleIntent(android.content.Intent r105, boolean r106, boolean r107, boolean r108, org.telegram.messenger.browser.Browser.Progress r109, boolean r110, boolean r111) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean, org.telegram.messenger.browser.Browser$Progress, boolean, boolean):boolean");
    }

    public void lambda$handleIntent$15(String str) {
        if (this.actionBarLayout.getFragmentStack().isEmpty()) {
            return;
        }
        this.actionBarLayout.getFragmentStack().get(0).presentFragment(new PremiumPreviewFragment(Uri.parse(str).getQueryParameter("ref")));
    }

    public void lambda$handleIntent$16(Intent intent, boolean z) {
        handleIntent(intent, true, false, false);
    }

    public void lambda$handleIntent$18(final AlertDialog alertDialog, final String str, final Bundle bundle, final TLRPC$TL_account_sendConfirmPhoneCode tLRPC$TL_account_sendConfirmPhoneCode, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$handleIntent$17(alertDialog, tLRPC$TL_error, str, bundle, tLObject, tLRPC$TL_account_sendConfirmPhoneCode);
            }
        });
    }

    public void lambda$handleIntent$17(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, String str, Bundle bundle, TLObject tLObject, TLRPC$TL_account_sendConfirmPhoneCode tLRPC$TL_account_sendConfirmPhoneCode) {
        alertDialog.dismiss();
        if (tLRPC$TL_error == null) {
            lambda$runLinkRequest$91(new LoginActivity().cancelAccountDeletion(str, bundle, (TLRPC$TL_auth_sentCode) tLObject));
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, getActionBarLayout().getLastFragment(), tLRPC$TL_account_sendConfirmPhoneCode, new Object[0]);
        }
    }

    public void lambda$handleIntent$19(long j, long j2, ChatActivity chatActivity) {
        FileLog.d("LaunchActivity openForum after load " + j + " " + j2 + " TL_forumTopic " + MessagesController.getInstance(this.currentAccount).getTopicsController().findTopic(j, j2));
        if (this.actionBarLayout != null) {
            ForumUtilities.applyTopic(chatActivity, MessagesStorage.TopicKey.of(-j, j2));
            getActionBarLayout().presentFragment(chatActivity);
        }
    }

    public void lambda$handleIntent$21(final int[] iArr, LocationController.SharingLocationInfo sharingLocationInfo) {
        int i = sharingLocationInfo.messageObject.currentAccount;
        iArr[0] = i;
        switchToAccount(i, true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(sharingLocationInfo.messageObject);
        final long dialogId = sharingLocationInfo.messageObject.getDialogId();
        locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate() {
            @Override
            public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
                LaunchActivity.lambda$handleIntent$20(iArr, dialogId, tLRPC$MessageMedia, i2, z, i3);
            }
        });
        lambda$runLinkRequest$91(locationActivity);
    }

    public static void lambda$handleIntent$20(int[] iArr, long j, TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
        SendMessagesHelper.getInstance(iArr[0]).sendMessage(SendMessagesHelper.SendMessageParams.of(tLRPC$MessageMedia, j, (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i2));
    }

    public void lambda$handleIntent$22() {
        if (this.actionBarLayout.getFragmentStack().isEmpty()) {
            return;
        }
        this.actionBarLayout.getFragmentStack().get(0).showDialog(new StickersAlert(this, this.importingStickersSoftware, this.importingStickers, this.importingStickersEmoji, (Theme.ResourcesProvider) null));
    }

    public void lambda$handleIntent$23(BaseFragment baseFragment, boolean z) {
        presentFragment(baseFragment, z, false);
    }

    public void lambda$handleIntent$24(boolean z, int[] iArr, TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(tLRPC$User.id);
        VoIPHelper.startCall(tLRPC$User, z, userFull != null && userFull.video_calls_available, this, userFull, AccountInstance.getInstance(iArr[0]));
    }

    public void lambda$handleIntent$28(final ActionIntroActivity actionIntroActivity, String str) {
        final AlertDialog alertDialog = new AlertDialog(this, 3);
        alertDialog.setCanCancel(false);
        alertDialog.show();
        byte[] decode = Base64.decode(str.substring(17), 8);
        TLRPC$TL_auth_acceptLoginToken tLRPC$TL_auth_acceptLoginToken = new TLRPC$TL_auth_acceptLoginToken();
        tLRPC$TL_auth_acceptLoginToken.token = decode;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_auth_acceptLoginToken, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.lambda$handleIntent$27(AlertDialog.this, actionIntroActivity, tLObject, tLRPC$TL_error);
            }
        });
    }

    public static void lambda$handleIntent$27(final AlertDialog alertDialog, final ActionIntroActivity actionIntroActivity, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$handleIntent$26(AlertDialog.this, tLObject, actionIntroActivity, tLRPC$TL_error);
            }
        });
    }

    public static void lambda$handleIntent$26(AlertDialog alertDialog, TLObject tLObject, final ActionIntroActivity actionIntroActivity, final TLRPC$TL_error tLRPC$TL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (tLObject instanceof TLRPC$TL_authorization) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$handleIntent$25(ActionIntroActivity.this, tLRPC$TL_error);
            }
        });
    }

    public static void lambda$handleIntent$25(ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
        AlertsCreator.showSimpleAlert(actionIntroActivity, LocaleController.getString(R.string.AuthAnotherClient), LocaleController.getString(R.string.ErrorOccurred) + "\n" + tLRPC$TL_error.text);
    }

    public void lambda$handleIntent$29(BaseFragment baseFragment, String str, String str2, DialogInterface dialogInterface, int i) {
        NewContactBottomSheet newContactBottomSheet = new NewContactBottomSheet(baseFragment, this);
        newContactBottomSheet.setInitialPhoneNumber(str, false);
        if (str2 != null) {
            String[] split = str2.split(" ", 2);
            newContactBottomSheet.setInitialName(split[0], split.length > 1 ? split[1] : null);
        }
        newContactBottomSheet.show();
    }

    public static int getTimestampFromLink(Uri uri) {
        String queryParameter;
        int i;
        if (uri.getPathSegments().contains("video")) {
            queryParameter = uri.getQuery();
        } else {
            queryParameter = uri.getQueryParameter("t") != null ? uri.getQueryParameter("t") : null;
        }
        if (queryParameter == null) {
            return -1;
        }
        try {
            i = Integer.parseInt(queryParameter);
        } catch (Throwable unused) {
            i = -1;
        }
        if (i == -1) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            try {
                return (int) ((simpleDateFormat.parse(queryParameter).getTime() - simpleDateFormat.parse("00:00").getTime()) / 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    private void openDialogsToSend(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("onlySelect", true);
        bundle.putBoolean("canSelectTopics", true);
        bundle.putInt("dialogsType", 3);
        bundle.putBoolean("allowSwitchAccount", true);
        ArrayList<TLRPC$User> arrayList = this.contactsToSend;
        if (arrayList != null) {
            if (arrayList.size() != 1) {
                bundle.putString("selectAlertString", LocaleController.getString(R.string.SendMessagesToText));
                bundle.putString("selectAlertStringGroup", LocaleController.getString(R.string.SendContactToGroupText));
            }
        } else {
            bundle.putString("selectAlertString", LocaleController.getString(R.string.SendMessagesToText));
            bundle.putString("selectAlertStringGroup", LocaleController.getString(R.string.SendMessagesToGroupText));
        }
        DialogsActivity dialogsActivity = new DialogsActivity(bundle) {
            @Override
            public boolean shouldShowNextButton(DialogsActivity dialogsActivity2, ArrayList<Long> arrayList2, CharSequence charSequence, boolean z2) {
                if (LaunchActivity.this.exportingChatUri != null) {
                    return false;
                }
                if (LaunchActivity.this.contactsToSend != null && LaunchActivity.this.contactsToSend.size() == 1 && !LaunchActivity.mainFragmentsStack.isEmpty()) {
                    return true;
                }
                if (arrayList2.size() <= 1) {
                    return LaunchActivity.this.videoPath != null || (LaunchActivity.this.photoPathsArray != null && LaunchActivity.this.photoPathsArray.size() > 0);
                }
                return false;
            }
        };
        dialogsActivity.setDelegate(this);
        getActionBarLayout().presentFragment(dialogsActivity, !AndroidUtilities.isTablet() ? this.actionBarLayout.getFragmentStack().size() <= 1 || !(this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1) instanceof DialogsActivity) : this.layersActionBarLayout.getFragmentStack().size() <= 0 || !(this.layersActionBarLayout.getFragmentStack().get(this.layersActionBarLayout.getFragmentStack().size() - 1) instanceof DialogsActivity), !z, true, false);
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(false, false);
        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(false, true);
        } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        StoryRecorder.destroyInstance();
        GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
        if (groupCallActivity != null) {
            groupCallActivity.dismiss();
        }
        if (z) {
            return;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        if (AndroidUtilities.isTablet()) {
            this.actionBarLayout.rebuildFragments(1);
            this.rightActionBarLayout.rebuildFragments(1);
        } else {
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        }
    }

    private int runCommentRequest(int i, Runnable runnable, Integer num, Integer num2, Long l, TLRPC$Chat tLRPC$Chat) {
        return runCommentRequest(i, runnable, num, num2, l, tLRPC$Chat, null, null, 0, -1);
    }

    private int runCommentRequest(final int i, final Runnable runnable, final Integer num, final Integer num2, final Long l, final TLRPC$Chat tLRPC$Chat, final Runnable runnable2, final String str, final int i2, final int i3) {
        if (tLRPC$Chat == null) {
            return 0;
        }
        final TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage = new TLRPC$TL_messages_getDiscussionMessage();
        tLRPC$TL_messages_getDiscussionMessage.peer = MessagesController.getInputPeer(tLRPC$Chat);
        tLRPC$TL_messages_getDiscussionMessage.msg_id = num2 != null ? num.intValue() : (int) l.longValue();
        return ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getDiscussionMessage, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.this.lambda$runCommentRequest$31(i, tLRPC$Chat, l, num2, num, runnable2, str, i2, i3, tLRPC$TL_messages_getDiscussionMessage, runnable, tLObject, tLRPC$TL_error);
            }
        });
    }

    public void lambda$runCommentRequest$31(final int i, final TLRPC$Chat tLRPC$Chat, final Long l, final Integer num, final Integer num2, final Runnable runnable, final String str, final int i2, final int i3, final TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage, final Runnable runnable2, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runCommentRequest$30(tLObject, i, tLRPC$Chat, l, num, num2, runnable, str, i2, i3, tLRPC$TL_messages_getDiscussionMessage, runnable2);
            }
        });
    }

    public void lambda$runCommentRequest$30(org.telegram.tgnet.TLObject r16, int r17, org.telegram.tgnet.TLRPC$Chat r18, java.lang.Long r19, java.lang.Integer r20, java.lang.Integer r21, java.lang.Runnable r22, java.lang.String r23, int r24, int r25, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage r26, java.lang.Runnable r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runCommentRequest$30(org.telegram.tgnet.TLObject, int, org.telegram.tgnet.TLRPC$Chat, java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.Runnable, java.lang.String, int, int, org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage, java.lang.Runnable):void");
    }

    private void openTopicRequest(final int r16, final int r17, final org.telegram.tgnet.TLRPC$Chat r18, final int r19, org.telegram.tgnet.TLRPC$TL_forumTopic r20, final java.lang.Runnable r21, final java.lang.String r22, final int r23, final java.util.ArrayList<org.telegram.messenger.MessageObject> r24, final int r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.openTopicRequest(int, int, org.telegram.tgnet.TLRPC$Chat, int, org.telegram.tgnet.TLRPC$TL_forumTopic, java.lang.Runnable, java.lang.String, int, java.util.ArrayList, int):void");
    }

    public void lambda$openTopicRequest$33(final int i, final TLRPC$Chat tLRPC$Chat, final int i2, final int i3, final Runnable runnable, final String str, final int i4, final ArrayList arrayList, final int i5, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$openTopicRequest$32(tLRPC$TL_error, tLObject, i, tLRPC$Chat, i2, i3, runnable, str, i4, arrayList, i5);
            }
        });
    }

    public void lambda$openTopicRequest$32(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i, TLRPC$Chat tLRPC$Chat, int i2, int i3, Runnable runnable, String str, int i4, ArrayList arrayList, int i5) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_forumTopics tLRPC$TL_messages_forumTopics = (TLRPC$TL_messages_forumTopics) tLObject;
            SparseArray<TLRPC$Message> sparseArray = new SparseArray<>();
            for (int i6 = 0; i6 < tLRPC$TL_messages_forumTopics.messages.size(); i6++) {
                sparseArray.put(tLRPC$TL_messages_forumTopics.messages.get(i6).id, tLRPC$TL_messages_forumTopics.messages.get(i6));
            }
            MessagesController.getInstance(i).putUsers(tLRPC$TL_messages_forumTopics.users, false);
            MessagesController.getInstance(i).putChats(tLRPC$TL_messages_forumTopics.chats, false);
            MessagesController.getInstance(i).getTopicsController().processTopics(tLRPC$Chat.id, tLRPC$TL_messages_forumTopics.topics, sparseArray, false, 2, -1);
            openTopicRequest(i, i2, tLRPC$Chat, i3, MessagesController.getInstance(i).getTopicsController().findTopic(tLRPC$Chat.id, i2), runnable, str, i4, arrayList, i5);
        }
    }

    private java.lang.String readImport(android.net.Uri r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.readImport(android.net.Uri):java.lang.String");
    }

    private void runImportRequest(final Uri uri, ArrayList<Uri> arrayList) {
        final int i = UserConfig.selectedAccount;
        final AlertDialog alertDialog = new AlertDialog(this, 3);
        final int[] iArr = {0};
        String readImport = readImport(uri);
        if (readImport == null) {
            return;
        }
        TLRPC$TL_messages_checkHistoryImport tLRPC$TL_messages_checkHistoryImport = new TLRPC$TL_messages_checkHistoryImport();
        tLRPC$TL_messages_checkHistoryImport.import_head = readImport;
        iArr[0] = ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_checkHistoryImport, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.this.lambda$runImportRequest$35(uri, i, alertDialog, tLObject, tLRPC$TL_error);
            }
        });
        final Runnable runnable = null;
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                LaunchActivity.lambda$runImportRequest$36(i, iArr, runnable, dialogInterface);
            }
        });
        try {
            alertDialog.showDelayed(300L);
        } catch (Exception unused) {
        }
    }

    public void lambda$runImportRequest$35(final Uri uri, final int i, final AlertDialog alertDialog, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runImportRequest$34(tLObject, uri, i, alertDialog);
            }
        }, 2L);
    }

    public void lambda$runImportRequest$34(TLObject tLObject, Uri uri, int i, AlertDialog alertDialog) {
        boolean z;
        if (isFinishing()) {
            return;
        }
        if (tLObject != null && this.actionBarLayout != null) {
            TLRPC$TL_messages_historyImportParsed tLRPC$TL_messages_historyImportParsed = (TLRPC$TL_messages_historyImportParsed) tLObject;
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySelect", true);
            bundle.putString("importTitle", tLRPC$TL_messages_historyImportParsed.title);
            bundle.putBoolean("allowSwitchAccount", true);
            if (tLRPC$TL_messages_historyImportParsed.pm) {
                bundle.putInt("dialogsType", 12);
            } else if (tLRPC$TL_messages_historyImportParsed.group) {
                bundle.putInt("dialogsType", 11);
            } else {
                String uri2 = uri.toString();
                Iterator<String> it = MessagesController.getInstance(i).exportPrivateUri.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    } else if (uri2.contains(it.next())) {
                        bundle.putInt("dialogsType", 12);
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    Iterator<String> it2 = MessagesController.getInstance(i).exportGroupUri.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        if (uri2.contains(it2.next())) {
                            bundle.putInt("dialogsType", 11);
                            z = true;
                            break;
                        }
                    }
                    if (!z) {
                        bundle.putInt("dialogsType", 13);
                    }
                }
            }
            if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                SecretMediaViewer.getInstance().closePhoto(false, false);
            } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                PhotoViewer.getInstance().closePhoto(false, true);
            } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                ArticleViewer.getInstance().close(false, true);
            }
            StoryRecorder.destroyInstance();
            GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
            if (groupCallActivity != null) {
                groupCallActivity.dismiss();
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            if (AndroidUtilities.isTablet()) {
                this.actionBarLayout.rebuildFragments(1);
                this.rightActionBarLayout.rebuildFragments(1);
            } else {
                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            }
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            dialogsActivity.setDelegate(this);
            getActionBarLayout().presentFragment(dialogsActivity, !AndroidUtilities.isTablet() ? this.actionBarLayout.getFragmentStack().size() <= 1 || !(this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1) instanceof DialogsActivity) : this.layersActionBarLayout.getFragmentStack().size() <= 0 || !(this.layersActionBarLayout.getFragmentStack().get(this.layersActionBarLayout.getFragmentStack().size() - 1) instanceof DialogsActivity), false, true, false);
        } else {
            if (this.documentsUrisArray == null) {
                this.documentsUrisArray = new ArrayList<>();
            }
            this.documentsUrisArray.add(0, this.exportingChatUri);
            this.exportingChatUri = null;
            openDialogsToSend(true);
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$runImportRequest$36(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void openMessage(final long j, final int i, final String str, final Browser.Progress progress, int i2, final int i3) {
        TLRPC$Chat chat;
        if (j < 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j))) != null && ChatObject.isForum(chat)) {
            if (progress != null) {
                progress.init();
            }
            openForumFromLink(j, Integer.valueOf(i), str, new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.lambda$openMessage$37(Browser.Progress.this);
                }
            }, i2, i3);
            return;
        }
        if (progress != null) {
            progress.init();
        }
        final Bundle bundle = new Bundle();
        if (j >= 0) {
            bundle.putLong("user_id", j);
        } else {
            long j2 = -j;
            TLRPC$Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j2));
            if (chat2 != null && chat2.forum) {
                openForumFromLink(j, Integer.valueOf(i), str, new Runnable() {
                    @Override
                    public final void run() {
                        LaunchActivity.lambda$openMessage$38(Browser.Progress.this);
                    }
                }, i2, i3);
                return;
            }
            bundle.putLong("chat_id", j2);
        }
        bundle.putInt("message_id", i);
        ArrayList<BaseFragment> arrayList = mainFragmentsStack;
        final BaseFragment baseFragment = !arrayList.isEmpty() ? arrayList.get(arrayList.size() - 1) : null;
        if (baseFragment == null || MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, baseFragment)) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$openMessage$42(bundle, i, str, i3, j, progress, baseFragment);
                }
            });
        }
    }

    public static void lambda$openMessage$37(Browser.Progress progress) {
        if (progress != null) {
            progress.end();
        }
    }

    public static void lambda$openMessage$38(Browser.Progress progress) {
        if (progress != null) {
            progress.end();
        }
    }

    public void lambda$openMessage$42(final Bundle bundle, final int i, final String str, final int i2, final long j, final Browser.Progress progress, final BaseFragment baseFragment) {
        final ChatActivity chatActivity = new ChatActivity(bundle);
        chatActivity.setHighlightQuote(i, str, i2);
        if ((AndroidUtilities.isTablet() ? this.rightActionBarLayout : getActionBarLayout()).presentFragment(chatActivity) || j >= 0) {
            if (progress != null) {
                progress.end();
                return;
            }
            return;
        }
        TLRPC$TL_channels_getChannels tLRPC$TL_channels_getChannels = new TLRPC$TL_channels_getChannels();
        TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
        tLRPC$TL_inputChannel.channel_id = -j;
        tLRPC$TL_channels_getChannels.id.add(tLRPC$TL_inputChannel);
        final int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getChannels, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.this.lambda$openMessage$40(progress, j, i, baseFragment, bundle, chatActivity, str, i2, tLObject, tLRPC$TL_error);
            }
        });
        if (progress != null) {
            progress.onCancel(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$openMessage$41(sendRequest);
                }
            });
        }
    }

    public void lambda$openMessage$40(final Browser.Progress progress, final long j, final int i, final BaseFragment baseFragment, final Bundle bundle, final ChatActivity chatActivity, final String str, final int i2, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$openMessage$39(progress, tLObject, j, i, baseFragment, bundle, chatActivity, str, i2);
            }
        });
    }

    public void lambda$openMessage$39(Browser.Progress progress, TLObject tLObject, long j, int i, BaseFragment baseFragment, Bundle bundle, ChatActivity chatActivity, String str, int i2) {
        if (progress != null) {
            progress.end();
        }
        if (tLObject instanceof TLRPC$TL_messages_chats) {
            TLRPC$TL_messages_chats tLRPC$TL_messages_chats = (TLRPC$TL_messages_chats) tLObject;
            if (!tLRPC$TL_messages_chats.chats.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).putChats(tLRPC$TL_messages_chats.chats, false);
                TLRPC$Chat tLRPC$Chat = tLRPC$TL_messages_chats.chats.get(0);
                if (tLRPC$Chat != null && tLRPC$Chat.forum) {
                    openForumFromLink(-j, Integer.valueOf(i), null);
                }
                if (baseFragment == null || MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, baseFragment)) {
                    ChatActivity chatActivity2 = new ChatActivity(bundle);
                    chatActivity.setHighlightQuote(i, str, i2);
                    getActionBarLayout().presentFragment(chatActivity2);
                    return;
                }
                return;
            }
        }
        showAlertDialog(AlertsCreator.createNoAccessAlert(this, LocaleController.getString(R.string.DialogNotAvailable), LocaleController.getString(R.string.LinkNotFound), null));
    }

    public void lambda$openMessage$41(int i) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    private void runLinkRequest(final int r52, final java.lang.String r53, final java.lang.String r54, final java.lang.String r55, final java.lang.String r56, final java.lang.String r57, final java.lang.String r58, final java.lang.String r59, final java.lang.String r60, final java.lang.String r61, final java.lang.String r62, final java.lang.String r63, final java.lang.String r64, final boolean r65, final java.lang.Integer r66, final java.lang.Long r67, final java.lang.Long r68, final java.lang.Integer r69, final java.lang.String r70, final java.util.HashMap<java.lang.String, java.lang.String> r71, final java.lang.String r72, final java.lang.String r73, final java.lang.String r74, final java.lang.String r75, final org.telegram.tgnet.TLRPC$TL_wallPaper r76, final java.lang.String r77, final java.lang.String r78, final java.lang.String r79, final boolean r80, final java.lang.String r81, final int r82, final int r83, final java.lang.String r84, final java.lang.String r85, final java.lang.String r86, final java.lang.String r87, final java.lang.String r88, final org.telegram.messenger.browser.Browser.Progress r89, final boolean r90, final int r91, final boolean r92, final java.lang.String r93, final boolean r94, final boolean r95, final boolean r96) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.runLinkRequest(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Long, java.lang.Long, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_wallPaper, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.messenger.browser.Browser$Progress, boolean, int, boolean, java.lang.String, boolean, boolean, boolean):void");
    }

    public void lambda$runLinkRequest$45(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, Integer num, Long l, Long l2, Integer num2, String str13, HashMap hashMap, String str14, String str15, String str16, String str17, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str18, String str19, String str20, boolean z2, String str21, int i2, String str22, String str23, String str24, String str25, String str26, Browser.Progress progress, boolean z3, int i3, boolean z4, String str27, boolean z5, boolean z6, boolean z7, int i4) {
        if (i4 != i) {
            switchToAccount(i4, true);
        }
        runLinkRequest(i4, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tLRPC$TL_wallPaper, str18, str19, str20, z2, str21, 1, i2, str22, str23, str24, str25, str26, progress, z3, i3, z4, str27, z5, z6, z7);
    }

    public static void lambda$runLinkRequest$46(Browser.Progress progress, AlertDialog alertDialog) {
        if (progress != null) {
            progress.end();
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public void lambda$runLinkRequest$48(final int i, final String str, final Runnable runnable, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$47(tLObject, i, str, tLRPC$TL_error, runnable);
            }
        });
    }

    public void lambda$runLinkRequest$47(TLObject tLObject, int i, String str, TLRPC$TL_error tLRPC$TL_error, Runnable runnable) {
        if (tLObject instanceof TLRPC$User) {
            TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
            MessagesController.getInstance(i).putUser(tLRPC$User, false);
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", tLRPC$User.id);
            lambda$runLinkRequest$91(new ChatActivity(bundle));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("cant import contact token. token=");
            sb.append(str);
            sb.append(" err=");
            sb.append(tLRPC$TL_error == null ? null : tLRPC$TL_error.text);
            FileLog.e(sb.toString());
            BulletinFactory.of(mainFragmentsStack.get(r2.size() - 1)).createErrorBulletin(LocaleController.getString(R.string.NoUsernameFound)).show();
        }
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$runLinkRequest$50(final int i, final String str, final Runnable runnable, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$runLinkRequest$49(TLObject.this, i, str, runnable);
            }
        });
    }

    public static void lambda$runLinkRequest$49(TLObject tLObject, int i, String str, Runnable runnable) {
        ArrayList<TLRPC$Chat> arrayList;
        ArrayList<TLRPC$User> arrayList2;
        BaseFragment baseFragment = mainFragmentsStack.get(r0.size() - 1);
        if (tLObject instanceof TL_chatlists$chatlist_ChatlistInvite) {
            TL_chatlists$chatlist_ChatlistInvite tL_chatlists$chatlist_ChatlistInvite = (TL_chatlists$chatlist_ChatlistInvite) tLObject;
            boolean z = tL_chatlists$chatlist_ChatlistInvite instanceof TL_chatlists$TL_chatlists_chatlistInvite;
            if (z) {
                TL_chatlists$TL_chatlists_chatlistInvite tL_chatlists$TL_chatlists_chatlistInvite = (TL_chatlists$TL_chatlists_chatlistInvite) tL_chatlists$chatlist_ChatlistInvite;
                arrayList = tL_chatlists$TL_chatlists_chatlistInvite.chats;
                arrayList2 = tL_chatlists$TL_chatlists_chatlistInvite.users;
            } else if (tL_chatlists$chatlist_ChatlistInvite instanceof TL_chatlists$TL_chatlists_chatlistInviteAlready) {
                TL_chatlists$TL_chatlists_chatlistInviteAlready tL_chatlists$TL_chatlists_chatlistInviteAlready = (TL_chatlists$TL_chatlists_chatlistInviteAlready) tL_chatlists$chatlist_ChatlistInvite;
                arrayList = tL_chatlists$TL_chatlists_chatlistInviteAlready.chats;
                arrayList2 = tL_chatlists$TL_chatlists_chatlistInviteAlready.users;
            } else {
                arrayList = null;
                arrayList2 = null;
            }
            MessagesController.getInstance(i).putChats(arrayList, false);
            MessagesController.getInstance(i).putUsers(arrayList2, false);
            if (!z || !((TL_chatlists$TL_chatlists_chatlistInvite) tL_chatlists$chatlist_ChatlistInvite).peers.isEmpty()) {
                FolderBottomSheet folderBottomSheet = new FolderBottomSheet(baseFragment, str, tL_chatlists$chatlist_ChatlistInvite);
                if (baseFragment != null) {
                    baseFragment.showDialog(folderBottomSheet);
                } else {
                    folderBottomSheet.show();
                }
            } else {
                BulletinFactory.of(baseFragment).createErrorBulletin(LocaleController.getString(R.string.NoFolderFound)).show();
            }
        } else {
            BulletinFactory.of(baseFragment).createErrorBulletin(LocaleController.getString(R.string.NoFolderFound)).show();
        }
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$runLinkRequest$55(final TLRPC$TL_inputInvoiceSlug tLRPC$TL_inputInvoiceSlug, final Runnable runnable, final int i, final String str, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$54(tLRPC$TL_error, tLObject, tLRPC$TL_inputInvoiceSlug, runnable, i, str);
            }
        });
    }

    public void lambda$runLinkRequest$54(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_inputInvoiceSlug tLRPC$TL_inputInvoiceSlug, final Runnable runnable, int i, String str) {
        PaymentFormActivity paymentFormActivity;
        if (tLRPC$TL_error != null) {
            BulletinFactory.of(mainFragmentsStack.get(r7.size() - 1)).createErrorBulletin(LocaleController.getString(R.string.PaymentInvoiceLinkInvalid)).show();
        } else if (!isFinishing()) {
            if (tLObject instanceof TLRPC$TL_payments_paymentFormStars) {
                final Runnable runnable2 = this.navigateToPremiumGiftCallback;
                this.navigateToPremiumGiftCallback = null;
                StarsController.getInstance(this.currentAccount).openPaymentForm(null, tLRPC$TL_inputInvoiceSlug, (TLRPC$TL_payments_paymentFormStars) tLObject, new Runnable() {
                    @Override
                    public final void run() {
                        LaunchActivity.lambda$runLinkRequest$51(runnable);
                    }
                }, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        LaunchActivity.lambda$runLinkRequest$52(runnable2, (String) obj);
                    }
                });
                return;
            }
            if (tLObject instanceof TLRPC$PaymentForm) {
                TLRPC$PaymentForm tLRPC$PaymentForm = (TLRPC$PaymentForm) tLObject;
                MessagesController.getInstance(i).putUsers(tLRPC$PaymentForm.users, false);
                paymentFormActivity = new PaymentFormActivity(tLRPC$PaymentForm, str, getActionBarLayout().getLastFragment());
            } else {
                paymentFormActivity = tLObject instanceof TLRPC$PaymentReceipt ? new PaymentFormActivity((TLRPC$PaymentReceipt) tLObject) : null;
            }
            if (paymentFormActivity != null) {
                final Runnable runnable3 = this.navigateToPremiumGiftCallback;
                if (runnable3 != null) {
                    this.navigateToPremiumGiftCallback = null;
                    paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() {
                        @Override
                        public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                            LaunchActivity.lambda$runLinkRequest$53(runnable3, invoiceStatus);
                        }
                    });
                }
                lambda$runLinkRequest$91(paymentFormActivity);
            }
        }
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$runLinkRequest$51(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$runLinkRequest$52(Runnable runnable, String str) {
        if (runnable == null || !"paid".equals(str)) {
            return;
        }
        runnable.run();
    }

    public static void lambda$runLinkRequest$53(Runnable runnable, PaymentFormActivity.InvoiceStatus invoiceStatus) {
        if (invoiceStatus == PaymentFormActivity.InvoiceStatus.PAID) {
            runnable.run();
        }
    }

    public void lambda$runLinkRequest$73(final int i, final Runnable runnable, final String str, final String str2, final boolean z, final String str3, final String str4, final int i2, final String str5, final String str6, final String str7, final String str8, final String str9, final String str10, final String str11, final String str12, final String str13, final String str14, final String str15, final String str16, final boolean z2, final Integer num, final Long l, final Long l2, final Integer num2, final HashMap hashMap, final String str17, final String str18, final String str19, final String str20, final TLRPC$TL_wallPaper tLRPC$TL_wallPaper, final String str21, final String str22, final int i3, final int i4, final String str23, final String str24, final String str25, final Browser.Progress progress, final boolean z3, final boolean z4, final String str26, final boolean z5, final boolean z6, final boolean z7, final String str27, int[] iArr, final Long l3) {
        long j;
        long j2;
        boolean z8;
        final TLRPC$User user;
        if (!isFinishing()) {
            if (i != 0 && l3 != null) {
                MessagesController.getInstance(this.currentAccount).getStoriesController().resolveStoryLink(l3.longValue(), i, new com.google.android.exoplayer2.util.Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        LaunchActivity.this.lambda$runLinkRequest$56(runnable, l3, (TL_stories$StoryItem) obj);
                    }
                });
            } else {
                if (l3 != null && this.actionBarLayout != null && ((str == null && str2 == null) || ((str != null && l3.longValue() > 0) || ((str2 != null && l3.longValue() > 0) || ((z && l3.longValue() < 0) || (str3 != null && l3.longValue() < 0)))))) {
                    if (!TextUtils.isEmpty(str4) && (user = MessagesController.getInstance(i2).getUser(l3)) != null && user.bot) {
                        if (user.bot_attach_menu && !MediaDataController.getInstance(i2).botInAttachMenu(user.id)) {
                            TLRPC$TL_messages_getAttachMenuBot tLRPC$TL_messages_getAttachMenuBot = new TLRPC$TL_messages_getAttachMenuBot();
                            tLRPC$TL_messages_getAttachMenuBot.bot = MessagesController.getInstance(i2).getInputUser(l3.longValue());
                            ConnectionsManager.getInstance(i2).sendRequest(tLRPC$TL_messages_getAttachMenuBot, new RequestDelegate() {
                                @Override
                                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    LaunchActivity.this.lambda$runLinkRequest$65(i2, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14, str15, str16, z2, num, l, l2, num2, str, hashMap, str17, str18, str19, str20, tLRPC$TL_wallPaper, str21, str22, str2, z, str3, i3, i4, str23, str24, str25, progress, z3, i, z4, str26, z5, z6, z7, l3, str4, str27, user, runnable, tLObject, tLRPC$TL_error);
                                }
                            });
                            return;
                        }
                        processWebAppBot(i2, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14, str15, str16, z2, num, l, l2, num2, str, hashMap, str17, str18, str19, str20, tLRPC$TL_wallPaper, str21, str22, str2, z, str3, i3, i4, str23, str24, str25, str4, str27, progress, z3, i, z4, str26, user, runnable, false, false, z5, z6, z7);
                        return;
                    }
                    String str28 = str16;
                    if (z4 && ChatObject.isBoostSupported(MessagesController.getInstance(i2).getChat(Long.valueOf(-l3.longValue())))) {
                        processBoostDialog(l3, runnable, progress);
                        return;
                    }
                    if (str27 != null) {
                        TLRPC$User user2 = MessagesController.getInstance(i2).getUser(l3);
                        if (user2 != null && user2.bot) {
                            MessagesController.getInstance(i2).openApp(null, user2, 0, progress);
                        }
                    } else if (str23 != null && str24 == null) {
                        TLRPC$User user3 = MessagesController.getInstance(i2).getUser(l3);
                        if (user3 != null && user3.bot) {
                            if (user3.bot_attach_menu) {
                                processAttachMenuBot(i2, l3.longValue(), str25, user3, str23, str27);
                            } else {
                                ArrayList<BaseFragment> arrayList = mainFragmentsStack;
                                BulletinFactory.of(arrayList.get(arrayList.size() - 1)).createErrorBulletin(LocaleController.getString(R.string.BotCantAddToAttachMenu)).show();
                            }
                        } else {
                            ArrayList<BaseFragment> arrayList2 = mainFragmentsStack;
                            BulletinFactory.of(arrayList2.get(arrayList2.size() - 1)).createErrorBulletin(LocaleController.getString(R.string.BotSetAttachLinkNotBot)).show();
                        }
                    } else if (num != null && ((num2 != null || l2 != null) && l3.longValue() < 0)) {
                        int runCommentRequest = runCommentRequest(i2, runnable, num, num2, l2, MessagesController.getInstance(i2).getChat(Long.valueOf(-l3.longValue())));
                        iArr[0] = runCommentRequest;
                        if (runCommentRequest != 0) {
                            return;
                        }
                    } else if (str != null) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("onlySelect", true);
                        bundle.putBoolean("cantSendToChannels", true);
                        bundle.putInt("dialogsType", 1);
                        bundle.putString("selectAlertString", LocaleController.getString(R.string.SendGameToText));
                        bundle.putString("selectAlertStringGroup", LocaleController.getString(R.string.SendGameToGroupText));
                        DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                        final TLRPC$User user4 = MessagesController.getInstance(i2).getUser(l3);
                        dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                            @Override
                            public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList3, CharSequence charSequence, boolean z9, TopicsFragment topicsFragment) {
                                boolean lambda$runLinkRequest$66;
                                lambda$runLinkRequest$66 = LaunchActivity.this.lambda$runLinkRequest$66(str, i2, user4, dialogsActivity2, arrayList3, charSequence, z9, topicsFragment);
                                return lambda$runLinkRequest$66;
                            }
                        });
                        getActionBarLayout().presentFragment(dialogsActivity, !AndroidUtilities.isTablet() ? this.actionBarLayout.getFragmentStack().size() <= 1 || !(this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1) instanceof DialogsActivity) : this.layersActionBarLayout.getFragmentStack().size() <= 0 || !(this.layersActionBarLayout.getFragmentStack().get(this.layersActionBarLayout.getFragmentStack().size() - 1) instanceof DialogsActivity), true, true, false);
                        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                            SecretMediaViewer.getInstance().closePhoto(false, false);
                        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                            PhotoViewer.getInstance().closePhoto(false, true);
                        } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                            ArticleViewer.getInstance().close(false, true);
                        }
                        StoryRecorder.destroyInstance();
                        GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
                        if (groupCallActivity != null) {
                            groupCallActivity.dismiss();
                        }
                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        if (AndroidUtilities.isTablet()) {
                            this.actionBarLayout.rebuildFragments(1);
                            this.rightActionBarLayout.rebuildFragments(1);
                        } else {
                            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                        }
                    } else if (str10 != null || str11 != null) {
                        final TLRPC$User user5 = MessagesController.getInstance(i2).getUser(l3);
                        if (user5 == null || (user5.bot && user5.bot_nochats)) {
                            try {
                                ArrayList<BaseFragment> arrayList3 = mainFragmentsStack;
                                if (arrayList3.isEmpty()) {
                                    return;
                                }
                                BulletinFactory.of(arrayList3.get(arrayList3.size() - 1)).createErrorBulletin(LocaleController.getString(R.string.BotCantJoinGroups)).show();
                                return;
                            } catch (Exception e) {
                                FileLog.e(e);
                                return;
                            }
                        }
                        Bundle bundle2 = new Bundle();
                        bundle2.putBoolean("onlySelect", true);
                        bundle2.putInt("dialogsType", 2);
                        bundle2.putBoolean("resetDelegate", false);
                        bundle2.putBoolean("closeFragment", false);
                        bundle2.putBoolean("allowGroups", str10 != null);
                        bundle2.putBoolean("allowChannels", str11 != null);
                        String str29 = TextUtils.isEmpty(str10) ? TextUtils.isEmpty(str11) ? null : str11 : str10;
                        final DialogsActivity dialogsActivity2 = new DialogsActivity(bundle2);
                        final String str30 = str29;
                        dialogsActivity2.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                            @Override
                            public final boolean didSelectDialogs(DialogsActivity dialogsActivity3, ArrayList arrayList4, CharSequence charSequence, boolean z9, TopicsFragment topicsFragment) {
                                boolean lambda$runLinkRequest$71;
                                lambda$runLinkRequest$71 = LaunchActivity.this.lambda$runLinkRequest$71(i2, user5, str12, str30, dialogsActivity2, dialogsActivity3, arrayList4, charSequence, z9, topicsFragment);
                                return lambda$runLinkRequest$71;
                            }
                        });
                        lambda$runLinkRequest$91(dialogsActivity2);
                    } else {
                        Bundle bundle3 = new Bundle();
                        TLRPC$User user6 = MessagesController.getInstance(i2).getUser(l3);
                        if (l3.longValue() < 0) {
                            bundle3.putLong("chat_id", -l3.longValue());
                            j2 = l3.longValue();
                        } else {
                            bundle3.putLong("user_id", l3.longValue());
                            long longValue = l3.longValue();
                            if (str28 != null) {
                                if (str28.startsWith("@")) {
                                    StringBuilder sb = new StringBuilder();
                                    j = longValue;
                                    sb.append(" ");
                                    sb.append(str28);
                                    str28 = sb.toString();
                                } else {
                                    j = longValue;
                                }
                                bundle3.putString("start_text", str28);
                            } else {
                                j = longValue;
                            }
                            j2 = j;
                        }
                        if (str9 == null || user6 == null || !user6.bot) {
                            z8 = false;
                        } else {
                            bundle3.putString("botUser", str9);
                            z8 = true;
                        }
                        if (this.navigateToPremiumBot) {
                            this.navigateToPremiumBot = false;
                            bundle3.putBoolean("premium_bot", true);
                        }
                        if (num != null) {
                            bundle3.putInt("message_id", num.intValue());
                        }
                        if (str2 != null) {
                            bundle3.putString("voicechat", str2);
                        }
                        if (z) {
                            bundle3.putBoolean("videochat", true);
                        }
                        if (str3 != null) {
                            bundle3.putString("livestream", str3);
                        }
                        if (i4 >= 0) {
                            bundle3.putInt("video_timestamp", i4);
                        }
                        if (str24 != null) {
                            bundle3.putString("attach_bot", str24);
                        }
                        if (str23 != null) {
                            bundle3.putString("attach_bot_start_command", str23);
                        }
                        ArrayList<BaseFragment> arrayList4 = mainFragmentsStack;
                        BaseFragment baseFragment = (arrayList4.isEmpty() || str2 != null) ? null : arrayList4.get(arrayList4.size() - 1);
                        if (baseFragment == null || MessagesController.getInstance(i2).checkCanOpenChat(bundle3, baseFragment)) {
                            boolean z9 = (baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).getDialogId() == j2;
                            if (z8 && z9) {
                                ((ChatActivity) baseFragment).setBotUser(str9);
                            } else if (str24 != null && z9) {
                                ((ChatActivity) baseFragment).openAttachBotLayout(str24);
                            } else {
                                long j3 = -j2;
                                TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j3));
                                if (z7) {
                                    try {
                                        runnable.run();
                                    } catch (Exception e2) {
                                        FileLog.e(e2);
                                    }
                                    if (isFinishing()) {
                                        return;
                                    }
                                    Bundle bundle4 = new Bundle();
                                    if (l3.longValue() < 0) {
                                        bundle4.putLong("chat_id", -l3.longValue());
                                    } else {
                                        bundle4.putLong("user_id", l3.longValue());
                                    }
                                    getActionBarLayout().presentFragment(new ProfileActivity(bundle4));
                                    return;
                                }
                                if (chat != null && chat.forum) {
                                    Long valueOf = (l2 != null || num == null) ? l2 : Long.valueOf(num.intValue());
                                    if (valueOf != null && valueOf.longValue() != 0) {
                                        openForumFromLink(j2, num, new Runnable() {
                                            @Override
                                            public final void run() {
                                                LaunchActivity.lambda$runLinkRequest$72(runnable);
                                            }
                                        });
                                        return;
                                    }
                                    Bundle bundle5 = new Bundle();
                                    bundle5.putLong("chat_id", j3);
                                    lambda$runLinkRequest$91(TopicsFragment.getTopicsOrChat(this, bundle5));
                                    try {
                                        runnable.run();
                                        return;
                                    } catch (Exception e3) {
                                        FileLog.e(e3);
                                        return;
                                    }
                                }
                                MessagesController.getInstance(i2).ensureMessagesLoaded(j2, num == null ? 0 : num.intValue(), new AnonymousClass18(runnable, str3, baseFragment, j2, num, bundle3));
                                return;
                            }
                        }
                    }
                } else {
                    try {
                        BaseFragment lastFragment = getLastFragment();
                        if (lastFragment != null) {
                            if (lastFragment instanceof ChatActivity) {
                                ((ChatActivity) lastFragment).shakeContent();
                            }
                            if (AndroidUtilities.isNumeric(str5)) {
                                BulletinFactory.of(lastFragment).createErrorBulletin(LocaleController.getString(R.string.NoPhoneFound)).show();
                            } else {
                                BulletinFactory.of(lastFragment).createErrorBulletin(LocaleController.getString(R.string.NoUsernameFound)).show();
                            }
                        }
                    } catch (Exception e4) {
                        FileLog.e(e4);
                    }
                }
                try {
                    runnable.run();
                } catch (Exception e5) {
                    FileLog.e(e5);
                }
            }
        }
    }

    public void lambda$runLinkRequest$56(Runnable runnable, Long l, TL_stories$StoryItem tL_stories$StoryItem) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        BaseFragment lastFragment = getLastFragment();
        if (tL_stories$StoryItem == null) {
            BulletinFactory global = BulletinFactory.global();
            if (global != null) {
                global.createSimpleBulletin(R.raw.story_bomb2, LocaleController.getString(R.string.StoryNotFound)).show();
                return;
            }
            return;
        }
        if (tL_stories$StoryItem instanceof TL_stories$TL_storyItemDeleted) {
            BulletinFactory global2 = BulletinFactory.global();
            if (global2 != null) {
                global2.createSimpleBulletin(R.raw.story_bomb1, LocaleController.getString(R.string.StoryNotFound)).show();
                return;
            }
            return;
        }
        if (lastFragment != null) {
            tL_stories$StoryItem.dialogId = l.longValue();
            StoryViewer createOverlayStoryViewer = lastFragment.createOverlayStoryViewer();
            createOverlayStoryViewer.instantClose();
            createOverlayStoryViewer.open(this, tL_stories$StoryItem, (StoryViewer.PlaceProvider) null);
        }
    }

    public void lambda$runLinkRequest$65(final int i, final String str, final String str2, final String str3, final String str4, final String str5, final String str6, final String str7, final String str8, final String str9, final String str10, final String str11, final String str12, final boolean z, final Integer num, final Long l, final Long l2, final Integer num2, final String str13, final HashMap hashMap, final String str14, final String str15, final String str16, final String str17, final TLRPC$TL_wallPaper tLRPC$TL_wallPaper, final String str18, final String str19, final String str20, final boolean z2, final String str21, final int i2, final int i3, final String str22, final String str23, final String str24, final Browser.Progress progress, final boolean z3, final int i4, final boolean z4, final String str25, final boolean z5, final boolean z6, final boolean z7, final Long l3, final String str26, final String str27, final TLRPC$User tLRPC$User, final Runnable runnable, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$64(tLRPC$TL_error, i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tLRPC$TL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, progress, z3, i4, z4, str25, z5, z6, z7, tLObject, l3, str26, str27, tLRPC$User, runnable);
            }
        });
    }

    public void lambda$runLinkRequest$57(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, Integer num, Long l, Long l2, Integer num2, String str13, HashMap hashMap, String str14, String str15, String str16, String str17, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str18, String str19, String str20, boolean z2, String str21, int i2, int i3, String str22, String str23, String str24, Browser.Progress progress, boolean z3, int i4, boolean z4, String str25, boolean z5, boolean z6, boolean z7) {
        runLinkRequest(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tLRPC$TL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, null, null, progress, z3, i4, z4, str25, z5, z6, z7);
    }

    public void lambda$runLinkRequest$64(TLRPC$TL_error tLRPC$TL_error, final int i, final String str, final String str2, final String str3, final String str4, final String str5, final String str6, final String str7, final String str8, final String str9, final String str10, final String str11, final String str12, final boolean z, final Integer num, final Long l, final Long l2, final Integer num2, final String str13, final HashMap hashMap, final String str14, final String str15, final String str16, final String str17, final TLRPC$TL_wallPaper tLRPC$TL_wallPaper, final String str18, final String str19, final String str20, final boolean z2, final String str21, final int i2, final int i3, final String str22, final String str23, final String str24, final Browser.Progress progress, final boolean z3, final int i4, final boolean z4, final String str25, final boolean z5, final boolean z6, final boolean z7, TLObject tLObject, final Long l3, final String str26, final String str27, final TLRPC$User tLRPC$User, final Runnable runnable) {
        LaunchActivity launchActivity;
        ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11 chatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11;
        if (tLRPC$TL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$runLinkRequest$57(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tLRPC$TL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, progress, z3, i4, z4, str25, z5, z6, z7);
                }
            });
            return;
        }
        if (tLObject instanceof TLRPC$TL_attachMenuBotsBot) {
            final TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot = ((TLRPC$TL_attachMenuBotsBot) tLObject).bot;
            final boolean z8 = tLRPC$TL_attachMenuBot != null && (tLRPC$TL_attachMenuBot.show_in_side_menu || tLRPC$TL_attachMenuBot.show_in_attach_menu);
            if ((tLRPC$TL_attachMenuBot.inactive || tLRPC$TL_attachMenuBot.side_menu_disclaimer_needed) && z8) {
                com.google.android.exoplayer2.util.Consumer consumer = new com.google.android.exoplayer2.util.Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        LaunchActivity.this.lambda$runLinkRequest$60(tLRPC$TL_attachMenuBot, i, l3, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tLRPC$TL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, str26, str27, progress, z3, i4, z4, str25, tLRPC$User, runnable, z8, z5, z6, z7, (Boolean) obj);
                    }
                };
                if (progress != null) {
                    chatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11 = new ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11(progress);
                    launchActivity = this;
                } else {
                    launchActivity = this;
                    chatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11 = null;
                }
                WebAppDisclaimerAlert.show(launchActivity, consumer, null, chatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11);
                return;
            }
            if (tLRPC$TL_attachMenuBot.request_write_access || z3) {
                final AtomicBoolean atomicBoolean = new AtomicBoolean(true);
                AlertsCreator.createBotLaunchAlert(getLastFragment(), atomicBoolean, tLRPC$User, new Runnable() {
                    @Override
                    public final void run() {
                        LaunchActivity.this.lambda$runLinkRequest$63(tLRPC$TL_attachMenuBot, atomicBoolean, i, l3, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tLRPC$TL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, str26, str27, progress, z3, i4, z4, str25, tLRPC$User, runnable, z5, z6, z7);
                    }
                });
            } else {
                processWebAppBot(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tLRPC$TL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, str26, str27, progress, z3, i4, z4, str25, tLRPC$User, runnable, false, false, z5, z6, z7);
            }
        }
    }

    public void lambda$runLinkRequest$60(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, final int i, Long l, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, Integer num, Long l2, Long l3, Integer num2, String str13, HashMap hashMap, String str14, String str15, String str16, String str17, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str18, String str19, String str20, boolean z2, String str21, int i2, int i3, String str22, String str23, String str24, String str25, String str26, Browser.Progress progress, boolean z3, int i4, boolean z4, String str27, TLRPC$User tLRPC$User, Runnable runnable, boolean z5, boolean z6, boolean z7, boolean z8, Boolean bool) {
        tLRPC$TL_attachMenuBot.inactive = false;
        tLRPC$TL_attachMenuBot.request_write_access = false;
        TLRPC$TL_messages_toggleBotInAttachMenu tLRPC$TL_messages_toggleBotInAttachMenu = new TLRPC$TL_messages_toggleBotInAttachMenu();
        tLRPC$TL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(i).getInputUser(l.longValue());
        tLRPC$TL_messages_toggleBotInAttachMenu.enabled = true;
        tLRPC$TL_messages_toggleBotInAttachMenu.write_allowed = true;
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.lambda$runLinkRequest$59(i, tLObject, tLRPC$TL_error);
            }
        }, 66);
        processWebAppBot(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l2, l3, num2, str13, hashMap, str14, str15, str16, str17, tLRPC$TL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, str25, str26, progress, z3, i4, z4, str27, tLRPC$User, runnable, z5, true, z6, z7, z8);
    }

    public static void lambda$runLinkRequest$59(final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$runLinkRequest$58(TLObject.this, i);
            }
        });
    }

    public static void lambda$runLinkRequest$58(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            MediaDataController.getInstance(i).loadAttachMenuBots(false, true, null);
        }
    }

    public void lambda$runLinkRequest$63(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, AtomicBoolean atomicBoolean, final int i, Long l, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, Integer num, Long l2, Long l3, Integer num2, String str13, HashMap hashMap, String str14, String str15, String str16, String str17, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str18, String str19, String str20, boolean z2, String str21, int i2, int i3, String str22, String str23, String str24, String str25, String str26, Browser.Progress progress, boolean z3, int i4, boolean z4, String str27, TLRPC$User tLRPC$User, Runnable runnable, boolean z5, boolean z6, boolean z7) {
        tLRPC$TL_attachMenuBot.inactive = false;
        tLRPC$TL_attachMenuBot.request_write_access = !atomicBoolean.get();
        TLRPC$TL_messages_toggleBotInAttachMenu tLRPC$TL_messages_toggleBotInAttachMenu = new TLRPC$TL_messages_toggleBotInAttachMenu();
        tLRPC$TL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(i).getInputUser(l.longValue());
        tLRPC$TL_messages_toggleBotInAttachMenu.write_allowed = atomicBoolean.get();
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.lambda$runLinkRequest$62(i, tLObject, tLRPC$TL_error);
            }
        }, 66);
        processWebAppBot(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l2, l3, num2, str13, hashMap, str14, str15, str16, str17, tLRPC$TL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, str25, str26, progress, z3, i4, z4, str27, tLRPC$User, runnable, false, false, z5, z6, z7);
    }

    public static void lambda$runLinkRequest$62(final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$runLinkRequest$61(TLObject.this, i);
            }
        });
    }

    public static void lambda$runLinkRequest$61(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            MediaDataController.getInstance(i).loadAttachMenuBots(false, true, null);
        }
    }

    public boolean lambda$runLinkRequest$66(String str, int i, TLRPC$User tLRPC$User, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, TopicsFragment topicsFragment) {
        long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
        TLRPC$TL_inputMediaGame tLRPC$TL_inputMediaGame = new TLRPC$TL_inputMediaGame();
        TLRPC$TL_inputGameShortName tLRPC$TL_inputGameShortName = new TLRPC$TL_inputGameShortName();
        tLRPC$TL_inputMediaGame.id = tLRPC$TL_inputGameShortName;
        tLRPC$TL_inputGameShortName.short_name = str;
        tLRPC$TL_inputGameShortName.bot_id = MessagesController.getInstance(i).getInputUser(tLRPC$User);
        SendMessagesHelper.getInstance(i).sendGame(MessagesController.getInstance(i).getInputPeer(j), tLRPC$TL_inputMediaGame, 0L, 0L);
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(j)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j));
        } else if (DialogObject.isUserDialog(j)) {
            bundle.putLong("user_id", j);
        } else {
            bundle.putLong("chat_id", -j);
        }
        if (MessagesController.getInstance(i).checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            getActionBarLayout().presentFragment(new ChatActivity(bundle), true, false, true, false);
        }
        return true;
    }

    public boolean lambda$runLinkRequest$71(final int i, final TLRPC$User tLRPC$User, final String str, final String str2, final DialogsActivity dialogsActivity, DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, TopicsFragment topicsFragment) {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        final long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
        final TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j));
        if (chat != null && (chat.creator || ((tLRPC$TL_chatAdminRights = chat.admin_rights) != null && tLRPC$TL_chatAdminRights.add_admins))) {
            MessagesController.getInstance(i).checkIsInChat(false, chat, tLRPC$User, new MessagesController.IsInChatCheckedCallback() {
                @Override
                public final void run(boolean z2, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2, String str3) {
                    LaunchActivity.this.lambda$runLinkRequest$69(str, str2, i, chat, dialogsActivity, tLRPC$User, j, z2, tLRPC$TL_chatAdminRights2, str3);
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            int i2 = R.string.AddBot;
            builder.setTitle(LocaleController.getString(i2));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, UserObject.getUserName(tLRPC$User), chat == null ? "" : chat.title)));
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            builder.setPositiveButton(LocaleController.getString(i2), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i3) {
                    LaunchActivity.this.lambda$runLinkRequest$70(j, i, tLRPC$User, str2, dialogInterface, i3);
                }
            });
            builder.show();
        }
        return true;
    }

    public void lambda$runLinkRequest$69(final String str, final String str2, final int i, final TLRPC$Chat tLRPC$Chat, final DialogsActivity dialogsActivity, final TLRPC$User tLRPC$User, final long j, final boolean z, final TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, final String str3) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$68(str, tLRPC$TL_chatAdminRights, z, str2, i, tLRPC$Chat, dialogsActivity, tLRPC$User, j, str3);
            }
        });
    }

    public void lambda$runLinkRequest$68(java.lang.String r24, org.telegram.tgnet.TLRPC$TL_chatAdminRights r25, boolean r26, java.lang.String r27, final int r28, final org.telegram.tgnet.TLRPC$Chat r29, final org.telegram.ui.DialogsActivity r30, org.telegram.tgnet.TLRPC$User r31, long r32, java.lang.String r34) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$68(java.lang.String, org.telegram.tgnet.TLRPC$TL_chatAdminRights, boolean, java.lang.String, int, org.telegram.tgnet.TLRPC$Chat, org.telegram.ui.DialogsActivity, org.telegram.tgnet.TLRPC$User, long, java.lang.String):void");
    }

    public void lambda$runLinkRequest$67(int i, TLRPC$Chat tLRPC$Chat, DialogsActivity dialogsActivity) {
        NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        bundle.putLong("chat_id", tLRPC$Chat.id);
        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
            presentFragment(new ChatActivity(bundle), true, false);
        }
    }

    public void lambda$runLinkRequest$70(long j, int i, TLRPC$User tLRPC$User, String str, DialogInterface dialogInterface, int i2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        long j2 = -j;
        bundle.putLong("chat_id", j2);
        ChatActivity chatActivity = new ChatActivity(bundle);
        NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
        MessagesController.getInstance(i).addUserToChat(j2, tLRPC$User, 0, str, chatActivity, null);
        getActionBarLayout().presentFragment(chatActivity, true, false, true, false);
    }

    public static void lambda$runLinkRequest$72(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public class AnonymousClass18 implements MessagesController.MessagesLoadedCallback {
        final Bundle val$args;
        final long val$dialog_id;
        final Runnable val$dismissLoading;
        final BaseFragment val$lastFragment;
        final String val$livestream;
        final Integer val$messageId;

        AnonymousClass18(Runnable runnable, String str, BaseFragment baseFragment, long j, Integer num, Bundle bundle) {
            this.val$dismissLoading = runnable;
            this.val$livestream = str;
            this.val$lastFragment = baseFragment;
            this.val$dialog_id = j;
            this.val$messageId = num;
            this.val$args = bundle;
        }

        @Override
        public void onMessagesLoaded(boolean z) {
            BaseFragment chatActivity;
            try {
                this.val$dismissLoading.run();
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (LaunchActivity.this.isFinishing()) {
                return;
            }
            if (this.val$livestream != null) {
                BaseFragment baseFragment = this.val$lastFragment;
                if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).getDialogId() == this.val$dialog_id) {
                    chatActivity = this.val$lastFragment;
                    final BaseFragment baseFragment2 = chatActivity;
                    final String str = this.val$livestream;
                    final long j = this.val$dialog_id;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            LaunchActivity.AnonymousClass18.this.lambda$onMessagesLoaded$2(str, j, baseFragment2);
                        }
                    }, 150L);
                }
            }
            BaseFragment baseFragment3 = this.val$lastFragment;
            if ((baseFragment3 instanceof ChatActivity) && ((ChatActivity) baseFragment3).getDialogId() == this.val$dialog_id && this.val$messageId == null) {
                ChatActivity chatActivity2 = (ChatActivity) this.val$lastFragment;
                AndroidUtilities.shakeViewSpring(chatActivity2.getChatListView(), 5.0f);
                BotWebViewVibrationEffect.APP_ERROR.vibrate();
                ChatActivityEnterView chatActivityEnterView = chatActivity2.getChatActivityEnterView();
                for (int i = 0; i < chatActivityEnterView.getChildCount(); i++) {
                    AndroidUtilities.shakeViewSpring(chatActivityEnterView.getChildAt(i), 5.0f);
                }
                ActionBar actionBar = chatActivity2.getActionBar();
                for (int i2 = 0; i2 < actionBar.getChildCount(); i2++) {
                    AndroidUtilities.shakeViewSpring(actionBar.getChildAt(i2), 5.0f);
                }
                chatActivity = this.val$lastFragment;
            } else {
                chatActivity = new ChatActivity(this.val$args);
                LaunchActivity.this.getActionBarLayout().presentFragment(chatActivity);
            }
            final BaseFragment baseFragment22 = chatActivity;
            final String str2 = this.val$livestream;
            final long j2 = this.val$dialog_id;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.AnonymousClass18.this.lambda$onMessagesLoaded$2(str2, j2, baseFragment22);
                }
            }, 150L);
        }

        public void lambda$onMessagesLoaded$2(String str, final long j, final BaseFragment baseFragment) {
            if (str != null) {
                final AccountInstance accountInstance = AccountInstance.getInstance(LaunchActivity.this.currentAccount);
                long j2 = -j;
                if (accountInstance.getMessagesController().getGroupCall(j2, false) != null) {
                    VoIPHelper.startCall(accountInstance.getMessagesController().getChat(Long.valueOf(j2)), accountInstance.getMessagesController().getInputPeer(j), null, false, Boolean.valueOf(!r0.call.rtmp_stream), LaunchActivity.this, baseFragment, accountInstance);
                    return;
                }
                TLRPC$ChatFull chatFull = accountInstance.getMessagesController().getChatFull(j2);
                if (chatFull != null) {
                    if (chatFull.call == null) {
                        if (baseFragment.getParentActivity() != null) {
                            BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.linkbroken, LocaleController.getString(R.string.InviteExpired)).show();
                            return;
                        }
                        return;
                    }
                    accountInstance.getMessagesController().getGroupCall(j2, true, new Runnable() {
                        @Override
                        public final void run() {
                            LaunchActivity.AnonymousClass18.this.lambda$onMessagesLoaded$1(accountInstance, j, baseFragment);
                        }
                    });
                }
            }
        }

        public void lambda$onMessagesLoaded$1(final AccountInstance accountInstance, final long j, final BaseFragment baseFragment) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.AnonymousClass18.this.lambda$onMessagesLoaded$0(accountInstance, j, baseFragment);
                }
            });
        }

        public void lambda$onMessagesLoaded$0(AccountInstance accountInstance, long j, BaseFragment baseFragment) {
            long j2 = -j;
            ChatObject.Call groupCall = accountInstance.getMessagesController().getGroupCall(j2, false);
            VoIPHelper.startCall(accountInstance.getMessagesController().getChat(Long.valueOf(j2)), accountInstance.getMessagesController().getInputPeer(j), null, false, Boolean.valueOf(groupCall == null || !groupCall.call.rtmp_stream), LaunchActivity.this, baseFragment, accountInstance);
        }

        @Override
        public void onError() {
            if (!LaunchActivity.this.isFinishing()) {
                AlertsCreator.showSimpleAlert((BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1), LocaleController.getString(R.string.JoinToGroupErrorNotExist));
            }
            try {
                this.val$dismissLoading.run();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void lambda$runLinkRequest$79(final int i, final AlertDialog alertDialog, final Runnable runnable, final String str, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$78(tLRPC$TL_error, tLObject, i, alertDialog, runnable, str);
            }
        });
    }

    public void lambda$runLinkRequest$78(org.telegram.tgnet.TLRPC$TL_error r10, org.telegram.tgnet.TLObject r11, int r12, org.telegram.ui.ActionBar.AlertDialog r13, final java.lang.Runnable r14, java.lang.String r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.lambda$runLinkRequest$78(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, int, org.telegram.ui.ActionBar.AlertDialog, java.lang.Runnable, java.lang.String):void");
    }

    public static void lambda$runLinkRequest$74(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    public void lambda$runLinkRequest$77(final long j, String str, final Long l) {
        if (!"paid".equals(str) || l.longValue() == 0) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$76(l, j);
            }
        });
    }

    public void lambda$runLinkRequest$76(Long l, final long j) {
        BaseFragment safeLastFragment = getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        final ChatActivity of = ChatActivity.of(l.longValue());
        safeLastFragment.presentFragment(of);
        final TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-l.longValue()));
        if (chat != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.lambda$runLinkRequest$75(BaseFragment.this, j, chat);
                }
            }, 250L);
        }
    }

    public static void lambda$runLinkRequest$75(BaseFragment baseFragment, long j, TLRPC$Chat tLRPC$Chat) {
        BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.stars_send, LocaleController.getString(R.string.StarsSubscriptionCompleted), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsSubscriptionCompletedText", (int) j, tLRPC$Chat.title))).show(true);
    }

    public void lambda$runLinkRequest$81(final int i, final Runnable runnable, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(i).processUpdates((TLRPC$Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$80(runnable, tLRPC$TL_error, tLObject, i);
            }
        });
    }

    public void lambda$runLinkRequest$80(Runnable runnable, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
        if (isFinishing()) {
            return;
        }
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLRPC$TL_error == null) {
            if (this.actionBarLayout != null) {
                TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
                if (tLRPC$Updates.chats.isEmpty()) {
                    return;
                }
                TLRPC$Chat tLRPC$Chat = tLRPC$Updates.chats.get(0);
                tLRPC$Chat.left = false;
                tLRPC$Chat.kicked = false;
                MessagesController.getInstance(i).putUsers(tLRPC$Updates.users, false);
                MessagesController.getInstance(i).putChats(tLRPC$Updates.chats, false);
                Bundle bundle = new Bundle();
                bundle.putLong("chat_id", tLRPC$Chat.id);
                ArrayList<BaseFragment> arrayList = mainFragmentsStack;
                if (arrayList.isEmpty() || MessagesController.getInstance(i).checkCanOpenChat(bundle, arrayList.get(arrayList.size() - 1))) {
                    ChatActivity chatActivity = new ChatActivity(bundle);
                    NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
                    getActionBarLayout().presentFragment(chatActivity, false, true, true, false);
                    return;
                }
                return;
            }
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(LocaleController.getString(R.string.AppName));
        if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            builder.setMessage(LocaleController.getString(R.string.FloodWait));
        } else if (tLRPC$TL_error.text.equals("USERS_TOO_MUCH")) {
            builder.setMessage(LocaleController.getString(R.string.JoinToGroupErrorFull));
        } else {
            builder.setMessage(LocaleController.getString(R.string.JoinToGroupErrorNotExist));
        }
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        showAlertDialog(builder);
    }

    public boolean lambda$runLinkRequest$82(boolean z, int i, String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z2, TopicsFragment topicsFragment) {
        long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        bundle.putBoolean("hasUrl", z);
        if (DialogObject.isEncryptedDialog(j)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j));
        } else if (DialogObject.isUserDialog(j)) {
            bundle.putLong("user_id", j);
        } else {
            bundle.putLong("chat_id", -j);
        }
        if (MessagesController.getInstance(i).checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            MediaDataController.getInstance(i).saveDraft(j, 0, str, null, null, false, 0L);
            getActionBarLayout().presentFragment(new ChatActivity(bundle), true, false, true, false);
        }
        return true;
    }

    public void lambda$runLinkRequest$86(int[] iArr, final int i, final Runnable runnable, final TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, final String str, final String str2, final String str3, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        final TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm = (TLRPC$TL_account_authorizationForm) tLObject;
        if (tLRPC$TL_account_authorizationForm != null) {
            iArr[0] = ConnectionsManager.getInstance(i).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error2) {
                    LaunchActivity.this.lambda$runLinkRequest$84(runnable, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3, tLObject2, tLRPC$TL_error2);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$runLinkRequest$85(runnable, tLRPC$TL_error);
                }
            });
        }
    }

    public void lambda$runLinkRequest$84(final Runnable runnable, final int i, final TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, final TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, final String str, final String str2, final String str3, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$83(runnable, tLObject, i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_getAuthorizationForm, str, str2, str3);
            }
        });
    }

    public void lambda$runLinkRequest$83(Runnable runnable, TLObject tLObject, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject != null) {
            MessagesController.getInstance(i).putUsers(tLRPC$TL_account_authorizationForm.users, false);
            lambda$runLinkRequest$91(new PassportActivity(5, tLRPC$TL_account_getAuthorizationForm.bot_id, tLRPC$TL_account_getAuthorizationForm.scope, tLRPC$TL_account_getAuthorizationForm.public_key, str, str2, str3, tLRPC$TL_account_authorizationForm, (TLRPC$account_Password) tLObject));
        }
    }

    public void lambda$runLinkRequest$85(Runnable runnable, TLRPC$TL_error tLRPC$TL_error) {
        try {
            runnable.run();
            if ("APP_VERSION_OUTDATED".equals(tLRPC$TL_error.text)) {
                AlertsCreator.showUpdateAppAlert(this, LocaleController.getString(R.string.UpdateAppAlert), true);
            } else {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(R.string.ErrorOccurred) + "\n" + tLRPC$TL_error.text));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$runLinkRequest$88(final Runnable runnable, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$87(runnable, tLObject);
            }
        });
    }

    public void lambda$runLinkRequest$87(Runnable runnable, TLObject tLObject) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TLRPC$TL_help_deepLinkInfo) {
            TLRPC$TL_help_deepLinkInfo tLRPC$TL_help_deepLinkInfo = (TLRPC$TL_help_deepLinkInfo) tLObject;
            AlertsCreator.showUpdateAppAlert(this, tLRPC$TL_help_deepLinkInfo.message, tLRPC$TL_help_deepLinkInfo.update_app);
        }
    }

    public void lambda$runLinkRequest$90(final Runnable runnable, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$89(runnable, tLObject, tLRPC$TL_error);
            }
        });
    }

    public void lambda$runLinkRequest$89(Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TLRPC$TL_langPackLanguage) {
            showAlertDialog(AlertsCreator.createLanguageAlert(this, (TLRPC$TL_langPackLanguage) tLObject));
            return;
        }
        if (tLRPC$TL_error != null) {
            if ("LANG_CODE_NOT_SUPPORTED".equals(tLRPC$TL_error.text)) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(R.string.LanguageUnsupportedError)));
                return;
            }
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(R.string.ErrorOccurred) + "\n" + tLRPC$TL_error.text));
        }
    }

    public void lambda$runLinkRequest$93(final Runnable runnable, final TLRPC$TL_wallPaper tLRPC$TL_wallPaper, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$92(runnable, tLObject, tLRPC$TL_wallPaper, tLRPC$TL_error);
            }
        });
    }

    public void lambda$runLinkRequest$92(Runnable runnable, TLObject tLObject, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, TLRPC$TL_error tLRPC$TL_error) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TLRPC$TL_wallPaper) {
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper2 = (TLRPC$TL_wallPaper) tLObject;
            if (tLRPC$TL_wallPaper2.pattern) {
                String str = tLRPC$TL_wallPaper2.slug;
                TLRPC$WallPaperSettings tLRPC$WallPaperSettings = tLRPC$TL_wallPaper.settings;
                WallpapersListActivity.ColorWallpaper colorWallpaper = new WallpapersListActivity.ColorWallpaper(str, tLRPC$WallPaperSettings.background_color, tLRPC$WallPaperSettings.second_background_color, tLRPC$WallPaperSettings.third_background_color, tLRPC$WallPaperSettings.fourth_background_color, AndroidUtilities.getWallpaperRotation(tLRPC$WallPaperSettings.rotation, false), r0.intensity / 100.0f, tLRPC$TL_wallPaper.settings.motion, null);
                colorWallpaper.pattern = tLRPC$TL_wallPaper2;
                tLRPC$TL_wallPaper2 = colorWallpaper;
            }
            ThemePreviewActivity themePreviewActivity = new ThemePreviewActivity(tLRPC$TL_wallPaper2, null, true, false);
            TLRPC$WallPaperSettings tLRPC$WallPaperSettings2 = tLRPC$TL_wallPaper.settings;
            themePreviewActivity.setInitialModes(tLRPC$WallPaperSettings2.blur, tLRPC$WallPaperSettings2.motion, tLRPC$WallPaperSettings2.intensity);
            lambda$runLinkRequest$91(themePreviewActivity);
            return;
        }
        showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(R.string.ErrorOccurred) + "\n" + tLRPC$TL_error.text));
    }

    public void lambda$runLinkRequest$94(Browser.Progress progress) {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
        if (progress != null) {
            progress.end();
        }
    }

    public void lambda$runLinkRequest$96(final AlertDialog alertDialog, final Runnable runnable, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$95(tLObject, alertDialog, runnable, tLRPC$TL_error);
            }
        });
    }

    public void lambda$runLinkRequest$95(TLObject tLObject, AlertDialog alertDialog, Runnable runnable, TLRPC$TL_error tLRPC$TL_error) {
        char c;
        if (tLObject instanceof TLRPC$TL_theme) {
            TLRPC$TL_theme tLRPC$TL_theme = (TLRPC$TL_theme) tLObject;
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = null;
            c = 0;
            TLRPC$ThemeSettings tLRPC$ThemeSettings = tLRPC$TL_theme.settings.size() > 0 ? tLRPC$TL_theme.settings.get(0) : null;
            if (tLRPC$ThemeSettings != null) {
                Theme.ThemeInfo theme = Theme.getTheme(Theme.getBaseThemeKey(tLRPC$ThemeSettings));
                if (theme != null) {
                    TLRPC$WallPaper tLRPC$WallPaper = tLRPC$ThemeSettings.wallpaper;
                    if (tLRPC$WallPaper instanceof TLRPC$TL_wallPaper) {
                        tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) tLRPC$WallPaper;
                        if (!FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$TL_wallPaper.document, true).exists()) {
                            this.loadingThemeProgressDialog = alertDialog;
                            this.loadingThemeAccent = true;
                            this.loadingThemeInfo = theme;
                            this.loadingTheme = tLRPC$TL_theme;
                            this.loadingThemeWallpaper = tLRPC$TL_wallPaper;
                            this.loadingThemeWallpaperName = FileLoader.getAttachFileName(tLRPC$TL_wallPaper.document);
                            FileLoader.getInstance(this.currentAccount).loadFile(tLRPC$TL_wallPaper.document, tLRPC$TL_wallPaper, 1, 1);
                            return;
                        }
                    }
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    openThemeAccentPreview(tLRPC$TL_theme, tLRPC$TL_wallPaper, theme);
                }
                c = 1;
            } else {
                TLRPC$Document tLRPC$Document = tLRPC$TL_theme.document;
                if (tLRPC$Document != null) {
                    this.loadingThemeAccent = false;
                    this.loadingTheme = tLRPC$TL_theme;
                    this.loadingThemeFileName = FileLoader.getAttachFileName(tLRPC$Document);
                    this.loadingThemeProgressDialog = alertDialog;
                    FileLoader.getInstance(this.currentAccount).loadFile(this.loadingTheme.document, tLRPC$TL_theme, 1, 1);
                }
                c = 1;
            }
        } else {
            if (tLRPC$TL_error == null || !"THEME_FORMAT_INVALID".equals(tLRPC$TL_error.text)) {
                c = 2;
            }
            c = 1;
        }
        if (c != 0) {
            try {
                runnable.run();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            if (c == 1) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(R.string.Theme), LocaleController.getString(R.string.ThemeNotSupported)));
            } else {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(R.string.Theme), LocaleController.getString(R.string.ThemeNotFound)));
            }
        }
    }

    public void lambda$runLinkRequest$98(final int[] iArr, final int i, final Runnable runnable, final Integer num, final Integer num2, final Long l, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$97(tLObject, iArr, i, runnable, num, num2, l);
            }
        });
    }

    public void lambda$runLinkRequest$97(TLObject tLObject, int[] iArr, int i, Runnable runnable, Integer num, Integer num2, Long l) {
        if (tLObject instanceof TLRPC$TL_messages_chats) {
            TLRPC$TL_messages_chats tLRPC$TL_messages_chats = (TLRPC$TL_messages_chats) tLObject;
            if (!tLRPC$TL_messages_chats.chats.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).putChats(tLRPC$TL_messages_chats.chats, false);
                iArr[0] = runCommentRequest(i, runnable, num, num2, l, tLRPC$TL_messages_chats.chats.get(0));
                return;
            }
        }
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        showAlertDialog(AlertsCreator.createNoAccessAlert(this, LocaleController.getString(R.string.DialogNotAvailable), LocaleController.getString(R.string.LinkNotFound), null));
    }

    public static void lambda$runLinkRequest$99(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$runLinkRequest$102(final Bundle bundle, final Long l, int[] iArr, final Runnable runnable, final boolean z, final Browser.Progress progress, final Long l2, final Integer num, final BaseFragment baseFragment, final int i) {
        if (getActionBarLayout().presentFragment(new ChatActivity(bundle))) {
            return;
        }
        TLRPC$TL_channels_getChannels tLRPC$TL_channels_getChannels = new TLRPC$TL_channels_getChannels();
        TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
        tLRPC$TL_inputChannel.channel_id = l.longValue();
        tLRPC$TL_channels_getChannels.id.add(tLRPC$TL_inputChannel);
        iArr[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getChannels, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.this.lambda$runLinkRequest$101(runnable, z, l, progress, l2, num, baseFragment, i, bundle, tLObject, tLRPC$TL_error);
            }
        });
    }

    public void lambda$runLinkRequest$101(final Runnable runnable, final boolean z, final Long l, final Browser.Progress progress, final Long l2, final Integer num, final BaseFragment baseFragment, final int i, final Bundle bundle, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$100(runnable, tLObject, z, l, progress, l2, num, baseFragment, i, bundle);
            }
        });
    }

    public void lambda$runLinkRequest$100(Runnable runnable, TLObject tLObject, boolean z, Long l, Browser.Progress progress, Long l2, Integer num, BaseFragment baseFragment, int i, Bundle bundle) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TLRPC$TL_messages_chats) {
            TLRPC$TL_messages_chats tLRPC$TL_messages_chats = (TLRPC$TL_messages_chats) tLObject;
            if (!tLRPC$TL_messages_chats.chats.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).putChats(tLRPC$TL_messages_chats.chats, false);
                TLRPC$Chat tLRPC$Chat = tLRPC$TL_messages_chats.chats.get(0);
                if (tLRPC$Chat != null && z && ChatObject.isBoostSupported(tLRPC$Chat)) {
                    processBoostDialog(Long.valueOf(-l.longValue()), null, progress);
                } else if (tLRPC$Chat != null && tLRPC$Chat.forum) {
                    if (l2 != null) {
                        openForumFromLink(-l.longValue(), num, null);
                    } else {
                        openForumFromLink(-l.longValue(), null, null);
                    }
                }
                if (baseFragment == null || MessagesController.getInstance(i).checkCanOpenChat(bundle, baseFragment)) {
                    getActionBarLayout().presentFragment(new ChatActivity(bundle));
                    return;
                }
                return;
            }
        }
        showAlertDialog(AlertsCreator.createNoAccessAlert(this, LocaleController.getString(R.string.DialogNotAvailable), LocaleController.getString(R.string.LinkNotFound), null));
    }

    public void lambda$runLinkRequest$104(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$runLinkRequest$103(tLObject);
            }
        });
    }

    public void lambda$runLinkRequest$103(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_account_resolvedBusinessChatLinks) {
            TLRPC$TL_account_resolvedBusinessChatLinks tLRPC$TL_account_resolvedBusinessChatLinks = (TLRPC$TL_account_resolvedBusinessChatLinks) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_account_resolvedBusinessChatLinks.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tLRPC$TL_account_resolvedBusinessChatLinks.chats, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tLRPC$TL_account_resolvedBusinessChatLinks.users, tLRPC$TL_account_resolvedBusinessChatLinks.chats, true, true);
            Bundle bundle = new Bundle();
            TLRPC$Peer tLRPC$Peer = tLRPC$TL_account_resolvedBusinessChatLinks.peer;
            if (tLRPC$Peer instanceof TLRPC$TL_peerUser) {
                bundle.putLong("user_id", tLRPC$Peer.user_id);
            } else if ((tLRPC$Peer instanceof TLRPC$TL_peerChat) || (tLRPC$Peer instanceof TLRPC$TL_peerChannel)) {
                bundle.putLong("chat_id", tLRPC$Peer.channel_id);
            }
            ChatActivity chatActivity = new ChatActivity(bundle);
            chatActivity.setResolvedChatLink(tLRPC$TL_account_resolvedBusinessChatLinks);
            presentFragment(chatActivity, false, true);
            return;
        }
        showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString(R.string.BusinessLink), LocaleController.getString(R.string.BusinessLinkInvalid)));
    }

    public static void lambda$runLinkRequest$105(int i, int[] iArr, Runnable runnable, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$runLinkRequest$106(int i, int[] iArr, Runnable runnable) {
        ConnectionsManager.getInstance(i).cancelRequest(iArr[0], true);
        if (runnable != null) {
            runnable.run();
        }
    }

    private void processWebAppBot(final int i, final String str, final String str2, final String str3, final String str4, final String str5, final String str6, final String str7, final String str8, final String str9, final String str10, final String str11, final String str12, final boolean z, final Integer num, final Long l, final Long l2, final Integer num2, final String str13, final HashMap<String, String> hashMap, final String str14, final String str15, final String str16, final String str17, final TLRPC$TL_wallPaper tLRPC$TL_wallPaper, final String str18, final String str19, final String str20, final boolean z2, final String str21, final int i2, final int i3, final String str22, final String str23, final String str24, String str25, final String str26, final Browser.Progress progress, final boolean z3, final int i4, final boolean z4, final String str27, final TLRPC$User tLRPC$User, final Runnable runnable, final boolean z5, final boolean z6, final boolean z7, final boolean z8, final boolean z9) {
        TLRPC$TL_messages_getBotApp tLRPC$TL_messages_getBotApp = new TLRPC$TL_messages_getBotApp();
        TLRPC$TL_inputBotAppShortName tLRPC$TL_inputBotAppShortName = new TLRPC$TL_inputBotAppShortName();
        tLRPC$TL_inputBotAppShortName.bot_id = MessagesController.getInstance(i).getInputUser(tLRPC$User);
        tLRPC$TL_inputBotAppShortName.short_name = str25;
        tLRPC$TL_messages_getBotApp.app = tLRPC$TL_inputBotAppShortName;
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getBotApp, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.this.lambda$processWebAppBot$111(progress, i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tLRPC$TL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, z3, i4, z4, str27, z7, z8, z9, runnable, tLRPC$User, str26, z6, z5, tLObject, tLRPC$TL_error);
            }
        });
    }

    public void lambda$processWebAppBot$111(final Browser.Progress progress, final int i, final String str, final String str2, final String str3, final String str4, final String str5, final String str6, final String str7, final String str8, final String str9, final String str10, final String str11, final String str12, final boolean z, final Integer num, final Long l, final Long l2, final Integer num2, final String str13, final HashMap hashMap, final String str14, final String str15, final String str16, final String str17, final TLRPC$TL_wallPaper tLRPC$TL_wallPaper, final String str18, final String str19, final String str20, final boolean z2, final String str21, final int i2, final int i3, final String str22, final String str23, final String str24, final boolean z3, final int i4, final boolean z4, final String str25, final boolean z5, final boolean z6, final boolean z7, final Runnable runnable, final TLRPC$User tLRPC$User, final String str26, final boolean z8, final boolean z9, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (progress != null) {
            progress.end();
        }
        if (tLRPC$TL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$processWebAppBot$107(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tLRPC$TL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, progress, z3, i4, z4, str25, z5, z6, z7);
                }
            });
        } else {
            final TLRPC$TL_messages_botApp tLRPC$TL_messages_botApp = (TLRPC$TL_messages_botApp) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$processWebAppBot$110(runnable, i, tLRPC$User, tLRPC$TL_messages_botApp, str26, z5, z6, z3, z8, z9, progress);
                }
            });
        }
    }

    public void lambda$processWebAppBot$107(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, Integer num, Long l, Long l2, Integer num2, String str13, HashMap hashMap, String str14, String str15, String str16, String str17, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, String str18, String str19, String str20, boolean z2, String str21, int i2, int i3, String str22, String str23, String str24, Browser.Progress progress, boolean z3, int i4, boolean z4, String str25, boolean z5, boolean z6, boolean z7) {
        runLinkRequest(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, hashMap, str14, str15, str16, str17, tLRPC$TL_wallPaper, str18, str19, str20, z2, str21, i2, i3, str22, str23, str24, null, null, progress, z3, i4, z4, str25, z5, z6, z7);
    }

    public void lambda$processWebAppBot$110(Runnable runnable, final int i, final TLRPC$User tLRPC$User, final TLRPC$TL_messages_botApp tLRPC$TL_messages_botApp, final String str, final boolean z, final boolean z2, final boolean z3, boolean z4, boolean z5, Browser.Progress progress) {
        LaunchActivity launchActivity;
        ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11 chatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11;
        runnable.run();
        final AtomicBoolean atomicBoolean = new AtomicBoolean();
        ArrayList<BaseFragment> arrayList = mainFragmentsStack;
        BaseFragment baseFragment = (arrayList == null || arrayList.isEmpty()) ? null : arrayList.get(arrayList.size() - 1);
        final BaseFragment baseFragment2 = baseFragment;
        final Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$processWebAppBot$108(baseFragment2, i, tLRPC$User, tLRPC$TL_messages_botApp, atomicBoolean, str, z, z2, z3);
            }
        };
        if (z4) {
            runnable2.run();
            return;
        }
        if (tLRPC$TL_messages_botApp.inactive && z5) {
            com.google.android.exoplayer2.util.Consumer consumer = new com.google.android.exoplayer2.util.Consumer() {
                @Override
                public final void accept(Object obj) {
                    runnable2.run();
                }
            };
            if (progress != null) {
                chatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11 = new ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11(progress);
                launchActivity = this;
            } else {
                launchActivity = this;
                chatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11 = null;
            }
            WebAppDisclaimerAlert.show(launchActivity, consumer, null, chatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11);
            return;
        }
        if (tLRPC$TL_messages_botApp.request_write_access || z3) {
            AlertsCreator.createBotLaunchAlert(baseFragment, atomicBoolean, tLRPC$User, runnable2);
        } else {
            runnable2.run();
        }
    }

    public void lambda$processWebAppBot$108(BaseFragment baseFragment, int i, TLRPC$User tLRPC$User, TLRPC$TL_messages_botApp tLRPC$TL_messages_botApp, AtomicBoolean atomicBoolean, String str, boolean z, boolean z2, boolean z3) {
        BaseFragment baseFragment2 = baseFragment;
        if (baseFragment2 == null || !isActive || isFinishing() || isDestroyed()) {
            return;
        }
        long j = tLRPC$User.id;
        WebViewRequestProps of = WebViewRequestProps.of(i, j, j, null, null, 3, 0, false, tLRPC$TL_messages_botApp.app, atomicBoolean.get(), str, tLRPC$User, 0, z);
        if (getBottomSheetTabs() == null || getBottomSheetTabs().tryReopenTab(of) == null) {
            if (AndroidUtilities.isTablet()) {
                BotWebViewSheet botWebViewSheet = new BotWebViewSheet(this, baseFragment.getResourceProvider());
                botWebViewSheet.setWasOpenedByLinkIntent(z2);
                botWebViewSheet.setDefaultFullsize(!z);
                botWebViewSheet.setNeedsContext(false);
                botWebViewSheet.setParentActivity(this);
                botWebViewSheet.requestWebView(baseFragment2, of);
                botWebViewSheet.show();
                if (tLRPC$TL_messages_botApp.inactive || z3) {
                    botWebViewSheet.showJustAddedBulletin();
                    return;
                }
                return;
            }
            if (baseFragment.getParentLayout() instanceof ActionBarLayout) {
                baseFragment2 = ((ActionBarLayout) baseFragment.getParentLayout()).getSheetFragment();
            }
            BotWebViewAttachedSheet createBotViewer = baseFragment2.createBotViewer();
            createBotViewer.setWasOpenedByLinkIntent(z2);
            createBotViewer.setDefaultFullsize(!z);
            createBotViewer.setNeedsContext(false);
            createBotViewer.setParentActivity(this);
            createBotViewer.requestWebView(baseFragment2, of);
            createBotViewer.show();
            if (tLRPC$TL_messages_botApp.inactive || z3) {
                createBotViewer.showJustAddedBulletin();
            }
        }
    }

    private void processAttachedMenuBotFromShortcut(long j) {
        for (int i = 0; i < this.visibleDialogs.size(); i++) {
            if (this.visibleDialogs.get(i) instanceof BotWebViewSheet) {
                BotWebViewSheet botWebViewSheet = (BotWebViewSheet) this.visibleDialogs.get(i);
                if (botWebViewSheet.isShowing() && botWebViewSheet.getBotId() == j) {
                    return;
                }
            }
        }
        final AtomicBoolean atomicBoolean = new AtomicBoolean(MediaDataController.getInstance(this.currentAccount).isMenuBotsUpdatedLocal());
        if (!atomicBoolean.get()) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$processAttachedMenuBotFromShortcut$112(atomicBoolean, countDownLatch);
                }
            });
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        if (atomicBoolean.get()) {
            TLRPC$TL_attachMenuBots attachMenuBots = MediaDataController.getInstance(this.currentAccount).getAttachMenuBots();
            if (attachMenuBots.bots.isEmpty()) {
                MediaDataController.getInstance(this.currentAccount).uninstallShortcut(j, MediaDataController.SHORTCUT_TYPE_ATTACHED_BOT);
                return;
            }
            for (int i2 = 0; i2 < attachMenuBots.bots.size(); i2++) {
                if (attachMenuBots.bots.get(i2).bot_id == j) {
                    if (getLastFragment() != null) {
                        showAttachMenuBot(attachMenuBots.bots.get(i2), null, false);
                        return;
                    }
                    return;
                }
            }
        }
    }

    public void lambda$processAttachedMenuBotFromShortcut$112(AtomicBoolean atomicBoolean, CountDownLatch countDownLatch) {
        atomicBoolean.set(MediaDataController.getInstance(this.currentAccount).isMenuBotsUpdatedLocal());
        countDownLatch.countDown();
    }

    private void processBoostDialog(Long l, Runnable runnable, Browser.Progress progress) {
        processBoostDialog(l, runnable, progress, null);
    }

    private void processBoostDialog(final Long l, final Runnable runnable, final Browser.Progress progress, final ChatMessageCell chatMessageCell) {
        final ChannelBoostsController boostsController = MessagesController.getInstance(this.currentAccount).getBoostsController();
        if (progress != null) {
            progress.init();
        }
        boostsController.getBoostsStats(l.longValue(), new com.google.android.exoplayer2.util.Consumer() {
            @Override
            public final void accept(Object obj) {
                LaunchActivity.this.lambda$processBoostDialog$114(progress, runnable, boostsController, l, chatMessageCell, (TL_stories$TL_premium_boostsStatus) obj);
            }
        });
    }

    public void lambda$processBoostDialog$114(final Browser.Progress progress, final Runnable runnable, ChannelBoostsController channelBoostsController, final Long l, final ChatMessageCell chatMessageCell, final TL_stories$TL_premium_boostsStatus tL_stories$TL_premium_boostsStatus) {
        if (tL_stories$TL_premium_boostsStatus != null) {
            channelBoostsController.userCanBoostChannel(l.longValue(), tL_stories$TL_premium_boostsStatus, new com.google.android.exoplayer2.util.Consumer() {
                @Override
                public final void accept(Object obj) {
                    LaunchActivity.this.lambda$processBoostDialog$113(progress, l, tL_stories$TL_premium_boostsStatus, chatMessageCell, runnable, (ChannelBoostsController.CanApplyBoost) obj);
                }
            });
            return;
        }
        if (progress != null) {
            progress.end();
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$processBoostDialog$113(Browser.Progress progress, Long l, TL_stories$TL_premium_boostsStatus tL_stories$TL_premium_boostsStatus, ChatMessageCell chatMessageCell, Runnable runnable, ChannelBoostsController.CanApplyBoost canApplyBoost) {
        RightSlidingDialogContainer rightSlidingDialogContainer;
        if (progress != null) {
            progress.end();
        }
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment == null) {
            return;
        }
        Theme.ResourcesProvider resourceProvider = lastFragment.getResourceProvider();
        if (lastFragment.getLastStoryViewer() != null && lastFragment.getLastStoryViewer().isFullyVisible()) {
            resourceProvider = lastFragment.getLastStoryViewer().getResourceProvider();
        }
        LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(lastFragment, this, 19, this.currentAccount, resourceProvider);
        limitReachedBottomSheet.setCanApplyBoost(canApplyBoost);
        boolean z = false;
        if (!(lastFragment instanceof ChatActivity) ? !(!(lastFragment instanceof DialogsActivity) || (rightSlidingDialogContainer = ((DialogsActivity) lastFragment).rightSlidingDialogContainer) == null || rightSlidingDialogContainer.getCurrentFragmetDialogId() != l.longValue()) : ((ChatActivity) lastFragment).getDialogId() == l.longValue()) {
            z = true;
        }
        limitReachedBottomSheet.setBoostsStats(tL_stories$TL_premium_boostsStatus, z);
        limitReachedBottomSheet.setDialogId(l.longValue());
        limitReachedBottomSheet.setChatMessageCell(chatMessageCell);
        lastFragment.showDialog(limitReachedBottomSheet);
        if (runnable != null) {
            try {
                runnable.run();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void processAttachMenuBot(final int i, final long j, final String str, final TLRPC$User tLRPC$User, final String str2, final String str3) {
        TLRPC$TL_messages_getAttachMenuBot tLRPC$TL_messages_getAttachMenuBot = new TLRPC$TL_messages_getAttachMenuBot();
        tLRPC$TL_messages_getAttachMenuBot.bot = MessagesController.getInstance(i).getInputUser(j);
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getAttachMenuBot, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.this.lambda$processAttachMenuBot$121(i, str3, str, tLRPC$User, str2, j, tLObject, tLRPC$TL_error);
            }
        });
    }

    public void lambda$processAttachMenuBot$121(final int i, final String str, final String str2, final TLRPC$User tLRPC$User, final String str3, final long j, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$processAttachMenuBot$120(tLObject, i, str, str2, tLRPC$User, str3, j);
            }
        });
    }

    public void lambda$processAttachMenuBot$120(TLObject tLObject, final int i, String str, String str2, final TLRPC$User tLRPC$User, final String str3, final long j) {
        DialogsActivity dialogsActivity;
        final TLRPC$User tLRPC$User2 = tLRPC$User;
        if (tLObject instanceof TLRPC$TL_attachMenuBotsBot) {
            TLRPC$TL_attachMenuBotsBot tLRPC$TL_attachMenuBotsBot = (TLRPC$TL_attachMenuBotsBot) tLObject;
            MessagesController.getInstance(i).putUsers(tLRPC$TL_attachMenuBotsBot.users, false);
            TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot = tLRPC$TL_attachMenuBotsBot.bot;
            if (str != null) {
                showAttachMenuBot(tLRPC$TL_attachMenuBot, str, false);
                return;
            }
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            BaseFragment baseFragment = arrayList.get(arrayList.size() - 1);
            if (AndroidUtilities.isTablet() && !(baseFragment instanceof ChatActivity)) {
                ArrayList<BaseFragment> arrayList2 = rightFragmentsStack;
                if (!arrayList2.isEmpty()) {
                    baseFragment = arrayList2.get(arrayList2.size() - 1);
                }
            }
            final BaseFragment baseFragment2 = baseFragment;
            ArrayList arrayList3 = new ArrayList();
            if (!TextUtils.isEmpty(str2)) {
                for (String str4 : str2.split(" ")) {
                    if (MediaDataController.canShowAttachMenuBotForTarget(tLRPC$TL_attachMenuBot, str4)) {
                        arrayList3.add(str4);
                    }
                }
            }
            if (arrayList3.isEmpty()) {
                dialogsActivity = null;
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt("dialogsType", 14);
                bundle.putBoolean("onlySelect", true);
                bundle.putBoolean("allowGroups", arrayList3.contains("groups"));
                bundle.putBoolean("allowMegagroups", arrayList3.contains("groups"));
                bundle.putBoolean("allowLegacyGroups", arrayList3.contains("groups"));
                bundle.putBoolean("allowUsers", arrayList3.contains("users"));
                bundle.putBoolean("allowChannels", arrayList3.contains("channels"));
                bundle.putBoolean("allowBots", arrayList3.contains("bots"));
                DialogsActivity dialogsActivity2 = new DialogsActivity(bundle);
                dialogsActivity2.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                    @Override
                    public final boolean didSelectDialogs(DialogsActivity dialogsActivity3, ArrayList arrayList4, CharSequence charSequence, boolean z, TopicsFragment topicsFragment) {
                        boolean lambda$processAttachMenuBot$115;
                        lambda$processAttachMenuBot$115 = LaunchActivity.this.lambda$processAttachMenuBot$115(tLRPC$User2, str3, i, dialogsActivity3, arrayList4, charSequence, z, topicsFragment);
                        return lambda$processAttachMenuBot$115;
                    }
                });
                dialogsActivity = dialogsActivity2;
            }
            if (tLRPC$TL_attachMenuBot.inactive) {
                AttachBotIntroTopView attachBotIntroTopView = new AttachBotIntroTopView(this);
                attachBotIntroTopView.setColor(Theme.getColor(Theme.key_chat_attachIcon));
                attachBotIntroTopView.setBackgroundColor(Theme.getColor(Theme.key_dialogTopBackground));
                attachBotIntroTopView.setAttachBot(tLRPC$TL_attachMenuBot);
                final DialogsActivity dialogsActivity3 = dialogsActivity;
                com.google.android.exoplayer2.util.Consumer consumer = new com.google.android.exoplayer2.util.Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        LaunchActivity.this.lambda$processAttachMenuBot$119(i, j, dialogsActivity3, baseFragment2, tLRPC$User, str3, (Boolean) obj);
                    }
                };
                if (!tLRPC$TL_attachMenuBot.request_write_access) {
                    tLRPC$User2 = null;
                }
                WebAppDisclaimerAlert.show(this, consumer, tLRPC$User2, null);
                return;
            }
            if (dialogsActivity != null) {
                if (baseFragment2 != null) {
                    baseFragment2.dismissCurrentDialog();
                }
                for (int i2 = 0; i2 < this.visibleDialogs.size(); i2++) {
                    if (this.visibleDialogs.get(i2).isShowing()) {
                        this.visibleDialogs.get(i2).dismiss();
                    }
                }
                this.visibleDialogs.clear();
                lambda$runLinkRequest$91(dialogsActivity);
                return;
            }
            if (baseFragment2 instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) baseFragment2;
                if (!MediaDataController.canShowAttachMenuBot(tLRPC$TL_attachMenuBot, chatActivity.getCurrentUser() != null ? chatActivity.getCurrentUser() : chatActivity.getCurrentChat())) {
                    BulletinFactory.of(baseFragment2).createErrorBulletin(LocaleController.getString(R.string.BotAlreadyAddedToAttachMenu)).show();
                    return;
                } else {
                    chatActivity.openAttachBotLayout(tLRPC$User2.id, str3, false);
                    return;
                }
            }
            BulletinFactory.of(baseFragment2).createErrorBulletin(LocaleController.getString(R.string.BotAlreadyAddedToAttachMenu)).show();
            return;
        }
        ArrayList<BaseFragment> arrayList4 = mainFragmentsStack;
        BulletinFactory.of(arrayList4.get(arrayList4.size() - 1)).createErrorBulletin(LocaleController.getString(R.string.BotCantAddToAttachMenu)).show();
    }

    public boolean lambda$processAttachMenuBot$115(TLRPC$User tLRPC$User, String str, int i, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, TopicsFragment topicsFragment) {
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
        bundle.putString("attach_bot", UserObject.getPublicUsername(tLRPC$User));
        if (str != null) {
            bundle.putString("attach_bot_start_command", str);
        }
        if (MessagesController.getInstance(i).checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            getActionBarLayout().presentFragment(new ChatActivity(bundle), true, false, true, false);
        }
        return true;
    }

    public void lambda$processAttachMenuBot$119(final int i, long j, final DialogsActivity dialogsActivity, final BaseFragment baseFragment, final TLRPC$User tLRPC$User, final String str, Boolean bool) {
        TLRPC$TL_messages_toggleBotInAttachMenu tLRPC$TL_messages_toggleBotInAttachMenu = new TLRPC$TL_messages_toggleBotInAttachMenu();
        tLRPC$TL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(i).getInputUser(j);
        tLRPC$TL_messages_toggleBotInAttachMenu.enabled = true;
        tLRPC$TL_messages_toggleBotInAttachMenu.write_allowed = true;
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.this.lambda$processAttachMenuBot$118(i, dialogsActivity, baseFragment, tLRPC$User, str, tLObject, tLRPC$TL_error);
            }
        }, 66);
    }

    public void lambda$processAttachMenuBot$118(final int i, final DialogsActivity dialogsActivity, final BaseFragment baseFragment, final TLRPC$User tLRPC$User, final String str, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$processAttachMenuBot$117(tLObject, i, dialogsActivity, baseFragment, tLRPC$User, str);
            }
        });
    }

    public void lambda$processAttachMenuBot$117(TLObject tLObject, int i, final DialogsActivity dialogsActivity, final BaseFragment baseFragment, final TLRPC$User tLRPC$User, final String str) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            MediaDataController.getInstance(i).loadAttachMenuBots(false, true, new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$processAttachMenuBot$116(dialogsActivity, baseFragment, tLRPC$User, str);
                }
            });
        }
    }

    public void lambda$processAttachMenuBot$116(DialogsActivity dialogsActivity, BaseFragment baseFragment, TLRPC$User tLRPC$User, String str) {
        if (dialogsActivity != null) {
            if (baseFragment != null) {
                baseFragment.dismissCurrentDialog();
            }
            for (int i = 0; i < this.visibleDialogs.size(); i++) {
                if (this.visibleDialogs.get(i).isShowing()) {
                    this.visibleDialogs.get(i).dismiss();
                }
            }
            this.visibleDialogs.clear();
            lambda$runLinkRequest$91(dialogsActivity);
            return;
        }
        if (baseFragment instanceof ChatActivity) {
            ((ChatActivity) baseFragment).openAttachBotLayout(tLRPC$User.id, str, true);
        }
    }

    private void openForumFromLink(long j, Integer num, Runnable runnable) {
        openForumFromLink(j, num, null, runnable, 0, -1);
    }

    private void openForumFromLink(final long j, final Integer num, final String str, final Runnable runnable, final int i, final int i2) {
        if (num == null) {
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", -j);
            lambda$runLinkRequest$91(TopicsFragment.getTopicsOrChat(this, bundle));
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages = new TLRPC$TL_channels_getMessages();
        tLRPC$TL_channels_getMessages.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(-j);
        tLRPC$TL_channels_getMessages.id.add(num);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getMessages, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LaunchActivity.this.lambda$openForumFromLink$123(num, j, runnable, str, i, i2, tLObject, tLRPC$TL_error);
            }
        });
    }

    public void lambda$openForumFromLink$123(final Integer num, final long j, final Runnable runnable, final String str, final int i, final int i2, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$openForumFromLink$122(tLObject, num, j, runnable, str, i, i2);
            }
        });
    }

    public void lambda$openForumFromLink$122(TLObject tLObject, Integer num, long j, Runnable runnable, String str, int i, int i2) {
        TLRPC$Message tLRPC$Message;
        if (tLObject instanceof TLRPC$messages_Messages) {
            ArrayList<TLRPC$Message> arrayList = ((TLRPC$messages_Messages) tLObject).messages;
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                if (arrayList.get(i3) != null && arrayList.get(i3).id == num.intValue()) {
                    tLRPC$Message = arrayList.get(i3);
                    break;
                }
            }
        }
        tLRPC$Message = null;
        if (tLRPC$Message != null) {
            int i4 = this.currentAccount;
            Integer valueOf = Integer.valueOf(tLRPC$Message.id);
            int i5 = this.currentAccount;
            runCommentRequest(i4, null, valueOf, null, Long.valueOf(MessageObject.getTopicId(i5, tLRPC$Message, MessagesController.getInstance(i5).isForum(tLRPC$Message))), MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j)), runnable, str, i, i2);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", -j);
        lambda$runLinkRequest$91(TopicsFragment.getTopicsOrChat(this, bundle));
        if (runnable != null) {
            runnable.run();
        }
    }

    private java.util.List<org.telegram.tgnet.TLRPC$TL_contact> findContacts(java.lang.String r19, java.lang.String r20, boolean r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.findContacts(java.lang.String, java.lang.String, boolean):java.util.List");
    }

    public void checkAppUpdate(boolean z, final Browser.Progress progress) {
        if (z || !BuildVars.DEBUG_VERSION) {
            if (z || BuildVars.CHECK_UPDATES) {
                if (z || Math.abs(System.currentTimeMillis() - SharedConfig.lastUpdateCheckTime) >= MessagesController.getInstance(0).updateCheckDelay * 1000) {
                    TLRPC$TL_help_getAppUpdate tLRPC$TL_help_getAppUpdate = new TLRPC$TL_help_getAppUpdate();
                    try {
                        tLRPC$TL_help_getAppUpdate.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
                    } catch (Exception unused) {
                    }
                    if (tLRPC$TL_help_getAppUpdate.source == null) {
                        tLRPC$TL_help_getAppUpdate.source = "";
                    }
                    final int i = this.currentAccount;
                    final int sendRequest = ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_help_getAppUpdate, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            LaunchActivity.this.lambda$checkAppUpdate$127(i, progress, tLObject, tLRPC$TL_error);
                        }
                    });
                    if (progress != null) {
                        progress.init();
                        progress.onCancel(new Runnable() {
                            @Override
                            public final void run() {
                                LaunchActivity.this.lambda$checkAppUpdate$128(sendRequest);
                            }
                        });
                    }
                }
            }
        }
    }

    public void lambda$checkAppUpdate$127(final int i, final Browser.Progress progress, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        SharedConfig.lastUpdateCheckTime = System.currentTimeMillis();
        SharedConfig.saveConfig();
        if (tLObject instanceof TLRPC$TL_help_appUpdate) {
            final TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate = (TLRPC$TL_help_appUpdate) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$checkAppUpdate$124(tLRPC$TL_help_appUpdate, i, progress);
                }
            });
        } else if (tLObject instanceof TLRPC$TL_help_noAppUpdate) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.lambda$checkAppUpdate$125(Browser.Progress.this);
                }
            });
        } else if (tLRPC$TL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.lambda$checkAppUpdate$126(Browser.Progress.this, tLRPC$TL_error);
                }
            });
        }
    }

    public void lambda$checkAppUpdate$124(TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, int i, Browser.Progress progress) {
        BaseFragment lastFragment;
        TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate2 = SharedConfig.pendingAppUpdate;
        if (tLRPC$TL_help_appUpdate2 == null || !tLRPC$TL_help_appUpdate2.version.equals(tLRPC$TL_help_appUpdate.version)) {
            boolean newAppVersionAvailable = SharedConfig.setNewAppVersionAvailable(tLRPC$TL_help_appUpdate);
            if (newAppVersionAvailable) {
                if (tLRPC$TL_help_appUpdate.can_not_skip) {
                    showUpdateActivity(i, tLRPC$TL_help_appUpdate, false);
                } else if (ApplicationLoader.isStandaloneBuild() || BuildVars.DEBUG_VERSION) {
                    this.drawerLayoutAdapter.notifyDataSetChanged();
                    ApplicationLoader.applicationLoaderInstance.showUpdateAppPopup(this, tLRPC$TL_help_appUpdate, i);
                }
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.appUpdateAvailable, new Object[0]);
            }
            if (progress != null) {
                progress.end();
                if (newAppVersionAvailable || (lastFragment = getLastFragment()) == null) {
                    return;
                }
                BulletinFactory.of(lastFragment).createSimpleBulletin(R.raw.chats_infotip, LocaleController.getString(R.string.YourVersionIsLatest)).show();
            }
        }
    }

    public static void lambda$checkAppUpdate$125(Browser.Progress progress) {
        if (progress != null) {
            progress.end();
            BaseFragment lastFragment = getLastFragment();
            if (lastFragment != null) {
                BulletinFactory.of(lastFragment).createSimpleBulletin(R.raw.chats_infotip, LocaleController.getString(R.string.YourVersionIsLatest)).show();
            }
        }
    }

    public static void lambda$checkAppUpdate$126(Browser.Progress progress, TLRPC$TL_error tLRPC$TL_error) {
        if (progress != null) {
            progress.end();
            BaseFragment lastFragment = getLastFragment();
            if (lastFragment != null) {
                BulletinFactory.of(lastFragment).showForError(tLRPC$TL_error);
            }
        }
    }

    public void lambda$checkAppUpdate$128(int i) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public Dialog showAlertDialog(AlertDialog.Builder builder) {
        try {
            final AlertDialog show = builder.show();
            show.setCanceledOnTouchOutside(true);
            show.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    LaunchActivity.this.lambda$showAlertDialog$129(show, dialogInterface);
                }
            });
            this.visibleDialogs.add(show);
            return show;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public void lambda$showAlertDialog$129(AlertDialog alertDialog, DialogInterface dialogInterface) {
        if (alertDialog != null) {
            if (alertDialog == this.localeDialog) {
                ActionBarLayout actionBarLayout = this.actionBarLayout;
                BaseFragment lastFragment = actionBarLayout == null ? null : actionBarLayout.getLastFragment();
                try {
                    String str = LocaleController.getInstance().getCurrentLocaleInfo().shortName;
                    if (lastFragment != null) {
                        BulletinFactory.of(lastFragment).createSimpleBulletin(R.raw.msg_translate, getStringForLanguageAlert(str.equals("en") ? this.englishLocaleStrings : this.systemLocaleStrings, "ChangeLanguageLater", R.string.ChangeLanguageLater)).setDuration(5000).show();
                    } else {
                        BulletinFactory.of(Bulletin.BulletinWindow.make(this), null).createSimpleBulletin(R.raw.msg_translate, getStringForLanguageAlert(str.equals("en") ? this.englishLocaleStrings : this.systemLocaleStrings, "ChangeLanguageLater", R.string.ChangeLanguageLater)).setDuration(5000).show();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.localeDialog = null;
            } else if (alertDialog == this.proxyErrorDialog) {
                MessagesController.getGlobalMainSettings();
                SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
                edit.putBoolean("proxy_enabled", false);
                edit.putBoolean("proxy_enabled_calls", false);
                edit.commit();
                ConnectionsManager.setProxySettings(false, "", 1080, "", "", "");
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.proxySettingsChanged, new Object[0]);
                this.proxyErrorDialog = null;
            }
        }
        this.visibleDialogs.remove(alertDialog);
    }

    public void showBulletin(Function<BulletinFactory, Bulletin> function) {
        BaseFragment baseFragment;
        ArrayList<BaseFragment> arrayList = layerFragmentsStack;
        if (!arrayList.isEmpty()) {
            baseFragment = arrayList.get(arrayList.size() - 1);
        } else {
            ArrayList<BaseFragment> arrayList2 = rightFragmentsStack;
            if (!arrayList2.isEmpty()) {
                baseFragment = arrayList2.get(arrayList2.size() - 1);
            } else {
                ArrayList<BaseFragment> arrayList3 = mainFragmentsStack;
                baseFragment = !arrayList3.isEmpty() ? arrayList3.get(arrayList3.size() - 1) : null;
            }
        }
        if (BulletinFactory.canShowBulletin(baseFragment)) {
            function.apply(BulletinFactory.of(baseFragment)).show();
        }
    }

    public void setNavigateToPremiumBot(boolean z) {
        this.navigateToPremiumBot = z;
    }

    public void setNavigateToPremiumGiftCallback(Runnable runnable) {
        this.navigateToPremiumGiftCallback = runnable;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false, null, true, true);
    }

    public void onNewIntent(Intent intent, Browser.Progress progress) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false, progress, true, false);
    }

    @Override
    public boolean didSelectDialogs(final org.telegram.ui.DialogsActivity r40, final java.util.ArrayList<org.telegram.messenger.MessagesStorage.TopicKey> r41, final java.lang.CharSequence r42, final boolean r43, org.telegram.ui.TopicsFragment r44) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didSelectDialogs(org.telegram.ui.DialogsActivity, java.util.ArrayList, java.lang.CharSequence, boolean, org.telegram.ui.TopicsFragment):boolean");
    }

    public void lambda$didSelectDialogs$130(int i, DialogsActivity dialogsActivity, boolean z, ArrayList arrayList, Uri uri, AlertDialog alertDialog, long j) {
        if (j != 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (!AndroidUtilities.isTablet()) {
                NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            }
            if (DialogObject.isUserDialog(j)) {
                bundle.putLong("user_id", j);
            } else {
                bundle.putLong("chat_id", -j);
            }
            ChatActivity chatActivity = new ChatActivity(bundle);
            chatActivity.setOpenImport();
            getActionBarLayout().presentFragment(chatActivity, dialogsActivity != null || z, dialogsActivity == null, true, false);
        } else {
            this.documentsUrisArray = arrayList;
            if (arrayList == null) {
                this.documentsUrisArray = new ArrayList<>();
            }
            this.documentsUrisArray.add(0, uri);
            openDialogsToSend(true);
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$didSelectDialogs$131(ChatActivity chatActivity, ArrayList arrayList, CharSequence charSequence, int i, boolean z, TLRPC$User tLRPC$User, boolean z2, int i2, long j, boolean z3) {
        MessageObject messageObject;
        TLRPC$TL_forumTopic findTopic;
        if (chatActivity != null) {
            getActionBarLayout().presentFragment(chatActivity, true, false, true, false);
        }
        AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            long j2 = ((MessagesStorage.TopicKey) arrayList.get(i3)).dialogId;
            long j3 = ((MessagesStorage.TopicKey) arrayList.get(i3)).topicId;
            if (j3 == 0 || (findTopic = accountInstance.getMessagesController().getTopicsController().findTopic(-j2, j3)) == null || findTopic.topicStartMessage == null) {
                messageObject = null;
            } else {
                MessageObject messageObject2 = new MessageObject(accountInstance.getCurrentAccount(), findTopic.topicStartMessage, false, false);
                messageObject2.isTopicMainMessage = true;
                messageObject = messageObject2;
            }
            SendMessagesHelper.SendMessageParams of = SendMessagesHelper.SendMessageParams.of(tLRPC$User, j2, messageObject, messageObject, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z2, i2);
            if (TextUtils.isEmpty(charSequence)) {
                of.effect_id = j;
            }
            of.invert_media = z3;
            SendMessagesHelper.getInstance(i).sendMessage(of);
            if (!TextUtils.isEmpty(charSequence)) {
                SendMessagesHelper.prepareSendingText(accountInstance, charSequence.toString(), j2, z, 0, j);
            }
        }
    }

    private void onFinish() {
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        if (this.finished) {
            return;
        }
        this.finished = true;
        int i = this.currentAccount;
        if (i != -1) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openBoostForUsersDialog);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mainUserInfoChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.attachMenuBotsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowPlayServicesAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.historyImportProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersImportComplete);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserShowLimitReachedDialog);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reloadInterface);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needCheckSystemBarColors);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.notificationsCountUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.screenStateChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.showBulletin);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.appUpdateAvailable);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.requestPermissions);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.billingConfirmPurchaseError);
        LiteMode.removeOnPowerSaverAppliedListener(new LaunchActivity$$ExternalSyntheticLambda21(this));
    }

    public void onPowerSaver(boolean z) {
        BaseFragment lastFragment;
        if (Build.VERSION.SDK_INT < 21 || this.actionBarLayout == null || !z || LiteMode.getPowerSaverLevel() >= 100 || (lastFragment = this.actionBarLayout.getLastFragment()) == null || (lastFragment instanceof LiteModeSettingsActivity)) {
            return;
        }
        int batteryLevel = LiteMode.getBatteryLevel();
        BulletinFactory.of(lastFragment).createSimpleBulletin(new BatteryDrawable(batteryLevel / 100.0f, -1, lastFragment.getThemedColor(Theme.key_dialogSwipeRemove), 1.3f), LocaleController.getString(R.string.LowPowerEnabledTitle), LocaleController.formatString("LowPowerEnabledSubtitle", R.string.LowPowerEnabledSubtitle, String.format("%d%%", Integer.valueOf(batteryLevel))), LocaleController.getString(R.string.Disable), new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$onPowerSaver$132();
            }
        }).setDuration(5000).show();
    }

    public void lambda$onPowerSaver$132() {
        lambda$runLinkRequest$91(new LiteModeSettingsActivity());
    }

    public void lambda$runLinkRequest$91(BaseFragment baseFragment) {
        getActionBarLayout().presentFragment(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2) {
        return getActionBarLayout().presentFragment(baseFragment, z, z2, true, false);
    }

    public INavigationLayout getActionBarLayout() {
        ActionBarLayout actionBarLayout = this.actionBarLayout;
        if (this.sheetFragmentsStack.isEmpty()) {
            return actionBarLayout;
        }
        return this.sheetFragmentsStack.get(r0.size() - 1);
    }

    public INavigationLayout getLayersActionBarLayout() {
        return this.layersActionBarLayout;
    }

    public INavigationLayout getRightActionBarLayout() {
        return this.rightActionBarLayout;
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        VoIPService sharedInstance;
        boolean canDrawOverlays;
        if (SharedConfig.passcodeHash.length() != 0 && SharedConfig.lastPauseTime != 0) {
            SharedConfig.lastPauseTime = 0;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset lastPauseTime onActivityResult");
            }
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        if (i == 105) {
            if (Build.VERSION.SDK_INT >= 23) {
                canDrawOverlays = Settings.canDrawOverlays(this);
                ApplicationLoader.canDrawOverlays = canDrawOverlays;
                if (canDrawOverlays) {
                    GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
                    if (groupCallActivity != null) {
                        groupCallActivity.dismissInternal();
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            LaunchActivity.this.lambda$onActivityResult$133();
                        }
                    }, 200L);
                    return;
                }
                return;
            }
            return;
        }
        super.onActivityResult(i, i2, intent);
        if (i == 520) {
            if (i2 != -1 || (sharedInstance = VoIPService.getSharedInstance()) == null) {
                return;
            }
            VideoCapturerDevice.mediaProjectionPermissionResultData = intent;
            sharedInstance.createCaptureDevice(true);
            return;
        }
        if (i == 140) {
            LocationController.getInstance(this.currentAccount).startFusedLocationRequest(i2 == -1);
            return;
        }
        if (i == 521) {
            Utilities.Callback<Boolean> callback = this.webviewShareAPIDoneListener;
            if (callback != null) {
                callback.run(Boolean.valueOf(i2 == -1));
                this.webviewShareAPIDoneListener = null;
                return;
            }
            return;
        }
        ThemeEditorView themeEditorView = ThemeEditorView.getInstance();
        if (themeEditorView != null) {
            themeEditorView.onActivityResult(i, i2, intent);
        }
        if (this.actionBarLayout.getFragmentStack().size() != 0) {
            BaseFragment baseFragment = this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1);
            baseFragment.onActivityResultFragment(i, i2, intent);
            if (baseFragment.getLastStoryViewer() != null) {
                baseFragment.getLastStoryViewer().onActivityResult(i, i2, intent);
            }
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.getFragmentStack().size() != 0) {
                this.rightActionBarLayout.getFragmentStack().get(this.rightActionBarLayout.getFragmentStack().size() - 1).onActivityResultFragment(i, i2, intent);
            }
            if (this.layersActionBarLayout.getFragmentStack().size() != 0) {
                this.layersActionBarLayout.getFragmentStack().get(this.layersActionBarLayout.getFragmentStack().size() - 1).onActivityResultFragment(i, i2, intent);
            }
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onActivityResultReceived, Integer.valueOf(i), Integer.valueOf(i2), intent);
    }

    public void lambda$onActivityResult$133() {
        GroupCallPip.clearForce();
        GroupCallPip.updateVisibility(this);
    }

    public void whenWebviewShareAPIDone(Utilities.Callback<Boolean> callback) {
        this.webviewShareAPIDoneListener = callback;
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (checkPermissionsResult(i, strArr, iArr)) {
            ApplicationLoader applicationLoader = ApplicationLoader.applicationLoaderInstance;
            if (applicationLoader == null || !applicationLoader.checkRequestPermissionResult(i, strArr, iArr)) {
                if (this.actionBarLayout.getFragmentStack().size() != 0) {
                    this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1).onRequestPermissionsResultFragment(i, strArr, iArr);
                }
                if (AndroidUtilities.isTablet()) {
                    if (this.rightActionBarLayout.getFragmentStack().size() != 0) {
                        this.rightActionBarLayout.getFragmentStack().get(this.rightActionBarLayout.getFragmentStack().size() - 1).onRequestPermissionsResultFragment(i, strArr, iArr);
                    }
                    if (this.layersActionBarLayout.getFragmentStack().size() != 0) {
                        this.layersActionBarLayout.getFragmentStack().get(this.layersActionBarLayout.getFragmentStack().size() - 1).onRequestPermissionsResultFragment(i, strArr, iArr);
                    }
                }
                VoIPFragment.onRequestPermissionsResult(i, strArr, iArr);
                StoryRecorder.onRequestPermissionsResult(i, strArr, iArr);
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onRequestPermissionResultReceived, Integer.valueOf(i), strArr, iArr);
                if (this.requestedPermissions.get(i, -1) >= 0) {
                    int i2 = this.requestedPermissions.get(i, -1);
                    this.requestedPermissions.delete(i);
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.permissionsGranted, Integer.valueOf(i2));
                }
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.activityPermissionsGranted, Integer.valueOf(i), strArr, iArr);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 4096);
        ApplicationLoader.mainInterfacePaused = true;
        final int i = this.currentAccount;
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$onPause$134(i);
            }
        });
        onPasscodePause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            ActionBarLayout actionBarLayout = this.rightActionBarLayout;
            if (actionBarLayout != null) {
                actionBarLayout.onPause();
            }
            ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
            if (actionBarLayout2 != null) {
                actionBarLayout2.onPause();
            }
        }
        PasscodeViewDialog passcodeViewDialog = this.passcodeDialog;
        if (passcodeViewDialog != null) {
            passcodeViewDialog.passcodeView.onPause();
        }
        Iterator<PasscodeView> it = this.overlayPasscodeViews.iterator();
        while (it.hasNext()) {
            it.next().onPause();
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(!(ApplicationLoader.applicationLoaderInstance != null ? r1.onPause() : false), false);
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onPause();
        }
        StoryRecorder.onPause();
        if (VoIPFragment.getInstance() != null) {
            VoIPFragment.onPause();
        }
        SpoilerEffect2.pause(true);
    }

    public static void lambda$onPause$134(int i) {
        ApplicationLoader.mainInterfacePausedStageQueue = true;
        ApplicationLoader.mainInterfacePausedStageQueueTime = 0L;
        if (VoIPService.getSharedInstance() == null) {
            MessagesController.getInstance(i).ignoreSetOnline = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Browser.bindCustomTabsService(this);
        ApplicationLoader.mainInterfaceStopped = false;
        GroupCallPip.updateVisibility(this);
        GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
        if (groupCallActivity != null) {
            groupCallActivity.onResume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Browser.unbindCustomTabsService(this);
        ApplicationLoader.mainInterfaceStopped = true;
        GroupCallPip.updateVisibility(this);
        GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
        if (groupCallActivity != null) {
            groupCallActivity.onPause();
        }
    }

    @Override
    public void onDestroy() {
        isActive = false;
        if (PhotoViewer.getPipInstance() != null) {
            PhotoViewer.getPipInstance().destroyPhotoViewer();
        }
        if (PhotoViewer.hasInstance()) {
            PhotoViewer.getInstance().destroyPhotoViewer();
        }
        if (SecretMediaViewer.hasInstance()) {
            SecretMediaViewer.getInstance().destroyPhotoViewer();
        }
        if (ArticleViewer.hasInstance()) {
            ArticleViewer.getInstance().destroyArticleViewer();
        }
        if (ContentPreviewViewer.hasInstance()) {
            ContentPreviewViewer.getInstance().destroy();
        }
        GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
        if (groupCallActivity != null) {
            groupCallActivity.dismissInternal();
        }
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        MediaController.getInstance().setBaseActivity(this, false);
        MediaController.getInstance().setFeedbackView(this.feedbackView, false);
        if (pipRoundVideoView != null) {
            pipRoundVideoView.close(false);
        }
        Theme.destroyResources();
        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        }
        ThemeEditorView themeEditorView = ThemeEditorView.getInstance();
        if (themeEditorView != null) {
            themeEditorView.destroy();
        }
        for (int i = 0; i < this.visibleDialogs.size(); i++) {
            try {
                if (this.visibleDialogs.get(i).isShowing()) {
                    this.visibleDialogs.get(i).dismiss();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        this.visibleDialogs.clear();
        try {
            if (this.onGlobalLayoutListener != null) {
                getWindow().getDecorView().getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        super.onDestroy();
        onFinish();
        FloatingDebugController.onDestroy();
        FlagSecureReason flagSecureReason = this.flagSecureReason;
        if (flagSecureReason != null) {
            flagSecureReason.detach();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        Iterator<Runnable> it = this.onUserLeaveHintListeners.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        ActionBarLayout actionBarLayout = this.actionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.onUserLeaveHint();
        }
    }

    @Override
    public void onResume() {
        boolean canDrawOverlays;
        MessageObject playingMessageObject;
        super.onResume();
        isResumed = true;
        Runnable runnable = onResumeStaticCallback;
        if (runnable != null) {
            runnable.run();
            onResumeStaticCallback = null;
        }
        if (Theme.selectedAutoNightType == 3) {
            Theme.checkAutoNightThemeConditions();
        }
        checkWasMutedByAdmin(true);
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 4096);
        MediaController mediaController = MediaController.getInstance();
        ViewGroup view = this.actionBarLayout.getView();
        this.feedbackView = view;
        mediaController.setFeedbackView(view, true);
        ApplicationLoader.mainInterfacePaused = false;
        MessagesController.getInstance(this.currentAccount).sortDialogs(null);
        showLanguageAlert(false);
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$onResume$135();
            }
        });
        checkFreeDiscSpace(0);
        MediaController.checkGallery();
        onPasscodeResume();
        PasscodeViewDialog passcodeViewDialog = this.passcodeDialog;
        if (passcodeViewDialog == null || passcodeViewDialog.passcodeView.getVisibility() != 0) {
            this.actionBarLayout.onResume();
            if (AndroidUtilities.isTablet()) {
                ActionBarLayout actionBarLayout = this.rightActionBarLayout;
                if (actionBarLayout != null) {
                    actionBarLayout.onResume();
                }
                ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
                if (actionBarLayout2 != null) {
                    actionBarLayout2.onResume();
                }
            }
        } else {
            this.actionBarLayout.dismissDialogs();
            if (AndroidUtilities.isTablet()) {
                ActionBarLayout actionBarLayout3 = this.rightActionBarLayout;
                if (actionBarLayout3 != null) {
                    actionBarLayout3.dismissDialogs();
                }
                ActionBarLayout actionBarLayout4 = this.layersActionBarLayout;
                if (actionBarLayout4 != null) {
                    actionBarLayout4.dismissDialogs();
                }
            }
            this.passcodeDialog.passcodeView.onResume();
            Iterator<PasscodeView> it = this.overlayPasscodeViews.iterator();
            while (it.hasNext()) {
                it.next().onResume();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        updateCurrentConnectionState(this.currentAccount);
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onResume();
        }
        StoryRecorder.onResume();
        if (PipRoundVideoView.getInstance() != null && MediaController.getInstance().isMessagePaused() && (playingMessageObject = MediaController.getInstance().getPlayingMessageObject()) != null) {
            MediaController.getInstance().seekToProgress(playingMessageObject, playingMessageObject.audioProgress);
        }
        if (UserConfig.getInstance(UserConfig.selectedAccount).unacceptedTermsOfService != null) {
            int i = UserConfig.selectedAccount;
            showTosActivity(i, UserConfig.getInstance(i).unacceptedTermsOfService);
        } else {
            TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate = SharedConfig.pendingAppUpdate;
            if (tLRPC$TL_help_appUpdate != null && tLRPC$TL_help_appUpdate.can_not_skip) {
                showUpdateActivity(UserConfig.selectedAccount, SharedConfig.pendingAppUpdate, true);
            }
        }
        checkAppUpdate(false, null);
        if (Build.VERSION.SDK_INT >= 23) {
            canDrawOverlays = Settings.canDrawOverlays(this);
            ApplicationLoader.canDrawOverlays = canDrawOverlays;
        }
        if (VoIPFragment.getInstance() != null) {
            VoIPFragment.onResume();
        }
        invalidateTabletMode();
        SpoilerEffect2.pause(false);
        ApplicationLoader applicationLoader = ApplicationLoader.applicationLoaderInstance;
        if (applicationLoader != null) {
            applicationLoader.onResume();
        }
        Runnable runnable2 = whenResumed;
        if (runnable2 != null) {
            runnable2.run();
            whenResumed = null;
        }
    }

    public static void lambda$onResume$135() {
        ApplicationLoader.mainInterfacePausedStageQueue = false;
        ApplicationLoader.mainInterfacePausedStageQueueTime = System.currentTimeMillis();
    }

    private void invalidateTabletMode() {
        long j;
        Boolean wasTablet = AndroidUtilities.getWasTablet();
        if (wasTablet == null) {
            return;
        }
        AndroidUtilities.resetWasTabletFlag();
        if (wasTablet.booleanValue() != AndroidUtilities.isTablet()) {
            long j2 = 0;
            if (wasTablet.booleanValue()) {
                ArrayList<BaseFragment> arrayList = mainFragmentsStack;
                ArrayList<BaseFragment> arrayList2 = rightFragmentsStack;
                arrayList.addAll(arrayList2);
                ArrayList<BaseFragment> arrayList3 = layerFragmentsStack;
                arrayList.addAll(arrayList3);
                arrayList2.clear();
                arrayList3.clear();
                j = 0;
            } else {
                ArrayList<BaseFragment> arrayList4 = mainFragmentsStack;
                ArrayList<BaseFragment> arrayList5 = new ArrayList(arrayList4);
                arrayList4.clear();
                rightFragmentsStack.clear();
                layerFragmentsStack.clear();
                long j3 = 0;
                j = 0;
                for (BaseFragment baseFragment : arrayList5) {
                    if (baseFragment instanceof DialogsActivity) {
                        DialogsActivity dialogsActivity = (DialogsActivity) baseFragment;
                        if (dialogsActivity.isMainDialogList() && !dialogsActivity.isArchive()) {
                            mainFragmentsStack.add(baseFragment);
                        }
                    }
                    if (baseFragment instanceof ChatActivity) {
                        ChatActivity chatActivity = (ChatActivity) baseFragment;
                        if (!chatActivity.isInScheduleMode()) {
                            rightFragmentsStack.add(baseFragment);
                            if (j3 == 0) {
                                j3 = chatActivity.getDialogId();
                                j = chatActivity.getTopicId();
                            }
                        }
                    }
                    layerFragmentsStack.add(baseFragment);
                }
                j2 = j3;
            }
            setupActionBarLayout();
            this.actionBarLayout.rebuildFragments(1);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.rebuildFragments(1);
                this.layersActionBarLayout.rebuildFragments(1);
                Iterator<BaseFragment> it = mainFragmentsStack.iterator();
                while (it.hasNext()) {
                    BaseFragment next = it.next();
                    if (next instanceof DialogsActivity) {
                        DialogsActivity dialogsActivity2 = (DialogsActivity) next;
                        if (dialogsActivity2.isMainDialogList()) {
                            dialogsActivity2.setOpenedDialogId(j2, j);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        AndroidUtilities.checkDisplaySize(this, configuration);
        super.onConfigurationChanged(configuration);
        checkLayout();
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        if (pipRoundVideoView != null) {
            pipRoundVideoView.onConfigurationChanged();
        }
        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
        if (embedBottomSheet != null) {
            embedBottomSheet.onConfigurationChanged(configuration);
        }
        BoostPagerBottomSheet boostPagerBottomSheet = BoostPagerBottomSheet.getInstance();
        if (boostPagerBottomSheet != null) {
            boostPagerBottomSheet.onConfigurationChanged(configuration);
        }
        PhotoViewer pipInstance = PhotoViewer.getPipInstance();
        if (pipInstance != null) {
            pipInstance.onConfigurationChanged(configuration);
        }
        ThemeEditorView themeEditorView = ThemeEditorView.getInstance();
        if (themeEditorView != null) {
            themeEditorView.onConfigurationChanged();
        }
        if (Theme.selectedAutoNightType == 3) {
            Theme.checkAutoNightThemeConditions();
        }
    }

    @Override
    public void onMultiWindowModeChanged(boolean z) {
        AndroidUtilities.isInMultiwindow = z;
        checkLayout();
        super.onMultiWindowModeChanged(z);
    }

    @Override
    public void didReceivedNotification(int r21, final int r22, java.lang.Object... r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    public static void lambda$didReceivedNotification$136(int i, DialogInterface dialogInterface, int i2) {
        ArrayList<BaseFragment> arrayList = mainFragmentsStack;
        if (arrayList.isEmpty()) {
            return;
        }
        MessagesController.getInstance(i).openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
    }

    public void lambda$didReceivedNotification$137(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    public void lambda$didReceivedNotification$139(final HashMap hashMap, final int i, DialogInterface dialogInterface, int i2) {
        ArrayList<BaseFragment> arrayList = mainFragmentsStack;
        if (!arrayList.isEmpty() && AndroidUtilities.isMapsInstalled(arrayList.get(arrayList.size() - 1))) {
            LocationActivity locationActivity = new LocationActivity(0);
            locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate() {
                @Override
                public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i3, boolean z, int i4) {
                    LaunchActivity.lambda$didReceivedNotification$138(hashMap, i, tLRPC$MessageMedia, i3, z, i4);
                }
            });
            lambda$runLinkRequest$91(locationActivity);
        }
    }

    public static void lambda$didReceivedNotification$138(HashMap hashMap, int i, TLRPC$MessageMedia tLRPC$MessageMedia, int i2, boolean z, int i3) {
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) ((Map.Entry) it.next()).getValue();
            SendMessagesHelper.getInstance(i).sendMessage(SendMessagesHelper.SendMessageParams.of(tLRPC$MessageMedia, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i3));
        }
    }

    public static void lambda$didReceivedNotification$140(int i, HashMap hashMap, boolean z, boolean z2, DialogInterface dialogInterface, int i2) {
        ContactsController.getInstance(i).syncPhoneBookByAlert(hashMap, z, z2, false);
    }

    public static void lambda$didReceivedNotification$141(int i, HashMap hashMap, boolean z, boolean z2, DialogInterface dialogInterface, int i2) {
        ContactsController.getInstance(i).syncPhoneBookByAlert(hashMap, z, z2, true);
    }

    public static void lambda$didReceivedNotification$142(int i, HashMap hashMap, boolean z, boolean z2, DialogInterface dialogInterface, int i2) {
        ContactsController.getInstance(i).syncPhoneBookByAlert(hashMap, z, z2, true);
    }

    public void lambda$didReceivedNotification$143(ValueAnimator valueAnimator) {
        this.frameLayout.invalidate();
    }

    public void lambda$didReceivedNotification$144() {
        if (this.isNavigationBarColorFrozen) {
            this.isNavigationBarColorFrozen = false;
            checkSystemBarColors(false, true);
        }
    }

    public void lambda$didReceivedNotification$146(final Theme.ThemeInfo themeInfo, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$didReceivedNotification$145(tLObject, themeInfo);
            }
        });
    }

    public void lambda$didReceivedNotification$145(TLObject tLObject, Theme.ThemeInfo themeInfo) {
        if (tLObject instanceof TLRPC$TL_wallPaper) {
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) tLObject;
            this.loadingThemeInfo = themeInfo;
            this.loadingThemeWallpaperName = FileLoader.getAttachFileName(tLRPC$TL_wallPaper.document);
            this.loadingThemeWallpaper = tLRPC$TL_wallPaper;
            FileLoader.getInstance(themeInfo.account).loadFile(tLRPC$TL_wallPaper.document, tLRPC$TL_wallPaper, 1, 1);
            return;
        }
        onThemeLoadFinish();
    }

    public void lambda$didReceivedNotification$148(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$didReceivedNotification$147();
            }
        });
    }

    public void lambda$didReceivedNotification$147() {
        if (this.loadingTheme == null) {
            return;
        }
        File file = new File(ApplicationLoader.getFilesDirFixed(), "remote" + this.loadingTheme.id + ".attheme");
        TLRPC$TL_theme tLRPC$TL_theme = this.loadingTheme;
        Theme.ThemeInfo applyThemeFile = Theme.applyThemeFile(file, tLRPC$TL_theme.title, tLRPC$TL_theme, true);
        if (applyThemeFile != null) {
            lambda$runLinkRequest$91(new ThemePreviewActivity(applyThemeFile, true, 0, false, false));
        }
        onThemeLoadFinish();
    }

    private void invalidateCachedViews(View view) {
        if (view.getLayerType() != 0) {
            view.invalidate();
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                invalidateCachedViews(viewGroup.getChildAt(i));
            }
        }
    }

    private void checkWasMutedByAdmin(boolean z) {
        ChatObject.Call call;
        long j;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        boolean z2 = false;
        if (sharedInstance != null && (call = sharedInstance.groupCall) != null) {
            boolean z3 = this.wasMutedByAdminRaisedHand;
            TLRPC$InputPeer groupCallPeer = sharedInstance.getGroupCallPeer();
            if (groupCallPeer != null) {
                j = groupCallPeer.user_id;
                if (j == 0) {
                    long j2 = groupCallPeer.chat_id;
                    if (j2 == 0) {
                        j2 = groupCallPeer.channel_id;
                    }
                    j = -j2;
                }
            } else {
                j = UserConfig.getInstance(this.currentAccount).clientUserId;
            }
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = call.participants.get(j);
            boolean z4 = (tLRPC$TL_groupCallParticipant == null || tLRPC$TL_groupCallParticipant.can_self_unmute || !tLRPC$TL_groupCallParticipant.muted) ? false : true;
            if (z4 && tLRPC$TL_groupCallParticipant.raise_hand_rating != 0) {
                z2 = true;
            }
            this.wasMutedByAdminRaisedHand = z2;
            if (z || !z3 || z2 || z4 || GroupCallActivity.groupCallInstance != null) {
                return;
            }
            showVoiceChatTooltip(38);
            return;
        }
        this.wasMutedByAdminRaisedHand = false;
    }

    private void showVoiceChatTooltip(int i) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null || mainFragmentsStack.isEmpty() || sharedInstance.groupCall == null) {
            return;
        }
        TLRPC$Chat chat = sharedInstance.getChat();
        BaseFragment baseFragment = this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1);
        UndoView undoView = null;
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            if (chatActivity.getDialogId() == (-chat.id)) {
                chat = null;
            }
            undoView = chatActivity.getUndoView();
        } else if (baseFragment instanceof DialogsActivity) {
            undoView = ((DialogsActivity) baseFragment).getUndoView();
        } else if (baseFragment instanceof ProfileActivity) {
            undoView = ((ProfileActivity) baseFragment).getUndoView();
        }
        if (undoView != null) {
            undoView.showWithAction(0L, i, chat);
        }
        if (i != 38 || VoIPService.getSharedInstance() == null) {
            return;
        }
        VoIPService.getSharedInstance().playAllowTalkSound();
    }

    private String getStringForLanguageAlert(HashMap<String, String> hashMap, String str, int i) {
        String str2 = hashMap.get(str);
        return str2 == null ? LocaleController.getString(str, i) : str2;
    }

    private void openThemeAccentPreview(TLRPC$TL_theme tLRPC$TL_theme, TLRPC$TL_wallPaper tLRPC$TL_wallPaper, Theme.ThemeInfo themeInfo) {
        int i = themeInfo.lastAccentId;
        Theme.ThemeAccent createNewAccent = themeInfo.createNewAccent(tLRPC$TL_theme, this.currentAccount);
        themeInfo.prevAccentId = themeInfo.currentAccentId;
        themeInfo.setCurrentAccentId(createNewAccent.id);
        createNewAccent.pattern = tLRPC$TL_wallPaper;
        lambda$runLinkRequest$91(new ThemePreviewActivity(themeInfo, i != themeInfo.lastAccentId, 0, false, false));
    }

    private void onThemeLoadFinish() {
        AlertDialog alertDialog = this.loadingThemeProgressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } finally {
                this.loadingThemeProgressDialog = null;
            }
        }
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeFileName = null;
        this.loadingTheme = null;
    }

    private void checkFreeDiscSpace(final int i) {
        staticInstanceForAlerts = this;
        AutoDeleteMediaTask.run();
        SharedConfig.checkLogsToDelete();
        if ((Build.VERSION.SDK_INT < 26 || i != 0) && !this.checkFreeDiscSpaceShown) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$checkFreeDiscSpace$151(i);
                }
            }, 2000L);
        }
    }

    public void lambda$checkFreeDiscSpace$151(int i) {
        File directory;
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            try {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if ((((i == 2 || i == 1) && Math.abs(this.alreadyShownFreeDiscSpaceAlertForced - System.currentTimeMillis()) > 240000) || Math.abs(globalMainSettings.getLong("last_space_check", 0L) - System.currentTimeMillis()) >= 259200000) && (directory = FileLoader.getDirectory(4)) != null) {
                    StatFs statFs = new StatFs(directory.getAbsolutePath());
                    long availableBlocksLong = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
                    if (i > 0 || availableBlocksLong < 52428800) {
                        if (i > 0) {
                            this.alreadyShownFreeDiscSpaceAlertForced = System.currentTimeMillis();
                        }
                        globalMainSettings.edit().putLong("last_space_check", System.currentTimeMillis()).commit();
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public final void run() {
                                LaunchActivity.this.lambda$checkFreeDiscSpace$150();
                            }
                        });
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    public void lambda$checkFreeDiscSpace$150() {
        if (this.checkFreeDiscSpaceShown) {
            return;
        }
        try {
            Dialog createFreeSpaceDialog = AlertsCreator.createFreeSpaceDialog(this);
            createFreeSpaceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    LaunchActivity.this.lambda$checkFreeDiscSpace$149(dialogInterface);
                }
            });
            this.checkFreeDiscSpaceShown = true;
            createFreeSpaceDialog.show();
        } catch (Throwable unused) {
        }
    }

    public void lambda$checkFreeDiscSpace$149(DialogInterface dialogInterface) {
        this.checkFreeDiscSpaceShown = false;
    }

    public static void checkFreeDiscSpaceStatic(int i) {
        LaunchActivity launchActivity = staticInstanceForAlerts;
        if (launchActivity != null) {
            launchActivity.checkFreeDiscSpace(i);
        }
    }

    private void showLanguageAlertInternal(org.telegram.messenger.LocaleController.LocaleInfo r18, org.telegram.messenger.LocaleController.LocaleInfo r19, java.lang.String r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.showLanguageAlertInternal(org.telegram.messenger.LocaleController$LocaleInfo, org.telegram.messenger.LocaleController$LocaleInfo, java.lang.String):void");
    }

    public static void lambda$showLanguageAlertInternal$152(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue(), true);
            i++;
        }
    }

    public void lambda$showLanguageAlertInternal$153(View view) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$91(new LanguageSelectActivity());
        for (int i = 0; i < this.visibleDialogs.size(); i++) {
            if (this.visibleDialogs.get(i).isShowing()) {
                this.visibleDialogs.get(i).dismiss();
            }
        }
        this.visibleDialogs.clear();
    }

    public void lambda$showLanguageAlertInternal$154(LocaleController.LocaleInfo[] localeInfoArr, DialogInterface dialogInterface, int i) {
        LocaleController.getInstance().applyLanguage(localeInfoArr[0], true, false, this.currentAccount);
        rebuildAllFragments(true);
    }

    public void drawRippleAbove(Canvas canvas, View view) {
        View view2;
        if (view == null || (view2 = this.rippleAbove) == null || view2.getBackground() == null) {
            return;
        }
        if (this.tempLocation == null) {
            this.tempLocation = new int[2];
        }
        this.rippleAbove.getLocationInWindow(this.tempLocation);
        int[] iArr = this.tempLocation;
        int i = iArr[0];
        int i2 = iArr[1];
        view.getLocationInWindow(iArr);
        int[] iArr2 = this.tempLocation;
        int i3 = i - iArr2[0];
        int i4 = i2 - iArr2[1];
        canvas.save();
        canvas.translate(i3, i4);
        this.rippleAbove.getBackground().draw(canvas);
        canvas.restore();
    }

    private void showLanguageAlert(boolean z) {
        String str;
        LocaleController.LocaleInfo localeInfo;
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            try {
                if (!this.loadingLocaleDialog && !ApplicationLoader.mainInterfacePaused) {
                    String string = MessagesController.getGlobalMainSettings().getString("language_showed2", "");
                    final String str2 = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
                    if (!z && string.equals(str2)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("alert already showed for " + string);
                            return;
                        }
                        return;
                    }
                    final LocaleController.LocaleInfo[] localeInfoArr = new LocaleController.LocaleInfo[2];
                    String str3 = str2.contains("-") ? str2.split("-")[0] : str2;
                    if ("in".equals(str3)) {
                        str = "id";
                    } else if ("iw".equals(str3)) {
                        str = "he";
                    } else {
                        str = "jw".equals(str3) ? "jv" : null;
                    }
                    for (int i = 0; i < LocaleController.getInstance().languages.size(); i++) {
                        LocaleController.LocaleInfo localeInfo2 = LocaleController.getInstance().languages.get(i);
                        if (localeInfo2.shortName.equals("en")) {
                            localeInfoArr[0] = localeInfo2;
                        }
                        if (localeInfo2.shortName.replace("_", "-").equals(str2) || localeInfo2.shortName.equals(str3) || localeInfo2.shortName.equals(str)) {
                            localeInfoArr[1] = localeInfo2;
                        }
                        if (localeInfoArr[0] != null && localeInfoArr[1] != null) {
                            break;
                        }
                    }
                    LocaleController.LocaleInfo localeInfo3 = localeInfoArr[0];
                    if (localeInfo3 != null && (localeInfo = localeInfoArr[1]) != null && localeInfo3 != localeInfo) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("show lang alert for " + localeInfoArr[0].getKey() + " and " + localeInfoArr[1].getKey());
                        }
                        this.systemLocaleStrings = null;
                        this.englishLocaleStrings = null;
                        this.loadingLocaleDialog = true;
                        TLRPC$TL_langpack_getStrings tLRPC$TL_langpack_getStrings = new TLRPC$TL_langpack_getStrings();
                        tLRPC$TL_langpack_getStrings.lang_code = localeInfoArr[1].getLangCode();
                        tLRPC$TL_langpack_getStrings.keys.add("English");
                        tLRPC$TL_langpack_getStrings.keys.add("ChooseYourLanguage");
                        tLRPC$TL_langpack_getStrings.keys.add("ChooseYourLanguageOther");
                        tLRPC$TL_langpack_getStrings.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings, new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                LaunchActivity.this.lambda$showLanguageAlert$156(localeInfoArr, str2, tLObject, tLRPC$TL_error);
                            }
                        }, 8);
                        TLRPC$TL_langpack_getStrings tLRPC$TL_langpack_getStrings2 = new TLRPC$TL_langpack_getStrings();
                        tLRPC$TL_langpack_getStrings2.lang_code = localeInfoArr[0].getLangCode();
                        tLRPC$TL_langpack_getStrings2.keys.add("English");
                        tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguage");
                        tLRPC$TL_langpack_getStrings2.keys.add("ChooseYourLanguageOther");
                        tLRPC$TL_langpack_getStrings2.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_langpack_getStrings2, new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                LaunchActivity.this.lambda$showLanguageAlert$158(localeInfoArr, str2, tLObject, tLRPC$TL_error);
                            }
                        }, 8);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void lambda$showLanguageAlert$156(final LocaleController.LocaleInfo[] localeInfoArr, final String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        final HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(i);
                hashMap.put(tLRPC$LangPackString.key, tLRPC$LangPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$showLanguageAlert$155(hashMap, localeInfoArr, str);
            }
        });
    }

    public void lambda$showLanguageAlert$155(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = hashMap;
        if (this.englishLocaleStrings == null || hashMap == null) {
            return;
        }
        showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
    }

    public void lambda$showLanguageAlert$158(final LocaleController.LocaleInfo[] localeInfoArr, final String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        final HashMap hashMap = new HashMap();
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$LangPackString tLRPC$LangPackString = (TLRPC$LangPackString) tLRPC$Vector.objects.get(i);
                hashMap.put(tLRPC$LangPackString.key, tLRPC$LangPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$showLanguageAlert$157(hashMap, localeInfoArr, str);
            }
        });
    }

    public void lambda$showLanguageAlert$157(HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.englishLocaleStrings = hashMap;
        if (hashMap == null || this.systemLocaleStrings == null) {
            return;
        }
        showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
    }

    private void onPasscodePause() {
        if (this.lockRunnable != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("cancel lockRunnable onPasscodePause");
            }
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (SharedConfig.passcodeHash.length() != 0) {
            SharedConfig.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (LaunchActivity.this.lockRunnable == this) {
                        if (AndroidUtilities.needShowPasscode(true)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("lock app");
                            }
                            LaunchActivity.this.showPasscodeActivity(true, false, -1, -1, null, null);
                            try {
                                NotificationsController.getInstance(UserConfig.selectedAccount).showNotifications();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("didn't pass lock check");
                        }
                        LaunchActivity.this.lockRunnable = null;
                    }
                }
            };
            this.lockRunnable = runnable;
            if (SharedConfig.appLocked) {
                AndroidUtilities.runOnUIThread(runnable, 1000L);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("schedule app lock in 1000");
                }
            } else if (SharedConfig.autoLockIn != 0) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("schedule app lock in " + ((SharedConfig.autoLockIn * 1000) + 1000));
                }
                AndroidUtilities.runOnUIThread(this.lockRunnable, (SharedConfig.autoLockIn * 1000) + 1000);
            }
        } else {
            SharedConfig.lastPauseTime = 0;
        }
        SharedConfig.saveConfig();
    }

    public void addOverlayPasscodeView(PasscodeView passcodeView) {
        this.overlayPasscodeViews.add(passcodeView);
    }

    public void removeOverlayPasscodeView(PasscodeView passcodeView) {
        this.overlayPasscodeViews.remove(passcodeView);
    }

    private void onPasscodeResume() {
        if (this.lockRunnable != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("cancel lockRunnable onPasscodeResume");
            }
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (AndroidUtilities.needShowPasscode(true)) {
            showPasscodeActivity(true, false, -1, -1, null, null);
        }
        if (SharedConfig.lastPauseTime != 0) {
            SharedConfig.lastPauseTime = 0;
            SharedConfig.saveConfig();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset lastPauseTime onPasscodeResume");
            }
        }
    }

    private void updateCurrentConnectionState(int i) {
        int i2;
        String str;
        if (this.actionBarLayout == null) {
            return;
        }
        int connectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
        this.currentConnectionState = connectionState;
        if (connectionState == 2) {
            i2 = R.string.WaitingForNetwork;
            str = "WaitingForNetwork";
        } else if (connectionState == 5) {
            i2 = R.string.Updating;
            str = "Updating";
        } else if (connectionState == 4) {
            i2 = R.string.ConnectingToProxy;
            str = "ConnectingToProxy";
        } else if (connectionState == 1) {
            i2 = R.string.Connecting;
            str = "Connecting";
        } else {
            i2 = 0;
            str = null;
        }
        this.actionBarLayout.setTitleOverlayText(str, i2, (connectionState == 1 || connectionState == 4) ? new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$updateCurrentConnectionState$159();
            }
        } : null);
    }

    public void lambda$updateCurrentConnectionState$159() {
        BaseFragment baseFragment;
        if (AndroidUtilities.isTablet()) {
            ArrayList<BaseFragment> arrayList = layerFragmentsStack;
            if (!arrayList.isEmpty()) {
                baseFragment = arrayList.get(arrayList.size() - 1);
            }
            baseFragment = null;
        } else {
            ArrayList<BaseFragment> arrayList2 = mainFragmentsStack;
            if (!arrayList2.isEmpty()) {
                baseFragment = arrayList2.get(arrayList2.size() - 1);
            }
            baseFragment = null;
        }
        if ((baseFragment instanceof ProxyListActivity) || (baseFragment instanceof ProxySettingsActivity)) {
            return;
        }
        lambda$runLinkRequest$91(new ProxyListActivity());
    }

    public void hideVisibleActionMode() {
        ActionMode actionMode = this.visibleActionMode;
        if (actionMode == null) {
            return;
        }
        actionMode.finish();
    }

    @Override
    protected void onSaveInstanceState(android.os.Bundle r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.onSaveInstanceState(android.os.Bundle):void");
    }

    @Override
    public void onBackPressed() {
        if (FloatingDebugController.onBackPressed()) {
            return;
        }
        PasscodeViewDialog passcodeViewDialog = this.passcodeDialog;
        if (passcodeViewDialog != null && passcodeViewDialog.passcodeView.getVisibility() == 0) {
            finish();
            return;
        }
        BottomSheetTabsOverlay bottomSheetTabsOverlay = this.bottomSheetTabsOverlay;
        if ((bottomSheetTabsOverlay == null || !bottomSheetTabsOverlay.onBackPressed()) && !SearchTagsList.onBackPressedRenameTagAlert()) {
            if (ContentPreviewViewer.hasInstance() && ContentPreviewViewer.getInstance().isVisible()) {
                ContentPreviewViewer.getInstance().closeWithMenu();
                return;
            }
            if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                SecretMediaViewer.getInstance().closePhoto(true, false);
                return;
            }
            if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                PhotoViewer.getInstance().closePhoto(true, false);
                return;
            }
            if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                ArticleViewer.getInstance().close(true, false);
                return;
            }
            if (this.drawerLayoutContainer.isDrawerOpened()) {
                this.drawerLayoutContainer.closeDrawer(false);
                return;
            }
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getView().getVisibility() == 0) {
                    this.layersActionBarLayout.onBackPressed();
                    return;
                }
                if (this.rightActionBarLayout.getView().getVisibility() == 0 && !this.rightActionBarLayout.getFragmentStack().isEmpty()) {
                    BaseFragment baseFragment = this.rightActionBarLayout.getFragmentStack().get(this.rightActionBarLayout.getFragmentStack().size() - 1);
                    if (baseFragment.onBackPressed()) {
                        baseFragment.lambda$onBackPressed$308();
                        return;
                    }
                    return;
                }
                this.actionBarLayout.onBackPressed();
                return;
            }
            this.actionBarLayout.onBackPressed();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ActionBarLayout actionBarLayout = this.actionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.onLowMemory();
            if (AndroidUtilities.isTablet()) {
                ActionBarLayout actionBarLayout2 = this.rightActionBarLayout;
                if (actionBarLayout2 != null) {
                    actionBarLayout2.onLowMemory();
                }
                ActionBarLayout actionBarLayout3 = this.layersActionBarLayout;
                if (actionBarLayout3 != null) {
                    actionBarLayout3.onLowMemory();
                }
            }
        }
    }

    @Override
    public void onActionModeStarted(ActionMode actionMode) {
        int type;
        super.onActionModeStarted(actionMode);
        this.visibleActionMode = actionMode;
        try {
            Menu menu = actionMode.getMenu();
            if (menu != null && !this.actionBarLayout.extendActionMode(menu) && AndroidUtilities.isTablet() && !this.rightActionBarLayout.extendActionMode(menu)) {
                this.layersActionBarLayout.extendActionMode(menu);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            type = actionMode.getType();
            if (type == 1) {
                return;
            }
        }
        this.actionBarLayout.onActionModeStarted(actionMode);
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onActionModeStarted(actionMode);
            this.layersActionBarLayout.onActionModeStarted(actionMode);
        }
    }

    @Override
    public void onActionModeFinished(ActionMode actionMode) {
        int type;
        super.onActionModeFinished(actionMode);
        if (this.visibleActionMode == actionMode) {
            this.visibleActionMode = null;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            type = actionMode.getType();
            if (type == 1) {
                return;
            }
        }
        this.actionBarLayout.onActionModeFinished(actionMode);
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onActionModeFinished(actionMode);
            this.layersActionBarLayout.onActionModeFinished(actionMode);
        }
    }

    @Override
    public boolean onPreIme() {
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(true, false);
            return true;
        }
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
            return true;
        }
        if (!ArticleViewer.hasInstance() || !ArticleViewer.getInstance().isVisible()) {
            return false;
        }
        ArticleViewer.getInstance().close(true, false);
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        BaseFragment lastFragment;
        int streamMinVolume;
        keyEvent.getKeyCode();
        if ((keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25) && (lastFragment = getLastFragment()) != null && lastFragment.getLastStoryViewer() != null) {
            lastFragment.getLastStoryViewer().dispatchKeyEvent(keyEvent);
            return true;
        }
        if (keyEvent.getAction() == 0 && (keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25)) {
            if (VoIPService.getSharedInstance() != null) {
                if (Build.VERSION.SDK_INT >= 32) {
                    boolean isSpeakerMuted = WebRtcAudioTrack.isSpeakerMuted();
                    AudioManager audioManager = (AudioManager) getSystemService("audio");
                    streamMinVolume = audioManager.getStreamMinVolume(0);
                    boolean z = audioManager.getStreamVolume(0) == streamMinVolume && keyEvent.getKeyCode() == 25;
                    WebRtcAudioTrack.setSpeakerMute(z);
                    if (isSpeakerMuted != WebRtcAudioTrack.isSpeakerMuted()) {
                        showVoiceChatTooltip(z ? 42 : 43);
                    }
                }
            } else {
                ArrayList<BaseFragment> arrayList = mainFragmentsStack;
                if (!arrayList.isEmpty() && ((!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isVisible()) && keyEvent.getRepeatCount() == 0)) {
                    BaseFragment baseFragment = arrayList.get(arrayList.size() - 1);
                    if ((baseFragment instanceof ChatActivity) && !BaseFragment.hasSheets(baseFragment) && ((ChatActivity) baseFragment).maybePlayVisibleVideo()) {
                        return true;
                    }
                    if (AndroidUtilities.isTablet()) {
                        ArrayList<BaseFragment> arrayList2 = rightFragmentsStack;
                        if (!arrayList2.isEmpty()) {
                            BaseFragment baseFragment2 = arrayList2.get(arrayList2.size() - 1);
                            if ((baseFragment2 instanceof ChatActivity) && !BaseFragment.hasSheets(baseFragment2) && ((ChatActivity) baseFragment2).maybePlayVisibleVideo()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        try {
            return super.dispatchKeyEvent(keyEvent);
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    @Override
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i == 82 && !SharedConfig.isWaitingForPasscodeEnter) {
            if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                return super.onKeyUp(i, keyEvent);
            }
            if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                return super.onKeyUp(i, keyEvent);
            }
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getView().getVisibility() == 0 && !this.layersActionBarLayout.getFragmentStack().isEmpty()) {
                    this.layersActionBarLayout.getView().onKeyUp(i, keyEvent);
                } else if (this.rightActionBarLayout.getView().getVisibility() == 0 && !this.rightActionBarLayout.getFragmentStack().isEmpty()) {
                    this.rightActionBarLayout.getView().onKeyUp(i, keyEvent);
                } else {
                    this.actionBarLayout.getView().onKeyUp(i, keyEvent);
                }
            } else if (this.actionBarLayout.getFragmentStack().size() == 1) {
                if (!this.drawerLayoutContainer.isDrawerOpened()) {
                    if (getCurrentFocus() != null) {
                        AndroidUtilities.hideKeyboard(getCurrentFocus());
                    }
                    this.drawerLayoutContainer.openDrawer(false);
                } else {
                    this.drawerLayoutContainer.closeDrawer(false);
                }
            } else {
                this.actionBarLayout.getView().onKeyUp(i, keyEvent);
            }
        }
        return super.onKeyUp(i, keyEvent);
    }

    @Override
    public boolean needPresentFragment(org.telegram.ui.ActionBar.INavigationLayout r9, org.telegram.ui.ActionBar.INavigationLayout.NavigationParams r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.needPresentFragment(org.telegram.ui.ActionBar.INavigationLayout, org.telegram.ui.ActionBar.INavigationLayout$NavigationParams):boolean");
    }

    @Override
    public boolean needAddFragmentToStack(org.telegram.ui.ActionBar.BaseFragment r6, org.telegram.ui.ActionBar.INavigationLayout r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.needAddFragmentToStack(org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.ActionBar.INavigationLayout):boolean");
    }

    @Override
    public boolean needCloseLastFragment(INavigationLayout iNavigationLayout) {
        if (AndroidUtilities.isTablet()) {
            if (iNavigationLayout == this.actionBarLayout && iNavigationLayout.getFragmentStack().size() <= 1 && !this.switchingAccount) {
                onFinish();
                finish();
                return false;
            }
            if (iNavigationLayout == this.rightActionBarLayout) {
                if (!this.tabletFullSize) {
                    this.backgroundTablet.setVisibility(0);
                }
            } else if (iNavigationLayout == this.layersActionBarLayout && this.actionBarLayout.getFragmentStack().isEmpty() && this.layersActionBarLayout.getFragmentStack().size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else {
            if (iNavigationLayout.getFragmentStack().size() <= 1) {
                onFinish();
                finish();
                return false;
            }
            if (iNavigationLayout.getFragmentStack().size() >= 2 && !(iNavigationLayout.getFragmentStack().get(0) instanceof LoginActivity)) {
                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            }
        }
        return true;
    }

    public void rebuildAllFragments(boolean z) {
        ActionBarLayout actionBarLayout = this.layersActionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.rebuildAllFragmentViews(z, z);
        } else {
            this.actionBarLayout.rebuildAllFragmentViews(z, z);
        }
    }

    @Override
    public void onRebuildAllFragments(INavigationLayout iNavigationLayout, boolean z) {
        if (AndroidUtilities.isTablet() && iNavigationLayout == this.layersActionBarLayout) {
            this.rightActionBarLayout.rebuildAllFragmentViews(z, z);
            this.actionBarLayout.rebuildAllFragmentViews(z, z);
        }
        this.drawerLayoutAdapter.notifyDataSetChanged();
    }

    public static BaseFragment getLastFragment() {
        LaunchActivity launchActivity = instance;
        if (launchActivity != null && !launchActivity.sheetFragmentsStack.isEmpty()) {
            return instance.sheetFragmentsStack.get(r0.size() - 1).getLastFragment();
        }
        LaunchActivity launchActivity2 = instance;
        if (launchActivity2 == null || launchActivity2.getActionBarLayout() == null) {
            return null;
        }
        return instance.getActionBarLayout().getLastFragment();
    }

    public static BaseFragment getSafeLastFragment() {
        LaunchActivity launchActivity = instance;
        if (launchActivity != null && !launchActivity.sheetFragmentsStack.isEmpty()) {
            return instance.sheetFragmentsStack.get(r0.size() - 1).getSafeLastFragment();
        }
        LaunchActivity launchActivity2 = instance;
        if (launchActivity2 == null || launchActivity2.getActionBarLayout() == null) {
            return null;
        }
        return instance.getActionBarLayout().getSafeLastFragment();
    }

    public void requestCustomNavigationBar() {
        if (this.customNavigationBar == null && Build.VERSION.SDK_INT >= 26) {
            this.customNavigationBar = this.drawerLayoutContainer.createNavigationBar();
            ((FrameLayout) getWindow().getDecorView()).addView(this.customNavigationBar);
        }
        View view = this.customNavigationBar;
        if (view != null) {
            if (view.getLayoutParams().height == AndroidUtilities.navigationBarHeight && ((FrameLayout.LayoutParams) this.customNavigationBar.getLayoutParams()).topMargin == this.customNavigationBar.getHeight()) {
                return;
            }
            this.customNavigationBar.getLayoutParams().height = AndroidUtilities.navigationBarHeight;
            ((FrameLayout.LayoutParams) this.customNavigationBar.getLayoutParams()).topMargin = this.drawerLayoutContainer.getMeasuredHeight();
            this.customNavigationBar.requestLayout();
        }
    }

    public int getNavigationBarColor() {
        int navigationBarColor;
        if (Build.VERSION.SDK_INT < 26) {
            return 0;
        }
        Window window = getWindow();
        if (this.customNavigationBar != null) {
            return this.drawerLayoutContainer.getNavigationBarColor();
        }
        navigationBarColor = window.getNavigationBarColor();
        return navigationBarColor;
    }

    public void setNavigationBarColor(int i, boolean z) {
        int navigationBarColor;
        if (Build.VERSION.SDK_INT >= 26) {
            Window window = getWindow();
            if (this.customNavigationBar != null) {
                if (this.drawerLayoutContainer.getNavigationBarColor() != i) {
                    this.drawerLayoutContainer.setNavigationBarColor(i);
                    if (z) {
                        AndroidUtilities.setLightNavigationBar(window, AndroidUtilities.computePerceivedBrightness(i) >= 0.721f);
                    }
                }
            } else {
                navigationBarColor = window.getNavigationBarColor();
                if (navigationBarColor != i) {
                    window.setNavigationBarColor(i);
                    if (z) {
                        AndroidUtilities.setLightNavigationBar(window, AndroidUtilities.computePerceivedBrightness(i) >= 0.721f);
                    }
                }
            }
        }
        BottomSheetTabs bottomSheetTabs = getBottomSheetTabs();
        if (bottomSheetTabs != null) {
            bottomSheetTabs.setNavigationBarColor(i);
        }
    }

    public BottomSheetTabs getBottomSheetTabs() {
        ActionBarLayout actionBarLayout = this.rightActionBarLayout;
        if (actionBarLayout != null && actionBarLayout.getBottomSheetTabs() != null) {
            return this.rightActionBarLayout.getBottomSheetTabs();
        }
        ActionBarLayout actionBarLayout2 = this.actionBarLayout;
        if (actionBarLayout2 == null || actionBarLayout2.getBottomSheetTabs() == null) {
            return null;
        }
        return this.actionBarLayout.getBottomSheetTabs();
    }

    public void animateNavigationBarColor(final int i) {
        ValueAnimator ofArgb;
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        ValueAnimator valueAnimator = this.navBarAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.navBarAnimator = null;
        }
        ofArgb = ValueAnimator.ofArgb(getNavigationBarColor(), i);
        this.navBarAnimator = ofArgb;
        ofArgb.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                LaunchActivity.this.lambda$animateNavigationBarColor$160(valueAnimator2);
            }
        });
        this.navBarAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                LaunchActivity.this.setNavigationBarColor(i, false);
            }
        });
        this.navBarAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.navBarAnimator.setDuration(320L);
        this.navBarAnimator.start();
    }

    public void lambda$animateNavigationBarColor$160(ValueAnimator valueAnimator) {
        setNavigationBarColor(((Integer) valueAnimator.getAnimatedValue()).intValue(), false);
    }

    public void setLightNavigationBar(boolean z) {
        if (Build.VERSION.SDK_INT >= 26) {
            AndroidUtilities.setLightNavigationBar(getWindow(), z);
        }
    }

    public boolean isLightNavigationBar() {
        return AndroidUtilities.getLightNavigationBar(getWindow());
    }

    private void openMyStory(final int r12, final boolean r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.openMyStory(int, boolean):void");
    }

    public void lambda$openMyStory$162(final int i, final long j, final boolean z, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.this.lambda$openMyStory$161(tLObject, i, j, z);
            }
        });
    }

    public void lambda$openMyStory$161(TLObject tLObject, int i, long j, boolean z) {
        StoriesListPlaceProvider storiesListPlaceProvider;
        TL_stories$StoryItem tL_stories$StoryItem;
        if (tLObject instanceof TL_stories$TL_stories_stories) {
            TL_stories$TL_stories_stories tL_stories$TL_stories_stories = (TL_stories$TL_stories_stories) tLObject;
            int i2 = 0;
            while (true) {
                storiesListPlaceProvider = null;
                if (i2 >= tL_stories$TL_stories_stories.stories.size()) {
                    tL_stories$StoryItem = null;
                    break;
                } else {
                    if (tL_stories$TL_stories_stories.stories.get(i2).id == i) {
                        tL_stories$StoryItem = tL_stories$TL_stories_stories.stories.get(i2);
                        break;
                    }
                    i2++;
                }
            }
            if (tL_stories$StoryItem != null) {
                tL_stories$StoryItem.dialogId = j;
                BaseFragment lastFragment = getLastFragment();
                if (lastFragment == null) {
                    return;
                }
                if (lastFragment instanceof DialogsActivity) {
                    try {
                        storiesListPlaceProvider = StoriesListPlaceProvider.of(((DialogsActivity) lastFragment).dialogStoriesCell.recyclerListView);
                    } catch (Exception unused) {
                    }
                }
                StoriesListPlaceProvider storiesListPlaceProvider2 = storiesListPlaceProvider;
                lastFragment.getOrCreateStoryViewer().instantClose();
                ArrayList<Long> arrayList = new ArrayList<>();
                arrayList.add(Long.valueOf(j));
                if (z) {
                    lastFragment.getOrCreateStoryViewer().showViewsAfterOpening();
                }
                lastFragment.getOrCreateStoryViewer().open(this, tL_stories$StoryItem, arrayList, 0, null, null, storiesListPlaceProvider2, false);
                return;
            }
        }
        BulletinFactory.global().createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.StoryNotFound)).show(false);
    }

    private void openStories(long[] jArr, boolean z) {
        boolean z2;
        StoriesListPlaceProvider of;
        final long[] jArr2 = jArr;
        int i = 0;
        while (true) {
            if (i >= jArr2.length) {
                z2 = true;
                break;
            }
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(jArr2[i]));
            if (user != null && !user.stories_hidden) {
                z2 = false;
                break;
            }
            i++;
        }
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment == null) {
            return;
        }
        StoriesController storiesController = MessagesController.getInstance(this.currentAccount).getStoriesController();
        ArrayList arrayList = new ArrayList(z2 ? storiesController.getHiddenList() : storiesController.getDialogListStories());
        ArrayList<Long> arrayList2 = new ArrayList<>();
        ArrayList arrayList3 = new ArrayList();
        if (!z2) {
            ArrayList arrayList4 = new ArrayList();
            for (int i2 = 0; i2 < jArr2.length; i2++) {
                TLRPC$User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(jArr2[i2]));
                if (user2 == null || !user2.stories_hidden) {
                    arrayList4.add(Long.valueOf(jArr2[i2]));
                }
            }
            jArr2 = Longs.toArray(arrayList4);
        }
        if (z) {
            for (long j : jArr2) {
                arrayList3.add(Long.valueOf(j));
            }
        } else {
            for (long j2 : jArr2) {
                arrayList2.add(Long.valueOf(j2));
            }
        }
        if (!arrayList3.isEmpty() && z) {
            final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            final int[] iArr = {arrayList3.size()};
            final Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    LaunchActivity.this.lambda$openStories$163(iArr, jArr2);
                }
            };
            for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                final long longValue = ((Long) arrayList3.get(i3)).longValue();
                TL_stories$TL_stories_getPeerStories tL_stories$TL_stories_getPeerStories = new TL_stories$TL_stories_getPeerStories();
                TLRPC$InputPeer inputPeer = messagesController.getInputPeer(longValue);
                tL_stories$TL_stories_getPeerStories.peer = inputPeer;
                if (inputPeer instanceof TLRPC$TL_inputPeerEmpty) {
                    iArr[0] = iArr[0] - 1;
                } else if (inputPeer == null) {
                    iArr[0] = iArr[0] - 1;
                } else {
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories$TL_stories_getPeerStories, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            LaunchActivity.lambda$openStories$165(MessagesController.this, longValue, runnable, tLObject, tLRPC$TL_error);
                        }
                    });
                }
            }
            return;
        }
        long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            long peerDialogId = DialogObject.getPeerDialogId(((TL_stories$PeerStories) arrayList.get(i4)).peer);
            if (peerDialogId != clientUserId && !arrayList2.contains(Long.valueOf(peerDialogId)) && storiesController.hasUnreadStories(peerDialogId)) {
                arrayList2.add(Long.valueOf(peerDialogId));
            }
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        if (lastFragment instanceof DialogsActivity) {
            try {
                of = StoriesListPlaceProvider.of(((DialogsActivity) lastFragment).dialogStoriesCell.recyclerListView);
            } catch (Exception unused) {
            }
            StoriesListPlaceProvider storiesListPlaceProvider = of;
            lastFragment.getOrCreateStoryViewer().instantClose();
            lastFragment.getOrCreateStoryViewer().open(this, null, arrayList2, 0, null, null, storiesListPlaceProvider, false);
        }
        of = null;
        StoriesListPlaceProvider storiesListPlaceProvider2 = of;
        lastFragment.getOrCreateStoryViewer().instantClose();
        lastFragment.getOrCreateStoryViewer().open(this, null, arrayList2, 0, null, null, storiesListPlaceProvider2, false);
    }

    public void lambda$openStories$163(int[] iArr, long[] jArr) {
        int i = iArr[0] - 1;
        iArr[0] = i;
        if (i == 0) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesUpdated, new Object[0]);
            openStories(jArr, false);
        }
    }

    public static void lambda$openStories$165(final MessagesController messagesController, final long j, final Runnable runnable, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LaunchActivity.lambda$openStories$164(TLObject.this, messagesController, j, runnable);
            }
        });
    }

    public static void lambda$openStories$164(TLObject tLObject, MessagesController messagesController, long j, Runnable runnable) {
        if (tLObject instanceof TL_stories$TL_stories_peerStories) {
            TL_stories$TL_stories_peerStories tL_stories$TL_stories_peerStories = (TL_stories$TL_stories_peerStories) tLObject;
            messagesController.putUsers(tL_stories$TL_stories_peerStories.users, false);
            messagesController.getStoriesController().putStories(j, tL_stories$TL_stories_peerStories.stories);
            runnable.run();
            return;
        }
        runnable.run();
    }

    public static void dismissAllWeb() {
        ArrayList<BaseFragment.AttachedSheet> arrayList;
        BaseFragment safeLastFragment = getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        EmptyBaseFragment sheetFragment = safeLastFragment.getParentLayout() instanceof ActionBarLayout ? ((ActionBarLayout) safeLastFragment.getParentLayout()).getSheetFragment(false) : null;
        if (sheetFragment != null && (arrayList = sheetFragment.sheetsStack) != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                sheetFragment.sheetsStack.get(size).dismiss(true);
            }
        }
        ArrayList<BaseFragment.AttachedSheet> arrayList2 = safeLastFragment.sheetsStack;
        if (arrayList2 != null) {
            for (int size2 = arrayList2.size() - 1; size2 >= 0; size2--) {
                safeLastFragment.sheetsStack.get(size2).dismiss(true);
            }
        }
    }

    public static void makeRipple(float f, float f2, float f3) {
        LaunchActivity launchActivity = instance;
        if (launchActivity == null) {
            return;
        }
        launchActivity.makeRippleInternal(f, f2, f3);
    }

    private void makeRippleInternal(float f, float f2, float f3) {
        ISuperRipple iSuperRipple;
        View decorView = getWindow().getDecorView();
        if (decorView == null) {
            return;
        }
        int i = Build.VERSION.SDK_INT;
        if (i >= 33) {
            ISuperRipple iSuperRipple2 = this.currentRipple;
            if (iSuperRipple2 == null || iSuperRipple2.view != decorView) {
                this.currentRipple = new SuperRipple(decorView);
            }
        } else if (i >= 26 && ((iSuperRipple = this.currentRipple) == null || iSuperRipple.view != decorView)) {
            this.currentRipple = new SuperRippleFallback(decorView);
        }
        ISuperRipple iSuperRipple3 = this.currentRipple;
        if (iSuperRipple3 != null) {
            iSuperRipple3.animate(f, f2, f3);
        }
    }
}
