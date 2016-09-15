/**
 * Created by NF on 9/4/2016.
 */
public class HyperCircuitSimTest {

    public static void main(String[] args) {
        HyperCircuitSim hcs = new HyperCircuitSim();
        hcs.init("lrc.txt");
        for (int i = 0; i < 500; i++)
            hcs.updateCircuit();
    }
}
