# Sensor - demo/compass

该部分主要涵盖了如何通过结合加速度传感器**TYPE_ACCELEROMETER**和地磁场传感器**TYPE_MAGNETIC_FIELD**实现一个简单的指南针，以及如何在Android上使用**GPS**定位获取经纬度。

## 指南针的旋转

### 1. 简述

在老版本的Android开发过程中，指南针的实现依赖于方向传感器TYPE_ORIENTATION，但是Android 2.2（API 级别 8）已弃用此传感器，Android 4.4W（API 级别 20）也已弃用此传感器类型。

老方法被弃用，自然有更精确和更高效的方法来代替。官方提供了新的，使用TYPE_ACCELEROMETER和TYPE_MAGNETIC_FIELD结合的方法来获得设备的方向，两个传感器的说明如下。

| 传感器                  | 传感器事件数据        | 说明                        | 度量单位     |
| ----------------------- | --------------------- | :-------------------------- | ------------ |
| **TYPE_ACCELEROMETER**  | SensorEvent.values[0] | 沿 x 轴的加速力（包括重力） | 米/秒^2      |
|                         | SensorEvent.values[1] | 沿 y 轴的加速力（包括重力） |              |
|                         | SensorEvent.values[2] | 沿 z 轴的加速力（包括重力） |              |
| **TYPE_MAGNETIC_FIELD** | SensorEvent.values[0] | 沿 x 轴的地磁场强度         | 微特斯拉(µT) |
|                         | SensorEvent.values[1] | 沿 y 轴的地磁场强度         |              |
|                         | SensorEvent.values[2] | 沿 z 轴的地磁场强度         |              |

在对方法进行详细说明之前，我们先了解一下一个重要的概念：传感器坐标系统（Sensor Coordinate System）。

在Android平台中，传感器框架通常是使用一个标准的三维坐标系去表示一个值的。以方向传感器为例，确定一个方向当然也需要一个三维坐标，毕竟我们的设备不可能永远水平端着，准确的说android给我们返回的方向值就是一个长度为3的float数组，包含三个方向的值。下面看一下官方提供的传感器API使用的坐标系统示意图：
![img](https://developer.android.google.cn/images/axis_device.png)

仔细看一下这张图，不难发现，z是指向地心的方位角，x轴是仰俯角（由静止状态开始前后反转），y轴是翻转角（由静止状态开始左右反转）。

### 2. 方法说明

如果你仍然在代码中使用TYPE_ORIENTATION，该传感器会在代码中被划掉 ~~TYPE_ORITNTATION~~，打开源代码可以看到这样的注释：

```java
/**
 * A constant describing an orientation sensor type.
 * <p>See {@link android.hardware.SensorEvent#values SensorEvent.values}
 * for more details.
 *
 * @deprecated use {@link android.hardware.SensorManager#getOrientation
 *             SensorManager.getOrientation()} instead.
 */
```

从注释中可以看到，官方推荐我们用SensorManager.getOrientation()这个方法去替代原来的TYPE_ORIENTATION。那我们继续在源码中看看这个方法：

```java
public static float[] getOrientation(float[] R, float[] values) {
    /*
     * 4x4 (length=16) case:
     *   /  R[ 0]   R[ 1]   R[ 2]   0  \
     *   |  R[ 4]   R[ 5]   R[ 6]   0  |
     *   |  R[ 8]   R[ 9]   R[10]   0  |
     *   \      0       0       0   1  /
     *
     * 3x3 (length=9) case:
     *   /  R[ 0]   R[ 1]   R[ 2]  \
     *   |  R[ 3]   R[ 4]   R[ 5]  |
     *   \  R[ 6]   R[ 7]   R[ 8]  /
     *
     */
    if (R.length == 9) {
        values[0] = (float) Math.atan2(R[1], R[4]);
        values[1] = (float) Math.asin(-R[7]);
        values[2] = (float) Math.atan2(-R[6], R[8]);
    } else {
        values[0] = (float) Math.atan2(R[1], R[5]);
        values[1] = (float) Math.asin(-R[9]);
        values[2] = (float) Math.atan2(-R[8], R[10]);
    }

    return values;
}
```

该方法能够基于旋转矩阵计算设备的方向，不用关心返回值，方法会根据R[ ]的数据得到我们想要的values[ ]。那么参数R[ ]要怎么获得呢，首先继续看源代码中的注释：

```java
* @param R
*        rotation matrix see {@link #getRotationMatrix}.
```

参数R[ ]很明显表示一个旋转矩阵，实际上它是用来保存磁场和加速度的数据的，根据注释我们可以看到让我们需要通过getRotationMatrix这个方法来填充这个参数R[ ]，那我们就再去看看这个方法源码，依旧是SensorManager的一个静态方法：

```java
public static boolean getRotationMatrix(float[] R, float[] I,
        float[] gravity, float[] geomagnetic) {
    // TODO: move this to native code for efficiency
    float Ax = gravity[0];
    float Ay = gravity[1];
    float Az = gravity[2];

    final float normsqA = (Ax * Ax + Ay * Ay + Az * Az);
    final float g = 9.81f;
    final float freeFallGravitySquared = 0.01f * g * g;
    if (normsqA < freeFallGravitySquared) {
        // gravity less than 10% of normal value
        return false;
    }

    final float Ex = geomagnetic[0];
    final float Ey = geomagnetic[1];
    final float Ez = geomagnetic[2];
    float Hx = Ey * Az - Ez * Ay;
    float Hy = Ez * Ax - Ex * Az;
    float Hz = Ex * Ay - Ey * Ax;
    final float normH = (float) Math.sqrt(Hx * Hx + Hy * Hy + Hz * Hz);

    if (normH < 0.1f) {
        // device is close to free fall (or in space?), or close to
        // magnetic north pole. Typical values are  > 100.
        return false;
    }
    final float invH = 1.0f / normH;
    Hx *= invH;
    Hy *= invH;
    Hz *= invH;
    final float invA = 1.0f / (float) Math.sqrt(Ax * Ax + Ay * Ay + Az * Az);
    Ax *= invA;
    Ay *= invA;
    Az *= invA;
    final float Mx = Ay * Hz - Az * Hy;
    final float My = Az * Hx - Ax * Hz;
    final float Mz = Ax * Hy - Ay * Hx;
    if (R != null) {
        if (R.length == 9) {
            R[0] = Hx;     R[1] = Hy;     R[2] = Hz;
            R[3] = Mx;     R[4] = My;     R[5] = Mz;
            R[6] = Ax;     R[7] = Ay;     R[8] = Az;
        } else if (R.length == 16) {
            R[0]  = Hx;    R[1]  = Hy;    R[2]  = Hz;   R[3]  = 0;
            R[4]  = Mx;    R[5]  = My;    R[6]  = Mz;   R[7]  = 0;
            R[8]  = Ax;    R[9]  = Ay;    R[10] = Az;   R[11] = 0;
            R[12] = 0;     R[13] = 0;     R[14] = 0;    R[15] = 1;
        }
    }
    if (I != null) {
        // compute the inclination matrix by projecting the geomagnetic
        // vector onto the Z (gravity) and X (horizontal component
        // of geomagnetic vector) axes.
        final float invE = 1.0f / (float) Math.sqrt(Ex * Ex + Ey * Ey + Ez * Ez);
        final float c = (Ex * Mx + Ey * My + Ez * Mz) * invE;
        final float s = (Ex * Ax + Ey * Ay + Ez * Az) * invE;
        if (I.length == 9) {
            I[0] = 1;     I[1] = 0;     I[2] = 0;
            I[3] = 0;     I[4] = c;     I[5] = s;
            I[6] = 0;     I[7] = -s;     I[8] = c;
        } else if (I.length == 16) {
            I[0] = 1;     I[1] = 0;     I[2] = 0;
            I[4] = 0;     I[5] = c;     I[6] = s;
            I[8] = 0;     I[9] = -s;     I[10] = c;
            I[3] = I[7] = I[11] = I[12] = I[13] = I[14] = 0;
            I[15] = 1;
        }
    }
    return true;
}
```

这个方法使用到了4个参数，如下：

- R[ ]自然表示我们需要在getOrientation方法中使用到的旋转矩阵，大小为9

- I[ ]从官方注释中看到，是一个倾角矩阵，大小为9，一般设置为null

- gravity[ ]是一个储存有三个float值的数组，包括了三个在设备坐标系下的重力向量, 关于该参数，源码的注释中提到了：

  ```java
  *        {@link android.hardware.Sensor#TYPE_ACCELEROMETER
  *        TYPE_ACCELEROMETER}.
  ```

- geomagnetic[ ]是一个储存有三个float值的数组，包括了三个在设备坐标系下的地磁向量, 关于该参数，源码的注释中提到了：

  ```java
  *        {@link android.hardware.Sensor#TYPE_MAGNETIC_FIELD
  *        TYPE_MAGNETIC_FIELD}.
  ```

很明显，在两个参数的注释中提到了另外两个传感器，**TYPE_ACCELEROMETER**和**TYPE_MAGNETIC_FIELD**，将两个传感器返回的值填充gravity[ ]和geomagnetic[ ]，这就是我们获得设备方向的新方法了。整理一下整个过程，先从传感器TYPE_ACCELEROMETER和TYPE_MAGNETIC_FIELD中获得重力向量和地磁向量，传入getRotationMatrix方法中获得旋转矩阵R[ ]，再将R[ ]传入方法getOrientation获得设备的旋转方向，那么怎么利用这个方向来实现指南针呢，下面就在具体实现中进行说明。

### 3. 具体实现

要想实现指南针功能，其实主要就是获取手机的方位，通过对比前一刻方位和现在手机方位算出手机旋转的角度，这个角度的获取方法也就是我们在上一点中提到的了，然后根据手机实际旋转的角度去旋转指南针的imageview。首先，准备一张标准的指南针的图片，例如下面这张，放在一个ImageView组件里。

<img src="imgs\compass.png" style="zoom:50%;" />

在java文件中，传感器的基本使用在这里不做过多介绍，要实现指南针，只需按照以下的步骤：

1. **变量声明**

   主要是布局文件中的控件声明以及所使用到的传感器声明，还有传感器返回的数值，如下：

   ```java
   //指南针图片
   ImageView compassImg;
   //sensor管理器
   SensorManager mSensorManager;
   //图片转过的角度
   float currentDegree = 0f;
   //加速度传感器
   Sensor mAccelerometer;
   float[] accelerometerValues = new float[3];
   //地磁传感器
   Sensor mMagnetic;
   float[] magneticValues = new float[3];
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

4. **旋转角度计算**

   在这里使用到了一个calculateOrientation方法来计算旋转角度。方法如下：

   ```java
   private float calculateOrientation(){
       float[] values = new float[3];
       float[] R = new float[9];
       SensorManager.getRotationMatrix(R,null,accelerometerValues,magneticValues);
       SensorManager.getOrientation(R,values);
       values[0] = (float)Math.toDegrees(values[0]);
       return -values[0];
   }
   ```

   value[ ]是我们最终需要的旋转角度，使用在方法说明中提到的步骤，分别调用getRotationMatrix方法和getOrientation方法来获得。因为要实现的是指南针应用，所以只用考虑设备在z轴方向上的旋转角度，也就是如代码中的取values[0]的值，然后使用toDegrees方法将参数转换为角度。最后要注意的是我们需要取的是负值，因为指南针的旋转方向应该和设备的旋转方向相反，这也符合我们对指南针的使用认知，所以在方法的最后返回的是角度的负值。

5. **执行旋转动画**

   直到上一步，我们已经获得了指南针需要旋转的角度，接下来就要实现指南针旋转的动画了，这一步同样在onSensorChanged方法中进行。实例代码如下，最后不要忘了更新当前角度currentDegree的大小。

   ```java
   //获得旋转度数
   float degree = calculateOrientation();
   //创建旋转动画
   RotateAnimation ra = new RotateAnimation(currentDegree, degree,
           Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
   //动画持续时间
   ra.setDuration(200);
   //运行动画
   compassImg.startAnimation(ra);
   currentDegree = degree;
   ```

## GPS定位

GPS定位的相关类介绍和更多代码封装的内容可见该链接，[GPS定位](https://www.cnblogs.com/powerwu/articles/9228307.html )。

### 1. 相关介绍

Android定位一般有四种方式，分别是GPS定位，WIFI定位，基站定位，AGPS定位。

> - GPS定位精确度高，走的是卫星通道，即使在没有网络的情况下也能用。但是比较耗电，绝大部分用户默认不开启GPS模块，而且在室内几乎无法使用。
> - WIFI定位并不需要真正连接上指定的WIFI路由器，只需要探测到有WIFI存在即可，设备在开启Wi-Fi的情况下，即可扫描并收集周围的AP信号，无论是否加密，是否已连接，甚至信号强度不足以显示在无线信号列表中，都可以获取到AP广播出来的MAC地址。设备将这些能够标示AP的数据发送到位置服务器，服务器检索出每一个AP的地理位置，并结合每个信号的强弱程度，计算出设备的地理位置并返回到用户设备。
> - 基站定位是根据通讯网络基站信息进行定位的方法，此定位方法需要有较丰富的基站地理信息数据支持。通常需要多个基站获取相应的GPS之后，按RSSI的距离才能算出来。
> - AGPS（Assisted GPS，A-GPS，网络辅助GPS）定位结合了GPS定位和蜂窝基站定位的优势，借助蜂窝网络的数据传输功能，可以达到很高的定位精度和很快的定位速度，在移动设备尤其是手机中被越来越广泛的使用。[AGPS定位基本机制](https://www.cnblogs.com/prayer521/p/6636457.html)

在该demo中主要介绍的是如何使用Android API进行GPS定位，另外还有通过地图API来获得经纬度坐标的方法。

#### 1.1 相关类介绍

- **位置服务管理类LocationManager**

  是获取位置信息的入口级类，要获取位置信息，首先需要获取一个LocationManger对象：

  ```java
  LocationManager mLocationManager = (LocationManager)Context.getSystemService(Context.LOCATION_SERVICE);
  ```

- **位置源提供者LocationProvider**

  用于描述位置提供者信息，可以先使用方法获取最佳提供者的名称：

  ```java
  String providerName = LocationManger.getBestProvider(Criteria criteria, boolean enabledOnly);
  ```

  LocationManger.getProvider(String name)获取LocationProvider对象。

- **位置对象Location**

  描述地理位置信息的类，记录了经纬度、海拔高度、获取坐标时间、速度、方位等。可以通过LocationManager.getLastKnowLocation(provider)获取位置坐标，provider就是GPS_PROVIDER、NETWORK_PROVIDER、PASSIVE_PROVIDER、FUSED_PROVIDER；不过很多时候得到的Location对象为null；实时动态坐标可以在监听器locationListener的onLocationChanged(Location location)方法中来获取。

- **位置监听接口LocationListener**

  用于监听位置（包括GPS、网络、基站等所有提供位置的）变化，监听设备开关与状态。实时动态获取位置信息，首先要实现该接口，在相关方法中添加实现功能的代码，实现该接口可以使用内部类或者匿名实现。然后注册监听：

  ```java
  LocationManger.requestLocationUpdates(Stringprovider, long minTime, float minDistance, LocationListener listener);
  ```

  使用完之后需要在适当的位置移除监听：

  ```java
  LocationManager.removeUpdates(LocationListener listener);
  ```

  > LocationListener需要实现的方法：
  >
  > - onLocationChanged(Location location)：当位置发生变化的时候会自动调用该方法，参数location记录了最新的位置信息。
  > - onStatusChanged(String provider, int status, Bundle extras)：当位置提供者的状态发生改变（可用到不可用、不可用到可用）时自动调用该方法；参数provider为位置提供者的名称，status为状态信息：OUT_OF_SERVICE 、AVAILABLE 、TEMPORARILY_UNAVAILABLE ，extras为附加数据：key/value，如satellites；
  > - onProviderEnabled(String provider)：位置信息提供者可用时自动调用，比如用户关闭了GPS时，provider则为“gps”；
  > - onProviderDisabled(String provider)：位置信息不可用时自动调用。

- **用于选择位置信息的辅助类Criteria**

  创建LocationProvider对象时会使用到该类。定位信息提供者会根据精度、电量、是否提供高度、速度、方位、服务商付费等信息进行排序选择定位提供者，实例参考：

  ```java
  /** this criteria needs high accuracy, high power and cost */  
   public static Criteria createFineCriteria() {  
    
      Criteriac = new Criteria();  
      c.setAccuracy(Criteria.ACCURACY_FINE);//高精度  
      c.setAltitudeRequired(true);//包含高度信息  
      c.setBearingRequired(true);//包含方位信息  
      c.setSpeedRequired(true);//包含速度信息  
      c.setCostAllowed(true);//允许付费  
      c.setPowerRequirement(Criteria.POWER_HIGH);//高耗电  
      return c;  
   }
  ```

- **GPS状态监听的接口GpsStatusListener**

  使用方法与locationListener接口类似，先实现接口并创建对象，实现接口中的方法：

  ```java
  onGpsStatusChanged(int event);
  ```

  在方法中实现对卫星状态信息变化的监听，根据event的类型编写逻辑代码。创建对象后再注册监听：

  ```java
  LocationManager.addGpsStatusListener(GpsStatus.Listener listener);
  ```

  使用后在合适的位置释放监听：

  ```java
  LocationManager.removeGpsStatusListener(GpsStatus.Listener listener);
  ```

### 2. GPS定位流程

下面介绍使用GPS进行定位的基本流程。

（1）配置权限

和许多我们平时接触到的需要GPS定位的应用一样，使用GPS定位是需要申请权限的。在AndroidManifest.xml文件中添加以下权限：

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />   <uses-permission android:name="android.permission..ACCESS_FINE_LOCATION" /> 
```

（2）获取LocationManager类型对象

```java
LocationManager mLocationManager =(LocationManager)mContext.getSystemService (Context.LOCATION_SERVICE);
```

（3）获取最佳定位方式pProvider

这步可有可无，根据情况而定。

```java
mLocationManager.getBestProvider(mCriteria,true);
```

mCriteria为一个Criteria类型的对象，使用方法和实例可见上面的类相关介绍。

（4）实现LocationListener接口

可以采用内部类（MyLocationListener）或匿名类方式实现，重写接口方法。

（5）创建MyLocationListener对象mLocationListener，并添加监听

```java
 mLocationListener = new MyLocationListener();
 mLocationManager.requestLocationUpdates(pProvider,MIN_TIME_UPDATE,MIN_DISTANCE_UPDATE, mLocationListener);
```

（6）使用完释放监听

```java
mLocationManager.removeUpdates(mLocationListener);
```

该方法执行的位置需要特别注意，如果是在Activity对象中，则需要考虑Activity的生命周期，onPause方法中比较合适，因为onStop、onDestroy两个方法在异常情况下不会被执行。

（7）如果需要监听GPS卫星状态，则需要实现GpsStatus.Listener接口，并创建对象、添加监听、使用完后释放监听：

```java
// 实现接口
private class MyGpsStatusListener implements GpsStatus.Listener;
// 创建对象
MyGpsStatusListener mGpsStatusListener = new MyGpsStatusListener();
// 添加监听
mLocationManager.addGpsStatusListener(mGpsStatusListener);
// 释放监听
mLocationManager.removeGpsStatusListener(mGpsStatusListener);
```

### 3. 代码封装

感谢lizhenya编写的GPS信息管理类，将功能逻辑封装好实现了模块化，使用该类只需在onCreate方法中进行初始化和开启定位，在onPause方法中终止定位。具体的类定义文件已经包含在demo项目中，如需使用只要复制到你的项目之中即可。这里只简单介绍一下各个类的作用和该封装类的用法，更多详细信息见[这里](https://www.cnblogs.com/powerwu/articles/9228307.html)。

- **GPSLocationListener类**

  利用java面向接口编程的方式定义，该接口用于实时监听数据回调。

- **GPSLocation类**

  实现动态地实时更新位置坐标信息、状态信息。

- **GPSProviderStatus类** 

  GPS状态信息类。

- **GPSLocationManager类**

  实现GPS定位的初始化、GPS定位的启动和终止。

下面说说如何使用上述类来实现GPS定位，首先定义一个管理类：

```java
private GPSLocationManager gpsLocationManager; 
```

然后在onCreate方法中进行初始化和开启定位：

```java
gpsLocationManager = GPSLocationManager.getInstances(ThirdActivity.this);  
//开启定位  
gpsLocationManager.start(new MyListener());  
```

更新位置信息和状态，例如可以编写一个继承自GPSLocationListener接口的类来处理位置信息和管理状态变化等。

最后不要忘记在在onPause()方法中终止定位 ：

```java
gpsLocationManager.stop(); 
```