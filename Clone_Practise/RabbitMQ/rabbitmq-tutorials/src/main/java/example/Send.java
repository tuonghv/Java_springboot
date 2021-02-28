package example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Send {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setHost("localhost");
        try 
        {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
           
            for(int i=0;i<10000;i++)
            {
                String message = "Hello World! " + i;
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent '" + message + " " + i+ "'");
            }
            if (connection != null) {
                connection.close();
             }

        }
        catch (Exception e) {
         System.out.println("IOException in try block =>" + e.getMessage());
        }
    }
}