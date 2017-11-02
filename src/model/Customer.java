package model;

import java.io.Serializable;

public class Customer extends Person implements Comparable<Customer>, Serializable {
	private String address;
	private String phone;
	private String email;

	public Customer(String name, String address, String phone, String email) {
		super(name);

		this.address = address;
		this.phone = phone;
		this.email = email;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int compareTo(Customer o) {
		return name.compareToIgnoreCase(o.getName());
	}

}
