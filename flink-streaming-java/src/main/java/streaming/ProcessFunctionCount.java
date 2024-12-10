package streaming;

import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class ProcessFunctionCount   extends ProcessWindowFunction<KafkaMessage,String, String, TimeWindow> {

    @Override
    public void process(String key, Context context, Iterable<KafkaMessage> input, Collector<String> out) {
        long count = 0;
        for (KafkaMessage in: input) {
            count++;
        }
        out.collect("window:" + context.window() + " key:"+ key + " count:" + count);
    }
}
