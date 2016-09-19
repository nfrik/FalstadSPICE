import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NF on 9/18/2016.
 */
public class MeasurementResult {

    private Map<String, ArrayList<Double>> resultMap = null;

    public MeasurementResult() {
        resultMap = new HashMap<String, ArrayList<Double>>();

    }

    public MeasurementResult getResultMap() {
        return this;
    }


}
