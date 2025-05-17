package org.einstein.controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.einstein.views.MainView;

public class ScreenController extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		Application app = this;
		app.init();
		stage.show();
	}
	
}
