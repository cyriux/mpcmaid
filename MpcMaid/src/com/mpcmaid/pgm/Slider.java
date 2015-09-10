package com.mpcmaid.pgm;

/**
 * Represents a pad out of the 64 pads on the Mpc1000
 * 
 * @author cyrille martraire
 */
public final class Slider extends BaseElement {

	// SLIDERs parameters
	private static final int SLIDER_SECTION = 0x29D9;

	private static final int SLIDER_LENGTH = 0x29E6 - SLIDER_SECTION; // slider2

	// pad -

	public static final Parameter PAD = Parameter.intOrOff("Pad", SLIDER_SECTION, 0, 64);

	private static final String[] SLIDER_PARAMETERS = { "Tune", "Filter", "Layer", "Attack", "Decay" };

	private static final Parameter PARAMETER = Parameter.enumType("Parameter", 0x29DB, SLIDER_PARAMETERS);

	// slider ranges

	// Tune range -120 - 120
	public static final Parameter TUNE_RANGE = Parameter.range("Tune", 0x29DC, -120, 120);

	// Tune range -50 - 50
	public static final Parameter FILTER_RANGE = Parameter.range("Filter", 0x29DE, -50, 50);

	// Tune range 0-127
	public static final Parameter LAYER_RANGE = Parameter.range("Layer", 0x29E0, 0, 127);

	// Tune range 0-100
	public static final Parameter ATTACK_RANGE = Parameter.range("Attack", 0x29E2, 0, 100);

	// Tune range 0-100
	public static final Parameter DECAY_RANGE = Parameter.range("Decay", 0x29E4, 0, 100);

	protected Slider(final Program parent, int sliderIndex) {
		super(parent, 0, sliderIndex, SLIDER_LENGTH);
		Program.assertIn(0, sliderIndex, 1, "slider");
	}

	public Parameter[] getParameters() {
		return new Parameter[] { PAD, PARAMETER, TUNE_RANGE, FILTER_RANGE, LAYER_RANGE, ATTACK_RANGE, DECAY_RANGE };
	}

	public int getSliderNumber() {
		return getElementIndex();
	}

	public int getSliderPad() {
		return getByte(PAD.getOffset());
	}

	public void setSliderPad(int pad) {
		Program.assertIn(0, pad, 63, "slider pad");
		setByte(PAD.getOffset(), pad);
	}

	public byte getSliderParameter() {
		return getByte(PARAMETER.getOffset());
	}

	public void setSliderParameter(final int type) {
		Program.assertIn(0, type, 4, "slider parameter");
		setByte(PARAMETER.getOffset(), type);
	}

	public Range getSliderRange(final int parameter) {
		return getRange(parameter);
	}

	public void setSliderRange(final int parameter, Range range) {
		setRange(parameter, range);
	}

	public String toString() {
		return "Slider" + (getSliderNumber() + 1);
	}

}