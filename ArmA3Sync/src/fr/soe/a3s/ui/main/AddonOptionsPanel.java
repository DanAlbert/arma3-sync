package fr.soe.a3s.ui.main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImageResizer;
import fr.soe.a3s.ui.ImageResizer.Resizing;
import fr.soe.a3s.ui.UIConstants;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class AddonOptionsPanel extends JPanel implements UIConstants {

	private final Facade facade;
	private JScrollPane scrollPane1, scrollPane2;
	private JList directoryList1, directoryList2;
	private JButton add, delete, downAddonPriority, upAddonPriority,
			resetAddonPriority;
	private JButton upDirectoryPriority;
	private JButton downDirectoryPriority;
	/* Services */
	private final AddonService addonService = new AddonService();
	private final ProfileService profileService = new ProfileService();
	private JButton topAddonPriority;

	public AddonOptionsPanel(Facade facade) {
		this.facade = facade;
		this.facade.setAddonOptionsPanel(this);
		this.setLayout(new BorderLayout());

		Box vertBox1 = Box.createVerticalBox();
		vertBox1.add(Box.createVerticalStrut(10));

		JPanel centerPanel = new JPanel();
		GridLayout grid1 = new GridLayout(2, 1);
		centerPanel.setLayout(grid1);
		vertBox1.add(centerPanel);
		this.add(vertBox1, BorderLayout.CENTER);

		JPanel addonSearchDirectoriesPanel = new JPanel();
		addonSearchDirectoriesPanel.setLayout(new BorderLayout());
		{
			JPanel list1Panel = new JPanel();
			list1Panel.setLayout(new BorderLayout());
			list1Panel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(),
					"Addon Search Directories"));
			directoryList1 = new JList();
			scrollPane1 = new JScrollPane(directoryList1);
			scrollPane1.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));
			list1Panel.add(scrollPane1, BorderLayout.CENTER);
			addonSearchDirectoriesPanel.add(list1Panel, BorderLayout.CENTER);
		}
		{
			Box vertBox2 = Box.createVerticalBox();
			vertBox2.add(Box.createVerticalStrut(15));
			add = new JButton();
			ImageIcon addIcon = new ImageIcon(
					ImageResizer.resizeToScreenResolution(ADD, Resizing.SMALL));
			add.setIcon(addIcon);
			vertBox2.add(add);
			delete = new JButton();
			ImageIcon deleteIcon = new ImageIcon(
					ImageResizer.resizeToScreenResolution(DELETE,
							Resizing.SMALL));
			delete.setIcon(deleteIcon);
			vertBox2.add(delete);
			upDirectoryPriority = new JButton();
			ImageIcon upIcon = new ImageIcon(
					ImageResizer.resizeToScreenResolution(UP, Resizing.SMALL));
			upDirectoryPriority.setIcon(upIcon);
			vertBox2.add(upDirectoryPriority);
			downDirectoryPriority = new JButton();
			ImageIcon downIcon = new ImageIcon(
					ImageResizer.resizeToScreenResolution(DOWN, Resizing.SMALL));
			downDirectoryPriority.setIcon(downIcon);
			vertBox2.add(downDirectoryPriority);
			addonSearchDirectoriesPanel.add(vertBox2, BorderLayout.EAST);
		}
		centerPanel.add(addonSearchDirectoriesPanel, BorderLayout.CENTER);

		JPanel addonPrioritiesPanel = new JPanel();
		addonPrioritiesPanel.setLayout(new BorderLayout());
		{
			JPanel list2Panel = new JPanel();
			list2Panel.setLayout(new BorderLayout());
			list2Panel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(), "Addon Priorities"));
			directoryList2 = new JList();
			directoryList2
					.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			scrollPane2 = new JScrollPane(directoryList2);
			scrollPane2.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));
			list2Panel.add(scrollPane2);
			addonPrioritiesPanel.add(list2Panel, BorderLayout.CENTER);
		}
		{
			Box vertBox2 = Box.createVerticalBox();
			vertBox2.add(Box.createVerticalStrut(15));
			resetAddonPriority = new JButton();
			ImageIcon resetIcon = new ImageIcon(
					ImageResizer.resizeToScreenResolution(DELETE,
							Resizing.SMALL));
			resetAddonPriority.setIcon(resetIcon);
			vertBox2.add(resetAddonPriority);
			topAddonPriority = new JButton();
			ImageIcon topIcon = new ImageIcon(
					ImageResizer.resizeToScreenResolution(TOP, Resizing.SMALL));
			topAddonPriority.setIcon(topIcon);
			vertBox2.add(topAddonPriority);
			upAddonPriority = new JButton();
			ImageIcon upIcon = new ImageIcon(
					ImageResizer.resizeToScreenResolution(UP, Resizing.SMALL));
			upAddonPriority.setIcon(upIcon);
			vertBox2.add(upAddonPriority);
			downAddonPriority = new JButton();
			ImageIcon downIcon = new ImageIcon(
					ImageResizer.resizeToScreenResolution(DOWN, Resizing.SMALL));
			downAddonPriority.setIcon(downIcon);
			vertBox2.add(downAddonPriority);
			addonPrioritiesPanel.add(vertBox2, BorderLayout.EAST);
		}
		centerPanel.add(addonPrioritiesPanel, BorderLayout.CENTER);

		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonAddPerformed();
			}
		});
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonDeletePerformed();
			}
		});
		upDirectoryPriority.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				upDirectoryPriorityPerformed();
			}
		});
		downDirectoryPriority.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downDirectoryPriorityPerformed();
			}
		});
		resetAddonPriority.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetAddonPriorityPerformed();
			}
		});
		topAddonPriority.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				topAddonPrioriyPerformed();
			}
		});
		upAddonPriority.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				upAddonPrioriyPerformed();
			}
		});
		downAddonPriority.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downAddonPriorityPerformed();
			}
		});

		setContextualHelp();
	}

	private void setContextualHelp() {

		add.setToolTipText("Add a new addon search directory");
		delete.setToolTipText("Delete the selected directory");
		upDirectoryPriority.setToolTipText("Up directory search priority");
		downDirectoryPriority.setToolTipText("Down directory search priority");
		resetAddonPriority.setToolTipText("Reset addon launch priority");
		topAddonPriority.setToolTipText("Move on top addon launch priority");
		upAddonPriority.setToolTipText("Up addon launch priority");
		downAddonPriority.setToolTipText("Down addon launch priority");
	}

	public void init() {

		/* Addon search directories */
		updateAddonSearchDirectories();

		/* Addon priorities */
		updateAddonPriorities();
	}

	private void buttonAddPerformed() {

		String lastPath = profileService.getLastAddedAddonSearchDirecotry();
		JFileChooser fc = null;
		if (lastPath == null) {
			fc = new JFileChooser();
		} else {
			if ((new File(lastPath)).exists()) {
				fc = new JFileChooser(lastPath);
			} else {
				fc = new JFileChooser();
			}
		}

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(AddonOptionsPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (file != null) {
				String path = file.getAbsolutePath();
				int size = directoryList1.getModel().getSize();
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < size; i++) {
					list.add((String) directoryList1.getModel().getElementAt(i));
				}
				boolean contains = false;
				for (int i = 0; i < list.size(); i++) {
					String osName = System.getProperty("os.name");
					if (osName.contains("Windows")) {
						if (path.equalsIgnoreCase(list.get(i))) {
							contains = true;
						}
					} else {
						if (path.equals(list.get(i))) {
							contains = true;
						}
					}
				}

				if (!contains) {
					profileService.addAddonSearchDirectoryPath(path);
					profileService.setLastAddedAddonSearchDirectory(path);
					updateAddonSearchDirectories();
					updateAddonPriorities();
					facade.getAddonsPanel().updateAvailableAddons();
					facade.getAddonsPanel().updateAddonGroups();
					facade.getLaunchOptionsPanel().updateRunParameters();
				}
			}
		}
	}

	private void buttonDeletePerformed() {

		String path = (String) directoryList1.getSelectedValue();

		if (path != null) {
			profileService.removeAddonSearchDirectoryPath(path);
			updateAddonSearchDirectories();
			updateAddonPriorities();
			facade.getAddonsPanel().updateAvailableAddons();
			facade.getAddonsPanel().updateAddonGroups();
			facade.getLaunchOptionsPanel().updateRunParameters();
		}
	}

	public void updateAddonSearchDirectories() {

		List<String> set = profileService.getAddonSearchDirectoryPaths();
		Iterator iter = set.iterator();
		List<String> paths = new ArrayList<String>();
		while (iter.hasNext()) {
			paths.add((String) iter.next());
		}
		String[] tab = new String[paths.size()];
		int i = 0;
		for (String p : paths) {
			tab[i] = p;
			i++;
		}
		directoryList1.clearSelection();
		directoryList1.setListData(tab);
		int numberLigneShown = paths.size();
		directoryList1.setVisibleRowCount(numberLigneShown);
		directoryList1.setPreferredSize(directoryList1
				.getPreferredScrollableViewportSize());
		scrollPane1.updateUI();
	}

	public void updateAddonPriorities() {

		directoryList2.setEnabled(false);
		directoryList2.removeAll();
		List<String> list = addonService.getAddonsByPriorityList();
		if (list != null) {
			String[] addonNames = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				addonNames[i] = list.get(i);
			}
			directoryList2.clearSelection();
			directoryList2.setListData(addonNames);
			int numberLigneShown = list.size();
			directoryList2.setVisibleRowCount(numberLigneShown);
			directoryList2.setPreferredSize(directoryList2
					.getPreferredScrollableViewportSize());
			scrollPane2.updateUI();
		}
		directoryList2.setEnabled(true);
	}

	private void upDirectoryPriorityPerformed() {

		int index = directoryList1.getSelectedIndex();

		if (index != -1) {
			profileService.upDirectoryPriority(index);
			this.updateAddonSearchDirectories();
			facade.getAddonsPanel().updateAvailableAddons();
			if (index == 0) {
				directoryList1.setSelectedIndex(index);
			} else {
				directoryList1.setSelectedIndex(index - 1);
				int currentValue = scrollPane1.getVerticalScrollBar()
						.getValue();
				int max = scrollPane1.getVerticalScrollBar().getMaximum();
				int nbIndex = directoryList1.getModel().getSize();
				int increment = max / nbIndex;
				int newValue = currentValue - increment;
				scrollPane1.getVerticalScrollBar().setValue(newValue);
			}
		}
	}

	private void downDirectoryPriorityPerformed() {

		int index = directoryList1.getSelectedIndex();

		if (index != -1) {
			profileService.downDirectoryPriority(index);
			this.updateAddonSearchDirectories();
			facade.getAddonsPanel().updateAvailableAddons();
			if (index == directoryList1.getModel().getSize() - 1) {
				directoryList1.setSelectedIndex(index);
			} else {
				directoryList1.setSelectedIndex(index + 1);
				int currentValue = scrollPane1.getVerticalScrollBar()
						.getValue();
				int max = scrollPane1.getVerticalScrollBar().getMaximum();
				int nbIndex = directoryList1.getModel().getSize();
				int increment = max / nbIndex;
				int newValue = currentValue + increment;
				scrollPane1.getVerticalScrollBar().setValue(newValue);
			}
		}
	}

	private void resetAddonPriorityPerformed() {

		profileService.resetAddonPriority();
		this.updateAddonPriorities();
		facade.getLaunchOptionsPanel().updateRunParameters();
	}

	private void topAddonPrioriyPerformed() {

		int index = directoryList2.getSelectedIndex();

		if (index != -1) {
			profileService.topAddonPriority(index);
			this.updateAddonPriorities();
			facade.getLaunchOptionsPanel().updateRunParameters();
			directoryList2.setSelectedIndex(0);
			scrollPane2.getVerticalScrollBar().setValue(0);
		}
	}

	private void upAddonPrioriyPerformed() {

		int index = directoryList2.getSelectedIndex();

		if (index != -1) {
			profileService.upAddonPriority(index);
			this.updateAddonPriorities();
			facade.getLaunchOptionsPanel().updateRunParameters();
			if (index == 0) {
				directoryList2.setSelectedIndex(index);
			} else {
				directoryList2.setSelectedIndex(index - 1);
				int currentValue = scrollPane2.getVerticalScrollBar()
						.getValue();
				int max = scrollPane2.getVerticalScrollBar().getMaximum();
				int nbIndex = directoryList2.getModel().getSize();
				int increment = max / nbIndex;
				int newValue = currentValue - increment;
				scrollPane2.getVerticalScrollBar().setValue(newValue);
			}
		}
	}

	private void downAddonPriorityPerformed() {

		int index = directoryList2.getSelectedIndex();

		if (index != -1) {
			profileService.downAddonPriority(index);
			this.updateAddonPriorities();
			facade.getLaunchOptionsPanel().updateRunParameters();
			if (index == directoryList2.getModel().getSize() - 1) {
				directoryList2.setSelectedIndex(index);
			} else {
				directoryList2.setSelectedIndex(index + 1);
				int currentValue = scrollPane2.getVerticalScrollBar()
						.getValue();
				int max = scrollPane2.getVerticalScrollBar().getMaximum();
				int nbIndex = directoryList2.getModel().getSize();
				int increment = max / nbIndex;
				int newValue = currentValue + increment;
				scrollPane2.getVerticalScrollBar().setValue(newValue);
			}
		}
	}
}
