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
	public boolean Registrazione(String login, String password){
		
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
			}else if(utenteCorrente instanceof Medico){
				System.out.println("Un medico ha effettuato l'accesso ");
			}
			return true;
			}
		//Se non trova nessun utente restituisce false
		return false;
		}
	}