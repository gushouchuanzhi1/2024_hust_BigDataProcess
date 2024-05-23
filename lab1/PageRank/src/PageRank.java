package org.apache.hadoop.examples;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.codehaus.jackson.map.DeserializerFactory.Config;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.examples.EdgeCounter;
import org.apache.hadoop.examples.EdgeCounter.EdgeMapper;
import org.apache.hadoop.examples.EdgeCounter.EdgeReducer1;
import org.apache.hadoop.examples.EdgeCounter.EdgeReducer2;
import org.apache.hadoop.examples.EdgeCounter.MyMapper;
import org.apache.hadoop.examples.EdgeCounter.NodeCounterMapper;
import org.apache.hadoop.examples.EdgeCounter.NodeCounterReducer;
import org.apache.hadoop.examples.SortRank.DescendingDoubleComparator;
import org.apache.hadoop.examples.SortRank.SortMapper;
import org.apache.hadoop.examples.SortRank.SortReducer;

import java.io.IOException;
// import java.nio.file.FileSystem;
import java.util.StringTokenizer;

public class PageRank {

  public static double threshold = 1.0E-7; // 收敛阈值
  public static int iternum = 0; // 最大迭代次数
  public static int num_reduce = 1; // reduce数量

  public enum PageCounter {
    TotalPages,
    ConvergePage
  }

  public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      String id = itr.nextToken(); // 当前网页
      double weight = Double.parseDouble(itr.nextToken()); // 当前网页的权值
      double out_weight = weight / itr.countTokens(); // 为每个网页投票的权值
      // System.out.println(id + " " + weight + " " + out_weight);
      String outs = "";
      while (itr.hasMoreTokens()) {
        String out_id = itr.nextToken();
        outs += out_id + " ";
        context.write(new Text(out_id), new Text("&" + out_weight));
      }
      // System.out.println(outs);
      context.write(new Text(id), new Text(outs));
      context.write(new Text(id), new Text("#" + weight));
      // '#'表示当前的权值，用于reduce阶段判断是否收敛
      // 枚举计数器，计算有几个Page
      // context.getCounter(PageCounter.TotalPage).increment(1);
    }
  }

  public static class WeightReducer extends Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      Configuration conf = context.getConfiguration();
      double n = conf.getDouble("n", 0);
      double sum_weight = 0; // 新的权值
      float pre_weight = 0; // 上一轮的权值，比较是否收敛
      String outs = "";
      for (Text val : values) {
        String tmp = val.toString();
        if (tmp.startsWith("&"))
          sum_weight += Float.parseFloat(tmp.substring(1));
        else if (tmp.startsWith("#"))
          pre_weight = Float.parseFloat(tmp.substring(1));
        else
          outs = tmp;
      }
    //  System.out.println("n: " + n);

      sum_weight = 0.85d * sum_weight + 0.15d * n; // 平滑处理，处理终止点和陷阱
      context.write(key, new Text(sum_weight + " " + outs));
      if (sum_weight - pre_weight <= threshold) // 枚举计数器，计算有几个已收敛
        context.getCounter(PageCounter.ConvergePage).increment(1);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    String in_path = args[0];

    String node_counter_path = "node_counter";
    String Counter_temppath = "Counter_temp";
    String Counter_outpath = "Counter_out";
    String PageRank_inpath = Counter_outpath;

    String out_path = args[1] + "/iter";
    String Sort_outpath = args[2];

    System.out.println("in_path: " + in_path + ", Counter_outpath: " + Counter_outpath + ", PageRank_inpath: " + PageRank_inpath + ", out_path: " + out_path);
    FileSystem fs = FileSystem.get(conf);

    Path outputPath1 = new Path(node_counter_path);
    Path outputPath2 = new Path(Counter_temppath);
    Path outputPath3 = new Path(Counter_outpath);
    Path outputPath4 = new Path(args[1]);
    if (fs.exists(outputPath1)) {
      // 如果输出目录存在，删除它
      fs.delete(outputPath1, true);
      System.out.println(outputPath1 + " is deleted");
    }
    if (fs.exists(outputPath2)) {
      // 如果输出目录存在，删除它
      fs.delete(outputPath2, true);
      System.out.println(outputPath2 + " is deleted");
    }
    if (fs.exists(outputPath3)) {
      // 如果输出目录存在，删除它
      fs.delete(outputPath3, true);
      System.out.println(outputPath3 + " is deleted");
    }
    if (fs.exists(outputPath4)) {
      // 如果输出目录存在，删除它
      fs.delete(outputPath4, true);
      System.out.println(outputPath4 + " is deleted");
    }

    System.out.println("node count job");
    Job countJob = Job.getInstance(conf, "node count");
    countJob.setJarByClass(PageRank.class);
    countJob.setMapperClass(NodeCounterMapper.class);    
    countJob.setReducerClass(NodeCounterReducer.class);
    countJob.setOutputKeyClass(Text.class);
    countJob.setOutputValueClass(NullWritable.class);
    FileInputFormat.addInputPath(countJob, new Path(in_path));
    FileOutputFormat.setOutputPath(countJob, new Path(node_counter_path));
    countJob.waitForCompletion(true);

    long totalPage = countJob.getCounters().findCounter(EdgeCounter.PageCounter.TotalPage).getValue();
    System.out.println("TotalPage: " + totalPage);
    double n = 1.0 / totalPage;
    System.out.println("n: " + n);
    conf.set("n", Double.toString(n));

    fs.delete(outputPath1, true);
    System.out.println(outputPath1 + " is deleted");

    System.out.println("edge count job");
    Job job1 = Job.getInstance(conf, "edge counter");
    job1.setJarByClass(PageRank.class);
    job1.setMapperClass(EdgeMapper.class);
    job1.setReducerClass(EdgeReducer1.class);
    job1.setOutputKeyClass(Text.class);
    job1.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job1, new Path(in_path));
    FileOutputFormat.setOutputPath(job1, new Path(Counter_temppath));
    job1.waitForCompletion(true);

    // 添加初始权
    System.out.println("add initial weight");
    Job job2 = Job.getInstance(conf, "edge counter job 2");
    job2.setJarByClass(EdgeCounter.class);
    job2.setMapperClass(MyMapper.class);
    job2.setReducerClass(EdgeReducer2.class);
    job2.setOutputKeyClass(Text.class);
    job2.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job2, new Path(Counter_temppath));
    FileOutputFormat.setOutputPath(job2, new Path(Counter_outpath));
    job2.waitForCompletion(true);

    fs.delete(outputPath2, true);
    System.out.println(outputPath2 + " is deleted");


    // PageRank工作
    System.out.println("PageRank job");
    int i = 1;
    long convergepage = 0;
    while(true) {
      System.out.println(i + " iteration");
      Job job = Job.getInstance(conf, "page rank");
      job.setJarByClass(PageRank.class);
      job.setMapperClass(TokenizerMapper.class);
      job.setReducerClass(WeightReducer.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(Text.class);
      FileInputFormat.addInputPath(job, new Path(PageRank_inpath));
      FileOutputFormat.setOutputPath(job, new Path(out_path + i));
      job.setNumReduceTasks(num_reduce); // 设置reduce数量
      PageRank_inpath = out_path + i; // 设置下一轮的输入
      job.waitForCompletion(true);

      Counters counters = job.getCounters();
      
      Counter counter = counters.findCounter(PageCounter.ConvergePage);
      convergepage = counter.getValue(); // 收敛计数值
      System.out.println("total page: " + totalPage);
      System.out.println("converge page: " + convergepage);
      if (totalPage == convergepage) {
        System.out.print("converge at iteration: " + i);
        break;
      }
      if (i == iternum) {
        System.out.println("iteration exceed " + iternum);
        break;
      }
      i++;
    }

    System.out.println("Sort job");
    Path outputPath5 = new Path(Sort_outpath);
    if (fs.exists(outputPath5)) {
      // 如果输出目录存在，删除它
      fs.delete(outputPath5, true);
      System.out.println(outputPath5 + " is deleted");
    }
    Job sortJob = Job.getInstance(conf, "sort");
    sortJob.setJarByClass(PageRank.class);
    sortJob.setMapperClass(SortMapper.class);
    sortJob.setReducerClass(SortReducer.class);
    sortJob.setOutputKeyClass(DoubleWritable.class);
    sortJob.setOutputValueClass(Text.class);
    sortJob.setSortComparatorClass(DescendingDoubleComparator.class);
    FileInputFormat.addInputPath(sortJob, new Path(PageRank_inpath));
    FileOutputFormat.setOutputPath(sortJob, outputPath5);
    sortJob.waitForCompletion(true);
    System.out.println("TotalPage: " +totalPage + "\n" + "Converge Page: " + convergepage);
    if (i > iternum) // iteration = 0
      System.out.println("converge at iteration: " + i);
    else 
      System.out.println("iteration exceed " + iternum);
  }

}