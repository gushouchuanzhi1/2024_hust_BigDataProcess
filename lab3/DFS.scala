import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._

object GraphXDFSStats {
  def main(args: Array[String]): Unit = {
    // 创建Spark配置和上下文
    val conf = new SparkConf().setAppName("GraphX DFS Stats").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // 定义图的顶点
    val vertices = Array((1L, ""), (2L, ""), (3L, ""), (4L, ""), (5L, ""))

    // 定义图的边
    val edges = Array(
      Edge(1L, 3L, 1),
      Edge(2L, 4L, 1),
      Edge(5L, 2L, 1),
      Edge(3L, 5L, 1),
      Edge(1L, 4L, 1),
      Edge(4L, 5L, 1)
    )

    // 使用GraphX库创建图
    val vertexRDD = sc.parallelize(vertices)
    val edgeRDD = sc.parallelize(edges)
    val graph = Graph(vertexRDD, edgeRDD)

    // 定义DFS函数，用于遍历图并统计顶点数和边数，以及计算每个顶点的出度和入度
    def dfs(graph: Graph[String, Int], startVertex: VertexId): Unit = {
      var visitedVertices = Set[VertexId]()
      var visitedEdges = Set[Edge[Int]]()

      def dfsVisit(vertexId: VertexId): Unit = {
        visitedVertices += vertexId
        for (edge <- graph.edges.filter(_.srcId == vertexId).collect()) {
          visitedEdges += edge
          if (!visitedVertices.contains(edge.dstId)) {
            dfsVisit(edge.dstId)
          }
        }
      }

      dfsVisit(startVertex)

      // 统计顶点数和边数
      val numVertices = visitedVertices.size
      val numEdges = visitedEdges.size

      // 计算每个顶点的出度和入度
      val inDegrees = graph.inDegrees.collect().toMap
      val outDegrees = graph.outDegrees.collect().toMap

      // 输出统计结果
      println(s"顶点数: $numVertices")
      println(s"边数: $numEdges")
      println("每个顶点的出度:")
      outDegrees.foreach { case (vertexId, outDegree) =>
        println(s"$vertexId: $outDegree")
      }
      println("每个顶点的入度:")
      inDegrees.foreach { case (vertexId, inDegree) =>
        println(s"$vertexId: $inDegree")
      }
    }

    // 从顶点1开始进行DFS
    dfs(graph, 1)

    // 关闭Spark上下文
    sc.stop()
  }
}
