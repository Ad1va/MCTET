package com.easytier;

/**
 * Java wrapper for the EasyTier FFI library.
 * <p>
 * All native methods operate on a global EasyTier runtime managed by the
 * underlying Rust library. Thread-safety is handled natively.
 */
public final class EasyTier {

    static {
        System.loadLibrary("easytier_jni");
    }

    private EasyTier() {}

    /**
     * Validate a TOML config string.
     *
     * @param cfgStr TOML configuration
     * @return 0 on success, -1 on error
     */
    public static int parseConfig(String cfgStr) {
        return nativeParseConfig(cfgStr);
    }

    /**
     * Start a network instance with the given TOML config.
     *
     * @param cfgStr TOML configuration (must contain inst_name)
     * @return 0 on success, -1 on error
     */
    public static int runNetworkInstance(String cfgStr) {
        return nativeRunNetworkInstance(cfgStr);
    }

    /**
     * Retain only the listed network instances; stop all others.
     * Pass null or empty array to stop all.
     *
     * @param instNames instance names to keep running
     * @return 0 on success, -1 on error
     */
    public static int retainNetworkInstance(String[] instNames) {
        return nativeRetainNetworkInstance(instNames);
    }

    /**
     * Collect info from all running network instances.
     *
     * @return array of [key, value] pairs where key=inst_name, value=JSON info
     */
    public static String[][] collectNetworkInfos() {
        return nativeCollectNetworkInfos();
    }

    /**
     * Set the TUN file descriptor for an instance (Android VPN).
     *
     * @param instName instance name
     * @param fd       file descriptor
     * @return 0 on success, -1 on error
     */
    public static int setTunFd(String instName, int fd) {
        return nativeSetTunFd(instName, fd);
    }

    /**
     * @return last error message, or null if no error
     */
    public static String getErrorMsg() {
        return nativeGetErrorMsg();
    }

    /* ---- native methods ---- */
    private static native int nativeParseConfig(String cfgStr);
    private static native int nativeRunNetworkInstance(String cfgStr);
    private static native int nativeRetainNetworkInstance(String[] instNames);
    private static native String[][] nativeCollectNetworkInfos();
    private static native int nativeSetTunFd(String instName, int fd);
    private static native String nativeGetErrorMsg();
}
