# Sensor - demo/gradienter

该部分主要介绍了如何通过结合加速度传感器**TYPE_ACCELEROMETER**和地磁场传感器**TYPE_MAGNETIC_FIELD**实现一个简单的水平仪。核心代码来自Canney，[github地址](https://github.com/canney-chen/android-projects/tree/master/Level)，[文章](https://blog.csdn.net/canney_chen/article/details/54693563)。

> 注意：该部分的相关传感器介绍和方向的获得方法和指南针compass.md中的相同，只是应用上的不同，重复内容不再做赘述。所以在阅读该文档之前，建议先阅读同目录下的compass.md文件中的前半部分。

### 自定义控件LevelView

demo中水平仪的实现使用到了一个自定义控件LevelView，控件的相关定义说明见demo源码或者阅读上面提供的文章链接。自定义控件的引入遵循以几个步骤：

1. 类的引入

   将LevelView.java文件复制进入项目，切记更改代码第一行的包名称为你自己项目对应的名称。

2. 属性的注册

   在xml文件中对自定义控件需要的属性进行注册，本控件示例如下：

   ```xml
   <resources>
       <declare-styleable name="LevelView">
           <attr name="limitRadius" format="dimension" />
           <attr name="limitColor" format="color"/>
           <attr name="limitCircleWidth" format="dimension"/>
           <attr name="bubbleRadius" format="dimension"/>
           <attr name="bubbleRuleColor" format="color"/>
           <attr name="bubbleRuleWidth" format="dimension"/>
           <attr name="bubbleRuleRadius" format="dimension"/>
           <attr name="bubbleColor" format="color" />
           <attr name="horizontalColor" format="color"/>
       </declare-styleable>
   </resources>
   ```

3. 控件的使用

   有了以上这些步骤之后，在Activity的布局文件中就可以直接使用控件，例如：

   ```xml
   <com.example.sensordemo.LevelView/>
   ```

   > 这里也要注意包的名称

### 水平仪的实现

之前已经提到过，水平仪需要使用到的传感器有TYPE_ACCELEROMETER和TYPE_MAGNETIC_FIELD，下面在代码中来看看是怎么实现的。

1. **变量声明**

   主要是布局文件中的控件声明以及所使用到的传感器声明，还有传感器返回的数值，如下：

   ```java
   SensorManager mSensorManager;
   //加速度传感器
   Sensor mAccelerometer;
   float[] accelerometerValues = new float[3];
   //地磁传感器
   Sensor mMagnetic;
   float[] magneticValues = new float[3];
   //旋转矩阵
   float[] r = new float[9];
   //模拟方向传感器的数据
   float[] values = new float[3];
   ```

2. **变量定义**

   定义以上各个参数，在onCreate方法中进行，在这里贴出与传感器相关的部分。

   ```java
   mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
   mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
   mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
   ```

3. **获得传感器返回的参数**

   在回调方法onSensorChanged中获得两个传感器返回的数值，存储在之前定义的数组中，我这里是accelerometerValues[ ]和magneticValues[ ]，代码如下：

   ```java
   case Sensor.TYPE_ACCELEROMETER:
       accelerometerValues = event.values;
       break;
   case Sensor.TYPE_MAGNETIC_FIELD:
       magneticValues = event.values;
   	break;
   ```

4. **角度计算**

   如果你看过上一个实现指南针的文档，那么现在请你回想一下传感器坐标系这个概念。实现指南针，我们只使用到了z轴上的旋转角度，但是水平仪不同的是，三个轴的旋转角度都需要使用到。在这里角度计算的方法是相同的，同样使用到了下面两个方法：

   ```java
   SensorManager.getRotationMatrix(r,null,accelerometerValues,magneticValues);
   SensorManager.getOrientation(r,values);
   ```

   然后我们得到我们想要的三个角度，注意正负值：

   ```java
   //获取沿着z轴转过的角度
   float azimuth = values[0];
   //获取沿着x轴倾斜时与y轴的夹角
   float pitchAngle = values[1];
   //获取沿着y轴的滚动时与x轴的角度
   float rollAngle = -values[2];
   ```

5. **角度变更并显示**

   在此demo中，得到了上面我们所需的角度，调用方法：

   ```java
   onAngleChanged(rollAngle,pitchAngle,azimuth);
   ```

   该方法的定义如下：

   ```java
   //角度变更并显示到页面
   private void onAngleChanged(float roll,float pitch,float azi){
       levelView.setAngle(roll,pitch);
       tvHorz.setText(String.valueOf((int)Math.toDegrees(roll)) + "°");
       tvVert.setText(String.valueOf((int)Math.toDegrees(pitch)) + "°");
   }
   ```

   可以看到主要是利用了向控件传入一个新的水平和垂直的角度来实现角度的变更，如果对z轴上的旋转方向没有要求的话，实际上只使用在x轴和y轴上的旋转角度就足够实现一个水平仪的效果了。另外对于控件如何处理角度达到demo中的显示效果，建议直接研究源代码。