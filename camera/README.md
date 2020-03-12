# Camera 

从 Android 5.0 开始，Google 引入了一套全新的相机框架 Camera2，旧的被废弃。Camera2 的出现给相机应用程序带来了巨大的变革，因为它的目的是为了给应用层提供更多的相机控制权限，从而构建出更高质量的相机应用程序。 

本项目主要介绍一些 Camera2 的重要概念和使用方法。

## 团队成员：

- 林英琮 
- 汪雨薇 
- 徐国聪 
- 卢文昊 
- 冯博

## 项目结构

--- code  示例代码

--- docs  团队文档与技术说明文档

## 内容介绍

### 一、核心概念 & 主要API

##### 1. Pipeline

Camera2 的 API 模型被设计成一个 Pipeline，它按顺序处理每一帧的请求并返回请求结果给客户端。 

##### 2. Supported Hardware Level

相机的所有操作和参数配置最终都是服务于图像捕获，例如对焦是为了让某一个区域的图像更加清晰，调节曝光补偿是为了调节图像的亮度。 

##### 3. Capture

相机的所有操作和参数配置最终都是服务于图像捕获。

##### 5. CameraManager

CameraManager 是一个负责查询和建立相机连接的系统服务。

##### 5. CameraCharacteristics

CameraCharacteristics 是一个只读的相机信息提供者，其内部携带大量的相机信息。

##### 6. CameraDevice

CameraDevice 代表当前连接的相机设备。

##### 7. Surface

Surface 是一块用于填充图像数据的内存空间。

##### 8. CameraCaptureSession

CameraCaptureSession 实际上就是配置了目标 Surface 的 Pipeline 实例，我们在使用相机功能之前必须先创建 CameraCaptureSession 实例。

##### 9. CaptureRequest

CaptureRequest 是向 CameraCaptureSession 提交 Capture 请求时的信息载体，其内部包括了本次 Capture 的参数配置和接收图像数据的 Surface。

##### 10. CaptureResult

CaptureResult 是每一次 Capture 操作的结果，里面包括了很多状态信息，包括闪光灯状态、对焦状态、时间戳等等。

##### 11. 其他 Camera2 提供的高级特性 

123

### 二、基础内容 - 预览、拍照

##### 1. 相机预览

123

##### 2. 获取、处理预览帧数据

123

##### 3. 拍照

123

### 三、进阶内容 - 使用opengl

##### 1. 使用opengl进行相机预览

123

##### 2. 使用opengl实现滤镜

123

## 食用方法

1. clone至你的本地

```git
git clone https://github.com/ohhhyeahhh/learn_android.git
```

## 参考资料

<https://www.jianshu.com/p/9a2e66916fcb> 

<https://www.android-doc.com/guide/topics/media/camera.html> 

## 项目记录

3.12 git test + 编写项目计划和目录

3.13