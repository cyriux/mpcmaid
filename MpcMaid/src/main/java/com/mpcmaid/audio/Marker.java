package com.mpcmaid.audio;

/**
 * Represents a marker on a location expressed in number of samples from the
 * start of the full sample
 * 
 * @author cyrille martraire
 */
public final class Marker implements Comparable<Marker> {
	private int location;

	public Marker(int location) {
		this.location = location;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public int move(final int ticks) {
		if (location + ticks >= 0) {
			this.location += ticks;
		}
		return this.location;
	}

	public Marker duplicate() {
		return new Marker(location);
	}

	public int compareTo(Marker o) {
		final Marker other = o;
		return location - other.location;
	}

	public String toString() {
		return "Marker location=" + location;
	}

}