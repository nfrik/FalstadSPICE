
import java.awt.Color;
import java.awt.Graphics;
import java.util.StringTokenizer;

class LEDArrayElm extends ChipElm {
    public LEDArrayElm(int xx, int yy) {
        super(xx, yy);
    }

    public LEDArrayElm(int xa, int ya, int xb, int yb, int f,
                       StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
    }

    @Override
    String getChipName() {
        return "LED Array";
    }

    Color darkgreen;


    @Override
    void setupPins() {
        darkgreen = new Color(0, 30, 0);
        sizeX = 2;
        sizeY = 10;
        pins = new Pin[10];
        pins[0] = new Pin(0, SIDE_W, "i9");
        pins[1] = new Pin(1, SIDE_W, "i8");
        pins[2] = new Pin(2, SIDE_W, "i7");
        pins[3] = new Pin(3, SIDE_W, "i6");
        pins[4] = new Pin(4, SIDE_W, "i5");
        pins[5] = new Pin(5, SIDE_W, "i4");
        pins[6] = new Pin(6, SIDE_W, "i3");
        pins[7] = new Pin(7, SIDE_W, "i2");
        pins[8] = new Pin(8, SIDE_W, "i1");
        pins[9] = new Pin(9, SIDE_W, "i0");
    }

    @Override
    void draw(Graphics g) {
        drawChip(g);
        g.setColor(Color.green);
        int s = csize * 16;
        int p = csize * 5;
        int xl = x + cspc * 3;
        int yl = y;

        setColor(g, 0);
        g.fillRect(xl, yl + (s * 0), p * 2, p);

        setColor(g, 1);
        g.fillRect(xl, yl + (s * 1), p * 2, p);

        setColor(g, 2);
        g.fillRect(xl, yl + (s * 2), p * 2, p);

        setColor(g, 3);
        g.fillRect(xl, yl + (s * 3), p * 2, p);

        setColor(g, 4);
        g.fillRect(xl, yl + (s * 4), p * 2, p);

        setColor(g, 5);
        g.fillRect(xl, yl + (s * 5), p * 2, p);

        setColor(g, 6);
        g.fillRect(xl, yl + (s * 6), p * 2, p);

        setColor(g, 7);
        g.fillRect(xl, yl + (s * 7), p * 2, p);

        setColor(g, 8);
        g.fillRect(xl, yl + (s * 8), p * 2, p);

        setColor(g, 9);
        g.fillRect(xl, yl + (s * 9), p * 2, p);
    }

    void setColor(Graphics g, int p) {
        g.setColor(pins[p].value ? Color.green :
                false ? Color.white : darkgreen);
    }

    @Override
    int getPostCount() {
        return 10;
    }

    @Override
    int getVoltageSourceCount() {
        return 0;
    }

    @Override
    int getDumpType() {
        return 176;
    }
}
