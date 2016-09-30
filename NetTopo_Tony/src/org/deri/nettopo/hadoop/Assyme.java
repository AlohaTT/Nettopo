package org.deri.nettopo.hadoop;


import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
/**
 * TextInputFormat处理的数据来自于一个InputSplit。InputSplit是根据大小划分的。
 * NLineInputFormat决定每个Mapper处理的记录数是相同的。
 */
public class Assyme {
	private static final String INPUT_PATH = "hdfs://192.168.100.100:9000/in ";
	private static final String OUT_PATH = "hdfs://192.168.100.100:9000/out";

	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();
		//设置每个map可以处理多少条记录
		conf.setInt("mapreduce.input.lineinputformat.linespermap", 1);
		final FileSystem filesystem = FileSystem.get(new URI(OUT_PATH), conf);
		filesystem.delete(new Path(OUT_PATH), true);
		
		@SuppressWarnings("deprecation")
		final Job job = new Job(conf , Assyme.class.getSimpleName());
		job.setJarByClass(Assyme.class);
		
		FileInputFormat.setInputPaths(job, INPUT_PATH);
		job.setInputFormatClass(NLineInputFormat.class);
		job.setMapperClass(MyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileOutputFormat.setOutputPath(job, new Path(OUT_PATH));
		
		job.waitForCompletion(true);
	}
	
	public static class MyMapper extends Mapper<LongWritable, Text, Text, Text>{
		//解析源文件会产生2个键值对，分别是<0,hello you><10,hello me>；所以map函数会被调用2次
		protected void map(LongWritable key, Text value, org.apache.hadoop.mapreduce.Mapper<LongWritable,Text,Text,Text>.Context context) throws java.io.IOException ,InterruptedException {
			//为什么要把hadoop类型转换为java类型？
			String line = value.toString();
			if(line.equals(""))
				return;
		};
	}
	
	//map函数执行结束后，map输出的<k,v>一共有4个，分别是<hello,1><you,1><hello,1><me,1>
	//分区，默认只有一个区
	//排序后的结果：<hello,1><hello,1><me,1><you,1>
	//分组后的结果：<hello,{1,1}>  <me,{1}>  <you,{1}>
	//归约(可选)
	
	
	
	//map产生的<k,v>分发到reduce的过程称作shuffle
	public static class MyReducer extends Reducer<Text, Text, Text, Text>{
		//每一组调用一次reduce函数，一共调用了3次
		//分组的数量与reduce函数的调用次数有什么关系？
		//reduce函数的调用次数与输出的<k,v>的数量有什么关系？
		protected void reduce(Text key, java.lang.Iterable<Text> values, org.apache.hadoop.mapreduce.Reducer<Text,Text,Text,Text>.Context context) throws java.io.IOException ,InterruptedException {
			String temp=null;
			for (Text str : values) {
				temp=str.toString();
				if(temp.contains("\tGG"))
				{
					temp=temp.replace("\tGG", "");
					context.write(new Text("GG"), new Text(temp));
				}
			}
			
		};
	}
}


