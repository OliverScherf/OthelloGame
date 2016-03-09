package org.game.othello.renderer;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.GameController;
import org.game.othello.interfaces.Loggable;
import org.game.othello.interfaces.OthelloConstants;
import org.game.othello.rules.GameLogic;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * This class handles the rendering of the Gamestats.
 * @author Oliver Scherf
 */
public class GameStatsRenderer implements OthelloConstants, Loggable {

	private Logger logger;
	private GraphicsContext gcGamestats;
	private GameLogic gameLogic;
	private VBox gamestatsVBox;

	/**
	 * Instantiate a new GameStatsRenderer.
	 */
	public GameStatsRenderer() {
		this.initializeLogging();
		this.gamestatsVBox = new VBox();
		this.gamestatsVBox.setSpacing(122.0);
		this.gameLogic = GameController.getSingleton().getGameLogic();
		this.initializeCanvas();
		this.initializeButtons();
		this.log(FINE, "The GameStatsRenderer was initialized.");
	}

	/**
	 * Initialize the GameStatsCanvas.
	 */
	private void initializeCanvas() {
		Canvas canvasGamestats = new Canvas(CANVAS_GAMESTATS_WIDTH, CANVAS_GAMESTATS_HEIGHT);
		this.gcGamestats = canvasGamestats.getGraphicsContext2D();
		this.gcGamestats.setFont(Font.loadFont(this.getClass().getResourceAsStream("resources/dejavusans.ttf"), 20.0));
		this.gcGamestats.setFill(Color.GREY);
		this.gcGamestats.fillRect(0, 0, CANVAS_GAMESTATS_WIDTH, CANVAS_GAMESTATS_HEIGHT);
		this.gcGamestats.setLineWidth(1f);
		this.gcGamestats.setStroke(Color.BROWN);
		this.gcGamestats.setFill(Color.CHOCOLATE);
		this.gamestatsVBox.getChildren().add(this.gcGamestats.getCanvas());
		this.updateGameStats();
		this.log(FINE, "The Gamestats Canvas was initialized.");
	}
	
	/**
	 * There are three buttons: Surrender, Menu and visit oliverscherf.de
	 */
	private void initializeButtons() {
		Button surrender = new Button("Surrender");
		if (this.gameLogic.isOfflineMode()) {
			surrender.setVisible(false);
		}
		surrender.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				GameController.getSingleton().surrender();
			}
		});
		Button menu = new Button("Menu");
		menu.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				if (GameController.getSingleton().getGameLogic().isOfflineMode() 
						|| GameController.getSingleton().getGameLogic().isGameFinished() 
						|| GameController.getSingleton().getGameLogic().isGameNotStarted()) {
					GameController.getSingleton().getRenderController().startMenu();
					if (!GameController.getSingleton().getGameLogic().isOfflineMode()) {
						GameController.getSingleton().getOthelloClient().closeConnection();
					}
				} else {
					GameController.getSingleton().getRenderController().getMessageRender().printInfoMessage("You can't go to menu now, please surrender first.");
				}
				
			}
		});
		Button oliverscherfde = new Button("Visit oliverscherf.de");
		oliverscherfde.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				try {
					Desktop.getDesktop().browse(new URL("http://www.oliverscherf.de").toURI());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		for (Button btn : Arrays.asList(surrender,menu, oliverscherfde)) {
			btn.setPrefSize(CANVAS_GAMESTATS_WIDTH, 30.0);
		}
		VBox btnBox = new VBox();	
		btnBox.setSpacing(4.0);
		btnBox.getChildren().addAll(surrender, menu, oliverscherfde);
		this.gamestatsVBox.getChildren().add(btnBox);
	}

	/**
	 * Redraws the Content, every change of the conent will lead to a redraw.
	 */
	public void updateGameStats() {
		this.gcGamestats.setFill(Color.GREY);
		this.gcGamestats.fillRect(0.0, 0.0, CANVAS_GAMESTATS_WIDTH, CANVAS_GAMESTATS_HEIGHT);
		int leftPadding = 10;
		this.gcGamestats.setFill(Color.WHITE);
		this.gcGamestats.setFont(new Font(19));
		this.gcGamestats.fillText(this.gameLogic.getCurrentPlayerAsString() + "'s turn", leftPadding, 30);
		this.gcGamestats.setFont(new Font(13));
		this.gcGamestats.fillText("Player  Black:",leftPadding, 60);
		this.gcGamestats.fillText("Player White:", leftPadding+2, 90);
		this.gcGamestats.fillText(String.valueOf(this.gameLogic.getGameFieldCount(PLAYER_BLACK)), leftPadding + 105, 60);
		this.gcGamestats.fillText(String.valueOf(this.gameLogic.getGameFieldCount(PLAYER_WHITE)), leftPadding + 105, 90);
		this.gcGamestats.fillText("Fields", leftPadding + 125, 60);
		this.gcGamestats.fillText("Fields", leftPadding + 125, 90);
	}

	/**
	 * If its an online game: infos such as Lobby Number and client color will be printed.
	 */
	public void drawOnlineMultiplayerInfo() {
		int leftPadding = 10;
		this.gcGamestats.fillText("You are: " + GameController.getSingleton().getGameLogic().getClientPlayerAsString(), leftPadding, 120);
		this.gcGamestats.fillText("Lobby No.: " + GameController.getSingleton().getOthelloClient().getLobbyNumber(), leftPadding, 150);
	}

	public Canvas getCanvas() {
		return this.gcGamestats.getCanvas();
	}
	
	public VBox getGamestatsVBox() {
		return this.gamestatsVBox;
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
		this.logger = Logger.getLogger("Log.GamestatsRenderer");
		this.logger.setLevel(null);
		this.log(INFO, "Initialized Gamestats Logger");
	}
}
