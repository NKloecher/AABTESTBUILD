package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Payment implements Serializable {
	private PaymentType paymentType;
	private double amount;
	private LocalDateTime date;

	public Payment(PaymentType paymentType, double amount) {
		assert paymentType != null;

		this.paymentType = paymentType;
		this.amount = amount;
		this.date = LocalDateTime.now();
	}

	public double getAmount() {
		return amount;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public LocalDateTime getDate() {
		return date;
	}

	@Override
	public String toString() {
		return date
				.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,
						FormatStyle.SHORT))
				+ " " + paymentType + " "
				+ String.format(Locale.GERMAN, "%.2fkr.", amount);
	}
}
