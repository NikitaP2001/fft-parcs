
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import parcs.*;

public class Application {
        private static final String inFileName = "sample.wav";
        private static final int trackSize = 2048;
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
                        List<channel> chann = new ArrayList<>();
                        List<point> points = new ArrayList<>();

                        for (var track : tracks) {
                                point p = info.createPoint();
                                channel c = p.createChannel();
                                p.execute("fft");
                                fft inst = new fft(track.channels.get(0));
                                c.write(inst.getCfg());
                                points.add(p);
                                chann.add(c);
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