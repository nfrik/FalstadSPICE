

import java.awt.Graphics;
import java.awt.Point;
import java.util.StringTokenizer;

class DiacElm extends CircuitElm {
    double onresistance, offresistance, breakdown, holdcurrent;
    boolean state;
    public DiacElm(int xx, int yy) {
	super(xx, yy);
	// FIXME need to adjust defaults to make sense for diac
	offresistance = 1e9;
	onresistance = 1e3;
	breakdown = 1e3;
	holdcurrent = 0.001;
	state = false;
    }
    public DiacElm(int xa, int ya, int xb, int yb, int f,
		       StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	onresistance = new Double(st.nextToken()).doubleValue();
	offresistance = new Double(st.nextToken()).doubleValue();
	breakdown = new Double(st.nextToken()).doubleValue();
	holdcurrent = new Double(st.nextToken()).doubleValue();
    }
    @Override
	boolean nonLinear() {return true;}
    @Override
	int getDumpType() { return 185; }
    @Override
	String dump() {
	return super.dump() + " " + onresistance + " " + offresistance + " "
	    + breakdown + " " + holdcurrent;
    }
    Point ps3, ps4;
    @Override
	void setPoints() {
	super.setPoints();
	calcLeads(32);
	ps3 = new Point();
	ps4 = new Point();
    }
    
    @Override
	void draw(Graphics g) {
	// FIXME need to draw Diac
	int i;
	double v1 = volts[0];
	double v2 = volts[1];
	setBbox(point1, point2, 6);
	draw2Leads(g);
	setPowerColor(g, true);
	doDots(g);
	drawPosts(g);
    }
    
    @Override
	void calculateCurrent() {
	double vd = volts[0] - volts[1];
	if(state)
	    current = vd/onresistance;
	else
	    current = vd/offresistance;
    }
    @Override
	void startIteration() {
	double vd = volts[0] - volts[1];
	if(Math.abs(current) < holdcurrent) state = false;
	if(Math.abs(vd) > breakdown) state = true;
	//System.out.print(this + " res current set to " + current + "\n");
    }
    @Override
	void doStep() {
	if(state)
	    sim.stampResistor(nodes[0], nodes[1], onresistance);
	else
	    sim.stampResistor(nodes[0], nodes[1], offresistance);
    }
    @Override
	void stamp() {
	sim.stampNonLinear(nodes[0]);
	sim.stampNonLinear(nodes[1]);
    }
    @Override
	void getInfo(String arr[]) {
	// FIXME
	arr[0] = "spark gap";
	getBasicInfo(arr);
	arr[3] = state ? "on" : "off";
	arr[4] = "Ron = " + getUnitText(onresistance, CirSim.ohmString);
	arr[5] = "Roff = " + getUnitText(offresistance, CirSim.ohmString);
	arr[6] = "Vbrkdn = " + getUnitText(breakdown, "V");
	arr[7] = "Ihold = " + getUnitText(holdcurrent, "A");
    }
    @Override
	public EditInfo getEditInfo(int n) {
	if (n == 0)
	    return new EditInfo("On resistance (ohms)", onresistance, 0, 0);
	if (n == 1)
	    return new EditInfo("Off resistance (ohms)", offresistance, 0, 0);
	if (n == 2)
	    return new EditInfo("Breakdown voltage (volts)", breakdown, 0, 0);
	if (n == 3)
	    return new EditInfo("Hold current (amps)", holdcurrent, 0, 0);
	return null;
    }
    @Override
	public void setEditValue(int n, EditInfo ei) {
	if (ei.value > 0 && n == 0)
	    onresistance = ei.value;
	if (ei.value > 0 && n == 1)
	    offresistance = ei.value;
	if (ei.value > 0 && n == 2)
	    breakdown = ei.value;
	if (ei.value > 0 && n == 3)
	    holdcurrent = ei.value;
    }
    @Override
	boolean needsShortcut() { return false; }
}

