package org.webrtc;

import java.util.Map;

public class RTCStats {
    private final String f1115id;
    private final Map<String, Object> members;
    private final long timestampUs;
    private final String type;

    public RTCStats(long j, String str, String str2, Map<String, Object> map) {
        this.timestampUs = j;
        this.type = str;
        this.f1115id = str2;
        this.members = map;
    }

    public double getTimestampUs() {
        return this.timestampUs;
    }

    public String getType() {
        return this.type;
    }

    public String getId() {
        return this.f1115id;
    }

    public Map<String, Object> getMembers() {
        return this.members;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ timestampUs: ");
        sb.append(this.timestampUs);
        sb.append(", type: ");
        sb.append(this.type);
        sb.append(", id: ");
        sb.append(this.f1115id);
        for (Map.Entry<String, Object> entry : this.members.entrySet()) {
            sb.append(", ");
            sb.append(entry.getKey());
            sb.append(": ");
            appendValue(sb, entry.getValue());
        }
        sb.append(" }");
        return sb.toString();
    }

    private static void appendValue(StringBuilder sb, Object obj) {
        if (obj instanceof Object[]) {
            Object[] objArr = (Object[]) obj;
            sb.append('[');
            for (int i = 0; i < objArr.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                appendValue(sb, objArr[i]);
            }
            sb.append(']');
        } else if (obj instanceof String) {
            sb.append('\"');
            sb.append(obj);
            sb.append('\"');
        } else {
            sb.append(obj);
        }
    }

    @CalledByNative
    static RTCStats create(long j, String str, String str2, Map map) {
        return new RTCStats(j, str, str2, map);
    }
}
