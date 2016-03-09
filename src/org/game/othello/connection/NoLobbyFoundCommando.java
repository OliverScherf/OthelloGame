package org.game.othello.connection;

import java.io.Serializable;

/**
 * An object of this instance will be send to the client, 
 * if there is no lobby referring to the lobby number the client asked for.
 * @author Oliver
 */
public class NoLobbyFoundCommando implements Serializable {
	private static final long serialVersionUID = -7974308308980431242L;
}
