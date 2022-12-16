package hadoop;

import com.google.gson.Gson;
import model.Business;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class AvgStars
{
    public static void main(String[] args) throws Exception
    {

        Path input = new Path("hdfs://localhost:9000/yelp_academic_dataset_business.json");
        Path output = new Path("hdfs://localhost:9000/avg_star");

        Configuration conf = new Configuration();

        FileSystem fileSystem = output.getFileSystem(conf);
        if (fileSystem.exists(output))
            fileSystem.delete(output, true);

        Job job = Job.getInstance(conf, "AvgStars");

        job.setJarByClass(AvgStars.class);
        job.setMapperClass(AvgStars.ASMapper.class);
        job.setReducerClass(AvgStars.ASReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

//        job.setMapOutputKeyClass(Text.class);
//        job.setMapOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job, input);
        FileOutputFormat.setOutputPath(job, output);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }


    /**
     * mapper for business
     * output key: category
     * output value: number of dollar symbol
     */
    public static class ASMapper extends Mapper<LongWritable, Text, Text, DoubleWritable>
    {
        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException
        {
            Gson gson = new Gson();
            Business business = gson.fromJson(value.toString(), Business.class);
            if (business.categories != null)
            {
                String[] categories = business.categories.split(", ");
                for (String category: categories)
                    context.write(new Text(category), new DoubleWritable(business.stars));
            }
        }
    }

    /**
     * reducer
     * output key: category
     * output value: avg stars
     */
    public static class ASReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable>
    {
        @Override
        public void reduce(Text key, Iterable<DoubleWritable> value, Context context
        ) throws IOException, InterruptedException
        {
            double sum = 0.0;
            long count = 0L;
            for (DoubleWritable star: value)
            {
                count++;
                sum += star.get();
            }
            if (count >= 100L)
                context.write(key, new DoubleWritable(sum / count));
        }
    }
}