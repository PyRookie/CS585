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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StatePopularBusiness
{
    public static void main(String[] args) throws Exception
    {
        Configuration conf = new Configuration();
        Path input = new Path("hdfs://localhost:9000/yelp_academic_dataset_business.json");
        Path output = new Path("hdfs://localhost:9000/state_popular_business");

        FileSystem fileSystem = output.getFileSystem(conf);
        if (fileSystem.exists(output))
            fileSystem.delete(output, true);

        Job job = Job.getInstance(conf, "state popular business");

        job.setJarByClass(StatePopularBusiness.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(SPBMapper.class);
        job.setReducerClass(SPBReducer.class);

        FileInputFormat.setInputPaths(job, input);
        FileOutputFormat.setOutputPath(job, output);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }


    /**
     * mapper for business
     * output key: state
     * output value: business name + stars + review count
     */
    public static class SPBMapper extends Mapper<LongWritable, Text, Text, Text>
    {
        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException
        {
            Gson gson = new Gson();
            Business business = gson.fromJson(value.toString(), Business.class);
            if (business.reviewCount >= 500)
            {
                String name_star_review = business.name + ";" + business.stars + ";" + business.reviewCount;
                context.write(new Text(business.state), new Text(name_star_review));
            }
        }
    }

    /**
     * reducer
     * output key: state + name
     * output value: stars + review count
     */
    public static class SPBReducer extends Reducer<Text, Text, Text, Text>
    {
        @Override
        public void reduce(Text key, Iterable<Text> value, Context context
        ) throws IOException, InterruptedException
        {
            List<String[]> list = new ArrayList<>();
            for (Text text: value)
            {
                String[] strings = text.toString().split(";");
                list.add(strings);
            }
            Collections.sort(list, new Comparator<String[]>() {
                @Override
                public int compare(String[] o1, String[] o2) {
                    try
                    {
                        double dif = Double.parseDouble(o1[1]) - Double.parseDouble(o2[1]);
                        if (dif == 0)
                            return 0;
                        else
                            return dif > 0 ? -1 : 1;
                    }
                    catch (Exception e)
                    {
                        System.out.println("e");
                        return 0;
                    }
                }
            });
            for (int i = 0; i < Math.min(list.size(), 100); i++)
            {
                context.write(new Text(key.toString() + ";" + list.get(i)[0] + ";" + list.get(i)[1] + ";" + list.get(i)[2]), new Text());
            }
        }
    }
}