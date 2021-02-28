package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class SpringbootActivemqExampleApplication implements CommandLineRunner {

	@Autowired
	private Producer producer;

    public static void main(String[] args) {
		SpringApplication.run(SpringbootActivemqExampleApplication.class, args);
		

	}


    @Override
	public void run(String... args) throws Exception {
		String messageData = "{\"name\":\"anh rat tuyet voi\"}";
		System.out.println( "start send");
		producer.sendMessage("in_tuong.queue", messageData);
		System.out.println( "end send");
	}
	  

}