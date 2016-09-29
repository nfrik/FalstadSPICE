import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NF on 9/17/2016.
 */
public class CircuitManager implements CircuitManagerInterface {

    public static final String WIRE_ELM = "WireElm";
    public static final String RAIL_ELM = "RailElm";
    public static final String CIR_TIME = "Time";

    public static final String INPUT = "Input";
    public static final String OUTPUT = "Output";
    public static final String CONTROL = "Control";

    public  CircuitManager(){
        resultMap = new HashMap<String, List<Double>>();
    }

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
    public Map<String, Integer> getCircuitControlParameters(String var) {
        Map<String, Integer> pars = new HashMap<String, Integer>();
        for (int i = 0; i != sim.elmList.size(); i++) {
            CircuitElm ce = sim.getElm(i);

            if ((ce.getDumpClass().getName().compareTo(WIRE_ELM) == 0)) {
                //check for input
                if ((((WireElm) ce).flags >= 1) && (((WireElm) ce).flags <= 99)) {
                    String key = INPUT + ((WireElm) ce).flags;
                    Integer val = ((WireElm) ce).flags;
                    if (!pars.containsKey(key)) {
                        pars.put(key, val);
                    }
                }

                //check for output
                if ((((WireElm) ce).flags >= 200) && (((WireElm) ce).flags <= 299)) {
                    String key = OUTPUT + ((WireElm) ce).flags;
                    Integer val = ((WireElm) ce).flags;
                    if (!pars.containsKey(key)) {
                        pars.put(key, val);
                    }
                }
            } else if ((ce.getDumpClass().getName().compareTo(RAIL_ELM) == 0)) {
                //check control voltages
                if ((((RailElm) ce).flags >= 100) && (((RailElm) ce).flags <= 199)) {
                    String key = CONTROL + ((RailElm) ce).flags;
                    Integer val = ((RailElm) ce).flags;
                    if (!pars.containsKey(key)) {
                        pars.put(key, val);
                    }
                }
            }
        }
        return pars;
    }

    private Map<String,List<Double>> resultMap;

    @Override
    public void peekCircuitParameters(CircuitElm ce) {

        if ((ce.getDumpClass().getName().compareTo(WIRE_ELM) == 0)) {
            //check for input and output
            if ((((WireElm) ce).flags >= 200) && (((WireElm) ce).flags <= 299)) {
                String key = OUTPUT + ((WireElm) ce).flags;
                Double val = ce.getVoltageDiff();
                if (!resultMap.containsKey(key)) {
                    resultMap.put(key, new ArrayList<Double>());
                }
                resultMap.get(key).add(val);
            }else if(((((WireElm) ce).flags >= 1) && (((WireElm) ce).flags <= 99))){
                String key = INPUT + ((WireElm) ce).flags;
                Double val = ce.getVoltageDiff();
                if (!resultMap.containsKey(key)) {
                    resultMap.put(key, new ArrayList<Double>());
                }
                resultMap.get(key).add(val);
            }


        } else if ((ce.getDumpClass().getName().compareTo(RAIL_ELM) == 0)) {
            //check control voltages
            if ((((RailElm) ce).flags >= 100) && (((RailElm) ce).flags <= 199)) {
                String key = CONTROL + ((RailElm) ce).flags;
                Double val = ce.getVoltageDiff();
                if (!resultMap.containsKey(key)) {
                    resultMap.put(key, new ArrayList<Double>());
                }
                resultMap.get(key).add(val);
            }
        }

    }

    @Override
    public void peekTime(double t) {
        if (!resultMap.containsKey(CIR_TIME)) {
            resultMap.put(CIR_TIME, new ArrayList<Double>());
        }
        resultMap.get(CIR_TIME).add(t);
    }

    @Override
    public MeasurementResult runMeasurement(double period_t, double tail_t, double[] var) {
        //set control voltages
        sim.stoppedCheck = false;

        //start timer


        //stop timer
        sim.stoppedCheck = true;

        //load results
        return null;
    }

    @Override
    public Double getResultGrade() {

        //1.0 is perfect 0.0 is worst
        Double grade = 0.;
        int  i =0;
        if(resultMap.keySet().contains("Output201")){
            List<Double> temp = resultMap.get("Output201");
            for(Double d : temp){
                grade+=d;
            }
            grade=grade/temp.size();
        }

        return grade;
    }
}
