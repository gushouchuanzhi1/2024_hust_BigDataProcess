import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PageRank {
    public static class PageRankMapper extends Mapper<Object, Text, Text, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] parts = value.toString().split(" ");
            if (parts.length == 2) {
                String fromNode = parts[0];
                String toNode = parts[1];
                context.write(new Text(fromNode), new Text("LINK:" + toNode));
                context.write(new Text(toNode), new Text("PR:1.0"));
            }
        }
    }

    public static class PageRankReducer extends Reducer<Text, Text, Text, DoubleWritable> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Set<String> outLinks = new HashSet<>();
            double sumPageRank = 0.0;
            for (Text val : values) {
                String valStr = val.toString();
                if (valStr.startsWith("LINK:")) {
                    outLinks.add(valStr.substring(5));
                } else if (valStr.startsWith("PR:")) {
                    sumPageRank += Double.parseDouble(valStr.substring(3));
                }
            }
            double newPageRank = 0.85 * sumPageRank + 0.15;
            context.write(key, new DoubleWritable(newPageRank));
            for (String link : outLinks) {
                context.write(new Text(link), new Text("PR:" + newPageRank / outLinks.size()));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://localhost:9000"); // 设置HDFS路径
        String[] otherArgs = new String[]{"input", "output"}; // 指定输入输出路径
        Path inputPath = new Path(otherArgs[0]);
        Path outputPath = new Path(otherArgs[1]);
        
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }
        
        Job job = Job.getInstance(conf, "PageRank");
        job.setJarByClass(PageRank.class);
        job.setMapperClass(PageRankMapper.class);
        job.setReducerClass(PageRankReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
