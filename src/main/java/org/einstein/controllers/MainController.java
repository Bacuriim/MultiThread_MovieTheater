package org.einstein.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.einstein.cinema.CinemaThread;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {

	@FXML
	private VBox vbMain;

	@FXML
	private AnchorPane ap;

	@FXML
	private Pane paneVisual;

	@FXML
	private GridPane gpFanStatus;

	@FXML
	private RowConstraints rcFila;

	@FXML
	private RowConstraints rcWait;

	@FXML
	private RowConstraints rcWatching;

	@FXML
	private RowConstraints rcBreakTime;

	@FXML
	private GridPane gridPane;

	@FXML
	public Button btAddFan;

	@FXML
	public Button btRemoveFan;

	@FXML
	private Button btStartDemonstrator;

	@FXML
	private Button btPauseDemonstrator;

	@FXML
	private Button btChangeDemonstrator;

	@FXML
	public TextArea log;

	@FXML
	public Label lbDemonstratorStatus;

	private static MainController mainController;

	public final List<CinemaThread.Fan> fans = new ArrayList<>();
	public final List<Label> fansLabels = new ArrayList<>();
	private final Map<Label, Timeline> blinkingFansTimelines = new HashMap<>();
	private final Map<Label, Boolean> isToBlinkingFansTimelines = new HashMap<>();
	private final Map<Label, Timeline> blinkingDemonstratorTimelines = new HashMap<>();
	private final Map<Label, Boolean> isToBlinkDemonstratorTimelines = new HashMap<>();
	private CinemaThread.Demonstrator demonstrator;
	private Stage stage;
	private boolean isConfigured = false;
	private boolean isToBlink = false;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public static MainController getInstance() {
		return mainController;
	}

	public static void setInstance(MainController mainController) {
		MainController.mainController = mainController;
	}

	@FXML
	private void initialize() {
		btAddFan.setOnAction(e -> handleAddFan());
		btAddFan.setDisable(true);
		btRemoveFan.setOnAction(e -> handleRemoveFan());
		btRemoveFan.setDisable(true);
		btChangeDemonstrator.setOnAction(e -> handleConfigDemonstrator());
		btStartDemonstrator.setOnAction(e -> handleStartDemonstrator());
		btPauseDemonstrator.setOnAction(e -> handlePauseDemonstrator());
		gpFanStatus.getColumnConstraints()
				.addListener((ListChangeListener<? super ColumnConstraints>) cc -> resizeGridPane());
	}

	@FXML
	public void updateTextArea(String msg) {
		Platform.runLater(() -> {
			if (log.getText().isEmpty()) {
				log.appendText("LOG:");
			}
			log.appendText("\n" + msg);
		});
	}

	private void handleAddFan() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreateFanView.fxml"));
			Parent root = loader.load();

			CreateFanController controller = loader.getController();
			controller.initData(fans);

			Stage createFanStage = new Stage();
			controller.setStage(createFanStage);

			createFanStage.setScene(new Scene(root));
			createFanStage.setTitle("Criar Fan");
			createFanStage.getIcons()
					.add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cinema.png"))));
			createFanStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleRemoveFan() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RemoveFanView.fxml"));
			Parent root = loader.load();

			RemoveFanController controller = loader.getController();
			controller.initData(fans);

			Stage removeFanStage = new Stage();
			controller.setStage(removeFanStage);

			removeFanStage.setScene(new Scene(root));
			removeFanStage.setTitle("Remover Fan");
			removeFanStage.getIcons()
					.add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cinema.png"))));
			removeFanStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleConfigDemonstrator() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ConfigDemonstratorView.fxml"));
			Parent root = loader.load();

			ConfigDemonstratorController controller = loader.getController();
			demonstrator = controller.getDemonstrator();

			Stage configDemonstratorStage = new Stage();
			controller.setStage(configDemonstratorStage);

			configDemonstratorStage.setScene(new Scene(root));
			configDemonstratorStage.setTitle("Configurar Demonstrador");
			configDemonstratorStage.getIcons()
					.add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cinema.png"))));
			configDemonstratorStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleStartDemonstrator() {
		if (demonstrator == null 
				|| (!CinemaThread.paused && isConfigured))
			return;
		if (!isConfigured) {
			btAddFan.setDisable(false);
			btRemoveFan.setDisable(false);
		}
		isConfigured = true;

		btChangeDemonstrator.setDisable(true);
		demonstrator.startDemonstrator();
		for (Timeline fanTimeline : blinkingFansTimelines.values()) {
			if (Animation.Status.STOPPED.equals(fanTimeline.getStatus())) {
				fanTimeline.play();
			}
		}

		for (Timeline timeline : blinkingDemonstratorTimelines.values()) {
			timeline.play();
		}
	}

	private void handlePauseDemonstrator() {
		if (demonstrator == null
				|| CinemaThread.paused)
			return;

		btChangeDemonstrator.setDisable(false);
		demonstrator.pauseDemonstrator();
		for (Timeline fanTimeline : blinkingFansTimelines.values()) {
			fanTimeline.pause();
		}

		for (Timeline timeline : blinkingDemonstratorTimelines.values()) {
			timeline.pause();
		}
	}

	public void updateFanStatus() {
		Platform.runLater(() -> {
			blinkingFansTimelines.clear();
			isToBlinkingFansTimelines.clear();
			gpFanStatus.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null
					&& GridPane.getColumnIndex(node) > 0);
			// Remove apenas labels dinâmicas (não apaga as fixas da col 0)
			fansLabels.clear();
			Map<CinemaThread.FanStatus, Integer> statusRowMap = Map.of(
					CinemaThread.FanStatus.NA_FILA, 0,
					CinemaThread.FanStatus.VOLTANDO_DO_LANCHE, 0,
					CinemaThread.FanStatus.ESPERANDO_O_FILME, 1,
					CinemaThread.FanStatus.LANCHANDO, 2,
					CinemaThread.FanStatus.SAIU_DA_SALA, 2,
					CinemaThread.FanStatus.ASSISTINDO, 3);

			// Contadores de colunas para cada row (começando na coluna 1)
			int[] colCounters = new int[] { 1, 1, 1, 1 };

			for (CinemaThread.Fan fan : fans) {
				Label fanLabel = new Label(fan.getNameThread());
				
				fansLabels.add(fanLabel);
				
				// Estilo conforme status
				String style = "-fx-padding: 5px;";
				List<Color> colors = Arrays.asList(Color.YELLOW, Color.GREEN, Color.ORANGE, Color.GREY);
				if (CinemaThread.FanStatus.NA_FILA.equals(fan.getStatus()) ||
						CinemaThread.FanStatus.VOLTANDO_DO_LANCHE.equals(fan.getStatus())) {
					style += "-fx-background-color: yellow;";
					isToBlinkingFansTimelines.put(fanLabel, false);
				} else if (CinemaThread.FanStatus.ESPERANDO_O_FILME.equals(fan.getStatus())) {
					style += "-fx-background-color: green; -fx-text-fill: white;";
					isToBlinkingFansTimelines.put(fanLabel, false);
				} else if (CinemaThread.FanStatus.LANCHANDO.equals(fan.getStatus())
						|| CinemaThread.FanStatus.SAIU_DA_SALA.equals(fan.getStatus())) {
					style += "-fx-background-color: orange;";
					isToBlinkingFansTimelines.put(fanLabel, true);
				} else if (CinemaThread.FanStatus.ASSISTINDO.equals(fan.getStatus())) {
					style += "-fx-background-color: grey;";
					isToBlinkingFansTimelines.put(fanLabel, true);
				}

				fanLabel.setStyle(style);

				// Determina row conforme status
				Integer row = statusRowMap.get(fan.getStatus());
				if (row != null) {
					int col = colCounters[row]++;
					GridPane.setHgrow(fanLabel, Priority.ALWAYS);
					fanLabel.setAlignment(Pos.CENTER);
					makeLabelBlink(fanLabel, fan, colors, Duration.seconds((double) fan.getBreakTimeThread() / 20),
							Duration.seconds((double) CinemaThread.exhibitionTime.get() / 60));
					gpFanStatus.add(fanLabel, col, row);
				}
				
			}
			for (Label fanLabel : fansLabels) {
				if (isToBlinkingFansTimelines.get(fanLabel))
					blinkingFansTimelines.get(fanLabel).play();
				else
					blinkingFansTimelines.get(fanLabel).stop();
			}
		});
	}

	private void makeLabelBlink(Label label, CinemaThread.Fan fan, List<Color> color, Duration breakTime,
			Duration watching) {
		int colorIndex;
		Duration duration = Duration.seconds(0.5);

		switch (fan.getStatus()) {
			case NA_FILA:
			case VOLTANDO_DO_LANCHE:
				colorIndex = 0;
				break;
			case ESPERANDO_O_FILME:
				colorIndex = 1;
				break;
			case LANCHANDO:
			case SAIU_DA_SALA:
				duration = breakTime;
				colorIndex = 2;
				break;
			case ASSISTINDO:
				duration = watching;
				colorIndex = 3;
				break;
			default:
				colorIndex = 0;
				break;
		}

		int finalColorIndex = Math.min(colorIndex, color.size() - 1);
		Color targetColor = color.get(finalColorIndex);

		// Se já houver uma animação, parar antes de criar nova
		Timeline existingTimeline = blinkingFansTimelines.get(label);
		if (existingTimeline != null) {
			existingTimeline.stop();
		}

		Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO,
						e -> label.setStyle("-fx-background-color: " + toRgbString(Color.WHITE) + ";")),
				new KeyFrame(duration,
						e -> label.setStyle("-fx-background-color: " + toRgbString(targetColor) + ";")));

		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.setAutoReverse(true);
		// Armazenar a nova animação
		blinkingFansTimelines.put(label, timeline);
	}

	private void makeLabelBlink(CinemaThread.Demonstrator demonstrator, List<Color> color) {
		int colorIndex;
		Duration duration = Duration.seconds(0.5);

		switch (demonstrator.getStatus()) {
			case ESPERANDO_FANS:
				colorIndex = 0;
				break;
			case EXIBINDO_FILME:
				colorIndex = 1;
				break;
			case EXIBICAO_ENCERRADA:
				colorIndex = 2;
				break;
			default:
				colorIndex = 0;
				break;
		}

		int finalColorIndex = Math.min(colorIndex, color.size() - 1);
		Color targetColor = color.get(finalColorIndex);

		// Se já houver uma animação, parar antes de criar nova
		Timeline existingTimeline = blinkingDemonstratorTimelines.get(lbDemonstratorStatus);
		if (existingTimeline != null) {
			existingTimeline.stop();
		}

		Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO,
						e -> lbDemonstratorStatus.setStyle("-fx-background-color: " + toRgbString(Color.WHITE) + ";")),
				new KeyFrame(duration,
						e -> lbDemonstratorStatus.setStyle("-fx-background-color: " + toRgbString(targetColor) + ";")));

		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.setAutoReverse(true);
		// Armazenar a nova animação
		blinkingDemonstratorTimelines.put(lbDemonstratorStatus, timeline);
	}

	private String toRgbString(Color color) {
		int r = (int) (color.getRed() * 255);
		int g = (int) (color.getGreen() * 255);
		int b = (int) (color.getBlue() * 255);
		return "rgb(" + r + "," + g + "," + b + ")";
	}

	private void resizeGridPane() {
		Platform.runLater(() -> {
			// O total de colunas deve ser o maior número de colunas usadas em qualquer
			// linha
			int maxColumns = fans.stream()
					.collect(Collectors.groupingBy(CinemaThread.Fan::getStatus))
					.values().stream()
					.mapToInt(List::size)
					.max()
					.orElse(0) + 1; // +1 para incluir a coluna 0 fixa
	
			// Ajusta o número de ColumnConstraints
			List<ColumnConstraints> constraints = gpFanStatus.getColumnConstraints();
	
			// Garante que temos constraints suficientes
			while (constraints.size() < maxColumns) {
				ColumnConstraints cc = new ColumnConstraints();
				cc.setHgrow(Priority.ALWAYS);
				constraints.add(cc);
			}
	
			// Ajusta o tamanho das colunas dinâmicas (exceto a 0)
			double availableWidth = gpFanStatus.getWidth() - gpFanStatus.getColumnConstraints().get(0).getPrefWidth();
			double widthPerCol = availableWidth / (maxColumns - 1);
	
			for (int i = 1; i < maxColumns; i++) {
				ColumnConstraints cc = gpFanStatus.getColumnConstraints().get(i);
				cc.setPrefWidth(widthPerCol);
				cc.setMinWidth(widthPerCol);
				cc.setMaxWidth(widthPerCol);
			}
		});
	}

	public void updateDemonstratorStatus() {
		Platform.runLater(() -> {
			if (demonstrator == null) return;
			isToBlinkDemonstratorTimelines.clear();
			lbDemonstratorStatus.setStyle("-fx-background-color: " + toRgbString(Color.GRAY) + ";");
			lbDemonstratorStatus.setText("Demonstrador: " + demonstrator.getStatus());
			makeLabelBlink(demonstrator, Arrays.asList(Color.GRAY, Color.GREEN, Color.YELLOW));
			
			if (CinemaThread.DemonstratorStatus.EXIBINDO_FILME.equals(demonstrator.getStatus())) {
				isToBlinkDemonstratorTimelines.put(lbDemonstratorStatus, true);
			} else if (CinemaThread.DemonstratorStatus.ESPERANDO_FANS.equals(demonstrator.getStatus()) 
					|| CinemaThread.DemonstratorStatus.EXIBICAO_ENCERRADA.equals(demonstrator.getStatus())) {
				isToBlinkDemonstratorTimelines.put(lbDemonstratorStatus, false);
			}

			if (isToBlinkDemonstratorTimelines.get(lbDemonstratorStatus))
				blinkingDemonstratorTimelines.get(lbDemonstratorStatus).play();
			else
				blinkingDemonstratorTimelines.get(lbDemonstratorStatus).stop();
		});
	}
	
	public void pauseAnimationFan() {
		for (Timeline fanTimeline : blinkingFansTimelines.values()) {
			if (Animation.Status.RUNNING.equals(fanTimeline.getStatus())) {
				fanTimeline.stop();
			}
		}
	}

	public void startAnimationFan() {
		for (Timeline fanTimeline : blinkingFansTimelines.values()) {
			if (Animation.Status.STOPPED.equals(fanTimeline.getStatus())) {
				fanTimeline.play();
			}
		}
	}

	public void pauseAnimationDemonstrator() {
		for (Timeline demonstratorTimeLine : blinkingDemonstratorTimelines.values()) {
			if (Animation.Status.RUNNING.equals(demonstratorTimeLine.getStatus())) {
				demonstratorTimeLine.stop();
			}
		}
	}

	public void startAnimationDemonstrator() {
		for (Timeline demonstratorTimeLine : blinkingDemonstratorTimelines.values()) {
			if (Animation.Status.STOPPED.equals(demonstratorTimeLine.getStatus())) {
				demonstratorTimeLine.play();
			}
		}
	}
}