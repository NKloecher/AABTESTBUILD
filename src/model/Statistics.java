package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.Service;

public class Statistics {
	private final Service service = Service.getInstance();
	private List<Order> orders;

	/**
	 * Returns a map with the total sales of each pricelist used in an order
	 */
	public Map<Pricelist, Double> getSalesPrPricelist() {
		Map<Pricelist, Double> sales = new HashMap<>();

		for (Order o : orders) {
			Double currentTotal = sales.get(o.getPricelist());

			if (currentTotal == null) {
				currentTotal = 0.0;
			}

			currentTotal += o.totalPrice();

			sales.put(o.getPricelist(), currentTotal);
		}

		return sales;
	}

	/**
	 * Returns a map with the total price spent on each 'beer' product, if used
	 * in an order NOTE: Products not sold will not show up in the map
	 */
	public Map<String, Double> getSalesPrBeer() {
		Map<String, Double> sales = new HashMap<>();

		for (Order o : orders) {
			for (ProductOrder po : o.getAllProducts()) {
				Product p = po.getProduct();
				String category = po.getProduct().getCategory();

				if (category.equals("fadøl") || category.equals("flaske")
						|| category.equals("fustage")) {

					String name = p.getName().split(",")[0].trim();
					Double currentTotal = sales.get(name);

					if (currentTotal == null) {
						currentTotal = 0.0;
					}

					currentTotal += po.price();

					sales.put(name, currentTotal);
				}
			}
		}

		return sales;
	}

	/**
	 * Returns the total sales price if all clip cards sold
	 */
	public double getClipCardSales() {
		double d = 0;

		for (Order o : orders) {
			for (ProductOrder po : o.getProductOrders()) {
				if (po.getProduct().getName().startsWith("Klippekort")) {
					d += po.price();
				}
			}
		}

		return d;
	}

	/**
	 * Returns the total amount of clips used for all orders
	 */
	public int getClipCardUses() {
		int d = 0;

		for (Order o : orders) {
			for (Payment p : o.getPayments()) {
				if (p.getPaymentType() == PaymentType.CLIP_CARD) {
					d += p.getAmount();
				}
			}
		}

		return d;
	}

	/**
	 * Returns a map with the total price of all sales for each category used in
	 * an order NOTE: Categories with no sale will not show up
	 */
	public Map<String, Double> getSalesPrCategory() {
		Map<String, Double> sales = new HashMap<>();

		for (Order o : orders) {
			for (ProductOrder po : o.getProductOrders()) {
				String category = po.getProduct().getCategory();
				Double currentTotal = sales.get(category);

				if (currentTotal == null) {
					currentTotal = 0.0;
				}

				currentTotal += po.price();

				sales.put(category, currentTotal);
			}
		}

		return sales;
	}

	/**
	 * Returns a map with the total price of all sales made by each user NOTE:
	 * users with no sales will not show up
	 */
	public Map<User, Double> getSalesPrUser() {
		Map<User, Double> sales = new HashMap<>();

		for (Order o : orders) {
			Double currentTotal = sales.get(o.getUser());

			if (currentTotal == null) {
				currentTotal = 0.0;
			}

			currentTotal += o.totalPrice();

			sales.put(o.getUser(), currentTotal);
		}

		return sales;
	}

	/**
	 * Returns the sum of the total price of all orders
	 */
	public double getTotalSales() {
		double total = 0;

		for (Order o : orders) {
			total += o.totalPrice();
		}

		return total;
	}

	/**
	 * Limits the orders to a specific time period
	 */
	public void setTimePeriod(TimePeriod timePeriod) {
		assert timePeriod != null;

		orders = service.getOrdersInPeriod(timePeriod);
	}
}
