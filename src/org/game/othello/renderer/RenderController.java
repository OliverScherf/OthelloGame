package org.game.othello.renderer;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.GameController;
import org.game.othello.interfaces.Loggable;
import org.game.othello.interfaces.OthelloConstants;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The Class RenderController holds different renderer.
 * It also manages the different scenes.
 * @author Oliver Scherf
 *
 */
public class RenderController implements OthelloConstants, Loggable {
 	
	private Logger logger;
	private Stage stage;
	private GameBoardRenderer gameBoardRenderer;
	private GameStatsRenderer gameStatsRenderer;
	private MessageRenderer messageRenderer;
	@SuppressWarnings("unused")
	private Scene othelloGameScene;
	private GridPane othelloGamePane;
	private GridPane othelloMenuPane;

	/**
	 * Instantiate a new RenderController.
	 * This will start the menu first.
	 * @param stage The JavaFX stage.
	 */
	public RenderController(Stage stage) {
		this.initializeLogging();
		this.stage = stage;
		this.stage.getIcons().add(new Image(this.getClass().getResourceAsStream("resources/othelloIcon.png")));
		this.log(INFO, "RenderController was initialized.");
	}
	

	/**
	 * Displays the menu.
	 */
	public void startMenu() {
		this.othelloMenuPane = this.initializeMenuPane();
		this.initializeMenuScene(this.stage);
		this.initializeOthelloLogo();
		this.initiazeMenuButtons();
		this.log(INFO, "Menu was started");
		
	}

	/**
	 * Displays the logo.
	 */
	private void initializeOthelloLogo() {
		Image logo = new Image(this.getClass().getResourceAsStream("resources/othello.png"));
		ImageView i = new ImageView();
		i.setImage(logo);
		GridPane.setHalignment(i, HPos.CENTER);
		this.othelloMenuPane.add(i, 0, 0);
	}


	/**
	 * Adds 3 Buttons to the menu.
	 */
	private void initiazeMenuButtons() {
		Button startOfflineBtn = new Button("Offline Game");
		Button startCreateLobbyBtn = new Button("Online Game: Create Lobby");
		Button startJoinLobbyBtn = new Button("Online Game: Join Lobby");
		Button helpBtn = new Button("Help");
		Button quitBtn = new Button("Quit");
		int width = 200;
		int height = 30;
		startOfflineBtn.setPrefSize(width, height);
		startCreateLobbyBtn.setPrefSize(width, height);
		startJoinLobbyBtn.setPrefSize(width, height);
		helpBtn.setPrefSize(width, height);
		quitBtn.setPrefSize(width, height);
		startOfflineBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				GameController.getSingleton().startOfflineMultiplayer();
			}
		});
		startCreateLobbyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				GameController.getSingleton().startOnlineMultiplayer(-1);
			}
		});
		startJoinLobbyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TextInputDialog dialog = new TextInputDialog("");
				dialog.setTitle("Enter lobby number");
				dialog.setHeaderText("Please enter the number of the lobby you want to join.");
				dialog.setContentText("Lobby Number:");
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()){
					if (RenderController.this.validateLobbyNumber(result.get())) {
						GameController.getSingleton().startOnlineMultiplayer(Integer.valueOf(result.get()));
					} else {
						Alert error = new Alert(AlertType.INFORMATION);
						error.setTitle("Incorrect lobby number.");
						error.setHeaderText("Please verify there is an active lobby with that number.");
						error.setContentText("The lobby number was incorrect, make sure the lobby exists.\nYou can also create a new lobby.");
						error.showAndWait();
					}
				}
			}
		});
		quitBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				GameController.getSingleton().quitGame();
			}
		});
		helpBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Alert helpAlert = new Alert(AlertType.INFORMATION);
				helpAlert.setResizable(true);
				helpAlert.getDialogPane().setPrefSize(400.0, 300.0);
				helpAlert.setTitle("Help");
				helpAlert.setHeaderText("How to play Othello:");
				helpAlert.setContentText("You can either start the game in offline - or online multiplayer mode. For online multiplayer mode you need to"
						+ "create or join a lobby. When you create a lobby, tell your opponent your lobby number. You find the lobby number "
						+ "on the right side when you're on the game screen. He just needs to write the number into the small textfield below"
						+ "the \"Online Game: Join Lobby\""
						+ "\n\nIf you don't know the rules of Othello, see here: https://en.wikipedia.org/wiki/Reversi#Rules");
				helpAlert.showAndWait();
			}
		});
		VBox buttonsBox = new VBox();
		buttonsBox.setSpacing(4.0);
		buttonsBox.setAlignment(Pos.CENTER);
		this.othelloMenuPane.setAlignment(Pos.CENTER);
		buttonsBox.getChildren().addAll(startOfflineBtn, startCreateLobbyBtn, startJoinLobbyBtn, helpBtn, quitBtn);
		this.othelloMenuPane.add(buttonsBox, 0, 1);
		this.log(FINE, "Initialized the Menu buttons.");
	}
	
	/**
	 * Simply prints a message, that no lobby was found with this number.
	 */
	public void noLobbyFound() {
		this.messageRenderer.printInfoMessage("No lobby with this number found! Please return to the menu.");
	}
	
	/**
	 * A lobby number is always between the range 100-999.
	 * @param text from the lobby Textfield
	 * @return true, if the number is between 100 and 999.
	 */
	private boolean validateLobbyNumber(String text) {
		return text.length() == 3 && text.matches("[0-9]*") && Integer.valueOf(text).intValue() > 99;
	}

	/**
	 * Initializes menu scene
	 * @param stage the JavaFX stage 
	 * @return menu scene
	 */
	private Scene initializeMenuScene(Stage stage) {
		Scene gameScene = new Scene(this.othelloMenuPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		stage.setScene(gameScene);
		stage.setTitle("Othello v1.0");
		stage.setResizable(false);
		stage.show();
		this.log(FINE, "The Stage was initialized.");
		return gameScene;
	}

	/**
	 * Initializes Menu Pane
	 * @return Menu Pane
	 */
	private GridPane initializeMenuPane() {
		GridPane menuPane = new GridPane();
		menuPane.getRowConstraints().add(new RowConstraints(300));
		menuPane.getRowConstraints().add(new RowConstraints(WINDOW_HEIGHT / 4));
		menuPane.getColumnConstraints().add(new ColumnConstraints(WINDOW_WIDTH));
		this.log(FINER, "Menu Pane was initialized.");
		return menuPane;
	}

	/**
	 * Starts the GameScene.
	 */
	public void startGameScene() {
		this.othelloGamePane = this.initializeGamePane();
		this.othelloGameScene = this.initializeGameScene();
		this.initializeGameboardRenderer();
		this.initializeGamestatsRenderer();
		this.initializeQuitButton();
		this.initializeMessageRenderer();
		this.update();
		this.log(FINE, "Started the game scene.");
	}

	/**
	 * Adds the messagebox and the chatfield to the pane.
	 */
	private void initializeMessageRenderer() {
		this.messageRenderer = new MessageRenderer();
		GridPane.setColumnSpan(this.messageRenderer.getMessageBox(), 4);
		this.othelloGamePane.add(this.messageRenderer.getMessageBox(), 1, 3);
		this.log(FINE, "The Message renderer was initialized.");
	}
	
	/**
	 * Creates and initialize the quit button.
	 */
	private void initializeQuitButton() {
		VBox quitBtnBox = new VBox();
		Button quit = new Button("Quit");
		quit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				GameController.getSingleton().quitGame();
			}
		});
		quitBtnBox.getChildren().add(quit);
		quitBtnBox.setAlignment(Pos.BASELINE_RIGHT);
		this.othelloGamePane.add(quitBtnBox, 1, 4);
		GridPane.setColumnIndex(quitBtnBox, 3);
	}
	
	
	/**
	 * Adds the gameboard to the pane.
	 */
	private void initializeGameboardRenderer() {
		this.gameBoardRenderer = new GameBoardRenderer();
		this.othelloGamePane.add(this.gameBoardRenderer.getCanvas(), 1, 1);
		this.log(FINE, "The Gameboard renderer was initialized.");
	}

	/**
	 * Creates the windows, defines the widht and height.
	 * @return the Scene of the application.
	 */
	private Scene initializeGameScene() {
		Scene gameScene = new Scene(this.othelloGamePane, WINDOW_WIDTH, WINDOW_HEIGHT);
		this.stage.setScene(gameScene);
		this.stage.setTitle("Othello v1.0");
		this.stage.setResizable(false);
		this.stage.show();
		this.log(FINE, "The stage was initialized.");
		return gameScene;
	}

	/**
	 * Builds the grid of the gridpane.
	 * @return the gamepane.
	 */
	private GridPane initializeGamePane() {
		GridPane pane = new GridPane();
		// HEIGHT
		pane.getRowConstraints().add(new RowConstraints(GAMEBOARD_BORDER_PADDING));				
		pane.getRowConstraints().add(new RowConstraints(CANVAS_GAMEBOARD_HEIGHT));				
		pane.getRowConstraints().add(new RowConstraints(CANVAS_SPACING_GAMEBOARD_MESSAGEAREA)); 
		pane.getRowConstraints().add(new RowConstraints(CANVAS_MESSAGEAREA_HEIGHT));			
		pane.getRowConstraints().add(new RowConstraints(GAMEBOARD_BORDER_PADDING)); 			
		// WIDTH
		pane.getColumnConstraints().add(new ColumnConstraints(GAMEBOARD_BORDER_PADDING));
		pane.getColumnConstraints().add(new ColumnConstraints(CANVAS_GAMEBOARD_WIDTH));
		pane.getColumnConstraints().add(new ColumnConstraints(CANVAS_SPACING_GAMEBOARD_GAMESTATS));
		pane.getColumnConstraints().add(new ColumnConstraints(CANVAS_GAMESTATS_WIDTH));
		pane.getColumnConstraints().add(new ColumnConstraints(GAMEBOARD_BORDER_PADDING)); 
		
		this.log(FINE, "The Gridpane was initialized.");
		return pane;
	}
	
	/**
	 * Redraw the GameBoard and GameBoard.
	 */
	public void update() {
		this.gameBoardRenderer.update();
		this.gameStatsRenderer.updateGameStats();
		if (!GameController.getSingleton().getGameLogic().isOfflineMode()) {
			this.gameStatsRenderer.drawOnlineMultiplayerInfo();
		}
	}
	
	/**
	 * Adds the gamestats to the pane.
	 */
	private void initializeGamestatsRenderer() {
		this.gameStatsRenderer = new GameStatsRenderer();
		this.othelloGamePane.add(this.gameStatsRenderer.getGamestatsVBox(), 3, 1);
		GridPane.setValignment(this.gameStatsRenderer.getGamestatsVBox(), VPos.TOP);
		this.log(FINE, "The Gamestats Renderer was initialized.");
	}

	public MessageRenderer getMessageRender() {
		return this.messageRenderer;
	}
	
	@Override
	public void log(Level level, String msg) {
		this.logger.log(level, msg);
	}

	@Override
	public void err(String msg, Exception e) {
		this.logger.log(ERROR, msg, e);
	}

	@Override
	public void initializeLogging() {
		this.logger = Logger.getLogger("Log.RenderController");
		this.logger.setLevel(null);
		this.log(FINE, "Initialized RenderController Logger");
	}
}
