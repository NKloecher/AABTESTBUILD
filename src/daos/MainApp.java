package daos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class MainApp {
	private static Connection conn = null;

	public static void main(String[] args) throws SQLException {

		try {
			String url = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=AarhusBryghus";
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection(url, "sa", "asdf");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Hvilken funktion vil du vælge?");
		System.out.println("1: Opret Produkt");
		System.out.println("2: Opret Kategori");
		System.out.println("3: Dagligt salg");
		System.out.println("4: Dagligt salg opdelt på kategori");

		try (Scanner scanner = new Scanner(System.in)) {
			int operation = scanner.nextInt();
			switch (operation) {
			case 1:
				createProduct();
				break;
			case 2:
				createCategory();
				break;
			case 3:
				dailySales();
				break;
			case 4:
				dailySalesByCategory();
				break;
			default:
				System.out.println("Ikke gyldig funktion, kør program igen");
				break;
			}
		} catch (InputMismatchException e) {
			System.out.println("Ikke gyldig funktion, kør program igen");
		}
	}

	private static void createCategory() throws SQLException {
		String category;
		System.out.println("Skriv navnet på den nye kategori");
		try (Scanner scanner = new Scanner(System.in)) {
			category = scanner.next();
			try (PreparedStatement s = conn
					.prepareStatement("insert into category values(?)")) {
				s.setString(1, category);
				s.executeUpdate();
			}
		} catch (Exception e) {
			if (e.getMessage().startsWith("Violation of PRIMARY KEY constraint")) {
				System.out.println("kategorien findes allerede prøv igen");
				createCategory();
			} else {
				throw e;
			}
		}
	}

	private static void dailySalesByCategory() throws SQLException {
		String dateString;
		LocalDate date = null;
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Indtast dato for salg (yy-mm-dd)");
			dateString = scanner.next();
			while (date == null) {
				try {
					date = LocalDate.parse(dateString);
				} catch (DateTimeParseException e) {
					System.out.println("Ikke gyldig dato prøv igen");
				}
			}
		}
		try (PreparedStatement s = conn
				.prepareStatement("exec daily_sales_by_category @date=?")) {
			s.setString(1, dateString);

			try (ResultSet rs = s.executeQuery()) {
				while (rs.next()) {
					System.out.printf(Locale.GERMAN, "%s %.2f kr\n",
							rs.getString("category_name"), rs.getFloat("price"));
				}
			}
		}
	}

	public static void createProduct() throws SQLException {
		String name;
		Integer clips = null;
		String category = null;

		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Skriv navnet på produktet");
			name = scanner.next();
			System.out.println("Skriv hvor mange antal klip produktet koster");

			while (clips == null) {
				String c = scanner.next();
				try {
					clips = Integer.parseInt(c);
				} catch (NumberFormatException | InputMismatchException e) {
					System.out.println("Indtast et tal, por favor");
				}
			}
			String categories = "";

			try (ResultSet rs = conn.createStatement()
					.executeQuery("select name from category")) {
				while (rs.next()) {
					categories += rs.getString("name") + ", ";
				}
				categories = categories.substring(0, categories.length() - 2);
			}

			System.out.println("Skriv categorien på produktet");
			System.out.println("Gyldige kategorier er " + categories);

			while (category == null) {
				category = scanner.next();
				try (PreparedStatement s = conn
						.prepareStatement("exec test_category @name=?")) {
					s.setString(1, category);

					try (ResultSet rs = s.executeQuery()) {
						rs.next();

						if (!rs.getBoolean("exists")) {
							category = null;
							System.out.println("Kategorien findes ikke, prøv igen");
							System.out.println("Gyldige kategorier er " + categories);
						}
					}
				}
			}
		}

		try (PreparedStatement s = conn
				.prepareStatement("insert into product values(?,?,?)")) {
			s.setString(1, name);
			s.setInt(2, clips);
			s.setString(3, category);
			s.executeUpdate();
			System.out.printf("[%s, %s klip, %s] er oprettet som produkt", name, clips,
					category);
		}
	}

	public static void dailySales() throws SQLException {
		String dateString;
		LocalDate date = null;
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Indtast dato for salg (yy-mm-dd)");
			dateString = scanner.next();
			while (date == null) {
				try {
					date = LocalDate.parse(dateString);
				} catch (DateTimeParseException e) {
					System.out.println("Ikke gyldig dato");
				}
			}
		}
		try (PreparedStatement s = conn.prepareStatement("exec daily_sales @date=?")) {
			s.setString(1, dateString);
			try (ResultSet rs = s.executeQuery()) {
				rs.next();
				System.out.println("Dagligt salg: " + rs.getFloat(1) + " kr.");
			}
		}
	}
}
