package hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class HadoopTemp
{
    public static void main(String[] args) throws Exception
    {
        Configuration conf = new Configuration();
        Path input = new Path("hdfs://localhost:9000/yelp/");
        Path output = new Path("hdfs://localhost:9000/yelp/");
        FileSystem fileSystem = output.getFileSystem(conf);
        if (fileSystem.exists(output))
            fileSystem.delete(output, true);

        Job job = Job.getInstance(conf, "HadoopTemp");

        job.setJarByClass(HadoopTemp.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(HMapper.class);
        job.setReducerClass(HReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, input);
        FileOutputFormat.setOutputPath(job, output);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }


    /**
     * mapper for Rectangle
     * output key: block id
     * output value: rectangle parameters
     */
    public static class HMapper extends Mapper<LongWritable, Text, Text, Text>
    {
        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException
        {

        }
    }

    /**
     * reducer
     * output key: spacial join result
     * output value: null
     */
    public static class HReducer extends Reducer<Text, Text, Text, Text>
    {
        @Override
        public void reduce(Text key, Iterable<Text> value, Context context
        ) throws IOException, InterruptedException
        {

        }
    }
}