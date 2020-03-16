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

Camera2 的 API 模型被设计成一个 Pipeline，它按顺序处理每一帧的请求并返回请求结果给客户端。 

##### 2. Supported Hardware Level

相机功能的强大与否和硬件息息相关，不同厂商对 Camera2 的支持程度也不同，所以 Camera2 定义了一个叫做 Supported Hardware Level 的重要概念，其作用是将不同设备上的 Camera2 根据功能的支持情况划分成多个不同级别以便开发者能够大概了解当前设备上 Camera2 的支持情况。截止到 Android P 为止，从低到高一共有 LEGACY、LIMITED、FULL 和 LEVEL_3 四个级别：

1.  **LEGACY**：向后兼容的级别，处于该级别的设备意味着它只支持 Camera1 的功能，不具备任何 Camera2 高级特性。
2.  **LIMITED**：除了支持 Camera1 的基础功能之外，还支持部分 Camera2 高级特性的级别。
3.  **FULL**：支持所有 Camera2 的高级特性。
4.  **LEVEL_3**：新增更多 Camera2 高级特性，例如 YUV 数据的后处理等。

##### 3. Capture

相机的所有操作和参数配置最终都是服务于图像捕获，例如对焦是为了让某一个区域的图像更加清晰，调节曝光补偿是为了调节图像的亮度。因此，在 Camera2 里面所有的相机操作和参数配置都被抽象成 Capture（捕获），所以不要简单的把 Capture 直接理解成是拍照，因为 Capture 操作可能仅仅是为了让预览画面更清晰而进行对焦而已。如果你熟悉 Camera1，那你可能会问 `setFlashMode()` 在哪？`setFocusMode()` 在哪？`takePicture()` 在哪？告诉你，它们都是通过 Capture 来实现的。

Capture 从执行方式上又被细分为【单次模式】、【多次模式】和【重复模式】三种，我们来一一解释下：

- **单次模式（One-shot）**：指的是只执行一次的 Capture 操作，例如设置闪光灯模式、对焦模式和拍一张照片等。多个一次性模式的 Capture 会进入队列按顺序执行。
- **多次模式（Burst）**：指的是连续多次执行指定的 Capture 操作，该模式和多次执行单次模式的最大区别是连续多次 Capture 期间不允许插入其他任何 Capture 操作，例如连续拍摄 100 张照片，在拍摄这 100 张照片期间任何新的 Capture 请求都会排队等待，直到拍完 100 张照片。多组多次模式的 Capture 会进入队列按顺序执行。
- **重复模式（Repeating）**：指的是不断重复执行指定的 Capture 操作，当有其他模式的 Capture 提交时会暂停该模式，转而执行其他被模式的 Capture，当其他模式的 Capture 执行完毕后又会自动恢复继续执行该模式的 Capture，例如显示预览画面就是不断 Capture 获取每一帧画面。该模式的 Capture 是全局唯一的，也就是新提交的重复模式 Capture 会覆盖旧的重复模式 Capture。

##### 4. CameraManager

CameraManager 是一个负责查询和建立相机连接的系统服务，它的功能不多，这里列出几个 CameraManager 的关键功能：

1. 将相机信息封装到 CameraCharacteristics 中，并提获取 CameraCharacteristics 实例的方式。
2. 根据指定的相机 ID 连接相机设备。
3. 提供将闪光灯设置成手电筒模式的快捷方式。

##### 5. CameraCharacteristics

CameraCharacteristics 是一个只读的相机信息提供者，其内部携带大量的相机信息，包括代表相机朝向的 `LENS_FACING`；判断闪光灯是否可用的 `FLASH_INFO_AVAILABLE`；获取所有可用 AE 模式的 `CONTROL_AE_AVAILABLE_MODES` 等等。如果你对 Camera1 比较熟悉，那么 CameraCharacteristics 有点像 Camera1 的 `Camera.CameraInfo` 或者 `Camera.Parameters`。

##### 6. CameraDevice

CameraDevice 代表当前连接的相机设备，它的职责有以下四个：

1. 根据指定的参数创建 CameraCaptureSession。
2. 根据指定的模板创建 CaptureRequest。
3. 关闭相机设备。
4. 监听相机设备的状态，例如断开连接、开启成功和开启失败等。

熟悉 Camera1 的人可能会说 CameraDevice 就是 Camera1 的 Camera 类，实则不是，Camera 类几乎负责了所有相机的操作，而 CameraDevice 的功能则十分的单一，就是只负责建立相机连接的事务，而更加细化的相机操作则交给了稍后会介绍的 CameraCaptureSession。

##### 7. Surface

Surface 是一块用于填充图像数据的内存空间，例如你可以使用 SurfaceView 的 Surface 接收每一帧预览数据用于显示预览画面，也可以使用 ImageReader 的 Surface 接收 JPEG 或 YUV 数据。每一个 Surface 都可以有自己的尺寸和数据格式，你可以从 CameraCharacteristics 获取某一个数据格式支持的尺寸列表。

##### 8. CameraCaptureSession

CameraCaptureSession 实际上就是配置了目标 Surface 的 Pipeline 实例，我们在使用相机功能之前必须先创建 CameraCaptureSession 实例。一个 CameraDevice 一次只能开启一个 CameraCaptureSession，绝大部分的相机操作都是通过向 CameraCaptureSession 提交一个 Capture 请求实现的，例如拍照、连拍、设置闪光灯模式、触摸对焦、显示预览画面等等。

##### 9. CaptureRequest

CaptureRequest 是向 CameraCaptureSession 提交 Capture 请求时的信息载体，其内部包括了本次 Capture 的参数配置和接收图像数据的 Surface。CaptureRequest 可以配置的信息非常多，包括图像格式、图像分辨率、传感器控制、闪光灯控制、3A 控制等等，可以说绝大部分的相机参数都是通过 CaptureRequest 配置的。值得注意的是每一个 CaptureRequest 表示一帧画面的操作，这意味着你可以精确控制每一帧的 Capture 操作。

##### 10. CaptureResult

CaptureResult 是每一次 Capture 操作的结果，里面包括了很多状态信息，包括闪光灯状态、对焦状态、时间戳等等。例如你可以在拍照完成的时候，通过 CaptureResult 获取本次拍照时的对焦状态和时间戳。需要注意的是，CaptureResult 并不包含任何图像数据，前面我们在介绍 Surface 的时候说了，图像数据都是从 Surface 获取的。

##### 11. 其他 Camera2 提供的高级特性

**在开启相机之前检查相机信息**
 出于某些原因，你可能需要先检查相机信息再决定是否开启相机，例如检查闪光灯是否可用。在 Caemra1 上，你无法在开机相机之前检查详细的相机信息，因为这些信息都是通过一个已经开启的相机实例提供的。在 Camera2 上，我们有了和相机实例完全剥离的 CameraCharacteristics 实例专门提供相机信息，所以我们可以在不开启相机的前提下检查几乎所有的相机信息。

**在不开启预览的情况下拍照**
 在 Camera1 上，开启预览是一个很重要的环节，因为只有在开启预览之后才能进行拍照，因此即使显示预览画面与实际业务需求相违背的时候，你也不得不开启预览。而 Camera2 则不强制要求你必须先开启预览才能拍照。

**一次拍摄多张不同格式和尺寸的图片**
 在 Camera1 上，一次只能拍摄一张图片，更不同谈多张不同格式和尺寸的图片了。而 Camera2 则支持一次拍摄多张图片，甚至是多张格式和尺寸都不同的图片。例如你可以同时拍摄一张 1440x1080 的 JPEG 图片和一张全尺寸的 RAW 图片。

**控制曝光时间**
 在暗环境下拍照的时候，如果能够适当延长曝光时间，就可以让图像画面的亮度得到提高。在 Camera2 上，你可以在规定的曝光时长范围内配置拍照的曝光时间，从而实现拍摄长曝光图片，你甚至可以延长每一帧预览画面的曝光时间让整个预览画面在暗环境下也能保证一定的亮度。而在 Camera1 上你只能 YY 一下。

**连拍**
 连拍 30 张图片这样的功能在 Camera2 出现之前恐怕只有系统相机才能做到了（通过 OpenGL 截取预览画面的做法除外），也可能是出于这个原因，市面上的第三方相机无一例外都不支持连拍。有了 Camera2，你完全可以让你的相机应用程序支持连拍功能，甚至是连续拍 30 张使用不同曝光时间的图片。

**灵活的 3A 控制**
 3A（AF、AE、AWB）的控制在 Camera2 上得到了最大化的放权，应用层可以根据业务需求灵活配置 3A 流程并且实时获取 3A 状态，而 Camera1 在 3A 的控制和监控方面提供的接口则要少了很多。例如你可以在拍照前进行 AE 操作，并且监听本这次拍照是否点亮闪光灯。