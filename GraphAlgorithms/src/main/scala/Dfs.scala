import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object DFS {
  def depthFirstSearch[VD](graph: Graph[VD, Int], startVertexId: VertexId): List[VertexId] = {
    var visited = Set[VertexId]()
    var result = List[VertexId]()
    
    def dfs(vertexId: VertexId): Unit = {
      if (!visited.contains(vertexId)) {
        visited += vertexId
        result = vertexId :: result
        val neighbors = graph.edges.filter(e => e.srcId == vertexId).map(_.dstId).collect()
        neighbors.foreach(dfs)
      }
    }
    
    dfs(startVertexId)
    result.reverse
  }

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("DFS").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val vertices: RDD[(VertexId, String)] = sc.textFile("data/graphx-wiki-vertices.txt").map { line =>
      val fields = line.split("\t")
      (fields(0).toLong, fields(1))
    }

    val edges: RDD[Edge[Int]] = sc.textFile("data/graphx-wiki-edges.txt").map { line =>
      val fields = line.split("\t")
      Edge(fields(0).toLong, fields(1).toLong, 1)
    }

    val graph: Graph[String, Int] = Graph(vertices, edges)

    val startVertexId: VertexId = vertices.first()._1
    val result = depthFirstSearch(graph, startVertexId)
    println(s"DFS Result starting from vertex $startVertexId: ${result.mkString(", ")}")
  }
}

