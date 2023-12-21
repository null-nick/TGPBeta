package org.telegram.tgnet;
public class TLRPC$TL_channelAdminLogEventActionChangeColor extends TLRPC$ChannelAdminLogEventAction {
    public int new_value;
    public int prev_value;

    @Override
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.prev_value = abstractSerializedData.readInt32(z);
        this.new_value = abstractSerializedData.readInt32(z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(1009460347);
        abstractSerializedData.writeInt32(this.prev_value);
        abstractSerializedData.writeInt32(this.new_value);
    }
}