package org.game.othello.rules;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.GameController;
import org.game.othello.ao.Coordinate;
import org.game.othello.ao.Gamefield;
import org.game.othello.connection.InitializationCommando;
import org.game.othello.interfaces.Loggable;
import org.game.othello.interfaces.OthelloConstants;

/**
 * This class handles the whole ruleset of othello. 
 * @author Oliver Scherf
 */
public class GameLogic implements OthelloConstants, Loggable {
	private Logger logger;
	private Vector<Vector<Gamefield>> gameBoard;
	private int currentPlayer;
	private int clientPlayer;
	private boolean offlineMode;
	private int gameState = ONLINE_GAME_STATE_NOT_STARTED;

	/**
	 * Instantiates a new GameLogic.
	 */
	public GameLogic() {
		this.initializeLogging();
		this.gameBoard = this.createGameBoardVector();
		for (int x = 0; x < GAMEBOARD_SIZE; ++x) {
			for (int y = 0; y < GAMEBOARD_SIZE; ++y) {
				this.gameBoard.get(x).add(new Gamefield(PLAYER_NOBODY, new Coordinate(x, y)));
			}
		}
		this.createStartingGameBoard();
		//this.debugCreateInstantGameOverGameField();
		this.log(INFO, "Gamefield was initialized.");
	}

	/**
	 * This will set the first game characters on the board.
	 */
	private void createStartingGameBoard() {
		this.gameBoard.get(3).set(3, new Gamefield(PLAYER_WHITE, new Coordinate(3, 3)));
		this.gameBoard.get(4).set(4, new Gamefield(PLAYER_WHITE, new Coordinate(4, 4)));
		this.gameBoard.get(3).set(4, new Gamefield(PLAYER_BLACK, new Coordinate(3, 4)));
		this.gameBoard.get(4).set(3, new Gamefield(PLAYER_BLACK, new Coordinate(4, 3)));
	}

	/**
	 * We need this method for testing.
	 */
	@SuppressWarnings("unused")
	private void debugCreateInstantGameOverGameField() {
		this.gameBoard.get(2).set(3, new Gamefield(PLAYER_WHITE, new Coordinate(3, 3)));
		this.gameBoard.get(3).set(3, new Gamefield(PLAYER_WHITE, new Coordinate(3, 3)));
		this.gameBoard.get(4).set(3, new Gamefield(PLAYER_BLACK, new Coordinate(4, 4)));

		this.gameBoard.get(2).set(5, new Gamefield(PLAYER_WHITE, new Coordinate(3, 3)));
		this.gameBoard.get(3).set(5, new Gamefield(PLAYER_BLACK, new Coordinate(3, 3)));
		this.gameBoard.get(4).set(5, new Gamefield(PLAYER_WHITE, new Coordinate(4, 4)));
		this.gameBoard.get(5).set(5, new Gamefield(PLAYER_WHITE, new Coordinate(4, 4)));
	}

	/**
	 * Depending on these flags, informations are send to the server.
	 */
	public void setOfflineFlags() {
		this.offlineMode = true;
		this.currentPlayer = PLAYER_BLACK;
		this.clientPlayer = PLAYER_BLACK;
	}

	/**
	 * Depending on these flags, informations are send to the server.
	 */
	public void setOnlineFlags() {
		this.offlineMode = false;
		this.currentPlayer = PLAYER_NOBODY;
		this.clientPlayer = PLAYER_NOBODY;
	}

	/**
	 * Creates the gameFields vector.
	 * @return the gameboardsvector 
	 */
	private Vector<Vector<Gamefield>> createGameBoardVector() {
		Vector<Vector<Gamefield>> newGameboard = new Vector<Vector<Gamefield>>(GAMEBOARD_SIZE);
		for (int i = 0; i < GAMEBOARD_SIZE; i++) {
			newGameboard.add(new Vector<Gamefield>());
		}
		this.log(FINER, "Created Gamefields Vector");
		return newGameboard;
	}

	/**
	 * Handles and validates a turn attempt.
	 * @param index the index where a new game character should placed on.  
	 * @return true if turn was valid
	 */
	public boolean handleTurnAttempt(Coordinate index) {
		if (!this.offlineMode && this.currentPlayer != this.clientPlayer) {
			this.log(FINE, "Its the opponent turn!");
			return false;
		}
		if (this.validateTurn(index)) {
			this.doTurn(index);
			if (this.isGameOver(index)) {
				this.setGameEndFlags();
				GameController.getSingleton().endGame(index);
				// We need to return false here, because otherwise, the GameController will send the Coordinate twice to
				// the server.
				return false;
			} else {
				this.switchTurnOrder();
			}
			if (this.offlineMode && !this.isAnyMovePossibleFor(this.currentPlayer)) {
				this.switchTurnOrder();
			}
			return true;
		}
		return false;
	}

	/**
	 * If there is no move possible for both teams, this method ends the game and inform the user.
	 * If its an online multiplayer game, the client informs the opponent about the end.
	 */
	private boolean isGameOver(Coordinate index) {
		return !this.isAnyMovePossibleFor(this.currentPlayer) && !this.isAnyMovePossibleFor(this.getOpponentPlayer());
	}

	/**
	 * Sets the current game to the state: Finished.
	 */
	public void setGameEndFlags() {
		this.gameState = ONLINE_GAME_STATE_FINISHED;
		this.currentPlayer = PLAYER_NOBODY;
		this.clientPlayer = PLAYER_NOBODY;
	}

	/**
	 * When the online multiplayer game is about to start, this method sets the needed precondition in the gamelogic.
	 * @param obj The {@link InitializationCommando} that comes from the server.
	 */
	public void startOnlineGame(InitializationCommando obj) {
		this.gameState = ONLINE_GAME_STATE_STARTED;
		this.currentPlayer = PLAYER_BLACK;
		if (obj.getStartingPlayer() == ONLINE_GAME_I_START) {
			this.clientPlayer = currentPlayer;
		} else {
			this.clientPlayer = this.getOpponentPlayer();
		}
		this.log(INFO, "Online Multiplayer game starts.");
		if (this.clientPlayer == currentPlayer) {
			this.log(INFO, "Starting player is this client.");
			GameController.getSingleton().getRenderController().getMessageRender().printInfoMessage("You are player Black. You start.");
		} else {
			this.log(INFO, "Starting player is the opponent.");
			GameController.getSingleton().getRenderController().getMessageRender().printInfoMessage("You are player White. Opponent start.");
		}
		GameController.getSingleton().getRenderController().update();
	}

	/**
	 * Checks if there is any move possible for a specific player.
	 * @param playerToCheck
	 * @return if a move is possible, or not
	 */
	private boolean isAnyMovePossibleFor(int playerToCheck) {
		int oldPlayer = this.currentPlayer;

		boolean isAnyMovePossible = false;
		this.currentPlayer = playerToCheck;
		for (Vector<Gamefield> vector : this.gameBoard) {
			for (Gamefield gameField : vector) {
				if (isAnyMovePossible) {
					break;
				}
				if (this.validateTurn(gameField.getIndex())) {
					isAnyMovePossible = true;
					break;
				}
			}
		}
		this.currentPlayer = oldPlayer;
		this.log(FINE, "No move possible for: " + this.getCurrentPlayerAsString());
		return isAnyMovePossible;
	}

	public boolean isAnyMovePossibleForThisClient() {
		return this.isAnyMovePossibleFor(this.clientPlayer);
	}
	
	/**
	 * @param c The coordinate where a game character should be placed.
	 * @return If a turn is possible on this coordinate.
	 */
	private boolean validateTurn(Coordinate c) {
		if (this.gameState == ONLINE_GAME_STATE_FINISHED) {
			return false;
		}
		if (clientPlayer == PLAYER_NOBODY && currentPlayer == PLAYER_NOBODY) {
			this.log(FINE, "Nobody is on turn!");
			return false;
		}
		if (!this.gameBoard.get(c.getX()).get(c.getY()).isEmpty()) {
			this.log(FINEST, "The index was empty.");
			return false;
		}

		if (!this.isHorizontalConnected(c) && !this.isVerticalConnected(c) && !this.isDiagonalConnected(c)) {
			this.log(FINER, "The index was not empty, but no turn was possible due the gamerules.");
			return false;
		}
		return true;

	}

	/**
	 * Checks if there is a diagonal connection to another game character with the same color.
	 * @param c
	 * @return if there is a diagonal connection between two game characters.
	 */
	private boolean isDiagonalConnected(Coordinate c) {
		return this.getDiagonalUpToLeftDownConnectedIndex(c) != null || this.getDiagonalUpToRightDownConnectedIndex(c) != null
				|| this.getDiagonalDownToLeftUpConnectedIndex(c) != null || this.getDiagonalDownToRightUpConnectedIndex(c) != null;
	}

	/**
	 * Checks if there is a horizontal connection to another game character with the same color.
	 * @param c
	 * @return if there is a horizontal connection between two game characters.
	 */
	private boolean isHorizontalConnected(Coordinate c) {
		return this.getLeftConnectedIndex(c) != null || this.getRightConnectedIndex(c) != null;
	}

	/**
	 * Checks if there is a vertical connection to another game character with the same color.
	 * @param c
	 * @return if there is a vertical connection between two game characters.
	 */
	private boolean isVerticalConnected(Coordinate c) {
		return this.getUpConnectedIndex(c) != null || this.getDownConnectedIndex(c) != null;
	}

	/**
	 * Checks if there is a connection above the coordinate.
	 * @param c
	 * @return the coordinate where the connectingg index is.
	 */
	private Coordinate getUpConnectedIndex(Coordinate c) {
		if (!(c.getY() - 1 >= 0)) {
			return null;
		}
		// if the field above is empty or NOT the opponent, there is no connection.
		if (this.gameBoard.get(c.getX()).get(c.getY() - 1).isEmpty() 
				|| this.gameBoard.get(c.getX()).get(c.getY() - 1).getOwner() != this.getOpponentPlayer())
			return null;

		for (int y = c.getY() - 2; y >= 0; --y) {
			if (this.gameBoard.get(c.getX()).get(y).getOwner() == this.currentPlayer) {
				return new Coordinate(c.getX(), y);
			}
			if (this.gameBoard.get(c.getX()).get(y).isEmpty()) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Checks if there is a connection under the coordinate.
	 * @param c
	 * @return the coordinate where the connecting index is.
	 */
	private Coordinate getDownConnectedIndex(Coordinate c) {

		if (!(c.getY() + 1 < GAMEBOARD_SIZE)) {
			return null;
		}
		// if the field below is empty or NOT the opponent, there is no connection.
		if (this.gameBoard.get(c.getX()).get(c.getY() + 1).isEmpty() || this.gameBoard.get(c.getX()).get(c.getY() + 1).getOwner() != this.getOpponentPlayer())
			return null;

		for (int y = c.getY() + 2; y < GAMEBOARD_SIZE; ++y) {
			if (this.gameBoard.get(c.getX()).get(y).getOwner() == this.currentPlayer) {
				return new Coordinate(c.getX(), y);
			}
			if (this.gameBoard.get(c.getX()).get(y).isEmpty()) {
				return null;
			}
		}
		return null;
	}

	/**
	 * Checks if there is a connection to the left of the coordinate.
	 * @param c
	 * @return the coordinate where the connecting index is.
	 */
	private Coordinate getLeftConnectedIndex(Coordinate c) {
		if (!(c.getX() - 1 >= 0)) {
			return null;
		}
		// if the field to the left is empty or NOT the opponent, there is no connection.
		if (this.gameBoard.get(c.getX() - 1).get(c.getY()).getOwner() != this.getOpponentPlayer() || this.gameBoard.get(c.getX() - 1).get(c.getY()).isEmpty())
			return null;

		for (int x = c.getX() - 2; x >= 0; --x) {
			if (this.gameBoard.get(x).get(c.getY()).getOwner() == this.currentPlayer) {
				return new Coordinate(x, c.getY());
			}
			if (this.gameBoard.get(x).get(c.getY()).isEmpty()) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Checks if there is a connection to the right of the coordinate.
	 * @param c
	 * @return the coordinate where the connecting index is.
	 */
	private Coordinate getRightConnectedIndex(Coordinate c) {
		if (!(c.getX() + 1 < GAMEBOARD_SIZE)) {
			return null;
		}
		// if the field to the right is empty or NOT the opponent, there is no connection.
		if (this.gameBoard.get(c.getX() + 1).get(c.getY()).getOwner() != this.getOpponentPlayer() || this.gameBoard.get(c.getX() + 1).get(c.getY()).isEmpty())
			return null;

		for (int x = c.getX() + 2; x < GAMEBOARD_SIZE; ++x) {
			if (this.gameBoard.get(x).get(c.getY()).getOwner() == this.currentPlayer) {
				return new Coordinate(x, c.getY());
			}
			if (this.gameBoard.get(x).get(c.getY()).isEmpty()) {
				return null;
			}
		}
		return null;
	}

	/**
	 * Checks if there is a connection to the direction diagonal right down.
	 * @param c
	 * @return the coordinate where the connecting index is.
	 */
	private Coordinate getDiagonalUpToRightDownConnectedIndex(Coordinate c) {
		if (!(c.getX() + 1 < GAMEBOARD_SIZE) || !(c.getY() + 1 < GAMEBOARD_SIZE)) {
			return null;
		}
		// if the field to the right/down is empty or NOT the opponent, there is no connection.
		if (this.gameBoard.get(c.getX() + 1).get(c.getY() + 1).getOwner() != getOpponentPlayer() || this.gameBoard.get(c.getX() + 1).get(c.getY() + 1).isEmpty())
			return null;

		int x = c.getX() + 2;
		int y = c.getY() + 2;

		while (x < GAMEBOARD_SIZE && y < GAMEBOARD_SIZE) {
			if (this.gameBoard.get(x).get(y).getOwner() == this.currentPlayer) {
				return new Coordinate(x, y);
			}
			if (this.gameBoard.get(x).get(y).isEmpty()) {
				return null;
			}
			++x;
			++y;
		}
		return null;
	}

	/**
	 * Checks if there is a connection to the direction diagonal left down.
	 * @param c
	 * @return the coordinate where the connecting index is.
	 */
	private Coordinate getDiagonalUpToLeftDownConnectedIndex(Coordinate c) {
		if (!(c.getX() - 1 >= 0) || !(c.getY() + 1 < GAMEBOARD_SIZE)) {
			return null;
		}
		// if the field to the left/down is empty or NOT the opponent, there is no connection.
		if (this.gameBoard.get(c.getX() - 1).get(c.getY() + 1).getOwner() != getOpponentPlayer() || this.gameBoard.get(c.getX() - 1).get(c.getY() + 1).isEmpty())
			return null;

		int x = c.getX() - 2;
		int y = c.getY() + 2;

		while (x >= 0 && y < GAMEBOARD_SIZE) {
			if (this.gameBoard.get(x).get(y).getOwner() == this.currentPlayer) {
				return new Coordinate(x, y);
			}
			if (this.gameBoard.get(x).get(y).isEmpty()) {
				return null;
			}
			--x;
			++y;
		}
		return null;
	}

	/**
	 * Checks if there is a connection to the direction diagonal left up.
	 * @param c
	 * @return the coordinate where the connecting index is.
	 */
	private Coordinate getDiagonalDownToLeftUpConnectedIndex(Coordinate c) {
		if (!(c.getX() - 1 >= 0) || !(c.getY() - 1 >= 0)) {
			return null;
		}
		// if the field to the left/up is empty or NOT the opponent, there is no connection.
		if (this.gameBoard.get(c.getX() - 1).get(c.getY() - 1).getOwner() != getOpponentPlayer() || this.gameBoard.get(c.getX() - 1).get(c.getY() - 1).isEmpty())
			return null;

		int x = c.getX() - 2;
		int y = c.getY() - 2;

		while (x >= 0 && y >= 0) {
			if (this.gameBoard.get(x).get(y).getOwner() == this.currentPlayer) {
				return new Coordinate(x, y);
			}
			if (this.gameBoard.get(x).get(y).isEmpty()) {
				return null;
			}
			--x;
			--y;
		}
		return null;
	}

	/**
	 * Checks if there is a connection to the direction diagonal right down.
	 * @param c
	 * @return the coordinate where the connecting index is.
	 */
	private Coordinate getDiagonalDownToRightUpConnectedIndex(Coordinate c) {
		if (!(c.getX() + 1 < GAMEBOARD_SIZE) || !(c.getY() - 1 >= 0)) {
			return null;
		}
		// if the field to the left/up is empty or NOT the opponent, there is no connection.
		if (this.gameBoard.get(c.getX() + 1).get(c.getY() - 1).getOwner() != getOpponentPlayer() || this.gameBoard.get(c.getX() + 1).get(c.getY() - 1).isEmpty())
			return null;

		int x = c.getX() + 2;
		int y = c.getY() - 2;

		while (x < GAMEBOARD_SIZE && y >= 0) {
			if (this.gameBoard.get(x).get(y).getOwner() == this.currentPlayer) {
				return new Coordinate(x, y);
			}
			if (this.gameBoard.get(x).get(y).isEmpty()) {
				return null;
			}
			++x;
			--y;
		}
		return null;
	}

	/**
	 * Simply switching the turn order.
	 */
	public void switchTurnOrder() {
		if (this.currentPlayer == Gamefield.PLAYER_BLACK)
			this.currentPlayer = Gamefield.PLAYER_WHITE;
		else if (currentPlayer == Gamefield.PLAYER_WHITE)
			this.currentPlayer = Gamefield.PLAYER_BLACK;
	}

	/**
	 * Places the gamecharacter on the board.
	 * @param c
	 */
	public void doTurn(Coordinate c) {
		this.gameBoard.get(c.getX()).get(c.getY()).setOwner(currentPlayer);
		if (this.getRightConnectedIndex(c) != null) {
			this.log(FINE, "Found a connected Gamecharater to the right, filled from " + c.toSimpleString() + "to " + this.getRightConnectedIndex(c).toSimpleString() + " with "
					+ this.getCurrentPlayerAsString() + " characters.");
			this.connectGameFieldsHorizontal(c, this.getRightConnectedIndex(c));
		}
		if (this.getLeftConnectedIndex(c) != null) {
			this.log(FINE, "Found a connected Gamecharater to the left, filled from " + c.toSimpleString() + "to " + this.getLeftConnectedIndex(c).toSimpleString() + " with "
					+ this.getCurrentPlayerAsString() + " characters.");
			this.connectGameFieldsHorizontal(this.getLeftConnectedIndex(c), c);
		}
		if (this.getUpConnectedIndex(c) != null) {
			this.log(FINE, "Found a connected Gamecharater to the up, filled from " + c.toSimpleString() + " to " + this.getUpConnectedIndex(c).toSimpleString() + " with "
					+ this.getCurrentPlayerAsString() + " characters.");
			this.connectGameFieldsVertical(this.getUpConnectedIndex(c), c);
		}
		if (this.getDownConnectedIndex(c) != null) {
			this.log(FINE, "Found a connected Gamecharater to the down, filled from " + c.toSimpleString() + " to " + this.getDownConnectedIndex(c).toSimpleString() + " with "
					+ this.getCurrentPlayerAsString() + " characters.");
			this.connectGameFieldsVertical(c, this.getDownConnectedIndex(c));
		}
		if (this.getDiagonalUpToRightDownConnectedIndex(c) != null) {
			this.log(FINE, "Found a connected Gamecharater from up to right down, filled from " + c.toSimpleString() + " to "
					+ this.getDiagonalUpToRightDownConnectedIndex(c).toSimpleString() + " with " + this.getCurrentPlayerAsString() + " characters.");
			this.connectGameFieldsDiagonalDown(c, this.getDiagonalUpToRightDownConnectedIndex(c));
		}
		if (this.getDiagonalUpToLeftDownConnectedIndex(c) != null) {
			this.log(FINE, "Found a connected Gamecharater from up to left down, filled from " + c.toSimpleString() + " to "
					+ this.getDiagonalUpToLeftDownConnectedIndex(c).toSimpleString() + " with " + this.getCurrentPlayerAsString() + " characters.");
			this.connectGameFieldsDiagonalUp(this.getDiagonalUpToLeftDownConnectedIndex(c), c);
		}
		if (this.getDiagonalDownToRightUpConnectedIndex(c) != null) {
			this.log(FINE, "Found a connected Gamecharater from down to right up, filled from " + c.toSimpleString() + " to "
					+ this.getDiagonalDownToRightUpConnectedIndex(c).toSimpleString() + " with " + this.getCurrentPlayerAsString() + " characters.");
			this.connectGameFieldsDiagonalUp(c, this.getDiagonalDownToRightUpConnectedIndex(c));
		}
		if (this.getDiagonalDownToLeftUpConnectedIndex(c) != null) {
			this.log(FINE, "Found a connected Gamecharater from down to left up, filled from " + c.toSimpleString() + " to "
					+ this.getDiagonalDownToLeftUpConnectedIndex(c).toSimpleString() + " with " + this.getCurrentPlayerAsString() + " characters.");
			this.connectGameFieldsDiagonalDown(this.getDiagonalDownToLeftUpConnectedIndex(c), c);
		}
	}

	/**
	 * Changes the gameboard and connects the game characters from right up to left down. 
	 * 							   \
	 * 								\
	 * 								 \
	 * @param firstIndex
	 * @param lastIndex		
	 */
	private void connectGameFieldsDiagonalDown(Coordinate firstIndex, Coordinate lastIndex) {
		int x = firstIndex.getX();
		int y = firstIndex.getY();
		int endX = lastIndex.getX();
		int endY = lastIndex.getY();

		while (x <= endX && y <= endY) {
			this.gameBoard.get(x).get(y).setOwner(this.currentPlayer);
			++x;
			++y;
		}
	}

	/**
	 * Changes the gameboard and connects the game characters from right down to left up.    
	 * 								  /
	 * 								 /
	 * 								/
	 * @param firstIndex	
	 * @param lastIndex		
	 */
	private void connectGameFieldsDiagonalUp(Coordinate firstIndex, Coordinate lastIndex) {
		int x = firstIndex.getX();
		int y = firstIndex.getY();
		int endX = lastIndex.getX();
		int endY = lastIndex.getY();

		while (x <= endX && y >= endY) {
			this.gameBoard.get(x).get(y).setOwner(this.currentPlayer);
			++x;
			--y;
		}
	}

	/**
	 * Changes the gameboard and connects the game characters vertical.
	 * @param firstIndex
	 * @param lastIndex
	 */
	private void connectGameFieldsVertical(Coordinate firstIndex, Coordinate lastIndex) {
		for (int y = firstIndex.getY(); y <= lastIndex.getY(); ++y) {
			this.gameBoard.get(firstIndex.getX()).get(y).setOwner(this.currentPlayer);
		}
	}

	/**
	 * Changes the gameboard and connects the game characters horizontal.
	 * @param firstIndex
	 * @param lastIndex
	 */
	private void connectGameFieldsHorizontal(Coordinate leftIndex, Coordinate rightIndex) {
		for (int x = leftIndex.getX(); x <= rightIndex.getX(); ++x)
			this.gameBoard.get(x).get(leftIndex.getY()).setOwner(this.currentPlayer);
	}

	/**
	 * Counts the field that a player is owning.
	 * @param player The gamefield of which player should be counted
	 * @return the gamefield count.
	 */
	public int getGameFieldCount(int player) {
		int counter = 0;
		for (Vector<Gamefield> vector : this.gameBoard) {
			for (Gamefield gameField : vector) {
				if (gameField.getOwner() == player) {
					++counter;
				}
			}
		}
		return counter;
	}

	/**
	 * @param c
	 * @return Returns the owner of a specific gamefield.
	 */
	public int getOwner(Coordinate c) {
		return this.gameBoard.get(c.getX()).get(c.getY()).getOwner();
	}

	/**
	 * @return the player who is no on turn;
	 */
	private int getOpponentPlayer() {
		if (this.currentPlayer == Gamefield.PLAYER_BLACK) {
			return Gamefield.PLAYER_WHITE;
		} else {
			return Gamefield.PLAYER_BLACK;
		}
	}

	/**
	 * @return the player on turn as a string.
	 */
	public String getCurrentPlayerAsString() {
		if (this.currentPlayer == Gamefield.PLAYER_BLACK)
			return "Black";
		else if (this.currentPlayer == Gamefield.PLAYER_WHITE)
			return "White";
		else
			return "Nobody";
	}

	/**
	 * @return the player from this client as string.
	 */
	public String getClientPlayerAsString() {
		if (this.clientPlayer == Gamefield.PLAYER_BLACK)
			return "Black";
		else if (this.clientPlayer == Gamefield.PLAYER_WHITE)
			return "White";
		else
			return "Nobody";
	}
	
	/**
	 * Returns the player with more gamefields.
	 * @return Black, White or Nobody.
	 */
	public int getWinner() {
		if (this.getGameFieldCount(PLAYER_BLACK) == this.getGameFieldCount(PLAYER_WHITE)) {
			return PLAYER_NOBODY;
		} else if (this.getGameFieldCount(PLAYER_BLACK) > this.getGameFieldCount(PLAYER_WHITE)) {
			return PLAYER_BLACK;
		} else {
			return PLAYER_WHITE;
		}
	}

	public int getCurrentPlayer() {
		return this.currentPlayer;
	}

	public Vector<Vector<Gamefield>> getGameBoard() {
		return gameBoard;
	}

	public int getClientPlayer() {
		return this.clientPlayer;
	}

	public boolean isOfflineMode() {
		return this.offlineMode;
	}

	public boolean isGameFinished() {
		return (this.gameState == ONLINE_GAME_STATE_FINISHED);
	}

	public boolean isGameNotStarted() {
		return (this.gameState == ONLINE_GAME_STATE_NOT_STARTED);
	}
	
	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	@Override
	public void initializeLogging() {
		this.logger = Logger.getLogger("Log.GameLogic");
		this.logger.setLevel(null);
		this.log(FINE, "Initialized GameLogic Logger");
	}
	
	@Override
	public void log(Level level, String msg) {
		this.logger.log(level, msg);
	}

	@Override
	public void err(String msg, Exception e) {
		this.logger.log(ERROR, msg, e);
	}
}
