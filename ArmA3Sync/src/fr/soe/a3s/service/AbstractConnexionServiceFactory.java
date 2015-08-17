package fr.soe.a3s.service;

import fr.soe.a3s.dao.repository.RepositoryDAO;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.Ftp;
import fr.soe.a3s.domain.Http;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;

public class AbstractConnexionServiceFactory {

	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();

	public static AbstractConnexionService getServiceFromProtocol(
			AbstractProtocole protocol) throws CheckException {

		if (protocol instanceof Ftp) {
			return new FtpService();
		} else if (protocol instanceof Http) {
			return new HttpService();
		} else {
			throw new CheckException("Unknown or unsupported protocol.");
		}
	}

	public static AbstractConnexionService getServiceFromRepository(
			String repositoryName) throws RepositoryException, CheckException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		} else if (repository.getProtocol() instanceof Ftp) {
			return new FtpService();
		} else if (repository.getProtocol() instanceof Http) {
			return new HttpService();
		} else {
			throw new CheckException(
					"Unknown or unsupported protocol for repository "
							+ repositoryName + ".");
		}
	}

	public static AbstractConnexionService getServiceFromRepositoryMultiConnections(
			String repositoryName, int numberOfConnections)
			throws RepositoryException, CheckException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		if (repository.getProtocol() instanceof Ftp) {
			return new FtpService(numberOfConnections);
		} else if (repository.getProtocol() instanceof Http) {
			return new HttpService(numberOfConnections);
		} else {
			throw new CheckException(
					"Unknown or unsupported protocol for repository "
							+ repositoryName + ".");
		}
	}

	public static AbstractConnexionService getRepositoryUploadServiceFromRepository(
			String repositoryName) throws RepositoryException, CheckException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		} else if (repository.getRepositoryUploadProtocole() instanceof Ftp) {
			return new FtpService();
		} else if (repository.getRepositoryUploadProtocole() instanceof Http) {
			return new HttpService();
		} else {
			throw new CheckException(
					"Unknown or unsupported protocol for repository "
							+ repositoryName + ".");
		}
	}
}
