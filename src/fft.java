import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import parcs.*;

public class fft implements AM {

        private fft_cpx[] twiddles;
        private List<Integer> factors = new ArrayList<>();
        private int nfft;
        private boolean isInverse;
        private static final int FIRST_FACTOR = 4;

        private fft_cpx[] in;
        private fft_cpx[] out;
        

        public fft(int[] in) {

        }

        public fft(fft_cpx[] iin) {

        }


        public void run(AMInfo info) {
                RoundCfg cfg = (RoundCfg)info.parent.readObject();
                readCfg(cfg);
                System.out.println(cfg.in);
        }       

        public RoundCfg getCfg() {
                return getCfg(0, 0, 1, 0);
        }

        private RoundCfg getCfg(int inPos, int outPos, int fStride, int fPos) {
                RoundCfg cfg = new RoundCfg();
                cfg.in = in;
                cfg.factors = factors;
                cfg.isInverse = isInverse;
                cfg.fPos = fPos;
                cfg.inPos = inPos;
                cfg.outPos = outPos;
                cfg.fftStride = fStride;
                return cfg;
        }

        private void readCfg(RoundCfg cfg) {
                twiddles = cfg.twiddles;
                factors = cfg.factors;
                nfft = cfg.in.length;
                in = cfg.in;
                out = Stream.generate(() -> new fft_cpx()).limit(nfft).toArray(fft_cpx[]::new);;
                isInverse = cfg.isInverse;
        }
}
