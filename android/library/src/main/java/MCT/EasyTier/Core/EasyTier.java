package MCT.EasyTier.Core;

import com.easytier.jni.EasyTierJNI;

public final class EasyTier {
    private static final int DEFAULT_MAX_INFOS = 128;

    private EasyTier() {}

    public static int parseConfig(String cfgStr) {
        return EasyTierJNI.parseConfig(cfgStr);
    }

    public static int runNetworkInstance(String cfgStr) {
        return EasyTierJNI.runNetworkInstance(cfgStr);
    }

    public static int retainNetworkInstance(String[] instNames) {
        return EasyTierJNI.retainNetworkInstance(instNames);
    }

    public static String collectNetworkInfos() {
        return EasyTierJNI.collectNetworkInfos(DEFAULT_MAX_INFOS);
    }

    public static String listInstances() {
        return EasyTierJNI.listInstances(DEFAULT_MAX_INFOS);
    }

    public static int setTunFd(String instName, int fd) {
        return EasyTierJNI.setTunFd(instName, fd);
    }

    public static String callJsonRpc(String serviceName, String methodName, String payloadJson) {
        return EasyTierJNI.callJsonRpc(serviceName, methodName, payloadJson);
    }

    public static String callJsonRpc(String serviceName, String methodName, String domainName, String payloadJson) {
        return EasyTierJNI.callJsonRpc(serviceName, methodName, domainName, payloadJson);
    }

    public static String getErrorMsg() {
        return EasyTierJNI.getLastError();
    }

    public static int stopAllInstances() {
        return EasyTierJNI.stopAllInstances();
    }
}
