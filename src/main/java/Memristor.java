/**
 * Created by nfrik on 10/8/16.
 */
public class Memristor {

    private double r_on, r_off, dopeWidth, totalWidth, mobility, resistance, current;

    public Memristor() {
        r_on = 100;
        r_off = 160 * r_on;
        dopeWidth = 0;
        totalWidth = 10e-9; // meters
        mobility = 1e-10;   // m^2/sV
        resistance = 100;
    }

    void startIteration(double timeStep) {
        double wd = dopeWidth / totalWidth;
        dopeWidth += timeStep * mobility * r_on * current / totalWidth;
        if (dopeWidth < 0)
            dopeWidth = 0;
        if (dopeWidth > totalWidth)
            dopeWidth = totalWidth;
        resistance = r_on * wd + r_off * (1 - wd);
    }
}
