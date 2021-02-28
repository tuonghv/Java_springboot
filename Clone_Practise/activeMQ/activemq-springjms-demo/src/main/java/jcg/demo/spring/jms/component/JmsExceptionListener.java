package jcg.demo.spring.jms.component;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class JmsExceptionListener implements ExceptionListener {

	private static final Logger LOG = Logger.getLogger(JmsExceptionListener.class);

	@Override
	public void onException(JMSException e) {
		String errorMessage = "Exception while processing the JMS message";
		LOG.error(errorMessage, e);		
	}

}
