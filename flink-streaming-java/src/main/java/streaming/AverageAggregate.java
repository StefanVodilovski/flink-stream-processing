package streaming;



import org.apache.flink.api.common.functions.AggregateFunction;

public class AverageAggregate  implements AggregateFunction<KafkaMessage, AggregateAccumulator, AggregateAccumulator> {
    @Override
    public AggregateAccumulator createAccumulator() {
        return new AggregateAccumulator();
    }

    @Override
    public AggregateAccumulator add(KafkaMessage message, AggregateAccumulator accumulator) {
        int value = message.getValue();
        accumulator.count++;
        accumulator.sum += value;
        accumulator.minValue = Math.min(accumulator.minValue, value);
        accumulator.maxValue = Math.max(accumulator.maxValue, value);
        return accumulator;
    }

    @Override
    public AggregateAccumulator getResult(AggregateAccumulator accumulator) {
        return accumulator;
    }

    @Override
    public AggregateAccumulator merge(AggregateAccumulator a, AggregateAccumulator b) {
        a.count += b.count;
        a.sum += b.sum;
        a.minValue = Math.min(a.minValue, b.minValue);
        a.maxValue = Math.max(a.maxValue, b.maxValue);
        return a;
    }

}
