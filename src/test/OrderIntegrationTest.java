package test;

import model.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderIntegrationTest {
	final Pricelist pl;
	final Product product100kr4clip;
	final Product product100kr3clip;
	final Product product50kr0clip;
	final DepositProduct depositProduct500kr100rent;
	final User user;

	public OrderIntegrationTest() {
		user = new User("test", "test", "test", Permission.NORMAL);
		pl = new Pricelist("Pricelist");
		product100kr4clip = new Product("100kr, 4clip", 4, "test", null);
		pl.setPrice(product100kr4clip, 100);
		product100kr3clip = new Product("100kr, 3clip", 3, "test", null);
		pl.setPrice(product100kr3clip, 100);
		product50kr0clip = new Product("50kr, 0clip", null, "test", null);
		pl.setPrice(product50kr0clip, 50);
		depositProduct500kr100rent = new DepositProduct("500kr, 100rent", null, "test",
				null, 100);
		pl.setPrice(depositProduct500kr100rent, 500);
	}

	@Test
	public void orderTotalPriceOneProduct() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(product100kr4clip);
		assertEquals(100, order.totalPrice(), 0.1);
	}

	@Test
	public void orderTotalPriceTwoProduct() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(product100kr4clip);
		order.createProductOrder(product100kr3clip);
		assertEquals(200, order.totalPrice(), 0.1);
	}

	@Test
	public void orderTotalPriceOneProductTwoItems() throws Exception {
		Order order = new Order(user, pl);
		ProductOrder productOrder = order.createProductOrder(product100kr4clip);
		productOrder.setAmount(2);
		assertEquals(200, order.totalPrice(), 0.1);
	}

	@Test
	public void orderTotalPriceOneDepositProduct() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(depositProduct500kr100rent);
		assertEquals(500, order.totalPrice(), 0.1);
	}

	@Test
	public void orderTotalDepositOneProduct() {
		Order order = new Order(user, pl);
		order.createRentalProductOrder(depositProduct500kr100rent);
		assertEquals(100, order.totalDeposit(), 0.1);
	}

	@Test
	public void orderTotalDepositOneProductTwoItems() {
		Order order = new Order(user, pl);
		RentalProductOrder rentalProductOrder = order
				.createRentalProductOrder(depositProduct500kr100rent);
		rentalProductOrder.setAmount(2);
		assertEquals(200, order.totalDeposit(), 0.1);
	}

	@Test
	public void orderTotalPriceOneProductDiscount50pct() throws Exception {
		Order order = new Order(user, pl);
		ProductOrder productOrder = order.createProductOrder(product100kr4clip);
		productOrder.setDiscount("50%");
		assertEquals(50, order.totalPrice(), 0.1);
	}

	@Test
	public void orderTotalPriceOneProductDiscount10kr() throws Exception {
		Order order = new Order(user, pl);
		ProductOrder productOrder = order.createProductOrder(product100kr4clip);
		productOrder.setDiscount("10");
		assertEquals(10, order.totalPrice(), 0.1);
	}

	@Test
	public void orderTotalPriceOneProductDiscountMinus10kr() throws Exception {
		Order order = new Order(user, pl);
		ProductOrder productOrder = order.createProductOrder(product100kr4clip);
		productOrder.setDiscount("-10");
		assertEquals(90, order.totalPrice(), 0.1);
	}

	@Test
	public void orderTotalPriceOneProductOrderDiscount() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(product100kr4clip);
		order.setDiscount("50%");
		assertEquals(50, order.totalPrice(), 0.1);
	}

	@Test
	public void orderTotalPriceTwoProductOrderDiscount() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(product100kr4clip);
		order.createProductOrder(product100kr3clip);
		order.setDiscount("50%");
		assertEquals(100, order.totalPrice(), 0.1);
	}

	@Test
	public void orderTotalPriceOneProductTwoItemsOrderDiscount() throws Exception {
		Order order = new Order(user, pl);
		ProductOrder productOrder = order.createProductOrder(product100kr4clip);
		productOrder.setAmount(2);
		order.setDiscount("50%");
		assertEquals(100, order.totalPrice(), 0.1);
	}

	@Test
	public void orderTotalPriceOneDepositProductOrderDiscount() throws Exception {
		Order order = new Order(user, pl);
		order.createRentalProductOrder(depositProduct500kr100rent);
		order.setDiscount("50%");
		assertEquals(250, order.totalPrice(), 0.1);
	}

	@Test
	public void orderTotalPriceOneDepositProductOrderDiscountDontEffectDeposit()
			throws Exception {
		Order order = new Order(user, pl);
		order.createRentalProductOrder(depositProduct500kr100rent);
		order.setDiscount("50%");
		assertEquals(100, order.totalDeposit(), 0.1);
	}

	@Test
	public void orderPaymentStatusNoProduct() throws Exception {
		Order order = new Order(user, pl);
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.UNPAID);
	}

	@Test
	public void orderPaymentStatusOneProductUnpaid() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(product100kr4clip);
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.UNPAID);
	}

	@Test
	public void orderPaymentStatusOneProductOneCorrectPayment() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(product100kr4clip);
		order.pay(new Payment(PaymentType.CASH, 100));
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.ORDERPAID);
	}

	@Test
	public void orderPaymentStatusOneProductManyCorrectPayment() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(product100kr4clip);
		for (int i = 0; i < 100; i++) {
			order.pay(new Payment(PaymentType.CASH, 1));
		}
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.ORDERPAID);
	}

	@Test
	public void orderPaymentStatusTwoProductOneCorrectPayment() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(product100kr4clip);
		order.createProductOrder(product50kr0clip);
		order.pay(new Payment(PaymentType.CASH, 150));
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.ORDERPAID);
	}

	@Test
	public void orderPaymentStatusOneProductTwoCorrectPayment() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(product100kr4clip);
		order.pay(new Payment(PaymentType.CASH, 50));
		order.pay(new Payment(PaymentType.MOBILE_PAY, 50));
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.ORDERPAID);
	}

	@Test
	public void orderPaymentStatusOneDepositProductUnpaid() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(depositProduct500kr100rent);
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.UNPAID);
	}

	@Test
	public void orderPaymentStatusOneDepositProductDepositPayed() throws Exception {
		Order order = new Order(user, pl);
		order.createRentalProductOrder(depositProduct500kr100rent);
		order.pay(new Payment(PaymentType.CASH, 100));
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.DEPOSITPAID);
	}

	@Test
	public void orderPaymentStatusOneDepositProductReturnedOrderPayed() throws Exception {
		Order order = new Order(user, pl);
		RentalProductOrder productOrder = order
				.createRentalProductOrder(depositProduct500kr100rent);
		productOrder.setReturned(1);
		order.pay(new Payment(PaymentType.CASH, 500));

		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.ORDERPAID);
	}

	@Test
	public void orderPaymentStatusOneDepositProductNotReturnedOrderPayed()
			throws Exception {
		Order order = new Order(user, pl);
		RentalProductOrder productOrder = order
				.createRentalProductOrder(depositProduct500kr100rent);
		productOrder.setNotReturned(1);
		order.pay(new Payment(PaymentType.CASH, 600));

		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.ORDERPAID);
	}

	@Test
	public void orderPaymentStatusOneDepositProductUnopenedOrderPayed() throws Exception {
		Order order = new Order(user, pl);
		RentalProductOrder productOrder = order
				.createRentalProductOrder(depositProduct500kr100rent);
		productOrder.setUnused(1);
		order.pay(new Payment(PaymentType.CASH, 0));

		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.ORDERPAID);
	}

	@Test
	public void orderPaymentStatusThreeDepositProductOneOfEachPayed() throws Exception {
		Order order = new Order(user, pl);
		RentalProductOrder productOrder = order
				.createRentalProductOrder(depositProduct500kr100rent);
		productOrder.setAmount(3);
		productOrder.setUnused(1);
		productOrder.setReturned(1);
		productOrder.setNotReturned(1);
		order.pay(new Payment(PaymentType.CASH, 1100));

		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.ORDERPAID);
	}

	@Test
	public void orderPaymentStatusOneDepositProductDepositNotPayedBackPayed()
			throws Exception {
		Order order = new Order(user, pl);
		RentalProductOrder productOrder = order
				.createRentalProductOrder(depositProduct500kr100rent);
		order.pay(new Payment(PaymentType.CASH, 600));
		productOrder.setReturned(1);
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.DEPOSITNOTPAIDBACK);
	}

	@Test
	public void orderPaymentClipCardOneProductPayed() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(product100kr4clip);
		order.pay(new Payment(PaymentType.CLIP_CARD, 4));

		assertEquals(order.totalPayment(), 100, 0.01);
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.ORDERPAID);
	}

	@Test
	public void orderPaymentClipCardTwoProductPayed() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(product100kr4clip);
		order.createProductOrder(product100kr3clip);
		order.pay(new Payment(PaymentType.CLIP_CARD, 7));

		assertEquals(order.totalPayment(), 200, 0.01);
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.ORDERPAID);
	}

	@Test
	public void orderPaymentClipCardTwoProductOnePayed() throws Exception {
		Order order = new Order(user, pl);
		order.createProductOrder(product100kr4clip);
		order.createProductOrder(product100kr3clip);
		order.pay(new Payment(PaymentType.CLIP_CARD, 3));

		assertEquals(order.totalPayment(), 100, 0.01);
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.UNPAID);
	}

	@Test
	public void orderPaymentClipCardOneProductTwoItemsPayed() throws Exception {
		Order order = new Order(user, pl);
		ProductOrder productOrder = order.createProductOrder(product100kr4clip);
		productOrder.setAmount(2);
		order.pay(new Payment(PaymentType.CLIP_CARD, 8));

		assertEquals(order.totalPayment(), 200, 0.01);
		PaymentStatus paymentStatus = order.paymentStatus();
		assertEquals(paymentStatus, PaymentStatus.ORDERPAID);
	}

}