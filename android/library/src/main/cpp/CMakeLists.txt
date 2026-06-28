cmake_minimum_required(VERSION 3.22.1)
project("easytier_jni")

# Locate the pre-built static library per ABI.
# The CI copies the .a into ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libeasytier_ffi.a
set(FFI_LIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}")

add_library(easytier_ffi STATIC IMPORTED)
set_target_properties(easytier_ffi PROPERTIES
    IMPORTED_LOCATION "${FFI_LIB_DIR}/libeasytier_ffi.a"
)

add_library(easytier_jni SHARED easytier_jni.c)

target_include_directories(easytier_jni PRIVATE
    "${CMAKE_CURRENT_SOURCE_DIR}/include"
)

target_link_libraries(easytier_jni
    easytier_ffi
    log
    android
    m
    dl
)
