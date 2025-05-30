package org.einstein.controllers;

import com.sun.javafx.property.adapter.PropertyDescriptor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.einstein.cinema.CinemaThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	
	private static MainController mainController;

	private final List<CinemaThread.Fan> fans = new ArrayList<>();
	private CinemaThread.Demonstrator demonstrator;
	private Stage stage;

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
		btRemoveFan.setOnAction(e -> handleRemoveFan());
		btChangeDemonstrator.setOnAction(e -> handleConfigDemonstrator());
		btStartDemonstrator.setOnAction(e -> handleStartDemonstrator());
		btPauseDemonstrator.setOnAction(e -> handlePauseDemonstrator());
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
			configDemonstratorStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleStartDemonstrator() {
		if (demonstrator == null) return;

		btChangeDemonstrator.setDisable(true);
		demonstrator.startDemonstrator();
	}
	
	private void handlePauseDemonstrator() {
		if (demonstrator == null) return;

		btChangeDemonstrator.setDisable(false);
		demonstrator.pauseDemonstrator();
	}
}