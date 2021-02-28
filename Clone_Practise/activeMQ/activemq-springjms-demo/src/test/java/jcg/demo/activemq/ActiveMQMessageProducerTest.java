package jcg.demo.activemq;

import javax.jms.JMSException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jcg.demo.jms.util.DataUtil;

public class ActiveMQMessageProducerTest {

	private ActiveMQMessageProducer msgQueueSender;

	@Before
	public void setup() {
		msgQueueSender = new ActiveMQMessageProducer("tcp://localhost:61616", "admin", "admin");
	}

	@After
	public void cleanup() throws JMSException {
		msgQueueSender.close();
	}

	@Test
	public void send_msg_to_no_transaction_Queue() throws JMSException {
		msgQueueSender.setup(false, false, DataUtil.TEST_GROUP1_QUEUE_1);
		msgQueueSender.sendMessage("JCG");
	}

	@Test
	public void send_msg_to_Group2_Queue1() throws JMSException {
		msgQueueSender.setup(false, false, DataUtil.TEST_GROUP2_QUEUE_1);
		msgQueueSender.sendMessage("JCG");
	}

	@Test
	public void send_msg_to_transaction_Group1_Queue2() throws JMSException {
		msgQueueSender.setup(true, false, DataUtil.TEST_GROUP1_QUEUE_2);
		msgQueueSender.sendMessage("DEMO");
		msgQueueSender.commit(true);
	}

	@Test
	public void send_msg_to_no_transaction_Group1_Topic() throws JMSException {
		msgQueueSender.setup(false, true, DataUtil.TEST_GROUP1_TOPIC);
		msgQueueSender.sendMessage("MZHENG");
	}

	@Test
	public void send_msg_to_Virtual_Topic() throws JMSException {
		msgQueueSender.setup(false, true, DataUtil.CUSTOMER_VTC_TOPIC);
		msgQueueSender.sendMessage("MZHENG");
	}

	@Test
	public void send_msg_to_Virtual_Topic_WithSelector() throws JMSException {
		msgQueueSender.setup(false, true, DataUtil.TEST_VTC_TOPIC_SELECTOR);
		msgQueueSender.sendMessage("DZONE");
	}

}
