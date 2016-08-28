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

		if (lookAndFeel.equals(LAF_DEFAULT.getName())) {
			return LAF_DEFAULT;
		} else if (lookAndFeel.equals(LAF_ALUMINIUM.getName())) {
			return LAF_ALUMINIUM;
		} else if (lookAndFeel.equals(LAF_GRAPHITE.getName())) {
			return LAF_GRAPHITE;
		} else if (lookAndFeel.equals(LAF_HIFI.getName())) {
			return LAF_HIFI;
		} else if (lookAndFeel.equals(LAF_NOIRE.getName())) {
			return LAF_NOIRE;
		} else {
			return null;
		}
	}
}
