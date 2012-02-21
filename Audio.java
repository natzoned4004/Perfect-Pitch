// William Ames Audio Utilities
// Spring 2010

import java.io.*;
import javax.sound.sampled.*;

/**
 * Class that can read a wav file, and play it. It also makes the samples
 * available in the form of two  arrays: left and right. These arrays may be
 * manipulated externally.
 * 
 * @author William Ames
 * @version Fall 2010
 */

public class Audio {
	// private Clip clip = null;
	private Thread playBackground = null;
//	private float[] right = null, left = null;
	private float sampleRate = AudioSystem.NOT_SPECIFIED; // initially unknown
	private AudioFormat desiredFormat = null; // to use during play
	private byte[] audioBytes;

	/**
	 * Creates a new sound. The file specified is read.
	 * 
	 * @param fileName
	 *            The name of the .wav file to be read.
	 */
		public Audio(String fileName) {
			File fileIn = new File(fileName);
			try {
				AudioInputStream audioInputStream = AudioSystem
						.getAudioInputStream(fileIn);
				AudioFormat format = audioInputStream.getFormat();
				int bytesPerFrame = format.getFrameSize();
				int bitsPerSample = format.getSampleSizeInBits();
				sampleRate = 44100;
				setupDesiredAudioFormat(sampleRate);
		
				if (format.getChannels() != 2) {
					System.out
							.println("Error: can only read 2-channel (stereo) audio files");
					System.exit(1);
				}
				if (bitsPerSample != 16 && bitsPerSample != 8) {
					System.out.println("Error: can only read audio files with "
							+ "8 or 16 bits per sample, found " + bitsPerSample);
					System.exit(1);
				}
		
				long numBytes = audioInputStream.getFrameLength() * bytesPerFrame;
				if (numBytes > Integer.MAX_VALUE) {
					System.out.println("Error: audio file too large. Sorry.");
					System.exit(1);
				}
				audioBytes = new byte[(int) numBytes];
				int numBytesRead = 0;
				numBytesRead = audioInputStream.read(audioBytes);
				if (numBytesRead != numBytes)
					System.out.println("Warning: Unable to read entire file.  Proceeding with partial file.");
				
				//convertToFloat(audioBytes, format);
				// dumpFloats("floats.csv"); // debug only
				// dumpBytes ("bytes.csv"); // debug only
				audioInputStream.close();
			} catch (Exception e) {
				System.out.println("Error encountered: " + e);
				System.exit(1);
			}
		}

	private void setupDesiredAudioFormat(float sampleRate) {
		desiredFormat = new AudioFormat( // to use during play
				sampleRate, // sampleRate,
				16, // sampleSizeInBits,
				2, // channels,
				true, // signed
				false // bigEndian
		);
	}

	/**
	 * Plays the current sound. Stops the sound that is currently playing, if
	 * any, then plays the current sound starting at the beginning.
	 */
		public void play() { // doesn't use clips, should have no length restrictions.
			stop();
			System.gc(); // about to use a lot of memory
			playBackground = new Thread() {
				private SourceDataLine sdl = null;
	
				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {
					try {
						//byte[] bytes = convertToBytes();
						sdl = AudioSystem.getSourceDataLine(desiredFormat);
						System.gc(); // in case old sdl can be cleaned up
						sdl.open(desiredFormat);
						// Info x = sdl.getLineInfo();
						// System.out.println(sdl.getLineInfo());
						sdl.start();
						sdl.write(audioBytes, 0, audioBytes.length); // this blocks until last
															// buffer
						synchronized (this) {
							sdl.drain();
							sdl.close();
						}
					} catch (LineUnavailableException e) {
						System.out.println("Cannot play: " + e);
					}
				}
	
				@Override
				public void interrupt() {
					if (sdl != null) { // shouldn't ever be null, just making sure.
						synchronized (this) {
							sdl.stop();
							sdl.flush();
							sdl.close();
						}
					}
				}
			};
			playBackground.setPriority(Thread.NORM_PRIORITY); // not event priority
			// SwingUtilities.invokeLater(playBackground); // seems to block !! ??
			playBackground.start();
		}

	/**
	 * Stops playing the current sound. If no sound is playing, the stop()
	 * method returns without doing anything.
	 */
		public void stop() {
			if (playBackground != null)
				playBackground.interrupt();
		}


		public byte[] getAudioBytes() {
			return audioBytes;
		}

	public static float signedByteToInt(byte b1, byte b2) {
		int sample = (b1 << 8) | (b2 & 0xFF);
		float result = sample / 32768.f;
		return result;
	}

	public static float unsignedByteToInt(byte b1, byte b2) {
		int sample = (b1 & 0xff) << 8 | (b2 & 0xff);
		float result = sample / 65535.f * 2 - 1;
		return result;
	}

	/**
	 * Retrieve the sample rate in use. Comes from the file given to the
	 * constructor.
	 * 
	 * @return the sample rate
	 */
		public float getSampleRate() {
			return sampleRate;
		}
}