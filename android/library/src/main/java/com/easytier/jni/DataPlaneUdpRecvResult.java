package com.easytier.jni;

public final class DataPlaneUdpRecvResult {
    public final byte[] data;
    public final DataPlaneSocketAddress peerAddress;

    public DataPlaneUdpRecvResult(byte[] data, DataPlaneSocketAddress peerAddress) {
        this.data = data;
        this.peerAddress = peerAddress;
    }
}
