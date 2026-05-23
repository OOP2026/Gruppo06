package controller;
import model.*;

import java.util.ArrayList;

/**
 * The type Controller.
 */
public class Controller {
	private ArrayList<Utente> utenteRegistrato;
	private Utente utenteLoggato;

	/**
	 * Instantiates a new Controller.
	 */
	public Controller() { //Blocco Costruttore
		//Inizializzazione lista
		utenteRegistrato = new ArrayList<>();

		//Inizializzazione classi
		Amministratore admin = new Amministratore(
				"MNeri",
				"admin",
				"AD4389",
				"Matteo",
				"Neri",
				"3456"
		);

		Medico medico = new Medico(
				"FRossi",
				"user",
				"DT6788998",
				"Francesco",
				"Rossi");

		utenteRegistrato.add(admin);
		utenteRegistrato.add(medico);
	}

	/**
	 * Registrazione boolean.
	 *
	 * @param login    the login
	 * @param password the password
	 * @return the boolean
	 */
	public boolean registrazione(String login, String password, String nome, String cognome, String pin, boolean isAdmin, String matricola) {
		for (Utente u : utenteRegistrato) {
			if (u.getLogin().equals(login)) {
				return false;
			}
		}

		if (isAdmin) {
			model.Amministratore a = (Amministratore) utenteRegistrato.get(0);
			if (pin.equals("1234")) {
				Amministratore nuovoAdmin = new Amministratore(login, password, matricola, nome,cognome, pin);
				utenteRegistrato.add(nuovoAdmin);
				return true;
			} else {
				return false;
			}
		} else {

			Medico nuovoMedico = new Medico(nome, cognome, login, password, matricola);
			utenteRegistrato.add(nuovoMedico);
			return true;
		}
	}

	/**
	 * Who is asking boolean.
	 *
	 * @param login     the login
	 * @param password  the password
	 * @param matricola the matricola
	 * @return the boolean
	 */
	//Metodo di riconoscimento e futura impostazione schermata
	public boolean whoIsAsking(String login, String password, String matricola) {
		System.out.println("=== TENTATIVO DI LOGIN ===");
		System.out.println("Hai digitato -> User: [" + login + "] Pass: [" + password + "]");

		for (Utente utenteCorrente : utenteRegistrato) {
			System.out.println("Utente in memoria -> User: [" + utenteCorrente.getLogin() + "] Pass: [" + utenteCorrente.getPassword() + "]");

			if (utenteCorrente.getLogin().equals(login) && utenteCorrente.getPassword().equals(password)) {

				this.utenteLoggato = utenteCorrente;

				if (utenteCorrente instanceof Amministratore) {
					System.out.println("Accesso Admin confermato.");
					return true;
				} else if (utenteCorrente instanceof Medico) {
					System.out.println("Accesso Medico confermato.");
					return true;
				}
			}
		}
		System.out.println("Nessuna corrispondenza trovata!");
		return false;
	}
}