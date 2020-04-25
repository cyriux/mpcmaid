package com.mpcmaid.pgm;

/**
 * Represents a sample in a layer on a pad
 * 
 * @author cyrille martraire
 */
public final class Layer extends BaseElement {

	// offset for each sample
	private static final int SAMPLE_LENGTH = 0x18;

	// ----- parameter -----

	public final static Parameter SAMPLE_NAME = Parameter.string("Sample", 0x00, 16);

	private final static Parameter LEVEL = Parameter.integer("Level", 0x11, 0, 100);

	private final static Parameter RANGE = Parameter.range("Range", 0x12, 0, 127);

	public final static Parameter TUNING = Parameter.decimal("Tuning", 0x14, new Range(-36, 36));

	private final static Parameter PLAY_MODE = Parameter.enumType("Play Mode", 0x16, new String[] { "One Shot",
			"Note On" });

	public final static Parameter[] PARAMETERS = { SAMPLE_NAME, LEVEL, TUNING, PLAY_MODE, RANGE };

	protected Layer(final Pad parent, int layerIndex) {
		super(parent, 0, layerIndex, SAMPLE_LENGTH);
		Program.assertIn(0, layerIndex, 3, "sample layer");
	}

	public Parameter[] getParameters() {
		return PARAMETERS;
	}

	public Pad getPad() {
		return (Pad) getParent();
	}

	public String getSampleName() {
		return getString(SAMPLE_NAME.getOffset());
	}

	public void setSampleName(String sampleName) {
		setString(SAMPLE_NAME.getOffset(), sampleName);
	}

	public double getTuning() {
		return getShort(TUNING.getOffset()) / 100.0;
	}

	public void setTuning(double tuning) {
		setShort(TUNING.getOffset(), (short) (100.0 * tuning));
	}

	public byte getLevel() {
		return getByte(LEVEL.getOffset());
	}

	public void setLevel(final int value) {
		setByte(LEVEL.getOffset(), value);
	}

	public Range getRange() {
		return getRange(RANGE.getOffset());
	}

	public void setRange(final Range range) {
		setRange(RANGE.getOffset(), range);
	}

	public boolean isOneShot() {
		return getByte(PLAY_MODE.getOffset()) == 0;
	}

	public boolean isNoteOn() {
		return !isOneShot();
	}

	public void setPlayMode(boolean onShot) {
		setByte(PLAY_MODE.getOffset(), ((byte) (onShot ? 0 : 1)));
	}

	public void setOneShot() {
		setPlayMode(true);
	}

	public void setNoteOn() {
		setPlayMode(false);
	}

	public String toString() {
		return "Layer " + getParent().toString() + " sample " + (getElementIndex() + 1);
	}

}