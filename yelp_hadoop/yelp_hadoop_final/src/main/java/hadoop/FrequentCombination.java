package hadoop;

import com.google.gson.Gson;
import model.Business;
import org.apache.commons.io.IOUtils;
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
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Three combination, filter categories / combination that its count exceeds 100
 */
public class FrequentCombination
{

    static List<String[]> category_pairs = new ArrayList<>();

    public static void main(String[] args) throws Exception
    {
        Configuration conf = new Configuration();
        Path input = new Path("hdfs://localhost:9000/yelp_academic_dataset_business.json");
        Path output1 = new Path("hdfs://localhost:9000/frequent_combination/iteration1");
        Path output2 = new Path("hdfs://localhost:9000/frequent_combination/iteration2");

        FileSystem fs1 = output1.getFileSystem(conf);
        if (fs1.exists(output1))
            fs1.delete(output1, true);

        FileSystem fs2 = output2.getFileSystem(conf);
        if (fs2.exists(output2))
            fs2.delete(output2, true);

        // iteration 1
        Job job1 = Job.getInstance(conf, "Frequent Combination");

        job1.setJarByClass(FrequentCombination.class);

        job1.setMapperClass(Mapper1.class);
        job1.setReducerClass(Reducer1.class);

        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(LongWritable.class);

        FileInputFormat.setInputPaths(job1, input);
        FileOutputFormat.setOutputPath(job1, output1);
        job1.waitForCompletion(true);
        // System.exit(job1.waitForCompletion(true) ? 0 : 1);

        // get all categories with more than x records (default x = 1000)
        URI uri = new URI("hdfs://localhost:9000/frequent_combination/iteration1/part-r-00000");
        FileSystem fs = FileSystem.get(uri,conf);
        InputStream in = fs.open(new Path(uri));
        String output = IOUtils.toString(in, StandardCharsets.UTF_8);
        String[] categories = output.split("\n");
        // generate category pairs
        for (int i = 0; i < categories.length; i++)
            categories[i] = categories[i].split("\t")[0];
        for (int i = 0; i < categories.length; i++)
            for (int j = 0; j < categories.length; j++)
                if (i != j)
                    category_pairs.add(new String[]{categories[i], categories[j]});

        // iteration 2
        Job job2 = Job.getInstance(conf, "Frequent Combination");

        job2.setJarByClass(FrequentCombination.class);

        job2.setMapperClass(Mapper2.class);
        job2.setReducerClass(Reducer2.class);

        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(LongWritable.class);

        FileInputFormat.setInputPaths(job2, input);
        FileOutputFormat.setOutputPath(job2, output2);
        System.exit(job2.waitForCompletion(true) ? 0 : 1);
    }


    /**
     * mapper for iteration 1
     * output key: category
     * output value: count
     */
    public static class Mapper1 extends Mapper<LongWritable, Text, Text, LongWritable>
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
                    context.write(new Text(category), new LongWritable(1));
            }
        }
    }

    /**
     * reducer for iteration 1
     * output key: category
     * output value: count
     */
    public static class Reducer1 extends Reducer<Text, LongWritable, Text, LongWritable>
    {
        @Override
        public void reduce(Text key, Iterable<LongWritable> value, Context context)
                throws IOException, InterruptedException
        {
            long count = 0L;
            for (LongWritable l: value)
                count++;
            if (count >= 1000L)
                context.write(key, new LongWritable(count));
        }
    }

    /**
     * mapper for iteration 2
     * output key: 2 categories
     * output value: count
     */
    public static class Mapper2 extends Mapper<LongWritable, Text, Text, LongWritable>
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
                Set<String> category_set = new HashSet<>(Arrays.asList(categories));
                for (String[] pair : category_pairs)
                    if (category_set.contains(pair[0]) && category_set.contains(pair[1]))
                        context.write(new Text(pair[0] + "," + pair[1]), new LongWritable(1));
            }
        }
    }

    /**
     * reducer for iteration 2
     * output key: 2 categories
     * output value: count
     */
    public static class Reducer2 extends Reducer<Text, LongWritable, Text, LongWritable>
    {
        @Override
        public void reduce(Text key, Iterable<LongWritable> value, Context context)
                throws IOException, InterruptedException
        {
            long count = 0L;
            for (LongWritable l: value)
                count++;
            if (count >= 1000L)
                context.write(key, new LongWritable(count));
        }
    }
}