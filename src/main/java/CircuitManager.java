/**
 * Created by NF on 9/17/2016.
 */
public class CircuitManager implements CircuitManagerInterface{

    private static HyperCircuitSim sim;

    static void initClass(HyperCircuitSim s) {
        sim = s;
    }

    public static HyperCircuitSim getSim() {
        return sim;
    }

    public static void setSim(HyperCircuitSim sim) {
        CircuitManager.sim = sim;
    }

    @Override
    public double[] getCircuitControlVoltages() {
        return new double[0];
    }

    @Override
    public double[] getCircuitInputVoltages() {
        return new double[0];
    }

    @Override
    public double[] getCircuitOutputVoltages() {
        return new double[0];
    }

    @Override
    public void setCircuitControlVoltages(double[] var) {

    }

    @Override
    public void setCircuitInputVoltages(double[] var) {

    }

    @Override
    public void setCircuitOutputVoltages(double[] var) {

    }

    @Override
    public double[] getCircuitControlParameters(String var){
        double[] temp;
        for (int i = 0; i != sim.elmList.size(); i++) {
            CircuitElm ce = sim.getElm(i);
//            ce.doStep();
//            ce.calculateCurrent();
//            if ((ce.getDumpClass().getName().compareTo("ResistorElm") == 0)) {
//                if (((ResistorElm) ce).resistance == 10.0) {
//                    System.out.println(ce.dump() + " voltage: " + ce.getVoltageDiff() + " current: " + ce.getCurrent() + " t: " + t);
//                }
//            }
        }
        return new double[var.length()];
    }

    @Override
    public MeasurementResult performMeasurement(double dt, double tolerance) {
        return null;
    }
}
