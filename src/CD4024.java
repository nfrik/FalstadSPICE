
import java.util.StringTokenizer;

class CD4024 extends ChipCDElm {
	final int FLAG_ENABLE = 2;
	final int bits = 7;

	public CD4024(int xx, int yy) { super(xx, yy); }

	public CD4024(int xa, int ya, int xb, int yb, int f,
				StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	String getChipName() { return "Counter 7-bit (4024)"; }

	void setupPins() {
		sizeX = 2;	//Columns
		sizeY = bits;	//Rows
		pins = new Pin[getPostCount()];
		pins[0] = new Pin(0, SIDE_W, "");
		pins[0].clock = true;
		pins[1] = new Pin(sizeY-1, SIDE_W, "R");
		pins[1].bubble = true;
		int i;
		for (i = 0; i != bits; i++) {
			int ii = i+2;
			pins[ii] = new Pin(i, SIDE_E, "Q" + (bits-i-1));
			pins[ii].output = pins[ii].state = true;
		}
		allocNodes();
	}

	int getPostCount() {
		return bits+2;
	}

	boolean hasEnable() { return (flags & FLAG_ENABLE) != 0; }

	int getVoltageSourceCount() { return bits; }

	void execute() {
		boolean en = true;
		if (hasEnable())
			en = pins[bits+2].value;
		
		if (!pins[0].value && lastClock && en) {
			int i;
			for (i = bits-1; i >= 0; i--) {
				int ii = i+2;
				if (!pins[ii].value) {
					pins[ii].value = true;
					break;
				}
				pins[ii].value = false;
			}
		}
		if (pins[1].value) {
			int i;
			for (i = 0; i != bits; i++)
				pins[i+2].value = false;
		}
		lastClock = pins[0].value;
	}

	int getDumpType() { return 182; }
}
