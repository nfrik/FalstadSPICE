import java.util.List;
import java.util.Map;

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

    public Map<String,Integer> getCircuitControlParameters(String var);

    public void peekCircuitParameters(CircuitElm ce);

    public void peekTime(double t);

    public MeasurementResult runMeasurement(double period_t, double tail_t, double[] var);

    public Double getResultGrade();

}
