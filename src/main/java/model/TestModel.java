package model;

public class TestModel {

	public static void main(String[] args) {
		//Creazione oggetto Amministratore
		Amministratore admin = new Amministratore(
				"MNeri",
				"admin",
				"AD4389",
				"Matteo",
				"Neri"
		);

		//Creazione oggetto Medico
		Medico medico = new Medico(
				"DRossi",
				"medico",
				"MD1234",
				"Giulia",
				"Rossi"
		);

		admin.anagraficaPaziente();
	}

}
