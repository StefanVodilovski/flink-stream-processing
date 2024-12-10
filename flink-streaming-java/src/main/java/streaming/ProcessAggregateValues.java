package streaming;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

public class ProcessAggregateValues extends ProcessWindowFunction<AggregateAccumulator,String, String, TimeWindow> {

    @Override
    public void process(String key, Context context, Iterable<AggregateAccumulator> elements, Collector<String> out) throws Exception {
        AggregateAccumulator accumulator = elements.iterator().next();

        try{
            String jsonOutput = toJson(key,context,accumulator);
            out.collect(jsonOutput);
        }
        catch(JsonProcessingException e){
            out.collect("");
        }


    }


    private String toJson(String key, Context context,AggregateAccumulator accumulator) throws JsonProcessingException {
        Map<String, Object> outputMap = new HashMap<>();
        outputMap.put("key", key);
        outputMap.put("window_start", context.window().getStart());
        outputMap.put("window_end", context.window().getEnd());
        outputMap.put("min_value", accumulator.minValue);
        outputMap.put("max_value", accumulator.maxValue);
        outputMap.put("average_value", accumulator.sum / accumulator.count);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(outputMap);
    }
}
