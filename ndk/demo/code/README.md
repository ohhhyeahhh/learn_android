# Android-NDK demo/code

该demo是基于Android NDK实现的可以流畅解压zip/7p的解压软件，由于软件整体过大下载地址见下方百度云链接：  

* 百度云盘源码下载链接：<https://pan.baidu.com/s/1RMjyxYjQpDixNAy2Hxz3ow>  
    - 提取码：1m0y    
  
* gitee源码下载地址：<https://gitee.com/ether123/ndk.git>

***

## 一、成果展示

* __app图标__  

<img src="https://github.com/Shadowmeoth/save_image/blob/master/image/show.jpg" width="30%">  

* __app的加载页__  

<img src="https://github.com/Shadowmeoth/save_image/blob/master/image/show1.jpg" width="30%">  

* __app的首页__  
    - readme按钮指向教程页，其上是两段文字，代表所使用的解压库的版本，此处调用了cpp的函数，不是写好的文本，最上面的大图标指向解压页。  

<img src="https://github.com/Shadowmeoth/save_image/blob/master/image/show2.jpg" width="30%">  

* __解压页__  
    - 从上至下，依次为：返回图标，选择解压方式，选择文件：未选择时显示none,解压路径：这里为了方便直接写好了一个路径，在android/data/com.example.ndk/files/extracted 路径下解压，本路径在安装app后就会生成。  
    - 使用方法：依次点击选择方式按钮，选择路径按钮，解压执行按钮。  

<img src="https://github.com/Shadowmeoth/save_image/blob/master/image/show3.jpg" width="30%">  
<img src="https://github.com/Shadowmeoth/save_image/blob/master/image/show4.jpg" width="30%">  

* __选择方式按钮调用本机自带的文档应用以选择压缩包__  

<img src="https://github.com/Shadowmeoth/save_image/blob/master/image/show5.jpg" width="30%">  

* __成功选择__  
    - 成功选择后，app会返回成功信息，app中所有信息均为底部弹出格式。  

<img src="https://github.com/Shadowmeoth/save_image/blob/master/image/show6.jpg" width="30%">  

* __错误选择__  
    - 如果选择的文件格式和选择的解压方式不一致，就会在cpp的代码中返回错误。  

<img src="https://github.com/Shadowmeoth/save_image/blob/master/image/show7.jpg" width="30%">  

* __解压成功__  
    - 解压成功后会给出相应提示。  

<img src="https://github.com/Shadowmeoth/save_image/blob/master/image/show8.jpg" width="30%">  

* __刚才选择的文件的解压后文件__  

<img src="https://github.com/Shadowmeoth/save_image/blob/master/image/show9.jpg" width="30%">  


## 二、部分核心代码解析

### 1、导入7z和zlib的项目

* __导入7z项目__  
1. 结构图:  
下图是本项目大致的结构图，unzip是用来解压7z文件时使用的。可以看出unzip的结构和本项目本身的一致，所以unzip就是一个项目，他被引用到了本项目中。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/7z1.png)  

2. 在app路径下的Gradie Scripts上做一系列修改：  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/7z2.png)  
* build.gradie
    - 以下为导入项目的代码段：  
    ```
    dependencies {
        implementation project(':un7zip')
        implementation 'com.getkeepsafe.relinker:relinker:1.3.1'
        implementation 'androidx.appcompat:appcompat:1.1.0'
        implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
        testImplementation 'junit:junit:4.12'
        androidTestImplementation 'androidx.test.ext:junit:1.1.1'
        androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
    }
    ```
    - 注意，其中的各种导入，并不是固定的，而是依据导入的项目中要用到什么而导入什么。而到底要导入什么，可以先在写入第一句，本次中为implementation project(':un7zip')，以及完成以下所有操作后执行一遍build,弹出的错误中就包含了需要引入的部分。  
    - 另外需要注意的是不要写错build.gradie,Gradie Scripts下会有两个build.gradie，分别是本项目的，和导入的项目的，as会在文件名后标注其作用的module,只需要改本项目的就可以了。  
* proguard-rules.pro
    - 添加导入的项目里的class, 本项目中就是`- keep class com.hzy.lib7z.** {*; }`。  
    - 注意，proguard-rules.pro也是两个，as也在文件名后做了标注，注意区分。
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/getin.png)  

* setting.gradie  
    - 添加要引入的项目，本项目中为`include ':app', ':un7zip'`，其中app为本项目的，un7zip就是要导入的。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/getin1.png)  

3. 至此，就可以在java代码中导入了。  
    - 并不需要导入so之类的文件，因为这些都在要导入的项目中做了，我们要做的只是import他。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/getin2.png)  

***

* __导入zlib库__  
值得注意的是，虽然在about ndk提供的资料中，生成so和使用so被放在了一起，但其实这是两个可以分开的行为，以下就用一个项目生成，另一个项目使用为例。  

1. 生成  
    - Zlib-1.2.11目录下的Android.mk：  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/zlib.png)  

    - jni下的android.mk：  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/zlib1.png)  
    - 这里`LOCAL_STATIC_LIBRARIES := libzlib`，定义了.a文件的名，`LOCAL_MODULE := libmyzlibtest`定义了模块的名。  
    - 因为zlib是一个在win/linux下都可用的库，所以生成时不需要zlib中的全部代码，Android.mk中写下的就是要用到的部分。  
    
    - 生成所在项目的结构:
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/zlib2.png)  

    - 点击之前定义好的额外工具ndk-build，就可以obj/local下生成各架构下的so文件。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/zlib3.png)  

2. 使用  
    - 将生成的so和a文件复制到本项目中的jniLibs中：  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/use.png)  

    - 但是跨项目的生成有一个问题，就是生成环境要和使用环境保持一致，由于在生成时是在com.hello.zlib的项目下生成的，那么调用时，就要在这个路径下使用，而本项目的路径是com.example.ndk，所以这里就需要新建专门的路径了。  
    
    - 在对应的java文件下导入后加上如下代码段：  
    ```
    static {
        System.loadLibrary("myzlibtest");
    }
    ```
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/use1.png)  

    - 如果想在其他java页面上使用，只需要和7z时一样，import就好。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/use2.png)  
    
***

## 三、编写调用程序

* 调用很简单,java和cpp两边的函数命名一致就可以实现调用，以本项目中的一个函数为例，本函数是用来解压zip文件的。  

* 需要两个string变量，返回一个string变量。  

* 在JAVA端加入如下代码行：  
    ```
    public static native String unzip(String input, String output);
    ```
    
* 在cpp端加入如下代码段：  
    ```
    extern "C"
    JNIEXPORT jstring JNICALL Java_com_hello_zlib_ZlibActivity_unzip(JNIEnv* env, jobject thiz, jstring input, jstring output)
    {
        text(jstringTostring(env,input),jstringTostring(env,output));
        return input;
    }
    ```
    
* 这样，java端就实现了调用cpp函数。  

***

## 四、app交互编写

### 1、实现两秒后界面自动跳转到主页
* 可以利用LoadActivity类中的StartMainActivity()函数来实现这个功能。具体步骤如下：  

1. 在函数中定义一个TimerTask抽象类，来完成对java Timer类的时间调度，即在定义时间结束后向MainActivity类发送一条启动信息，并同时结束LoadActivity启动页从而完成自动跳转。  
```
TimerTask delaytast=new TimerTask() {
    @Override
    public void run() {
        Intent mainIntent=new Intent(LoadActivity.this,MainActivity.class);
        startActivity(mainIntent);
        LoadActivity.this.finish();
    }
};
```

2. 定义计时器并设定时间两秒后运行线程任务。  
```
Timer timer=new Timer();
timer.schedule(delaytast,2000);
```

### 2、实现界面下入下出效果
* 由anim_1.xml、anim_2.xml实现下入，anim_3.xml、anim_4.xml实现下出，两两差别不大。这里只对下入动画效果的实现做详细解释，具体如下：  

1. 首先在文件最开始利用`android:duration="250"`定义动画持续时间为250毫秒，这将作为整个动画的完成时间，四个文件保持一样。  

2. 在anim_1.xml中通过设置fromYDelta和toYDelta的值设定动画起始时 Y坐标上的位置从界面100%的位置（画面最底端）到动画结束时 Y坐标上的位置的0值（画面最顶端），完成下入的表面设置（下出则将fromYDelta设为0，toYDelta设为100%即可）。但由于此时画面只是整个平移上去遮住下面的界面，效果不太明显，所以还需要anim_2.xml文件实现辅助的动画效果。  
```
<translate
    android:fromYDelta="100%"
    android:toYDelta="0" />
```

3. 在anim_2.xml中通过fromXScale、toXScale等值调整初始X轴缩放比例、结束X轴缩放比例以及y轴的缩放比例，1表示无变化，并设置pivotX和pivotY来设置缩放起点X、y轴坐标，这个时候就可以实现在下一个界面出现到界面50%的位置的时候，后面的那个界面缩小到了原来的90%。  
```
<scale
    android:fromXScale="1"
    android:fromYScale="1"
    android:pivotX="50%"
    android:pivotY="50%"
    android:toXScale="0.9"
    android:toYScale="0.9" />
```
* 实现效果如下图所示：  

<img src="https://github.com/Shadowmeoth/save_image/blob/master/image/a1.png" width="30%">  

4. 实现了大小变化之后需要再实现一个透明度的变化来增加效果的真实性：通过设置fromAlpha和toAlpha设置动画开始的透明度和结束时的透明度，1是完全不透明，0是完全透明，这里不需要完全透明，差不多就可以了，所以我这里设定的结束透明度为0.3。这样就可以完整实现下面界面从下进入的同时前一个界面缩小并逐渐变黑（显出背景色）的效果。  
```
<alpha
    android:fromAlpha="1"
    android:toAlpha="0.3" />
```
* 下出效果则把初始和结束值倒一下就可以了，这里不作详述。  

* 实现最终效果如下图所示：  

<img src="https://github.com/Shadowmeoth/save_image/blob/master/image/a2.png" width="30%">  

5. 在跳转界面中用以下函数调用文件实现动画效果：  
```
overridePendingTransition(R.anim.anim_1,R.anim.anim_2);
```
* 后面两个文件顺序没有严格要求，可以随便放。  

***

## 五、app优化的相关尝试

### 1、非阻塞相关概念  
* __非阻塞：__  
    - 阻塞和非阻塞通常形容多线程间的相互影响。比如一个线程占用了临界区资源，那么其它所有需要这个资源的线程就必须在这个临界区中进行等待，等待会导致线程挂起。这种情况就是阻塞。此时，如果占用资源的线程一直不愿意释放资源，那么其它所有阻塞在这个临界区上的线程都不能工作。而非阻塞允许多个线程同时进入临界区。  
    - 通俗的说阻塞就是干不完不准回来，非阻塞就是你先干，我现看看有其他事没有，完了告诉我一声。  
    
* __NIO:__  
    - NIO是new I/O的简写,非阻塞在Android开发中主要是NIO非阻塞包，从jdk1.4开始引入。  
    - 相对应传统的I/O，比如Socket的accpet()、read()这些方法而言都是阻塞的。  
    - NIO主要使用了Channel和Selector来实现，Java的Selector类似Winsock的Select模式，是一种基于事件驱动的，整个处理方法使用了轮训的状态机。  
    
* 但经过了解非阻塞模式不太适合解决本DEMO的底层代码运行可能存在的问题。  

### 2、点击后退按钮返回主界面/上一界面
* 通过监听函数，监听一个按钮是否被点击，然后实现返回功能。这里给出一个例子。  

1. 创建layout（activity_test.xml）  
    - 在src/main/res/layout鼠标右键new->LayoutResource File，然后输入一个file name,比如:activity_test。点ok键完成创建。   
    
2. 创建activity（TestActivity.java）  
    - src/main/java/com.example.test鼠标右键new->java.class，然后输入一个name，比如，TestActivity。kind选择Activity。点ok键完成创建。  
    - 进入刚创建的TestActivity.java文件在onCreate方法内增加`setContentView(R.layout.activity_test);`用来指向我们刚创建的名为activity_test的layout。
    - 整个函数代码段如下：  
    ```
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
    ```
    
3. 跳转到第刚创建的TestActivity  
    - 在按钮事件增加以下代码:  
    ```
    Intent intent = new Intent();
    intent.setClass(MainActivity.this, TestActivity.class);
    MainActivity.this.startActivity(intent);
    ```
    
4. 在mainfest 中AndroidMainfest 添加Activity  
    ```
    </Activity>
    <Activity android:name="TestActivity">
    </Activity>
    ```
    
### 3、对按下back的作用进行优化
* Android studio模拟机的back键就已经实现了返回上一界面的功能而通过重写onbackpressed（）可以对按下back的作用进行改变。  

* 下面是一些在这个基础上的优化。  

1. 实现点击返回键返回主界面当前界面不销毁  
```
@Override
	public void onBackPressed() {
		Intent i = new Intent(Intent.ACTION_MAIN);
		// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}
```

2. 按back键退出程序时，实现“再按一次退出”的功能  
```
long startTime = 0;
 
@Override
public void onBackPressed() {
 
	long currentTime = System.currentTimeMillis();
	if ((currentTime - startTime) >= 2000) {
		Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
		startTime = currentTime;
	} else {
		finish();
	}
}
```

3. 重写Activity的onKeyDown方法  
    - 这些获取back键都是通过重写onBackPressed()方法实现的，需要Android2.0及之后的版本。在这之前还有一种常规方法，是通过重写Activity的onKeyDown方法来实现。代码如下：  
    ```
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
           Toast.makeText(xxxx.this,"Back键测试",1).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    ```
