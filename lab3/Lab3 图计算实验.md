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

运行jar包

/usr/local/spark/bin/spark-submit --class "DFS" ~/lab3/target/scala-2.12/

```
/usr/local/spark/bin/spark-submit --class "WordCountApp" ~/sparkapp/target/scala-2.12/simple-project_2.12-1.0.jar
```





在 ./sparkapp/src/main/scala 下建立一个名为 WordCountApp.scala 的文件（`vim ./sparkapp/src/main/scala/WordCountApp.scala`）

```
import java.io.File

import scala.io.Source

object WordCountApp {

  def main(args: Array[String]): Unit = {
   //文件路径
   val filePath = "file:///usr/local/hadoop/README.txt "
   val codec = "utf-8"
   //打开文件
   val file = Source.fromFile(filePath, codec) 
   val wc = file.getLines().flatMap(_.split("\t")).toList.map((_, 1)).groupBy((_._1)).mapValues(_.size)

println(wc)
   // 关闭文件
   file.close()
  }
 }


```

./sparkapp 中新建文件 simple.sbt（`vim ./sparkapp/simple.sbt`）

```
name := "Simple Project" 

version := "1.0" 

scalaVersion := "2.12.18" 

libraryDependencies += "org.apache.spark" %% "spark-core" % "3.5.1" 
```

然后在/sparkapp文件夹中运行以下代码进行打包：

```
/usr/local/sbt/sbt package
```

打包完成之后使用： 记得需要修改成自己的scala版本

```
/usr/local/spark/bin/spark-submit --class "WordCountApp" ~/sparkapp/target/scala-2.12/simple-project_2.12-1.0.jar
```

