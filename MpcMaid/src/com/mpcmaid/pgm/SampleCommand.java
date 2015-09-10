package com.mpcmaid.pgm;

/**
 * A closure to execute agaisnt this sample matrix
 * 
 * @pattern Command
 * 
 * @author cyrille martraire
 */
public interface SampleCommand {

	Object execute(SampleMatrix matrix);

}