package controller;

import dao.*;
import implementazioneDao.*;
import model.*;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import java.awt.*;

/**
 * The type Controller.
 */
public class Controller {
	private UtenteDAO utenteDAO;
	private MedicoDAO medicoDAO;
	private Turno_LavoroDAO turnoDAO;
	private AssenzaDAO assenzaDAO;
	private PazienteDAO pazienteDAO;
	private LettoDAO lettoDAO;
	private RicoveroDAO ricoveroDAO;
	private AgendaDAO agendaDAO;

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
		pazienteDAO = new PazientePostgresDao();
		lettoDAO = new LettoPostgresDao();
		ricoveroDAO = new RicoveroPostgresDao();
		agendaDAO = new AgendaPostgresDAO();
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

	// =========================================================
	// METODI AMMINISTRATORE (PAZIENTI, LETTI, RICOVERI E DIMISSIONI)
	// =========================================================

	public ArrayList<ArrayList<String>> getAllPazienti() {
		return pazienteDAO.getAllPazienti();
	}

	public boolean gestisciCreazioneNuovoPaziente() {
		JTextField cfInput = new JTextField();
		JTextField nomeInput = new JTextField();
		JTextField cognomeInput = new JTextField();
		JTextField dataNascitaInput = new JTextField(); // YYYY-MM-DD
		JTextField sessoInput = new JTextField(); // M o F
		JTextField residenzaInput = new JTextField();
		JTextField diagnosiInput = new JTextField();

		JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
		panel.add(new JLabel("Codice Fiscale:")); panel.add(cfInput);
		panel.add(new JLabel("Nome:")); panel.add(nomeInput);
		panel.add(new JLabel("Cognome:")); panel.add(cognomeInput);
		panel.add(new JLabel("Data Nascita (AAAA-MM-GG):")); panel.add(dataNascitaInput);
		panel.add(new JLabel("Sesso (M/F):")); panel.add(sessoInput);
		panel.add(new JLabel("Residenza:")); panel.add(residenzaInput);
		panel.add(new JLabel("Diagnosi:")); panel.add(diagnosiInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Registra Nuovo Paziente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			String cf = cfInput.getText().trim();
			String nome = nomeInput.getText().trim();
			String cognome = cognomeInput.getText().trim();
			String dataNascita = dataNascitaInput.getText().trim();
			String sesso = sessoInput.getText().trim().toUpperCase();
			String residenza = residenzaInput.getText().trim();
			String diagnosi = diagnosiInput.getText().trim();

			boolean successo = anagraficaPaziente(cf, nome, cognome, dataNascita, sesso, residenza, diagnosi);
			if (successo) {
				JOptionPane.showMessageDialog(null, "Paziente aggiunto con successo al database!", "Successo", JOptionPane.INFORMATION_MESSAGE);
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "Errore durante l'aggiunta. Controlla i dati o possibili CF duplicati.", "Errore", JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

	public boolean anagraficaPaziente(String cf, String nome, String cognome, String dataNascita, String sesso, String residenza, String diagnosi) {
		if (isNullOrEmpty(cf) || isNullOrEmpty(nome) || isNullOrEmpty(cognome)) {
			System.err.println("Errore: CF, Nome e Cognome obbligatori.");
			return false;
		}
		ArrayList<String> esiste = pazienteDAO.getPazienteByCf(cf);
		if (esiste != null && !esiste.isEmpty()) {
			System.err.println("Errore: Paziente già registrato con questo CF.");
			return false;
		}
		return pazienteDAO.aggiungiPaziente(cf, nome, cognome, dataNascita, sesso, residenza, diagnosi);
	}

	public boolean assegnaLetto(String idLetto, boolean occupato) {
		return lettoDAO.aggiornaStatoLetto(idLetto, occupato);
	}

	public String setDataOraInizio() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return now.format(formatter);
	}

	public boolean registraRicovero(String cfPaziente, String idLetto, String motivo) {
		if (isNullOrEmpty(cfPaziente) || isNullOrEmpty(idLetto)) {
			System.err.println("Errore: CF Paziente o ID Letto mancanti.");
			return false;
		}
		
		if (!checkDisponibilitaLetto(idLetto)) {
			System.err.println("Errore: Letto non disponibile o inesistente.");
			return false;
		}

		String dataInizio = setDataOraInizio();
		boolean successo = ricoveroDAO.aggiungiRicovero(cfPaziente, idLetto, dataInizio, motivo);
		
		if (successo) {
			lettoDAO.aggiornaStatoLetto(idLetto, true); // Cambia lo stato del letto ad occupato.
			System.out.println("Ricovero registrato e letto assegnato con successo.");
		}
		return successo;
	}

	public String calcolaPrognosi(int giorniPrognosi) {
		return "Il paziente ha una prognosi di " + giorniPrognosi + " giorni";
	}

	public boolean dimissioni(String cfPaziente, String esito, int giorniPrognosi) {
		ArrayList<String> ricoveroAttivo = ricoveroDAO.getRicoveroAttivo(cfPaziente);
		if (ricoveroAttivo == null || ricoveroAttivo.isEmpty()) {
			System.err.println("Errore: Il paziente non ha un ricovero attivo.");
			return false;
		}
		
		String idRicovero = ricoveroAttivo.get(0);
		String idLetto = ricoveroAttivo.get(2);
		String dataFine = setDataOraInizio();
		String prognosi = calcolaPrognosi(giorniPrognosi);

		boolean successo = ricoveroDAO.aggiornaRicoveroDimissione(idRicovero, dataFine, prognosi, esito);
		if (successo) {
			lettoDAO.aggiornaStatoLetto(idLetto, false); // Libera il letto
			System.out.println("Paziente dimesso e letto liberato.");
		}
		return successo;
	}

	public ArrayList<ArrayList<String>> ricercaDimissioni() {
		return ricoveroDAO.getAllDimissioni();
	}

	public boolean checkDisponibilitaLetto(String idLetto) {
		ArrayList<String> letto = lettoDAO.getLettoById(idLetto);
		if (letto != null && !letto.isEmpty()) {
			return "false".equals(letto.get(2)); // true se il letto (parametro occupato=false) è libero.
		}
		return false;
	}

	// =========================================================
	// METODI PER LA GESTIONE DELL'AGENDA
	// =========================================================

	public ArrayList<Agenda> getEventiPerMedico(String matricola) {
		if (isNullOrEmpty(matricola)) {
			System.err.println("Matricola non valida per la ricerca eventi.");
			return new ArrayList<>(); // Ritorna una lista vuota per evitare NullPointerException
		}
		return agendaDAO.getEventiByMedico(matricola);
	}

	public boolean addEvento(Agenda evento) {
		if (evento == null) {
			System.err.println("Errore: L'oggetto evento non può essere nullo.");
			return false;
		}
		// Business Logic: Controlla sovrapposizioni prima di aggiungere
		if (checkSovrapposizioneEvento(evento)) {
			JOptionPane.showMessageDialog(null, "L'orario selezionato si sovrappone con un altro evento esistente.", "Errore di Sovrapposizione", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return agendaDAO.addEvento(evento);
	}

	public boolean updateEvento(Agenda evento) {
		if (evento == null) {
			System.err.println("Errore: L'oggetto evento non può essere nullo.");
			return false;
		}
		// Business Logic: Controlla sovrapposizioni prima di aggiornare
		if (checkSovrapposizioneEvento(evento)) {
			JOptionPane.showMessageDialog(null, "L'orario modificato si sovrappone con un altro evento esistente.", "Errore di Sovrapposizione", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return agendaDAO.updateEvento(evento);
	}

	public boolean deleteEvento(int idEvento) {
		return agendaDAO.deleteEvento(idEvento);
	}

	private boolean checkSovrapposizioneEvento(Agenda nuovoEvento) {
		ArrayList<Agenda> eventiEsistenti = getEventiPerMedico(nuovoEvento.getMatricolaMedico());
		for (Agenda eventoEsistente : eventiEsistenti) {
			// Salta il controllo se stiamo modificando lo stesso evento
			if (eventoEsistente.getIdEvento() == nuovoEvento.getIdEvento()) {
				continue;
			}

			// Logica di sovrapposizione: (StartA < EndB) and (EndA > StartB)
			boolean siSovrappone = nuovoEvento.getDataOraInizio().before(eventoEsistente.getDataOraFine()) &&
					nuovoEvento.getDataOraFine().after(eventoEsistente.getDataOraInizio());

			if (siSovrappone) return true; // Trovata una sovrapposizione
		}
		return false; // Nessuna sovrapposizione
	}
}