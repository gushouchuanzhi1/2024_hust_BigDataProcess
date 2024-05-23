import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object SCC {
  def stronglyConnectedComponents(graph: Graph[Int, Int]): Graph[VertexId, Int] = {
    graph.stronglyConnectedComponents(10)
  }

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SCC").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val vertices: RDD[(VertexId, Int)] = sc.textFile("data/graphx-wiki-vertices.txt").map { line =>
      val fields = line.split("\t")
      (fields(0).toLong, 1)
    }

    val edges: RDD[Edge[Int]] = sc.textFile("data/graphx-wiki-edges.txt").map { line =>
      val fields = line.split("\t")
      Edge(fields(0).toLong, fields(1).toLong, 1)
    }

    val graph: Graph[Int, Int] = Graph(vertices, edges)
    val sccGraph = stronglyConnectedComponents(graph)
    sccGraph.vertices.collect().foreach { case (vertexId, sccId) =>
      println(s"Vertex $vertexId is in SCC $sccId")
    }
  }
}

