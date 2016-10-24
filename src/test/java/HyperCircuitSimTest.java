/**
 * Created by NF on 9/4/2016.
 */
public class HyperCircuitSimTest {

    public static void main(String[] args) {

        //Enable circuit manager to control bidirectional flow of data
        CircuitManager cm = new CircuitManager();

        HyperCircuitSim hcs = new HyperCircuitSim(cm);

        hcs.init("real_xor.cmf");

        //enable peeking on circuit data
        cm.setDoPeek(true);

        cm.setInputOn(true);
        for (int i = 0; i < 2; i++) {
            hcs.updateCircuit();
            cm.waitForEquilibriumWithTolerance(0.1);
        }

        //disable peeking
        cm.setDoPeek(false);


        //Obtain grade of the circuit result where 1.0 is excellent and 0.0 is bad
        System.out.println("Result grade: "+cm.getResultScore());

//        System.out.println("Result map: "+cm.getResultMap());

        cm.plotResults();


    }
}
