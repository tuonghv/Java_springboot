package jcg.demo.spring.jms.app;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;

import jcg.demo.jms.util.DataUtil;
import jcg.demo.spring.jms.component.GroupQueueListener;
import jcg.demo.spring.jms.component.MessageSender;
import jcg.demo.spring.jms.config.JmsConfig;

@Configuration
public class SpringJmsApp {
	public static void main(String[] args) throws URISyntaxException, Exception {
		Gson gson = new Gson();

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JmsConfig.class);
		context.register(SpringJmsApp.class);

		try {
			MessageSender springJmsProducer = (MessageSender) context.getBean("messageSender");
			GroupQueueListener group1Queue1Listener = (GroupQueueListener) context.getBean("groupQueueListener");

			Map<String, String> headers = new HashMap<>();
			headers.put("action", "NewCustomer");
			headers.put("JMSXGroupID", "group1");

			springJmsProducer.postToQueue(DataUtil.TEST_GROUP1_QUEUE_1, headers,
					gson.toJson(DataUtil.buildDummyCustomerEvent()));

			System.out.println("group1Queue1Listener receives " + group1Queue1Listener.receiveMessage());

		} finally {
			context.close();
		}
	}

}
