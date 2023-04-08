import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

public class Writer {
	private AudioFormat playFormat;
        private File aFile;

        public Writer(File outFile, AudioFormat format) {
                aFile = outFile;
                playFormat = format;
        }
        
        public boolean write(SoundTrack track) {
                boolean result = true;
                try {
                        byte[] soundData = formDataLine(track);
                        ByteArrayInputStream bais = new ByteArrayInputStream(soundData);
                        AudioInputStream audioInputStream = new AudioInputStream(bais, playFormat, soundData.length);
                        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, aFile);
                        audioInputStream.close();
                } catch (IOException e) {
                        e.printStackTrace();
                        result = false;
                }
                return result;
        }

        private byte[] formDataLine(SoundTrack track) {
                byte[] data = new byte[track.length * playFormat.getFrameSize()];

                for (int iFrame = 0; iFrame < track.length; iFrame++)
                        putFrame(track, iFrame, data);	

                return data;
        }

        private void putFrame(SoundTrack track, int iFrame, byte[] data) {
                int sampleSize = playFormat.getSampleSizeInBits() / 8;
                int nChannels = playFormat.getChannels();
                int offset = iFrame * playFormat.getFrameSize();

                for (int iChan = 0; iChan < nChannels; iChan++) {
                        ByteBuffer buffer = ByteBuffer.wrap(data, offset, sampleSize);
                        int sample = track.getSample(iFrame, iChan);
                        if (playFormat.isBigEndian())
                                putSampleBigEnd(buffer, sample);
                        else
                                putSampleLitEnd(buffer, sample);
                        offset += sampleSize;
                }
        }

        private void putSampleBigEnd(ByteBuffer buffer, int sample) {
                int totalSize = buffer.remaining();
                byte[] iBytes = new byte[totalSize];
                for (int ib = 0; ib < totalSize; ib++) {
                        byte val = (byte)(sample & 0xFF);
                        iBytes[totalSize - ib - 1] = val;
                        sample >>= Byte.SIZE;
                }
                for (byte digit : iBytes)
                        buffer.put(digit);
        }

        private void putSampleLitEnd(ByteBuffer buffer, int sample) {
                int totalSize = buffer.remaining();
                for (int ib = 0; ib < totalSize; ib++) {
                        byte val = (byte)(sample & 0xFF);
                        buffer.put(val);
                        sample >>= Byte.SIZE;
                }
        } 
}
