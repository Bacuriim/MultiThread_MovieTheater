package org.einstein.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.einstein.cinema.CinemaThread;

import java.util.List;

public class CreateFanController {

	@FXML
	private TextField tfFanName;

	@FXML
	private TextField tfBreakTime;

	@FXML
	private Button btCreateFan;

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
		btCreateFan.setOnAction(e -> handleCreateFan());
	}

	private void handleCreateFan() {
		String name = tfFanName.getText();
		String breakTimeStr = tfBreakTime.getText();

		if ((!name.isEmpty() && !breakTimeStr.isEmpty())) {
			if (fans.stream().anyMatch(fan -> fan.getNameThread().equals(name))) return;
			try {
				int breakTime = Integer.parseInt(breakTimeStr);
				CinemaThread.Fan fan = new CinemaThread.Fan(name, breakTime, MainController.getInstance());
				fans.add(fan);
				fan.start();
				System.out.println("Fan criado: " + name + " com break time: " + breakTime);
			} catch (NumberFormatException ex) {
				System.out.println("Break time inválido!");
			}
		} else {
			System.out.println("Campos obrigatórios não preenchidos.");
		}
	}
}