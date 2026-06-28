package com.easytier.jni;

public final class DataPlaneUdpBindResult {
    public final long handle;
    public final DataPlaneSocketAddress localAddress;

    public DataPlaneUdpBindResult(long handle, DataPlaneSocketAddress localAddress) {
        this.handle = handle;
        this.localAddress = localAddress;
    }
}
