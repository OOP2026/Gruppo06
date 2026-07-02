package controller;

import dao.*;
import implementazioneDao.*;
import model.*;

import java.util.ArrayList;

/**
 * The type Controller.
 */
public class Controller {
	private UtenteDAO utenteDAO;
	private MedicoDAO medicoDAO;
	private Turno_LavoroDAO turnoDAO;
	private AssenzaDAO assenzaDAO;

	private Utente utenteLoggato;

	/**
	 * Instantiates a new Controller.
	 */
	public Controller() { //Blocco Costruttore

		//inizializzazione DAO per Postgre
		utenteDAO = new UtentePostgresDao();
		medicoDAO = new MedicoPostgresDao();
		turnoDAO = new Turno_LavoroPostgresDao();
		assenzaDAO = new AssenzaPostgresDao();
	}

	/**
	 * Metodo di utilità per verificare se una stringa è nulla o vuota.
	 * @param str la stringa da controllare
	 * @return true se nulla o vuota, false altrimenti
	 */
	private boolean isNullOrEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	/**
	 * Registrazione boolean.
	 *
	 * @param login    the login
	 * @param password the password
	 * @return the boolean
	 */
	public boolean registrazione(String login, String password, String nome, String cognome, String pin, boolean isAdmin) {
		// Logica di validazione: controlliamo che i campi base non siano vuoti
		if (isNullOrEmpty(login) || isNullOrEmpty(password) || isNullOrEmpty(nome) || isNullOrEmpty(cognome)) {
			System.err.println("Errore di registrazione: tutti i campi obbligatori devono essere compilati.");
			return false;
		}

		if (utenteDAO.checkLoginEsistente(login)) {
			System.err.println("Errore di registrazione: l'username '" + login + "' è già in uso.");
			return false;
		}

		if (isAdmin) {
			if (pin.equals("1234")) {
				return utenteDAO.aggiungiUtente(nome, cognome, login, password, "", pin);
			} else {
				return false;
			}
		} else {
			return utenteDAO.aggiungiUtente(nome, cognome, login, password, "", null);
		}
	}

	/**
	 * Who is asking boolean.
	 *
	 * @param login     the login
	 * @param password  the password
	 * @return the boolean
	 */
	//Metodo di riconoscimento e futura impostazione schermata
	public boolean whoIsAsking(String login, String password) {

		ArrayList<String> datiUtente = utenteDAO.getUtenteByLoginAndPassword(login, password);
		
		if (datiUtente != null && !datiUtente.isEmpty()) {
			String dbNome = datiUtente.get(0);
			String dbCognome = datiUtente.get(1);
			String dbLogin = datiUtente.get(2);
			String dbPassword = datiUtente.get(3);
			String dbMatricola = datiUtente.get(4);
			String dbRuolo = datiUtente.get(5);
			String dbPin = datiUtente.get(6);

			if ("ADMIN".equals(dbRuolo) || "AMMINISTRATORE".equals(dbRuolo)) {
				this.utenteLoggato = new Amministratore(dbLogin, dbPassword, dbMatricola, dbNome, dbCognome, dbPin);
				System.out.println("Accesso Admin confermato.");
				return true;
			} else if ("MEDICO".equals(dbRuolo)) {
				this.utenteLoggato = new Medico(dbNome, dbCognome, dbLogin, dbPassword, dbMatricola);
				System.out.println("Accesso Medico confermato.");
				return true;
			}
		}

		System.out.println("Accesso negato, utente non trovato");
		return false;
	}

	/**
	 * Ritorna l'utente attualmente loggato nel sistema.
	 * @return l'utente loggato, o null se nessuno ha effettuato l'accesso.
	 */
	public Utente getUtenteLoggato() {
		return utenteLoggato;
	}

	/**
	 * Effettua il logout dell'utente corrente.
	 */
	public void logout() {
		this.utenteLoggato = null;
		System.out.println("Logout effettuato con successo.");
	}

	// =========================================================
	// METODI PER LA GESTIONE DEI MEDICI
	// =========================================================

	public boolean aggiungiMedico(String nome, String cognome, String login, String password, String matricola, String iscrizioneAlbo, String specializzazione, String reparto) {
		// Validazione campi obbligatori
		if (isNullOrEmpty(matricola) || isNullOrEmpty(nome) || isNullOrEmpty(cognome)) {
			System.err.println("Errore: Nome, Cognome e Matricola sono campi obbligatori per il medico.");
			return false;
		}
		// Business Logic: controlliamo se esiste già un medico con questa matricola
		ArrayList<String> medicoEsistente = getMedicoByMatricola(matricola);
		if (medicoEsistente != null && !medicoEsistente.isEmpty()) {
			System.err.println("Errore: Impossibile aggiungere. Esiste già un medico con matricola " + matricola);
			return false;
		}
		return medicoDAO.aggiungiMedico(nome, cognome, login, password, matricola, iscrizioneAlbo, specializzazione, reparto);
	}

	public ArrayList<String> getMedicoByMatricola(String matricola) {
		return medicoDAO.getMedicoByMatricola(matricola);
	}

	public ArrayList<ArrayList<String>> getAllMedici() {
		return medicoDAO.getAllMedici();
	}

	public boolean aggiornaMedico(String nome, String cognome, String login, String password, String matricola, String iscrizioneAlbo, String specializzazione, String reparto) {
		return medicoDAO.aggiornaMedico(nome, cognome, login, password, matricola, iscrizioneAlbo, specializzazione, reparto);
	}

	public boolean eliminaMedico(String matricola) {
		return medicoDAO.eliminaMedico(matricola);
	}

	// =========================================================
	// METODI PER LA GESTIONE DEI TURNI DI LAVORO
	// =========================================================

	public boolean aggiungiTurno(String matricola, String data, String inizioTurno, String fineTurno) {
		// Validazione input
		if (isNullOrEmpty(matricola) || isNullOrEmpty(data) || isNullOrEmpty(inizioTurno) || isNullOrEmpty(fineTurno)) {
			System.err.println("Errore: Dati del turno incompleti.");
			return false;
		}
		
		// Business Logic: evitiamo di inserire un turno duplicato nello stesso giorno alla stessa ora di inizio
		ArrayList<String> turnoEsistente = getTurno(matricola, data, inizioTurno);
		if (turnoEsistente != null && !turnoEsistente.isEmpty()) {
			System.err.println("Errore: Il medico " + matricola + " ha già un turno assegnato il " + data + " con inizio alle " + inizioTurno);
			return false;
		}
		return turnoDAO.aggiungiTurno(matricola, data, inizioTurno, fineTurno);
	}

	public ArrayList<String> getTurno(String matricola, String data, String inizioTurno) {
		return turnoDAO.getTurno(matricola, data, inizioTurno);
	}

	public ArrayList<ArrayList<String>> getTurniByMedico(String matricola) {
		return turnoDAO.getTurniByMedico(matricola);
	}

	public boolean aggiornaTurno(String matricola, String data, String inizioTurno, String fineTurno) {
		return turnoDAO.aggiornaTurno(matricola, data, inizioTurno, fineTurno);
	}

	public boolean eliminaTurno(String matricola, String data, String inizioTurno) {
		return turnoDAO.eliminaTurno(matricola, data, inizioTurno);
	}

	// =========================================================
	// METODI PER LA GESTIONE DELLE ASSENZE
	// =========================================================

	public boolean aggiungiAssenza(String matricola, String dataInizio, String dataFine, String motivazione, boolean approvazione) {
		// Validazione input
		if (isNullOrEmpty(matricola) || isNullOrEmpty(dataInizio) || isNullOrEmpty(dataFine)) {
			System.err.println("Errore: Dati dell'assenza incompleti (matricola e date sono obbligatorie).");
			return false;
		}

		// Business Logic: verificare se l'assenza esiste già (per evitare richieste duplicate)
		ArrayList<String> assenzaEsistente = getAssenza(matricola, dataInizio);
		if (assenzaEsistente != null && !assenzaEsistente.isEmpty()) {
			System.err.println("Errore: Esiste già una richiesta di assenza registrata a partire dal " + dataInizio + " per il medico " + matricola);
			return false;
		}
		return assenzaDAO.aggiungiAssenza(matricola, dataInizio, dataFine, motivazione, approvazione);
	}

	public ArrayList<String> getAssenza(String matricola, String dataInizio) {
		return assenzaDAO.getAssenza(matricola, dataInizio);
	}

	public ArrayList<ArrayList<String>> getAssenzeByMedico(String matricola) {
		return assenzaDAO.getAssenzeByMedico(matricola);
	}

	public boolean aggiornaAssenza(String matricola, String dataInizio, String dataFine, String motivazione, boolean approvazione) {
		return assenzaDAO.aggiornaAssenza(matricola, dataInizio, dataFine, motivazione, approvazione);
	}

	public boolean eliminaAssenza(String matricola, String dataInizio) {
		return assenzaDAO.eliminaAssenza(matricola, dataInizio);
	}
}