# Android-NDK demo/code

该demo是基于Android NDK实现的可以流畅解压zip/7p的解压软件，由于软件整体过大下载地址见下方百度云链接：  

* 百度云盘源码下载链接：<https://pan.baidu.com/s/1RMjyxYjQpDixNAy2Hxz3ow>  
    - 提取码：1m0y    
  
* gitee源码下载地址：<https://gitee.com/ether123/ndk.git>

***

## 一、成果展示

* __app图标__  

<img src="https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/show.jpg" width="30%">  

* __app的加载页__  

<img src="https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/show1.jpg" width="30%">  

* __app的首页__  
    - readme按钮指向教程页，其上是两段文字，代表所使用的解压库的版本，此处调用了cpp的函数，不是写好的文本，最上面的大图标指向解压页。  

<img src="https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/show2.jpg" width="30%">  

* __解压页__  
    - 从上至下，依次为：返回图标，选择解压方式，选择文件：未选择时显示none,解压路径：这里为了方便直接写好了一个路径，在android/data/com.example.ndk/files/extracted 路径下解压，本路径在安装app后就会生成。  
    - 使用方法：依次点击选择方式按钮，选择路径按钮，解压执行按钮。  

<img src="https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/show3.jpg" width="30%">  
<img src="https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/show4.jpg" width="30%">  

* __选择方式按钮调用本机自带的文档应用以选择压缩包__  

<img src="https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/show5.jpg" width="30%">  

* __成功选择__  
    - 成功选择后，app会返回成功信息，app中所有信息均为底部弹出格式。  

<img src="https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/show6.jpg" width="30%">  

* __错误选择__  
    - 如果选择的文件格式和选择的解压方式不一致，就会在cpp的代码中返回错误。  

<img src="https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/show7.jpg" width="30%">  

* __解压成功__  
    - 解压成功后会给出相应提示。  

<img src="https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/show8.jpg" width="30%">  

* __刚才选择的文件的解压后文件__  

<img src="https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/show9.jpg" width="30%">  


## 二、部分核心代码解析

### 1、导入7z和zlib的项目

* __导入7z项目__  
1. 结构图:  
下图是本项目大致的结构图，unzip是用来解压7z文件时使用的。可以看出unzip的结构和本项目本身的一致，所以unzip就是一个项目，他被引用到了本项目中。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change1.png)  

2. 在app路径下的Gradie Scripts上做一系列修改：  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change1.png)  
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
* roguard-rules.pro
    - 添加导入的项目里的class, 本项目中就是`- keep class com.hzy.lib7z.** {*; }`。  
    - 注意，proguard-rules.pro也是两个，as也在文件名后做了标注，注意区分。
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change1.png)  

* setting.gradie  
    - 添加要引入的项目，本项目中为`include ':app', ':un7zip'`，其中app为本项目的，un7zip就是要导入的。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change1.png)  

3. 至此，就可以在java代码中导入了。  
    - 并不需要导入so之类的文件，因为这些都在要导入的项目中做了，我们要做的只是import他。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change1.png)  

***

* __导入zlib库__  
值得注意的是，虽然在about ndk提供的资料中，生成so和使用so被放在了一起，但其实这是两个可以分开的行为，以下就用一个项目生成，另一个项目使用为例。  

1. 生成  
    - Zlib-1.2.11目录下的Android.mk：  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change1.png)  

    - jni下的android.mk：  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change1.png)  
    - 这里`LOCAL_STATIC_LIBRARIES := libzlib`，定义了.a文件的名，`LOCAL_MODULE := libmyzlibtest`定义了模块的名。  
    - 因为zlib是一个在win/linux下都可用的库，所以生成时不需要zlib中的全部代码，Android.mk中写下的就是要用到的部分。  
    
    - 生成所在项目的结构:
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change1.png)  



## 三、编写调用程序

***

## 四、

（添加动图）
