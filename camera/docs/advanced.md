# Camera - docs/advanced 

​	从 Android 5.0 开始，Google 引入了一套全新的相机框架 Camera2，旧的被废弃，本项目主要为Camera2 的重要概念和使用方法的说明文档。

## 进阶内容 - 使用opengl

##### 1. 使用opengl进行相机预览

​	为了避免 ImageReader 拿到数据之后做处理再显示的繁琐和卡顿，将使用 opengles 对相机的预览数据进行渲染。这一部分将使用 opengles 将相机预览数据渲染到 GLSurfaceView。

##### 2. 使用opengl实现滤镜

​	结合 opengles 的离屏渲染机制实现实时滤镜功能。  

- **灰度滤镜**

  123

- **黑白滤镜**

  123

- **反色滤镜**

  123

- **亮度滤镜**

  123

- **色调分离**

  123

- **lut滤镜**

  123