package com.easytier.jni;

public final class DataPlaneTcpAcceptResult {
    public final long handle;
    public final DataPlaneSocketAddress localAddress;
    public final DataPlaneSocketAddress peerAddress;

    public DataPlaneTcpAcceptResult(long handle, DataPlaneSocketAddress localAddress, DataPlaneSocketAddress peerAddress) {
        this.handle = handle;
        this.localAddress = localAddress;
        this.peerAddress = peerAddress;
    }
}
