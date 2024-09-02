package org.telegram.messenger;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$DraftMessage;
import org.telegram.tgnet.TLRPC$EmojiStatus;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_dialogFolder;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$TL_emojiStatusUntil;
import org.telegram.tgnet.TLRPC$TL_username;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;

public class DialogObject {
    public static int getEncryptedChatId(long j) {
        return (int) (j & 4294967295L);
    }

    public static int getFolderId(long j) {
        return (int) j;
    }

    public static boolean isEncryptedDialog(long j) {
        return (4611686018427387904L & j) != 0 && (j & Long.MIN_VALUE) == 0;
    }

    public static boolean isFolderDialogId(long j) {
        return (2305843009213693952L & j) != 0 && (j & Long.MIN_VALUE) == 0;
    }

    public static long makeEncryptedDialogId(long j) {
        return (j & 4294967295L) | 4611686018427387904L;
    }

    public static long makeFolderDialogId(int i) {
        return i | 2305843009213693952L;
    }

    public static boolean isChannel(TLRPC$Dialog tLRPC$Dialog) {
        return (tLRPC$Dialog == null || (tLRPC$Dialog.flags & 1) == 0) ? false : true;
    }

    public static void initDialog(TLRPC$Dialog tLRPC$Dialog) {
        if (tLRPC$Dialog == null || tLRPC$Dialog.id != 0) {
            return;
        }
        if (tLRPC$Dialog instanceof TLRPC$TL_dialog) {
            TLRPC$Peer tLRPC$Peer = tLRPC$Dialog.peer;
            if (tLRPC$Peer == null) {
                return;
            }
            long j = tLRPC$Peer.user_id;
            if (j != 0) {
                tLRPC$Dialog.id = j;
                return;
            }
            long j2 = tLRPC$Peer.chat_id;
            if (j2 != 0) {
                tLRPC$Dialog.id = -j2;
                return;
            } else {
                tLRPC$Dialog.id = -tLRPC$Peer.channel_id;
                return;
            }
        }
        if (tLRPC$Dialog instanceof TLRPC$TL_dialogFolder) {
            tLRPC$Dialog.id = makeFolderDialogId(((TLRPC$TL_dialogFolder) tLRPC$Dialog).folder.id);
        }
    }

    public static long getPeerDialogId(TLRPC$Peer tLRPC$Peer) {
        if (tLRPC$Peer == null) {
            return 0L;
        }
        long j = tLRPC$Peer.user_id;
        if (j != 0) {
            return j;
        }
        long j2 = tLRPC$Peer.chat_id;
        return j2 != 0 ? -j2 : -tLRPC$Peer.channel_id;
    }

    public static long getPeerDialogId(TLRPC$InputPeer tLRPC$InputPeer) {
        if (tLRPC$InputPeer == null) {
            return 0L;
        }
        long j = tLRPC$InputPeer.user_id;
        if (j != 0) {
            return j;
        }
        long j2 = tLRPC$InputPeer.chat_id;
        return j2 != 0 ? -j2 : -tLRPC$InputPeer.channel_id;
    }

    public static long getLastMessageOrDraftDate(TLRPC$Dialog tLRPC$Dialog, TLRPC$DraftMessage tLRPC$DraftMessage) {
        int i;
        return (tLRPC$DraftMessage == null || (i = tLRPC$DraftMessage.date) < tLRPC$Dialog.last_message_date) ? tLRPC$Dialog.last_message_date : i;
    }

    public static boolean isChatDialog(long j) {
        return (isEncryptedDialog(j) || isFolderDialogId(j) || j >= 0) ? false : true;
    }

    public static boolean isUserDialog(long j) {
        return (isEncryptedDialog(j) || isFolderDialogId(j) || j <= 0) ? false : true;
    }

    public static String getDialogTitle(TLObject tLObject) {
        return setDialogPhotoTitle(null, null, tLObject);
    }

    public static String setDialogPhotoTitle(ImageReceiver imageReceiver, AvatarDrawable avatarDrawable, TLObject tLObject) {
        String str;
        if (tLObject instanceof TLRPC$User) {
            TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
            if (UserObject.isReplyUser(tLRPC$User)) {
                String string = LocaleController.getString(R.string.RepliesTitle);
                if (avatarDrawable != null) {
                    avatarDrawable.setAvatarType(12);
                }
                if (imageReceiver == null) {
                    return string;
                }
                imageReceiver.setForUserOrChat(null, avatarDrawable);
                return string;
            }
            if (UserObject.isUserSelf(tLRPC$User)) {
                String string2 = LocaleController.getString(R.string.SavedMessages);
                if (avatarDrawable != null) {
                    avatarDrawable.setAvatarType(1);
                }
                if (imageReceiver == null) {
                    return string2;
                }
                imageReceiver.setForUserOrChat(null, avatarDrawable);
                return string2;
            }
            str = UserObject.getUserName(tLRPC$User);
            if (avatarDrawable != null) {
                avatarDrawable.setInfo(tLRPC$User);
            }
            if (imageReceiver != null) {
                imageReceiver.setForUserOrChat(tLObject, avatarDrawable);
            }
        } else {
            if (!(tLObject instanceof TLRPC$Chat)) {
                return "";
            }
            TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject;
            str = tLRPC$Chat.title;
            if (avatarDrawable != null) {
                avatarDrawable.setInfo(tLRPC$Chat);
            }
            if (imageReceiver != null) {
                imageReceiver.setForUserOrChat(tLObject, avatarDrawable);
            }
        }
        return str;
    }

    public static String setDialogPhotoTitle(BackupImageView backupImageView, TLObject tLObject) {
        if (backupImageView != null) {
            return setDialogPhotoTitle(backupImageView.getImageReceiver(), backupImageView.getAvatarDrawable(), tLObject);
        }
        return setDialogPhotoTitle(null, null, tLObject);
    }

    public static String getPublicUsername(TLObject tLObject) {
        if (tLObject instanceof TLRPC$Chat) {
            return ChatObject.getPublicUsername((TLRPC$Chat) tLObject);
        }
        if (tLObject instanceof TLRPC$User) {
            return UserObject.getPublicUsername((TLRPC$User) tLObject);
        }
        return null;
    }

    public static long getEmojiStatusDocumentId(TLRPC$EmojiStatus tLRPC$EmojiStatus) {
        if (tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatus) {
            return ((TLRPC$TL_emojiStatus) tLRPC$EmojiStatus).document_id;
        }
        if (!(tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatusUntil)) {
            return 0L;
        }
        TLRPC$TL_emojiStatusUntil tLRPC$TL_emojiStatusUntil = (TLRPC$TL_emojiStatusUntil) tLRPC$EmojiStatus;
        if (tLRPC$TL_emojiStatusUntil.until > ((int) (System.currentTimeMillis() / 1000))) {
            return tLRPC$TL_emojiStatusUntil.document_id;
        }
        return 0L;
    }

    public static int getEmojiStatusUntil(TLRPC$EmojiStatus tLRPC$EmojiStatus) {
        if (!(tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatusUntil)) {
            return 0;
        }
        TLRPC$TL_emojiStatusUntil tLRPC$TL_emojiStatusUntil = (TLRPC$TL_emojiStatusUntil) tLRPC$EmojiStatus;
        if (tLRPC$TL_emojiStatusUntil.until > ((int) (System.currentTimeMillis() / 1000))) {
            return tLRPC$TL_emojiStatusUntil.until;
        }
        return 0;
    }

    public static boolean emojiStatusesEqual(TLRPC$EmojiStatus tLRPC$EmojiStatus, TLRPC$EmojiStatus tLRPC$EmojiStatus2) {
        return getEmojiStatusDocumentId(tLRPC$EmojiStatus) == getEmojiStatusDocumentId(tLRPC$EmojiStatus2) && getEmojiStatusUntil(tLRPC$EmojiStatus) == getEmojiStatusUntil(tLRPC$EmojiStatus2);
    }

    public static TLRPC$TL_username findUsername(String str, TLRPC$User tLRPC$User) {
        if (tLRPC$User == null) {
            return null;
        }
        return findUsername(str, tLRPC$User.usernames);
    }

    public static TLRPC$TL_username findUsername(String str, TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$Chat == null) {
            return null;
        }
        return findUsername(str, tLRPC$Chat.usernames);
    }

    public static TLRPC$TL_username findUsername(String str, ArrayList<TLRPC$TL_username> arrayList) {
        if (arrayList == null) {
            return null;
        }
        Iterator<TLRPC$TL_username> it = arrayList.iterator();
        while (it.hasNext()) {
            TLRPC$TL_username next = it.next();
            if (next != null && TextUtils.equals(next.username, str)) {
                return next;
            }
        }
        return null;
    }
}
