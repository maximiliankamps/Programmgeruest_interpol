import java.util.Arrays;

/**
 * Die Klasse Newton-Polynom beschreibt die Newton-Interpolation. Die Klasse
 * bietet Methoden zur Erstellung und Auswertung eines Newton-Polynoms, welches
 * uebergebene Stuetzpunkte interpoliert.
 *
 * @author braeckle
 *
 */
public class NewtonPolynom implements InterpolationMethod {

    /** Stuetzstellen xi */
    double[] x;

    /**
     * Koeffizienten/Gewichte des Newton Polynoms p(x) = a0 + a1*(x-x0) +
     * a2*(x-x0)*(x-x1)+...
     */
    double[] a;

    /**
     * die Diagonalen des Dreiecksschemas. Diese dividierten Differenzen werden
     * fuer die Erweiterung der Stuetzstellen benoetigt.
     */
    double[] f;

    /**
     * leerer Konstruktore
     */
    public NewtonPolynom() {
    };

    /**
     * Konstruktor
     *
     * @param x
     *            Stuetzstellen
     * @param y
     *            Stuetzwerte
     */
    public NewtonPolynom(double[] x, double[] y) {
        this.init(x, y);
    }

    /**
     * {@inheritDoc} Zusaetzlich werden die Koeffizienten fuer das
     * Newton-Polynom berechnet.
     */
    @Override
    public void init(double a, double b, int n, double[] y) {
        x = new double[n + 1];
        double h = (b - a) / n;

        for (int i = 0; i < n + 1; i++) {
            x[i] = a + i * h;
        }
        computeCoefficients(y);
    }

    /**
     * Initialisierung der Newtoninterpolation mit beliebigen Stuetzstellen. Die
     * Faelle "x und y sind unterschiedlich lang" oder "eines der beiden Arrays
     * ist leer" werden nicht beachtet.
     *
     * @param x
     *            Stuetzstellen
     * @param y
     *            Stuetzwerte
     */
    public void init(double[] x, double[] y) {
        this.x = Arrays.copyOf(x, x.length);
        computeCoefficients(y);
    }

    /**
     * computeCoefficients belegt die Membervariablen a und f. Sie berechnet zu
     * uebergebenen Stuetzwerten y, mit Hilfe des Dreiecksschemas der
     * Newtoninterpolation, die Koeffizienten a_i des Newton-Polynoms. Die
     * Berechnung des Dreiecksschemas soll dabei lokal in nur einem Array der
     * Laenge n erfolgen (z.B. spaltenweise Berechnung). Am Ende steht die
     * Diagonale des Dreiecksschemas in der Membervariable f, also f[0],f[1],
     * ...,f[n] = [x0...x_n]f,[x1...x_n]f,...,[x_n]f. Diese koennen spaeter bei
     * der Erweiterung der Stuetzstellen verwendet werden.
     *
     * Es gilt immer: x und y sind gleich lang.
     */
    private void computeCoefficients(double[] y) {
        a = new double[y.length];
        f = new double[y.length];

        double[] c_arr = new double[y.length*y.length];

        for(int i = 0; i < y.length; i++) { //initialize first column with y values
            c_arr[i] = y[i];
        }

        a[0] = c_arr[0]; //first coefficient

        int ctr = y.length - 1;
        f[ctr] = c_arr[ctr--];
        for(int k = 1; k < y.length; k++) {
            for(int i = 0; i < y.length - k; i++) {
                int c_i_k = (k * y.length) + i; //jth element in kth column
                int c_ip1_km1 = (((k - 1) * (y.length)) + (i + 1));
                int c_i_km1 = (((k - 1) * y.length) + i);

                c_arr[c_i_k] = (c_arr[c_ip1_km1] - c_arr[c_i_km1]) / (x[i + k] - x[i]); //calculate coefficient

                if (i == 0) //add coefficient to a
                    a[k] = c_arr[c_i_k];


                if(y.length == i + k + 1)
                    f[ctr--] = c_arr[c_i_k];
            }
        }
    }

    /**
     * Gibt die Koeffizienten des Newton-Polynoms a zurueck
     */
    public double[] getCoefficients() {
        return a;
    }

    /**
     * Gibt die Dividierten Differenzen der Diagonalen des Dreiecksschemas f
     * zurueck
     */
    public double[] getDividedDifferences() {
        return f;
    }

    /**
     * addSamplintPoint fuegt einen weiteren Stuetzpunkt (x_new, y_new) zu x
     * hinzu. Daher werden die Membervariablen x, a und f vergoessert und
     * aktualisiert . Das gesamte Dreiecksschema muss dazu nicht neu aufgebaut
     * werden, da man den neuen Punkt unten anhaengen und das alte
     * Dreiecksschema erweitern kann. Fuer diese Erweiterungen ist nur die
     * Kenntnis der Stuetzstellen und der Diagonalen des Schemas, bzw. der
     * Koeffizienten noetig. Ist x_new schon als Stuetzstelle vorhanden, werden
     * die Stuetzstellen nicht erweitert.
     *
     * @param x_new
     *            neue Stuetzstelle
     * @param y_new
     *            neuer Stuetzwert
     */
    public void addSamplingPoint(double x_new, double y_new) {
        for(int i = 0; i < x.length; i++) {
            if(x_new == x[i]) {
                return;
            }
        }

        x = Arrays.copyOf(x, x.length+1);
        x[x.length-1] = x_new;
        a = Arrays.copyOf(a, a.length+1);
        f = Arrays.copyOf(f, f.length+1);
        double[] f_tmp = new double[f.length];
        f[f.length-1] = f_tmp[f.length-1] = y_new;

        for(int i = f.length - 1; i > 0; i--) {
            f_tmp[i - 1] = (f_tmp[i] - f[i-1]) / (x[i + (f.length - i - 1)] - x[i-1]);
        }
        f = f_tmp;
        a[a.length-1] = f[0];
    }

    /**
     * {@inheritDoc} Das Newton-Polynom soll effizient mit einer Vorgehensweise
     * aehnlich dem Horner-Schema ausgewertet werden. Es wird davon ausgegangen,
     * dass die Stuetzstellen nicht leer sind.
     */
    @Override
    public double evaluate(double z) {
        double p_x = 0;
        for(int i = 0; i < a.length; i++) {
            double tmp = a[i];
            for (int j = 0; j < i; j++) {
                tmp = tmp*(z - x[j]);
            }
            p_x += tmp;
        }
        return p_x;
    }


    public static void main(String[] args) {
        double[] x = {1, 3};
        double[] y = {-2, -2};
        NewtonPolynom newtonPolynom = new NewtonPolynom(x, y);
        newtonPolynom.computeCoefficients(y);
        newtonPolynom.addSamplingPoint(1.5, 5.0);

        System.out.println(Arrays.toString(newtonPolynom.getCoefficients()));
        System.out.println(Arrays.toString(newtonPolynom.getDividedDifferences()));
        System.out.println(newtonPolynom.evaluate(4));
    }



}

