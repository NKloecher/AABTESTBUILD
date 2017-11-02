package gui;

import com.sun.javafx.scene.control.skin.DatePickerSkin;
import gui.table.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.PaymentStatus;
import model.Tour;
import service.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Tours extends GridPane {
	private final Service service = Service.getInstance();
	private final Controller controller = new Controller();
	private final DatePicker dp = new DatePicker(LocalDate.now());
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private final Label lError = new Label();
	private final Table<Tour> table = new Table<>(
			(error, isValid) -> lError.setText(error));
	private final TextField tfNewPersons = new TextField();
	private final DatePicker dpNewDate = new DatePicker();
	private final TextField tfNewStart = new TextField();
	private final TextField tfNewEnd = new TextField();
	private final TextField tfNewPrice = new TextField();
	private Set<LocalDate> tourDates;
	private final Stage owner;

	public Tours(Stage owner) {
		this.owner = owner;

		setHgap(10);
		setVgap(10);
		setAlignment(Pos.TOP_CENTER);

		final Column<Tour> personsColumn = new PrimitiveColumn<>("Antal",
				PrimitiveColumn.Type.Integer, Tour::getPersons,
				service::updateTourPersons, (t, v) -> {
					if (Pattern.matches("^\\d+$", v))
						return null;
					return "Antal skal være et posetivt tal";
				});
		personsColumn.setPrefWidth(60.0);
		table.addColumn(personsColumn);

		final DateColumn<Tour> dateColumn = new DateColumn<>("Dato", controller::getDate,
				controller::updateDate);
		dateColumn.setPrefWidth(110.);
		table.addColumn(dateColumn);

		final Column<Tour> startColumn = new PrimitiveColumn<>("Start",
				PrimitiveColumn.Type.String, controller::getTimeStart,
				controller::updateStartTime);
		startColumn.setPrefWidth(70.);
		table.addColumn(startColumn);

		final Column<Tour> endColumn = new PrimitiveColumn<>("Slut",
				PrimitiveColumn.Type.String, controller::getTimeEnd,
				controller::updateEndTime);
		endColumn.setPrefWidth(70.);
		table.addColumn(endColumn);

		final Column<Tour> priceColumn = new PrimitiveColumn<>("Pris",
				PrimitiveColumn.Type.Double, Tour::totalPrice, service::updateTourPrice,
				(t, v) -> {
					if (Pattern.matches("^\\d+$", v))
						return null;
					return "Pris skal være et posetivt tal";
				});
		priceColumn.setPrefWidth(120.);
		table.addColumn(priceColumn);

		final ButtonColumn<Tour> buttonColumn = new ButtonColumn<>("Betal",
				controller::getPayments, controller::pay);
		table.addColumn(buttonColumn);

		tourDates = service.getTourDates();

		// Styling for the datepicker widget so that days with active events are
		// bold and underlined
		Callback<DatePicker, DateCell> dayCellFactory = (
				DatePicker datePicker) -> new DateCell() {
					@Override
					public void updateItem(LocalDate item, boolean empty) {
						super.updateItem(item, empty);
						if (tourDates.contains(item)) {
							setStyle("-fx-underline: true; -fx-font-weight: bolder");
						}
					}
				};

		dp.setDayCellFactory(dayCellFactory);
		DatePickerSkin datePickerSkin = new DatePickerSkin(dp);
		add(datePickerSkin.getPopupContent(), 0, 0);

		controller.openDate(LocalDate.now());

		add(table.getPane(), 0, 1);

		dp.valueProperty().addListener((x, y, value) -> controller.openDate(value));

		add(new Label("Opret ny:"), 0, 2);

		HBox hbAdd = new HBox(10);
		tfNewPersons.setPromptText("Antal Personer");
		tfNewPersons.setPrefWidth(60);
		hbAdd.getChildren().add(tfNewPersons);
		dpNewDate.setPromptText("Dato");
		dpNewDate.setPrefWidth(110);
		hbAdd.getChildren().add(dpNewDate);
		tfNewStart.setPromptText("Start tid");
		tfNewStart.setPrefWidth(70);
		hbAdd.getChildren().add(tfNewStart);
		tfNewEnd.setPromptText("Slut tid");
		tfNewEnd.setPrefWidth(70);
		hbAdd.getChildren().add(tfNewEnd);
		tfNewPrice.setPromptText("Pris");
		tfNewPrice.setPrefWidth(120.);
		hbAdd.getChildren().add(tfNewPrice);
		Button btnNew = new Button("Opret");
		hbAdd.getChildren().add(btnNew);
		btnNew.setOnAction(x -> controller.createTour());
		add(hbAdd, 0, 3);

		lError.setStyle("-fx-text-fill: red");
		add(lError, 0, 4);
	}

	private class Controller {
		public void openDate(LocalDate date) {
			List<Tour> tours = service.getTours(date);
			table.getPane().setVisible(tours.size() != 0);
			table.setItems(tours);
			tourDates = service.getTourDates(); // Reload the calendar widget
												// style
			dpNewDate.setValue(dp.getValue());
		}

		public LocalDate getDate(Tour tour) {
			return tour.getDate().toLocalDate();
		}

		public String getTimeStart(Tour tour) {
			return tour.getDate().toLocalTime().format(timeFormatter);
		}

		public String getTimeEnd(Tour tour) {
			return tour.getDate().toLocalTime().plus(tour.getDuration())
					.format(timeFormatter);
		}

		public void createTour() {
			Integer persons;
			try {
				int amount = Integer.parseInt(tfNewPersons.getText());
				if (amount > 0) {
					persons = amount;
				} else {
					lError.setText("Antallet af personer skal være størrer en 0");
					return;
				}
			} catch (NumberFormatException ex) {
				lError.setText("Antal personer er formateret forkert");
				return;
			}

			LocalDateTime startDateTime;
			Duration duration;
			try {
				LocalTime newStartTime = LocalTime.parse(tfNewStart.getText(),
						timeFormatter);
				LocalTime newEndTime = LocalTime.parse(tfNewEnd.getText(), timeFormatter);
				startDateTime = LocalDateTime.of(dpNewDate.getValue(), newStartTime);
				duration = Duration.between(newStartTime, newEndTime);
				if (duration.isNegative()) {
					lError.setText("Starttidspunktet skal være før sluttidspunktet");
					return;
				}
			} catch (DateTimeParseException e) {
				lError.setText(
						"Starttiden eller Sluttiden er formateret forkert (HH:MM)");
				return;
			}

			Double price;
			try {
				double amount = Double.parseDouble(tfNewPrice.getText());
				if (amount >= 0) {
					price = amount;
				} else {
					lError.setText("Prisen skal mindst værre 0");
					return;
				}
			} catch (NumberFormatException ex) {
				lError.setText("Prisen er formateret forkert");
				return;
			}

			service.createTour(persons, startDateTime, price, duration);
			// update ui
			openDate(dp.getValue());
			tfNewEnd.clear();
			tfNewPersons.clear();
			tfNewPrice.clear();
			tfNewStart.clear();
			dpNewDate.getEditor().clear();
			lError.setText("");
		}

		public void updateDate(Tour tour, LocalDate date) {
			service.updateTourDate(tour,
					LocalDateTime.of(date, tour.getDate().toLocalTime()));
		}

		public void updateStartTime(Tour tour, String time) {
			try {
				LocalTime newStartTime = LocalTime.parse(time, timeFormatter);
				LocalTime endTime = tour.getDate().toLocalTime().plus(tour.getDuration());

				if (newStartTime.isBefore(endTime)) {
					service.updateTourDate(tour,
							LocalDateTime.of(tour.getDate().toLocalDate(), newStartTime));
					lError.setText("");
				} else {
					lError.setText("Starttidspunktet skal være før sluttidspunktet");
				}
			} catch (DateTimeParseException e) {
				lError.setText("Starttiden er formateret forkert (HH:MM)");
			}
		}

		public void updateEndTime(Tour tour, String time) {
			try {
				LocalTime startTime = tour.getDate().toLocalTime();
				LocalTime newEndTime = LocalTime.parse(time, timeFormatter);

				if (newEndTime.isAfter(startTime)) {
					LocalDateTime newEndDateTime = LocalDateTime
							.of(tour.getDate().toLocalDate(), newEndTime);
					service.updateTourDuration(tour,
							Duration.between(tour.getDate(), newEndDateTime));
					lError.setText("");
				} else {
					lError.setText("Sluttidspunktet skal være efter starttidspunktet");
				}
			} catch (DateTimeParseException e) {
				lError.setText("Sluttiden er formateret forkert (HH:MM)");
			}
		}

		public Button getPayments(Tour tour) {
			Button btn = new Button();
			if (tour.paymentStatus() == PaymentStatus.ORDERPAID) {
				btn.setText("Betalt");
				btn.setDisable(true);
			} else {
				btn.setText("Betal Nu");
			}
			return btn;
		}

		public void pay(Tour tour) {
			PayDialog pd = new PayDialog(owner, tour, tour.totalPrice(), null);

			pd.showAndWait();

			if (tour.paymentStatus() == PaymentStatus.ORDERPAID) {
				// Update the ui
				openDate(dp.getValue());
			}

		}
	}
}