package com.easytier.jni;

public final class EasyTierDataPlaneJNI {
    private EasyTierDataPlaneJNI() {}

    public static int dataPlaneAsyncOpStatus(long handle) {
        return EasyTierJNI.dataPlaneAsyncOpStatus(handle);
    }

    public static int dataPlaneAsyncOpWait(long handle, long timeoutMs) {
        return EasyTierJNI.dataPlaneAsyncOpWait(handle, timeoutMs);
    }

    public static int dataPlaneAsyncOpCancel(long handle) {
        return EasyTierJNI.dataPlaneAsyncOpCancel(handle);
    }

    public static int dataPlaneAsyncOpFree(long handle) {
        return EasyTierJNI.dataPlaneAsyncOpFree(handle);
    }

    public static long dataPlaneTcpConnectStart(String instanceName, String dstIp, int dstPort, long timeoutMs) {
        return EasyTierJNI.dataPlaneTcpConnectStart(instanceName, dstIp, dstPort, timeoutMs);
    }

    public static DataPlaneTcpConnectResult dataPlaneTcpConnectFinish(long op) {
        return EasyTierJNI.dataPlaneTcpConnectFinish(op);
    }

    public static long dataPlaneTcpBindStart(String instanceName, int localPort, long timeoutMs) {
        return EasyTierJNI.dataPlaneTcpBindStart(instanceName, localPort, timeoutMs);
    }

    public static DataPlaneTcpBindResult dataPlaneTcpBindFinish(long op) {
        return EasyTierJNI.dataPlaneTcpBindFinish(op);
    }

    public static long dataPlaneTcpAcceptStart(long handle, long timeoutMs) {
        return EasyTierJNI.dataPlaneTcpAcceptStart(handle, timeoutMs);
    }

    public static DataPlaneTcpAcceptResult dataPlaneTcpAcceptFinish(long op) {
        return EasyTierJNI.dataPlaneTcpAcceptFinish(op);
    }

    public static long dataPlaneTcpReadStart(long handle, int maxLength, long timeoutMs) {
        return EasyTierJNI.dataPlaneTcpReadStart(handle, maxLength, timeoutMs);
    }

    public static DataPlaneTcpReadResult dataPlaneTcpReadFinish(long op) {
        return EasyTierJNI.dataPlaneTcpReadFinish(op);
    }

    public static long dataPlaneTcpWriteStart(long handle, byte[] data, long timeoutMs) {
        return EasyTierJNI.dataPlaneTcpWriteStart(handle, data, timeoutMs);
    }

    public static int dataPlaneTcpWriteFinish(long op) {
        return EasyTierJNI.dataPlaneTcpWriteFinish(op);
    }

    public static long dataPlaneUdpBindStart(String instanceName, int localPort, long timeoutMs) {
        return EasyTierJNI.dataPlaneUdpBindStart(instanceName, localPort, timeoutMs);
    }

    public static DataPlaneUdpBindResult dataPlaneUdpBindFinish(long op) {
        return EasyTierJNI.dataPlaneUdpBindFinish(op);
    }

    public static long dataPlaneUdpSendToStart(long handle, String dstIp, int dstPort, byte[] data, long timeoutMs) {
        return EasyTierJNI.dataPlaneUdpSendToStart(handle, dstIp, dstPort, data, timeoutMs);
    }

    public static int dataPlaneUdpSendToFinish(long op) {
        return EasyTierJNI.dataPlaneUdpSendToFinish(op);
    }

    public static long dataPlaneUdpRecvFromStart(long handle, int maxLength, long timeoutMs) {
        return EasyTierJNI.dataPlaneUdpRecvFromStart(handle, maxLength, timeoutMs);
    }

    public static DataPlaneUdpRecvResult dataPlaneUdpRecvFromFinish(long op) {
        return EasyTierJNI.dataPlaneUdpRecvFromFinish(op);
    }

    public static int dataPlaneTcpClose(long handle) {
        return EasyTierJNI.dataPlaneTcpClose(handle);
    }

    public static int dataPlaneTcpListenerClose(long handle) {
        return EasyTierJNI.dataPlaneTcpListenerClose(handle);
    }

    public static int dataPlaneUdpClose(long handle) {
        return EasyTierJNI.dataPlaneUdpClose(handle);
    }
}
