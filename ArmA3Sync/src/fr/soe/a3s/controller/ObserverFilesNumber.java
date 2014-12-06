package fr.soe.a3s.controller;

import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public interface ObserverFilesNumber {
	public void update(SyncTreeNodeDTO downloadingNode);
}

