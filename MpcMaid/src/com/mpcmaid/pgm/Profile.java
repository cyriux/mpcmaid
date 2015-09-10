package com.mpcmaid.pgm;

/**
 * Represents the profile of an MPC
 * 
 * @author cyrille martraire
 */
public final class Profile {

	private final String name;

	private final int rowNumber;

	private final int colNumber;

	private final int sliderNumber;

	private final int filterNumber;

	public final static Profile MPC500 = new Profile("MPC500", 4, 3, 1, 1);

	public final static Profile MPC1000 = new Profile("MPC1000", 4, 4, 2, 2);

	public Profile(String name, int rowNumber, int colNumber, int sliderNumber, int filterNumber) {
		this.name = name;
		this.rowNumber = rowNumber;
		this.colNumber = colNumber;
		this.sliderNumber = sliderNumber;
		this.filterNumber = filterNumber;
	}

	public String getName() {
		return name;
	}

	public int getPadNumber() {
		return rowNumber * colNumber;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public int getColNumber() {
		return colNumber;
	}

	public int getSliderNumber() {
		return sliderNumber;
	}

	public int getFilterNumber() {
		return filterNumber;
	}

	public String toString() {
		return "Profile " + name;
	}

	public static Profile getProfile(String name) {
		return "MPC1000".equalsIgnoreCase(name) ? MPC1000 : MPC500;
	}

}
