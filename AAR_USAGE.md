# MCT EasyTier Core AAR 引用说明书

本文说明如何在 Android App 中引用 `easytier-releases-v2.6.4-MCTDev.aar`。

## 1. 下载哪个文件

GitHub Actions 构建完成后会出现多个 Artifacts：

| Artifact 名称 | 用途 | 是否直接给 App 引用 |
|---|---|---|
| `easytier-aar` | 完整 AAR 包 | 是 |
| `easytier-android-jni-libs` | 四个 ABI 的 `.so` 压缩包 | 否 |
| `arm64-v8a` | 单 ABI `.so` | 否 |
| `armeabi-v7a` | 单 ABI `.so` | 否 |
| `x86` | 单 ABI `.so` | 否 |
| `x86_64` | 单 ABI `.so` | 否 |

你需要下载：

```text
easytier-aar
```

下载后解压，里面会有真正可引用的 AAR 文件：

```text
easytier-releases-v2.6.4-MCTDev.aar
```

## 2. AAR 放置位置

把 AAR 文件放到宿主 App 的 `app/libs/` 目录：

```text
你的项目/
└── app/
    └── libs/
        └── easytier-releases-v2.6.4-MCTDev.aar
```

如果 `libs` 目录不存在，手动创建即可。

## 3. Gradle 引用方式

### Kotlin DSL：`build.gradle.kts`

在 app 模块的 `build.gradle.kts` 中添加：

```kotlin
dependencies {
    implementation(files("libs/easytier-releases-v2.6.4-MCTDev.aar"))
}
```

如果你的项目没有自动识别 `libs` 目录，也可以显式添加 flatDir：

```kotlin
repositories {
    flatDir {
        dirs("libs")
    }
}

dependencies {
    implementation(files("libs/easytier-releases-v2.6.4-MCTDev.aar"))
}
```

### Groovy DSL：`build.gradle`

在 app 模块的 `build.gradle` 中添加：

```groovy
dependencies {
    implementation files('libs/easytier-releases-v2.6.4-MCTDev.aar')
}
```

如果你的项目没有自动识别 `libs` 目录，也可以显式添加 flatDir：

```groovy
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    implementation files('libs/easytier-releases-v2.6.4-MCTDev.aar')
}
```

## 4. 最低 SDK 要求

当前 AAR 的最低 SDK 是：

```text
minSdk 24
```

宿主 App 的 `minSdk` 不能低于 24。

## 5. 支持的 CPU 架构

AAR 内置以下四个 ABI：

```text
arm64-v8a
armeabi-v7a
x86
x86_64
```

一个 AAR 可以同时用于真机和模拟器。Android 安装或运行时会自动选择对应架构的 `libeasytier_android_jni.so`。

## 6. Manifest 权限

宿主 App 至少需要添加网络权限：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
```

如果你要接入 Android VPN，还需要在宿主 App 中声明自己的 `VpnService`，示例：

```xml
<service
    android:name=".YourVpnService"
    android:permission="android.permission.BIND_VPN_SERVICE"
    android:exported="false">
    <intent-filter>
        <action android:name="android.net.VpnService" />
    </intent-filter>
</service>
```

## 7. Java 调用方式

对外推荐使用这个包名：

```java
import MCT.EasyTier.Core.EasyTier;
```

### 解析配置

```java
String config = """
inst_name = "mct_node"
network = "mct_network"
""";

int result = EasyTier.parseConfig(config);
if (result != 0) {
    throw new RuntimeException(EasyTier.getErrorMsg());
}
```

### 启动 EasyTier 实例

```java
int result = EasyTier.runNetworkInstance(config);
if (result != 0) {
    throw new RuntimeException(EasyTier.getErrorMsg());
}
```

### 获取实例信息

```java
String infosJson = EasyTier.collectNetworkInfos();
```

返回值是 JSON 字符串，宿主 App 可自行用 Gson、Moshi、Jackson 或 org.json 解析。

### 停止所有实例

```java
int result = EasyTier.stopAllInstances();
if (result != 0) {
    throw new RuntimeException(EasyTier.getErrorMsg());
}
```

### 保留指定实例

```java
int result = EasyTier.retainNetworkInstance(new String[]{"mct_node"});
if (result != 0) {
    throw new RuntimeException(EasyTier.getErrorMsg());
}
```

## 8. Kotlin 调用方式

```kotlin
import MCT.EasyTier.Core.EasyTier

val config = """
inst_name = "mct_node"
network = "mct_network"
""".trimIndent()

val result = EasyTier.runNetworkInstance(config)
if (result != 0) {
    error(EasyTier.getErrorMsg() ?: "EasyTier 启动失败")
}
```

## 9. VPN TUN FD 接入

如果宿主 App 使用 Android `VpnService`，在建立 VPN interface 后，把 fd 交给 EasyTier：

```java
ParcelFileDescriptor tun = builder.establish();
if (tun != null) {
    int result = EasyTier.setTunFd("mct_node", tun.getFd());
    if (result != 0) {
        throw new RuntimeException(EasyTier.getErrorMsg());
    }
}
```

注意：`setTunFd` 的实例名必须和配置中的 `inst_name` 一致。

## 10. JSON RPC 调用

如果需要调用 EasyTier 暴露的 JSON RPC：

```java
String response = EasyTier.callJsonRpc(
    "api.logger.LoggerRpcService",
    "get_logger_config",
    "{}"
);
```

如果 RPC 需要 domainName，可以使用四参数版本：

```java
String response = EasyTier.callJsonRpc(
    "service.name",
    "method_name",
    "tcp",
    "{}"
);
```

## 11. 包名说明

对外推荐使用：

```text
MCT.EasyTier.Core.EasyTier
```

AAR 内部仍包含：

```text
com.easytier.jni.EasyTierJNI
com.easytier.jni.EasyTierDataPlaneJNI
```

这是因为 EasyTier 官方 Rust JNI 动态库导出的 native symbol 固定绑定 `com.easytier.jni.*`，不能直接删除。普通 App 代码不需要直接引用这些内部类。

## 12. 常见问题

### 12.1 应该下载哪个 Artifact？

下载 `easytier-aar`。解压后使用里面的 `.aar` 文件。

### 12.2 可以只下载 arm64-v8a 吗？

不建议。`arm64-v8a` artifact 只是单独的 `.so`，不是 AAR。除非你打算自己手动集成 JNI so，否则直接下载 `easytier-aar`。

### 12.3 AAR 为什么这么大？

因为它内置四个 ABI 的 native `.so`，属于胖包。优点是兼容真机和模拟器，缺点是体积更大。

### 12.4 如何减小 APK 体积？

如果只面向 64 位 ARM 真机，可在宿主 App 中限制 ABI：

```kotlin
android {
    defaultConfig {
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }
}
```

Groovy：

```groovy
android {
    defaultConfig {
        ndk {
            abiFilters 'arm64-v8a'
        }
    }
}
```

### 12.5 报 `java.lang.UnsatisfiedLinkError` 怎么办？

检查：

- AAR 是否已正确放入 `app/libs/`
- Gradle 是否正确引用 AAR
- APK 内是否存在 `lib/<abi>/libeasytier_android_jni.so`
- 宿主 App 是否过滤掉了当前设备 ABI

### 12.6 报找不到 `MCT.EasyTier.Core.EasyTier` 怎么办？

检查：

- 是否引用的是最新构建的 AAR
- 是否下载的是 `easytier-aar`，而不是单独 ABI artifact
- Gradle Sync 是否成功

## 13. 推荐接入流程

1. 下载 `easytier-aar`
2. 解压得到 `easytier-releases-v2.6.4-MCTDev.aar`
3. 放到宿主 App 的 `app/libs/`
4. 在 app 模块 Gradle 中添加 `implementation(files(...))`
5. 在 Manifest 中添加网络权限
6. 使用 `MCT.EasyTier.Core.EasyTier` 调用
7. 真机运行验证
