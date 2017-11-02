package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import model.TimePeriod;
import model.User;

@FunctionalInterface
interface DataGetter<Entry extends Map.Entry<?, ?>> {
	PieChart.Data get(Entry e);
}

public class Statistics extends GridPane {
	private final Controller controller = new Controller();
	private final ComboBox<String> cbTimePeriod = new ComboBox<>();
	private final model.Statistics statictics = new model.Statistics();
	private final PieChart pricelistPieChart = new PieChart();
	private final PieChart beerPieChart = new PieChart();
	private final PieChart categoryPieChart = new PieChart();
	private final Label caption = new Label();
	private final BarChart<Number, String> bc;
	private final Label clipCardSales = new Label();
	private final Label clipCardUses = new Label();
	private final Label total = new Label();

	public Statistics() {
		final NumberAxis xAxis = new NumberAxis();
		final CategoryAxis yAxis = new CategoryAxis();
		bc = new BarChart<>(xAxis, yAxis);
		bc.setTitle("Medarbejder Salg");
		bc.setPrefWidth(9999);
		xAxis.setLabel("Salg i kr.");
		xAxis.setTickLabelRotation(90);
		yAxis.setLabel("Medarbejder");

		final TimePeriod[] timePeriods = TimePeriod.values();
		final String[] timePeriodStrings = new String[timePeriods.length];

		for (int i = 0; i < timePeriods.length; i++) {

			switch (timePeriods[i]) {
			case DAY:
				timePeriodStrings[i] = "1 dag";
				break;
			case WEEK:
				timePeriodStrings[i] = "1 uge";
				break;
			case MONTH:
				timePeriodStrings[i] = "1 måned";
				break;
			case YEAR:
				timePeriodStrings[i] = "1 år";
				break;
			case FOREVER:
				timePeriodStrings[i] = "altid";
			default:
				break;
			}
		}

		cbTimePeriod.getItems().setAll(timePeriodStrings);
		cbTimePeriod.getSelectionModel().selectedItemProperty()
				.addListener(e -> controller.selectTimePeriod());
		cbTimePeriod.getSelectionModel().select(0);

		add(cbTimePeriod, 0, 0);

		pricelistPieChart.setTitle("Salg pr. prisliste");
		pricelistPieChart.setLegendSide(Side.LEFT);
		pricelistPieChart.setPrefWidth(99999);
		add(pricelistPieChart, 0, 1);

		beerPieChart.setTitle("Salg pr. øl");
		beerPieChart.setLegendSide(Side.LEFT);
		beerPieChart.setPrefWidth(99999);
		add(beerPieChart, 1, 1);

		categoryPieChart.setTitle("Salg pr. kategori");
		categoryPieChart.setLegendSide(Side.LEFT);
		categoryPieChart.setPrefWidth(99999);
		add(categoryPieChart, 2, 1);

		add(bc, 0, 2, 2, 1);

		add(clipCardSales, 0, 3);
		add(clipCardUses, 0, 4);
		add(total, 0, 5);

		caption.setStyle(
			"-fx-background-color: white; -fx-border-width: 1; -fx-border-color: black;"
			+ "-fx-border-style: solid; -fx-border-raduis: 3px");
		caption.setPadding(new Insets(5));
		caption.setTranslateX(-100);
		caption.setTranslateY(50);
		add(caption, 0, 0);
	}

	class Controller {

		public void selectTimePeriod() {
			TimePeriod timePeriod = null;

			switch (cbTimePeriod.getSelectionModel().getSelectedItem()) {
			case "1 dag":
				timePeriod = TimePeriod.DAY;
				break;
			case "1 uge":
				timePeriod = TimePeriod.WEEK;
				break;
			case "1 måned":
				timePeriod = TimePeriod.MONTH;
				break;
			case "1 år":
				timePeriod = TimePeriod.YEAR;
				break;
			case "altid":
				timePeriod = TimePeriod.FOREVER;
			default:
				break;
			}

			statictics.setTimePeriod(timePeriod);

			setPieChartData(pricelistPieChart,
					statictics.getSalesPrPricelist().entrySet(), entry -> {
						double amount = entry.getValue();

						return new PieChart.Data(entry.getKey().getName(), amount);
					});

			setPieChartData(beerPieChart, statictics.getSalesPrBeer().entrySet(),
					entry -> {
						final double amount = entry.getValue();

						return new PieChart.Data(entry.getKey(), amount);
					});

			setPieChartData(categoryPieChart, statictics.getSalesPrCategory().entrySet(),
					entry -> {
						final double amount = entry.getValue();

						return new PieChart.Data(entry.getKey(), amount);
					});

			setBarChartData();

			clipCardSales.setText(String.format("Klippekort solgt: %.2fkr.",
					statictics.getClipCardSales()));
			clipCardUses.setText("Klip brugt: " + statictics.getClipCardUses() + " klip");
			total.setText(String.format("I alt: %.2fkr.", statictics.getTotalSales()));
		}

		public <Entry extends Map.Entry<?, ?>> void setPieChartData(PieChart chart,
				Set<Entry> entrySet, DataGetter<Entry> dataGetter) {
			final ObservableList<PieChart.Data> data = FXCollections
					.observableArrayList();

			for (Entry entry : entrySet) {
				data.add(dataGetter.get(entry));
			}

			chart.setData(data);

			for (PieChart.Data d : data) {
				d.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
					caption.setTranslateX(e.getSceneX() - 100);
					caption.setTranslateY(e.getSceneY() - 100);
					caption.setText(
							String.format(Locale.GERMAN, "%.2f kr.", d.getPieValue()));
				});
			}
		}

		public void setBarChartData() {
			final List<XYChart.Series<Number, String>> series = new ArrayList<>();

			for (Map.Entry<User, Double> entry : statictics.getSalesPrUser().entrySet()) {
				final double amount = entry.getValue();
				final XYChart.Series<Number, String> s = new XYChart.Series<>();
				final XYChart.Data<Number, String> d = new XYChart.Data<>(amount,
						entry.getKey().getName());
				s.setName(entry.getKey().getName());

				s.getData().add(d);

				series.add(s);
			}

			bc.getData().setAll(series);

			for (XYChart.Series<Number, String> s : series) {
				for (XYChart.Data<Number, String> d : s.getData()) {
					d.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
						caption.setTranslateX(e.getSceneX() - 100);
						caption.setTranslateY(e.getSceneY() - 100);
						caption.setText(String.format(Locale.GERMAN, "%.2f kr.",
								d.getXValue().doubleValue()));
					});
				}
			}
		}
	}
}
