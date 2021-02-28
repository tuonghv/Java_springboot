package jcg.demo.spring.jms.component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import jcg.demo.jms.util.DataUtil;
import jcg.demo.model.CustomerEvent;
import jcg.demo.spring.service.MessageTransformer;
import jcg.demo.spring.service.SupportService;

@Component
public class SupportAppListener {

	@Autowired
	private JmsTemplate jmsQueueTemplate;
	
	@Autowired
	private SupportService supportService;
	
	@Autowired
	private MessageTransformer msgTransformer;
	
	private String queueName = "Consumer.Support." + DataUtil.CUSTOMER_VTC_TOPIC;

	public String receiveMessage() throws JMSException {
		System.out.println(Thread.currentThread().getName() + ": SupportAppListener receiveMessage." );

		Destination destination = new ActiveMQQueue(queueName);
		TextMessage textMessage = (TextMessage) jmsQueueTemplate.receive(destination);
		
		CustomerEvent customerEvt = msgTransformer.fromJson(textMessage.getText(), CustomerEvent.class);
		return supportService.handleNewCustomer(customerEvt);
	}
}
