/*
 * Code by Federico García García.
 * 27/11/2015.
 * 
 * Class that allows to save and open files in .cmf (CircuitMod File) extension.
 * What it does is save the text created by Export in a .cmf file and read it back when opened.
 */

import java.awt.Frame;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SaveOpenDialog {
	private JFileChooser fc; // File chooser
	
	// File extension
	private final String FILE_EXTENSION = "cmf";
	private final FileFilter FILE_FILTER = new FileNameExtensionFilter("CircuitMod | *."+FILE_EXTENSION, FILE_EXTENSION);

	private String currentFilePath = null; // Path of current file.
	private String currentFileName = null; // Name of current file.
	private Frame parent;
	
	// Init
	public SaveOpenDialog(Frame parent) {
		// Set parent
		this.parent = parent;
		
		// Disable renaming of files
		Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
		
		// Create file chooser with custom file filter
		fc = new JFileChooser(){
		    @Override
		    public void approveSelection(){
		    	String s = getExtension(getSelectedFile().getAbsolutePath());
		    	File f = new File(s);
		        
		        if(f.exists() && getDialogType() == SAVE_DIALOG){
		            int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
		            switch(result){
		                case JOptionPane.YES_OPTION:
		                    super.approveSelection();
		                    return;
		                case JOptionPane.NO_OPTION:
		                    return;
		                case JOptionPane.CLOSED_OPTION:
		                    return;
		                case JOptionPane.CANCEL_OPTION:
		                    cancelSelection();
		                    return;
		            }
		        }
		        super.approveSelection();
		    }        
		};
		
		fc.setFileFilter(FILE_FILTER);
		
		UIManager.put("FileChooser.readOnly", old);
	}
	
	// Save file as. Returns true if saved, false otherwise.
	boolean saveAs(String s) {
		int accept = fc.showSaveDialog(parent);
		
		if(accept == JFileChooser.APPROVE_OPTION) {
			currentFilePath = getExtension(fc.getSelectedFile().getAbsolutePath());
			currentFileName = getPathName(currentFilePath);
			saveFile(s);
			return true;
		}
		
		return false;
	}
	
	// Save file. Returns true if saved, false otherwise.
	boolean save(String s) {
		if(currentFilePath == null) {
			return saveAs(s);
		}
		else {
			saveFile(s);
			return true;
		}
	}
	
	// Save file
	void saveFile(String s) {
		PrintWriter out;
		try {
			out = new PrintWriter(currentFilePath);
			out.println(s);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Open file
	String open(String s) {
		int accept = fc.showOpenDialog(parent);
		
		if(accept == JFileChooser.APPROVE_OPTION) {
			return load(fc.getSelectedFile());
		}
		
		return null;
	}
	
	String load(File f) {
		return load(f.getAbsolutePath());
	}
	
	String load(String s) {
		try {
			currentFilePath = s;
			currentFileName = getPathName(currentFilePath);
			
			return readFile(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            
	            line = br.readLine();
	        }
	        
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
	
	// Check if file ends with extension. If it doesn't, add it.
	private String getExtension(String s) {
		if(s.toLowerCase().endsWith("."+FILE_EXTENSION)) {
			return s;
		}

		return s+"."+FILE_EXTENSION;
	}
	
	// Get file name
	private String getPathName(String s) {
		if(s != null) {
			for(int i=s.length()-1; i>=0; i--) {
				if(s.charAt(i) == '/' || s.charAt(i) == '\'' || s.charAt(i) == '\\') {
					return s.substring(i+1, s.length());
				}
			}
		}
		
		return null;
	}
	
	// Get current file's name
	public String getFileName() {
		return currentFileName;
	}
}
