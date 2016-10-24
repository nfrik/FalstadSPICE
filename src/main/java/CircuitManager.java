import plot.PltSeries;
import plot.PltXYScatter;

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

    private static final String MIN = "MIN";
    private static final String MAX = "MAX";
    private static final String ELEMENT = "ELEMENT";

    private boolean doPeek;

    public boolean isDoPeek() {
        return doPeek;
    }

    public void setDoPeek(boolean doPeek) {
        this.doPeek = doPeek;
    }

    class ControlElement{

        public ControlElement(Integer min, Integer max, String elementName){
            setBoundaryMin(min);
            setBoundaryMax(max);
            setElementName(elementName);
        }

        public Integer getBoundaryMin() {
            return boundaryMin;
        }

        public void setBoundaryMin(Integer boundaryMin) {
            this.boundaryMin = boundaryMin;
        }

        private Integer boundaryMin;

        public Integer getBoundaryMax() {
            return boundaryMax;
        }

        public void setBoundaryMax(Integer boundaryMax) {
            this.boundaryMax = boundaryMax;
        }

        private Integer boundaryMax;

        public String getElementName() {
            return elementName;
        }

        public void setElementName(String elementName) {
            this.elementName = elementName;
        }

        private String  elementName;
    }

    private static Map<String,ControlElement> controlsMap = new HashMap<String,ControlElement>();

    {
        controlsMap.put(INPUT,new ControlElement(INPUT_MIN,INPUT_MAX,WIRE_ELM));
        controlsMap.put(COMPARATOR_OUTPUT,new ControlElement(COMPARATOR_OUTPUT_MIN,COMPARATOR_OUTPUT_MAX,WIRE_ELM));
        controlsMap.put(DOT_OUTPUT,new ControlElement(DOT_OUTPUT_MIN,DOT_OUTPUT_MAX,WIRE_ELM));
        controlsMap.put(CONTROL,new ControlElement(CONTROL_MIN, CONTROL_MAX, RAIL_ELM));
        controlsMap.put(SWITCH,new ControlElement(SWITCH_MIN,SWITCH_MAX,SWITCH_ELM));
    }

    public static List<String> getPeekElements() {
        return peekElements;
    }

    private static List<String> peekElements = Arrays.asList(INPUT,DOT_OUTPUT,CONTROL,COMPARATOR_OUTPUT);

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

//    private Map<String, CircuitControlElement> circuitControlElementMap;

    private Map<String, List<CircuitElm>> controlElementMap;

    @Override
    /*Scan all elements in the circuit and map*/
    public Map<String, Map<String,CircuitElm>> getCircuitControlParametersMap() {

        Map<String, Map<String,CircuitElm>> pars = new HashMap<String, Map<String,CircuitElm>>();

        for(String control: controlsMap.keySet()){
            pars.put(control,new HashMap<String, CircuitElm>());
        }

        for (int i = 0; i != sim.elmList.size(); i++) {
            CircuitElm ce = sim.getElm(i);
            int flag = ce.flags;
            for(String control : controlsMap.keySet()){

                if(ce.getDumpClass().getName().equals(controlsMap.get(control).getElementName())){
                    if((flag >= controlsMap.get(control).getBoundaryMin()) && (flag <= controlsMap.get(control).getBoundaryMax())){
                        String key = control + flag;
                        Integer val;
                        pars.get(control).put(key,ce);
                    }
                }
            }
        }
        return pars;
    }


    public Map<String, List<Double>> getResultMap() {
        return resultMap;
    }

    private Map<String, List<Double>> resultMap;

    @Override
    public void peekCircuitParameters(CircuitElm ce) {

        int flag = ce.flags;

        for(String peekElement : peekElements){
            if(ce.getDumpClass().getName().equals(controlsMap.get(peekElement).getElementName())){
                if((flag >= controlsMap.get(peekElement).getBoundaryMin()) && (flag <= controlsMap.get(peekElement).getBoundaryMax())){
                    String key = peekElement + flag;
                    Integer val;
                    if(!resultMap.containsKey(key)){
                        resultMap.put(key,new ArrayList<Double>());
                    }
                    resultMap.get(key).add(ce.getVoltageDiff());
                }
            }
        }

    }

    @Override
    public void peekTime(double t) {
        if(isDoPeek()) {
            if (!resultMap.containsKey(CIR_TIME)) {
                resultMap.put(CIR_TIME, new ArrayList<Double>());
            }
            resultMap.get(CIR_TIME).add(t);
        }
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
    public void waitForEquilibriumWithTolerance(Double eps) {

        System.out.println("Waiting for equilibrium");
        setInputOn(false);

        List<CircuitElm> outElemsList = new ArrayList<CircuitElm>(getCircuitControlParametersMap().get(DOT_OUTPUT).values());

//        CircuitElm outputElm = getCircuitControlParametersMap().get(DOT_OUTPUT);
//        CircuitElm outputElm = sim.getElm(getCircuitControlParametersMap().get(DOT_OUTPUT+400).getNumber());

        Double oldValue = outElemsList.get(outElemsList.size()-1).getVoltageDiff();

        while (oldValue > eps) {
            sim.updateCircuit();
            oldValue = Math.abs(oldValue-outElemsList.get(outElemsList.size()-1).getVoltageDiff());
        }

        setInputOn(true);
        System.out.println("Equilibrium reached");

    }

    @Override
    public void setInputOn(boolean on) {
        List<CircuitElm> switchElemsList = new ArrayList<CircuitElm>(getCircuitControlParametersMap().get(SWITCH).values());
        SwitchElm ce = (SwitchElm) switchElemsList.get(switchElemsList.size()-1);
//        SwitchElm ce = (SwitchElm) sim.getElm(getCircuitControlParametersMap().get(SWITCH + 301).getNumber());
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
        if (resultMap.keySet().contains(COMPARATOR_OUTPUT+201)) {
            List<Double> temp = resultMap.get(COMPARATOR_OUTPUT+201);
            for (Double d : temp) {
                grade += d;
            }
            grade = grade / temp.size();
        }else{
            System.out.println("Comparator was not found during score computation");
        }

        return grade;
    }

    public void plotResults(){

        List<Double> input = getResultMap().get(INPUT+3);
        List<Double> time = getResultMap().get(CIR_TIME);

        for(int i =0;i<input.size();i++){
//            PltSeries.getInstance().plotYt(input.get(i),0);
            PltXYScatter.getInstance().plotXY(time.get(i),input.get(i));
        }

    }

}
