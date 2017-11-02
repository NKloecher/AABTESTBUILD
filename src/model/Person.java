package model;

import java.io.Serializable;

public class Person implements Serializable {
	protected String name;

	public Person(String name) {
		assert name != null && !name.isEmpty();

		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		assert name != null;

		this.name = name;
	}
}
