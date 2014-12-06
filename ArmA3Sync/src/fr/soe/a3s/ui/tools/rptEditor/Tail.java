package fr.soe.a3s.ui.tools.rptEditor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.JTextArea;

/**
 * Implements console-based log file tailing, or more specifically, tail
 * following: it is somewhat equivalent to the unix command "tail -f"
 */
public class Tail implements LogFileTailerListener {
	/**
	 * The log file tailer
	 */
	private LogFileTailer tailer;

	private JTextArea textArea;

	public Tail(File file,JTextArea textArea) {
		this.textArea = textArea;
		readFile(file);
		tailer = new LogFileTailer(file, 1000, false);
		tailer.addLogFileTailerListener(this);
		
	}

	/**
	 * A new line has been added to the tailed log file
	 * 
	 * @param line
	 *            The new line that has been added to the tailed log file
	 */
	public void newLogFileLine(String line) {
		//System.out.println(line);
		textArea.append(line + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
//		areaRows++;
//		textArea.setRows(areaRows);
	}
	
	public void readFile(File file){
		try {
			FileInputStream fin = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			new DataInputStream(fin).readFully(buffer);
			fin.close();
			String s = new String(buffer);
			textArea.append(s + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public void start() {
		tailer.start();
	}

	public void stop() {
		tailer.interrupt();
	}

	/**
	 * Command-line launcher
	 */
//	public static void main(String[] args) {
//	
//		if (args.length < 1) {
//			System.out.println("Usage: Tail <filename>");
//			System.exit(0);
//		}
//		Tail tail = new Tail(args[0]);
//	}
}