package org.einstein;

import javafx.application.Application;
import javafx.stage.Stage;
import org.einstein.cinema.CinemaThread;
import org.einstein.cinema.Test;
import org.einstein.controllers.ScreenController;
import org.einstein.views.MainView;

public class Main extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		ScreenController controller = new ScreenController();
		controller.start(MainView.show());
		CinemaThread.Demonstrator demonstrator = new CinemaThread.Demonstrator();
		demonstrator.start();
	}
}