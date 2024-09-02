package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FactCheckController$Key$$ExternalSyntheticBackport0;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SavedMessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputUser;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotos;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterVideo;
import org.telegram.tgnet.TLRPC$TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC$TL_messages_getCommonChats;
import org.telegram.tgnet.TLRPC$TL_messages_getSearchResultsPositions;
import org.telegram.tgnet.TLRPC$TL_messages_messages;
import org.telegram.tgnet.TLRPC$TL_messages_messagesSlice;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$TL_messages_searchResultsPositions;
import org.telegram.tgnet.TLRPC$TL_searchResultPosition;
import org.telegram.tgnet.TLRPC$TL_webPageEmpty;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.tgnet.TLRPC$messages_Chats;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.tgnet.tl.TL_bots$BotInfo;
import org.telegram.tgnet.tl.TL_stories$MediaArea;
import org.telegram.tgnet.tl.TL_stories$StoryItem;
import org.telegram.tgnet.tl.TL_stories$TL_stories_storyViews;
import org.telegram.tgnet.tl.TL_stories$TL_storyItem;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell2;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatActivityContainer;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.StoryViewer;
import org.telegram.ui.Stories.UserListPoller;
import org.telegram.ui.Stories.ViewsForPeerStoriesRequester;
import org.telegram.ui.Stories.bots.BotPreviewsEditContainer;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.TopicsFragment;

public class SharedMediaLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, DialogCell.DialogCellDelegate {
    private ActionBar actionBar;
    private AnimatorSet actionModeAnimation;
    private LinearLayout actionModeLayout;
    private ArrayList<View> actionModeViews;
    private SpannableStringBuilder addPostButton;
    private float additionalFloatingTranslation;
    private boolean allowStoriesSingleColumn;
    private int animateToColumnsCount;
    private boolean animatingForward;
    private boolean animatingToOptions;
    private StoriesAdapter animationSupportingArchivedStoriesAdapter;
    private SharedPhotoVideoAdapter animationSupportingPhotoVideoAdapter;
    private StoriesAdapter animationSupportingStoriesAdapter;
    private StoriesAdapter archivedStoriesAdapter;
    private SharedDocumentsAdapter audioAdapter;
    private ArrayList<SharedAudioCell> audioCache;
    private ArrayList<SharedAudioCell> audioCellCache;
    private MediaSearchAdapter audioSearchAdapter;
    private boolean backAnimation;
    private BackDrawable backDrawable;
    private Paint backgroundPaint;
    private BotPreviewsEditContainer botPreviewsContainer;
    private ArrayList<SharedPhotoVideoCell> cache;
    private int cantDeleteMessagesCount;
    private ArrayList<SharedPhotoVideoCell> cellCache;
    private int changeColumnsTab;
    private boolean changeTypeAnimation;
    private ChannelRecommendationsAdapter channelRecommendationsAdapter;
    private ChatUsersAdapter chatUsersAdapter;
    private ImageView closeButton;
    private CommonGroupsAdapter commonGroupsAdapter;
    final Delegate delegate;
    private ActionBarMenuItem deleteItem;
    private long dialog_id;
    private boolean disableScrolling;
    private SharedDocumentsAdapter documentsAdapter;
    private MediaSearchAdapter documentsSearchAdapter;
    private AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private ActionBarMenuItem forwardItem;
    private FragmentContextView fragmentContextView;
    private HintView fwdRestrictedHint;
    private GifAdapter gifAdapter;
    FlickerLoadingView globalGradientView;
    private ActionBarMenuItem gotoItem;
    private GroupUsersSearchAdapter groupUsersSearchAdapter;
    private int[] hasMedia;
    private Runnable hideFloatingDateRunnable;
    private boolean ignoreSearchCollapse;
    private TLRPC$ChatFull info;
    private int initialTab;
    protected boolean isActionModeShowed;
    boolean isInPinchToZoomTouchMode;
    boolean isPinnedToTop;
    Runnable jumpToRunnable;
    int lastMeasuredTopPadding;
    private SharedLinksAdapter linksAdapter;
    private MediaSearchAdapter linksSearchAdapter;
    private int maximumVelocity;
    boolean maybePinchToZoomTouchMode;
    boolean maybePinchToZoomTouchMode2;
    private boolean maybeStartTracking;
    private int[] mediaColumnsCount;
    private MediaPage[] mediaPages;
    private ActionBarMenuSubItem mediaZoomInItem;
    private ActionBarMenuSubItem mediaZoomOutItem;
    private long mergeDialogId;
    SparseArray<Float> messageAlphaEnter;
    AnimationNotificationsLocker notificationsLocker;
    private float optionsAlpha;
    private RLottieImageView optionsSearchImageView;
    ActionBarPopupWindow optionsWindow;
    private SharedPhotoVideoAdapter photoVideoAdapter;
    private boolean photoVideoChangeColumnsAnimation;
    private float photoVideoChangeColumnsProgress;
    public ImageView photoVideoOptionsItem;
    private ActionBarMenuItem pinItem;
    int pinchCenterOffset;
    int pinchCenterPosition;
    int pinchCenterX;
    int pinchCenterY;
    float pinchScale;
    boolean pinchScaleUp;
    float pinchStartDistance;
    private Drawable pinnedHeaderShadowDrawable;
    private int pointerId1;
    private int pointerId2;
    private BaseFragment profileActivity;
    private PhotoViewer.PhotoViewerProvider provider;
    android.graphics.Rect rect;
    private Theme.ResourcesProvider resourcesProvider;
    private SavedDialogsAdapter savedDialogsAdapter;
    private ChatActivityContainer savedMessagesContainer;
    private SavedMessagesSearchAdapter savedMessagesSearchAdapter;
    public ScrollSlidingTextTabStripInner scrollSlidingTextTabStrip;
    private boolean scrolling;
    public boolean scrollingByUser;
    private float searchAlpha;
    private ActionBarMenuItem searchItem;
    public ActionBarMenuItem searchItemIcon;
    private int searchItemState;
    private StoriesController.StoriesList searchStoriesList;
    public SearchTagsList searchTagsList;
    private boolean searchWas;
    private boolean searching;
    private ReactionsLayoutInBubble.VisibleReaction searchingReaction;
    private SparseArray<MessageObject>[] selectedFiles;
    private NumberTextView selectedMessagesCountTextView;
    private View shadowLine;
    SharedLinkCell.SharedLinkCellDelegate sharedLinkCellDelegate;
    private SharedMediaData[] sharedMediaData;
    private SharedMediaPreloader sharedMediaPreloader;
    private float shiftDp;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private StoriesAdapter storiesAdapter;
    private boolean storiesColumnsCountSet;
    private ItemTouchHelper storiesReorder;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    int topPadding;
    private long topicId;
    private ActionBarMenuItem unpinItem;
    private TLRPC$UserFull userInfo;
    private VelocityTracker velocityTracker;
    private final int viewType;
    private SharedDocumentsAdapter voiceAdapter;
    private static final int[] supportedFastScrollTypes = {0, 1, 2, 4};
    private static final Interpolator interpolator = new Interpolator() {
        @Override
        public final float getInterpolation(float f) {
            float lambda$static$2;
            lambda$static$2 = SharedMediaLayout.lambda$static$2(f);
            return lambda$static$2;
        }
    };

    public interface Delegate {
        boolean canSearchMembers();

        TLRPC$Chat getCurrentChat();

        RecyclerListView getListView();

        boolean isFragmentOpened();

        boolean onMemberClick(TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z, boolean z2, View view);

        void scrollToSharedMedia();

        void updateSelectedMediaTabText();
    }

    public interface SharedMediaPreloaderDelegate {
        void mediaCountUpdated();
    }

    public static boolean lambda$new$11(View view, MotionEvent motionEvent) {
        return true;
    }

    public static float lambda$static$2(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    public void showFloatingDateView() {
    }

    @Override
    public boolean canClickButtonInside() {
        return false;
    }

    protected boolean canShowSearchItem() {
        return true;
    }

    protected boolean customTabs() {
        return false;
    }

    protected int getInitialTab() {
        return 0;
    }

    public TL_stories$MediaArea getStoriesArea() {
        return null;
    }

    public String getStoriesHashtag() {
        return null;
    }

    protected boolean includeSavedDialogs() {
        return false;
    }

    protected boolean includeStories() {
        return true;
    }

    protected void invalidateBlur() {
    }

    protected boolean isArchivedOnlyStoriesView() {
        return false;
    }

    protected boolean isSelf() {
        return false;
    }

    protected boolean isStoriesView() {
        return false;
    }

    public boolean isTabZoomable(int i) {
        return i == 0 || i == 8 || i == 9;
    }

    public void onActionModeSelectedUpdate(SparseArray<MessageObject> sparseArray) {
    }

    @Override
    public void onButtonClicked(DialogCell dialogCell) {
    }

    @Override
    public void onButtonLongPress(DialogCell dialogCell) {
    }

    protected boolean onMemberClick(TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z, View view) {
        return false;
    }

    protected void onSearchStateChanged(boolean z) {
    }

    public void onTabProgress(float f) {
    }

    protected void onTabScroll(boolean z) {
    }

    @Override
    public void openHiddenStories() {
    }

    public int overrideColumnsCount() {
        return -1;
    }

    protected int processColor(int i) {
        return i;
    }

    @Override
    public void showChatPreview(DialogCell dialogCell) {
    }

    static int access$5008(SharedMediaLayout sharedMediaLayout) {
        int i = sharedMediaLayout.cantDeleteMessagesCount;
        sharedMediaLayout.cantDeleteMessagesCount = i + 1;
        return i;
    }

    static int access$5010(SharedMediaLayout sharedMediaLayout) {
        int i = sharedMediaLayout.cantDeleteMessagesCount;
        sharedMediaLayout.cantDeleteMessagesCount = i - 1;
        return i;
    }

    public boolean isInFastScroll() {
        MediaPage mediaPage = this.mediaPages[0];
        return (mediaPage == null || mediaPage.listView.getFastScroll() == null || !this.mediaPages[0].listView.getFastScroll().isPressed()) ? false : true;
    }

    public boolean dispatchFastScrollEvent(MotionEvent motionEvent) {
        View view = (View) getParent();
        motionEvent.offsetLocation(((-view.getX()) - getX()) - this.mediaPages[0].listView.getFastScroll().getX(), (((-view.getY()) - getY()) - this.mediaPages[0].getY()) - this.mediaPages[0].listView.getFastScroll().getY());
        return this.mediaPages[0].listView.getFastScroll().dispatchTouchEvent(motionEvent);
    }

    public boolean checkPinchToZoom(MotionEvent motionEvent) {
        RecyclerView.Adapter adapter;
        BotPreviewsEditContainer botPreviewsEditContainer;
        int i = this.mediaPages[0].selectedType;
        if (i == 13 && (botPreviewsEditContainer = this.botPreviewsContainer) != null) {
            return botPreviewsEditContainer.checkPinchToZoom(motionEvent);
        }
        if ((i != 0 && i != 8 && i != 9) || getParent() == null) {
            return false;
        }
        if (this.photoVideoChangeColumnsAnimation && !this.isInPinchToZoomTouchMode) {
            return true;
        }
        if (motionEvent.getActionMasked() == 0 || motionEvent.getActionMasked() == 5) {
            if (this.maybePinchToZoomTouchMode && !this.isInPinchToZoomTouchMode && motionEvent.getPointerCount() == 2) {
                this.pinchStartDistance = (float) Math.hypot(motionEvent.getX(1) - motionEvent.getX(0), motionEvent.getY(1) - motionEvent.getY(0));
                this.pinchScale = 1.0f;
                this.pointerId1 = motionEvent.getPointerId(0);
                this.pointerId2 = motionEvent.getPointerId(1);
                this.mediaPages[0].listView.cancelClickRunnables(false);
                this.mediaPages[0].listView.cancelLongPress();
                this.mediaPages[0].listView.dispatchTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
                View view = (View) getParent();
                this.pinchCenterX = (int) (((((int) ((motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f)) - view.getX()) - getX()) - this.mediaPages[0].getX());
                int y = (int) (((((int) ((motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f)) - view.getY()) - getY()) - this.mediaPages[0].getY());
                this.pinchCenterY = y;
                selectPinchPosition(this.pinchCenterX, y);
                this.maybePinchToZoomTouchMode2 = true;
            }
            if (motionEvent.getActionMasked() == 0) {
                if (((motionEvent.getY() - ((View) getParent()).getY()) - getY()) - this.mediaPages[0].getY() > 0.0f) {
                    this.maybePinchToZoomTouchMode = true;
                }
            }
        } else if (motionEvent.getActionMasked() == 2 && (this.isInPinchToZoomTouchMode || this.maybePinchToZoomTouchMode2)) {
            int i2 = -1;
            int i3 = -1;
            for (int i4 = 0; i4 < motionEvent.getPointerCount(); i4++) {
                if (this.pointerId1 == motionEvent.getPointerId(i4)) {
                    i2 = i4;
                }
                if (this.pointerId2 == motionEvent.getPointerId(i4)) {
                    i3 = i4;
                }
            }
            if (i2 == -1 || i3 == -1) {
                this.maybePinchToZoomTouchMode = false;
                this.maybePinchToZoomTouchMode2 = false;
                this.isInPinchToZoomTouchMode = false;
                finishPinchToMediaColumnsCount();
                return false;
            }
            float hypot = ((float) Math.hypot(motionEvent.getX(i3) - motionEvent.getX(i2), motionEvent.getY(i3) - motionEvent.getY(i2))) / this.pinchStartDistance;
            this.pinchScale = hypot;
            if (!this.isInPinchToZoomTouchMode && (hypot > 1.01f || hypot < 0.99f)) {
                this.isInPinchToZoomTouchMode = true;
                boolean z = hypot > 1.0f;
                this.pinchScaleUp = z;
                startPinchToMediaColumnsCount(z);
            }
            if (this.isInPinchToZoomTouchMode) {
                boolean z2 = this.pinchScaleUp;
                if ((!z2 || this.pinchScale >= 1.0f) && (z2 || this.pinchScale <= 1.0f)) {
                    this.photoVideoChangeColumnsProgress = Math.max(0.0f, Math.min(1.0f, z2 ? 1.0f - ((2.0f - this.pinchScale) / 1.0f) : (1.0f - this.pinchScale) / 0.5f));
                } else {
                    this.photoVideoChangeColumnsProgress = 0.0f;
                }
                float f = this.photoVideoChangeColumnsProgress;
                if (f == 1.0f || f == 0.0f) {
                    int i5 = this.changeColumnsTab;
                    if (i5 == 8) {
                        adapter = this.storiesAdapter;
                    } else if (i5 == 9) {
                        adapter = this.archivedStoriesAdapter;
                    } else {
                        adapter = this.photoVideoAdapter;
                    }
                    if (f == 1.0f) {
                        int i6 = this.animateToColumnsCount;
                        int ceil = (((int) Math.ceil(this.pinchCenterPosition / this.animateToColumnsCount)) * i6) + ((int) ((this.startedTrackingX / (this.mediaPages[0].listView.getMeasuredWidth() - ((int) (this.mediaPages[0].listView.getMeasuredWidth() / this.animateToColumnsCount)))) * (i6 - 1)));
                        if (ceil >= adapter.getItemCount()) {
                            ceil = adapter.getItemCount() - 1;
                        }
                        this.pinchCenterPosition = ceil;
                    }
                    finishPinchToMediaColumnsCount();
                    if (this.photoVideoChangeColumnsProgress == 0.0f) {
                        this.pinchScaleUp = !this.pinchScaleUp;
                    }
                    startPinchToMediaColumnsCount(this.pinchScaleUp);
                    this.pinchStartDistance = (float) Math.hypot(motionEvent.getX(1) - motionEvent.getX(0), motionEvent.getY(1) - motionEvent.getY(0));
                }
                this.mediaPages[0].listView.invalidate();
                MediaPage mediaPage = this.mediaPages[0];
                if (mediaPage.fastScrollHintView != null) {
                    mediaPage.invalidate();
                }
            }
        } else if ((motionEvent.getActionMasked() == 1 || ((motionEvent.getActionMasked() == 6 && checkPointerIds(motionEvent)) || motionEvent.getActionMasked() == 3)) && this.isInPinchToZoomTouchMode) {
            this.maybePinchToZoomTouchMode2 = false;
            this.maybePinchToZoomTouchMode = false;
            this.isInPinchToZoomTouchMode = false;
            finishPinchToMediaColumnsCount();
        }
        return this.isInPinchToZoomTouchMode;
    }

    private void selectPinchPosition(int i, int i2) {
        this.pinchCenterPosition = -1;
        int i3 = i2 + this.mediaPages[0].listView.blurTopPadding;
        if (getY() != 0.0f && this.viewType == 1) {
            i3 = 0;
        }
        for (int i4 = 0; i4 < this.mediaPages[0].listView.getChildCount(); i4++) {
            View childAt = this.mediaPages[0].listView.getChildAt(i4);
            childAt.getHitRect(this.rect);
            if (this.rect.contains(i, i3)) {
                this.pinchCenterPosition = this.mediaPages[0].listView.getChildLayoutPosition(childAt);
                this.pinchCenterOffset = childAt.getTop();
            }
        }
        if (this.delegate.canSearchMembers() && this.pinchCenterPosition == -1) {
            this.pinchCenterPosition = (int) (this.mediaPages[0].layoutManager.findFirstVisibleItemPosition() + ((this.mediaColumnsCount[(this.mediaPages[0].selectedType == 8 || this.mediaPages[0].selectedType == 9) ? (char) 1 : (char) 0] - 1) * Math.min(1.0f, Math.max(i / this.mediaPages[0].listView.getMeasuredWidth(), 0.0f))));
            this.pinchCenterOffset = 0;
        }
    }

    private boolean checkPointerIds(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == motionEvent.getPointerId(0) && this.pointerId2 == motionEvent.getPointerId(1)) {
            return true;
        }
        return this.pointerId1 == motionEvent.getPointerId(1) && this.pointerId2 == motionEvent.getPointerId(0);
    }

    public boolean isSwipeBackEnabled() {
        return ((canEditStories() && ((getClosestTab() == 8 || getClosestTab() == 13) && isActionModeShown())) || this.photoVideoChangeColumnsAnimation || this.tabsAnimationInProgress) ? false : true;
    }

    public int getPhotosVideosTypeFilter() {
        return this.sharedMediaData[0].filterType;
    }

    public boolean isPinnedToTop() {
        return this.isPinnedToTop;
    }

    public void setPinnedToTop(boolean z) {
        if (this.isPinnedToTop == z) {
            return;
        }
        this.isPinnedToTop = z;
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return;
            }
            updateFastScrollVisibility(mediaPageArr[i], true);
            i++;
        }
    }

    public void drawListForBlur(Canvas canvas, ArrayList<SizeNotifierFrameLayout.IViewWithInvalidateCallback> arrayList) {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return;
            }
            MediaPage mediaPage = mediaPageArr[i];
            if (mediaPage != null && mediaPage.getVisibility() == 0) {
                for (int i2 = 0; i2 < this.mediaPages[i].listView.getChildCount(); i2++) {
                    View childAt = this.mediaPages[i].listView.getChildAt(i2);
                    if (childAt.getY() < this.mediaPages[i].listView.blurTopPadding + AndroidUtilities.dp(100.0f)) {
                        int save = canvas.save();
                        canvas.translate(this.mediaPages[i].getX() + childAt.getX(), getY() + this.mediaPages[i].getY() + this.mediaPages[i].listView.getY() + childAt.getY());
                        childAt.draw(canvas);
                        if (arrayList != null && (childAt instanceof SizeNotifierFrameLayout.IViewWithInvalidateCallback)) {
                            arrayList.add((SizeNotifierFrameLayout.IViewWithInvalidateCallback) childAt);
                        }
                        canvas.restoreToCount(save);
                    }
                }
            }
            i++;
        }
    }

    @Override
    public void openStory(DialogCell dialogCell, Runnable runnable) {
        BaseFragment baseFragment = this.profileActivity;
        if (baseFragment != null && baseFragment.getMessagesController().getStoriesController().hasStories(dialogCell.getDialogId())) {
            this.profileActivity.getOrCreateStoryViewer().doOnAnimationReady(runnable);
            StoryViewer orCreateStoryViewer = this.profileActivity.getOrCreateStoryViewer();
            Context context = this.profileActivity.getContext();
            long dialogId = dialogCell.getDialogId();
            StoriesListPlaceProvider of = StoriesListPlaceProvider.of((RecyclerListView) dialogCell.getParent());
            BaseFragment baseFragment2 = this.profileActivity;
            orCreateStoryViewer.open(context, dialogId, of.addBottomClip(((baseFragment2 instanceof ProfileActivity) && ((ProfileActivity) baseFragment2).myProfile) ? AndroidUtilities.dp(68.0f) : 0));
        }
    }

    public static class MediaPage extends FrameLayout {
        private ClippingImageView animatingImageView;
        private GridLayoutManager animationSupportingLayoutManager;
        private InternalListView animationSupportingListView;
        private ButtonWithCounterView buttonView;
        private StickerEmptyView emptyView;
        public ObjectAnimator fastScrollAnimator;
        public boolean fastScrollEnabled;
        public Runnable fastScrollHideHintRunnable;
        public boolean fastScrollHinWasShown;
        public SharedMediaFastScrollTooltip fastScrollHintView;
        public boolean highlightAnimation;
        public int highlightMessageId;
        public float highlightProgress;
        private DefaultItemAnimator itemAnimator;
        public long lastCheckScrollTime;
        private ExtendedGridLayoutManager layoutManager;
        private InternalListView listView;
        private FlickerLoadingView progressView;
        private RecyclerAnimationScrollHelper scrollHelper;
        private RecyclerView.RecycledViewPool searchViewPool;
        private int selectedType;
        private RecyclerView.RecycledViewPool viewPool;

        public MediaPage(Context context) {
            super(context);
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (view == this.animationSupportingListView) {
                return true;
            }
            return super.drawChild(canvas, view, j);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip = this.fastScrollHintView;
            if (sharedMediaFastScrollTooltip == null || sharedMediaFastScrollTooltip.getVisibility() != 0) {
                return;
            }
            RecyclerListView.FastScroll fastScroll = this.listView.getFastScroll();
            if (fastScroll != null) {
                float scrollBarY = fastScroll.getScrollBarY() + AndroidUtilities.dp(36.0f);
                if (this.selectedType == 9) {
                    scrollBarY += AndroidUtilities.dp(64.0f);
                }
                float measuredWidth = (getMeasuredWidth() - this.fastScrollHintView.getMeasuredWidth()) - AndroidUtilities.dp(16.0f);
                this.fastScrollHintView.setPivotX(r2.getMeasuredWidth());
                this.fastScrollHintView.setPivotY(0.0f);
                this.fastScrollHintView.setTranslationX(measuredWidth);
                this.fastScrollHintView.setTranslationY(scrollBarY);
            }
            if (fastScroll.getProgress() > 0.85f) {
                SharedMediaLayout.showFastScrollHint(this, null, false);
            }
        }
    }

    public float getPhotoVideoOptionsAlpha(float f) {
        float f2 = 0.0f;
        if (isArchivedOnlyStoriesView()) {
            return 0.0f;
        }
        MediaPage mediaPage = this.mediaPages[1];
        if (mediaPage != null && (mediaPage.selectedType == 0 || ((this.mediaPages[1].selectedType == 8 && TextUtils.isEmpty(getStoriesHashtag())) || this.mediaPages[1].selectedType == 9 || this.mediaPages[1].selectedType == 11 || this.mediaPages[1].selectedType == 13))) {
            f2 = 0.0f + f;
        }
        MediaPage mediaPage2 = this.mediaPages[0];
        return mediaPage2 != null ? (mediaPage2.selectedType == 0 || (this.mediaPages[0].selectedType == 8 && TextUtils.isEmpty(getStoriesHashtag())) || this.mediaPages[0].selectedType == 9 || this.mediaPages[0].selectedType == 11 || this.mediaPages[0].selectedType == 13) ? f2 + (1.0f - f) : f2 : f2;
    }

    public float getSearchAlpha(float f) {
        float f2 = 0.0f;
        if (isArchivedOnlyStoriesView()) {
            return 0.0f;
        }
        MediaPage mediaPage = this.mediaPages[1];
        if (mediaPage != null && isSearchItemVisible(mediaPage.selectedType) && this.mediaPages[1].selectedType != 11) {
            f2 = 0.0f + f;
        }
        MediaPage mediaPage2 = this.mediaPages[0];
        return (mediaPage2 == null || !isSearchItemVisible(mediaPage2.selectedType) || this.mediaPages[0].selectedType == 11) ? f2 : f2 + (1.0f - f);
    }

    public void updateSearchItemIcon(float f) {
        if (this.searchItemIcon == null) {
            return;
        }
        MediaPage mediaPage = this.mediaPages[1];
        float f2 = 0.0f;
        if (mediaPage != null && mediaPage.selectedType == 11) {
            f2 = 0.0f + f;
        }
        MediaPage mediaPage2 = this.mediaPages[0];
        if (mediaPage2 != null && mediaPage2.selectedType == 11) {
            f2 += 1.0f - f;
        }
        this.searchItemIcon.setAlpha(f2);
        float f3 = (0.15f * f2) + 0.85f;
        this.searchItemIcon.setScaleX(f3);
        this.searchItemIcon.setScaleY(f3);
        this.searchItemIcon.setVisibility(f2 <= 0.01f ? 8 : 0);
    }

    public void updateSearchItemIconAnimated() {
        if (this.searchItemIcon == null) {
            return;
        }
        MediaPage mediaPage = this.mediaPages[1];
        final boolean z = mediaPage != null && mediaPage.selectedType == 11;
        if (z) {
            this.searchItemIcon.setVisibility(0);
        }
        this.searchItemIcon.animate().alpha(z ? 1.0f : 0.0f).scaleX(z ? 1.0f : 0.85f).scaleY(z ? 1.0f : 0.85f).withEndAction(new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.this.lambda$updateSearchItemIconAnimated$0(z);
            }
        }).setDuration(420L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
    }

    public void lambda$updateSearchItemIconAnimated$0(boolean z) {
        if (z) {
            return;
        }
        this.searchItemIcon.setVisibility(8);
    }

    public void updateFastScrollVisibility(MediaPage mediaPage, boolean z) {
        boolean z2 = mediaPage.fastScrollEnabled && this.isPinnedToTop;
        RecyclerListView.FastScroll fastScroll = mediaPage.listView.getFastScroll();
        ObjectAnimator objectAnimator = mediaPage.fastScrollAnimator;
        if (objectAnimator != null) {
            objectAnimator.removeAllListeners();
            mediaPage.fastScrollAnimator.cancel();
        }
        if (!z) {
            fastScroll.animate().setListener(null).cancel();
            fastScroll.setVisibility(z2 ? 0 : 8);
            fastScroll.setTag(z2 ? 1 : null);
            fastScroll.setAlpha(1.0f);
            fastScroll.setScaleX(1.0f);
            fastScroll.setScaleY(1.0f);
            return;
        }
        if (z2 && fastScroll.getTag() == null) {
            fastScroll.animate().setListener(null).cancel();
            if (fastScroll.getVisibility() != 0) {
                fastScroll.setVisibility(0);
                fastScroll.setAlpha(0.0f);
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(fastScroll, (Property<RecyclerListView.FastScroll, Float>) View.ALPHA, fastScroll.getAlpha(), 1.0f);
            mediaPage.fastScrollAnimator = ofFloat;
            ofFloat.setDuration(150L).start();
            fastScroll.setTag(r4);
            return;
        }
        if (z2 || fastScroll.getTag() == null) {
            return;
        }
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(fastScroll, (Property<RecyclerListView.FastScroll, Float>) View.ALPHA, fastScroll.getAlpha(), 0.0f);
        ofFloat2.addListener(new HideViewAfterAnimation(fastScroll));
        mediaPage.fastScrollAnimator = ofFloat2;
        ofFloat2.setDuration(150L).start();
        fastScroll.animate().setListener(null).cancel();
        fastScroll.setTag(null);
    }

    public void lambda$new$1() {
        hideFloatingDateView(true);
    }

    public static class SharedMediaPreloader implements NotificationCenter.NotificationCenterDelegate {
        private boolean checkedHasSavedMessages;
        private long dialogId;
        public boolean hasPreviews;
        public boolean hasSavedMessages;
        private boolean mediaWasLoaded;
        private long mergeDialogId;
        private BaseFragment parentFragment;
        private SharedMediaData[] sharedMediaData;
        private long topicId;
        private int[] mediaCount = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private int[] mediaMergeCount = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private int[] lastMediaCount = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private int[] lastLoadMediaCount = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private ArrayList<SharedMediaPreloaderDelegate> delegates = new ArrayList<>();

        public boolean hasSharedMedia() {
            int[] lastMediaCount = getLastMediaCount();
            if (lastMediaCount == null) {
                return false;
            }
            for (int i : lastMediaCount) {
                if (i > 0) {
                    return true;
                }
            }
            if (this.hasSavedMessages) {
                return true;
            }
            BaseFragment baseFragment = this.parentFragment;
            return baseFragment != null && this.dialogId == baseFragment.getUserConfig().getClientUserId() && this.topicId == 0 && this.parentFragment.getMessagesController().getSavedMessagesController().hasDialogs();
        }

        public SharedMediaPreloader(BaseFragment baseFragment) {
            this.parentFragment = baseFragment;
            if (baseFragment instanceof ChatActivityInterface) {
                ChatActivityInterface chatActivityInterface = (ChatActivityInterface) baseFragment;
                this.dialogId = chatActivityInterface.getDialogId();
                this.mergeDialogId = chatActivityInterface.getMergeDialogId();
                this.topicId = chatActivityInterface.getTopicId();
                if (this.dialogId != baseFragment.getUserConfig().getClientUserId()) {
                    baseFragment.getMessagesController().getSavedMessagesController().hasSavedMessages(this.dialogId, new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            SharedMediaLayout.SharedMediaPreloader.this.lambda$new$0((Boolean) obj);
                        }
                    });
                }
            } else if (baseFragment instanceof ProfileActivity) {
                ProfileActivity profileActivity = (ProfileActivity) baseFragment;
                if (profileActivity.saved) {
                    this.dialogId = profileActivity.getUserConfig().getClientUserId();
                    this.topicId = profileActivity.getDialogId();
                } else {
                    this.dialogId = profileActivity.getDialogId();
                    this.topicId = profileActivity.getTopicId();
                    if (this.dialogId != baseFragment.getUserConfig().getClientUserId()) {
                        baseFragment.getMessagesController().getSavedMessagesController().hasSavedMessages(this.dialogId, new Utilities.Callback() {
                            @Override
                            public final void run(Object obj) {
                                SharedMediaLayout.SharedMediaPreloader.this.lambda$new$1((Boolean) obj);
                            }
                        });
                    }
                }
            } else if (baseFragment instanceof MediaActivity) {
                this.dialogId = ((MediaActivity) baseFragment).getDialogId();
            } else if (baseFragment instanceof DialogsActivity) {
                this.dialogId = baseFragment.getUserConfig().getClientUserId();
            }
            this.sharedMediaData = new SharedMediaData[6];
            int i = 0;
            while (true) {
                SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                if (i < sharedMediaDataArr.length) {
                    sharedMediaDataArr[i] = new SharedMediaData();
                    this.sharedMediaData[i].setMaxId(0, DialogObject.isEncryptedDialog(this.dialogId) ? Integer.MIN_VALUE : Integer.MAX_VALUE);
                    i++;
                } else {
                    loadMediaCounts();
                    NotificationCenter notificationCenter = this.parentFragment.getNotificationCenter();
                    notificationCenter.addObserver(this, NotificationCenter.mediaCountsDidLoad);
                    notificationCenter.addObserver(this, NotificationCenter.mediaCountDidLoad);
                    notificationCenter.addObserver(this, NotificationCenter.didReceiveNewMessages);
                    notificationCenter.addObserver(this, NotificationCenter.messageReceivedByServer);
                    notificationCenter.addObserver(this, NotificationCenter.mediaDidLoad);
                    notificationCenter.addObserver(this, NotificationCenter.messagesDeleted);
                    notificationCenter.addObserver(this, NotificationCenter.replaceMessagesObjects);
                    notificationCenter.addObserver(this, NotificationCenter.chatInfoDidLoad);
                    notificationCenter.addObserver(this, NotificationCenter.fileLoaded);
                    notificationCenter.addObserver(this, NotificationCenter.storiesListUpdated);
                    notificationCenter.addObserver(this, NotificationCenter.savedMessagesDialogsUpdate);
                    return;
                }
            }
        }

        public void lambda$new$0(Boolean bool) {
            boolean booleanValue = bool.booleanValue();
            this.hasSavedMessages = booleanValue;
            this.checkedHasSavedMessages = true;
            if (booleanValue) {
                int size = this.delegates.size();
                for (int i = 0; i < size; i++) {
                    this.delegates.get(i).mediaCountUpdated();
                }
            }
        }

        public void lambda$new$1(Boolean bool) {
            boolean booleanValue = bool.booleanValue();
            this.hasSavedMessages = booleanValue;
            this.checkedHasSavedMessages = true;
            if (booleanValue) {
                int size = this.delegates.size();
                for (int i = 0; i < size; i++) {
                    this.delegates.get(i).mediaCountUpdated();
                }
            }
        }

        public void addDelegate(SharedMediaPreloaderDelegate sharedMediaPreloaderDelegate) {
            this.delegates.add(sharedMediaPreloaderDelegate);
        }

        public void removeDelegate(SharedMediaPreloaderDelegate sharedMediaPreloaderDelegate) {
            this.delegates.remove(sharedMediaPreloaderDelegate);
        }

        public void onDestroy(BaseFragment baseFragment) {
            if (baseFragment != this.parentFragment) {
                return;
            }
            this.delegates.clear();
            NotificationCenter notificationCenter = this.parentFragment.getNotificationCenter();
            notificationCenter.removeObserver(this, NotificationCenter.mediaCountsDidLoad);
            notificationCenter.removeObserver(this, NotificationCenter.mediaCountDidLoad);
            notificationCenter.removeObserver(this, NotificationCenter.didReceiveNewMessages);
            notificationCenter.removeObserver(this, NotificationCenter.messageReceivedByServer);
            notificationCenter.removeObserver(this, NotificationCenter.mediaDidLoad);
            notificationCenter.removeObserver(this, NotificationCenter.messagesDeleted);
            notificationCenter.removeObserver(this, NotificationCenter.replaceMessagesObjects);
            notificationCenter.removeObserver(this, NotificationCenter.chatInfoDidLoad);
            notificationCenter.removeObserver(this, NotificationCenter.fileLoaded);
            notificationCenter.removeObserver(this, NotificationCenter.storiesListUpdated);
            notificationCenter.removeObserver(this, NotificationCenter.savedMessagesDialogsUpdate);
        }

        public int[] getLastMediaCount() {
            return this.lastMediaCount;
        }

        public SharedMediaData[] getSharedMediaData() {
            return this.sharedMediaData;
        }

        @Override
        public void didReceivedNotification(int r28, final int r29, java.lang.Object... r30) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloader.didReceivedNotification(int, int, java.lang.Object[]):void");
        }

        private void loadMediaCounts() {
            this.parentFragment.getMediaDataController().getMediaCounts(this.dialogId, this.topicId, this.parentFragment.getClassGuid());
            if (this.mergeDialogId != 0) {
                this.parentFragment.getMediaDataController().getMediaCounts(this.mergeDialogId, this.topicId, this.parentFragment.getClassGuid());
            }
        }

        private void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
            if (tLRPC$ChatFull != null) {
                long j = tLRPC$ChatFull.migrated_from_chat_id;
                if (j == 0 || this.mergeDialogId != 0) {
                    return;
                }
                this.mergeDialogId = -j;
                this.parentFragment.getMediaDataController().getMediaCounts(this.mergeDialogId, this.topicId, this.parentFragment.getClassGuid());
            }
        }

        public boolean isMediaWasLoaded() {
            return this.mediaWasLoaded;
        }
    }

    public static class SharedMediaData {
        private int endLoadingStubs;
        public boolean fastScrollDataLoaded;
        public int frozenEndLoadingStubs;
        public int frozenStartOffset;
        private boolean hasPhotos;
        private boolean hasVideos;
        public boolean isFrozen;
        public boolean loading;
        public boolean loadingAfterFastScroll;
        public int min_id;
        public int requestIndex;
        private int startOffset;
        public int totalCount;
        public ArrayList<MessageObject> messages = new ArrayList<>();
        public SparseArray<MessageObject>[] messagesDict = {new SparseArray<>(), new SparseArray<>()};
        public ArrayList<String> sections = new ArrayList<>();
        public HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap<>();
        public ArrayList<Period> fastScrollPeriods = new ArrayList<>();
        public boolean[] endReached = {false, true};
        public int[] max_id = {0, 0};
        public boolean startReached = true;
        public int filterType = 0;
        public ArrayList<MessageObject> frozenMessages = new ArrayList<>();
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();

        static int access$10310(SharedMediaData sharedMediaData) {
            int i = sharedMediaData.endLoadingStubs;
            sharedMediaData.endLoadingStubs = i - 1;
            return i;
        }

        static int access$710(SharedMediaData sharedMediaData) {
            int i = sharedMediaData.startOffset;
            sharedMediaData.startOffset = i - 1;
            return i;
        }

        public void setTotalCount(int i) {
            this.totalCount = i;
        }

        public void setMaxId(int i, int i2) {
            this.max_id[i] = i2;
        }

        public void setEndReached(int i, boolean z) {
            this.endReached[i] = z;
        }

        public boolean addMessage(MessageObject messageObject, int i, boolean z, boolean z2) {
            if (this.messagesDict[i].indexOfKey(messageObject.getId()) >= 0) {
                return false;
            }
            ArrayList<MessageObject> arrayList = this.sectionArrays.get(messageObject.monthKey);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.sectionArrays.put(messageObject.monthKey, arrayList);
                if (z) {
                    this.sections.add(0, messageObject.monthKey);
                } else {
                    this.sections.add(messageObject.monthKey);
                }
            }
            if (z) {
                arrayList.add(0, messageObject);
                this.messages.add(0, messageObject);
            } else {
                arrayList.add(messageObject);
                this.messages.add(messageObject);
            }
            this.messagesDict[i].put(messageObject.getId(), messageObject);
            if (!z2) {
                if (messageObject.getId() > 0) {
                    this.max_id[i] = Math.min(messageObject.getId(), this.max_id[i]);
                    this.min_id = Math.max(messageObject.getId(), this.min_id);
                }
            } else {
                this.max_id[i] = Math.max(messageObject.getId(), this.max_id[i]);
                this.min_id = Math.min(messageObject.getId(), this.min_id);
            }
            if (!this.hasVideos && messageObject.isVideo()) {
                this.hasVideos = true;
            }
            if (!this.hasPhotos && messageObject.isPhoto()) {
                this.hasPhotos = true;
            }
            return true;
        }

        public MessageObject deleteMessage(int i, int i2) {
            ArrayList<MessageObject> arrayList;
            MessageObject messageObject = this.messagesDict[i2].get(i);
            if (messageObject == null || (arrayList = this.sectionArrays.get(messageObject.monthKey)) == null) {
                return null;
            }
            arrayList.remove(messageObject);
            this.messages.remove(messageObject);
            this.messagesDict[i2].remove(messageObject.getId());
            if (arrayList.isEmpty()) {
                this.sectionArrays.remove(messageObject.monthKey);
                this.sections.remove(messageObject.monthKey);
            }
            int i3 = this.totalCount - 1;
            this.totalCount = i3;
            if (i3 < 0) {
                this.totalCount = 0;
            }
            return messageObject;
        }

        public void replaceMid(int i, int i2) {
            MessageObject messageObject = this.messagesDict[0].get(i);
            if (messageObject != null) {
                this.messagesDict[0].remove(i);
                this.messagesDict[0].put(i2, messageObject);
                messageObject.messageOwner.id = i2;
                int[] iArr = this.max_id;
                iArr[0] = Math.min(i2, iArr[0]);
            }
        }

        public ArrayList<MessageObject> getMessages() {
            return this.isFrozen ? this.frozenMessages : this.messages;
        }

        public int getStartOffset() {
            return this.isFrozen ? this.frozenStartOffset : this.startOffset;
        }

        public void setListFrozen(boolean z) {
            if (this.isFrozen == z) {
                return;
            }
            this.isFrozen = z;
            if (z) {
                this.frozenStartOffset = this.startOffset;
                this.frozenEndLoadingStubs = this.endLoadingStubs;
                this.frozenMessages.clear();
                this.frozenMessages.addAll(this.messages);
            }
        }

        public int getEndLoadingStubs() {
            return this.isFrozen ? this.frozenEndLoadingStubs : this.endLoadingStubs;
        }
    }

    public static class Period {
        int date;
        public String formatedDate;
        int maxId;
        public int startOffset;

        public Period(TLRPC$TL_searchResultPosition tLRPC$TL_searchResultPosition) {
            int i = tLRPC$TL_searchResultPosition.date;
            this.date = i;
            this.maxId = tLRPC$TL_searchResultPosition.msg_id;
            this.startOffset = tLRPC$TL_searchResultPosition.offset;
            this.formatedDate = LocaleController.formatYearMont(i, true);
        }
    }

    public boolean hasInternet() {
        return this.profileActivity.getConnectionsManager().getConnectionState() == 3;
    }

    public SharedMediaLayout(Context context, long j, SharedMediaPreloader sharedMediaPreloader, int i, ArrayList<Integer> arrayList, TLRPC$ChatFull tLRPC$ChatFull, TLRPC$UserFull tLRPC$UserFull, int i2, BaseFragment baseFragment, Delegate delegate, int i3, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        TL_bots$BotInfo tL_bots$BotInfo;
        int i4;
        float f;
        int i5;
        MediaPage mediaPage;
        RecyclerListView.Holder holder;
        TLRPC$ChatFull tLRPC$ChatFull2;
        TLRPC$ChatFull tLRPC$ChatFull3 = tLRPC$ChatFull;
        this.rect = new android.graphics.Rect();
        this.mediaPages = new MediaPage[2];
        this.cellCache = new ArrayList<>(10);
        this.cache = new ArrayList<>(10);
        this.audioCellCache = new ArrayList<>(10);
        this.audioCache = new ArrayList<>(10);
        this.hideFloatingDateRunnable = new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.this.lambda$new$1();
            }
        };
        this.actionModeViews = new ArrayList<>();
        this.backgroundPaint = new Paint();
        boolean z = false;
        this.selectedFiles = new SparseArray[]{new SparseArray<>(), new SparseArray<>()};
        this.allowStoriesSingleColumn = false;
        this.storiesColumnsCountSet = false;
        this.mediaColumnsCount = new int[]{3, 3};
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() {
            @Override
            public org.telegram.ui.PhotoViewer.PlaceProviderObject getPlaceForPhoto(org.telegram.messenger.MessageObject r17, org.telegram.tgnet.TLRPC$FileLocation r18, int r19, boolean r20) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.AnonymousClass1.getPlaceForPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, int, boolean):org.telegram.ui.PhotoViewer$PlaceProviderObject");
            }
        };
        this.shiftDp = -5.0f;
        this.sharedMediaData = new SharedMediaData[6];
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.messageAlphaEnter = new SparseArray<>();
        this.sharedLinkCellDelegate = new AnonymousClass43();
        this.viewType = i3;
        this.resourcesProvider = resourcesProvider;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.globalGradientView = flickerLoadingView;
        flickerLoadingView.setIsSingleCell(true);
        TLRPC$User user = baseFragment.getMessagesController().getUser(Long.valueOf(j));
        this.sharedMediaPreloader = sharedMediaPreloader;
        this.delegate = delegate;
        int[] lastMediaCount = sharedMediaPreloader.getLastMediaCount();
        long j2 = this.sharedMediaPreloader.topicId;
        this.topicId = j2;
        this.hasMedia = new int[]{lastMediaCount[0], lastMediaCount[1], lastMediaCount[2], lastMediaCount[3], lastMediaCount[4], lastMediaCount[5], j2 == 0 ? i : 0};
        if (i2 == 10) {
            this.initialTab = i2;
        } else if (i2 == 11) {
            this.initialTab = i2;
        } else if (user != null && user.bot && user.bot_has_main_app && user.bot_can_edit) {
            this.initialTab = 13;
        } else if (tLRPC$UserFull != null && (tL_bots$BotInfo = tLRPC$UserFull.bot_info) != null && tL_bots$BotInfo.has_preview_medias) {
            this.initialTab = 8;
        } else if ((tLRPC$UserFull != null && tLRPC$UserFull.stories_pinned_available) || ((tLRPC$ChatFull3 != null && tLRPC$ChatFull3.stories_pinned_available) || isStoriesView())) {
            this.initialTab = getInitialTab();
        } else if (i2 != -1 && this.topicId == 0) {
            this.initialTab = i2;
        } else {
            int i6 = 0;
            while (true) {
                int[] iArr = this.hasMedia;
                if (i6 >= iArr.length) {
                    break;
                }
                int i7 = iArr[i6];
                if (i7 == -1 || i7 > 0) {
                    break;
                } else {
                    i6++;
                }
            }
            this.initialTab = i6;
        }
        this.info = tLRPC$ChatFull3;
        this.userInfo = tLRPC$UserFull;
        if (tLRPC$ChatFull3 != null) {
            this.mergeDialogId = -tLRPC$ChatFull3.migrated_from_chat_id;
        }
        this.dialog_id = j;
        int i8 = 0;
        while (true) {
            SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            if (i8 >= sharedMediaDataArr.length) {
                break;
            }
            sharedMediaDataArr[i8] = new SharedMediaData();
            this.sharedMediaData[i8].max_id[0] = DialogObject.isEncryptedDialog(this.dialog_id) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            fillMediaData(i8);
            if (this.mergeDialogId != 0 && (tLRPC$ChatFull2 = this.info) != null) {
                SharedMediaData sharedMediaData = this.sharedMediaData[i8];
                sharedMediaData.max_id[1] = tLRPC$ChatFull2.migrated_from_max_id;
                sharedMediaData.endReached[1] = false;
            }
            i8++;
        }
        this.profileActivity = baseFragment;
        this.actionBar = baseFragment.getActionBar();
        this.mediaColumnsCount[0] = overrideColumnsCount() <= 0 ? SharedConfig.mediaColumnsCount : overrideColumnsCount();
        this.mediaColumnsCount[1] = overrideColumnsCount() <= 0 ? SharedConfig.storiesColumnsCount : overrideColumnsCount();
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.mediaDidLoad);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByServer);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingDidReset);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingDidStart);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.storiesListUpdated);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.storiesUpdated);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.channelRecommendationsLoaded);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.savedMessagesDialogsUpdate);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.dialogsNeedReload);
        for (int i9 = 0; i9 < 10; i9++) {
            if (i2 == 4) {
                SharedAudioCell sharedAudioCell = new SharedAudioCell(context) {
                    @Override
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? SharedMediaLayout.this.sharedMediaData[4].messages : null, false);
                            return playMessage;
                        }
                        if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(SharedMediaLayout.this.sharedMediaData[4].messages, messageObject, SharedMediaLayout.this.mergeDialogId);
                        }
                        return false;
                    }
                };
                sharedAudioCell.initStreamingIcons();
                this.audioCellCache.add(sharedAudioCell);
            }
        }
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        this.searching = false;
        this.searchingReaction = null;
        SearchTagsList searchTagsList = this.searchTagsList;
        if (searchTagsList != null) {
            searchTagsList.show(false);
        }
        this.searchWas = false;
        Drawable drawable = context.getResources().getDrawable(R.drawable.photos_header_shadow);
        this.pinnedHeaderShadowDrawable = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_windowBackgroundGrayShadow), PorterDuff.Mode.MULTIPLY));
        ScrollSlidingTextTabStripInner scrollSlidingTextTabStripInner = this.scrollSlidingTextTabStrip;
        int currentTabId = scrollSlidingTextTabStripInner != null ? scrollSlidingTextTabStripInner.getCurrentTabId() : i2;
        this.scrollSlidingTextTabStrip = createScrollingTextTabStrip(context);
        for (int i10 = 1; i10 >= 0; i10--) {
            this.selectedFiles[i10].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.actionModeViews.clear();
        SavedDialogsAdapter savedDialogsAdapter = this.savedDialogsAdapter;
        if (savedDialogsAdapter != null) {
            savedDialogsAdapter.unselectAll();
        }
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i11, int i12, int i13, int i14, int i15, int i16, int i17, int i18) {
                if (SharedMediaLayout.this.searchItem == null) {
                    return;
                }
                SharedMediaLayout.this.searchItem.setTranslationX(((View) SharedMediaLayout.this.searchItem.getParent()).getMeasuredWidth() - SharedMediaLayout.this.searchItem.getRight());
            }
        });
        if (this.dialog_id == this.profileActivity.getUserConfig().getClientUserId() && (this.profileActivity instanceof MediaActivity) && canShowSearchItem()) {
            this.searchItemIcon = createMenu.addItem(11, R.drawable.ic_ab_search);
        }
        ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(0, 0).setIsSearchField(true).setActionBarMenuItemSearchListener(new AnonymousClass4());
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setTranslationY(AndroidUtilities.dp(10.0f));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        SearchTagsList searchTagsList2 = this.searchTagsList;
        actionBarMenuItem.setSearchFieldHint(LocaleController.getString((searchTagsList2 != null && searchTagsList2.hasFilters() && getSelectedTab() == 11) ? R.string.SavedTagSearchHint : R.string.Search));
        this.searchItem.setContentDescription(LocaleController.getString("Search", R.string.Search));
        this.searchItem.setVisibility(isStoriesView() ? 8 : 4);
        ImageView imageView = new ImageView(context);
        this.photoVideoOptionsItem = imageView;
        imageView.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.photoVideoOptionsItem.setTranslationY(AndroidUtilities.dp(10.0f));
        this.photoVideoOptionsItem.setVisibility(4);
        if (!isArchivedOnlyStoriesView() && !isSearchingStories()) {
            this.actionBar.addView(this.photoVideoOptionsItem, LayoutHelper.createFrame(48, 56, 85));
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.optionsSearchImageView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            this.optionsSearchImageView.setAnimation(R.raw.options_to_search, 24, 24);
            this.optionsSearchImageView.getAnimatedDrawable().multiplySpeed(2.0f);
            this.optionsSearchImageView.getAnimatedDrawable().setPlayInDirectionOfCustomEndFrame(true);
            this.optionsSearchImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_actionBarActionModeDefaultIcon), PorterDuff.Mode.MULTIPLY));
            this.optionsSearchImageView.setVisibility(8);
            this.actionBar.addView(this.optionsSearchImageView, LayoutHelper.createFrame(48, 56, 85));
        }
        this.photoVideoOptionsItem.setOnClickListener(new AnonymousClass5(resourcesProvider, context));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        int i11 = Theme.key_windowBackgroundWhiteBlackText;
        searchField.setTextColor(getThemedColor(i11));
        searchField.setHintTextColor(getThemedColor(Theme.key_player_time));
        searchField.setCursorColor(getThemedColor(i11));
        this.searchItemState = 0;
        BaseFragment baseFragment2 = this.profileActivity;
        BlurredLinearLayout blurredLinearLayout = new BlurredLinearLayout(context, (baseFragment2 == null || !(baseFragment2.getFragmentView() instanceof SizeNotifierFrameLayout)) ? null : (SizeNotifierFrameLayout) this.profileActivity.getFragmentView());
        this.actionModeLayout = blurredLinearLayout;
        blurredLinearLayout.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
        this.actionModeLayout.setAlpha(0.0f);
        this.actionModeLayout.setClickable(true);
        this.actionModeLayout.setVisibility(4);
        ImageView imageView2 = new ImageView(context);
        this.closeButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView3 = this.closeButton;
        BackDrawable backDrawable = new BackDrawable(true);
        this.backDrawable = backDrawable;
        imageView3.setImageDrawable(backDrawable);
        BackDrawable backDrawable2 = this.backDrawable;
        int i12 = Theme.key_actionBarActionModeDefaultIcon;
        backDrawable2.setColor(getThemedColor(i12));
        ImageView imageView4 = this.closeButton;
        int i13 = Theme.key_actionBarActionModeDefaultSelector;
        imageView4.setBackground(Theme.createSelectorDrawable(getThemedColor(i13), 1));
        this.closeButton.setContentDescription(LocaleController.getString("Close", R.string.Close));
        this.actionModeLayout.addView(this.closeButton, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
        this.actionModeViews.add(this.closeButton);
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                SharedMediaLayout.this.lambda$new$3(view);
            }
        });
        NumberTextView numberTextView = new NumberTextView(context);
        this.selectedMessagesCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.bold());
        this.selectedMessagesCountTextView.setTextColor(getThemedColor(i12));
        this.actionModeLayout.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 18, 0, 0, 0));
        this.actionModeViews.add(this.selectedMessagesCountTextView);
        if (DialogObject.isEncryptedDialog(this.dialog_id)) {
            i4 = i13;
            f = 2.0f;
        } else {
            if (isStoriesView()) {
                i4 = i13;
                f = 2.0f;
            } else {
                i4 = i13;
                f = 2.0f;
                ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, getThemedColor(i13), getThemedColor(i12), false);
                this.gotoItem = actionBarMenuItem2;
                actionBarMenuItem2.setIcon(R.drawable.msg_message);
                this.gotoItem.setContentDescription(LocaleController.getString("AccDescrGoToMessage", R.string.AccDescrGoToMessage));
                this.gotoItem.setDuplicateParentStateEnabled(false);
                this.actionModeLayout.addView(this.gotoItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
                this.actionModeViews.add(this.gotoItem);
                this.gotoItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        SharedMediaLayout.this.lambda$new$4(view);
                    }
                });
                ActionBarMenuItem actionBarMenuItem3 = new ActionBarMenuItem(context, (ActionBarMenu) null, getThemedColor(i4), getThemedColor(i12), false);
                this.forwardItem = actionBarMenuItem3;
                actionBarMenuItem3.setIcon(R.drawable.msg_forward);
                this.forwardItem.setContentDescription(LocaleController.getString("Forward", R.string.Forward));
                this.forwardItem.setDuplicateParentStateEnabled(false);
                this.actionModeLayout.addView(this.forwardItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
                this.actionModeViews.add(this.forwardItem);
                this.forwardItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        SharedMediaLayout.this.lambda$new$5(view);
                    }
                });
            }
            ActionBarMenuItem actionBarMenuItem4 = new ActionBarMenuItem(context, (ActionBarMenu) null, getThemedColor(i4), getThemedColor(i12), false);
            this.pinItem = actionBarMenuItem4;
            actionBarMenuItem4.setIcon(R.drawable.msg_pin);
            this.pinItem.setContentDescription(LocaleController.getString(R.string.PinMessage));
            this.pinItem.setDuplicateParentStateEnabled(false);
            this.pinItem.setVisibility(8);
            this.actionModeLayout.addView(this.pinItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
            this.actionModeViews.add(this.pinItem);
            this.pinItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SharedMediaLayout.this.lambda$new$6(view);
                }
            });
            ActionBarMenuItem actionBarMenuItem5 = new ActionBarMenuItem(context, (ActionBarMenu) null, getThemedColor(i4), getThemedColor(i12), false);
            this.unpinItem = actionBarMenuItem5;
            actionBarMenuItem5.setIcon(R.drawable.msg_unpin);
            this.unpinItem.setContentDescription(LocaleController.getString(R.string.UnpinMessage));
            this.unpinItem.setDuplicateParentStateEnabled(false);
            this.unpinItem.setVisibility(8);
            this.actionModeLayout.addView(this.unpinItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
            this.actionModeViews.add(this.unpinItem);
            this.unpinItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SharedMediaLayout.this.lambda$new$7(view);
                }
            });
            updateForwardItem();
        }
        ActionBarMenuItem actionBarMenuItem6 = new ActionBarMenuItem(context, (ActionBarMenu) null, getThemedColor(i4), getThemedColor(i12), false);
        this.deleteItem = actionBarMenuItem6;
        actionBarMenuItem6.setIcon(R.drawable.msg_delete);
        this.deleteItem.setContentDescription(LocaleController.getString("Delete", R.string.Delete));
        this.deleteItem.setDuplicateParentStateEnabled(false);
        this.actionModeLayout.addView(this.deleteItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
        this.actionModeViews.add(this.deleteItem);
        this.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                SharedMediaLayout.this.lambda$new$8(view);
            }
        });
        this.photoVideoAdapter = new SharedPhotoVideoAdapter(context) {
            @Override
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                MediaPage mediaPage2 = SharedMediaLayout.this.getMediaPage(0);
                if (mediaPage2 == null || mediaPage2.animationSupportingListView.getVisibility() != 0) {
                    return;
                }
                SharedMediaLayout.this.animationSupportingPhotoVideoAdapter.notifyDataSetChanged();
            }
        };
        this.animationSupportingPhotoVideoAdapter = new SharedPhotoVideoAdapter(context);
        this.documentsAdapter = new SharedDocumentsAdapter(context, 1);
        this.voiceAdapter = new SharedDocumentsAdapter(context, 2);
        this.audioAdapter = new SharedDocumentsAdapter(context, 4);
        this.gifAdapter = new GifAdapter(context);
        this.documentsSearchAdapter = new MediaSearchAdapter(context, 1);
        this.audioSearchAdapter = new MediaSearchAdapter(context, 4);
        this.linksSearchAdapter = new MediaSearchAdapter(context, 3);
        this.groupUsersSearchAdapter = new GroupUsersSearchAdapter(context);
        this.commonGroupsAdapter = new CommonGroupsAdapter(context);
        this.channelRecommendationsAdapter = new ChannelRecommendationsAdapter(context);
        this.savedDialogsAdapter = new SavedDialogsAdapter(context);
        this.savedMessagesSearchAdapter = new SavedMessagesSearchAdapter(context);
        if (!isStoriesView() && !includeSavedDialogs() && this.topicId == 0) {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", this.profileActivity.getUserConfig().getClientUserId());
            bundle.putInt("chatMode", 3);
            ChatActivityContainer chatActivityContainer = new ChatActivityContainer(context, this.profileActivity.getParentLayout(), bundle) {
                @Override
                protected void onSearchLoadingUpdate(boolean z2) {
                    if (SharedMediaLayout.this.searchItem != null) {
                        SharedMediaLayout.this.searchItem.setShowSearchProgress(z2);
                    }
                }
            };
            this.savedMessagesContainer = chatActivityContainer;
            chatActivityContainer.chatActivity.setSavedDialog(this.dialog_id);
            this.savedMessagesContainer.chatActivity.reversed = true;
        }
        ChatUsersAdapter chatUsersAdapter = new ChatUsersAdapter(context);
        this.chatUsersAdapter = chatUsersAdapter;
        if (this.topicId == 0) {
            chatUsersAdapter.sortedUsers = arrayList;
            this.chatUsersAdapter.chatInfo = currentTabId != 7 ? null : tLRPC$ChatFull3;
        }
        this.storiesAdapter = new StoriesAdapter(context, z) {
            @Override
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                MediaPage mediaPage2 = SharedMediaLayout.this.getMediaPage(8);
                if (mediaPage2 != null && mediaPage2.animationSupportingListView.getVisibility() == 0) {
                    SharedMediaLayout.this.animationSupportingStoriesAdapter.notifyDataSetChanged();
                }
                if (mediaPage2 != null) {
                    StickerEmptyView stickerEmptyView = mediaPage2.emptyView;
                    StoriesController.StoriesList storiesList = this.storiesList;
                    stickerEmptyView.showProgress(storiesList != null && (storiesList.isLoading() || (SharedMediaLayout.this.hasInternet() && this.storiesList.getCount() > 0)));
                }
            }
        };
        this.storiesReorder = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            private RecyclerListView listView;

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i14) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return SharedMediaLayout.this.isActionModeShowed;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                if (sharedMediaLayout.isActionModeShowed && sharedMediaLayout.storiesAdapter.canReorder(viewHolder.getAdapterPosition())) {
                    InternalListView internalListView = SharedMediaLayout.this.mediaPages[0] == null ? null : SharedMediaLayout.this.mediaPages[0].listView;
                    this.listView = internalListView;
                    if (internalListView != null) {
                        internalListView.setItemAnimator(SharedMediaLayout.this.mediaPages[0].itemAnimator);
                    }
                    return ItemTouchHelper.Callback.makeMovementFlags(15, 0);
                }
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
                if (!SharedMediaLayout.this.storiesAdapter.canReorder(viewHolder.getAdapterPosition()) || !SharedMediaLayout.this.storiesAdapter.canReorder(viewHolder2.getAdapterPosition())) {
                    return false;
                }
                SharedMediaLayout.this.storiesAdapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
                return true;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i14) {
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView != null && viewHolder != null) {
                    recyclerListView.hideSelector(false);
                }
                if (i14 == 0) {
                    SharedMediaLayout.this.storiesAdapter.reorderDone();
                    RecyclerListView recyclerListView2 = this.listView;
                    if (recyclerListView2 != null) {
                        recyclerListView2.setItemAnimator(null);
                    }
                } else {
                    RecyclerListView recyclerListView3 = this.listView;
                    if (recyclerListView3 != null) {
                        recyclerListView3.cancelClickRunnables(false);
                    }
                    if (viewHolder != null) {
                        viewHolder.itemView.setPressed(true);
                    }
                }
                super.onSelectedChanged(viewHolder, i14);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setPressed(false);
            }
        });
        this.animationSupportingStoriesAdapter = new StoriesAdapter(context, false);
        this.archivedStoriesAdapter = new StoriesAdapter(context, true) {
            @Override
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                MediaPage mediaPage2 = SharedMediaLayout.this.getMediaPage(9);
                if (mediaPage2 != null && mediaPage2.animationSupportingListView.getVisibility() == 0) {
                    SharedMediaLayout.this.animationSupportingArchivedStoriesAdapter.notifyDataSetChanged();
                }
                if (mediaPage2 != null) {
                    StickerEmptyView stickerEmptyView = mediaPage2.emptyView;
                    StoriesController.StoriesList storiesList = this.storiesList;
                    stickerEmptyView.showProgress(storiesList != null && (storiesList.isLoading() || (SharedMediaLayout.this.hasInternet() && this.storiesList.getCount() > 0)));
                }
            }
        };
        this.animationSupportingArchivedStoriesAdapter = new StoriesAdapter(context, true);
        this.linksAdapter = new SharedLinksAdapter(context);
        if (isBot()) {
            this.botPreviewsContainer = new AnonymousClass11(context, this.profileActivity, this.dialog_id);
        }
        setWillNotDraw(false);
        int i14 = -1;
        int i15 = 0;
        int i16 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i15 >= mediaPageArr.length) {
                break;
            }
            if (i15 == 0 && (mediaPage = mediaPageArr[i15]) != null && mediaPage.layoutManager != null) {
                i14 = this.mediaPages[i15].layoutManager.findFirstVisibleItemPosition();
                if (i14 == this.mediaPages[i15].layoutManager.getItemCount() - 1 || (holder = (RecyclerListView.Holder) this.mediaPages[i15].listView.findViewHolderForAdapterPosition(i14)) == null) {
                    i14 = -1;
                } else {
                    i16 = holder.itemView.getTop();
                }
            }
            final MediaPage mediaPage2 = new MediaPage(context) {
                @Override
                public void setTranslationX(float f2) {
                    super.setTranslationX(f2);
                    if (SharedMediaLayout.this.tabsAnimationInProgress) {
                        if (SharedMediaLayout.this.mediaPages[0] == this) {
                            float abs = Math.abs(SharedMediaLayout.this.mediaPages[0].getTranslationX()) / SharedMediaLayout.this.mediaPages[0].getMeasuredWidth();
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            sharedMediaLayout.scrollSlidingTextTabStrip.selectTabWithId(sharedMediaLayout.mediaPages[1].selectedType, abs);
                            if (SharedMediaLayout.this.canShowSearchItem()) {
                                if (SharedMediaLayout.this.searchItemState == 2) {
                                    SharedMediaLayout.this.searchAlpha = 1.0f - abs;
                                } else if (SharedMediaLayout.this.searchItemState == 1) {
                                    SharedMediaLayout.this.searchAlpha = abs;
                                }
                                SharedMediaLayout.this.updateSearchItemIcon(abs);
                                SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                                sharedMediaLayout2.optionsAlpha = sharedMediaLayout2.getPhotoVideoOptionsAlpha(abs);
                                SharedMediaLayout sharedMediaLayout3 = SharedMediaLayout.this;
                                sharedMediaLayout3.photoVideoOptionsItem.setVisibility((sharedMediaLayout3.optionsAlpha == 0.0f || !SharedMediaLayout.this.canShowSearchItem() || SharedMediaLayout.this.isArchivedOnlyStoriesView()) ? 4 : 0);
                            } else {
                                SharedMediaLayout.this.searchAlpha = 0.0f;
                            }
                            SharedMediaLayout.this.updateOptionsSearch();
                        }
                    }
                    SharedMediaLayout.this.invalidateBlur();
                }
            };
            addView(mediaPage2, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, mediaPageTopMargin(), 0.0f, 0.0f));
            if (i15 == 1) {
                mediaPage2.setTranslationX(AndroidUtilities.displaySize.x);
            }
            this.mediaPages[i15] = mediaPage2;
            final ExtendedGridLayoutManager extendedGridLayoutManager = mediaPage2.layoutManager = new ExtendedGridLayoutManager(context, 100) {
                private Size size = new Size();

                @Override
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                @Override
                public void calculateExtraLayoutSpace(RecyclerView.State state, int[] iArr2) {
                    super.calculateExtraLayoutSpace(state, iArr2);
                    if (mediaPage2.selectedType != 0 && mediaPage2.selectedType != 8 && mediaPage2.selectedType != 9) {
                        if (mediaPage2.selectedType == 1) {
                            iArr2[1] = Math.max(iArr2[1], AndroidUtilities.dp(56.0f) * 2);
                            return;
                        }
                        return;
                    }
                    iArr2[1] = Math.max(iArr2[1], SharedPhotoVideoCell.getItemSize(1) * 2);
                }

                @Override
                protected Size getSizeForItem(int i17) {
                    int i18;
                    int i19;
                    TLRPC$Document document = (mediaPage2.listView.getAdapter() != SharedMediaLayout.this.gifAdapter || SharedMediaLayout.this.sharedMediaData[5].messages.isEmpty()) ? null : SharedMediaLayout.this.sharedMediaData[5].messages.get(i17).getDocument();
                    Size size = this.size;
                    size.height = 100.0f;
                    size.width = 100.0f;
                    if (document != null) {
                        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                        if (closestPhotoSizeWithSize != null && (i18 = closestPhotoSizeWithSize.w) != 0 && (i19 = closestPhotoSizeWithSize.h) != 0) {
                            Size size2 = this.size;
                            size2.width = i18;
                            size2.height = i19;
                        }
                        ArrayList<TLRPC$DocumentAttribute> arrayList2 = document.attributes;
                        for (int i20 = 0; i20 < arrayList2.size(); i20++) {
                            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = arrayList2.get(i20);
                            if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeImageSize) || (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo)) {
                                Size size3 = this.size;
                                size3.width = tLRPC$DocumentAttribute.w;
                                size3.height = tLRPC$DocumentAttribute.h;
                                break;
                            }
                        }
                    }
                    return this.size;
                }

                @Override
                public int getFlowItemCount() {
                    if (mediaPage2.listView.getAdapter() != SharedMediaLayout.this.gifAdapter) {
                        return 0;
                    }
                    return getItemCount();
                }

                @Override
                public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfoForItem(recycler, state, view, accessibilityNodeInfoCompat);
                    AccessibilityNodeInfoCompat.CollectionItemInfoCompat collectionItemInfo = accessibilityNodeInfoCompat.getCollectionItemInfo();
                    if (collectionItemInfo == null || !collectionItemInfo.isHeading()) {
                        return;
                    }
                    accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(collectionItemInfo.getRowIndex(), collectionItemInfo.getRowSpan(), collectionItemInfo.getColumnIndex(), collectionItemInfo.getColumnSpan(), false));
                }
            };
            extendedGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int i17) {
                    int i18 = SharedMediaLayout.this.mediaColumnsCount[(mediaPage2.selectedType == 8 || mediaPage2.selectedType == 9) ? (char) 1 : (char) 0];
                    if (mediaPage2.listView.getAdapter() == SharedMediaLayout.this.photoVideoAdapter) {
                        if (SharedMediaLayout.this.photoVideoAdapter.getItemViewType(i17) == 2) {
                            return i18;
                        }
                        return 1;
                    }
                    if (mediaPage2.listView.getAdapter() == SharedMediaLayout.this.storiesAdapter) {
                        if (SharedMediaLayout.this.storiesAdapter.getItemViewType(i17) == 2) {
                            return i18;
                        }
                        return 1;
                    }
                    if (mediaPage2.listView.getAdapter() == SharedMediaLayout.this.archivedStoriesAdapter) {
                        if (SharedMediaLayout.this.storiesAdapter.getItemViewType(i17) == 2) {
                            return i18;
                        }
                        return 1;
                    }
                    if (mediaPage2.listView.getAdapter() != SharedMediaLayout.this.gifAdapter) {
                        return mediaPage2.layoutManager.getSpanCount();
                    }
                    return (mediaPage2.listView.getAdapter() == SharedMediaLayout.this.gifAdapter && SharedMediaLayout.this.sharedMediaData[5].messages.isEmpty()) ? mediaPage2.layoutManager.getSpanCount() : mediaPage2.layoutManager.getSpanSizeForItem(i17);
                }
            });
            this.mediaPages[i15].itemAnimator = new DefaultItemAnimator();
            this.mediaPages[i15].itemAnimator.setDurations(280L);
            this.mediaPages[i15].itemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.mediaPages[i15].itemAnimator.setSupportsChangeAnimations(false);
            this.mediaPages[i15].buttonView = new ButtonWithCounterView(context, resourcesProvider);
            this.mediaPages[i15].buttonView.setText(addPostText(), false);
            this.mediaPages[i15].buttonView.setVisibility(8);
            this.mediaPages[i15].buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SharedMediaLayout.this.lambda$new$9(view);
                }
            });
            this.mediaPages[i15].listView = new SharedMediaListView(context) {
                float lastY;
                float startY;

                @Override
                public RecyclerListView.FastScrollAdapter getMovingAdapter() {
                    if (SharedMediaLayout.this.changeColumnsTab == 8) {
                        return SharedMediaLayout.this.storiesAdapter;
                    }
                    return SharedMediaLayout.this.changeColumnsTab == 9 ? SharedMediaLayout.this.archivedStoriesAdapter : SharedMediaLayout.this.photoVideoAdapter;
                }

                @Override
                public RecyclerListView.FastScrollAdapter getSupportingAdapter() {
                    if (SharedMediaLayout.this.changeColumnsTab == 8) {
                        return SharedMediaLayout.this.animationSupportingStoriesAdapter;
                    }
                    return SharedMediaLayout.this.changeColumnsTab == 9 ? SharedMediaLayout.this.animationSupportingArchivedStoriesAdapter : SharedMediaLayout.this.animationSupportingPhotoVideoAdapter;
                }

                @Override
                public int getColumnsCount() {
                    return (SharedMediaLayout.this.changeColumnsTab == 8 || SharedMediaLayout.this.changeColumnsTab == 9) ? SharedMediaLayout.this.mediaColumnsCount[1] : SharedMediaLayout.this.mediaColumnsCount[0];
                }

                @Override
                public int getAnimateToColumnsCount() {
                    return SharedMediaLayout.this.animateToColumnsCount;
                }

                @Override
                public boolean isChangeColumnsAnimation() {
                    return SharedMediaLayout.this.photoVideoChangeColumnsAnimation;
                }

                @Override
                public float getChangeColumnsProgress() {
                    return SharedMediaLayout.this.photoVideoChangeColumnsProgress;
                }

                @Override
                public boolean isThisListView() {
                    return this == mediaPage2.listView;
                }

                @Override
                public SparseArray<Float> getMessageAlphaEnter() {
                    return SharedMediaLayout.this.messageAlphaEnter;
                }

                @Override
                public boolean isStories() {
                    return SharedMediaLayout.this.changeColumnsTab == 8 || SharedMediaLayout.this.changeColumnsTab == 9;
                }

                @Override
                public InternalListView getSupportingListView() {
                    return mediaPage2.animationSupportingListView;
                }

                @Override
                public void checkHighlightCell(SharedPhotoVideoCell2 sharedPhotoVideoCell2) {
                    if (sharedPhotoVideoCell2.getMessageId() == mediaPage2.highlightMessageId && sharedPhotoVideoCell2.imageReceiver.hasBitmapImage()) {
                        MediaPage mediaPage3 = mediaPage2;
                        if (!mediaPage3.highlightAnimation) {
                            mediaPage3.highlightProgress = 0.0f;
                            mediaPage3.highlightAnimation = true;
                        }
                        float f2 = mediaPage3.highlightProgress;
                        sharedPhotoVideoCell2.setHighlightProgress(f2 < 0.3f ? f2 / 0.3f : f2 > 0.7f ? (1.0f - f2) / 0.3f : 1.0f);
                        return;
                    }
                    sharedPhotoVideoCell2.setHighlightProgress(0.0f);
                }

                @Override
                public void onLayout(boolean z2, int i17, int i18, int i19, int i20) {
                    super.onLayout(z2, i17, i18, i19, i20);
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    MediaPage mediaPage3 = mediaPage2;
                    sharedMediaLayout.checkLoadMoreScroll(mediaPage3, mediaPage3.listView, extendedGridLayoutManager);
                    if (mediaPage2.selectedType == 0) {
                        PhotoViewer.getInstance().checkCurrentImageVisibility();
                    }
                }

                @Override
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    if (SharedMediaLayout.this.profileActivity != null && SharedMediaLayout.this.profileActivity.isInPreviewMode()) {
                        this.lastY = motionEvent.getY();
                        if (motionEvent.getAction() == 1) {
                            SharedMediaLayout.this.profileActivity.finishPreviewFragment();
                        } else if (motionEvent.getAction() == 2) {
                            float f2 = this.startY - this.lastY;
                            SharedMediaLayout.this.profileActivity.movePreviewFragment(f2);
                            if (f2 < 0.0f) {
                                this.startY = this.lastY;
                            }
                        }
                        return true;
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }

                @Override
                protected void emptyViewUpdated(boolean z2, boolean z3) {
                    if (getAdapter() == SharedMediaLayout.this.storiesAdapter) {
                        if (z3) {
                            mediaPage2.buttonView.animate().alpha(z2 ? 0.0f : 1.0f).start();
                        } else {
                            mediaPage2.buttonView.setAlpha(z2 ? 0.0f : 1.0f);
                        }
                    }
                }

                @Override
                protected void dispatchDraw(Canvas canvas) {
                    View childAt;
                    if ((getAdapter() == SharedMediaLayout.this.archivedStoriesAdapter || getAdapter() == SharedMediaLayout.this.storiesAdapter) && getChildCount() > 0) {
                        View childAt2 = getChildAt(0);
                        if (childAt2 == null || getChildAdapterPosition(childAt2) != 0) {
                            if (getAdapter() == SharedMediaLayout.this.storiesAdapter) {
                                mediaPage2.buttonView.setTranslationY(-AndroidUtilities.dp(72.0f));
                            }
                        } else {
                            int top = childAt2.getTop();
                            if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                                if (SharedMediaLayout.this.changeColumnsTab == (getAdapter() == SharedMediaLayout.this.storiesAdapter ? 8 : 9) && mediaPage2.animationSupportingListView.getChildCount() > 0 && (childAt = mediaPage2.animationSupportingListView.getChildAt(0)) != null && mediaPage2.animationSupportingListView.getChildAdapterPosition(childAt) == 0) {
                                    top = AndroidUtilities.lerp(top, childAt.getTop(), SharedMediaLayout.this.photoVideoChangeColumnsProgress);
                                }
                            }
                            if (getAdapter() == SharedMediaLayout.this.storiesAdapter) {
                                mediaPage2.buttonView.setVisibility(0);
                                mediaPage2.buttonView.setTranslationY((getY() + top) - AndroidUtilities.dp(72.0f));
                            } else {
                                if (this.archivedHintPaint == null) {
                                    TextPaint textPaint = new TextPaint(1);
                                    this.archivedHintPaint = textPaint;
                                    textPaint.setTextSize(AndroidUtilities.dp(14.0f));
                                    this.archivedHintPaint.setColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText2));
                                }
                                int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(60.0f);
                                StaticLayout staticLayout = this.archivedHintLayout;
                                if (staticLayout == null || staticLayout.getWidth() != measuredWidth) {
                                    this.archivedHintLayout = new StaticLayout(LocaleController.getString(SharedMediaLayout.this.isArchivedOnlyStoriesView() ? SharedMediaLayout.this.profileActivity != null && ChatObject.isChannelAndNotMegaGroup(SharedMediaLayout.this.profileActivity.getMessagesController().getChat(Long.valueOf(-SharedMediaLayout.this.dialog_id))) ? R.string.ProfileStoriesArchiveChannelHint : R.string.ProfileStoriesArchiveGroupHint : R.string.ProfileStoriesArchiveHint), this.archivedHintPaint, measuredWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                                    this.archivedHintLayoutWidth = 0.0f;
                                    this.archivedHintLayoutLeft = measuredWidth;
                                    for (int i17 = 0; i17 < this.archivedHintLayout.getLineCount(); i17++) {
                                        this.archivedHintLayoutWidth = Math.max(this.archivedHintLayoutWidth, this.archivedHintLayout.getLineWidth(i17));
                                        this.archivedHintLayoutLeft = Math.min(this.archivedHintLayoutLeft, this.archivedHintLayout.getLineLeft(i17));
                                    }
                                }
                                canvas.save();
                                canvas.translate(((getWidth() - this.archivedHintLayoutWidth) / 2.0f) - this.archivedHintLayoutLeft, top - ((AndroidUtilities.dp(64.0f) + this.archivedHintLayout.getHeight()) / 2.0f));
                                this.archivedHintLayout.draw(canvas);
                                canvas.restore();
                            }
                        }
                    }
                    super.dispatchDraw(canvas);
                    MediaPage mediaPage3 = mediaPage2;
                    if (mediaPage3.highlightAnimation) {
                        float f2 = mediaPage3.highlightProgress + 0.010666667f;
                        mediaPage3.highlightProgress = f2;
                        if (f2 >= 1.0f) {
                            mediaPage3.highlightProgress = 0.0f;
                            mediaPage3.highlightAnimation = false;
                            mediaPage3.highlightMessageId = 0;
                        }
                        invalidate();
                    }
                    if (this.poller == null) {
                        this.poller = UserListPoller.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount());
                    }
                    this.poller.checkList(this);
                    if (isChangeColumnsAnimation()) {
                        return;
                    }
                    SharedMediaLayout.this.changeColumnsTab = -1;
                }

                @Override
                public Integer getSelectorColor(int i17) {
                    if (getAdapter() == SharedMediaLayout.this.channelRecommendationsAdapter && SharedMediaLayout.this.channelRecommendationsAdapter.more > 0 && i17 == SharedMediaLayout.this.channelRecommendationsAdapter.getItemCount() - 1) {
                        return 0;
                    }
                    return super.getSelectorColor(i17);
                }

                @Override
                public void onScrolled(int i17, int i18) {
                    super.onScrolled(i17, i18);
                    if (this.scrollingByUser && SharedMediaLayout.this.getSelectedTab() == 11 && SharedMediaLayout.this.profileActivity != null) {
                        AndroidUtilities.hideKeyboard(SharedMediaLayout.this.profileActivity.getParentActivity().getCurrentFocus());
                    }
                }
            };
            this.mediaPages[i15].listView.setFastScrollEnabled(1);
            this.mediaPages[i15].listView.setScrollingTouchSlop(1);
            this.mediaPages[i15].listView.setPinnedSectionOffsetY(-AndroidUtilities.dp(f));
            this.mediaPages[i15].listView.setPadding(0, 0, 0, 0);
            this.mediaPages[i15].listView.setItemAnimator(null);
            this.mediaPages[i15].listView.setClipToPadding(false);
            this.mediaPages[i15].listView.setSectionsType(2);
            this.mediaPages[i15].listView.setLayoutManager(extendedGridLayoutManager);
            MediaPage mediaPage3 = this.mediaPages[i15];
            mediaPage3.addView(mediaPage3.listView, LayoutHelper.createFrame(-1, -1.0f));
            MediaPage mediaPage4 = this.mediaPages[i15];
            mediaPage4.addView(mediaPage4.buttonView, LayoutHelper.createFrame(-1, 48.0f, 55, 12.0f, 12.0f, 12.0f, 12.0f));
            this.mediaPages[i15].animationSupportingListView = new InternalListView(context);
            this.mediaPages[i15].animationSupportingListView.setLayoutManager(this.mediaPages[i15].animationSupportingLayoutManager = new GridLayoutManager(context, 3) {
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                @Override
                public int scrollVerticallyBy(int i17, RecyclerView.Recycler recycler, RecyclerView.State state) {
                    if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                        i17 = 0;
                    }
                    return super.scrollVerticallyBy(i17, recycler, state);
                }
            });
            MediaPage mediaPage5 = this.mediaPages[i15];
            mediaPage5.addView(mediaPage5.animationSupportingListView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[i15].animationSupportingListView.setVisibility(8);
            this.mediaPages[i15].animationSupportingListView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                    if (view instanceof SharedPhotoVideoCell2) {
                        SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) view;
                        int childAdapterPosition = mediaPage2.animationSupportingListView.getChildAdapterPosition(sharedPhotoVideoCell2);
                        int spanCount = mediaPage2.animationSupportingLayoutManager.getSpanCount();
                        int i17 = childAdapterPosition % spanCount;
                        sharedPhotoVideoCell2.isFirst = i17 == 0;
                        sharedPhotoVideoCell2.isLast = i17 == spanCount - 1;
                        rect.left = 0;
                        rect.top = 0;
                        rect.bottom = 0;
                        rect.right = 0;
                        return;
                    }
                    rect.left = 0;
                    rect.top = 0;
                    rect.bottom = 0;
                    rect.right = 0;
                }
            });
            this.mediaPages[i15].listView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                    if (mediaPage2.listView.getAdapter() == SharedMediaLayout.this.gifAdapter) {
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                        rect.left = 0;
                        rect.bottom = 0;
                        if (!mediaPage2.layoutManager.isFirstRow(childAdapterPosition)) {
                            rect.top = AndroidUtilities.dp(2.0f);
                        } else {
                            rect.top = 0;
                        }
                        rect.right = mediaPage2.layoutManager.isLastInRow(childAdapterPosition) ? 0 : AndroidUtilities.dp(2.0f);
                        return;
                    }
                    if (view instanceof SharedPhotoVideoCell2) {
                        SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) view;
                        int childAdapterPosition2 = mediaPage2.listView.getChildAdapterPosition(sharedPhotoVideoCell2);
                        int spanCount = mediaPage2.layoutManager.getSpanCount();
                        int i17 = childAdapterPosition2 % spanCount;
                        sharedPhotoVideoCell2.isFirst = i17 == 0;
                        sharedPhotoVideoCell2.isLast = i17 == spanCount - 1;
                        rect.left = 0;
                        rect.top = 0;
                        rect.bottom = 0;
                        rect.right = 0;
                        return;
                    }
                    rect.left = 0;
                    rect.top = 0;
                    rect.bottom = 0;
                    rect.right = 0;
                }
            });
            this.mediaPages[i15].listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
                @Override
                public boolean hasDoubleTap(View view, int i17) {
                    return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i17);
                }

                @Override
                public void onDoubleTap(View view, int i17, float f2, float f3) {
                    RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i17, f2, f3);
                }

                @Override
                public final void onItemClick(View view, int i17, float f2, float f3) {
                    SharedMediaLayout.this.lambda$new$10(mediaPage2, view, i17, f2, f3);
                }
            });
            this.mediaPages[i15].listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int i17) {
                    SharedMediaLayout.this.scrolling = i17 != 0;
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int i17, int i18) {
                    SharedMediaLayout.this.checkLoadMoreScroll(mediaPage2, (RecyclerListView) recyclerView, extendedGridLayoutManager);
                    if (i18 != 0 && ((SharedMediaLayout.this.mediaPages[0].selectedType == 0 || SharedMediaLayout.this.mediaPages[0].selectedType == 5) && !SharedMediaLayout.this.sharedMediaData[0].messages.isEmpty())) {
                        SharedMediaLayout.this.showFloatingDateView();
                    }
                    if (i18 != 0 && (mediaPage2.selectedType == 0 || mediaPage2.selectedType == 8 || mediaPage2.selectedType == 9)) {
                        SharedMediaLayout.showFastScrollHint(mediaPage2, SharedMediaLayout.this.sharedMediaData, true);
                    }
                    mediaPage2.listView.checkSection(true);
                    MediaPage mediaPage6 = mediaPage2;
                    if (mediaPage6.fastScrollHintView != null) {
                        mediaPage6.invalidate();
                    }
                    SharedMediaLayout.this.invalidateBlur();
                }
            });
            this.mediaPages[i15].listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListenerExtended() {
                @Override
                public boolean onItemClick(View view, int i17, float f2, float f3) {
                    int i18;
                    int i19 = 0;
                    if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation || mediaPage2.listView.getAdapter() == SharedMediaLayout.this.savedMessagesSearchAdapter) {
                        return false;
                    }
                    if (!SharedMediaLayout.this.isActionModeShowed || mediaPage2.selectedType == 11) {
                        if (mediaPage2.selectedType != 7 || !(view instanceof UserCell)) {
                            if (mediaPage2.selectedType == 1 && (view instanceof SharedDocumentCell)) {
                                return SharedMediaLayout.this.onItemLongClick(((SharedDocumentCell) view).getMessage(), view, 0);
                            }
                            if (mediaPage2.selectedType == 3 && (view instanceof SharedLinkCell)) {
                                return SharedMediaLayout.this.onItemLongClick(((SharedLinkCell) view).getMessage(), view, 0);
                            }
                            if ((mediaPage2.selectedType == 2 || mediaPage2.selectedType == 4) && (view instanceof SharedAudioCell)) {
                                return SharedMediaLayout.this.onItemLongClick(((SharedAudioCell) view).getMessage(), view, 0);
                            }
                            if (mediaPage2.selectedType == 5 && (view instanceof ContextLinkCell)) {
                                return SharedMediaLayout.this.onItemLongClick((MessageObject) ((ContextLinkCell) view).getParentObject(), view, 0);
                            }
                            if ((mediaPage2.selectedType != 0 && mediaPage2.selectedType != 9 && (mediaPage2.selectedType != 8 || !SharedMediaLayout.this.canEditStories())) || !(view instanceof SharedPhotoVideoCell2)) {
                                if (mediaPage2.selectedType == 10) {
                                    SharedMediaLayout.this.channelRecommendationsAdapter.openPreview(i17);
                                    return true;
                                }
                                if (mediaPage2.selectedType == 11) {
                                    SharedMediaLayout.this.savedDialogsAdapter.select(view);
                                    return true;
                                }
                            } else {
                                MessageObject messageObject = ((SharedPhotoVideoCell2) view).getMessageObject();
                                if (messageObject != null) {
                                    return SharedMediaLayout.this.onItemLongClick(messageObject, view, mediaPage2.selectedType);
                                }
                            }
                            return false;
                        }
                        if (SharedMediaLayout.this.chatUsersAdapter.sortedUsers.isEmpty()) {
                            i18 = i17;
                        } else {
                            if (i17 >= SharedMediaLayout.this.chatUsersAdapter.sortedUsers.size()) {
                                return false;
                            }
                            i18 = ((Integer) SharedMediaLayout.this.chatUsersAdapter.sortedUsers.get(i17)).intValue();
                        }
                        if (i18 < 0 || i18 >= SharedMediaLayout.this.chatUsersAdapter.chatInfo.participants.participants.size()) {
                            return false;
                        }
                        TLRPC$ChatParticipant tLRPC$ChatParticipant = SharedMediaLayout.this.chatUsersAdapter.chatInfo.participants.participants.get(i18);
                        RecyclerListView recyclerListView = (RecyclerListView) view.getParent();
                        while (true) {
                            if (i19 >= recyclerListView.getChildCount()) {
                                break;
                            }
                            View childAt = recyclerListView.getChildAt(i19);
                            if (recyclerListView.getChildAdapterPosition(childAt) == i17) {
                                view = childAt;
                                break;
                            }
                            i19++;
                        }
                        return SharedMediaLayout.this.onMemberClick(tLRPC$ChatParticipant, true, view);
                    }
                    mediaPage2.listView.clickItem(view, i17);
                    return true;
                }

                @Override
                public void onMove(float f2, float f3) {
                    if (SharedMediaLayout.this.profileActivity != null) {
                        android.graphics.Point point = AndroidUtilities.displaySize;
                        if (point.x > point.y) {
                            SharedMediaLayout.this.profileActivity.movePreviewFragment(f3);
                        }
                    }
                }

                @Override
                public void onLongClickRelease() {
                    if (SharedMediaLayout.this.profileActivity != null) {
                        android.graphics.Point point = AndroidUtilities.displaySize;
                        if (point.x > point.y) {
                            SharedMediaLayout.this.profileActivity.finishPreviewFragment();
                        }
                    }
                }
            });
            if (i15 == 0 && i14 != -1) {
                extendedGridLayoutManager.scrollToPositionWithOffset(i14, i16);
            }
            final InternalListView internalListView = this.mediaPages[i15].listView;
            this.mediaPages[i15].animatingImageView = new ClippingImageView(context) {
                @Override
                public void invalidate() {
                    super.invalidate();
                    internalListView.invalidate();
                }
            };
            this.mediaPages[i15].animatingImageView.setVisibility(8);
            this.mediaPages[i15].listView.addOverlayView(this.mediaPages[i15].animatingImageView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[i15].progressView = new FlickerLoadingView(context) {
                @Override
                public int getColumnsCount() {
                    return SharedMediaLayout.this.mediaColumnsCount[(mediaPage2.selectedType == 8 || mediaPage2.selectedType == 9) ? (char) 1 : (char) 0];
                }

                @Override
                public int getViewType() {
                    setIsSingleCell(false);
                    if (mediaPage2.selectedType == 0 || mediaPage2.selectedType == 5) {
                        return 2;
                    }
                    if (mediaPage2.selectedType == 1) {
                        return 3;
                    }
                    if (mediaPage2.selectedType == 2 || mediaPage2.selectedType == 4) {
                        return 6;
                    }
                    if (mediaPage2.selectedType == 3) {
                        return 5;
                    }
                    if (mediaPage2.selectedType == 7) {
                        return 6;
                    }
                    if (mediaPage2.selectedType != 6) {
                        return (mediaPage2.selectedType == 8 || mediaPage2.selectedType == 9) ? 27 : 1;
                    }
                    if (SharedMediaLayout.this.scrollSlidingTextTabStrip.getTabsCount() == 1) {
                        setIsSingleCell(true);
                    }
                    return 1;
                }

                @Override
                public void onDraw(Canvas canvas) {
                    SharedMediaLayout.this.backgroundPaint.setColor(SharedMediaLayout.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), SharedMediaLayout.this.backgroundPaint);
                    super.onDraw(canvas);
                }
            };
            this.mediaPages[i15].progressView.showDate(false);
            if (i15 != 0) {
                this.mediaPages[i15].setVisibility(8);
            }
            MediaPage mediaPage6 = this.mediaPages[i15];
            mediaPage6.emptyView = new StickerEmptyView(context, mediaPage6.progressView, 1);
            this.mediaPages[i15].emptyView.setVisibility(8);
            this.mediaPages[i15].emptyView.setAnimateLayoutChange(true);
            MediaPage mediaPage7 = this.mediaPages[i15];
            mediaPage7.addView(mediaPage7.emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[i15].emptyView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    boolean lambda$new$11;
                    lambda$new$11 = SharedMediaLayout.lambda$new$11(view, motionEvent);
                    return lambda$new$11;
                }
            });
            this.mediaPages[i15].emptyView.showProgress(true, false);
            this.mediaPages[i15].emptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
            this.mediaPages[i15].emptyView.button.setVisibility(8);
            this.mediaPages[i15].emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
            this.mediaPages[i15].emptyView.button.setVisibility(8);
            this.mediaPages[i15].emptyView.addView(this.mediaPages[i15].progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[i15].listView.setEmptyView(this.mediaPages[i15].emptyView);
            this.mediaPages[i15].listView.setAnimateEmptyView(true, 0);
            MediaPage mediaPage8 = this.mediaPages[i15];
            mediaPage8.scrollHelper = new RecyclerAnimationScrollHelper(mediaPage8.listView, this.mediaPages[i15].layoutManager);
            i15++;
        }
        ChatActionCell chatActionCell = new ChatActionCell(context);
        this.floatingDateView = chatActionCell;
        chatActionCell.setCustomDate((int) (System.currentTimeMillis() / 1000), false, false);
        this.floatingDateView.setAlpha(0.0f);
        this.floatingDateView.setOverrideColor(Theme.key_chat_mediaTimeBackground, Theme.key_chat_mediaTimeText);
        this.floatingDateView.setTranslationY(-AndroidUtilities.dp(48.0f));
        addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 52.0f, 0.0f, 0.0f));
        if (customTabs()) {
            i5 = -1;
        } else {
            FragmentContextView fragmentContextView = new FragmentContextView(context, baseFragment, this, false, resourcesProvider);
            this.fragmentContextView = fragmentContextView;
            addView(fragmentContextView, LayoutHelper.createFrame(-1, 38.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
            this.fragmentContextView.setDelegate(new FragmentContextView.FragmentContextViewDelegate() {
                @Override
                public final void onAnimation(boolean z2, boolean z3) {
                    SharedMediaLayout.this.lambda$new$12(z2, z3);
                }
            });
            addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 48, 51));
            Context context2 = getContext();
            BaseFragment baseFragment3 = this.profileActivity;
            SearchTagsList searchTagsList3 = new SearchTagsList(context2, baseFragment3, null, baseFragment3.getCurrentAccount(), includeSavedDialogs() ? 0L : this.dialog_id, resourcesProvider, false) {
                @Override
                protected boolean setFilter(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
                    SharedMediaLayout.this.searchingReaction = visibleReaction;
                    String obj = SharedMediaLayout.this.searchItem.getSearchField().getText().toString();
                    SharedMediaLayout.this.searchWas = (obj.length() == 0 && SharedMediaLayout.this.searchingReaction == null) ? false : true;
                    SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                    if (SharedMediaLayout.this.mediaPages[0].selectedType == 11) {
                        if (SharedMediaLayout.this.savedMessagesSearchAdapter != null) {
                            SharedMediaLayout.this.savedMessagesSearchAdapter.search(obj, SharedMediaLayout.this.searchingReaction);
                        }
                        AndroidUtilities.hideKeyboard(SharedMediaLayout.this.searchItem.getSearchField());
                    } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 12 && SharedMediaLayout.this.savedMessagesContainer != null) {
                        SharedMediaLayout.this.savedMessagesContainer.chatActivity.setTagFilter(visibleReaction);
                    }
                    return true;
                }

                @Override
                protected void onShownUpdate(boolean z2) {
                    SharedMediaLayout.this.scrollSlidingTextTabStrip.setAlpha(1.0f - this.shownT);
                    SharedMediaLayout.this.scrollSlidingTextTabStrip.setPivotX(r5.getWidth() / 2.0f);
                    SharedMediaLayout.this.scrollSlidingTextTabStrip.setScaleX(((1.0f - this.shownT) * 0.2f) + 0.8f);
                    SharedMediaLayout.this.scrollSlidingTextTabStrip.setPivotY(AndroidUtilities.dp(48.0f));
                    SharedMediaLayout.this.scrollSlidingTextTabStrip.setScaleY(((1.0f - this.shownT) * 0.2f) + 0.8f);
                }

                @Override
                public void updateTags(boolean z2) {
                    super.updateTags(z2);
                    show(SharedMediaLayout.this.searching && (SharedMediaLayout.this.getSelectedTab() == 11 || SharedMediaLayout.this.getSelectedTab() == 12) && SharedMediaLayout.this.searchTagsList.hasFilters());
                    ActionBarMenuItem actionBarMenuItem7 = SharedMediaLayout.this.searchItemIcon;
                    if (actionBarMenuItem7 != null) {
                        actionBarMenuItem7.setIcon((hasFilters() && SharedMediaLayout.this.profileActivity.getUserConfig().isPremium()) ? R.drawable.navbar_search_tag : R.drawable.ic_ab_search, z2);
                    }
                    if (SharedMediaLayout.this.searchItem != null) {
                        ActionBarMenuItem actionBarMenuItem8 = SharedMediaLayout.this.searchItem;
                        SearchTagsList searchTagsList4 = SharedMediaLayout.this.searchTagsList;
                        actionBarMenuItem8.setSearchFieldHint(LocaleController.getString((searchTagsList4 != null && searchTagsList4.hasFilters() && SharedMediaLayout.this.getSelectedTab() == 11) ? R.string.SavedTagSearchHint : R.string.Search));
                    }
                }
            };
            this.searchTagsList = searchTagsList3;
            searchTagsList3.setShown(0.0f);
            this.searchTagsList.attach();
            addView(this.searchTagsList, LayoutHelper.createFrame(-1, 40.0f, 51, 0.0f, 4.0f, 0.0f, 0.0f));
            i5 = -1;
            addView(this.actionModeLayout, LayoutHelper.createFrame(-1, 48, 51));
        }
        View view = new View(context);
        this.shadowLine = view;
        view.setBackgroundColor(getThemedColor(Theme.key_divider));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(i5, 1);
        layoutParams.topMargin = customTabs() ? 0 : AndroidUtilities.dp(48.0f) - 1;
        addView(this.shadowLine, layoutParams);
        updateTabs(false);
        switchToCurrentSelectedMode(false);
        if (this.hasMedia[0] >= 0) {
            loadFastScrollData(false);
        }
    }

    public class AnonymousClass4 extends ActionBarMenuItem.ActionBarMenuItemSearchListener {
        AnonymousClass4() {
        }

        @Override
        public void onSearchExpand() {
            SharedMediaLayout.this.searching = true;
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            SearchTagsList searchTagsList = sharedMediaLayout.searchTagsList;
            if (searchTagsList != null) {
                searchTagsList.show((sharedMediaLayout.getSelectedTab() == 11 || SharedMediaLayout.this.getSelectedTab() == 12) && SharedMediaLayout.this.searchTagsList.hasFilters());
            }
            ImageView imageView = SharedMediaLayout.this.photoVideoOptionsItem;
            if (imageView != null) {
                imageView.setVisibility(8);
            }
            ActionBarMenuItem actionBarMenuItem = SharedMediaLayout.this.searchItemIcon;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(8);
            }
            SharedMediaLayout.this.searchItem.setVisibility(8);
            SharedMediaLayout.this.onSearchStateChanged(true);
            if (SharedMediaLayout.this.optionsSearchImageView != null) {
                SharedMediaLayout.this.optionsSearchImageView.animate().scaleX(0.6f).scaleY(0.6f).alpha(0.0f).setDuration(320L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
            }
        }

        @Override
        public void onSearchCollapse() {
            SharedMediaLayout.this.searching = false;
            SharedMediaLayout.this.searchingReaction = null;
            ActionBarMenuItem actionBarMenuItem = SharedMediaLayout.this.searchItemIcon;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(0);
            }
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            if (sharedMediaLayout.photoVideoOptionsItem != null && sharedMediaLayout.getPhotoVideoOptionsAlpha(0.0f) > 0.5f) {
                SharedMediaLayout.this.photoVideoOptionsItem.setVisibility(0);
            }
            SearchTagsList searchTagsList = SharedMediaLayout.this.searchTagsList;
            if (searchTagsList != null) {
                searchTagsList.clear();
                SharedMediaLayout.this.searchTagsList.show(false);
            }
            if (SharedMediaLayout.this.savedMessagesContainer != null) {
                SharedMediaLayout.this.savedMessagesContainer.chatActivity.clearSearch();
            }
            SharedMediaLayout.this.searchWas = false;
            SharedMediaLayout.this.searchItem.setVisibility(0);
            SharedMediaLayout.this.documentsSearchAdapter.search(null, true);
            SharedMediaLayout.this.linksSearchAdapter.search(null, true);
            SharedMediaLayout.this.audioSearchAdapter.search(null, true);
            SharedMediaLayout.this.groupUsersSearchAdapter.search(null, true);
            if (SharedMediaLayout.this.savedMessagesSearchAdapter != null) {
                SharedMediaLayout.this.savedMessagesSearchAdapter.search(null, null);
            }
            SharedMediaLayout.this.onSearchStateChanged(false);
            if (SharedMediaLayout.this.optionsSearchImageView != null) {
                SharedMediaLayout.this.optionsSearchImageView.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(320L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
            }
            if (SharedMediaLayout.this.ignoreSearchCollapse) {
                SharedMediaLayout.this.ignoreSearchCollapse = false;
            } else {
                SharedMediaLayout.this.switchToCurrentSelectedMode(false);
            }
        }

        @Override
        public void onTextChanged(EditText editText) {
            String obj = editText.getText().toString();
            if (SharedMediaLayout.this.savedMessagesContainer != null) {
                SharedMediaLayout.this.savedMessagesContainer.chatActivity.setSearchQuery(obj);
                if (TextUtils.isEmpty(obj) && SharedMediaLayout.this.searchingReaction == null) {
                    SharedMediaLayout.this.savedMessagesContainer.chatActivity.clearSearch();
                }
            }
            SharedMediaLayout.this.searchItem.setVisibility(8);
            SharedMediaLayout.this.searchWas = (obj.length() == 0 && SharedMediaLayout.this.searchingReaction == null) ? false : true;
            SharedMediaLayout.this.post(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.AnonymousClass4.this.lambda$onTextChanged$0();
                }
            });
            if (SharedMediaLayout.this.mediaPages[0].selectedType == 1) {
                if (SharedMediaLayout.this.documentsSearchAdapter == null) {
                    return;
                }
                SharedMediaLayout.this.documentsSearchAdapter.search(obj, true);
                return;
            }
            if (SharedMediaLayout.this.mediaPages[0].selectedType == 3) {
                if (SharedMediaLayout.this.linksSearchAdapter == null) {
                    return;
                }
                SharedMediaLayout.this.linksSearchAdapter.search(obj, true);
            } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 4) {
                if (SharedMediaLayout.this.audioSearchAdapter == null) {
                    return;
                }
                SharedMediaLayout.this.audioSearchAdapter.search(obj, true);
            } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 7) {
                if (SharedMediaLayout.this.groupUsersSearchAdapter == null) {
                    return;
                }
                SharedMediaLayout.this.groupUsersSearchAdapter.search(obj, true);
            } else {
                if (SharedMediaLayout.this.mediaPages[0].selectedType != 11 || SharedMediaLayout.this.savedMessagesSearchAdapter == null) {
                    return;
                }
                SharedMediaLayout.this.savedMessagesSearchAdapter.search(obj, SharedMediaLayout.this.searchingReaction);
            }
        }

        public void lambda$onTextChanged$0() {
            SharedMediaLayout.this.switchToCurrentSelectedMode(false);
        }

        @Override
        public void onSearchPressed(EditText editText) {
            super.onSearchPressed(editText);
            if (SharedMediaLayout.this.savedMessagesContainer != null) {
                SharedMediaLayout.this.savedMessagesContainer.chatActivity.hitSearch();
            }
        }

        @Override
        public void onLayout(int i, int i2, int i3, int i4) {
            SharedMediaLayout.this.searchItem.setTranslationX(((View) SharedMediaLayout.this.searchItem.getParent()).getMeasuredWidth() - SharedMediaLayout.this.searchItem.getRight());
        }
    }

    public class AnonymousClass5 implements View.OnClickListener {
        final Context val$context;
        final Theme.ResourcesProvider val$resourcesProvider;

        AnonymousClass5(Theme.ResourcesProvider resourcesProvider, Context context) {
            this.val$resourcesProvider = resourcesProvider;
            this.val$context = context;
        }

        @Override
        public void onClick(View view) {
            TLRPC$Chat chat;
            TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
            final int closestTab = SharedMediaLayout.this.getClosestTab();
            boolean z = true;
            char c = (closestTab == 8 || closestTab == 9) ? (char) 1 : (char) 0;
            TLRPC$User user = MessagesController.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).getUser(Long.valueOf(SharedMediaLayout.this.dialog_id));
            if (closestTab == 13 && user != null && user.bot && user.bot_has_main_app && user.bot_can_edit && SharedMediaLayout.this.botPreviewsContainer != null) {
                ItemOptions.makeOptions(SharedMediaLayout.this.profileActivity, SharedMediaLayout.this.photoVideoOptionsItem).addIf(SharedMediaLayout.this.botPreviewsContainer.getItemsCount() < SharedMediaLayout.this.profileActivity.getMessagesController().botPreviewMediasMax, R.drawable.msg_addbot, LocaleController.getString(R.string.ProfileBotAddPreview), new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.AnonymousClass5.this.lambda$onClick$0();
                    }
                }).addIf(SharedMediaLayout.this.botPreviewsContainer.getItemsCount() > 1 && !SharedMediaLayout.this.botPreviewsContainer.isSelectedAll(), R.drawable.tabs_reorder, LocaleController.getString(R.string.ProfileBotReorder), new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.AnonymousClass5.this.lambda$onClick$1();
                    }
                }).addIf(SharedMediaLayout.this.botPreviewsContainer.getItemsCount() > 0, R.drawable.msg_select, LocaleController.getString(SharedMediaLayout.this.botPreviewsContainer.isSelectedAll() ? R.string.ProfileBotUnSelect : R.string.ProfileBotSelect), new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.AnonymousClass5.this.lambda$onClick$2();
                    }
                }).addIf(!TextUtils.isEmpty(SharedMediaLayout.this.botPreviewsContainer.getCurrentLang()), R.drawable.msg_delete, (CharSequence) LocaleController.formatString(R.string.ProfileBotRemoveLang, TranslateAlert2.languageName(SharedMediaLayout.this.botPreviewsContainer.getCurrentLang())), true, new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.AnonymousClass5.this.lambda$onClick$3();
                    }
                }).translate(0.0f, -AndroidUtilities.dp(52.0f)).setDimAlpha(0).show();
                return;
            }
            if (SharedMediaLayout.this.getSelectedTab() == 11) {
                ItemOptions add = ItemOptions.makeOptions(SharedMediaLayout.this.profileActivity, SharedMediaLayout.this.photoVideoOptionsItem).add(R.drawable.msg_discussion, LocaleController.getString(R.string.SavedViewAsMessages), new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.AnonymousClass5.this.lambda$onClick$4();
                    }
                }).addGap().add(R.drawable.msg_home, LocaleController.getString(R.string.AddShortcut), new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.AnonymousClass5.this.lambda$onClick$5();
                    }
                });
                int i = R.drawable.msg_delete;
                String string = LocaleController.getString(R.string.DeleteAll);
                final Theme.ResourcesProvider resourcesProvider = this.val$resourcesProvider;
                add.add(i, string, new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.AnonymousClass5.this.lambda$onClick$7(resourcesProvider);
                    }
                }).translate(0.0f, -AndroidUtilities.dp(52.0f)).setDimAlpha(0).show();
                return;
            }
            final DividerCell dividerCell = new DividerCell(this.val$context);
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.val$context, this.val$resourcesProvider) {
                @Override
                public void onMeasure(int i2, int i3) {
                    if (dividerCell.getParent() != null) {
                        dividerCell.setVisibility(8);
                        super.onMeasure(i2, i3);
                        dividerCell.getLayoutParams().width = getMeasuredWidth() - AndroidUtilities.dp(16.0f);
                        dividerCell.setVisibility(0);
                        super.onMeasure(i2, i3);
                        return;
                    }
                    super.onMeasure(i2, i3);
                }
            };
            SharedMediaLayout.this.mediaZoomInItem = new ActionBarMenuSubItem(this.val$context, true, false, this.val$resourcesProvider);
            SharedMediaLayout.this.mediaZoomOutItem = new ActionBarMenuSubItem(this.val$context, false, false, this.val$resourcesProvider);
            SharedMediaLayout.this.mediaZoomInItem.setTextAndIcon(LocaleController.getString("MediaZoomIn", R.string.MediaZoomIn), R.drawable.msg_zoomin);
            SharedMediaLayout.this.mediaZoomInItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    SharedMediaLayout.AnonymousClass5.this.lambda$onClick$8(view2);
                }
            });
            actionBarPopupWindowLayout.addView(SharedMediaLayout.this.mediaZoomInItem);
            SharedMediaLayout.this.mediaZoomOutItem.setTextAndIcon(LocaleController.getString("MediaZoomOut", R.string.MediaZoomOut), R.drawable.msg_zoomout);
            SharedMediaLayout.this.mediaZoomOutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    SharedMediaLayout.AnonymousClass5.this.lambda$onClick$9(view2);
                }
            });
            actionBarPopupWindowLayout.addView(SharedMediaLayout.this.mediaZoomOutItem);
            if (c == 0 || !SharedMediaLayout.this.allowStoriesSingleColumn) {
                if (SharedMediaLayout.this.mediaColumnsCount[c] == 2) {
                    SharedMediaLayout.this.mediaZoomInItem.setEnabled(false);
                    SharedMediaLayout.this.mediaZoomInItem.setAlpha(0.5f);
                } else if (SharedMediaLayout.this.mediaColumnsCount[c] == 9) {
                    SharedMediaLayout.this.mediaZoomOutItem.setEnabled(false);
                    SharedMediaLayout.this.mediaZoomOutItem.setAlpha(0.5f);
                }
            } else {
                SharedMediaLayout.this.mediaZoomInItem.setEnabled(false);
                SharedMediaLayout.this.mediaZoomInItem.setAlpha(0.5f);
                SharedMediaLayout.this.mediaZoomOutItem.setEnabled(false);
                SharedMediaLayout.this.mediaZoomOutItem.setAlpha(0.5f);
            }
            boolean z2 = (c == 0 && (!SharedMediaLayout.this.sharedMediaData[0].hasPhotos || !SharedMediaLayout.this.sharedMediaData[0].hasVideos) && SharedMediaLayout.this.sharedMediaData[0].endReached[0] && SharedMediaLayout.this.sharedMediaData[0].endReached[1] && SharedMediaLayout.this.sharedMediaData[0].startReached) ? false : true;
            if (!DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id) && (user == null || !user.bot)) {
                ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(this.val$context, false, false, this.val$resourcesProvider);
                actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("Calendar", R.string.Calendar), R.drawable.msg_calendar2);
                actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
                actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        SharedMediaLayout.this.showMediaCalendar(closestTab, false);
                        ActionBarPopupWindow actionBarPopupWindow = SharedMediaLayout.this.optionsWindow;
                        if (actionBarPopupWindow != null) {
                            actionBarPopupWindow.dismiss();
                        }
                    }
                });
                if (SharedMediaLayout.this.info != null && !SharedMediaLayout.this.isStoriesView() && (chat = MessagesController.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).getChat(Long.valueOf(SharedMediaLayout.this.info.id))) != null && (tLRPC$TL_chatAdminRights = chat.admin_rights) != null && tLRPC$TL_chatAdminRights.edit_stories) {
                    ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(this.val$context, false, true, this.val$resourcesProvider);
                    actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(R.string.OpenChannelArchiveStories), R.drawable.msg_archive);
                    actionBarMenuSubItem2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view2) {
                            SharedMediaLayout.AnonymousClass5.this.lambda$onClick$10(view2);
                        }
                    });
                    actionBarPopupWindowLayout.addView(actionBarMenuSubItem2);
                }
                if (z2) {
                    actionBarPopupWindowLayout.addView(dividerCell);
                    final ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(this.val$context, true, false, false, this.val$resourcesProvider);
                    final ActionBarMenuSubItem actionBarMenuSubItem4 = new ActionBarMenuSubItem(this.val$context, true, false, true, this.val$resourcesProvider);
                    actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString("MediaShowPhotos", R.string.MediaShowPhotos), 0);
                    actionBarPopupWindowLayout.addView(actionBarMenuSubItem3);
                    actionBarMenuSubItem4.setTextAndIcon(LocaleController.getString("MediaShowVideos", R.string.MediaShowVideos), 0);
                    actionBarPopupWindowLayout.addView(actionBarMenuSubItem4);
                    if (c != 0) {
                        final StoriesAdapter storiesAdapter = closestTab == 8 ? SharedMediaLayout.this.storiesAdapter : SharedMediaLayout.this.archivedStoriesAdapter;
                        StoriesController.StoriesList storiesList = storiesAdapter.storiesList;
                        if (storiesList != null) {
                            actionBarMenuSubItem3.setChecked(storiesList.showPhotos());
                            actionBarMenuSubItem4.setChecked(storiesAdapter.storiesList.showVideos());
                        }
                        actionBarMenuSubItem3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public final void onClick(View view2) {
                                SharedMediaLayout.AnonymousClass5.this.lambda$onClick$11(actionBarMenuSubItem4, actionBarMenuSubItem3, storiesAdapter, view2);
                            }
                        });
                        actionBarMenuSubItem4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public final void onClick(View view2) {
                                SharedMediaLayout.AnonymousClass5.this.lambda$onClick$12(actionBarMenuSubItem3, actionBarMenuSubItem4, storiesAdapter, view2);
                            }
                        });
                    } else {
                        actionBarMenuSubItem3.setChecked(SharedMediaLayout.this.sharedMediaData[0].filterType == 0 || SharedMediaLayout.this.sharedMediaData[0].filterType == 1);
                        actionBarMenuSubItem3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view2) {
                                if (SharedMediaLayout.this.changeTypeAnimation) {
                                    return;
                                }
                                if (!actionBarMenuSubItem4.getCheckView().isChecked() && actionBarMenuSubItem3.getCheckView().isChecked()) {
                                    ActionBarMenuSubItem actionBarMenuSubItem5 = actionBarMenuSubItem3;
                                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                    AndroidUtilities.shakeViewSpring(actionBarMenuSubItem5, sharedMediaLayout.shiftDp = -sharedMediaLayout.shiftDp);
                                    return;
                                }
                                actionBarMenuSubItem3.setChecked(!r3.getCheckView().isChecked());
                                if (!actionBarMenuSubItem3.getCheckView().isChecked() || !actionBarMenuSubItem4.getCheckView().isChecked()) {
                                    SharedMediaLayout.this.sharedMediaData[0].filterType = 2;
                                } else {
                                    SharedMediaLayout.this.sharedMediaData[0].filterType = 0;
                                }
                                SharedMediaLayout.this.changeMediaFilterType();
                            }
                        });
                        if (SharedMediaLayout.this.sharedMediaData[0].filterType != 0 && SharedMediaLayout.this.sharedMediaData[0].filterType != 2) {
                            z = false;
                        }
                        actionBarMenuSubItem4.setChecked(z);
                        actionBarMenuSubItem4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view2) {
                                if (SharedMediaLayout.this.changeTypeAnimation) {
                                    return;
                                }
                                if (!actionBarMenuSubItem3.getCheckView().isChecked() && actionBarMenuSubItem4.getCheckView().isChecked()) {
                                    ActionBarMenuSubItem actionBarMenuSubItem5 = actionBarMenuSubItem4;
                                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                    AndroidUtilities.shakeViewSpring(actionBarMenuSubItem5, sharedMediaLayout.shiftDp = -sharedMediaLayout.shiftDp);
                                    return;
                                }
                                actionBarMenuSubItem4.setChecked(!r3.getCheckView().isChecked());
                                if (!actionBarMenuSubItem3.getCheckView().isChecked() || !actionBarMenuSubItem4.getCheckView().isChecked()) {
                                    SharedMediaLayout.this.sharedMediaData[0].filterType = 1;
                                } else {
                                    SharedMediaLayout.this.sharedMediaData[0].filterType = 0;
                                }
                                SharedMediaLayout.this.changeMediaFilterType();
                            }
                        });
                    }
                }
            }
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            sharedMediaLayout.optionsWindow = AlertsCreator.showPopupMenu(actionBarPopupWindowLayout, sharedMediaLayout.photoVideoOptionsItem, 0, -AndroidUtilities.dp(56.0f));
        }

        public void lambda$onClick$0() {
            StoryRecorder.getInstance(SharedMediaLayout.this.profileActivity.getParentActivity(), SharedMediaLayout.this.profileActivity.getCurrentAccount()).openBot(SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.botPreviewsContainer.getCurrentLang(), null);
        }

        public void lambda$onClick$1() {
            SharedMediaLayout.this.botPreviewsContainer.selectAll();
        }

        public void lambda$onClick$2() {
            if (SharedMediaLayout.this.botPreviewsContainer.isSelectedAll()) {
                SharedMediaLayout.this.botPreviewsContainer.unselectAll();
            } else {
                SharedMediaLayout.this.botPreviewsContainer.selectAll();
            }
        }

        public void lambda$onClick$3() {
            SharedMediaLayout.this.botPreviewsContainer.deleteLang(SharedMediaLayout.this.botPreviewsContainer.getCurrentLang());
        }

        public void lambda$onClick$4() {
            SharedMediaLayout.this.profileActivity.getMessagesController().setSavedViewAs(false);
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", SharedMediaLayout.this.profileActivity.getUserConfig().getClientUserId());
            SharedMediaLayout.this.profileActivity.presentFragment(new ChatActivity(bundle), true);
        }

        public void lambda$onClick$5() {
            try {
                SharedMediaLayout.this.profileActivity.getMediaDataController().installShortcut(SharedMediaLayout.this.profileActivity.getUserConfig().getClientUserId(), MediaDataController.SHORTCUT_TYPE_USER_OR_CHAT);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void lambda$onClick$7(Theme.ResourcesProvider resourcesProvider) {
            final TLRPC$User currentUser = SharedMediaLayout.this.profileActivity.getUserConfig().getCurrentUser();
            AlertsCreator.createClearOrDeleteDialogAlert(SharedMediaLayout.this.profileActivity, false, null, currentUser, false, true, true, new MessagesStorage.BooleanCallback() {
                @Override
                public final void run(boolean z) {
                    SharedMediaLayout.AnonymousClass5.this.lambda$onClick$6(currentUser, z);
                }
            }, resourcesProvider);
        }

        public void lambda$onClick$6(TLRPC$User tLRPC$User, boolean z) {
            SharedMediaLayout.this.profileActivity.lambda$onBackPressed$308();
            if (SharedMediaLayout.this.profileActivity instanceof NotificationCenter.NotificationCenterDelegate) {
                SharedMediaLayout.this.profileActivity.getNotificationCenter().removeObserver((NotificationCenter.NotificationCenterDelegate) SharedMediaLayout.this.profileActivity, NotificationCenter.closeChats);
            }
            SharedMediaLayout.this.profileActivity.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            SharedMediaLayout.this.profileActivity.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needDeleteDialog, Long.valueOf(SharedMediaLayout.this.dialog_id), tLRPC$User, null, Boolean.valueOf(z));
            SharedMediaLayout.this.profileActivity.getMessagesController().setSavedViewAs(false);
        }

        public void lambda$onClick$8(View view) {
            SharedMediaLayout.this.zoomIn();
        }

        public void lambda$onClick$9(View view) {
            SharedMediaLayout.this.zoomOut();
        }

        public void lambda$onClick$10(View view) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", 2);
            bundle.putLong("dialog_id", -SharedMediaLayout.this.info.id);
            MediaActivity mediaActivity = new MediaActivity(bundle, null);
            mediaActivity.setChatInfo(SharedMediaLayout.this.info);
            SharedMediaLayout.this.profileActivity.presentFragment(mediaActivity);
            ActionBarPopupWindow actionBarPopupWindow = SharedMediaLayout.this.optionsWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
            }
        }

        public void lambda$onClick$11(ActionBarMenuSubItem actionBarMenuSubItem, ActionBarMenuSubItem actionBarMenuSubItem2, StoriesAdapter storiesAdapter, View view) {
            if (SharedMediaLayout.this.changeTypeAnimation) {
                return;
            }
            if (!actionBarMenuSubItem.getCheckView().isChecked() && actionBarMenuSubItem2.getCheckView().isChecked()) {
                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                AndroidUtilities.shakeViewSpring(view, sharedMediaLayout.shiftDp = -sharedMediaLayout.shiftDp);
                return;
            }
            actionBarMenuSubItem2.getCheckView().setChecked(!actionBarMenuSubItem2.getCheckView().isChecked(), true);
            StoriesController.StoriesList storiesList = storiesAdapter.storiesList;
            if (storiesList == null) {
                return;
            }
            storiesList.updateFilters(actionBarMenuSubItem2.getCheckView().isChecked(), actionBarMenuSubItem.getCheckView().isChecked());
        }

        public void lambda$onClick$12(ActionBarMenuSubItem actionBarMenuSubItem, ActionBarMenuSubItem actionBarMenuSubItem2, StoriesAdapter storiesAdapter, View view) {
            if (SharedMediaLayout.this.changeTypeAnimation) {
                return;
            }
            if (!actionBarMenuSubItem.getCheckView().isChecked() && actionBarMenuSubItem2.getCheckView().isChecked()) {
                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                AndroidUtilities.shakeViewSpring(view, sharedMediaLayout.shiftDp = -sharedMediaLayout.shiftDp);
                return;
            }
            actionBarMenuSubItem2.getCheckView().setChecked(!actionBarMenuSubItem2.getCheckView().isChecked(), true);
            StoriesController.StoriesList storiesList = storiesAdapter.storiesList;
            if (storiesList == null) {
                return;
            }
            storiesList.updateFilters(actionBarMenuSubItem.getCheckView().isChecked(), actionBarMenuSubItem2.getCheckView().isChecked());
        }
    }

    public void lambda$new$3(View view) {
        closeActionMode();
    }

    public void lambda$new$4(View view) {
        onActionBarItemClick(view, 102);
    }

    public void lambda$new$5(View view) {
        onActionBarItemClick(view, 100);
    }

    public void lambda$new$6(View view) {
        onActionBarItemClick(view, 103);
    }

    public void lambda$new$7(View view) {
        onActionBarItemClick(view, 104);
    }

    public void lambda$new$8(View view) {
        onActionBarItemClick(view, 101);
    }

    public class AnonymousClass11 extends BotPreviewsEditContainer {
        AnonymousClass11(Context context, BaseFragment baseFragment, long j) {
            super(context, baseFragment, j);
        }

        @Override
        public void onSelectedTabChanged() {
            SharedMediaLayout.this.onSelectedTabChanged();
        }

        @Override
        protected boolean isSelected(MessageObject messageObject) {
            return SharedMediaLayout.this.selectedFiles[(messageObject.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0;
        }

        @Override
        protected boolean select(MessageObject messageObject) {
            if (messageObject == null) {
                return false;
            }
            char c = messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1;
            if (SharedMediaLayout.this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0 || SharedMediaLayout.this.selectedFiles[0].size() + SharedMediaLayout.this.selectedFiles[1].size() >= 100) {
                return false;
            }
            SharedMediaLayout.this.selectedFiles[c].put(messageObject.getId(), messageObject);
            if (!messageObject.canDeleteMessage(false, null)) {
                SharedMediaLayout.access$5008(SharedMediaLayout.this);
            }
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            if (!sharedMediaLayout.isActionModeShowed) {
                AndroidUtilities.hideKeyboard(sharedMediaLayout.profileActivity.getParentActivity().getCurrentFocus());
                int i = 8;
                SharedMediaLayout.this.deleteItem.setVisibility(SharedMediaLayout.this.cantDeleteMessagesCount == 0 ? 0 : 8);
                if (SharedMediaLayout.this.gotoItem != null) {
                    SharedMediaLayout.this.gotoItem.setVisibility((SharedMediaLayout.this.getClosestTab() == 8 || SharedMediaLayout.this.getClosestTab() == 13) ? 8 : 0);
                }
                if (SharedMediaLayout.this.pinItem != null) {
                    SharedMediaLayout.this.pinItem.setVisibility(8);
                }
                if (SharedMediaLayout.this.unpinItem != null) {
                    SharedMediaLayout.this.unpinItem.setVisibility(8);
                }
                if (SharedMediaLayout.this.forwardItem != null) {
                    ActionBarMenuItem actionBarMenuItem = SharedMediaLayout.this.forwardItem;
                    if (SharedMediaLayout.this.getClosestTab() != 8 && SharedMediaLayout.this.getClosestTab() != 13) {
                        i = 0;
                    }
                    actionBarMenuItem.setVisibility(i);
                }
                SharedMediaLayout.this.selectedMessagesCountTextView.setNumber(SharedMediaLayout.this.selectedFiles[0].size() + SharedMediaLayout.this.selectedFiles[1].size(), false);
                AnimatorSet animatorSet = new AnimatorSet();
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < SharedMediaLayout.this.actionModeViews.size(); i2++) {
                    View view = (View) SharedMediaLayout.this.actionModeViews.get(i2);
                    AndroidUtilities.clearDrawableAnimation(view);
                    arrayList.add(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_Y, 0.1f, 1.0f));
                }
                animatorSet.playTogether(arrayList);
                animatorSet.setDuration(250L);
                animatorSet.start();
                SharedMediaLayout.this.scrolling = false;
                SharedMediaLayout.this.showActionMode(true);
            } else {
                sharedMediaLayout.selectedMessagesCountTextView.setNumber(SharedMediaLayout.this.selectedFiles[0].size() + SharedMediaLayout.this.selectedFiles[1].size(), true);
            }
            updateSelection(true);
            return true;
        }

        @Override
        protected boolean unselect(MessageObject messageObject) {
            if (messageObject == null) {
                return false;
            }
            char c = messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1;
            if (SharedMediaLayout.this.selectedFiles[c].indexOfKey(messageObject.getId()) < 0) {
                return false;
            }
            SharedMediaLayout.this.selectedFiles[c].remove(messageObject.getId());
            if (!messageObject.canDeleteMessage(false, null)) {
                SharedMediaLayout.access$5010(SharedMediaLayout.this);
            }
            if (SharedMediaLayout.this.selectedFiles[0].size() != 0 || SharedMediaLayout.this.selectedFiles[1].size() != 0) {
                SharedMediaLayout.this.selectedMessagesCountTextView.setNumber(SharedMediaLayout.this.selectedFiles[0].size() + SharedMediaLayout.this.selectedFiles[1].size(), true);
            } else {
                AndroidUtilities.hideKeyboard(SharedMediaLayout.this.profileActivity.getParentActivity().getCurrentFocus());
                SharedMediaLayout.this.selectedFiles[0].clear();
                SharedMediaLayout.this.selectedFiles[1].clear();
                int i = 8;
                SharedMediaLayout.this.deleteItem.setVisibility(SharedMediaLayout.this.cantDeleteMessagesCount == 0 ? 0 : 8);
                if (SharedMediaLayout.this.gotoItem != null) {
                    SharedMediaLayout.this.gotoItem.setVisibility((SharedMediaLayout.this.getClosestTab() == 8 || SharedMediaLayout.this.getClosestTab() == 13) ? 8 : 0);
                }
                if (SharedMediaLayout.this.pinItem != null) {
                    SharedMediaLayout.this.pinItem.setVisibility(8);
                }
                if (SharedMediaLayout.this.unpinItem != null) {
                    SharedMediaLayout.this.unpinItem.setVisibility(8);
                }
                if (SharedMediaLayout.this.forwardItem != null) {
                    ActionBarMenuItem actionBarMenuItem = SharedMediaLayout.this.forwardItem;
                    if (SharedMediaLayout.this.getClosestTab() != 8 && SharedMediaLayout.this.getClosestTab() != 13) {
                        i = 0;
                    }
                    actionBarMenuItem.setVisibility(i);
                }
                AnimatorSet animatorSet = new AnimatorSet();
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < SharedMediaLayout.this.actionModeViews.size(); i2++) {
                    View view = (View) SharedMediaLayout.this.actionModeViews.get(i2);
                    AndroidUtilities.clearDrawableAnimation(view);
                    arrayList.add(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_Y, 1.0f, 0.1f));
                }
                animatorSet.playTogether(arrayList);
                animatorSet.setDuration(250L);
                animatorSet.start();
                SharedMediaLayout.this.scrolling = false;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.AnonymousClass11.this.lambda$unselect$0();
                    }
                }, 20L);
            }
            updateSelection(true);
            return true;
        }

        public void lambda$unselect$0() {
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            if (sharedMediaLayout.isActionModeShowed) {
                sharedMediaLayout.showActionMode(false);
            }
        }

        @Override
        protected boolean isActionModeShowed() {
            return SharedMediaLayout.this.isActionModeShowed;
        }

        @Override
        public int getStartedTrackingX() {
            return SharedMediaLayout.this.startedTrackingX;
        }
    }

    public void lambda$new$9(View view) {
        if (view.getAlpha() < 0.5f) {
            return;
        }
        this.profileActivity.getMessagesController().getMainSettings().edit().putBoolean("story_keep", true).apply();
        openStoryRecorder();
    }

    public void lambda$new$10(MediaPage mediaPage, View view, int i, float f, float f2) {
        long j;
        if (mediaPage.selectedType != 7) {
            if (mediaPage.selectedType != 6 || !(view instanceof ProfileSearchCell)) {
                if (mediaPage.selectedType != 1 || !(view instanceof SharedDocumentCell)) {
                    if (mediaPage.selectedType != 3 || !(view instanceof SharedLinkCell)) {
                        if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
                            onItemClick(i, view, ((SharedAudioCell) view).getMessage(), 0, mediaPage.selectedType);
                            return;
                        }
                        if (mediaPage.selectedType != 5 || !(view instanceof ContextLinkCell)) {
                            if (mediaPage.selectedType != 0 || !(view instanceof SharedPhotoVideoCell2)) {
                                if ((mediaPage.selectedType != 8 && mediaPage.selectedType != 9) || !(view instanceof SharedPhotoVideoCell2)) {
                                    if (mediaPage.selectedType != 10) {
                                        if (mediaPage.selectedType == 11) {
                                            RecyclerView.Adapter adapter = mediaPage.listView.getAdapter();
                                            SavedMessagesSearchAdapter savedMessagesSearchAdapter = this.savedMessagesSearchAdapter;
                                            if (adapter != savedMessagesSearchAdapter) {
                                                if (this.isActionModeShowed) {
                                                    if (this.savedDialogsAdapter.itemTouchHelper.isIdle()) {
                                                        this.savedDialogsAdapter.select(view);
                                                        return;
                                                    }
                                                    return;
                                                }
                                                Bundle bundle = new Bundle();
                                                if (i < 0 || i >= this.savedDialogsAdapter.dialogs.size()) {
                                                    return;
                                                }
                                                SavedMessagesController.SavedDialog savedDialog = (SavedMessagesController.SavedDialog) this.savedDialogsAdapter.dialogs.get(i);
                                                bundle.putLong("user_id", this.profileActivity.getUserConfig().getClientUserId());
                                                bundle.putInt("chatMode", 3);
                                                ChatActivity chatActivity = new ChatActivity(bundle);
                                                chatActivity.setSavedDialog(savedDialog.dialogId);
                                                this.profileActivity.presentFragment(chatActivity);
                                                return;
                                            }
                                            if (i < 0) {
                                                return;
                                            }
                                            if (i < savedMessagesSearchAdapter.dialogs.size()) {
                                                SavedMessagesController.SavedDialog savedDialog2 = this.savedMessagesSearchAdapter.dialogs.get(i);
                                                Bundle bundle2 = new Bundle();
                                                bundle2.putLong("user_id", this.profileActivity.getUserConfig().getClientUserId());
                                                bundle2.putInt("chatMode", 3);
                                                ChatActivity chatActivity2 = new ChatActivity(bundle2);
                                                chatActivity2.setSavedDialog(savedDialog2.dialogId);
                                                this.profileActivity.presentFragment(chatActivity2);
                                                return;
                                            }
                                            final int size = i - this.savedMessagesSearchAdapter.dialogs.size();
                                            if (size < this.savedMessagesSearchAdapter.messages.size()) {
                                                MessageObject messageObject = this.savedMessagesSearchAdapter.messages.get(size);
                                                Bundle bundle3 = new Bundle();
                                                bundle3.putLong("user_id", this.profileActivity.getUserConfig().getClientUserId());
                                                bundle3.putInt("message_id", messageObject.getId());
                                                ChatActivity chatActivity3 = new ChatActivity(bundle3) {
                                                    boolean firstCreateView = true;

                                                    @Override
                                                    public void onTransitionAnimationStart(boolean z, boolean z2) {
                                                        if (this.firstCreateView) {
                                                            if (this.searchItem != null) {
                                                                lambda$openSearchWithText$311("");
                                                                this.searchItem.setSearchFieldText(SharedMediaLayout.this.savedMessagesSearchAdapter.lastQuery, false);
                                                            }
                                                            SearchTagsList searchTagsList = this.actionBarSearchTags;
                                                            if (searchTagsList != null) {
                                                                searchTagsList.setChosen(SharedMediaLayout.this.savedMessagesSearchAdapter.lastReaction, false);
                                                            }
                                                            SharedMediaLayout.this.profileActivity.getMediaDataController().portSavedSearchResults(getClassGuid(), SharedMediaLayout.this.savedMessagesSearchAdapter.lastReaction, SharedMediaLayout.this.savedMessagesSearchAdapter.lastQuery, SharedMediaLayout.this.savedMessagesSearchAdapter.cachedMessages, SharedMediaLayout.this.savedMessagesSearchAdapter.loadedMessages, size, SharedMediaLayout.this.savedMessagesSearchAdapter.count, SharedMediaLayout.this.savedMessagesSearchAdapter.endReached);
                                                            this.firstCreateView = false;
                                                        }
                                                        super.onTransitionAnimationStart(z, z2);
                                                    }
                                                };
                                                chatActivity3.setHighlightMessageId(messageObject.getId());
                                                this.profileActivity.presentFragment(chatActivity3);
                                                return;
                                            }
                                            return;
                                        }
                                        return;
                                    }
                                    if (((view instanceof ProfileSearchCell) || f2 < AndroidUtilities.dp(60.0f)) && i >= 0 && i < this.channelRecommendationsAdapter.chats.size()) {
                                        Bundle bundle4 = new Bundle();
                                        bundle4.putLong("chat_id", ((TLRPC$Chat) this.channelRecommendationsAdapter.chats.get(i)).id);
                                        this.profileActivity.presentFragment(new ChatActivity(bundle4));
                                        return;
                                    }
                                    return;
                                }
                                MessageObject messageObject2 = ((SharedPhotoVideoCell2) view).getMessageObject();
                                if (messageObject2 != null) {
                                    onItemClick(i, view, messageObject2, 0, mediaPage.selectedType);
                                    return;
                                }
                                return;
                            }
                            SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) view;
                            if (sharedPhotoVideoCell2.canRevealSpoiler()) {
                                sharedPhotoVideoCell2.startRevealMedia(f, f2);
                                return;
                            }
                            MessageObject messageObject3 = sharedPhotoVideoCell2.getMessageObject();
                            if (messageObject3 != null) {
                                onItemClick(i, view, messageObject3, 0, mediaPage.selectedType);
                                return;
                            }
                            return;
                        }
                        onItemClick(i, view, (MessageObject) ((ContextLinkCell) view).getParentObject(), 0, mediaPage.selectedType);
                        return;
                    }
                    onItemClick(i, view, ((SharedLinkCell) view).getMessage(), 0, mediaPage.selectedType);
                    return;
                }
                onItemClick(i, view, ((SharedDocumentCell) view).getMessage(), 0, mediaPage.selectedType);
                return;
            }
            TLRPC$Chat chat = ((ProfileSearchCell) view).getChat();
            Bundle bundle5 = new Bundle();
            bundle5.putLong("chat_id", chat.id);
            if (this.profileActivity.getMessagesController().checkCanOpenChat(bundle5, this.profileActivity)) {
                if (chat.forum) {
                    BaseFragment baseFragment = this.profileActivity;
                    baseFragment.presentFragment(TopicsFragment.getTopicsOrChat(baseFragment, bundle5));
                    return;
                } else {
                    this.profileActivity.presentFragment(new ChatActivity(bundle5));
                    return;
                }
            }
            return;
        }
        if (!(view instanceof UserCell)) {
            RecyclerView.Adapter adapter2 = mediaPage.listView.getAdapter();
            GroupUsersSearchAdapter groupUsersSearchAdapter = this.groupUsersSearchAdapter;
            if (adapter2 == groupUsersSearchAdapter) {
                TLObject item = groupUsersSearchAdapter.getItem(i);
                if (item instanceof TLRPC$ChannelParticipant) {
                    j = MessageObject.getPeerId(((TLRPC$ChannelParticipant) item).peer);
                } else if (!(item instanceof TLRPC$ChatParticipant)) {
                    return;
                } else {
                    j = ((TLRPC$ChatParticipant) item).user_id;
                }
                if (j == 0 || j == this.profileActivity.getUserConfig().getClientUserId()) {
                    return;
                }
                Bundle bundle6 = new Bundle();
                bundle6.putLong("user_id", j);
                this.profileActivity.presentFragment(new ProfileActivity(bundle6));
                return;
            }
            return;
        }
        if (!this.chatUsersAdapter.sortedUsers.isEmpty()) {
            i = ((Integer) this.chatUsersAdapter.sortedUsers.get(i)).intValue();
        }
        TLRPC$ChatParticipant tLRPC$ChatParticipant = this.chatUsersAdapter.chatInfo.participants.participants.get(i);
        if (i < 0 || i >= this.chatUsersAdapter.chatInfo.participants.participants.size()) {
            return;
        }
        onMemberClick(tLRPC$ChatParticipant, false, view);
    }

    public void lambda$new$12(boolean z, boolean z2) {
        if (z) {
            return;
        }
        requestLayout();
    }

    protected boolean isBot() {
        TLRPC$User user;
        return this.dialog_id > 0 && (user = MessagesController.getInstance(this.profileActivity.getCurrentAccount()).getUser(Long.valueOf(this.dialog_id))) != null && user.bot;
    }

    public void setStoriesFilter(boolean z, boolean z2) {
        StoriesController.StoriesList storiesList;
        StoriesController.StoriesList storiesList2;
        StoriesAdapter storiesAdapter = this.storiesAdapter;
        if (storiesAdapter != null && (storiesList2 = storiesAdapter.storiesList) != null) {
            storiesList2.updateFilters(z, z2);
        }
        StoriesAdapter storiesAdapter2 = this.archivedStoriesAdapter;
        if (storiesAdapter2 == null || (storiesList = storiesAdapter2.storiesList) == null) {
            return;
        }
        storiesList.updateFilters(z, z2);
    }

    public int mediaPageTopMargin() {
        return customTabs() ? 0 : 48;
    }

    public void setForwardRestrictedHint(HintView hintView) {
        this.fwdRestrictedHint = hintView;
    }

    public static int getMessageId(View view) {
        if (view instanceof SharedPhotoVideoCell2) {
            return ((SharedPhotoVideoCell2) view).getMessageId();
        }
        if (view instanceof SharedDocumentCell) {
            return ((SharedDocumentCell) view).getMessage().getId();
        }
        if (view instanceof SharedAudioCell) {
            return ((SharedAudioCell) view).getMessage().getId();
        }
        return 0;
    }

    private void updateForwardItem() {
        if (this.forwardItem == null) {
            return;
        }
        boolean z = this.profileActivity.getMessagesController().isChatNoForwards(-this.dialog_id) || hasNoforwardsMessage();
        this.forwardItem.setAlpha(z ? 0.5f : 1.0f);
        if (z && this.forwardItem.getBackground() != null) {
            this.forwardItem.setBackground(null);
        } else {
            if (z || this.forwardItem.getBackground() != null) {
                return;
            }
            this.forwardItem.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_actionBarActionModeDefaultSelector), 5));
        }
    }

    private boolean hasNoforwardsMessage() {
        MessageObject messageObject;
        TLRPC$Message tLRPC$Message;
        boolean z = false;
        for (int i = 1; i >= 0; i--) {
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < this.selectedFiles[i].size(); i2++) {
                arrayList.add(Integer.valueOf(this.selectedFiles[i].keyAt(i2)));
            }
            Iterator it = arrayList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Integer num = (Integer) it.next();
                if (num.intValue() > 0 && (messageObject = this.selectedFiles[i].get(num.intValue())) != null && (tLRPC$Message = messageObject.messageOwner) != null && tLRPC$Message.noforwards) {
                    z = true;
                    break;
                }
            }
            if (z) {
                break;
            }
        }
        return z;
    }

    public void changeMediaFilterType() {
        final Bitmap bitmap;
        final MediaPage mediaPage = getMediaPage(0);
        if (mediaPage != null && mediaPage.getMeasuredHeight() > 0 && mediaPage.getMeasuredWidth() > 0) {
            try {
                bitmap = Bitmap.createBitmap(mediaPage.getMeasuredWidth(), mediaPage.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            } catch (Exception e) {
                FileLog.e(e);
                bitmap = null;
            }
            if (bitmap != null) {
                this.changeTypeAnimation = true;
                mediaPage.listView.draw(new Canvas(bitmap));
                final View view = new View(mediaPage.getContext());
                view.setBackground(new BitmapDrawable(bitmap));
                mediaPage.addView(view);
                view.animate().alpha(0.0f).setDuration(200L).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        SharedMediaLayout.this.changeTypeAnimation = false;
                        if (view.getParent() != null) {
                            mediaPage.removeView(view);
                            bitmap.recycle();
                        }
                    }
                }).start();
                mediaPage.listView.setAlpha(0.0f);
                mediaPage.listView.animate().alpha(1.0f).setDuration(200L).start();
            }
        }
        int[] lastMediaCount = this.sharedMediaPreloader.getLastMediaCount();
        ArrayList<MessageObject> arrayList = this.sharedMediaPreloader.getSharedMediaData()[0].messages;
        SharedMediaData sharedMediaData = this.sharedMediaData[0];
        int i = sharedMediaData.filterType;
        if (i == 0) {
            sharedMediaData.setTotalCount(lastMediaCount[0]);
        } else if (i == 1) {
            sharedMediaData.setTotalCount(lastMediaCount[6]);
        } else {
            sharedMediaData.setTotalCount(lastMediaCount[7]);
        }
        this.sharedMediaData[0].fastScrollDataLoaded = false;
        jumpToDate(0, DialogObject.isEncryptedDialog(this.dialog_id) ? Integer.MIN_VALUE : Integer.MAX_VALUE, 0, true);
        loadFastScrollData(false);
        this.delegate.updateSelectedMediaTabText();
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(this.dialog_id);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            MessageObject messageObject = arrayList.get(i2);
            SharedMediaData sharedMediaData2 = this.sharedMediaData[0];
            int i3 = sharedMediaData2.filterType;
            if (i3 == 0) {
                sharedMediaData2.addMessage(messageObject, 0, false, isEncryptedDialog);
            } else if (i3 == 1) {
                if (messageObject.isPhoto()) {
                    this.sharedMediaData[0].addMessage(messageObject, 0, false, isEncryptedDialog);
                }
            } else if (!messageObject.isPhoto()) {
                this.sharedMediaData[0].addMessage(messageObject, 0, false, isEncryptedDialog);
            }
        }
    }

    public MediaPage getMediaPage(int i) {
        int i2 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i2 >= mediaPageArr.length) {
                return null;
            }
            MediaPage mediaPage = mediaPageArr[i2];
            if (mediaPage != null && mediaPage.selectedType == i) {
                return this.mediaPages[i2];
            }
            i2++;
        }
    }

    public void showMediaCalendar(int r10, boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.showMediaCalendar(int, boolean):void");
    }

    private void startPinchToMediaColumnsCount(boolean z) {
        int i;
        final MediaPage mediaPage;
        int i2;
        if (this.photoVideoChangeColumnsAnimation) {
            return;
        }
        int i3 = 0;
        int i4 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            i = 8;
            if (i4 >= mediaPageArr.length) {
                mediaPage = null;
                break;
            } else if (mediaPageArr[i4].selectedType == 0 || this.mediaPages[i4].selectedType == 8 || this.mediaPages[i4].selectedType == 9) {
                break;
            } else {
                i4++;
            }
        }
        if (mediaPage == null) {
            return;
        }
        int i5 = mediaPage.selectedType;
        this.changeColumnsTab = i5;
        int i6 = (i5 == 8 || i5 == 9) ? 1 : 0;
        int nextMediaColumnsCount = getNextMediaColumnsCount(i6, this.mediaColumnsCount[i6], z);
        this.animateToColumnsCount = nextMediaColumnsCount;
        if (nextMediaColumnsCount == this.mediaColumnsCount[i6]) {
            return;
        }
        if (this.allowStoriesSingleColumn && ((i2 = this.changeColumnsTab) == 8 || i2 == 9)) {
            return;
        }
        mediaPage.animationSupportingListView.setVisibility(0);
        int i7 = this.changeColumnsTab;
        if (i7 == 8) {
            mediaPage.animationSupportingListView.setAdapter(this.animationSupportingStoriesAdapter);
        } else if (i7 == 9) {
            mediaPage.animationSupportingListView.setAdapter(this.animationSupportingArchivedStoriesAdapter);
        } else {
            mediaPage.animationSupportingListView.setAdapter(this.animationSupportingPhotoVideoAdapter);
        }
        mediaPage.animationSupportingListView.setPadding(mediaPage.animationSupportingListView.getPaddingLeft(), this.changeColumnsTab == 9 ? AndroidUtilities.dp(64.0f) : 0, mediaPage.animationSupportingListView.getPaddingRight(), isStoriesView() ? AndroidUtilities.dp(72.0f) : 0);
        ButtonWithCounterView buttonWithCounterView = mediaPage.buttonView;
        if (this.changeColumnsTab == 8 && isStoriesView()) {
            i = 0;
        }
        buttonWithCounterView.setVisibility(i);
        mediaPage.animationSupportingLayoutManager.setSpanCount(nextMediaColumnsCount);
        mediaPage.animationSupportingListView.invalidateItemDecorations();
        mediaPage.animationSupportingLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i8) {
                if (mediaPage.animationSupportingListView.getAdapter() == SharedMediaLayout.this.animationSupportingPhotoVideoAdapter) {
                    if (SharedMediaLayout.this.animationSupportingPhotoVideoAdapter.getItemViewType(i8) == 2) {
                        return mediaPage.animationSupportingLayoutManager.getSpanCount();
                    }
                    return 1;
                }
                if (mediaPage.animationSupportingListView.getAdapter() == SharedMediaLayout.this.animationSupportingStoriesAdapter) {
                    if (SharedMediaLayout.this.animationSupportingStoriesAdapter.getItemViewType(i8) == 2) {
                        return mediaPage.animationSupportingLayoutManager.getSpanCount();
                    }
                    return 1;
                }
                if (mediaPage.animationSupportingListView.getAdapter() == SharedMediaLayout.this.animationSupportingArchivedStoriesAdapter && SharedMediaLayout.this.animationSupportingArchivedStoriesAdapter.getItemViewType(i8) == 2) {
                    return mediaPage.animationSupportingLayoutManager.getSpanCount();
                }
                return 1;
            }
        });
        AndroidUtilities.updateVisibleRows(mediaPage.listView);
        this.photoVideoChangeColumnsAnimation = true;
        if (this.changeColumnsTab == 0) {
            this.sharedMediaData[0].setListFrozen(true);
        }
        this.photoVideoChangeColumnsProgress = 0.0f;
        if (this.pinchCenterPosition < 0) {
            saveScrollPosition();
            return;
        }
        while (true) {
            MediaPage[] mediaPageArr2 = this.mediaPages;
            if (i3 >= mediaPageArr2.length) {
                return;
            }
            if (mediaPageArr2[i3].selectedType == this.changeColumnsTab) {
                this.mediaPages[i3].animationSupportingLayoutManager.scrollToPositionWithOffset(this.pinchCenterPosition, this.pinchCenterOffset - this.mediaPages[i3].animationSupportingListView.getPaddingTop());
            }
            i3++;
        }
    }

    private void finishPinchToMediaColumnsCount() {
        final MediaPage mediaPage;
        RecyclerView.Adapter adapter;
        int i = 0;
        if (!this.photoVideoChangeColumnsAnimation) {
            return;
        }
        int i2 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i2 >= mediaPageArr.length) {
                mediaPage = null;
                break;
            } else {
                if (mediaPageArr[i2].selectedType == this.changeColumnsTab) {
                    mediaPage = this.mediaPages[i2];
                    break;
                }
                i2++;
            }
        }
        if (mediaPage == null) {
            return;
        }
        final int i3 = (mediaPage.selectedType == 8 || mediaPage.selectedType == 9) ? 1 : 0;
        float f = this.photoVideoChangeColumnsProgress;
        if (f != 1.0f) {
            if (f == 0.0f) {
                this.photoVideoChangeColumnsAnimation = false;
                if (this.changeColumnsTab == 0) {
                    this.sharedMediaData[0].setListFrozen(false);
                }
                mediaPage.animationSupportingListView.setVisibility(8);
                mediaPage.listView.invalidate();
                return;
            }
            final boolean z = f > 0.2f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(f, z ? 1.0f : 0.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SharedMediaLayout.this.photoVideoChangeColumnsProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    mediaPage.listView.invalidate();
                }
            });
            ofFloat.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    View findViewByPosition;
                    RecyclerView.Adapter adapter2;
                    SharedMediaLayout.this.photoVideoChangeColumnsAnimation = false;
                    if (z) {
                        SharedMediaLayout.this.mediaColumnsCount[i3] = SharedMediaLayout.this.animateToColumnsCount;
                        if (i3 == 0) {
                            SharedConfig.setMediaColumnsCount(SharedMediaLayout.this.animateToColumnsCount);
                        } else if (SharedMediaLayout.this.getStoriesCount(mediaPage.selectedType) >= 5) {
                            SharedConfig.setStoriesColumnsCount(SharedMediaLayout.this.animateToColumnsCount);
                        }
                    }
                    for (int i4 = 0; i4 < SharedMediaLayout.this.mediaPages.length; i4++) {
                        if (SharedMediaLayout.this.mediaPages[i4] != null && SharedMediaLayout.this.mediaPages[i4].listView != null) {
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            if (sharedMediaLayout.isTabZoomable(sharedMediaLayout.mediaPages[i4].selectedType) && (adapter2 = SharedMediaLayout.this.mediaPages[i4].listView.getAdapter()) != null) {
                                int itemCount = adapter2.getItemCount();
                                if (i4 == 0) {
                                    SharedMediaLayout.this.sharedMediaData[0].setListFrozen(false);
                                }
                                if (z) {
                                    SharedMediaLayout.this.mediaPages[i4].layoutManager.setSpanCount(SharedMediaLayout.this.mediaColumnsCount[i3]);
                                    SharedMediaLayout.this.mediaPages[i4].listView.invalidateItemDecorations();
                                    if (adapter2.getItemCount() == itemCount) {
                                        AndroidUtilities.updateVisibleRows(SharedMediaLayout.this.mediaPages[i4].listView);
                                    } else {
                                        adapter2.notifyDataSetChanged();
                                    }
                                }
                                SharedMediaLayout.this.mediaPages[i4].animationSupportingListView.setVisibility(8);
                            }
                        }
                    }
                    SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                    if (sharedMediaLayout2.pinchCenterPosition >= 0) {
                        for (int i5 = 0; i5 < SharedMediaLayout.this.mediaPages.length; i5++) {
                            if (SharedMediaLayout.this.mediaPages[i5].selectedType == SharedMediaLayout.this.changeColumnsTab) {
                                if (z && (findViewByPosition = SharedMediaLayout.this.mediaPages[i5].animationSupportingLayoutManager.findViewByPosition(SharedMediaLayout.this.pinchCenterPosition)) != null) {
                                    SharedMediaLayout.this.pinchCenterOffset = findViewByPosition.getTop();
                                }
                                ExtendedGridLayoutManager extendedGridLayoutManager = SharedMediaLayout.this.mediaPages[i5].layoutManager;
                                SharedMediaLayout sharedMediaLayout3 = SharedMediaLayout.this;
                                extendedGridLayoutManager.scrollToPositionWithOffset(sharedMediaLayout3.pinchCenterPosition, (-sharedMediaLayout3.mediaPages[i5].listView.getPaddingTop()) + SharedMediaLayout.this.pinchCenterOffset);
                            }
                        }
                    } else {
                        sharedMediaLayout2.saveScrollPosition();
                    }
                    super.onAnimationEnd(animator);
                }
            });
            ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
            ofFloat.setDuration(200L);
            ofFloat.start();
            return;
        }
        this.photoVideoChangeColumnsAnimation = false;
        int[] iArr = this.mediaColumnsCount;
        int i4 = this.animateToColumnsCount;
        iArr[i3] = i4;
        if (i3 != 0) {
            if (getStoriesCount(mediaPage.selectedType) >= 5) {
                SharedConfig.setStoriesColumnsCount(this.animateToColumnsCount);
            }
        } else {
            SharedConfig.setMediaColumnsCount(i4);
        }
        int i5 = 0;
        while (true) {
            MediaPage[] mediaPageArr2 = this.mediaPages;
            if (i5 >= mediaPageArr2.length) {
                break;
            }
            MediaPage mediaPage2 = mediaPageArr2[i5];
            if (mediaPage2 != null && mediaPage2.listView != null && isTabZoomable(this.mediaPages[i5].selectedType) && (adapter = this.mediaPages[i5].listView.getAdapter()) != null) {
                int itemCount = adapter.getItemCount();
                if (i5 == 0) {
                    this.sharedMediaData[0].setListFrozen(false);
                }
                this.mediaPages[i5].animationSupportingListView.setVisibility(8);
                this.mediaPages[i5].layoutManager.setSpanCount(this.mediaColumnsCount[i3]);
                this.mediaPages[i5].listView.invalidateItemDecorations();
                this.mediaPages[i5].listView.invalidate();
                if (adapter.getItemCount() == itemCount) {
                    AndroidUtilities.updateVisibleRows(this.mediaPages[i5].listView);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
            i5++;
        }
        if (this.pinchCenterPosition < 0) {
            saveScrollPosition();
            return;
        }
        while (true) {
            MediaPage[] mediaPageArr3 = this.mediaPages;
            if (i >= mediaPageArr3.length) {
                return;
            }
            if (mediaPageArr3[i].selectedType == this.changeColumnsTab) {
                View findViewByPosition = this.mediaPages[i].animationSupportingLayoutManager.findViewByPosition(this.pinchCenterPosition);
                if (findViewByPosition != null) {
                    this.pinchCenterOffset = findViewByPosition.getTop();
                }
                this.mediaPages[i].layoutManager.scrollToPositionWithOffset(this.pinchCenterPosition, (-this.mediaPages[i].listView.getPaddingTop()) + this.pinchCenterOffset);
            }
            i++;
        }
    }

    private void animateToMediaColumnsCount(final int i) {
        final int i2 = 1;
        final MediaPage mediaPage = getMediaPage(this.changeColumnsTab);
        this.pinchCenterPosition = -1;
        if (mediaPage != null) {
            mediaPage.listView.stopScroll();
            this.animateToColumnsCount = i;
            mediaPage.animationSupportingListView.setVisibility(0);
            int i3 = this.changeColumnsTab;
            if (i3 == 8) {
                mediaPage.animationSupportingListView.setAdapter(this.animationSupportingStoriesAdapter);
            } else if (i3 == 9) {
                mediaPage.animationSupportingListView.setAdapter(this.animationSupportingArchivedStoriesAdapter);
            } else {
                mediaPage.animationSupportingListView.setAdapter(this.animationSupportingPhotoVideoAdapter);
            }
            InternalListView internalListView = mediaPage.animationSupportingListView;
            int paddingLeft = mediaPage.animationSupportingListView.getPaddingLeft();
            InternalListView internalListView2 = mediaPage.animationSupportingListView;
            int dp = this.changeColumnsTab == 9 ? AndroidUtilities.dp(64.0f) : 0;
            internalListView2.hintPaddingTop = dp;
            int paddingRight = mediaPage.animationSupportingListView.getPaddingRight();
            InternalListView internalListView3 = mediaPage.animationSupportingListView;
            int dp2 = isStoriesView() ? AndroidUtilities.dp(72.0f) : 0;
            internalListView3.hintPaddingBottom = dp2;
            internalListView.setPadding(paddingLeft, dp, paddingRight, dp2);
            mediaPage.buttonView.setVisibility(this.changeColumnsTab == 8 ? 0 : 8);
            mediaPage.buttonView.setVisibility(this.changeColumnsTab == 8 ? 0 : 8);
            mediaPage.animationSupportingLayoutManager.setSpanCount(i);
            mediaPage.animationSupportingListView.invalidateItemDecorations();
            int i4 = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i4 >= mediaPageArr.length) {
                    break;
                }
                MediaPage mediaPage2 = mediaPageArr[i4];
                if (mediaPage2 != null && isTabZoomable(mediaPage2.selectedType)) {
                    AndroidUtilities.updateVisibleRows(this.mediaPages[i4].listView);
                }
                i4++;
            }
            this.photoVideoChangeColumnsAnimation = true;
            if (this.changeColumnsTab == 0) {
                this.sharedMediaData[0].setListFrozen(true);
            }
            this.photoVideoChangeColumnsProgress = 0.0f;
            saveScrollPosition();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.notificationsLocker.lock();
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SharedMediaLayout.this.photoVideoChangeColumnsProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    mediaPage.listView.invalidate();
                }
            });
            if (mediaPage.selectedType != 8 && mediaPage.selectedType != 9) {
                i2 = 0;
            }
            ofFloat.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    RecyclerView.Adapter adapter;
                    SharedMediaLayout.this.notificationsLocker.unlock();
                    SharedMediaLayout.this.photoVideoChangeColumnsAnimation = false;
                    SharedMediaLayout.this.mediaColumnsCount[i2] = i;
                    for (int i5 = 0; i5 < SharedMediaLayout.this.mediaPages.length; i5++) {
                        if (SharedMediaLayout.this.mediaPages[i5] != null && SharedMediaLayout.this.mediaPages[i5].listView != null) {
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            if (sharedMediaLayout.isTabZoomable(sharedMediaLayout.mediaPages[i5].selectedType) && (adapter = SharedMediaLayout.this.mediaPages[i5].listView.getAdapter()) != null) {
                                int itemCount = adapter.getItemCount();
                                if (i5 == 0) {
                                    SharedMediaLayout.this.sharedMediaData[0].setListFrozen(false);
                                }
                                SharedMediaLayout.this.mediaPages[i5].layoutManager.setSpanCount(SharedMediaLayout.this.mediaColumnsCount[i2]);
                                SharedMediaLayout.this.mediaPages[i5].listView.invalidateItemDecorations();
                                if (adapter.getItemCount() == itemCount) {
                                    AndroidUtilities.updateVisibleRows(SharedMediaLayout.this.mediaPages[i5].listView);
                                } else {
                                    adapter.notifyDataSetChanged();
                                }
                                SharedMediaLayout.this.mediaPages[i5].animationSupportingListView.setVisibility(8);
                            }
                        }
                    }
                    SharedMediaLayout.this.saveScrollPosition();
                }
            });
            ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
            ofFloat.setStartDelay(100L);
            ofFloat.setDuration(350L);
            ofFloat.start();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (this.scrollSlidingTextTabStrip != null) {
            canvas.save();
            canvas.translate(this.scrollSlidingTextTabStrip.getX(), this.scrollSlidingTextTabStrip.getY());
            this.scrollSlidingTextTabStrip.drawBackground(canvas);
            canvas.restore();
        }
        super.dispatchDraw(canvas);
        FragmentContextView fragmentContextView = this.fragmentContextView;
        if (fragmentContextView == null || !fragmentContextView.isCallStyle()) {
            return;
        }
        canvas.save();
        canvas.translate(this.fragmentContextView.getX(), this.fragmentContextView.getY());
        this.fragmentContextView.setDrawOverlay(true);
        this.fragmentContextView.draw(canvas);
        this.fragmentContextView.setDrawOverlay(false);
        canvas.restore();
    }

    private ScrollSlidingTextTabStripInner createScrollingTextTabStrip(Context context) {
        ScrollSlidingTextTabStripInner scrollSlidingTextTabStripInner = new ScrollSlidingTextTabStripInner(context, this.resourcesProvider) {
            @Override
            protected int processColor(int i) {
                return SharedMediaLayout.this.processColor(i);
            }
        };
        int i = this.initialTab;
        if (i != -1) {
            scrollSlidingTextTabStripInner.setInitialTabId(i);
            this.initialTab = -1;
        }
        scrollSlidingTextTabStripInner.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
        scrollSlidingTextTabStripInner.setColors(Theme.key_profile_tabSelectedLine, Theme.key_profile_tabSelectedText, Theme.key_profile_tabText, Theme.key_profile_tabSelector);
        scrollSlidingTextTabStripInner.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
            @Override
            public void onPageSelected(int i2, boolean z) {
                if (SharedMediaLayout.this.mediaPages[0].selectedType == i2) {
                    return;
                }
                SharedMediaLayout.this.mediaPages[1].selectedType = i2;
                SharedMediaLayout.this.mediaPages[1].setVisibility(0);
                SharedMediaLayout.this.hideFloatingDateView(true);
                SharedMediaLayout.this.switchToCurrentSelectedMode(true);
                SharedMediaLayout.this.animatingForward = z;
                SharedMediaLayout.this.onSelectedTabChanged();
                SharedMediaLayout.this.animateSearchToOptions(!r5.isSearchItemVisible(i2), true);
                SharedMediaLayout.this.updateOptionsSearch(true);
            }

            @Override
            public void onSamePageSelected() {
                SharedMediaLayout.this.scrollToTop();
            }

            @Override
            public void onPageScrolled(float f) {
                if (f != 1.0f || SharedMediaLayout.this.mediaPages[1].getVisibility() == 0) {
                    if (SharedMediaLayout.this.animatingForward) {
                        SharedMediaLayout.this.mediaPages[0].setTranslationX((-f) * SharedMediaLayout.this.mediaPages[0].getMeasuredWidth());
                        SharedMediaLayout.this.mediaPages[1].setTranslationX(SharedMediaLayout.this.mediaPages[0].getMeasuredWidth() - (SharedMediaLayout.this.mediaPages[0].getMeasuredWidth() * f));
                    } else {
                        SharedMediaLayout.this.mediaPages[0].setTranslationX(SharedMediaLayout.this.mediaPages[0].getMeasuredWidth() * f);
                        SharedMediaLayout.this.mediaPages[1].setTranslationX((SharedMediaLayout.this.mediaPages[0].getMeasuredWidth() * f) - SharedMediaLayout.this.mediaPages[0].getMeasuredWidth());
                    }
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    sharedMediaLayout.onTabProgress(sharedMediaLayout.getTabProgress());
                    SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                    sharedMediaLayout2.optionsAlpha = sharedMediaLayout2.getPhotoVideoOptionsAlpha(f);
                    SharedMediaLayout sharedMediaLayout3 = SharedMediaLayout.this;
                    sharedMediaLayout3.photoVideoOptionsItem.setVisibility((sharedMediaLayout3.optionsAlpha == 0.0f || !SharedMediaLayout.this.canShowSearchItem() || SharedMediaLayout.this.isArchivedOnlyStoriesView()) ? 4 : 0);
                    if (!SharedMediaLayout.this.canShowSearchItem()) {
                        SharedMediaLayout.this.searchItem.setVisibility(SharedMediaLayout.this.isStoriesView() ? 8 : 4);
                        SharedMediaLayout.this.searchAlpha = 0.0f;
                    } else {
                        SharedMediaLayout sharedMediaLayout4 = SharedMediaLayout.this;
                        sharedMediaLayout4.searchAlpha = sharedMediaLayout4.getSearchAlpha(f);
                        SharedMediaLayout.this.updateSearchItemIconAnimated();
                    }
                    SharedMediaLayout.this.updateOptionsSearch();
                    if (f == 1.0f) {
                        MediaPage mediaPage = SharedMediaLayout.this.mediaPages[0];
                        SharedMediaLayout.this.mediaPages[0] = SharedMediaLayout.this.mediaPages[1];
                        SharedMediaLayout.this.mediaPages[1] = mediaPage;
                        SharedMediaLayout.this.mediaPages[1].setVisibility(8);
                        if (SharedMediaLayout.this.searchItemState == 2) {
                            SharedMediaLayout.this.searchItem.setVisibility(SharedMediaLayout.this.isStoriesView() ? 8 : 4);
                        }
                        SharedMediaLayout.this.searchItemState = 0;
                        SharedMediaLayout.this.startStopVisibleGifs();
                    }
                }
            }
        });
        return scrollSlidingTextTabStripInner;
    }

    protected void drawBackgroundWithBlur(Canvas canvas, float f, android.graphics.Rect rect, Paint paint) {
        canvas.drawRect(rect, paint);
    }

    private boolean fillMediaData(int i) {
        SharedMediaData[] sharedMediaData = this.sharedMediaPreloader.getSharedMediaData();
        if (sharedMediaData == null) {
            return false;
        }
        if (i == 0) {
            SharedMediaData sharedMediaData2 = this.sharedMediaData[i];
            if (!sharedMediaData2.fastScrollDataLoaded) {
                sharedMediaData2.totalCount = sharedMediaData[i].totalCount;
            }
        } else {
            this.sharedMediaData[i].totalCount = sharedMediaData[i].totalCount;
        }
        this.sharedMediaData[i].messages.addAll(sharedMediaData[i].messages);
        this.sharedMediaData[i].sections.addAll(sharedMediaData[i].sections);
        for (Map.Entry<String, ArrayList<MessageObject>> entry : sharedMediaData[i].sectionArrays.entrySet()) {
            this.sharedMediaData[i].sectionArrays.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        for (int i2 = 0; i2 < 2; i2++) {
            this.sharedMediaData[i].messagesDict[i2] = sharedMediaData[i].messagesDict[i2].clone();
            SharedMediaData sharedMediaData3 = this.sharedMediaData[i];
            int[] iArr = sharedMediaData3.max_id;
            SharedMediaData sharedMediaData4 = sharedMediaData[i];
            iArr[i2] = sharedMediaData4.max_id[i2];
            sharedMediaData3.endReached[i2] = sharedMediaData4.endReached[i2];
        }
        this.sharedMediaData[i].fastScrollPeriods.addAll(sharedMediaData[i].fastScrollPeriods);
        return !sharedMediaData[i].messages.isEmpty();
    }

    public void hideFloatingDateView(boolean z) {
        AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
        if (this.floatingDateView.getTag() == null) {
            return;
        }
        this.floatingDateView.setTag(null);
        AnimatorSet animatorSet = this.floatingDateAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.floatingDateAnimation = null;
        }
        if (z) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.floatingDateAnimation = animatorSet2;
            animatorSet2.setDuration(180L);
            this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingDateView, (Property<ChatActionCell, Float>) View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.floatingDateView, (Property<ChatActionCell, Float>) View.TRANSLATION_Y, (-AndroidUtilities.dp(48.0f)) + this.additionalFloatingTranslation));
            this.floatingDateAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    SharedMediaLayout.this.floatingDateAnimation = null;
                }
            });
            this.floatingDateAnimation.start();
            return;
        }
        this.floatingDateView.setAlpha(0.0f);
    }

    public void scrollToTop() {
        int itemSize;
        int i = this.mediaPages[0].selectedType;
        if (i == 0) {
            itemSize = SharedPhotoVideoCell.getItemSize(1);
        } else {
            if (i != 1 && i != 2) {
                if (i == 3) {
                    itemSize = AndroidUtilities.dp(100.0f);
                } else if (i != 4) {
                    if (i == 5) {
                        itemSize = AndroidUtilities.dp(60.0f);
                    } else {
                        itemSize = AndroidUtilities.dp(58.0f);
                    }
                }
            }
            itemSize = AndroidUtilities.dp(56.0f);
        }
        if ((this.mediaPages[0].selectedType == 0 ? this.mediaPages[0].layoutManager.findFirstVisibleItemPosition() / this.mediaColumnsCount[0] : this.mediaPages[0].layoutManager.findFirstVisibleItemPosition()) * itemSize >= this.mediaPages[0].listView.getMeasuredHeight() * 1.2f) {
            this.mediaPages[0].scrollHelper.setScrollDirection(1);
            this.mediaPages[0].scrollHelper.scrollToPosition(0, 0, false, true);
        } else {
            this.mediaPages[0].listView.smoothScrollToPosition(0);
        }
    }

    public void checkLoadMoreScroll(MediaPage mediaPage, final RecyclerListView recyclerListView, LinearLayoutManager linearLayoutManager) {
        int i;
        int i2;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (this.photoVideoChangeColumnsAnimation || this.jumpToRunnable != null) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (recyclerListView.getFastScroll() == null || !recyclerListView.getFastScroll().isPressed() || currentTimeMillis - mediaPage.lastCheckScrollTime >= 300) {
            mediaPage.lastCheckScrollTime = currentTimeMillis;
            if ((this.searching && this.searchWas && mediaPage.selectedType != 11) || mediaPage.selectedType == 7) {
                return;
            }
            int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int abs = findFirstVisibleItemPosition == -1 ? 0 : Math.abs(linearLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
            int itemCount = recyclerListView.getAdapter() == null ? 0 : recyclerListView.getAdapter().getItemCount();
            if (mediaPage.selectedType == 0 || mediaPage.selectedType == 1 || mediaPage.selectedType == 2 || mediaPage.selectedType == 4) {
                final int i3 = mediaPage.selectedType;
                int startOffset = this.sharedMediaData[i3].getStartOffset() + this.sharedMediaData[i3].messages.size();
                SharedMediaData sharedMediaData = this.sharedMediaData[i3];
                if (sharedMediaData.fastScrollDataLoaded && sharedMediaData.fastScrollPeriods.size() > 2 && mediaPage.selectedType == 0 && this.sharedMediaData[i3].messages.size() != 0) {
                    float f = i3 == 0 ? this.mediaColumnsCount[0] : 1;
                    int measuredHeight = (int) ((recyclerListView.getMeasuredHeight() / (recyclerListView.getMeasuredWidth() / f)) * f * 1.5f);
                    if (measuredHeight < 100) {
                        measuredHeight = 100;
                    }
                    if (measuredHeight < this.sharedMediaData[i3].fastScrollPeriods.get(1).startOffset) {
                        measuredHeight = this.sharedMediaData[i3].fastScrollPeriods.get(1).startOffset;
                    }
                    if ((findFirstVisibleItemPosition > startOffset && findFirstVisibleItemPosition - startOffset > measuredHeight) || ((i = findFirstVisibleItemPosition + abs) < this.sharedMediaData[i3].startOffset && this.sharedMediaData[0].startOffset - i > measuredHeight)) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public final void run() {
                                SharedMediaLayout.this.lambda$checkLoadMoreScroll$13(i3, recyclerListView);
                            }
                        };
                        this.jumpToRunnable = runnable;
                        AndroidUtilities.runOnUIThread(runnable);
                        return;
                    }
                }
                itemCount = startOffset;
            }
            if (mediaPage.selectedType == 7) {
                return;
            }
            if (mediaPage.selectedType != 8) {
                if (mediaPage.selectedType != 9) {
                    if (mediaPage.selectedType != 6) {
                        if (mediaPage.selectedType == 11) {
                            int i4 = -1;
                            for (int i5 = 0; i5 < mediaPage.listView.getChildCount(); i5++) {
                                i4 = Math.max(mediaPage.listView.getChildAdapterPosition(mediaPage.listView.getChildAt(i5)), i4);
                            }
                            RecyclerView.Adapter adapter = mediaPage.listView.getAdapter();
                            SavedMessagesSearchAdapter savedMessagesSearchAdapter = this.savedMessagesSearchAdapter;
                            if (adapter == savedMessagesSearchAdapter) {
                                if (i4 + 1 >= savedMessagesSearchAdapter.dialogs.size() + this.savedMessagesSearchAdapter.loadedMessages.size()) {
                                    this.savedMessagesSearchAdapter.loadMore();
                                    return;
                                }
                                return;
                            } else {
                                if (i4 + 1 >= this.profileActivity.getMessagesController().getSavedMessagesController().getLoadedCount()) {
                                    this.profileActivity.getMessagesController().getSavedMessagesController().loadDialogs(false);
                                    return;
                                }
                                return;
                            }
                        }
                        int i6 = 10;
                        if (mediaPage.selectedType == 10 || mediaPage.selectedType == 12 || mediaPage.selectedType == 13) {
                            return;
                        }
                        if (mediaPage.selectedType == 0) {
                            i6 = 3;
                        } else if (mediaPage.selectedType != 5) {
                            i6 = 6;
                        }
                        if ((abs + findFirstVisibleItemPosition > itemCount - i6 || this.sharedMediaData[mediaPage.selectedType].loadingAfterFastScroll) && !this.sharedMediaData[mediaPage.selectedType].loading) {
                            if (mediaPage.selectedType != 0) {
                                if (mediaPage.selectedType == 1) {
                                    i2 = 1;
                                } else if (mediaPage.selectedType == 2) {
                                    i2 = 2;
                                } else if (mediaPage.selectedType == 4) {
                                    i2 = 4;
                                } else {
                                    i2 = mediaPage.selectedType == 5 ? 5 : 3;
                                }
                            } else {
                                int i7 = this.sharedMediaData[0].filterType;
                                i2 = i7 == 1 ? 6 : i7 == 2 ? 7 : 0;
                            }
                            if (!this.sharedMediaData[mediaPage.selectedType].endReached[0]) {
                                this.sharedMediaData[mediaPage.selectedType].loading = true;
                                this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, this.sharedMediaData[mediaPage.selectedType].max_id[0], 0, i2, this.topicId, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[mediaPage.selectedType].requestIndex, null, null);
                            } else if (this.mergeDialogId != 0 && !this.sharedMediaData[mediaPage.selectedType].endReached[1]) {
                                this.sharedMediaData[mediaPage.selectedType].loading = true;
                                this.profileActivity.getMediaDataController().loadMedia(this.mergeDialogId, 50, this.sharedMediaData[mediaPage.selectedType].max_id[1], 0, i2, this.topicId, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[mediaPage.selectedType].requestIndex, null, null);
                            }
                        }
                        int i8 = this.sharedMediaData[mediaPage.selectedType].startOffset;
                        if (mediaPage.selectedType == 0) {
                            i8 = this.photoVideoAdapter.getPositionForIndex(0);
                        }
                        if (findFirstVisibleItemPosition - i8 < i6 + 1 && !this.sharedMediaData[mediaPage.selectedType].loading && !this.sharedMediaData[mediaPage.selectedType].startReached && !this.sharedMediaData[mediaPage.selectedType].loadingAfterFastScroll) {
                            loadFromStart(mediaPage.selectedType);
                        }
                        if (this.mediaPages[0].listView == recyclerListView) {
                            if ((this.mediaPages[0].selectedType != 0 && this.mediaPages[0].selectedType != 5) || findFirstVisibleItemPosition == -1 || (findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition)) == null) {
                                return;
                            }
                            if (findViewHolderForAdapterPosition.getItemViewType() == 0 || findViewHolderForAdapterPosition.getItemViewType() == 12) {
                                View view = findViewHolderForAdapterPosition.itemView;
                                if (view instanceof SharedPhotoVideoCell) {
                                    MessageObject messageObject = ((SharedPhotoVideoCell) view).getMessageObject(0);
                                    if (messageObject != null) {
                                        this.floatingDateView.setCustomDate(messageObject.messageOwner.date, false, true);
                                        return;
                                    }
                                    return;
                                }
                                if (view instanceof ContextLinkCell) {
                                    this.floatingDateView.setCustomDate(((ContextLinkCell) view).getDate(), false, true);
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    if (abs <= 0 || this.commonGroupsAdapter.endReached || this.commonGroupsAdapter.loading || this.commonGroupsAdapter.chats.isEmpty() || findFirstVisibleItemPosition + abs < itemCount - 5) {
                        return;
                    }
                    CommonGroupsAdapter commonGroupsAdapter = this.commonGroupsAdapter;
                    commonGroupsAdapter.getChats(((TLRPC$Chat) commonGroupsAdapter.chats.get(this.commonGroupsAdapter.chats.size() - 1)).id, 100);
                    return;
                }
                StoriesController.StoriesList storiesList = this.archivedStoriesAdapter.storiesList;
                if (storiesList == null || findFirstVisibleItemPosition + abs <= storiesList.getLoadedCount() - this.mediaColumnsCount[1]) {
                    return;
                }
                this.archivedStoriesAdapter.load(false);
                return;
            }
            StoriesController.StoriesList storiesList2 = this.storiesAdapter.storiesList;
            if (storiesList2 == null || findFirstVisibleItemPosition + abs <= storiesList2.getLoadedCount() - this.mediaColumnsCount[1]) {
                return;
            }
            this.storiesAdapter.load(false);
        }
    }

    public void lambda$checkLoadMoreScroll$13(int i, RecyclerListView recyclerListView) {
        findPeriodAndJumpToDate(i, recyclerListView, false);
        this.jumpToRunnable = null;
    }

    private void loadFromStart(int i) {
        int i2;
        if (i == 0) {
            int i3 = this.sharedMediaData[0].filterType;
            i2 = i3 == 1 ? 6 : i3 == 2 ? 7 : 0;
        } else {
            i2 = i == 1 ? 1 : i == 2 ? 2 : i == 4 ? 4 : i == 5 ? 5 : 3;
        }
        this.sharedMediaData[i].loading = true;
        this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, 0, this.sharedMediaData[i].min_id, i2, this.topicId, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[i].requestIndex, null, null);
    }

    public ActionBarMenuItem getSearchItem() {
        return this.searchItem;
    }

    public RLottieImageView getSearchOptionsItem() {
        return this.optionsSearchImageView;
    }

    public boolean isSearchItemVisible() {
        return isSearchItemVisible(this.mediaPages[0].selectedType);
    }

    public boolean isSearchItemVisible(int i) {
        if (i == 7) {
            return this.delegate.canSearchMembers();
        }
        return (isSearchingStories() || i == 0 || i == 8 || i == 9 || i == 2 || i == 5 || i == 6 || i == 11 || i == 10 || i == 13) ? false : true;
    }

    public boolean isCalendarItemVisible() {
        return this.mediaPages[0].selectedType == 0 || this.mediaPages[0].selectedType == 8 || this.mediaPages[0].selectedType == 9 || this.mediaPages[0].selectedType == 11;
    }

    public boolean isOptionsItemVisible() {
        return this.mediaPages[0].selectedType == 0 || this.mediaPages[0].selectedType == 8 || this.mediaPages[0].selectedType == 9 || this.mediaPages[0].selectedType == 11 || this.mediaPages[0].selectedType == 13;
    }

    public int getSelectedTab() {
        return this.scrollSlidingTextTabStrip.getCurrentTabId();
    }

    public int getClosestTab() {
        MediaPage mediaPage = this.mediaPages[1];
        if (mediaPage == null || mediaPage.getVisibility() != 0 || ((!this.tabsAnimationInProgress || this.backAnimation) && Math.abs(this.mediaPages[1].getTranslationX()) >= this.mediaPages[1].getMeasuredWidth() / 2.0f)) {
            return this.scrollSlidingTextTabStrip.getCurrentTabId();
        }
        return this.mediaPages[1].selectedType;
    }

    public void onSelectedTabChanged() {
        boolean z = isStoriesView() || isArchivedOnlyStoriesView();
        if (this.archivedStoriesAdapter.poller != null) {
            this.archivedStoriesAdapter.poller.start(z && getClosestTab() == 9);
        }
        if (this.storiesAdapter.poller != null) {
            this.storiesAdapter.poller.start(z && getClosestTab() == 8);
        }
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            SearchTagsList searchTagsList = this.searchTagsList;
            actionBarMenuItem.setSearchFieldHint(LocaleController.getString((searchTagsList != null && searchTagsList.hasFilters() && getSelectedTab() == 11) ? R.string.SavedTagSearchHint : R.string.Search));
        }
    }

    public void onDestroy() {
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.mediaDidLoad);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagesDeleted);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByServer);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagePlayingDidReset);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagePlayingDidStart);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.storiesListUpdated);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.storiesUpdated);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.channelRecommendationsLoaded);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.savedMessagesDialogsUpdate);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.dialogsNeedReload);
        SearchTagsList searchTagsList = this.searchTagsList;
        if (searchTagsList != null) {
            searchTagsList.detach();
        }
        StoriesAdapter storiesAdapter = this.storiesAdapter;
        if (storiesAdapter != null && storiesAdapter.storiesList != null) {
            storiesAdapter.destroy();
        }
        StoriesAdapter storiesAdapter2 = this.archivedStoriesAdapter;
        if (storiesAdapter2 == null || storiesAdapter2.storiesList == null) {
            return;
        }
        storiesAdapter2.destroy();
    }

    public void checkCurrentTabValid() {
        if (this.scrollSlidingTextTabStrip.hasTab(this.scrollSlidingTextTabStrip.getCurrentTabId())) {
            return;
        }
        int firstTabId = this.scrollSlidingTextTabStrip.getFirstTabId();
        this.scrollSlidingTextTabStrip.setInitialTabId(firstTabId);
        this.mediaPages[0].selectedType = firstTabId;
        switchToCurrentSelectedMode(false);
    }

    public void setNewMediaCounts(int[] iArr) {
        boolean z;
        int i = 0;
        while (true) {
            if (i >= 6) {
                z = false;
                break;
            } else {
                if (this.hasMedia[i] >= 0) {
                    z = true;
                    break;
                }
                i++;
            }
        }
        System.arraycopy(iArr, 0, this.hasMedia, 0, 6);
        updateTabs(true);
        if (!z && this.scrollSlidingTextTabStrip.getCurrentTabId() == 6) {
            this.scrollSlidingTextTabStrip.resetTab();
        }
        checkCurrentTabValid();
        if (this.hasMedia[0] >= 0) {
            loadFastScrollData(false);
        }
    }

    private void loadFastScrollData(boolean z) {
        if (this.topicId != 0 || isSearchingStories()) {
            return;
        }
        int i = 0;
        while (true) {
            int[] iArr = supportedFastScrollTypes;
            if (i >= iArr.length) {
                return;
            }
            final int i2 = iArr[i];
            if ((this.sharedMediaData[i2].fastScrollDataLoaded && !z) || DialogObject.isEncryptedDialog(this.dialog_id)) {
                return;
            }
            this.sharedMediaData[i2].fastScrollDataLoaded = false;
            TLRPC$TL_messages_getSearchResultsPositions tLRPC$TL_messages_getSearchResultsPositions = new TLRPC$TL_messages_getSearchResultsPositions();
            if (i2 == 0) {
                int i3 = this.sharedMediaData[i2].filterType;
                if (i3 == 1) {
                    tLRPC$TL_messages_getSearchResultsPositions.filter = new TLRPC$TL_inputMessagesFilterPhotos();
                } else if (i3 == 2) {
                    tLRPC$TL_messages_getSearchResultsPositions.filter = new TLRPC$TL_inputMessagesFilterVideo();
                } else {
                    tLRPC$TL_messages_getSearchResultsPositions.filter = new TLRPC$TL_inputMessagesFilterPhotoVideo();
                }
            } else if (i2 == 1) {
                tLRPC$TL_messages_getSearchResultsPositions.filter = new TLRPC$TL_inputMessagesFilterDocument();
            } else if (i2 == 2) {
                tLRPC$TL_messages_getSearchResultsPositions.filter = new TLRPC$TL_inputMessagesFilterRoundVoice();
            } else {
                tLRPC$TL_messages_getSearchResultsPositions.filter = new TLRPC$TL_inputMessagesFilterMusic();
            }
            tLRPC$TL_messages_getSearchResultsPositions.limit = 100;
            tLRPC$TL_messages_getSearchResultsPositions.peer = this.profileActivity.getMessagesController().getInputPeer(this.dialog_id);
            if (this.topicId != 0 && this.profileActivity.getUserConfig().getClientUserId() == this.dialog_id) {
                tLRPC$TL_messages_getSearchResultsPositions.flags |= 4;
                tLRPC$TL_messages_getSearchResultsPositions.saved_peer_id = this.profileActivity.getMessagesController().getInputPeer(this.topicId);
            }
            final int i4 = this.sharedMediaData[i2].requestIndex;
            ConnectionsManager.getInstance(this.profileActivity.getCurrentAccount()).bindRequestToGuid(ConnectionsManager.getInstance(this.profileActivity.getCurrentAccount()).sendRequest(tLRPC$TL_messages_getSearchResultsPositions, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SharedMediaLayout.this.lambda$loadFastScrollData$17(i4, i2, tLObject, tLRPC$TL_error);
                }
            }), this.profileActivity.getClassGuid());
            i++;
        }
    }

    public void lambda$loadFastScrollData$17(final int i, final int i2, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.this.lambda$loadFastScrollData$16(tLRPC$TL_error, i, i2, tLObject);
            }
        });
    }

    public void lambda$loadFastScrollData$16(final TLRPC$TL_error tLRPC$TL_error, final int i, final int i2, final TLObject tLObject) {
        NotificationCenter.getInstance(this.profileActivity.getCurrentAccount()).doOnIdle(new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.this.lambda$loadFastScrollData$15(tLRPC$TL_error, i, i2, tLObject);
            }
        });
    }

    public void lambda$loadFastScrollData$15(TLRPC$TL_error tLRPC$TL_error, int i, int i2, TLObject tLObject) {
        if (tLRPC$TL_error != null) {
            return;
        }
        SharedMediaData sharedMediaData = this.sharedMediaData[i2];
        if (i != sharedMediaData.requestIndex) {
            return;
        }
        TLRPC$TL_messages_searchResultsPositions tLRPC$TL_messages_searchResultsPositions = (TLRPC$TL_messages_searchResultsPositions) tLObject;
        sharedMediaData.fastScrollPeriods.clear();
        int size = tLRPC$TL_messages_searchResultsPositions.positions.size();
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            TLRPC$TL_searchResultPosition tLRPC$TL_searchResultPosition = tLRPC$TL_messages_searchResultsPositions.positions.get(i4);
            if (tLRPC$TL_searchResultPosition.date != 0) {
                this.sharedMediaData[i2].fastScrollPeriods.add(new Period(tLRPC$TL_searchResultPosition));
            }
        }
        Collections.sort(this.sharedMediaData[i2].fastScrollPeriods, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$loadFastScrollData$14;
                lambda$loadFastScrollData$14 = SharedMediaLayout.lambda$loadFastScrollData$14((SharedMediaLayout.Period) obj, (SharedMediaLayout.Period) obj2);
                return lambda$loadFastScrollData$14;
            }
        });
        this.sharedMediaData[i2].setTotalCount(tLRPC$TL_messages_searchResultsPositions.count);
        SharedMediaData sharedMediaData2 = this.sharedMediaData[i2];
        sharedMediaData2.fastScrollDataLoaded = true;
        if (!sharedMediaData2.fastScrollPeriods.isEmpty()) {
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i3 >= mediaPageArr.length) {
                    break;
                }
                if (mediaPageArr[i3].selectedType == i2) {
                    MediaPage mediaPage = this.mediaPages[i3];
                    mediaPage.fastScrollEnabled = true;
                    updateFastScrollVisibility(mediaPage, true);
                }
                i3++;
            }
        }
        this.photoVideoAdapter.notifyDataSetChanged();
    }

    public static int lambda$loadFastScrollData$14(Period period, Period period2) {
        return period2.date - period.date;
    }

    public static void showFastScrollHint(final MediaPage mediaPage, SharedMediaData[] sharedMediaDataArr, boolean z) {
        Runnable runnable;
        if (z) {
            if (SharedConfig.fastScrollHintCount <= 0 || mediaPage.fastScrollHintView != null || mediaPage.fastScrollHinWasShown || mediaPage.listView.getFastScroll() == null || !mediaPage.listView.getFastScroll().isVisible || mediaPage.listView.getFastScroll().getVisibility() != 0 || sharedMediaDataArr[0].totalCount < 50) {
                return;
            }
            SharedConfig.setFastScrollHintCount(SharedConfig.fastScrollHintCount - 1);
            mediaPage.fastScrollHinWasShown = true;
            final SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip = new SharedMediaFastScrollTooltip(mediaPage.getContext());
            mediaPage.fastScrollHintView = sharedMediaFastScrollTooltip;
            mediaPage.addView(sharedMediaFastScrollTooltip, LayoutHelper.createFrame(-2, -2.0f));
            mediaPage.fastScrollHintView.setAlpha(0.0f);
            mediaPage.fastScrollHintView.setScaleX(0.8f);
            mediaPage.fastScrollHintView.setScaleY(0.8f);
            mediaPage.fastScrollHintView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150L).start();
            mediaPage.invalidate();
            Runnable runnable2 = new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.lambda$showFastScrollHint$18(SharedMediaLayout.MediaPage.this, sharedMediaFastScrollTooltip);
                }
            };
            mediaPage.fastScrollHideHintRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 4000L);
            return;
        }
        if (mediaPage.fastScrollHintView == null || (runnable = mediaPage.fastScrollHideHintRunnable) == null) {
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(runnable);
        mediaPage.fastScrollHideHintRunnable.run();
        mediaPage.fastScrollHideHintRunnable = null;
        mediaPage.fastScrollHintView = null;
    }

    public static void lambda$showFastScrollHint$18(MediaPage mediaPage, final SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip) {
        mediaPage.fastScrollHintView = null;
        mediaPage.fastScrollHideHintRunnable = null;
        sharedMediaFastScrollTooltip.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(220L).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (SharedMediaFastScrollTooltip.this.getParent() != null) {
                    ((ViewGroup) SharedMediaFastScrollTooltip.this.getParent()).removeView(SharedMediaFastScrollTooltip.this);
                }
            }
        }).start();
    }

    public void setCommonGroupsCount(int i) {
        if (this.topicId == 0) {
            this.hasMedia[6] = i;
        }
        updateTabs(true);
        checkCurrentTabValid();
    }

    public void onActionBarItemClick(View view, int i) {
        BotPreviewsEditContainer botPreviewsEditContainer;
        TLRPC$Chat chat;
        TLRPC$User tLRPC$User;
        TLRPC$EncryptedChat tLRPC$EncryptedChat;
        boolean z;
        TLRPC$User user;
        if (i != 101) {
            if (i == 100) {
                if (this.info != null) {
                    TLRPC$Chat chat2 = this.profileActivity.getMessagesController().getChat(Long.valueOf(this.info.id));
                    if (this.profileActivity.getMessagesController().isChatNoForwards(chat2)) {
                        HintView hintView = this.fwdRestrictedHint;
                        if (hintView != null) {
                            hintView.setText((!ChatObject.isChannel(chat2) || chat2.megagroup) ? LocaleController.getString("ForwardsRestrictedInfoGroup", R.string.ForwardsRestrictedInfoGroup) : LocaleController.getString("ForwardsRestrictedInfoChannel", R.string.ForwardsRestrictedInfoChannel));
                            this.fwdRestrictedHint.showForView(view, true);
                            return;
                        }
                        return;
                    }
                }
                if (hasNoforwardsMessage()) {
                    HintView hintView2 = this.fwdRestrictedHint;
                    if (hintView2 != null) {
                        hintView2.setText(LocaleController.getString("ForwardsRestrictedInfoBot", R.string.ForwardsRestrictedInfoBot));
                        this.fwdRestrictedHint.showForView(view, true);
                        return;
                    }
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlySelect", true);
                bundle.putBoolean("canSelectTopics", true);
                bundle.putInt("dialogsType", 3);
                DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                    @Override
                    public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z2, TopicsFragment topicsFragment) {
                        boolean lambda$onActionBarItemClick$23;
                        lambda$onActionBarItemClick$23 = SharedMediaLayout.this.lambda$onActionBarItemClick$23(dialogsActivity2, arrayList, charSequence, z2, topicsFragment);
                        return lambda$onActionBarItemClick$23;
                    }
                });
                this.profileActivity.presentFragment(dialogsActivity);
                return;
            }
            if (i == 102) {
                if (this.selectedFiles[0].size() + this.selectedFiles[1].size() != 1) {
                    return;
                }
                SparseArray<MessageObject>[] sparseArrayArr = this.selectedFiles;
                MessageObject valueAt = sparseArrayArr[sparseArrayArr[0].size() == 1 ? (char) 0 : (char) 1].valueAt(0);
                Bundle bundle2 = new Bundle();
                long dialogId = valueAt.getDialogId();
                if (DialogObject.isEncryptedDialog(dialogId)) {
                    bundle2.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
                } else if (DialogObject.isUserDialog(dialogId)) {
                    bundle2.putLong("user_id", dialogId);
                } else {
                    TLRPC$Chat chat3 = this.profileActivity.getMessagesController().getChat(Long.valueOf(-dialogId));
                    if (chat3 != null && chat3.migrated_to != null) {
                        bundle2.putLong("migrated_to", dialogId);
                        dialogId = -chat3.migrated_to.channel_id;
                    }
                    bundle2.putLong("chat_id", -dialogId);
                }
                bundle2.putInt("message_id", valueAt.getId());
                bundle2.putBoolean("need_remove_previous_same_chat_activity", false);
                ChatActivity chatActivity = new ChatActivity(bundle2);
                chatActivity.highlightMessageId = valueAt.getId();
                long j = this.topicId;
                if (j != 0) {
                    ForumUtilities.applyTopic(chatActivity, MessagesStorage.TopicKey.of(dialogId, j));
                    bundle2.putInt("message_id", valueAt.getId());
                }
                this.profileActivity.presentFragment(chatActivity, false);
                return;
            }
            if (i == 103 || i == 104) {
                if (getClosestTab() == 8) {
                    StoriesAdapter storiesAdapter = this.storiesAdapter;
                    if (storiesAdapter == null || storiesAdapter.storiesList == null) {
                        return;
                    }
                    ArrayList<Integer> arrayList = new ArrayList<>();
                    for (int i2 = 0; i2 < this.selectedFiles[0].size(); i2++) {
                        arrayList.add(Integer.valueOf(this.selectedFiles[0].valueAt(i2).getId()));
                    }
                    if (i == 103 && arrayList.size() > this.profileActivity.getMessagesController().storiesPinnedToTopCountMax) {
                        BulletinFactory.of(this.profileActivity).createSimpleBulletin(R.raw.chats_infotip, AndroidUtilities.replaceTags(LocaleController.formatPluralString("StoriesPinLimit", this.profileActivity.getMessagesController().storiesPinnedToTopCountMax, new Object[0]))).show();
                        return;
                    }
                    if (this.storiesAdapter.storiesList.updatePinned(arrayList, i == 103)) {
                        BulletinFactory.of(this.profileActivity).createSimpleBulletin(R.raw.chats_infotip, AndroidUtilities.replaceTags(LocaleController.formatPluralString("StoriesPinLimit", this.profileActivity.getMessagesController().storiesPinnedToTopCountMax, new Object[0]))).show();
                    } else if (i == 103) {
                        BulletinFactory.of(this.profileActivity).createSimpleBulletin(R.raw.ic_pin, AndroidUtilities.replaceTags(LocaleController.formatPluralString("StoriesPinned", arrayList.size(), new Object[0])), LocaleController.formatPluralString("StoriesPinnedText", arrayList.size(), new Object[0])).show();
                    } else {
                        BulletinFactory.of(this.profileActivity).createSimpleBulletin(R.raw.ic_unpin, AndroidUtilities.replaceTags(LocaleController.formatPluralString("StoriesUnpinned", arrayList.size(), new Object[0]))).show();
                    }
                    closeActionMode(false);
                    return;
                }
                SavedMessagesController savedMessagesController = this.profileActivity.getMessagesController().getSavedMessagesController();
                ArrayList<Long> arrayList2 = new ArrayList<>();
                for (int i3 = 0; i3 < savedMessagesController.allDialogs.size(); i3++) {
                    long j2 = savedMessagesController.allDialogs.get(i3).dialogId;
                    if (this.savedDialogsAdapter.selectedDialogs.contains(Long.valueOf(j2))) {
                        arrayList2.add(Long.valueOf(j2));
                    }
                }
                if (savedMessagesController.updatePinned(arrayList2, i == 103, true)) {
                    int i4 = 0;
                    while (true) {
                        MediaPage[] mediaPageArr = this.mediaPages;
                        if (i4 >= mediaPageArr.length) {
                            break;
                        }
                        if (mediaPageArr[i4].selectedType == 11) {
                            this.mediaPages[i4].layoutManager.scrollToPositionWithOffset(0, 0);
                            break;
                        }
                        i4++;
                    }
                } else {
                    this.profileActivity.showDialog(new LimitReachedBottomSheet(this.profileActivity, getContext(), 33, this.profileActivity.getCurrentAccount(), null));
                }
                closeActionMode(true);
                return;
            }
            return;
        }
        if (getSelectedTab() == 8 || getSelectedTab() == 9 || getSelectedTab() == 13) {
            if (this.selectedFiles[0] != null) {
                if (isBot() && (botPreviewsEditContainer = this.botPreviewsContainer) != null && botPreviewsEditContainer.getCurrentList() != null) {
                    final StoriesController.BotPreviewsList currentList = this.botPreviewsContainer.getCurrentList();
                    final ArrayList arrayList3 = new ArrayList();
                    for (int i5 = 0; i5 < this.selectedFiles[0].size(); i5++) {
                        TL_stories$StoryItem tL_stories$StoryItem = this.selectedFiles[0].valueAt(i5).storyItem;
                        if (tL_stories$StoryItem != null) {
                            arrayList3.add(tL_stories$StoryItem.media);
                        }
                    }
                    if (arrayList3.isEmpty()) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), this.resourcesProvider);
                    builder.setTitle(LocaleController.getString(arrayList3.size() > 1 ? R.string.DeleteBotPreviewsTitle : R.string.DeleteBotPreviewTitle));
                    builder.setMessage(LocaleController.formatPluralString("DeleteBotPreviewsSubtitle", arrayList3.size(), new Object[0]));
                    builder.setPositiveButton(LocaleController.getString(R.string.Delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i6) {
                            currentList.delete(arrayList3);
                            BulletinFactory.of(SharedMediaLayout.this.profileActivity).createSimpleBulletin(R.raw.ic_delete, LocaleController.formatPluralString("BotPreviewsDeleted", arrayList3.size(), new Object[0])).show();
                            SharedMediaLayout.this.closeActionMode(false);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i6) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog create = builder.create();
                    create.show();
                    create.redPositive();
                    return;
                }
                final ArrayList arrayList4 = new ArrayList();
                for (int i6 = 0; i6 < this.selectedFiles[0].size(); i6++) {
                    TL_stories$StoryItem tL_stories$StoryItem2 = this.selectedFiles[0].valueAt(i6).storyItem;
                    if (tL_stories$StoryItem2 != null) {
                        arrayList4.add(tL_stories$StoryItem2);
                    }
                }
                if (arrayList4.isEmpty()) {
                    return;
                }
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext(), this.resourcesProvider);
                builder2.setTitle(LocaleController.getString(arrayList4.size() > 1 ? R.string.DeleteStoriesTitle : R.string.DeleteStoryTitle));
                builder2.setMessage(LocaleController.formatPluralString("DeleteStoriesSubtitle", arrayList4.size(), new Object[0]));
                builder2.setPositiveButton(LocaleController.getString(R.string.Delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i7) {
                        SharedMediaLayout.this.profileActivity.getMessagesController().getStoriesController().deleteStories(SharedMediaLayout.this.dialog_id, arrayList4);
                        BulletinFactory.of(SharedMediaLayout.this.profileActivity).createSimpleBulletin(R.raw.ic_delete, LocaleController.formatPluralString("StoriesDeleted", arrayList4.size(), new Object[0])).show();
                        SharedMediaLayout.this.closeActionMode(false);
                    }
                });
                builder2.setNegativeButton(LocaleController.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i7) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog create2 = builder2.create();
                create2.show();
                create2.redPositive();
                return;
            }
            return;
        }
        if (getSelectedTab() == 11) {
            SavedMessagesController savedMessagesController2 = this.profileActivity.getMessagesController().getSavedMessagesController();
            final ArrayList arrayList5 = new ArrayList();
            for (int i7 = 0; i7 < savedMessagesController2.allDialogs.size(); i7++) {
                long j3 = savedMessagesController2.allDialogs.get(i7).dialogId;
                if (this.savedDialogsAdapter.selectedDialogs.contains(Long.valueOf(j3))) {
                    arrayList5.add(Long.valueOf(j3));
                }
            }
            String str = "";
            if (arrayList5.isEmpty()) {
                z = false;
            } else {
                Long l = (Long) arrayList5.get(0);
                long longValue = l.longValue();
                z = longValue == this.profileActivity.getUserConfig().getClientUserId();
                if (longValue < 0) {
                    TLRPC$Chat chat4 = this.profileActivity.getMessagesController().getChat(Long.valueOf(-longValue));
                    if (chat4 != null) {
                        str = chat4.title;
                    }
                } else if (longValue >= 0 && (user = this.profileActivity.getMessagesController().getUser(l)) != null) {
                    if (UserObject.isAnonymous(user)) {
                        str = LocaleController.getString(R.string.AnonymousForward);
                    } else {
                        str = UserObject.getUserName(user);
                    }
                }
            }
            AlertDialog create3 = new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(arrayList5.size() == 1 ? LocaleController.formatString(z ? R.string.ClearHistoryMyNotesTitle : R.string.ClearHistoryTitleSingle2, str) : LocaleController.formatPluralString("ClearHistoryTitleMultiple", arrayList5.size(), new Object[0])).setMessage(arrayList5.size() == 1 ? LocaleController.formatString(z ? R.string.ClearHistoryMyNotesMessage : R.string.ClearHistoryMessageSingle, str) : LocaleController.formatPluralString("ClearHistoryMessageMultiple", arrayList5.size(), new Object[0])).setPositiveButton(LocaleController.getString(R.string.Remove), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i8) {
                    SharedMediaLayout.this.lambda$onActionBarItemClick$21(arrayList5, dialogInterface, i8);
                }
            }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
            this.profileActivity.showDialog(create3);
            TextView textView = (TextView) create3.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                return;
            }
            return;
        }
        if (DialogObject.isEncryptedDialog(this.dialog_id)) {
            tLRPC$EncryptedChat = this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(this.dialog_id)));
            tLRPC$User = null;
            chat = null;
        } else if (DialogObject.isUserDialog(this.dialog_id)) {
            tLRPC$User = this.profileActivity.getMessagesController().getUser(Long.valueOf(this.dialog_id));
            chat = null;
            tLRPC$EncryptedChat = null;
        } else {
            chat = this.profileActivity.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
            tLRPC$User = null;
            tLRPC$EncryptedChat = null;
        }
        AlertsCreator.createDeleteMessagesAlert(this.profileActivity, tLRPC$User, chat, tLRPC$EncryptedChat, null, this.mergeDialogId, null, this.selectedFiles, null, 0, 0, null, new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.this.lambda$onActionBarItemClick$22();
            }
        }, null, this.resourcesProvider);
    }

    public void lambda$onActionBarItemClick$21(ArrayList arrayList, DialogInterface dialogInterface, int i) {
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            this.profileActivity.getMessagesController().deleteSavedDialog(((Long) arrayList.get(i2)).longValue());
        }
        closeActionMode();
    }

    public void lambda$onActionBarItemClick$22() {
        showActionMode(false);
        this.actionBar.closeSearchField();
        this.cantDeleteMessagesCount = 0;
    }

    public boolean lambda$onActionBarItemClick$23(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, TopicsFragment topicsFragment) {
        ArrayList<MessageObject> arrayList2 = new ArrayList<>();
        int i = 1;
        while (true) {
            if (i < 0) {
                break;
            }
            ArrayList arrayList3 = new ArrayList();
            for (int i2 = 0; i2 < this.selectedFiles[i].size(); i2++) {
                arrayList3.add(Integer.valueOf(this.selectedFiles[i].keyAt(i2)));
            }
            Collections.sort(arrayList3);
            Iterator it = arrayList3.iterator();
            while (it.hasNext()) {
                Integer num = (Integer) it.next();
                if (num.intValue() > 0) {
                    arrayList2.add(this.selectedFiles[i].get(num.intValue()));
                }
            }
            this.selectedFiles[i].clear();
            i--;
        }
        this.cantDeleteMessagesCount = 0;
        showActionMode(false);
        SavedDialogsAdapter savedDialogsAdapter = this.savedDialogsAdapter;
        if (savedDialogsAdapter != null) {
            savedDialogsAdapter.unselectAll();
        }
        if (arrayList.size() > 1 || ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId == this.profileActivity.getUserConfig().getClientUserId() || charSequence != null) {
            updateRowsSelection(true);
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                long j = ((MessagesStorage.TopicKey) arrayList.get(i3)).dialogId;
                if (charSequence != null) {
                    this.profileActivity.getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of(charSequence.toString(), j, null, null, null, true, null, null, null, true, 0, null, false));
                }
                this.profileActivity.getSendMessagesHelper().sendMessage(arrayList2, j, false, false, true, 0);
            }
            dialogsActivity.lambda$onBackPressed$308();
            BaseFragment baseFragment = this.profileActivity;
            UndoView undoView = baseFragment instanceof ProfileActivity ? ((ProfileActivity) baseFragment).getUndoView() : null;
            if (undoView != null) {
                if (arrayList.size() == 1) {
                    undoView.showWithAction(((MessagesStorage.TopicKey) arrayList.get(0)).dialogId, 53, Integer.valueOf(arrayList2.size()));
                } else {
                    undoView.showWithAction(0L, 53, Integer.valueOf(arrayList2.size()), Integer.valueOf(arrayList.size()), (Runnable) null, (Runnable) null);
                }
            }
        } else {
            long j2 = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (DialogObject.isEncryptedDialog(j2)) {
                bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j2));
            } else {
                if (DialogObject.isUserDialog(j2)) {
                    bundle.putLong("user_id", j2);
                } else {
                    bundle.putLong("chat_id", -j2);
                }
                if (!this.profileActivity.getMessagesController().checkCanOpenChat(bundle, dialogsActivity)) {
                    return true;
                }
            }
            this.profileActivity.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            ChatActivity chatActivity = new ChatActivity(bundle);
            ForumUtilities.applyTopic(chatActivity, (MessagesStorage.TopicKey) arrayList.get(0));
            dialogsActivity.presentFragment(chatActivity, true);
            chatActivity.showFieldPanelForForward(true, arrayList2);
        }
        return true;
    }

    private boolean prepareForMoving(MotionEvent motionEvent, boolean z) {
        MediaPage mediaPage;
        BotPreviewsEditContainer botPreviewsEditContainer;
        int nextPageId = this.scrollSlidingTextTabStrip.getNextPageId(z);
        if (nextPageId < 0) {
            return false;
        }
        if (!canShowSearchItem()) {
            this.searchItem.setVisibility(isStoriesView() ? 8 : 4);
            this.searchAlpha = 0.0f;
        } else {
            this.searchAlpha = getSearchAlpha(0.0f);
            updateSearchItemIcon(0.0f);
        }
        if (this.searching && getSelectedTab() == 11) {
            return false;
        }
        if (canEditStories() && this.isActionModeShowed && getClosestTab() == 8) {
            return false;
        }
        MediaPage mediaPage2 = this.mediaPages[0];
        if (mediaPage2 != null && mediaPage2.selectedType == 13 && (botPreviewsEditContainer = this.botPreviewsContainer) != null && !botPreviewsEditContainer.canScroll(z)) {
            return false;
        }
        if (this.isActionModeShowed && (mediaPage = this.mediaPages[0]) != null && mediaPage.selectedType == 13) {
            return false;
        }
        updateOptionsSearch();
        getParent().requestDisallowInterceptTouchEvent(true);
        hideFloatingDateView(true);
        this.maybeStartTracking = false;
        this.startedTracking = true;
        onTabScroll(true);
        this.startedTrackingX = (int) motionEvent.getX();
        this.actionBar.setEnabled(false);
        this.scrollSlidingTextTabStrip.setEnabled(false);
        this.mediaPages[1].selectedType = nextPageId;
        this.mediaPages[1].setVisibility(0);
        this.animatingForward = z;
        switchToCurrentSelectedMode(true);
        if (z) {
            this.mediaPages[1].setTranslationX(r7[0].getMeasuredWidth());
        } else {
            this.mediaPages[1].setTranslationX(-r7[0].getMeasuredWidth());
        }
        onTabProgress(getTabProgress());
        return true;
    }

    @Override
    public void forceHasOverlappingRendering(boolean z) {
        super.forceHasOverlappingRendering(z);
    }

    @Override
    public void setPadding(int i, int i2, int i3, int i4) {
        this.topPadding = i2;
        int i5 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i5 >= mediaPageArr.length) {
                break;
            }
            mediaPageArr[i5].setTranslationY(this.topPadding - this.lastMeasuredTopPadding);
            i5++;
        }
        FragmentContextView fragmentContextView = this.fragmentContextView;
        if (fragmentContextView != null) {
            fragmentContextView.setTranslationY(AndroidUtilities.dp(48.0f) + i2);
        }
        this.additionalFloatingTranslation = i2;
        ChatActionCell chatActionCell = this.floatingDateView;
        chatActionCell.setTranslationY((chatActionCell.getTag() == null ? -AndroidUtilities.dp(48.0f) : 0) + this.additionalFloatingTranslation);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int height = this.delegate.getListView() != null ? this.delegate.getListView().getHeight() : 0;
        if (height == 0) {
            height = View.MeasureSpec.getSize(i2);
        }
        setMeasuredDimension(size, height);
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt != null && childAt.getVisibility() != 8) {
                if (childAt instanceof MediaPage) {
                    measureChildWithMargins(childAt, i, 0, View.MeasureSpec.makeMeasureSpec(height, 1073741824), 0);
                    MediaPage mediaPage = (MediaPage) childAt;
                    mediaPage.listView.setPadding(0, mediaPage.listView.topPadding, 0, mediaPage.listView.bottomPadding);
                } else {
                    measureChildWithMargins(childAt, i, 0, i2, 0);
                }
            }
        }
    }

    public boolean checkTabsAnimationInProgress() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.checkTabsAnimationInProgress():boolean");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return checkTabsAnimationInProgress() || this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(motionEvent);
    }

    public boolean isCurrentTabFirst() {
        return this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId();
    }

    public RecyclerListView getCurrentListView() {
        ChatActivityContainer chatActivityContainer;
        if (this.mediaPages[0].selectedType != 13) {
            if (this.mediaPages[0].selectedType != 12 || (chatActivityContainer = this.savedMessagesContainer) == null) {
                return this.mediaPages[0].listView;
            }
            return chatActivityContainer.chatActivity.getChatListView();
        }
        return this.botPreviewsContainer.getCurrentListView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        if (this.disableScrolling || this.profileActivity.getParentLayout() == null || this.profileActivity.getParentLayout().checkTransitionAnimation() || checkTabsAnimationInProgress() || this.isInPinchToZoomTouchMode) {
            return false;
        }
        if (motionEvent != null) {
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.velocityTracker.addMovement(motionEvent);
            HintView hintView = this.fwdRestrictedHint;
            if (hintView != null) {
                hintView.hide();
            }
        }
        if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking && motionEvent.getY() >= AndroidUtilities.dp(48.0f)) {
            this.startedTrackingPointerId = motionEvent.getPointerId(0);
            this.maybeStartTracking = true;
            this.startedTrackingX = (int) motionEvent.getX();
            this.startedTrackingY = (int) motionEvent.getY();
            this.velocityTracker.clear();
        } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
            int x = (int) (motionEvent.getX() - this.startedTrackingX);
            int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
            if (this.startedTracking && (((z = this.animatingForward) && x > 0) || (!z && x < 0))) {
                if (!prepareForMoving(motionEvent, x < 0)) {
                    this.maybeStartTracking = true;
                    this.startedTracking = false;
                    onTabScroll(false);
                    this.mediaPages[0].setTranslationX(0.0f);
                    this.mediaPages[1].setTranslationX(this.animatingForward ? r4[0].getMeasuredWidth() : -r4[0].getMeasuredWidth());
                    this.scrollSlidingTextTabStrip.selectTabWithId(this.mediaPages[1].selectedType, 0.0f);
                    onTabProgress(getTabProgress());
                }
            }
            if (!this.maybeStartTracking || this.startedTracking) {
                if (this.startedTracking) {
                    this.mediaPages[0].setTranslationX(x);
                    if (this.animatingForward) {
                        this.mediaPages[1].setTranslationX(r9[0].getMeasuredWidth() + x);
                    } else {
                        this.mediaPages[1].setTranslationX(x - r9[0].getMeasuredWidth());
                    }
                    float abs2 = Math.abs(x) / this.mediaPages[0].getMeasuredWidth();
                    if (!canShowSearchItem()) {
                        this.searchAlpha = 0.0f;
                    } else {
                        this.searchAlpha = getSearchAlpha(abs2);
                        updateSearchItemIcon(abs2);
                        float photoVideoOptionsAlpha = getPhotoVideoOptionsAlpha(abs2);
                        this.optionsAlpha = photoVideoOptionsAlpha;
                        this.photoVideoOptionsItem.setVisibility((photoVideoOptionsAlpha == 0.0f || !canShowSearchItem() || isArchivedOnlyStoriesView()) ? 4 : 0);
                    }
                    updateOptionsSearch();
                    this.scrollSlidingTextTabStrip.selectTabWithId(this.mediaPages[1].selectedType, abs2);
                    onTabProgress(getTabProgress());
                    onSelectedTabChanged();
                }
            } else if (Math.abs(x) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(x) > abs) {
                prepareForMoving(motionEvent, x < 0);
            }
        } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
            stopScroll(motionEvent);
        }
        return this.startedTracking;
    }

    public void scrollToPage(int i) {
        ScrollSlidingTextTabStripInner scrollSlidingTextTabStripInner;
        if (this.disableScrolling || (scrollSlidingTextTabStripInner = this.scrollSlidingTextTabStrip) == null) {
            return;
        }
        scrollSlidingTextTabStripInner.scrollTo(i);
    }

    private void stopScroll(MotionEvent motionEvent) {
        float f;
        float f2;
        float measuredWidth;
        int measuredWidth2;
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker == null) {
            return;
        }
        velocityTracker.computeCurrentVelocity(1000, this.maximumVelocity);
        if (motionEvent == null || motionEvent.getAction() == 3) {
            f = 0.0f;
            f2 = 0.0f;
        } else {
            f = this.velocityTracker.getXVelocity();
            f2 = this.velocityTracker.getYVelocity();
            if (!this.startedTracking && Math.abs(f) >= 3000.0f && Math.abs(f) > Math.abs(f2)) {
                prepareForMoving(motionEvent, f < 0.0f);
            }
        }
        if (this.startedTracking) {
            float x = this.mediaPages[0].getX();
            this.tabsAnimation = new AnimatorSet();
            this.backAnimation = Math.abs(x) < ((float) this.mediaPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(f) < 3500.0f || Math.abs(f) < Math.abs(f2));
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SharedMediaLayout.this.lambda$stopScroll$24(valueAnimator);
                }
            });
            if (this.backAnimation) {
                measuredWidth = Math.abs(x);
                if (this.animatingForward) {
                    AnimatorSet animatorSet = this.tabsAnimation;
                    MediaPage mediaPage = this.mediaPages[0];
                    Property property = View.TRANSLATION_X;
                    animatorSet.playTogether(ObjectAnimator.ofFloat(mediaPage, (Property<MediaPage, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.mediaPages[1], (Property<MediaPage, Float>) property, r11.getMeasuredWidth()), ofFloat);
                } else {
                    AnimatorSet animatorSet2 = this.tabsAnimation;
                    MediaPage mediaPage2 = this.mediaPages[0];
                    Property property2 = View.TRANSLATION_X;
                    animatorSet2.playTogether(ObjectAnimator.ofFloat(mediaPage2, (Property<MediaPage, Float>) property2, 0.0f), ObjectAnimator.ofFloat(this.mediaPages[1], (Property<MediaPage, Float>) property2, -r11.getMeasuredWidth()), ofFloat);
                }
            } else {
                measuredWidth = this.mediaPages[0].getMeasuredWidth() - Math.abs(x);
                if (this.animatingForward) {
                    AnimatorSet animatorSet3 = this.tabsAnimation;
                    MediaPage mediaPage3 = this.mediaPages[0];
                    Property property3 = View.TRANSLATION_X;
                    animatorSet3.playTogether(ObjectAnimator.ofFloat(mediaPage3, (Property<MediaPage, Float>) property3, -mediaPage3.getMeasuredWidth()), ObjectAnimator.ofFloat(this.mediaPages[1], (Property<MediaPage, Float>) property3, 0.0f), ofFloat);
                } else {
                    AnimatorSet animatorSet4 = this.tabsAnimation;
                    MediaPage mediaPage4 = this.mediaPages[0];
                    Property property4 = View.TRANSLATION_X;
                    animatorSet4.playTogether(ObjectAnimator.ofFloat(mediaPage4, (Property<MediaPage, Float>) property4, mediaPage4.getMeasuredWidth()), ObjectAnimator.ofFloat(this.mediaPages[1], (Property<MediaPage, Float>) property4, 0.0f), ofFloat);
                }
            }
            this.tabsAnimation.setInterpolator(interpolator);
            int measuredWidth3 = getMeasuredWidth();
            float f3 = measuredWidth3 / 2;
            float distanceInfluenceForSnapDuration = f3 + (AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (measuredWidth * 1.0f) / measuredWidth3)) * f3);
            float abs = Math.abs(f);
            if (abs > 0.0f) {
                measuredWidth2 = Math.round(Math.abs(distanceInfluenceForSnapDuration / abs) * 1000.0f) * 4;
            } else {
                measuredWidth2 = (int) (((measuredWidth / getMeasuredWidth()) + 1.0f) * 100.0f);
            }
            this.tabsAnimation.setDuration(Math.max(150, Math.min(measuredWidth2, 600)));
            this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    SharedMediaLayout.this.tabsAnimation = null;
                    if (SharedMediaLayout.this.backAnimation) {
                        SharedMediaLayout.this.mediaPages[1].setVisibility(8);
                        if (!SharedMediaLayout.this.canShowSearchItem()) {
                            SharedMediaLayout.this.searchItem.setVisibility(SharedMediaLayout.this.isStoriesView() ? 8 : 4);
                            SharedMediaLayout.this.searchAlpha = 0.0f;
                        } else {
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            sharedMediaLayout.searchAlpha = sharedMediaLayout.getSearchAlpha(0.0f);
                            SharedMediaLayout.this.updateSearchItemIcon(0.0f);
                        }
                        SharedMediaLayout.this.updateOptionsSearch();
                        SharedMediaLayout.this.searchItemState = 0;
                    } else {
                        MediaPage mediaPage5 = SharedMediaLayout.this.mediaPages[0];
                        SharedMediaLayout.this.mediaPages[0] = SharedMediaLayout.this.mediaPages[1];
                        SharedMediaLayout.this.mediaPages[1] = mediaPage5;
                        SharedMediaLayout.this.mediaPages[1].setVisibility(8);
                        if (SharedMediaLayout.this.searchItemState == 2) {
                            SharedMediaLayout.this.searchItem.setVisibility(SharedMediaLayout.this.isStoriesView() ? 8 : 4);
                        }
                        SharedMediaLayout.this.searchItemState = 0;
                        SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                        sharedMediaLayout2.scrollSlidingTextTabStrip.selectTabWithId(sharedMediaLayout2.mediaPages[0].selectedType, 1.0f);
                        SharedMediaLayout.this.onSelectedTabChanged();
                        SharedMediaLayout.this.startStopVisibleGifs();
                    }
                    SharedMediaLayout.this.tabsAnimationInProgress = false;
                    SharedMediaLayout.this.maybeStartTracking = false;
                    SharedMediaLayout.this.startedTracking = false;
                    SharedMediaLayout.this.onTabScroll(false);
                    SharedMediaLayout.this.actionBar.setEnabled(true);
                    SharedMediaLayout.this.scrollSlidingTextTabStrip.setEnabled(true);
                }
            });
            this.tabsAnimation.start();
            this.tabsAnimationInProgress = true;
            this.startedTracking = false;
            onSelectedTabChanged();
        } else {
            this.maybeStartTracking = false;
            this.actionBar.setEnabled(true);
            this.scrollSlidingTextTabStrip.setEnabled(true);
        }
        VelocityTracker velocityTracker2 = this.velocityTracker;
        if (velocityTracker2 != null) {
            velocityTracker2.recycle();
            this.velocityTracker = null;
        }
    }

    public void lambda$stopScroll$24(ValueAnimator valueAnimator) {
        onTabProgress(getTabProgress());
    }

    public void disableScroll(boolean z) {
        if (z) {
            stopScroll(null);
        }
        this.disableScrolling = z;
    }

    public boolean closeActionMode() {
        return closeActionMode(true);
    }

    public boolean closeActionMode(boolean z) {
        if (!this.isActionModeShowed) {
            return false;
        }
        for (int i = 1; i >= 0; i--) {
            this.selectedFiles[i].clear();
        }
        this.cantDeleteMessagesCount = 0;
        onActionModeSelectedUpdate(this.selectedFiles[0]);
        BotPreviewsEditContainer botPreviewsEditContainer = this.botPreviewsContainer;
        if (botPreviewsEditContainer != null) {
            botPreviewsEditContainer.unselectAll();
            this.botPreviewsContainer.updateSelection(true);
        }
        showActionMode(false);
        updateRowsSelection(z);
        SavedDialogsAdapter savedDialogsAdapter = this.savedDialogsAdapter;
        if (savedDialogsAdapter != null) {
            savedDialogsAdapter.unselectAll();
        }
        return true;
    }

    public void setVisibleHeight(int i) {
        for (int i2 = 0; i2 < this.mediaPages.length; i2++) {
            float f = (-(getMeasuredHeight() - Math.max(i, AndroidUtilities.dp(this.mediaPages[i2].selectedType == 8 ? 280.0f : 120.0f)))) / 2.0f;
            this.mediaPages[i2].emptyView.setTranslationY(f);
            this.mediaPages[i2].progressView.setTranslationY(-f);
        }
        BotPreviewsEditContainer botPreviewsEditContainer = this.botPreviewsContainer;
        if (botPreviewsEditContainer != null) {
            botPreviewsEditContainer.setVisibleHeight(i);
        }
    }

    public SparseArray<MessageObject> getActionModeSelected() {
        return this.selectedFiles[0];
    }

    public boolean isActionModeShown() {
        return this.isActionModeShowed;
    }

    public void showActionMode(final boolean z) {
        if (this.isActionModeShowed == z) {
            return;
        }
        this.isActionModeShowed = z;
        AnimatorSet animatorSet = this.actionModeAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (z) {
            this.actionModeLayout.setVisibility(0);
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.actionModeAnimation = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this.actionModeLayout, (Property<LinearLayout, Float>) View.ALPHA, z ? 1.0f : 0.0f));
        this.actionModeAnimation.setDuration(180L);
        this.actionModeAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                SharedMediaLayout.this.actionModeAnimation = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (SharedMediaLayout.this.actionModeAnimation == null) {
                    return;
                }
                SharedMediaLayout.this.actionModeAnimation = null;
                if (z) {
                    return;
                }
                SharedMediaLayout.this.actionModeLayout.setVisibility(4);
            }
        });
        this.actionModeAnimation.start();
        if (z) {
            updateStoriesPinButton();
        }
    }

    private void updateStoriesPinButton() {
        boolean z;
        StoriesController.StoriesList storiesList;
        if (isBot()) {
            ActionBarMenuItem actionBarMenuItem = this.pinItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(8);
            }
            ActionBarMenuItem actionBarMenuItem2 = this.unpinItem;
            if (actionBarMenuItem2 != null) {
                actionBarMenuItem2.setVisibility(8);
                return;
            }
            return;
        }
        if (getClosestTab() == 9) {
            ActionBarMenuItem actionBarMenuItem3 = this.pinItem;
            if (actionBarMenuItem3 != null) {
                actionBarMenuItem3.setVisibility(8);
            }
            ActionBarMenuItem actionBarMenuItem4 = this.unpinItem;
            if (actionBarMenuItem4 != null) {
                actionBarMenuItem4.setVisibility(8);
                return;
            }
            return;
        }
        if (getClosestTab() == 8) {
            int i = 0;
            while (true) {
                if (i >= this.selectedFiles[0].size()) {
                    z = false;
                    break;
                }
                MessageObject valueAt = this.selectedFiles[0].valueAt(i);
                StoriesAdapter storiesAdapter = this.storiesAdapter;
                if (storiesAdapter != null && (storiesList = storiesAdapter.storiesList) != null && !storiesList.isPinned(valueAt.getId())) {
                    z = true;
                    break;
                }
                i++;
            }
            ActionBarMenuItem actionBarMenuItem5 = this.pinItem;
            if (actionBarMenuItem5 != null) {
                actionBarMenuItem5.setVisibility(z ? 0 : 8);
            }
            ActionBarMenuItem actionBarMenuItem6 = this.unpinItem;
            if (actionBarMenuItem6 != null) {
                actionBarMenuItem6.setVisibility(z ? 8 : 0);
            }
        }
    }

    @Override
    public void didReceivedNotification(int r32, int r33, java.lang.Object... r34) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    public void saveScrollPosition() {
        int i;
        int i2 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i2 >= mediaPageArr.length) {
                return;
            }
            InternalListView internalListView = mediaPageArr[i2].listView;
            if (internalListView != null) {
                int i3 = 0;
                int i4 = 0;
                for (int i5 = 0; i5 < internalListView.getChildCount(); i5++) {
                    View childAt = internalListView.getChildAt(i5);
                    if (childAt instanceof SharedPhotoVideoCell2) {
                        SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) childAt;
                        int messageId = sharedPhotoVideoCell2.getMessageId();
                        i4 = sharedPhotoVideoCell2.getTop();
                        i3 = messageId;
                    }
                    if (childAt instanceof SharedDocumentCell) {
                        SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) childAt;
                        int id = sharedDocumentCell.getMessage().getId();
                        i4 = sharedDocumentCell.getTop();
                        i3 = id;
                    }
                    if (childAt instanceof SharedAudioCell) {
                        SharedAudioCell sharedAudioCell = (SharedAudioCell) childAt;
                        i3 = sharedAudioCell.getMessage().getId();
                        i4 = sharedAudioCell.getTop();
                    }
                    if (i3 != 0) {
                        break;
                    }
                }
                if (i3 != 0) {
                    int i6 = this.mediaPages[i2].selectedType;
                    int i7 = -1;
                    if (i6 == 8 || i6 == 9) {
                        StoriesAdapter storiesAdapter = i6 == 8 ? this.storiesAdapter : this.archivedStoriesAdapter;
                        if (storiesAdapter.storiesList != null) {
                            int i8 = 0;
                            while (true) {
                                if (i8 >= storiesAdapter.storiesList.messageObjects.size()) {
                                    break;
                                }
                                if (i3 == storiesAdapter.storiesList.messageObjects.get(i8).getId()) {
                                    i7 = i8;
                                    break;
                                }
                                i8++;
                            }
                        }
                        i = i7;
                    } else if (i6 >= 0 && i6 < this.sharedMediaData.length) {
                        int i9 = 0;
                        while (true) {
                            if (i9 >= this.sharedMediaData[i6].messages.size()) {
                                break;
                            }
                            if (i3 == this.sharedMediaData[i6].messages.get(i9).getId()) {
                                i7 = i9;
                                break;
                            }
                            i9++;
                        }
                        i = this.sharedMediaData[i6].startOffset + i7;
                    }
                    if (i7 >= 0) {
                        ((LinearLayoutManager) internalListView.getLayoutManager()).scrollToPositionWithOffset(i, (-this.mediaPages[i2].listView.getPaddingTop()) + i4);
                        if (this.photoVideoChangeColumnsAnimation) {
                            this.mediaPages[i2].animationSupportingLayoutManager.scrollToPositionWithOffset(i, (-this.mediaPages[i2].listView.getPaddingTop()) + i4);
                        }
                    }
                }
            }
            i2++;
        }
    }

    public void animateItemsEnter(RecyclerListView recyclerListView, int i, SparseBooleanArray sparseBooleanArray) {
        int childCount = recyclerListView.getChildCount();
        View view = null;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = recyclerListView.getChildAt(i2);
            if (childAt instanceof FlickerLoadingView) {
                view = childAt;
            }
        }
        if (view != null) {
            recyclerListView.removeView(view);
        }
        getViewTreeObserver().addOnPreDrawListener(new AnonymousClass40(recyclerListView, sparseBooleanArray, view, i));
    }

    public class AnonymousClass40 implements ViewTreeObserver.OnPreDrawListener {
        final SparseBooleanArray val$addedMesages;
        final RecyclerListView val$finalListView;
        final View val$finalProgressView;
        final int val$oldItemCount;

        AnonymousClass40(RecyclerListView recyclerListView, SparseBooleanArray sparseBooleanArray, View view, int i) {
            this.val$finalListView = recyclerListView;
            this.val$addedMesages = sparseBooleanArray;
            this.val$finalProgressView = view;
            this.val$oldItemCount = i;
        }

        @Override
        public boolean onPreDraw() {
            SharedMediaLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
            RecyclerView.Adapter adapter = this.val$finalListView.getAdapter();
            if (adapter == SharedMediaLayout.this.photoVideoAdapter || adapter == SharedMediaLayout.this.documentsAdapter || adapter == SharedMediaLayout.this.audioAdapter || adapter == SharedMediaLayout.this.voiceAdapter) {
                if (this.val$addedMesages != null) {
                    int childCount = this.val$finalListView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        final int messageId = SharedMediaLayout.getMessageId(this.val$finalListView.getChildAt(i));
                        if (messageId != 0 && this.val$addedMesages.get(messageId, false)) {
                            SharedMediaLayout.this.messageAlphaEnter.put(messageId, Float.valueOf(0.0f));
                            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                            final RecyclerListView recyclerListView = this.val$finalListView;
                            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    SharedMediaLayout.AnonymousClass40.this.lambda$onPreDraw$0(messageId, recyclerListView, valueAnimator);
                                }
                            });
                            ofFloat.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    SharedMediaLayout.this.messageAlphaEnter.remove(messageId);
                                    AnonymousClass40.this.val$finalListView.invalidate();
                                }
                            });
                            ofFloat.setStartDelay((int) ((Math.min(this.val$finalListView.getMeasuredHeight(), Math.max(0, r7.getTop())) / this.val$finalListView.getMeasuredHeight()) * 100.0f));
                            ofFloat.setDuration(250L);
                            ofFloat.start();
                        }
                        this.val$finalListView.invalidate();
                    }
                }
            } else {
                int childCount2 = this.val$finalListView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i2 = 0; i2 < childCount2; i2++) {
                    View childAt = this.val$finalListView.getChildAt(i2);
                    if (childAt != this.val$finalProgressView && this.val$finalListView.getChildAdapterPosition(childAt) >= this.val$oldItemCount - 1) {
                        childAt.setAlpha(0.0f);
                        int min = (int) ((Math.min(this.val$finalListView.getMeasuredHeight(), Math.max(0, childAt.getTop())) / this.val$finalListView.getMeasuredHeight()) * 100.0f);
                        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(childAt, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f);
                        ofFloat2.setStartDelay(min);
                        ofFloat2.setDuration(200L);
                        animatorSet.playTogether(ofFloat2);
                    }
                    View view = this.val$finalProgressView;
                    if (view != null && view.getParent() == null) {
                        this.val$finalListView.addView(this.val$finalProgressView);
                        final RecyclerView.LayoutManager layoutManager = this.val$finalListView.getLayoutManager();
                        if (layoutManager != null) {
                            layoutManager.ignoreView(this.val$finalProgressView);
                            View view2 = this.val$finalProgressView;
                            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(view2, (Property<View, Float>) View.ALPHA, view2.getAlpha(), 0.0f);
                            ofFloat3.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    AnonymousClass40.this.val$finalProgressView.setAlpha(1.0f);
                                    layoutManager.stopIgnoringView(AnonymousClass40.this.val$finalProgressView);
                                    AnonymousClass40 anonymousClass40 = AnonymousClass40.this;
                                    anonymousClass40.val$finalListView.removeView(anonymousClass40.val$finalProgressView);
                                }
                            });
                            ofFloat3.start();
                        }
                    }
                }
                animatorSet.start();
            }
            return true;
        }

        public void lambda$onPreDraw$0(int i, RecyclerListView recyclerListView, ValueAnimator valueAnimator) {
            SharedMediaLayout.this.messageAlphaEnter.put(i, (Float) valueAnimator.getAnimatedValue());
            recyclerListView.invalidate();
        }
    }

    public void onResume() {
        this.scrolling = true;
        SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
        if (sharedPhotoVideoAdapter != null) {
            sharedPhotoVideoAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
        }
        SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
        if (sharedLinksAdapter != null) {
            sharedLinksAdapter.notifyDataSetChanged();
        }
        for (int i = 0; i < this.mediaPages.length; i++) {
            fixLayoutInternal(i);
        }
        ChatActivityContainer chatActivityContainer = this.savedMessagesContainer;
        if (chatActivityContainer != null) {
            chatActivityContainer.onResume();
        }
    }

    public void onPause() {
        ChatActivityContainer chatActivityContainer = this.savedMessagesContainer;
        if (chatActivityContainer != null) {
            chatActivityContainer.onPause();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return;
            }
            if (mediaPageArr[i].listView != null) {
                this.mediaPages[i].listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        SharedMediaLayout.this.mediaPages[i].getViewTreeObserver().removeOnPreDrawListener(this);
                        SharedMediaLayout.this.fixLayoutInternal(i);
                        return true;
                    }
                });
            }
            i++;
        }
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        TLRPC$ChatFull tLRPC$ChatFull2 = this.info;
        boolean z = tLRPC$ChatFull2 != null && tLRPC$ChatFull2.stories_pinned_available;
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null) {
            long j = tLRPC$ChatFull.migrated_from_chat_id;
            if (j != 0 && this.mergeDialogId == 0) {
                this.mergeDialogId = -j;
                int i = 0;
                while (true) {
                    SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                    if (i >= sharedMediaDataArr.length) {
                        break;
                    }
                    SharedMediaData sharedMediaData = sharedMediaDataArr[i];
                    sharedMediaData.max_id[1] = this.info.migrated_from_max_id;
                    sharedMediaData.endReached[1] = false;
                    i++;
                }
            }
        }
        TLRPC$ChatFull tLRPC$ChatFull3 = this.info;
        if (tLRPC$ChatFull3 == null || z == tLRPC$ChatFull3.stories_pinned_available) {
            return;
        }
        ScrollSlidingTextTabStripInner scrollSlidingTextTabStripInner = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStripInner != null) {
            scrollSlidingTextTabStripInner.setInitialTabId(isArchivedOnlyStoriesView() ? 9 : 8);
        }
        updateTabs(true);
        switchToCurrentSelectedMode(false);
    }

    public void setUserInfo(TLRPC$UserFull tLRPC$UserFull) {
        TLRPC$UserFull tLRPC$UserFull2 = this.userInfo;
        boolean z = tLRPC$UserFull2 != null && tLRPC$UserFull2.stories_pinned_available;
        this.userInfo = tLRPC$UserFull;
        updateTabs(true);
        if (tLRPC$UserFull == null || z == tLRPC$UserFull.stories_pinned_available) {
            return;
        }
        scrollToPage(8);
    }

    public void setChatUsers(ArrayList<Integer> arrayList, TLRPC$ChatFull tLRPC$ChatFull) {
        int i = 0;
        int i2 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i2 < mediaPageArr.length) {
                if (mediaPageArr[i2].selectedType == 7 && this.mediaPages[i2].listView.getAdapter() != null && this.mediaPages[i2].listView.getAdapter().getItemCount() != 0 && this.profileActivity.getMessagesController().getStoriesController().hasLoadingStories()) {
                    return;
                } else {
                    i2++;
                }
            } else {
                if (this.topicId == 0) {
                    this.chatUsersAdapter.chatInfo = tLRPC$ChatFull;
                    this.chatUsersAdapter.sortedUsers = arrayList;
                }
                updateTabs(true);
                while (true) {
                    MediaPage[] mediaPageArr2 = this.mediaPages;
                    if (i >= mediaPageArr2.length) {
                        return;
                    }
                    if (mediaPageArr2[i].selectedType == 7 && this.mediaPages[i].listView.getAdapter() != null) {
                        this.mediaPages[i].listView.getAdapter().notifyDataSetChanged();
                    }
                    i++;
                }
            }
        }
    }

    public void updateAdapters() {
        SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
        if (sharedPhotoVideoAdapter != null) {
            sharedPhotoVideoAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter2 = this.voiceAdapter;
        if (sharedDocumentsAdapter2 != null) {
            sharedDocumentsAdapter2.notifyDataSetChanged();
        }
        SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
        if (sharedLinksAdapter != null) {
            sharedLinksAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter3 = this.audioAdapter;
        if (sharedDocumentsAdapter3 != null) {
            sharedDocumentsAdapter3.notifyDataSetChanged();
        }
        GifAdapter gifAdapter = this.gifAdapter;
        if (gifAdapter != null) {
            gifAdapter.notifyDataSetChanged();
        }
        StoriesAdapter storiesAdapter = this.storiesAdapter;
        if (storiesAdapter != null) {
            storiesAdapter.notifyDataSetChanged();
        }
    }

    private void updateRowsSelection(boolean z) {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return;
            }
            int childCount = mediaPageArr[i].listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.mediaPages[i].listView.getChildAt(i2);
                if (childAt instanceof SharedDocumentCell) {
                    ((SharedDocumentCell) childAt).setChecked(false, z);
                } else if (childAt instanceof SharedPhotoVideoCell2) {
                    ((SharedPhotoVideoCell2) childAt).setChecked(false, z);
                } else if (childAt instanceof SharedLinkCell) {
                    ((SharedLinkCell) childAt).setChecked(false, z);
                } else if (childAt instanceof SharedAudioCell) {
                    ((SharedAudioCell) childAt).setChecked(false, z);
                } else if (childAt instanceof ContextLinkCell) {
                    ((ContextLinkCell) childAt).setChecked(false, z);
                } else if (childAt instanceof DialogCell) {
                    ((DialogCell) childAt).setChecked(false, z);
                }
            }
            i++;
        }
    }

    public void setMergeDialogId(long j) {
        this.mergeDialogId = j;
    }

    public void updateTabs(boolean r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.updateTabs(boolean):void");
    }

    public void startStopVisibleGifs() {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return;
            }
            int childCount = mediaPageArr[i].listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.mediaPages[i].listView.getChildAt(i2);
                if (childAt instanceof ContextLinkCell) {
                    ImageReceiver photoImage = ((ContextLinkCell) childAt).getPhotoImage();
                    if (i == 0) {
                        photoImage.setAllowStartAnimation(true);
                        photoImage.startAnimation();
                    } else {
                        photoImage.setAllowStartAnimation(false);
                        photoImage.stopAnimation();
                    }
                }
            }
            i++;
        }
    }

    public void switchToCurrentSelectedMode(boolean r32) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.switchToCurrentSelectedMode(boolean):void");
    }

    public void lambda$switchToCurrentSelectedMode$25(View view) {
        this.profileActivity.getMessagesController().getMainSettings().edit().putBoolean("story_keep", true).apply();
        StoryRecorder.getInstance(this.profileActivity.getParentActivity(), this.profileActivity.getCurrentAccount()).open(null);
    }

    public void lambda$switchToCurrentSelectedMode$26(View view) {
        this.profileActivity.getMessagesController().getMainSettings().edit().putBoolean("story_keep", true).apply();
        StoryRecorder.getInstance(this.profileActivity.getParentActivity(), this.profileActivity.getCurrentAccount()).open(null);
    }

    public boolean onItemLongClick(MessageObject messageObject, View view, int i) {
        if (this.isActionModeShowed || this.profileActivity.getParentActivity() == null || messageObject == null) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.profileActivity.getParentActivity().getCurrentFocus());
        this.selectedFiles[messageObject.getDialogId() == this.dialog_id ? (char) 0 : (char) 1].put(messageObject.getId(), messageObject);
        if (!messageObject.canDeleteMessage(false, null)) {
            this.cantDeleteMessagesCount++;
        }
        int i2 = 8;
        this.deleteItem.setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
        ActionBarMenuItem actionBarMenuItem = this.gotoItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility((getClosestTab() == 8 || getClosestTab() == 13) ? 8 : 0);
        }
        ActionBarMenuItem actionBarMenuItem2 = this.forwardItem;
        if (actionBarMenuItem2 != null) {
            if (getClosestTab() != 8 && getClosestTab() != 13) {
                i2 = 0;
            }
            actionBarMenuItem2.setVisibility(i2);
        }
        this.selectedMessagesCountTextView.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        for (int i3 = 0; i3 < this.actionModeViews.size(); i3++) {
            View view2 = this.actionModeViews.get(i3);
            AndroidUtilities.clearDrawableAnimation(view2);
            arrayList.add(ObjectAnimator.ofFloat(view2, (Property<View, Float>) View.SCALE_Y, 0.1f, 1.0f));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(250L);
        animatorSet.start();
        this.scrolling = false;
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(true, true);
        } else if (view instanceof SharedPhotoVideoCell) {
            ((SharedPhotoVideoCell) view).setChecked(i, true, true);
        } else if (view instanceof SharedLinkCell) {
            ((SharedLinkCell) view).setChecked(true, true);
        } else if (view instanceof SharedAudioCell) {
            ((SharedAudioCell) view).setChecked(true, true);
        } else if (view instanceof ContextLinkCell) {
            ((ContextLinkCell) view).setChecked(true, true);
        } else if (view instanceof SharedPhotoVideoCell2) {
            ((SharedPhotoVideoCell2) view).setChecked(true, true);
        }
        if (!this.isActionModeShowed) {
            showActionMode(true);
        }
        onActionModeSelectedUpdate(this.selectedFiles[0]);
        updateForwardItem();
        return true;
    }

    private void onItemClick(int i, View view, MessageObject messageObject, int i2, int i3) {
        if (messageObject == null || this.photoVideoChangeColumnsAnimation) {
            return;
        }
        String str = null;
        int i4 = 8;
        int i5 = 0;
        i5 = 0;
        if (this.isActionModeShowed) {
            if (i3 == 8 && !canEditStories()) {
                return;
            }
            char c = messageObject.getDialogId() == this.dialog_id ? (char) 0 : (char) 1;
            if (this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0) {
                this.selectedFiles[c].remove(messageObject.getId());
                if (!messageObject.canDeleteMessage(false, null)) {
                    this.cantDeleteMessagesCount--;
                }
            } else {
                if (this.selectedFiles[0].size() + this.selectedFiles[1].size() >= 100) {
                    return;
                }
                this.selectedFiles[c].put(messageObject.getId(), messageObject);
                if (!messageObject.canDeleteMessage(false, null)) {
                    this.cantDeleteMessagesCount++;
                }
            }
            onActionModeSelectedUpdate(this.selectedFiles[0]);
            if (this.selectedFiles[0].size() == 0 && this.selectedFiles[1].size() == 0) {
                showActionMode(false);
            } else {
                this.selectedMessagesCountTextView.setNumber(this.selectedFiles[0].size() + this.selectedFiles[1].size(), true);
                this.deleteItem.setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
                ActionBarMenuItem actionBarMenuItem = this.gotoItem;
                if (actionBarMenuItem != null) {
                    actionBarMenuItem.setVisibility((getClosestTab() == 8 || getClosestTab() == 13 || this.selectedFiles[0].size() != 1) ? 8 : 0);
                }
                ActionBarMenuItem actionBarMenuItem2 = this.forwardItem;
                if (actionBarMenuItem2 != null) {
                    if (getClosestTab() != 8 && getClosestTab() != 13) {
                        i4 = 0;
                    }
                    actionBarMenuItem2.setVisibility(i4);
                }
                updateStoriesPinButton();
            }
            this.scrolling = false;
            if (view instanceof SharedDocumentCell) {
                ((SharedDocumentCell) view).setChecked(this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0, true);
            } else if (view instanceof SharedPhotoVideoCell) {
                ((SharedPhotoVideoCell) view).setChecked(i2, this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0, true);
            } else if (view instanceof SharedLinkCell) {
                ((SharedLinkCell) view).setChecked(this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0, true);
            } else if (view instanceof SharedAudioCell) {
                ((SharedAudioCell) view).setChecked(this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0, true);
            } else if (view instanceof ContextLinkCell) {
                ((ContextLinkCell) view).setChecked(this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0, true);
            } else if (view instanceof SharedPhotoVideoCell2) {
                ((SharedPhotoVideoCell2) view).setChecked(this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0, true);
            }
        } else if (i3 == 0) {
            int i6 = i - this.sharedMediaData[i3].startOffset;
            if (i6 >= 0 && i6 < this.sharedMediaData[i3].messages.size()) {
                PhotoViewer.getInstance().setParentActivity(this.profileActivity);
                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i3].messages, i6, this.dialog_id, this.mergeDialogId, this.topicId, this.provider);
            }
        } else if (i3 == 2 || i3 == 4) {
            if (view instanceof SharedAudioCell) {
                ((SharedAudioCell) view).didPressedButton();
            }
        } else if (i3 == 5) {
            PhotoViewer.getInstance().setParentActivity(this.profileActivity);
            int indexOf = this.sharedMediaData[i3].messages.indexOf(messageObject);
            if (indexOf < 0) {
                ArrayList<MessageObject> arrayList = new ArrayList<>();
                arrayList.add(messageObject);
                PhotoViewer.getInstance().openPhoto(arrayList, 0, 0L, 0L, 0L, this.provider);
            } else {
                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i3].messages, indexOf, this.dialog_id, this.mergeDialogId, this.topicId, this.provider);
            }
        } else if (i3 == 1) {
            if (view instanceof SharedDocumentCell) {
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
                TLRPC$Document document = messageObject.getDocument();
                if (sharedDocumentCell.isLoaded()) {
                    if (messageObject.canPreviewDocument()) {
                        PhotoViewer.getInstance().setParentActivity(this.profileActivity);
                        int indexOf2 = this.sharedMediaData[i3].messages.indexOf(messageObject);
                        if (indexOf2 < 0) {
                            ArrayList<MessageObject> arrayList2 = new ArrayList<>();
                            arrayList2.add(messageObject);
                            PhotoViewer.getInstance().openPhoto(arrayList2, 0, 0L, 0L, 0L, this.provider);
                            return;
                        }
                        PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i3].messages, indexOf2, this.dialog_id, this.mergeDialogId, this.topicId, this.provider);
                        return;
                    }
                    AndroidUtilities.openDocument(messageObject, this.profileActivity.getParentActivity(), this.profileActivity);
                } else if (!sharedDocumentCell.isLoading()) {
                    MessageObject message = sharedDocumentCell.getMessage();
                    message.putInDownloadsStore = true;
                    this.profileActivity.getFileLoader().loadFile(document, message, 0, 0);
                    sharedDocumentCell.updateFileExistIcon(true);
                } else {
                    this.profileActivity.getFileLoader().cancelLoadFile(document);
                    sharedDocumentCell.updateFileExistIcon(true);
                }
            }
        } else if (i3 == 3) {
            try {
                TLRPC$WebPage tLRPC$WebPage = MessageObject.getMedia(messageObject.messageOwner) != null ? MessageObject.getMedia(messageObject.messageOwner).webpage : null;
                if (tLRPC$WebPage != null && !(tLRPC$WebPage instanceof TLRPC$TL_webPageEmpty)) {
                    if (tLRPC$WebPage.cached_page != null) {
                        LaunchActivity launchActivity = LaunchActivity.instance;
                        if (launchActivity == null || launchActivity.getBottomSheetTabs() == null || LaunchActivity.instance.getBottomSheetTabs().tryReopenTab(messageObject) == null) {
                            this.profileActivity.createArticleViewer(false).open(messageObject);
                            return;
                        }
                        return;
                    }
                    String str2 = tLRPC$WebPage.embed_url;
                    if (str2 != null && str2.length() != 0) {
                        openWebView(tLRPC$WebPage, messageObject);
                        return;
                    }
                    str = tLRPC$WebPage.url;
                }
                if (str == null) {
                    str = ((SharedLinkCell) view).getLink(0);
                }
                if (str != null) {
                    openUrl(str);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (i3 == 8 || i3 == 9) {
            final StoriesController.StoriesList storiesList = (i3 == 8 ? this.storiesAdapter : this.archivedStoriesAdapter).storiesList;
            if (storiesList == null) {
                return;
            }
            StoryViewer orCreateStoryViewer = this.profileActivity.getOrCreateStoryViewer();
            Context context = getContext();
            int id = messageObject.getId();
            StoriesListPlaceProvider with = StoriesListPlaceProvider.of(this.mediaPages[i2].listView).with(new StoriesListPlaceProvider.LoadNextInterface() {
                @Override
                public final void loadNext(boolean z) {
                    SharedMediaLayout.lambda$onItemClick$27(StoriesController.StoriesList.this, z);
                }
            });
            BaseFragment baseFragment = this.profileActivity;
            if ((baseFragment instanceof ProfileActivity) && ((ProfileActivity) baseFragment).myProfile) {
                i5 = AndroidUtilities.dp(68.0f);
            }
            orCreateStoryViewer.open(context, id, storiesList, with.addBottomClip(i5));
        }
        updateForwardItem();
    }

    public static void lambda$onItemClick$27(StoriesController.StoriesList storiesList, boolean z) {
        if (z) {
            storiesList.load(false, 30);
        }
    }

    public void openUrl(String str) {
        if (AndroidUtilities.shouldShowUrlInAlert(str)) {
            AlertsCreator.showOpenUrlAlert(this.profileActivity, str, true, true);
        } else {
            Browser.openUrl(this.profileActivity.getParentActivity(), str);
        }
    }

    public void openWebView(TLRPC$WebPage tLRPC$WebPage, MessageObject messageObject) {
        EmbedBottomSheet.show(this.profileActivity, messageObject, this.provider, tLRPC$WebPage.site_name, tLRPC$WebPage.description, tLRPC$WebPage.url, tLRPC$WebPage.embed_url, tLRPC$WebPage.embed_width, tLRPC$WebPage.embed_height, false);
    }

    private void recycleAdapter(RecyclerView.Adapter adapter) {
        if (adapter instanceof SharedPhotoVideoAdapter) {
            this.cellCache.addAll(this.cache);
            this.cache.clear();
        } else if (adapter == this.audioAdapter) {
            this.audioCellCache.addAll(this.audioCache);
            this.audioCache.clear();
        }
    }

    public void fixLayoutInternal(int i) {
        ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if (i == 0) {
            if (!AndroidUtilities.isTablet() && ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 2) {
                this.selectedMessagesCountTextView.setTextSize(18);
            } else {
                this.selectedMessagesCountTextView.setTextSize(20);
            }
        }
        if (i == 0) {
            this.photoVideoAdapter.notifyDataSetChanged();
        }
    }

    public class AnonymousClass43 implements SharedLinkCell.SharedLinkCellDelegate {
        AnonymousClass43() {
        }

        @Override
        public void needOpenWebView(TLRPC$WebPage tLRPC$WebPage, MessageObject messageObject) {
            SharedMediaLayout.this.openWebView(tLRPC$WebPage, messageObject);
        }

        @Override
        public boolean canPerformActions() {
            return !SharedMediaLayout.this.isActionModeShowed;
        }

        @Override
        public void onLinkPress(final String str, boolean z) {
            if (!z) {
                SharedMediaLayout.this.openUrl(str);
                return;
            }
            BottomSheet.Builder builder = new BottomSheet.Builder(SharedMediaLayout.this.profileActivity.getParentActivity());
            builder.setTitle(str);
            builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    SharedMediaLayout.AnonymousClass43.this.lambda$onLinkPress$0(str, dialogInterface, i);
                }
            });
            SharedMediaLayout.this.profileActivity.showDialog(builder.create());
        }

        public void lambda$onLinkPress$0(String str, DialogInterface dialogInterface, int i) {
            if (i == 0) {
                SharedMediaLayout.this.openUrl(str);
                return;
            }
            if (i == 1) {
                if (str.startsWith("mailto:")) {
                    str = str.substring(7);
                } else if (str.startsWith("tel:")) {
                    str = str.substring(4);
                }
                AndroidUtilities.addToClipboard(str);
            }
        }
    }

    public class SharedLinksAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;

        @Override
        public Object getItem(int i, int i2) {
            return null;
        }

        @Override
        public String getLetter(int i) {
            return null;
        }

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() != 0 || SharedMediaLayout.this.sharedMediaData[3].loading) {
                return i == 0 || i2 != 0;
            }
            return false;
        }

        @Override
        public int getSectionCount() {
            int i = 1;
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[3].loading) {
                return 1;
            }
            int size = SharedMediaLayout.this.sharedMediaData[3].sections.size();
            if (SharedMediaLayout.this.sharedMediaData[3].sections.isEmpty() || (SharedMediaLayout.this.sharedMediaData[3].endReached[0] && SharedMediaLayout.this.sharedMediaData[3].endReached[1])) {
                i = 0;
            }
            return size + i;
        }

        @Override
        public int getCountForSection(int i) {
            if ((SharedMediaLayout.this.sharedMediaData[3].sections.size() != 0 || SharedMediaLayout.this.sharedMediaData[3].loading) && i < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                return SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[3].sections.get(i)).size() + (i == 0 ? 0 : 1);
            }
            return 1;
        }

        @Override
        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(SharedMediaLayout.this.getThemedColor(Theme.key_graySection) & (-218103809));
            }
            if (i != 0) {
                if (i < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                    view.setAlpha(1.0f);
                    ((GraySectionCell) view).setText(LocaleController.formatSectionDate(SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[3].sections.get(i)).get(0).messageOwner.date));
                }
            } else {
                view.setAlpha(0.0f);
            }
            return view;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            GraySectionCell graySectionCell;
            if (i == 3) {
                graySectionCell = new GraySectionCell(this.mContext, SharedMediaLayout.this.resourcesProvider);
            } else if (i == 4) {
                SharedLinkCell sharedLinkCell = new SharedLinkCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
                sharedLinkCell.setDelegate(SharedMediaLayout.this.sharedLinkCellDelegate);
                graySectionCell = sharedLinkCell;
            } else {
                if (i == 5) {
                    View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 3, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                    createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(createEmptyStubView);
                }
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext, SharedMediaLayout.this.resourcesProvider);
                flickerLoadingView.setIsSingleCell(true);
                flickerLoadingView.showDate(false);
                flickerLoadingView.setViewType(5);
                graySectionCell = flickerLoadingView;
            }
            graySectionCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(graySectionCell);
        }

        @Override
        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 6 || viewHolder.getItemViewType() == 5) {
                return;
            }
            ArrayList<MessageObject> arrayList = SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[3].sections.get(i));
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 3) {
                MessageObject messageObject = arrayList.get(0);
                View view = viewHolder.itemView;
                if (view instanceof GraySectionCell) {
                    ((GraySectionCell) view).setText(LocaleController.formatSectionDate(messageObject.messageOwner.date));
                    return;
                }
                return;
            }
            if (itemViewType != 4) {
                return;
            }
            if (i != 0) {
                i2--;
            }
            if (!(viewHolder.itemView instanceof SharedLinkCell) || i2 < 0 || i2 >= arrayList.size()) {
                return;
            }
            SharedLinkCell sharedLinkCell = (SharedLinkCell) viewHolder.itemView;
            MessageObject messageObject2 = arrayList.get(i2);
            sharedLinkCell.setLink(messageObject2, i2 != arrayList.size() - 1 || (i == SharedMediaLayout.this.sharedMediaData[3].sections.size() - 1 && SharedMediaLayout.this.sharedMediaData[3].loading));
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            if (sharedMediaLayout.isActionModeShowed) {
                sharedLinkCell.setChecked(sharedMediaLayout.selectedFiles[(messageObject2.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject2.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject2.getId()) >= 0, !SharedMediaLayout.this.scrolling);
            } else {
                sharedLinkCell.setChecked(false, !sharedMediaLayout.scrolling);
            }
        }

        @Override
        public int getItemViewType(int i, int i2) {
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[3].loading) {
                return 5;
            }
            if (i < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                return (i == 0 || i2 != 0) ? 4 : 3;
            }
            return 6;
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = 0;
            iArr[1] = 0;
        }
    }

    public class SharedDocumentsAdapter extends RecyclerListView.FastScrollAdapter {
        private int currentType;
        private boolean inFastScrollMode;
        private Context mContext;

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public SharedDocumentsAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
        }

        @Override
        public int getItemCount() {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].loadingAfterFastScroll) {
                return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[this.currentType].loading) {
                return 1;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() == 0 && ((!SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[0] || !SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[1]) && SharedMediaLayout.this.sharedMediaData[this.currentType].startReached)) {
                return 0;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount == 0) {
                int startOffset = SharedMediaLayout.this.sharedMediaData[this.currentType].getStartOffset() + SharedMediaLayout.this.sharedMediaData[this.currentType].getMessages().size();
                return startOffset != 0 ? (SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[0] && SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[1]) ? startOffset : SharedMediaLayout.this.sharedMediaData[this.currentType].getEndLoadingStubs() != 0 ? startOffset + SharedMediaLayout.this.sharedMediaData[this.currentType].getEndLoadingStubs() : startOffset + 1 : startOffset;
            }
            return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            int i2 = 0;
            if (i == 7) {
                SharedDocumentCell sharedDocumentCell = new SharedDocumentCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
                sharedDocumentCell.setGlobalGradientView(SharedMediaLayout.this.globalGradientView);
                view = sharedDocumentCell;
            } else if (i == 8) {
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext, SharedMediaLayout.this.resourcesProvider);
                if (this.currentType == 2) {
                    flickerLoadingView.setViewType(4);
                } else {
                    flickerLoadingView.setViewType(3);
                }
                flickerLoadingView.showDate(false);
                flickerLoadingView.setIsSingleCell(true);
                flickerLoadingView.setGlobalGradientView(SharedMediaLayout.this.globalGradientView);
                view = flickerLoadingView;
            } else {
                if (i == 9) {
                    View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, this.currentType, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                    createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(createEmptyStubView);
                }
                if (this.currentType == 4 && !SharedMediaLayout.this.audioCellCache.isEmpty()) {
                    View view3 = (View) SharedMediaLayout.this.audioCellCache.get(0);
                    SharedMediaLayout.this.audioCellCache.remove(0);
                    ViewGroup viewGroup2 = (ViewGroup) view3.getParent();
                    view2 = view3;
                    if (viewGroup2 != null) {
                        viewGroup2.removeView(view3);
                        view2 = view3;
                    }
                } else {
                    view2 = new SharedAudioCell(this.mContext, i2, SharedMediaLayout.this.resourcesProvider) {
                        @Override
                        public boolean needPlayMessage(MessageObject messageObject) {
                            if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                                boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                                MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? SharedMediaLayout.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages : null, false);
                                return playMessage;
                            }
                            if (messageObject.isMusic()) {
                                return MediaController.getInstance().setPlaylist(SharedMediaLayout.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages, messageObject, SharedMediaLayout.this.mergeDialogId);
                            }
                            return false;
                        }
                    };
                }
                SharedAudioCell sharedAudioCell = (SharedAudioCell) view2;
                sharedAudioCell.setGlobalGradientView(SharedMediaLayout.this.globalGradientView);
                view = view2;
                if (this.currentType == 4) {
                    SharedMediaLayout.this.audioCache.add(sharedAudioCell);
                    view = view2;
                }
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ArrayList<MessageObject> arrayList = SharedMediaLayout.this.sharedMediaData[this.currentType].messages;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 7) {
                View view = viewHolder.itemView;
                if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
                    MessageObject messageObject = arrayList.get(i - SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset);
                    sharedDocumentCell.setDocument(messageObject, i != arrayList.size() - 1);
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    if (sharedMediaLayout.isActionModeShowed) {
                        sharedDocumentCell.setChecked(sharedMediaLayout.selectedFiles[(messageObject.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0, !SharedMediaLayout.this.scrolling);
                        return;
                    } else {
                        sharedDocumentCell.setChecked(false, !sharedMediaLayout.scrolling);
                        return;
                    }
                }
                return;
            }
            if (itemViewType != 10) {
                return;
            }
            View view2 = viewHolder.itemView;
            if (view2 instanceof SharedAudioCell) {
                SharedAudioCell sharedAudioCell = (SharedAudioCell) view2;
                MessageObject messageObject2 = arrayList.get(i - SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset);
                sharedAudioCell.setMessageObject(messageObject2, i != arrayList.size() - 1);
                SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                if (sharedMediaLayout2.isActionModeShowed) {
                    sharedAudioCell.setChecked(sharedMediaLayout2.selectedFiles[(messageObject2.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject2.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject2.getId()) >= 0, !SharedMediaLayout.this.scrolling);
                } else {
                    sharedAudioCell.setChecked(false, !sharedMediaLayout2.scrolling);
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[this.currentType].loading) {
                return 9;
            }
            if (i < SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset || i >= SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset + SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size()) {
                return 8;
            }
            int i2 = this.currentType;
            return (i2 == 2 || i2 == 4) ? 10 : 7;
        }

        @Override
        public String getLetter(int i) {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].fastScrollPeriods == null) {
                return "";
            }
            ArrayList<Period> arrayList = SharedMediaLayout.this.sharedMediaData[this.currentType].fastScrollPeriods;
            if (arrayList.isEmpty()) {
                return "";
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (i <= arrayList.get(i2).startOffset) {
                    return arrayList.get(i2).formatedDate;
                }
            }
            return arrayList.get(arrayList.size() - 1).formatedDate;
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            float totalItemsCount = f * ((getTotalItemsCount() * measuredHeight) - (recyclerListView.getMeasuredHeight() - recyclerListView.getPaddingTop()));
            iArr[0] = (int) (totalItemsCount / measuredHeight);
            iArr[1] = ((int) totalItemsCount) % measuredHeight;
        }

        @Override
        public void onStartFastScroll() {
            this.inFastScrollMode = true;
            MediaPage mediaPage = SharedMediaLayout.this.getMediaPage(this.currentType);
            if (mediaPage != null) {
                SharedMediaLayout.showFastScrollHint(mediaPage, null, false);
            }
        }

        @Override
        public void onFinishFastScroll(RecyclerListView recyclerListView) {
            if (this.inFastScrollMode) {
                this.inFastScrollMode = false;
                if (recyclerListView != null) {
                    int i = 0;
                    for (int i2 = 0; i2 < recyclerListView.getChildCount() && (i = SharedMediaLayout.getMessageId(recyclerListView.getChildAt(i2))) == 0; i2++) {
                    }
                    if (i == 0) {
                        SharedMediaLayout.this.findPeriodAndJumpToDate(this.currentType, recyclerListView, true);
                    }
                }
            }
        }

        @Override
        public int getTotalItemsCount() {
            return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
        }
    }

    public static View createEmptyStubView(Context context, int i, long j, Theme.ResourcesProvider resourcesProvider) {
        EmptyStubView emptyStubView = new EmptyStubView(context, resourcesProvider);
        if (i == 0) {
            if (DialogObject.isEncryptedDialog(j)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoMediaSecret));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoMedia));
            }
        } else if (i == 1) {
            if (DialogObject.isEncryptedDialog(j)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoSharedFilesSecret));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoSharedFiles));
            }
        } else if (i == 2) {
            if (DialogObject.isEncryptedDialog(j)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoSharedVoiceSecret));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoSharedVoice));
            }
        } else if (i == 3) {
            if (DialogObject.isEncryptedDialog(j)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoSharedLinksSecret));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoSharedLinks));
            }
        } else if (i == 4) {
            if (DialogObject.isEncryptedDialog(j)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoSharedAudioSecret));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoSharedAudio));
            }
        } else if (i == 5) {
            if (DialogObject.isEncryptedDialog(j)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoSharedGifSecret));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoGIFs));
            }
        } else if (i == 6) {
            emptyStubView.emptyImageView.setImageDrawable(null);
            emptyStubView.emptyTextView.setText(LocaleController.getString(R.string.NoGroupsInCommon));
        } else if (i == 7) {
            emptyStubView.emptyImageView.setImageDrawable(null);
            emptyStubView.emptyTextView.setText("");
        }
        return emptyStubView;
    }

    public static class EmptyStubView extends LinearLayout {
        final ImageView emptyImageView;
        final TextView emptyTextView;
        boolean ignoreRequestLayout;

        public EmptyStubView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            TextView textView = new TextView(context);
            this.emptyTextView = textView;
            ImageView imageView = new ImageView(context);
            this.emptyImageView = imageView;
            setOrientation(1);
            setGravity(17);
            addView(imageView, LayoutHelper.createLinear(-2, -2));
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
            textView.setGravity(17);
            textView.setTextSize(1, 17.0f);
            textView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            addView(textView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            this.ignoreRequestLayout = true;
            if (AndroidUtilities.isTablet()) {
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            } else if (rotation == 3 || rotation == 1) {
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
            } else {
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            }
            this.ignoreRequestLayout = false;
            super.onMeasure(i, i2);
        }

        @Override
        public void requestLayout() {
            if (this.ignoreRequestLayout) {
                return;
            }
            super.requestLayout();
        }
    }

    public class SharedPhotoVideoAdapter extends RecyclerListView.FastScrollAdapter {
        protected boolean inFastScrollMode;
        protected Context mContext;
        SharedPhotoVideoCell2.SharedResources sharedResources;

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
        }

        public int getPositionForIndex(int i) {
            return SharedMediaLayout.this.sharedMediaData[0].startOffset + i;
        }

        @Override
        public int getItemCount() {
            if (DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id)) {
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[0].loading) {
                    return 1;
                }
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && (!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1])) {
                    return 0;
                }
                int startOffset = SharedMediaLayout.this.sharedMediaData[0].getStartOffset() + SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
                return startOffset != 0 ? (SharedMediaLayout.this.sharedMediaData[0].endReached[0] && SharedMediaLayout.this.sharedMediaData[0].endReached[1]) ? startOffset : startOffset + 1 : startOffset;
            }
            if (SharedMediaLayout.this.sharedMediaData[0].loadingAfterFastScroll) {
                return SharedMediaLayout.this.sharedMediaData[0].totalCount;
            }
            if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[0].loading) {
                return 1;
            }
            if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && ((!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1]) && SharedMediaLayout.this.sharedMediaData[0].startReached)) {
                return 0;
            }
            if (SharedMediaLayout.this.sharedMediaData[0].totalCount == 0) {
                int startOffset2 = SharedMediaLayout.this.sharedMediaData[0].getStartOffset() + SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
                return startOffset2 != 0 ? (SharedMediaLayout.this.sharedMediaData[0].endReached[0] && SharedMediaLayout.this.sharedMediaData[0].endReached[1]) ? startOffset2 : SharedMediaLayout.this.sharedMediaData[0].getEndLoadingStubs() != 0 ? startOffset2 + SharedMediaLayout.this.sharedMediaData[0].getEndLoadingStubs() : startOffset2 + 1 : startOffset2;
            }
            return SharedMediaLayout.this.sharedMediaData[0].totalCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0 && i != 19) {
                View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 0, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(createEmptyStubView);
            }
            if (this.sharedResources == null) {
                this.sharedResources = new SharedPhotoVideoCell2.SharedResources(viewGroup.getContext(), SharedMediaLayout.this.resourcesProvider);
            }
            SharedPhotoVideoCell2 sharedPhotoVideoCell2 = new SharedPhotoVideoCell2(this.mContext, this.sharedResources, SharedMediaLayout.this.profileActivity.getCurrentAccount());
            if (i == 19) {
                sharedPhotoVideoCell2.setCheck2();
            }
            sharedPhotoVideoCell2.setGradientView(SharedMediaLayout.this.globalGradientView);
            if (this == SharedMediaLayout.this.storiesAdapter || this == SharedMediaLayout.this.archivedStoriesAdapter) {
                sharedPhotoVideoCell2.isStory = true;
            }
            sharedPhotoVideoCell2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(sharedPhotoVideoCell2);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ArrayList<MessageObject> messages = SharedMediaLayout.this.sharedMediaData[0].getMessages();
                int startOffset = i - SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
                View view = viewHolder.itemView;
                if (view instanceof SharedPhotoVideoCell2) {
                    SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) view;
                    int messageId = sharedPhotoVideoCell2.getMessageId();
                    int i2 = this == SharedMediaLayout.this.photoVideoAdapter ? SharedMediaLayout.this.mediaColumnsCount[0] : (this == SharedMediaLayout.this.storiesAdapter || this == SharedMediaLayout.this.archivedStoriesAdapter) ? SharedMediaLayout.this.mediaColumnsCount[1] : SharedMediaLayout.this.animateToColumnsCount;
                    if (startOffset >= 0 && startOffset < messages.size()) {
                        MessageObject messageObject = messages.get(startOffset);
                        boolean z = messageObject.getId() == messageId;
                        SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                        if (sharedMediaLayout.isActionModeShowed) {
                            sharedPhotoVideoCell2.setChecked(sharedMediaLayout.selectedFiles[(messageObject.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0, z);
                        } else {
                            sharedPhotoVideoCell2.setChecked(false, z);
                        }
                        sharedPhotoVideoCell2.setMessageObject(messageObject, i2);
                        return;
                    }
                    sharedPhotoVideoCell2.setMessageObject(null, i2);
                    sharedPhotoVideoCell2.setChecked(false, false);
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            if (!this.inFastScrollMode && SharedMediaLayout.this.sharedMediaData[0].getMessages().size() == 0 && !SharedMediaLayout.this.sharedMediaData[0].loading && SharedMediaLayout.this.sharedMediaData[0].startReached) {
                return 2;
            }
            SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
            SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
            SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
            return 0;
        }

        @Override
        public String getLetter(int i) {
            if (SharedMediaLayout.this.sharedMediaData[0].fastScrollPeriods == null) {
                return "";
            }
            ArrayList<Period> arrayList = SharedMediaLayout.this.sharedMediaData[0].fastScrollPeriods;
            if (arrayList.isEmpty()) {
                return "";
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (i <= arrayList.get(i2).startOffset) {
                    return arrayList.get(i2).formatedDate;
                }
            }
            return arrayList.get(arrayList.size() - 1).formatedDate;
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            int i = (this == SharedMediaLayout.this.animationSupportingPhotoVideoAdapter || this == SharedMediaLayout.this.animationSupportingStoriesAdapter || this == SharedMediaLayout.this.animationSupportingArchivedStoriesAdapter) ? SharedMediaLayout.this.animateToColumnsCount : (this == SharedMediaLayout.this.storiesAdapter || this == SharedMediaLayout.this.archivedStoriesAdapter) ? SharedMediaLayout.this.mediaColumnsCount[1] : SharedMediaLayout.this.mediaColumnsCount[0];
            double ceil = Math.ceil(getTotalItemsCount() / i);
            double d = measuredHeight;
            Double.isNaN(d);
            int i2 = (int) (ceil * d);
            int measuredHeight2 = recyclerListView.getMeasuredHeight() - recyclerListView.getPaddingTop();
            if (measuredHeight == 0) {
                iArr[1] = 0;
                iArr[0] = 0;
            } else {
                float f2 = f * (i2 - measuredHeight2);
                iArr[0] = ((int) (f2 / measuredHeight)) * i;
                iArr[1] = ((int) f2) % measuredHeight;
            }
        }

        @Override
        public void onStartFastScroll() {
            this.inFastScrollMode = true;
            MediaPage mediaPage = SharedMediaLayout.this.getMediaPage(0);
            if (mediaPage != null) {
                SharedMediaLayout.showFastScrollHint(mediaPage, null, false);
            }
        }

        @Override
        public void onFinishFastScroll(RecyclerListView recyclerListView) {
            if (this.inFastScrollMode) {
                this.inFastScrollMode = false;
                if (recyclerListView != null) {
                    int i = 0;
                    for (int i2 = 0; i2 < recyclerListView.getChildCount(); i2++) {
                        View childAt = recyclerListView.getChildAt(i2);
                        if (childAt instanceof SharedPhotoVideoCell2) {
                            i = ((SharedPhotoVideoCell2) childAt).getMessageId();
                        }
                        if (i != 0) {
                            break;
                        }
                    }
                    if (i == 0) {
                        SharedMediaLayout.this.findPeriodAndJumpToDate(0, recyclerListView, true);
                    }
                }
            }
        }

        @Override
        public int getTotalItemsCount() {
            return SharedMediaLayout.this.sharedMediaData[0].totalCount;
        }

        @Override
        public float getScrollProgress(RecyclerListView recyclerListView) {
            int i = (this == SharedMediaLayout.this.animationSupportingPhotoVideoAdapter || this == SharedMediaLayout.this.animationSupportingStoriesAdapter || this == SharedMediaLayout.this.animationSupportingArchivedStoriesAdapter) ? SharedMediaLayout.this.animateToColumnsCount : (this == SharedMediaLayout.this.storiesAdapter || this == SharedMediaLayout.this.archivedStoriesAdapter) ? SharedMediaLayout.this.mediaColumnsCount[1] : SharedMediaLayout.this.mediaColumnsCount[0];
            int ceil = (int) Math.ceil(getTotalItemsCount() / i);
            if (recyclerListView.getChildCount() == 0) {
                return 0.0f;
            }
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(0)) < 0) {
                return 0.0f;
            }
            return (((r5 / i) * measuredHeight) - (r1.getTop() - recyclerListView.getPaddingTop())) / ((ceil * measuredHeight) - (recyclerListView.getMeasuredHeight() - recyclerListView.getPaddingTop()));
        }

        @Override
        public boolean fastScrollIsVisible(RecyclerListView recyclerListView) {
            if (SharedMediaLayout.this.isSearchingStories()) {
                return false;
            }
            return recyclerListView.getChildCount() != 0 && ((int) Math.ceil((double) (((float) getTotalItemsCount()) / ((float) ((this == SharedMediaLayout.this.photoVideoAdapter || this == SharedMediaLayout.this.storiesAdapter || this == SharedMediaLayout.this.archivedStoriesAdapter) ? SharedMediaLayout.this.mediaColumnsCount[0] : SharedMediaLayout.this.animateToColumnsCount))))) * recyclerListView.getChildAt(0).getMeasuredHeight() > recyclerListView.getMeasuredHeight();
        }

        @Override
        public void onFastScrollSingleTap() {
            SharedMediaLayout.this.showMediaCalendar(0, true);
        }
    }

    public void findPeriodAndJumpToDate(int i, RecyclerListView recyclerListView, boolean z) {
        ArrayList<Period> arrayList = this.sharedMediaData[i].fastScrollPeriods;
        int findFirstVisibleItemPosition = ((LinearLayoutManager) recyclerListView.getLayoutManager()).findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition >= 0) {
            Period period = null;
            if (arrayList != null) {
                int i2 = 0;
                while (true) {
                    if (i2 >= arrayList.size()) {
                        break;
                    }
                    if (findFirstVisibleItemPosition <= arrayList.get(i2).startOffset) {
                        period = arrayList.get(i2);
                        break;
                    }
                    i2++;
                }
                if (period == null) {
                    period = arrayList.get(arrayList.size() - 1);
                }
            }
            if (period != null) {
                jumpToDate(i, period.maxId, period.startOffset + 1, z);
            }
        }
    }

    public void jumpToDate(int i, int i2, int i3, boolean z) {
        this.sharedMediaData[i].messages.clear();
        this.sharedMediaData[i].messagesDict[0].clear();
        this.sharedMediaData[i].messagesDict[1].clear();
        this.sharedMediaData[i].setMaxId(0, i2);
        this.sharedMediaData[i].setEndReached(0, false);
        SharedMediaData sharedMediaData = this.sharedMediaData[i];
        sharedMediaData.startReached = false;
        sharedMediaData.startOffset = i3;
        SharedMediaData sharedMediaData2 = this.sharedMediaData[i];
        sharedMediaData2.endLoadingStubs = (sharedMediaData2.totalCount - i3) - 1;
        if (this.sharedMediaData[i].endLoadingStubs < 0) {
            this.sharedMediaData[i].endLoadingStubs = 0;
        }
        SharedMediaData sharedMediaData3 = this.sharedMediaData[i];
        sharedMediaData3.min_id = i2;
        sharedMediaData3.loadingAfterFastScroll = true;
        sharedMediaData3.loading = false;
        sharedMediaData3.requestIndex++;
        MediaPage mediaPage = getMediaPage(i);
        if (mediaPage != null && mediaPage.listView.getAdapter() != null) {
            mediaPage.listView.getAdapter().notifyDataSetChanged();
        }
        if (!z) {
            return;
        }
        int i4 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i4 >= mediaPageArr.length) {
                return;
            }
            if (mediaPageArr[i4].selectedType == i) {
                ExtendedGridLayoutManager extendedGridLayoutManager = this.mediaPages[i4].layoutManager;
                SharedMediaData sharedMediaData4 = this.sharedMediaData[i];
                extendedGridLayoutManager.scrollToPositionWithOffset(Math.min(sharedMediaData4.totalCount - 1, sharedMediaData4.startOffset), 0);
            }
            i4++;
        }
    }

    public class MediaSearchAdapter extends RecyclerListView.SelectionAdapter {
        private int currentType;
        private int lastReqId;
        private Context mContext;
        private Runnable searchRunnable;
        private int searchesInProgress;
        private ArrayList<MessageObject> searchResult = new ArrayList<>();
        protected ArrayList<MessageObject> globalSearch = new ArrayList<>();
        private int reqId = 0;

        @Override
        public int getItemViewType(int i) {
            return 24;
        }

        public MediaSearchAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
        }

        public void queryServerSearch(final String str, final int i, long j, long j2) {
            if (DialogObject.isEncryptedDialog(j)) {
                return;
            }
            if (this.reqId != 0) {
                SharedMediaLayout.this.profileActivity.getConnectionsManager().cancelRequest(this.reqId, true);
                this.reqId = 0;
                this.searchesInProgress--;
            }
            if (str == null || str.length() == 0) {
                this.globalSearch.clear();
                this.lastReqId = 0;
                notifyDataSetChanged();
                return;
            }
            TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
            tLRPC$TL_messages_search.limit = 50;
            tLRPC$TL_messages_search.offset_id = i;
            int i2 = this.currentType;
            if (i2 == 1) {
                tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterDocument();
            } else if (i2 == 3) {
                tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterUrl();
            } else if (i2 == 4) {
                tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterMusic();
            }
            tLRPC$TL_messages_search.q = str;
            tLRPC$TL_messages_search.peer = SharedMediaLayout.this.profileActivity.getMessagesController().getInputPeer(j);
            if (j2 != 0) {
                if (j == SharedMediaLayout.this.profileActivity.getUserConfig().getClientUserId()) {
                    tLRPC$TL_messages_search.flags |= 4;
                    tLRPC$TL_messages_search.saved_peer_id = SharedMediaLayout.this.profileActivity.getMessagesController().getInputPeer(j2);
                } else {
                    tLRPC$TL_messages_search.flags |= 2;
                    tLRPC$TL_messages_search.top_msg_id = (int) j2;
                }
            }
            if (tLRPC$TL_messages_search.peer == null) {
                return;
            }
            final int i3 = this.lastReqId + 1;
            this.lastReqId = i3;
            this.searchesInProgress++;
            this.reqId = SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(tLRPC$TL_messages_search, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SharedMediaLayout.MediaSearchAdapter.this.lambda$queryServerSearch$1(i, i3, str, tLObject, tLRPC$TL_error);
                }
            }, 2);
            SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(this.reqId, SharedMediaLayout.this.profileActivity.getClassGuid());
        }

        public void lambda$queryServerSearch$1(int i, final int i2, final String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            final ArrayList arrayList = new ArrayList();
            if (tLRPC$TL_error == null) {
                TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
                for (int i3 = 0; i3 < tLRPC$messages_Messages.messages.size(); i3++) {
                    TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i3);
                    if (i == 0 || tLRPC$Message.id <= i) {
                        arrayList.add(new MessageObject(SharedMediaLayout.this.profileActivity.getCurrentAccount(), tLRPC$Message, false, true));
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.lambda$queryServerSearch$0(i2, arrayList, str);
                }
            });
        }

        public void lambda$queryServerSearch$0(int i, ArrayList arrayList, String str) {
            if (this.reqId != 0) {
                if (i == this.lastReqId) {
                    int itemCount = getItemCount();
                    this.globalSearch = arrayList;
                    this.searchesInProgress--;
                    int itemCount2 = getItemCount();
                    if (this.searchesInProgress == 0 || itemCount2 != 0) {
                        SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                    }
                    for (int i2 = 0; i2 < SharedMediaLayout.this.mediaPages.length; i2++) {
                        if (SharedMediaLayout.this.mediaPages[i2].selectedType == this.currentType) {
                            if (this.searchesInProgress == 0 && itemCount2 == 0) {
                                SharedMediaLayout.this.mediaPages[i2].emptyView.title.setText(LocaleController.formatString("NoResultFoundFor", R.string.NoResultFoundFor, str));
                                SharedMediaLayout.this.mediaPages[i2].emptyView.button.setVisibility(8);
                                SharedMediaLayout.this.mediaPages[i2].emptyView.showProgress(false, true);
                            } else if (itemCount == 0) {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[i2].listView, 0, null);
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
                this.reqId = 0;
            }
        }

        public void search(final String str, boolean z) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (!this.searchResult.isEmpty() || !this.globalSearch.isEmpty()) {
                this.searchResult.clear();
                this.globalSearch.clear();
                notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(str)) {
                for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                    if (SharedMediaLayout.this.mediaPages[i].selectedType == this.currentType) {
                        SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(true, z);
                    }
                }
                Runnable runnable2 = new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.MediaSearchAdapter.this.lambda$search$3(str);
                    }
                };
                this.searchRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 300L);
                return;
            }
            if (this.searchResult.isEmpty() && this.globalSearch.isEmpty() && this.searchesInProgress == 0) {
                return;
            }
            this.searchResult.clear();
            this.globalSearch.clear();
            if (this.reqId != 0) {
                SharedMediaLayout.this.profileActivity.getConnectionsManager().cancelRequest(this.reqId, true);
                this.reqId = 0;
                this.searchesInProgress--;
            }
        }

        public void lambda$search$3(final String str) {
            int i;
            if (!SharedMediaLayout.this.sharedMediaData[this.currentType].messages.isEmpty() && ((i = this.currentType) == 1 || i == 4)) {
                MessageObject messageObject = SharedMediaLayout.this.sharedMediaData[this.currentType].messages.get(SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() - 1);
                queryServerSearch(str, messageObject.getId(), messageObject.getDialogId(), SharedMediaLayout.this.dialog_id == SharedMediaLayout.this.profileActivity.getUserConfig().getClientUserId() ? messageObject.getSavedDialogId() : 0L);
            } else if (this.currentType == 3) {
                queryServerSearch(str, 0, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.topicId);
            }
            int i2 = this.currentType;
            if (i2 == 1 || i2 == 4) {
                final ArrayList arrayList = new ArrayList(SharedMediaLayout.this.sharedMediaData[this.currentType].messages);
                this.searchesInProgress++;
                Utilities.searchQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.MediaSearchAdapter.this.lambda$search$2(str, arrayList);
                    }
                });
            }
        }

        public void lambda$search$2(String str, ArrayList arrayList) {
            TLRPC$Document tLRPC$Document;
            boolean z;
            String str2;
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.length() == 0) {
                updateSearchResults(new ArrayList<>());
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
            if (lowerCase.equals(translitString) || translitString.length() == 0) {
                translitString = null;
            }
            int i = (translitString != null ? 1 : 0) + 1;
            String[] strArr = new String[i];
            strArr[0] = lowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList<MessageObject> arrayList2 = new ArrayList<>();
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i2);
                int i3 = 0;
                while (true) {
                    if (i3 < i) {
                        String str3 = strArr[i3];
                        String documentName = messageObject.getDocumentName();
                        if (documentName != null && documentName.length() != 0) {
                            if (documentName.toLowerCase().contains(str3)) {
                                arrayList2.add(messageObject);
                                break;
                            }
                            if (this.currentType == 4) {
                                if (messageObject.type == 0) {
                                    tLRPC$Document = MessageObject.getMedia(messageObject.messageOwner).webpage.document;
                                } else {
                                    tLRPC$Document = MessageObject.getMedia(messageObject.messageOwner).document;
                                }
                                int i4 = 0;
                                while (true) {
                                    if (i4 >= tLRPC$Document.attributes.size()) {
                                        z = false;
                                        break;
                                    }
                                    TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i4);
                                    if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                                        String str4 = tLRPC$DocumentAttribute.performer;
                                        z = str4 != null ? str4.toLowerCase().contains(str3) : false;
                                        if (!z && (str2 = tLRPC$DocumentAttribute.title) != null) {
                                            z = str2.toLowerCase().contains(str3);
                                        }
                                    } else {
                                        i4++;
                                    }
                                }
                                if (z) {
                                    arrayList2.add(messageObject);
                                    break;
                                }
                            } else {
                                continue;
                            }
                        }
                        i3++;
                    }
                }
            }
            updateSearchResults(arrayList2);
        }

        private void updateSearchResults(final ArrayList<MessageObject> arrayList) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.lambda$updateSearchResults$4(arrayList);
                }
            });
        }

        public void lambda$updateSearchResults$4(ArrayList arrayList) {
            if (SharedMediaLayout.this.searching) {
                this.searchesInProgress--;
                int itemCount = getItemCount();
                this.searchResult = arrayList;
                int itemCount2 = getItemCount();
                if (this.searchesInProgress == 0 || itemCount2 != 0) {
                    SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                }
                for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                    if (SharedMediaLayout.this.mediaPages[i].selectedType == this.currentType) {
                        if (this.searchesInProgress == 0 && itemCount2 == 0) {
                            SharedMediaLayout.this.mediaPages[i].emptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
                            SharedMediaLayout.this.mediaPages[i].emptyView.button.setVisibility(8);
                            SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(false, true);
                        } else if (itemCount == 0) {
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[i].listView, 0, null);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return this.searchResult.size() + this.globalSearch.size() != 0;
        }

        @Override
        public int getItemCount() {
            int size = this.searchResult.size();
            int size2 = this.globalSearch.size();
            return size2 != 0 ? size + size2 : size;
        }

        public MessageObject getItem(int i) {
            if (i < this.searchResult.size()) {
                return this.searchResult.get(i);
            }
            return this.globalSearch.get(i - this.searchResult.size());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            int i2 = this.currentType;
            int i3 = 0;
            if (i2 == 1) {
                frameLayout = new SharedDocumentCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
            } else if (i2 == 4) {
                frameLayout = new SharedAudioCell(this.mContext, i3, SharedMediaLayout.this.resourcesProvider) {
                    @Override
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? MediaSearchAdapter.this.searchResult : null, false);
                            if (messageObject.isRoundVideo()) {
                                MediaController.getInstance().setCurrentVideoVisible(false);
                            }
                            return playMessage;
                        }
                        if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(MediaSearchAdapter.this.searchResult, messageObject, SharedMediaLayout.this.mergeDialogId);
                        }
                        return false;
                    }
                };
            } else {
                SharedLinkCell sharedLinkCell = new SharedLinkCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
                sharedLinkCell.setDelegate(SharedMediaLayout.this.sharedLinkCellDelegate);
                frameLayout = sharedLinkCell;
            }
            frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(frameLayout);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2 = this.currentType;
            if (i2 == 1) {
                View view = viewHolder.itemView;
                if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
                    MessageObject item = getItem(i);
                    sharedDocumentCell.setDocument(item, i != getItemCount() - 1);
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    if (sharedMediaLayout.isActionModeShowed) {
                        sharedDocumentCell.setChecked(sharedMediaLayout.selectedFiles[(item.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (item.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(item.getId()) >= 0, !SharedMediaLayout.this.scrolling);
                        return;
                    } else {
                        sharedDocumentCell.setChecked(false, !sharedMediaLayout.scrolling);
                        return;
                    }
                }
                return;
            }
            if (i2 == 3) {
                View view2 = viewHolder.itemView;
                if (view2 instanceof SharedLinkCell) {
                    SharedLinkCell sharedLinkCell = (SharedLinkCell) view2;
                    MessageObject item2 = getItem(i);
                    sharedLinkCell.setLink(item2, i != getItemCount() - 1);
                    SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                    if (sharedMediaLayout2.isActionModeShowed) {
                        sharedLinkCell.setChecked(sharedMediaLayout2.selectedFiles[(item2.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (item2.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(item2.getId()) >= 0, !SharedMediaLayout.this.scrolling);
                        return;
                    } else {
                        sharedLinkCell.setChecked(false, !sharedMediaLayout2.scrolling);
                        return;
                    }
                }
                return;
            }
            if (i2 == 4) {
                View view3 = viewHolder.itemView;
                if (view3 instanceof SharedAudioCell) {
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) view3;
                    MessageObject item3 = getItem(i);
                    sharedAudioCell.setMessageObject(item3, i != getItemCount() - 1);
                    SharedMediaLayout sharedMediaLayout3 = SharedMediaLayout.this;
                    if (sharedMediaLayout3.isActionModeShowed) {
                        sharedAudioCell.setChecked(sharedMediaLayout3.selectedFiles[(item3.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (item3.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(item3.getId()) >= 0, !SharedMediaLayout.this.scrolling);
                    } else {
                        sharedAudioCell.setChecked(false, !sharedMediaLayout3.scrolling);
                    }
                }
            }
        }
    }

    public class GifAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        @Override
        public long getItemId(int i) {
            return i;
        }

        public GifAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading;
        }

        @Override
        public int getItemCount() {
            if (SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading) {
                return SharedMediaLayout.this.sharedMediaData[5].messages.size();
            }
            return 1;
        }

        @Override
        public int getItemViewType(int i) {
            return (SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading) ? 12 : 11;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 11) {
                View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 5, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(createEmptyStubView);
            }
            ContextLinkCell contextLinkCell = new ContextLinkCell(this.mContext, true, SharedMediaLayout.this.resourcesProvider);
            contextLinkCell.setCanPreviewGif(true);
            return new RecyclerListView.Holder(contextLinkCell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MessageObject messageObject;
            TLRPC$Document document;
            if (viewHolder.getItemViewType() != 12 || (document = (messageObject = SharedMediaLayout.this.sharedMediaData[5].messages.get(i)).getDocument()) == null) {
                return;
            }
            View view = viewHolder.itemView;
            if (view instanceof ContextLinkCell) {
                ContextLinkCell contextLinkCell = (ContextLinkCell) view;
                contextLinkCell.setGif(document, messageObject, messageObject.messageOwner.date, false);
                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                if (sharedMediaLayout.isActionModeShowed) {
                    contextLinkCell.setChecked(sharedMediaLayout.selectedFiles[(messageObject.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0, !SharedMediaLayout.this.scrolling);
                } else {
                    contextLinkCell.setChecked(false, !sharedMediaLayout.scrolling);
                }
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ContextLinkCell) {
                ImageReceiver photoImage = ((ContextLinkCell) view).getPhotoImage();
                if (SharedMediaLayout.this.mediaPages[0].selectedType == 5) {
                    photoImage.setAllowStartAnimation(true);
                    photoImage.startAnimation();
                } else {
                    photoImage.setAllowStartAnimation(false);
                    photoImage.stopAnimation();
                }
            }
        }
    }

    public class SavedDialogsAdapter extends RecyclerListView.SelectionAdapter {
        public RecyclerListView attachedToRecyclerView;
        private final SavedMessagesController controller;
        private final Context mContext;
        private boolean orderChanged;
        private final ArrayList<SavedMessagesController.SavedDialog> oldDialogs = new ArrayList<>();
        private final ArrayList<SavedMessagesController.SavedDialog> dialogs = new ArrayList<>();
        private Runnable notifyOrderUpdate = new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.SavedDialogsAdapter.this.lambda$new$0();
            }
        };
        public final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        public final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!SharedMediaLayout.this.isActionModeShowed || recyclerView.getAdapter() == SharedMediaLayout.this.savedMessagesSearchAdapter) {
                    return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
                }
                SavedMessagesController.SavedDialog dialog = getDialog(viewHolder);
                if (dialog != null && dialog.pinned) {
                    return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
                }
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }

            private SavedMessagesController.SavedDialog getDialog(RecyclerView.ViewHolder viewHolder) {
                int adapterPosition;
                if (viewHolder != null && (adapterPosition = viewHolder.getAdapterPosition()) >= 0 && adapterPosition < SavedDialogsAdapter.this.dialogs.size()) {
                    return (SavedMessagesController.SavedDialog) SavedDialogsAdapter.this.dialogs.get(adapterPosition);
                }
                return null;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
                RecyclerListView recyclerListView;
                if (viewHolder != null && (recyclerListView = SavedDialogsAdapter.this.attachedToRecyclerView) != null) {
                    recyclerListView.hideSelector(false);
                }
                if (i == 0) {
                    AndroidUtilities.cancelRunOnUIThread(SavedDialogsAdapter.this.notifyOrderUpdate);
                    AndroidUtilities.runOnUIThread(SavedDialogsAdapter.this.notifyOrderUpdate, 300L);
                }
                super.onSelectedChanged(viewHolder, i);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
                if (SharedMediaLayout.this.isActionModeShowed && recyclerView.getAdapter() != SharedMediaLayout.this.savedMessagesSearchAdapter) {
                    SavedMessagesController.SavedDialog dialog = getDialog(viewHolder);
                    SavedMessagesController.SavedDialog dialog2 = getDialog(viewHolder2);
                    if (dialog != null && dialog2 != null && dialog.pinned && dialog2.pinned) {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        int adapterPosition2 = viewHolder2.getAdapterPosition();
                        SavedDialogsAdapter.this.dialogs.remove(adapterPosition);
                        SavedDialogsAdapter.this.dialogs.add(adapterPosition2, dialog);
                        SavedDialogsAdapter.this.notifyItemMoved(adapterPosition, adapterPosition2);
                        SavedDialogsAdapter.this.orderChanged = true;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setPressed(false);
            }
        });
        public final HashSet<Long> selectedDialogs = new HashSet<>();

        @Override
        public int getItemViewType(int i) {
            return 13;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public void lambda$new$0() {
            if (this.orderChanged) {
                this.orderChanged = false;
                ArrayList<Long> arrayList = new ArrayList<>();
                for (int i = 0; i < this.dialogs.size(); i++) {
                    if (this.dialogs.get(i).pinned) {
                        arrayList.add(Long.valueOf(this.dialogs.get(i).dialogId));
                    }
                }
                SharedMediaLayout.this.profileActivity.getMessagesController().getSavedMessagesController().updatePinnedOrder(arrayList);
            }
        }

        public SavedDialogsAdapter(Context context) {
            this.mContext = context;
            SavedMessagesController savedMessagesController = SharedMediaLayout.this.profileActivity.getMessagesController().getSavedMessagesController();
            this.controller = savedMessagesController;
            if (SharedMediaLayout.this.includeSavedDialogs()) {
                savedMessagesController.loadDialogs(false);
            }
            setHasStableIds(true);
            update(false);
        }

        @Override
        public long getItemId(int i) {
            return (i < 0 || i >= this.dialogs.size()) ? i : this.dialogs.get(i).dialogId;
        }

        public void update(boolean z) {
            this.oldDialogs.clear();
            this.oldDialogs.addAll(this.dialogs);
            this.dialogs.clear();
            this.dialogs.addAll(this.controller.allDialogs);
            if (z) {
                notifyDataSetChanged();
            }
        }

        public void select(View view) {
            SavedMessagesController.SavedDialog savedDialog;
            if (view instanceof DialogCell) {
                DialogCell dialogCell = (DialogCell) view;
                long dialogId = dialogCell.getDialogId();
                int i = 0;
                while (true) {
                    if (i >= this.dialogs.size()) {
                        savedDialog = null;
                        break;
                    } else {
                        if (this.dialogs.get(i).dialogId == dialogId) {
                            savedDialog = this.dialogs.get(i);
                            break;
                        }
                        i++;
                    }
                }
                if (savedDialog == null) {
                    return;
                }
                if (this.selectedDialogs.contains(Long.valueOf(savedDialog.dialogId))) {
                    this.selectedDialogs.remove(Long.valueOf(savedDialog.dialogId));
                    if (this.selectedDialogs.size() <= 0) {
                        SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                        if (sharedMediaLayout.isActionModeShowed) {
                            sharedMediaLayout.showActionMode(false);
                        }
                    }
                } else {
                    this.selectedDialogs.add(Long.valueOf(savedDialog.dialogId));
                    if (this.selectedDialogs.size() > 0) {
                        SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                        if (!sharedMediaLayout2.isActionModeShowed) {
                            sharedMediaLayout2.showActionMode(true);
                            if (SharedMediaLayout.this.gotoItem != null) {
                                SharedMediaLayout.this.gotoItem.setVisibility(8);
                            }
                            if (SharedMediaLayout.this.forwardItem != null) {
                                SharedMediaLayout.this.forwardItem.setVisibility(8);
                            }
                        }
                    }
                }
                SharedMediaLayout.this.selectedMessagesCountTextView.setNumber(this.selectedDialogs.size(), true);
                boolean z = this.selectedDialogs.size() > 0;
                Iterator<Long> it = this.selectedDialogs.iterator();
                while (it.hasNext()) {
                    long longValue = it.next().longValue();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= this.dialogs.size()) {
                            break;
                        }
                        SavedMessagesController.SavedDialog savedDialog2 = this.dialogs.get(i2);
                        if (savedDialog2.dialogId != longValue) {
                            i2++;
                        } else if (!savedDialog2.pinned) {
                            z = false;
                        }
                    }
                    if (!z) {
                        break;
                    }
                }
                if (SharedMediaLayout.this.pinItem != null) {
                    SharedMediaLayout.this.pinItem.setVisibility(z ? 8 : 0);
                }
                if (SharedMediaLayout.this.unpinItem != null) {
                    SharedMediaLayout.this.unpinItem.setVisibility(z ? 0 : 8);
                }
                if (view instanceof DialogCell) {
                    dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(savedDialog.dialogId)), true);
                }
            }
        }

        public void unselectAll() {
            this.selectedDialogs.clear();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            DialogCell dialogCell = new DialogCell(null, this.mContext, false, true) {
                @Override
                public boolean isForumCell() {
                    return false;
                }

                @Override
                public boolean getIsPinned() {
                    int childAdapterPosition;
                    RecyclerListView recyclerListView = SavedDialogsAdapter.this.attachedToRecyclerView;
                    if (recyclerListView == null) {
                        return false;
                    }
                    RecyclerView.Adapter adapter = recyclerListView.getAdapter();
                    SavedDialogsAdapter savedDialogsAdapter = SavedDialogsAdapter.this;
                    if (adapter != savedDialogsAdapter || (childAdapterPosition = savedDialogsAdapter.attachedToRecyclerView.getChildAdapterPosition(this)) < 0 || childAdapterPosition >= SavedDialogsAdapter.this.dialogs.size()) {
                        return false;
                    }
                    return ((SavedMessagesController.SavedDialog) SavedDialogsAdapter.this.dialogs.get(childAdapterPosition)).pinned;
                }
            };
            dialogCell.setDialogCellDelegate(SharedMediaLayout.this);
            dialogCell.isSavedDialog = true;
            dialogCell.setBackgroundColor(SharedMediaLayout.this.getThemedColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(dialogCell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            View view = viewHolder.itemView;
            if (view instanceof DialogCell) {
                DialogCell dialogCell = (DialogCell) view;
                SavedMessagesController.SavedDialog savedDialog = this.dialogs.get(i);
                dialogCell.setDialog(savedDialog.dialogId, savedDialog.message, savedDialog.getDate(), false, false);
                dialogCell.isSavedDialogCell = true;
                dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(savedDialog.dialogId)), false);
                dialogCell.useSeparator = i + 1 < getItemCount();
            }
        }

        @Override
        public int getItemCount() {
            return this.dialogs.size();
        }
    }

    public class SavedMessagesSearchAdapter extends RecyclerListView.SelectionAdapter {
        private final int currentAccount;
        private String lastQuery;
        private ReactionsLayoutInBubble.VisibleReaction lastReaction;
        int lastSearchId;
        private boolean loading;
        private final Context mContext;
        public final ArrayList<SavedMessagesController.SavedDialog> dialogs = new ArrayList<>();
        public final ArrayList<MessageObject> messages = new ArrayList<>();
        public final ArrayList<MessageObject> loadedMessages = new ArrayList<>();
        public final ArrayList<MessageObject> cachedMessages = new ArrayList<>();
        private boolean endReached = false;
        private int oldItemCounts = 0;
        private int count = 0;
        private int reqId = -1;
        private Runnable searchRunnable = new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.SavedMessagesSearchAdapter.this.sendRequest();
            }
        };

        @Override
        public int getItemViewType(int i) {
            return 23;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public SavedMessagesSearchAdapter(Context context) {
            this.mContext = context;
            this.currentAccount = SharedMediaLayout.this.profileActivity.getCurrentAccount();
            setHasStableIds(true);
        }

        public void search(String str, ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
            if (TextUtils.equals(str, this.lastQuery)) {
                ReactionsLayoutInBubble.VisibleReaction visibleReaction2 = this.lastReaction;
                if (visibleReaction2 == null && visibleReaction == null) {
                    return;
                }
                if (visibleReaction2 != null && visibleReaction2.equals(visibleReaction)) {
                    return;
                }
            }
            this.lastQuery = str;
            this.lastReaction = visibleReaction;
            if (this.reqId >= 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = -1;
            }
            this.cachedMessages.clear();
            this.loadedMessages.clear();
            this.messages.clear();
            this.count = 0;
            this.endReached = false;
            this.loading = true;
            this.dialogs.clear();
            if (this.lastReaction == null) {
                this.dialogs.addAll(MessagesController.getInstance(this.currentAccount).getSavedMessagesController().searchDialogs(str));
            }
            for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                if (SharedMediaLayout.this.mediaPages[i].selectedType == 11) {
                    SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(true, true);
                }
            }
            if (this.lastReaction == null) {
                notifyDataSetChanged();
            }
            AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            AndroidUtilities.runOnUIThread(this.searchRunnable, this.lastReaction != null ? 60L : 600L);
        }

        public void loadMore() {
            if (this.endReached || this.loading) {
                return;
            }
            this.loading = true;
            sendRequest();
        }

        public void sendRequest() {
            if (TextUtils.isEmpty(this.lastQuery) && this.lastReaction == null) {
                this.loading = false;
                return;
            }
            final TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
            tLRPC$TL_messages_search.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(UserConfig.getInstance(this.currentAccount).getClientUserId());
            tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterEmpty();
            tLRPC$TL_messages_search.q = this.lastQuery;
            ReactionsLayoutInBubble.VisibleReaction visibleReaction = this.lastReaction;
            if (visibleReaction != null) {
                tLRPC$TL_messages_search.flags |= 8;
                tLRPC$TL_messages_search.saved_reaction.add(visibleReaction.toTLReaction());
            }
            if (this.loadedMessages.size() > 0) {
                tLRPC$TL_messages_search.offset_id = this.loadedMessages.get(r2.size() - 1).getId();
            }
            tLRPC$TL_messages_search.limit = 10;
            this.endReached = false;
            final int i = this.lastSearchId + 1;
            this.lastSearchId = i;
            final Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.SavedMessagesSearchAdapter.this.lambda$sendRequest$2(i, tLRPC$TL_messages_search);
                }
            };
            if (this.lastReaction != null) {
                MessagesStorage.getInstance(this.currentAccount).searchSavedByTag(this.lastReaction.toTLReaction(), 0L, this.lastQuery, 100, this.cachedMessages.size(), new Utilities.Callback4() {
                    @Override
                    public final void run(Object obj, Object obj2, Object obj3, Object obj4) {
                        SharedMediaLayout.SavedMessagesSearchAdapter.this.lambda$sendRequest$3(runnable, (ArrayList) obj, (ArrayList) obj2, (ArrayList) obj3, (ArrayList) obj4);
                    }
                }, false);
            } else {
                runnable.run();
            }
        }

        public void lambda$sendRequest$2(final int i, TLRPC$TL_messages_search tLRPC$TL_messages_search) {
            if (i != this.lastSearchId) {
                return;
            }
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_search, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SharedMediaLayout.SavedMessagesSearchAdapter.this.lambda$sendRequest$1(i, tLObject, tLRPC$TL_error);
                }
            });
        }

        public void lambda$sendRequest$1(final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.SavedMessagesSearchAdapter.this.lambda$sendRequest$0(tLObject, i);
                }
            });
        }

        public void lambda$sendRequest$0(TLObject tLObject, int i) {
            if ((tLObject instanceof TLRPC$messages_Messages) && i == this.lastSearchId) {
                TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
                MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$messages_Messages.users, false);
                MessagesController.getInstance(this.currentAccount).putChats(tLRPC$messages_Messages.chats, false);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
                for (int i2 = 0; i2 < tLRPC$messages_Messages.messages.size(); i2++) {
                    MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$messages_Messages.messages.get(i2), false, true);
                    if (messageObject.hasValidGroupId()) {
                        messageObject.isPrimaryGroupMessage = true;
                    }
                    messageObject.setQuery(this.lastQuery);
                    this.loadedMessages.add(messageObject);
                }
                this.count = tLRPC$messages_Messages.count;
                if (tLRPC$messages_Messages instanceof TLRPC$TL_messages_messagesSlice) {
                    this.endReached = this.loadedMessages.size() >= tLRPC$messages_Messages.count;
                } else if (tLRPC$messages_Messages instanceof TLRPC$TL_messages_messages) {
                    this.endReached = true;
                }
                updateMessages(false);
                this.loading = false;
                this.reqId = -1;
            }
        }

        public void lambda$sendRequest$3(Runnable runnable, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
            MessagesController.getInstance(this.currentAccount).putUsers(arrayList2, true);
            MessagesController.getInstance(this.currentAccount).putChats(arrayList3, true);
            AnimatedEmojiDrawable.getDocumentFetcher(this.currentAccount).processDocuments(arrayList4);
            for (int i = 0; i < arrayList.size(); i++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i);
                if (messageObject.hasValidGroupId() && messageObject.messageOwner.reactions != null) {
                    messageObject.isPrimaryGroupMessage = true;
                }
                messageObject.setQuery(this.lastQuery);
                this.cachedMessages.add(messageObject);
            }
            updateMessages(true);
            AndroidUtilities.runOnUIThread(runnable, 540L);
        }

        private void updateMessages(boolean z) {
            this.messages.clear();
            HashSet hashSet = new HashSet();
            for (int i = 0; i < this.loadedMessages.size(); i++) {
                MessageObject messageObject = this.loadedMessages.get(i);
                if (messageObject != null && !hashSet.contains(Integer.valueOf(messageObject.getId()))) {
                    hashSet.add(Integer.valueOf(messageObject.getId()));
                    this.messages.add(messageObject);
                }
            }
            for (int i2 = 0; i2 < this.cachedMessages.size(); i2++) {
                MessageObject messageObject2 = this.cachedMessages.get(i2);
                if (messageObject2 != null && !hashSet.contains(Integer.valueOf(messageObject2.getId()))) {
                    hashSet.add(Integer.valueOf(messageObject2.getId()));
                    this.messages.add(messageObject2);
                }
            }
            if (!z || !this.cachedMessages.isEmpty()) {
                for (int i3 = 0; i3 < SharedMediaLayout.this.mediaPages.length; i3++) {
                    if (SharedMediaLayout.this.mediaPages[i3].selectedType == 11 && this.messages.isEmpty() && this.dialogs.isEmpty()) {
                        SharedMediaLayout.this.mediaPages[i3].emptyView.title.setText((this.lastReaction == null || !TextUtils.isEmpty(this.lastQuery)) ? LocaleController.formatString(R.string.NoResultFoundFor, this.lastQuery) : AndroidUtilities.replaceCharSequence("%s", LocaleController.getString(R.string.NoResultFoundForTag), this.lastReaction.toCharSequence(SharedMediaLayout.this.mediaPages[i3].emptyView.title.getPaint().getFontMetricsInt())));
                        SharedMediaLayout.this.mediaPages[i3].emptyView.button.setVisibility(8);
                        SharedMediaLayout.this.mediaPages[i3].emptyView.showProgress(false, true);
                    }
                }
            }
            this.oldItemCounts = this.count;
            notifyDataSetChanged();
        }

        @Override
        public long getItemId(int i) {
            int hash;
            if (i < 0) {
                return i;
            }
            if (i < this.dialogs.size()) {
                hash = Objects.hash(1, Long.valueOf(this.dialogs.get(i).dialogId));
            } else {
                int size = i - this.dialogs.size();
                if (size >= this.messages.size()) {
                    return size;
                }
                hash = Objects.hash(2, Long.valueOf(this.messages.get(size).getSavedDialogId()), Integer.valueOf(this.messages.get(size).getId()));
            }
            return hash;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            DialogCell dialogCell = new DialogCell(null, this.mContext, false, true) {
                @Override
                public boolean isForumCell() {
                    return false;
                }
            };
            dialogCell.setDialogCellDelegate(SharedMediaLayout.this);
            dialogCell.isSavedDialog = true;
            dialogCell.setBackgroundColor(SharedMediaLayout.this.getThemedColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(dialogCell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (i < 0) {
                return;
            }
            View view = viewHolder.itemView;
            if (view instanceof DialogCell) {
                DialogCell dialogCell = (DialogCell) view;
                dialogCell.useSeparator = i + 1 < getItemCount();
                if (i < this.dialogs.size()) {
                    SavedMessagesController.SavedDialog savedDialog = this.dialogs.get(i);
                    dialogCell.setDialog(savedDialog.dialogId, savedDialog.message, savedDialog.getDate(), false, false);
                    return;
                }
                int size = i - this.dialogs.size();
                if (size < this.messages.size()) {
                    MessageObject messageObject = this.messages.get(size);
                    dialogCell.setDialog(messageObject.getSavedDialogId(), messageObject, messageObject.messageOwner.date, false, false);
                }
            }
        }

        @Override
        public int getItemCount() {
            return this.dialogs.size() + this.messages.size();
        }
    }

    public class ChannelRecommendationsAdapter extends RecyclerListView.SelectionAdapter {
        private final ArrayList<TLRPC$Chat> chats = new ArrayList<>();
        private final Context mContext;
        private int more;

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ChannelRecommendationsAdapter(Context context) {
            this.mContext = context;
            update(false);
        }

        public void update(boolean z) {
            TLRPC$Chat chat;
            if (SharedMediaLayout.this.profileActivity == null || !DialogObject.isChatDialog(SharedMediaLayout.this.dialog_id) || (chat = MessagesController.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).getChat(Long.valueOf(-SharedMediaLayout.this.dialog_id))) == null || !ChatObject.isChannelAndNotMegaGroup(chat)) {
                return;
            }
            MessagesController.ChannelRecommendations channelRecommendations = MessagesController.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).getChannelRecommendations(chat.id);
            this.chats.clear();
            int i = 0;
            if (channelRecommendations != null) {
                for (int i2 = 0; i2 < channelRecommendations.chats.size(); i2++) {
                    TLRPC$Chat tLRPC$Chat = channelRecommendations.chats.get(i2);
                    if (tLRPC$Chat != null && ChatObject.isNotInChat(tLRPC$Chat)) {
                        this.chats.add(tLRPC$Chat);
                    }
                }
            }
            if (!this.chats.isEmpty() && !UserConfig.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).isPremium()) {
                i = channelRecommendations.more;
            }
            this.more = i;
            if (z) {
                notifyDataSetChanged();
            }
        }

        @Override
        public int getItemCount() {
            return this.chats.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View profileSearchCell;
            if (i == 18) {
                profileSearchCell = new MoreRecommendationsCell(SharedMediaLayout.this.profileActivity == null ? UserConfig.selectedAccount : SharedMediaLayout.this.profileActivity.getCurrentAccount(), this.mContext, SharedMediaLayout.this.resourcesProvider, new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.ChannelRecommendationsAdapter.this.lambda$onCreateViewHolder$0();
                    }
                });
            } else {
                profileSearchCell = new ProfileSearchCell(this.mContext, SharedMediaLayout.this.resourcesProvider);
            }
            profileSearchCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(profileSearchCell);
        }

        public void lambda$onCreateViewHolder$0() {
            if (SharedMediaLayout.this.profileActivity != null) {
                SharedMediaLayout.this.profileActivity.presentFragment(new PremiumPreviewFragment("similar_channels"));
            }
        }

        public void openPreview(final int i) {
            if (i < 0 || i >= this.chats.size()) {
                return;
            }
            final TLRPC$Chat tLRPC$Chat = this.chats.get(i);
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", tLRPC$Chat.id);
            ChatActivity chatActivity = new ChatActivity(bundle);
            if (SharedMediaLayout.this.profileActivity instanceof ProfileActivity) {
                ((ProfileActivity) SharedMediaLayout.this.profileActivity).prepareBlurBitmap();
            }
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(SharedMediaLayout.this.getContext(), R.drawable.popup_fixed_alert, SharedMediaLayout.this.resourcesProvider, 2);
            actionBarPopupWindowLayout.setBackgroundColor(SharedMediaLayout.this.getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
            ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(SharedMediaLayout.this.getContext(), false, false);
            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(R.string.OpenChannel2), R.drawable.msg_channel);
            actionBarMenuSubItem.setMinimumWidth(160);
            actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SharedMediaLayout.ChannelRecommendationsAdapter.this.lambda$openPreview$1(view);
                }
            });
            actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
            ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(SharedMediaLayout.this.getContext(), false, false);
            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(R.string.ProfileJoinChannel), R.drawable.msg_addbot);
            actionBarMenuSubItem2.setMinimumWidth(160);
            actionBarMenuSubItem2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SharedMediaLayout.ChannelRecommendationsAdapter.this.lambda$openPreview$3(tLRPC$Chat, i, view);
                }
            });
            actionBarPopupWindowLayout.addView(actionBarMenuSubItem2);
            SharedMediaLayout.this.profileActivity.presentFragmentAsPreviewWithMenu(chatActivity, actionBarPopupWindowLayout);
        }

        public void lambda$openPreview$1(View view) {
            if (SharedMediaLayout.this.profileActivity == null || SharedMediaLayout.this.profileActivity.getParentLayout() == null) {
                return;
            }
            SharedMediaLayout.this.profileActivity.getParentLayout().expandPreviewFragment();
        }

        public void lambda$openPreview$3(final TLRPC$Chat tLRPC$Chat, int i, View view) {
            SharedMediaLayout.this.profileActivity.finishPreviewFragment();
            tLRPC$Chat.left = false;
            update(false);
            notifyItemRemoved(i);
            if (this.chats.isEmpty()) {
                SharedMediaLayout.this.updateTabs(true);
                SharedMediaLayout.this.checkCurrentTabValid();
            }
            SharedMediaLayout.this.profileActivity.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.channelRecommendationsLoaded, Long.valueOf(-SharedMediaLayout.this.dialog_id));
            SharedMediaLayout.this.profileActivity.getMessagesController().addUserToChat(tLRPC$Chat.id, SharedMediaLayout.this.profileActivity.getUserConfig().getCurrentUser(), 0, null, SharedMediaLayout.this.profileActivity, new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.ChannelRecommendationsAdapter.this.lambda$openPreview$2(tLRPC$Chat);
                }
            });
        }

        public void lambda$openPreview$2(TLRPC$Chat tLRPC$Chat) {
            BulletinFactory.of(SharedMediaLayout.this.profileActivity).createSimpleBulletin(R.raw.contact_check, LocaleController.formatString(R.string.YouJoinedChannel, tLRPC$Chat == null ? "" : tLRPC$Chat.title)).show(true);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ProfileSearchCell profileSearchCell;
            if (viewHolder.getItemViewType() == 17) {
                View view = viewHolder.itemView;
                if (!(view instanceof ProfileSearchCell)) {
                    return;
                } else {
                    profileSearchCell = (ProfileSearchCell) view;
                }
            } else if (viewHolder.getItemViewType() == 18) {
                View view2 = viewHolder.itemView;
                if (!(view2 instanceof MoreRecommendationsCell)) {
                    return;
                } else {
                    profileSearchCell = ((MoreRecommendationsCell) view2).channelCell;
                }
            } else {
                profileSearchCell = null;
            }
            if (profileSearchCell != null) {
                profileSearchCell.setData(this.chats.get(i), null, null, null, false, false);
                profileSearchCell.useSeparator = i != this.chats.size() - 1;
            }
        }

        @Override
        public int getItemViewType(int i) {
            return (this.more <= 0 || i != getItemCount() + (-1)) ? 17 : 18;
        }
    }

    private static class MoreRecommendationsCell extends FrameLayout {
        private final ButtonWithCounterView button;
        public final ProfileSearchCell channelCell;
        private final int currentAccount;
        private final View gradientView;
        private final Theme.ResourcesProvider resourcesProvider;
        private final LinkSpanDrawable.LinksTextView textView;

        public MoreRecommendationsCell(int i, Context context, Theme.ResourcesProvider resourcesProvider, final Runnable runnable) {
            super(context);
            this.currentAccount = i;
            this.resourcesProvider = resourcesProvider;
            ProfileSearchCell profileSearchCell = new ProfileSearchCell(context, resourcesProvider);
            this.channelCell = profileSearchCell;
            profileSearchCell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 2));
            addView(profileSearchCell, LayoutHelper.createFrame(-1, -2.0f));
            View view = new View(context);
            this.gradientView = view;
            GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
            int i2 = Theme.key_windowBackgroundWhite;
            view.setBackground(new GradientDrawable(orientation, new int[]{Theme.multAlpha(Theme.getColor(i2, resourcesProvider), 0.4f), Theme.getColor(i2, resourcesProvider)}));
            addView(view, LayoutHelper.createFrame(-1, 60.0f));
            ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
            this.button = buttonWithCounterView;
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) LocaleController.getString(R.string.MoreSimilarButton));
            spannableStringBuilder.append((CharSequence) " ");
            SpannableString spannableString = new SpannableString("l");
            spannableString.setSpan(new ColoredImageSpan(R.drawable.msg_mini_lock2), 0, 1, 33);
            spannableStringBuilder.append((CharSequence) spannableString);
            buttonWithCounterView.setText(spannableStringBuilder, false);
            addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 48, 14.0f, 38.0f, 14.0f, 0.0f));
            buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    SharedMediaLayout.MoreRecommendationsCell.lambda$new$0(runnable, view2);
                }
            });
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
            this.textView = linksTextView;
            linksTextView.setTextSize(1, 13.0f);
            linksTextView.setTextAlignment(4);
            linksTextView.setGravity(17);
            linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
            linksTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, resourcesProvider));
            linksTextView.setLineSpacing(AndroidUtilities.dp(3.0f), 1.0f);
            SpannableStringBuilder premiumText = AndroidUtilities.premiumText(LocaleController.getString(R.string.MoreSimilarText), new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.MoreRecommendationsCell.lambda$new$1(runnable);
                }
            });
            SpannableString spannableString2 = new SpannableString("" + MessagesController.getInstance(i).recommendedChannelsLimitPremium);
            spannableString2.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, spannableString2.length(), 33);
            linksTextView.setText(AndroidUtilities.replaceCharSequence("%s", premiumText, spannableString2));
            addView(linksTextView, LayoutHelper.createFrame(-1, -2.0f, 49, 24.0f, 96.0f, 24.0f, 12.0f));
        }

        public static void lambda$new$0(Runnable runnable, View view) {
            if (runnable != null) {
                runnable.run();
            }
        }

        public static void lambda$new$1(Runnable runnable) {
            if (runnable != null) {
                runnable.run();
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(145.0f), 1073741824));
        }
    }

    public class CommonGroupsAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<TLRPC$Chat> chats = new ArrayList<>();
        private boolean endReached;
        private boolean firstLoaded;
        private boolean loading;
        private Context mContext;

        public CommonGroupsAdapter(Context context) {
            this.mContext = context;
        }

        public void getChats(long j, final int i) {
            long j2;
            if (this.loading) {
                return;
            }
            TLRPC$TL_messages_getCommonChats tLRPC$TL_messages_getCommonChats = new TLRPC$TL_messages_getCommonChats();
            if (!DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id)) {
                j2 = SharedMediaLayout.this.dialog_id;
            } else {
                j2 = SharedMediaLayout.this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(SharedMediaLayout.this.dialog_id))).user_id;
            }
            TLRPC$InputUser inputUser = SharedMediaLayout.this.profileActivity.getMessagesController().getInputUser(j2);
            tLRPC$TL_messages_getCommonChats.user_id = inputUser;
            if (inputUser instanceof TLRPC$TL_inputUserEmpty) {
                return;
            }
            tLRPC$TL_messages_getCommonChats.limit = i;
            tLRPC$TL_messages_getCommonChats.max_id = j;
            this.loading = true;
            notifyDataSetChanged();
            SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(tLRPC$TL_messages_getCommonChats, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SharedMediaLayout.CommonGroupsAdapter.this.lambda$getChats$1(i, tLObject, tLRPC$TL_error);
                }
            }), SharedMediaLayout.this.profileActivity.getClassGuid());
        }

        public void lambda$getChats$1(final int i, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.CommonGroupsAdapter.this.lambda$getChats$0(tLRPC$TL_error, tLObject, i);
                }
            });
        }

        public void lambda$getChats$0(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
            int itemCount = getItemCount();
            if (tLRPC$TL_error == null) {
                TLRPC$messages_Chats tLRPC$messages_Chats = (TLRPC$messages_Chats) tLObject;
                SharedMediaLayout.this.profileActivity.getMessagesController().putChats(tLRPC$messages_Chats.chats, false);
                this.endReached = tLRPC$messages_Chats.chats.isEmpty() || tLRPC$messages_Chats.chats.size() != i;
                this.chats.addAll(tLRPC$messages_Chats.chats);
            } else {
                this.endReached = true;
            }
            for (int i2 = 0; i2 < SharedMediaLayout.this.mediaPages.length; i2++) {
                if (SharedMediaLayout.this.mediaPages[i2].selectedType == 6 && SharedMediaLayout.this.mediaPages[i2].listView != null) {
                    InternalListView internalListView = SharedMediaLayout.this.mediaPages[i2].listView;
                    if (this.firstLoaded || itemCount == 0) {
                        SharedMediaLayout.this.animateItemsEnter(internalListView, 0, null);
                    }
                }
            }
            this.loading = false;
            this.firstLoaded = true;
            notifyDataSetChanged();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() != this.chats.size();
        }

        @Override
        public int getItemCount() {
            if (this.chats.isEmpty() && !this.loading) {
                return 1;
            }
            int size = this.chats.size();
            return (this.chats.isEmpty() || this.endReached) ? size : size + 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ProfileSearchCell profileSearchCell;
            if (i == 14) {
                profileSearchCell = new ProfileSearchCell(this.mContext, SharedMediaLayout.this.resourcesProvider);
            } else {
                if (i == 15) {
                    View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 6, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                    createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(createEmptyStubView);
                }
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext, SharedMediaLayout.this.resourcesProvider);
                flickerLoadingView.setIsSingleCell(true);
                flickerLoadingView.showDate(false);
                flickerLoadingView.setViewType(1);
                profileSearchCell = flickerLoadingView;
            }
            profileSearchCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(profileSearchCell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 14) {
                View view = viewHolder.itemView;
                if (view instanceof ProfileSearchCell) {
                    ProfileSearchCell profileSearchCell = (ProfileSearchCell) view;
                    profileSearchCell.setData(this.chats.get(i), null, null, null, false, false);
                    boolean z = true;
                    if (i == this.chats.size() - 1 && this.endReached) {
                        z = false;
                    }
                    profileSearchCell.useSeparator = z;
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            if (!this.chats.isEmpty() || this.loading) {
                return i < this.chats.size() ? 14 : 16;
            }
            return 15;
        }
    }

    public int getStoriesCount(int i) {
        StoriesController.StoriesList storiesList;
        if (i != 8) {
            if (i == 9) {
                storiesList = this.archivedStoriesAdapter.storiesList;
            }
            return 0;
        }
        storiesList = this.storiesAdapter.storiesList;
        if (storiesList != null) {
            return storiesList.getCount();
        }
        return 0;
    }

    public String getBotPreviewsSubtitle(boolean z) {
        int i;
        int i2;
        TLRPC$MessageMedia tLRPC$MessageMedia;
        BotPreviewsEditContainer botPreviewsEditContainer;
        if (!isBot()) {
            return LocaleController.getString(R.string.BotPreviewEmpty);
        }
        if (z && (botPreviewsEditContainer = this.botPreviewsContainer) != null) {
            return botPreviewsEditContainer.getBotPreviewsSubtitle();
        }
        StoriesAdapter storiesAdapter = this.storiesAdapter;
        if (storiesAdapter == null || storiesAdapter.storiesList == null) {
            i = 0;
            i2 = 0;
        } else {
            i = 0;
            i2 = 0;
            for (int i3 = 0; i3 < this.storiesAdapter.storiesList.messageObjects.size(); i3++) {
                MessageObject messageObject = this.storiesAdapter.storiesList.messageObjects.get(i3);
                TL_stories$StoryItem tL_stories$StoryItem = messageObject.storyItem;
                if (tL_stories$StoryItem != null && (tLRPC$MessageMedia = tL_stories$StoryItem.media) != null) {
                    if (MessageObject.isVideoDocument(tLRPC$MessageMedia.document)) {
                        i2++;
                    } else if (messageObject.storyItem.media.photo != null) {
                        i++;
                    }
                }
            }
        }
        if (i == 0 && i2 == 0) {
            return LocaleController.getString(R.string.BotPreviewEmpty);
        }
        StringBuilder sb = new StringBuilder();
        if (i > 0) {
            sb.append(LocaleController.formatPluralString("Images", i, new Object[0]));
        }
        if (i2 > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.formatPluralString("Videos", i2, new Object[0]));
        }
        return sb.toString();
    }

    public class StoriesAdapter extends SharedPhotoVideoAdapter {
        public boolean applyingReorder;
        private int id;
        private final boolean isArchive;
        public ArrayList<Integer> lastPinnedIds;
        private ViewsForPeerStoriesRequester poller;
        public final StoriesController.StoriesList storiesList;
        private StoriesAdapter supportingAdapter;
        private final ArrayList<StoriesController.UploadingStory> uploadingStories;

        @Override
        public int getItemViewType(int i) {
            return 19;
        }

        public int getTopOffset() {
            return 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public boolean fastScrollIsVisible(RecyclerListView recyclerListView) {
            return super.fastScrollIsVisible(recyclerListView);
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            super.getPositionForScrollProgress(recyclerListView, f, iArr);
        }

        @Override
        public float getScrollProgress(RecyclerListView recyclerListView) {
            return super.getScrollProgress(recyclerListView);
        }

        @Override
        public void onFinishFastScroll(RecyclerListView recyclerListView) {
            super.onFinishFastScroll(recyclerListView);
        }

        @Override
        public void onStartFastScroll() {
            super.onStartFastScroll();
        }

        public StoriesAdapter(Context context, boolean z) {
            super(context);
            TLRPC$User user;
            this.uploadingStories = new ArrayList<>();
            this.lastPinnedIds = new ArrayList<>();
            this.isArchive = z;
            int currentAccount = SharedMediaLayout.this.profileActivity.getCurrentAccount();
            if (!TextUtils.isEmpty(SharedMediaLayout.this.getStoriesHashtag())) {
                if (SharedMediaLayout.this.searchStoriesList == null) {
                    SharedMediaLayout.this.searchStoriesList = new StoriesController.SearchStoriesList(currentAccount, SharedMediaLayout.this.getStoriesHashtag());
                }
                this.storiesList = SharedMediaLayout.this.searchStoriesList;
            } else if (SharedMediaLayout.this.getStoriesArea() != null) {
                if (SharedMediaLayout.this.searchStoriesList == null) {
                    SharedMediaLayout.this.searchStoriesList = new StoriesController.SearchStoriesList(currentAccount, SharedMediaLayout.this.getStoriesArea());
                }
                this.storiesList = SharedMediaLayout.this.searchStoriesList;
            } else if ((z && !SharedMediaLayout.this.isStoriesView()) || (!z && SharedMediaLayout.this.isArchivedOnlyStoriesView())) {
                this.storiesList = null;
            } else {
                int i = 1;
                boolean z2 = SharedMediaLayout.this.dialog_id > 0 && (user = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(SharedMediaLayout.this.dialog_id))) != null && user.bot;
                StoriesController storiesController = SharedMediaLayout.this.profileActivity.getMessagesController().getStoriesController();
                long j = SharedMediaLayout.this.dialog_id;
                if (z2) {
                    i = 4;
                } else if (!z) {
                    i = 0;
                }
                this.storiesList = storiesController.getStoriesList(j, i);
            }
            StoriesController.StoriesList storiesList = this.storiesList;
            if (storiesList != null) {
                this.id = storiesList.link();
                this.poller = new ViewsForPeerStoriesRequester(SharedMediaLayout.this.profileActivity.getMessagesController().getStoriesController(), SharedMediaLayout.this.dialog_id, this.storiesList.currentAccount) {
                    @Override
                    protected void getStoryIds(ArrayList<Integer> arrayList) {
                        InternalListView internalListView;
                        MessageObject messageObject;
                        int i2 = 0;
                        while (true) {
                            if (i2 >= SharedMediaLayout.this.mediaPages.length) {
                                internalListView = null;
                                break;
                            }
                            if (SharedMediaLayout.this.mediaPages[i2].listView != null) {
                                RecyclerView.Adapter adapter = SharedMediaLayout.this.mediaPages[i2].listView.getAdapter();
                                StoriesAdapter storiesAdapter = StoriesAdapter.this;
                                if (adapter == storiesAdapter) {
                                    internalListView = SharedMediaLayout.this.mediaPages[i2].listView;
                                    break;
                                }
                            }
                            i2++;
                        }
                        if (internalListView != null) {
                            for (int i3 = 0; i3 < internalListView.getChildCount(); i3++) {
                                View childAt = internalListView.getChildAt(i3);
                                if ((childAt instanceof SharedPhotoVideoCell2) && (messageObject = ((SharedPhotoVideoCell2) childAt).getMessageObject()) != null && messageObject.isStory()) {
                                    arrayList.add(Integer.valueOf(messageObject.storyItem.id));
                                }
                            }
                        }
                    }

                    @Override
                    protected boolean updateStories(ArrayList<Integer> arrayList, TL_stories$TL_stories_storyViews tL_stories$TL_stories_storyViews) {
                        StoriesAdapter.this.storiesList.updateStoryViews(arrayList, tL_stories$TL_stories_storyViews.views);
                        return true;
                    }
                };
            }
            checkColumns();
        }

        public void destroy() {
            StoriesController.StoriesList storiesList = this.storiesList;
            if (storiesList != null) {
                storiesList.unlink(this.id);
            }
        }

        private void checkColumns() {
            if (this.storiesList == null || this.isArchive) {
                return;
            }
            if ((!SharedMediaLayout.this.storiesColumnsCountSet || (SharedMediaLayout.this.allowStoriesSingleColumn && this.storiesList.getCount() > 1)) && this.storiesList.getCount() > 0 && !SharedMediaLayout.this.isStoriesView()) {
                if (this.storiesList.getCount() < 5) {
                    SharedMediaLayout.this.mediaColumnsCount[1] = this.storiesList.getCount();
                    if (SharedMediaLayout.this.mediaPages != null && SharedMediaLayout.this.mediaPages[0] != null && SharedMediaLayout.this.mediaPages[1] != null && SharedMediaLayout.this.mediaPages[0].listView != null && SharedMediaLayout.this.mediaPages[1].listView != null) {
                        SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                    }
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    sharedMediaLayout.allowStoriesSingleColumn = sharedMediaLayout.mediaColumnsCount[1] == 1;
                } else if (SharedMediaLayout.this.allowStoriesSingleColumn) {
                    SharedMediaLayout.this.allowStoriesSingleColumn = false;
                    SharedMediaLayout.this.mediaColumnsCount[1] = Math.max(2, SharedConfig.storiesColumnsCount);
                    if (SharedMediaLayout.this.mediaPages != null && SharedMediaLayout.this.mediaPages[0] != null && SharedMediaLayout.this.mediaPages[1] != null && SharedMediaLayout.this.mediaPages[0].listView != null && SharedMediaLayout.this.mediaPages[1].listView != null) {
                        SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                    }
                }
                SharedMediaLayout.this.storiesColumnsCountSet = true;
            }
        }

        @Override
        public void notifyDataSetChanged() {
            if (this.storiesList != null && SharedMediaLayout.this.isBot()) {
                this.uploadingStories.clear();
                ArrayList<StoriesController.UploadingStory> uploadingStories = MessagesController.getInstance(this.storiesList.currentAccount).getStoriesController().getUploadingStories(SharedMediaLayout.this.dialog_id);
                if (uploadingStories != null) {
                    this.uploadingStories.addAll(uploadingStories);
                }
            }
            super.notifyDataSetChanged();
            StoriesAdapter storiesAdapter = this.supportingAdapter;
            if (storiesAdapter != null) {
                storiesAdapter.notifyDataSetChanged();
            }
            checkColumns();
        }

        public int columnsCount() {
            if (this == SharedMediaLayout.this.photoVideoAdapter) {
                return SharedMediaLayout.this.mediaColumnsCount[0];
            }
            return (this == SharedMediaLayout.this.storiesAdapter || this == SharedMediaLayout.this.archivedStoriesAdapter) ? SharedMediaLayout.this.mediaColumnsCount[1] : SharedMediaLayout.this.animateToColumnsCount;
        }

        @Override
        public int getItemCount() {
            if (this.storiesList == null) {
                return 0;
            }
            return this.uploadingStories.size() + ((this.storiesList.isOnlyCache() && SharedMediaLayout.this.hasInternet()) ? 0 : this.storiesList.getCount());
        }

        @Override
        public int getTotalItemsCount() {
            return getItemCount();
        }

        @Override
        public int getPositionForIndex(int i) {
            return this.isArchive ? getTopOffset() + i : i;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            RecyclerView.ViewHolder onCreateViewHolder = super.onCreateViewHolder(viewGroup, i);
            View view = onCreateViewHolder.itemView;
            if (view instanceof SharedPhotoVideoCell2) {
                ((SharedPhotoVideoCell2) view).isStory = true;
            }
            return onCreateViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (this.storiesList != null && viewHolder.getItemViewType() == 19) {
                View view = viewHolder.itemView;
                if (view instanceof SharedPhotoVideoCell2) {
                    SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) view;
                    sharedPhotoVideoCell2.isStory = true;
                    if (i >= 0 && i < this.uploadingStories.size()) {
                        StoriesController.UploadingStory uploadingStory = this.uploadingStories.get(i);
                        sharedPhotoVideoCell2.isStoryPinned = false;
                        if (uploadingStory.sharedMessageObject == null) {
                            TL_stories$TL_storyItem tL_stories$TL_storyItem = new TL_stories$TL_storyItem();
                            int m = FactCheckController$Key$$ExternalSyntheticBackport0.m(uploadingStory.random_id);
                            tL_stories$TL_storyItem.messageId = m;
                            tL_stories$TL_storyItem.id = m;
                            tL_stories$TL_storyItem.attachPath = uploadingStory.firstFramePath;
                            MessageObject messageObject = new MessageObject(this.storiesList.currentAccount, tL_stories$TL_storyItem) {
                                @Override
                                public float getProgress() {
                                    return this.uploadingStory.progress;
                                }
                            };
                            uploadingStory.sharedMessageObject = messageObject;
                            messageObject.uploadingStory = uploadingStory;
                        }
                        sharedPhotoVideoCell2.setMessageObject(uploadingStory.sharedMessageObject, columnsCount());
                        sharedPhotoVideoCell2.isStory = true;
                        sharedPhotoVideoCell2.setReorder(false);
                        sharedPhotoVideoCell2.setChecked(false, false);
                        return;
                    }
                    int size = i - this.uploadingStories.size();
                    if (size < 0 || size >= this.storiesList.messageObjects.size()) {
                        sharedPhotoVideoCell2.isStoryPinned = false;
                        sharedPhotoVideoCell2.setMessageObject(null, columnsCount());
                        sharedPhotoVideoCell2.isStory = true;
                        return;
                    }
                    MessageObject messageObject2 = this.storiesList.messageObjects.get(size);
                    sharedPhotoVideoCell2.isStoryPinned = messageObject2 != null && this.storiesList.isPinned(messageObject2.getId());
                    sharedPhotoVideoCell2.setReorder(SharedMediaLayout.this.isBot() || sharedPhotoVideoCell2.isStoryPinned);
                    sharedPhotoVideoCell2.isSearchingHashtag = SharedMediaLayout.this.isSearchingStories();
                    sharedPhotoVideoCell2.setMessageObject(messageObject2, columnsCount());
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    if (!sharedMediaLayout.isActionModeShowed || messageObject2 == null) {
                        sharedPhotoVideoCell2.setChecked(false, false);
                    } else {
                        sharedPhotoVideoCell2.setChecked(sharedMediaLayout.selectedFiles[(messageObject2.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject2.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject2.getId()) >= 0, true);
                    }
                }
            }
        }

        public void load(boolean z) {
            if (this.storiesList == null) {
                return;
            }
            int columnsCount = columnsCount();
            this.storiesList.load(z, Math.min(100, Math.max(1, columnsCount / 2) * columnsCount * columnsCount));
        }

        @Override
        public String getLetter(int i) {
            int topOffset;
            MessageObject messageObject;
            TL_stories$StoryItem tL_stories$StoryItem;
            if (this.storiesList == null || (topOffset = i - getTopOffset()) < 0 || topOffset >= this.storiesList.messageObjects.size() || (messageObject = this.storiesList.messageObjects.get(topOffset)) == null || (tL_stories$StoryItem = messageObject.storyItem) == null) {
                return null;
            }
            return LocaleController.formatYearMont(tL_stories$StoryItem.date, true);
        }

        @Override
        public void onFastScrollSingleTap() {
            SharedMediaLayout.this.showMediaCalendar(this.isArchive ? 9 : 8, true);
        }

        public boolean canReorder(int i) {
            StoriesController.StoriesList storiesList;
            if (this.isArchive || (storiesList = this.storiesList) == null) {
                return false;
            }
            if (storiesList instanceof StoriesController.BotPreviewsList) {
                TLRPC$User user = MessagesController.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).getUser(Long.valueOf(SharedMediaLayout.this.dialog_id));
                return user != null && user.bot && user.bot_has_main_app && user.bot_can_edit;
            }
            if (i < 0 || i >= storiesList.messageObjects.size()) {
                return false;
            }
            return this.storiesList.isPinned(this.storiesList.messageObjects.get(i).getId());
        }

        public boolean swapElements(int i, int i2) {
            StoriesController.StoriesList storiesList;
            ArrayList<Integer> arrayList;
            if (this.isArchive || (storiesList = this.storiesList) == null || i < 0 || i >= storiesList.messageObjects.size() || i2 < 0 || i2 >= this.storiesList.messageObjects.size()) {
                return false;
            }
            if (this.storiesList instanceof StoriesController.BotPreviewsList) {
                arrayList = new ArrayList<>();
                for (int i3 = 0; i3 < this.storiesList.messageObjects.size(); i3++) {
                    arrayList.add(Integer.valueOf(this.storiesList.messageObjects.get(i3).getId()));
                }
            } else {
                arrayList = new ArrayList<>(this.storiesList.pinnedIds);
            }
            if (!this.applyingReorder) {
                this.lastPinnedIds.clear();
                this.lastPinnedIds.addAll(arrayList);
                this.applyingReorder = true;
            }
            MessageObject messageObject = this.storiesList.messageObjects.get(i);
            this.storiesList.messageObjects.get(i2);
            arrayList.remove(Integer.valueOf(messageObject.getId()));
            arrayList.add(Utilities.clamp(i2, arrayList.size(), 0), Integer.valueOf(messageObject.getId()));
            this.storiesList.updatePinnedOrder(arrayList, false);
            notifyItemMoved(i, i2);
            return true;
        }

        public void reorderDone() {
            StoriesController.StoriesList storiesList;
            ArrayList<Integer> arrayList;
            if (this.isArchive || (storiesList = this.storiesList) == null || !this.applyingReorder) {
                return;
            }
            if (storiesList instanceof StoriesController.BotPreviewsList) {
                arrayList = new ArrayList<>();
                for (int i = 0; i < this.storiesList.messageObjects.size(); i++) {
                    arrayList.add(Integer.valueOf(this.storiesList.messageObjects.get(i).getId()));
                }
            } else {
                arrayList = storiesList.pinnedIds;
            }
            boolean z = this.lastPinnedIds.size() != arrayList.size();
            if (!z) {
                int i2 = 0;
                while (true) {
                    if (i2 >= this.lastPinnedIds.size()) {
                        break;
                    }
                    if (this.lastPinnedIds.get(i2) != arrayList.get(i2)) {
                        z = true;
                        break;
                    }
                    i2++;
                }
            }
            if (z) {
                this.storiesList.updatePinnedOrder(arrayList, true);
            }
            this.applyingReorder = false;
        }
    }

    public class ChatUsersAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC$ChatFull chatInfo;
        private Context mContext;
        private ArrayList<Integer> sortedUsers;

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ChatUsersAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
            if (tLRPC$ChatFull != null && tLRPC$ChatFull.participants.participants.isEmpty()) {
                return 1;
            }
            TLRPC$ChatFull tLRPC$ChatFull2 = this.chatInfo;
            if (tLRPC$ChatFull2 != null) {
                return tLRPC$ChatFull2.participants.participants.size();
            }
            return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 20) {
                View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 7, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(createEmptyStubView);
            }
            UserCell userCell = new UserCell(this.mContext, 9, 0, true, false, SharedMediaLayout.this.resourcesProvider);
            userCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(userCell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$ChatParticipant tLRPC$ChatParticipant;
            String string;
            View view = viewHolder.itemView;
            if (view instanceof UserCell) {
                UserCell userCell = (UserCell) view;
                if (!this.sortedUsers.isEmpty()) {
                    tLRPC$ChatParticipant = this.chatInfo.participants.participants.get(this.sortedUsers.get(i).intValue());
                } else {
                    tLRPC$ChatParticipant = this.chatInfo.participants.participants.get(i);
                }
                if (tLRPC$ChatParticipant != null) {
                    if (tLRPC$ChatParticipant instanceof TLRPC$TL_chatChannelParticipant) {
                        TLRPC$ChannelParticipant tLRPC$ChannelParticipant = ((TLRPC$TL_chatChannelParticipant) tLRPC$ChatParticipant).channelParticipant;
                        if (!TextUtils.isEmpty(tLRPC$ChannelParticipant.rank)) {
                            string = tLRPC$ChannelParticipant.rank;
                        } else if (tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantCreator) {
                            string = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                        } else {
                            if (tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantAdmin) {
                                string = LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin);
                            }
                            string = null;
                        }
                    } else if (tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantCreator) {
                        string = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                    } else {
                        if (tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin) {
                            string = LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin);
                        }
                        string = null;
                    }
                    userCell.setAdminRole(string);
                    userCell.setData(SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Long.valueOf(tLRPC$ChatParticipant.user_id)), null, null, 0, i != this.chatInfo.participants.participants.size() - 1);
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
            return (tLRPC$ChatFull == null || !tLRPC$ChatFull.participants.participants.isEmpty()) ? 21 : 20;
        }
    }

    public class GroupUsersSearchAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC$Chat currentChat;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private Runnable searchRunnable;
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private int totalCount = 0;
        int searchCount = 0;

        @Override
        public int getItemViewType(int i) {
            return 22;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public GroupUsersSearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
                @Override
                public boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                @Override
                public LongSparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                @Override
                public LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                @Override
                public final void onDataSetChanged(int i) {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$new$0(i);
                }

                @Override
                public void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
            this.currentChat = SharedMediaLayout.this.delegate.getCurrentChat();
        }

        public void lambda$new$0(int i) {
            notifyDataSetChanged();
            if (i == 1) {
                int i2 = this.searchCount - 1;
                this.searchCount = i2;
                if (i2 == 0) {
                    for (int i3 = 0; i3 < SharedMediaLayout.this.mediaPages.length; i3++) {
                        if (SharedMediaLayout.this.mediaPages[i3].selectedType == 7) {
                            if (getItemCount() == 0) {
                                SharedMediaLayout.this.mediaPages[i3].emptyView.showProgress(false, true);
                            } else {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[i3].listView, 0, null);
                            }
                        }
                    }
                }
            }
        }

        private boolean createMenuForParticipant(TLObject tLObject, boolean z, View view) {
            if (tLObject instanceof TLRPC$ChannelParticipant) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject;
                TLRPC$TL_chatChannelParticipant tLRPC$TL_chatChannelParticipant = new TLRPC$TL_chatChannelParticipant();
                tLRPC$TL_chatChannelParticipant.channelParticipant = tLRPC$ChannelParticipant;
                tLRPC$TL_chatChannelParticipant.user_id = MessageObject.getPeerId(tLRPC$ChannelParticipant.peer);
                tLRPC$TL_chatChannelParticipant.inviter_id = tLRPC$ChannelParticipant.inviter_id;
                tLRPC$TL_chatChannelParticipant.date = tLRPC$ChannelParticipant.date;
                tLObject = tLRPC$TL_chatChannelParticipant;
            }
            return SharedMediaLayout.this.delegate.onMemberClick((TLRPC$ChatParticipant) tLObject, true, z, view);
        }

        public void search(final String str, boolean z) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults(null);
            this.searchAdapterHelper.queryServerSearch(null, true, false, true, false, false, ChatObject.isChannel(this.currentChat) ? this.currentChat.id : 0L, false, 2, 0);
            notifyDataSetChanged();
            for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                if (SharedMediaLayout.this.mediaPages[i].selectedType == 7 && !TextUtils.isEmpty(str)) {
                    SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(true, z);
                }
            }
            if (TextUtils.isEmpty(str)) {
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$search$1(str);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }

        public void lambda$search$1(final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$processSearch$3(str);
                }
            });
        }

        public void lambda$processSearch$3(final String str) {
            final ArrayList arrayList = null;
            this.searchRunnable = null;
            if (!ChatObject.isChannel(this.currentChat) && SharedMediaLayout.this.info != null) {
                arrayList = new ArrayList(SharedMediaLayout.this.info.participants.participants);
            }
            this.searchCount = 2;
            if (arrayList != null) {
                Utilities.searchQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$processSearch$2(str, arrayList);
                    }
                });
            } else {
                this.searchCount = 1;
            }
            this.searchAdapterHelper.queryServerSearch(str, false, false, true, false, false, ChatObject.isChannel(this.currentChat) ? this.currentChat.id : 0L, false, 2, 1);
        }

        public void lambda$processSearch$2(java.lang.String r19, java.util.ArrayList r20) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.GroupUsersSearchAdapter.lambda$processSearch$2(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(final ArrayList<CharSequence> arrayList, final ArrayList<TLObject> arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$updateSearchResults$4(arrayList, arrayList2);
                }
            });
        }

        public void lambda$updateSearchResults$4(ArrayList arrayList, ArrayList arrayList2) {
            if (SharedMediaLayout.this.searching) {
                this.searchResultNames = arrayList;
                this.searchCount--;
                if (!ChatObject.isChannel(this.currentChat)) {
                    ArrayList<TLObject> groupSearch = this.searchAdapterHelper.getGroupSearch();
                    groupSearch.clear();
                    groupSearch.addAll(arrayList2);
                }
                if (this.searchCount == 0) {
                    for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                        if (SharedMediaLayout.this.mediaPages[i].selectedType == 7) {
                            if (getItemCount() == 0) {
                                SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(false, true);
                            } else {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[i].listView, 0, null);
                            }
                        }
                    }
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public int getItemCount() {
            return this.totalCount;
        }

        @Override
        public void notifyDataSetChanged() {
            int size = this.searchAdapterHelper.getGroupSearch().size();
            this.totalCount = size;
            if (size > 0 && SharedMediaLayout.this.searching && SharedMediaLayout.this.mediaPages[0].selectedType == 7 && SharedMediaLayout.this.mediaPages[0].listView.getAdapter() != this) {
                SharedMediaLayout.this.switchToCurrentSelectedMode(false);
            }
            super.notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (i < 0 || i >= size) {
                return null;
            }
            return this.searchAdapterHelper.getGroupSearch().get(i);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 9, 5, true, SharedMediaLayout.this.resourcesProvider);
            manageChatUserCell.setBackgroundColor(SharedMediaLayout.this.getThemedColor(Theme.key_windowBackgroundWhite));
            manageChatUserCell.setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() {
                @Override
                public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell2, boolean z) {
                    boolean lambda$onCreateViewHolder$5;
                    lambda$onCreateViewHolder$5 = SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$onCreateViewHolder$5(manageChatUserCell2, z);
                    return lambda$onCreateViewHolder$5;
                }
            });
            return new RecyclerListView.Holder(manageChatUserCell);
        }

        public boolean lambda$onCreateViewHolder$5(ManageChatUserCell manageChatUserCell, boolean z) {
            TLObject item = getItem(((Integer) manageChatUserCell.getTag()).intValue());
            if (item instanceof TLRPC$ChannelParticipant) {
                return createMenuForParticipant((TLRPC$ChannelParticipant) item, !z, manageChatUserCell);
            }
            return false;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$User user;
            SpannableStringBuilder spannableStringBuilder;
            TLObject item = getItem(i);
            if (item instanceof TLRPC$ChannelParticipant) {
                user = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(((TLRPC$ChannelParticipant) item).peer)));
            } else if (!(item instanceof TLRPC$ChatParticipant)) {
                return;
            } else {
                user = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Long.valueOf(((TLRPC$ChatParticipant) item).user_id));
            }
            UserObject.getPublicUsername(user);
            this.searchAdapterHelper.getGroupSearch().size();
            String lastFoundChannel = this.searchAdapterHelper.getLastFoundChannel();
            if (lastFoundChannel != null) {
                String userName = UserObject.getUserName(user);
                spannableStringBuilder = new SpannableStringBuilder(userName);
                int indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(userName, lastFoundChannel);
                if (indexOfIgnoreCase != -1) {
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(SharedMediaLayout.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOfIgnoreCase, lastFoundChannel.length() + indexOfIgnoreCase, 33);
                }
            } else {
                spannableStringBuilder = null;
            }
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) view;
                manageChatUserCell.setTag(Integer.valueOf(i));
                manageChatUserCell.setData(user, spannableStringBuilder, null, false);
            }
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.shadowLine, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider));
        RLottieImageView iconView = this.deleteItem.getIconView();
        int i = ThemeDescription.FLAG_IMAGECOLOR;
        int i2 = Theme.key_actionBarActionModeDefaultIcon;
        arrayList.add(new ThemeDescription(iconView, i, null, null, null, null, i2));
        ActionBarMenuItem actionBarMenuItem = this.deleteItem;
        int i3 = ThemeDescription.FLAG_BACKGROUNDFILTER;
        int i4 = Theme.key_actionBarActionModeDefaultSelector;
        arrayList.add(new ThemeDescription(actionBarMenuItem, i3, null, null, null, null, i4));
        if (this.gotoItem != null) {
            arrayList.add(new ThemeDescription(this.gotoItem.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, i2));
            arrayList.add(new ThemeDescription(this.gotoItem, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, i4));
        }
        if (this.forwardItem != null) {
            arrayList.add(new ThemeDescription(this.forwardItem.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, i2));
            arrayList.add(new ThemeDescription(this.forwardItem, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, i4));
        }
        arrayList.add(new ThemeDescription(this.closeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, new Drawable[]{this.backDrawable}, null, i2));
        arrayList.add(new ThemeDescription(this.closeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, i4));
        LinearLayout linearLayout = this.actionModeLayout;
        int i5 = ThemeDescription.FLAG_BACKGROUND;
        int i6 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(linearLayout, i5, null, null, null, null, i6));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i6));
        arrayList.add(new ThemeDescription(this.floatingDateView, 0, null, null, null, null, Theme.key_chat_mediaTimeBackground));
        arrayList.add(new ThemeDescription(this.floatingDateView, 0, null, null, null, null, Theme.key_chat_mediaTimeText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip, 0, new Class[]{ScrollSlidingTextTabStrip.class}, new String[]{"selectorDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_tabSelectedLine));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_profile_tabSelectedText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_profile_tabText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, Theme.key_profile_tabSelector));
        if (this.fragmentContextView != null) {
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerBackground));
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPlayPause));
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerTitle));
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPerformer));
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerClose));
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_returnToCallBackground));
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_returnToCallText));
        }
        for (final int i7 = 0; i7 < this.mediaPages.length; i7++) {
            ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
                @Override
                public final void didSetColor() {
                    SharedMediaLayout.this.lambda$getThemeDescriptions$28(i7);
                }

                @Override
                public void onAnimationProgress(float f) {
                    ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
                }
            };
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
            FlickerLoadingView flickerLoadingView = this.mediaPages[i7].progressView;
            int i8 = Theme.key_windowBackgroundWhite;
            arrayList.add(new ThemeDescription(flickerLoadingView, 0, null, null, null, null, i8));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
            int i9 = Theme.key_progressCircle;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i9));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_creatorIcon));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
            int i10 = Theme.key_windowBackgroundWhiteBlackText;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
            int i11 = Theme.key_windowBackgroundWhiteGrayText;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, i11));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText));
            Drawable[] drawableArr = Theme.avatarDrawables;
            int i12 = Theme.key_avatar_text;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{UserCell.class}, null, drawableArr, null, i12));
            TextPaint[] textPaintArr = Theme.dialogs_namePaint;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_name));
            TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_secretName));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{ProfileSearchCell.class}, null, Theme.avatarDrawables, null, i12));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
            int i13 = Theme.key_windowBackgroundWhiteGrayText2;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyStubView.class}, new String[]{"emptyTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i13));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
            int i14 = Theme.key_sharedMedia_startStopLoadIcon;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i14));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i14));
            int i15 = Theme.key_checkbox;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i15));
            int i16 = Theme.key_checkboxCheck;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_folderIcon));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_iconText));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i9));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i15));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, null, null, i10));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, null, null, i13));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i15));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{SharedLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_linkPlaceholderText));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_linkPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_SECTIONS | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{SharedMediaSectionCell.class}, null, null, null, i8));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
            int i17 = Theme.key_sharedMedia_photoPlaceholder;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{SharedPhotoVideoCell.class}, new String[]{"backgroundPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i17));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, null, null, themeDescriptionDelegate, i15));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, null, null, themeDescriptionDelegate, i16));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{ContextLinkCell.class}, new String[]{"backgroundPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i17));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{ContextLinkCell.class}, null, null, themeDescriptionDelegate, i15));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{ContextLinkCell.class}, null, null, themeDescriptionDelegate, i16));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, null, null, new Drawable[]{this.pinnedHeaderShadowDrawable}, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i10));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i11));
        }
        return arrayList;
    }

    public void lambda$getThemeDescriptions$28(int i) {
        if (this.mediaPages[i].listView != null) {
            int childCount = this.mediaPages[i].listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.mediaPages[i].listView.getChildAt(i2);
                if (childAt instanceof SharedPhotoVideoCell) {
                    ((SharedPhotoVideoCell) childAt).updateCheckboxColor();
                } else if (childAt instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) childAt).update(0);
                } else if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
    }

    public int getNextMediaColumnsCount(int i, int i2, boolean z) {
        int i3 = i2 + (!z ? 1 : -1);
        if (i3 > 6) {
            i3 = !z ? 9 : 6;
        }
        return Utilities.clamp(i3, 9, (this.allowStoriesSingleColumn && i == 1) ? 1 : 2);
    }

    public Boolean zoomIn() {
        if (this.photoVideoChangeColumnsAnimation) {
            return null;
        }
        MediaPage mediaPage = this.mediaPages[0];
        if (mediaPage == null) {
            return null;
        }
        int i = mediaPage.selectedType;
        this.changeColumnsTab = i;
        int i2 = (i == 8 || i == 9) ? 1 : 0;
        int nextMediaColumnsCount = getNextMediaColumnsCount(i2, this.mediaColumnsCount[i2], true);
        if (this.mediaZoomInItem != null && nextMediaColumnsCount == getNextMediaColumnsCount(i2, nextMediaColumnsCount, true)) {
            this.mediaZoomInItem.setEnabled(false);
            this.mediaZoomInItem.animate().alpha(0.5f).start();
        }
        if (this.mediaColumnsCount[i2] != nextMediaColumnsCount) {
            ActionBarMenuSubItem actionBarMenuSubItem = this.mediaZoomOutItem;
            if (actionBarMenuSubItem != null && !actionBarMenuSubItem.isEnabled()) {
                this.mediaZoomOutItem.setEnabled(true);
                this.mediaZoomOutItem.animate().alpha(1.0f).start();
            }
            if (i2 != 0) {
                if (getStoriesCount(this.mediaPages[0].selectedType) >= 5) {
                    SharedConfig.setStoriesColumnsCount(nextMediaColumnsCount);
                }
            } else {
                SharedConfig.setMediaColumnsCount(nextMediaColumnsCount);
            }
            animateToMediaColumnsCount(nextMediaColumnsCount);
        }
        return Boolean.valueOf(nextMediaColumnsCount != getNextMediaColumnsCount(i2, nextMediaColumnsCount, true));
    }

    public Boolean zoomOut() {
        if (this.photoVideoChangeColumnsAnimation) {
            return null;
        }
        MediaPage mediaPage = this.mediaPages[0];
        if (mediaPage == null) {
            return null;
        }
        if (this.allowStoriesSingleColumn && (mediaPage.selectedType == 8 || this.mediaPages[0].selectedType == 9)) {
            return null;
        }
        int i = this.mediaPages[0].selectedType;
        this.changeColumnsTab = i;
        int i2 = (i == 8 || i == 9) ? 1 : 0;
        int nextMediaColumnsCount = getNextMediaColumnsCount(i2, this.mediaColumnsCount[i2], false);
        if (this.mediaZoomOutItem != null && nextMediaColumnsCount == getNextMediaColumnsCount(i2, nextMediaColumnsCount, false)) {
            this.mediaZoomOutItem.setEnabled(false);
            this.mediaZoomOutItem.animate().alpha(0.5f).start();
        }
        if (this.mediaColumnsCount[i2] != nextMediaColumnsCount) {
            ActionBarMenuSubItem actionBarMenuSubItem = this.mediaZoomInItem;
            if (actionBarMenuSubItem != null && !actionBarMenuSubItem.isEnabled()) {
                this.mediaZoomInItem.setEnabled(true);
                this.mediaZoomInItem.animate().alpha(1.0f).start();
            }
            if (i2 != 0) {
                if (getStoriesCount(this.mediaPages[0].selectedType) >= 5) {
                    SharedConfig.setStoriesColumnsCount(nextMediaColumnsCount);
                }
            } else {
                SharedConfig.setMediaColumnsCount(nextMediaColumnsCount);
            }
            animateToMediaColumnsCount(nextMediaColumnsCount);
        }
        return Boolean.valueOf(nextMediaColumnsCount != getNextMediaColumnsCount(i2, nextMediaColumnsCount, false));
    }

    public boolean canZoomIn() {
        MediaPage mediaPage;
        MediaPage[] mediaPageArr = this.mediaPages;
        if (mediaPageArr == null || (mediaPage = mediaPageArr[0]) == null) {
            return false;
        }
        int i = (mediaPage.selectedType == 8 || this.mediaPages[0].selectedType == 9) ? 1 : 0;
        int i2 = this.mediaColumnsCount[i];
        return i2 != getNextMediaColumnsCount(i, i2, true);
    }

    public boolean canZoomOut() {
        MediaPage mediaPage;
        MediaPage[] mediaPageArr = this.mediaPages;
        if (mediaPageArr == null || (mediaPage = mediaPageArr[0]) == null) {
            return false;
        }
        if (this.allowStoriesSingleColumn && (mediaPage.selectedType == 8 || this.mediaPages[0].selectedType == 9)) {
            return false;
        }
        int i = (this.mediaPages[0].selectedType == 8 || this.mediaPages[0].selectedType == 9) ? 1 : 0;
        int i2 = this.mediaColumnsCount[i];
        return i2 != getNextMediaColumnsCount(i, i2, false);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (view == this.fragmentContextView) {
            canvas.save();
            canvas.clipRect(0, this.mediaPages[0].getTop(), view.getMeasuredWidth(), this.mediaPages[0].getTop() + view.getMeasuredHeight() + AndroidUtilities.dp(12.0f));
            boolean drawChild = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild;
        }
        return super.drawChild(canvas, view, j);
    }

    public class ScrollSlidingTextTabStripInner extends ScrollSlidingTextTabStrip {
        public int backgroundColor;
        protected Paint backgroundPaint;
        private android.graphics.Rect blurBounds;

        public ScrollSlidingTextTabStripInner(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            this.backgroundColor = 0;
            this.blurBounds = new android.graphics.Rect();
        }

        protected void drawBackground(Canvas canvas) {
            if (!SharedConfig.chatBlurEnabled() || this.backgroundColor == 0) {
                return;
            }
            if (this.backgroundPaint == null) {
                this.backgroundPaint = new Paint();
            }
            this.backgroundPaint.setColor(this.backgroundColor);
            this.blurBounds.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            SharedMediaLayout.this.drawBackgroundWithBlur(canvas, getY(), this.blurBounds, this.backgroundPaint);
        }

        @Override
        public void setBackgroundColor(int i) {
            this.backgroundColor = i;
            invalidate();
        }
    }

    public int getThemedColor(int i) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider != null) {
            return resourcesProvider.getColor(i);
        }
        return Theme.getColor(i);
    }

    public float getTabProgress() {
        float f = 0.0f;
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return f;
            }
            if (mediaPageArr[i] != null) {
                f += r2.selectedType * (1.0f - Math.abs(this.mediaPages[i].getTranslationX() / getWidth()));
            }
            i++;
        }
    }

    public static class InternalListView extends BlurredRecyclerView implements StoriesListPlaceProvider.ClippedView {
        public int hintPaddingBottom;
        public int hintPaddingTop;

        public InternalListView(Context context) {
            super(context);
        }

        @Override
        public void updateClip(int[] iArr) {
            iArr[0] = (getPaddingTop() - AndroidUtilities.dp(2.0f)) - this.hintPaddingTop;
            iArr[1] = (getMeasuredHeight() - getPaddingBottom()) - this.hintPaddingBottom;
        }
    }

    public void updateOptionsSearch() {
        updateOptionsSearch(false);
    }

    public void updateOptionsSearch(boolean z) {
        RLottieImageView rLottieImageView = this.optionsSearchImageView;
        if (rLottieImageView == null) {
            return;
        }
        rLottieImageView.setAlpha(this.searching ? 0.0f : Utilities.clamp(this.searchAlpha + this.optionsAlpha, 1.0f, 0.0f));
        if (z) {
            animateSearchToOptions(getPhotoVideoOptionsAlpha(1.0f) > 0.5f, true);
        } else if (this.searchItemState == 2) {
            animateSearchToOptions(this.optionsAlpha > 0.1f, true);
        } else {
            animateSearchToOptions(this.searchAlpha < 0.1f, true);
        }
    }

    public void animateSearchToOptions(boolean z, boolean z2) {
        RLottieImageView rLottieImageView = this.optionsSearchImageView;
        if (rLottieImageView == null || this.animatingToOptions == z) {
            return;
        }
        this.animatingToOptions = z;
        if (!z && rLottieImageView.getAnimatedDrawable().getCurrentFrame() < 20) {
            this.optionsSearchImageView.getAnimatedDrawable().setCustomEndFrame(0);
        } else {
            this.optionsSearchImageView.getAnimatedDrawable().setCustomEndFrame(this.animatingToOptions ? 50 : 100);
        }
        if (z2) {
            this.optionsSearchImageView.getAnimatedDrawable().start();
        } else {
            this.optionsSearchImageView.getAnimatedDrawable().setCurrentFrame(this.optionsSearchImageView.getAnimatedDrawable().getCustomEndFrame());
        }
    }

    private CharSequence addPostText() {
        if (this.addPostButton == null) {
            this.addPostButton = new SpannableStringBuilder();
            if (isBot()) {
                this.addPostButton.append((CharSequence) LocaleController.getString(R.string.ProfileBotPreviewEmptyButton));
            } else {
                this.addPostButton.append((CharSequence) "c");
                this.addPostButton.setSpan(new ColoredImageSpan(R.drawable.filled_premium_camera), 0, 1, 33);
                this.addPostButton.append((CharSequence) "  ").append((CharSequence) LocaleController.getString(R.string.StoriesAddPost));
            }
        }
        return this.addPostButton;
    }

    public boolean canEditStories() {
        BaseFragment baseFragment;
        if (!isBot()) {
            return isStoriesView() || ((baseFragment = this.profileActivity) != null && baseFragment.getMessagesController().getStoriesController().canEditStories(this.dialog_id));
        }
        TLRPC$User user = MessagesController.getInstance(this.profileActivity.getCurrentAccount()).getUser(Long.valueOf(this.dialog_id));
        return user != null && user.bot && user.bot_can_edit;
    }

    public void openStoryRecorder() {
        StoryRecorder.getInstance(this.profileActivity.getParentActivity(), this.profileActivity.getCurrentAccount()).open(null);
    }

    public boolean isSearchingStories() {
        return (TextUtils.isEmpty(getStoriesHashtag()) && getStoriesArea() == null) ? false : true;
    }

    public static class SharedMediaListView extends InternalListView {
        private final ArrayList<SharedPhotoVideoCell2> animationSupportingSortedCells;
        private int animationSupportingSortedCellsOffset;
        protected StaticLayout archivedHintLayout;
        protected float archivedHintLayoutLeft;
        protected float archivedHintLayoutWidth;
        protected TextPaint archivedHintPaint;
        final ArrayList<SharedPhotoVideoCell2> drawingViews;
        final ArrayList<SharedPhotoVideoCell2> drawingViews2;
        final ArrayList<SharedPhotoVideoCell2> drawingViews3;
        final HashSet<SharedPhotoVideoCell2> excludeDrawViews;
        UserListPoller poller;

        public void checkHighlightCell(SharedPhotoVideoCell2 sharedPhotoVideoCell2) {
        }

        public int getAnimateToColumnsCount() {
            return 3;
        }

        public float getChangeColumnsProgress() {
            return 0.0f;
        }

        public int getColumnsCount() {
            return 3;
        }

        public SparseArray<Float> getMessageAlphaEnter() {
            return null;
        }

        public RecyclerListView.FastScrollAdapter getMovingAdapter() {
            return null;
        }

        public int getPinchCenterPosition() {
            return 0;
        }

        public RecyclerListView.FastScrollAdapter getSupportingAdapter() {
            return null;
        }

        public InternalListView getSupportingListView() {
            return null;
        }

        public boolean isChangeColumnsAnimation() {
            return false;
        }

        public boolean isStories() {
            return false;
        }

        public boolean isThisListView() {
            return true;
        }

        public SharedMediaListView(Context context) {
            super(context);
            this.excludeDrawViews = new HashSet<>();
            this.drawingViews = new ArrayList<>();
            this.drawingViews2 = new ArrayList<>();
            this.drawingViews3 = new ArrayList<>();
            this.animationSupportingSortedCells = new ArrayList<>();
        }

        @Override
        public void dispatchDraw(android.graphics.Canvas r32) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.SharedMediaListView.dispatchDraw(android.graphics.Canvas):void");
        }

        @Override
        public boolean drawChild(Canvas canvas, View view, long j) {
            RecyclerListView.FastScrollAdapter movingAdapter = getMovingAdapter();
            if (isThisListView() && getAdapter() == movingAdapter && isChangeColumnsAnimation() && (view instanceof SharedPhotoVideoCell2)) {
                return true;
            }
            return super.drawChild(canvas, view, j);
        }
    }
}
