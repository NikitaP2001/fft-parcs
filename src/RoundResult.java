import java.io.Serializable;

public class RoundResult implements Serializable {
        public fft_cpx[] result;

        public RoundResult(fft_cpx[] out) {
                result = out;
        }

}
