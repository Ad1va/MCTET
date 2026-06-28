package com.easytier.jni;

public final class EasyTierDataPlaneJNI {
    static {
        System.loadLibrary("easytier_ffi");
        System.loadLibrary("easytier_android_jni");
    }

    private EasyTierDataPlaneJNI() {}

    public static native int dataPlaneAsyncOpStatus(long handle);

    public static native int dataPlaneAsyncOpWait(long handle, long timeoutMs);

    public static native int dataPlaneAsyncOpCancel(long handle);

    public static native int dataPlaneAsyncOpFree(long handle);

    public static native long dataPlaneTcpConnectStart(String instanceName, String dstIp, int dstPort, long timeoutMs);

    public static native DataPlaneTcpConnectResult dataPlaneTcpConnectFinish(long op);

    public static native long dataPlaneTcpBindStart(String instanceName, int localPort, long timeoutMs);

    public static native DataPlaneTcpBindResult dataPlaneTcpBindFinish(long op);

    public static native long dataPlaneTcpAcceptStart(long handle, long timeoutMs);

    public static native DataPlaneTcpAcceptResult dataPlaneTcpAcceptFinish(long op);

    public static native long dataPlaneTcpReadStart(long handle, int maxLength, long timeoutMs);

    public static native DataPlaneTcpReadResult dataPlaneTcpReadFinish(long op);

    public static native long dataPlaneTcpWriteStart(long handle, byte[] data, long timeoutMs);

    public static native int dataPlaneTcpWriteFinish(long op);

    public static native long dataPlaneUdpBindStart(String instanceName, int localPort, long timeoutMs);

    public static native DataPlaneUdpBindResult dataPlaneUdpBindFinish(long op);

    public static native long dataPlaneUdpSendToStart(long handle, String dstIp, int dstPort, byte[] data, long timeoutMs);

    public static native int dataPlaneUdpSendToFinish(long op);

    public static native long dataPlaneUdpRecvFromStart(long handle, int maxLength, long timeoutMs);

    public static native DataPlaneUdpRecvResult dataPlaneUdpRecvFromFinish(long op);

    public static native int dataPlaneTcpClose(long handle);

    public static native int dataPlaneTcpListenerClose(long handle);

    public static native int dataPlaneUdpClose(long handle);
}
