package model;

import java.io.Serializable;

public class RentalProductOrder extends ProductOrder implements Serializable {
	private int unused = 0;
	private int returned = 0;
	private int notReturned = 0;

	public RentalProductOrder(Product product, Pricelist pricelist) {
		super(product, pricelist);
	}

	/**
	 * Returns true if all items are returned and accounted for
	 */
	public boolean isReturned() {
		return getAmount() == getReturned() + getUnused() + getNotReturned();
	}

	/**
	 * Get the total deposit price
	 */
	public double getDeposit() {
		return ((DepositProduct) getProduct()).getDeposit() * getAmount();
	}

	public int getUnused() {
		return unused;
	}

	public int getReturned() {
		return returned;
	}

	public int getNotReturned() {
		return notReturned;
	}

	public void setUnused(int unused) {
		assert unused >= 0;

		this.unused = unused;
	}

	public void setReturned(int returned) {
		assert returned >= 0;

		this.returned = returned;
	}

	public void setNotReturned(int notReturned) {
		assert notReturned >= 0;

		this.notReturned = notReturned;
	}

	/**
	 * Returns the price of the deposit after the return NOTE: The refund on
	 * unopened returned items is also calculated here
	 */
	public double getDepositAfterReturn() {
		double sum = getNotReturned() * ((DepositProduct) getProduct()).getDeposit();

		sum -= getUnused() * individualPrice();

		return sum;
	}
}
