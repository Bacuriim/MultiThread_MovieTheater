package org.einstein;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.einstein.cinema.CinemaThread;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CinemaView.fxml"));
		Parent root = loader.load();

		primaryStage.setTitle("Cinema");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		primaryStage.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});

		// Inicia demonstrador depois que a tela principal estiver visível.
		CinemaThread.Demonstrator demonstrator = new CinemaThread.Demonstrator();
		demonstrator.start();
	}

	public static void main(String[] args) {
		launch(args);
	}
}