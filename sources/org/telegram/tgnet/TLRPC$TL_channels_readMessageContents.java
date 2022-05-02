package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_channels_readMessageContents extends TLObject {
    public static int constructor = -357180360;
    public TLRPC$InputChannel channel;
    public ArrayList<Integer> f895id = new ArrayList<>();

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.channel.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(481674261);
        int size = this.f895id.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeInt32(this.f895id.get(i).intValue());
        }
    }
}
