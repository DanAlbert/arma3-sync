package fr.soe.a3s.service;

import fr.soe.a3s.dao.PreferencesDAO;
import fr.soe.a3s.domain.Preferences;
import fr.soe.a3s.dto.configuration.PreferencesDTO;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;

public class PreferencesService {

	private static final PreferencesDAO preferencesDAO = new PreferencesDAO();

	public void read() throws LoadingException {
		preferencesDAO.read();
	}

	public void write() throws WritingException {
		preferencesDAO.write();
	}

	public PreferencesDTO getPreferences() {
		PreferencesDTO preferencesDTO = transformPreferences2DTO(preferencesDAO
				.getPreferences());
		return preferencesDTO;
	}

	public void setPreferences(PreferencesDTO preferencesDTO) {
		Preferences preferences = transformDTO2Preferences(preferencesDTO);
		preferencesDAO.setPreferences(preferences);
	}

	private Preferences transformDTO2Preferences(PreferencesDTO preferencesDTO) {

		final Preferences preferences = new Preferences();
		preferences.setLaunchPanelGameLaunch(preferencesDTO
				.getLaunchPanelGameLaunch());
		preferences.setLaunchPanelMinimized(preferencesDTO
				.getLaunchPanelMinimized());
		preferences.setLookAndFeel(preferencesDTO.getLookAndFeel());
		return preferences;
	}

	private PreferencesDTO transformPreferences2DTO(Preferences preferences) {

		final PreferencesDTO preferencesDTO = new PreferencesDTO();
		preferencesDTO.setLaunchPanelGameLaunch(preferences
				.getLaunchPanelGameLaunch());
		preferencesDTO.setLaunchPanelMinimized(preferences
				.getLaunchPanelMinimized());
		preferencesDTO.setLookAndFeel(preferences.getLookAndFeel());
		return preferencesDTO;
	}

}
