package jcg.demo.model;

public class CustomerEvent {
	private String type;
	private Integer customerId;

	public CustomerEvent(String type, Integer customerId) {
		this.type = type;
		this.customerId = customerId;
	}

	public String getType() {
		return type;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public String toString() {
		return "CustomerEvent: type(" + type + "), customerId(" + customerId + ")";
	}

	public String getCustomerDetailUri() {
		return "https://localhost:8080/support/customer/" + customerId;
	}
}
