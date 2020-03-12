# Camera - docs/core 

​	从 Android 5.0 开始，Google 引入了一套全新的相机框架 Camera2，旧的被废弃，本项目主要为Camera2 的重要概念和使用方法的说明文档。

## 核心概念&API

### 一、数据流通方式

​	Camera2引用了pipeline的概念，将安卓设备和摄像头之间连接起来。系统向摄像头发送 Capture 请求，而摄像头会返回 CameraMetadata。这一切建立在一个叫作 CameraCaptureSession 的会话中。

​	Camera2数据流通方式的架构图如下：

![pipeline](../assets/pipeline.png )

​	一个新的 CaptureRequest 会被放入一个被称作 Pending Request Queue 的队列中等待被执行，当 In-Flight Capture Queue 队列空闲的时候就会从 Pending Request Queue 获取若干个待处理的 CaptureRequest，并且根据每一个 CaptureRequest 的配置进行 Capture 操作。最后，我们从不同尺寸的 Surface 中获取图片数据并且还会得到一个包含了很多与本次拍照相关的信息的 CaptureResult。

​	核心概念的具体内容将在下文介绍。

### 二、核心概念

##### 1. Pipeline

​	Camera2 的 API 模型被设计成一个 Pipeline，它按顺序处理每一帧的请求并返回请求结果给客户端。 

##### 2. Supported Hardware Level

​	相机的所有操作和参数配置最终都是服务于图像捕获，例如对焦是为了让某一个区域的图像更加清晰，调节曝光补偿是为了调节图像的亮度。 

##### 3. Capture

​	相机的所有操作和参数配置最终都是服务于图像捕获。

##### 5. CameraManager

​	CameraManager 是一个负责查询和建立相机连接的系统服务。

##### 5. CameraCharacteristics

​	CameraCharacteristics 是一个只读的相机信息提供者，其内部携带大量的相机信息。

##### 6. CameraDevice

​	CameraDevice 代表当前连接的相机设备。

##### 7. Surface

​	Surface 是一块用于填充图像数据的内存空间。

##### 8. CameraCaptureSession

​	CameraCaptureSession 实际上就是配置了目标 Surface 的 Pipeline 实例，我们在使用相机功能之前必须先创建 CameraCaptureSession 实例。

##### 9. CaptureRequest

​	CaptureRequest 是向 CameraCaptureSession 提交 Capture 请求时的信息载体，其内部包括了本次 Capture 的参数配置和接收图像数据的 Surface。

##### 10. CaptureResult

​	CaptureResult 是每一次 Capture 操作的结果，里面包括了很多状态信息，包括闪光灯状态、对焦状态、时间戳等等。

##### 11. 其他 Camera2 提供的高级特性

​	123
