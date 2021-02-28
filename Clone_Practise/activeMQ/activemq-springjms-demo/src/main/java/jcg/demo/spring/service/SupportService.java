package jcg.demo.spring.service;

import org.springframework.stereotype.Service;

import jcg.demo.model.CustomerEvent;

@Service
public class SupportService {

	public String handleNewCustomer(CustomerEvent customerEvt) {
		String message = "SupportService handleNewCustomer " + customerEvt.toString();
		return message;
	}

}
