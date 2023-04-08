
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import parcs.*;

public class Application {
        private static final String inFileName = "sample.wav";
        private static final int trackSize = 65536;
        private Reader audioReader;
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
                boolean isRead = initFileInfo(info.curtask.findFile(inFileName));
                if (isRead) {
                        List<channel> channels = new ArrayList<>();
                        List<point> points = new ArrayList<>();

                        int t = 0;
                        for (var track : tracks) {
                                point p = info.createPoint();
                                channel c = p.createChannel();
                                p.execute("fft");
                                fft inst = new fft(track.channels.get(0));
                                c.write(inst.getCfg());
                                points.add(p);
                                channels.add(c);
                                System.out.println("opened " + t++);
                        }

                        t = 0;
                        for (int i = 0; i < points.size(); i++) {
                                channel c = channels.get(i);
                                point p = points.get(i);
                                RoundResult out = (RoundResult)c.readObject();
                                p.execute("fft");
                                fft inst = new fft(out.result);
                                c.write(inst.getCfg());
                                System.out.println("inversed " + t++);
                        }

                        t = 0;
                        for (int i = 0; i < points.size(); i++) {
                                channel c = channels.get(i);
                                RoundResult out = (RoundResult)c.readObject();
                                int[] realVal = tracks.get(i).channels.get(0);
                                int[] got = fft.cpxToInt(out.result);
                                for (int j = 0; j < realVal.length; j++) {
                                        if (realVal[i] != got[i]) {
                                                System.out.println("Inverse failed");
                                        }
                                }
                                System.out.println("finished " + t++);
                        }


                }
        }

        private boolean initFileInfo(String filePath) {
                boolean result = false;
                try {
                        audioReader = new Reader(new File(filePath));
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