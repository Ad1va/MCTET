# MCT EasyTier Core AAR Builder

这个仓库用于通过 GitHub Actions 构建 EasyTier Android AAR。当前方案使用 EasyTier v2.6.4 源码内置的官方 Android JNI crate：`easytier-contrib/easytier-android-jni`。

## 构建产物

Actions 完成后会生成：

- `easytier-aar`：最终 Android AAR 包
- `easytier-android-jni-libs`：各 ABI 的 `libeasytier_android_jni.so` 压缩包

AAR 文件名格式：

```text
easytier-releases-v2.6.4-MCTDev.aar
```

## 支持架构

AAR 内包含以下 ABI 的原生库：

```text
arm64-v8a/libeasytier_android_jni.so
armeabi-v7a/libeasytier_android_jni.so
x86/libeasytier_android_jni.so
x86_64/libeasytier_android_jni.so
```

因此一个 AAR 可用于主流 Android 真机和模拟器。Android 会根据设备 CPU 自动选择对应的 `.so`。

## GitHub Actions 触发方式

 workflow 文件位于：

```text
.github/workflows/android.yml
```

触发方式：

- push 到 `main` 分支自动触发
- Actions 页面手动点击 `Run workflow` 触发

## 构建配置

主要配置在 `BUILD_CONFIG.env`：

```ini
REPO_URL=https://github.com/EasyTier/EasyTier.git
ET_REF=releases/v2.6.4
NDK_VERSION=r27
RUST_TARGETS=aarch64-linux-android,armv7-linux-androideabi,i686-linux-android,x86_64-linux-android
CARGO_PROFILE=release
ABIS=arm64-v8a,armeabi-v7a,x86,x86_64
JNI_MANIFEST=third_party/easytier/easytier-contrib/easytier-android-jni/Cargo.toml
DEVELOPER=MCTDev
```

## Android 项目引用方式

把构建出的 `.aar` 放到 Android 项目的 `app/libs/` 目录：

```text
app/libs/easytier-releases-v2.6.4-MCTDev.aar
```

在 app 模块的 `build.gradle` 或 `build.gradle.kts` 中引用。

### Kotlin DSL

```kotlin
dependencies {
    implementation(files("libs/easytier-releases-v2.6.4-MCTDev.aar"))
}
```

### Groovy DSL

```groovy
dependencies {
    implementation files('libs/easytier-releases-v2.6.4-MCTDev.aar')
}
```

## Java 调用方式

对外推荐使用包名：

```java
import MCT.EasyTier.Core.EasyTier;
```

示例：

```java
String config = """
inst_name = "mct_node"
network = "mct_network"
""";

int parseResult = EasyTier.parseConfig(config);
if (parseResult != 0) {
    throw new RuntimeException(EasyTier.getErrorMsg());
}

int runResult = EasyTier.runNetworkInstance(config);
if (runResult != 0) {
    throw new RuntimeException(EasyTier.getErrorMsg());
}

String infosJson = EasyTier.collectNetworkInfos();
```

## VPN TUN FD 使用

如果在 Android VPNService 中使用，需要建立 VPN interface 后把 fd 传给 EasyTier：

```java
int result = EasyTier.setTunFd("mct_node", parcelFileDescriptor.getFd());
if (result != 0) {
    throw new RuntimeException(EasyTier.getErrorMsg());
}
```

## Manifest 权限

宿主 App 通常需要至少添加：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
```

如果使用 VPNService，还需要在宿主 App 中声明自己的 `VpnService`。

## 注意事项

- AAR 使用官方 `libeasytier_android_jni.so`，不是手写 C JNI 静态链接方案。
- 内部仍保留 `com.easytier.jni.EasyTierJNI`，这是因为官方 Rust JNI 导出的 native symbol 固定匹配该包名。
- 对外使用时优先引用 `MCT.EasyTier.Core.EasyTier`。
- EasyTier v2.6.4 的 Rust toolchain 要求较高，CI 会在 EasyTier 源码目录内安装 Android Rust targets，避免 `can't find crate for core`。
- AAR 是胖包，包含四个 ABI，兼容性好但体积会更大。
- 如果宿主项目只面向 `arm64-v8a`，可在宿主 App 的 `ndk.abiFilters` 中限制 ABI 来减小 APK/AAB 体积。
- 不要把密钥、token、服务器密码写进 `BUILD_CONFIG.env`，该文件会进入公开仓库。

## 产物下载

构建完成后进入：

```text
GitHub → Actions → Android AAR → 对应 run → Artifacts
```

下载 `easytier-aar` 即可。
