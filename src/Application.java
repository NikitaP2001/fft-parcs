
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import parcs.*;

public class Application {
        private static final int trackSize = 65536;
        private static final int nMaxChann = 30;
        private Reader audioReader;
        private Writer audioWriter;
        private AudioFormat sampleFormat;
        private List<channel> channels = new ArrayList<>();
        private List<point> points = new ArrayList<>();
        private float sampleRate;
        private String inFileName = "sample.wav";
        private String outFileName = "result.wav";
        private int filterMin = 0;
        private int filterMax = 22000;

        public static void main(String[] args) {
                task curTask = new task();
                curTask.addJarFile("Application.jar");
                Application app = new Application(args);
                AMInfo info = new AMInfo(curTask, null);
                app.run(info);
                curTask.end();
        }

        public Application(String[] args) {
                try {
                        for (int i = 0; i + 1 < args.length; i++) {
                                if (args[i].equals("-f"))
                                        inFileName = args[++i];
                                else if (args[i].equals("-o"))
                                        outFileName = args[++i];
                                else if (args[i].equals("-m"))
                                        filterMin = Integer.valueOf(args[++i]);
                                else if (args[i].equals("-M"))
                                        filterMax = Integer.valueOf(args[++i]);
                        }
                } catch (NumberFormatException ex) {
                        System.out.println("[-] Wrong argument provided");
                }
        }

        private static void printHeap() {
                long heapSize = Runtime.getRuntime().totalMemory(); 
                long heapMaxSize = Runtime.getRuntime().maxMemory();
                System.out.println("curr heap: " + heapSize + " heap max : " + heapMaxSize);
        }

        private boolean runForward(AMInfo info) {
                SoundTrack track;
                while (!(track = readTrack()).isEmpty()) {
                        for (var ch : track.channels) {
                                point p = info.createPoint();
                                channel c = p.createChannel();
                                p.execute("fft");
                                fft inst = new fft(ch);
                                c.write(inst.getCfg());
                                points.add(p);
                                channels.add(c);
                        }
                        if (points.size() >= nMaxChann)
                                return false;
                }
                return true;
        }

        private fft_cpx[] filter(fft_cpx[] data, float minPass, float maxPass) {
                int nNyquist = data.length / 2;
                float freqPerBin = sampleRate / (2 * nNyquist);
                for (int i = 0; i < data.length; i++) {
                        if (i > nNyquist) {
                                data[i].clear();
                                continue;
                        }
                        if (i * freqPerBin < minPass) {
                                data[i].clear();
                        } else if (i * freqPerBin > maxPass) {
                                data[i].clear();
                        }
                }
                return data;
        }

        private void runInverse(AMInfo info) {
                boolean first = false;
                for (int i = 0; i < points.size(); i++) {
                        long time = System.currentTimeMillis();
                        RoundResult out = (RoundResult)channels.get(i).readObject();
                        long completedIn = System.currentTimeMillis() - time;
                        if (!first) { System.out.println("wait for inv: " + completedIn + "ms"); first = true; }
                        point p = info.createPoint();
                        channel c = p.createChannel();
                        p.execute("fft");
                        fft inst = new fft(filter(out.result, filterMin, filterMax));
                        c.write(inst.getCfg());
                        points.set(i, p);
                        channels.set(i, c);
                }
        }

        private void writeResult() {
                int nSoundChan = sampleFormat.getChannels();
                boolean first = false;
                for (int i = 0; i < points.size() / nSoundChan; i++) {
                        SoundTrack resTrack = new SoundTrack(sampleFormat, trackSize);
                        resTrack.length = trackSize;
                        for (int chi = 0; chi < nSoundChan; chi++) {
                                int iPC = i * nSoundChan + chi;
                                long time = System.currentTimeMillis();
                                RoundResult out = (RoundResult)channels.get(iPC).readObject();
                                long completedIn = System.currentTimeMillis() - time;
                                if (!first) { System.out.println("wait for write: " + completedIn + "ms"); first = true; }
                                resTrack.channels.set(chi, fft.cpxToInt(out.result));
                        }
                        audioWriter.write(resTrack);
                }
        }

        private void run(AMInfo info) {
                boolean isRead = initFileInfo(info.curtask.findFile(inFileName), info.curtask.findFile(outFileName));
                if (isRead) {
                        while (!runForward(info)) {
                                printHeap();
                                runInverse(info); 
                                writeResult();                                
                                points.clear();
                                channels.clear();                                                        
                        }

                        if (audioWriter.commit())
                                System.out.println("[+] result written");
                        else
                                System.out.println("[-] error writing result");
                }
        }

        private boolean initFileInfo(String inPath, String outPath) {
                boolean result = false;
                try {
                        audioReader = new Reader(new File(inPath));
                        sampleFormat = audioReader.getFormat();
                        sampleRate = sampleFormat.getSampleRate();
                        audioWriter = new Writer(new File(outPath), sampleFormat);
                        result = true;
                } catch (UnsupportedAudioFileException ex) {
			System.out.println("Unsupported: " + ex.getMessage());
		} catch (IOException ex) {
			System.out.println("Unsupported: " + ex.getMessage());
		}
                return result;
        }

        private SoundTrack readTrack() {
                SoundTrack res = null;
                try {
                        res = audioReader.read(trackSize);
                } catch (IOException ex) {
                        System.out.println("Error: " + ex.getMessage());
                        System.exit(0);
                }
                return res;
        }
}