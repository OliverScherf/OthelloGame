/**
 * 
 */
package org.game.othello.ao;

import java.io.Serializable;

/**
 * Class to represent a coordiante on the an {@link Gamefield}.
 * @author Oliver Scherf
 */
public class Coordinate implements Serializable {

	private static final long serialVersionUID = 850672941981194452L;
	private int xIndex;
	private int yIndex;

	/**
	 * Instantiate a new Coordinate.
	 * @param xIndex
	 * @param yIndex
	 */
	public Coordinate(int xIndex, int yIndex) {
		this.xIndex = xIndex;
		this.yIndex = yIndex;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Coordinate) {
			return ((Coordinate) obj).xIndex == this.xIndex && ((Coordinate) obj).yIndex == this.yIndex;
		}
		return false;
	}

	@Override
	public String toString() {
		return "X: " + this.xIndex + " Y: " + this.yIndex;
	}

	/**
	 * @return The Coordinate as string in a simplified form.
	 */
	public String toSimpleString() {
		return "[" + this.xIndex + "][" + this.yIndex + "]";
	}

	public int getX() {
		return xIndex;
	}

	public int getY() {
		return yIndex;
	}
}
