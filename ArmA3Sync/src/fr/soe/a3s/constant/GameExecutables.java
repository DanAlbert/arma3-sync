package fr.soe.a3s.constant;

public enum GameExecutables {

	GAME("arma3.exe"),GAME_x64("arma3_x64.exe"),WIN_SERVER("arma3server.exe"),WIN_SERVER_x64("arma3server_x64.exe") ,LINUX_SERVER(""), STEAM(
			"steam.exe"), BATTLEYE("arma3battleye.exe");

	private String description;

	private GameExecutables(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}
