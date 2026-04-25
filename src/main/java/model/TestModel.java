package model;

public class TestModel {

	public static void main(String[] args) {
		Utente Rossi = new Medico("Rossi", "1234", "M001", "Mario", "Rossi", "agenda1", null, "Cardiologia", "Cardiologia");
		Utente Bianchi = new Amministratore("Bianchi", "5678", "A001");

		System.out.println("Login di Rossi: " + Rossi.login("Rossi", "1234", "M001"));
		System.out.println("Login di Bianchi: " + Bianchi.login("Bianchi", "5678", "4313"));


	}

}
