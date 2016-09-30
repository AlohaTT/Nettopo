package test;

import java.io.IOException;
import java.math.BigDecimal;
//import java.math.BigDecimal;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.MultipleOutputs;
//处理数据，求平均值，且按图类型 写入各自的文件
public class DA {
	public static class DAMapper extends MapReduceBase implements
			Mapper<LongWritable, Text, Text, DoubleWritable> {

		public void map(LongWritable key, Text value,
				OutputCollector<Text, DoubleWritable> output, Reporter reporter)
				throws IOException {
			// TODO Auto-generated method stub
			String DataInfo = value.toString();
			StringTokenizer takeLine = new StringTokenizer(DataInfo, "\n");
			Text wordKey = new Text();
			Text wordKey2 = new Text();
			while (takeLine.hasMoreTokens()) {
				StringTokenizer tokenizer = new StringTokenizer(
						takeLine.nextToken(), "\t");

				String Gtype = tokenizer.nextToken();
				String GasRadius = tokenizer.nextToken();
				String NodeNum = tokenizer.nextToken();
				
				String NP = tokenizer.nextToken();//失效概率
				String OldArea = tokenizer.nextToken();//原始面积
				String NewArea = tokenizer.nextToken();//新面积
				double oldValue = Double.parseDouble(OldArea);
				double newValue = Double.parseDouble(NewArea);
				if (oldValue == 0 || newValue == 0) {
					continue;
				}
				wordKey.set("Old" + Gtype + "\t" + GasRadius + "\t" + NodeNum
						+ "\t" + NP);
				wordKey2.set("New" + Gtype + "\t" + GasRadius + "\t" + NodeNum
						+ "\t" + NP);

				output.collect(wordKey, new DoubleWritable(oldValue));
				output.collect(wordKey2, new DoubleWritable(newValue));

			}

		}
	}

	public static class DAReducer extends MapReduceBase implements
			Reducer<Text, DoubleWritable, Text, DoubleWritable> {

		private MultipleOutputs mos;

		public void configure(JobConf conf) {
			mos = new MultipleOutputs(conf);
		}

		@SuppressWarnings("unchecked")
		public void reduce(Text key, Iterator<DoubleWritable> values,
				OutputCollector<Text, DoubleWritable> output, Reporter Reporter)
				throws IOException {
			// TODO Auto-generated method stub
			double sum = 0, avg = 0;
			int count = 0;
			while (values.hasNext()) {
				sum += values.next().get();
				count++;

			}
			String[] str = key.toString().trim().split("\t");
			avg = sum / count;
			//保留2位小数
			avg = new BigDecimal(avg).setScale(2, BigDecimal.ROUND_HALF_UP)
					.doubleValue();
			output = mos.getCollector(str[0], Reporter);
			output.collect(key, new DoubleWritable(avg));
		}

		public void close() throws IOException {
			mos.close();
		}

	}

	public int run(String[] args) throws Exception {
		// String[]目的是：在hadoop jar运行时，默认无需添加输入输出路径参数
		String[] staticArgs = { "hdfs://192.168.100.100:9000/5files",
				"hdfs://192.168.100.100:9000/afterdeal2" };
		if (args.length < 2)
			args = staticArgs;
		JobConf conf = new JobConf(DA.class);
		conf.set("mapreduce.task.timeout", "0");
		conf.set("mapreduce.tasktracker.map.tasks.maximum", "10");
		conf.setJobName("datadeal2");
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(DoubleWritable.class);
		conf.setMapperClass(DAMapper.class);
		conf.setReducerClass(DAReducer.class);
		
		conf.setNumReduceTasks(1);
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		MultipleOutputs.addNamedOutput(conf, "OldGG", TextOutputFormat.class,
				Text.class, DoubleWritable.class);
		MultipleOutputs.addNamedOutput(conf, "OldYG", TextOutputFormat.class,
				Text.class, DoubleWritable.class);
		MultipleOutputs.addNamedOutput(conf, "OldRNG", TextOutputFormat.class,
				Text.class, DoubleWritable.class);
		MultipleOutputs.addNamedOutput(conf, "OldDEL", TextOutputFormat.class,
				Text.class, DoubleWritable.class);
		MultipleOutputs.addNamedOutput(conf, "OldLDEL", TextOutputFormat.class,
				Text.class, DoubleWritable.class);
		MultipleOutputs.addNamedOutput(conf, "NewGG", TextOutputFormat.class,
				Text.class, DoubleWritable.class);
		MultipleOutputs.addNamedOutput(conf, "NewYG", TextOutputFormat.class,
				Text.class, DoubleWritable.class);
		MultipleOutputs.addNamedOutput(conf, "NewRNG", TextOutputFormat.class,
				Text.class, DoubleWritable.class);
		MultipleOutputs.addNamedOutput(conf, "NewDEL", TextOutputFormat.class,
				Text.class, DoubleWritable.class);
		MultipleOutputs.addNamedOutput(conf, "NewLDEL", TextOutputFormat.class,
				Text.class, DoubleWritable.class);
		//计算耗费时间
		long t1 = System.currentTimeMillis();
		JobClient.runJob(conf);
		long t2 = System.currentTimeMillis();
		long t = t2 - t1;
		System.out.println("totaltime:" + t);
		System.out.println("Finished...");
		return 0;

	}

	public static void main(String[] args) throws Exception {
		DA sr = new DA();
		sr.run(args);
		System.exit(-1);
	}

}
