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
        hcs.setDoPeek(true);

        cm.setInputOn(true);
        for (int i = 0; i < 10; i++) {
            hcs.updateCircuit();
            cm.equilibrate(0.1);
        }

        //disable peeking
        hcs.setDoPeek(false);

        //Obtain grade of the circuit result where 1.0 is excellent and 0.0 is bad
        System.out.println("Result grade: "+cm.getResultScore());
    }
}
