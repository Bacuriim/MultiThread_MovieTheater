package org.einstein.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.einstein.cinema.CinemaThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainView {

	@FXML
	private Pane paneMain;

	@FXML
	private VBox vbMain;

	@FXML
	private Pane paneVisual;

	@FXML
	private Button btAddFan;

	@FXML
	private Button btRemoveFan;

	private final List<CinemaThread.Fan> fans = new ArrayList<>();

	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void initialize() {
		btAddFan.setOnAction(e -> handleAddFan());
		btRemoveFan.setOnAction(e -> handleRemoveFan());
	}

	private void handleAddFan() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreateFanView.fxml"));
			Parent root = loader.load();

			CreateFanView controller = loader.getController();
			controller.initData(fans); // Passa a lista de fãs

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