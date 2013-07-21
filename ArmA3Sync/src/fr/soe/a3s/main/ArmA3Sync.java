package fr.soe.a3s.main;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.soe.a3s.constant.MinimizationType;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.mainEditor.MainPanel;

public class ArmA3Sync {

	public static void main(String[] args) {

		/* Set ui properties */
		try {
			String osName = System.getProperty("os.name");
			if (osName.contains("Windows")) {
				javax.swing.UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} else if (osName.contains("Linux")) {
				javax.swing.UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			}
		} catch (Exception e) {
		}
		
		/* Single instance */
		if (!lockInstance()) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, "ArmA3Sync is already running.","ArmA3Sync", JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}

		Facade facade = new Facade();

		boolean devMode = false;
		MinimizationType mimimize = null;

		for (String arg : args) {
			if (arg.contains("-dev")) {
				devMode = true;
			}
			if (arg.contains("-taskbar")) {
				mimimize = MinimizationType.TASK_BAR;
			}
			if (arg.contains("-systemtray")) {
				mimimize = MinimizationType.TRAY;
			}
		}

		if (devMode) {
			facade.setDevMode(true);
			System.out.println("DevMode=true");
		} else {
			facade.setDevMode(false);
			System.out.println("DevMode=false");
		}

		/* Start */
		MainPanel mainPanel = new MainPanel(facade);
		mainPanel.drawGUI();
		mainPanel.init();
		if (MinimizationType.TASK_BAR.equals(mimimize)) {
			mainPanel.setVisible(false);
			mainPanel.setToTaskBar();
			mainPanel.setVisible(true);
			System.out.println("Minimize mode = Task bar");
		} else if (MinimizationType.TRAY.equals(mimimize)) {
			mainPanel.setVisible(false);
			mainPanel.setToTray();
			System.out.println("Minimize mode = System tray");
		} else {
			mainPanel.setVisible(true);
			System.out.println("Minimize mode = Normal");
		}

		// Check for updates
		mainPanel.checkForUpdate(false);
	}
	
	private static boolean lockInstance() {
	    
		final String path = "lock";
		try {
	        final File file = new File(path);
	        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
	        final FileLock fileLock = randomAccessFile.getChannel().tryLock();
	        if (fileLock != null) {
	            Runtime.getRuntime().addShutdownHook(new Thread() {
	                public void run() {
	                    try {
	                        fileLock.release();
	                        randomAccessFile.close();
	                        file.delete();
	                    } catch (Exception e) {
	                       System.out.println(("Unable to remove lock file: " + path));
	                    }
	                }
	            });
	            return true;
	        }
	    } catch (Exception e) {
	    	System.out.println(("Unable to create and/or lock file: " + path));
	    }
	    return false;
	}
}
