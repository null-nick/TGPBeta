package org.webrtc;

import org.webrtc.PeerConnection;

public class RtcCertificatePem {
    private static final long DEFAULT_EXPIRY = 2592000;
    public final String certificate;
    public final String privateKey;

    public RtcCertificatePem(String str, String str2) {
        this.privateKey = str;
        this.certificate = str2;
    }

    public static RtcCertificatePem generateCertificate() {
        return nativeGenerateCertificate(PeerConnection.KeyType.ECDSA, 2592000L);
    }

    public static RtcCertificatePem generateCertificate(long j) {
        return nativeGenerateCertificate(PeerConnection.KeyType.ECDSA, j);
    }

    public static RtcCertificatePem generateCertificate(PeerConnection.KeyType keyType) {
        return nativeGenerateCertificate(keyType, 2592000L);
    }

    public static RtcCertificatePem generateCertificate(PeerConnection.KeyType keyType, long j) {
        return nativeGenerateCertificate(keyType, j);
    }

    private static native RtcCertificatePem nativeGenerateCertificate(PeerConnection.KeyType keyType, long j);

    String getCertificate() {
        return this.certificate;
    }

    String getPrivateKey() {
        return this.privateKey;
    }
}
