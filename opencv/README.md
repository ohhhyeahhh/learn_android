# android-opencv

OpenCV是一个基于BSD许可（开源）发行的跨平台计算机视觉库，可以运行在Linux、Windows、Android和Mac OS操作系统上。它轻量级而且高效——由一系列 C 函数和少量 C++ 类构成，同时提供了Python、Ruby、MATLAB等语言的接口，实现了图像处理和计算机视觉方面的很多通用算法。

其运用领域有：人机互动、物体识别、图像分割、人脸识别、动作识别、运动跟踪、机器人、运动分析、机器视觉、结构分析、汽车安全驾驶等

本项目主要介绍opencv的重要概念及在android的使用方法。


## 功能
1. 打开相机检测是否有人脸；
1. 根据人脸信息匹配人脸特征值；
1. 框选画面中出现的人脸。

## 项目结构
--code：示例代码

--资料整理：存放整理后资料

--？：技术文档

## 编译环境（待描述）


## 知识储备

### Surface

Surfaces是用来处理屏幕显示内容合成器所管理的原始缓存区的工具。它通常由图像缓冲区的消费者来创建（如：SurfaceTexture，MediaRecorder），然后被移交给生产者（如：MediaPlayer）或者是显示到其上（如：CameraDevice）。

### SurfaceView

SurfaceView提供了嵌入视图层级中的专用surface。你可以控制surface的格式或大小。SurfaceView负责把surface显示在屏幕的正确位置。

### SurfaceHolder

一个抽象接口，给持有surface的对象使用。它可以控制surface的大小和格式，编辑surface中的像素，以及监听surface的变化，这个接口通常通过SurfaceView类获得。

### Camera
Camera负责采集数据和各种操作,主要内部类如下：

**1. CameraInfo**
CameraInfo类用来描述相机信息，通过Camera类中`getCameraInfo(int cameraId, CameraInfo cameraInfo)`方法获得，主要包括以下两个成员变量：facing 代表相机的方向,orientation是相机采集图片的角度。

**2. Parameters**
Parameters是相机服务设置，不同的相机可能是不相同的。比如相机所支持的图片大小，对焦模式等等。

**3. PreviewCallback**
PreviewCallback是一个抽象接口`void onPreviewFrame(byte[] data, Camera camera)`

通过onPreviewFrame方法来获取到相机预览的数据，第一个参数data，就是相机预览到的原始数据。

### Opencv
**1. cvtColor**
cvtColor函数是OpenCV里用于图像颜色空间转换，可以实现RGB颜色、HSV颜色、HSI颜色、lab颜色、YUV颜色等转换，也可以彩色和灰度图互转。

**2. 降噪**
尽量保留图像细节特征的条件下对目标图像的噪声进行抑制和平滑处理，是图像预处理中不可缺少的操作，其处理效果的好坏将直接影响到后续图像处理和分析的有效性和可靠性。

**3. 边缘检测**
主要步骤：图片灰度化，将彩色图片转为灰度图、高斯模糊去噪、使用Canny算法进行边缘识别、将边缘识别后的图像二值化、提取图像边框，选组合适边框、对图像轮廓点进行多边形拟合、在多边形中选择合适点作为扫描图形的边界点、对结果图像使用透视技术纠正角度

### Opencv-CascadeClassifier
CascadeClassifier是opencv下objdetect模块中用来做目标检测的级联分类器的一个类；简而言之是滑动窗口机制+级联分类器的方式。
- 加载人脸识别的级联分类器
`CascadeClassifier cascadeClassifier;`

### DetectMultiScale
opencv2中人脸检测使用的是 detectMultiScale函数。它可以检测出图片中所有的人脸，并将人脸用vector保存各个人脸的坐标、大小（用矩形表示）。
### Rectangle
该函数用于绘制矩形，可以利用对角线两点来绘制矩形或者传入矩形参数来绘制矩形。

## 参考资料
- [人脸识别功能教程——实现支付宝人脸识别功能](https://www.jianshu.com/p/fe8dbb9f72ef?utm_source=desktop&utm_medium=timeline "人脸识别功能教程——实现支付宝人脸识别功能")
- [android 使用 surfaceView 获取 camera 预览界面图像数据](https://blog.csdn.net/DucklikeJAVA/article/details/81288624 "android 使用 surfaceView 获取 camera 预览界面图像数据")
- [Android: Camera相机开发详解](https://www.jianshu.com/p/f8d0d1467584 "Android: Camera相机开发详解")
- [android使用OpenCV之图像滤波处理](https://www.jianshu.com/p/e9562f8af1cb "android使用OpenCV之图像滤波处理")
- [opencv4android 常用函数API](https://blog.csdn.net/hbl_for_android/article/details/51941106 "opencv4android 常用函数API")

## 资料整合工作（后删除）
1. Opencv功能介绍
1. 摄像头获取图片信息
1. 识别人脸并进行框选（Camera预览surfaceview 并转为bitmap格式）


## 项目成员

于之希——资料整理

金子钰——资料整理

陈俊锦——搭建环境+整合代码

徐旸——相机预览

任峻扬——识别功能

## 项目记录

3.12  组内分工、readme文档编写

3.13  更新opencv介绍文档，修改readme文档

3.14  修改readme文档，添加camera相关资料
