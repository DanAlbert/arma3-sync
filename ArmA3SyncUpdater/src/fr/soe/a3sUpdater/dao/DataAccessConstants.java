package fr.soe.a3sUpdater.dao;

public interface DataAccessConstants {
	
	/** Resources */
	String INSTALLATION_PATH = System.getProperty("user.dir");
	String HOME_PATH = System.getProperty("user.home");

	/** FTP */
	String UPDATE_REPOSITORY = "/ArmA3/ArmA3Sync/download";
	String UPDATE_REPOSITORY_DEV = "/ArmA3/ArmA3Sync/development";
	String UPDTATE_REPOSITORY_ADRESS = "www.sonsofexiled.fr";
	int UPDTATE_REPOSITORY_PORT = 21;
	String UPDTATE_REPOSITORY_LOGIN = "anonymous";
	String UPDTATE_REPOSITORY_PASS = "";

}
