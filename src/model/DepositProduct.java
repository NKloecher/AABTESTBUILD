package model;

import java.io.Serializable;

public class DepositProduct extends Product implements Serializable {
	private double deposit;

	public DepositProduct(String name, Integer clips, String category, String image,
			double deposit) {
		super(name, clips, category, image);

		assert deposit >= 0;

		this.deposit = deposit;
	}

	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}
}
