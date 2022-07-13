package kafka_demo;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Scanner;

public class Kafka_ProducersWithKeys {
    private static final Logger log= LoggerFactory.getLogger(Kafka_ProducersWithKeys.class.getSimpleName());
    public static void main(String[] args) {
//        System.out.println("Kafka Producers with callback");
        log.info("Kafka Producers");
        //create producer properties
        Properties properties= new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create producer
        KafkaProducer<String, String> producer= new KafkaProducer<>(properties);

            //Same key goes to same partition.
            //e.g. id_1 goes to partition 1, then on next run, id_1 will go to partition 1 only.(sticky assigner)
            //run this code two times and compare the responses.
            for(int i=0; i<10; i++){
                String topic= "My Topic";
                String value= "Hey Kafka" + i;
                String key= "id_" + i;
                //create producer record
                ProducerRecord<String, String> producerRecord= new ProducerRecord<>(topic, key, value);

                //send data(asynchronous) +callback
                producer.send(producerRecord, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        //executes everytime a record successfully sent or exception occur
                        if(e==null){
                            log.info("Success:" +
                                    "\nTopic: "+ recordMetadata.topic()+
                                    "\nkey"+ producerRecord.key()+
                                    "\nPartition: "+ recordMetadata.partition()+
                                    "\nOffset: "+ recordMetadata.offset()+
                                    "\nTimestamp: "+ recordMetadata.timestamp()
                            );
                        }
                        else{
                            log.error(e.getMessage());
                        }
                    }
                });
                //to see the behaviour of round-robin send messages in a period of time e.g. 1000 miliseconds
//                try{
//                    Thread.sleep(1000);
//                }
//                catch(Exception e){
//                    e.printStackTrace();
//                }
        }

        //flush(synchronous) & close the producer
        producer.flush();
        producer.close();
    }
}
