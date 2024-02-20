package org.telegram.tgnet;
public class TLRPC$TL_account_resetAuthorization extends TLObject {
    public long hash;

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(-545786948);
        abstractSerializedData.writeInt64(this.hash);
    }
}
