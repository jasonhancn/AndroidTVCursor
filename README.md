# AndroidTVCursor
使用遥控器方向键模拟鼠标指针

**本项目使用了Android Studio 3.0，并采用Gradle 4.1构建，如需要修改项目文件，请使用高版本的IDE**

## 使用方法

使用TvCursorActivity替代需要使用指针的页面的AppCompactActivity即可

在需要显示鼠标指针的时候，调用showCursor()

在需要隐藏鼠标指针的时候，调用hideCursor()

要获取鼠标显示情况，可以调用*boolean* isShowCursor() 

## 已知问题

没有解决滚轮问题（常用的解决方法是当鼠标位于最顶/底部的时候，再次按下方向键触发滚轮效果，有待后续增加）

没有右键（不过TV上并不需要右键）

点击位置暂没有精确对准指针尖端

## 其他

鼠标移动和点击的实现方法受Tamicer的[MouseView_TV](https://github.com/Tamicer/MouseView_TV)项目启发，在此感谢

感谢不愿意透露姓名的某设计师所绘的鼠标指针图案（无版权限制）