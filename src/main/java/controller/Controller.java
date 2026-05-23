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
				"Neri"
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
	public boolean registrazione(String login, String password,String nome, String cognome, String pin, boolean isAdmin, String matricola) {
		for (Utente u : utenteRegistrato){
			if(u.getLogin().equals(login)){
				return false;
			}
		}
		if(isAdmin){
			if (pin.equals("1234")) {
				Amministratore nuovoAdmin = new Amministratore(login, password, matricola, nome, pin);
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
	public boolean whoIsAsking(String login,String password,String matricola) {
		for(Utente utenteCorrente : utenteRegistrato) {
			if (utenteCorrente instanceof Amministratore){
				Amministratore admin = (Amministratore) utenteCorrente;
				System.out.println("La Matricola" + admin.getMatricola() + "ha effettuato l'accesso come Admin");
				return true;
			}else if(utenteCorrente instanceof Medico){
				System.out.println("Un medico ha effettuato l'accesso ");
				return true;
			}
		}
		//Se non trova nessun utente restituisce false
		return false;
		}
	}