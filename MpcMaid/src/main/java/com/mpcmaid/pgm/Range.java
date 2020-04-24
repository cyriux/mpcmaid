package com.mpcmaid.pgm;

/**
 * Represents a range of values [low..high]
 * 
 * @author cyrille martraire
 */
public final class Range {
	private final int low;

	private final int high;

	public Range(int low, int high) {
		this.low = low;
		this.high = high;
	}

	public int getLow() {
		return low;
	}

	public int getHigh() {
		return high;
	}

	public boolean isReversed() {
		return high < low;
	}

	public Range reverse() {
		return new Range(high, low);
	}

	public boolean contains(final double value) {
		return low <= value && value <= high;
	}

	/**
	 * @return true if this Range is equal to the given Range
	 */
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Range)) {
			return false;
		}
		final Range other = (Range) arg0;
		if (this == other) {
			return true;
		}
		return other.low == low && other.high == high;
	}

	public int hashCode() {
		return low + 97 * high;
	}

	public String toString() {
		return low + ".." + high;
	}
}