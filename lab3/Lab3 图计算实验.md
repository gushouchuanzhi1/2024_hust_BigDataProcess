本次实验使用Spark中的graphx库来实现图计算内容

我们需要实现dfs和scc算法，我们分成这么几个步骤来完成，先完成DFS算法和SCC算法完成编写，然后还要完成测试数据的传入。随后使用/usr/local/sbt/sbt package来实现打包，最后使用submit来提交实现程序的运行即可



built.sbt文件内容，添加graphx内容依赖

```
name := "lab3"

version := "1.0"

scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
        "org.apache.spark" %% "spark-core" % "3.5.1",
        "org.apache.spark" %% "spark-graphx" % "3.5.1"
)
```
这一步根据你的程序中文件读取的位置来看

在运行jar包之前需要先start-dfs.sh打开hdfs服务

然后将data数据上传到hdfs中hdfs dfs -put ./data /user/hadoop 即可

运行jar包

```
/usr/local/spark/bin/spark-submit --class "DFS" ~/lab3/target/scala-2.12/lab3_2.12-1.0.jar
```



