/**
 * Created by NF on 9/20/2016.
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AffineTransformTest extends JPanel
{
    private static final long serialVersionUID = 1L;
    private AffineTransformTest()
    {
        super(null);
        setOpaque(true);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600,600));
    }

    @Override
    protected void paintComponent(Graphics g1) {
        super.paintComponent(g1);
        Shape r= new Ellipse2D.Double(5,380,400,200);
        double cx= r.getBounds2D().getCenterX();
        double cy= r.getBounds2D().getCenterY();
        Graphics2D g=(Graphics2D)g1;
        g.setColor(Color.BLACK);
        AffineTransform old= g.getTransform();
        g.drawLine((int)cx-10, (int)cy, (int)cx+10, (int)cy);
        g.drawLine((int)cx, (int)cy-10, (int)cx, (int)cy+10);
        for(double zoom=1; zoom>=0.1; zoom-=0.1)
        {


            AffineTransform tr2 =AffineTransform.getTranslateInstance(-cx, -cy);
            AffineTransform  tr= AffineTransform.getScaleInstance(zoom,zoom);
            tr.concatenate(tr2); tr2=tr;
            tr =AffineTransform.getTranslateInstance(cx, cy);
            tr.concatenate(tr2); tr2=tr;

            tr= new AffineTransform(old);
            tr.concatenate(tr2);  tr2=tr;

            g.setTransform(tr2);



            g.draw(r);
            g.setTransform(old);
        }
    }


    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, new AffineTransformTest());
    }
}
