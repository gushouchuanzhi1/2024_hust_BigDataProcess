import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageRank {
    // 网页数
    private int numPages;
    // 邻接矩阵
    private int[][] adjacencyMatrix;
    // 阻尼系数
    private double dampingFactor;
    // 迭代次数
    private int iterations;
    // PageRank 值
    private double[] pageRanks;

    // 构造函数
    public PageRank(int numPages, int[][] adjacencyMatrix, double dampingFactor, int iterations) {
        this.numPages = numPages;
        this.adjacencyMatrix = adjacencyMatrix;
        this.dampingFactor = dampingFactor;
        this.iterations = iterations;
        this.pageRanks = new double[numPages];
    }

    // 从文件中读取数据并初始化 PageRank 对象
    public static PageRank fromFile(String filename, double dampingFactor, int iterations) throws IOException {
        List<int[]> edges = new ArrayList<>();
        int numPages = 0;

        // 读取文件
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            // 跳过前四行
            while (lineNumber < 4 && (line = reader.readLine()) != null) {
                lineNumber++;
            }
            // 读取数据行
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                int fromNodeId = Integer.parseInt(parts[0]);
                int toNodeId = Integer.parseInt(parts[1]);
                edges.add(new int[]{fromNodeId, toNodeId});
            }
            numPages = edges.stream().mapToInt(edge -> Math.max(edge[0], edge[1])).max().orElse(0);
        }

        // 构建邻接矩阵
        int[][] adjacencyMatrix = new int[numPages][numPages];
        for (int[] edge : edges) {
            adjacencyMatrix[edge[0] - 1][edge[1] - 1] = 1;
        }

        return new PageRank(numPages, adjacencyMatrix, dampingFactor, iterations);
    }

    // 计算 PageRank
    public void calculatePageRank() {
        // 初始化 PageRank 值
        Arrays.fill(pageRanks, 1.0 / numPages);

        // 迭代计算
        for (int iter = 0; iter < iterations; iter++) {
            double[] newPageRanks = new double[numPages];

            // 计算新的 PageRank 值
            for (int i = 0; i < numPages; i++) {
                double sum = 0.0;
                for (int j = 0; j < numPages; j++) {
                    if (adjacencyMatrix[j][i] == 1) {
                        sum += pageRanks[j] / getOutDegree(j);
                    }
                }
                newPageRanks[i] = (1 - dampingFactor) / numPages + dampingFactor * sum;
            }

            // 更新 PageRank 值
            pageRanks = newPageRanks;
        }
    }

    // 获取指定节点的出度
    private int getOutDegree(int node) {
        int outDegree = 0;
        for (int i = 0; i < numPages; i++) {
            if (adjacencyMatrix[node][i] == 1) {
                outDegree++;
            }
        }
        return outDegree;
    }

    // 打印 PageRank 值
    public void printPageRanks() {
        System.out.println("PageRank Values:");
        for (int i = 0; i < numPages; i++) {
            System.out.println("Page " + (i + 1) + ": " + pageRanks[i]);
        }
    }

    public static void main(String[] args) {
        String filename = "G:\\code\\idea\\PageRank\\src\\data.txt"; // 数据文件路径
        double dampingFactor = 0.85;
        int iterations = 100;

        try {
            PageRank pageRank = PageRank.fromFile(filename, dampingFactor, iterations);
            pageRank.calculatePageRank();
            pageRank.printPageRanks();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
