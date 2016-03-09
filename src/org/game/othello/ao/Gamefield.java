package org.game.othello.ao;

import org.game.othello.interfaces.OthelloConstants;

/**
 * Class to represent a gamefield on the an {@link Gamefield}. 
 * @author Oliver Scherf
 */
public class Gamefield implements OthelloConstants {
	private int owner = PLAYER_NOBODY;
	private Coordinate index;

	/**
	 * Instantiate an new Gamefield.
	 * @param owner
	 * @param index
	 */
	public Gamefield(int owner, Coordinate index) {
		this.owner = owner;
		this.index = index;
	}

	public int getOwner() {
		return this.owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	/**
	 * @return true, when the owner is nobody.
	 */
	public boolean isEmpty() {
		if (this.owner == PLAYER_NOBODY)
			return true;
		else
			return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			if (obj == this) {
				return true;
			} else if (obj instanceof Gamefield 
					&& ((Gamefield) obj).getIndex().equals(this.getIndex())
					&& this.owner == ((Gamefield) obj).getOwner()) {
				return true;
			}
		}
		return false;
	}

	public int getXIndex() {
		return this.index.getX();
	}

	public int getYIndex() {
		return this.index.getY();
	}

	public Coordinate getIndex() {
		return this.index;
	}
}
