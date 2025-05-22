package org.einstein.views;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.einstein.cinema.CinemaThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainView {

	@FXML
	protected Pane paneMain;

	@FXML
	protected VBox vbMain;

	@FXML
	protected Pane paneVisual;

	@FXML
	protected GridPane gridPane;

	@FXML
	protected Button btAddFan;

	@FXML
	protected Button btRemoveFan;
	
	protected TextField tfFanName = new TextField();
	protected TextField tfBreakTime = new TextField();
	
	static int i = 1;
	
	static ArrayList<CinemaThread.Fan> fans = new ArrayList<>();
	
	public void initialize() {
		btAddFan.setOnAction(e -> {
			try {
				createFan();
			} catch (IOException ignored) {
			}
			if (!tfFanName.getText().isEmpty() && !tfBreakTime.getText().isEmpty()) {
				fans.add(new CinemaThread.Fan(tfFanName.getText(), Integer.parseInt(tfBreakTime.getText())));
				fans.get(i - 1).start();
				i++;
			}
		});

		btRemoveFan.setOnAction(e -> {
			if (fans.isEmpty()) {
				System.out.println("Fans is empty");
			}
			else if (!tfFanName.getText().isEmpty()){
				fans.get(fans.size() - 1).terminate();
				fans.remove(fans.size() - 1);
				i--;
			}
		});
	}
	
	public static Stage show() throws IOException {
		Parent root = FXMLLoader.load(Objects.requireNonNull(MainView.class.getResource("/views/CinemaView.fxml")));
		Scene scene = new Scene(root);
		Stage window = new Stage();
		window.setScene(scene);
		window.setTitle("Cinema");
		return window;
	}
	
	public static void createFan() throws IOException {
		Parent root = FXMLLoader.load(Objects.requireNonNull(MainView.class.getResource("/views/CreateFanView.fxml")));
		Stage window = new Stage();
		Scene scene = new Scene(root, 200, 200);
		window.setScene(scene);
		window.setTitle("Criar Fan");

		TextField tfFanName = new TextField();
		tfFanName.setPromptText("Nome do Fan");

		TextField tfBreakTime = new TextField();
		tfBreakTime.setPromptText("Tempo de lanche do fan em segundos");

		Label lbFanName = new Label("Nome do Fan");
		Label lbBreakTime = new Label("Tempo de lanche do fan");

		VBox vbFan = new VBox();
		HBox hbFanName = new HBox();
		hbFanName.getChildren().addAll(tfFanName, lbFanName);
		HBox hbBreakTime = new HBox();
		hbBreakTime.getChildren().addAll(tfBreakTime, lbBreakTime);
		vbFan.getChildren().add(hbFanName);
		vbFan.getChildren().add(hbBreakTime);
		
		window.show();
		
		window.setOnCloseRequest(e -> {
			window.hide();
		});
	}
	
}
