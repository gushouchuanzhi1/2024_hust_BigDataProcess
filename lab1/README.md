本文件夹中pagerank和wordcount中为lab1所需的内容，需要在eclipse中借助hdfs服务来完成


实验一的配置过程请参考以下内容（word文件中也可查看）

1.通过虚拟机配置hadoop https://dblab.xmu.edu.cn/blog/2441/ 

2.通过eclipse来编译java文件实现mapreduce https://dblab.xmu.edu.cn/blog/31/  

十分感谢林子雨老师为学生编写了如此详细的博客。



配置过程中相关资源（百度网盘链接）包含hadoop-3.3.6，jdk8以及eclipse linux64位版本

链接: https://pan.baidu.com/s/1tTWfpB0THwXVK-VEfGVgfw?pwd=9ydw 提取码: 9ydw 复制这段内容后打开百度网盘手机App，操作更方便哦



在配置的过程中比较难的一步是将eclipse linux64位tar.gz压缩包传入虚拟机，这里建议在/Desktop直接解压，

```
sudo tar -zxvf eclipse.tar.gz -C ./Desktop
```

解压完毕后在ubuntu桌面即可看到eclipse的文件夹，然后将hadoop的插件jar包使用sudo指令移入plugins文件夹，

```
sudo mv ./hadoop.jar ./Desktop/eclipse/plugins
```

最后在eclipse文件夹中可以看到eclipse的文件，在解压生成的eclipse文件夹中，打开终端输入：

```
./eclipse
```

即可成功打开eclipse软件，详细请看林子雨老师的博客。


