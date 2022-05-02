package org.telegram.tgnet;

public class TLRPC$TL_decryptedMessageMediaVideo extends TLRPC$DecryptedMessageMedia {
    public static int constructor = -1760785394;
    public byte[] thumb;

    @Override
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.thumb = abstractSerializedData.readByteArray(z);
        this.thumb_w = abstractSerializedData.readInt32(z);
        this.thumb_h = abstractSerializedData.readInt32(z);
        this.duration = abstractSerializedData.readInt32(z);
        this.mime_type = abstractSerializedData.readString(z);
        this.f848w = abstractSerializedData.readInt32(z);
        this.f845h = abstractSerializedData.readInt32(z);
        this.size = abstractSerializedData.readInt32(z);
        this.key = abstractSerializedData.readByteArray(z);
        this.f847iv = abstractSerializedData.readByteArray(z);
        this.caption = abstractSerializedData.readString(z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeByteArray(this.thumb);
        abstractSerializedData.writeInt32(this.thumb_w);
        abstractSerializedData.writeInt32(this.thumb_h);
        abstractSerializedData.writeInt32(this.duration);
        abstractSerializedData.writeString(this.mime_type);
        abstractSerializedData.writeInt32(this.f848w);
        abstractSerializedData.writeInt32(this.f845h);
        abstractSerializedData.writeInt32(this.size);
        abstractSerializedData.writeByteArray(this.key);
        abstractSerializedData.writeByteArray(this.f847iv);
        abstractSerializedData.writeString(this.caption);
    }
}
