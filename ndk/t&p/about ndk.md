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
1. 在Java中声明Native方法（即需要调用的本地方法） 

2. 编译上述 Java源文件javac（得到 .class文件）  

3. 通过javah命令导出JNI的头文件（.h文件）  

4. 使用Java需要交互的本地代码 实现在Java中声明的Native方法  
> 如Java需要与C++交互，那么就用C++实现Java的Native方法  

5. 编译.so库文件  

6. 通过Java命令执行 Java程序，最终实现Java调用本地代码  

* __JNI类型与Java类型对应的关系介绍:__  
1. 基本数据类型  

| JNI类型 | Java类型 | 描述 |  
| ---- | ---- | ---- |  
| jboolean | boolean | 无符号的char类型 |  
| jbyte | byte | 带符号的8位整型 |  
| jchar | char | 无符号的16位整型 |  
| jshort | short | 带符号的16位整型 |  
| jlong | long | 带符号的64位整型 |  
| jint | int | 带符号的32位整型 |  
| jfloat | float | 32位浮点型 |  
| jdouble | double | 64位浮点型 |  
| void | void | 无类型 |  

2. 引用类型（类、对象、数组）  

| JNI类型 | Java类型 | 描述 |  
| ---- | ---- | ---- |  
| jobject | Object | 任何JAVA对象 |  
| jclass | class | Class对象 |  
| jstring | String | 字符串对象 |  
| jobjectArray | Object[] | 对象数组 |  
| jbooleanArray | boolean[] | 布尔型数组 |  
| jbyteArray | byte[] | 比特型数组 |  
| jcharArray | char[] | 字符型数组 |  
| jshortArray | short[] | 短整型数组 |  
| jintArray | int[] | 整型数组 |  
| jlongArray | long[] | 长整型数组 |  
| jfloatArray | float[] | 浮点型数组 |  
| jdoubleArray | double[] | 双浮点数组 |  
| jthrowable | Throwable | Throwable |  

3. 数据类型的签名（标识JAVA类型）  

| Java类型 | 签名 |  
| ---- | ---- |  
| boolean | Z |  
| byte | B |  
| char | C |  
| short | S |  
| long | J |  
| int | I |  
| float | F |  
| double | D |  
| void | V |  

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
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/new1.png)  

2. 点击SDK Tools保证下图三个工具全部勾选，第一次勾选会进行下载，在完成后文件的默认位置是C:\Users\<u>XXXXX</u>\AppData\Local\Android\Sdk(带下划线部分不同)，后续NDK的位置需要用到。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/new2.png)  

3. 点击file->new->new project,弹出窗口选择新建Native c++工程文件,然后一直点NEXT完成新建。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/new3.png)  

* __配置NDK：__  
1. 打开右上角的Project Structure,打开SDK Location。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/dl1.1.png)  

2. 这里的NDK Location是空的，复制上一行SDK的位置再预览可以在NDK下找到它安装的版本（本教程版本21.0.6113669）。  

3. 将sdk目录下的ndk路径填入Android NDK location后点击OK完成路径配置。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/dl1.2.png)  

4. 可以在local.properties文件中查看sdk与ndk的路径情况。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/dl3.png)  


***

### （2）使用mk方式安装

* 如果使用mk方式安装，需要先将app中的cpp文件夹删除后继续进行搭建。  

* __配置插件__  
  
我们借助强大的Android Studio的插件功能，在External Tools下配置三个非常有用的插件。  
进入File->Settings–>Tools–>ExternalTools，点击+号增加。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/tool1.png)  
  
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
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/javah1.png)  
    - 添加完成后应如下图所示：  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/javah2.png)  
  
2. Ndk-bulid命令  
    - Name：ndk -bulid  
    > 该命令是用于根据C/C++文件生成so文件的。  
    - Description：根据C/C++文件生成so文件  
    - Program: 这里配置的是ndk下的ndk-build.cmd的路径，根据自己安装的实际情况填写。  
    - Working directory:$ProjectFileDir$$ModuleFileDir$\src\main\  
    - 使用方式：选中C/C++文件—>右键—>ExternalTools—>ndk-build，将在main文件夹下生成libs文件夹以及多个so文件，我们可以移动至jniLibs目录下去。 
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/ndk1.png)  
  
3. Ndk-bulid clean命令  
    - Name：ndk-bulid clean  
    > 该命令用来清理生成的二进制文件和目标文件。  
    - Description：清理生成文件  
    - Program: 这里和ndk -bulid命令一样配置的是ndk下的ndk-build.cmd的路径，根据自己安装的实际情况填写。
    - Arguments:clean  
    - Working directory:$ProjectFileDir$\app\src\main  
    > 这里指调用project目录下的app\src\main目录。  
  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/ndk2.png)  
  
全部配置完成后点击OK完成插件配置。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/tool2.png)  

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
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/change1.png)  

2. 修改MyNdk下的gradle.properties文件  
    - 增添以下代码：
    ```
    android.useDeprecatedNdk=true
    ```
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/change2.png)  

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
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/newjava.png)  

2. 右击新建的类找到刚刚新建的插件工具external tools->javah -jni，完成后生成一个有.h类的jni文件夹  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/newjava1.png)  

3. 在jni下创建Android.mk和Application.mk以及hello.cpp三个文件  
    - .mk文件可以通过直接新建一个file，再输入文件名+后缀.mk创建，之后会提示安装相关文件。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/newjava2.png)  

4. 在hello.cpp文件下输入如下代码段：  

    ```
    #include "com_example_myapplication_jnitest.h"
    #include<jni.h>
    JNIEXPORT jstring JNICALL Java_com_example_myapplication_jnitest_getString
            (JNIEnv *env, jobject obj){
        return (*env).NewStringUTF("This is mylibrary !!!");
    }
    ```  

    - 注意：第一行代码的com_example_myapplication_jnitest.h和  
           第三行代码的Java_com_example_myapplication_jnitest_getString  
           根据每个人自己的命名进行相应修改。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/newjava3.png)  

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
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/newjava4.png)  
    - 这里将libs文件夹改名为jniLibs，这个文件夹名是默认的访问地址，注意大小写。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/newjava5.png)  

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
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/newjava6.png)  

* 配置好NDK后，Android Studio会自动生成C++文件并设置好调用的代码。只需要根据需求修改C++文件就可以使用了。  

*** 

### （3）使用cmake方式安装

* ndk开发除了通过ndk_build，采用前面所述的Android.mk+Application.mk+src方式，cmake是现在最新版本as的ndk默认方式,采用CmakeLists.txt+src的方式安装相关文件。    
  在开始使用cmake安装之前，要确保已经按照之前的教程下载了Cmake构建工具、LLDB调试工具和NDK开发工具集，并创建了支持C/C++的新项目。  
  
* 创建完成后同样需要按照之前的教程配置NDK。全部配置完毕后，直接点击运行，可以发现Android Studio已经帮我们自动生成了一个可以运行的cpp文件（如下图所示），只需要根据需求修改native-lib.cpp文件以及Android就可以使用了。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/camke1.png)  

***

* __已有项目结构与普通项目的一些区别__  
1. app里的build.gradle配置比对代码可以发现里面面添加了两处externalNativeBuild配置项：  
    - defaultConfig里面的配置项：主要配置了Cmake的命令参数。  
    - defaultConfig外面的配置项：主要定义了CMake的构建脚本CMakeLists.txt的路径。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/cmake2.png)  
    
2. CMake的构建脚本CMakeLists.txt：  
    - CMakeLists.txt是CMake的构建脚本，在其中包含 CMake 构建 C/C++ 库时需要使用的命令。作用相当于ndk-build中的Android.mk。  
    - 更多详细的脚本配置可以参考这个中文版的[CMAKE手册](https://www.zybuluo.com/khan-lau/note/254724)
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/cmake3.png)  

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
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/cmake4.png)  

***

* __搭建cmake方式__  
1. 在模块的build.gradle文件中添加cmake的设置。  
    - cppFlags：是交给C++编译器的参数。  
    - path：CMakeLists.txt文件目录的地址，文件可以放在别的目录，地址在这里声明就行，不一定在build.gradle同目录。  
    
2. 在一个新创建的项目的build.gradle中添加以下代码段：  
    ```
    android {
        compileSdkVersion 26
        buildToolsVersion "26.0.0"
        defaultConfig {
            externalNativeBuild {
                cmake {
                    cppFlags ""
                }
            }
        }
        externalNativeBuild {
            cmake {
                path "CMakeLists.txt"
            }
        }
        ndk{
            moduleName "native-lib"
            abiFilters "x86", "x86_64", "armeabi-v7a", "arm64-v8a"
        }
    }
    dependencies {
        implementation fileTree(dir: 'libs', include: ['*.jar'])
        implementation 'com.blankj:utilcode:1.25.9'
        implementation 'androidx.appcompat:appcompat:1.1.0'
        implementation 'com.google.android.material:material:1.0.0'
        implementation 'com.jakewharton:butterknife:10.2.0'
        annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.0'
        //导入另一个ndk工程的配置方法，这是后面我们自己的工程使用的别人的7z解压方式的工程
        implementation project(':un7zip')
        implementation 'com.getkeepsafe.relinker:relinker:1.3.1'
        implementation 'androidx.appcompat:appcompat:1.1.0'
        implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
        testImplementation 'junit:junit:4.12'
        androidTestImplementation 'androidx.test.ext:junit:1.1.1'
        androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
    }
    ```

***

* __编写cpp程序__  
1. 在CmakeLists的同一路径下编写cpp程序文件名要与在CmakeLists中所留的保持一致，如果想使用标准cpp库，或者各种新的cpp标准中的特性，可在build.gradle中额外设置如下字段：  
    ```
    android {
        defaultConfig {
            externalNativeBuild {
                cmake {
                    cppFlags "-std=c++11"
                    arguments "-DANDROID_ARM_NEON=TRUE", "-DCMAKE_BUILD_TYPE=Release"
                }
            }
        }
    }
    ```
    - 以上设置代表了程序使用cpp的11标准。  

***

* __生成so文件__
    - 在Cmakelists.txt和native-lib.cpp都准备好后，只需要build->Make project或者rebuild就可以生成。  
    - native-lib.cpp在创建c++支持的project时android studio会自动生成。  
    - So文件会生成在build-->intermediates-->cmake-->debug-->obj下。  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/cmake5.png)  

***

### 2、Android Studio2.2 以下版本实现NDK配置：
Android Studio2.2以下版本并没有内部集成ndk，手动配置部分较多，虽然现在都已经使用了新版本的AS，但是旧版本的这些配置方式并不是在新版本中不使用了，而是新版本自动帮我们完成了配置，但这种配置只是默认配置，要想个性化的使用ndk,满足特殊的要求，繁琐的配置是不可或缺的。  

### （1）配置 Android NDK环境  

* __下载Android NDK工具包__
地址：官网——<https://developer.android.com/ndk/downloads/index.html>  

* __解压NDK包__  
注意：解压路径不要出现空格和中文。  
将解压路径设置为：Android Studio的SDK目录里，并命名为ndk-bundle，这样做的好处是启动Android Studio时，Android Studio会自动检查它并直接添加到ndk.dir中，那么在使用时，就不用配置Android Studio与NDK的关联工作。当然后续步骤当您未按此路径做进行讲解。  

* __安装配置NDK__  
在终端依次输入下列命令:  
1. 先输入以下命令:
    ```
    pico .bash_profile
    ```
    
2. 再依次输入下列命令（后面的路径需要根据实际NDK解压路径设置）:  
    ```
    export PATH=${PATH}:/Users/Carson_Ho/Library/Android/sdk/ndk-bundle 
    A_NDK_ROOT=/Users/Carson_Ho/Library/Android/sdk/ndk-bundle
    export A_NDK_ROOT
    ```
    - 注意检查空格、中英字符区分。  
    
3. 输入以下组合命令,进行保存:  
    ```
    control＋X
    ```
    - 输入后，选择Y。  
    
4. 最后，更新刚配置的环境变量：  
    ```
    source .bash_profile
    ```
    
5. 验证NDK是否配置成功:  
    - 关闭终端并重新打开。  
    - 若无错误提示，则成功配置。  

![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/extra1.png)  

***

### （2）关联Andorid Studio项目与NDK

* 当项目每次需要使用NDK时，都需要将该项目关联到NDK，此处使用的是Andorid Studio，与Eclipse不同，还在使用Eclipse的同学请自行查找资料配置。

* __在Gradle的 local.properties中添加配置：__  
    - 若ndk目录存放在SDK的目录中，并命名为ndk-bundle，则该配置自动添加。  
    - 在文件中添加如下代码段：  
    ```
    ndk.dir=/Users/Carson_Ho/Library/Android/sdk/ndk-bundle
    ```
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/extra2.png)  

* __在Gradle的 gradle.properties中添加配置:__  
    - 在文件中添加如下代码段：  
    ```
    android.useDeprecatedNdk=true
    ```
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/extra3.png)  

* __在Gradle的build.gradle添加ndk节点:__  
    - 在文件中添加如下代码段：  
    ```
    ndk{
            moduleName "hello_jni"
            stl "stlport_static"
            ldLibs "log"
        }
    ```
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/extra4.png)  

***

### （3）创建本地代码文件

* __c与c++代码使用方式上的不同:__  
1. c语言：以hello.c为例:  
    - 在C中没有引用，传递的env是个两级指针，用（* env）->调用方法且方法中要传入env。  
    - 代码段如下所示：  
    ```
    #include <jni.h>
    jstring Java_com_example_Hello_hello(JNIEnv* env, jobject thiz) {
        return (*env)->NewStringUTF(env,"Hello Jni---->C!");
    }
    ```
    - Android.mk文件,更改后缀名为.c，代码段如下所示：  
    ```
    LOCAL_PATH := $(call my-dir)
    include $(CLEAR_VARS)
    LOCAL_MODULE    := hello
    LOCAL_SRC_FILES := hello.c
    include $(BUILD_SHARED_LIBRARY)
    ```
    
2. c++语言：以hello.cpp为例  
    - C++中env为一级指针，用env->调用方法，无需传入env；C++语言在编译的时候为了解决函数的多态问题，会将函数名和参数联合起来生成一个中间的函数名称，而C语言则不会，因此会造成链接时找不到对应函数的情况，此时C函数就需要用extern "C"进行链接指定，这告诉编译器：“请保持我的名称，不要给我生成用于链接的中间函数名”；exter "C"{jni代码}。  
    - 具体代码如下所示：  
    ```
    #include <jni.h>
    
    #ifdef __cplusplus
    extern "C" {
    #endif
    jstring Java_com_example_Hello_hello(JNIEnv* env, jobject thiz) {
        return env->NewStringUTF("Hello Jni---->C++!");
    }
    #ifdef __cplusplus
    }
    #endif
    ```
    - Android.mk文件,更改后缀名为.cpp，代码段如下所示：  
    ```
    LOCAL_PATH := $(call my-dir)
    include $(CLEAR_VARS)
    LOCAL_MODULE    := hello
    LOCAL_SRC_FILES := hello.cpp
    include $(BUILD_SHARED_LIBRARY)
    ```

***

* __创建本地代码文件 此处采用C++作为展示:__  
    - 如果本地代码是C++（.cpp或者.cc），要使用extern "C" { }把本地方法括进去。  
    - JNIEXPORT jstring JNICALL中的JNIEXPORT和JNICALL不能省。  
    - 关于方法名Java_scut_carson_1ho_ndk_1demo_MainActivity_getFromJNI：  
    > + 格式 = Java _ 包名 _ 类名 _ Java需要调用的方法名  
    > + Java必须大写  
    > + 对于包名，包名里的.要改成_，_要改成_1,如果这里的包名是：scut.carson_ho.ndk_demo，则需要改成scut_carson_1ho_ndk_1demo  
    - 最后，将创建好的test.cpp文件放入到工程文件目录中的src/main/jni文件夹  
    - 若无jni文件夹，则手动创建  
    - 完整代码段如下：  
    ```
    # include <jni.h>
    # include <stdio.h>
    extern "C"
    {
        JNIEXPORT jstring JNICALL Java_scut_carson_1ho_ndk_1demo_MainActivity_getFromJNI(JNIEnv *env, jobject obj ){
           // 参数说明
           // 1. JNIEnv：代表了VM里面的环境，本地的代码可以通过该参数与Java代码进行操作
           // 2. obj：定义JNI方法的类的一个本地引用（this）
        return env -> NewStringUTF("Hello i am from JNI!");
        // 上述代码是返回一个String类型的"Hello i am from JNI!"字符串
        }
    }
    ```

***

### （4）创建 Android.mk文件和Application.mk文件

* __创建Android.mk文件:__  
    - 创建Android.mk文件，放在src/main/jni文件夹中。  
    - 作用：指定源码编译的配置信息，如工作目录，编译模块的名称，参与编译的文件等。  
    - 具体代码如下所示：  
    ```
    LOCAL_PATH       :=  $(call my-dir)
    // 设置工作目录，而my-dir则会返回Android.mk文件所在的目录

    include              $(CLEAR_VARS)
    // 清除几乎所有以LOCAL——PATH开头的变量（不包括LOCAL_PATH）

    LOCAL_MODULE     :=  hello_jni
    // 设置模块的名称，即编译出来.so文件名
    // 注，要和上述步骤中build.gradle中NDK节点设置的名字相同

    LOCAL_SRC_FILES  :=  test.cpp
    // 指定参与模块编译的C/C++源文件名

    include              $(BUILD_SHARED_LIBRARY)
    // 指定生成的静态库或者共享库在运行时依赖的共享库模块列表。
    ```
    
* __创建Application.mk文件:__  
    - 创建Application.mk文件，放在src/main/jni文件夹中。  
    - 作用：配置编译平台相关内容。  
    - 具体代码如下所示：  
    ```
    APP_ABI := armeabi
    // 最常用的APP_ABI字段：指定需要基于哪些CPU平台的.so文件
    // 常见的平台有armeabi x86 mips，其中移动设备主要是armeabi平台
    // 默认情况下，Android平台会生成所有平台的.so文件，即同APP_ABI := armeabi x86 mips
    // 指定CPU平台类型后，就只会生成该平台的.so文件，即上述语句只会生成armeabi平台的.so文件
    ```

***

### （5）生成.so库文件，并放入到工程文件中

* __编译上述文件，生成.so库文件__  
    - 打开终端，输入以下命令:  
    ```
    // 步骤1：进入该文件夹
    cd /Users/Carson_Ho/AndroidStudioProjects/NDK_Demo/app/src/main/jni 
    // 步骤2：运行NDK编译命令
    ndk-build
    ```
    - 编译成功后，在src/main/会多了两个文件夹libs和obj，其中libs下存放的是.so库文件，有时也会同时生成.a文件。  
    
* __在src/main/中创建一个名为jniLibs的文件夹，并将上述生成的so文件夹放到该目录下__
    - 要把名为 CPU平台的文件夹放进去，而不是把.so文件放进去。  
    - 如果本来就有.so文件，那么就直接创建名为jniLibs的文件夹并放进去就可以。  
    - jnilibs是使用.so文件的默认路径。但也不是固定的，如果像想使用自己创建的路径，可以在配置文件中指定路径。  

***

### （6）在Andoird Studio项目中使用NDK实现JNI功能

* 此时，我们已经将本地代码文件编译成.so库文件并放入到工程文件中，接下来只要编辑MainActivity文件就可以实现在Andoird Studio项目中使用NDK实现JNI功能。 

* 具体代码如下所示：  
    ```
    public class MainActivity extends AppCompatActivity  {

    // 步骤1:加载生成的so库文件
    // 注意要跟.so库文件名相同
    static {

        System.loadLibrary("hello_jni");
    }
    
    // 步骤2:定义在JNI中实现的方法
    public native String getFromJNI();
    
    // 此处设置了一个按钮用于触发JNI方法
    private Button Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 通过Button调用JNI中的方法
        Button = (Button) findViewById(R.id.button);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button.setText(getFromJNI());
                
            }
        });
    }
    ```
    
* 至此，NDK在Andoird Studio2.2版本以下配置已经全部完成，可以在项目中愉快的使用NDK了。  

***

## 四、问题及解决办法

### 1、问题：Android.mk must not contain space 以及 Defaulting to minimum supported version android=16  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/question1.png)  

* __原因：__  
第一个问题是Android.mk文件中含有空格，第二个问题是平台版本不够。  

* __解决方法：__  
第一个问题的解决方法是去掉所有空格即可（包括注释，最好把注释全删除）。  
第二个问题的解决方法是在Application.mk 中加上以下代码行：  
    ```
    APP_PLATFORM := android-16
    ```
    - 不过还是会出现warning,但是不影响运行。  
    
***

### 2、问题：error：invalid preprocessing directive  #include “jnitest.h”  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/question2.png)  

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
* [Android NDK Jni 开发C和C++的区别](https://www.cnblogs.com/gengchangjing/p/ndk.html)

