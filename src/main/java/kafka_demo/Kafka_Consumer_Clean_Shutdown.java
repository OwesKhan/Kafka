package kafka_demo;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class Kafka_Consumer_Clean_Shutdown {
    private static final Logger log= LoggerFactory.getLogger(Kafka_Producers.class.getSimpleName());
    public static void main(String[] args) {
//        System.out.println("Kafka Producers");
        log.info("Kafka Consumer");
        String bootstrap_server= "localhost:9092";
        String groupId= "my-consumer";
        String topic= "MyTopic";

        //create consumer config
        Properties properties= new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_server);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //Auto offset param:"none/earliest/latest"
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        //create consumer
        KafkaConsumer<String, String> consumer= new KafkaConsumer<>(properties);

        //get reference of current thread
        final Thread mainThread= Thread.currentThread();

        //adding the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                log.info("Detected Shutdown, lets exit by calling consumer.wakeup()");
                consumer.wakeup();

                //join main thread to allow execution on code in main thread
                try{
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        try{
            //subscribe to a topic(s)
            consumer.subscribe(Arrays.asList(topic));


            //poll for new data
            while(true){
                log.info("Polling");
                //consumer.poll will throw exception on exit
                ConsumerRecords<String, String> records= consumer.poll(Duration.ofMillis(1000));
                for(ConsumerRecord<String, String> record: records){
                    log.info("key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset: " + record.offset());
                }
            }
        }
        catch (WakeupException e){
            log.info("Wakeup Exception");
            //Should be ignored as it's an expected exception when closing a consumer
        }
        catch(Exception e){
            log.info("Unexpected Exception");
        }
        finally {
            consumer.close();
            log.info("Consumer has gracefully closed");
        }


    }
}
