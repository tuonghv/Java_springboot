package jcg.demo.spring.service;

import org.springframework.stereotype.Service;

import jcg.demo.model.CustomerEvent;

@Service
public class BillingService {
	public String handleNewCustomer(CustomerEvent customerEvt){
		String message = "BillingService handleNewCustomer " + customerEvt.toString();
		return message;
	}
}
