package fr.soe.a3s.ui.mainEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public class InfoUpdatedRepositoryPanel extends JDialog implements UIConstants {

	private Facade facade;
	private JButton buttonOK;
	private JButton buttonUpdateNow;
	private JList list;
	private JScrollPane scrollPane;
	private RepositoryService repositoryService;

	public InfoUpdatedRepositoryPanel(Facade facade) {

		super(facade.getMainPanel(), "Repository", false);
		this.facade = facade;
		this.facade.setInfoUpdatedRepositoryPanel(this);
		this.setResizable(false);
		this.setSize(400, 256);
		setIconImage(ICON);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);
		this.setLayout(new BorderLayout());
		{
			JPanel controlPanel = new JPanel();
			buttonUpdateNow = new JButton("Update now");
			buttonUpdateNow.setEnabled(false);
			buttonOK = new JButton("OK");
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonUpdateNow);
			controlPanel.add(buttonOK);
			this.add(controlPanel, BorderLayout.SOUTH);
		}
		{
			JPanel centerPanel = new JPanel();
			centerPanel.setLayout(new BorderLayout());
			centerPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEmptyBorder(),
					"The following repositories have been updated:"));
			this.add(centerPanel, BorderLayout.CENTER);
			{
				list = new JList();
				scrollPane = new JScrollPane(list);
				scrollPane.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				centerPanel.add(scrollPane, BorderLayout.CENTER);
			}
		}
		{
			JPanel sidePanel1 = new JPanel();
			this.add(sidePanel1, BorderLayout.NORTH);
		}
		buttonUpdateNow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonUpdateNowPerformed();
			}
		});
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonOKPerformed();
			}
		});
	}

	public void init(List<String> repositoryNames) {

		String[] tab = new String[repositoryNames.size()];
		for (int i = 0; i < repositoryNames.size(); i++) {
			tab[i] = repositoryNames.get(i);
		}
		list.setListData(tab);

		if (tab.length != 0) {
			list.setSelectedIndex(0);
			buttonUpdateNow.setEnabled(true);
			getRootPane().setDefaultButton(buttonUpdateNow);
		}
	}

	private void buttonUpdateNowPerformed() {
		int index = list.getSelectedIndex();
		if (index !=-1){
			this.dispose();
			String repositoryName = (String) list.getModel().getElementAt(index);
			facade.getMainPanel().openRepository(repositoryName, null,true);
		}
	}

	private void buttonOKPerformed() {
		this.dispose();
	}
}
