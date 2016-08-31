import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;

/*
 * Code by Federico García García.
 * 06/12/2015.
 * 
 * Frame that shows the logo at startup.
 */


public class LogoFrame extends JDialog {
	public LogoFrame() {
		super();
		setUndecorated(true);
		setSize(400, 240);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2);
		
		setContentPane(new LogoFrameCanvas());
		setVisible(true);
	}
}

class LogoFrameCanvas extends JComponent {
	private BufferedImage img = null;
	
	public LogoFrameCanvas() {
		try {
			img = ImageIO.read(getClass().getResource("images/logo.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		if(img != null) {
			g2.drawImage(img, 0, 0, null);
		}
	}
}