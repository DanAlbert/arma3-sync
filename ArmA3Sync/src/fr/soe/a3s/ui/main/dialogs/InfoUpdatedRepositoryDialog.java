package fr.soe.a3s.ui.main.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.RepositoryPanel;

public class InfoUpdatedRepositoryDialog extends AbstractDialog {

	private JList list;
	private JScrollPane scrollPane;

	public InfoUpdatedRepositoryDialog(Facade facade) {
		super(facade, "Repository", true);
		this.setResizable(true);

		{
			buttonOK.setText("Update now");
			getRootPane().setDefaultButton(buttonOK);
			buttonCancel.setText("Close");
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

		this.setMinimumSize(new Dimension(400, 256));
		this.setPreferredSize(new Dimension(400, 256));
		this.pack();
		this.setLocationRelativeTo(facade.getMainPanel());
	}

	public void init(List<String> repositoryNames) {

		String[] tab = new String[repositoryNames.size()];
		for (int i = 0; i < repositoryNames.size(); i++) {
			tab[i] = repositoryNames.get(i);
		}
		list.setListData(tab);

		if (tab.length != 0) {
			list.setSelectedIndex(0);
			buttonOK.setEnabled(true);
			getRootPane().setDefaultButton(buttonOK);
		}
	}

	@Override
	protected void buttonOKPerformed() {

		int index = list.getSelectedIndex();
		if (index != -1) {
			this.dispose();
			String repositoryName = (String) list.getModel()
					.getElementAt(index);
			if (repositoryName != null) {
				RepositoryPanel repositoryPanel = facade.getMainPanel()
						.openRepository(repositoryName);
				if (repositoryPanel != null) {
					repositoryPanel.download(repositoryName, null);
				}
			}
		}
	}

	@Override
	protected void buttonCancelPerformed() {
		this.dispose();
	}

	@Override
	protected void menuExitPerformed() {
		this.dispose();
	}
}
