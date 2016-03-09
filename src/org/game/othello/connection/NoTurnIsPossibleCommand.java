package org.game.othello.connection;

import java.io.Serializable;

/**
 * An Object of this class will be send, when no move is possible
 * for this player. 
 * @author Oliver Scherf
 */
public class NoTurnIsPossibleCommand implements Serializable{
	private static final long serialVersionUID = 3709306166226788676L;
}
