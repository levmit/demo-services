package com.microservice.test.test_app.kafka;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.twitter.bijection.Injection;
import com.twitter.bijection.avro.GenericAvroCodecs;

public class SimpleStringProducer {

	public static void main(String[] args) {

		SimpleStringProducer producer = new SimpleStringProducer();
		producer.send();

	}

	private void send() {
		ClassLoader classLoader = new SimpleStringProducer().getClass().getClassLoader();
		File schemaFile = new File(classLoader.getResource("test.avsc").getFile());

		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

		Schema.Parser parser = new Schema.Parser();
		Schema schema = null;
		try {
			schema = parser.parse(schemaFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Injection<GenericRecord, byte[]> recordInjection = GenericAvroCodecs.toBinary(schema);

		KafkaProducer<String, byte[]> producer = new KafkaProducer<>(props);

		for (int i = 0; i < 1000; i++) {
			GenericData.Record avroRecord = new GenericData.Record(schema);
			avroRecord.put("str1", "Str 1-" + i);
			avroRecord.put("str2", "Str 2-" + i);
			avroRecord.put("int1", i);

			byte[] bytes = recordInjection.apply(avroRecord);

			ProducerRecord<String, byte[]> record = new ProducerRecord<>("mytopic", bytes);
			producer.send(record);

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		producer.close();
	}
}
