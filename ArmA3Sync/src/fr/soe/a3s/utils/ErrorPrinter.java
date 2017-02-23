package fr.soe.a3s.utils;

public class ErrorPrinter {

	public static String printRepositoryManagedError(String repositoryName,
			Exception ex) {

		String message = "Repository: " + repositoryName + " - Error: "
				+ ex.getMessage();
		System.out.println(message);
		return message;
	}

	public static String printRepositoryUnexpectedError(String repositoryName,
			Exception ex) {

		String message = "Repository: " + repositoryName + " - "
				+ "An unexpected error has occured:";
		System.out.println(message);
		ex.printStackTrace();
		return message;
	}
}
