package org.einstein.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.einstein.cinema.CinemaThread;

import java.util.List;

public class ConfigDemonstratorController {
	@FXML
	private Pane pane;

	@FXML
	private VBox vbMain;

	@FXML
	private TextField tfExbTime;

	@FXML
	private Spinner<Integer> spCapacity;

	@FXML
	private Button btConfigDemonstrator;
	
	private Button btOtherConfigDemonstrator;

	private CinemaThread.Demonstrator demonstrator = new CinemaThread.Demonstrator(30 ,5);
	private Stage stage;

	public CinemaThread.Demonstrator getDemonstrator() {
		return demonstrator;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void initialize() {
		setSpinners(spCapacity, 1, 10, 1, true);
		btConfigDemonstrator.setOnAction(e -> handleConfigureDemonstrator());
	}

	private void handleConfigureDemonstrator() {
		int exbTime = 30;
		try {
			exbTime = Integer.parseInt(tfExbTime.getText());
		} catch (NumberFormatException e) {
			 tfExbTime.setText("Insira um numero em segundos");
		}
		int capacity = spCapacity.getValue();

		if (exbTime > 0 && capacity > 0) {
			if (demonstrator == null) {
				demonstrator = new CinemaThread.Demonstrator(exbTime, capacity);
				demonstrator.start();
			} else {
				demonstrator.setCapacity(capacity);
				demonstrator.setExhibitionTime(exbTime);
			}
			System.out.println("Demonstrador configurado: " + "Capacidade: " + capacity + " com tempo de exibicao: " + exbTime);
		} else {
			System.out.println("Campos obrigatórios não preenchidos.");
		}
	}

	public void setSpinners(Spinner<Integer> spinner, int minValue, int maxValue, int defaultValue, boolean isEditable) {
		spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, defaultValue));
		spinner.setEditable(isEditable);

		spinner.getEditor().setTextFormatter(new TextFormatter<>(change ->
				change.getControlNewText().matches("\\d*") ? change : null
		));

		spinner.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue) {
				commitEditorText(spinner);
				try {
					if (spinner.getValueFactory().getValue() < minValue) spinner.getValueFactory().setValue(minValue);
					if (spinner.getValueFactory().getValue() > maxValue) spinner.getValueFactory().setValue(maxValue);
				} catch (NumberFormatException e) {
					spinner.getValueFactory().setValue(defaultValue);
				}
			}
		});
	}

	private void commitEditorText(Spinner<Integer> spinner) {
		if (!spinner.isEditable()) return;
		String text = spinner.getEditor().getText();
		SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
		if (valueFactory != null) {
			try {
				int enteredValue = Integer.parseInt(text);
				valueFactory.setValue(enteredValue);
			} catch (NumberFormatException e) {
				spinner.getEditor().setText(String.valueOf(valueFactory.getValue()));
			}
		}
	}
}
