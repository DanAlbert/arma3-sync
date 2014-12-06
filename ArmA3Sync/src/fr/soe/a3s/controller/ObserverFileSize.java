package fr.soe.a3s.controller;

import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public interface ObserverFileSize {
	public void update(long value,SyncTreeNodeDTO node);
}
