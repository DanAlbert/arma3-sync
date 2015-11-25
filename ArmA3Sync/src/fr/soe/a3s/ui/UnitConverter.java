package fr.soe.a3s.ui;

import java.text.DecimalFormat;

public class UnitConverter {

	public static String convertSize(long size) {

		String response = "";
		if (size >= Math.pow(1024, 3)) {// GB
			double value = size / Math.pow(1024, 3);
			value = ((int) (value * 100.0)) / 100.0;
			response = Double.toString(value) + " GB";
		} else if (size >= Math.pow(1024, 2)) {// MB
			double value = size / Math.pow(1024, 2);
			value = ((int) (value * 100.0)) / 100.0;
			response = Double.toString(value) + " MB";
		} else if (size >= Math.pow(1024, 1)) {// KB
			double value = size / Math.pow(1024, 1);
			value = ((int) (value * 100.0)) / 100.0;
			response = Double.toString(value) + " KB";
		} else {// B
			double value = size / Math.pow(1024, 0);
			value = ((int) (value * 100.0)) / 100.0;
			response = Double.toString(value) + " B";
		}
		return response;
	}

	public static String convertSpeed(long speed) {

		String response = "";
		if (speed >= Math.pow(10, 9)) {// GB
			double value = speed / Math.pow(10, 9);
			response = Double.toString(((int) (value * 10.0)) / 10.0) + " GB/s";
		} else if (speed >= Math.pow(10, 6)) {// MB
			double value = speed / Math.pow(10, 6);
			response = Double.toString(((int) (value * 10.0)) / 10.0) + " MB/s";
		} else if (speed >= Math.pow(10, 3)) {// KB
			double value = speed / Math.pow(10, 3);
			response = Double.toString((int) value) + " KB/s";
		} else {// B
			double value = speed / Math.pow(10, 0);
			response = Double.toString((int) value) + " B/s";
		}
		return response;
	}

	public static String convertTime(long time) {

		int seconds = (int) (time) % 60;
		int minutes = (int) ((time / 60) % 60);
		int hours = (int) ((time / (60 * 60)) % 24);
		DecimalFormat formatter = new DecimalFormat("00");
		return String.format("%s:%s:%s", formatter.format(hours),
				formatter.format(minutes), formatter.format(seconds));
	}
}
