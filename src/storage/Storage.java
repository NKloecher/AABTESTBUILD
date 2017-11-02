package storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import model.*;

public class Storage implements Serializable {
	private static Storage instance = new Storage();

	private final List<User> users = new ArrayList<>();
	private final List<Product> products = new ArrayList<>();
	private final List<Pricelist> pricelists = new ArrayList<>();
	private final List<Payment> payments = new ArrayList<>();
	private final List<Order> orders = new ArrayList<>();
	private final List<Tour> tours = new ArrayList<>();
	private final List<Customer> customers = new ArrayList<>();
	private final List<String> categories = new ArrayList<>();

	private Storage() {
	}

	public static Storage getInstance() {
		return instance;
	}

	public static void saveStorage() throws IOException {
		try (FileOutputStream file_out = new FileOutputStream("Storage.data");
				ObjectOutputStream obj_out = new ObjectOutputStream(file_out)) {
			obj_out.writeObject(instance);
		}
	}

	/**
	 * Loads the "Storage.data" file and returns the ListStorage
	 */
	public static Storage loadStorage() throws IOException, ClassNotFoundException {
		try (FileInputStream file_in = new FileInputStream("Storage.data");
				ObjectInputStream obj_in = new ObjectInputStream(file_in)) {
			Object obj = obj_in.readObject();
			instance = (Storage) obj;
			return instance;
		}
	}

	public void addCategory(String category) {
		categories.add(category);
	}

	public List<String> getCategories() {
		return new ArrayList<>(categories);
	}

	public void removeCategory(String s) {
		categories.remove(s);
	}

	public List<Tour> getTours() {
		return new ArrayList<>(tours);
	}

	public void addTour(Tour tour) {
		tours.add(tour);
	}

	public List<Customer> getCustomers() {
		return new ArrayList<>(customers);
	}

	public void addCustomer(Customer c) {
		customers.add(c);
	}

	public void removeCustomer(Customer c) {
		customers.remove(c);
	}

	public List<User> getUsers() {
		return new ArrayList<>(users);
	}

	public void addUser(User u) {
		users.add(u);
	}

	public List<Product> getProducts() {
		return new ArrayList<>(products);
	}

	public void addProduct(Product p) {
		products.add(p);
	}

	public void removeProduct(Product p) {
		products.remove(p);
	}

	public List<Pricelist> getPricelists() {
		return new ArrayList<>(pricelists);
	}

	public void addPricelist(Pricelist pricelist) {
		pricelists.add(pricelist);
	}

	public void removePricelist(Pricelist pricelist) {
		pricelists.remove(pricelist);
	}

	public List<Payment> getPayments() {
		return new ArrayList<>(payments);
	}

	public void addPayment(Payment payment) {
		payments.add(payment);
	}

	public List<Order> getOrders() {
		return new ArrayList<>(orders);
	}

	public void addOrder(Order order) {
		orders.add(order);
	}
}