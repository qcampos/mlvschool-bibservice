package application;

import static application.utils.Constants.SF_DISPLAY_LIGHT;
import static application.utils.Constants.SF_DISPLAY_REGULAR;
import static application.utils.Constants.SF_DISPLAY_THIN;
import static application.utils.Constants.SF_DISPLAY_ULTRALIGHT;
import static application.utils.Constants.SF_TEXT_LIGHT;
import static application.utils.Constants.SF_TEXT_MEDIUM;
import static application.utils.Constants.SF_TEXT_REGULAR;
import static application.utils.Constants.SF_TEXT_SEMIBOLD;

import java.io.IOException;

import application.utils.Constants;
import application.utils.FontManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			// Loading custom resources.
			loadFonts();
			// Loading the connection screen.
			Pane screen = loadScreen(Constants.CONNECTION_SCREEN_MODULE);
			Scene scene = new Scene(screen, 1200, 800);
			loadCSS(scene, "/css/florange.css");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			System.err.println("Cannot load the scene : " + e.getCause() + '\n' + e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	private void loadCSS(Scene scene, String cssPath) {
		scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
	}

	private Pane loadScreen(String screenPath) throws IOException {
		return FXMLLoader.load(getClass().getResource(screenPath));
	}

	/**
	 * Workaround for JavaFX SDK 8 : jdk8.0_u65 has a bug in exported font
	 * management.
	 */
	private void loadFonts() {
		FontManager fontManager = FontManager.getInstance();
		fontManager.register(SF_DISPLAY_REGULAR, 32);
		fontManager.register(SF_DISPLAY_REGULAR, 110);
		fontManager.register(SF_DISPLAY_LIGHT, 18);
		fontManager.register(SF_DISPLAY_LIGHT, 24);
		fontManager.register(SF_DISPLAY_LIGHT, 30);
		fontManager.register(SF_DISPLAY_ULTRALIGHT, 24);
		fontManager.register(SF_DISPLAY_THIN, 28);
		fontManager.register(SF_TEXT_SEMIBOLD, 18);
		fontManager.register(SF_TEXT_SEMIBOLD, 24);
		fontManager.register(SF_TEXT_SEMIBOLD, 36);
		fontManager.register(SF_TEXT_SEMIBOLD, 52);
		fontManager.register(SF_TEXT_REGULAR, 18);
		fontManager.register(SF_TEXT_REGULAR, 20);
		fontManager.register(SF_TEXT_REGULAR, 24);
		fontManager.register(SF_TEXT_REGULAR, 28);
		fontManager.register(SF_TEXT_MEDIUM, 20);
		fontManager.register(SF_TEXT_LIGHT, 18);
		fontManager.register(SF_TEXT_LIGHT, 24);
	}

}
