package application;

import static application.utils.Constants.SF_DISPLAY_LIGHT;
import static application.utils.Constants.SF_DISPLAY_REGULAR;
import static application.utils.Constants.SF_DISPLAY_THIN;
import static application.utils.Constants.SF_DISPLAY_ULTRALIGHT;
import static application.utils.Constants.SF_TEXT_LIGHT;
import static application.utils.Constants.SF_TEXT_MEDIUM;
import static application.utils.Constants.SF_TEXT_REGULAR;
import static application.utils.Constants.SF_TEXT_SEMIBOLD;

import application.controllers.ModuleLoader;
import application.controllers.Screen;
import application.controllers.connection_screen.ConnectionScreen;
import application.controllers.connection_screen.LogInModule;
import application.controllers.connection_screen.SignUpModule;
import application.controllers.home_screen.AddBookModule;
import application.controllers.home_screen.BookSpinerModule;
import application.controllers.home_screen.BurgerMenuModule;
import application.controllers.home_screen.CategoryDescriptionModule;
import application.controllers.home_screen.HomeScreen;
import application.controllers.home_screen.SearchModule;
import application.controllers.research_screen.BookEntryModule;
import application.controllers.research_screen.ResearchScreen;
import application.model.ProxyModel;
import application.utils.Animations;
import application.utils.Constants;
import application.utils.FontManager;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main JAVAFX client application. It has to extend {@link Application} to be
 * conform the the JAVAFX cycle activity.
 * 
 * @author Jefferson
 *
 */
public class ClientMLVSchool extends Application {

	public static void main(String[] args) {
		String codebase = "file:///Users/Baxtalou/Documents/Master2/REST2/ProjetRMIREST/bilious-octoprune/Server/src/";
		System.setProperty("java.security.policy", Constants.SECURITY_POLICY_PATH);
		System.setProperty("java.rmi.server.codebase", codebase);
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		loadFonts();
		loadModules();
		launch(args);
	}

	/**
	 * The Application is a "stand alone" singleton. According to the JAVAFX
	 * cycle activity.
	 */
	private static ClientMLVSchool INSTANCE;
	private ProxyModel proxyModel;
	private Scene scene;
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		// Loading the JavaFX connection screen.
		ConnectionScreen connectionScreen = loadConnectionScreen();
		scene = new Scene(connectionScreen.getView(), 1200, 800);
		proxyModel = new ProxyModel();
		connectionScreen.setModel(proxyModel);
		connectionScreen.startCient();
		loadCSS(scene, "/css/florange.css"); // Custom CSS style sheet.
		loadCSS(scene, "/css/combobox.css"); // Custom CSS style sheet.
		primaryStage.setScene(scene);
		primaryStage.show();

		// Setting the first screen to start.
		INSTANCE = this;
	}

	public static ClientMLVSchool getINSTANCE() {
		return INSTANCE;
	}

	public Stage getStage() {
		return primaryStage;
	}

	/**
	 * Loads the application's first screen. And resets the model.
	 */
	public static void reloadApplicationFirstScreen() {
		ConnectionScreen connectionScreen = loadConnectionScreen();
		Scene scene = INSTANCE.getScene();
		scene.setRoot(connectionScreen.getView());
		INSTANCE.proxyModel = new ProxyModel();
		connectionScreen.setModel(INSTANCE.proxyModel);
		connectionScreen.startCient();
	}

	/**
	 * Loads every JavaFX Modules.
	 */
	private static void loadModules() {
		ModuleLoader loader = ModuleLoader.getInstance();
		loader.registerFXMLLoader(ConnectionScreen.class, Constants.CONNECTION_SCREEN_MODULE);
		loader.registerFXMLLoader(SignUpModule.class, Constants.CONNECTION_SIGNUP_MODULE);
		loader.registerFXMLLoader(LogInModule.class, Constants.CONNECTION_LOGIN_MODULE);
		loader.registerFXMLLoader(HomeScreen.class, Constants.HOME_SCREEN_MODULE);
		loader.registerFXMLLoader(BurgerMenuModule.class, Constants.HOME_BURGER_MENU_MODULE);
		loader.registerFXMLLoader(AddBookModule.class, Constants.HOME_ADD_BOOK_MODULE);
		loader.registerFXMLLoader(CategoryDescriptionModule.class, Constants.HOME_CATEGORY_MODULE);
		loader.registerFXMLLoader(SearchModule.class, Constants.HOME_SEARCH_MODULE);
		loader.registerFXMLLoader(BookSpinerModule.class, Constants.HOME_SPINER_MODULE);
		loader.registerFXMLLoader(ResearchScreen.class, Constants.RESEARCH_SCREEN);
		loader.registerFXMLLoader(BookEntryModule.class, Constants.RESEARCH_BOOK_ENTRY_MODULE);
	}

	/**
	 * Workaround for JavaFX SDK 8 : jdk8.0_u65 has a bug in exported font
	 * management.
	 */
	private static void loadFonts() {
		FontManager fontManager = FontManager.getInstance();
		fontManager.register(SF_DISPLAY_REGULAR, 32);
		fontManager.register(SF_DISPLAY_REGULAR, 110);
		fontManager.register(SF_DISPLAY_LIGHT, 18);
		fontManager.register(SF_DISPLAY_LIGHT, 24);
		fontManager.register(SF_DISPLAY_LIGHT, 30);
		fontManager.register(SF_DISPLAY_ULTRALIGHT, 18);
		fontManager.register(SF_DISPLAY_ULTRALIGHT, 24);
		fontManager.register(SF_DISPLAY_THIN, 28);
		fontManager.register(SF_TEXT_SEMIBOLD, 18);
		fontManager.register(SF_TEXT_SEMIBOLD, 24);
		fontManager.register(SF_TEXT_SEMIBOLD, 32);
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

	private void loadCSS(Scene scene, String cssPath) {
		scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
	}

	private Scene getScene() {
		return scene;
	}

	private static ConnectionScreen loadConnectionScreen() {
		return ModuleLoader.getInstance().load(ConnectionScreen.class);
	}

	/**
	 * Transitions, after a delay <code>delay</code>, the entire scene from the
	 * current layout, to the new <code>newLayout</code>.
	 */
	public static void setNewScreen(double delay, Screen currentScreen, Screen newScreen) {
		// The root is a VBox of the current and the new layout.
		Pane newView = newScreen.getView();
		Pane currentView = currentScreen.getView();
		VBox root = new VBox();
		Scene scene = INSTANCE.getScene();
		root.getChildren().addAll(newView, currentView);
		root.setTranslateY(-scene.getHeight());

		// Caching the animation for better performances.
		root.setCache(true);
		root.setCacheShape(true);
		root.setCacheHint(CacheHint.SPEED);
		scene.setRoot(root);

		// The translation animation.
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(800), root);
		translateTransition.setInterpolator(Interpolator.EASE_OUT);
		translateTransition.setToY(0);

		// Reseting the newLayout as the only root at the end of the sequence.
		Transition sequence = Animations.delay(delay, translateTransition);
		sequence.setOnFinished(e -> {
			root.getChildren().remove(newView);
			scene.setRoot(newView);
			newScreen.startHasMainScreen();
		});
		sequence.play();
	}

	/**
	 * Change the entire scene from the current layout, to the new
	 * <code>newLayout</code>.
	 */
	public static void setInstantNewScreen(Screen currentScreen, Screen newScreen) {
		Pane newView = newScreen.getView();
		Scene scene = INSTANCE.getScene();
		scene.setRoot(newView);
		newView.setCache(true);
		newView.setCacheShape(true);
		newView.setCacheHint(CacheHint.SPEED);
		newScreen.startHasMainScreen();
	}

	@Override
	public void stop() throws Exception {
		INSTANCE.proxyModel.disconnectUser();
	}
}
