package org.telegram.tgnet;

public class TLRPC$TL_stickers_changeStickerPosition extends TLObject {
    public int position;
    public TLRPC$InputDocument sticker;

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$messages_StickerSet.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(-4795190);
        this.sticker.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.position);
    }
}
