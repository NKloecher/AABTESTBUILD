package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Pricelist implements Serializable {
	private String name;
	private final Map<Product, Double> products = new HashMap<>();

	public Pricelist(String name) {
		assert name != null && !name.isEmpty();

		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns the price of a product
	 */
	public double getPrice(Product key) {
		return products.get(key);
	}

	public void setPrice(Product product, double price) {
		assert product != null;
		assert price >= 0;

		products.put(product, price);
	}

	public ArrayList<Product> getProducts() {
		return new ArrayList<>(products.keySet());
	}

	public void removeProduct(Product product) {
		assert products.containsKey(product);

		products.remove(product);
	}

	@Override
	public String toString() {
		return name;
	}
}
