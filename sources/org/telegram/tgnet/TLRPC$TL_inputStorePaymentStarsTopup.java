package org.telegram.tgnet;
public class TLRPC$TL_inputStorePaymentStarsTopup extends TLRPC$InputStorePaymentPurpose {
    public long amount;
    public String currency;
    public long stars;

    @Override
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.stars = abstractSerializedData.readInt64(z);
        this.currency = abstractSerializedData.readString(z);
        this.amount = abstractSerializedData.readInt64(z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(-572715178);
        abstractSerializedData.writeInt64(this.stars);
        abstractSerializedData.writeString(this.currency);
        abstractSerializedData.writeInt64(this.amount);
    }
}