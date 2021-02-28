package jcg.demo.jms.util;

import java.util.Random;

import jcg.demo.model.CustomerEvent;

public final class DataUtil {
	public static final String TEST_GROUP1_TOPIC = "test.group1.topic";
	
	public static final String TEST_GROUP1_QUEUE_1 = "test.group1.queue1";
	public static final String TEST_GROUP1_QUEUE_2 = "test.group1.queue2";
	
	public static final String TEST_GROUP2_QUEUE_1 = "test.group2.queue1";
	public static final String TEST_GROUP2_QUEUE_2 = "test.group2.queue2";
	
	public static final String TEST_VTC_TOPIC_SELECTOR = "JCG.Mary.Topic";

	public static final String CUSTOMER_VTC_TOPIC = "VirtualTopic.Customer.Topic";
	
	public static CustomerEvent buildDummyCustomerEvent() {
		Random rand = new Random();
		int value = rand.nextInt(100);
		CustomerEvent event = new CustomerEvent("NEWCUSTOMER", value);

		return event;
	}

}
