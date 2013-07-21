package fr.soe.a3s.ui.repositoryEditor.outline;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.UIManager;

import org.netbeans.swing.outline.RenderDataProvider;

import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class RenderData implements RenderDataProvider {

	@Override
	public Color getBackground(Object arg0) {
		return null;
	}

	@Override
	public String getDisplayName(Object object) {
		return ((SyncTreeNodeDTO) object).getName();
	}

	@Override
	public Color getForeground(Object object) {
		SyncTreeNodeDTO f = (SyncTreeNodeDTO) object;
		if (f.isLeaf()) {
			return UIManager.getColor("controlShadow");
		}
		return null;
	}

	@Override
	public Icon getIcon(Object arg0) {
		return null;
	}

	@Override
	public String getTooltipText(Object object) {
		SyncTreeNodeDTO f = (SyncTreeNodeDTO) object;
		return f.getDestinationPath();
	}

	@Override
	public boolean isHtmlDisplayName(Object arg0) {
		return false;
	}

}
