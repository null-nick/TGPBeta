package org.telegram.p009ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0952R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.p009ui.ActionBar.ActionBarMenuItem;
import org.telegram.p009ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.p009ui.ActionBar.AlertDialog;
import org.telegram.p009ui.ActionBar.BaseFragment;
import org.telegram.p009ui.ActionBar.C1006ActionBar;
import org.telegram.p009ui.ActionBar.Theme;
import org.telegram.p009ui.ActionBar.ThemeDescription;
import org.telegram.p009ui.Cells.HeaderCell;
import org.telegram.p009ui.Cells.TextCheckCell;
import org.telegram.p009ui.Cells.TextInfoPrivacyCell;
import org.telegram.p009ui.Cells.TextSettingsCell;
import org.telegram.p009ui.Components.AlertsCreator;
import org.telegram.p009ui.Components.CubicBezierInterpolator;
import org.telegram.p009ui.Components.CustomPhoneKeyboardView;
import org.telegram.p009ui.Components.Easings;
import org.telegram.p009ui.Components.EditTextBoldCursor;
import org.telegram.p009ui.Components.NumberPicker;
import org.telegram.p009ui.Components.OutlineTextContainerView;
import org.telegram.p009ui.Components.RLottieImageView;
import org.telegram.p009ui.Components.RecyclerListView;
import org.telegram.p009ui.Components.TextViewSwitcher;
import org.telegram.p009ui.Components.TransformableLoginButtonView;
import org.telegram.p009ui.Components.VerticalPositionAutoAnimator;
import org.telegram.p009ui.PasscodeActivity;
import org.telegram.tgnet.ConnectionsManager;

public class PasscodeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int autoLockDetailRow;
    private int autoLockRow;
    private int captureDetailRow;
    private int captureHeaderRow;
    private int captureRow;
    private int changePasscodeRow;
    private CodeFieldContainer codeFieldContainer;
    private TextViewSwitcher descriptionTextSwitcher;
    private int disablePasscodeRow;
    private int fingerprintRow;
    private String firstPassword;
    private VerticalPositionAutoAnimator floatingAutoAnimator;
    private Animator floatingButtonAnimator;
    private FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    private int hintRow;
    private CustomPhoneKeyboardView keyboardView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private RLottieImageView lockImageView;
    private Runnable onShowKeyboardCallback;
    private ActionBarMenuItem otherItem;
    private OutlineTextContainerView outlinePasswordView;
    private TextView passcodesDoNotMatchTextView;
    private ImageView passwordButton;
    private EditTextBoldCursor passwordEditText;
    private boolean postedHidePasscodesDoNotMatch;
    private int rowCount;
    private TextView titleTextView;
    private int type;
    private int utyanRow;
    private int currentPasswordType = 0;
    private int passcodeSetStep = 0;
    private Runnable hidePasscodesDoNotMatch = new Runnable() {
        @Override
        public final void run() {
            PasscodeActivity.this.lambda$new$0();
        }
    };

    public void lambda$new$0() {
        this.postedHidePasscodesDoNotMatch = false;
        AndroidUtilities.updateViewVisibilityAnimated(this.passcodesDoNotMatchTextView, false);
    }

    public PasscodeActivity(int i) {
        this.type = i;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type != 0) {
            return true;
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.type == 0) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override
    public android.view.View createView(final android.content.Context r30) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.PasscodeActivity.createView(android.content.Context):android.view.View");
    }

    public void lambda$createView$1(int i, boolean z) {
        Runnable runnable;
        if (i >= AndroidUtilities.m34dp(20.0f) && (runnable = this.onShowKeyboardCallback) != null) {
            runnable.run();
            this.onShowKeyboardCallback = null;
        }
    }

    public void lambda$createView$5(View view, final int i) {
        if (view.isEnabled()) {
            if (i == this.disablePasscodeRow) {
                AlertDialog create = new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString((int) C0952R.string.DisablePasscode)).setMessage(LocaleController.getString((int) C0952R.string.DisablePasscodeConfirmMessage)).setNegativeButton(LocaleController.getString((int) C0952R.string.Cancel), null).setPositiveButton(LocaleController.getString((int) C0952R.string.DisablePasscodeTurnOff), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        PasscodeActivity.this.lambda$createView$2(dialogInterface, i2);
                    }
                }).create();
                create.show();
                ((TextView) create.getButton(-1)).setTextColor(Theme.getColor("dialogTextRed"));
            } else if (i == this.changePasscodeRow) {
                presentFragment(new PasscodeActivity(1));
            } else if (i == this.autoLockRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("AutoLock", C0952R.string.AutoLock));
                    final NumberPicker numberPicker = new NumberPicker(getParentActivity());
                    numberPicker.setMinValue(0);
                    numberPicker.setMaxValue(4);
                    int i2 = SharedConfig.autoLockIn;
                    if (i2 == 0) {
                        numberPicker.setValue(0);
                    } else if (i2 == 60) {
                        numberPicker.setValue(1);
                    } else if (i2 == 300) {
                        numberPicker.setValue(2);
                    } else if (i2 == 3600) {
                        numberPicker.setValue(3);
                    } else if (i2 == 18000) {
                        numberPicker.setValue(4);
                    }
                    numberPicker.setFormatter(PasscodeActivity$$ExternalSyntheticLambda21.INSTANCE);
                    builder.setView(numberPicker);
                    builder.setNegativeButton(LocaleController.getString("Done", C0952R.string.Done), new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i3) {
                            PasscodeActivity.this.lambda$createView$4(numberPicker, i, dialogInterface, i3);
                        }
                    });
                    showDialog(builder.create());
                }
            } else if (i == this.fingerprintRow) {
                SharedConfig.useFingerprint = !SharedConfig.useFingerprint;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ((TextCheckCell) view).setChecked(SharedConfig.useFingerprint);
            } else if (i == this.captureRow) {
                SharedConfig.allowScreenCapture = !SharedConfig.allowScreenCapture;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ((TextCheckCell) view).setChecked(SharedConfig.allowScreenCapture);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, Boolean.FALSE);
                if (!SharedConfig.allowScreenCapture) {
                    AlertsCreator.showSimpleAlert(this, LocaleController.getString("ScreenCaptureAlert", C0952R.string.ScreenCaptureAlert));
                }
            }
        }
    }

    public void lambda$createView$2(DialogInterface dialogInterface, int i) {
        SharedConfig.passcodeHash = "";
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        getMediaDataController().buildShortcuts();
        int childCount = this.listView.getChildCount();
        int i2 = 0;
        while (true) {
            if (i2 >= childCount) {
                break;
            }
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof TextSettingsCell) {
                ((TextSettingsCell) childAt).setTextColor(Theme.getColor("windowBackgroundWhiteGrayText7"));
                break;
            }
            i2++;
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        finishFragment();
    }

    public static String lambda$createView$3(int i) {
        if (i == 0) {
            return LocaleController.getString("AutoLockDisabled", C0952R.string.AutoLockDisabled);
        }
        return i == 1 ? LocaleController.formatString("AutoLockInTime", C0952R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", 1)) : i == 2 ? LocaleController.formatString("AutoLockInTime", C0952R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", 5)) : i == 3 ? LocaleController.formatString("AutoLockInTime", C0952R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", 1)) : i == 4 ? LocaleController.formatString("AutoLockInTime", C0952R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", 5)) : "";
    }

    public void lambda$createView$4(NumberPicker numberPicker, int i, DialogInterface dialogInterface, int i2) {
        int value = numberPicker.getValue();
        if (value == 0) {
            SharedConfig.autoLockIn = 0;
        } else if (value == 1) {
            SharedConfig.autoLockIn = 60;
        } else if (value == 2) {
            SharedConfig.autoLockIn = 300;
        } else if (value == 3) {
            SharedConfig.autoLockIn = 3600;
        } else if (value == 4) {
            SharedConfig.autoLockIn = 18000;
        }
        this.listAdapter.notifyItemChanged(i);
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    public class C31644 extends C1006ActionBar.ActionBarMenuOnItemClick {
        final ActionBarMenuSubItem val$switchItem;

        C31644(ActionBarMenuSubItem actionBarMenuSubItem) {
            this.val$switchItem = actionBarMenuSubItem;
        }

        @Override
        public void onItemClick(int i) {
            if (i == -1) {
                PasscodeActivity.this.finishFragment();
                return;
            }
            int i2 = 1;
            if (i == 1) {
                PasscodeActivity passcodeActivity = PasscodeActivity.this;
                if (passcodeActivity.currentPasswordType != 0) {
                    i2 = 0;
                }
                passcodeActivity.currentPasswordType = i2;
                final ActionBarMenuSubItem actionBarMenuSubItem = this.val$switchItem;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        PasscodeActivity.C31644.this.lambda$onItemClick$0(actionBarMenuSubItem);
                    }
                }, 150L);
                PasscodeActivity.this.passwordEditText.setText("");
                for (CodeNumberField codeNumberField : PasscodeActivity.this.codeFieldContainer.codeField) {
                    codeNumberField.setText("");
                }
                PasscodeActivity.this.updateFields();
            }
        }

        public void lambda$onItemClick$0(ActionBarMenuSubItem actionBarMenuSubItem) {
            actionBarMenuSubItem.setText(LocaleController.getString(PasscodeActivity.this.currentPasswordType == 0 ? C0952R.string.PasscodeSwitchToPassword : C0952R.string.PasscodeSwitchToPIN));
            actionBarMenuSubItem.setIcon(PasscodeActivity.this.currentPasswordType == 0 ? C0952R.C0953drawable.msg_permissions : C0952R.C0953drawable.msg_pin_code);
            PasscodeActivity.this.showKeyboard();
            if (PasscodeActivity.this.isPinCode()) {
                PasscodeActivity.this.passwordEditText.setInputType(524417);
                AndroidUtilities.updateViewVisibilityAnimated(PasscodeActivity.this.passwordButton, true, 0.1f, false);
            }
        }
    }

    public static View lambda$createView$6(Context context) {
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        textView.setGravity(1);
        textView.setLineSpacing(AndroidUtilities.m34dp(2.0f), 1.0f);
        textView.setTextSize(1, 15.0f);
        return textView;
    }

    public static void lambda$createView$7(Context context, View view) {
        AlertsCreator.createForgotPasscodeDialog(context).show();
    }

    public void lambda$createView$8(View view, boolean z) {
        this.outlinePasswordView.animateSelection(z ? 1.0f : 0.0f);
    }

    public void lambda$createView$9(AtomicBoolean atomicBoolean, View view) {
        atomicBoolean.set(!atomicBoolean.get());
        int selectionStart = this.passwordEditText.getSelectionStart();
        int selectionEnd = this.passwordEditText.getSelectionEnd();
        this.passwordEditText.setInputType((atomicBoolean.get() ? 144 : ConnectionsManager.RequestFlagNeedQuickAck) | 1);
        this.passwordEditText.setSelection(selectionStart, selectionEnd);
        this.passwordButton.setColorFilter(Theme.getColor(atomicBoolean.get() ? "windowBackgroundWhiteInputFieldActivated" : "windowBackgroundWhiteHintText"));
    }

    public boolean lambda$createView$10(TextView textView, int i, KeyEvent keyEvent) {
        int i2 = this.passcodeSetStep;
        if (i2 == 0) {
            processNext();
            return true;
        } else if (i2 != 1) {
            return false;
        } else {
            processDone();
            return true;
        }
    }

    public class C31688 extends CodeFieldContainer {
        C31688(Context context) {
            super(context);
        }

        @Override
        protected void processNextPressed() {
            if (PasscodeActivity.this.passcodeSetStep == 0) {
                postDelayed(new Runnable() {
                    @Override
                    public final void run() {
                        PasscodeActivity.C31688.this.lambda$processNextPressed$0();
                    }
                }, 260L);
            } else {
                PasscodeActivity.this.processDone();
            }
        }

        public void lambda$processNextPressed$0() {
            PasscodeActivity.this.processNext();
        }
    }

    public void lambda$createView$11(CodeNumberField codeNumberField, View view, boolean z) {
        this.keyboardView.setEditText(codeNumberField);
        this.keyboardView.setDispatchBackWhenEmpty(true);
    }

    public void lambda$createView$12(View view) {
        int i = this.type;
        if (i == 1) {
            if (this.passcodeSetStep == 0) {
                processNext();
            } else {
                processDone();
            }
        } else if (i == 2) {
            processDone();
        }
    }

    @Override
    public boolean hasForceLightStatusBar() {
        return this.type != 0;
    }

    private void setCustomKeyboardVisible(final boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        } else {
            AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
        }
        int i = 0;
        float f = 1.0f;
        float f2 = 0.0f;
        if (!z2) {
            CustomPhoneKeyboardView customPhoneKeyboardView = this.keyboardView;
            if (!z) {
                i = 8;
            }
            customPhoneKeyboardView.setVisibility(i);
            CustomPhoneKeyboardView customPhoneKeyboardView2 = this.keyboardView;
            if (!z) {
                f = 0.0f;
            }
            customPhoneKeyboardView2.setAlpha(f);
            CustomPhoneKeyboardView customPhoneKeyboardView3 = this.keyboardView;
            if (!z) {
                f2 = AndroidUtilities.m34dp(230.0f);
            }
            customPhoneKeyboardView3.setTranslationY(f2);
            this.fragmentView.requestLayout();
            return;
        }
        float[] fArr = new float[2];
        fArr[0] = z ? 0.0f : 1.0f;
        if (!z) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(150L);
        duration.setInterpolator(z ? CubicBezierInterpolator.DEFAULT : Easings.easeInOutQuad);
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PasscodeActivity.this.lambda$setCustomKeyboardVisible$13(valueAnimator);
            }
        });
        duration.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (z) {
                    PasscodeActivity.this.keyboardView.setVisibility(0);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!z) {
                    PasscodeActivity.this.keyboardView.setVisibility(8);
                }
            }
        });
        duration.start();
    }

    public void lambda$setCustomKeyboardVisible$13(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(floatValue);
        this.keyboardView.setTranslationY((1.0f - floatValue) * AndroidUtilities.m34dp(230.0f) * 0.75f);
        this.fragmentView.requestLayout();
    }

    private void setFloatingButtonVisible(final boolean z, boolean z2) {
        Animator animator = this.floatingButtonAnimator;
        if (animator != null) {
            animator.cancel();
            this.floatingButtonAnimator = null;
        }
        int i = 0;
        float f = 1.0f;
        if (!z2) {
            this.floatingAutoAnimator.setOffsetY(z ? 0.0f : AndroidUtilities.m34dp(70.0f));
            FrameLayout frameLayout = this.floatingButtonContainer;
            if (!z) {
                f = 0.0f;
            }
            frameLayout.setAlpha(f);
            FrameLayout frameLayout2 = this.floatingButtonContainer;
            if (!z) {
                i = 8;
            }
            frameLayout2.setVisibility(i);
            return;
        }
        float[] fArr = new float[2];
        fArr[0] = z ? 0.0f : 1.0f;
        if (!z) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(150L);
        duration.setInterpolator(z ? AndroidUtilities.decelerateInterpolator : AndroidUtilities.accelerateInterpolator);
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PasscodeActivity.this.lambda$setFloatingButtonVisible$14(valueAnimator);
            }
        });
        duration.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator2) {
                if (z) {
                    PasscodeActivity.this.floatingButtonContainer.setVisibility(0);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator2) {
                if (!z) {
                    PasscodeActivity.this.floatingButtonContainer.setVisibility(8);
                }
                if (PasscodeActivity.this.floatingButtonAnimator == animator2) {
                    PasscodeActivity.this.floatingButtonAnimator = null;
                }
            }
        });
        duration.start();
        this.floatingButtonAnimator = duration;
    }

    public void lambda$setFloatingButtonVisible$14(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(AndroidUtilities.m34dp(70.0f) * (1.0f - floatValue));
        this.floatingButtonContainer.setAlpha(floatValue);
    }

    public static BaseFragment determineOpenFragment() {
        if (SharedConfig.passcodeHash.length() != 0) {
            return new PasscodeActivity(2);
        }
        return new ActionIntroActivity(6);
    }

    private void animateSuccessAnimation(final Runnable runnable) {
        if (!isPinCode()) {
            runnable.run();
            return;
        }
        int i = 0;
        while (true) {
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            CodeNumberField[] codeNumberFieldArr = codeFieldContainer.codeField;
            if (i < codeNumberFieldArr.length) {
                final CodeNumberField codeNumberField = codeNumberFieldArr[i];
                codeNumberField.postDelayed(new Runnable() {
                    @Override
                    public final void run() {
                        CodeNumberField.this.animateSuccessProgress(1.0f);
                    }
                }, i * 75);
                i++;
            } else {
                codeFieldContainer.postDelayed(new Runnable() {
                    @Override
                    public final void run() {
                        PasscodeActivity.this.lambda$animateSuccessAnimation$16(runnable);
                    }
                }, (this.codeFieldContainer.codeField.length * 75) + 350);
                return;
            }
        }
    }

    public void lambda$animateSuccessAnimation$16(Runnable runnable) {
        for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
            codeNumberField.animateSuccessProgress(0.0f);
        }
        runnable.run();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        int i;
        super.onConfigurationChanged(configuration);
        setCustomKeyboardVisible(isCustomKeyboardVisible(), false);
        RLottieImageView rLottieImageView = this.lockImageView;
        if (rLottieImageView != null) {
            if (!AndroidUtilities.isSmallScreen()) {
                Point point = AndroidUtilities.displaySize;
                if (point.x < point.y) {
                    i = 0;
                    rLottieImageView.setVisibility(i);
                }
            }
            i = 8;
            rLottieImageView.setVisibility(i);
        }
        for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
            codeNumberField.setShowSoftInputOnFocusCompat(!isCustomKeyboardVisible());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (this.type != 0 && !isCustomKeyboardVisible()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PasscodeActivity.this.showKeyboard();
                }
            }, 200L);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.didSetPasscode) {
            return;
        }
        if ((objArr.length == 0 || ((Boolean) objArr[0]).booleanValue()) && this.type == 0) {
            updateRows();
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.utyanRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.hintRow = i;
        this.rowCount = i2 + 1;
        this.changePasscodeRow = i2;
        try {
            if (Build.VERSION.SDK_INT < 23) {
                this.fingerprintRow = -1;
            } else if (!FingerprintManagerCompat.from(ApplicationLoader.applicationContext).isHardwareDetected() || !AndroidUtilities.isKeyguardSecure()) {
                this.fingerprintRow = -1;
            } else {
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.fingerprintRow = i3;
            }
        } catch (Throwable th) {
            FileLog.m30e(th);
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.autoLockRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.autoLockDetailRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.captureHeaderRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.captureRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.captureDetailRow = i8;
        this.rowCount = i9 + 1;
        this.disablePasscodeRow = i9;
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.type != 0) {
            showKeyboard();
        }
    }

    public void showKeyboard() {
        if (isPinCode()) {
            this.codeFieldContainer.codeField[0].requestFocus();
            if (!isCustomKeyboardVisible()) {
                AndroidUtilities.showKeyboard(this.codeFieldContainer.codeField[0]);
            }
        } else if (isPassword()) {
            this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    public void updateFields() {
        String str;
        int i = this.type;
        int i2 = C0952R.string.CreatePasscodeInfoPIN;
        if (i == 2) {
            str = LocaleController.getString((int) C0952R.string.EnterYourPasscodeInfo);
        } else if (this.passcodeSetStep == 0) {
            str = LocaleController.getString(this.currentPasswordType == 0 ? C0952R.string.CreatePasscodeInfoPIN : C0952R.string.CreatePasscodeInfoPassword);
        } else {
            str = this.descriptionTextSwitcher.getCurrentView().getText().toString();
        }
        final boolean z = !this.descriptionTextSwitcher.getCurrentView().getText().equals(str) && !TextUtils.isEmpty(this.descriptionTextSwitcher.getCurrentView().getText());
        if (this.type == 2) {
            this.descriptionTextSwitcher.setText(LocaleController.getString((int) C0952R.string.EnterYourPasscodeInfo), z);
        } else if (this.passcodeSetStep == 0) {
            TextViewSwitcher textViewSwitcher = this.descriptionTextSwitcher;
            if (this.currentPasswordType != 0) {
                i2 = C0952R.string.CreatePasscodeInfoPassword;
            }
            textViewSwitcher.setText(LocaleController.getString(i2), z);
        }
        if (isPinCode()) {
            AndroidUtilities.updateViewVisibilityAnimated(this.codeFieldContainer, true, 1.0f, z);
            AndroidUtilities.updateViewVisibilityAnimated(this.outlinePasswordView, false, 1.0f, z);
        } else if (isPassword()) {
            AndroidUtilities.updateViewVisibilityAnimated(this.codeFieldContainer, false, 1.0f, z);
            AndroidUtilities.updateViewVisibilityAnimated(this.outlinePasswordView, true, 1.0f, z);
        }
        final boolean isPassword = isPassword();
        if (isPassword) {
            Runnable passcodeActivity$$ExternalSyntheticLambda20 = new Runnable() {
                @Override
                public final void run() {
                    PasscodeActivity.this.lambda$updateFields$17(isPassword, z);
                }
            };
            this.onShowKeyboardCallback = passcodeActivity$$ExternalSyntheticLambda20;
            AndroidUtilities.runOnUIThread(passcodeActivity$$ExternalSyntheticLambda20, 3000L);
        } else {
            setFloatingButtonVisible(isPassword, z);
        }
        setCustomKeyboardVisible(isCustomKeyboardVisible(), z);
        showKeyboard();
    }

    public void lambda$updateFields$17(boolean z, boolean z2) {
        setFloatingButtonVisible(z, z2);
        AndroidUtilities.cancelRunOnUIThread(this.onShowKeyboardCallback);
    }

    public boolean isCustomKeyboardVisible() {
        if (isPinCode() && this.type != 0 && !AndroidUtilities.isTablet()) {
            Point point = AndroidUtilities.displaySize;
            if (point.x < point.y && !AndroidUtilities.isAccessibilityTouchExplorationEnabled()) {
                return true;
            }
        }
        return false;
    }

    public void processNext() {
        if (!(this.currentPasswordType == 1 && this.passwordEditText.getText().length() == 0) && (this.currentPasswordType != 0 || this.codeFieldContainer.getCode().length() == 4)) {
            ActionBarMenuItem actionBarMenuItem = this.otherItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(8);
            }
            this.titleTextView.setText(LocaleController.getString("ConfirmCreatePasscode", C0952R.string.ConfirmCreatePasscode));
            this.descriptionTextSwitcher.setText(AndroidUtilities.replaceTags(LocaleController.getString("PasscodeReinstallNotice", C0952R.string.PasscodeReinstallNotice)));
            this.firstPassword = isPinCode() ? this.codeFieldContainer.getCode() : this.passwordEditText.getText().toString();
            this.passwordEditText.setText("");
            this.passwordEditText.setInputType(524417);
            for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
                codeNumberField.setText("");
            }
            showKeyboard();
            this.passcodeSetStep = 1;
            return;
        }
        onPasscodeError();
    }

    public boolean isPinCode() {
        int i = this.type;
        if (i == 1 && this.currentPasswordType == 0) {
            return true;
        }
        return i == 2 && SharedConfig.passcodeType == 0;
    }

    private boolean isPassword() {
        int i = this.type;
        if (i == 1 && this.currentPasswordType == 1) {
            return true;
        }
        return i == 2 && SharedConfig.passcodeType == 1;
    }

    public void processDone() {
        if (!isPassword() || this.passwordEditText.getText().length() != 0) {
            String code = isPinCode() ? this.codeFieldContainer.getCode() : this.passwordEditText.getText().toString();
            int i = this.type;
            int i2 = 0;
            if (i == 1) {
                if (!this.firstPassword.equals(code)) {
                    AndroidUtilities.updateViewVisibilityAnimated(this.passcodesDoNotMatchTextView, true);
                    for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
                        codeNumberField.setText("");
                    }
                    if (isPinCode()) {
                        this.codeFieldContainer.codeField[0].requestFocus();
                    }
                    this.passwordEditText.setText("");
                    onPasscodeError();
                    this.codeFieldContainer.removeCallbacks(this.hidePasscodesDoNotMatch);
                    this.codeFieldContainer.post(new Runnable() {
                        @Override
                        public final void run() {
                            PasscodeActivity.this.lambda$processDone$18();
                        }
                    });
                    return;
                }
                final boolean z = SharedConfig.passcodeHash.length() == 0;
                try {
                    SharedConfig.passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(SharedConfig.passcodeSalt);
                    byte[] bytes = this.firstPassword.getBytes("UTF-8");
                    int length = bytes.length + 32;
                    byte[] bArr = new byte[length];
                    System.arraycopy(SharedConfig.passcodeSalt, 0, bArr, 0, 16);
                    System.arraycopy(bytes, 0, bArr, 16, bytes.length);
                    System.arraycopy(SharedConfig.passcodeSalt, 0, bArr, bytes.length + 16, 16);
                    SharedConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bArr, 0, length));
                } catch (Exception e) {
                    FileLog.m30e(e);
                }
                SharedConfig.allowScreenCapture = true;
                SharedConfig.passcodeType = this.currentPasswordType;
                SharedConfig.saveConfig();
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                int length2 = codeNumberFieldArr.length;
                while (i2 < length2) {
                    CodeNumberField codeNumberField2 = codeNumberFieldArr[i2];
                    codeNumberField2.clearFocus();
                    AndroidUtilities.hideKeyboard(codeNumberField2);
                    i2++;
                }
                this.keyboardView.setEditText(null);
                animateSuccessAnimation(new Runnable() {
                    @Override
                    public final void run() {
                        PasscodeActivity.this.lambda$processDone$19(z);
                    }
                });
            } else if (i == 2) {
                long j = SharedConfig.passcodeRetryInMs;
                if (j > 0) {
                    double d = j;
                    Double.isNaN(d);
                    Toast.makeText(getParentActivity(), LocaleController.formatString("TooManyTries", C0952R.string.TooManyTries, LocaleController.formatPluralString("Seconds", Math.max(1, (int) Math.ceil(d / 1000.0d)))), 0).show();
                    for (CodeNumberField codeNumberField3 : this.codeFieldContainer.codeField) {
                        codeNumberField3.setText("");
                    }
                    this.passwordEditText.setText("");
                    if (isPinCode()) {
                        this.codeFieldContainer.codeField[0].requestFocus();
                    }
                    onPasscodeError();
                } else if (!SharedConfig.checkPasscode(code)) {
                    SharedConfig.increaseBadPasscodeTries();
                    this.passwordEditText.setText("");
                    for (CodeNumberField codeNumberField4 : this.codeFieldContainer.codeField) {
                        codeNumberField4.setText("");
                    }
                    if (isPinCode()) {
                        this.codeFieldContainer.codeField[0].requestFocus();
                    }
                    onPasscodeError();
                } else {
                    SharedConfig.badPasscodeTries = 0;
                    SharedConfig.saveConfig();
                    this.passwordEditText.clearFocus();
                    AndroidUtilities.hideKeyboard(this.passwordEditText);
                    CodeNumberField[] codeNumberFieldArr2 = this.codeFieldContainer.codeField;
                    int length3 = codeNumberFieldArr2.length;
                    while (i2 < length3) {
                        CodeNumberField codeNumberField5 = codeNumberFieldArr2[i2];
                        codeNumberField5.clearFocus();
                        AndroidUtilities.hideKeyboard(codeNumberField5);
                        i2++;
                    }
                    this.keyboardView.setEditText(null);
                    animateSuccessAnimation(new Runnable() {
                        @Override
                        public final void run() {
                            PasscodeActivity.this.lambda$processDone$20();
                        }
                    });
                }
            }
        } else {
            onPasscodeError();
        }
    }

    public void lambda$processDone$18() {
        this.codeFieldContainer.postDelayed(this.hidePasscodesDoNotMatch, 3000L);
        this.postedHidePasscodesDoNotMatch = true;
    }

    public void lambda$processDone$19(boolean z) {
        getMediaDataController().buildShortcuts();
        if (z) {
            presentFragment(new PasscodeActivity(0), true);
        } else {
            finishFragment();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
    }

    public void lambda$processDone$20() {
        presentFragment(new PasscodeActivity(0), true);
    }

    private void onPasscodeError() {
        if (getParentActivity() != null) {
            try {
                this.fragmentView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (isPinCode()) {
                for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
                    codeNumberField.animateErrorProgress(1.0f);
                }
            } else {
                this.outlinePasswordView.animateError(1.0f);
            }
            AndroidUtilities.shakeViewSpring(isPinCode() ? this.codeFieldContainer : this.outlinePasswordView, isPinCode() ? 10.0f : 4.0f, new Runnable() {
                @Override
                public final void run() {
                    PasscodeActivity.this.lambda$onPasscodeError$22();
                }
            });
        }
    }

    public void lambda$onPasscodeError$22() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PasscodeActivity.this.lambda$onPasscodeError$21();
            }
        }, isPinCode() ? 150L : 1000L);
    }

    public void lambda$onPasscodeError$21() {
        if (isPinCode()) {
            for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
                codeNumberField.animateErrorProgress(0.0f);
            }
            return;
        }
        this.outlinePasswordView.animateError(0.0f);
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == PasscodeActivity.this.fingerprintRow || adapterPosition == PasscodeActivity.this.autoLockRow || adapterPosition == PasscodeActivity.this.captureRow || adapterPosition == PasscodeActivity.this.changePasscodeRow || adapterPosition == PasscodeActivity.this.disablePasscodeRow;
        }

        @Override
        public int getItemCount() {
            return PasscodeActivity.this.rowCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            if (i == 0) {
                view2 = new TextCheckCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i == 1) {
                view2 = new TextSettingsCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i != 3) {
                if (i != 4) {
                    view = new TextInfoPrivacyCell(this.mContext);
                } else {
                    view = new RLottieImageHolderView(this.mContext);
                }
                return new RecyclerListView.Holder(view);
            } else {
                view2 = new HeaderCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            view = view2;
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                if (i == PasscodeActivity.this.fingerprintRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("UnlockFingerprint", C0952R.string.UnlockFingerprint), SharedConfig.useFingerprint, true);
                } else if (i == PasscodeActivity.this.captureRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString((int) C0952R.string.ScreenCaptureShowContent), SharedConfig.allowScreenCapture, false);
                }
            } else if (itemViewType != 1) {
                int i2 = 3;
                if (itemViewType == 2) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == PasscodeActivity.this.hintRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString((int) C0952R.string.PasscodeScreenHint));
                        textInfoPrivacyCell.setBackground(null);
                        textInfoPrivacyCell.getTextView().setGravity(1);
                    } else if (i == PasscodeActivity.this.autoLockDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString((int) C0952R.string.AutoLockInfo));
                        textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.mContext, (int) C0952R.C0953drawable.greydivider, "windowBackgroundGrayShadow"));
                        TextView textView = textInfoPrivacyCell.getTextView();
                        if (LocaleController.isRTL) {
                            i2 = 5;
                        }
                        textView.setGravity(i2);
                    } else if (i == PasscodeActivity.this.captureDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString((int) C0952R.string.ScreenCaptureInfo));
                        textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.mContext, (int) C0952R.C0953drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                        TextView textView2 = textInfoPrivacyCell.getTextView();
                        if (LocaleController.isRTL) {
                            i2 = 5;
                        }
                        textView2.setGravity(i2);
                    }
                } else if (itemViewType == 3) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    headerCell.setHeight(46);
                    if (i == PasscodeActivity.this.captureHeaderRow) {
                        headerCell.setText(LocaleController.getString((int) C0952R.string.ScreenCaptureHeader));
                    }
                } else if (itemViewType == 4) {
                    RLottieImageHolderView rLottieImageHolderView = (RLottieImageHolderView) viewHolder.itemView;
                    rLottieImageHolderView.imageView.setAnimation(C0952R.raw.utyan_passcode, 100, 100);
                    rLottieImageHolderView.imageView.playAnimation();
                }
            } else {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == PasscodeActivity.this.changePasscodeRow) {
                    textSettingsCell.setText(LocaleController.getString("ChangePasscode", C0952R.string.ChangePasscode), true);
                    if (SharedConfig.passcodeHash.length() == 0) {
                        textSettingsCell.setTag("windowBackgroundWhiteGrayText7");
                        textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText7"));
                        return;
                    }
                    textSettingsCell.setTag("windowBackgroundWhiteBlackText");
                    textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                } else if (i == PasscodeActivity.this.autoLockRow) {
                    int i3 = SharedConfig.autoLockIn;
                    if (i3 == 0) {
                        str = LocaleController.formatString("AutoLockDisabled", C0952R.string.AutoLockDisabled, new Object[0]);
                    } else if (i3 < 3600) {
                        str = LocaleController.formatString("AutoLockInTime", C0952R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", i3 / 60));
                    } else {
                        str = i3 < 86400 ? LocaleController.formatString("AutoLockInTime", C0952R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", (int) Math.ceil((i3 / 60.0f) / 60.0f))) : LocaleController.formatString("AutoLockInTime", C0952R.string.AutoLockInTime, LocaleController.formatPluralString("Days", (int) Math.ceil(((i3 / 60.0f) / 60.0f) / 24.0f)));
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("AutoLock", C0952R.string.AutoLock), str, true);
                    textSettingsCell.setTag("windowBackgroundWhiteBlackText");
                    textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                } else if (i == PasscodeActivity.this.disablePasscodeRow) {
                    textSettingsCell.setText(LocaleController.getString((int) C0952R.string.DisablePasscode), false);
                    textSettingsCell.setTag("dialogTextRed");
                    textSettingsCell.setTextColor(Theme.getColor("dialogTextRed"));
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            if (i == PasscodeActivity.this.fingerprintRow || i == PasscodeActivity.this.captureRow) {
                return 0;
            }
            if (i == PasscodeActivity.this.changePasscodeRow || i == PasscodeActivity.this.autoLockRow || i == PasscodeActivity.this.disablePasscodeRow) {
                return 1;
            }
            if (i == PasscodeActivity.this.autoLockDetailRow || i == PasscodeActivity.this.captureDetailRow || i == PasscodeActivity.this.hintRow) {
                return 2;
            }
            if (i == PasscodeActivity.this.captureHeaderRow) {
                return 3;
            }
            return i == PasscodeActivity.this.utyanRow ? 4 : 0;
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckCell.class, TextSettingsCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText7"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        return arrayList;
    }

    public static final class RLottieImageHolderView extends FrameLayout {
        private RLottieImageView imageView;

        private RLottieImageHolderView(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    PasscodeActivity.RLottieImageHolderView.this.lambda$new$0(view);
                }
            });
            int dp = AndroidUtilities.m34dp(120.0f);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dp, dp);
            layoutParams.gravity = 1;
            addView(this.imageView, layoutParams);
            setPadding(0, AndroidUtilities.m34dp(32.0f), 0, 0);
            setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        }

        public void lambda$new$0(View view) {
            if (!this.imageView.getAnimatedDrawable().isRunning()) {
                this.imageView.getAnimatedDrawable().setCurrentFrame(0, false);
                this.imageView.playAnimation();
            }
        }
    }
}
