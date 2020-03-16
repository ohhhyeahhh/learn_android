# Camera - docs/basic 

​	从 Android 5.0 开始，Google 引入了一套全新的相机框架 Camera2，旧的被废弃，本项目主要为Camera2 的重要概念和使用方法的说明文档。

## 基础内容 - 预览、拍照

##### 1. 相机预览

​	将实现相机预览功能，预览不同输出尺寸的图像并且可以选择最优预览输出尺寸 ，纠正画面拉伸变形问题以及横竖向布局切换，重点放在梳理相机预览的关键步骤和相关类的使用（CameraManager、CameraCharacteristics、CameraDevice、CameraCaptureSession、CaptureRequest）。

##### 2. 获取、处理预览帧数据

​	将使用 `ImageReader` 类间接获取预览帧数据，在相应的回调方法中接收预览帧，并实现具体的处理逻辑。 

##### 3. 拍照

​	将对相机的拍照流程、拍照方向及屏幕旋转时的适配问题进行梳理，使在屏幕旋转时也能保证正常的预览窗口大小和预览方向。拍照后，进行照片的本地保存。
