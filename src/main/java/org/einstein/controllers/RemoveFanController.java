package org.einstein.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.einstein.cinema.CinemaThread;

import javax.print.attribute.standard.MediaSize;
import java.util.List;
import java.util.Optional;

public class RemoveFanController {
	
	@FXML
	private Pane pane;

	@FXML
	private VBox vbMain;

	@FXML
	private TextField tfFanName;

	@FXML
	private Button btRemoveFan;

	private List<CinemaThread.Fan> fans;
	private Stage stage;

	public void initData(List<CinemaThread.Fan> fans) {
		this.fans = fans;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void initialize() {
		btRemoveFan.setOnAction(e -> handleRemoveFan());
	}

	private void handleRemoveFan() {
		String name = tfFanName.getText();

		if (fans.isEmpty()) {
			System.out.println("Fans esta vazio");
		} else if (!name.isEmpty() && fans.stream().anyMatch(fan -> fan.getNameThread().equals(name))) {
			CinemaThread.Fan fan = fans.stream().filter(fanToRemove -> fanToRemove.getNameThread().equals(name)).findFirst().orElse(null);
			if (fan != null) {
				fans.remove(fan);
				fan.terminate();
				System.out.println("Fan " + name + " removido com sucesso");
			} else {
				System.out.println("Fan " + name + " nao encontrado");
			}
		} else {
			System.out.println("O fan: " + name + " não existe!");
		}
	}
}
