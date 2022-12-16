package hadoop;

import com.google.gson.Gson;
import model.Review;
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

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ReviewWordCount
{

    static Set<String> adj_set;

    public static void main(String[] args) throws Exception
    {
        // read adjective file
        adj_set = new HashSet<>();

        File adjs = new File("/Users/stevewang/Downloads/zeyuhu/yelp_dataset/adjs.txt");
        Scanner sc = new Scanner(adjs);
        while (sc.hasNextLine())
            adj_set.add(sc.nextLine());

        Configuration conf = new Configuration();
        Path input = new Path("hdfs://localhost:9000/yelp_academic_dataset_review.json");
        Path output = new Path("hdfs://localhost:9000/review_word_count");

        FileSystem fileSystem = output.getFileSystem(conf);
        if (fileSystem.exists(output))
            fileSystem.delete(output, true);

        Job job = Job.getInstance(conf, "Review Word Count");

        job.setJarByClass(ReviewWordCount.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        job.setMapperClass(RWCMapper.class);
        job.setReducerClass(RWCReducer.class);

        FileInputFormat.setInputPaths(job, input);
        FileOutputFormat.setOutputPath(job, output);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }


    /**
     * mapper for Review
     * output key: adjective
     * output value: appear count
     */
    public static class RWCMapper extends Mapper<LongWritable, Text, Text, LongWritable>
    {
        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException
        {
            Gson gson = new Gson();
            Review review = gson.fromJson(value.toString(), Review.class);
            if (review.stars >= 2.5 && review.stars < 4.0)
            {
                String text = review.text;
                text = text.replaceAll("\\p{Punct}", "").replaceAll("\\pP", "").replaceAll("\\pP", "");
                text = text.toLowerCase();
                String[] words = text.split(" ");
                for (String word : words)
                    if (adj_set.contains(word))
                        context.write(new Text(word), new LongWritable(1));
            }
        }
    }

    /**
     * reducer
     * output key: adjective
     * output value: appear count
     */
    public static class RWCReducer extends Reducer<Text, LongWritable, Text, LongWritable>
    {
        @Override
        public void reduce(Text key, Iterable<LongWritable> value, Context context
        ) throws IOException, InterruptedException
        {
            long count = 0L;
            for (LongWritable record: value)
                count++;
            context.write(new Text(key), new LongWritable(count));
        }
    }
}