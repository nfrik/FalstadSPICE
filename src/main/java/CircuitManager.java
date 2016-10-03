import java.util.*;

/**
 * Created by NF on 9/17/2016.
 */
public class CircuitManager implements CircuitManagerI {

    private static final String WIRE_ELM = "WireElm";

    private static final String RAIL_ELM = "RailElm";

    public static final String RESISTOR_ELM = "ResistorElm";

    private static final String SWITCH_ELM = "SwitchElm";

    private static final String CIR_TIME = "Time";


    private static final String INPUT = "Input";
    private static final Integer INPUT_MIN = 1;
    private static final Integer INPUT_MAX = 9;

    private static final String COMPARATOR_OUTPUT = "ResultOutput";
    private static final Integer COMPARATOR_OUTPUT_MIN = 200;
    private static final Integer COMPARATOR_OUTPUT_MAX = 299;

    private static final String DOT_OUTPUT = "DOTOutput";
    private static final Integer DOT_OUTPUT_MIN = 400;
    private static final Integer DOT_OUTPUT_MAX = 499;

    private static final String CONTROL = "Control";
    private static final Integer CONTROL_MIN = 100;
    private static final Integer CONTROL_MAX = 199;

    private static final String SWITCH = "Switch";
    private static final Integer SWITCH_MIN = 301;
    private static final Integer SWITCH_MAX = 399;

    private static Map<String,List<Integer>> controlsFlagBoundaryMap = new HashMap<String,List<Integer>>();
    private static Map<String,String> controlsNameToElmMap = new HashMap<String,String>();

    static {
        controlsFlagBoundaryMap.put(INPUT,Arrays.asList(INPUT_MIN,INPUT_MAX));
        controlsFlagBoundaryMap.put(COMPARATOR_OUTPUT,Arrays.asList(COMPARATOR_OUTPUT_MIN,COMPARATOR_OUTPUT_MAX));
        controlsFlagBoundaryMap.put(DOT_OUTPUT,Arrays.asList(DOT_OUTPUT_MIN,DOT_OUTPUT_MAX));
        controlsFlagBoundaryMap.put(CONTROL,Arrays.asList(CONTROL_MIN,CONTROL_MAX));
        controlsFlagBoundaryMap.put(SWITCH,Arrays.asList(SWITCH_MIN,SWITCH_MAX));

        controlsNameToElmMap.put(INPUT,WIRE_ELM);
        controlsNameToElmMap.put(COMPARATOR_OUTPUT,WIRE_ELM);
        controlsNameToElmMap.put(DOT_OUTPUT,WIRE_ELM);
        controlsNameToElmMap.put(CONTROL,RAIL_ELM);
        controlsNameToElmMap.put(SWITCH,SWITCH_ELM);
    }

    public CircuitManager() {

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


    private Map<String, CircuitControlElement> circuitControlElementMap;

    @Override
    public Map<String, CircuitControlElement> getCircuitControlParametersMap() {

        if (circuitControlElementMap != null) {
            return circuitControlElementMap;
        }

        Map<String, CircuitControlElement> pars = new HashMap<String, CircuitControlElement>();

        for (int i = 0; i != sim.elmList.size(); i++) {
            CircuitElm ce = sim.getElm(i);

//            for(String cont : controlsFlagBoundaryMap.keySet()){
//                String elmStr = ce.getDumpClass().getName();
//                controlsFlagBoundaryMap.get
//            }

//            switch(ce.getDumpClass().getName()){
//                case WIRE_ELM: /*check input or output wire*/  break;
//                case RAIL_ELM: /*check control rails or not*/ break;
//                case SWITCH_ELM: /*check if the switch we want*/ break;
//            }

            if ((ce.getDumpClass().getName().compareTo(WIRE_ELM) == 0)) {
                int flag = ((WireElm) ce).flags;
                //check for input
                if ((flag >= INPUT_MIN) && (flag <= INPUT_MAX)) {
                    String key = INPUT + flag;
                    Integer val = flag;
                    if (!pars.containsKey(key)) {
                        pars.put(key, new CircuitControlElement(val, i));
                    }
                } else if ((flag >= COMPARATOR_OUTPUT_MIN) && (flag <= COMPARATOR_OUTPUT_MAX)) {
                    String key = COMPARATOR_OUTPUT + flag;
                    Integer val = flag;
                    if (!pars.containsKey(key)) {
                        pars.put(key, new CircuitControlElement(val, i));
                    }
                } else if ((flag >= DOT_OUTPUT_MIN) && (flag <= DOT_OUTPUT_MAX)) {
                    String key = DOT_OUTPUT + flag;
                    Integer val = flag;
                    if (!pars.containsKey(key)) {
                        pars.put(key, new CircuitControlElement(val, i));
                    }
                }


            } else if ((ce.getDumpClass().getName().compareTo(RAIL_ELM) == 0)) {
                int flag = ((RailElm) ce).flags;
                //check control voltages
                if ((flag >= CONTROL_MIN) && (flag <= CONTROL_MAX)) {
                    String key = CONTROL + flag;
                    Integer val = flag;
                    if (!pars.containsKey(key)) {
                        pars.put(key, new CircuitControlElement(val, i));
                    }
                }
            } else if ((ce.getDumpClass().getName().compareTo(SWITCH_ELM) == 0)) {
                //check for switch
                int flag = ((SwitchElm) ce).flags;
                if ((flag >= SWITCH_MIN) && (flag <= SWITCH_MAX)) {
                    String key = SWITCH + flag;
                    Integer val = flag;
                    if (!pars.containsKey(key)) {
                        pars.put(key, new CircuitControlElement(val, i));
                    }
                }
            }
        }
        return pars;
    }


    private Map<String, List<Double>> resultMap;

    @Override
    public void peekCircuitParameters(CircuitElm ce) {

        if ((ce.getDumpClass().getName().compareTo(WIRE_ELM) == 0)) {
            //check for input and output
            if ((((WireElm) ce).flags >= 200) && (((WireElm) ce).flags <= 299)) {
                String key = COMPARATOR_OUTPUT + ((WireElm) ce).flags;
                Double val = ce.getVoltageDiff();
                if (!resultMap.containsKey(key)) {
                    resultMap.put(key, new ArrayList<Double>());
                }
                resultMap.get(key).add(val);
            } else if (((((WireElm) ce).flags >= 1) && (((WireElm) ce).flags <= 99))) {
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
    public void equilibrate(Double eps) {



        setInputOn(false);

        CircuitElm outputElm = sim.getElm(getCircuitControlParametersMap().get(DOT_OUTPUT+400).getNumber());

        Double oldValue = outputElm.getVoltageDiff();

        while (oldValue > eps) {
            sim.updateCircuit();
            oldValue = Math.abs(oldValue-outputElm.getVoltageDiff());
        }

        setInputOn(true);
    }

    @Override
    public void setInputOn(boolean on) {

        SwitchElm ce = (SwitchElm) sim.getElm(getCircuitControlParametersMap().get(SWITCH + 301).getNumber());
        if (on == true)
            ce.position = 0; //note 1 is off
        else
            ce.position = 1;
    }


    @Override
    public Double getResultScore() {

        //1.0 is perfect 0.0 is worst
        Double grade = 0.;
        int i = 0;
        if (resultMap.keySet().contains("Output201")) {
            List<Double> temp = resultMap.get("Output201");
            for (Double d : temp) {
                grade += d;
            }
            grade = grade / temp.size();
        }

        return grade;
    }


}
