# YougetGUI

**<u>注：you-get没有更新最新版exe，现在无法使用</u>**



https://github.com/soimort/you-get 的Windows图形化界面实现

> you-get 是一个支持几乎所有主流视频网站视频下载的python脚本实现
>

# 使用方法

1. 安装`java(8u40+)`版本
2. 双击运行源代码根目录`YougetGUI.jar`文件 （或者双击运行源代码根目录`运行.bat`文件）


# 演示

![演示](https://cloud.githubusercontent.com/assets/13044819/18734559/4ed2f3ac-80a8-11e6-8756-4ee9b0c71267.gif)

# 有什么特点

- 提供图形化界面，显示下载进度和速度，提供设置代理服务器
- 监听剪切板功能，自动添加链接到下载队列 
- 支持多个下载任务顺序下载
- 自动保存下载记录
- 自动保存代理服务器等设置

https://zhuanlan.zhihu.com/p/21571327
# 注意

如果通过`运行.bat`来运行程序，那么请通过关闭程序窗口来关闭程序，不要通过关闭输出log的控制台窗口来关闭程序。


# 实现原理

- 通过调用`you-get`命令行来实现视频下载
- 使用`JavaFX`实现UI
