# android-opencv

OpenCV是一个基于BSD许可（开源）发行的跨平台计算机视觉库，可以运行在Linux、Windows、Android和Mac OS操作系统上。它轻量级而且高效——由一系列 C 函数和少量 C++ 类构成，同时提供了Python、Ruby、MATLAB等语言的接口，实现了图像处理和计算机视觉方面的很多通用算法。

其运用领域有：人机互动、物体识别、图像分割、人脸识别、动作识别、运动跟踪、机器人、运动分析、机器视觉、结构分析、汽车安全驾驶等

本项目主要介绍opencv的重要概念及在android的使用方法。


## 功能
1. 打开相机检测是否有人脸；
1. 根据人脸信息匹配人脸特征值；
1. 框选画面中出现的人脸。

## 项目结构
--code：示例代码及技术文档

--资料整理：存放整理后资料


## 基于 Android 的 OpenCV 开发环境搭建

### OpenCV SDK 准备
采用直接加载openCV官方提供的Android SDK来进行导入：

先到官网http://opencv.org/releases.html ，下载Android 包，如：opencv-3.2.0-android-sdk.zip
1. 进入后选择 Android SDK 点击下载即可
1. 下载成功后解压得到我们要的OpenCV 资源

### 将 OpenCV 导入到项目中去
1. 新建 Android 项目
2. 导入 OpenCV SDK 
	- 导入刚刚下载解压到 SDK 包：选择：File > New > New Module
    - 选择Import Eclipse ADT Project
	- 选择刚刚下载解压得到 SDK 文件，点击open 路径为\OpenCV-android-sdk\sdk\java
	- 设置 Module 名称，Finish
3. 直接在 app 目录下build.gradle 文件里dependencies 大括号下添加 compile project(':openCVLibrary') 版本不同可能要将compile换成implementation
4. 打开刚导入的模块下 build.gradle 文件，把 compileSdkVersion 和 minSdkVersion 和 targetSdkVersion修改成build.gradle(module:app)的SDK版本
5. 接着在 app/src/main 目录下 创建一个jniLibs 目录，然后把sdk/native/libs 下所有文件拷贝到jniLibs下，编译，运行。


## 参考资料
- [Android 接入 OpenCV库的三种方式](https://www.cnblogs.com/xiaoxiaoqingyi/p/6676096.html)
- [OpenCV4Android学习之图像特征匹配算法](https://blog.csdn.net/gulingfengze/article/details/53571605 "OpenCV4Android学习之图像特征匹配算法")
- [Android: Camera相机开发详解](https://www.jianshu.com/p/f8d0d1467584 "Android: Camera相机开发详解")
- [android使用OpenCV之图像滤波处理](https://www.jianshu.com/p/e9562f8af1cb "android使用OpenCV之图像滤波处理")
- [opencv4android 常用函数API](https://blog.csdn.net/hbl_for_android/article/details/51941106 "opencv4android 常用函数API")

## 资料整合工作（后删除）
1. Opencv功能介绍
1. 摄像头获取图片信息
1. 识别人脸并进行框选（Camera预览surfaceview 并转为bitmap格式）

## 源码下载
地址：https://pan.baidu.com/s/12EUmC2lJ0-JLGv6_TQRcVw 

提取码：w721 


## 项目成员

于之希——资料整理、PPT制作排版

金子钰——资料整理、PPT制作

陈俊锦——人脸检测功能

徐旸——摄像机/整合代码

任峻扬——配置Opencv环境

## 项目记录

3.12  组内分工、readme文档编写

3.13  更新opencv介绍文档，修改readme文档

3.14  修改readme文档，添加camera相关资料

3.15  OpenCVDemo 实现后置摄像头框选功能

3.23 添加打包项目

3.26 添加技术文档

4.08 修改技术文档

4.09 修改技术文档 

4.10 完成PPT

4.13 修改PPT
