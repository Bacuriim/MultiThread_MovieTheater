package org.einstein;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.einstein.cinema.CinemaThread;
import org.einstein.controllers.MainController;

import java.util.Objects;

public class Main extends Application {

	MainController mainController;
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CinemaView.fxml"));
		Parent root = loader.load();
		
		primaryStage.setTitle("Cinema");
		primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cinema.png"))));
		primaryStage.setScene(new Scene(root));
		primaryStage.setWidth(1200);
		primaryStage.setHeight(600);
		primaryStage.show();
		primaryStage.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});
		MainController.setInstance(loader.getController());
		mainController = MainController.getInstance();
		mainController.setStage(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}