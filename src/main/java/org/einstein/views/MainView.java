package org.einstein.views;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.einstein.cinema.CinemaThread;

import java.util.ArrayList;

public class MainView {
	
	static int i = 1;
	
	static ArrayList<CinemaThread.Fan> fans = new ArrayList<>();
	
	public static Stage show() {
		Stage window = new Stage();
		Label label = new Label("Olá, JavaFX no Java 17!");
		Scene scene = new Scene(label, 300, 100);
		
		Button btAddFans = new Button("Add Fans");
		Button btRemoveFans = new Button("Remove Fans");
		
		btAddFans.setOnAction(e -> {
			fans.add(new CinemaThread.Fan(i, i));
			fans.get(i - 1).start();
			i++;
		});
		
		btRemoveFans.setOnAction(e -> {
			if (fans.isEmpty())
				System.out.println("Fans is empty");
			else {
				fans.get(fans.size() - 1).terminate();
				fans.remove(fans.size() - 1);
				i--;
			}
		});

		TitledPane titledPane = new TitledPane();
		AnchorPane anchorPane = new AnchorPane();
		titledPane.setContent(anchorPane);
		anchorPane.getChildren().add(btAddFans);
		anchorPane.getChildren().add(btRemoveFans);
		btRemoveFans.setLayoutY(btAddFans.getLayoutY() + 40);
		scene.setRoot(titledPane);
		
		window.setScene(scene);
		window.setTitle("JavaFX + Java 17");
		return window;
	}
	
}
