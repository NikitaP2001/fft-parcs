import java.io.Serializable;
import java.util.List;

public class RoundCfg implements Serializable {
        public fft_cpx[] in;
        public fft_cpx[] twiddles;
        public List<Integer> factors;
        public boolean isInverse;
        public int fPos;
        public int inPos;
        public int outPos;
        public int fftStride;
        public int m;
        public int p;
}
