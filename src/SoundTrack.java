import java.util.List;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;


public class SoundTrack {

	public List<int[]> channels;

	public int length = 0;

	public SoundTrack(AudioFormat format, int samples) {
		channels = new ArrayList<>();
		for (int i = 0 ; i < format.getChannels(); i++)
			channels.add(new int[samples]);
	}

	public void addFrame(int[] frame) throws WrongFrameFormat {
		if (frame.length == channels.size()) {
			for (int i = 0; i < frame.length; i++)	
				channels.get(i)[length] = frame[i];
			length += 1;
		} else
			throw new WrongFrameFormat("Wrong frame length");
	}

	public int getSample(int nFrame, int nChannel) {
		return channels.get(nChannel)[nFrame];
	}

	public boolean isEmpty() {
		boolean result = false;
		if (channels.size() != 0)
			result = channels.get(0).length == 0;
		return result;
	}
}