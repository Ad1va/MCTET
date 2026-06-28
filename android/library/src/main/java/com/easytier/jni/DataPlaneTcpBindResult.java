package com.easytier.jni;

public final class DataPlaneTcpBindResult {
    public final long handle;
    public final DataPlaneSocketAddress localAddress;

    public DataPlaneTcpBindResult(long handle, DataPlaneSocketAddress localAddress) {
        this.handle = handle;
        this.localAddress = localAddress;
    }
}
