## lab2 spark实验


由于markdown文件不能很好的导入图片，重新将lab2实验.md写为lab2实验过程.docx，如果可以的话请使用typora来查看md文件，并且同时对照docx文件



实验相关资源可见百度网盘：

链接: https://pan.baidu.com/s/1bDf5qVuLU4KUxhhs5nGQJQ?pwd=i3hv 提取码: i3hv 复制这段内容后打开百度网盘手机App，操作更方便哦



若分开安装Hadoop和Spark，请参考https://dblab.xmu.edu.cn/blog/804/

### 1.Spark配置相关的问题



启动spark-shell：

```
bin/spark-shell
```

停止spark-shell：

```
:quit
```



3.使用sbt打包scala程序，

这一步需要很久，因为需要下载相关的依赖包。

![image-20240415160711102](C:\Users\古手川\AppData\Roaming\Typora\typora-user-images\image-20240415160711102.png)

在这一步可能会报错：

![image-20240415161030789](C:\Users\古手川\AppData\Roaming\Typora\typora-user-images\image-20240415161030789.png)

相关报错请查看链接：[spark踩坑记 - 灰信网（软件开发博客聚合） (freesion.com)](https://www.freesion.com/article/1005811317/)



4.通过 spark-submit 运行程序（这里需要将其中的文件jar修改成符合自己的，修改scala版本号）

```
/usr/local/spark/bin/spark-submit --class "SimpleApp" ~/sparkapp/target/scala-2.12/simple-project_2.12-1.0.jar
```



### 2.Java独立应用编程

直接按照教程即可，应该没有问题

