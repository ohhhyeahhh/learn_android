# Sensor
大多数 Android 设备都有内置传感器，用来测量运动、屏幕方向和各种环境条件。这些传感器能够提供高度精确的原始数据，非常适合用来监测设备的三维移动或定位，监测设备周围环境的变化，并且能够广泛地应用于各类软件和游戏中。
本项目主要介绍Andrio传感器的种类、概念以及使用方法。

## 团队成员：

 - 李燚丹
 - 姚硕
 - 柯爱
 - 张慧婷
 - 黄心苗

## 项目结构

---code示例代码

---docs团队文档与技术说明文档

## 内容介绍

### 一、传感器种类 

动态传感器

##### 1. TYPE_ACCELEROMETER

##### 2. TYPE_GRAVITY

##### 3. TYPE_GYROSCOPE

##### 4. TYPE_LINEAR_ACCELERATION


环境传感器

##### 1. TYPE_AMBIENT_TEMPERATURE

##### 2. TYPE_LIGHT

##### 3. TYPE_PRESSURE

##### 4. TYPE_RELATIVE_HUMIDITY

位置传感器

##### 1. TYPE_MAGNETIC_FIELD

##### 2. TYPE_PROXIMITY

##### 3. TYPE_ROTATION_VECTOR

### 二、相关API

API的作用：（1）识别传感器和传感器特性  （2）监控传感器时间 【会在后续文档中详述】

##### 1. SensorManager

创建传感器服务的实例。该类提供了各种方法来访问和列出传感器，注册和取消注册传感器事件监听器，以及获取屏幕方向信息。它还提供了几个传感器常量，用于报告传感器精确度，设置数据采集频率和校准传感器。

##### 2. Sensor

创建特定传感器的实例。该类提供了各种方法来确定传感器的特性。

##### 3. SensorEvent

创建传感器事件对象，该对象提供有关传感器事件的信息。传感器事件对象中包含以下信息：原始传感器数据、生成事件的传感器类型、数据的准确度和事件的时间戳。

##### 4. SensorEventListener

创建两种回调方法，以在传感器值或传感器精确度发生变化时接收通知（传感器事件）。

## 参考资料

<https://developer.android.google.cn/guide/topics/sensors> 

<https://blog.csdn.net/weixin_38379772/article/details/79069494> 

## 项目记录

3.12 组内分工+ 项目目录初步编写

3.13
