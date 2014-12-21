package fr.soe.a3s.service;

import fr.soe.a3s.constant.Protocol;
import fr.soe.a3s.dao.RepositoryDAO;
import fr.soe.a3s.domain.Ftp;
import fr.soe.a3s.domain.Http;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.RepositoryException;

public class ConnexionServiceFactory {

	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();

	public static AbstractConnexionService getServiceFromUrl(
			String urlWithProtocole) throws CheckException {

		if (urlWithProtocole.toLowerCase().trim()
				.contains(Protocol.FTP.getPrompt())) {
			return new FtpService();
		} else if (urlWithProtocole.toLowerCase().trim()
				.contains(Protocol.HTTP.getPrompt())) {
			return new HttpService();
		} else {
			throw new CheckException("Invalid url or unsupported protocole."
					+ "\n" + "Url must start with " + Protocol.FTP.getPrompt()
					+ " or " + Protocol.HTTP.getPrompt());
		}
	}

	public static AbstractConnexionService getServiceFromRepository(
			String repositoryName) throws RepositoryException, CheckException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		} else if (repository.getProtocole() instanceof Ftp) {
			return new FtpService();
		} else if (repository.getProtocole() instanceof Http) {
			return new HttpService();
		} else {
			throw new CheckException(
					"Unknown or unsupported protocole for repository "
							+ repositoryName + ".");
		}
	}

	public static AbstractConnexionService getServiceFromRepositoryMultiConnections(
			String repositoryName) throws RepositoryException, CheckException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		ServerInfo serverInfo = repository.getServerInfo();
		int numberOfConnections = 0;
		if (serverInfo != null) {
			numberOfConnections = serverInfo.getNumberOfConnections();
		}
		if (numberOfConnections == 0) {
			numberOfConnections = 1;
		}

		if (repository.getProtocole() instanceof Ftp) {
			return new FtpService(numberOfConnections);
		} else if (repository.getProtocole() instanceof Http) {
			return new HttpService(numberOfConnections);
		} else {
			throw new CheckException(
					"Unknown or unsupported protocole for repository "
							+ repositoryName + ".");
		}
	}

	public static AbstractConnexionService getRepositoryUploadServiceFromRepository(
			String repositoryName) throws RepositoryException, CheckException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		} else if (repository.getRepositoryUploadProtocole() instanceof Ftp) {
			return new FtpService();
		} else if (repository.getRepositoryUploadProtocole() instanceof Http) {
			return new HttpService();
		} else {
			throw new CheckException(
					"Unknown or unsupported protocole for repository "
							+ repositoryName + ".");
		}
	}
}
