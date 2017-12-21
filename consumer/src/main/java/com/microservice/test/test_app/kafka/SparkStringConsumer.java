package com.microservice.test.test_app.kafka;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.twitter.bijection.Injection;
import com.twitter.bijection.avro.GenericAvroCodecs;

import kafka.serializer.DefaultDecoder;
import kafka.serializer.StringDecoder;

public class SparkStringConsumer {
	public static final String USER_SCHEMA = "{" + "\"type\":\"record\"," + "\"name\":\"myrecord\"," + "\"fields\":["
			+ "  { \"name\":\"str1\", \"type\":\"string\" }," + "  { \"name\":\"str2\", \"type\":\"string\" },"
			+ "  { \"name\":\"int1\", \"type\":\"int\" }" + "]}";

	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("kafka-sandbox").setMaster("local[*]");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaStreamingContext ssc = new JavaStreamingContext(sc, new Duration(2000));

		Set<String> topics = Collections.singleton("mytopic");
		Map<String, String> kafkaParams = new HashMap<>();
		kafkaParams.put("metadata.broker.list", "localhost:9092");

		JavaPairInputDStream<String, byte[]> directKafkaStream = KafkaUtils.createDirectStream(ssc, String.class,
				byte[].class, StringDecoder.class, DefaultDecoder.class, kafkaParams, topics);

		directKafkaStream.foreachRDD(rdd -> {
			rdd.foreach(avroRecord -> {
				Schema.Parser parser = new Schema.Parser();
				Schema schema = parser.parse(USER_SCHEMA);
				Injection<GenericRecord, byte[]> recordInjection = GenericAvroCodecs.toBinary(schema);
				GenericRecord record = recordInjection.invert(avroRecord._2).get();

				System.out.println("str1= " + record.get("str1") + ", str2= " + record.get("str2") + ", int1="
						+ record.get("int1"));
			});
		});

		ssc.start();
		try {
			ssc.awaitTermination();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
