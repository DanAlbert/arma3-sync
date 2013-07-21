package fr.soe.a3s.dao;

import java.io.InputStream;
import java.io.StringWriter;

public class StreamReader extends Thread {
	
	private InputStream is;
	private StringWriter sw;
	private Process process;

	public StreamReader(Process process) {
		this.is = process.getInputStream();
		sw = new StringWriter();
	}

	public void run() {
		try {
			int c;
			while (((c = is.read()) != -1)){
				sw.write(c);
			}
			is.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (process!=null){
				process.destroy();
			}
		}
	}

	public String getResult() {
		return sw.toString();
	}
}
