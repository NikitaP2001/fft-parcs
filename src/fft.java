import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import parcs.*;

/* After using one of the constructor to initilize transformation, get
* its config by fft.getCfg() and simply pass the confing through the channel.
* Config will arrive on fft.run() method. After computation read RoundResult 
* through the channel. 
*/

public class fft implements AM {
        private static final int FIRST_FACTOR = 4;
        public static final double PI = Math.PI;

        private fft_cpx[] twiddles;
        private List<Integer> factors = new ArrayList<>();
        private int nfft;
        private boolean isInverse;

        private fft_cpx[] in;
        private fft_cpx[] out;

        /* initilize forward transofrmation from time domain input */
        public fft(int[] in) {
                nfft = in.length / 2;
                this.in = new fft_cpx[nfft];
                this.out = new fft_cpx[nfft];
                for (int i = 0; i < in.length; i++) {
                        this.in[i] = new fft_cpx();
                        this.in[i].real = Double.valueOf(in[2 * i]);
                        this.in[i].imag = Double.valueOf(in[2 * i + 1]);
                        out[i] = new fft_cpx();
                }
                isInverse = false;
                init();
        }

        /* initilize inverse transofmation from freq domain input */
        public fft(fft_cpx[] iin) {
                nfft = iin.length;
                isInverse = true;
                in = iin;
                out = Stream.generate(() -> new fft_cpx()).limit(nfft).toArray(fft_cpx[]::new);
                init();
        }


        public void run(AMInfo info) {
                RoundCfg cfg = (RoundCfg)info.parent.readObject();
                readCfg(cfg);


        }       

        public RoundCfg getCfg() {
                return getCfg(0, 0, 1, 0);
        }

        public static int[] cpxToInt(fft_cpx[] icpx) {
                int nfft = icpx.length;
                int[] result = new int[nfft * 2];
                for (int i = 0; i < nfft; i++) {
                        result[i * 2] = (int)(icpx[i].real * 1.f / nfft);
                        result[i * 2 + 1] = (int)(icpx[i].imag * 1.f / nfft);
                }
                return result;
        }
        
        /* initilize factors and twiddles */
        private void init() {
                twiddles = new fft_cpx[nfft];

                for (int i = 0; i < nfft; i++) {
                        double phase = 2*PI*i/nfft * (isInverse ? 1 : -1);
                        twiddles[i] = new fft_cpx();
                        twiddles[i].cexp(phase);
                }
                initFactors(nfft);
        }

        private void initFactors(int n) {
                factors.clear();
                int power = FIRST_FACTOR;
                double floor_sqrt = Math.floor(Math.sqrt(n));
               
                do {
                        while (n % power != 0) {
                                if (power > floor_sqrt)
                                        power = n;
                                else
                                        power = nextPower(power);
                        }
                        n /= power;
                        factors.add(power);
                        factors.add(n);
                } while (n > 1);
        }

        private static int nextPower(int power) {
                switch (power) {
                        case 4: 
                                return 2; 
                        case 2:
                                return 3;
                        default:
                                return power + 2;
                }
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
