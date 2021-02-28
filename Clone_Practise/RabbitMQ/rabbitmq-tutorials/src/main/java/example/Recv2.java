package example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Recv2 {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv)  {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setHost("localhost");

        try 
        {

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            


            // Consumer consumer = new DefaultConsumer(channel) {
            //     @Override
            //     public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
            //             byte[] body) throws IOException {

            //                 String message = new String(body, StandardCharsets.UTF_8);
            //                 System.out.println("messsage>"+ message);
                   
            //     }
            // };
            // channel.basicConsume(QUEUE_NAME, true, consumer);



            boolean autoAck = false;
            channel.basicConsume(QUEUE_NAME, autoAck, "myConsumerTag",
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                                Envelope envelope,
                                                AMQP.BasicProperties properties,
                                                byte[] body)
                        throws IOException
                    {
                        // String routingKey = envelope.getRoutingKey();
                        // String contentType = properties.contentType;
                        // long deliveryTag = envelope.getDeliveryTag();
                        // (process the message components here ...)

                            String message = new String(body, StandardCharsets.UTF_8);
                            System.out.println("messsage>"+ message);


                        // channel.basicAck(deliveryTag, false);
                    }
                    });


        }
        catch (Exception e) {
        System.out.println("IOException in try block =>" + e.getMessage());
        }



    }

}