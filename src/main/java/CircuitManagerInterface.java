/**
 * Created by NF on 9/17/2016.
 */
public interface CircuitManagerInterface {

    public double[] getCircuitControlVoltages();

    public double[] getCircuitInputVoltages();

    public double[] getCircuitOutputVoltages();

    public void setCircuitControlVoltages(double[] var);

    public void setCircuitInputVoltages(double[] var);

    public void setCircuitOutputVoltages(double[] var);

    public double[] getCircuitControlParameters(String var);

    public MeasurementResult runMeasurement(double period_t, double tail_t, double[] var);
}
