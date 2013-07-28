package fr.soe.a3sUpdater.dao;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.io.output.CountingOutputStream;

import fr.soe.a3sUpdater.controller.Observable;
import fr.soe.a3sUpdater.controller.Observateur;

public class DownloadCountingOutputStream extends CountingOutputStream implements Observable{

	private int count=0;
	private ArrayList<Observateur> listObservateur = new ArrayList<Observateur>();
	
	public DownloadCountingOutputStream(OutputStream out) {
		super(out);
	}

    @Override
    protected void afterWrite(int n) throws IOException {
		super.afterWrite(n);
		System.out.println(getCount());
		count = getCount();
		updateObservateur();
    }

	@Override
	public void addObservateur(Observateur obs) {
		this.listObservateur.add(obs);
	}

	@Override
	public void delObservateur() {
		this.listObservateur = new ArrayList<Observateur>();
		
	}

	@Override
	public void updateObservateur() {
		for (Observateur obs : this.listObservateur) {
			obs.update(this.count);
		}
	}
	
}
