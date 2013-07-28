package fr.soe.a3sUpdater.dao;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class XmlDAO implements DataAccessConstants {

	public String getVersion() throws DocumentException {
		String version = null;
		File file = new File(INSTALLATION_PATH + "/" + "a3s.xml");
		if (file.exists()){
			SAXReader reader = new SAXReader();
			Document documentLeaVersion = reader.read(file);
			Element root = documentLeaVersion.getRootElement();
			version = root.selectSingleNode("nom").getText();
		}
		return version;
	}

	public String getZipFileName() throws DocumentException {
		File file = new File(INSTALLATION_PATH + "/" + "a3s.xml");
		String fileName = null;
		if (file.exists()){
			SAXReader reader = new SAXReader();
			Document documentLeaVersion = reader.read(file);
			Element root = documentLeaVersion.getRootElement();
			fileName = root.selectSingleNode("file").getText();
		}
		return fileName;
	}

}
