package jcg.demo.spring.jms.component;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {

	@Autowired
	private JmsTemplate jmsQueueTemplate;

	@Autowired
	private JmsTemplate jmsTopicTemplate;

	public void postToQueue(final String queueName, final String message) {

		MessageCreator messageCreator = new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		};

		jmsQueueTemplate.send(queueName, messageCreator);
	
	}

	public void postToQueue(final String queueName, Map<String, String> headers, final String message) {

		jmsQueueTemplate.send(queueName, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				Message msg = session.createTextMessage(message);
				headers.forEach((k, v) -> {
					try {
						msg.setStringProperty(k, v);
					} catch (JMSException e) {
						System.out.println(
								String.format("JMS fails to set the Header value '%s' to property '%s'", v, k));
					}
				});
				return msg;
			}
		});
	}

	public void postToTopic(final String topicName, Map<String, String> headers, final String message) {

		jmsTopicTemplate.send(topicName, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				Message msg = session.createTextMessage(message);
				headers.forEach((k, v) -> {
					try {
						msg.setStringProperty(k, v);
					} catch (JMSException e) {
						System.out.println(
								String.format("JMS fails to set the Header value '%s' to property '%s'", v, k));
					}
				});
				return msg;
			}
		});
	}

}
