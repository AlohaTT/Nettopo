package test;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
//import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.MultipleOutputs;
import org.deri.nettopo.util.DuplicateCoordinateException;
//输入是5个文件，每个文件中只包含一个值：图的类型名字，共5种图，所以是5个文件。目的是为了产生5个MAP，实现简单并行化效果
//输出格式：GG gasRadius sensorNodeNum 概率   原面积  新面积

public class StasMap {
	public static class StasMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,NullWritable> {
		//MultipleOutputs用于将同类型的图的结果写入同一文件，方便数据处理
		private MultipleOutputs mos;
		public void configure(JobConf conf) {
			mos=new MultipleOutputs(conf);
		}
		
		public void map(LongWritable key, Text value, OutputCollector<Text,NullWritable> output,
				Reporter reporter) throws IOException {
			// TODO Auto-generated method stub
			String GType=value.toString().trim();
			if (!GType.equals("")) {
				StasTest_Parallel st=new StasTest_Parallel();
				try {
					//MultipleOutputs 进行多文件输出，且命名
					st.run(output, mos,GType,reporter);
				} catch (DuplicateCoordinateException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		public void close() throws IOException {
			mos.close();
		}
	}

//此统计程序没有采用reduce,而是只使用map直接输出
	public int run(String[] args) throws Exception{
		//String[]目的是：在hadoop jar运行时，默认无需添加输入输出路径参数
	    	String[] staticArgs={"hdfs://192.168.100.100:9000/Ginput","hdfs://192.168.100.100:9000/5files"};
	    	if(args.length<2)
	    		args=staticArgs;
	        JobConf conf = new JobConf(StasMap.class);
	        conf.set("mapreduce.task.timeout", "0");
	        conf.set("mapreduce.tasktracker.map.tasks.maximum", "5");
	        //conf.set("mapreduce.tasktracker.reduce.tasks.maximum", "6");
	        conf.setJobName("Stas5Files");  //        
	        conf.setOutputKeyClass(Text.class);    
	        conf.setOutputValueClass(NullWritable.class);  
	        conf.setMapperClass(StasMapper.class);
	        //conf.setReducerClass(StasReducer.class);
	        conf.setNumReduceTasks(0);//统计程序结果，不需要Reduce归约排序
	        conf.setInputFormat(TextInputFormat.class);    
	        //conf.setOutputFormat(TextOutputFormat.class);//只采用了map,没有用reduce,所以可以不设置输出格式
	        Path path1 = new Path(args[0]);																													 
	        Path path2 = new Path(args[1]);
	        //当HDFS文件夹已存在时，判断是否删除
	       /* FileSystem fs=(new Path(args[1]).getFileSystem(conf));
	        if (fs.exists(path2)){
				fs.delete(path2);
			}*/
	        FileInputFormat.setInputPaths(conf, path1);
	        FileOutputFormat.setOutputPath(conf, path2);
	        //需要先注册图类型名字，然后才能对数据归类
	        MultipleOutputs.addNamedOutput(conf, "GG", TextOutputFormat.class, Text.class, Text.class);
	        MultipleOutputs.addNamedOutput(conf, "YG", TextOutputFormat.class, Text.class, Text.class);
	        MultipleOutputs.addNamedOutput(conf, "RNG", TextOutputFormat.class, Text.class, Text.class);
	        MultipleOutputs.addNamedOutput(conf, "DEL", TextOutputFormat.class, Text.class, Text.class);
	        MultipleOutputs.addNamedOutput(conf, "LDEL", TextOutputFormat.class, Text.class, Text.class);
	        
	        JobClient.runJob(conf); 

	        return 0;
		
	}
	public static void main(String[] args) throws Exception {
		
		StasMap sr=new StasMap();
		long t1=System.currentTimeMillis();
		
		sr.run(args);
		long t2=System.currentTimeMillis();
		long t=t2-t1;
		System.out.println("Finished...");
		System.out.println("Total time:"+t);
		System.exit(-1);
	}

}
