package com.easytier.jni;

public final class DataPlaneTcpConnectResult {
    public final long handle;
    public final DataPlaneSocketAddress localAddress;

    public DataPlaneTcpConnectResult(long handle, DataPlaneSocketAddress localAddress) {
        this.handle = handle;
        this.localAddress = localAddress;
    }
}
