package com.jobtrackr.dto.emailDto;

public class TokenEntry {
    private final String token;
    private final long timestamp;

    public TokenEntry(String token, long timestamp) {
        this.token = token;
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
