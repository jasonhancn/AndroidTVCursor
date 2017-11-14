# AndroidTVCursor
使用遥控器方向键模拟鼠标指针

**本项目使用了Android Studio 3.0，并采用Gradle 4.1构建，如需要修改项目文件，请使用高版本的IDE**

## 主要功能

- 用遥控器模拟鼠标指针移动，支持上下左右四个方向，随意控制隐藏或显示

- 弹出输入法框时自动停止指针移动，收起输入法框自动恢复

- 指针移动到最上和最下时触发滚轮移动效果（需先指定目标View）

- 按住遥控器中键，同时点击方向键，可以实现拖动/滑动的类似效果

- 移动过程中的连续点击会进行线性加速，提升移动效率

- 自带了优质的指针图案素材，适用于大部分场景，并可以调整指针大小，更换指针图案，并调整指针尖端位于图片中的位置

## 使用方法
- 使用Gradle构建

  ```
  compile 'com.github.jasonhancn:tvcursor:0.9'
  ```

- 使用**TvCursorActivity**替代需要使用指针的页面的**AppCompactActivity**即可

- 在需要显示鼠标指针的时候，调用

  ```
  showCursor()
  ```

- 在需要隐藏鼠标指针的时候，调用

  ```
  hideCursor()
  ```
  
- 要获取鼠标显示情况，可以调用 

  ```
  boolean isShowCursor()
  ```
  
- 要设置需要滚动的View，调用

  ```
  setScrollTargetView(View view)
  ```
  
  view 要滚动的view
  
- 要设置鼠标指针的大小，调用 

  ```
  setCursorSize(int size)
  ```
  
  size 指针大小，单位为px
  
- 要设置鼠标指针的图案，调用

  ```
  setCursorResource(int pointerResource, int pointerSize, int pointerX, int pointerY)
  ```
  pointerResource 资源文件的索引(R.xxx.xx)
  
  pointerSize 指针大小，单位为px
  
  pointerX 指针尖端相对于图片素材左上角的水平距离，单位为px
  
  pointerY 指针尖端相对于图片素材左上角的垂直距离，单位为px
  
## Demo

demo程序提供了最基本的功能体验（为WebView提供鼠标支持），可以按menu键切换鼠标指针的显示或隐藏，切换注释掉的代码可以体验大多数情景。
  
## 已知问题

- 没有右键（不过TV上并不需要右键）

- 点按拖动由于遥控器的按键冲突可能会有中断（主要发生在换方向的情况下）

- 指针不能移动到Action Bar上

## 其他

鼠标移动和点击的实现方法最初受Tamicer的[MouseView_TV](https://github.com/Tamicer/MouseView_TV)项目启发，在此感谢

感谢不愿意透露姓名的某设计师所绘的鼠标指针图案（无版权限制）

demo中使用了一张手型图片作为测试数据，图源来自网络