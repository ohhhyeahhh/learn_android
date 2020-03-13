# Android-NDK t&p/about ndk
## 一、概念
### NDK
* __定义:__  
Android Native Development Kit，简称NDK。  
Native Development Kit（NDK）是一系列工具的集合。它提供了一系列的工具，帮助开发者快速开发C/C++的动态库，并能自动将so和java一起打包成apk。
* __优点：__
    > 1. 项目需要调用底层的一些C/C++的一些东西（java无法直接访问到操作系统底层（如系统硬件等）），或者已经在C/C++环境下实现了功能代码（大部分现存的开源库都是用C/C++代码编写的。），直接使用即可。NDK开发常用于驱动开发、无线热点共享、数学运算、实时渲染的游戏、音视频处理、文件压缩、人脸识别、图片处理等。  
    > 2. 为了效率更加高效些。将要求高性能的应用逻辑使用C/C++开发，从而提高应用程序的执行效率。但是C/C++代码虽然是高效的，在java与C/C++相互调用时却增大了开销；  
    > 3. 基于安全性的考虑。防止代码被反编译，为了安全起见，使用C/C++语言来编写重要的部分以增大系统的安全性，最后生成so库（用过第三方库的应该都不陌生）便于给人提供方便。（任何有效的代码混淆对于会smail语法反编译你apk是分分钟的事，即使你加壳也不能幸免高手的攻击）  
    > 4. 便于移植。用C/C++写得库可以方便在其他的嵌入式平台上再次使用。  
### JNI
Java Native Interface，简称JNI。  
Java Native Interface（JNI）标准是java平台的一部分，JNI是Java语言提供的Java和C/C++相互沟通的机制，Java可以通过JNI调用C/C++代码，C/C++的代码也可以调用java代码。  
### JNI与NDK的关系
NDK可以为我们生成了C/C++的动态链接库，JNI是java和C/C++沟通的接口，两者与android没有半毛钱关系，只因为安卓是java程序语言开发，然后通过JNI又能与C/C++沟通，所以我们可以使用NDK+JNI来实现“Java+C”的开发方式。 
### CMake
允许开发者编写一种平台无关的 CMakeList.txt 文件来定制整个编译流程，然后再根据目标用户的平台进一步生成所需的本地化 Makefile 和工程文件，如 Unix 的 Makefile 或 Windows 的 Visual Studio 工程。从而做到“Write once, run everywhere”。  
## 二、安装与环境配置
---等待后续完善---
## 三、问题及解决办法
---等待后续完善---
