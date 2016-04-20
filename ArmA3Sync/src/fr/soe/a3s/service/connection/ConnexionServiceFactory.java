package fr.soe.a3s.service.connection;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dao.repository.RepositoryDAO;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.BitTorrent;
import fr.soe.a3s.domain.Ftp;
import fr.soe.a3s.domain.Http;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;

public class ConnexionServiceFactory {

	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();

	public static ConnexionService getServiceForAutoconfigURLimportation(
			AbstractProtocole protocol) throws CheckException {

		if (protocol instanceof Ftp) {
			return new FtpService();
		} else if (protocol instanceof Http) {
			return new HttpService();
		} else {
			throw new CheckException("Unknown or unsupported protocol.");
		}
	}

	public static ConnexionService getServiceForRepositoryManagement(
			String repositoryName) throws RepositoryException, CheckException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		if (repository.getProtocol() instanceof Ftp) {
			return new FtpService();
		} else if (repository.getProtocol() instanceof Http) {
			return new HttpService();
		} else if (repository.getProtocol() instanceof BitTorrent) {
			if (repository.getProtocol().getProtocolType()
					.equals(ProtocolType.FTP_BITTORRENT)) {
				return new BitTorrentService(new FtpService());
			} else if (repository.getProtocol().getProtocolType()
					.equals(ProtocolType.HTTP_BITTORRENT)) {
				return new BitTorrentService(new HttpService());
			} else if (repository.getProtocol().getProtocolType()
					.equals(ProtocolType.HTTPS_BITTORRENT)) {
				return new BitTorrentService(new HttpService());
			} else {
				throw new CheckException(
						"Unknown or unsupported protocol for repository "
								+ repositoryName + ".");
			}
		} else {
			throw new CheckException(
					"Unknown or unsupported protocol for repository "
							+ repositoryName + ".");
		}
	}

	public static ConnexionService getServiceForFilesSynchronization(
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
		} else if (repository.getProtocol() instanceof BitTorrent) {
			return new BitTorrentService(numberOfConnections);
		} else {
			throw new CheckException(
					"Unknown or unsupported protocol for repository "
							+ repositoryName + ".");
		}
	}

	public static ConnexionService getServiceForRepositoryUpload(
			String repositoryName) throws RepositoryException, CheckException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		if (repository.getUploadProtocole() instanceof Ftp) {
			return new FtpService();
		} else {
			throw new CheckException(
					"Unknown or unsupported protocol for repository "
							+ repositoryName + ".");
		}
	}
}
