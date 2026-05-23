package controller;
import model.*;

import java.util.ArrayList;


public class Controller {
	private ArrayList<Utente> utentiRegistrati;
	private Utente utenteLoggato;

	public Controller() { //Blocco Costruttore
		//Inizializzazione lista
		utentiRegistrati = new ArrayList<>();

		//Inizializzazione classi
		Amministratore admin = new Amministratore(
				"Matteo Neri",
				"admin",
				"BA45671");

		Medico medico = new Medico(
				"FRossi",
				"user",
				"DT6788998",
				"Francesco",
				"Rossi");

		utentiRegistrati.add(admin);
		utentiRegistrati.add(medico);
	}

public boolean login(String login, String password) {
		for(Medico medico : utentiRegistrati) {
			if(medico.)
		}
}


}
