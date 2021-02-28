package jcg.demo.activemq.app;

import javax.jms.JMSException;

import jcg.demo.activemq.ActiveMQMessageConsumer;
import jcg.demo.jms.util.DataUtil;

public class ActiveMQMessageConsumerMainApp {

	public static void main(String[] args) {

		consumeCustomerVTCQueue();
		consumerVTCQueueWithSelector();
		consumeGroup1Topic();
		consumeAllGroup2();
		consume_queue_with_prefetchsize();

	}

	private static void consumeCustomerVTCQueue() {
		// the message in the topic before this subscriber starts will not be
		// picked up.
		ActiveMQMessageConsumer queueMsgListener = new ActiveMQMessageConsumer("tcp://localhost:61616", "admin",
				"admin");
		queueMsgListener.setDestinationName("Consumer.zheng." + DataUtil.CUSTOMER_VTC_TOPIC);

		try {
			queueMsgListener.run();
		} catch (JMSException e) {

			e.printStackTrace();
		}
	}

	private static void consumerVTCQueueWithSelector() {
		ActiveMQMessageConsumer queueMsgListener = new ActiveMQMessageConsumer("tcp://localhost:61616", "admin",
				"admin");
		queueMsgListener.setDestinationName("VTC.DZONE." + DataUtil.TEST_VTC_TOPIC_SELECTOR);
		queueMsgListener.setSelector("action='DZONE'");
		try {
			queueMsgListener.run();
		} catch (JMSException e) {

			e.printStackTrace();
		}
	}

	private static void consumeGroup1Topic() {
		ActiveMQMessageConsumer queueMsgListener = new ActiveMQMessageConsumer("tcp://localhost:61616", "admin",
				"admin");
		queueMsgListener.setDestinationName(DataUtil.TEST_GROUP1_TOPIC);
		queueMsgListener.setDestinationTopic(true);

		try {
			queueMsgListener.run();
		} catch (JMSException e) {

			e.printStackTrace();
		}
	}

	private static void consumeAllGroup2() {
		ActiveMQMessageConsumer queueMsgListener = new ActiveMQMessageConsumer("tcp://localhost:61616", "admin",
				"admin");
		queueMsgListener.setDestinationName("*.group2.*");

		try {
			queueMsgListener.run();
		} catch (JMSException e) {

			e.printStackTrace();
		}
	}

	private static void exclusive_queue_Consumer() {
		ActiveMQMessageConsumer queueMsgListener = new ActiveMQMessageConsumer("tcp://localhost:61616", "admin",
				"admin");
		queueMsgListener.setDestinationName(DataUtil.TEST_GROUP2_QUEUE_2 + "?consumer.exclusive=true");

		try {
			queueMsgListener.run();
		} catch (JMSException e) {

			e.printStackTrace();
		}
	}

	private static void consume_queue_with_prefetchsize() {
		ActiveMQMessageConsumer queueMsgListener = new ActiveMQMessageConsumer("tcp://localhost:61616", "admin",
				"admin");
		queueMsgListener.setDestinationName(DataUtil.TEST_GROUP1_QUEUE_2 + "?consumer.prefetchSize=10");

		try {
			queueMsgListener.run();
		} catch (JMSException e) {

			e.printStackTrace();
		}
	}

}
