package model;

import exceptions.DiscountParseException;

import java.io.Serializable;

public class ProductOrder implements Serializable {
	private Discount discount = null;
	private int amount = 1;
	private Product product;
	private double price;
	private boolean giftStatus = false; // Bruges til klassificering af
										// gaveobjekter

	public ProductOrder(Product product, Pricelist pricelist) {
		assert product != null;
		assert pricelist != null;

		this.product = product;
		this.price = pricelist.getPrice(product);
	}

	public void setGiftStatus() {
		this.giftStatus = true;
	}

	public boolean getGift() {
		return giftStatus;
	}

	/**
	 * Returns the price of an individual product when the discount is applied
	 */
	public double individualPrice() throws DiscountParseException {
		return price() / amount;
	}

	/**
	 * Returns the price of the product order with the discount applied
	 */
	public double price() throws DiscountParseException {
		if (getGift()) {
			return 0;
		}
		double p = price * amount;

		if (discount != null) {
			p = discount.getPrice(p);
		}

		return p;
	}

	public String getDiscount() {
		if (discount == null) {
			return "";
		}

		return discount.getValue();
	}

	public void setDiscount(String str) throws DiscountParseException {
		assert str != null && !str.isEmpty();

		if (discount == null) {
			discount = new Discount();
		}

		discount.setDiscount(str);
	}

	public Product getProduct() {
		return product;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		assert amount >= 0;

		this.amount = amount;
	}

	public double getOriginalPrice() {
		return price;
	}
}
