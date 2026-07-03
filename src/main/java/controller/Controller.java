package controller;

import dao.*;
import implementazioneDao.*;
import model.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
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
	private JFrame finestraAttiva = null;
	private JFrame homeFrame = null;

	private static final String ERRORE_TITLE = "Errore";
	private static final String SUCCESSO_TITLE = "Successo";
	private static final String ERRORE_AGGIUNTA_DATI = "Errore durante l'aggiunta. Controlla i dati.";
	private static final String MSG_CONFERMA_USCITA = "Sei sicuro di voler uscire?";
	private static final String TITLE_CONFERMA_USCITA = "Conferma uscita";
	private static final String LABEL_NOME = "Nome:";
	private static final String LABEL_COGNOME = "Cognome:";
	private static final String LABEL_MATRICOLA_MEDICO = "Matricola Medico:";
	private static final String LABEL_DATA = "Data (AAAA-MM-GG):";
	private static final String LABEL_ORA_INIZIO = "Ora Inizio (HH:MM:SS):";
	private static final String LABEL_ORA_FINE = "Ora Fine (HH:MM:SS):";
	private static final String DEFAULT_DATE = "2026-05-21";

	private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

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
			LOGGER.warning("Errore di registrazione: tutti i campi obbligatori devono essere compilati.");
			return false;
		}

		if (utenteDAO.checkLoginEsistente(login)) {
			LOGGER.warning(() -> "Errore di registrazione: l'username '" + login + "' è già in uso.");
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

		List<String> datiUtente = utenteDAO.getUtenteByLoginAndPassword(login, password);

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
				LOGGER.info("Accesso Admin confermato.");
				return true;
			} else if ("MEDICO".equals(dbRuolo)) {
				this.utenteLoggato = new Medico(dbNome, dbCognome, dbLogin, dbPassword, dbMatricola);
				LOGGER.info("Accesso Medico confermato.");
				return true;
			}
		}

		LOGGER.warning("Accesso negato, utente non trovato");
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
		LOGGER.info("Logout effettuato con successo.");
	}

	// =========================================================
	// METODI PER LA GESTIONE DEI MEDICI
	// =========================================================

	public boolean aggiungiMedico(String nome, String cognome, String login, String password, String matricola, String iscrizioneAlbo, String specializzazione, String reparto) {
		// Validazione campi obbligatori
		if (isNullOrEmpty(matricola) || isNullOrEmpty(nome) || isNullOrEmpty(cognome)) {
			LOGGER.warning("Errore: Nome, Cognome e Matricola sono campi obbligatori per il medico.");
			return false;
		}
		// Business Logic: controlliamo se esiste già un medico con questa matricola
		List<String> medicoEsistente = getMedicoByMatricola(matricola);
		if (medicoEsistente != null && !medicoEsistente.isEmpty()) {
			LOGGER.warning(() -> "Errore: Impossibile aggiungere. Esiste già un medico con matricola " + matricola);
			return false;
		}
		return medicoDAO.aggiungiMedico(nome, cognome, login, password, matricola, iscrizioneAlbo, specializzazione, reparto);
	}

	public List<String> getMedicoByMatricola(String matricola) {
		return medicoDAO.getMedicoByMatricola(matricola);
	}

	public List<ArrayList<String>> getAllMedici() {
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

	public boolean aggiungiTurno(String matricola, String data, String inizioTurno, String fineTurno, String idAgenda) {
		// Validazione input
		if (isNullOrEmpty(matricola) || isNullOrEmpty(data) || isNullOrEmpty(inizioTurno) || isNullOrEmpty(fineTurno) || isNullOrEmpty(idAgenda)) {
			LOGGER.warning("Errore: Dati del turno incompleti.");
			return false;
		}

		// Business Logic: evitiamo di inserire un turno duplicato nello stesso giorno alla stessa ora di inizio
		List<String> turnoEsistente = getTurno(matricola, data, inizioTurno);
		if (turnoEsistente != null && !turnoEsistente.isEmpty()) {
			LOGGER.warning(() -> "Errore: Il medico " + matricola + " ha già un turno assegnato il " + data + " con inizio alle " + inizioTurno);
			return false;
		}
		return turnoDAO.aggiungiTurno(matricola, data, inizioTurno, fineTurno, idAgenda);
	}

	public List<String> getTurno(String matricola, String data, String inizioTurno) {
		return turnoDAO.getTurno(matricola, data, inizioTurno);
	}

	public List<ArrayList<String>> getTurniByMedico(String matricola) {
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
			LOGGER.warning("Errore: Dati dell'assenza incompleti (matricola e date sono obbligatorie).");
			return false;
		}

		// Business Logic: verificare se l'assenza esiste già (per evitare richieste duplicate)
		List<String> assenzaEsistente = getAssenza(matricola, dataInizio);
		if (assenzaEsistente != null && !assenzaEsistente.isEmpty()) {
			LOGGER.warning(() -> "Errore: Esiste già una richiesta di assenza registrata a partire dal " + dataInizio + " per il medico " + matricola);
			return false;
		}
		return assenzaDAO.aggiungiAssenza(matricola, dataInizio, dataFine, motivazione, approvazione);
	}

	public List<String> getAssenza(String matricola, String dataInizio) {
		return assenzaDAO.getAssenza(matricola, dataInizio);
	}

	public List<ArrayList<String>> getAssenzeByMedico(String matricola) {
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

	public List<ArrayList<String>> getAllPazienti() {
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
		panel.add(new JLabel(LABEL_NOME)); panel.add(nomeInput);
		panel.add(new JLabel(LABEL_COGNOME)); panel.add(cognomeInput);
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

			try {
				boolean successo = anagraficaPaziente(cf, nome, cognome, dataNascita, sesso, residenza, diagnosi);
				if (successo) {
					JOptionPane.showMessageDialog(null, "Paziente aggiunto con successo al database!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
					return true;
				} else {
					JOptionPane.showMessageDialog(null, "Errore durante l'aggiunta. Controlla i dati o possibili CF duplicati.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
				}
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, "Formato della data non valido.\nAssicurati di usare il formato AAAA-MM-GG (es: 1990-12-31).", "Errore Inserimento Data", JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

	public boolean anagraficaPaziente(String cf, String nome, String cognome, String dataNascita, String sesso, String residenza, String diagnosi) {
		if (isNullOrEmpty(cf) || isNullOrEmpty(nome) || isNullOrEmpty(cognome)) {
			LOGGER.warning("Errore: CF, Nome e Cognome obbligatori.");
			return false;
		}
		List<String> esiste = pazienteDAO.getPazienteByCf(cf);
		if (esiste != null && !esiste.isEmpty()) {
			LOGGER.warning("Errore: Paziente già registrato con questo CF.");
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
			LOGGER.warning("Errore: CF Paziente o ID Letto mancanti.");
			return false;
		}
		
		if (!checkDisponibilitaLetto(idLetto)) {
			LOGGER.warning("Errore: Letto non disponibile o inesistente.");
			return false;
		}

		String dataInizio = setDataOraInizio();
		boolean successo = ricoveroDAO.aggiungiRicovero(cfPaziente, idLetto, dataInizio, motivo);
		
		if (successo) {
			lettoDAO.aggiornaStatoLetto(idLetto, true); // Cambia lo stato del letto ad occupato.
			LOGGER.info("Ricovero registrato e letto assegnato con successo.");
		}
		return successo;
	}

	/**
	 * Gestisce l'intero flusso di assegnazione di un paziente esistente a un letto specifico.
	 *
	 * @param idLetto L'ID del letto a cui si vuole assegnare il paziente.
	 * @return true se l'operazione ha successo, false altrimenti.
	 */
	public boolean gestisciAssegnazionePazienteLetto(String idLetto) {
		// 1. Controlla la disponibilità del letto. Il chiamante (GUI) dovrebbe averlo già fatto,
		// ma lo ricontrolliamo per sicurezza. Il messaggio di errore viene gestito dal chiamante.
		if (isNullOrEmpty(idLetto) || !checkDisponibilitaLetto(idLetto)) {
			// Non mostriamo un dialogo qui per evitare di duplicare i messaggi.
			return false;
		}

		// 2. Ottieni la lista di pazienti non ricoverati
		List<ArrayList<String>> tuttiPazienti = pazienteDAO.getAllPazienti();
		List<String> pazientiDisponibili = new ArrayList<>();
		List<String> cfPazientiDisponibili = new ArrayList<>();

		for (List<String> datiPaziente : tuttiPazienti) {
			String cf = datiPaziente.get(0);
			String nome = datiPaziente.get(1);
			String cognome = datiPaziente.get(2);
			// Controlla se il paziente ha un ricovero attivo
			if (ricoveroDAO.getRicoveroAttivo(cf) == null) {
				pazientiDisponibili.add(cognome + " " + nome + " (" + cf + ")");
				cfPazientiDisponibili.add(cf);
			}
		}

		if (pazientiDisponibili.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Non ci sono pazienti disponibili per un nuovo ricovero.", "Nessun Paziente", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		// 3. Mostra la finestra di dialogo per selezionare un paziente
		String pazienteScelto = (String) JOptionPane.showInputDialog(
				null,
				"Seleziona un paziente da ricoverare in questo letto:",
				"Assegna Paziente al Letto " + idLetto,
				JOptionPane.PLAIN_MESSAGE,
				null,
				pazientiDisponibili.toArray(),
				pazientiDisponibili.get(0)
		);

		if (pazienteScelto == null) return false; // L'utente ha annullato

		// 4. Ottieni il CF del paziente scelto e chiedi il motivo
		int indiceScelto = pazientiDisponibili.indexOf(pazienteScelto);
		String cfScelto = cfPazientiDisponibili.get(indiceScelto);
		String motivo = JOptionPane.showInputDialog(null, "Inserisci il motivo del ricovero per " + pazienteScelto + ":", "Motivo Ricovero", JOptionPane.PLAIN_MESSAGE);

		if (motivo == null) return false; // L'utente ha annullato

		// 5. Registra il ricovero usando il metodo esistente
		boolean successo = registraRicovero(cfScelto, idLetto, motivo);
		if (!successo) {
			// Se la registrazione fallisce, mostra un messaggio di errore specifico.
			JOptionPane.showMessageDialog(null, "Impossibile completare l'assegnazione. Errore durante la registrazione del ricovero nel database.", "Errore di Registrazione", JOptionPane.ERROR_MESSAGE);
		}
		return successo;
	}

	public String calcolaPrognosi(int giorniPrognosi) {
		return "Il paziente ha una prognosi di " + giorniPrognosi + " giorni";
	}

	public boolean dimissioni(String cfPaziente, String esito, int giorniPrognosi) {
		List<String> ricoveroAttivo = ricoveroDAO.getRicoveroAttivo(cfPaziente);
		if (ricoveroAttivo == null || ricoveroAttivo.isEmpty()) {
			LOGGER.warning("Errore: Il paziente non ha un ricovero attivo.");
			return false;
		}
		
		String idRicovero = ricoveroAttivo.get(0);
		String idLetto = ricoveroAttivo.get(2);
		String dataFine = setDataOraInizio();
		String prognosi = calcolaPrognosi(giorniPrognosi);

		boolean successo = ricoveroDAO.aggiornaRicoveroDimissione(idRicovero, dataFine, prognosi, esito);
		if (successo) {
			lettoDAO.aggiornaStatoLetto(idLetto, false); // Libera il letto
			LOGGER.info("Paziente dimesso e letto liberato.");
		}
		return successo;
	}

	public List<ArrayList<String>> ricercaDimissioni() {
		return ricoveroDAO.getAllDimissioni();
	}

	public boolean checkDisponibilitaLetto(String idLetto) {
		List<String> letto = lettoDAO.getLettoById(idLetto);
		if (letto != null && !letto.isEmpty()) {
			return "false".equals(letto.get(2)); // true se il letto (parametro occupato=false) è libero.
		}
		return false;
	}

	// =========================================================
	// METODI PER LA GESTIONE DELL'AGENDA
	// =========================================================

	public List<Agenda> getEventiPerMedico(String matricola) {
		if (isNullOrEmpty(matricola)) {
			LOGGER.warning("Matricola non valida per la ricerca eventi.");
			return new ArrayList<>(); // Ritorna una lista vuota per evitare NullPointerException
		}
		return agendaDAO.getEventiByMedico(matricola);
	}

	public boolean addEvento(Agenda evento) {
		if (evento == null) {
			LOGGER.warning("Errore: L'oggetto evento non può essere nullo.");
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
			LOGGER.warning("Errore: L'oggetto evento non può essere nullo.");
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
		List<Agenda> eventiEsistenti = getEventiPerMedico(nuovoEvento.getMatricolaMedico());
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

	public boolean gestisciCreazioneNuovoMedico() {
		JTextField nomeInput = new JTextField();
		JTextField cognomeInput = new JTextField();
		JTextField loginInput = new JTextField();
		JPasswordField passwordInput = new JPasswordField();
		JTextField matricolaInput = new JTextField();
		JTextField iscrizioneInput = new JTextField(); // YYYY-MM-DD
		JTextField specializzazioneInput = new JTextField();
		JTextField repartoInput = new JTextField();

		JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
		panel.add(new JLabel(LABEL_NOME)); panel.add(nomeInput);
		panel.add(new JLabel(LABEL_COGNOME)); panel.add(cognomeInput);
		panel.add(new JLabel("Username (Login):")); panel.add(loginInput);
		panel.add(new JLabel("Password:")); panel.add(passwordInput);
		panel.add(new JLabel("Matricola:")); panel.add(matricolaInput);
		panel.add(new JLabel("Data Iscrizione Albo (AAAA-MM-GG):")); panel.add(iscrizioneInput);
		panel.add(new JLabel("Specializzazione:")); panel.add(specializzazioneInput);
		panel.add(new JLabel("Reparto:")); panel.add(repartoInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Registra Nuovo Medico", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			String nome = nomeInput.getText().trim();
			String cognome = cognomeInput.getText().trim();
			String login = loginInput.getText().trim();
			String password = new String(passwordInput.getPassword()).trim();
			String matricola = matricolaInput.getText().trim();
			String iscrizioneAlbo = iscrizioneInput.getText().trim();
			String specializzazione = specializzazioneInput.getText().trim();
			String reparto = repartoInput.getText().trim();

			boolean successo = aggiungiMedico(nome, cognome, login, password, matricola, iscrizioneAlbo, specializzazione, reparto);
			if (successo) {
				JOptionPane.showMessageDialog(null, "Medico aggiunto con successo al database!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
				return true;
			} else {
				JOptionPane.showMessageDialog(null, ERRORE_AGGIUNTA_DATI, ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
			}
		}
        return false;
	}

	public boolean gestisciCreazioneNuovoTurno() {
		JTextField matricolaInput = new JTextField();
		JTextField dataInput = new JTextField(DEFAULT_DATE); // YYYY-MM-DD
		JTextField inizioInput = new JTextField("08:00:00");
		JTextField fineInput = new JTextField("14:00:00");
		JTextField idAgendaInput = new JTextField();

		JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
		panel.add(new JLabel(LABEL_MATRICOLA_MEDICO)); panel.add(matricolaInput);
		panel.add(new JLabel(LABEL_DATA)); panel.add(dataInput);
		panel.add(new JLabel(LABEL_ORA_INIZIO)); panel.add(inizioInput);
		panel.add(new JLabel(LABEL_ORA_FINE)); panel.add(fineInput);
		panel.add(new JLabel("ID Agenda:")); panel.add(idAgendaInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Registra Nuovo Turno", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			try {
				boolean successo = aggiungiTurno(matricolaInput.getText().trim(), dataInput.getText().trim(), inizioInput.getText().trim(), fineInput.getText().trim(), idAgendaInput.getText().trim());
				if (successo) {
					JOptionPane.showMessageDialog(null, "Turno aggiunto con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
					return true;
				} else {
					JOptionPane.showMessageDialog(null, ERRORE_AGGIUNTA_DATI, ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "L'ID Agenda deve essere un numero intero valido.", "Errore di Parsing", JOptionPane.ERROR_MESSAGE);
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, "Formato data o ora non valido. Assicurati di usare AAAA-MM-GG e HH:MM:SS", "Errore di Parsing", JOptionPane.ERROR_MESSAGE);
			}
		}
        return false;
	}

	public boolean gestisciCreazioneNuovoRicovero() {
		JTextField cfInput = new JTextField();
		JTextField lettoInput = new JTextField();
		JTextField motivoInput = new JTextField();

		JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
		panel.add(new JLabel("CF Paziente:")); panel.add(cfInput);
		panel.add(new JLabel("ID Letto:")); panel.add(lettoInput);
		panel.add(new JLabel("Motivo:")); panel.add(motivoInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Registra Nuovo Ricovero", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			boolean successo = registraRicovero(cfInput.getText().trim(), lettoInput.getText().trim(), motivoInput.getText().trim());
			if (successo) {
				JOptionPane.showMessageDialog(null, "Ricovero aggiunto con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "Errore durante l'aggiunta. Controlla disponibilità letto e CF.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
			}
		}
        return false;
	}

	public boolean gestisciArchiviaDimissione() {
		JTextField cfInput = new JTextField();
		JTextField esitoInput = new JTextField();
		JTextField prognosiInput = new JTextField("0");

		JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
		panel.add(new JLabel("CF Paziente (Ricoverato):")); panel.add(cfInput);
		panel.add(new JLabel("Esito:")); panel.add(esitoInput);
		panel.add(new JLabel("Giorni Prognosi:")); panel.add(prognosiInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Archivia Dimissione", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
            try {
			    boolean successo = dimissioni(cfInput.getText().trim(), esitoInput.getText().trim(), Integer.parseInt(prognosiInput.getText().trim()));
			    if (successo) {
				    JOptionPane.showMessageDialog(null, "Dimissione registrata con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
				    return true;
			    } else {
				    JOptionPane.showMessageDialog(null, "Errore durante l'archiviazione. Il paziente è ricoverato?", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
			    }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Inserisci un numero valido per la prognosi.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
            }
		}
        return false;
	}

	// =========================================================
	// METODI DI NAVIGAZIONE E GESTIONE SCHERMATE (ORCHESTRAZIONE GUI)
	// =========================================================

	private void mostraFinestraSecondaria(JFrame nuovaFinestra) {
		// Chiude la finestra secondaria aperta in precedenza, se esiste
		if (finestraAttiva != null && finestraAttiva.isVisible()) {
			finestraAttiva.dispose();
		}

		// Nasconde la schermata principale (Home) per mostrare solo la nuova
		if (homeFrame != null && homeFrame.isVisible()) {
			homeFrame.setVisible(false);
		}

		// Quando la finestra secondaria viene chiusa, riapriamo la schermata principale
		nuovaFinestra.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				if (homeFrame != null) {
					homeFrame.setVisible(true);
				}
			}
		});

		finestraAttiva = nuovaFinestra;
		finestraAttiva.setVisible(true);
	}

	public void avviaSchermataAmministratore(String nomeUtente) {
		gui.Schermata_Amministratore adminFrame = new gui.Schermata_Amministratore(nomeUtente);
		
		homeFrame = adminFrame; // Imposta come schermata principale

		// Il Controller si iscrive agli eventi della GUI "stupida"
		adminFrame.addPazientiListener(e -> apriSchermataPazienti());
		adminFrame.addLettiListener(e -> apriSchermataLetti());
		adminFrame.addPrestazioniListener(e -> apriSchermataPrestazioni());
		adminFrame.addMediciListener(e -> apriSchermataMedici());
		adminFrame.addDimissioniListener(e -> apriSchermataDimissioni());
		adminFrame.addRicoveroListener(e -> apriSchermataRicoveri());
		adminFrame.addTurniListener(e -> apriSchermataTurni());
		
		adminFrame.addRicercaAgendaListener(e -> aggiornaAgendaGUI(adminFrame));
		adminFrame.addNewEventListener(e -> {
            if (gestisciNuovoEvento()) aggiornaAgendaGUI(adminFrame);
        });

        aggiornaAgendaGUI(adminFrame);
		
		// Gestione del tasto esci
		adminFrame.addEsciListener(e -> {
			int conferma = JOptionPane.showConfirmDialog(null, MSG_CONFERMA_USCITA, TITLE_CONFERMA_USCITA, JOptionPane.YES_NO_OPTION);
			if (conferma == JOptionPane.YES_OPTION) {
				homeFrame = null; // Rimuove il riferimento prima di chiudere tutto
				if (finestraAttiva != null) finestraAttiva.dispose(); // Chiude eventuali finestre figlie aperte
				adminFrame.dispose();
				logout();
				avviaSchermataLogin(); // Routing al login centralizzato
			}
		});

		adminFrame.setVisible(true);
	}

	public void apriSchermataPazienti() {
		gui.Pazienti pazientiFrame = new gui.Pazienti();
		if (pazientiFrame.panelPrincipale != null) {
			pazientiFrame.setContentPane(pazientiFrame.panelPrincipale);
		}
		
		pazientiFrame.setTitle("Gestione Pazienti");
		pazientiFrame.setSize(1100, 750);
		pazientiFrame.setLocationRelativeTo(null);
		pazientiFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        pazientiFrame.addNuovoPazienteListener(e -> {
            if (gestisciCreazioneNuovoPaziente()) {
                pazientiFrame.aggiornaTabella(getAllPazienti());
            }
        });

        pazientiFrame.aggiornaTabella(getAllPazienti());
		mostraFinestraSecondaria(pazientiFrame);
	}

	/**
	 * Metodo helper per ricaricare e aggiornare la tabella dei letti.
	 * @param lettiFrame Il frame della GUI che contiene la tabella.
	 */
	private void ricaricaEAggiornaTabellaLetti(gui.Letti lettiFrame) {
		List<ArrayList<String>> datiLetti = lettoDAO.getAllLetti();
		Object[][] datiPerTabella = preparaDatiLettiPerTabella(datiLetti);
		lettiFrame.aggiornaTabella(datiPerTabella);
	}

	public void apriSchermataLetti() {
		// 1. Crea l'istanza della schermata
		gui.Letti lettiFrame = new gui.Letti();

		// Imposta le proprietà della finestra per farla aprire al centro
		lettiFrame.setTitle("Gestione Letti");
		lettiFrame.setSize(1000, 680);
		lettiFrame.setLocationRelativeTo(null);
		lettiFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// 2. Collega il pulsante "Assegna Paziente" alla sua logica
		lettiFrame.addAssegnaPazienteListener(e -> {
			String idLettoSelezionato = lettiFrame.getIdLettoSelezionato();

			if (idLettoSelezionato != null) {
				// Prima di procedere, verifichiamo che il letto sia ancora disponibile
				if (!checkDisponibilitaLetto(idLettoSelezionato)) {
					JOptionPane.showMessageDialog(lettiFrame, "Il letto selezionato risulta già occupato o non è valido.", "Letto non Disponibile", JOptionPane.WARNING_MESSAGE);
					ricaricaEAggiornaTabellaLetti(lettiFrame); // Aggiorna la vista con lo stato reale
					return;
				}

				boolean successo = gestisciAssegnazionePazienteLetto(idLettoSelezionato);

				if (successo) {
					JOptionPane.showMessageDialog(lettiFrame, "Paziente assegnato con successo!", "Operazione Riuscita", JOptionPane.INFORMATION_MESSAGE);
					ricaricaEAggiornaTabellaLetti(lettiFrame); // Ricarica per mostrare il letto come "Occupato"
				}
			}
		});

		// 3. Carica i dati iniziali nella tabella quando la schermata si apre
		ricaricaEAggiornaTabellaLetti(lettiFrame);

		// 4. Mostra la finestra
		mostraFinestraSecondaria(lettiFrame);
	}

	public void apriSchermataPrestazioni() {
		gui.Prestazioni prestazioniFrame = new gui.Prestazioni();
		if (prestazioniFrame.mainPanel != null) {
			prestazioniFrame.setContentPane(prestazioniFrame.mainPanel);
		}
		prestazioniFrame.setTitle("Ricerca Prestazioni Mediche");
		prestazioniFrame.setSize(1000, 680);
		prestazioniFrame.setLocationRelativeTo(null);
		prestazioniFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		prestazioniFrame.aggiornaTabella(new Object[0][0]); // TODO: Integrare con relativo DAO per DB
		mostraFinestraSecondaria(prestazioniFrame);
	}

	public void avviaSchermataMedico(String nomeUtente) {
		gui.Schermata_Medico medicoHome = new gui.Schermata_Medico(nomeUtente);
		
		homeFrame = medicoHome; // Imposta come schermata principale

		// Esposizione e deleghe per il Medico
		medicoHome.addPazientiListener(e -> apriSchermataPazienti());
		medicoHome.addLettiListener(e -> apriSchermataLetti());
		medicoHome.addPrestazioniListener(e -> apriSchermataPrestazioni());
		medicoHome.addDimissioniListener(e -> apriSchermataDimissioni());
		medicoHome.addRicoveroListener(e -> apriSchermataRicoveri());
		medicoHome.addTurniListener(e -> apriSchermataTurni());
		
		medicoHome.addRicercaAgendaListener(e -> aggiornaAgendaGUI(medicoHome));
		medicoHome.addNewEventListener(e -> {
            if (gestisciNuovoEvento()) aggiornaAgendaGUI(medicoHome);
        });

        aggiornaAgendaGUI(medicoHome);
		
		medicoHome.addEsciListener(e -> {
			int conferma = JOptionPane.showConfirmDialog(null, MSG_CONFERMA_USCITA, TITLE_CONFERMA_USCITA, JOptionPane.YES_NO_OPTION);
			if (conferma == JOptionPane.YES_OPTION) {
				homeFrame = null; // Rimuove il riferimento prima di chiudere tutto
				if (finestraAttiva != null) finestraAttiva.dispose(); // Chiude eventuali finestre figlie aperte
				medicoHome.dispose();
				logout();
				avviaSchermataLogin(); // Routing al login centralizzato
			}
		});

		medicoHome.setVisible(true);
	}

	public void apriSchermataMedici() {
		gui.Medici mediciFrame = new gui.Medici();
		if (mediciFrame.mainPanel != null) {
			mediciFrame.setContentPane(mediciFrame.mainPanel);
		}
		mediciFrame.setTitle("Gestione Medici");
		mediciFrame.setSize(1000, 680);
		mediciFrame.setLocationRelativeTo(null);
		mediciFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        mediciFrame.addNuovoMedicoListener(e -> {
            if (gestisciCreazioneNuovoMedico()) {
                mediciFrame.aggiornaTabella(formattaDatiMedici(getAllMedici()));
            }
        });

		mediciFrame.aggiornaTabella(formattaDatiMedici(getAllMedici()));
		mostraFinestraSecondaria(mediciFrame);
	}

	public void apriSchermataDimissioni() {
		gui.Dimissioni dimissioniFrame = new gui.Dimissioni();
		if (dimissioniFrame.JpanelPrincipale != null) {
			dimissioniFrame.setContentPane(dimissioniFrame.JpanelPrincipale);
		}
		dimissioniFrame.setTitle("Ricerca Dimissioni");
		dimissioniFrame.setSize(1164, 680);
		dimissioniFrame.setLocationRelativeTo(null);
		dimissioniFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        dimissioniFrame.addArchiviaDimissioneListener(e -> {
            if (gestisciArchiviaDimissione()) {
                dimissioniFrame.aggiornaTabella(formattaDatiDimissioni(ricercaDimissioni()));
            }
        });

		dimissioniFrame.aggiornaTabella(formattaDatiDimissioni(ricercaDimissioni()));
		mostraFinestraSecondaria(dimissioniFrame);
	}

	public void apriSchermataRicoveri() {
		gui.Ricovero ricoveroFrame = new gui.Ricovero();
		if (ricoveroFrame.JPanelPrincipale != null) {
			ricoveroFrame.setContentPane(ricoveroFrame.JPanelPrincipale);
		}
		ricoveroFrame.setTitle("Ricerca Ricovero");
		ricoveroFrame.setSize(1024, 680);
		ricoveroFrame.setLocationRelativeTo(null);
		ricoveroFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        ricoveroFrame.addNuovoRicoveroListener(e -> {
            if (gestisciCreazioneNuovoRicovero()) {
                // ricoveroFrame.aggiornaTabella(getRicoveriDaDB()); // TODO
            }
        });

		ricoveroFrame.aggiornaTabella(new Object[0][0]); // TODO: Integrare con RicoveroDAO per DB
		mostraFinestraSecondaria(ricoveroFrame);
	}

	public void apriSchermataTurni() {
		gui.Turni turniFrame = new gui.Turni();
		if (turniFrame.panelHome != null) {
			turniFrame.setContentPane(turniFrame.panelHome);
		}
		turniFrame.setTitle("Gestione Turni Lavorativi");
		turniFrame.setSize(1044, 680);
		turniFrame.setLocationRelativeTo(null);
		turniFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        turniFrame.addNuovoTurnoListener(e -> {
            if (gestisciCreazioneNuovoTurno()) {
                // turniFrame.aggiornaTabella(getTurniDaDB()); // TODO
            }
        });

		turniFrame.aggiornaTabella(new Object[0][0]); // TODO: Integrare con TurnoDAO per DB
		mostraFinestraSecondaria(turniFrame);
	}

	public boolean gestisciNuovoEvento() {
		JTextField idEventoInput = new JTextField();
		String defaultMatricola = utenteLoggato != null ? utenteLoggato.getMatricola() : "";
		JTextField matricolaInput = new JTextField(defaultMatricola);
		JTextField dataInput = new JTextField(DEFAULT_DATE);
		JTextField oraInizioInput = new JTextField("08:30:00");
		JTextField oraFineInput = new JTextField("10:00:00");
		JTextField titoloInput = new JTextField("Nuova Visita");
		JTextField descrizioneInput = new JTextField("-");

		JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
		panel.add(new JLabel("ID Evento:")); panel.add(idEventoInput);
		panel.add(new JLabel(LABEL_MATRICOLA_MEDICO)); panel.add(matricolaInput);
		panel.add(new JLabel(LABEL_DATA)); panel.add(dataInput);
		panel.add(new JLabel(LABEL_ORA_INIZIO)); panel.add(oraInizioInput);
		panel.add(new JLabel(LABEL_ORA_FINE)); panel.add(oraFineInput);
		panel.add(new JLabel("Titolo:")); panel.add(titoloInput);
		panel.add(new JLabel("Descrizione:")); panel.add(descrizioneInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Nuovo Evento Agenda", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if (result == JOptionPane.OK_OPTION) {
			String matricola = matricolaInput.getText().trim();
			String inizio = dataInput.getText().trim() + " " + oraInizioInput.getText().trim();
			String fine = dataInput.getText().trim() + " " + oraFineInput.getText().trim();
			String titolo = titoloInput.getText().trim();
			String descrizione = descrizioneInput.getText().trim();
			
            try {
			    int idEvento = Integer.parseInt(idEventoInput.getText().trim());
			    java.sql.Timestamp tsInizio = java.sql.Timestamp.valueOf(inizio);
			    java.sql.Timestamp tsFine = java.sql.Timestamp.valueOf(fine);

			    Agenda nuovoEvento = new Agenda(idEvento, matricola, titolo, descrizione, tsInizio, tsFine);
			    boolean successo = addEvento(nuovoEvento);
                if (successo) {
                    JOptionPane.showMessageDialog(null, "Evento inserito con successo nel DB!");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Errore. Verifica eventuali sovrapposizioni.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "L'ID Evento deve essere un numero intero valido.", "Errore Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, "Formato data o ora non valido.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Errore nella creazione dell'evento.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
            }
		}
        return false;
	}

	private Object[][] formattaDatiMedici(List<ArrayList<String>> mediciDb) {
		if (mediciDb == null) return new Object[0][0];
		Object[][] dati = new Object[mediciDb.size()][6];
		for (int i = 0; i < mediciDb.size(); i++) {
			List<String> m = mediciDb.get(i);
			try {
				dati[i][0] = m.size() > 4 ? m.get(4) : "-"; // Matricola
				dati[i][1] = (m.size() > 1 ? m.get(1) : "") + " " + (m.size() > 0 ? m.get(0) : ""); // Cognome Nome
				dati[i][2] = m.size() > 6 ? m.get(6) : "-"; // Specializzazione
				dati[i][3] = m.size() > 7 ? m.get(7) : "-"; // Reparto Assegnato
				dati[i][4] = "Attivo"; // Stato
				dati[i][5] = "-"; // Note
			} catch (Exception e) {
				final int riga = i;
				LOGGER.warning(() -> "Errore nella formattazione dei dati medici alla riga " + riga + ": " + e.getMessage());
			}
		}
		return dati;
	}

	private Object[][] formattaDatiDimissioni(List<ArrayList<String>> dimDb) {
		if (dimDb == null) return new Object[0][0];
		Object[][] dati = new Object[dimDb.size()][6];
		for (int i = 0; i < dimDb.size(); i++) {
			List<String> d = dimDb.get(i);
			try {
				dati[i][0] = d.size() > 0 ? d.get(0) : "-"; // ID Paziente / CF
				dati[i][1] = d.size() > 1 ? d.get(1) : "-"; // Paziente
				dati[i][2] = d.size() > 2 ? d.get(2) : "-"; // CF
				dati[i][3] = d.size() > 3 ? d.get(3) : "-"; // Reparto
				dati[i][4] = d.size() > 4 ? d.get(4) : "-"; // Tipo (Esito)
				dati[i][5] = d.size() > 5 ? d.get(5) : "-"; // Data
			} catch (Exception e) {
				final int riga = i;
				LOGGER.warning(() -> "Errore nella formattazione dei dati dimissioni alla riga " + riga + ": " + e.getMessage());
			}
		}
		return dati;
	}

	private Object[][] formattaDatiAgenda(List<Agenda> eventi) {
        if (eventi == null) return new Object[0][0];
		Object[][] dati = new Object[eventi.size()][2];
		for (int i = 0; i < eventi.size(); i++) {
			Agenda ev = eventi.get(i);
			String ora = ev.getDataOraInizio() != null ? ev.getDataOraInizio().toString() : "N/D";
			String descrizione = ev.getTitolo() != null ? ev.getTitolo() : "Evento #" + ev.getIdEvento();
			dati[i][0] = ora;
			dati[i][1] = descrizione;
		}
		return dati;
	}

	/**
	 * Metodo di supporto per convertire i dati dei letti dal formato del DAO
	 * al formato Object[][] richiesto dalla tabella della GUI.
	 *
	 * @param datiLetti La lista di letti proveniente dal DAO.
	 * @return Una matrice di Object pronta per essere mostrata in una JTable.
	 */
	private Object[][] preparaDatiLettiPerTabella(List<ArrayList<String>> datiLetti) {
		if (datiLetti == null || datiLetti.isEmpty()) {
			return new Object[0][3];
		}

		Object[][] dati = new Object[datiLetti.size()][3];
		for (int i = 0; i < datiLetti.size(); i++) {
			List<String> letto = datiLetti.get(i);
			dati[i][0] = letto.get(0); // ID Letto
			dati[i][1] = letto.get(1); // Reparto
			dati[i][2] = Boolean.parseBoolean(letto.get(2)) ? "Occupato" : "Disponibile";
		}
		return dati;
	}

    private void aggiornaAgendaGUI(JFrame frame) {
        if (utenteLoggato == null) return;
        Object[][] dati = formattaDatiAgenda(getEventiPerMedico(utenteLoggato.getMatricola()));
        if (frame instanceof gui.Schermata_Amministratore) ((gui.Schermata_Amministratore) frame).aggiornaAgenda(dati);
        if (frame instanceof gui.Schermata_Medico) ((gui.Schermata_Medico) frame).aggiornaAgenda(dati);
    }

	// =========================================================
	// METODI DI AVVIO PRINCIPALE APP E AUTENTICAZIONE
	// =========================================================

	public void avvia() {
		avviaSchermataLogin();
	}

	private void avviaSchermataLogin() {
		gui.Login loginView = new gui.Login();
		JFrame frame = new JFrame("Login - Ospedale San Raffaele");
		frame.setContentPane(loginView.mainPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(1000, 680);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);

		// Listener delegato dal bottone Accedi nella GUI
		loginView.addLoginListener(e -> {
			String username = loginView.getUsername();
			String password = loginView.getPassword();

			if (username.isEmpty() || password.isEmpty()) {
				loginView.showMessage("Campi vuoti", "Inserisci Username e Password per accedere.", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (whoIsAsking(username, password)) {
				frame.dispose(); // Chiude la schermata di login
				indirizzaUtenteLoggato();
			} else {
				loginView.showMessage("Errore di accesso", "Credenziali errate. Utente non trovato o password sbagliata.", JOptionPane.ERROR_MESSAGE);
			}
		});

		// Listener delegato dal bottone (testo) "Registrati" nella GUI
		loginView.addRegisterListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				frame.dispose();
				avviaSchermataRegistrazione();
			}
		});

		frame.setVisible(true);
	}

	private void indirizzaUtenteLoggato() {
		Utente utente = getUtenteLoggato();
		if (utente instanceof Amministratore) {
			Amministratore admin = (Amministratore) utente;
			avviaSchermataAmministratore("Dott. " + admin.getNome() + " " + admin.getCognome());
		} else if (utente instanceof Medico) {
			Medico medico = (Medico) utente;
			avviaSchermataMedico("Dott. " + medico.getNome() + " " + medico.getCognome());
		}
	}

	private void avviaSchermataRegistrazione() {
		gui.Registrazione regView = new gui.Registrazione();
		JFrame frame = new JFrame("Registrazione - Ospedale San Raffaele");
		frame.setContentPane(regView.registerPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(1000, 680);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);

		regView.addRegisterListener(e -> {
			String nome = regView.getNome();
			String cognome = regView.getCognome();
			String username = regView.getUsername();
			String password = regView.getPassword();
			boolean isAdmin = regView.isAdmin();
			String pin = regView.getPin();

			if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() || password.isEmpty()) {
				regView.showMessage(ERRORE_TITLE, "Compila tutti i campi obbligatori (Nome, Cognome, Username, Password).", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (isAdmin && pin.isEmpty()) {
				regView.showMessage("Errore PIN", "Inserisci il PIN per registrarti come Amministratore.", JOptionPane.WARNING_MESSAGE);
				return;
			}

			boolean successo = registrazione(username, password, nome, cognome, pin, isAdmin);

			if (successo) {
				regView.showMessage(SUCCESSO_TITLE, "Registrazione completata con successo!\nBenvenuto " + nome + " " + cognome, JOptionPane.INFORMATION_MESSAGE);
				frame.dispose();
				avviaSchermataLogin(); // Torna al login
			} else {
				regView.showMessage("Errore Registrazione", "Registrazione fallita!\nVerifica che l'username non sia già in uso e, se hai selezionato 'Amministratore', che il PIN di sicurezza sia corretto.", JOptionPane.ERROR_MESSAGE);
			}
		});

		regView.addLoginListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				frame.dispose();
				avviaSchermataLogin();
			}
		});

		frame.setVisible(true);
	}
}