package org.telegram.ui.Stories;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Consumer;
import j$.util.Comparator$CC;
import j$.util.function.ToIntFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$StoryItem;
import org.telegram.tgnet.TLRPC$StoryViews;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_stories_getStoryViewsList;
import org.telegram.tgnet.TLRPC$TL_stories_storyViewsList;
import org.telegram.tgnet.TLRPC$TL_storyView;
import org.telegram.tgnet.TLRPC$TL_storyViews;
import org.telegram.tgnet.TLRPC$TL_userStories;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.FixedHeightEmptyCell;
import org.telegram.ui.Cells.ReactedUserHolderView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.CustomPopupMenu;
import org.telegram.ui.Components.FillLastLinearLayoutManager;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ReplaceableIconDrawable;
import org.telegram.ui.Components.SearchField;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.RecyclerListViewScroller;
import org.telegram.ui.Stories.SelfStoryViewsPage;
import org.telegram.ui.Stories.SelfStoryViewsView;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.recorder.StoryEntry;
import org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet;
public class SelfStoryViewsPage extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private int TOP_PADDING;
    private boolean checkAutoscroll;
    int currentAccount;
    ViewsModel currentModel;
    ViewsModel defaultModel;
    HeaderView headerView;
    boolean isSearchDebounce;
    public FillLastLinearLayoutManager layoutManager;
    ListAdapter listAdapter;
    private int measuerdHeight;
    Consumer<SelfStoryViewsPage> onSharedStateChanged;
    private CustomPopupMenu popupMenu;
    RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
    RecyclerListView recyclerListView;
    Theme.ResourcesProvider resourcesProvider;
    RecyclerAnimationScrollHelper scrollHelper;
    private final RecyclerListViewScroller scroller;
    SearchField searchField;
    Drawable shadowDrawable;
    private final View shadowView;
    private final View shadowView2;
    final FiltersState sharedFilterState;
    private boolean showContactsFilter;
    private boolean showReactionsSort;
    private boolean showSearch;
    final FiltersState state;
    SelfStoryViewsView.StoryItemInternal storyItem;
    StoryViewer storyViewer;
    private final TextView titleView;
    private final FrameLayout topViewsContainer;

    public void onTopOffsetChanged(int i) {
    }

    public void updateSharedState() {
    }

    public boolean isStoryShownToUser(TLRPC$TL_storyView tLRPC$TL_storyView) {
        StoryEntry storyEntry;
        StoryPrivacyBottomSheet.StoryPrivacy storyPrivacy;
        if (!MessagesController.getInstance(this.currentAccount).getStoriesController().isBlocked(tLRPC$TL_storyView) && MessagesController.getInstance(this.currentAccount).blockePeers.indexOfKey(tLRPC$TL_storyView.user_id) < 0) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tLRPC$TL_storyView.user_id));
            SelfStoryViewsView.StoryItemInternal storyItemInternal = this.storyItem;
            if (storyItemInternal != null) {
                TLRPC$StoryItem tLRPC$StoryItem = storyItemInternal.storyItem;
                if (tLRPC$StoryItem != null) {
                    if (tLRPC$StoryItem.parsedPrivacy == null) {
                        tLRPC$StoryItem.parsedPrivacy = new StoryPrivacyBottomSheet.StoryPrivacy(this.currentAccount, tLRPC$StoryItem.privacy);
                    }
                    return this.storyItem.storyItem.parsedPrivacy.containsUser(user);
                }
                StoriesController.UploadingStory uploadingStory = storyItemInternal.uploadingStory;
                if (uploadingStory == null || (storyEntry = uploadingStory.entry) == null || (storyPrivacy = storyEntry.privacy) == null) {
                    return true;
                }
                return storyPrivacy.containsUser(user);
            }
            return true;
        }
        return false;
    }

    public SelfStoryViewsPage(final StoryViewer storyViewer, Context context, FiltersState filtersState, Consumer<SelfStoryViewsPage> consumer) {
        super(context);
        this.TOP_PADDING = 96;
        this.state = new FiltersState();
        this.sharedFilterState = filtersState;
        this.onSharedStateChanged = consumer;
        this.resourcesProvider = storyViewer.resourcesProvider;
        this.storyViewer = storyViewer;
        this.currentAccount = storyViewer.currentAccount;
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, this.resourcesProvider));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
        this.headerView = new HeaderView(getContext());
        RecyclerListViewInner recyclerListViewInner = new RecyclerListViewInner(context, this.resourcesProvider) {
            @Override
            public void onMeasure(int i, int i2) {
                SelfStoryViewsPage.this.measuerdHeight = View.MeasureSpec.getSize(i2);
                super.onMeasure(i, i2);
            }
        };
        this.recyclerListView = recyclerListViewInner;
        recyclerListViewInner.setClipToPadding(false);
        this.recyclerItemsEnterAnimator = new RecyclerItemsEnterAnimator(this.recyclerListView, true);
        RecyclerListView recyclerListView = this.recyclerListView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(context, 0, recyclerListView);
        this.layoutManager = fillLastLinearLayoutManager;
        recyclerListView.setLayoutManager(fillLastLinearLayoutManager);
        this.recyclerListView.setNestedScrollingEnabled(true);
        RecyclerListView recyclerListView2 = this.recyclerListView;
        ListAdapter listAdapter = new ListAdapter();
        this.listAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        RecyclerAnimationScrollHelper recyclerAnimationScrollHelper = new RecyclerAnimationScrollHelper(this.recyclerListView, this.layoutManager);
        this.scrollHelper = recyclerAnimationScrollHelper;
        recyclerAnimationScrollHelper.setScrollListener(new RecyclerAnimationScrollHelper.ScrollListener() {
            @Override
            public void onScroll() {
                SelfStoryViewsPage.this.invalidate();
            }
        });
        addView(this.recyclerListView);
        this.scroller = new RecyclerListViewScroller(this.recyclerListView);
        this.recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                SelfStoryViewsPage.this.checkLoadMore();
                SelfStoryViewsPage.this.invalidate();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
                if (i == 0) {
                    SelfStoryViewsPage.this.checkAutoscroll = true;
                    SelfStoryViewsPage.this.invalidate();
                }
                if (i == 1) {
                    SelfStoryViewsPage.this.checkAutoscroll = false;
                    SelfStoryViewsPage.this.scroller.cancel();
                    AndroidUtilities.hideKeyboard(SelfStoryViewsPage.this);
                }
            }
        });
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                SelfStoryViewsPage.this.lambda$new$0(storyViewer, view, i);
            }
        });
        this.recyclerListView.setOnItemLongClickListener(new AnonymousClass4(storyViewer));
        this.listAdapter.updateRows();
        FrameLayout frameLayout = new FrameLayout(getContext());
        this.topViewsContainer = frameLayout;
        View view = new View(getContext());
        this.shadowView = view;
        GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        int i = Theme.key_dialogBackground;
        view.setBackground(new GradientDrawable(orientation, new int[]{Theme.getColor(i, this.resourcesProvider), 0}));
        frameLayout.addView(view, LayoutHelper.createFrame(-1, 8.0f, 0, 0.0f, this.TOP_PADDING - 8, 0.0f, 0.0f));
        View view2 = new View(getContext());
        this.shadowView2 = view2;
        view2.setBackgroundColor(Theme.getColor(i, this.resourcesProvider));
        frameLayout.addView(view2, LayoutHelper.createFrame(-1, 10.0f, 0, 0.0f, this.TOP_PADDING - 17, 0.0f, 0.0f));
        frameLayout.addView(this.headerView);
        frameLayout.addView(textView);
        AnonymousClass5 anonymousClass5 = new AnonymousClass5(getContext(), true, 13.0f, this.resourcesProvider);
        this.searchField = anonymousClass5;
        anonymousClass5.setHint(LocaleController.getString("Search", R.string.Search));
        frameLayout.addView(this.searchField, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        addView(frameLayout);
    }

    public void lambda$new$0(StoryViewer storyViewer, View view, int i) {
        TLRPC$TL_storyView tLRPC$TL_storyView;
        if (i < 0 || i >= this.listAdapter.items.size() || (tLRPC$TL_storyView = this.listAdapter.items.get(i).user) == null) {
            return;
        }
        storyViewer.presentFragment(ProfileActivity.of(tLRPC$TL_storyView.user_id));
    }

    public class AnonymousClass4 implements RecyclerListView.OnItemLongClickListener {
        final StoryViewer val$storyViewer;

        AnonymousClass4(StoryViewer storyViewer) {
            this.val$storyViewer = storyViewer;
        }

        @Override
        public boolean onItemClick(View view, int i) {
            final MessagesController messagesController;
            final TLRPC$User user;
            if (view instanceof ReactedUserHolderView) {
                final ReactedUserHolderView reactedUserHolderView = (ReactedUserHolderView) view;
                StoryViewer storyViewer = this.val$storyViewer;
                if (storyViewer == null || storyViewer.containerView == null) {
                    return false;
                }
                final TLRPC$TL_storyView tLRPC$TL_storyView = SelfStoryViewsPage.this.listAdapter.items.get(i).user;
                if (tLRPC$TL_storyView == null || (user = (messagesController = MessagesController.getInstance(SelfStoryViewsPage.this.currentAccount)).getUser(Long.valueOf(tLRPC$TL_storyView.user_id))) == null) {
                    return false;
                }
                boolean z = messagesController.blockePeers.indexOfKey(user.id) >= 0;
                boolean z2 = user.contact || ContactsController.getInstance(SelfStoryViewsPage.this.currentAccount).contactsDict.get(Long.valueOf(user.id)) != null;
                boolean isStoryShownToUser = SelfStoryViewsPage.this.isStoryShownToUser(tLRPC$TL_storyView);
                boolean isBlocked = messagesController.getStoriesController().isBlocked(tLRPC$TL_storyView);
                String str = TextUtils.isEmpty(user.first_name) ? TextUtils.isEmpty(user.last_name) ? "" : user.last_name : user.first_name;
                int indexOf = str.indexOf(" ");
                if (indexOf > 2) {
                    str = str.substring(0, indexOf);
                }
                final String str2 = str;
                ItemOptions cutTextInFancyHalf = ItemOptions.makeOptions(this.val$storyViewer.containerView, SelfStoryViewsPage.this.resourcesProvider, view).setGravity(3).ignoreX().setScrimViewBackground(new ColorDrawable(Theme.getColor(Theme.key_dialogBackground, SelfStoryViewsPage.this.resourcesProvider))).setDimAlpha(133).addIf((!isStoryShownToUser || isBlocked || z) ? false : true, R.drawable.msg_stories_myhide, LocaleController.formatString(R.string.StoryHideFrom, str2), new Runnable() {
                    @Override
                    public final void run() {
                        SelfStoryViewsPage.AnonymousClass4.this.lambda$onItemClick$0(messagesController, user, str2, reactedUserHolderView, tLRPC$TL_storyView);
                    }
                }).makeMultiline(false).cutTextInFancyHalf().addIf(isBlocked && !z, R.drawable.msg_menu_stories, LocaleController.formatString(R.string.StoryShowBackTo, str2), new Runnable() {
                    @Override
                    public final void run() {
                        SelfStoryViewsPage.AnonymousClass4.this.lambda$onItemClick$1(messagesController, user, str2, reactedUserHolderView, tLRPC$TL_storyView);
                    }
                }).makeMultiline(false).cutTextInFancyHalf();
                boolean z3 = (z2 || z) ? false : true;
                int i2 = R.drawable.msg_user_remove;
                cutTextInFancyHalf.addIf(z3, i2, LocaleController.getString(R.string.BlockUser), true, new Runnable() {
                    @Override
                    public final void run() {
                        SelfStoryViewsPage.AnonymousClass4.this.lambda$onItemClick$2(messagesController, user, reactedUserHolderView, tLRPC$TL_storyView);
                    }
                }).addIf(!z2 && z, R.drawable.msg_block, LocaleController.getString(R.string.Unblock), new Runnable() {
                    @Override
                    public final void run() {
                        SelfStoryViewsPage.AnonymousClass4.this.lambda$onItemClick$3(messagesController, user, reactedUserHolderView, tLRPC$TL_storyView);
                    }
                }).addIf(z2, i2, LocaleController.getString(R.string.DeleteContact), true, new Runnable() {
                    @Override
                    public final void run() {
                        SelfStoryViewsPage.AnonymousClass4.this.lambda$onItemClick$4(user, str2, reactedUserHolderView, tLRPC$TL_storyView);
                    }
                }).show();
                try {
                    try {
                        SelfStoryViewsPage.this.performHapticFeedback(0, 1);
                        return true;
                    } catch (Exception unused) {
                        return true;
                    }
                } catch (Exception unused2) {
                    return true;
                }
            }
            return false;
        }

        public void lambda$onItemClick$0(MessagesController messagesController, TLRPC$User tLRPC$User, String str, ReactedUserHolderView reactedUserHolderView, TLRPC$TL_storyView tLRPC$TL_storyView) {
            messagesController.getStoriesController().updateBlockUser(tLRPC$User.id, true);
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            BulletinFactory.of(selfStoryViewsPage, selfStoryViewsPage.resourcesProvider).createSimpleBulletin(R.raw.ic_ban, LocaleController.formatString(R.string.StoryHidFromToast, str)).show();
            reactedUserHolderView.animateAlpha(SelfStoryViewsPage.this.isStoryShownToUser(tLRPC$TL_storyView) ? 1.0f : 0.5f, true);
        }

        public void lambda$onItemClick$1(MessagesController messagesController, TLRPC$User tLRPC$User, String str, ReactedUserHolderView reactedUserHolderView, TLRPC$TL_storyView tLRPC$TL_storyView) {
            messagesController.getStoriesController().updateBlockUser(tLRPC$User.id, false);
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            BulletinFactory.of(selfStoryViewsPage, selfStoryViewsPage.resourcesProvider).createSimpleBulletin(R.raw.contact_check, LocaleController.formatString(R.string.StoryShownBackToToast, str)).show();
            reactedUserHolderView.animateAlpha(SelfStoryViewsPage.this.isStoryShownToUser(tLRPC$TL_storyView) ? 1.0f : 0.5f, true);
        }

        public void lambda$onItemClick$2(MessagesController messagesController, TLRPC$User tLRPC$User, ReactedUserHolderView reactedUserHolderView, TLRPC$TL_storyView tLRPC$TL_storyView) {
            messagesController.blockPeer(tLRPC$User.id);
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            BulletinFactory.of(selfStoryViewsPage, selfStoryViewsPage.resourcesProvider).createBanBulletin(true).show();
            reactedUserHolderView.animateAlpha(SelfStoryViewsPage.this.isStoryShownToUser(tLRPC$TL_storyView) ? 1.0f : 0.5f, true);
        }

        public void lambda$onItemClick$3(MessagesController messagesController, TLRPC$User tLRPC$User, ReactedUserHolderView reactedUserHolderView, TLRPC$TL_storyView tLRPC$TL_storyView) {
            messagesController.getStoriesController().updateBlockUser(tLRPC$User.id, false);
            messagesController.unblockPeer(tLRPC$User.id);
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            BulletinFactory.of(selfStoryViewsPage, selfStoryViewsPage.resourcesProvider).createBanBulletin(false).show();
            reactedUserHolderView.animateAlpha(SelfStoryViewsPage.this.isStoryShownToUser(tLRPC$TL_storyView) ? 1.0f : 0.5f, true);
        }

        public void lambda$onItemClick$4(TLRPC$User tLRPC$User, String str, ReactedUserHolderView reactedUserHolderView, TLRPC$TL_storyView tLRPC$TL_storyView) {
            ArrayList<TLRPC$User> arrayList = new ArrayList<>();
            arrayList.add(tLRPC$User);
            ContactsController.getInstance(SelfStoryViewsPage.this.currentAccount).deleteContact(arrayList, false);
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            BulletinFactory.of(selfStoryViewsPage, selfStoryViewsPage.resourcesProvider).createSimpleBulletin(R.raw.ic_ban, LocaleController.formatString(R.string.DeletedFromYourContacts, str)).show();
            reactedUserHolderView.animateAlpha(SelfStoryViewsPage.this.isStoryShownToUser(tLRPC$TL_storyView) ? 1.0f : 0.5f, true);
        }
    }

    public class AnonymousClass5 extends SearchField {
        Runnable runnable;

        AnonymousClass5(Context context, boolean z, float f, Theme.ResourcesProvider resourcesProvider) {
            super(context, z, f, resourcesProvider);
        }

        @Override
        public void onTextChange(final String str) {
            Runnable runnable = this.runnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            this.runnable = new Runnable() {
                @Override
                public final void run() {
                    SelfStoryViewsPage.AnonymousClass5.this.lambda$onTextChange$0(str);
                }
            };
            if (!TextUtils.isEmpty(str)) {
                AndroidUtilities.runOnUIThread(this.runnable, 300L);
            } else {
                this.runnable.run();
            }
            if (this.runnable != null) {
                SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
                if (selfStoryViewsPage.isSearchDebounce) {
                    return;
                }
                selfStoryViewsPage.isSearchDebounce = true;
                selfStoryViewsPage.listAdapter.updateRows();
                SelfStoryViewsPage selfStoryViewsPage2 = SelfStoryViewsPage.this;
                selfStoryViewsPage2.layoutManager.scrollToPositionWithOffset(0, -selfStoryViewsPage2.recyclerListView.getPaddingTop());
            }
        }

        public void lambda$onTextChange$0(String str) {
            this.runnable = null;
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            selfStoryViewsPage.isSearchDebounce = false;
            selfStoryViewsPage.state.searchQuery = str.toLowerCase();
            SelfStoryViewsPage.this.reload();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int paddingTop;
        View view = null;
        int i = -1;
        for (int i2 = 0; i2 < this.recyclerListView.getChildCount(); i2++) {
            View childAt = this.recyclerListView.getChildAt(i2);
            int childLayoutPosition = this.recyclerListView.getChildLayoutPosition(childAt);
            if (childLayoutPosition < i || i == -1) {
                view = childAt;
                i = childLayoutPosition;
            }
        }
        if (i == 0) {
            paddingTop = (int) Math.max(0.0f, view.getY());
        } else {
            paddingTop = i > 0 ? 0 : this.recyclerListView.getPaddingTop();
        }
        float f = paddingTop;
        if (this.topViewsContainer.getTranslationY() != f) {
            this.topViewsContainer.setTranslationY(f);
            onTopOffsetChanged(paddingTop);
        }
        this.shadowDrawable.setBounds(-AndroidUtilities.dp(6.0f), paddingTop, getMeasuredWidth() + AndroidUtilities.dp(6.0f), getMeasuredHeight());
        this.shadowDrawable.draw(canvas);
        if (this.checkAutoscroll) {
            this.checkAutoscroll = false;
            if (this.topViewsContainer.getTranslationY() != 0.0f && this.topViewsContainer.getTranslationY() != this.recyclerListView.getPaddingTop()) {
                if (this.topViewsContainer.getTranslationY() > this.recyclerListView.getPaddingTop() / 2.0f) {
                    this.scroller.smoothScrollBy((int) (-(this.recyclerListView.getPaddingTop() - this.topViewsContainer.getTranslationY())));
                } else {
                    this.scroller.smoothScrollBy((int) this.topViewsContainer.getTranslationY());
                }
            }
        }
        super.dispatchDraw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getY() < this.topViewsContainer.getTranslationY()) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getY() < this.topViewsContainer.getTranslationY()) {
            return false;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (view == this.recyclerListView) {
            canvas.save();
            canvas.clipRect(0, AndroidUtilities.dp(this.TOP_PADDING), getMeasuredWidth(), getMeasuredHeight());
            super.drawChild(canvas, view, j);
            canvas.restore();
            return true;
        }
        return super.drawChild(canvas, view, j);
    }

    public void checkLoadMore() {
        if (this.currentModel == null || this.layoutManager.findLastVisibleItemPosition() <= this.listAdapter.getItemCount() - 10) {
            return;
        }
        this.currentModel.loadNext();
    }

    public void setStoryItem(SelfStoryViewsView.StoryItemInternal storyItemInternal) {
        this.storyItem = storyItemInternal;
        updateViewsVisibility();
        updateViewState(false);
    }

    private void updateViewsVisibility() {
        this.showSearch = false;
        this.showContactsFilter = false;
        this.showReactionsSort = false;
        TLRPC$StoryItem tLRPC$StoryItem = this.storyItem.storyItem;
        if (tLRPC$StoryItem != null) {
            TLRPC$StoryViews tLRPC$StoryViews = tLRPC$StoryItem.views;
            boolean z = true;
            if (tLRPC$StoryViews != null) {
                int i = tLRPC$StoryViews.views_count;
                this.showSearch = i >= 15;
                this.showReactionsSort = tLRPC$StoryViews.reactions_count >= (BuildVars.DEBUG_PRIVATE_VERSION ? 5 : 10);
                this.showContactsFilter = (i < 20 || tLRPC$StoryItem.contacts || tLRPC$StoryItem.close_friends || tLRPC$StoryItem.selected_contacts) ? false : true;
            }
            ViewsModel viewsModel = MessagesController.getInstance(this.currentAccount).storiesController.selfViewsModel.get(tLRPC$StoryItem.id);
            this.defaultModel = viewsModel;
            TLRPC$StoryViews tLRPC$StoryViews2 = tLRPC$StoryItem.views;
            int i2 = tLRPC$StoryViews2 == null ? 0 : tLRPC$StoryViews2.views_count;
            if (viewsModel == null || viewsModel.totalCount != i2) {
                if (viewsModel != null) {
                    viewsModel.release();
                }
                ViewsModel viewsModel2 = new ViewsModel(this.currentAccount, tLRPC$StoryItem, true);
                this.defaultModel = viewsModel2;
                viewsModel2.reloadIfNeed(this.state, this.showContactsFilter, this.showReactionsSort);
                this.defaultModel.loadNext();
                MessagesController.getInstance(this.currentAccount).storiesController.selfViewsModel.put(tLRPC$StoryItem.id, this.defaultModel);
            } else {
                viewsModel.reloadIfNeed(this.state, this.showContactsFilter, this.showReactionsSort);
            }
            ViewsModel viewsModel3 = this.defaultModel;
            this.currentModel = viewsModel3;
            if (!viewsModel3.isExpiredViews || UserConfig.getInstance(this.currentAccount).isPremium()) {
                ViewsModel viewsModel4 = this.currentModel;
                if (viewsModel4.loading || viewsModel4.hasNext || !viewsModel4.views.isEmpty() || !TextUtils.isEmpty(this.currentModel.state.searchQuery)) {
                    TLRPC$StoryViews tLRPC$StoryViews3 = tLRPC$StoryItem.views;
                    if (tLRPC$StoryViews3 == null || tLRPC$StoryViews3.views_count == 0) {
                        this.showSearch = false;
                        this.showReactionsSort = false;
                        this.showContactsFilter = false;
                        this.titleView.setText(LocaleController.getString("Viewers", R.string.Viewers));
                        this.searchField.setVisibility(8);
                        this.headerView.setVisibility(8);
                        this.TOP_PADDING = 46;
                    } else {
                        this.headerView.setVisibility(0);
                        ViewsModel viewsModel5 = this.currentModel;
                        if (viewsModel5.showReactionOnly) {
                            TextView textView = this.titleView;
                            int i3 = tLRPC$StoryItem.views.reactions_count;
                            textView.setText(LocaleController.formatPluralString("Likes", i3, Integer.valueOf(i3)));
                            this.showSearch = false;
                            this.showReactionsSort = false;
                            this.showContactsFilter = false;
                        } else {
                            if (viewsModel5.views.size() < 20 && this.currentModel.views.size() < tLRPC$StoryItem.views.views_count) {
                                ViewsModel viewsModel6 = this.currentModel;
                                if (!viewsModel6.loading && !viewsModel6.hasNext) {
                                    this.showSearch = false;
                                    this.showReactionsSort = false;
                                    this.showContactsFilter = false;
                                    this.titleView.setText(LocaleController.getString("Viewers", R.string.Viewers));
                                }
                            }
                            TLRPC$StoryViews tLRPC$StoryViews4 = tLRPC$StoryItem.views;
                            int i4 = tLRPC$StoryViews4.views_count;
                            this.showSearch = i4 >= 15;
                            this.showReactionsSort = tLRPC$StoryViews4.reactions_count >= (BuildVars.DEBUG_VERSION ? 5 : 10);
                            this.showContactsFilter = (i4 < 20 || tLRPC$StoryItem.contacts || tLRPC$StoryItem.close_friends || tLRPC$StoryItem.selected_contacts) ? false : false;
                            this.titleView.setText(LocaleController.getString("Viewers", R.string.Viewers));
                        }
                        this.searchField.setVisibility(this.showSearch ? 0 : 8);
                        this.TOP_PADDING = this.showSearch ? 96 : 46;
                    }
                }
            }
            this.showSearch = false;
            this.showReactionsSort = false;
            this.showContactsFilter = false;
            this.titleView.setText(LocaleController.getString("Viewers", R.string.Viewers));
            this.searchField.setVisibility(8);
            this.headerView.setVisibility(8);
            this.TOP_PADDING = 46;
        } else {
            this.TOP_PADDING = 46;
            this.titleView.setText(LocaleController.getString("UploadingStory", R.string.UploadingStory));
            this.searchField.setVisibility(8);
            this.headerView.setVisibility(8);
        }
        this.headerView.buttonContainer.setVisibility(this.showReactionsSort ? 0 : 8);
        this.headerView.allViewersView.setVisibility(this.showContactsFilter ? 0 : 8);
        this.headerView.contactsViewersView.setVisibility(this.showContactsFilter ? 0 : 8);
        if (!this.showContactsFilter) {
            this.titleView.setVisibility(0);
        } else {
            this.titleView.setVisibility(8);
        }
        ((ViewGroup.MarginLayoutParams) this.shadowView.getLayoutParams()).topMargin = AndroidUtilities.dp(this.TOP_PADDING - 8);
        ((ViewGroup.MarginLayoutParams) this.shadowView2.getLayoutParams()).topMargin = AndroidUtilities.dp(this.TOP_PADDING - 17);
    }

    public static void preload(int i, TLRPC$StoryItem tLRPC$StoryItem) {
        if (tLRPC$StoryItem == null) {
            return;
        }
        ViewsModel viewsModel = MessagesController.getInstance(i).storiesController.selfViewsModel.get(tLRPC$StoryItem.id);
        TLRPC$StoryViews tLRPC$StoryViews = tLRPC$StoryItem.views;
        int i2 = tLRPC$StoryViews == null ? 0 : tLRPC$StoryViews.views_count;
        if (viewsModel == null || viewsModel.totalCount != i2) {
            if (viewsModel != null) {
                viewsModel.release();
            }
            ViewsModel viewsModel2 = new ViewsModel(i, tLRPC$StoryItem, true);
            viewsModel2.loadNext();
            MessagesController.getInstance(i).storiesController.selfViewsModel.put(tLRPC$StoryItem.id, viewsModel2);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewsModel viewsModel = this.currentModel;
        if (viewsModel != null) {
            viewsModel.addListener(this);
            this.currentModel.animateDateForUsers.clear();
        }
        this.listAdapter.updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.storiesUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.storiesBlocklistUpdate);
        Bulletin.addDelegate(this, new Bulletin.Delegate() {
            @Override
            public boolean allowLayoutChanges() {
                return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
            }

            @Override
            public boolean clipWithGradient(int i) {
                return Bulletin.Delegate.CC.$default$clipWithGradient(this, i);
            }

            @Override
            public int getTopOffset(int i) {
                return Bulletin.Delegate.CC.$default$getTopOffset(this, i);
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
            public int getBottomOffset(int i) {
                return SelfStoryViewsPage.this.recyclerListView.getPaddingBottom();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ViewsModel viewsModel = this.currentModel;
        if (viewsModel != null) {
            viewsModel.removeListener(this);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.storiesUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.storiesBlocklistUpdate);
        Bulletin.removeDelegate(this);
    }

    public void onDataRecieved(ViewsModel viewsModel) {
        NotificationCenter.getInstance(this.currentAccount).doOnIdle(new Runnable() {
            @Override
            public final void run() {
                SelfStoryViewsPage.this.lambda$onDataRecieved$1();
            }
        });
    }

    public void lambda$onDataRecieved$1() {
        int itemCount = this.listAdapter.getItemCount();
        if (TextUtils.isEmpty(this.state.searchQuery) && !this.state.contactsOnly) {
            updateViewsVisibility();
        }
        this.listAdapter.updateRows();
        this.recyclerItemsEnterAnimator.showItemsAnimated(itemCount);
        checkLoadMore();
    }

    public void setListBottomPadding(float f) {
        if (f != this.recyclerListView.getPaddingBottom()) {
            this.recyclerListView.setPadding(0, (int) f, 0, 0);
            this.recyclerListView.requestLayout();
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int childAdapterPosition;
        TLRPC$TL_userStories stories;
        int i3 = 0;
        if (i == NotificationCenter.storiesUpdated) {
            if (this.storyItem.uploadingStory == null || (stories = MessagesController.getInstance(this.currentAccount).storiesController.getStories(UserConfig.getInstance(this.currentAccount).clientUserId)) == null) {
                return;
            }
            while (i3 < stories.stories.size()) {
                TLRPC$StoryItem tLRPC$StoryItem = stories.stories.get(i3);
                String str = tLRPC$StoryItem.attachPath;
                if (str != null && str.equals(this.storyItem.uploadingStory.path)) {
                    SelfStoryViewsView.StoryItemInternal storyItemInternal = this.storyItem;
                    storyItemInternal.uploadingStory = null;
                    storyItemInternal.storyItem = tLRPC$StoryItem;
                    setStoryItem(storyItemInternal);
                    return;
                }
                i3++;
            }
        } else if (i == NotificationCenter.storiesBlocklistUpdate) {
            while (i3 < this.recyclerListView.getChildCount()) {
                View childAt = this.recyclerListView.getChildAt(i3);
                if ((childAt instanceof ReactedUserHolderView) && (childAdapterPosition = this.recyclerListView.getChildAdapterPosition(childAt)) >= 0 && childAdapterPosition < this.listAdapter.items.size()) {
                    ((ReactedUserHolderView) childAt).animateAlpha(isStoryShownToUser(this.listAdapter.items.get(childAdapterPosition).user) ? 1.0f : 0.5f, true);
                }
                i3++;
            }
        }
    }

    public void setShadowDrawable(Drawable drawable) {
        this.shadowDrawable = drawable;
    }

    public void onKeyboardShown() {
        this.recyclerListView.dispatchTouchEvent(AndroidUtilities.emptyMotionEvent());
        if (this.topViewsContainer.getTranslationY() != 0.0f) {
            this.scroller.smoothScrollBy((int) this.topViewsContainer.getTranslationY(), 250L, AdjustPanLayoutHelper.keyboardInterpolator);
        }
    }

    public boolean onBackPressed() {
        if (Math.abs(this.topViewsContainer.getTranslationY() - this.recyclerListView.getPaddingTop()) > AndroidUtilities.dp(2.0f)) {
            this.recyclerListView.dispatchTouchEvent(AndroidUtilities.emptyMotionEvent());
            this.recyclerListView.smoothScrollToPosition(0);
            return true;
        }
        return false;
    }

    public float getTopOffset() {
        return this.topViewsContainer.getTranslationY();
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        ArrayList<Item> items;

        private ListAdapter() {
            this.items = new ArrayList<>();
        }

        public void lambda$onCreateViewHolder$0() {
            SelfStoryViewsPage.this.showPremiumAlert();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FlickerLoadingView flickerLoadingView;
            LinkSpanDrawable.LinksTextView linksTextView;
            switch (i) {
                case 0:
                    linksTextView = new View(SelfStoryViewsPage.this.getContext()) {
                        @Override
                        protected void onMeasure(int i2, int i3) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(SelfStoryViewsPage.this.TOP_PADDING), 1073741824));
                        }
                    };
                    break;
                case 1:
                    int i2 = ReactedUserHolderView.STYLE_STORY;
                    SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
                    linksTextView = new ReactedUserHolderView(i2, selfStoryViewsPage.currentAccount, selfStoryViewsPage.getContext(), SelfStoryViewsPage.this.resourcesProvider) {
                        @Override
                        public void openStory(long j, Runnable runnable) {
                            LaunchActivity.getLastFragment().getOrCreateOverlayStoryViewer().doOnAnimationReady(runnable);
                            LaunchActivity.getLastFragment().getOrCreateOverlayStoryViewer().open(getContext(), j, StoriesListPlaceProvider.of(SelfStoryViewsPage.this.recyclerListView));
                        }
                    };
                    break;
                case 2:
                case 9:
                default:
                    linksTextView = new View(SelfStoryViewsPage.this.getContext()) {
                        @Override
                        protected void onMeasure(int i3, int i4) {
                            int lastItemHeight = SelfStoryViewsPage.this.layoutManager.getLastItemHeight();
                            if (lastItemHeight >= SelfStoryViewsPage.this.recyclerListView.getPaddingTop() && !SelfStoryViewsPage.this.showSearch) {
                                lastItemHeight = 0;
                            }
                            super.onMeasure(i3, View.MeasureSpec.makeMeasureSpec(lastItemHeight, 1073741824));
                        }
                    };
                    break;
                case 3:
                    linksTextView = new FixedHeightEmptyCell(SelfStoryViewsPage.this.getContext(), 70);
                    break;
                case 4:
                    flickerLoadingView = new FlickerLoadingView(SelfStoryViewsPage.this.getContext(), SelfStoryViewsPage.this.resourcesProvider);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(28);
                    flickerLoadingView.showDate(false);
                    linksTextView = flickerLoadingView;
                    break;
                case 5:
                case 7:
                case 8:
                case 10:
                    StickerEmptyView stickerEmptyView = new StickerEmptyView(SelfStoryViewsPage.this.getContext(), null, SelfStoryViewsPage.this.defaultModel.isExpiredViews ? 12 : (i == 10 || i == 7 || i == 8 || i == 5) ? 1 : 0, SelfStoryViewsPage.this.resourcesProvider) {
                        @Override
                        protected void onMeasure(int i3, int i4) {
                            super.onMeasure(i3, View.MeasureSpec.makeMeasureSpec((SelfStoryViewsPage.this.measuerdHeight - SelfStoryViewsPage.this.recyclerListView.getPaddingTop()) - AndroidUtilities.dp(SelfStoryViewsPage.this.TOP_PADDING), 1073741824));
                        }
                    };
                    if (i == 7) {
                        stickerEmptyView.title.setVisibility(8);
                        stickerEmptyView.setSubtitle(LocaleController.getString("NoResult", R.string.NoResult));
                    } else if (i == 8) {
                        stickerEmptyView.title.setVisibility(8);
                        stickerEmptyView.setSubtitle(LocaleController.getString("NoContactsViewed", R.string.NoContactsViewed));
                    } else if (i == 10) {
                        stickerEmptyView.title.setVisibility(0);
                        stickerEmptyView.title.setText(LocaleController.getString("ServerErrorViewersTitle", R.string.ServerErrorViewersTitle));
                        stickerEmptyView.setSubtitle(LocaleController.getString("ServerErrorViewers", R.string.ServerErrorViewers));
                    } else if (SelfStoryViewsPage.this.defaultModel.isExpiredViews) {
                        stickerEmptyView.title.setVisibility(8);
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        spannableStringBuilder.append((CharSequence) AndroidUtilities.replaceTags(LocaleController.getString("ExpiredViewsStub", R.string.ExpiredViewsStub)));
                        spannableStringBuilder.append((CharSequence) "\n\n");
                        spannableStringBuilder.append(AndroidUtilities.replaceSingleTag(LocaleController.getString("ExpiredViewsStubPremiumDescription", R.string.ExpiredViewsStubPremiumDescription), new Runnable() {
                            @Override
                            public final void run() {
                                SelfStoryViewsPage.ListAdapter.this.lambda$onCreateViewHolder$1();
                            }
                        }));
                        stickerEmptyView.subtitle.setText(spannableStringBuilder);
                        stickerEmptyView.createButtonLayout(LocaleController.getString("LearnMore", R.string.LearnMore), new Runnable() {
                            @Override
                            public final void run() {
                                SelfStoryViewsPage.ListAdapter.this.lambda$onCreateViewHolder$2();
                            }
                        });
                    } else {
                        stickerEmptyView.title.setVisibility(0);
                        stickerEmptyView.title.setText(LocaleController.getString("NoViews", R.string.NoViews));
                        stickerEmptyView.setSubtitle(LocaleController.getString("NoViewsStub", R.string.NoViewsStub));
                    }
                    stickerEmptyView.showProgress(false, false);
                    linksTextView = stickerEmptyView;
                    break;
                case 6:
                    flickerLoadingView = new FlickerLoadingView(SelfStoryViewsPage.this.getContext(), SelfStoryViewsPage.this.resourcesProvider);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setIgnoreHeightCheck(true);
                    flickerLoadingView.setItemsCount(20);
                    flickerLoadingView.setViewType(28);
                    flickerLoadingView.showDate(false);
                    linksTextView = flickerLoadingView;
                    break;
                case 11:
                case 12:
                    LinkSpanDrawable.LinksTextView linksTextView2 = new LinkSpanDrawable.LinksTextView(SelfStoryViewsPage.this.getContext());
                    linksTextView2.setTextSize(1, 13.0f);
                    linksTextView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, SelfStoryViewsPage.this.resourcesProvider));
                    linksTextView2.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, SelfStoryViewsPage.this.resourcesProvider));
                    int dp = AndroidUtilities.dp(16.0f);
                    int dp2 = AndroidUtilities.dp(21.0f);
                    linksTextView2.setPadding(dp2, dp, dp2, dp);
                    linksTextView2.setMaxLines(ConnectionsManager.DEFAULT_DATACENTER_ID);
                    linksTextView2.setGravity(17);
                    linksTextView2.setDisablePaddingsOffsetY(true);
                    if (i == 11) {
                        linksTextView2.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString("StoryViewsPremiumHint", R.string.StoryViewsPremiumHint), new Runnable() {
                            @Override
                            public final void run() {
                                SelfStoryViewsPage.ListAdapter.this.lambda$onCreateViewHolder$0();
                            }
                        }));
                    } else {
                        linksTextView2.setText(LocaleController.getString("ServerErrorViewersFull", R.string.ServerErrorViewersFull));
                    }
                    linksTextView2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    linksTextView = linksTextView2;
                    break;
            }
            return new RecyclerListView.Holder(linksTextView);
        }

        public void lambda$onCreateViewHolder$1() {
            SelfStoryViewsPage.this.showPremiumAlert();
        }

        public void lambda$onCreateViewHolder$2() {
            SelfStoryViewsPage.this.showPremiumAlert();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ReactionsLayoutInBubble.VisibleReaction fromTLReaction;
            String str;
            boolean z = true;
            if (viewHolder.getItemViewType() == 1) {
                ReactedUserHolderView reactedUserHolderView = (ReactedUserHolderView) viewHolder.itemView;
                TLRPC$User user = MessagesController.getInstance(SelfStoryViewsPage.this.currentAccount).getUser(Long.valueOf(this.items.get(i).user.user_id));
                boolean remove = SelfStoryViewsPage.this.defaultModel.animateDateForUsers.remove(Long.valueOf(this.items.get(i).user.user_id));
                boolean z2 = (this.items.get(i).user.reaction == null || (fromTLReaction = ReactionsLayoutInBubble.VisibleReaction.fromTLReaction(this.items.get(i).user.reaction)) == null || (str = fromTLReaction.emojicon) == null || !str.equals("❤")) ? false : true;
                reactedUserHolderView.setUserReaction(user, null, z2 ? null : this.items.get(i).user.reaction, z2, this.items.get(i).user.date, true, remove);
                int i2 = i < this.items.size() - 1 ? this.items.get(i + 1).viewType : -1;
                if (i2 != 1 && i2 != 11 && i2 != 12) {
                    z = false;
                }
                reactedUserHolderView.drawDivider = z;
                reactedUserHolderView.animateAlpha(SelfStoryViewsPage.this.isStoryShownToUser(this.items.get(i).user) ? 1.0f : 0.5f, false);
            }
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }

        public void updateRows() {
            this.items.clear();
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            ViewsModel viewsModel = selfStoryViewsPage.currentModel;
            if (selfStoryViewsPage.isSearchDebounce) {
                this.items.add(new Item(0));
                this.items.add(new Item(6));
            } else {
                this.items.add(new Item(0));
                if (viewsModel != null && viewsModel.views.isEmpty() && (viewsModel.isExpiredViews || (!viewsModel.loading && !viewsModel.hasNext))) {
                    if (!TextUtils.isEmpty(viewsModel.state.searchQuery)) {
                        this.items.add(new Item(7));
                    } else if (viewsModel.isExpiredViews) {
                        this.items.add(new Item(5));
                    } else {
                        int i = viewsModel.totalCount;
                        if (i > 0 && viewsModel.state.contactsOnly) {
                            this.items.add(new Item(8));
                        } else if (i > 0) {
                            this.items.add(new Item(10));
                        } else {
                            this.items.add(new Item(5));
                        }
                    }
                } else {
                    if (viewsModel != null) {
                        for (int i2 = 0; i2 < viewsModel.views.size(); i2++) {
                            this.items.add(new Item(1, viewsModel.views.get(i2)));
                        }
                    }
                    if (viewsModel != null && (viewsModel.loading || viewsModel.hasNext)) {
                        if (viewsModel.views.isEmpty()) {
                            this.items.add(new Item(6));
                        } else {
                            this.items.add(new Item(4));
                        }
                    } else if (viewsModel != null && viewsModel.showReactionOnly) {
                        this.items.add(new Item(11));
                    } else if (viewsModel != null && viewsModel.views.size() < viewsModel.totalCount && TextUtils.isEmpty(viewsModel.state.searchQuery) && !viewsModel.state.contactsOnly) {
                        this.items.add(new Item(12));
                    }
                }
            }
            this.items.add(new Item(9));
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int i) {
            return this.items.get(i).viewType;
        }
    }

    public void showPremiumAlert() {
        new PremiumFeatureBottomSheet(this.storyViewer.fragment, 14, false).show();
    }

    public class Item {
        TLRPC$TL_storyView user;
        final int viewType;

        private Item(SelfStoryViewsPage selfStoryViewsPage, int i) {
            this.viewType = i;
        }

        private Item(SelfStoryViewsPage selfStoryViewsPage, int i, TLRPC$TL_storyView tLRPC$TL_storyView) {
            this.viewType = i;
            this.user = tLRPC$TL_storyView;
        }
    }

    public static class ViewsModel {
        int currentAccount;
        boolean initial;
        boolean isExpiredViews;
        boolean loading;
        String offset;
        boolean showReactionOnly;
        TLRPC$StoryItem storyItem;
        public int totalCount;
        boolean useLocalFilters;
        ArrayList<TLRPC$TL_storyView> views = new ArrayList<>();
        ArrayList<TLRPC$TL_storyView> originalViews = new ArrayList<>();
        boolean hasNext = true;
        int reqId = -1;
        HashSet<Long> animateDateForUsers = new HashSet<>();
        ArrayList<SelfStoryViewsPage> listeners = new ArrayList<>();
        FiltersState state = new FiltersState();

        public ViewsModel(int i, TLRPC$StoryItem tLRPC$StoryItem, boolean z) {
            TLRPC$StoryViews tLRPC$StoryViews;
            this.currentAccount = i;
            this.storyItem = tLRPC$StoryItem;
            TLRPC$StoryViews tLRPC$StoryViews2 = tLRPC$StoryItem.views;
            int i2 = tLRPC$StoryViews2 == null ? 0 : tLRPC$StoryViews2.views_count;
            this.totalCount = i2;
            if (i2 < 200) {
                this.useLocalFilters = true;
            }
            boolean z2 = StoriesUtilities.hasExpiredViews(tLRPC$StoryItem) && !UserConfig.getInstance(i).isPremium();
            this.isExpiredViews = z2;
            if (z2 && (tLRPC$StoryViews = tLRPC$StoryItem.views) != null && tLRPC$StoryViews.reactions_count > 0) {
                this.isExpiredViews = false;
                this.showReactionOnly = true;
            }
            if (this.isExpiredViews) {
                return;
            }
            this.initial = true;
            if (tLRPC$StoryItem.views == null || !z) {
                return;
            }
            for (int i3 = 0; i3 < tLRPC$StoryItem.views.recent_viewers.size(); i3++) {
                TLRPC$TL_storyView tLRPC$TL_storyView = new TLRPC$TL_storyView();
                tLRPC$TL_storyView.user_id = tLRPC$StoryItem.views.recent_viewers.get(i3).longValue();
                tLRPC$TL_storyView.date = 0;
                this.views.add(tLRPC$TL_storyView);
            }
        }

        public void loadNext() {
            if (this.loading || !this.hasNext || this.isExpiredViews) {
                return;
            }
            TLRPC$TL_stories_getStoryViewsList tLRPC$TL_stories_getStoryViewsList = new TLRPC$TL_stories_getStoryViewsList();
            tLRPC$TL_stories_getStoryViewsList.id = this.storyItem.id;
            if (this.useLocalFilters) {
                tLRPC$TL_stories_getStoryViewsList.q = "";
                tLRPC$TL_stories_getStoryViewsList.just_contacts = false;
                tLRPC$TL_stories_getStoryViewsList.reactions_first = true;
            } else {
                String str = this.state.searchQuery;
                tLRPC$TL_stories_getStoryViewsList.q = str;
                if (!TextUtils.isEmpty(str)) {
                    tLRPC$TL_stories_getStoryViewsList.flags |= 2;
                }
                FiltersState filtersState = this.state;
                tLRPC$TL_stories_getStoryViewsList.just_contacts = filtersState.contactsOnly;
                tLRPC$TL_stories_getStoryViewsList.reactions_first = filtersState.sortByReactions;
            }
            int i = 20;
            if (!this.initial && this.views.size() >= 20) {
                i = 100;
            }
            tLRPC$TL_stories_getStoryViewsList.limit = i;
            String str2 = this.offset;
            tLRPC$TL_stories_getStoryViewsList.offset = str2;
            if (str2 == null) {
                tLRPC$TL_stories_getStoryViewsList.offset = "";
            }
            this.loading = true;
            int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_stories_getStoryViewsList, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SelfStoryViewsPage.ViewsModel.this.lambda$loadNext$1(r2, tLObject, tLRPC$TL_error);
                }
            });
            this.reqId = sendRequest;
            final int[] iArr = {sendRequest};
        }

        public void lambda$loadNext$1(final int[] iArr, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SelfStoryViewsPage.ViewsModel.this.lambda$loadNext$0(iArr, tLObject);
                }
            });
        }

        public void lambda$loadNext$0(int[] iArr, TLObject tLObject) {
            if (iArr[0] != this.reqId) {
                return;
            }
            this.loading = false;
            this.reqId = -1;
            if (tLObject != null) {
                TLRPC$TL_stories_storyViewsList tLRPC$TL_stories_storyViewsList = (TLRPC$TL_stories_storyViewsList) tLObject;
                MessagesController.getInstance(this.currentAccount).getStoriesController().applyStoryViewsBlocked(tLRPC$TL_stories_storyViewsList);
                MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_stories_storyViewsList.users, false);
                if (this.initial) {
                    this.initial = false;
                    for (int i = 0; i < this.views.size(); i++) {
                        this.animateDateForUsers.add(Long.valueOf(this.views.get(i).user_id));
                    }
                    this.views.clear();
                    this.originalViews.clear();
                }
                if (this.useLocalFilters) {
                    this.originalViews.addAll(tLRPC$TL_stories_storyViewsList.views);
                    applyLocalFilter();
                } else {
                    this.views.addAll(tLRPC$TL_stories_storyViewsList.views);
                }
                if (!tLRPC$TL_stories_storyViewsList.views.isEmpty()) {
                    ArrayList<TLRPC$TL_storyView> arrayList = tLRPC$TL_stories_storyViewsList.views;
                    arrayList.get(arrayList.size() - 1);
                    this.hasNext = this.views.size() < tLRPC$TL_stories_storyViewsList.count;
                } else {
                    this.hasNext = false;
                }
                String str = tLRPC$TL_stories_storyViewsList.next_offset;
                this.offset = str;
                if (TextUtils.isEmpty(str)) {
                    this.hasNext = false;
                }
                TLRPC$StoryItem tLRPC$StoryItem = this.storyItem;
                if (tLRPC$StoryItem.views == null) {
                    tLRPC$StoryItem.views = new TLRPC$TL_storyViews();
                }
                int i2 = tLRPC$TL_stories_storyViewsList.count;
                TLRPC$StoryViews tLRPC$StoryViews = this.storyItem.views;
                if (i2 > tLRPC$StoryViews.views_count) {
                    tLRPC$StoryViews.recent_viewers.clear();
                    for (int i3 = 0; i3 < Math.min(3, tLRPC$TL_stories_storyViewsList.users.size()); i3++) {
                        this.storyItem.views.recent_viewers.add(Long.valueOf(tLRPC$TL_stories_storyViewsList.users.get(i3).id));
                    }
                    this.storyItem.views.views_count = tLRPC$TL_stories_storyViewsList.count;
                }
            } else {
                this.hasNext = false;
            }
            for (int i4 = 0; i4 < this.listeners.size(); i4++) {
                this.listeners.get(i4).onDataRecieved(this);
            }
            if (this.views.size() >= 20 || !this.hasNext) {
                return;
            }
            loadNext();
        }

        private void applyLocalFilter() {
            String str;
            this.views.clear();
            FiltersState filtersState = this.state;
            if (filtersState.contactsOnly || !TextUtils.isEmpty(filtersState.searchQuery)) {
                String str2 = null;
                if (TextUtils.isEmpty(this.state.searchQuery)) {
                    str = null;
                } else {
                    str2 = this.state.searchQuery.trim().toLowerCase();
                    str = LocaleController.getInstance().getTranslitString(str2);
                }
                for (int i = 0; i < this.originalViews.size(); i++) {
                    TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.originalViews.get(i).user_id));
                    boolean z = !this.state.contactsOnly || (user != null && user.contact);
                    if (z && str2 != null) {
                        String lowerCase = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                        String publicUsername = UserObject.getPublicUsername(user);
                        if (!lowerCase.contains(str2) && !lowerCase.contains(str) && (publicUsername == null || (!publicUsername.contains(str2) && !publicUsername.contains(str)))) {
                            z = false;
                        }
                    }
                    if (z) {
                        this.views.add(this.originalViews.get(i));
                    }
                }
            } else {
                this.views.addAll(this.originalViews);
            }
            if (this.state.sortByReactions) {
                return;
            }
            Collections.sort(this.views, Comparator$CC.comparingInt(new ToIntFunction() {
                @Override
                public final int applyAsInt(Object obj) {
                    int lambda$applyLocalFilter$2;
                    lambda$applyLocalFilter$2 = SelfStoryViewsPage.ViewsModel.lambda$applyLocalFilter$2((TLRPC$TL_storyView) obj);
                    return lambda$applyLocalFilter$2;
                }
            }));
        }

        public static int lambda$applyLocalFilter$2(TLRPC$TL_storyView tLRPC$TL_storyView) {
            return -tLRPC$TL_storyView.date;
        }

        public void addListener(SelfStoryViewsPage selfStoryViewsPage) {
            if (this.listeners.contains(selfStoryViewsPage)) {
                return;
            }
            this.listeners.add(selfStoryViewsPage);
        }

        public void removeListener(SelfStoryViewsPage selfStoryViewsPage) {
            this.listeners.remove(selfStoryViewsPage);
        }

        public void release() {
            if (this.reqId >= 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, false);
            }
            this.reqId = -1;
        }

        public void reloadIfNeed(FiltersState filtersState, boolean z, boolean z2) {
            FiltersState filtersState2 = new FiltersState();
            filtersState2.set(filtersState);
            if (!z) {
                filtersState2.contactsOnly = false;
            }
            if (!z2) {
                filtersState2.sortByReactions = true;
            }
            if (this.state.equals(filtersState2)) {
                return;
            }
            this.state.set(filtersState2);
            if (this.useLocalFilters) {
                applyLocalFilter();
                for (int i = 0; i < this.listeners.size(); i++) {
                    this.listeners.get(i).onDataRecieved(this);
                }
                return;
            }
            release();
            this.views.clear();
            this.initial = true;
            this.loading = false;
            this.hasNext = true;
            this.offset = "";
            loadNext();
        }
    }

    public class HeaderView extends FrameLayout {
        TextView allViewersView;
        float animateFromAlpha1;
        float animateFromAlpha2;
        RectF animateFromRect;
        float animationProgress;
        ValueAnimator animator;
        private final LinearLayout buttonContainer;
        TextView contactsViewersView;
        boolean lastSortType;
        RectF rectF;
        ReplaceableIconDrawable replacableDrawable;
        int selected;
        Paint selectedPaint;

        public HeaderView(Context context) {
            super(context);
            this.selectedPaint = new Paint(1);
            this.animateFromRect = new RectF();
            this.rectF = new RectF();
            this.animationProgress = 1.0f;
            Paint paint = this.selectedPaint;
            int i = Theme.key_listSelector;
            paint.setColor(Theme.getColor(i, SelfStoryViewsPage.this.resourcesProvider));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            TextView textView = new TextView(context);
            this.allViewersView = textView;
            textView.setText(LocaleController.getString("AllViewers", R.string.AllViewers));
            TextView textView2 = this.allViewersView;
            int i2 = Theme.key_dialogTextBlack;
            textView2.setTextColor(Theme.getColor(i2, SelfStoryViewsPage.this.resourcesProvider));
            this.allViewersView.setTextSize(1, 14.0f);
            this.allViewersView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.allViewersView.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f));
            TextView textView3 = new TextView(context);
            this.contactsViewersView = textView3;
            textView3.setText(LocaleController.getString("Contacts", R.string.Contacts));
            this.contactsViewersView.setTextColor(Theme.getColor(i2, SelfStoryViewsPage.this.resourcesProvider));
            this.contactsViewersView.setTextSize(1, 14.0f);
            this.contactsViewersView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.contactsViewersView.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f));
            linearLayout.setPadding(0, AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f));
            linearLayout.addView(this.allViewersView, LayoutHelper.createLinear(-2, -2, 0, 13, 0, 0, 0));
            linearLayout.addView(this.contactsViewersView, LayoutHelper.createLinear(-2, -2, 0, 0, 0, 0, 0));
            LinearLayout linearLayout2 = new LinearLayout(getContext());
            this.buttonContainer = linearLayout2;
            linearLayout2.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
            linearLayout2.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(26.0f), Theme.getColor(i, SelfStoryViewsPage.this.resourcesProvider)));
            linearLayout2.setOrientation(0);
            ReplaceableIconDrawable replaceableIconDrawable = new ReplaceableIconDrawable(getContext());
            this.replacableDrawable = replaceableIconDrawable;
            replaceableIconDrawable.exactlyBounds = true;
            this.lastSortType = true;
            replaceableIconDrawable.setIcon(R.drawable.menu_views_reactions3, false);
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageDrawable(this.replacableDrawable);
            linearLayout2.addView(imageView, LayoutHelper.createLinear(26, 26));
            ImageView imageView2 = new ImageView(getContext());
            imageView2.setImageResource(R.drawable.arrow_more);
            linearLayout2.addView(imageView2, LayoutHelper.createLinear(16, 26));
            addView(linearLayout, LayoutHelper.createFrame(-2, -2.0f));
            addView(linearLayout2, LayoutHelper.createFrame(-2, -2.0f, 5, 13.0f, 6.0f, 13.0f, 6.0f));
            this.allViewersView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SelfStoryViewsPage.HeaderView.this.lambda$new$0(view);
                }
            });
            this.contactsViewersView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SelfStoryViewsPage.HeaderView.this.lambda$new$1(view);
                }
            });
            linearLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SelfStoryViewsPage.HeaderView.this.lambda$new$2(view);
                }
            });
        }

        public void lambda$new$0(View view) {
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            FiltersState filtersState = selfStoryViewsPage.state;
            if (filtersState.contactsOnly) {
                filtersState.contactsOnly = false;
                selfStoryViewsPage.updateViewState(true);
                SelfStoryViewsPage.this.reload();
            }
        }

        public void lambda$new$1(View view) {
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            FiltersState filtersState = selfStoryViewsPage.state;
            if (filtersState.contactsOnly) {
                return;
            }
            filtersState.contactsOnly = true;
            selfStoryViewsPage.updateViewState(true);
            SelfStoryViewsPage.this.reload();
        }

        public class AnonymousClass1 extends CustomPopupMenu {
            @Override
            protected void onDismissed() {
            }

            AnonymousClass1(Context context, Theme.ResourcesProvider resourcesProvider, boolean z) {
                super(context, resourcesProvider, z);
            }

            @Override
            protected void onCreate(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
                actionBarPopupWindowLayout.setBackgroundColor(ColorUtils.blendARGB(-16777216, -1, 0.18f));
                ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(actionBarPopupWindowLayout, SelfStoryViewsPage.this.state.sortByReactions ? R.drawable.menu_views_reactions2 : R.drawable.menu_views_reactions, LocaleController.getString("SortByReactions", R.string.SortByReactions), false, SelfStoryViewsPage.this.resourcesProvider);
                if (!SelfStoryViewsPage.this.state.sortByReactions) {
                    addItem.setAlpha(0.5f);
                }
                addItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        SelfStoryViewsPage.HeaderView.AnonymousClass1.this.lambda$onCreate$0(view);
                    }
                });
                ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(actionBarPopupWindowLayout, !SelfStoryViewsPage.this.state.sortByReactions ? R.drawable.menu_views_recent2 : R.drawable.menu_views_recent, LocaleController.getString("SortByTime", R.string.SortByTime), false, SelfStoryViewsPage.this.resourcesProvider);
                if (SelfStoryViewsPage.this.state.sortByReactions) {
                    addItem2.setAlpha(0.5f);
                }
                addItem2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        SelfStoryViewsPage.HeaderView.AnonymousClass1.this.lambda$onCreate$1(view);
                    }
                });
                ActionBarPopupWindow.GapView gapView = new ActionBarPopupWindow.GapView(HeaderView.this.getContext(), SelfStoryViewsPage.this.resourcesProvider, Theme.key_actionBarDefaultSubmenuSeparator);
                gapView.setTag(R.id.fit_width_tag, 1);
                actionBarPopupWindowLayout.addView((View) gapView, LayoutHelper.createLinear(-1, 8));
                ActionBarMenuItem.addText(actionBarPopupWindowLayout, LocaleController.getString("StoryViewsSortDescription", R.string.StoryViewsSortDescription), SelfStoryViewsPage.this.resourcesProvider);
            }

            public void lambda$onCreate$0(View view) {
                SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
                FiltersState filtersState = selfStoryViewsPage.state;
                if (!filtersState.sortByReactions) {
                    FiltersState filtersState2 = selfStoryViewsPage.sharedFilterState;
                    if (filtersState2 != null) {
                        filtersState.sortByReactions = true;
                        filtersState2.sortByReactions = true;
                    } else {
                        filtersState.sortByReactions = true;
                    }
                    selfStoryViewsPage.updateViewState(true);
                    SelfStoryViewsPage.this.reload();
                    SelfStoryViewsPage selfStoryViewsPage2 = SelfStoryViewsPage.this;
                    selfStoryViewsPage2.onSharedStateChanged.accept(selfStoryViewsPage2);
                }
                if (SelfStoryViewsPage.this.popupMenu != null) {
                    SelfStoryViewsPage.this.popupMenu.dismiss();
                }
            }

            public void lambda$onCreate$1(View view) {
                SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
                FiltersState filtersState = selfStoryViewsPage.state;
                if (filtersState.sortByReactions) {
                    FiltersState filtersState2 = selfStoryViewsPage.sharedFilterState;
                    if (filtersState2 != null) {
                        filtersState.sortByReactions = false;
                        filtersState2.sortByReactions = false;
                    } else {
                        filtersState.sortByReactions = false;
                    }
                    selfStoryViewsPage.updateViewState(true);
                    SelfStoryViewsPage.this.reload();
                    SelfStoryViewsPage selfStoryViewsPage2 = SelfStoryViewsPage.this;
                    selfStoryViewsPage2.onSharedStateChanged.accept(selfStoryViewsPage2);
                }
                if (SelfStoryViewsPage.this.popupMenu != null) {
                    SelfStoryViewsPage.this.popupMenu.dismiss();
                }
            }
        }

        public void lambda$new$2(View view) {
            SelfStoryViewsPage.this.popupMenu = new AnonymousClass1(getContext(), SelfStoryViewsPage.this.resourcesProvider, false);
            CustomPopupMenu customPopupMenu = SelfStoryViewsPage.this.popupMenu;
            LinearLayout linearLayout = this.buttonContainer;
            customPopupMenu.show(linearLayout, 0, (-linearLayout.getMeasuredHeight()) - AndroidUtilities.dp(8.0f));
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            float f;
            if (SelfStoryViewsPage.this.showContactsFilter) {
                float f2 = 0.5f;
                if (this.selected == 0) {
                    this.allViewersView.getHitRect(AndroidUtilities.rectTmp2);
                    f = 0.5f;
                    f2 = 1.0f;
                } else {
                    this.contactsViewersView.getHitRect(AndroidUtilities.rectTmp2);
                    f = 1.0f;
                }
                this.rectF.set(AndroidUtilities.rectTmp2);
                float f3 = this.animationProgress;
                if (f3 != 1.0f) {
                    f2 = AndroidUtilities.lerp(this.animateFromAlpha1, f2, f3);
                    f = AndroidUtilities.lerp(this.animateFromAlpha2, f, this.animationProgress);
                    RectF rectF = this.animateFromRect;
                    RectF rectF2 = this.rectF;
                    AndroidUtilities.lerp(rectF, rectF2, this.animationProgress, rectF2);
                }
                this.allViewersView.setAlpha(f2);
                this.contactsViewersView.setAlpha(f);
                float height = this.rectF.height() / 2.0f;
                canvas.drawRoundRect(this.rectF, height, height, this.selectedPaint);
            }
            super.dispatchDraw(canvas);
        }

        public void setState(boolean z, boolean z2) {
            if (z == this.selected && z2) {
                return;
            }
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.animator.cancel();
            }
            this.selected = z ? 1 : 0;
            if (!z2) {
                this.animationProgress = 1.0f;
                invalidate();
                return;
            }
            this.animateFromRect.set(this.rectF);
            this.animateFromAlpha1 = this.allViewersView.getAlpha();
            this.animateFromAlpha2 = this.contactsViewersView.getAlpha();
            this.animationProgress = 0.0f;
            invalidate();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    SelfStoryViewsPage.HeaderView.this.lambda$setState$3(valueAnimator2);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    HeaderView headerView = HeaderView.this;
                    headerView.animator = null;
                    headerView.animationProgress = 1.0f;
                    headerView.invalidate();
                }
            });
            this.animator.setDuration(250L);
            this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animator.start();
        }

        public void lambda$setState$3(ValueAnimator valueAnimator) {
            this.animationProgress = ((Float) this.animator.getAnimatedValue()).floatValue();
            invalidate();
        }
    }

    public void reload() {
        ViewsModel viewsModel = this.currentModel;
        if (viewsModel != null) {
            viewsModel.removeListener(this);
        }
        ViewsModel viewsModel2 = this.defaultModel;
        this.currentModel = viewsModel2;
        if (viewsModel2 == null) {
            return;
        }
        viewsModel2.reloadIfNeed(this.state, this.showContactsFilter, this.showReactionsSort);
        this.currentModel.addListener(this);
        this.listAdapter.updateRows();
        this.layoutManager.scrollToPositionWithOffset(0, (int) (getTopOffset() - this.recyclerListView.getPaddingTop()));
    }

    public void updateViewState(boolean z) {
        this.headerView.setState(this.state.contactsOnly, z);
        HeaderView headerView = this.headerView;
        boolean z2 = headerView.lastSortType;
        boolean z3 = this.state.sortByReactions;
        if (z2 != z3) {
            headerView.lastSortType = z3;
            headerView.replacableDrawable.setIcon(z3 ? R.drawable.menu_views_reactions3 : R.drawable.menu_views_recent3, z);
        }
    }

    public static class FiltersState {
        boolean contactsOnly;
        String searchQuery;
        boolean sortByReactions = true;

        public void set(FiltersState filtersState) {
            this.sortByReactions = filtersState.sortByReactions;
            this.contactsOnly = filtersState.contactsOnly;
            this.searchQuery = filtersState.searchQuery;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || FiltersState.class != obj.getClass()) {
                return false;
            }
            FiltersState filtersState = (FiltersState) obj;
            return this.sortByReactions == filtersState.sortByReactions && this.contactsOnly == filtersState.contactsOnly && ((TextUtils.isEmpty(this.searchQuery) && TextUtils.isEmpty(filtersState.searchQuery)) || Objects.equals(this.searchQuery, filtersState.searchQuery));
        }

        public int hashCode() {
            return Objects.hash(Boolean.valueOf(this.sortByReactions), Boolean.valueOf(this.contactsOnly), this.searchQuery);
        }
    }

    private class RecyclerListViewInner extends RecyclerListView implements StoriesListPlaceProvider.ClippedView {
        public RecyclerListViewInner(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        @Override
        public void updateClip(int[] iArr) {
            iArr[0] = AndroidUtilities.dp(SelfStoryViewsPage.this.TOP_PADDING);
            iArr[1] = getMeasuredHeight();
        }
    }
}
