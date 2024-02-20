package org.telegram.tgnet;
public class TLRPC$TL_account_setAccountTTL extends TLObject {
    public TLRPC$TL_accountDaysTTL ttl;

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(608323678);
        this.ttl.serializeToStream(abstractSerializedData);
    }
}
