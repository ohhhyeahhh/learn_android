# Android-NDK demo/about

* 这里主要是关于项目实例代码的相关概念说明介绍以及问题汇总。  

***

## 一、相关概念

### 1、RAR压缩
* Roshal ARchive，简称RAR。  

* RAR是一种专利文件格式，用于数据压缩与归档打包，开发者为尤金·罗谢尔。首个公开版本RAR 1.3发布于1993年。  

* 虽然RAR是一款较为常见的压缩格式，但由于这是一种商用软件，所以并没有开源代码，这里不做详细介绍。   

### 2、7Z压缩
* 7z 是一种主流高效的压缩格式，它拥有极高的压缩比。在计算机科学中，7z是一种可以使用多种压缩算法进行数据压缩的档案格式。  

* 该格式最初被7-Zip实现并采用，但是这种档案格式是公有的，并且7-Zip软件本身亦在GNU宽通用公共许可证 (GNU LGPL)协议下开放源代码。LZMA软件开发工具包的最新版本为V9.34。7z格式的MIME类型为application/x-7z-compressed。  

* 主要特征：  

> 开源且模块化的组件结构（允许使用任何压缩，转换或加密算法）  
> 强大的 AES-256 加密  
> 具有最高的压缩比  
> 可更改和配置压缩的算法  
> 支持超大文件（最大支持到16EB）  
> Unicode文件名支持  
> 支持固实压缩，容许内类的档案在用一个串流中压缩，使类似的内容被有效的压缩  
> 支持档案的文件头压缩  
> 支援多线程压缩  

* 7z压缩格式的算法：  

|  压缩格式   | 备注  |  
|  ----  | ----  |  
| LZMA  | 改良与优化后的 LZ77 算法 |  
| LZMA2  | 改良的 LZMA 算法 |  
| PPMD  | 基于 Dmitry Shkarin 的 PPMdH 算法 |  
| BCJ  | 32 位 x86 可执行文件转换程序 |  
| BCJ2  | 32 位 x86 可执行文件转换程序 |  
| BZip2  | 标准 BWT 算法 |  
| Deflate  | 标准 LZ77-based 算法 |  

***

### 3、Zip压缩
* ZIP文件格式是一种数据压缩和文档储存的文件格式，原名Deflate，发明者为菲尔·卡茨（Phil Katz），他于1989年1月公布了该格式的资料。  

*  ZIP通常使用后缀名“.zip”，它的MIME格式为application/zip。  

* 当前，ZIP格式属于几种主流的压缩格式之一，其竞争者包括RAR格式以及开放源码的7z格式。从性能上比较，RAR及7z格式较ZIP格式压缩率较高，而7-Zip由于提供了免费的压缩工具而逐渐在更多的领域得到应用。Microsoft从Windows ME操作系统开始内置对zip格式的支持，即使用户的计算机上没有安装解压缩软件，也能打开和制作zip格式的压缩文件，OS X和流行的Linux操作系统也对zip格式提供了类似的支持。因此如果在网络上传播和分发文件，zip格式往往是最常用的选择。  

* 压缩方法：  
1. Shrinking  
收缩（Shrinking）是LZW的微小调整的一个异体，同样也受到LZW专利问题的影响。  

2. Reducing  
缩小（Reducing）包括压缩重复字节序列的组合，然后应用一个基于概率的编码得到结果。  

3. Imploding  
爆聚（Imploding）包括使用一个滑动窗口压缩重复字节序列，然后使用多重Shannon-Fano树压缩得到结果。  

4. Tokenizing  
令牌化（Tokenizing）的方法数是保留的。PKWARE规约没有为其定义一个算法。  

5. Deflate和增强的Deflate  
这些方法使用众所周知的Deflate算法。Deflate允许最大32K的窗口。增强的Deflate允许最大64K的窗口。增强版完成任务稍稍成功一些，但是并没有被广泛的支持。
Deflate比较尺寸是52.1MiB（使用pkzip for Windows，版本8.00.0038测试）  
增强的Deflate比较尺寸是52.8MiB（使用pkzip for Windows，版本8.00.0038测试）  

6. PKWARE Data Compression Library Imploding  
PKWARE数据压缩库爆聚（PKWARE Data Compression Library Imploding），官方ZIP格式规约就此没有给出更多的信息。  
比较尺寸是61.6MiB（使用pkzip for Windows，版本8.00.0038测试，选择二进制模式）  

7. Bzip2  
此方法使用众所周知的bzip2算法。此算法比deflate高效但是并没有被（基于Windows平台的）工具所支持。  
比较尺寸是50.6MiB（使用pkzip for Windows，版本8.00.0038测试）  

***

### 4、zlib
* zlib 是通用的压缩库，提供了一套 in-memory 压缩和解压函数，并能检测解压出来的数据的完整性(integrity)。zlib 也支持读写 gzip (.gz) 格式的文件。  

* 默认且目前仅使用deflate算法压缩data部分；deflate是一种压缩算法,是huffman编码的一种加强。  

* zlib中主要函数解析：
1. 压缩函数  
    ```
    int compress(unsigned char * dest, unsigned long * destLen, unsigned char *source, unsigned long sourceLen);
    ```
    > dest：压缩后数据保存的目标缓冲区  
    > destLen：目标缓冲区的大小（必须在调用前设置，并且它是一个指针）  
    > source：要压缩的数据  
    > sourceLen：要压缩的数据长度  
    
    - compress()函数成功返回Z_OK，如果内存不够，返回Z_MEM_ERROR，如果目标缓冲区太小，返回Z_BUF_ERROR  
    
    ```
    int compress2 (Bytef *dest, uLongf *destLen, const Bytef *source, uLongsourceLen, int level); 
    ```
    > level: 相比前一个函数增加了压缩级别  
    
2. 解压缩函数  
    ```
    int uncompress(unsigned char * dest,  unsigned long * destLen, unsignedchar * source, unsigned long sourceLen);
    ```
    > dest：解压后数据保存的目标缓冲区  
    > destLen：目标缓冲区的大小（必须在调用前设置，并且它是一个指针）  
    > source：要解压的数据  
    > sourceLen：要解压的数据长度  
    
    - uncompress()函数成功返回Z_OK，如果内存不够，返回Z_MEM_ERROR，如果目标缓冲区太小，返回Z_BUF_ERROR，如果要解压的数据损坏或不完整，返回Z_DATA_ERROR。  

***

## 二、相关问题及解决方法  
1. error LNK2026: 模块对于 SAFESEH 映像是不安全的  
    - 解决办法：  
    ①zlibvc-属性-配置属性-链接器-命令行 在后面加上 /SAFESEH:NO  
    ②testzlib-属性-配置属性-链接器-命令行 在后面加上 /SAFESEH:NO  
    
2. fatal error LNK1118: “VERSION”语句中的语法错误  
    - 解决办法：找到报错位置，zlibvc.def文件第4行的VERSION语句行，直接删除该行或者在行首加上;号进行注释。  

3. 报错：无法定位程序输入点CreateFile2于动态链接库KERNEL32.dll上  
    - 解决办法：
    ①找到iowin32.c文件，在第一行位置上增加宏判断：  
    ```
    #if _WIN32_WINNT >= _WIN32_WINNT_WIN8
    ```
    最后一行加上 #endif  
    ②zlibvc-属性-配置属性-C/C++-预处理器定义 加入一行 _WIN32_WINNT=0x0601  
    
4. 问题具体见下图：  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/q1.png)  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/q2.png)  
    - 原因：  
    以上两种错误，第一种是提示找不到iostream头文件，第二个是无法识别iostream头文件相关的代码。在本案例中错误二是对错误一进行改正后又出现的错误，这里的原因是本案例中LOCAL_SRC_FILES是mytest.c，并且又在mytest.c中加入了C++头文件而产生的。  
    - 解决方法：  
    使用.cpp类型的源文件。  
    
5. 问题具体见下图：  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/q3.png)  
    - 原因：语法错误。  
    - 解决办法：  
    在.cpp源文件中使用C语言，C语言对应的jni语言和C++对应的jni语言是有所差异的，在直接套用前应该进行一定的修改。  
    
6. 问题具体见下图：  
![download ndk](https://github.com/Shadowmeoth/save_image/blob/master/image/q4.png)  
    - 原因：  
    上述头文件crypt.h，是生成的静态库libzlib中zip.c源文件所需要的头文件，但是它并没有对应的.c源文件，所以无法像其他还有源文件的头文件一样直接在LOCAL_SRC_FILES中加入对应源文件。  
    - 解决办法：  
    将LOCAL_SRC_FILES直接写在mytest.cpp的开头所需的头文件里，并将该文件放在mytest.cpp同级目录下。  
    
7. undefined reference to报错：  
    - 一般编译器报 “undefined reference to”的错误是以下几种情况：  
    - （1）没有指定对应的库（.o/.a/.so）  
        > 使用了库中定义的实体，但没有指定库（-lXXX）或者没有指定库路径（-LYYY），会导致该错误。  
        > 在Android.mk中 用LDFLAGS参数来定义库（-lXXX）和（-LYYY）的。  
        
    - （2）连接库参数的顺序不对  
        > 在默认情况下,对于-l 使用库的要求是越是基础的库越要写在后面,无论是静态还动态。  
        > -D参数的使用，也会导致“undefined reference to”的错误，如果想在做宏控制的时候，建议把-D参数放到最后。  

    - （3）gcc/ld 版本不匹配   
        > gcc/ld的版本的兼容性问题,由于gcc2 到 gcc3大版本的兼容性存在问题(其实gcc3.2到3.4也一定程度上存在这样的问题)。  
        > 当在高版本机器上使用低版本的机器就会导致这样的错误, 这个问题比较常见在32位的环境上, 另外就在32位环境不小心使用了64位的库或者反过来64位环境使用了32位的库。这个问题与Linux下几乎一样。  
        
    - （4）C/C++相互依赖和链接  
        > gcc和g++编译结果的混用需要保证能够extern "C" 两边都可以使用的接口,在我们的64位环境中gcc链接g++的库还需要加上-lstdc++，具体见前文对于混合编译的说明。  
        > 需要用JNI链接纯C语言的库，即在extern “C”的使用时候，必须要包含头文件。  

    - （5）运行期报错  
        > 这个问题基本上是由于程序使用了dlopen方式载入.so, 但.so没有把所有需要的库都链接上,具体参加上文中对于静态库和动态库混合使用的说明。  
        > 关于执行时动态链接的问题，在Android链接的库依赖于现在当前的库，就变成了你链接我，我再链接你，导致死都编不过，最好撇清这种关系。  

***

## 三、相关注意点

* __界面相关：__  
1. Activity的java文件中，onCreate(Bundle savedInstanceState)函数内：  
    ```
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    ```
    - 以上两句的顺序不能更改，否则app页面中的图片将无法显示，而且该错误并不会产生报错。  
    
* __调用so库相关：__  
1. 如果xxxx.cpp需要引用一些c++头文件，那么就需要在Android.mk中加入之前所述的c++头文件库，否则会无法找到这些头文件。  
    - 最好在Application.mk中加入以下代码行：  
    ```
    APP_STL := c++_static
    ```

2. 如果创建的是xxxx.c，那么即使如上一个问题中加入c++头文件库，也会无法识别c++语言而报错，建议如果要使用c++头文件的话请生成.cpp文件。  

3. .c和.cpp中的jni语言的语法是不同的，编写的时候请注意。不过似乎单纯引入头文件而不写具体的函数功能也能编译通过生成.so。  

***

## 参考资料
* [RAR-百度百科](https://baike.baidu.com/item/rar/2502036?fr=aladdin)  
* [7z-百度百科](https://baike.baidu.com/item/7Z/3651842?fr=aladdin)  
* [zip-百度百科](https://baike.baidu.com/item/Zip/16684862?fr=aladdin)  
* [zlib库使用简单讲解](https://blog.csdn.net/t146lla128xx0x/article/details/80429149)  
* [zlib 2.1.8 编译遇到的问题以及解决方法](https://www.cnblogs.com/chevin/p/5676317.html)  

