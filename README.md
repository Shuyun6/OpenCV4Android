# OpenCV4Android
OpenCV for Android

## How to import OpenCV
1)  Download OpenCV SDK from [OpenCV release website](https://opencv.org/releases.html)
2)  (JAVA) If you just want to use OpenCV in Java code, you can 
  - Import module by File->New->Import Module
  - Select module from OpenCV-android-sdk\sdk\java
  - Create if not exist: app/src/main/jniLibs, then copy SHARED LIBRARY from OpenCV-android-sdk\sdk\native\libs
  - Invoke `OpenCVLoader.initDebug();` before you use any OpenCV java codes

3)  (Native) If you want to use OpenCV via Java JNI (C++), you can
  - Create if not exist: app/CMakeLists.txt
  - Write these in CMakeLists.txt(reference my file):
  
``` 
cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,--exclude-libs,libippicv.a -Wl,--exclude-libs,libippiw.a")

set( OpenCV_DIR F:/OpenCV-android-sdk/sdk/native/jni )
find_package(OpenCV REQUIRED)

add_library(
        native-lib
        SHARED
        src/main/cpp/native-lib.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
    native-lib
    ${OpenCV_LIBS}
    ${log-lib}
)
```
  - Build, then you can use OpenCV in C++ (reference native-lib.cpp)

Any problem in OpenCV project build, you can e-mail to me 
