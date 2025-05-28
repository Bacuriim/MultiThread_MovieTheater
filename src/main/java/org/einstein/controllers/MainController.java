package org.einstein.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
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
	public Button btAddFan;

	@FXML
	public Button btRemoveFan;
	
	@FXML
	public TextArea log;
	
	private static MainController mainController;

	private final List<CinemaThread.Fan> fans = new ArrayList<>();
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
		if (fans.isEmpty()) {
			System.out.println("Fans is empty");
		} else {
			CinemaThread.Fan fan = fans.remove(fans.size() - 1);
			fan.terminate();
			System.out.println("Fan removido: " + fan.getName());
		}
	}
}