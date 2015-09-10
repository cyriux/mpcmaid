package com.mpcmaid.pgm;

/**
 * Represents the basic interface for each element of the Pgm format structure
 * 
 * @author cyrille martraire
 */
public interface Element {

	Parameter[] getParameters();

	Object get(Parameter parameter);

	void set(Parameter parameter, Object value);

}