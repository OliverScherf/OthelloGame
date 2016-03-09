package org.game.othello.interfaces;

/**
 * This interface contains constants used in most classes.
 * @author Oliver Scherf
 */
public interface OthelloConstants {

	/* Logic */
	public static final int GAMEBOARD_SIZE = 8;
	public static final int FIELD_SIZE = 50;
	public static final int PLAYER_NOBODY = 0;
	public static final int PLAYER_WHITE = 1;
	public static final int PLAYER_BLACK = 2;

	/* Socket */
	public static final String SERVER_IP = "oliverscherf.de";
	public static final int SERVER_PORT = 8555;
	
	/* OthelloClient */
	public static final Boolean ONLINE_GAME_I_START = Boolean.TRUE;
	public static final Boolean ONLINE_GAME_OPPONENT_START = Boolean.FALSE;
	public static final int ONLINE_GAME_STATE_NOT_STARTED = 0;
	public static final int ONLINE_GAME_STATE_STARTED = 1;
	public static final int ONLINE_GAME_STATE_FINISHED = 2;
	public static final int ONLINE_GAME_SERVER_COMMANDO_GAME_INTERUPT = 0;
	public static final int ONLINE_GAME_SERVER_COMMANDO_GAME_GAME_END = 1;
	public static final int ONLINE_GAME_SERVER_COMMANDO_INFO = 2;
	public static final int CHAT_MESSAGE_THIS_CLIENT = 0;
	public static final int CHAT_MESSAGE_OPPONENT_CLIENT = 1;

	/* Renderer */
	public static final double WINDOW_HEIGHT = 630;
	public static final double WINDOW_WIDTH = 680;
	public static final double CANVAS_GAMEBOARD_WIDTH = 400;
	public static final double CANVAS_GAMEBOARD_HEIGHT = 400;
	public static final double CANVAS_GAMESTATS_WIDTH = 180;
	public static final double CANVAS_GAMESTATS_HEIGHT = 180;
	public static final double CANVAS_SPACING_GAMEBOARD_GAMESTATS = 20;
	public static final double CANVAS_SPACING_GAMEBOARD_MESSAGEAREA = 20;
	public static final double CANVAS_MESSAGEAREA_HEIGHT = 120;
	public static final double CANVAS_MESSAGEAREA_WIDTH = CANVAS_GAMEBOARD_WIDTH + CANVAS_SPACING_GAMEBOARD_GAMESTATS
			+ CANVAS_GAMESTATS_WIDTH;
	public static final double CANVAS_CHATFIELD_WIDHT = CANVAS_MESSAGEAREA_WIDTH;
	public static final double CANVAS_CHATFIELD_HEIGHT = 20;

	public static final double GAMEBOARD_BORDER_PADDING = 40;
	public static final double BORDER_HEIGHT = 40;
	
	public static final double GAME_CHARACTER_SIZE = 44.0;
	public static final double BORDER_LINE_WIDTH = 6;
}
