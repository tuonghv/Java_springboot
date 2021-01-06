package stackjava.com.springbootkafka.listener;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Date;

@Component
public class StartupListener implements ApplicationRunner {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String msg) {
        kafkaTemplate.send("demo", msg);
    }

    @KafkaListener(topics = "demo", groupId = "group-id")
    public void listen(String message, Acknowledgment ack) {
        System.out.println("Received Message in group - group-id: " + message);
        ack.acknowledge();
        try {
            Thread.sleep(60000);
        } catch (Exception e) {
            //TODO: handle exception
        }
        
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // for (int i = 0; i < 1000; i++) {
        //     sendMessage("Now: " + new Date());
        //     Thread.sleep(2000);
        // }
    }
}
