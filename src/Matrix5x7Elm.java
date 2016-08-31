
import java.awt.Color;
import java.awt.Graphics;
import java.util.StringTokenizer;

class Matrix5x7Elm extends ChipElm {
	public Matrix5x7Elm(int xx, int yy) { super(xx, yy); }
	public Matrix5x7Elm(int xa, int ya, int xb, int yb, int f,
			   StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	}
	
	@Override
	String getChipName() { return "5x7 matrix driver/display"; }
	
	Color darkred;
	
	boolean needsBits() { return true; }
	
	@Override
	void setupPins() {
	    darkred = new Color(30, 0, 0);
	    sizeX = 6;
	    sizeY = 8;
	    pins = new Pin[getPostCount()]; // 12 Pines
	    pins[0] = new Pin(0, SIDE_W, "i1");
	    pins[1] = new Pin(1, SIDE_W, "i2");
	    pins[2] = new Pin(2, SIDE_W, "i3");
	    pins[3] = new Pin(3, SIDE_W, "i4");
	    pins[4] = new Pin(4, SIDE_W, "i5");
	    pins[5] = new Pin(5, SIDE_W, "i6");
	    pins[6] = new Pin(6, SIDE_W, "i7");
	    
	    pins[7] = new Pin(1, SIDE_S, "o1");
	    pins[8] = new Pin(2, SIDE_S, "o2");
	    pins[9] = new Pin(3, SIDE_S, "o3");
	    pins[10] = new Pin(4, SIDE_S, "o4");
	    pins[11] = new Pin(5, SIDE_S, "o5");
	    
	    allocNodes();
	}
	
	@Override
	void execute() {

	}
	
	@Override
	void draw(Graphics g) {
	    drawChip(g);
	    
	    int xl = x+(csize*24);
	    int yl = y;
	    int xs = csize*12;
	    int ys = csize*12;
	    int p = csize*5;
	    
	    
	    for(int b=0; b<5; b++) {
		    for(int a=0; a<7; a++) {
			    g.setColor(darkred);
			    g.fillOval(xl+(xs*(b+1)), yl+(ys*(a+1)), p, p);
		    }
	    }
	    
	    
	    for(int b=0; b<7; b++) {
	    	if(pins[b].value == true) {
			    for(int a=0; a<5; a++) {
					g.setColor(pins[7+a].value ? Color.red : darkred);
					g.fillOval(xl+(xs*(a+1)), yl+(ys*(b+1)), p, p);
			    }
	    	}
	    }
	    
	    /*
		if(pins[0].value == true) {
			g.setColor(pins[7].value ? Color.red : darkred);
			g.fillOval(xl+(xs*(1)), yl+(ys*(1)), p, p);
			
			g.setColor(pins[8].value ? Color.red : darkred);
			g.fillOval(xl+(xs*(2)), yl+(ys*(1)), p, p);
			
		}
	    */
	    //System.out.println("+");
	}
	
	void setColor(Graphics g, int p) {
	    g.setColor(pins[p].value ? Color.red :
		       sim.printableCheckItem.getState() ? Color.white : darkred);
	}
	
	@Override
	int getVoltageSourceCount() { return 0; }	
	@Override
	int getPostCount() { return 7+5; }
	@Override
	int getDumpType() { return 180; }
}
