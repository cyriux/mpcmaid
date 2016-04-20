package com.mpcmaid.audio;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * A sample-playing thread which uses single dataline to play samples.
 */
public class AudioWorker extends Thread {

	private final BlockingQueue<Sample> clipQueue;

	AudioWorker(BlockingQueue<Sample> queue) {
		this.clipQueue = queue;
	}

	/**
	 * Plays the queued clips, closing the
	 * data line if no new AudioClips are fetched within a certain (short)
	 * period of time.
	 */
	public void run() {
		SourceDataLine dataLine = null;
		while (true) {
			try {
				Sample sample;
				try {
					sample = (Sample) clipQueue.poll(5, TimeUnit.SECONDS);
					if (sample == null) {
						if (dataLine != null && dataLine.isOpen() && !dataLine.isRunning()) {
							dataLine.close();
							dataLine = null;
						}
						continue;
					}
				} catch (InterruptedException e) {
					if (dataLine != null && dataLine.isOpen() && !dataLine.isRunning()) {
						dataLine.close();
						dataLine = null;
					}
					continue;
				}
				AudioFormat format = sample.getFormat();
				if (dataLine == null) {
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
					dataLine = (SourceDataLine) AudioSystem.getLine(info);
				}

				if (!format.matches(dataLine.getFormat())) {
					dataLine.close();
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
					dataLine = (SourceDataLine) AudioSystem.getLine(info);
				}

				if (!dataLine.isOpen())
					dataLine.open(format);

				if (!dataLine.isRunning())
					dataLine.start();

				dataLine.write(sample.getBytes(), 0, sample.getBytes().length);
				//dataLine.close();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
