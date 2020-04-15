# Android-NDK t&p/about ndk
## 一、主要概念介绍

### 1、NDK
* __定义:__  
Android Native Development Kit，简称NDK。  

Native Development Kit（NDK）是一种基于原生程序接口的软件开发工具。它提供了一系列的工具，帮助开发者快速开发C/C++的动态库，并能自动将so和java一起打包成apk。通过此工具开发的程序直接以本地语言运行，而非虚拟机。因此只有java等基于虚拟机运行的语言的程序才会有原生开发工具包。  

NDK集成了交叉编译器，并提供了相应的mk文件隔离CPU、平台、ABI等差异，开发人员只需要简单修改mk文件（指出“哪些文件需要编译”、“编译特性要求”等），就可以创建出so库。  

* __优点：__
    > + 项目需要调用底层的一些C/C++的一些东西（java无法直接访问到操作系统底层（如系统硬件等）），或者已经在C/C++环境下实现了功能代码（大部分现存的开源库都是用C/C++代码编写的。），直接使用即可。NDK开发常用于驱动开发、无线热点共享、数学运算、实时渲染的游戏、音视频处理、文件压缩、人脸识别、图片处理等。  
    > + 为了效率更加高效些。将要求高性能的应用逻辑使用C/C++开发，从而提高应用程序的执行效率。但是C/C++代码虽然是高效的，在java与C/C++相互调用时却增大了开销；  
    > + 基于安全性的考虑。防止代码被反编译，为了安全起见，使用C/C++语言来编写重要的部分以增大系统的安全性，最后生成so库（用过第三方库的应该都不陌生）便于给人提供方便。（任何有效的代码混淆对于会smail语法反编译你apk是分分钟的事，即使你加壳也不能幸免高手的攻击）  
    > + 便于代码复用和移植。用本地代码（如c/c++)开发的代码除了在Android中使用还能嵌入到其他类型平台使用。  

* __缺点：__
    > + 开发难度大，同时不易调试，耗费更多人力、物力。  
    > + 提供的库有限，一般仅用于处理算法效率和敏感的问题。  

***

### 2、NDK的“前身”——JNI
* __定义:__
Java Native Interface，简称JNI。  

Java Native Interface（JNI）标准是java平台的一部分，JNI是Java语言提供的Java和C/C++相互沟通的机制，Java可以通过JNI调用C/C++代码，C/C++的代码也可以调用java代码。  

在实际使用中，Java 需要与本地代码进行交互，而因为Java具备跨平台的特点，所以Java与本地代码交互的能力非常弱，因此需要采用JNI特性来增强Java与本地代码交互的能力。  

* __JNI与NDK的关系:__
NDK是Android中的工具开发包，可以为我们生成了C/C++的动态链接库；JNI是java和C/C++沟通的接口，用于java与C/C++的交互。  

JNI是最终要达到的目的，NDK是Android中实现JNI的手段，即在Android Studio中通过NDK从而实现JNI的功能。  

> 注意：JNI是 Java 调用 Native 语言的一种特性，是属于 Java 的，与 Android 并无直接关系。  

* __实现步骤:__
①在Java中声明Native方法（即需要调用的本地方法） 
②编译上述 Java源文件javac（得到 .class文件）  
③通过javah命令导出JNI的头文件（.h文件）  
④使用Java需要交互的本地代码 实现在Java中声明的Native方法  
> 如Java需要与C++交互，那么就用C++实现Java的Native方法  
⑤编译.so库文件  
⑥通过Java命令执行 Java程序，最终实现Java调用本地代码  

***

## 二、其他概念介绍

### 1、CMake
允许开发者编写一种平台无关的 CMakeList.txt 文件来定制整个编译流程，然后再根据目标用户的平台进一步生成所需的本地化 Makefile 和工程文件，如 Unix 的 Makefile 或 Windows 的 Visual Studio 工程。从而做到“Write once, run everywhere”。  

### 2、ABI
Application Binary Interface，简称ABI。  
Application Binary Interface（ABI）是一种应用程序二进制接口，不同的CPU支持不同的指令集，而CPU与指令集的每种组合都有其自己的应用二进制接口（或ABI），ABI 可以非常精确地定义应用的机器代码在运行时应该如何与系统交互。NDK 根据这些定义编译 .so 文件。  
不同的 ABI 对应不同的架构：NDK 为 32 位 ARM、AArch64、x86 及 x86-64 提供 ABI 支持。   

### 3、SO
Shared Object，简称SO。  
SO（shared object，共享库）是机器可以直接运行的二进制代码，是Android上的动态链接库，类似于Windows上的dll。每一个Android应用所支持的ABI是由其APK提供的.so文件决定的，这些so文件被打包在apk文件的lib/目录下，其中ABI可以是上面表格中的一个或者多个。  

### 4、a
archive归档包，即静态库。  
.a文件是unix系统中对于静态库的文件后缀，在软件打包时和主程序表态链接在一起，表现形式是在链接成同一个文件。  

### 5、LLDB
Low Level Debugger，简称LLDB。  
LLDB是一个高效的C/C++调试器，是Android Studio 用于调试原生代码的调试器。与LLVM编译器一起使用，提供了丰富的流程控制和数据监测，有效的帮助我们调试程序。  

***

## 三、安装与环境配置

### 1、Android Studio2.2 以上版本实现NDK配置：
因为Android Studio2.2以上已经内部集成 NDK，所以只需要在Android Studio内部进行配置就可以。  
如果是Android Studio2.2以下版本请直接参考后面的第二部分。  

### （1）新建文件
* __新建工程文件__
1. 打开Android Studio,点击右上角SDK Manager。  
（如果看不到下面的图片请参照 [教程](https://blog.csdn.net/qq_38232598/article/details/91346392) )  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/new1.png)  

2. 点击SDK Tools保证下图三个工具全部勾选，第一次勾选会进行下载，在完成后文件的默认位置是C:\Users\<u>XXXXX</u>\AppData\Local\Android\Sdk(带下划线部分不同)，后续NDK的位置需要用到。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/new2.png)  

3. 点击file->new->new project,弹出窗口选择新建Native c++工程文件,然后一直点NEXT完成新建。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/new3.png)  

* __配置NDK：__  
1. 打开右上角的Project Structure,打开SDK Location。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/dl1.1.png)  

2. 这里的NDK Location是空的，复制上一行SDK的位置再预览可以在NDK下找到它安装的版本（本教程版本21.0.6113669）。  

3. 将sdk目录下的ndk路径填入Android NDK location后点击OK完成路径配置。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/dl1.2.png)  

4. 可以在local.properties文件中查看sdk与ndk的路径情况。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/dl3.png)  


***

### （2）使用mk方式安装

* 如果使用mk方式安装，需要先将app中的cpp文件夹删除后继续进行搭建。  

***

* __配置插件__  
  
我们借助强大的Android Studio的插件功能，在External Tools下配置三个非常有用的插件。  
进入File->Settings–>Tools–>ExternalTools，点击+号增加。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/tool1.png)  
  
1. javah -jni命令  
    - Name：javah -jni  
    > 该命令是用来根据java文件生成.h头文件的，会自动根据java文件中的类名（包含包名）与方法名生成对应的C/C++里面的方法名。  
    - Description：根据java文件生成.h头文件  
    - Program: $JDKPath$\bin\javah.exe  
    > 这里配置的是JDK目录下的javah.exe的路径。也可以直接在安装Java的JDK路径下的bin文件夹中找到javah.exe文件。  
    - Arguments: -classpath . -jni -d $ModuleFileDir$/src/main/jni $FileClass$  
    > 这里$ModuleFileDir$/src/main/jni表示生成的文件保存在这个module目录的src/main/jni目录下，$FileClass$指的是要执行操作的类名（即我们操作的文件）。  
    - Working directory:$ModuleFileDir$\src\main\java module  
    > 这里指调用module目录下的src\main\java目录。  
    - 使用方式：选中java文件—>右键—>External Tools—>javah-jni，就可以生成jni文件夹以及文件夹下的 包名.类名的.h头文件 （名字过长，我们可以自己重命名）。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/javah1.png)  
    - 添加完成后应如下图所示：  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/javah2.png)  
  
2. Ndk-bulid命令  
    - Name：ndk -bulid  
    > 该命令是用于根据C/C++文件生成so文件的。  
    - Description：根据C/C++文件生成so文件  
    - Program: 这里配置的是ndk下的ndk-build.cmd的路径，根据自己安装的实际情况填写。  
    - Working directory:$ProjectFileDir$$ModuleFileDir$\src\main\  
    - 使用方式：选中C/C++文件—>右键—>ExternalTools—>ndk-build，将在main文件夹下生成libs文件夹以及多个so文件，我们可以移动至jniLibs目录下去。 
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/ndk1.png)  
  
3. Ndk-bulid clean命令  
    - Name：ndk-bulid clean  
    > 该命令用来清理生成的二进制文件和目标文件。  
    - Description：清理生成文件  
    - Program: 这里和ndk -bulid命令一样配置的是ndk下的ndk-build.cmd的路径，根据自己安装的实际情况填写。
    - Arguments:clean  
    - Working directory:$ProjectFileDir$\app\src\main  
    > 这里指调用project目录下的app\src\main目录。  
  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/ndk2.png)  
  
全部配置完成后点击OK完成插件配置。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/tool2.png)  

***
 
* __修改文件__  
1. 修改app下的build.gradle文件  
    - 增添以下代码段：  
    ```
    ndk{
    moduleName "MyLibrary"
    }
    sourceSets.main{
    jni.srcDirs = []
    jniLibs.srcDir "src/main/libs"}
    ```
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change1.png)  

2. 修改MyNdk下的gradle.properties文件  
    - 增添以下代码：
    ```
    android.useDeprecatedNdk=true
    ```
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change2.png)  

***

* __环境配置测试__  
1. 在java文件夹下的文件夹内新建一个java类  
    - 在类中添加如下代码段：  
    ```
    static {
    System.loadLibrary("MyLibrary");
    }
    public native String getString();
    ```
    - 注意：这里的MyLibrary后续还要用到。   
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava.png)  

2. 右击新建的类找到刚刚新建的插件工具external tools->javah -jni，完成后生成一个有.h类的jni文件夹  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava1.png)  

3. 在jni下创建Android.mk和Application.mk以及hello.cpp三个文件  
    - .mk文件可以通过直接新建一个file，再输入文件名+后缀.mk创建，之后会提示安装相关文件。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava2.png)  

4. 在hello.cpp文件下输入如下代码段：  

    ```
    #include "com_example_myapplication_jnitest.h"
    #include<jni.h>
    JNIEXPORT jstring JNICALL Java_com_example_myapplication_jnitest_getString
            (JNIEnv *env, jobject obj){
        return (*env).NewStringUTF("This is mylibrary !!!");
    }
    ```  

    - 注意：第1行代码的com_example_myapplication_jnitest.h和  
           第三行代码的Java_com_example_myapplication_jnitest_getString  
           根据每个人自己的命名进行相应修改。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava3.png)  

5. 在Android.mk文件中输入如下代码段：  
    ```
    LOCAL_PATH := $(call my-dir)
    include $(CLEAR_VARS)
    LOCAL_MODULE := MyLibrary
    LOCAL_SRC_FILES =: hello.cpp
    include $(BUILD_SHARED_LIBRARY)
    ```
    
6. 在Applicaton.mk文件中输入如下代码段：  
    ```
    APP_MODULES := MyLibrary
    APP_ABI := all
    ```
    
7. 右击jni文件夹external tools ndk_build,完成后得到一个libs文件夹和一个obj文件夹。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava4.png)  
    - 这里将libs文件夹改名为jniLibs，这个文件夹名是默认的访问地址，注意大小写。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava5.png)  

8. 最后JAVA文件夹下的MainActivity类中的测试代码段如下所示：  
    ```
    package com.example.myapplication;

    import androidx.appcompat.app.AppCompatActivity;

    import android.os.Bundle;
    import android.widget.TextView;

    public class MainActivity extends AppCompatActivity {

        // Used to load the 'native-lib' library on application startup.
        static {
            System.loadLibrary("MyLibrary");
        }

        @Override

        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);

            TextView tv = (TextView) findViewById(R.id.sample_text);

            tv.setText(new jnitest().getString());

        }
    }
    ```
    - 点击运行后如下图所示正常运行，则环境配置成功。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava6.png)  

* 配置好NDK后，Android Studio会自动生成C++文件并设置好调用的代码。只需要根据需求修改C++文件就可以使用了。  

*** 

### （3）使用cmake方式安装

* ndk开发除了通过ndk_build，采用前面所述的Android.mk+Application.mk+src方式，还可以通过cmake，采用CmakeLists.txt+src的方式安装相关文件。  
  cmake是现在最新版本as的ndk默认方式，make生成so也同样使用与上面的两个教程。  
  在开始使用cmake安装之前，要确保已经按照之前的教程下载了Cmake构建工具、LLDB调试工具和NDK开发工具集，并创建了支持C/C++的新项目。  
  
* 创建完成后同样需要按照之前的教程配置NDK。全部配置完毕后，直接点击运行，可以发现Android Studio已经帮我们自动生成了一个可以运行的cpp文件（如下图所示）。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava.png)  

***

* __已有项目结构与普通项目的一些区别__
1. app里的build.gradle配置比对代码可以发现里面面添加了两处externalNativeBuild配置项：  
    - defaultConfig里面的配置项：主要配置了Cmake的命令参数。  
    - defaultConfig外面的配置项：主要定义了CMake的构建脚本CMakeLists.txt的路径。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava1.png)  
    
2. CMake的构建脚本CMakeLists.txt：  
    - CMakeLists.txt是CMake的构建脚本，作用相当于ndk-build中的Android.mk。  
    - 更多详细的脚本配置可以参考这个中文版的[CMAKE手册](https://www.zybuluo.com/khan-lau/note/254724)
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava2.png)  

3. 原生代码native-lib.cpp:  
    - Android提供了一个简单的JNI交互Demo，返回一个字符串给Java层，方法名是通过 Java_包名_类名_方法名 的方式命名的，并通过MainActivity调用。  
    - 加载native-lib：  
    ```
    static {
        System.loadLibrary("native-lib");
    }
    ```
    - 将native-lib中获取的字符串显示在TextView上：  
    ```
    TextView tv = findViewById(R.id.sample_text);
    tv.setText(stringFromJNI());
    ```
    - native-lib中的原生方法:
    ```
    public native String stringFromJNI();
    ```
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava3.png)  

***

* __生成so文件__


***

### 2、Android Studio2.2 以下版本实现NDK配置：



## 四、问题及解决办法

### 1、问题：Android.mk must not contain space 以及 Defaulting to minimum supported version android=16  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava3.png)  

* __原因：__  
第一个问题是Android.mk文件中含有空格，第二个问题是平台版本不够。  

* __解决方法：__  
第一个问题的解决方法是去掉所有空格即可（包括注释，最好把注释全删除）。  
第二个问题的解决方法是在Application.mk 中加上以下代码行：  
    ```
    APP_PLATFORM := android-16
    ```
    - 不过还是会出现warning。  
    
***

### 2、问题：error：invalid preprocessing directive  #include “jnitest.h”  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/newjava3.png)  

* __原因：__  
无效的预处理。  

* __解决方法：__  
将#include “jnitest.h”直接删除可以解决。  

***

## 参考资料
* [Android NDK开发（一）](https://www.jianshu.com/p/16f6a3e3fc45)
* [NDK开发 从入门到放弃(一：基本流程入门了解)](https://blog.csdn.net/xiaoyu_93/article/details/52870395)
* [超级简单的Android Studio jni 实现(无需命令行)](https://blog.csdn.net/chuhongcai/article/details/52558049)
* [Android NDK开发（一） 使用CMake构建工具进行NDK开发](https://www.jianshu.com/p/81548d9f4ec4)
* [Android：JNI 与 NDK到底是什么？](https://blog.csdn.net/carson_ho/article/details/73250163)

