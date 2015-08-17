package fr.soe.a3s.ui.repositoryEditor;

import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;

public class FileSizeComputer {

	public static long computeExpectedSize(SyncTreeLeafDTO leafDTO) {

		if (leafDTO.getComplete() == 0) {
			if (leafDTO.isCompressed()) {
				return leafDTO.getCompressedSize();
			} else {
				return leafDTO.getSize();
			}
		} else {
			return (long) (leafDTO.getSize() * (100 - leafDTO.getComplete()) / 100);
		}
	}
}
