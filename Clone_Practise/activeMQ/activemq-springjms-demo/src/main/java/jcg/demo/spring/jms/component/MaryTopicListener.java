package jcg.demo.spring.jms.component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import jcg.demo.jms.util.DataUtil;

@Component
public class MaryTopicListener {

	@Autowired
	private JmsTemplate jmsTopicTemplate;

	private String topicName = DataUtil.TEST_GROUP1_TOPIC;

	public String receiveMessage() throws JMSException {
		System.out.println(Thread.currentThread().getName() + ": MaryTopicListener receiveMessage.");

		Destination destination = new ActiveMQTopic(topicName);
		TextMessage textMessage = (TextMessage) jmsTopicTemplate.receive(destination);

		return textMessage.getText();
	}
}
