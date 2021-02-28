package jcg.demo.activemq.app;

import jcg.demo.activemq.ActiveMQMessageProducer;
import jcg.demo.jms.util.DataUtil;

public class OnBoardNewCustomerApp {
	public static void main(String[] args) {
		ActiveMQMessageProducer msgQueueSender = new ActiveMQMessageProducer("tcp://localhost:61616", "admin", "admin");
		try {
			msgQueueSender.setup(false, true, DataUtil.CUSTOMER_VTC_TOPIC);
			msgQueueSender.sendMessage("CUSTOMER");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
