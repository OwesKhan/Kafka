package kafka_demo;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

public class Kafka_Producers {
    private static final Logger log= LoggerFactory.getLogger(Kafka_Producers.class.getSimpleName());
    public static void main(String[] args) {
//        System.out.println("Kafka Producers");
        log.info("Kafka Producers");
        //create producer properties (allows to configure kafka)
        Properties properties= new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create producer
        KafkaProducer<String, String> producer= new KafkaProducer<>(properties);
//        char []a= {'A', 'B'};
        //create producer record
        ProducerRecord<String, String> producerRecord= new ProducerRecord<>("MyTopic", "Hey Kafka Topic");

        //send data(asynchronous)
        producer.send(producerRecord);

        //flush(synchronous) & close the producer
        producer.flush();
        producer.close();
    }
}
