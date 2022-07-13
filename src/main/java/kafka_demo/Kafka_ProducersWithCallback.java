package kafka_demo;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Scanner;

public class Kafka_ProducersWithCallback {
    private static final Logger log= LoggerFactory.getLogger(Kafka_ProducersWithCallback.class.getSimpleName());
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

        //input: 0 for single record, 1 for multiple
        Scanner sc= new Scanner(System.in);
        int input= sc.nextInt();

        //create record and sending single data
        if(input==0){
            //create producer record
            ProducerRecord<String, String> producerRecord= new ProducerRecord<>("MyTopic", "Hey Kafka Topic");

            //send data(asynchronous) +callback
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    //executes everytime a record successfully sent or exception occur
                    if(e==null){
                        System.out.println("Success:" +
                                "\nTopic: "+ recordMetadata.topic()+
                                "\nPartition: "+ recordMetadata.partition()+
                                "\nOffset: "+ recordMetadata.offset()+
                                "\nTimestamp: "+ recordMetadata.timestamp()
                        );
                    }
                    else{
//                    System.out.println(e.getMessage());
                        log.error(e.getMessage());
                    }
                }
            });
        }
        //create record and sending multiple data
        else{
            //Sending above message 10 times to see sticky partitioning behavior
            //When we send data very fast, Kafka is intelligent enough
            // it creates batch and send it to single partition with some offset,
            // then move to another partition and start sending in it.
            for(int i=0; i<10; i++){
                //create producer record
                ProducerRecord<String, String> producerRecord10= new ProducerRecord<>("MyTopic", "Hey Kafka Topic"+i);

                //send data(asynchronous) +callback
                producer.send(producerRecord10, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        //executes everytime a record successfully sent or exception occur
                        if(e==null){
                            log.info("Success:" +
                                    "\nTopic: "+ recordMetadata.topic()+
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
                //to see the behaviour of round robin send messages in a period of time e.g. 1000 miliseconds
//                try{
//                    Thread.sleep(1000);
//                }
//                catch(Exception e){
//                    e.printStackTrace();
//                }
            }
        }

        //flush(synchronous) & close the producer
        producer.flush();
        producer.close();
    }
}
