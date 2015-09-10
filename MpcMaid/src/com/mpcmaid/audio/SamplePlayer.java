package com.mpcmaid.audio;

import java.applet.AudioClip;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sun.media.sound.JavaSoundAudioClip;

/**
 * A sample player based on the SoundManager (multithread audio player)
 * 
 * @pattern Singleton We only need one sample player for every window, so that
 *          to control the overall polyphony.
 * 
 * @author cyrille martraire
 */
public final class SamplePlayer {

	private final static SamplePlayer INSTANCE = new SamplePlayer();

	public static SamplePlayer getInstance() {
		return INSTANCE;
	}

	public void play(File file) {
		AudioClip clip;
		try {
			final FileInputStream fis = new FileInputStream(file);
			clip = new JavaSoundAudioClip(fis);
			clip.play();

			// supposed to free the data line...
			clip = null;
			fis.close();
			//System.gc();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return "SamplePlayer ";
	}
}
