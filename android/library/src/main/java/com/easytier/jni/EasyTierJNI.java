package com.easytier.jni;

public final class EasyTierJNI {
    static {
        System.loadLibrary("easytier_ffi");
        System.loadLibrary("easytier_android_jni");
    }

    private EasyTierJNI() {}

    public static native int setTunFd(String instanceName, int fd);

    public static native int parseConfig(String config);

    public static native int runNetworkInstance(String config);

    public static native int startConfigServerClient(
            String url,
            String hostname,
            String machineId,
            boolean secureMode,
            ConfigServerEventCallback callback
    );

    public static native int stopConfigServerClient();

    public static native boolean isConfigServerClientConnected();

    public static native int retainNetworkInstance(String[] instanceNames);

    public static native String collectNetworkInfos(int maxLength);

    public static native String listInstances(int maxLength);

    public static native String callJsonRpc(
            String serviceName,
            String methodName,
            String domainName,
            String payloadJson
    );

    public static String callJsonRpc(String serviceName, String methodName, String payloadJson) {
        return callJsonRpc(serviceName, methodName, null, payloadJson);
    }

    public static native String getLastError();

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

    public static int stopAllInstances() {
        return retainNetworkInstance(null);
    }

    public static int retainSingleInstance(String instanceName) {
        return retainNetworkInstance(new String[]{instanceName});
    }
}
