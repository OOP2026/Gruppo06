package controller;
import dao.UtenteDAO;
import implementazioneDao.UtentePostgresDao;
import model.*;


/**
 * The type Controller.
 */
public class Controller {
	private UtenteDAO utenteDAO;
	private Utente utenteLoggato;

	/**
	 * Instantiates a new Controller.
	 */
	public Controller() { //Blocco Costruttore

		//inizializzazione DAO per Postgre
		utenteDAO = new UtentePostgresDao();
	}

	/**
	 * Registrazione boolean.
	 *
	 * @param login    the login
	 * @param password the password
	 * @return the boolean
	 */
	public boolean registrazione(String login, String password, String nome, String cognome, String pin, boolean isAdmin, String matricola) {
		if (utenteDAO.checkLoginEsistente(login)) {
			return false;
		}

		if (isAdmin) {
			if (pin.equals("1234")) {
				Amministratore nuovoAdmin = new Amministratore(login, password, matricola, nome,cognome, pin);
				return utenteDAO.aggiungiUtente(nuovoAdmin, pin);
			} else {
				return false;
			}
		} else {
			Medico nuovoMedico = new Medico(nome, cognome, login, password, matricola);
			return utenteDAO.aggiungiUtente(nuovoMedico, pin);
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

		Utente utenteCorrente = utenteDAO.getUtenteByLoginAndPassword(login, password);
		if (utenteCorrente != null) {
			this.utenteLoggato = utenteCorrente;

				if (utenteCorrente instanceof Amministratore) {
					System.out.println("Accesso Admin confermato.");
					return true;
				} else if (utenteCorrente instanceof Medico) {
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
}