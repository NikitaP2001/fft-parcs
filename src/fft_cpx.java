import java.io.Serializable;

public class fft_cpx implements Serializable {
        public double real = 0;
        public double imag = 0;

        public fft_cpx(double real, double imag) {
                this.real = real;
                this.imag = imag;
        }

        public fft_cpx() {
                this.real = 0;
                this.imag = 0;
        }

        public void cexp(double phase) {
                real = Math.cos(phase);
                imag = Math.sin(phase);
        }

        public void mul(fft_cpx a, fft_cpx b) {
                real = a.real * b.real - a.imag * b.imag;
                imag = a.real * b.imag + a.imag * b.real;
        }

        public void smul(double val) {
                real *= val;
                imag *= val;
        }

        public void sub(fft_cpx a, fft_cpx b) {
                real = a.real - b.real;
                imag = a.imag - b.imag;
        }

        public void add(fft_cpx a, fft_cpx b) {
                real = a.real + b.real;
                imag = a.imag + b.imag;
        }

        public void add_to(fft_cpx val) {
                real += val.real;
                imag += val.imag;
        }
}