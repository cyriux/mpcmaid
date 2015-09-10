package com.mpcmaid.pgm;

/**
 * Represents the envelope section of a pad
 * 
 * @author cyrille martraire
 */
public final class PadEnvelope extends BaseElement {

	public static final Parameter ATTACK = Parameter.integer("Attack", 0x66, 0, 100);

	public static final Parameter DECAY = Parameter.integer("Decay", 0x67, 0, 100);

	// 0="End", 1="Start"
	public static final Parameter DECAY_MODE = Parameter.enumType("Decay Mode", 0x68, new String[] { "End", "Start" });

	public static final Parameter VELOCITY_TO_LEVEL = Parameter.integer("Velocity to Level", 0x6B, 0, 100);

	public final static Parameter[] PARAMETERS = { ATTACK, DECAY, DECAY_MODE, VELOCITY_TO_LEVEL };

	protected PadEnvelope(final Pad parent) {
		super(parent);
	}

	public Parameter[] getParameters() {
		return PARAMETERS;
	}

	public String toString() {
		return getParent().toString() + " Envelope";
	}

}