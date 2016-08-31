
import java.util.StringTokenizer;

class CD4017 extends ChipCDElm {
	
    public CD4017(int xx, int yy) { super(xx, yy); }
    
    public CD4017(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
    	super(xa, ya, xb, yb, f, st);
    }
    
    @Override
	boolean needsBits() { return true; }
    
    @Override
	String getChipName() { return "Decade Counter (4017)"; }
    
    int loadPin;
    int Q;
    
    @Override
	void setupPins() {
    	// num of column
    	sizeX = 2;
    	// num of rows
    	sizeY = 11;
    	// output bits
    	bits = 11;
    	
    	pins = new Pin[getPostCount()];
    	
    	// Inputs
    	pins[0] = new Pin(0, SIDE_W, "");
    	pins[0].clock = true;
    	
    	pins[1] = new Pin(1, SIDE_W, "CE");
    	
    	pins[2] = new Pin(2, SIDE_W, "R");
    	pins[2].bubble = true;
  
    	// Outputs
    	for (int i = 0; i < 10; i++) {
    	    pins[i+3] = new Pin(i, SIDE_E, "Q" + i);
    	    pins[i+3].output = true;
    	}
    	
	    pins[13] = new Pin(10, SIDE_E, "Cry");
	    pins[13].output = true;

    	allocNodes();
    	
    	Q = 0;
    	pins[3].value = true;
    	pins[13].value = true;
    }
    
    @Override
	void execute() {
	    int i;
	    
	    //System.out.println(pins[0].value + " * " + lastClock + "-");
	    
		for (i = 0; i < 10; i++) {
		    if (i == Q) {
		    	pins[i+3].value = true;
		    } else {
		    	pins[i+3].value = false;
		    }
		    
			// Carry
			if (Q < 5)
				pins[13].value = true;
			if (Q > 4)
				pins[13].value = false;
		}
		
	    // Reset Condition
	    if (pins[2].value) {
			for (i = 1; i != 10; i++)
			    pins[i+3].value = false;
			pins[3].value = true;
			pins[13].value = true;
			Q = 0;
	    }
	    
	    // Clock Inhibit / Clock Enable
	    if(pins[1].value) return;
	    
	    // Clock Counter
	    if (pins[0].value && !lastClock) {
	    	System.out.println(pins[0].value + " X " + lastClock + "-");
	    	
	    	Q += 1;
	    	if(Q>9) Q=0;
	    	

	    	
	    }

	    lastClock = pins[0].value;
    }
    
    @Override
	int getVoltageSourceCount() { return 11; }	// Outputs
    @Override
	int getPostCount() { return 3+11; }			// Total Pins
    @Override
	int getDumpType() { return 189; }
}
    