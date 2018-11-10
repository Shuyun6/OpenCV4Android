cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_VERBOSE_MAKEFILE on)
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++14 -fexceptions -frtti")
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
        jnigraphics
)