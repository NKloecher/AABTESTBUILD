package model;

import exceptions.DiscountParseException;

import java.io.Serializable;

public class Discount implements Serializable {
	private double discountAmount;
	private DiscountType discountType;

	public String getValue() {
		if (discountType == null) {
			return null;
		} else if (discountType == DiscountType.SUBTRACT) {
			return "-" + Double.toString(discountAmount);
		} else if (discountType == DiscountType.PERCENT) {
			return Double.toString(discountAmount) + "%";
		} else {
			return Double.toString(discountAmount);
		}
	}

	public void setDiscount(double discountAmount, DiscountType discountType) {
		assert discountAmount >= 0;
		assert discountType != null;

		this.discountType = discountType;
		this.discountAmount = discountAmount;
	}

	/**
	 * helper method using string format
	 * 
	 * @param discount
	 *            must be a number and can end with a procent sign
	 */
	public void setDiscount(String discount) throws DiscountParseException {
		assert discount != null && discount.matches("(-?\\d+)|(\\d+%?)");

		if (discount.isEmpty()) {
			setDiscount(0, null);
		} else if (discount.startsWith("-")) {
			setDiscount(Double.parseDouble(discount.substring(1, discount.length())),
					DiscountType.SUBTRACT);
		} else if (discount.endsWith("%")) {
			setDiscount(Double.parseDouble(discount.substring(0, discount.length() - 1))
					/ 100, DiscountType.PERCENT);
		} else {
			try {
				setDiscount(Double.parseDouble(discount), DiscountType.NEW);
			} catch (NumberFormatException ex) {
				throw new DiscountParseException("Ugyldig rabat " + discount);
			}
		}

	}

	/**
	 * Returns the price after applying the appropriate discount
	 */
	public double getPrice(double total) throws DiscountParseException {
		assert total >= 0;

		if (discountType == null) {
			return total;
		}

		if (discountType == DiscountType.SUBTRACT) {
			if (discountAmount > total) {
				throw new DiscountParseException(
						discountAmount + " rabat er h\u00F8jere end " + total);
			}
			return total - discountAmount;
		} else if (discountType == DiscountType.PERCENT) {
			if (discountAmount > 1) {
				throw new DiscountParseException(
						discountAmount * 100 + "% er mere end 100% rabat");
			}
			return total * (1 - discountAmount);
		} else {
			return discountAmount;
		}
	}
}
