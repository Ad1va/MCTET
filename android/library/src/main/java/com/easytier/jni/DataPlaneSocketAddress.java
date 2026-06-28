package com.easytier.jni;

public final class DataPlaneSocketAddress {
    public final String ip;
    public final int port;

    public DataPlaneSocketAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
