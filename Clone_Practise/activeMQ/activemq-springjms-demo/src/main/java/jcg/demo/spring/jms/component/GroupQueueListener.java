package jcg.demo.spring.jms.component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import jcg.demo.jms.util.DataUtil;

@Component
public class GroupQueueListener {
	@Autowired
	private JmsTemplate jmsQueueTemplate;

	private String queueName = DataUtil.TEST_GROUP1_QUEUE_1;

	public String receiveMessage() throws JMSException {
		System.out.println(Thread.currentThread().getName() + ": GroupQueueListener receiveMessage." );
		
		Destination destination = new ActiveMQQueue(queueName);
		TextMessage textMessage = (TextMessage) jmsQueueTemplate.receive(destination);
		return textMessage.getText();
	}
}
