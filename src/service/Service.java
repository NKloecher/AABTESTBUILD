package service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.sasl.AuthenticationException;

import exceptions.DiscountParseException;
import model.*;
import storage.Storage;

public class Service {
	private final static Service instance = new Service();
	private User activeUser;
	private Storage storage = Storage.getInstance();
	private Pricelist selectedPricelist;

	private Service() {
	}

	public void updateProductName(Product product, String name) {
		assert product != null;
		assert name != null && !name.isEmpty();

		product.setName(name);
	}

	public void updateProductClips(Product product, Integer clips) {
		assert product != null;

		product.setClips(clips);
	}

	public void updateOrderDiscount(Order order, String discount) {
		order.setDiscount(discount);
	}

	public void updateProductCategory(Product product, String category) {
		assert product != null;
		assert category != null && !category.isEmpty();

		product.setCategory(category);
	}

	/**
	 * returns all orders that are not returned or paid
	 */
	public List<Order> getRentals() {
		List<Order> rentals = new ArrayList<>();

		for (Order o : storage.getOrders()) {
			PaymentStatus s = o.paymentStatus();

			if (s == PaymentStatus.DEPOSITPAID) {
				rentals.add(o);
			}
		}

		return rentals;
	}

	/**
	 * returns all product where the query is contained in the name and the
	 * products category equals category parameter if category is "All" every
	 * category will be selected
	 */
	public List<Product> getMatchingProducts(String query, String category,
			List<Product> products) {
		// assert query != null && !query.isEmpty();
		assert category != null && !category.isEmpty();
		assert products != null;

		List<Product> selected = new ArrayList<>();

		for (Product p : products) {
			if (p.getName().toLowerCase().contains(query.toLowerCase())
					&& (category.equals("All") || p.getCategory().equals(category))) {
				selected.add(p);
			}
		}

		return selected;
	}

	/**
	 * sets serivce.user if username and password is correct if username or
	 * password is not correct it throws an error
	 */
	public void login(String username, String password) throws AuthenticationException {
		assert username != null && !username.isEmpty();
		assert password != null && !password.isEmpty();

		for (User u : storage.getUsers()) {
			if (u.getUsername().equals(username)) {
				if (u.checkPassword(password)) {
					activeUser = u;
					return;
				}
			}
		}

		throw new AuthenticationException("wrong username or password");
	}

	/**
	 * sets activeUser to null
	 */
	public void logout() {
		assert activeUser != null;
		activeUser = null;
	}

	public void setSelectedPricelist(Pricelist pricelist) {
		assert pricelist != null;

		selectedPricelist = pricelist;
	}

	public void setProductToPricelist(Product product, Pricelist pricelist,
			double price) {
		assert pricelist != null;
		assert product != null;

		pricelist.setPrice(product, price);
	}

	public Pricelist getSelectedPricelist() {
		return selectedPricelist;
	}

	/**
	 * is null if no user is logged in
	 */
	public User getActiveUser() {
		return activeUser;
	}

	public void addCategory(String category) {
		assert category != null && !category.isEmpty();

		storage.addCategory(category);
	}

	public User createUser(String name, String username, String password,
			Permission permission) {
		assert name != null && !name.isEmpty();
		assert username != null && !username.isEmpty();
		assert password != null && !password.isEmpty();
		assert permission != null;

		User u = new User(name, username, password, permission);

		storage.addUser(u);

		return u;
	}

	public void updateUserPassword(User user, String password) {
		assert user != null;
		assert password != null && !password.isEmpty();

		user.setPassword(password);
	}

	public void deleteUser(User user) {
		assert user != null;

		user.setDeleted();
	}

	public void updateUserName(User user, String name) {
		assert user != null;
		assert name != null && !name.isEmpty();

		user.setName(name);
	}

	public void updateUserUsername(User user, String username) {
		assert user != null;
		assert username != null && !username.isEmpty();

		user.setUsername(username);
	}

	public void updateUserPermission(User user, Permission permission) {
		assert user != null;
		assert permission != null;

		user.setPermission(permission);
	}

	public boolean usernameIsUnique(String username, User user) {
		assert user != null;
		assert username != null && !username.isEmpty();

		for (User u : storage.getUsers()) {
			if (!u.equals(user) && u.getUsername().equals(username)) {
				return false;
			}
		}

		return true;
	}

	public Tour createTour(int persons, LocalDateTime date, double price,
			Duration duration) {
		assert date != null;
		assert duration != null;

		Tour tour = new Tour(persons, date, price, duration, activeUser);
		storage.addTour(tour);
		return tour;
	}

	public List<Tour> getTours() {
		return storage.getTours();
	}

	public List<Tour> getTours(LocalDate date) {
		assert date != null;

		List<Tour> tours = new ArrayList<>();
		for (Tour tour : getTours()) {
			if (tour.getDate().toLocalDate().equals(date)) {
				tours.add(tour);
			}
		}
		return tours;
	}

	public Set<LocalDate> getTourDates() {
		Set<LocalDate> dates = new HashSet<>();
		for (Tour tour : getTours()) {
			dates.add(tour.getDate().toLocalDate());
		}
		return dates;
	}

	public void updateTourPersons(Tour tour, int persons) {
		assert tour != null;

		tour.setPersons(persons);
	}

	public void updateTourDate(Tour tour, LocalDateTime date) {
		assert tour != null;
		assert date != null;

		tour.setDate(date);
	}

	public void updateTourPrice(Tour tour, double price) {
		assert tour != null;

		tour.setPrice(price);
	}

	public void updateTourDuration(Tour tour, Duration duration) {
		assert tour != null;
		assert duration != null;

		tour.setDuration(duration);
	}

	public void updateProductOrderAmount(ProductOrder productOrder, int amount) {
		assert productOrder != null;

		productOrder.setAmount(amount);
	}

	public void updateProductOrderReturned(RentalProductOrder po, int returned) {
		po.setReturned(returned);
	}

	public void updateProductOrderUnused(RentalProductOrder po, int unused) {
		po.setUnused(unused);
	}

	public void removeProduct(Product p) {
		assert p != null;

		storage.removeProduct(p);
	}

	public Product createProduct(String name, Integer clips, String category,
			String image) {
		assert name != null;

		Product product = new Product(name, clips, category, image);
		storage.addProduct(product);

		return product;
	}

	public DepositProduct createDepositProduct(String name, Integer clips,
			String category, String image, double deposit) {
		assert name != null;

		DepositProduct depositProduct = new DepositProduct(name, clips, category, image,
				deposit);
		storage.addProduct(depositProduct);
		return depositProduct;
	}

	public void updateDeposit(DepositProduct product, double value) {
		product.setDeposit(value);
	}

	public List<Pricelist> getPricelists() {
		return storage.getPricelists();
	}

	public void removePricelist(Pricelist pricelist) {
		assert pricelist != null;

		storage.removePricelist(pricelist);
		setSelectedPricelist(storage.getPricelists().get(0));
	}

	public Pricelist createPricelist(String name) {
		assert name != null && !name.isEmpty();

		Pricelist pricelist = new Pricelist(name);
		storage.addPricelist(pricelist);
		return pricelist;
	}

	public Payment createPayment(Payable payable, double amount,
			PaymentType paymentType) {
		assert payable != null;
		assert paymentType != null;

		Payment payment = new Payment(paymentType, amount);

		payable.pay(payment);
		storage.addPayment(payment);
		return payment;
	}

	public List<Product> getProducts() {
		return storage.getProducts();
	}

	public Order createOrder() {
		return createOrder(activeUser, selectedPricelist);
	}

	public Order createOrder(User user, Pricelist pricelist) {
		assert user != null;
		assert pricelist != null;

		Order order = new Order(user, pricelist);
		storage.addOrder(order);
		return order;
	}

	public ProductOrder createProductOrder(Order order, Product product) {
		assert order != null;
		assert product != null;

		return order.createProductOrder(product);
	}

	public RentalProductOrder createRentalProductOrder(Order order,
			DepositProduct product) {
		assert order != null;
		assert product != null;

		return order.createRentalProductOrder(product);
	}

	public Customer createCustomer(String name, String address, String phone,
			String email) {
		assert name != null && !name.isEmpty();

		Customer c = new Customer(name, address, phone, email);
		storage.addCustomer(c);
		return c;
	}

	public void removeCustomer(Customer c) throws Exception {
		assert c != null;

		for (Order o : storage.getOrders()) {
			if (o.getCustomer() != null && o.getCustomer().equals(c)) {
				throw new Exception("Customer has orders");
			}
		}

		storage.removeCustomer(c);
	}

	public Storage loadStorage() throws IOException, ClassNotFoundException {
		return Storage.loadStorage();
	}

	public void saveStorage() throws IOException {
		Storage.saveStorage();
	}

	public void initStorage() throws DiscountParseException, AuthenticationException {
		try {
			storage = loadStorage();
			System.out.println("Loaded data from storage");
			setSelectedPricelist(storage.getPricelists().get(0));
			return;
		} catch (IOException | ClassNotFoundException e) {
			System.out
					.println("Could not load storage, generating data from initStorage");
		}

		User user = createUser("John", "test", "test", Permission.ADMIN);
		User user1 = createUser("John Johnson", "test1", "test", Permission.NORMAL);

		login("test", "test");

		Pricelist pl1 = createPricelist("Fredagsbar");
		setSelectedPricelist(pl1);
		Pricelist pl2 = createPricelist("Butik");

		addCategory("flaske");
		addCategory("fadøl");
		addCategory("fustage");
		addCategory("andet");
		addCategory("spiritus");
		addCategory("kulsyre");
		addCategory("malt");
		addCategory("beklædning");
		addCategory("anlæg");
		addCategory("glas");
		addCategory("sampakninger");

		Product productKlippekort = createProduct("Klippekort, 4 klip", 4, "andet", null);
		setProductToPricelist(productKlippekort, pl1, 100);
		setProductToPricelist(productKlippekort, pl2, 100);

		// FLASKE ØL
		Product productFlaskeKlosterbryg = createProduct("Klosterbryg", 2, "flaske",
				null);
		setProductToPricelist(productFlaskeKlosterbryg, pl1, 50);
		setProductToPricelist(productFlaskeKlosterbryg, pl2, 36);
		Product productFlaskeSweetGeorgiaBrown = createProduct("Sweet Georgia Brown", 2,
				"flaske", null);
		setProductToPricelist(productFlaskeSweetGeorgiaBrown, pl1, 50);
		setProductToPricelist(productFlaskeSweetGeorgiaBrown, pl2, 36);
		Product productFlaskeExtraPilsner = createProduct("Extra Pilsner", 2, "flaske",
				"extra-pilsner.png");
		setProductToPricelist(productFlaskeExtraPilsner, pl1, 50);
		setProductToPricelist(productFlaskeExtraPilsner, pl2, 36);
		Product productCelebration = createProduct("Celebration", 2, "flaske", null);
		setProductToPricelist(productCelebration, pl1, 50);
		setProductToPricelist(productCelebration, pl2, 36);
		Product productBlondie = createProduct("Blondie", 2, "flaske", null);
		setProductToPricelist(productBlondie, pl1, 50);
		setProductToPricelist(productBlondie, pl2, 36);
		Product productForaarsbryg = createProduct("Forårsbryg", 2, "flaske", null);
		setProductToPricelist(productForaarsbryg, pl1, 50);
		setProductToPricelist(productForaarsbryg, pl2, 36);
		Product productIndiaPaleAle = createProduct("India Pale Ale", 2, "flaske", null);
		setProductToPricelist(productIndiaPaleAle, pl1, 50);
		setProductToPricelist(productIndiaPaleAle, pl2, 36);
		Product productJulebryg = createProduct("Julebryg", 2, "flaske", null);
		setProductToPricelist(productJulebryg, pl1, 50);
		setProductToPricelist(productJulebryg, pl2, 36);
		Product productJuletoenden = createProduct("Juletønden", 2, "flaske", null);
		setProductToPricelist(productJuletoenden, pl1, 50);
		setProductToPricelist(productJuletoenden, pl2, 36);
		Product productOldStrongAle = createProduct("Old Strong Ale", 2, "flaske", null);
		setProductToPricelist(productOldStrongAle, pl1, 50);
		setProductToPricelist(productOldStrongAle, pl2, 36);
		Product productFregattenJylland = createProduct("Fregatten Jylland", 2, "flaske",
				null);
		setProductToPricelist(productFregattenJylland, pl1, 50);
		setProductToPricelist(productFregattenJylland, pl2, 36);
		Product productImperialStout = createProduct("Imperial Stout", 2, "flaske", null);
		setProductToPricelist(productImperialStout, pl1, 50);
		setProductToPricelist(productImperialStout, pl2, 36);
		Product productTribute = createProduct("Tribute", 2, "flaske", null);
		setProductToPricelist(productTribute, pl1, 50);
		setProductToPricelist(productTribute, pl2, 36);
		Product productBlackMonster = createProduct("Black Monster", 2, "flaske", null);
		setProductToPricelist(productBlackMonster, pl1, 50);
		setProductToPricelist(productBlackMonster, pl2, 50);

		// FADØL
		Product productFadolKlosterbryg = createProduct("Klosterbryg", 2, "fadøl", null);
		setProductToPricelist(productFadolKlosterbryg, pl1, 30);
		Product productFadolJazzClassic = createProduct("Jazz Classic", 2, "fadøl", null);
		setProductToPricelist(productFadolJazzClassic, pl1, 30);
		Product productFadolExtraPilsner = createProduct("Extra Pilsner", 2, "fadøl",
				null);
		setProductToPricelist(productFadolExtraPilsner, pl1, 30);
		Product productFadolCelebration = createProduct("Celebration", 2, "fadøl", null);
		setProductToPricelist(productFadolCelebration, pl1, 30);
		Product productFadolBlondie = createProduct("Blondie", 2, "fadøl", null);
		setProductToPricelist(productFadolBlondie, pl1, 30);
		Product productFadolForaarsbryg = createProduct("Forårsbryg", 2, "fadøl", null);
		setProductToPricelist(productFadolForaarsbryg, pl1, 30);
		Product productFadolIndiaPaleAle = createProduct("India Pale Ale", 2, "fadøl",
				null);
		setProductToPricelist(productFadolIndiaPaleAle, pl1, 30);
		Product productFadolJulebryg = createProduct("Julebryg", 2, "fadøl", null);
		setProductToPricelist(productFadolJulebryg, pl1, 30);
		Product productFadolImperialStout = createProduct("Imperial Stout", 2, "fadøl",
				null);
		setProductToPricelist(productFadolImperialStout, pl1, 30);
		Product productFadolSpecial = createProduct("Special", 2, "fadøl", null);
		setProductToPricelist(productFadolSpecial, pl1, 30);
		Product productFadolAeblebrus = createProduct("Æblebrus", 1, "fadøl", null);
		setProductToPricelist(productFadolAeblebrus, pl1, 15);
		Product productFadolChips = createProduct("Chips", 1, "fadøl", null);
		setProductToPricelist(productFadolChips, pl1, 10);
		Product productFadolPeanuts = createProduct("Peanuts", 1, "fadøl", null);
		setProductToPricelist(productFadolPeanuts, pl1, 10);
		Product productFadolCola = createProduct("Cola", 1, "fadøl", null);
		setProductToPricelist(productFadolCola, pl1, 15);
		Product productFadolNikoline = createProduct("Nikoline", 1, "fadøl", null);
		setProductToPricelist(productFadolNikoline, pl1, 15);
		Product productFadol7Up = createProduct("7-Up", 1, "fadøl", null);
		setProductToPricelist(productFadol7Up, pl1, 15);
		Product productFadolWater = createProduct("Vand", 1, "fadøl", null);
		setProductToPricelist(productFadolWater, pl1, 10);

		// SPIRITUS
		Product productSpiritOfAarhus = createProduct("Spirit of Aarhus", null,
				"spiritus", null);
		setProductToPricelist(productSpiritOfAarhus, pl1, 300);
		setProductToPricelist(productSpiritOfAarhus, pl2, 300);
		Product productSoAStick = createProduct("Spirit of Aarhus with stick", null,
				"spiritus", null);
		setProductToPricelist(productSoAStick, pl1, 350);
		setProductToPricelist(productSoAStick, pl2, 350);
		Product productWhisky = createProduct("Whisky", null, "spiritus", null);
		setProductToPricelist(productWhisky, pl1, 500);
		setProductToPricelist(productWhisky, pl2, 500);
		Product productLiquorOfAarhus = createProduct("Liquor of Aarhus", null,
				"spiritus", null);
		setProductToPricelist(productLiquorOfAarhus, pl1, 175);
		setProductToPricelist(productLiquorOfAarhus, pl2, 175);

		// FUSTAGE
		DepositProduct depositProductKlosterbryg = createDepositProduct(
				"Klosterbryg, 20 liter", null, "fustage", null, 200);
		setProductToPricelist(depositProductKlosterbryg, pl2, 775);
		DepositProduct depositProductKlosterbryg25 = createDepositProduct(
				"Klosterbryg, 25 liter", null, "fustage", null, 200);
		setProductToPricelist(depositProductKlosterbryg25, pl2, 968.75);
		DepositProduct depositProductJazzClassic = createDepositProduct(
				"Jazz Classic, 25 liter", null, "fustage", null, 200);
		setProductToPricelist(depositProductJazzClassic, pl2, 625);
		DepositProduct depositProductExtraPilsner = createDepositProduct(
				"Extra Pilsner, 25 liter", null, "fustage", null, 200);
		setProductToPricelist(depositProductExtraPilsner, pl2, 575);
		DepositProduct depositProductCelebration = createDepositProduct(
				"Celebration, 20 liter", null, "fustage", null, 200);
		setProductToPricelist(depositProductCelebration, pl2, 775);
		DepositProduct depositProductBlondie = createDepositProduct("Blondie, 25 liter",
				null, "fustage", null, 200);
		setProductToPricelist(depositProductBlondie, pl2, 700);
		DepositProduct depositProductForaarsbryg = createDepositProduct(
				"Forrsårsbryg, 20 liter", null, "fustage", null, 200);
		setProductToPricelist(depositProductForaarsbryg, pl2, 775);
		DepositProduct depositProductIndiaPaleAle = createDepositProduct(
				"India Pale Ale, 20 liter", null, "fustage", null, 200);
		setProductToPricelist(depositProductIndiaPaleAle, pl2, 775);
		DepositProduct depositProductJulebryg = createDepositProduct("Julebryg, 20 liter",
				null, "fustage", null, 200);
		setProductToPricelist(depositProductJulebryg, pl2, 775);
		DepositProduct depositProductImperialStout = createDepositProduct(
				"Imperial Stout, 20 liter", null, "fustage", null, 200);
		setProductToPricelist(depositProductImperialStout, pl2, 775);

		// KULSYRE
		DepositProduct productCO2kg6 = createDepositProduct("Kulsyre", null, "kulsyre",
				null, 1000);
		setProductToPricelist(productCO2kg6, pl1, 400);
		setProductToPricelist(productCO2kg6, pl2, 400);
		DepositProduct productCO2kg4 = createDepositProduct("Kulsyre", null, "kulsyre",
				null, 1000);
		setProductToPricelist(productCO2kg4, pl1, 266);
		setProductToPricelist(productCO2kg4, pl2, 266);
		DepositProduct productCO2kg10 = createDepositProduct("Kulsyre", null, "kulsyre",
				null, 1000);
		setProductToPricelist(productCO2kg10, pl1, 666);
		setProductToPricelist(productCO2kg10, pl2, 666);

		// MALT
		Product productMalt = createProduct("Malt, 25kg", null, "malt", null);
		setProductToPricelist(productMalt, pl2, 300);

		// BEKLÆDNING
		Product productTShirt = createProduct("T-Shirt", null, "beklædning", null);
		setProductToPricelist(productTShirt, pl1, 70);
		setProductToPricelist(productTShirt, pl2, 70);
		Product productPolo = createProduct("Polo", null, "beklædning", null);
		setProductToPricelist(productPolo, pl1, 100);
		setProductToPricelist(productPolo, pl2, 100);
		Product productCap = createProduct("Cap", null, "beklædning", null);
		setProductToPricelist(productCap, pl1, 30);
		setProductToPricelist(productCap, pl2, 30);

		// ANLÆG
		DepositProduct product1Valve = createDepositProduct("Anlæg 1-hane", null, "anlæg",
				null, 0);
		setProductToPricelist(product1Valve, pl2, 250);
		DepositProduct product2Valve = createDepositProduct("Anlæg 2-haner", null,
				"anlæg", null, 0);
		setProductToPricelist(product2Valve, pl2, 400);
		DepositProduct productBar = createDepositProduct("Kulsyre", null, "kulsyre", null,
				0);
		setProductToPricelist(productBar, pl2, 500);
		Product productDelivery = createProduct("Levering", null, "anlæg", null);
		setProductToPricelist(productDelivery, pl2, 500);
		Product productKrus = createProduct("Krus", null, "anlæg", null);
		setProductToPricelist(productKrus, pl2, 60);

		// GLAS
		Product productGlass = createProduct("Glas", null, "glas", null);
		setProductToPricelist(productGlass, pl2, 15);

		// SAMPAKNINGER
		Product productGiftbag2Beer2Glass = createProduct("Gaveæske 2 øl, 2 glas", null,
				"sampakninger", null);
		setProductToPricelist(productGiftbag2Beer2Glass, pl1, 100);
		setProductToPricelist(productGiftbag2Beer2Glass, pl2, 100);
		Product productGiftbag4Beer = createProduct("Gaveæske 4 øl", null, "sampakninger",
				null);
		setProductToPricelist(productGiftbag4Beer, pl1, 130);
		setProductToPricelist(productGiftbag4Beer, pl2, 130);
		Product productGiftbag6Beer = createProduct("Trækasse 6 øl", null, "sampakninger",
				null);
		setProductToPricelist(productGiftbag6Beer, pl1, 240);
		setProductToPricelist(productGiftbag6Beer, pl2, 240);
		Product productGiftbag6Beer2Glass = createProduct("Gavekurv 6 øl, 2 glas", null,
				"sampakninger", null);
		setProductToPricelist(productGiftbag6Beer2Glass, pl1, 250);
		setProductToPricelist(productGiftbag6Beer2Glass, pl2, 250);
		Product productGiftbag6Beer6Glass = createProduct("Trækasse 6 øl, 6 glas", null,
				"sampakninger", null);
		setProductToPricelist(productGiftbag6Beer6Glass, pl1, 290);
		setProductToPricelist(productGiftbag6Beer6Glass, pl2, 290);
		Product productGiftbag12BeerWood = createProduct("Trækasse 12 øl", null,
				"sampakninger", null);
		setProductToPricelist(productGiftbag12BeerWood, pl1, 390);
		setProductToPricelist(productGiftbag12BeerWood, pl2, 390);
		Product productGiftbag12Beer = createProduct("Papkasse 12 øl", null,
				"sampakninger", null);
		setProductToPricelist(productGiftbag12Beer, pl1, 360);
		setProductToPricelist(productGiftbag12Beer, pl2, 360);

		createTour(5, LocalDateTime.now(), 1000, Duration.of(1, ChronoUnit.HOURS));
		// Customers
		createCustomer("Hans Hansen", "Vestervej 38", "35698457",
				"somewhere@somethere.dk");
		createCustomer("Hans Jensen", "Østervej 38", "35864557", "somehere@somethere.dk");
		createCustomer("Østerli Nielsen", "Søndenvej 38", "8979854",
				"where@somethere.dk");
		Customer dos = createCustomer("Person 2.0", "Nordenvej 38", "39875557",
				"here@somethere.dk");
		createCustomer("Niels Sommer", "Åkæret 1", "35634687", "there@somethere.dk");
		Customer uno = createCustomer("Jens-Peter Petersen", "Nyborg", "878788457",
				"somehow@somethere.dk");

		// Order
		Order order1 = createOrder(user, pl1);
		ProductOrder po1 = createProductOrder(order1, productFadolNikoline);
		createProductOrder(order1, productFadolCelebration);
		ProductOrder po2 = createProductOrder(order1, productFlaskeExtraPilsner);
		po1.setAmount(5);
		po2.setDiscount("-10");
		order1.setCustomer(uno);

		Order order2 = createOrder(user, pl2);
		createRentalProductOrder(order2, depositProductKlosterbryg);
		createPayment(order2, order2.totalPrice() + order2.totalDeposit(),
				PaymentType.CASH);
		order2.setCustomer(uno);

		Order order3 = createOrder(user1, pl1);
		createProductOrder(order3, productFadolNikoline);
		createProductOrder(order3, productFadolCelebration);
		ProductOrder po4 = createProductOrder(order3, productFlaskeExtraPilsner);
		po4.setAmount(5);
		order3.setCustomer(dos);

	}

	public List<Order> getOrdersInPeriod(TimePeriod timePeriod) {
		List<Order> selected = new ArrayList<>();
		LocalDate fromDate = LocalDate.now();

		switch (timePeriod) {
		case DAY:
			fromDate = fromDate.minus(1, ChronoUnit.DAYS);
		case WEEK:
			fromDate = fromDate.minus(1, ChronoUnit.WEEKS);
		case MONTH:
			fromDate = fromDate.minus(1, ChronoUnit.MONTHS);
		case YEAR:
			// noinspection UnusedAssignment
			fromDate = fromDate.minus(1, ChronoUnit.YEARS);
		case FOREVER:
			fromDate = LocalDate.ofYearDay(1970, 1);
		default:
			break;
		}

		for (Order o : storage.getOrders()) {
			if (o.getDate().isAfter(fromDate)) {
				selected.add(o);
			}
		}

		return selected;
	}

	public static Service getInstance() {
		return instance;
	}

	public void removeProductFromPricelist(Product product, Pricelist p) {
		p.removeProduct(product);
	}
}
