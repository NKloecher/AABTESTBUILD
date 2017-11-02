package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import exceptions.InvalidPaymentAmount;
import model.DepositProduct;
import model.Order;
import model.Payment;
import model.PaymentType;
import model.Permission;
import model.Pricelist;
import model.Product;
import model.ProductOrder;
import model.RentalProductOrder;
import model.User;

public class OrderTest {
	final User user;
	final Pricelist pricelist;
	final Product simpleProduct;
	final DepositProduct simpleDepositProduct;
	final Product simpleBeer;
	final Product simpleSoda;
	final Product simpleGift;

	public OrderTest() {
		user = new User("test", "test", "test", Permission.NORMAL);
		pricelist = new Pricelist("test");

		simpleProduct = new Product("Simple Product", null, "test", null);
		pricelist.setPrice(simpleProduct, 100);

		simpleDepositProduct = new DepositProduct("Simple Deposit Product", null, "test",
				null, 10);
		pricelist.setPrice(simpleDepositProduct, 100);

		simpleBeer = new Product("Simple Beer", 2, "test", null);
		pricelist.setPrice(simpleBeer, 50);

		simpleSoda = new Product("Simple Soda", 1, "fadoel", null);
		pricelist.setPrice(simpleSoda, 15);

		simpleGift = new Product("Simple Gift", null, "sampakninger", null);
		pricelist.setPrice(simpleGift, 100);

	}

	@Test
	public void orderHasRentalOrderTS1None() throws Exception {
		Order order = new Order(user, pricelist);

		assertEquals(false, order.hasRentalOrder());
	}

	@Test
	public void orderHasRentalOrderTS2DepositProduct() throws Exception {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleDepositProduct);

		assertEquals(true, order.hasRentalOrder());
	}

	@Test
	public void orderHasRentalOrderTS3Product() throws Exception {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleProduct);

		assertEquals(false, order.hasRentalOrder());
	}

	@Test
	public void orderTotalPriceTS2OneProductOrder() throws Exception {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleProduct);

		assertEquals(100, order.totalPrice(), 0.01);
	}

	@Test
	public void orderTotalPriceTS3OneRentalProductOrder() throws Exception {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleDepositProduct);

		assertEquals(100, order.totalPrice(), 0.01);
	}

	@Test
	public void orderTotalPriceTS4OneRentalProductOrderOneProductOrder()
			throws Exception {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleDepositProduct);
		order.addProduct(simpleProduct);

		assertEquals(200, order.totalPrice(), 0.01);
	}

	@Test
	public void orderTotalPriceTS4OneRentalProductOrderOneProductOrderDiscount()
			throws Exception {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleDepositProduct);
		order.addProduct(simpleProduct);
		order.setDiscount("40%");

		assertEquals(120, order.totalPrice(), 0.01);
	}

	@Test
	public void orderTotalPriceGiftbag() {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleGift);
		ProductOrder po = order.addProduct(simpleProduct);
		po.setGiftStatus();

		assertEquals(100, order.totalPrice(), 0.01);
	}

	@Test
	public void orderAllRentalsReturnedTS1NoRentals() throws Exception {
		Order order = new Order(user, pricelist);
		assertEquals(true, order.allRentalsReturned());
	}

	@Test
	public void orderAllRentalsReturnedTS2OneRentalsReturned() throws Exception {
		Order order = new Order(user, pricelist);
		RentalProductOrder rentalProductOrder = (RentalProductOrder) order
				.addProduct(simpleDepositProduct);
		rentalProductOrder.setReturned(1);

		assertEquals(true, order.allRentalsReturned());
	}

	@Test
	public void orderAllRentalsReturnedTS3OneRentalsNotReturned() throws Exception {
		Order order = new Order(user, pricelist);
		RentalProductOrder rentalProductOrder = (RentalProductOrder) order
				.addProduct(simpleDepositProduct);
		rentalProductOrder.setReturned(0);

		assertEquals(false, order.allRentalsReturned());
	}

	@Test
	public void orderAllRentalsReturnedTS4TwoRentalsOneReturned() throws Exception {
		Order order = new Order(user, pricelist);
		RentalProductOrder rentalProductOrder1 = (RentalProductOrder) order
				.addProduct(simpleDepositProduct);
		RentalProductOrder rentalProductOrder2 = (RentalProductOrder) order
				.addProduct(simpleDepositProduct);
		rentalProductOrder1.setReturned(1);
		rentalProductOrder2.setReturned(0);

		assertEquals(false, order.allRentalsReturned());
	}

	@Test
	public void orderTotalDepositTS1NoProducts() throws Exception {
		Order order = new Order(user, pricelist);

		assertEquals(null, order.totalDeposit());
	}

	@Test
	public void orderTotalDepositTS2OneProductsOrder() throws Exception {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleProduct);

		assertEquals(null, order.totalDeposit());
	}

	@Test
	public void orderTotalDepositTS3OneDepositProductsOrder() throws Exception {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleDepositProduct);

		assertEquals(10, order.totalDeposit(), 0.01);
	}

	@Test
	public void orderTotalDepositTS4TwoDepositProductsOrder() throws Exception {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleDepositProduct);
		DepositProduct dp = new DepositProduct("Test desposit", null, "test", null, 20);
		pricelist.setPrice(dp, 100);
		order.addProduct(dp);

		assertEquals(30, order.totalDeposit(), 0.01);
	}

	@Test
	public void orderTotalDepositAfterReturn() {
		Order order = new Order(user, pricelist);
		RentalProductOrder rp = (RentalProductOrder) order
				.addProduct(simpleDepositProduct);
		rp.setAmount(5);
		rp.setUnused(1);
		assertEquals(-100, order.totalDepositAfterReturn(), 0.01);
	}

	@Test
	public void orderTotalDepositAfterReturn2() {
		Order order = new Order(user, pricelist);
		RentalProductOrder rp = (RentalProductOrder) order
				.addProduct(simpleDepositProduct);
		RentalProductOrder rp2 = (RentalProductOrder) order
				.addProduct(simpleDepositProduct);
		rp.setAmount(5);
		rp.setNotReturned(1);
		rp.setUnused(1);
		rp2.setAmount(4);
		rp2.setUnused(4);
		assertEquals(-490, order.totalDepositAfterReturn(), 0.01);
	}

	@Test
	public void orderTotalPayment50Cash() {
		Order order = new Order(user, pricelist);
		ProductOrder po = order.addProduct(simpleBeer);
		po.setAmount(2);

		Payment payment = new Payment(PaymentType.CASH, 50);
		order.pay(payment);

		assertEquals(50, order.totalPayment(), 0.01);
	}

	@Test
	public void orderTotalPayment50Card() {
		Order order = new Order(user, pricelist);
		ProductOrder po = order.addProduct(simpleBeer);
		po.setAmount(2);

		Payment payment = new Payment(PaymentType.CREDIT_CARD, 50);
		order.pay(payment);

		assertEquals(50, order.totalPayment(), 0.01);
	}

	@Test
	public void orderTotalPayment50Mobile() {
		Order order = new Order(user, pricelist);
		ProductOrder po = order.addProduct(simpleBeer);
		po.setAmount(2);

		Payment payment = new Payment(PaymentType.MOBILE_PAY, 50);
		order.pay(payment);

		assertEquals(50, order.totalPayment(), 0.01);
	}

	@Test
	public void orderTotalPayment2ClipCard() {
		Order order = new Order(user, pricelist);
		ProductOrder po = order.addProduct(simpleBeer);
		po.setAmount(2);

		Payment payment = new Payment(PaymentType.CLIP_CARD, 2);
		order.pay(payment);

		assertEquals(50, order.totalPayment(), 0.01);
	}

	@Test
	public void orderTotalPayment4CipCard() {
		Order order = new Order(user, pricelist);
		ProductOrder po = order.addProduct(simpleBeer);
		po.setAmount(2);

		Payment payment = new Payment(PaymentType.CLIP_CARD, 4);
		order.pay(payment);

		assertEquals(100, order.totalPayment(), 0.01);
	}

	@Test
	public void orderTotalPayment50Cash50Card() {
		Order order = new Order(user, pricelist);
		ProductOrder po = order.addProduct(simpleBeer);
		po.setAmount(2);

		Payment payment = new Payment(PaymentType.CASH, 50);
		Payment payment2 = new Payment(PaymentType.CREDIT_CARD, 50);
		order.pay(payment);
		order.pay(payment2);

		assertEquals(100, order.totalPayment(), 0.01);
	}

	@Test
	public void orderTotalPayment50Cash2ClipCard() {
		Order order = new Order(user, pricelist);
		ProductOrder po = order.addProduct(simpleBeer);
		po.setAmount(2);

		Payment payment = new Payment(PaymentType.CASH, 50);
		Payment payment2 = new Payment(PaymentType.CLIP_CARD, 2);
		order.pay(payment);
		order.pay(payment2);

		assertEquals(100, order.totalPayment(), 0.01);
	}

	@Test
	public void orderTotalPayment1Clip() {
		Order order = new Order(user, pricelist);
		ProductOrder po = order.addProduct(simpleBeer);
		po.setAmount(2);

		Payment payment = new Payment(PaymentType.CLIP_CARD, 1);
		order.pay(payment);

		assertEquals(25, order.totalPayment(), 0.01);
	}

	@Test(expected = InvalidPaymentAmount.class)
	public void orderTotalPayment150Cash() {
		Order order = new Order(user, pricelist);
		ProductOrder po = order.addProduct(simpleBeer);
		po.setAmount(2);

		Payment payment = new Payment(PaymentType.CASH, 150);
		order.pay(payment);
	}

	@Test(expected = InvalidPaymentAmount.class)
	public void orderTotalPayment5Clip() {
		Order order = new Order(user, pricelist);
		ProductOrder po = order.addProduct(simpleBeer);
		po.setAmount(2);

		Payment payment = new Payment(PaymentType.CLIP_CARD, 5);
		order.pay(payment);

		assertEquals(100, order.totalPayment(), 0.01);
	}

	@Test
	public void totalPaymentClipCard1Beer2Clip() {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleBeer);

		Payment payment = new Payment(PaymentType.CLIP_CARD, 2);
		order.pay(payment);

		assertEquals(50, order.totalPaymentClipCard(), 0.01);
	}

	@Test
	public void totalPaymentClipCard1Beer1Clip1() {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleBeer);

		Payment payment = new Payment(PaymentType.CLIP_CARD, 1);
		order.pay(payment);

		assertEquals(25, order.totalPaymentClipCard(), 0.01);
	}

	@Test
	public void totalPaymentClipCard1Soda1Clip() {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleSoda);

		Payment payment = new Payment(PaymentType.CLIP_CARD, 1);
		order.pay(payment);

		assertEquals(15, order.totalPaymentClipCard(), 0.01);
	}

	@Test
	public void totalPaymentClipCard1Beer1Soda2Clips() {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleBeer);
		order.addProduct(simpleSoda);

		Payment payment = new Payment(PaymentType.CLIP_CARD, 2);
		order.pay(payment);

		assertEquals(50, order.totalPaymentClipCard(), 0.01);
	}

	@Test
	public void totalPaymentClipCard1Beer1Soda2ClipsOrdering() {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleSoda);
		order.addProduct(simpleBeer);

		Payment payment = new Payment(PaymentType.CLIP_CARD, 2);
		order.pay(payment);

		assertEquals(50, order.totalPaymentClipCard(), 0.01);
	}

	@Test
	public void totalPaymentClipCard1Beer1Soda1Clips() {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleBeer);
		order.addProduct(simpleSoda);

		Payment payment = new Payment(PaymentType.CLIP_CARD, 1);
		order.pay(payment);

		assertEquals(25, order.totalPaymentClipCard(), 0.01);
	}

	@Test(expected = InvalidPaymentAmount.class)
	public void totalPaymentClipCard1Beer3Clips() {
		Order order = new Order(user, pricelist);
		order.addProduct(simpleBeer);

		Payment payment = new Payment(PaymentType.CLIP_CARD, 3);
		order.pay(payment);
		assertEquals(-1, order.totalPaymentClipCard(), 0.01);

	}

}