package model;

import exceptions.DiscountParseException;
import exceptions.InvalidPaymentAmount;
import javafx.util.Pair;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tour implements Payable, Serializable {
	private final List<Payment> payments = new ArrayList<>();
	private int persons;
	private LocalDateTime date;
	private double price;
	private Duration duration;
	// private User user; //Bliver ikke brugt, da vi ikke nåede den del af
	// statistikken

	public Tour(int persons, LocalDateTime date, double price, Duration duration,
			User user) {
		assert persons > 0;
		assert date != null;
		assert price >= 0;
		assert duration != null;
		assert user != null;

		this.persons = persons;
		this.date = date;
		this.price = price;
		this.duration = duration;
		// this.user = user;
	}

	/**
	 * Calculates the current payment status
	 */
	@Override
	public PaymentStatus paymentStatus() throws DiscountParseException {
		if (totalPayment() >= price) {
			return PaymentStatus.ORDERPAID;
		}
		return PaymentStatus.UNPAID;
	}

	public int getPersons() {
		return persons;
	}

	public void setPersons(int persons) {
		assert persons > 0;

		this.persons = persons;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		assert date != null;

		this.date = date;
	}

	public double totalPrice() {
		return price;
	}

	public void setPrice(double price) {
		assert price >= 0;

		this.price = price;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		assert duration != null;

		this.duration = duration;
	}

	/**
	 * Calculate the total payment
	 */
	@Override
	public double totalPayment() {
		double sum = 0;
		for (Payment payment : payments) {
			sum += payment.getAmount();
		}
		return sum;
	}

	@Override
	public double getPrice() {
		return 0;
	}

	@Override
	public Pair<Integer, Double> totalClipCardPrice() {
		return null;
	}

	@Override
	public void pay(Payment payment) {
		assert payment != null;

		payments.add(payment);
		try {
			paymentStatus();
		} catch (InvalidPaymentAmount e) {
			payments.remove(payment);
		}
	}
}
