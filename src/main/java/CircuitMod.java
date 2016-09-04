
import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
//import java.io.FileNotFoundException;
//import java.io.PrintWriter;


public class CircuitMod extends Applet implements ComponentListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    static CirSim ogf;

    void destroyFrame() {
        if (ogf != null)
            ogf.dispose();
        ogf = null;
        repaint();
    }

    boolean started = false;

    @Override
    public void init() {
        addComponentListener(this);
    }

    public static void main(String args[]) {
        // Create program frame
        ogf = new CirSim(null);

        // Init program in background thread.
        // Show logo in the meantime.
        final LogoFrame logoFrame = new LogoFrame();

        final String[] a = args;

        final long time = System.nanoTime();

        Thread t1 = new Thread() {
            public void run() {
                // If a file is being loaded at startup
                if (a.length > 0) {
                    ogf.init(a[0]);
                } else {
                    ogf.init(null);
                }

                // When program finishes initializing, dispose logo and show program frame.
                // Make it so that the logo is shown at least 1.5 seconds.
                long timeElapsed = (System.nanoTime() - time) / 1000000;

                if (timeElapsed < 1500) {
                    try {
                        sleep(1500 - (int) timeElapsed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                logoFrame.dispose();
                ogf.setFrameAndShow();
            }
        };
        t1.start();
    }

    void showFrame() {
        if (ogf == null) {
            started = true;
            ogf = new CirSim(this);
            ogf.init(null);
            repaint();
        }
    }

    public void toggleSwitch(int x) {
        ogf.toggleSwitch(x);
    }

    @Override
    public void paint(Graphics g) {
        String s = "Applet is open in a separate window.";
        if (!started)
            s = "Applet is starting.";
        else if (ogf == null)
            s = "Applet is finished.";
        else if (ogf.useFrame)
            ogf.triggerShow();
        g.drawString(s, 10, 30);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
        showFrame();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (ogf != null)
            ogf.componentResized(e);
    }

    @Override
    public void destroy() {
        if (ogf != null)
            ogf.dispose();
        ogf = null;
        repaint();
    }
};

