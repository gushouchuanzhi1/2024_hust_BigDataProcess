package org.apache.hadoop.examples;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;

import com.codahale.metrics.Timer.Context;


public class SortRank {
  public static class SortMapper extends Mapper<Object, Text, DoubleWritable, Text> {
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      String id = itr.nextToken();
      double weight = Double.parseDouble(itr.nextToken());
      context.write(new DoubleWritable(weight), new Text(id));
    }
  }

  public static class SortReducer extends Reducer<DoubleWritable, Text, Text, DoubleWritable> {
    public void reduce(DoubleWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      for (Text val : values) {
        context.write(val, key);
      }
    }
  }

  public static class DescendingDoubleComparator extends WritableComparator {
    protected DescendingDoubleComparator() {
      super(DoubleWritable.class, true);
    }

    @SuppressWarnings("rawtypes")
    public int compare(WritableComparable w1, WritableComparable w2) {
      DoubleWritable key1 = (DoubleWritable) w1;
      DoubleWritable key2 = (DoubleWritable) w2;          
      return -1 * key1.compareTo(key2);
    }
  }
}