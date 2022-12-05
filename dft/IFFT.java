package dft;

import java.util.Arrays;

/**
 * Schnelle inverse Fourier-Transformation
 *
 * @author Sebastian Rettenberger
 */
public class IFFT {
    /**
     * Schnelle inverse Fourier-Transformation (IFFT).
     *
     * Die Funktion nimmt an, dass die Laenge des Arrays c immer eine
     * Zweierpotenz ist. Es gilt also: c.length == 2^m fuer ein beliebiges m.
     */

    private static Complex[] copyArr(Complex[] from, int offset){
        Complex[] res = new Complex[from.length>>1];
        int ctr = 0;
        for(int i = 0 + offset; i < from.length; i += 2){
            res[ctr++] = from[i];
        }
        return res;
    }


    public static Complex[] ifft(Complex[] c) {
        int n = c.length;
        if(n == 1){
            return new Complex[]{c[0]};
        }else {
            double m = n/2.0;
            Complex[] z1 = ifft(copyArr(c, 0));
            Complex[] z2 = ifft(copyArr(c, 1));

            Complex omega = Complex.fromPolar(1, (2.0*Math.PI)/n);

            Complex[] v = new Complex[n];
            for(int j = 0; j < m; ++j) {
                v[j] = z1[j].add(omega.power(j).mul(z2[j]));
                int index = (int)m + j;
                v[index] = z1[j].sub(omega.power(j).mul(z2[j]));
            }

            return v;
        }
    }
}
