package com.mpcmaid.audio;

/**
 * Represents a location range
 * 
 * @author cyrille martraire
 */
public final class LocationRange {

	private final int from;

	private final int to;

	public LocationRange(int from, int to) {
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public int getMidLocation() {
		return (from + to) / 2;
	}

	public String toString() {
		return "LocationRange [" + from + " - " + to + "]";
	}

}
