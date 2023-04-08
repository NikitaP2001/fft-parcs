import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;


public class Reader {

	AudioInputStream audioIn;
	AudioFormat format;

	public Reader(File wavFile) throws UnsupportedAudioFileException, IOException {

		try {
			audioIn = AudioSystem.getAudioInputStream(wavFile);
			format = audioIn.getFormat();
			if (format.getEncoding() != Encoding.PCM_SIGNED)
				throw new UnsupportedAudioFileException("Encoding not pcms");
			if (format.getSampleSizeInBits() > 32)
				throw new UnsupportedAudioFileException("Encoding too precise");
		} catch(UnsupportedAudioFileException ex) {
			System.out.println("Inv file: " + ex.getMessage());
			throw ex;
		} catch (IOException ex) {
			System.out.println("IO err: " + ex.getMessage());
			throw ex;
		}
	}

	public SoundTrack read(int count) throws IOException {
		SoundTrack result = null;
		try {
			int bSize = count * format.getFrameSize();
			byte[] buff = new byte[bSize];
			int read = audioIn.read(buff, 0, bSize);
			if (read == -1)
				result = new SoundTrack(format, 0);
			else 
				result =  readPCMSTrack(buff);
		} catch (IOException ex) {
			System.out.println("File read error: " + ex.getMessage());
			throw ex;
		}
		
		return result;
	}

	public AudioFormat getFormat() {
		return format;
	}

	private SoundTrack readPCMSTrack(byte[] buff) {
		int count = buff.length / format.getFrameSize();
		SoundTrack track = new SoundTrack(format, count);
		
		for (int iFrame = 0; iFrame < count; iFrame++) {
			int[] frame = readFrame(buff, iFrame);
			track.addFrame(frame);
		}
		
		return track;
	}

	private int[] readFrame(byte[] buff, int nFrame) {
		int[] frame = new int[format.getChannels()];
		int pos = nFrame * format.getFrameSize();
		for (int i = 0; i < format.getChannels(); i++) {
			int samplePos = pos + getBytesPerSample() * i;
			if (format.isBigEndian())
				frame[i] = readSampleBinEng(buff, samplePos);
			else
				frame[i] = readSampleLitEnd(buff, samplePos);
		}
		return frame;
		
	}

	private int readSampleLitEnd(byte[] buff, int pos) {
		int sampleSize = getBytesPerSample();
		int sample = 0;
		boolean signBit = false;
		for (int bi = 0; bi < sampleSize; bi++) {
			int bytePos = pos + bi;
			signBit = buff[bytePos] < 0;
			/*
			if (buff[bytePos] == -5 && buff[bytePos + 1] == -1)
				System.out.println();
				*/
			int byteVal = intToUnsigned(buff[bytePos]);
			sample |= byteVal << bi * Byte.SIZE;
		}
		if (signBit)
			sample = intToSigned(sample);
		return sample;
	}

	private int intToUnsigned(int value) {
		if (value < 0) {
			int maxValue = 0xff;
			while (Math.abs(value) > maxValue + 1)
				maxValue = maxValue << 8 + 0xff;
			value = maxValue + value + 1;
		}
		return value;	
	}

	private int intToSigned(int value) {
		if (value > 0) {
			int maxValue = 0xff;
			while (Math.abs(value) > maxValue + 1)
				maxValue = (maxValue << 8) + 0xff;
			value = -(maxValue - value + 1);
		}
		return value;	
	}

	private int readSampleBinEng(byte[] buff, int pos) {
		int sampleSize = getBytesPerSample();
		int sample = 0;
		boolean signBit = false;
		for (int bi = 0; bi < sampleSize; bi++) {
			int bytePos = pos + sampleSize - bi - 1;
			byte val = buff[bytePos];
			signBit = (val & (1 << 7)) == (1 << 7);
			sample |= buff[bytePos] << bi * Byte.SIZE;
		}
		if (!signBit && sample < 0)
			sample = Integer.MAX_VALUE + sample + 1;
		return sample;
	}

	private int getBytesPerSample() {
		int bits = format.getSampleSizeInBits();
		if (bits % 8 != 0) {
			System.out.println("Unsupported sample size");
			System.exit(0);
		}
		return bits / 8;
	}
}