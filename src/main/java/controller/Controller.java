package controller;

import dao.*;
import implementazioneDao.*;
import model.*;

import java.time.Clock;
import java.time.ZoneId;
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
		try (java.sql.Connection conn = database_connection.ConnessioneDatabase.getConnection()) {
			if (conn != null && !conn.isClosed()) {
				LOGGER.info("CONNESSIONE AL DB RIUSCITA!");
			}
		} catch (java.sql.SQLException e) {
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
			return amministratoreDAO.aggiungiAmministratore(matricola, login, password, nome, cognome, pin);
		} else {
			if (medicoDAO.checkLoginEsistente(login)) {
				LOGGER.warning("Tentativo di registrazione con login già esistente: " + login);
				return false;
			}
			LOGGER.info("Tentativo di registrazione nuovo medico con matricola: " + matricola);
			boolean successo = medicoDAO.aggiungiMedico(nome, cognome, matricola, login, password, null, null, null);
			if (successo) {
				agendaDAO.creaAgendaPerMedico(matricola);
			}
			return successo;
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
		if (successo) {
			agendaDAO.creaAgendaPerMedico(matricola);
		}
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
		java.util.Map<String, List<String>> lettiDisponibiliPerReparto = new java.util.HashMap<>();
		java.util.Set<String> repartiDisponibili = new java.util.TreeSet<>();

		if (tuttiLetti != null) {
			for (List<String> letto : tuttiLetti) {
				String idLetto = letto.get(0);
				String reparto = letto.get(1);
				if (checkDisponibilitaLetto(idLetto, reparto)) {
					repartiDisponibili.add(reparto);
					lettiDisponibiliPerReparto.computeIfAbsent(reparto, k -> new ArrayList<>()).add(idLetto);
				}
			}
		}

		if (repartiDisponibili.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Non ci sono letti disponibili in nessun reparto.", "Nessun Letto Disponibile", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		JComboBox<String> repartiComboBox = new JComboBox<>(repartiDisponibili.toArray(new String[0]));
		JComboBox<String> lettiComboBox = new JComboBox<>();
		JTextField motivoInput = new JTextField();

		repartiComboBox.addActionListener(e -> {
			String repartoSelezionato = (String) repartiComboBox.getSelectedItem();
			lettiComboBox.removeAllItems();
			if (repartoSelezionato != null) {
				List<String> letti = lettiDisponibiliPerReparto.get(repartoSelezionato);
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

		JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
		panel.add(new JLabel("Paziente:")); panel.add(new JLabel(nomePaziente + " (" + cfPaziente + ")"));
		panel.add(new JLabel("Reparto:")); panel.add(repartiComboBox);
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
		List<ArrayList<String>> ricoveriAttivi = ricoveroDAO.getAllRicoveriAttivi();
		if (ricoveriAttivi != null) {
			for (List<String> ricovero : ricoveriAttivi) {
				if (ricovero.size() > 1) pazientiRicoverati.add(ricovero.get(1));
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
		java.util.Map<String, List<String>> lettiDisponibiliPerReparto = new java.util.HashMap<>();
		java.util.Set<String> repartiDisponibili = new java.util.TreeSet<>(); // TreeSet per ordine alfabetico

		if (tuttiLetti != null) {
			for (List<String> letto : tuttiLetti) {
				String idLetto = letto.get(0);
				String reparto = letto.get(1);
				if (checkDisponibilitaLetto(idLetto, reparto)) {
					repartiDisponibili.add(reparto);
					lettiDisponibiliPerReparto.computeIfAbsent(reparto, k -> new ArrayList<>()).add(idLetto);
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
		JComboBox<String> lettiComboBox = new JComboBox<>();
		JTextField motivoInput = new JTextField();

		// Logica per aggiornare i letti quando cambia il reparto
		repartiComboBox.addActionListener(e -> {
			String repartoSelezionato = (String) repartiComboBox.getSelectedItem();
			lettiComboBox.removeAllItems();
			if (repartoSelezionato != null) {
				List<String> letti = lettiDisponibiliPerReparto.get(repartoSelezionato);
				if (letti != null) {
					for (String letto : letti) {
						lettiComboBox.addItem(letto);
					}
				}
			}
		});

		// Popola i letti per il primo reparto selezionato di default
		if (repartiComboBox.getItemCount() > 0) {
			repartiComboBox.setSelectedIndex(0);
		}

		// 4. Mostra il dialogo
		JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
		panel.add(new JLabel("Paziente:")); panel.add(pazientiComboBox);
		panel.add(new JLabel("Reparto:")); panel.add(repartiComboBox);
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
		
		adminFrame.addRicercaAgendaListener(e -> aggiornaAgendaGUI(adminFrame));
		adminFrame.addNewEventListener(e -> {
            if (gestisciNuovoEvento()) aggiornaAgendaGUI(adminFrame);
        });

        aggiornaAgendaGUI(adminFrame);
		
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

        pazientiFrame.aggiornaTabella(formattaDatiPazienti(getAllPazienti()));
		mostraFinestraSecondaria(pazientiFrame, frameDaChiudere);
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

	public void apriSchermataLetti(JFrame frameDaChiudere) {
		// 1. Crea l'istanza della schermata
		gui.Letti lettiFrame = new gui.Letti();

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
				ricaricaEAggiornaTabellaLetti(lettiFrame); // Aggiorna la vista con lo stato reale
				return;
			}

			boolean successo = gestisciAssegnazionePazienteLetto(idLettoSelezionato, repartoLettoSelezionato);

			if (successo) {
				JOptionPane.showMessageDialog(lettiFrame, "Paziente assegnato con successo!", "Operazione Riuscita", JOptionPane.INFORMATION_MESSAGE);
				ricaricaEAggiornaTabellaLetti(lettiFrame); // Ricarica per mostrare il letto come "Occupato"
			}
		});

		// 3. Carica i dati iniziali nella tabella quando la schermata si apre
		ricaricaEAggiornaTabellaLetti(lettiFrame);

		// 4. Mostra la finestra
		mostraFinestraSecondaria(lettiFrame, frameDaChiudere);
	}

	public boolean gestisciCreazioneNuovaPrestazione() {
		JTextField idPrestazioneInput = new JTextField();
		JComboBox<String> tipologiaInput = new JComboBox<>(new String[]{
				"Chirurgia Generale", "Radiologia Interventistica", "Diagnostica Avanzata",
				"Chirurgia Robotica", "Procedure Endoscopiche", "Radioterapia", "Cardiologia", "Oncologia"
		});
		JTextField esitoInput = new JTextField();
		JTextField idTurnoInput = new JTextField();
		JTextField cfPazienteInput = new JTextField();
		JTextField matricolaInput = new JTextField();
		JTextField idAgendaInput = new JTextField();

		JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
		panel.add(new JLabel("ID Prestazione:")); panel.add(idPrestazioneInput);
		panel.add(new JLabel("Tipologia:")); panel.add(tipologiaInput);
		panel.add(new JLabel("Esito Prestazione:")); panel.add(esitoInput);
		panel.add(new JLabel("ID Turno:")); panel.add(idTurnoInput);
		panel.add(new JLabel("CF Paziente:")); panel.add(cfPazienteInput);
		panel.add(new JLabel("Matricola Medico:")); panel.add(matricolaInput);
		panel.add(new JLabel("ID Agenda:")); panel.add(idAgendaInput);

		int result = JOptionPane.showConfirmDialog(null, panel, "Nuova Prestazione", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			try {
				int idPrestazione = Integer.parseInt(idPrestazioneInput.getText().trim());
				String tipologia = (String) tipologiaInput.getSelectedItem();
				String esito = esitoInput.getText().trim();
				String idTurno = idTurnoInput.getText().trim();
				String cfPaziente = cfPazienteInput.getText().trim();
				String matricola = matricolaInput.getText().trim();
				String idAgenda = idAgendaInput.getText().trim();

				boolean successo = prestazioneDAO.aggiungiPrestazione(idPrestazione, tipologia, esito, idTurno, cfPaziente, matricola, idAgenda);
				if (successo) {
					JOptionPane.showMessageDialog(null, "Prestazione aggiunta con successo!", SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
					return true;
				} else {
					JOptionPane.showMessageDialog(null, ERRORE_AGGIUNTA_DATI, ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "ID Prestazione, ID Turno e ID Agenda devono essere numeri validi.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

	public void apriSchermataPrestazioni(JFrame frameDaChiudere) {
		gui.Prestazioni prestazioniFrame = new gui.Prestazioni();
		impostaSchermata(prestazioniFrame, prestazioniFrame.mainPanel, "Ricerca Prestazioni Mediche", WindowConstants.DISPOSE_ON_CLOSE);
		
		prestazioniFrame.addNuovaPrestazioneListener(e -> {
			if (gestisciCreazioneNuovaPrestazione()) {
				prestazioniFrame.aggiornaTabella(formattaDatiPrestazioni(prestazioneDAO.getAllPrestazioni()));
			}
		});

		prestazioniFrame.aggiornaTabella(formattaDatiPrestazioni(prestazioneDAO.getAllPrestazioni()));
		mostraFinestraSecondaria(prestazioniFrame, frameDaChiudere);
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
		
		medicoHome.addRicercaAgendaListener(e -> aggiornaAgendaGUI(medicoHome));
		medicoHome.addNewEventListener(e -> {
            if (gestisciNuovoEvento()) aggiornaAgendaGUI(medicoHome);
        });

        aggiornaAgendaGUI(medicoHome);
		
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

		mediciFrame.aggiornaTabella(formattaDatiMedici(getAllMedici()));
		mostraFinestraSecondaria(mediciFrame, frameDaChiudere);
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

		dimissioniFrame.aggiornaTabella(formattaDatiDimissioni(ricercaDimissioni()));
		mostraFinestraSecondaria(dimissioniFrame, frameDaChiudere);
	}

	public void apriSchermataRicoveri(JFrame frameDaChiudere) {
		gui.Ricovero ricoveroFrame = new gui.Ricovero();
		impostaSchermata(ricoveroFrame, ricoveroFrame.mainPanel, "Ricerca Ricovero", WindowConstants.DISPOSE_ON_CLOSE);

        ricoveroFrame.addNuovoRicoveroListener(e -> {
            if (gestisciCreazioneNuovoRicovero()) {
                caricaDatiRicoveri(ricoveroFrame);
            }
        });

		ricoveroFrame.addGestisciRicoveroListener(e -> {
			String[] selezionato = ricoveroFrame.getRicoveroSelezionato();
			if (selezionato != null) {
				if (gestisciDimissioneDaRicovero(selezionato[1])) {
					caricaDatiRicoveri(ricoveroFrame); // Ricarica la tabella dopo la dimissione
				}
			} else {
				JOptionPane.showMessageDialog(ricoveroFrame, "Per favore, seleziona un ricovero dalla tabella prima di cliccare su Gestisci Ricovero.", "Nessun Ricovero Selezionato", JOptionPane.WARNING_MESSAGE);
			}
		});

		caricaDatiRicoveri(ricoveroFrame);
		mostraFinestraSecondaria(ricoveroFrame, frameDaChiudere);
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
				// datiSelezionati[0] = data, [1] = matricola, [2] = orario
				if (gestisciModificaTurno(datiSelezionati[1], datiSelezionati[0], datiSelezionati[2])) {
					caricaDatiTurni(turniFrame); // Ricarica i dati se la modifica ha successo
				}
			} else {
				JOptionPane.showMessageDialog(turniFrame, "Per favore, seleziona un turno dalla tabella prima di cliccare Modifica.", "Nessun Turno Selezionato", JOptionPane.WARNING_MESSAGE);
			}
		});

		caricaDatiTurni(turniFrame);
		mostraFinestraSecondaria(turniFrame, frameDaChiudere);
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
		JTextField repartoInput = new JTextField(datiMedico.get(7));

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
			String reparto = repartoInput.getText().trim();

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
		java.util.Map<String, String> pazientiRicoverati = new java.util.HashMap<>();
		List<ArrayList<String>> ricoveriAttivi = ricoveroDAO.getAllRicoveriAttivi();
		if (ricoveriAttivi != null) {
			for (List<String> ricovero : ricoveriAttivi) {
				if (ricovero.size() > 2) {
					pazientiRicoverati.put(ricovero.get(1), ricovero.get(2)); // Mappa CF -> ID Letto
				}
			}
		}

		for (ArrayList<String> p : pazientiDb) {
			String cf = p.get(0);
			String idLetto = pazientiRicoverati.getOrDefault(cf, "");
			// Il DAO ci fornisce 7 attributi grezzi. Aggiungiamo l'id_letto come 8° elemento (indice 7) per farlo leggere alla GUI.
			if (p.size() == 7) p.add(idLetto);
			else if (p.size() > 7) p.set(7, idLetto);
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
		Object[][] dati = new Object[prestazioniDb.size()][7];
		for (int i = 0; i < prestazioniDb.size(); i++) {
			List<String> p = prestazioniDb.get(i);
			try {
				dati[i][0] = p.size() > 0 ? p.get(0) : "-"; // ID Prestaz.
				dati[i][1] = p.size() > 1 ? p.get(1) : "-"; // Tipologia
				dati[i][2] = p.size() > 2 ? p.get(2) : "-"; // Esito
				dati[i][3] = p.size() > 3 ? p.get(3) : "-"; // ID Turno
				dati[i][4] = p.size() > 4 ? p.get(4) : "-"; // CF Paziente
				dati[i][5] = p.size() > 5 ? p.get(5) : "-"; // Matricola Medico
				dati[i][6] = p.size() > 6 ? p.get(6) : "-"; // ID Agenda
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

		Object[][] dati = new Object[datiLetti.size()][6];
		for (int i = 0; i < datiLetti.size(); i++) {
			List<String> letto = datiLetti.get(i);
			
			String idLetto = letto.size() > 0 ? letto.get(0) : "-";
			String repartoLetto = letto.size() > 1 ? letto.get(1) : "-";

			// 2. Si determina lo stato controllando la chiave composta (id_reparto)
			boolean isOccupato = ricoveriMap.containsKey(idLetto + "_" + repartoLetto);

			// Popolamento dati fissi del letto
			dati[i][0] = idLetto;                                    // Numero Letto (ID)
			dati[i][1] = letto.size() > 3 ? letto.get(3) : "-";      // Stanza
			dati[i][4] = repartoLetto;                               // Reparto

			// Popolamento dati variabili in base allo stato di occupazione
			if (isOccupato) {
				String[] infoPaziente = ricoveriMap.get(idLetto + "_" + repartoLetto);
				dati[i][2] = infoPaziente[0];                        // Nome Paziente
				dati[i][3] = infoPaziente[1];                        // Codice Fiscale
				dati[i][5] = "🔴 Occupato";                           // Stato con emoji
			} else {
				dati[i][2] = "-";                                    // Nome Paziente
				dati[i][3] = "-";                                    // Codice Fiscale
				dati[i][5] = "🟢 Libero";                             // Stato con emoji
			}
		}
		return dati;
	}

    private void caricaDatiTurni(gui.Turni turniFrame) {
        List<ArrayList<String>> turni = new ArrayList<>();
        if (utenteLoggato instanceof Amministratore) {
            List<ArrayList<String>> medici = medicoDAO.getAllMedici();
            for (ArrayList<String> medico : medici) {
                String matricola = medico.get(4);
                turni.addAll(turnoDAO.getTurniByMedico(matricola));
            }
        } else if (utenteLoggato instanceof Medico) {
            turni = turnoDAO.getTurniByMedico(utenteLoggato.getMatricola());
        }
        turniFrame.aggiornaTabella(formattaDatiTurni(turni));
    }

    private void caricaDatiRicoveri(gui.Ricovero ricoveroFrame) {
        // La logica precedente era inefficiente e nascondeva il problema di ricoveri multipli per un singolo paziente.
        // Questa nuova logica è più corretta ed efficiente: chiede al DB tutti i ricoveri attivi in una sola volta.
        List<ArrayList<String>> ricoveriAttivi = ricoveroDAO.getAllRicoveriAttivi();
        ricoveroFrame.aggiornaTabella(formattaDatiRicoveri(ricoveriAttivi));
    }

    private void aggiornaAgendaGUI(JFrame frame) {
        if (utenteLoggato == null) return;
        Object[][] dati = formattaDatiAgenda(getEventiPerMedico(utenteLoggato.getMatricola()));
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