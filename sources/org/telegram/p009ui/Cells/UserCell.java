package org.telegram.p009ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0952R;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.p009ui.ActionBar.SimpleTextView;
import org.telegram.p009ui.ActionBar.Theme;
import org.telegram.p009ui.Components.AvatarDrawable;
import org.telegram.p009ui.Components.BackupImageView;
import org.telegram.p009ui.Components.CheckBox;
import org.telegram.p009ui.Components.CheckBoxSquare;
import org.telegram.p009ui.Components.LayoutHelper;
import org.telegram.p009ui.NotificationsSettingsActivity;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$User;

public class UserCell extends FrameLayout {
    private TextView addButton;
    private TextView adminTextView;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private CheckBox checkBox;
    private CheckBoxSquare checkBoxBig;
    private int currentAccount;
    private int currentDrawable;
    private int currentId;
    private CharSequence currentName;
    private Object currentObject;
    private CharSequence currentStatus;
    private ImageView imageView;
    private TLRPC$FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private boolean selfAsSavedMessages;
    private int statusColor;
    private int statusOnlineColor;
    private SimpleTextView statusTextView;

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    public UserCell(Context context, int i, int i2, boolean z) {
        this(context, i, i2, z, false);
    }

    public UserCell(Context context, int i, int i2, boolean z, boolean z2) {
        super(context);
        int i3;
        int i4;
        float f;
        this.currentAccount = UserConfig.selectedAccount;
        if (z2) {
            TextView textView = new TextView(context);
            this.addButton = textView;
            textView.setGravity(17);
            this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.addButton.setTextSize(1, 14.0f);
            this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.addButton.setBackgroundDrawable(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 4.0f));
            this.addButton.setText(LocaleController.getString("Add", C0952R.string.Add));
            this.addButton.setPadding(AndroidUtilities.m34dp(17.0f), 0, AndroidUtilities.m34dp(17.0f), 0);
            View view = this.addButton;
            boolean z3 = LocaleController.isRTL;
            addView(view, LayoutHelper.createFrame(-2, 28.0f, (z3 ? 3 : 5) | 48, z3 ? 14.0f : 0.0f, 15.0f, z3 ? 0.0f : 14.0f, 0.0f));
            i3 = (int) Math.ceil((this.addButton.getPaint().measureText(this.addButton.getText().toString()) + AndroidUtilities.m34dp(48.0f)) / AndroidUtilities.density);
        } else {
            i3 = 0;
        }
        this.statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
        this.statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.m34dp(24.0f));
        View view2 = this.avatarImageView;
        boolean z4 = LocaleController.isRTL;
        addView(view2, LayoutHelper.createFrame(46, 46.0f, (z4 ? 5 : 3) | 48, z4 ? 0.0f : i + 7, 6.0f, z4 ? i + 7 : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view3 = this.nameTextView;
        boolean z5 = LocaleController.isRTL;
        int i5 = (z5 ? 5 : 3) | 48;
        int i6 = 18;
        if (z5) {
            i4 = (i2 == 2 ? 18 : 0) + 28 + i3;
        } else {
            i4 = i + 64;
        }
        float f2 = i4;
        if (z5) {
            f = i + 64;
        } else {
            f = (i2 != 2 ? 0 : i6) + 28 + i3;
        }
        addView(view3, LayoutHelper.createFrame(-1, 20.0f, i5, f2, 10.0f, f, 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(15);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view4 = this.statusTextView;
        boolean z6 = LocaleController.isRTL;
        addView(view4, LayoutHelper.createFrame(-1, 20.0f, (z6 ? 5 : 3) | 48, z6 ? i3 + 28 : i + 64, 32.0f, z6 ? i + 64 : i3 + 28, 0.0f));
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        View view5 = this.imageView;
        boolean z7 = LocaleController.isRTL;
        addView(view5, LayoutHelper.createFrame(-2, -2.0f, (z7 ? 5 : 3) | 16, z7 ? 0.0f : 16.0f, 0.0f, z7 ? 16.0f : 0.0f, 0.0f));
        if (i2 == 2) {
            CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context, false);
            this.checkBoxBig = checkBoxSquare;
            boolean z8 = LocaleController.isRTL;
            addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, (z8 ? 3 : 5) | 16, z8 ? 19.0f : 0.0f, 0.0f, z8 ? 0.0f : 19.0f, 0.0f));
        } else if (i2 == 1) {
            CheckBox checkBox = new CheckBox(context, C0952R.C0953drawable.round_check2);
            this.checkBox = checkBox;
            checkBox.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            View view6 = this.checkBox;
            boolean z9 = LocaleController.isRTL;
            addView(view6, LayoutHelper.createFrame(22, 22.0f, (z9 ? 5 : 3) | 48, z9 ? 0.0f : i + 37, 40.0f, z9 ? i + 37 : 0.0f, 0.0f));
        }
        if (z) {
            TextView textView2 = new TextView(context);
            this.adminTextView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.adminTextView.setTextColor(Theme.getColor("profile_creatorIcon"));
            View view7 = this.adminTextView;
            boolean z10 = LocaleController.isRTL;
            addView(view7, LayoutHelper.createFrame(-2, -2.0f, (z10 ? 3 : 5) | 48, z10 ? 23.0f : 0.0f, 10.0f, z10 ? 0.0f : 23.0f, 0.0f));
        }
        setFocusable(true);
    }

    public void setAvatarPadding(int i) {
        int i2;
        float f;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.avatarImageView.getLayoutParams();
        float f2 = 0.0f;
        layoutParams.leftMargin = AndroidUtilities.m34dp(LocaleController.isRTL ? 0.0f : i + 7);
        layoutParams.rightMargin = AndroidUtilities.m34dp(LocaleController.isRTL ? i + 7 : 0.0f);
        this.avatarImageView.setLayoutParams(layoutParams);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        int i3 = 18;
        if (LocaleController.isRTL) {
            i2 = (this.checkBoxBig != null ? 18 : 0) + 28;
        } else {
            i2 = i + 64;
        }
        layoutParams2.leftMargin = AndroidUtilities.m34dp(i2);
        if (LocaleController.isRTL) {
            f = i + 64;
        } else {
            if (this.checkBoxBig == null) {
                i3 = 0;
            }
            f = i3 + 28;
        }
        layoutParams2.rightMargin = AndroidUtilities.m34dp(f);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.statusTextView.getLayoutParams();
        float f3 = 28.0f;
        layoutParams3.leftMargin = AndroidUtilities.m34dp(LocaleController.isRTL ? 28.0f : i + 64);
        if (LocaleController.isRTL) {
            f3 = i + 64;
        }
        layoutParams3.rightMargin = AndroidUtilities.m34dp(f3);
        CheckBox checkBox = this.checkBox;
        if (checkBox != null) {
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) checkBox.getLayoutParams();
            layoutParams4.leftMargin = AndroidUtilities.m34dp(LocaleController.isRTL ? 0.0f : i + 37);
            if (LocaleController.isRTL) {
                f2 = i + 37;
            }
            layoutParams4.rightMargin = AndroidUtilities.m34dp(f2);
        }
    }

    public void setAddButtonVisible(boolean z) {
        TextView textView = this.addButton;
        if (textView != null) {
            textView.setVisibility(z ? 0 : 8);
        }
    }

    public void setAdminRole(String str) {
        TextView textView = this.adminTextView;
        if (textView != null) {
            textView.setVisibility(str != null ? 0 : 8);
            this.adminTextView.setText(str);
            if (str != null) {
                CharSequence text = this.adminTextView.getText();
                int ceil = (int) Math.ceil(this.adminTextView.getPaint().measureText(text, 0, text.length()));
                this.nameTextView.setPadding(LocaleController.isRTL ? AndroidUtilities.m34dp(6.0f) + ceil : 0, 0, !LocaleController.isRTL ? ceil + AndroidUtilities.m34dp(6.0f) : 0, 0);
                return;
            }
            this.nameTextView.setPadding(0, 0, 0, 0);
        }
    }

    public CharSequence getName() {
        return this.nameTextView.getText();
    }

    public void setData(Object obj, CharSequence charSequence, CharSequence charSequence2, int i) {
        setData(obj, null, charSequence, charSequence2, i, false);
    }

    public void setData(Object obj, CharSequence charSequence, CharSequence charSequence2, int i, boolean z) {
        setData(obj, null, charSequence, charSequence2, i, z);
    }

    public void setData(Object obj, TLRPC$EncryptedChat tLRPC$EncryptedChat, CharSequence charSequence, CharSequence charSequence2, int i, boolean z) {
        if (obj == null && charSequence == null && charSequence2 == null) {
            this.currentStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.currentStatus = charSequence2;
        this.currentName = charSequence;
        this.currentObject = obj;
        this.currentDrawable = i;
        this.needDivider = z;
        setWillNotDraw(!z);
        update(0);
    }

    public Object getCurrentObject() {
        return this.currentObject;
    }

    public void setException(NotificationsSettingsActivity.NotificationException notificationException, CharSequence charSequence, boolean z) {
        String str;
        TLRPC$User user;
        boolean z2 = notificationException.hasCustom;
        int i = notificationException.notify;
        int i2 = notificationException.muteUntil;
        boolean z3 = false;
        if (i != 3 || i2 == Integer.MAX_VALUE) {
            if (i == 0 || i == 1) {
                z3 = true;
            }
            if (!z3 || !z2) {
                str = z3 ? LocaleController.getString("NotificationsUnmuted", C0952R.string.NotificationsUnmuted) : LocaleController.getString("NotificationsMuted", C0952R.string.NotificationsMuted);
            } else {
                str = LocaleController.getString("NotificationsCustom", C0952R.string.NotificationsCustom);
            }
        } else {
            int currentTime = i2 - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (currentTime <= 0) {
                if (z2) {
                    str = LocaleController.getString("NotificationsCustom", C0952R.string.NotificationsCustom);
                } else {
                    str = LocaleController.getString("NotificationsUnmuted", C0952R.string.NotificationsUnmuted);
                }
            } else if (currentTime < 3600) {
                str = LocaleController.formatString("WillUnmuteIn", C0952R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", currentTime / 60));
            } else if (currentTime < 86400) {
                str = LocaleController.formatString("WillUnmuteIn", C0952R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil((currentTime / 60.0f) / 60.0f)));
            } else {
                str = currentTime < 31536000 ? LocaleController.formatString("WillUnmuteIn", C0952R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil(((currentTime / 60.0f) / 60.0f) / 24.0f))) : null;
            }
        }
        if (str == null) {
            str = LocaleController.getString("NotificationsOff", C0952R.string.NotificationsOff);
        }
        String str2 = str;
        if (DialogObject.isEncryptedDialog(notificationException.did)) {
            TLRPC$EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(notificationException.did)));
            if (encryptedChat != null && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(encryptedChat.user_id))) != null) {
                setData(user, encryptedChat, charSequence, str2, 0, false);
            }
        } else if (DialogObject.isUserDialog(notificationException.did)) {
            TLRPC$User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(notificationException.did));
            if (user2 != null) {
                setData(user2, null, charSequence, str2, 0, z);
            }
        } else {
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-notificationException.did));
            if (chat != null) {
                setData(chat, null, charSequence, str2, 0, z);
            }
        }
    }

    public void setNameTypeface(Typeface typeface) {
        this.nameTextView.setTypeface(typeface);
    }

    public void setCurrentId(int i) {
        this.currentId = i;
    }

    public void setChecked(boolean z, boolean z2) {
        CheckBox checkBox = this.checkBox;
        if (checkBox != null) {
            if (checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(z, z2);
            return;
        }
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            if (checkBoxSquare.getVisibility() != 0) {
                this.checkBoxBig.setVisibility(0);
            }
            this.checkBoxBig.setChecked(z, z2);
        }
    }

    public void setCheckDisabled(boolean z) {
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.setDisabled(z);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.m34dp(58.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    @Override
    public void invalidate() {
        super.invalidate();
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.invalidate();
        }
    }

    public void update(int r17) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Cells.UserCell.update(int):void");
    }

    public void setSelfAsSavedMessages(boolean z) {
        this.selfAsSavedMessages = z;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.m34dp(68.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.m34dp(68.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare == null || checkBoxSquare.getVisibility() != 0) {
            CheckBox checkBox = this.checkBox;
            if (checkBox != null && checkBox.getVisibility() == 0) {
                accessibilityNodeInfo.setCheckable(true);
                accessibilityNodeInfo.setChecked(this.checkBox.isChecked());
                accessibilityNodeInfo.setClassName("android.widget.CheckBox");
                return;
            }
            return;
        }
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.checkBoxBig.isChecked());
        accessibilityNodeInfo.setClassName("android.widget.CheckBox");
    }
}
