package controller;

import dao.*;
import database_connection.ConnessioneDatabase;
import implementazioneDao.*;
import model.*;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The type Controller.
 */
public class Controller {
	private MedicoDAO medicoDAO;
	private Turno_LavoroDAO turnoDAO;
	private AssenzaDAO assenzaDAO;
	private PazienteDAO pazienteDAO;
	private LettoDAO lettoDAO;
	private RicoveroDAO ricoveroDAO;
	private AgendaDAO agendaDAO;
	private DimissioniDAO dimissioniDAO;
	private AmministratoreDAO amministratoreDAO;
	private PrestazioneDAO prestazioneDAO;

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
	private static final String DEFAULT_DATE = java.time.LocalDate.now().toString();
	private static final String INFO_TITLE = "Informazione";

	private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

	/**
	 * Instantiates a new Controller.
	 */
	public Controller() { //Blocco Costruttore

		//inizializzazione DAO per Postgre
		medicoDAO = new MedicoPostgresDao();
		turnoDAO = new TurnoLavoroPostgresDao();
		assenzaDAO = new AssenzaPostgresDao();
		pazienteDAO = new PazientePostgresDao();
		lettoDAO = new LettoPostgresDao();
		ricoveroDAO = new RicoveroPostgresDao();
		agendaDAO = new AgendaPostgresDAO();
		dimissioniDAO = new DimissioniPostgresDao();
		amministratoreDAO = new AmministratorePostgresDao();
		prestazioneDAO = new PrestazionePostgresDao();

		// Test di connessione al database all'avvio
		try (Connection conn = ConnessioneDatabase.getConnection()) {
			if (conn != null && !conn.isClosed()) {
				LOGGER.info("CONNESSIONE AL DB RIUSCITA!");
			}
		} catch (SQLException e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Connessione al database fallita all'avvio", e);
		}
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
		// La logica di generazione della matricola dovrebbe essere più robusta
		String matricola = (isAdmin ? "A" : "M") + (int)(Math.random() * 1000);

		if (isAdmin) {
			if (amministratoreDAO.checkLoginEsistente(login)) {
				LOGGER.warning("Tentativo di registrazione con login già esistente: " + login);
				return false;
			}
			LOGGER.info("Tentativo di registrazione nuovo amministratore con matricola: " + matricola);
			boolean successoAdmin = amministratoreDAO.aggiungiAmministratore(matricola, login, password, nome, cognome, pin);

			// La registrazione ha successo solo se vengono creati sia l'utente che la sua agenda.
			// L'istanza dell'agenda non viene più creata di default alla registrazione.
			return successoAdmin;
		} else {
			if (medicoDAO.checkLoginEsistente(login)) {
				LOGGER.warning("Tentativo di registrazione con login già esistente: " + login);
				return false;
			}
			LOGGER.info("Tentativo di registrazione nuovo medico con matricola: " + matricola);
			boolean successoMedico = medicoDAO.aggiungiMedico(nome, cognome, matricola, login, password, null, null, null);

			// La registrazione ha successo solo se vengono creati sia l'utente che la sua agenda.
			// L'istanza dell'agenda non viene più creata di default alla registrazione.
			return successoMedico;
		}
	}

	/**
	 * Who is asking boolean.
	 *
	 * @param login     the login
	 * @param password  the password
	 * @param pin       the pin (se inserito nel form)
	 * @return the boolean
	 */
	//Metodo di riconoscimento e futura impostazione schermata
	public boolean whoIsAsking(String login, String password, String pin) {
		// Prova a fare il login come amministratore
		ArrayList<String> datiAmministratore = amministratoreDAO.getAmministratoreByLoginAndPassword(login, password);
		if (datiAmministratore != null && !datiAmministratore.isEmpty()) {
			String nome = datiAmministratore.get(0);
			String cognome = datiAmministratore.get(1);
			String matricola = datiAmministratore.get(4);
			this.utenteLoggato = new Amministratore(matricola, nome, cognome, login, "amministratore");
			LOGGER.info("Accesso Amministratore confermato per " + login);
			return true;
		}

		// Se fallisce, prova a fare il login come medico
		ArrayList<String> datiMedico = medicoDAO.getMedicoByLoginAndPassword(login, password);
		if (datiMedico != null && !datiMedico.isEmpty()) {
			String nome = datiMedico.get(0);
			String cognome = datiMedico.get(1);
			String matricola = datiMedico.get(4);
			this.utenteLoggato = new Medico(matricola, nome, cognome, login, "medico");
			LOGGER.info("Accesso Medico confermato per " + login);
			return true;
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

	public boolean aggiungiMedico(String nome, String cognome, String matricola, String login, String password, String iscrizioneAlbo, String specializzazione, String reparto) {
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
		boolean successo = medicoDAO.aggiungiMedico(nome, cognome, matricola, login, password, iscrizioneAlbo, specializzazione, reparto);
		return successo;
	}

	public List<String> getMedicoByMatricola(String matricola) {
		return medicoDAO.getMedicoByMatricola(matricola);
	}

	public List<ArrayList<String>> getAllMedici() {
		return medicoDAO.getAllMedici();
	}

	public boolean aggiornaMedico(String nome, String cognome, String matricola, String iscrizioneAlbo, String specializzazione, String reparto) {
		return medicoDAO.aggiornaMedico(nome, cognome, matricola, iscrizioneAlbo, specializzazione, reparto);
	}

	public boolean eliminaMedico(String matricola) {
		return medicoDAO.eliminaMedico(matricola);
	}

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

	// =========================================================
	// METODI PER LA GESTIONE DEI TURNI DI LAVORO
	// =========================================================

	public List<String> getTurno(String matricola, String data, String inizioTurno) {
		return turnoDAO.getTurno(matricola, data, inizioTurno);
	}

	public List<ArrayList<String>> getTurniByMedico(String matricola) {
		return turnoDAO.getTurniByMedico(matricola);
	}

	public boolean aggiornaTurno(String matricola, String data, String vecchioInizio, String nuovoInizio, String nuovaFine) {
		return turnoDAO.aggiornaTurno(matricola, data, vecchioInizio, nuovoInizio, nuovaFine);
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

	public boolean assegnaLetto(String idLetto, String reparto, boolean occupato) {
		return lettoDAO.aggiornaStatoLetto(idLetto, reparto, occupato);
	}

	public String setDataOraInizio() {
		LocalDateTime now = LocalDateTime.now(Clock.system(ZoneId.of("Europe/Rome")));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return now.format(formatter);
	}

	public boolean registraRicovero(String cfPaziente, String idLetto, String reparto, String motivo) {
		if (isNullOrEmpty(cfPaziente) || isNullOrEmpty(idLetto) || isNullOrEmpty(reparto)) {
			LOGGER.warning("Errore: CF Paziente, ID Letto o Reparto mancanti.");
			return false;
		}
		
		if (!checkDisponibilitaLetto(idLetto, reparto)) {
			LOGGER.warning("Errore: Letto non disponibile o inesistente.");
			return false;
		}

		String dataInizio = setDataOraInizio();
		boolean successo = ricoveroDAO.aggiungiRicovero(cfPaziente, idLetto, reparto, dataInizio, motivo);
		
		if (successo) {
			lettoDAO.aggiornaStatoLetto(idLetto, reparto, true); // Cambia lo stato del letto ad occupato.
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
	public boolean gestisciAssegnazionePazienteLetto(String idLetto, String reparto) {
		// 1. Controlla la disponibilità del letto. Il chiamante (GUI) dovrebbe averlo già fatto,
		// ma lo ricontrolliamo per sicurezza. Il messaggio di errore viene gestito dal chiamante.
		if (isNullOrEmpty(idLetto) || isNullOrEmpty(reparto) || !checkDisponibilitaLetto(idLetto, reparto)) {
			// Non mostriamo un dialogo qui per evitare di duplicare i messaggi.
			return false;
		}

		// 2. Ottieni la lista di pazienti non ricoverati (LOGICA OTTIMIZZATA)

		// Creiamo un set con i CF di tutti i pazienti che hanno già un ricovero attivo.
		// Questo è molto più efficiente che interrogare il DB per ogni singolo paziente.
		java.util.Set<String> pazientiRicoverati = new java.util.HashSet<>();
		List<ArrayList<String>> ricoveriAttivi = ricoveroDAO.getAllRicoveriAttivi();
		if (ricoveriAttivi != null) {
			for (List<String> ricovero : ricoveriAttivi) {
				if (ricovero.size() > 1) {
					pazientiRicoverati.add(ricovero.get(1)); // Indice 1 è cf_paziente nella tabella ricoveri
				}
			}
		}

		// Ora scorriamo tutti i pazienti e aggiungiamo alla lista solo quelli il cui CF non è nel set.
		List<ArrayList<String>> tuttiPazienti = pazienteDAO.getAllPazienti();
		List<String> pazientiDisponibili = new ArrayList<>();
		List<String> cfPazientiDisponibili = new ArrayList<>();

		if (tuttiPazienti != null) {
			for (List<String> datiPaziente : tuttiPazienti) {
				String cf = datiPaziente.get(0);
				// Se il paziente NON è nel set dei ricoverati, è disponibile.
				if (!pazientiRicoverati.contains(cf)) {
					pazientiDisponibili.add(datiPaziente.get(2) + " " + datiPaziente.get(1) + " (" + cf + ")"); // Cognome + Nome + CF
					cfPazientiDisponibili.add(cf);
				}
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
		boolean successo = registraRicovero(cfScelto, idLetto, reparto, motivo);
		if (!successo) {
			// Se la registrazione fallisce, mostra un messaggio di errore specifico.
			JOptionPane.showMessageDialog(null, "Impossibile completare l'assegnazione. Errore durante la registrazione del ricovero nel database.", "Errore di Registrazione", JOptionPane.ERROR_MESSAGE);
		}
		return successo;
	}

	/**
	 * Gestisce la logica per la ricerca filtrata delle dimissioni.
	 * @param dimissioniFrame L'istanza della GUI delle dimissioni.
	 */
	public void gestisciRicercaDimissioni(gui.Dimissioni dimissioniFrame) {
		String cf = dimissioniFrame.getCodiceFiscale();
		String nomeCognome = dimissioniFrame.getNomeCognome();
		String reparto = dimissioniFrame.getRepartoSelezionato();
		String tipoDimissione = dimissioniFrame.getTipoDimissioneSelezionato();
		java.util.Date data = dimissioniFrame.getDataSelezionata();

		// Qui dovresti chiamare un metodo del DAO che supporti i filtri.
		// Poiché ricoveroDAO.getAllDimissioni() non accetta parametri,
		// per ora simuliamo un filtraggio lato client o assumiamo che il DAO venga esteso.
		// In un'applicazione reale, estenderesti RicoveroDAO con un metodo tipo:
		// ricercaDimissioni(cf, nomeCognome, reparto, tipoDimissione, data);
		List<ArrayList<String>> risultati = ricercaDimissioni(); // Usiamo il metodo esistente

		// Esempio di come potresti filtrare i risultati qui se il DAO non lo fa
		// (non è l'approccio ideale per performance, meglio farlo a livello DB)

		dimissioniFrame.aggiornaTabella(formattaDatiDimissioni(risultati));

		if (risultati.isEmpty()) {
			JOptionPane.showMessageDialog(dimissioniFrame, "Nessuna dimissione trovata con i criteri specificati.", INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Mostra i dettagli di una dimissione selezionata.
	 * @param cfPaziente Il CF del paziente di cui mostrare i dettagli della dimissione.
	 */
	public void mostraDettagliDimissione(String cfPaziente) {
		ArrayList<String> ricoveroChiuso = dimissioniDAO.getUltimoRicoveroChiuso(cfPaziente);

		if (ricoveroChiuso == null || ricoveroChiuso.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Nessun dettaglio dimissione trovato per il paziente " + cfPaziente, ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}

		String motivoRicovero = ricoveroChiuso.size() > 6 ? ricoveroChiuso.get(6) : "-";
		String prognosi = ricoveroChiuso.size() > 7 ? ricoveroChiuso.get(7) : "-";
		String esito = ricoveroChiuso.size() > 8 ? ricoveroChiuso.get(8) : "-";

		String messaggio = "Dettagli Dimissione per Paziente CF: " + cfPaziente + "\n\n" +
						   "Motivo Ricovero Originale: " + motivoRicovero + "\n" +
						   "Motivo Dimissione (Esito): " + esito + "\n" +
						   "Giorni di Prognosi Previsti: " + prognosi + "\n";

		Object[] options = {"Chiudi", "Ricovera Nuovamente"};
		int choice = JOptionPane.showOptionDialog(null, messaggio, "Dettaglio Dimissione",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

		if (choice == 1) { // Ricovera Nuovamente
			// Controllo se il paziente è già ricoverato
			List<String> ricoveroAttivo = ricoveroDAO.getRicoveroAttivo(cfPaziente);
			if (ricoveroAttivo != null && !ricoveroAttivo.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Il paziente risulta già attualmente ricoverato.", "Impossibile Ricoverare", JOptionPane.WARNING_MESSAGE);
			} else {
				gestisciNuovoRicoveroDaDimissione(cfPaziente);
			}
		}
	}

	public boolean gestisciNuovoRicoveroDaDimissione(String cfPaziente) {
		List<String> paziente = pazienteDAO.getPazienteByCf(cfPaziente);
		String nomePaziente = (paziente != null && paziente.size() > 2) ? paziente.get(1) + " " + paziente.get(2) : cfPaziente;

		List<ArrayList<String>> tuttiLetti = lettoDAO.getAllLetti();
		java.util.Map<String, java.util.Map<String, List<String>>> repartiStanzeLetti = new java.util.HashMap<>();
		java.util.Set<String> repartiDisponibili = new java.util.TreeSet<>();

		// Ottimizzazione: mappa in memoria i letti già occupati per evitare query N+1
		java.util.Set<String> lettiOccupati = new java.util.HashSet<>();
		List<ArrayList<String>> ricoveriAttivi = ricoveroDAO.getAllRicoveriAttivi();
		if (ricoveriAttivi != null) {
			for (List<String> ricovero : ricoveriAttivi) {
				if (ricovero.size() > 3) lettiOccupati.add(ricovero.get(2) + "_" + ricovero.get(3));
			}
		}

		if (tuttiLetti != null) {
			for (List<String> letto : tuttiLetti) {
				String idLetto = letto.size() > 0 ? letto.get(0) : "";
				String reparto = letto.size() > 1 ? letto.get(1) : "";
				String stanza = letto.size() > 3 ? letto.get(3) : "Sconosciuta";

				if (!lettiOccupati.contains(idLetto + "_" + reparto)) {
					repartiDisponibili.add(reparto);
					repartiStanzeLetti.computeIfAbsent(reparto, k -> new java.util.TreeMap<>()).computeIfAbsent(stanza, k -> new ArrayList<>()).add(idLetto);
				}
			}
		}

		if (repartiDisponibili.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Non ci sono letti disponibili in nessun reparto.", "Nessun Letto Disponibile", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		JComboBox<String> repartiComboBox = new JComboBox<>(repartiDisponibili.toArray(new String[0]));
		JComboBox<String> stanzeComboBox = new JComboBox<>();
		JComboBox<String> lettiComboBox = new JComboBox<>();
		JTextField motivoInput = new JTextField();

		repartiComboBox.addActionListener(e -> {
			String repartoSelezionato = (String) repartiComboBox.getSelectedItem();
			stanzeComboBox.removeAllItems();
			lettiComboBox.removeAllItems();
			if (repartoSelezionato != null) {
				java.util.Map<String, List<String>> stanze = repartiStanzeLetti.get(repartoSelezionato);
				if (stanze != null) {
					for (String stanza : stanze.keySet()) {
						stanzeComboBox.addItem(stanza);
					}
				}
			}
		});

		stanzeComboBox.addActionListener(e -> {
			String repartoSelezionato = (String) repartiComboBox.getSelectedItem();
			String stanzaSelezionata = (String) stanzeComboBox.getSelectedItem();
			lettiComboBox.removeAllItems();
			if (repartoSelezionato != null && stanzaSelezionata != null) {
				List<String> letti = repartiStanzeLetti.get(repartoSelezionato).get(stanzaSelezionata);
				if (letti != null) {
					for (String letto : letti) {
						lettiComboBox.addItem(letto);
					}
				}
			}
		});

		if (repartiComboBox.getItemCount() > 0) {
			repartiComboBox.setSelectedIndex(0);
		}

		JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
		panel.add(new JLabel("Paziente:")); panel.add(new JLabel(nomePaziente + " (" + cfPaziente + ")"));
		panel.add(new JLabel("Reparto:")); panel.add(repartiComboBox);
		panel.add(new JLabel("Stanza:")); panel.add(stanzeComboBox);
		panel.add(new JLabel("ID Letto:")); panel.add(lettiComboBox);
		panel.add(new JLabel("Motivo:")); panel.add(motivoInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Registra Nuovo Ricovero", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION && lettiComboBox.getSelectedIndex() != -1) {
			String repartoSelezionato = (String) repartiComboBox.getSelectedItem();
			String lettoSelezionato = (String) lettiComboBox.getSelectedItem();
			String motivo = motivoInput.getText().trim();

			boolean successo = registraRicovero(cfPaziente, lettoSelezionato, repartoSelezionato, motivo);
			if (successo) {
				JOptionPane.showMessageDialog(null, "Ricovero aggiunto con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "Errore durante l'aggiunta. Controlla disponibilità letto e CF.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

	public String calcolaPrognosi(int giorniPrognosi) {
		return giorniPrognosi + " giorni";
	}

	public boolean dimissioni(String cfPaziente, String esito, int giorniPrognosi) {
		List<String> ricoveroAttivo = ricoveroDAO.getRicoveroAttivo(cfPaziente);
		if (ricoveroAttivo == null || ricoveroAttivo.isEmpty()) {
			LOGGER.warning("Errore: Il paziente non ha un ricovero attivo.");
			return false;
		}
		
		String idRicovero = ricoveroAttivo.get(0);
		String idLetto = ricoveroAttivo.get(2);
		String reparto = ricoveroAttivo.get(3);
		String dataFine = setDataOraInizio();
		String prognosi = calcolaPrognosi(giorniPrognosi);

		boolean successo = dimissioniDAO.creaDimissione(idRicovero, dataFine, prognosi, esito);
		if (successo) {
			lettoDAO.aggiornaStatoLetto(idLetto, reparto, false); // Libera il letto
			LOGGER.info("Paziente dimesso e letto liberato.");
		}
		return successo;
	}

	public List<ArrayList<String>> ricercaDimissioni() {
		return dimissioniDAO.getAllDimissioni();
	}

	public boolean checkDisponibilitaLetto(String idLetto, String reparto) {
		// Un letto è disponibile se esiste e non ha un ricovero attivo associato.
		// Questa è la "source of truth", la stessa usata per visualizzare lo stato nella tabella.

		// 1. Verifichiamo che il letto esista fisicamente nel DB.
		List<String> letto = lettoDAO.getLettoById(idLetto, reparto);
		if (letto == null || letto.isEmpty()) {
			LOGGER.warning("Tentativo di verificare disponibilità per un letto inesistente: ID " + idLetto);
			return false; // Il letto non esiste, quindi non è disponibile.
		}

		// 2. Verifichiamo se è occupato secondo la fonte di verità (tabella ricoveri).
		// Il metodo isLettoAttualmenteOccupato interroga la tabella ricoveri.
		// Se NON è occupato, allora è disponibile.
		return !ricoveroDAO.isLettoAttualmenteOccupato(idLetto, reparto);
	}

	// =========================================================
	// METODI PER LA GESTIONE DELL'AGENDA
	// =========================================================

	public List<ArrayList<String>> getEventiPerUtente(String matricola) {
		if (isNullOrEmpty(matricola)) {
			LOGGER.warning("Matricola non valida per la ricerca eventi.");
			return new ArrayList<>(); // Ritorna una lista vuota per evitare NullPointerException
		}
		return agendaDAO.getEventiByMatricola(matricola);
	}

	public boolean addEvento(String matricola, String titolo, String descrizione, java.sql.Timestamp inizio, java.sql.Timestamp fine) {
		if (inizio == null || fine == null) {
			LOGGER.warning("Errore: L'oggetto evento non può essere nullo.");
			return false;
		}
		// Business Logic: Controlla sovrapposizioni prima di aggiungere
		if (checkSovrapposizioneEvento(-1, matricola, inizio, fine)) {
			JOptionPane.showMessageDialog(null, "L'orario selezionato si sovrappone con un altro evento esistente.", "Errore di Sovrapposizione", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return agendaDAO.addEvento(titolo, matricola, descrizione, inizio, fine);
	}

	public boolean updateEvento(int idEvento, String matricola, String titolo, String descrizione, java.sql.Timestamp inizio, java.sql.Timestamp fine) {
		if (inizio == null || fine == null) {
			LOGGER.warning("Errore: L'oggetto evento non può essere nullo.");
			return false;
		}
		// Business Logic: Controlla sovrapposizioni prima di aggiornare
		if (checkSovrapposizioneEvento(idEvento, matricola, inizio, fine)) {
			JOptionPane.showMessageDialog(null, "L'orario modificato si sovrappone con un altro evento esistente.", "Errore di Sovrapposizione", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return agendaDAO.updateEvento(idEvento, titolo, descrizione, inizio, fine);
	}

	public boolean deleteEvento(int idEvento) {
		return agendaDAO.deleteEvento(idEvento);
	}

	private boolean checkSovrapposizioneEvento(int nuovoId, String matricola, java.util.Date nuovoInizio, java.util.Date nuovoFine) {
		List<ArrayList<String>> eventiEsistenti = getEventiPerUtente(matricola);
		for (ArrayList<String> eventoEsistente : eventiEsistenti) {
			// Salta il controllo se stiamo modificando lo stesso evento
			if (Integer.parseInt(eventoEsistente.get(0)) == nuovoId) {
				continue;
			}

			// Logica di sovrapposizione: (StartA < EndB) and (EndA > StartB)
			try {
				java.sql.Timestamp inizioEsistente = java.sql.Timestamp.valueOf(eventoEsistente.get(4));
				java.sql.Timestamp fineEsistente = java.sql.Timestamp.valueOf(eventoEsistente.get(5));
				boolean siSovrappone = nuovoInizio.before(fineEsistente) && nuovoFine.after(inizioEsistente);
				if (siSovrappone) return true; // Trovata una sovrapposizione
			} catch (Exception e) {
				LOGGER.warning("Impossibile parsare la data per il controllo di sovrapposizione: " + e.getMessage());
			}
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
		JComboBox<String> repartoInput = new JComboBox<>(new String[]{
				"Nessuno", "Cardiologia", "Ortopedia", "Chirurgia Generale"
		});

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
			String reparto = (String) repartoInput.getSelectedItem();

			try {
				boolean successo = aggiungiMedico(nome, cognome, matricola, login, password, iscrizioneAlbo, specializzazione, reparto);

				if (successo) {
					JOptionPane.showMessageDialog(null, "Medico aggiunto con successo al database!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
					return true;
				} else {
					JOptionPane.showMessageDialog(null, ERRORE_AGGIUNTA_DATI, ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
				}
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, "Formato data non valido. Assicurati di usare AAAA-MM-GG per la Data Iscrizione Albo.", "Errore di Formato", JOptionPane.ERROR_MESSAGE);
			}
		}
        return false;
	}

	private void impostaIdAgendaPerMatricola(String matricola, JTextField idAgendaInput) {
		if (matricola == null || matricola.isEmpty()) {
			idAgendaInput.setText("");
			return;
		}
		List<ArrayList<String>> eventi = agendaDAO.getEventiByMatricola(matricola);
		if (eventi == null || eventi.isEmpty()) {
			if (matricola.toUpperCase().startsWith("A")) {
				agendaDAO.creaAgendaPerAmministratore(matricola);
			} else {
				agendaDAO.creaAgendaPerMedico(matricola);
			}
			eventi = agendaDAO.getEventiByMatricola(matricola);
		}
		if (eventi != null && !eventi.isEmpty()) {
			idAgendaInput.setText(eventi.get(0).get(0));
		} else {
			idAgendaInput.setText("");
		}
	}

	public boolean gestisciCreazioneNuovoTurno() {
		boolean isMedico = utenteLoggato instanceof Medico;
		String matricolaDefault = isMedico ? utenteLoggato.getMatricola() : "";

		List<ArrayList<String>> tuttiMedici = medicoDAO.getAllMedici();
		List<String> matricoleList = new ArrayList<>();
		if (tuttiMedici != null) {
			for (List<String> medico : tuttiMedici) {
				if (medico.size() > 4) matricoleList.add(medico.get(4));
			}
		}

		JComboBox<String> matricolaComboBox = new JComboBox<>(matricoleList.toArray(new String[0]));
		JTextField idAgendaInput = new JTextField();
		idAgendaInput.setEditable(false); // L'ID Agenda si autodetermina

		if (isMedico) {
			matricolaComboBox.setSelectedItem(matricolaDefault);
			matricolaComboBox.setEnabled(false);
			impostaIdAgendaPerMatricola(matricolaDefault, idAgendaInput);
		} else {
			if (matricolaComboBox.getItemCount() > 0) {
				impostaIdAgendaPerMatricola((String) matricolaComboBox.getSelectedItem(), idAgendaInput);
			}
			matricolaComboBox.addActionListener(e -> {
				String selezionata = (String) matricolaComboBox.getSelectedItem();
				impostaIdAgendaPerMatricola(selezionata, idAgendaInput);
			});
		}

		JTextField dataInput = new JTextField(DEFAULT_DATE); // YYYY-MM-DD
		JTextField inizioInput = new JTextField("08:00:00");
		JTextField fineInput = new JTextField("14:00:00");

		JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
		panel.add(new JLabel(LABEL_MATRICOLA_MEDICO)); panel.add(matricolaComboBox);
		panel.add(new JLabel(LABEL_DATA)); panel.add(dataInput);
		panel.add(new JLabel(LABEL_ORA_INIZIO)); panel.add(inizioInput);
		panel.add(new JLabel(LABEL_ORA_FINE)); panel.add(fineInput);
		panel.add(new JLabel("ID Agenda:")); panel.add(idAgendaInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Registra Nuovo Turno", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			try {
				String matricola = (String) matricolaComboBox.getSelectedItem();
				boolean successo = aggiungiTurno(matricola, dataInput.getText().trim(), inizioInput.getText().trim(), fineInput.getText().trim(), idAgendaInput.getText().trim());
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

	public boolean gestisciModificaTurno(String matricola, String data, String orarioEffettivo) {
		// Estrai il vecchio orario di inizio dalla stringa "inizio - fine"
		String[] orari = orarioEffettivo.split(" - ");
		String vecchioInizio = orari.length > 0 ? orari[0].trim() : "08:00:00";
		String vecchiaFine = orari.length > 1 ? orari[1].trim() : "14:00:00";

		// Prepara i campi per il nuovo orario, pre-compilandoli con i valori attuali
		JTextField nuovoInizioInput = new JTextField(vecchioInizio);
		JTextField nuovaFineInput = new JTextField(vecchiaFine);

		// Crea il pannello del dialogo
		JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
		panel.add(new JLabel(LABEL_MATRICOLA_MEDICO));
		panel.add(new JLabel("<html><b>" + matricola + "</b></html>")); // Mostra la matricola (non editabile)
		panel.add(new JLabel(LABEL_DATA));
		panel.add(new JLabel("<html><b>" + data + "</b></html>")); // Mostra la data (non editabile)
		panel.add(new JLabel("Nuovo " + LABEL_ORA_INIZIO));
		panel.add(nuovoInizioInput);
		panel.add(new JLabel("Nuova " + LABEL_ORA_FINE));
		panel.add(nuovaFineInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Modifica Turno", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			try {
				String nuovoInizio = nuovoInizioInput.getText().trim();
				String nuovaFine = nuovaFineInput.getText().trim();

				boolean successo = aggiornaTurno(matricola, data, vecchioInizio, nuovoInizio, nuovaFine);

				if (successo) {
					JOptionPane.showMessageDialog(null, "Turno modificato con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
					return true;
				} else {
					JOptionPane.showMessageDialog(null, "Errore durante la modifica del turno. Verifica i dati.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				LOGGER.log(java.util.logging.Level.SEVERE, "Errore nel parsing per la modifica del turno", ex);
				JOptionPane.showMessageDialog(null, "Formato ora non valido. Assicurati di usare HH:MM:SS.", "Errore di Formato", JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

	public boolean gestisciCreazioneNuovoRicovero() {
		// Logica per ottenere i pazienti disponibili (non ancora ricoverati)
		// 1. Ottieni i pazienti disponibili (non ancora ricoverati)
		java.util.Set<String> pazientiRicoverati = new java.util.HashSet<>();
		java.util.Set<String> lettiOccupati = new java.util.HashSet<>();
		List<ArrayList<String>> ricoveriAttivi = ricoveroDAO.getAllRicoveriAttivi();
		if (ricoveriAttivi != null) {
			for (List<String> ricovero : ricoveriAttivi) {
				if (ricovero.size() > 1) pazientiRicoverati.add(ricovero.get(1));
				if (ricovero.size() > 3) lettiOccupati.add(ricovero.get(2) + "_" + ricovero.get(3));
			}
		}

		List<ArrayList<String>> tuttiPazienti = pazienteDAO.getAllPazienti();
		List<String> pazientiDisponibiliNomi = new ArrayList<>();
		List<String> pazientiDisponibiliCf = new ArrayList<>();
		if (tuttiPazienti != null) {
			for (List<String> datiPaziente : tuttiPazienti) {
				String cf = datiPaziente.get(0);
				if (!pazientiRicoverati.contains(cf)) {
					pazientiDisponibiliNomi.add(datiPaziente.get(2) + " " + datiPaziente.get(1) + " (" + cf + ")");
					pazientiDisponibiliCf.add(cf);
				}
			}
		}

		if (pazientiDisponibiliNomi.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Non ci sono pazienti disponibili per un nuovo ricovero.", "Nessun Paziente", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		// 2. Ottieni reparti e letti disponibili
		List<ArrayList<String>> tuttiLetti = lettoDAO.getAllLetti();
		java.util.Map<String, java.util.Map<String, List<String>>> repartiStanzeLetti = new java.util.HashMap<>();
		java.util.Set<String> repartiDisponibili = new java.util.TreeSet<>(); // TreeSet per ordine alfabetico

		if (tuttiLetti != null) {
			for (List<String> letto : tuttiLetti) {
				String idLetto = letto.size() > 0 ? letto.get(0) : "";
				String reparto = letto.size() > 1 ? letto.get(1) : "";
				String stanza = letto.size() > 3 ? letto.get(3) : "Sconosciuta";

				if (!lettiOccupati.contains(idLetto + "_" + reparto)) {
					repartiDisponibili.add(reparto);
					repartiStanzeLetti.computeIfAbsent(reparto, k -> new java.util.TreeMap<>()).computeIfAbsent(stanza, k -> new ArrayList<>()).add(idLetto);
				}
			}
		}

		if (repartiDisponibili.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Non ci sono letti disponibili in nessun reparto.", "Nessun Letto Disponibile", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		// 3. Prepara i componenti della GUI
		JComboBox<String> pazientiComboBox = new JComboBox<>(pazientiDisponibiliNomi.toArray(new String[0]));
		JComboBox<String> repartiComboBox = new JComboBox<>(repartiDisponibili.toArray(new String[0]));
		JComboBox<String> stanzeComboBox = new JComboBox<>();
		JComboBox<String> lettiComboBox = new JComboBox<>();
		JTextField motivoInput = new JTextField();

		// Logica per aggiornare le stanze quando cambia il reparto
		repartiComboBox.addActionListener(e -> {
			String repartoSelezionato = (String) repartiComboBox.getSelectedItem();
			stanzeComboBox.removeAllItems();
			lettiComboBox.removeAllItems();
			if (repartoSelezionato != null) {
				java.util.Map<String, List<String>> stanze = repartiStanzeLetti.get(repartoSelezionato);
				if (stanze != null) {
					for (String stanza : stanze.keySet()) {
						stanzeComboBox.addItem(stanza);
					}
				}
			}
		});

		// Logica per aggiornare i letti quando cambia la stanza
		stanzeComboBox.addActionListener(e -> {
			String repartoSelezionato = (String) repartiComboBox.getSelectedItem();
			String stanzaSelezionata = (String) stanzeComboBox.getSelectedItem();
			lettiComboBox.removeAllItems();
			if (repartoSelezionato != null && stanzaSelezionata != null) {
				List<String> letti = repartiStanzeLetti.get(repartoSelezionato).get(stanzaSelezionata);
				if (letti != null) {
					for (String letto : letti) {
						lettiComboBox.addItem(letto);
					}
				}
			}
		});

		// Popola le stanze e i letti per il primo reparto selezionato di default
		if (repartiComboBox.getItemCount() > 0) {
			repartiComboBox.setSelectedIndex(0);
		}

		// 4. Mostra il dialogo
		JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
		panel.add(new JLabel("Paziente:")); panel.add(pazientiComboBox);
		panel.add(new JLabel("Reparto:")); panel.add(repartiComboBox);
		panel.add(new JLabel("Stanza:")); panel.add(stanzeComboBox);
		panel.add(new JLabel("ID Letto:")); panel.add(lettiComboBox);
		panel.add(new JLabel("Motivo:")); panel.add(motivoInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Registra Nuovo Ricovero", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION && pazientiComboBox.getSelectedIndex() != -1 && lettiComboBox.getSelectedIndex() != -1) {
			String cfSelezionato = pazientiDisponibiliCf.get(pazientiComboBox.getSelectedIndex());
			String repartoSelezionato = (String) repartiComboBox.getSelectedItem();
			String lettoSelezionato = (String) lettiComboBox.getSelectedItem();
			String motivo = motivoInput.getText().trim();

			boolean successo = registraRicovero(cfSelezionato, lettoSelezionato, repartoSelezionato, motivo);
			if (successo) {
				JOptionPane.showMessageDialog(null, "Ricovero aggiunto con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "Errore durante l'aggiunta. Controlla disponibilità letto e CF.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
			}
		}
        return false;
	}

	public boolean gestisciArchiviaDimissione(String idRicovero) {
		int result = JOptionPane.showConfirmDialog(
			null,
			"Sei sicuro di voler archiviare (eliminare) la dimissione selezionata?",
			"Conferma Archiviazione",
			JOptionPane.YES_NO_OPTION
		);

		if (result == JOptionPane.YES_OPTION) {
			boolean successo = dimissioniDAO.eliminaDimissione(idRicovero);
			if (successo) {
				JOptionPane.showMessageDialog(null, "Dimissione archiviata (eliminata) con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "Errore durante l'archiviazione della dimissione.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
			}
		}
        return false;
	}

	public boolean gestisciDimissioneDaRicovero(String cfPaziente) {
		JTextField esitoInput = new JTextField();
		JTextField prognosiInput = new JTextField("0");

		JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
		panel.add(new JLabel("Motivo Dimissione (Esito):")); panel.add(esitoInput);
		panel.add(new JLabel("Giorni Prognosi:")); panel.add(prognosiInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Dimetti Paziente " + cfPaziente, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
            try {
			    boolean successo = dimissioni(cfPaziente, esitoInput.getText().trim(), Integer.parseInt(prognosiInput.getText().trim()));
			    if (successo) {
				    JOptionPane.showMessageDialog(null, "Paziente dimesso con successo!\nIl posto letto è stato liberato.", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
				    return true;
			    } else {
				    JOptionPane.showMessageDialog(null, "Errore durante la dimissione. Il paziente è ancora ricoverato?", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
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

	public static void impostaSchermata(JFrame frame, JPanel panel, String titolo, int defaultCloseOperation) {
		Dimension strictSize = new Dimension(1000, 680);
		if (panel != null) {
			panel.setPreferredSize(strictSize);
			frame.setContentPane(panel);
		}
		frame.setTitle(titolo);
		frame.setDefaultCloseOperation(defaultCloseOperation);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}

	private void mostraFinestraSecondaria(JFrame nuovaFinestra, JFrame frameDaChiudere) {
		// Chiude la finestra secondaria aperta in precedenza, se esiste
		if (finestraAttiva != null && finestraAttiva.isVisible()) {
			finestraAttiva.dispose();
		}

		// Aggiungiamo il listener alla nuova finestra
		nuovaFinestra.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				indirizzaUtenteLoggato(); // Ricrea e mostra la schermata home corretta
			}
		});

		// 1. PRIMA rendi visibile la nuova finestra.
		// Questo garantisce che ci sia sempre almeno una finestra attiva nell'Event Dispatch Thread.
		finestraAttiva = nuovaFinestra;
		finestraAttiva.setVisible(true);

		// 2. DOPO chiudi la schermata principale.
		if (frameDaChiudere != null) {
			frameDaChiudere.dispose();
		}
	}
	public void avviaSchermataAmministratore(String nomeUtente) {
		gui.SchermataAmministratore adminFrame = new gui.SchermataAmministratore(nomeUtente);
		impostaSchermata(adminFrame, adminFrame.mainPanel, "Ospedale - Home Amministratore", WindowConstants.DISPOSE_ON_CLOSE);

		homeFrame = adminFrame; // Imposta come schermata principale

		// Il Controller si iscrive agli eventi della GUI "stupida"
		adminFrame.addPazientiListener(e -> apriSchermataPazienti(adminFrame));
		adminFrame.addLettiListener(e -> apriSchermataLetti(adminFrame));
		adminFrame.addPrestazioniListener(e -> apriSchermataPrestazioni(adminFrame));
		adminFrame.addMediciListener(e -> apriSchermataMedici(adminFrame));
		adminFrame.addDimissioniListener(e -> apriSchermataDimissioni(adminFrame));
		adminFrame.addRicoveroListener(e -> apriSchermataRicoveri(adminFrame));
		adminFrame.addTurniListener(e -> apriSchermataTurni(adminFrame));
		
		// Aggiunto listener per aprire il calendario settimanale
		adminFrame.addSettimanaleListener(e -> apriSchermataCalendario(adminFrame));
		
		adminFrame.addRicercaAgendaListener(e -> aggiornaAgendaGUI(adminFrame));
		adminFrame.addNewEventListener(e -> {
            if (gestisciNuovoEvento()) aggiornaAgendaGUI(adminFrame);
        });

        aggiornaAgendaGUI(adminFrame);

		// Listener per il click sul nome in alto (Modifica Profilo Amministratore)
		adminFrame.addProfiloListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (gestisciModificaProfiloAmministratore()) {
					// Aggiorna l'interfaccia con il nuovo nome se modificato
					adminFrame.updateUtenteLoggatoLabel("Dott. " + utenteLoggato.getNome() + " " + utenteLoggato.getCognome());
				}
			}
		});
		
		// Gestione del tasto esci
		adminFrame.addEsciListener(e -> {
			int conferma = JOptionPane.showConfirmDialog(null, MSG_CONFERMA_USCITA, TITLE_CONFERMA_USCITA, JOptionPane.YES_NO_OPTION);
			if (conferma == JOptionPane.YES_OPTION) {
				// Torna alla schermata di login
				adminFrame.dispose();
				logout();
				avviaSchermataLogin();
			}
		});

		adminFrame.setVisible(true);
	}

	public void apriSchermataPazienti(JFrame frameDaChiudere) {
		gui.Pazienti pazientiFrame = new gui.Pazienti();
		impostaSchermata(pazientiFrame, pazientiFrame.mainPanel, "Gestione Pazienti", WindowConstants.DISPOSE_ON_CLOSE);

        pazientiFrame.addNuovoPazienteListener(e -> {
            if (gestisciCreazioneNuovoPaziente()) {
                pazientiFrame.aggiornaTabella(formattaDatiPazienti(getAllPazienti()));
            }
        });

        pazientiFrame.addStoricoPazienteListener(e -> {
            String cfSelezionato = pazientiFrame.getCfPazienteSelezionato();
            if (cfSelezionato != null) {
                gestisciStoricoPaziente(cfSelezionato);
            }
        });

		mostraFinestraSecondaria(pazientiFrame, frameDaChiudere);

		// Utilizzo di SwingWorker per non bloccare la GUI durante il caricamento dati
		pazientiFrame.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		SwingWorker<List<ArrayList<String>>, Void> worker = new SwingWorker<List<ArrayList<String>>, Void>() {
			@Override
			protected List<ArrayList<String>> doInBackground() throws Exception {
				return formattaDatiPazienti(getAllPazienti()); // Query in background
			}
			@Override
			protected void done() {
				try { pazientiFrame.aggiornaTabella(get()); } catch (Exception e) { LOGGER.warning("Errore caricamento pazienti"); }
				pazientiFrame.setCursor(java.awt.Cursor.getDefaultCursor());
			}
		};
		worker.execute();
	}

	public void gestisciStoricoPaziente(String cfPaziente) {
		List<ArrayList<String>> storico = ricoveroDAO.getStoricoRicoveri(cfPaziente);
		if (storico == null || storico.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Nessun ricovero trovato per il paziente selezionato.", "Storico Paziente", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String[] colonne = {"ID", "Reparto", "Letto", "Data Inizio", "Data Fine", "Motivazione", "Esito"};
		Object[][] dati = new Object[storico.size()][7];
		for (int i = 0; i < storico.size(); i++) {
			List<String> r = storico.get(i);
			dati[i][0] = r.size() > 0 ? r.get(0) : "";
			dati[i][1] = r.size() > 3 ? r.get(3) : "";
			dati[i][2] = r.size() > 2 ? r.get(2) : "";
			dati[i][3] = r.size() > 4 ? r.get(4) : "";
			dati[i][4] = r.size() > 5 ? r.get(5) : "In corso";
			dati[i][5] = r.size() > 6 ? r.get(6) : "";
			dati[i][6] = r.size() > 8 && r.get(8) != null ? r.get(8) : ""; // Evita null sull'esito
		}

		JTable table = new JTable(dati, colonne) {
			@Override
			public boolean isCellEditable(int row, int column) { return false; }
		};
		gui.Login.setupTableStyle(table); // Applica il tuo stile standard
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(800, 300));

		JOptionPane.showMessageDialog(null, scrollPane, "Storico Ricoveri - Paziente: " + cfPaziente, JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Metodo helper per ricaricare e aggiornare la tabella dei letti.
	 * @param lettiFrame Il frame della GUI che contiene la tabella.
	 */
	private void ricaricaEAggiornaTabellaLetti(gui.Letti lettiFrame, String statoFilter, String repartoFilter) {
		List<ArrayList<String>> datiLetti = lettoDAO.getAllLetti();
		Object[][] datiPerTabella = preparaDatiLettiPerTabella(datiLetti, statoFilter, repartoFilter);
		lettiFrame.aggiornaTabella(datiPerTabella);
	}

	public void apriSchermataLetti(JFrame frameDaChiudere) {
		// 1. Crea l'istanza della schermata
		gui.Letti lettiFrame = new gui.Letti();

		// Popola la lista dei reparti
		// TODO: Assicurarsi che LettoDAO abbia un metodo getAllReparti() che restituisca List<String>
		// lettiFrame.setRepartiList(lettoDAO.getAllReparti());

		impostaSchermata(lettiFrame, lettiFrame.mainPanel, "Gestione Letti", WindowConstants.DISPOSE_ON_CLOSE);

		// 2. Collega il pulsante "Assegna Paziente" alla sua logica
		lettiFrame.addAssegnaPazienteListener(e -> {
			String idLettoSelezionato = lettiFrame.getIdLettoSelezionato();
			String repartoLettoSelezionato = lettiFrame.getRepartoLettoSelezionato();

			// La logica di controllo della selezione è ora nel controller
			if (idLettoSelezionato == null || repartoLettoSelezionato == null) {
				JOptionPane.showMessageDialog(lettiFrame, "Per favore, seleziona un letto dalla tabella.", "Nessun Letto Selezionato", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Prima di procedere, verifichiamo che il letto sia ancora disponibile
			if (!checkDisponibilitaLetto(idLettoSelezionato, repartoLettoSelezionato)) {
				JOptionPane.showMessageDialog(lettiFrame, "Il letto selezionato risulta già occupato o non è valido.", "Letto non Disponibile", JOptionPane.WARNING_MESSAGE);
				ricaricaEAggiornaTabellaLetti(lettiFrame, lettiFrame.getSelectedStato(), lettiFrame.getSelectedReparto()); // Aggiorna la vista con lo stato reale
				return;
			}

			boolean successo = gestisciAssegnazionePazienteLetto(idLettoSelezionato, repartoLettoSelezionato);

			if (successo) {
				JOptionPane.showMessageDialog(lettiFrame, "Paziente assegnato con successo!", "Operazione Riuscita", JOptionPane.INFORMATION_MESSAGE);
				ricaricaEAggiornaTabellaLetti(lettiFrame, lettiFrame.getSelectedStato(), lettiFrame.getSelectedReparto()); // Ricarica per mostrare il letto come "Occupato"
			}
		});

		// Collega il pulsante "Cerca" alla sua logica
		lettiFrame.addCercaListener(e -> gestisciRicercaLetti(lettiFrame));

		// Collega il pulsante "Reset" alla sua logica
		lettiFrame.addResetListener(e -> {
			lettiFrame.resetCampiRicerca(); // Resetta i campi della GUI
			gestisciRicercaLetti(lettiFrame); // Esegue una ricerca con i campi resettati
		});

		// Collega il pulsante "Storico Letti" alla sua logica
		lettiFrame.addStoricoLettiListener(e -> {
			String idLettoSelezionato = lettiFrame.getIdLettoSelezionato();
			String repartoLettoSelezionato = lettiFrame.getRepartoLettoSelezionato();

			if (idLettoSelezionato == null || repartoLettoSelezionato == null) {
				JOptionPane.showMessageDialog(lettiFrame, "Per favore, seleziona un letto dalla tabella per visualizzarne lo storico.", "Nessun Letto Selezionato", JOptionPane.WARNING_MESSAGE);
				return;
			}
			gestisciStoricoLetto(idLettoSelezionato, repartoLettoSelezionato);
		});

		// 4. Mostra la finestra
		mostraFinestraSecondaria(lettiFrame, frameDaChiudere);

		// Caricamento iniziale dei dati con i filtri di default (Tutti, nessun reparto selezionato)
		caricaDatiLettiAsync(lettiFrame, "Tutti", null);
	}

	private void caricaDatiLettiAsync(gui.Letti lettiFrame, String statoFilter, String repartoFilter) {
		lettiFrame.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		SwingWorker<Object[][], Void> worker = new SwingWorker<Object[][], Void>() {
			@Override
			protected Object[][] doInBackground() throws Exception {
				// Passa i filtri al metodo preparaDatiLettiPerTabella
				return preparaDatiLettiPerTabella(lettoDAO.getAllLetti(), statoFilter, repartoFilter);
			}
			@Override
			protected void done() {
				try { lettiFrame.aggiornaTabella(get()); } catch (Exception e) { LOGGER.warning("Errore caricamento letti"); }
				lettiFrame.setCursor(java.awt.Cursor.getDefaultCursor());
			}
		};
		worker.execute();
	}

	public void gestisciRicercaLetti(gui.Letti lettiFrame) {
		String stato = lettiFrame.getSelectedStato();
		String reparto = lettiFrame.getSelectedReparto();
		caricaDatiLettiAsync(lettiFrame, stato, reparto);
	}

	public void gestisciStoricoLetto(String idLetto, String reparto) {
		// TODO: Implementare in RicoveroDAO e RicoveroPostgresDao il metodo getStoricoRicoveriByLetto(idLetto, reparto)
		// che restituisca tutti i ricoveri (anche chiusi) per un dato letto.
		List<ArrayList<String>> storico = ricoveroDAO.getStoricoRicoveriByLetto(idLetto, reparto);
		// List<ArrayList<String>> storico = new ArrayList<>(); // Placeholder - Rimuovi questa riga
		if (storico.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Nessun ricovero storico trovato per il letto " + idLetto + " nel reparto " + reparto + ".", "Storico Letto", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String[] colonne = {"ID Ricovero", "Paziente (CF)", "Data Inizio", "Data Fine", "Motivo", "Esito"};
		Object[][] dati = new Object[storico.size()][6];
		for (int i = 0; i < storico.size(); i++) {
			List<String> r = storico.get(i);
			String cfPaziente = r.size() > 1 ? r.get(1) : "";
			List<String> paziente = pazienteDAO.getPazienteByCf(cfPaziente);
			String nomePaziente = (paziente != null && !paziente.isEmpty()) ? (paziente.get(1) + " " + paziente.get(2)) : "";

			dati[i][0] = r.size() > 0 ? r.get(0) : ""; // ID Ricovero
			dati[i][1] = nomePaziente.trim() + " (" + cfPaziente + ")"; // Paziente (CF)
			dati[i][2] = r.size() > 4 ? r.get(4) : ""; // Data Inizio
			dati[i][3] = r.size() > 5 ? r.get(5) : "In corso"; // Data Fine
			dati[i][4] = r.size() > 6 ? r.get(6) : ""; // Motivo
			dati[i][5] = r.size() > 8 && r.get(8) != null ? r.get(8) : ""; // Esito
		}

		JTable table = new JTable(dati, colonne) {
			@Override
			public boolean isCellEditable(int row, int column) { return false; }
		};
		gui.Login.setupTableStyle(table);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(800, 300));

		JOptionPane.showMessageDialog(null, scrollPane, "Storico Ricoveri per Letto " + idLetto + " - Reparto " + reparto, JOptionPane.PLAIN_MESSAGE);
	}

	public boolean gestisciCreazioneNuovaPrestazione() {
		// 1. Recupero automatico ID Agenda in base al medico loggato
		String matricolaMedico = utenteLoggato != null ? utenteLoggato.getMatricola() : "";
		boolean isMedico = utenteLoggato instanceof Medico;
		String idAgenda = "";

		if (isMedico) {
			List<ArrayList<String>> eventi = agendaDAO.getEventiByMatricola(matricolaMedico);
			if (eventi == null || eventi.isEmpty()) {
				agendaDAO.creaAgendaPerMedico(matricolaMedico);
				eventi = agendaDAO.getEventiByMatricola(matricolaMedico);
			}
			
			if (eventi != null && !eventi.isEmpty()) {
				idAgenda = eventi.get(0).get(0); // Prende il primo id_agenda associato al medico
			} else {
				JOptionPane.showMessageDialog(null, "Errore: Impossibile recuperare o creare un'agenda per questo medico.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		JComboBox<String> tipoProceduraInput = new JComboBox<>(new String[]{
				"Risonanza Magnetica", "Tomografia Computerizzata (TAC)", "Ecografia",
				"Elettrocardiogramma (ECG)", "Endoscopia", "Radiografia"
		});
		// Rende il menù a tendina "scrivibile", permettendo l'inserimento libero di un esame personalizzato
		tipoProceduraInput.setEditable(true); 

		JComboBox<String> esitoInput = new JComboBox<>(new String[]{
				"Erogata", "Non erogata"
		});

		// 2. Tendina dinamica dei Turni del Medico odierni
		String[] turniOdierni;
		if (isMedico) {
			turniOdierni = ottieniTurniOggiPerMedico(matricolaMedico);
		} else {
			turniOdierni = new String[]{"Inserimento manuale per Admin"};
		}

		JComponent turnoInput;
		if (isMedico) {
			turnoInput = new JComboBox<>(turniOdierni);
		} else {
			turnoInput = new JTextField();
		}

		// Popola la tendina per la selezione del paziente
		List<ArrayList<String>> tuttiPazienti = pazienteDAO.getAllPazienti();
		List<String> pazientiNomi = new ArrayList<>();
		List<String> pazientiCf = new ArrayList<>();
		if (tuttiPazienti != null) {
			for (List<String> datiPaziente : tuttiPazienti) {
				// Formato DAO: cf, nome, cognome, ...
				if (datiPaziente.size() >= 3) {
					String cf = datiPaziente.get(0);
					String nome = datiPaziente.get(1);
					String cognome = datiPaziente.get(2);
					pazientiNomi.add(cognome + " " + nome + " (" + cf + ")");
					pazientiCf.add(cf);
				}
			}
		}
		JComboBox<String> cfPazienteInput = new JComboBox<>(pazientiNomi.toArray(new String[0]));
		JTextField matricolaInput = new JTextField(matricolaMedico);
		matricolaInput.setEditable(!isMedico);
		JTextField idAgendaInput = new JTextField(idAgenda);
		idAgendaInput.setEditable(!isMedico);

		JPanel panel = new JPanel(new GridLayout(isMedico ? 4 : 6, 2, 10, 10));
		panel.add(new JLabel("Tipo Esame/Prestazione:")); panel.add(tipoProceduraInput);
		panel.add(new JLabel("Esito Prestazione:")); panel.add(esitoInput);
		panel.add(new JLabel("ID Turno (Oggi):")); panel.add(turnoInput);
		panel.add(new JLabel("CF Paziente:")); panel.add(cfPazienteInput);

		if (!isMedico) {
			panel.add(new JLabel("Matricola Medico:")); panel.add(matricolaInput);
			panel.add(new JLabel("ID Agenda:")); panel.add(idAgendaInput);
		}

		int result = JOptionPane.showConfirmDialog(null, panel, "Nuova Prestazione", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			try {
				String tipologiaPrestazione = (String) tipoProceduraInput.getSelectedItem();
				
				if (tipologiaPrestazione == null || tipologiaPrestazione.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Inserisci o seleziona un tipo di esame.", ERRORE_TITLE, JOptionPane.WARNING_MESSAGE);
					return false;
				}
				
				String esito = (String) esitoInput.getSelectedItem();
				String idTurno;
				if (isMedico) {
					idTurno = (String) ((JComboBox<?>) turnoInput).getSelectedItem();
				} else {
					idTurno = ((JTextField) turnoInput).getText().trim();
				}
				String cfPaziente = "";
				int selectedPatientIndex = cfPazienteInput.getSelectedIndex();
				if (selectedPatientIndex != -1) {
					cfPaziente = pazientiCf.get(selectedPatientIndex);
				}
				String matricolaFinale = matricolaInput.getText().trim();
				String idAgendaFinale = idAgendaInput.getText().trim();

				if (isMedico && (idTurno == null || idTurno.equals("Nessun turno odierno trovato"))) {
					JOptionPane.showMessageDialog(null, "Devi selezionare un turno valido (odierno).", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
					return false;
				}

				if (cfPaziente.isEmpty()) {
					JOptionPane.showMessageDialog(null, "È necessario selezionare un paziente.", ERRORE_TITLE, JOptionPane.WARNING_MESSAGE);
					return false;
				}

				// 4. Salvataggio della prestazione collegata
				boolean successo = prestazioneDAO.aggiungiPrestazione(tipologiaPrestazione, esito, idTurno, cfPaziente, matricolaFinale, idAgendaFinale);
				if (successo) {
					JOptionPane.showMessageDialog(null, "Prestazione aggiunta con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
					return true;
				} else {
					JOptionPane.showMessageDialog(null, ERRORE_AGGIUNTA_DATI, ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Errore nella conversione dell'ID turno in intero.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

	// HELPER: Metodo per prelevare i turni di oggi per il JComboBox
	private String[] ottieniTurniOggiPerMedico(String matricola) {
		java.util.List<String> turniOggi = new java.util.ArrayList<>();
		java.util.List<ArrayList<String>> turni = turnoDAO.getTurniByMedico(matricola);
		String today = java.time.LocalDate.now().toString(); // Formato YYYY-MM-DD
		
		if (turni != null) {
			for (ArrayList<String> t : turni) {
				if (t.size() > 2) {
					String dataTurno = t.get(2);
					if (dataTurno.startsWith(today)) {
						turniOggi.add(t.get(0)); // Aggiunge l'ID del turno
					}
				}
			}
		}
		
		if (turniOggi.isEmpty()) {
			return new String[]{"Nessun turno odierno trovato"};
		}
		return turniOggi.toArray(new String[0]);
	}

	public void apriSchermataPrestazioni(JFrame frameDaChiudere) {
		gui.Prestazioni prestazioniFrame = new gui.Prestazioni();
		impostaSchermata(prestazioniFrame, prestazioniFrame.mainPanel, "Ricerca Prestazioni Mediche", WindowConstants.DISPOSE_ON_CLOSE);
		
		prestazioniFrame.addNuovaPrestazioneListener(e -> {
			if (gestisciCreazioneNuovaPrestazione()) {
				caricaDatiPrestazioni(prestazioniFrame);
			}
		});

		// Aggiunta logica di ricerca e reset
		prestazioniFrame.addCercaListener(e -> gestisciRicercaPrestazioni(prestazioniFrame));
		prestazioniFrame.addResetListener(e -> {
			prestazioniFrame.resetCampiRicerca();
			caricaDatiPrestazioni(prestazioniFrame);
		});

		mostraFinestraSecondaria(prestazioniFrame, frameDaChiudere);

		// Utilizzo di SwingWorker per non bloccare la GUI durante il caricamento dati
		prestazioniFrame.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		SwingWorker<Object[][], Void> worker = new SwingWorker<Object[][], Void>() {
			@Override
			protected Object[][] doInBackground() throws Exception {
				List<ArrayList<String>> prestazioni;
				if (utenteLoggato instanceof Medico) {
					prestazioni = prestazioneDAO.getPrestazioniByMedico(utenteLoggato.getMatricola());
				} else {
					prestazioni = prestazioneDAO.getAllPrestazioni();
				}
				return formattaDatiPrestazioni(prestazioni);
			}
			@Override
			protected void done() {
				try { prestazioniFrame.aggiornaTabella(get()); } catch (Exception e) { LOGGER.warning("Errore caricamento prestazioni"); }
				prestazioniFrame.setCursor(java.awt.Cursor.getDefaultCursor());
			}
		};
		worker.execute();
	}

	private void caricaDatiPrestazioni(gui.Prestazioni prestazioniFrame) {
		List<ArrayList<String>> prestazioni;
		if (utenteLoggato instanceof Medico) {
			prestazioni = prestazioneDAO.getPrestazioniByMedico(utenteLoggato.getMatricola());
		} else {
			prestazioni = prestazioneDAO.getAllPrestazioni();
		}
		prestazioniFrame.aggiornaTabella(formattaDatiPrestazioni(prestazioni));
	}

	public void gestisciRicercaPrestazioni(gui.Prestazioni prestazioniFrame) {
		String idRicerca = prestazioniFrame.getCodPrestazione();
		String nomeCognomeRicerca = prestazioniFrame.getNomeCognome();
		String dataRicerca = prestazioniFrame.getData();

		List<ArrayList<String>> prestazioni;
		if (utenteLoggato instanceof Medico) {
			prestazioni = prestazioneDAO.getPrestazioniByMedico(utenteLoggato.getMatricola());
		} else {
			prestazioni = prestazioneDAO.getAllPrestazioni();
		}

		List<ArrayList<String>> risultati = new ArrayList<>();
		for (ArrayList<String> p : prestazioni) {
			boolean matchId = true;
			boolean matchNome = true;
			boolean matchData = true;

			if (idRicerca != null && !idRicerca.trim().isEmpty()) {
				matchId = p.get(0).equals(idRicerca.trim());
			}

			// La GUI Prestazioni usa un JSpinner, che ha sempre un valore.
			// A differenza di un JTextField con placeholder, qui il filtro per data è sempre attivo.
			// Rimuoviamo il check sul placeholder "AAAA-MM-GG".
			if (dataRicerca != null && !dataRicerca.trim().isEmpty()) {
				String dataTurno = p.size() > 3 && p.get(3) != null ? p.get(3) : "";
				matchData = dataTurno.startsWith(dataRicerca.trim());
			}

			if (nomeCognomeRicerca != null && !nomeCognomeRicerca.trim().isEmpty()) {
				String cf = p.size() > 4 ? p.get(4) : "";
				List<String> paziente = pazienteDAO.getPazienteByCf(cf);
				if (paziente != null && !paziente.isEmpty()) {
					String nome = paziente.size() > 1 ? paziente.get(1).toLowerCase() : "";
					String cognome = paziente.size() > 2 ? paziente.get(2).toLowerCase() : "";
					String searchStr = nomeCognomeRicerca.trim().toLowerCase();
					
					matchNome = (nome + " " + cognome).contains(searchStr) || 
								(cognome + " " + nome).contains(searchStr) || 
								cf.toLowerCase().contains(searchStr);
				} else {
					matchNome = false;
				}
			}

			if (matchId && matchNome && matchData) {
				risultati.add(p);
			}
		}

		prestazioniFrame.aggiornaTabella(formattaDatiPrestazioni(risultati));
		
		if (risultati.isEmpty()) {
			JOptionPane.showMessageDialog(prestazioniFrame, "Nessuna prestazione trovata con i criteri specificati.", INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void avviaSchermataMedico(String nomeUtente) {
		gui.SchermataMedico medicoHome = new gui.SchermataMedico(nomeUtente);
		impostaSchermata(medicoHome, medicoHome.mainPanel, "Ospedale - Home Medico", WindowConstants.DISPOSE_ON_CLOSE);

		homeFrame = medicoHome; // Imposta come schermata principale

		// Esposizione e deleghe per il Medico
		medicoHome.addPazientiListener(e -> apriSchermataPazienti(medicoHome));
		medicoHome.addLettiListener(e -> apriSchermataLetti(medicoHome));
		medicoHome.addPrestazioniListener(e -> apriSchermataPrestazioni(medicoHome));
		medicoHome.addDimissioniListener(e -> apriSchermataDimissioni(medicoHome));
		medicoHome.addRicoveroListener(e -> apriSchermataRicoveri(medicoHome));
		medicoHome.addTurniListener(e -> apriSchermataTurni(medicoHome));
		
		// Aggiunto listener per aprire il calendario settimanale
		medicoHome.addSettimanaleListener(e -> apriSchermataCalendario(medicoHome));
		
		medicoHome.addRicercaAgendaListener(e -> aggiornaAgendaGUI(medicoHome));
		medicoHome.addNewEventListener(e -> {
            if (gestisciNuovoEvento()) aggiornaAgendaGUI(medicoHome);
        });

        aggiornaAgendaGUI(medicoHome);
		
		// Listener per il click sul nome in alto (Modifica Profilo)
		medicoHome.addProfiloListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (gestisciModificaProfiloPersonale()) {
					// Aggiorna l'interfaccia con il nuovo nome se modificato
					medicoHome.updateUtenteLoggatoLabel("Dott. " + utenteLoggato.getNome() + " " + utenteLoggato.getCognome());
				}
			}
		});

		medicoHome.addEsciListener(e -> {
			int conferma = JOptionPane.showConfirmDialog(null, MSG_CONFERMA_USCITA, TITLE_CONFERMA_USCITA, JOptionPane.YES_NO_OPTION);
			if (conferma == JOptionPane.YES_OPTION) {
				// Torna alla schermata di login
				medicoHome.dispose();
				logout();
				avviaSchermataLogin();
			}
		});

		medicoHome.setVisible(true);
	}

	public void apriSchermataMedici(JFrame frameDaChiudere) {
		gui.Medici mediciFrame = new gui.Medici();
		impostaSchermata(mediciFrame, mediciFrame.mainPanel, "Gestione Medici", WindowConstants.DISPOSE_ON_CLOSE);

        mediciFrame.addNuovoMedicoListener(e -> {
            if (gestisciCreazioneNuovoMedico()) {
                mediciFrame.aggiornaTabella(formattaDatiMedici(medicoDAO.getAllMedici()));
            }
        });

		// Collegamento per il pulsante "Aggiorna Medico"
		mediciFrame.addModificaMedicoListener(e -> {
			String[] datiSelezionati = mediciFrame.getDatiMedicoSelezionato();
			if (datiSelezionati != null && datiSelezionati.length > 0) {
				String matricola = datiSelezionati[0]; // La matricola è il primo elemento
				if (gestisciModificaMedico(matricola)) {
					// Ricarica la tabella per mostrare i dati aggiornati
					mediciFrame.aggiornaTabella(formattaDatiMedici(medicoDAO.getAllMedici()));
				}
			} else {
				JOptionPane.showMessageDialog(mediciFrame, "Per favore, seleziona un medico dalla tabella prima di cliccare su 'Aggiorna Medico'.", "Nessun Medico Selezionato", JOptionPane.WARNING_MESSAGE);
			}
		});

		mediciFrame.addAssenzaListener(e -> {
			String[] datiSelezionati = mediciFrame.getDatiMedicoSelezionato();
			if (datiSelezionati != null && datiSelezionati.length > 0) {
				String matricola = datiSelezionati[0];
				List<ArrayList<String>> assenze = assenzaDAO.getAssenzeByMedico(matricola);
				List<String> assenzaCorrente = null;
				java.time.LocalDate oggi = java.time.LocalDate.now();
				for(List<String> a : assenze) {
					if ("true".equalsIgnoreCase(a.get(4))) {
						try {
							java.time.LocalDate dataInizio = java.time.LocalDate.parse(a.get(1));
							java.time.LocalDate dataFine = java.time.LocalDate.parse(a.get(2));
							if (!oggi.isBefore(dataInizio) && !oggi.isAfter(dataFine)) {
								assenzaCorrente = a;
								break;
							}
						} catch(Exception ex) {}
					}
				}
				
				if (assenzaCorrente != null) {
					String messaggio = "Il medico è attualmente assente.\n" +
									   "Data Inizio: " + assenzaCorrente.get(1) + "\n" +
									   "Data Fine: " + assenzaCorrente.get(2) + "\n" +
									   "Motivazione: " + assenzaCorrente.get(3);
					Object[] options = {"Chiudi", "Revoca Assenza", "Aggiungi Nuova Assenza"};
					int choice = JOptionPane.showOptionDialog(mediciFrame, messaggio, "Dettagli Assenza Approvata",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
					
					if (choice == 1) { // Revoca Assenza
						int conferma = JOptionPane.showConfirmDialog(mediciFrame, "Sei sicuro di voler revocare questa assenza?", "Conferma Revoca", JOptionPane.YES_NO_OPTION);
						if (conferma == JOptionPane.YES_OPTION) {
							if (eliminaAssenza(matricola, assenzaCorrente.get(1))) {
								JOptionPane.showMessageDialog(mediciFrame, "Assenza revocata con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
								mediciFrame.aggiornaTabella(formattaDatiMedici(medicoDAO.getAllMedici()));
							} else {
								JOptionPane.showMessageDialog(mediciFrame, "Errore durante la revoca dell'assenza.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
							}
						}
					} else if (choice == 2) { // Aggiungi Nuova Assenza
						if (gestisciCreazioneNuovaAssenza(matricola)) {
							mediciFrame.aggiornaTabella(formattaDatiMedici(medicoDAO.getAllMedici()));
						}
					}
				} else {
					if (gestisciCreazioneNuovaAssenza(matricola)) {
						mediciFrame.aggiornaTabella(formattaDatiMedici(medicoDAO.getAllMedici()));
					}
				}
			} else {
				JOptionPane.showMessageDialog(mediciFrame, "Per favore, seleziona un medico dalla tabella prima di gestire un'assenza.", "Nessun Medico Selezionato", JOptionPane.WARNING_MESSAGE);
			}
		});

		mostraFinestraSecondaria(mediciFrame, frameDaChiudere);

		// Utilizzo di SwingWorker per non bloccare la GUI a causa delle Query N+1
		mediciFrame.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		SwingWorker<Object[][], Void> worker = new SwingWorker<Object[][], Void>() {
			@Override
			protected Object[][] doInBackground() throws Exception {
				return formattaDatiMedici(getAllMedici()); // Lavoro pensante delegato al thread secondario
			}
			@Override
			protected void done() {
				try { mediciFrame.aggiornaTabella(get()); } catch (Exception e) { LOGGER.warning("Errore caricamento medici"); }
				mediciFrame.setCursor(java.awt.Cursor.getDefaultCursor());
			}
		};
		worker.execute();
	}

	public void apriSchermataDimissioni(JFrame frameDaChiudere) {
		gui.Dimissioni dimissioniFrame = new gui.Dimissioni();
		impostaSchermata(dimissioniFrame, dimissioniFrame.mainPanel, "Ricerca Dimissioni", WindowConstants.DISPOSE_ON_CLOSE);
	
		// Logica per il pulsante "Archivia Dimissione"
        dimissioniFrame.addArchiviaDimissioneListener(e -> {
            String idSelezionato = dimissioniFrame.getIdRicoveroSelezionato();
            if (idSelezionato != null) {
                if (gestisciArchiviaDimissione(idSelezionato)) {
                    dimissioniFrame.aggiornaTabella(formattaDatiDimissioni(ricercaDimissioni()));
                }
            } else {
                JOptionPane.showMessageDialog(dimissioniFrame, "Seleziona una dimissione dalla tabella per archiviarla.", INFO_TITLE, JOptionPane.WARNING_MESSAGE);
            }
        });

		// Logica per il pulsante "Cerca"
		dimissioniFrame.addCercaListener(e -> gestisciRicercaDimissioni(dimissioniFrame));

		// Logica per il pulsante "Reset"
		dimissioniFrame.addResetListener(e -> {
			dimissioniFrame.resetCampiRicerca();
			dimissioniFrame.aggiornaTabella(formattaDatiDimissioni(ricercaDimissioni()));
		});

		// Logica per il pulsante "Lettura Dimissione"
		dimissioniFrame.addLetturaDimissioneListener(e -> {
			String cfSelezionato = dimissioniFrame.getCFPazienteSelezionato();
			if (cfSelezionato != null) {
				mostraDettagliDimissione(cfSelezionato);
			} else {
				JOptionPane.showMessageDialog(dimissioniFrame, "Seleziona una dimissione dalla tabella per vederne i dettagli.", INFO_TITLE, JOptionPane.WARNING_MESSAGE);
			}
		});

		mostraFinestraSecondaria(dimissioniFrame, frameDaChiudere);

		// Utilizzo di SwingWorker per caricare i dati in background
		dimissioniFrame.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		SwingWorker<Object[][], Void> worker = new SwingWorker<Object[][], Void>() {
			@Override
			protected Object[][] doInBackground() throws Exception {
				return formattaDatiDimissioni(ricercaDimissioni());
			}
			@Override
			protected void done() {
				try { dimissioniFrame.aggiornaTabella(get()); } catch (Exception e) { LOGGER.warning("Errore caricamento dimissioni"); }
				dimissioniFrame.setCursor(java.awt.Cursor.getDefaultCursor());
			}
		};
		worker.execute();
	}

	public void apriSchermataRicoveri(JFrame frameDaChiudere) {
		gui.Ricovero ricoveroFrame = new gui.Ricovero();
		impostaSchermata(ricoveroFrame, ricoveroFrame.mainPanel, "Ricerca Ricovero", WindowConstants.DISPOSE_ON_CLOSE);

        ricoveroFrame.addNuovoRicoveroListener(e -> {
            if (gestisciCreazioneNuovoRicovero()) {
                caricaDatiRicoveriAsync(ricoveroFrame);
            }
        });

		ricoveroFrame.addGestisciRicoveroListener(e -> {
			String[] selezionato = ricoveroFrame.getRicoveroSelezionato();
			if (selezionato != null) {
				if (gestisciDimissioneDaRicovero(selezionato[1])) {
					caricaDatiRicoveriAsync(ricoveroFrame); // Ricarica la tabella dopo la dimissione
				}
			} else {
				JOptionPane.showMessageDialog(ricoveroFrame, "Per favore, seleziona un ricovero dalla tabella prima di cliccare su Gestisci Ricovero.", "Nessun Ricovero Selezionato", JOptionPane.WARNING_MESSAGE);
			}
		});

		ricoveroFrame.addCercaListener(e -> gestisciRicercaRicoveri(ricoveroFrame));
		ricoveroFrame.addResetListener(e -> {
			ricoveroFrame.resetCampiRicerca();
			caricaDatiRicoveriAsync(ricoveroFrame);
		});

		mostraFinestraSecondaria(ricoveroFrame, frameDaChiudere);

		// Caricamento iniziale asincrono
		caricaDatiRicoveriAsync(ricoveroFrame);
	}

	public void apriSchermataTurni(JFrame frameDaChiudere) {
		gui.Turni turniFrame = new gui.Turni();
		impostaSchermata(turniFrame, turniFrame.panelHome, "Gestione Turni Lavorativi", WindowConstants.DISPOSE_ON_CLOSE);

        turniFrame.addNuovoTurnoListener(e -> {
            if (gestisciCreazioneNuovoTurno()) {
                caricaDatiTurni(turniFrame);
            }
        });

		// Aggiungiamo il listener per la modifica del turno
		turniFrame.addModificaTurnoListener(e -> {
			String[] datiSelezionati = turniFrame.getDatiTurnoSelezionato();
			if (datiSelezionati != null) {
				String matricolaTurno = datiSelezionati[1]; // datiSelezionati[1] contiene la matricola

				// Controllo per impedire ai medici di modificare i turni dei colleghi
				if (utenteLoggato instanceof Medico && !utenteLoggato.getMatricola().equals(matricolaTurno)) {
					JOptionPane.showMessageDialog(turniFrame, "Non sei autorizzato a modificare i turni di altri colleghi.", "Accesso Negato", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// datiSelezionati[0] = data, [1] = matricola, [2] = orario
				if (gestisciModificaTurno(matricolaTurno, datiSelezionati[0], datiSelezionati[2])) {
					caricaDatiTurni(turniFrame); // Ricarica i dati se la modifica ha successo
				}
			} else {
				JOptionPane.showMessageDialog(turniFrame, "Per favore, seleziona un turno dalla tabella prima di cliccare Modifica.", "Nessun Turno Selezionato", JOptionPane.WARNING_MESSAGE);
			}
		});

		mostraFinestraSecondaria(turniFrame, frameDaChiudere);

		// Utilizzo di SwingWorker per caricare i dati in background
		turniFrame.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		SwingWorker<Object[][], Void> worker = new SwingWorker<Object[][], Void>() {
			@Override
			protected Object[][] doInBackground() throws Exception {
				List<ArrayList<String>> turni = new ArrayList<>();
				List<ArrayList<String>> medici = medicoDAO.getAllMedici();
				if (medici != null) {
					for (ArrayList<String> medico : medici) {
						if (medico.size() > 4) {
							List<ArrayList<String>> turniMedico = turnoDAO.getTurniByMedico(medico.get(4));
							if (turniMedico != null) turni.addAll(turniMedico);
						}
					}
				}
				return formattaDatiTurni(turni);
			}
			@Override
			protected void done() {
				try { turniFrame.aggiornaTabella(get()); } catch (Exception e) { LOGGER.warning("Errore caricamento turni"); }
				turniFrame.setCursor(java.awt.Cursor.getDefaultCursor());
			}
		};
		worker.execute();
	}

	public void apriSchermataCalendario(JFrame frameDaChiudere) {
		gui.Calendario calendarioFrame = new gui.Calendario();
		impostaSchermata(calendarioFrame, calendarioFrame.mainPanel, "Calendario Settimanale", WindowConstants.DISPOSE_ON_CLOSE);
	
		// Funzione per ricaricare gli eventi nel calendario
		Runnable ricaricaEventi = () -> {
			if (utenteLoggato != null) {
				List<ArrayList<String>> eventi = getEventiPerUtente(utenteLoggato.getMatricola());
				calendarioFrame.setEventi(eventi);
			}
		};
	
		calendarioFrame.addAggiungiEventoListener(e -> {
			// Recupera la data e l'ora dalla cella selezionata, se presente
			LocalDateTime dataOraSelezionata = calendarioFrame.getTimestampCellaSelezionata();
			if (gestisciNuovoEvento(dataOraSelezionata)) {
				ricaricaEventi.run(); // Ricarica gli eventi dopo l'aggiunta/modifica
			}
		});
	
		calendarioFrame.addModificaEventoListener(e -> {
			ArrayList<String> eventoSelezionato = calendarioFrame.getEventoSelezionato();
			if (eventoSelezionato == null) {
				JOptionPane.showMessageDialog(calendarioFrame, "Seleziona un evento dal calendario per modificarlo.", "Nessun Evento Selezionato", JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (gestisciModificaEvento(eventoSelezionato)) {
				ricaricaEventi.run(); // Ricarica gli eventi dopo la modifica
			}
		});
	
		ricaricaEventi.run(); // Caricamento iniziale degli eventi
		mostraFinestraSecondaria(calendarioFrame, frameDaChiudere);
	}

	public boolean gestisciNuovoEvento() { return gestisciNuovoEvento(null); }

	public boolean gestisciNuovoEvento(LocalDateTime dataOraDefault) {
		String defaultMatricola = utenteLoggato != null ? utenteLoggato.getMatricola() : "";

		String dataDefault = DEFAULT_DATE;
		String oraInizioDefault = "08:30:00";
		String oraFineDefault = "10:00:00";

		if (dataOraDefault != null) {
			dataDefault = dataOraDefault.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
			oraInizioDefault = dataOraDefault.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
			oraFineDefault = dataOraDefault.toLocalTime().plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		}

		JTextField matricolaInput = new JTextField(defaultMatricola);
		JTextField dataInput = new JTextField(dataDefault);
		JTextField oraInizioInput = new JTextField(oraInizioDefault);
		JTextField oraFineInput = new JTextField(oraFineDefault);
		JTextField titoloInput = new JTextField("Nuova Visita");
		JTextField descrizioneInput = new JTextField("-");

		JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
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
			    java.sql.Timestamp tsInizio = java.sql.Timestamp.valueOf(inizio);
			    java.sql.Timestamp tsFine = java.sql.Timestamp.valueOf(fine);

			    boolean successo = addEvento(matricola, titolo, descrizione, tsInizio, tsFine);
                if (successo) {
                    JOptionPane.showMessageDialog(null, "Evento inserito con successo nel DB!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Errore. Verifica eventuali sovrapposizioni.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }  catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, "Formato data o ora non valido.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Errore nella creazione dell'evento.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
            }
		}
        return false;
	}

	private boolean gestisciModificaEvento(ArrayList<String> evento) {
		if (evento == null || evento.isEmpty()) return false;

		try {
			int idEvento = Integer.parseInt(evento.get(0));
			String titolo = evento.get(1);
			String descrizione = evento.get(2);
			String matricola = evento.get(3);
			java.sql.Timestamp tsInizio = java.sql.Timestamp.valueOf(evento.get(4));
			java.sql.Timestamp tsFine = java.sql.Timestamp.valueOf(evento.get(5));

			LocalDateTime ldtInizio = tsInizio.toLocalDateTime();
			LocalDateTime ldtFine = tsFine.toLocalDateTime();

			JTextField titoloInput = new JTextField(titolo);
			JTextField descrizioneInput = new JTextField(descrizione);
			JTextField dataInput = new JTextField(ldtInizio.toLocalDate().toString());
			JTextField oraInizioInput = new JTextField(ldtInizio.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
			JTextField oraFineInput = new JTextField(ldtFine.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

			JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
			panel.add(new JLabel("Matricola:")); panel.add(new JLabel(matricola)); // Non modificabile
			panel.add(new JLabel("Titolo:")); panel.add(titoloInput);
			panel.add(new JLabel("Descrizione:")); panel.add(descrizioneInput);
			panel.add(new JLabel(LABEL_DATA)); panel.add(dataInput);
			panel.add(new JLabel(LABEL_ORA_INIZIO)); panel.add(oraInizioInput);
			panel.add(new JLabel(LABEL_ORA_FINE)); panel.add(oraFineInput);

			Object[] options = {"Salva Modifiche", "Elimina Evento", "Annulla"};
			int choice = JOptionPane.showOptionDialog(null, panel, "Gestisci Evento Agenda",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

			if (choice == 0) { // Salva Modifiche
				String nuovoTitolo = titoloInput.getText().trim();
				String nuovaDescrizione = descrizioneInput.getText().trim();
				String nuovoInizioStr = dataInput.getText().trim() + " " + oraInizioInput.getText().trim();
				String nuovoFineStr = dataInput.getText().trim() + " " + oraFineInput.getText().trim();

				java.sql.Timestamp nuovoInizio = java.sql.Timestamp.valueOf(nuovoInizioStr);
				java.sql.Timestamp nuovoFine = java.sql.Timestamp.valueOf(nuovoFineStr);

				boolean successo = updateEvento(idEvento, matricola, nuovoTitolo, nuovaDescrizione, nuovoInizio, nuovoFine);
				if (successo) {
					JOptionPane.showMessageDialog(null, "Evento aggiornato con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
					return true;
				}
			} else if (choice == 1) { // Elimina Evento
				int conferma = JOptionPane.showConfirmDialog(null,
						"Sei sicuro di voler eliminare questo evento?\nL'azione è irreversibile.",
						"Conferma Eliminazione Evento",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (conferma == JOptionPane.YES_OPTION) {
					boolean successo = deleteEvento(idEvento);
					if (successo) {
						JOptionPane.showMessageDialog(null, "Evento eliminato con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
						return true;
					} else {
						JOptionPane.showMessageDialog(null, "Errore durante l'eliminazione dell'evento.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Errore durante la gestione della modifica evento", e);
			JOptionPane.showMessageDialog(null, "Errore: dati dell'evento non validi o formato data/ora errato (HH:mm:ss).", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	public boolean gestisciModificaProfiloPersonale() {
		String matricola = utenteLoggato.getMatricola();
		List<String> datiMedico = medicoDAO.getMedicoByMatricola(matricola);
		if (datiMedico == null || datiMedico.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Impossibile trovare i dati per il tuo profilo.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
			return false;
		}

		// Pre-popola i campi con i dati esistenti, bloccando quelli non modificabili dall'utente
		JTextField nomeInput = new JTextField(datiMedico.get(0));
		JTextField cognomeInput = new JTextField(datiMedico.get(1));
		JTextField loginInput = new JTextField(datiMedico.get(2));
		loginInput.setEditable(false); // Login assegnato
		JTextField iscrizioneInput = new JTextField(datiMedico.get(5));
		JTextField specializzazioneInput = new JTextField(datiMedico.get(6));
		JComboBox<String> repartoInput = new JComboBox<>(new String[]{
				"Nessuno", "Cardiologia", "Ortopedia", "Chirurgia Generale"
		});
		repartoInput.setSelectedItem(datiMedico.get(7));
		// repartoInput.setEnabled(false); // Rimosso blocco per permettere al medico di modificare il proprio reparto

		JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
		panel.add(new JLabel("Matricola:")); panel.add(new JLabel(" " + matricola)); // Non modificabile
		panel.add(new JLabel("Username (Login):")); panel.add(loginInput);
		panel.add(new JLabel(LABEL_NOME)); panel.add(nomeInput);
		panel.add(new JLabel(LABEL_COGNOME)); panel.add(cognomeInput);
		panel.add(new JLabel("Data Iscrizione Albo (AAAA-MM-GG):")); panel.add(iscrizioneInput);
		panel.add(new JLabel("Specializzazione:")); panel.add(specializzazioneInput);
		panel.add(new JLabel("Reparto Assegnato:")); panel.add(repartoInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Il Mio Profilo", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			try {
				boolean successo = aggiornaMedico(nomeInput.getText().trim(), cognomeInput.getText().trim(), matricola, iscrizioneInput.getText().trim(), specializzazioneInput.getText().trim(), (String) repartoInput.getSelectedItem());
				if (successo) {
					JOptionPane.showMessageDialog(null, "Profilo aggiornato con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
					this.utenteLoggato = new Medico(matricola, nomeInput.getText().trim(), cognomeInput.getText().trim(), loginInput.getText().trim(), "medico");
					return true;
				} else {
					JOptionPane.showMessageDialog(null, "Errore durante l'aggiornamento. Controlla la validità dei dati.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
				}
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, "Formato data non valido. Assicurati di usare AAAA-MM-GG.", "Errore di Formato", JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

	public boolean gestisciModificaProfiloAmministratore() {
		String matricola = utenteLoggato.getMatricola();
		
		JTextField nomeInput = new JTextField(utenteLoggato.getNome());
		JTextField cognomeInput = new JTextField(utenteLoggato.getCognome());
		JTextField loginInput = new JTextField(utenteLoggato.getLogin());
		loginInput.setEditable(false); // Login non modificabile

		JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
		panel.add(new JLabel("Matricola:")); panel.add(new JLabel(" " + matricola));
		panel.add(new JLabel("Username (Login):")); panel.add(loginInput);
		panel.add(new JLabel(LABEL_NOME)); panel.add(nomeInput);
		panel.add(new JLabel(LABEL_COGNOME)); panel.add(cognomeInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Il Mio Profilo (Amministratore)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			try {
				String nuovoNome = nomeInput.getText().trim();
				String nuovoCognome = cognomeInput.getText().trim();

				// TODO: Sostituire `successo = true` con la vera chiamata di aggiornamento al DB, per es:
				// boolean successo = amministratoreDAO.aggiornaAmministratore(matricola, nuovoNome, nuovoCognome);
				boolean successo = true; 

				if (successo) {
					JOptionPane.showMessageDialog(null, "Profilo aggiornato con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
					// Aggiorna localmente l'utente loggato per riflettere le modifiche nell'app
					this.utenteLoggato = new Amministratore(matricola, nuovoNome, nuovoCognome, loginInput.getText().trim(), "amministratore");
					return true;
				} else {
					JOptionPane.showMessageDialog(null, "Errore durante l'aggiornamento. Controlla la validità dei dati.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Si è verificato un errore inaspettato.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

	public boolean gestisciModificaMedico(String matricola) {
		List<String> datiMedico = medicoDAO.getMedicoByMatricola(matricola);
		if (datiMedico == null || datiMedico.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Impossibile trovare i dati per il medico con matricola " + matricola, ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
			return false;
		}

		// Pre-popola i campi con i dati esistenti
		JTextField nomeInput = new JTextField(datiMedico.get(0));
		JTextField cognomeInput = new JTextField(datiMedico.get(1));
		JTextField iscrizioneInput = new JTextField(datiMedico.get(5));
		JTextField specializzazioneInput = new JTextField(datiMedico.get(6));
		JComboBox<String> repartoInput = new JComboBox<>(new String[]{
				"Nessuno", "Cardiologia", "Ortopedia", "Chirurgia Generale"
		});
		repartoInput.setSelectedItem(datiMedico.get(7));

		JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
		panel.add(new JLabel("Matricola:"));
		panel.add(new JLabel(matricola)); // Non editabile
		panel.add(new JLabel(LABEL_NOME)); panel.add(nomeInput);
		panel.add(new JLabel(LABEL_COGNOME)); panel.add(cognomeInput);
		panel.add(new JLabel("Data Iscrizione Albo (AAAA-MM-GG):")); panel.add(iscrizioneInput);
		panel.add(new JLabel("Specializzazione:")); panel.add(specializzazioneInput);
		panel.add(new JLabel("Reparto:")); panel.add(repartoInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Modifica Dati Medico", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			String nome = nomeInput.getText().trim();
			String cognome = cognomeInput.getText().trim();
			String iscrizioneAlbo = iscrizioneInput.getText().trim();
			String specializzazione = specializzazioneInput.getText().trim();
			String reparto = (String) repartoInput.getSelectedItem();

			try {
				boolean successo = aggiornaMedico(nome, cognome, matricola, iscrizioneAlbo, specializzazione, reparto);

				if (successo) {
					JOptionPane.showMessageDialog(null, "Dati del medico aggiornati con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
					return true;
				} else {
					JOptionPane.showMessageDialog(null, "Errore durante l'aggiornamento. Controlla la validità dei dati.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
				}
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, "Formato data non valido. Assicurati di usare AAAA-MM-GG per la Data Iscrizione Albo.", "Errore di Formato", JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

	public boolean gestisciCreazioneNuovaAssenza(String matricola) {
		JTextField dataInizioInput = new JTextField(DEFAULT_DATE);
		JTextField dataFineInput = new JTextField(DEFAULT_DATE);
		JTextField motivazioneInput = new JTextField();
		JCheckBox approvataCheckbox = new JCheckBox("Approvata", true);

		JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
		panel.add(new JLabel(LABEL_MATRICOLA_MEDICO));
		panel.add(new JLabel(matricola));
		panel.add(new JLabel("Data Inizio (AAAA-MM-GG):")); panel.add(dataInizioInput);
		panel.add(new JLabel("Data Fine (AAAA-MM-GG):")); panel.add(dataFineInput);
		panel.add(new JLabel("Motivazione:")); panel.add(motivazioneInput);
		panel.add(new JLabel("")); panel.add(approvataCheckbox);

		int result = JOptionPane.showConfirmDialog(null, panel, "Registra Nuova Assenza", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			String dataInizio = dataInizioInput.getText().trim();
			String dataFine = dataFineInput.getText().trim();
			String motivazione = motivazioneInput.getText().trim();
			boolean approvata = approvataCheckbox.isSelected();

			try {
				boolean successo = aggiungiAssenza(matricola, dataInizio, dataFine, motivazione, approvata);

				if (successo) {
					JOptionPane.showMessageDialog(null, "Assenza aggiunta con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
					return true;
				} else {
					JOptionPane.showMessageDialog(null, ERRORE_AGGIUNTA_DATI, ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
				}
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, "Formato data non valido. Assicurati di usare AAAA-MM-GG.", "Errore di Formato", JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

	private List<ArrayList<String>> formattaDatiPazienti(List<ArrayList<String>> pazientiDb) {
		if (pazientiDb == null) return new ArrayList<>();
		
		// Creiamo una mappa dei ricoveri attivi per associare velocemente il CF all'ID del Letto
		java.util.Map<String, String[]> pazientiRicoverati = new java.util.HashMap<>();
		List<ArrayList<String>> ricoveriAttivi = ricoveroDAO.getAllRicoveriAttivi();
		if (ricoveriAttivi != null) {
			for (List<String> ricovero : ricoveriAttivi) {
				if (ricovero.size() > 3) {
					pazientiRicoverati.put(ricovero.get(1), new String[]{ricovero.get(2), ricovero.get(3)}); // Mappa CF -> [ID Letto, Reparto]
				}
			}
		}

		for (ArrayList<String> p : pazientiDb) {
			String cf = p.get(0);
			String[] infoRicovero = pazientiRicoverati.get(cf);
			String idLetto = infoRicovero != null ? infoRicovero[0] : "";
			String reparto = infoRicovero != null ? infoRicovero[1] : "";
			// Il DAO ci fornisce 7 attributi grezzi. Aggiungiamo l'id_letto come 8° elemento (indice 7) e reparto come 9° (indice 8).
			if (p.size() == 7) { p.add(idLetto); p.add(reparto); }
			else if (p.size() > 7) { 
                p.set(7, idLetto); 
                if (p.size() > 8) p.set(8, reparto); else p.add(reparto); 
            }
		}
		return pazientiDb;
	}

	private Object[][] formattaDatiMedici(List<ArrayList<String>> mediciDb) {
		if (mediciDb == null) return new Object[0][0];
		Object[][] dati = new Object[mediciDb.size()][6];
		java.time.LocalDate oggi = java.time.LocalDate.now();
		for (int i = 0; i < mediciDb.size(); i++) {
			List<String> m = mediciDb.get(i);
			try {
				String matricola = m.size() > 4 ? m.get(4) : "-";
				dati[i][0] = matricola;
				dati[i][1] = (m.size() > 1 ? m.get(1) : "") + " " + (!m.isEmpty() ? m.get(0) : ""); // Cognome Nome
				dati[i][2] = m.size() > 6 ? m.get(6) : "-"; // Specializzazione
				dati[i][3] = m.size() > 7 ? m.get(7) : "-"; // Reparto Assegnato
				
				String stato = "Attivo";
				if (!"-".equals(matricola)) {
					List<ArrayList<String>> assenze = assenzaDAO.getAssenzeByMedico(matricola);
					for (List<String> assenza : assenze) {
						if ("true".equalsIgnoreCase(assenza.get(4))) { // Approvata
							try {
								java.time.LocalDate dataInizio = java.time.LocalDate.parse(assenza.get(1));
								java.time.LocalDate dataFine = java.time.LocalDate.parse(assenza.get(2));
								if (!oggi.isBefore(dataInizio) && !oggi.isAfter(dataFine)) {
									stato = "Assente";
									break;
								}
							} catch (Exception ex) {
								LOGGER.warning("Errore parsing date assenza: " + ex.getMessage());
							}
						}
					}
				}
				dati[i][4] = stato; // Stato
				dati[i][5] = "-"; // Note/Contatto
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
				String cf = d.size() > 1 ? d.get(1) : "-";
				List<String> paziente = pazienteDAO.getPazienteByCf(cf);
				String nomePaziente = "Sconosciuto";
				if (paziente != null && !paziente.isEmpty()) {
					nomePaziente = (paziente.size() > 1 ? paziente.get(1) : "") + " " + (paziente.size() > 2 ? paziente.get(2) : "");
				}
				
				dati[i][0] = d.get(0); // ID Ricovero
				dati[i][1] = nomePaziente.trim(); // Paziente
				dati[i][2] = cf; // CF
				dati[i][3] = d.size() > 3 ? d.get(3) : "-"; // Reparto
				dati[i][4] = d.size() > 8 ? d.get(8) : "-"; // Tipo (Esito)
				dati[i][5] = d.size() > 5 ? d.get(5) : "-"; // Data
			} catch (Exception e) {
				final int riga = i;
				LOGGER.warning(() -> "Errore nella formattazione dei dati dimissioni alla riga " + riga + ": " + e.getMessage());
			}
		}
		return dati;
	}

	private Object[][] formattaDatiPrestazioni(List<ArrayList<String>> prestazioniDb) {
		if (prestazioniDb == null) return new Object[0][0];
		Object[][] dati = new Object[prestazioniDb.size()][6];
		for (int i = 0; i < prestazioniDb.size(); i++) {
			List<String> p = prestazioniDb.get(i);
			try {
				// DAO returns: 0:id, 1:tipologia_prestazione, 2:esito, 3:data, 4:cf, 5:matricola
				dati[i][0] = p.size() > 0 ? p.get(0) : "-"; // ID Prestaz.
				dati[i][1] = p.size() > 1 ? p.get(1) : "-"; // Tipo Prestazione
				dati[i][2] = p.size() > 2 ? p.get(2) : "-"; // Esito
				
				String dataTurno = p.size() > 3 && p.get(3) != null ? p.get(3) : "-";
				String turnoFormattato = "-";
				if (!dataTurno.equals("-") && !dataTurno.trim().isEmpty()) {
					try {
						String soloData = dataTurno.contains(" ") ? dataTurno.split(" ")[0] : dataTurno;
						java.time.LocalDate date = java.time.LocalDate.parse(soloData);
						java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
						turnoFormattato = date.format(formatter);
					} catch (Exception ex) {
						turnoFormattato = dataTurno;
					}
				}
				dati[i][3] = turnoFormattato; // Data Prestazione
				dati[i][4] = p.size() > 4 ? p.get(4) : "-"; // CF Paziente

				String matricola = p.size() > 5 && p.get(5) != null ? p.get(5) : "";
				String repartoErogante = "-";
				if (!matricola.trim().isEmpty()) {
					List<String> medico = medicoDAO.getMedicoByMatricola(matricola);
					if (medico != null && medico.size() > 7 && medico.get(7) != null && !medico.get(7).trim().isEmpty()) {
						repartoErogante = medico.get(7);
					}
				}
				dati[i][5] = repartoErogante; // Reparto Erogante
			} catch (Exception e) {
				final int riga = i;
				LOGGER.warning(() -> "Errore nella formattazione dei dati prestazioni alla riga " + riga + ": " + e.getMessage());
			}
		}
		return dati;
	}

	private Object[][] formattaDatiTurni(List<ArrayList<String>> turniDb) {
		if (turniDb == null) return new Object[0][0];
		// La GUI si aspetta 7 colonne: ID Turno, Data, Matricola, Dipendente, Ruolo, Reparto, Orario
		Object[][] dati = new Object[turniDb.size()][7];
		for (int i = 0; i < turniDb.size(); i++) {
			List<String> t = turniDb.get(i);
			try {
				// Leggiamo i dati nell'ordine corretto fornito dal DAO
				String idTurno =   t.size() > 0 && t.get(0) != null ? t.get(0) : "";
				String matricola = t.size() > 1 && t.get(1) != null ? t.get(1) : "";
				String data =      t.size() > 2 && t.get(2) != null ? t.get(2) : "";
				String oraInizio = t.size() > 3 && t.get(3) != null ? t.get(3) : "";
				String oraFine =   t.size() > 4 && t.get(4) != null ? t.get(4) : "";

				// Recuperiamo le info del medico (nome, cognome, reparto)
				List<String> medico = medicoDAO.getMedicoByMatricola(matricola);
				String nomeCognome = "Sconosciuto";
				String reparto = "-";
				if (medico != null && !medico.isEmpty()) {
					nomeCognome = (medico.size() > 1 ? medico.get(1) : "") + " " + (medico.size() > 0 ? medico.get(0) : ""); // Cognome + Nome
					if (medico.size() > 7 && medico.get(7) != null && !medico.get(7).trim().isEmpty()) {
						reparto = medico.get(7);
					}
				}
				// Costruiamo la riga nell'ordine atteso dalla GUI
				dati[i][0] = idTurno;
				dati[i][1] = data;
				dati[i][2] = matricola;
				dati[i][3] = nomeCognome.trim();
				dati[i][4] = "Medico";
				dati[i][5] = reparto;
				dati[i][6] = oraInizio + " - " + oraFine;
			} catch (Exception e) {
				final int riga = i;
				LOGGER.warning(() -> "Errore nella formattazione dei dati turni alla riga " + riga + ": " + e.getMessage());
			}
		}
		return dati;
	}

	private Object[][] formattaDatiRicoveri(List<ArrayList<String>> ricoveriDb) {
		if (ricoveriDb == null) return new Object[0][0];
		// Colonne: ID Ricovero, Paziente, Codice Fiscale, Motivazione Ricovero, Reparto di Ricovero, Data e Ora Ingresso
		Object[][] dati = new Object[ricoveriDb.size()][6];
		for (int i = 0; i < ricoveriDb.size(); i++) {
			List<String> r = ricoveriDb.get(i);
			try {
				String idRicovero = r.get(0);
				String cf = r.get(1);
				String idLetto = r.get(2);
				String reparto = r.get(3);
				String dataInizio = r.get(4);
				String motivazione = r.get(5);
				
				List<String> paziente = pazienteDAO.getPazienteByCf(cf);
				String nomePaziente = "Sconosciuto";
				if (paziente != null && !paziente.isEmpty()) {
					nomePaziente = (paziente.size() > 1 ? paziente.get(1) : "") + " " + (paziente.size() > 2 ? paziente.get(2) : "");
				}
				
				dati[i][0] = idRicovero;
				dati[i][1] = nomePaziente.trim();
				dati[i][2] = cf;
				dati[i][3] = motivazione;
				dati[i][4] = reparto;
				dati[i][5] = dataInizio;
			} catch (Exception e) {
				final int riga = i;
				LOGGER.warning(() -> "Errore nella formattazione dei dati ricoveri alla riga " + riga + ": " + e.getMessage());
			}
		}
		return dati;
	}

	private Object[][] formattaDatiAgenda(List<ArrayList<String>> eventi) {
        if (eventi == null) return new Object[0][0];
		Object[][] dati = new Object[eventi.size()][2];
		for (int i = 0; i < eventi.size(); i++) {
			ArrayList<String> ev = eventi.get(i);
			dati[i][0] = ev.size() > 4 ? ev.get(4) : "N/D"; // data_ora_inizio
			String descrizione = ev.size() > 2 ? ev.get(2) : "Evento #" + (ev.size() > 0 ? ev.get(0) : ""); // titolo o ID
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
	private Object[][] preparaDatiLettiPerTabella(List<ArrayList<String>> datiLetti, String statoFilter, String repartoFilter) {
		if (datiLetti == null || datiLetti.isEmpty()) {
			return new Object[0][6]; // Colonne: Numero Letto, Stanza, Nome Paziente, Codice Fiscale, Reparto, Stato
		}

		// LA LOGICA VIENE CAMBIATA: si usa la tabella dei ricoveri come UNICA FONTE DI VERITÀ
		// per determinare se un letto è occupato, ignorando il flag "occupato" della tabella letti
		// che potrebbe essere non aggiornato.
		java.util.Map<String, String[]> ricoveriMap = new java.util.HashMap<>();
		if (ricoveroDAO != null && pazienteDAO != null) {
			// 1. Otteniamo tutti i ricoveri ancora aperti (source of truth)
			List<ArrayList<String>> ricoveriAttivi = ricoveroDAO.getAllRicoveriAttivi(); // Assumiamo che questo metodo esista

			for (ArrayList<String> ricovero : ricoveriAttivi) {
				String cfPaziente = ricovero.get(1);
				String idLetto = ricovero.get(2);
				String repartoRicovero = ricovero.get(3);

				// 2. Per ogni ricovero, troviamo i dettagli del paziente
				List<String> paziente = pazienteDAO.getPazienteByCf(cfPaziente);
				if (paziente != null && !paziente.isEmpty()) {
					String nomeCompleto = (paziente.size() > 2 ? paziente.get(2) : "") + " " + (paziente.size() > 1 ? paziente.get(1) : "");
					// Usiamo una chiave composta per distinguere i letti omonimi in reparti diversi
					ricoveriMap.put(idLetto + "_" + repartoRicovero, new String[]{nomeCompleto.trim(), cfPaziente});
				}
			}
		}

		// Usiamo una lista temporanea per i dati filtrati, poi la convertiamo in array
		java.util.List<Object[]> datiFiltrati = new java.util.ArrayList<>();

		for (List<String> letto : datiLetti) {
			
			String idLetto = letto.size() > 0 ? letto.get(0) : "-";
			String repartoLetto = letto.size() > 1 ? letto.get(1) : "-";

			// Applica i filtri
			if (repartoFilter != null && !repartoFilter.isEmpty() && !repartoLetto.equalsIgnoreCase(repartoFilter)) {
				continue; // Salta questo letto se non corrisponde al reparto
			}

			// 2. Si determina lo stato controllando la chiave composta (id_reparto)
			boolean isOccupato = ricoveriMap.containsKey(idLetto + "_" + repartoLetto);
			String statoCorrente = isOccupato ? "Occupato" : "Libero";

			if (statoFilter != null && !statoFilter.equals("Tutti") && !statoCorrente.equalsIgnoreCase(statoFilter)) {
				continue; // Salta questo letto se non corrisponde allo stato filtrato
			}

			Object[] rigaDati = new Object[6];
			// Popolamento dati fissi del letto
			rigaDati[0] = idLetto;                                    // Numero Letto (ID)
			rigaDati[1] = letto.size() > 3 ? letto.get(3) : "-";      // Stanza
			rigaDati[4] = repartoLetto;                               // Reparto

			// Popolamento dati variabili in base allo stato di occupazione
			if (isOccupato) {
				String[] infoPaziente = ricoveriMap.get(idLetto + "_" + repartoLetto); // Recupera info paziente dal ricovero
				rigaDati[2] = infoPaziente[0];                        // Nome Paziente
				rigaDati[3] = infoPaziente[1];                        // Codice Fiscale
				rigaDati[5] = "🔴 Occupato";                           // Stato con emoji
			} else {
				rigaDati[2] = "-";                                    // Nome Paziente
				rigaDati[3] = "-";                                    // Codice Fiscale
				rigaDati[5] = "🟢 Libero";                             // Stato con emoji
			}
			datiFiltrati.add(rigaDati);
		}
		return datiFiltrati.toArray(new Object[0][0]);
	}

    private void caricaDatiTurni(gui.Turni turniFrame) {
        List<ArrayList<String>> turni = new ArrayList<>();
        List<ArrayList<String>> medici = medicoDAO.getAllMedici();
        if (medici != null) {
            for (ArrayList<String> medico : medici) {
                if (medico.size() > 4) {
                    String matricola = medico.get(4);
                    List<ArrayList<String>> turniMedico = turnoDAO.getTurniByMedico(matricola);
                    if (turniMedico != null) {
                        turni.addAll(turniMedico);
                    }
                }
            }
        }
        turniFrame.aggiornaTabella(formattaDatiTurni(turni));
    }

	private void gestisciRicercaRicoveri(gui.Ricovero ricoveroFrame) {
		String nome = ricoveroFrame.getNome();
		String cf = ricoveroFrame.getCodiceFiscale();
		String id = ricoveroFrame.getIdPaziente();
		String reparto = ricoveroFrame.getRepartoSelezionato();

		ricoveroFrame.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		SwingWorker<Object[][], Void> worker = new SwingWorker<Object[][], Void>() {
			@Override
			protected Object[][] doInBackground() throws Exception {
				List<ArrayList<String>> ricoveriAttivi = ricoveroDAO.getAllRicoveriAttivi();
				List<ArrayList<String>> risultatiFiltrati = new ArrayList<>();

				for (ArrayList<String> ricovero : ricoveriAttivi) {
					String idRicovero = ricovero.get(0);
					String cfPaziente = ricovero.get(1);
					String repartoRicovero = ricovero.get(3);

					List<String> paziente = pazienteDAO.getPazienteByCf(cfPaziente);
					String nomePaziente = "";
					if (paziente != null && !paziente.isEmpty()) {
						nomePaziente = (paziente.get(1) + " " + paziente.get(2)).toLowerCase();
					}

					boolean match = true;
					if (match && !isNullOrEmpty(nome) && !nomePaziente.contains(nome.toLowerCase())) match = false;
					if (match && !isNullOrEmpty(cf) && !cfPaziente.toLowerCase().contains(cf.toLowerCase())) match = false;
					if (match && !isNullOrEmpty(id) && !idRicovero.equals(id)) match = false;
					if (match && !isNullOrEmpty(reparto) && !repartoRicovero.equalsIgnoreCase(reparto)) match = false;

					if (match) risultatiFiltrati.add(ricovero);
				}
				return formattaDatiRicoveri(risultatiFiltrati);
			}

			@Override
			protected void done() {
				try {
					Object[][] dati = get();
					ricoveroFrame.aggiornaTabella(dati);
					if (dati.length == 0) {
						JOptionPane.showMessageDialog(ricoveroFrame, "Nessun ricovero trovato con i criteri specificati.", INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (Exception e) {
					LOGGER.warning("Errore durante la ricerca dei ricoveri: " + e.getMessage());
				} finally {
					ricoveroFrame.setCursor(java.awt.Cursor.getDefaultCursor());
				}
			}
		};
		worker.execute();
	}

    private void caricaDatiRicoveriAsync(gui.Ricovero ricoveroFrame) {
		ricoveroFrame.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		SwingWorker<Object[][], Void> worker = new SwingWorker<Object[][], Void>() {
			@Override
			protected Object[][] doInBackground() throws Exception {
				return formattaDatiRicoveri(ricoveroDAO.getAllRicoveriAttivi());
			}
			@Override
			protected void done() {
				try { ricoveroFrame.aggiornaTabella(get()); } catch (Exception e) { LOGGER.warning("Errore caricamento ricoveri"); }
				finally { ricoveroFrame.setCursor(java.awt.Cursor.getDefaultCursor()); }
			}
		};
		worker.execute();
	}

    private void aggiornaAgendaGUI(JFrame frame) {
        if (utenteLoggato == null) return;
        Object[][] dati = formattaDatiAgenda(agendaDAO.getEventiByMatricola(utenteLoggato.getMatricola()));
        if (frame instanceof gui.SchermataAmministratore) ((gui.SchermataAmministratore) frame).aggiornaAgenda(dati);
        if (frame instanceof gui.SchermataMedico) ((gui.SchermataMedico) frame).aggiornaAgenda(dati);
    }

	// =========================================================
	// METODI DI AVVIO PRINCIPALE APP E AUTENTICAZIONE
	// =========================================================

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Controller app = new Controller();
			app.avvia(); // Questo farà partire la tua schermata di Login!
		});
	}

	public void avvia() {
		avviaSchermataRegistrazione();
	}

	private void avviaSchermataLogin() {
		gui.Login loginView = new gui.Login();
		JFrame frame = new JFrame("Login - Ospedale San Raffaele");
		impostaSchermata(frame, loginView.mainPanel, "Login - Ospedale San Raffaele", WindowConstants.EXIT_ON_CLOSE);

		// Listener delegato dal bottone Accedi nella GUI
		loginView.addLoginListener(e -> {
			String username = loginView.getUsername();
			String password = loginView.getPassword();
			// Legge il PIN dalla GUI. Se la GUI Login non lo ha, aggiungi il campo e il metodo getPin()!
			String pin = loginView.getPin(); 

			if (username.isEmpty() || password.isEmpty()) {
				loginView.showMessage("Campi vuoti", "Inserisci Username e Password per accedere.", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (whoIsAsking(username, password, pin)) {
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
		impostaSchermata(frame, regView.mainPanel, "Registrazione - Ospedale San Raffaele", WindowConstants.EXIT_ON_CLOSE);

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
				regView.showMessage("Errore PIN", "Scegli un PIN personale per registrarti come Amministratore.", JOptionPane.WARNING_MESSAGE);
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