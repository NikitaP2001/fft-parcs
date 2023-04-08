
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import parcs.*;

public class Application {
        private static final String inFileName = "sample.wav";
        private static final String outFileName = "result.wav";
        private static final int trackSize = 65536;
        private Reader audioReader;
        private Writer audioWriter;
        private AudioFormat sampleFormat;
        private List<SoundTrack> tracks;


        public static void main(String[] args) {
                task curTask = new task();
                curTask.addJarFile("Application.jar");
                Application app = new Application();
                AMInfo info = new AMInfo(curTask, null);
                app.run(info);
                curTask.end();
        }

        private void run(AMInfo info) {
                boolean isRead = initFileInfo(info.curtask.findFile(inFileName), info.curtask.findFile(outFileName));
                if (isRead) {
                        List<channel> channels = new ArrayList<>();
                        List<point> points = new ArrayList<>();

                        int t = 0;
                        for (var track : tracks) {
                                for (var ch : track.channels) {
                                        point p = info.createPoint();
                                        channel c = p.createChannel();
                                        p.execute("fft");
                                        fft inst = new fft(ch);
                                        c.write(inst.getCfg());
                                        points.add(p);
                                        channels.add(c);
                                        System.out.println("opened " + t++);
                                }
                        }

                        t = 0;
                        for (int i = 0; i < points.size(); i++) {
                                RoundResult out = (RoundResult)channels.get(i).readObject();
                                point p = info.createPoint();
                                channel c = p.createChannel();
                                p.execute("fft");
                                fft inst = new fft(out.result);
                                c.write(inst.getCfg());
                                System.out.println("inversed " + t++);
                                points.set(i, p);
                                channels.set(i, c);
                        }

                        t = 0;
                        for (int i = 0; i < points.size(); i++) {
                                for (int chi = 0; chi < tracks.get(i).channels.size(); chi++) {
                                        channel c = channels.get(i);
                                        RoundResult out = (RoundResult)c.readObject();
                                        ch = fft.cpxToInt(out.result);
                                        System.out.println("finished " + t++);
                                }
                                audioWriter.write(tracks.get(i));
                        }
                }
        }

        private boolean initFileInfo(String inPath, String outPath) {
                boolean result = false;
                try {
                        audioReader = new Reader(new File(inPath));
                        audioWriter = new Writer(new File(outPath), sampleFormat);
                        sampleFormat = audioReader.getFormat();
                        tracks = readAudioFile();
                        result = true;
                } catch (UnsupportedAudioFileException ex) {
			System.out.println("Unsupported: " + ex.getMessage());
		} catch (IOException ex) {
			System.out.println("Unsupported: " + ex.getMessage());
		}
                return result;
        }
        
        private List<SoundTrack> readAudioFile() {
                List<SoundTrack> tracks = new ArrayList<>();
                try {
                        SoundTrack res = null;
                        do { 
                                res = audioReader.read(trackSize);
                                if (res.length != 0)
                                        tracks.add(res);
                        } while (!res.isEmpty());
                } catch (IOException ex) {
                        System.out.println("Error: " + ex.getMessage());
                        System.exit(0);
                }
                return tracks;
        }

}