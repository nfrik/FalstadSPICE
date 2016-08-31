/*
 * Code by Federico García García.
 * 08/11/2015.
 * 
 * Frame that shows the About info.
 */

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AboutFrame extends JDialog implements WindowListener {
	private final String TITLE = "About CircuitMod";
	private final String NAMES_TITLE = "Brought to you by:";
	private final String [] NAMES  = {"federoggio", "munukuntla4313", "fgg192"};
	private final String NAME_ORIGINAL_TITLE = "Original code by:";
	private final String NAME_ORIGINAL = "Paul Falstad";
	
	private final String LICENSE_TITLE   = "GNU GENERAL PUBLIC LICENSE";
	private final String LICENSE_VERSION = "Version 3, 29 June 2007";
	private final String LICENSE         =
											"<HTML>"
											+"Copyright (C) 2007 Free Software Foundation, Inc. "
											+"&lt;http://fsf.org/&gt;"
											+"<br>"
											+"Everyone is permitted to copy and distribute verbatim copies"
											+"<br>"
											+"of this license document, but changing it is not allowed."
											+"</HTML>";
	
	private final String WEBSITE_TITLE = "Website";
	private final String WEBSITE = "http://circuitmod.sourceforge.net/";
	
	private CirSim cirSim;
	
	public AboutFrame(CirSim cirSim) {
		// Set parent
		super(cirSim);
		this.cirSim = cirSim;
		
		// Set frame properties.
		setTitle(TITLE);
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		
		// Get default Font
		Font font = getFont();
		
		// Create main panel.
		JPanel panelMain = new JPanel();
		final int border = 16;
		panelMain.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
		
		// Create panel with program name and icon.
		JPanel panelTitle = new JPanel();
		
		ImageIcon img = new ImageIcon(getClass().getResource("images/about.png"));
		JLabel labelImage = new JLabel(img);
		panelTitle.add(labelImage);
		
		JLabel labelProgramName = new JLabel(cirSim.PROGRAM_NAME+"         ");
		labelProgramName.setFont(new Font("Serif", Font.BOLD, font.getSize()*2)); // Set big bold serif font
		panelTitle.add(labelProgramName);
		
		panelMain.add(panelTitle);
		
		// Create panel with names.
		JPanel panelNames = new JPanel();
		String names = "";
		
		for(String s : NAMES) {
			names += s+"<br>";
		}
		
		// We use HTML for new lines.
		JLabel labelNamesTitle = new JLabel(
				"<html>"
				+ "<center>"
				+ "<b>"+NAMES_TITLE+"</b>"
				+ "<br>"
				+ names
				+ "<br>"
				+ "<b>"+NAME_ORIGINAL_TITLE+"</b>"
				+ "<br>"
				+ NAME_ORIGINAL
				+ "</center>"
				+ "</html>");
		panelNames.add(labelNamesTitle);
		
		panelMain.add(panelNames);
		
		// Create panel with license.
		JPanel panelLicense = new JPanel();
		
		JLabel labelLicense = new JLabel(
				"<html>"
				+ "<center>"
				+ "<b>"+LICENSE_TITLE+"</b>"
				+ "<br>"
				+ LICENSE_VERSION
				+ "</center>"
				+"<br>"
				+ LICENSE
				+ "</html>");
		
		panelLicense.add(labelLicense);
		
		panelMain.add(panelLicense);
		
		// Create panel with website.
		JPanel panelWebsite = new JPanel();

		JLabel labelWebsite = new JLabel();
		goToWebsite(labelWebsite, WEBSITE, WEBSITE_TITLE, WEBSITE);
		panelWebsite.add(labelWebsite);
		
		panelMain.add(panelWebsite);
		
		// Create panel with button
		JPanel panelButton = new JPanel();
		JButton button = new JButton("   Ok   ");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});
		
		panelButton.add(button);
		
		panelMain.add(panelButton);
		
		// Add main panel to frame.
		add(panelMain);

		// Show frame.
		pack();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(((int)d.getWidth()-getWidth())/2, ((int)d.getHeight()-getHeight())/2);
		setVisible(true);
	}
	
	private void close() {
		cirSim.setEnabled(true);
		dispose();
	}
	
	private void goToWebsite(JLabel website, final String url, String title, String text) {
        website.setText("<html><center><b>"+title+"</b></center><a href=\"\">"+text+"</a></html>");
        website.setCursor(new Cursor(Cursor.HAND_CURSOR));
        website.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                    try {
                            Desktop.getDesktop().browse(new URI(url));
                    } catch (Exception ex) {
                            ex.printStackTrace();
                    }
            }
        });
    }
	
	
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// Enable cirSim frame and dispose About.
		close();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
