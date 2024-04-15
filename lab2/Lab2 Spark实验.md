## Lab2 Spark实验

### 第二部分实验流程：

实际操作的文件路径为：/usr/local/hadoop/README.txt

首先先在/usr/local/spark文件夹打开终端，输入bin/spark-shell启动spark

 ![image-20240415225542889](C:\Users\古手川\AppData\Roaming\Typora\typora-user-images\image-20240415225542889.png)

加载本地文件：这里使用`file://`是本地文件，使用`hdfs://`是hdfs结点上的文件

```
val textFile = sc.textFile("file:///usr/local/hadoop/README.txt")
```

使用

```
textFile.count()
```

![image-20240415231912908](C:\Users\古手川\AppData\Roaming\Typora\typora-user-images\image-20240415231912908.png)

然后接着来查询有hadoop的行数量：（这里请注意，word试验任务书中引号为中文的，记得自己修改为英文）

```
val linesWithHadoop = textFile.filter(line => line.contains("hadoop"))
linesWithHadoop.count()
```

![image-20240415232229814](C:\Users\古手川\AppData\Roaming\Typora\typora-user-images\image-20240415232229814.png)



