# Android OpenCV

## 前言

- OpenCV是一个基于BSD许可（开源）发行的跨平台计算机视觉库，可以运行在Linux、Windows和Mac OS操作系统上。它轻量级而且高效——由一系列 C 函数和少量 C++ 类构成，同时提供了Python、Ruby、MATLAB等语言的接口，实现了图像处理和计算机视觉方面的很多通用算法。

- Android是由Google领导的开放手机联盟开发的基于Linux的开源移动操作系统。有关详细信息，请参阅Android教程。
Android的开发与其他平台的开发显着不同。所以在开始编程Android之前，建议熟悉以下主题：
	1. Java编程语言是Android操作系统的主要开发技术。此外，还可以在Java上找到有用的Java文档。
	1. Java本机接口（JNI）是Java虚拟机中运行本机代码的技术。此外，还可以在JNI上找到Oracle文档。
	1. Android Activity及其生命周期，这是一个必不可少的Android API类。
	1. OpenCV的开发一定要了解Android Camera的具体细节。

## 基于 Android 的 OpenCV 开发环境搭建

### OpenCV SDK 准备
采用直接加载openCV官方提供的Android SDK来进行导入：

#### 首先 OpenCV 下载地址： [这里](https://opencv.org/releases/ "这里")
1. 进入后选择 Android SDK 点击下载即可
1. 下载成功后解压得到我们要的 OpenCV 资源

### 将 OpenCV 导入到项目中去
1. 新建 Android 项目
1. 导入 OpenCV SDK 
	1. 导入刚刚下载解压到 SDK 包：选择 File->new->Import Module
	1. 选择刚刚下载解压得到到 SDK 文件，点击open
	1. 设置 Module 名称，Finish
	1. 在 APP 模块中引入新到 module

    `dependencies{
		implementation fileTree(dir:'libs',include:['*.jar'])
        implementation"org.jetbrains.kotlin:kotlin-stdlib-jdk:$kotlin_version"
        implementation 'com.android.support:appcompat-v7:28.0.0'
        implementation 'com.android.support.constraint:constraint-layout:1.1.3'
        testImplementation 'junit:junit:4.12'
        androidTestImplementation 'com.android.support.test:runner:1.0.2'
        androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
        implementation project(":opencv")
    }
`
## 基于 Android OpenCV 的高斯差分
1. 引用 OpenCV 的依赖库
这里为了不用加载 OpenCV 的依赖 APK ，我们在需要使用的地方需要加载 OpenCV SDK 提供的依赖库。

    	init{
			System.loadLibrary("opencv_java3")
		}
		
1. 下面我们简单介绍一个有关图片的效果处理：高斯差分
分为下面几个步骤：
	1. 将图像转换成灰度
	1. 进行两次不同大小的高斯模糊
	1. 将两次模糊后的图像相减
	1. 反转二值阈值化

**代码实现:**
    

    fun differenceOfGaussian() {
       	 imageBitmap?.apply {val grayMat = Mat()
            val blur1 = Mat()
            val blur2 = Mat()
            // 将图像转换成灰度图像
            val originalMat = Mat(height, width, CvType.CV_8UC4)
            Utils.bitmapToMat(imageBitmap, originalMat)
            Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGRA2GRAY)
            // 进行高斯模糊
            Imgproc.GaussianBlur(originalMat, blur1, Size(15.0, 15.0), 5.0)
            Imgproc.GaussianBlur(originalMat, blur2, Size(21.0, 21.0), 5.0)
            // 将两幅模糊后的图像相减
            val doG = Mat()
            Core.absdiff(blur1, blur2, doG)
    
            // 反转二值阈值化
            Core.multiply(doG, Scalar(100.0), doG)
            Imgproc.threshold(doG, doG, 50.0, 255.0, Imgproc.THRESH_BINARY_INV)
            val resultBitmap = Bitmap.createBitmap(doG.cols(), doG.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(doG, resultBitmap)
            imageResult.setImageBitmap(resultBitmap)
        }
    

## 基于 Android OpenCV 的图片处理

在进行图像处理的一些高级操作时候，我们需要对图片进行预处理，以过滤一些不必要对信息，缩短计算时间，以下是图片处理操作
- 灰度
- 模糊
- 降噪
- 锐化
- 腐蚀和膨胀
- 阈值化、自适应阈值
- 直方图均衡

### OpenCV 中图像对存储

OpenCV有提供相关对工具类：**Mat**
**Mat**对象保存来图片对行数（高度）、列数（宽度）、通道（颜色通道）、图片数据等相关信息，并封装来一些图片等操作方法。

### 利用 OpenCV 处理图片

#### 一、灰度处理

在进行许多复杂的图像处理之前，都需要将图像转换成灰度单通道。
在没有使用 OpenCV 之前，在 Android 上操作 Bitmap 是如何得到一张 RGBA 图片的灰度图像的：
Android 转化为 bitmap 类得到灰度图像，并对其数据做如下操作：

    A通道保持不变，然后逐像素计算：X = 0.3×R+0.59×G+0.11×B，并使这个像素的值新R，G，B值为X，即：

    new_R = X, new_G = X, new_B = X

    例如：原来一个像素是4个byte，分别为ARGB，现在这个像素应该为AXXX


得到的图片的通道仍然与原图保持一致，归根揭底还是一个RGBA的图片，只是颜色值为gray而已。

OpenCV 给我们提供来更为彻底的处理函数：

    Imgproc.cvtColor(Mat src, Mat dst, ind code)
    
- src为要处理的图片
- dst为处理后输出图片
- code为转换模型
例如我们要将一个RGBA的图片转换成GRAY，如下处理：


     Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2GRAY)
	 
#### 二、线性滤波

线性滤波最常见对一种用途是降噪，噪声是图像中亮度或色彩信息对随机变化，一般用模糊操作来减少图像中对噪声。

##### 2.1、高斯模糊

高斯模糊是比较常用的模糊算法，在许多图片美化工具中都用经常用到。
OpenCV 提供了内置函数用来在应用中执行高斯模糊：**GaussianBlur**

    Imgproc.GaussianBlur(
    	Mat src, // 要处理的图像
    	Mat dst, // 输出图像
    	Size kSize, // 高斯内核大小
    	double sigmax, // 高斯函数在x方向上的标准偏差
    	double sigmay  // 高斯函数在y方向上的标准偏差
    )
    
例如对图片进行一次高斯模糊处理：

    // 高斯模糊
    Imgproc.GaussianBlur(src, src, Size(3.0, 3.0), 0.0,0.0)
    

##### 2.2、中值模糊

噪声在图片是是一种比较常见对现象，尤其是椒盐噪声，该噪声是疏密分布与图片中对黑色白色像素点。
OpenCV给我们提供了 **medianBlur** 内置函数来进行中值滤波：

    medianBlur(Mat src, Mat dst, int ksize) 
    
##### 2.3、均值模糊

均值模糊是最简单一中模糊处理方式，在 OpenCV 中，我们使用内置函数： **blur** 来进行处理：

    blur(Mat src, Mat dst, Size ksize)
    
##### 2.4、锐化

锐化可以看作是一种线性滤波操作，通常我们在处理沙滩、毛发之类的图片时会经常用到锐化的操作，可以给人一中更有质感的感觉。 OpenCV 中的内置函数： **filter2D**  可以实现这一效果：

    filter2D(Mat src, Mat dst, int ddepth, Mat kernel)
    
	
#### 三、阈值化

阈值化是一种将我们想要在图像中分析的区域分割出来的方法，基本原理是把每个像素值跟我们预设的值进行比较，再根据比较结果进行像素调整。

通过内置函数：**threshold** 来处理：

    threshold(Mat src, Mat dst, double thresh, double maxval, int type)
    
当然，图像受关照条件等的影响，我们只定义一个全局的阈值比不是好的选择，为了克服这个限制，我们要试图根据邻像素为任意像素计算阈值：**自适应阈值**
OpenCV提供给我们自适应阈值的操作：**adaptiveThreshold**

    adaptiveThreshold(
    	Mat src, 
    	Mat dst, 
    	double maxValue, 
    	int adaptiveMethod, 
    	int thresholdType, 
    	int blockSize, 
    	double C
      ) 
    
- adaptiveMethod ： 自适应方法，ADAPTIVE_THRESH_MEAN_C （阈值是领域像素的均值），ADAPTIVE_THRESH_GAUSSIAN_C（阈值是领域像素的加权和，权重来自高斯核）
- thresholdType ：阈值类型，API中说明：Thresholding type that must be either #THRESH_BINARY or #THRESH_BINARY_INV
- blockSize ：领域的大小
- C ：从每个像素计算得到的像素值或加权值减去的常量

#### 四、腐蚀和膨胀

形态学运算是一类根据图像特征和结构元素进行图像处理的操作，大多针对二值或灰度图像。

膨胀是一种将图像中亮区域扩张的方法，相反的，腐蚀是一种将图像中暗区域扩张的方法。
我们利用OpenCV 通过的 ** erode** 方法进行腐蚀处理，用 **dilate** 进行膨胀处理

     dilate(Mat src, Mat dst, Mat kernel)
     
     erode(Mat src, Mat dst, Mat kernel) 
    
#### 五、均衡

直方图均衡化是图像处理领域中利用图像直方图对对比度进行调整的方法。通过这种方法，亮度可以更好地在直方图上分布。这样就可以用于增强局部的对比度而不影响整体的对比度。
利用：**equalizeHist**函数来处理（注：该函数只能处理单通道图像）

    equalizeHist(Mat src, Mat dst)
    
##### 5.1、单通道直方图均衡处理

如我们将一个图片均衡处理：
- 先将图片转换成但通道
- 均衡化


    Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY)
    Imgproc.equalizeHist(src, src)
	
##### 5.2、多通道直方图均衡处理

进行直方图均衡处理得到一张彩色图片
- 分离通道
- 直方图均衡处理每个通道
- 合并通道


    val mats = ArrayList<Mat>()
    // 通道分解
    Core.split(src, mats)
    mats.forEach {
       // 直方图均衡每个通道
       Imgproc.equalizeHist(it, it)
     }
    // 通道合并
    Core.merge(mats, src)
    

## 基于 Android OpenCV 的提取图片中的文字

### 第一步：阈值化&腐蚀

由于字体大小，或其他一些字体间的间距、行宽等影响，不同的字体图片可能需要用到等腐蚀效果不同。
        /**
         * 阈值化，并腐蚀
         * @param src
         */
        private void erode(Mat src) {
            // 阈值化
            Imgproc.threshold(src, src, 100, 255, Imgproc.THRESH_BINARY);
    //        Imgproc.adaptiveThreshold(opMat,opMat,255.0,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,3,0.0);
    
            Mat erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
            Imgproc.erode(src, src, erodeKernel);
        }
    
### 第二步 滤波降噪

这里我们采用最为简单的中值滤波进行降噪：

        /**
         * 采用中值滤波进行降噪
         *
         * @param src
         */
        private void medianBlur(Mat src) {
            Imgproc.medianBlur(src, src, 7);
        }
    
### 第三步 区域检测

区域检测，这里我们采用检测连通区域再根据联通区域计算边框的方式进行检测，当然，还有其他的一些检测方法，比如轮廓检测法、边缘检测等方法。
- 连通区域检测
- Rect边框计算

#### 连通区域检测

首先进行连通区域检测，这里我们采用**种子填充法**进行连通区域检测，比较常用的还有**两遍扫描法**等

#### 种子填充法

下面开始进行种子填充算法，具体代码如下：

        /**
         * 种子填充法进行联通区域检测
         */
        private Mat seedFill(Mat binImg, Mat src) {
            // 用来记录连通区域都数据图
            Mat lableImg = new Mat();
            // 这个是用来展示连通区域都效果图。
            Mat showMat = new Mat(binImg.size(), CvType.CV_8UC3);
            // 不需要记录额外都数据，一个通道就够了。
            binImg.convertTo(lableImg, CvType.CV_32SC1);
    
            int rows = lableImg.rows();
            int cols = lableImg.cols();
    
            double lable = 0;
    
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    // 获取种子像素点
                    double[] data = lableImg.get(r, c);
                    if (data == null || data.length < 1) {
                        continue;
                    }
                    if (data[0] == 255) {
                    	// 不是我们要都种子像素，继续
                        // 展示图背景设置为白色
                        showMat.put(r, c, 255, 255, 255);
                        continue;
                    }
                    if (data[0] != 0) {
                        // 已经标记过了，继续
                        continue;
                    }
    
                    // 走到这里说明找到了新的种子像素点，新的填充开始
                    lable++;
                    // 随机生成一个颜色，用来填充展示图
                    double[] color = {Math.random() * 255, Math.random() * 255, Math.random() * 255};
    
                    // 开始种子填充
                    LinkedList<Point> neighborPixels = new LinkedList<>();
                    neighborPixels.push(new Point(r, c));
                    
                    while (!neighborPixels.isEmpty()) {
                        Point curPx = neighborPixels.pop();
                        int row = (int) curPx.x;
                        int col = (int) curPx.y;
                        lableImg.put(row, col, lable);
                        showMat.put(row, col, color);
    
                        // 左边
                        double[] left = lableImg.get(row, col - 1);
                        if (left != null && left.length > 0 && left[0] == 0) {
                            neighborPixels.push(new Point(row, col - 1));
                        }
    
                        // 右边
                        double[] right = lableImg.get(row, col + 1);
                        if (right != null && right.length > 0 && right[0] == 0) {
                            neighborPixels.push(new Point(row, col + 1));
                        }
    
                        // 上边
                        double[] top = lableImg.get(row - 1, col);
                        if (top != null && top.length > 0 && top[0] == 0) {
                            neighborPixels.push(new Point(row - 1, col));
                        }
    
                        // 下边
                        double[] bottom = lableImg.get(row + 1, col);
                        if (bottom != null && bottom.length > 0 && bottom[0] == 0) {
                            neighborPixels.push(new Point(row + 1, col));
                        }
                    }
                }
            }
            // 返回展示图
            return showMat;
        }
    
    
#### 字体边框计算

通过上一步骤的检测，我们可以得到每个连通区域的像素点，修改上一步骤的代码，在开始新的连通区域检测之前，我们定义一个连通区域坐标点列表集合：

    List<List<Point>> texts = new LinkedList<>();
    
在进行连通区域检测的时候，每次出栈一个点，就将这个点加入到这个坐标点集合中去：

    Point curPx = neighborPixels.pop();
    int row = (int) curPx.x;
    int col = (int) curPx.y;
    textPoint.add(new Point(col, row));
    
这样当我们把连通区域检测完成之后就得到一个连通区域点集合的列表：texts
然后开始计算每个连通区域的边框：

            for (List<Point> data : texts) {
                MatOfPoint mat = new MatOfPoint();
                mat.fromList(data);
                Rect rect = Imgproc.boundingRect(mat);
                Imgproc.rectangle(showMat, rect.tl(), rect.br(), new Scalar(255, 0, 0, 255));
            }
    

#### 截取文字

继续修改上一步的代码：

            // 定义一个文字图片列表用来保存截取出来的文字。
            List<Mat> textMats = new LinkedList<>();
            for (List<Point> data : texts) {
                MatOfPoint mat = new MatOfPoint();
                mat.fromList(data);
                // 计算边框
                Rect rect = Imgproc.boundingRect(mat);
                // 绘制边框
                Imgproc.rectangle(showMat, rect.tl(), rect.br(), new Scalar(255, 0, 0, 255));
                // 利用边框截取文字，并加入到列表中。
                textMats.add(src.submat(rect));
            }
            adapter.setData(textMats);
    
结合文字匹配程序，我们可以在此基础上做更多好玩的工具：身份证信息提取、发票信息提取、名片信息提取等等。

## 基于 Android OpenCV 的图像特征检测

### 边缘和角点检测

边缘检测和角点检测是最为基本而且最为常用的两中特征检测算法，经常用于找出图像中目标图像的边界、角点，或者分析一幅图像的旋转情况，目标在图像序列（视频）中的移动情况等。

### 边缘检测之高斯差分技术

高斯差分技术我们在环境搭建，高斯差分中有做介绍，这里就不再重复。

### 边缘检测之Canny边缘检测器

**Canny边缘检测器**在计算机视觉中被广泛采用，并被认为是边缘检测最优的算法。

算法步骤如下：

- 平滑图像
- 计算图像的梯度
- 非最大值抑制
- 用滞后阈值化选择边缘

我们可以直接使用OpenCV提供的方法进行Canny边缘检测：

    Canny(Mat image, 
    	Mat edges, 
    	double threshold1, // 低阈值
    	double threshold2  // 高阈值
    )
    
	
### 边缘检测之Sobel算子

Sobel算子边缘检测与Canny类似，去计算像素的灰度梯度，只不过是换用另一种方式。

Sobel算子步骤：
- 将图像转换成灰度
- 计算水平方向灰度梯度绝对值
- 计算垂直方向灰度梯度绝对值
- 计算最终梯度


    	/**
         * Sobel滤波
         */
        fun sobel() {
            imageBitmap?.apply {
                val originalMat = Mat(height, width, CvType.CV_8UC4)
                val grayMat = Mat()
                Utils.bitmapToMat(imageBitmap, originalMat)
                Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGRA2GRAY)
    
                val sobelMat = Mat()
                val gradX = Mat()
                val absGradX = Mat()
                val gradY = Mat()
                val absGradY = Mat()
    
                // 计算水平方向梯度
                Imgproc.Sobel(grayMat, gradX, CvType.CV_16S, 1, 0, 3, 1.0, 0.0)
                // 计算竖直方向梯度
                Imgproc.Sobel(grayMat, gradY, CvType.CV_16S, 0, 1, 3, 1.0, 0.0)
    
                // 计算两个方向上的绝对梯度
                Core.convertScaleAbs(gradX, absGradX)
                Core.convertScaleAbs(gradY, absGradY)
    
                // 计算结果梯度
                Core.addWeighted(absGradX, 0.5, absGradY, 0.5, 1.0, sobelMat)
    
                val resultBitmap = Bitmap.createBitmap(sobelMat.cols(), sobelMat.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(sobelMat, resultBitmap)
                imageResult.setImageBitmap(resultBitmap)
            }
        }
    
	
### 角点检测

**角点** 一般是指两条边缘的交点或者在局部领域中有多个显著边缘方向的点，通常用来当作图像中的兴趣点。
下面用**Harris**角点检测来检测图像的角点：

    	/**
         * harris 角点检测
         */
        fun harris() {
            imageBitmap?.apply {
                val originalMat = Mat(height, width, CvType.CV_8UC4)
                val grayMat = Mat()
                val cornersMat = Mat()
                Utils.bitmapToMat(this, originalMat)
    
                Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY)
                val tempDstMat = Mat()
                // 找出角点
                Imgproc.cornerHarris(grayMat, tempDstMat, 2, 3, 0.04)
                // 归一化harris角点输出
                val tempDstNorMat = Mat()
                Core.normalize(tempDstMat, tempDstNorMat, 0.0, 255.0, Core.NORM_MINMAX)
                Core.convertScaleAbs(tempDstNorMat, cornersMat)
                // 在新图像上绘制角点
                for (i in 0 until tempDstNorMat.cols()) {
                    for (j in 0 until tempDstNorMat.rows()) {
                        val value: DoubleArray? = tempDstNorMat.get(i, j)
                        value?.apply {
                            if (value[0] > 150) {
                                cornersMat.get(i, j)
                                Imgproc.circle(cornersMat, Point(i.toDouble(), j.toDouble()), 5, Scalar(255.0),2)
                            }
                        }
    
                    }
                }
    
                val resultBitmap = Bitmap.createBitmap(cornersMat.cols(), cornersMat.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(cornersMat, resultBitmap)
                imageResult.setImageBitmap(resultBitmap)
            }
        }
    
	
### 霍夫变化

通常会使用霍夫变换来检测形状。

### 霍夫直线

霍夫直线检测是霍夫变换中最为简单的一种用途：

    	/**
         * 霍夫直线检测
         */
        fun houghLines() {
            imageBitmap?.apply {
                val originalMat = Mat(height, width, CvType.CV_8UC4)
                val grayMat = Mat()
                Utils.bitmapToMat(this, originalMat)
    
    			// 转换为灰度图像
                Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGRA2GRAY)
                val cannyMat = Mat()
                // 进行边缘检测
                Imgproc.Canny(grayMat, cannyMat, 400.0, 500.0, 5, false)
                val linesMat = Mat()
                // 计算霍夫直线
                Imgproc.HoughLinesP(cannyMat, linesMat, 1.0, Math.PI / 180, 50, 0.0, 0.0)
                val houghLinesMat = Mat()
                // 这里我们将检测到到直线绘制到图像上，以方便展示
                houghLinesMat.create(cannyMat.size(), CvType.CV_8UC1)
                for (i in 0 until linesMat.rows()) {
                    val points = linesMat.get(i, 0)
                    val x1 = points[0]
                    val y1 = points[1]
                    val x2 = points[2]
                    val y2 = points[3]
    
                    val ponit1 = Point(x1, y1)
                    val point2 = Point(x2, y2)
                    Imgproc.line(houghLinesMat, ponit1, point2, Scalar(255.0, 0.0, 0.0), 1, Imgproc.LINE_4, 0)
    
                }
    
                val resultBitmap = Bitmap.createBitmap(houghLinesMat.cols(), houghLinesMat.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(houghLinesMat, resultBitmap)
                imageResult.setImageBitmap(resultBitmap)
    
            }
        }
    
    
上面到操作如下：
- 将图像转换成灰度图像
- 进行边缘检测，得到边缘轮廓图像
- 进行霍夫直线检测
- 将霍夫直线绘制到图像上并展示出来

霍夫圆到操作与霍夫直线类似，我们用 OpenCV 提供到 **HoughCircles** 方法可以进行霍夫圆的检测。

### 轮廓检测

利用轮廓检测，我们可以得到图像中的图元（连通部分），通常以图片中的边缘来计算：

       /**
         * 轮廓检测
         */
        fun contouurs() {
            imageBitmap?.apply {
                val originalMat = Mat(height, width, CvType.CV_8UC4)
                Utils.bitmapToMat(imageBitmap, originalMat)
    
                val grayMat = Mat()
    
                val cannyEdges = Mat()
                val hierarchy = Mat()
                val contourList = ArrayList<MatOfPoint>()
    			// 转换为灰度图像
                Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY)
                // 找出边缘
                Imgproc.Canny(grayMat, cannyEdges, 10.0, 100.0)
                // 找出轮廓
                Imgproc.findContours(cannyEdges, contourList, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)
    
                // 在新图上绘制轮廓
                val contours = Mat()
                contours.create(cannyEdges.rows(), cannyEdges.cols(), CvType.CV_8UC3)
                val r = Random
                for (i in 0 until contourList.size) {
                    Imgproc.drawContours(
                        contours,
                        contourList,
                        i,
                        Scalar(r.nextDouble(255.0), r.nextDouble(255.0), r.nextDouble(255.0)),
                        -1
                    )
                }
    
                showResult(contours, imageResult)
            }
        }
    
    
上述代码我们进行的操作主要有：
- 将图像转换成灰度图像
- 边缘检测
- 轮廓检测
- 在新图上用色块填充轮廓

## 基于Android OpenCV的图片扫描器

### 纸面识别

开始之前，为了提高效率，我们将图片进行缩放处理，并进行一次高斯模糊减少噪声：

    val scalFactor = calcScaleFactor(srcOrig.rows(), srcOrig.cols())
    val src = Mat()
    Imgproc.resize(
      srcOrig,
      src,
    Size(srcOrig.cols() / scalFactor.toDouble(), srcOrig.rows() / scalFactor.toDouble())
    )
    Imgproc.GaussianBlur(src, src, Size(5.0, 5.0), 1.0)
    
    
	        fun calcScaleFactor(rows: Int, cols: Int): Int {
            var ideaRows = 0
            var ideaCols = 0
    
            if (rows < cols) {
                ideaRows = 240
                ideaCols = 320
            } else {
                ideaCols = 240
                ideaRows = 320
            }
    
            val value = Math.min(rows / ideaRows, cols / ideaCols)
            return if (value < 0) {
                1
            } else {
                value
            }
        }
    
    
接下来，我们使用**K-均值聚类算法**对图像进行处理。（K-均值聚类算法），其效果将图片的背景与纸面有更加清晰的区别。
首先执行包含两个聚类中心的K均值聚类：

                    val samples = Mat(src.rows() * src.cols(), 3, CvType.CV_32F)
                    for (y in 0 until src.rows()) {
                        for (x in 0 until src.cols()) {
                            for (z in 0 until 3)
                                samples.put(x + y * src.cols(), z, src.get(y, x)[z])
                        }
                    }
    
然后执行K-均值算法:

    				val clusterCount = 2
                    val lables = Mat()
                    val attempts = 5
                    val centers = Mat()
    
    
                    Log.i("kmeans", "--------start--------")
                    Core.kmeans(
                        samples,
                        clusterCount,
                        lables,
                        TermCriteria(TermCriteria.MAX_ITER or TermCriteria.EPS, 10000, 0.0001),
                        attempts,
                        Core.KMEANS_PP_CENTERS,
                        centers
                    )
    
我们得到了两个聚类中心，并且原始图像中每个像素都有了标签，然后我们利用这两个聚类来检测哪一个是纸面。
找出两个中心的颜色与白色之间的欧式距离,较近的我们认为是纸面:

                    val center0 = calcWhiteDist(centers.get(0, 0)[0], centers.get(0, 1)[0], centers.get(0, 2)[0])
                    val center1 = calcWhiteDist(centers.get(1, 0)[0], centers.get(1, 1)[0], centers.get(1, 2)[0])
                    Log.i("calcWhiteDist", "--------end--------")
    
                    val paperCluter = if (center0 < center1) {
                        0
                    } else {
                        1
                    }
    
    
            /**
         * 计算距离
         */
        fun calcWhiteDist(r: Double, g: Double, a: Double): Double {
            return Math.sqrt(Math.pow(255 - r, 2.0) + Math.pow(255 - g, 2.0) + Math.pow(255 - a, 2.0))
        }
    
进行图像分割，将前景显示为白色，将背景显示为黑色:

                    val srcRes = Mat(src.size(), src.type())
                    val srcGray = Mat()
    
    
                    for (y in 0 until src.rows()) {
                        for (x in 0 until src.cols()) {
                            val clusterIdx = lables.get(x + y * src.cols(), 0)[0].toInt()
                            if (clusterIdx == paperCluter) {
                                srcRes.put(y, x, 0.0, 0.0, 0.0, 255.0)
                            } else {
                                srcRes.put(y, x, 255.0, 255.0, 255.0, 255.0)
                            }
                        }
                    }
    
    
### 轮廓检测

接下来我们进行轮廓检测:

                    Log.i("Canny", "--------start--------")
                    Imgproc.cvtColor(srcRes, srcGray, Imgproc.COLOR_BGR2GRAY)
                    Imgproc.Canny(srcGray, srcGray, 50.0, 150.0)
                    Log.i("Canny", "--------end--------")
    
                    Log.i("findContours", "--------start--------")
                    val contours = ArrayList<MatOfPoint>()
                    val hierarchy = Mat()
                    Imgproc.findContours(srcGray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)
    
                    Log.i("contours", "${contours.size}")
                    Log.i("findContours", "--------end--------")
    
    
                    Log.i("contourArea", "--------start--------")
                    var index = 0
                    var maxim = Imgproc.contourArea(contours[0])
                    for (contourIdx in 0 until contours.size) {
                        val temp = Imgproc.contourArea(contours[contourIdx])
                        if (maxim < temp) {
                            maxim = temp
                            index = contourIdx
                        }
    
                    }
                    Log.i("contourArea", "--------end--------")
    
                    val drawing = Mat.zeros(srcRes.size(), CvType.CV_8UC1)
                    Imgproc.drawContours(drawing, contours, index, Scalar(255.0), 1)
    
    
我们进行轮廓检测的处理方式：
- 灰度图像
- Canny边缘检测
- 轮廓检测
- 将轮廓绘制在一张新的图像上

### 角点检测

为了能够准确的找到我们轮廓的四个角的顶点，我们这里不直接采用OpenCV提供的角点检测的算法，我们的做法如下：
- 霍夫直线检测
- 计算每两条直线的交点

                    Log.i("HoughLinesP", "--------start--------")
                    val lines = Mat()
                    Imgproc.HoughLinesP(drawing, lines, 1.0, Math.PI / 180, 70, 30.0, 10.0)
                    println("" + lines.rows() + "---------" + lines.cols())
                    var corners = ArrayList<Point>()
                    for (i in 0 until lines.rows()) {
                        for (j in i + 1 until lines.rows()) {
                            val line1 = lines.get(i, 0)
                            val line2 = lines.get(j, 0)
                            val p = findIntersection(line1, line2)
                            if (p.x > 0 && p.x < drawing.width() && p.y > 0 && p.y < drawing.height()) {
                                corners.add(p)
                            }
                        }
                    }
    
    
                    Log.i("HoughLinesP", "--------end--------")
    
                    if (corners.size < 4) {
                        Log.i("------------", "不能完美检测到角点")
                        return null
                    }
    
    
	        /**
         * 计算两条直线之间的交点
         */
        fun findIntersection(line1: DoubleArray, line2: DoubleArray): Point {
            Log.i("findIntersection", "--------start--------")
            val startX1 = line1[0]
            val startY1 = line1[1]
            val endX1 = line1[2]
            val endY1 = line1[3]
    
            val startX2 = line2[0]
            val startY2 = line2[1]
            val endX2 = line2[2]
            val endY2 = line2[3]
    
            val denominator = (startX1 - endX1) * (startY2 - endY2) - (startY1 - endY1) * (startX2 - endX2)
            if (denominator != 0.0) {
                val pt = Point()
                pt.x =
                    ((startX1 * endY1 - startY1 * endX1) * (startX2 - endX2) - (startX1 - endX1) * (startX2 * endY2 - startY2 * endX2)) / denominator
                pt.y =
                    ((startX1 * endY1 - startY1 * endX1) * (startY2 - endY2) - (startY1 - endY1) * (startX2 * endY2 - startY2 * endX2)) / denominator
    
                return pt
            }
            return Point(-1.0, -1.0)
        }
    
    
如果最终得到的角点个数小于4个，说明我们没有从图片中成功提取到目标区域:

                   corners.forEach {
                        Imgproc.circle(drawing, it, 5, Scalar(255.0, 255.0, 0.0, 255.0), 10)
                    }
                    if(1==1){
                        return returnBitmap(drawing)
                    }
    
    


### 角点归位

上面我们成功拿到了四个顶点的角点，但是我们还不确定每个角点的位置，接下来我们就来确定一下每个角点在图像中的位置：

                    Log.i("setCorners", "--------start--------")
    
                    corners = setCorners(corners, scalFactor)
                    Log.i("setCorners", "--------end--------")
    
    
    
	        /**
         * 确定四个顶点的位置
         */
        fun setCorners(corners: ArrayList<Point>, scalFactor: Int): ArrayList<Point> {
    
            var topLeft = Point()
            var topRight = Point()
            var bottomLeft = Point()
            var bottomRight = Point()
    
            var centerX = 0.0
            var centerY = 0.0
    
    
            for (i in 0 until corners.size) {
                centerX += corners[i].x / corners.size
                centerY += corners[i].y / corners.size
            }
    
            for (i in 0 until corners.size) {
                val point = corners[i]
                if (point.y < centerY && point.x > centerX) {
                    topRight.x = point.x * scalFactor
                    topRight.y = point.y * scalFactor
                } else if (point.y < centerY && point.x < centerX) {
                    topLeft.x = point.x * scalFactor
                    topLeft.y = point.y * scalFactor
                } else if (point.y > centerY && point.x < centerX) {
                    bottomLeft.x = point.x * scalFactor
                    bottomLeft.y = point.y * scalFactor
                } else if (point.y > centerY && point.x > centerX) {
                    bottomRight.x = point.x * scalFactor
                    bottomRight.y = point.y * scalFactor
                }
            }
    
    
            corners.clear()
            corners.add(topLeft)
            corners.add(topRight)
            corners.add(bottomRight)
            corners.add(bottomLeft)
    
            return corners
        }
    
    
    
这里由于每个角得到的角点不止一个，我们取每个角其中的一个即可。并且我们将前面计算的缩放因子计算进去，得到角点真正的图像中的位置。接下来我们进行目标区域尺寸的确定:

                    val top =
                        Math.sqrt(Math.pow(corners[0].x - corners[1].x, 2.0) + Math.pow(corners[0].y - corners[1].y, 2.0))
                    val right =
                        Math.sqrt(Math.pow(corners[1].x - corners[2].x, 2.0) + Math.pow(corners[1].y - corners[2].y, 2.0))
                    val bottom =
                        Math.sqrt(Math.pow(corners[3].x - corners[3].x, 2.0) + Math.pow(corners[3].y - corners[2].y, 2.0))
                    val left =
                        Math.sqrt(Math.pow(corners[3].x - corners[1].x, 2.0) + Math.pow(corners[3].y - corners[1].y, 2.0))
    
                    val quad = Mat.zeros(Size(Math.max(top, bottom), Math.max(left, right)), CvType.CV_8UC3)
    
    
    
### 透视变换

有了目前图像，以及图像的尺寸，下面我们进行最后一步的处理，**透视变换** 使得整个纸面占据整个图像:

                    val resultPoints = ArrayList<Point>()
                    resultPoints.add(Point(0.0, 0.0))
                    resultPoints.add(Point(quad.cols().toDouble(), 0.0))
                    resultPoints.add(Point(quad.cols().toDouble(), quad.rows().toDouble()))
                    resultPoints.add(Point(0.0, quad.rows().toDouble()))
    
                    val cornerPts = Converters.vector_Point2f_to_Mat(corners)
                    val resultPts = Converters.vector_Point2f_to_Mat(resultPoints)
    
                    Log.i("getPerspectiveTransform", "--------start--------")
                    val transformation = Imgproc.getPerspectiveTransform(cornerPts, resultPts)
                    Imgproc.warpPerspective(srcOrig, quad, transformation, quad.size())
    
    
执行完这一步，基本完成。