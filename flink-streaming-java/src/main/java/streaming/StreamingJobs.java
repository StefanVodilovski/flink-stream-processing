/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package streaming;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * Skeleton code for the datastream walkthrough
 */
public class StreamingJobs {
	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		String brokers = "localhost:9092";

		KafkaSource<String> source = KafkaSource.<String>builder()
				.setBootstrapServers(brokers)
				.setTopics("sensors")
				.setGroupId("my-group")
				.setStartingOffsets(OffsetsInitializer.latest())
				.setValueOnlyDeserializer(new SimpleStringSchema())
				.build();

		DataStream<String> rawJsonStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");


		DataStream<String> countByKey = rawJsonStream
				.map(new KafkaMessageDeserializer())
				.keyBy(KafkaMessage::getKey)
				.window(SlidingProcessingTimeWindows.of(Time.seconds(3),Time.seconds(2)))
				.process(new ProcessFunctionCount())
				.name("Count by key");

		DataStream<String> averageValues = rawJsonStream
				.map(new KafkaMessageDeserializer())
				.keyBy(KafkaMessage::getKey)
				.window(SlidingProcessingTimeWindows.of(Time.seconds(6), Time.seconds(2)))
				.aggregate(new AverageAggregate(), new ProcessAggregateValues())
				.name("average value per key");

		KafkaSink<String> sink_result_1 = KafkaSink.<String>builder()
				.setBootstrapServers(brokers)
				.setRecordSerializer(KafkaRecordSerializationSchema.builder()
						.setTopic("result_1")
						.setValueSerializationSchema(new SimpleStringSchema())
						.build()
				)
				.setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
				.build();

		KafkaSink<String> sink_result_2 = KafkaSink.<String>builder()
				.setBootstrapServers(brokers)
				.setRecordSerializer(KafkaRecordSerializationSchema.builder()
						.setTopic("result_2")
						.setValueSerializationSchema(new SimpleStringSchema())
						.build()
				)
				.setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
				.build();

		countByKey.sinkTo(sink_result_1);
		averageValues.sinkTo(sink_result_2);


		env.execute();


	}
}
