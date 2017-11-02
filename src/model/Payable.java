package model;

import exceptions.DiscountParseException;
import exceptions.InvalidPaymentAmount;
import javafx.util.Pair;

public interface Payable {

	/**
	 * Adds a payment
	 */
	void pay(Payment payment);

	/**
	 * Returns the PaymentStatus of a the object on which it is called
	 */
	PaymentStatus paymentStatus() throws DiscountParseException, InvalidPaymentAmount;

	/**
	 * Sums up all payments the method caller has made
	 * 
	 * @see #pay(Payment)
	 */
	double totalPayment();

	/**
	 * Returns the total price for the payable object
	 */
	double getPrice();

	/**
	 * Returns the total price for the payable object, if paid fully with clip
	 * card
	 */
	Pair<Integer, Double> totalClipCardPrice();
}
