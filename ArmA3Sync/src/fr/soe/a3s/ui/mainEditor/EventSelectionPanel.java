package fr.soe.a3s.ui.mainEditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public class EventSelectionPanel extends JDialog implements UIConstants {

	private Facade facade;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JList listEvents;
	private JScrollPane scrollPane;
	private RepositoryService repositoryService = new RepositoryService();
	private Map<Integer, EventDTO> mapEvents;
	private List<String> eventNames;

	public EventSelectionPanel(Facade facade) {
		super(facade.getMainPanel(), "Events", true);
		this.facade = facade;
		setResizable(false);
		this.setSize(345, 250);
		setIconImage(ICON);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);

		this.setLayout(new BorderLayout());
		Container contenu = getContentPane();
		{
			JPanel controlPanel = new JPanel();
			buttonOK = new JButton("OK");
			getRootPane().setDefaultButton(buttonOK);
			buttonCancel = new JButton("Cancel");
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonOK);
			controlPanel.add(buttonCancel);
			contenu.add(controlPanel, BorderLayout.SOUTH);
		}
		{
			JPanel topPanel = new JPanel();
			topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel label = new JLabel();
			label.setText("  Get addon group from event");
			topPanel.add(label);
			contenu.add(topPanel, BorderLayout.NORTH);
		}
		{
			JPanel centerPanel = new JPanel();
			contenu.add(centerPanel, BorderLayout.CENTER);
			centerPanel.setLayout(new BorderLayout());
			listEvents = new JList();
			scrollPane = new JScrollPane(listEvents);
			scrollPane.setColumnHeader(null);
			scrollPane.setBorder(BorderFactory
					.createEtchedBorder(BevelBorder.LOWERED));
			centerPanel.add(scrollPane);
		}
		{
			JPanel eastPanel = new JPanel();
			JPanel westPanel = new JPanel();
			contenu.add(eastPanel, BorderLayout.EAST);
			contenu.add(westPanel, BorderLayout.WEST);
		}
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonOKPerformed();
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonCancelPerformed();
			}
		});
	}

	public void init() {

		mapEvents = new LinkedHashMap<Integer, EventDTO>();
		eventNames = new ArrayList<String>();

		List<RepositoryDTO> repositoryDTOs = repositoryService
				.getRepositories();
		for (RepositoryDTO repositoryDTO : repositoryDTOs) {
			try {
				List<EventDTO> list = repositoryService.getEvents(repositoryDTO
						.getName());
				if (list!=null){
					for (EventDTO eventDTO : list) {
						eventNames.add(eventDTO.getName());
						mapEvents.put(eventNames.size() - 1, eventDTO);
					}
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		String[] tab = new String[eventNames.size()];
		for (int j = 0; j < tab.length; j++) {
			tab[j] = eventNames.get(j);
		}
		listEvents.setListData(tab);
	}

	private void buttonOKPerformed() {

		int index = listEvents.getSelectedIndex();

		if (index == -1 || (index > listEvents.getVisibleRowCount())) {
			this.dispose();
		}else {
			EventDTO eventDTO = mapEvents.get(index);
			//List<String> listAddonNames = eventDTO.getAddonNames();
//			for (Iterator<String> iter = eventDTO.getAddonNames().keySet().iterator() ; iter.hasNext() ; ){
//				
//			}
			facade.getAddonsPanel().createGroupFromEvent(eventDTO.getName(),eventDTO.getAddonNames());
			this.dispose();
		}
	}

	private void buttonCancelPerformed() {
		this.dispose();
	}

}
