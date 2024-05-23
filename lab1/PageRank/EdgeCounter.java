package org.apache.hadoop.examples;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.yarn.server.webproxy.ProxyUtils.Page;

public class EdgeCounter {

  public enum PageCounter {
    TotalPage
  }

  /*
   * 计数器，统计有多少个节点
   */
  public static class NodeCounterMapper extends Mapper<Object, Text, Text, NullWritable> {
    private Text word = new Text();

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      String line = value.toString();
      if (line.startsWith("#")) {
        return;
      }
      StringTokenizer itr = new StringTokenizer(line);

      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        context.write(word, NullWritable.get());
      }

    }  
  }
  public static class NodeCounterReducer extends Reducer<Text, NullWritable, Text, NullWritable> {
    public void reduce(Text key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
      context.write(key, NullWritable.get());
      context.getCounter(PageCounter.TotalPage).increment(1);
    }
  }


  public static class EdgeMapper extends Mapper<Object, Text, Text, Text> {
    private Text word = new Text();

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      String line = value.toString();
      if (line.startsWith("#")) {
        return;
      }
      StringTokenizer itr = new StringTokenizer(line);
      String startNode = "";
      String endNode = "";
      if (itr.hasMoreTokens()) {
        startNode = itr.nextToken();
        if (itr.hasMoreTokens()) {
          endNode = itr.nextToken();
          context.write(new Text(startNode), new Text(endNode));
        }
      }
      // System.out.println("startNode: " + startNode + ", endNode: " + endNode);
    }
  }

  public static class EdgeReducer1 extends Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      String edges = "";
      for (Text val : values) {
        edges += val.toString() + " ";
      }
      context.write(key, new Text(edges.trim()));
      // context.getCounter(PageCounter.TotalPage).increment(1);
    }
  }

  public static class MyMapper extends Mapper<LongWritable, Text, Text, Text> {
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      String line = value.toString();
      String[] fields = line.split("\t");

      if (fields.length >= 2) {
        String startPoint = fields[0];
        String endPointSet = fields[1];

        context.write(new Text(startPoint), new Text(endPointSet));
      }
    }
  }

  public static class EdgeReducer2 extends Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      Configuration conf = context.getConfiguration();
      double n = conf.getDouble("n", 1.0);

      String edges = "";
      for (Text val : values) {
        edges += val.toString() + " ";
      }
      edges = edges.trim();

      String output = key.toString() + "\t" + n + "\t" + edges;
      context.write(new Text(output), new Text(""));
    }
  }
}
