# Android-NDK t&p/about ndk
## 一、概念
### 1、NDK
* __定义:__  
Android Native Development Kit，简称NDK。  
Native Development Kit（NDK）是一系列工具的集合。它提供了一系列的工具，帮助开发者快速开发C/C++的动态库，并能自动将so和java一起打包成apk。
* __优点：__
    > + 项目需要调用底层的一些C/C++的一些东西（java无法直接访问到操作系统底层（如系统硬件等）），或者已经在C/C++环境下实现了功能代码（大部分现存的开源库都是用C/C++代码编写的。），直接使用即可。NDK开发常用于驱动开发、无线热点共享、数学运算、实时渲染的游戏、音视频处理、文件压缩、人脸识别、图片处理等。  
    > + 为了效率更加高效些。将要求高性能的应用逻辑使用C/C++开发，从而提高应用程序的执行效率。但是C/C++代码虽然是高效的，在java与C/C++相互调用时却增大了开销；  
    > + 基于安全性的考虑。防止代码被反编译，为了安全起见，使用C/C++语言来编写重要的部分以增大系统的安全性，最后生成so库（用过第三方库的应该都不陌生）便于给人提供方便。（任何有效的代码混淆对于会smail语法反编译你apk是分分钟的事，即使你加壳也不能幸免高手的攻击）  
    > + 便于移植。用C/C++写得库可以方便在其他的嵌入式平台上再次使用。  

### 2、JNI
Java Native Interface，简称JNI。  
Java Native Interface（JNI）标准是java平台的一部分，JNI是Java语言提供的Java和C/C++相互沟通的机制，Java可以通过JNI调用C/C++代码，C/C++的代码也可以调用java代码。  

### 3、JNI与NDK的关系
NDK可以为我们生成了C/C++的动态链接库，JNI是java和C/C++沟通的接口，两者与android没有半毛钱关系，只因为安卓是java程序语言开发，然后通过JNI又能与C/C++沟通，所以我们可以使用NDK+JNI来实现“Java+C”的开发方式。 

### 4、CMake
允许开发者编写一种平台无关的 CMakeList.txt 文件来定制整个编译流程，然后再根据目标用户的平台进一步生成所需的本地化 Makefile 和工程文件，如 Unix 的 Makefile 或 Windows 的 Visual Studio 工程。从而做到“Write once, run everywhere”。  

***

## 二、安装与环境配置
### 1、新建文件
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

### 2、使用mk方式安装

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
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change2.png)  

2. 右击新建的类找到刚刚新建的插件工具external tools->javah -jni，完成后生成一个有.h类的jni文件夹  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change2.png)  

3. 在jni下创建Android.mk和Application.mk以及hello.cpp三个文件  
    - .mk文件可以通过直接新建一个file，再输入文件名+后缀.mk创建，之后会提示安装相关文件。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change2.png)  

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
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change2.png)  

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
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change2.png)  
    - 这里将libs文件夹改名为jniLibs，这个文件夹名是默认的访问地址，注意大小写。  
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change2.png)  

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
![download ndk](https://github.com/Shadowmeoth/learn_android/blob/master/ndk/t%26p/image/change2.png)  

*** 

### 3、使用cmake方式安装

## 三、问题及解决办法
---等待后续完善---

## 参考资料
* <https://www.jianshu.com/p/16f6a3e3fc45>
* <https://blog.csdn.net/xiaoyu_93/article/details/52870395>
* <https://blog.csdn.net/chuhongcai/article/details/52558049>
