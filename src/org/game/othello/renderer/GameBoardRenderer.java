package org.game.othello.renderer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.GameController;
import org.game.othello.ao.Coordinate;
import org.game.othello.interfaces.Loggable;
import org.game.othello.interfaces.OthelloConstants;
import org.game.othello.rules.GameLogic;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * This class handles the rendering of the gameboard.
 * @author Oliver Scherf
 */
public class GameBoardRenderer implements OthelloConstants, Loggable {

	private Logger logger;
	private GameLogic gameLogic;
	private GraphicsContext gcGameBoard;

	/**
	 * Instantiate a new GameBoardRenderer
	 */
	public GameBoardRenderer() {
		this.initializeLogging();
		this.gameLogic = GameController.getSingleton().getGameLogic();
		this.initializeCanvas();
	}

	/**
	 * Draws the GameBoard. Initializes the Mouse events.
	 */
	private void initializeCanvas() {
		Canvas canvasGameboard = new Canvas(CANVAS_GAMEBOARD_WIDTH, CANVAS_GAMEBOARD_HEIGHT);
		this.gcGameBoard = canvasGameboard.getGraphicsContext2D();
		this.gcGameBoard.setFill(Color.LIGHTSEAGREEN);
		this.gcGameBoard.fillRect(0, 0, CANVAS_GAMEBOARD_WIDTH, CANVAS_GAMEBOARD_HEIGHT);
		this.gcGameBoard.setLineWidth(BORDER_LINE_WIDTH / 2);
		this.gcGameBoard.setStroke(Color.BROWN);
		this.gcGameBoard.setFill(Color.CHOCOLATE);
		this.gcGameBoard.setLineWidth(BORDER_LINE_WIDTH);
		this.gcGameBoard.strokeRect(0, 0, CANVAS_GAMEBOARD_WIDTH, CANVAS_GAMEBOARD_HEIGHT);

		for (int x = 0; x < GAMEBOARD_SIZE; ++x) {
			for (int y = 0; y < GAMEBOARD_SIZE; ++y) {
				if (!this.gameLogic.getGameBoard().get(x).get(y).isEmpty()) {
					this.drawGameCharacterOnBoard(new Coordinate(x, y));
				}
			}
		}
		for (int i = 0; i < this.gameLogic.getGameBoard().size(); ++i) {
			for (int j = 0; j < this.gameLogic.getGameBoard().get(0).size(); ++j) {
				this.gcGameBoard.strokeRect(i * FIELD_SIZE, j * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
			}
		}
		this.initializeMouseEvents(canvasGameboard);
		this.update();
		this.log(FINE, "The GameBoard Canvas was initialized.");
	}

	/**
	 * Draws a Coordinate on the GameBoard.
	 * @param c
	 */
	private void drawGameCharacterOnBoard(Coordinate c) {
		final double gameboardPadding = (FIELD_SIZE - GAME_CHARACTER_SIZE) / 2; // Gamecharacter should be draw in the middle of the gamefield
		this.gcGameBoard.fillOval(c.getX() * FIELD_SIZE + gameboardPadding, c.getY() * FIELD_SIZE + gameboardPadding,
				GAME_CHARACTER_SIZE, GAME_CHARACTER_SIZE);
		this.gcGameBoard.strokeRect(c.getX() * FIELD_SIZE, c.getY() * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
	}

	/**
	 * When a MouseEvent occurs, this method delegates the the calculated index to {@link GameController#handleGameBoardMouseAction(Coordinate, MouseEvent)}
	 * @param canvasGameboard
	 */
	private void initializeMouseEvents(Canvas canvasGameboard) {
		canvasGameboard.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				Coordinate indexClicked = GameBoardRenderer.this.getGameBoardIndex((int) (mouseEvent.getSceneX() - GAMEBOARD_BORDER_PADDING),
						(int) (mouseEvent.getSceneY() - GAMEBOARD_BORDER_PADDING));
				GameController.getSingleton().handleGameBoardMouseAction(indexClicked, mouseEvent);
			}
		});
		this.log(FINE, "The GameBoard Mouse events were initialized.");
	}
	
	/**
	 * @param x MouseEvent X coordinate
	 * @param y MouseEvent Y coordinate
	 * @return {@link Coordinate}
	 */
	private Coordinate getGameBoardIndex(int x, int y) {
		return new Coordinate(x / FIELD_SIZE, y / FIELD_SIZE);
	}

	/**
	 * Updates the gameboard.
	 */
	public void update() {		
		for (int x = 0; x < GAMEBOARD_SIZE; ++x) {
			for (int y = 0; y < GAMEBOARD_SIZE; ++y) {
				this.setFillColor(new Coordinate(x, y));
				this.drawGameCharacterOnBoard(new Coordinate(x, y));
			}
		}
	}

	/**
	 * Changes the fill color of the canvas depending on the gameboard with the location of c.
	 * @param c
	 */
	private void setFillColor(Coordinate c) {
		if (this.gameLogic.getOwner(c) == PLAYER_NOBODY) {
			this.gcGameBoard.setFill(Color.LIGHTSEAGREEN);
		} else if (this.gameLogic.getOwner(c) == PLAYER_WHITE) {
			this.gcGameBoard.setFill(Color.WHITE);
		} else if (this.gameLogic.getOwner(c) == PLAYER_BLACK) {
			this.gcGameBoard.setFill(Color.BLACK);
		}
		this.log(FINEST, "GameBoard FillColor is now : " + gcGameBoard.getFill().toString());
	}

	public Canvas getCanvas() {
		return this.gcGameBoard.getCanvas();
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
		this.logger = Logger.getLogger("Log.GameBoardRenderer");
		this.logger.setLevel(null);
		this.log(INFO, "Initialized GameBoard Renderer Logger");
	}
}
