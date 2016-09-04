
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;

class CircuitCanvas extends Canvas {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	CirSim pg;
	
    CircuitCanvas(CirSim p) {
    	pg = p;
    }
    
    @Override
	public Dimension getPreferredSize() {
    	return new Dimension(300,400);
    }
    
    @Override
	public void update(Graphics g) {
    	pg.updateCircuit(g);
    }
    
    @Override
	public void paint(Graphics g) {
    	pg.updateCircuit(g);
    }
};
