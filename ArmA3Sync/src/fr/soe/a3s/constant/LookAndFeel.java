package fr.soe.a3s.constant;

import com.jtattoo.plaf.acryl.AcrylLookAndFeel;

public enum LookAndFeel {
	LAF_DEFAULT("Default"), LAF_ALUMINIUM("Aluminium"), LAF_GRAPHITE("Graphite"), LAF_HIFI(
			"Hifi"), LAF_NOIRE("Noire");

	private String name;

	private LookAndFeel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static LookAndFeel getEnum(String lookAndFeel) {

		if (lookAndFeel.equals("Default")) {
			return LAF_DEFAULT;
		} else if (lookAndFeel.equals("Aluminium")) {
			return LAF_ALUMINIUM;
		} else if (lookAndFeel.equals("Graphite")) {
			return LAF_GRAPHITE;
		} else if (lookAndFeel.equals("Hifi")) {
			return LAF_HIFI;
		} else if (lookAndFeel.equals("Noire")) {
			return LAF_NOIRE;
		} else {
			return null;
		}
	}
}
