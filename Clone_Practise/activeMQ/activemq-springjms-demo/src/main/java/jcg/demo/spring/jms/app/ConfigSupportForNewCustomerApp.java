package jcg.demo.spring.jms.app;

import java.net.URISyntaxException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;

import jcg.demo.spring.jms.component.SupportAppListener;
import jcg.demo.spring.jms.config.JmsConfig;

@Configuration
public class ConfigSupportForNewCustomerApp {
	public static void main(String[] args) throws URISyntaxException, Exception {
		Gson gson = new Gson();

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JmsConfig.class);
		context.register(ConfigSupportForNewCustomerApp.class);

		try {
			SupportAppListener supportAppListener = (SupportAppListener) context.getBean("supportAppListener");
			System.out.println("ConfigSupportForNewCustomerApp receives " + supportAppListener.receiveMessage());

		} finally {
			context.close();
		}
	}

}
