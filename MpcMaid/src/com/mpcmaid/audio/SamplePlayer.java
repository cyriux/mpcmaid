package com.mpcmaid.audio;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * An sample player which employs limited number of threads to play clips. Each
 * thread creates it's own dataLine.
 *
 * @pattern Singleton We only need one sample player for every window, so that
 *          to control the overall polyphony.
 *
 */
public final class SamplePlayer {

	private final static SamplePlayer INSTANCE = new SamplePlayer();

	private final static BlockingQueue<Sample> queue = new ArrayBlockingQueue<Sample>(1);

	static {
		// only six sounds can be heard at once
		int numWorkers = 6;
		AudioWorker[] workers = new AudioWorker[numWorkers];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new AudioWorker(queue);
			workers[i].start();
		}
	}

	public static SamplePlayer getInstance() {
		return INSTANCE;
	}

	public void play(Sample sample) {
		if (queue.isEmpty())
			queue.add(sample);
	}

	public void play(File file) {
		try {
			if (queue.isEmpty())
				queue.add(Sample.open(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return "SamplePlayer ";
	}
}
