package jcg.demo.spring.jms.config;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import jcg.demo.spring.jms.component.JmsExceptionListener;

@Configuration
@EnableJms
@ComponentScan(basePackages = "jcg.demo.spring.jms.component, jcg.demo.spring.service")
public class JmsConfig {

	private String concurrency = "1-10";
	private String brokerURI = "tcp://localhost:61616";

	@Autowired
	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(JmsExceptionListener jmsExceptionListener) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(jmsConnectionFactory(jmsExceptionListener));
		factory.setDestinationResolver(destinationResolver());
		factory.setConcurrency(concurrency);
		factory.setPubSubDomain(false);
		return factory;
	}

	@Bean
	@Autowired
	public ConnectionFactory jmsConnectionFactory(JmsExceptionListener jmsExceptionListener) {
		return createJmsConnectionFactory(brokerURI, jmsExceptionListener);
	}

	private ConnectionFactory createJmsConnectionFactory(String brokerURI, JmsExceptionListener jmsExceptionListener) {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerURI);
		activeMQConnectionFactory.setExceptionListener(jmsExceptionListener);

		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(activeMQConnectionFactory);
		return connectionFactory;
	}

	@Bean(name = "jmsQueueTemplate")
	@Autowired
	public JmsTemplate createJmsQueueTemplate(ConnectionFactory jmsConnectionFactory) {
		return new JmsTemplate(jmsConnectionFactory);
	}

	@Bean(name = "jmsTopicTemplate")
	@Autowired
	public JmsTemplate createJmsTopicTemplate(ConnectionFactory jmsConnectionFactory) {
		JmsTemplate template = new JmsTemplate(jmsConnectionFactory);
		template.setPubSubDomain(true);
		return template;
	}

	@Bean
	public DestinationResolver destinationResolver() {
		return new DynamicDestinationResolver();
	}

}
