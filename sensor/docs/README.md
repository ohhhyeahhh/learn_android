# 

#### Types

- 动态传感器

1. TYPE_ACCELEROMETER

2. TYPE_GRAVITY

3. TYPE_GYROSCOPE

4. TYPE_LINEAR_ACCELERATION

- 
  环境传感器


1. TYPE_AMBIENT_TEMPERATURE

2. TYPE_LIGHT

3. TYPE_PRESSURE

4. TYPE_RELATIVE_HUMIDITY

- 位置传感器


1. TYPE_MAGNETIC_FIELD

2. TYPE_PROXIMITY

3. TYPE_ROTATION_VECTOR



#### API

##### 1. SensorManager

创建传感器服务的实例。该类提供了各种方法来访问和列出传感器，注册和取消注册传感器事件监听器，以及获取屏幕方向信息。它还提供了几个传感器常量，用于报告传感器精确度，设置数据采集频率和校准传感器。

##### 2. Sensor

创建特定传感器的实例。该类提供了各种方法来确定传感器的特性。

##### 3. SensorEvent

创建传感器事件对象，该对象提供有关传感器事件的信息。传感器事件对象中包含以下信息：原始传感器数据、生成事件的传感器类型、数据的准确度和事件的时间戳。

##### 4. SensorEventListener

创建两种回调方法，以在传感器值或传感器精确度发生变化时接收通知（传感器事件）。