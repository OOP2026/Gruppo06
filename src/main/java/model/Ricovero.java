package model;

import java.time.LocalDateTime;

/**
 * Rappresenta il ricovero di un paziente.
 * Contiene informazioni sulle date, la prognosi e il motivo della dimissione.
 */
public class Ricovero {

    /** La data e l'ora in cui ha avuto inizio il ricovero. */
    private LocalDateTime dataOraInizio;
    /** La data e l'ora previste per la fine del ricovero. */
    private LocalDateTime dataOraFinePrevista;
    /** La data e l'ora effettive in cui il ricovero si è concluso. */
    private LocalDateTime dataOraFineEffettiva;
    /** La motivazione o l'esito della dimissione del paziente. */
    private String motivoDimissione;
    /** Il numero di giorni di prognosi stimati al momento della dimissione. */
    private int giorniPrognosi;
    /** Il paziente associato a questo ricovero. */
    private Paziente paziente;
    /** Il letto attualmente assegnato al paziente per questo ricovero. */
    private Letto lettoAssegnato;

    /**
     * Costruisce un nuovo oggetto Ricovero.
     *
     * @param dataOraInizio         La data di inizio del ricovero.
     * @param dataOraFinePrevista   La data di fine prevista.
     * @param dataOraFineEffettiva  La data di fine effettiva.
     * @param motivoDimissione      Il motivo della dimissione.
     * @param giorniPrognosi        I giorni di prognosi.
     * @param paziente              Il paziente ricoverato.
     */
    public Ricovero(LocalDateTime dataOraInizio, LocalDateTime dataOraFinePrevista, LocalDateTime dataOraFineEffettiva, String motivoDimissione, int giorniPrognosi, Paziente paziente){
        this.dataOraInizio = dataOraInizio;
        this.dataOraFinePrevista = dataOraFinePrevista;
        this.dataOraFineEffettiva = dataOraFineEffettiva;
        this.motivoDimissione = motivoDimissione;
        this.giorniPrognosi = giorniPrognosi;
        this.paziente = paziente;
    }

    /**
     * Restituisce la data e l'ora di inizio del ricovero.
     * @return la data e l'ora di inizio.
     */
    public LocalDateTime getDataOraInizio() {
        return dataOraInizio;
    }

    /**
     * Imposta la data e l'ora di inizio del ricovero.
     * @param dataOraInizio la nuova data e ora di inizio.
     */
    public void setDataOraInizio(LocalDateTime dataOraInizio) {
        this.dataOraInizio = dataOraInizio;
    }

    /**
     * Restituisce la data e l'ora di fine previste per il ricovero.
     * @return la data e l'ora di fine previste.
     */
    public LocalDateTime getDataOraFinePrevista() {
        return dataOraFinePrevista;
    }

    /**
     * Imposta la data e l'ora di fine previste per il ricovero.
     * @param dataOraFinePrevista la nuova data e ora di fine previste.
     */
    public void setDataOraFinePrevista(LocalDateTime dataOraFinePrevista) {
        this.dataOraFinePrevista = dataOraFinePrevista;
    }

    /**
     * Restituisce la data e l'ora di fine effettive del ricovero.
     * @return la data e l'ora di fine effettive.
     */
    public LocalDateTime getDataOraFineEffettiva() {
        return dataOraFineEffettiva;
    }

    /**
     * Imposta la data e l'ora di fine effettive del ricovero.
     * @param dataOraFineEffettiva la nuova data e ora di fine effettive.
     */
    public void setDataOraFineEffettiva(LocalDateTime dataOraFineEffettiva) {
        this.dataOraFineEffettiva = dataOraFineEffettiva;
    }

    /**
     * Restituisce il motivo della dimissione.
     * @return il motivo della dimissione.
     */
    public String getMotivoDimissione() {
        return motivoDimissione;
    }

    /**
     * Imposta il motivo della dimissione.
     * @param motivoDimissione il nuovo motivo della dimissione.
     */
    public void setMotivoDimissione(String motivoDimissione) {
        this.motivoDimissione = motivoDimissione;
    }

    /**
     * Restituisce i giorni di prognosi assegnati.
     * @return i giorni di prognosi.
     */
    public int getGiorniPrognosi() {
        return giorniPrognosi;
    }

    /**
     * Imposta i giorni di prognosi.
     * @param giorniPrognosi il nuovo numero di giorni di prognosi.
     */
    public void setGiorniPrognosi(int giorniPrognosi) {
        this.giorniPrognosi = giorniPrognosi;
    }

    /**
     * Restituisce il paziente associato a questo ricovero.
     * @return l'oggetto Paziente.
     */
    public Paziente getPaziente() {
        return paziente;
    }

    /**
     * Imposta il paziente per questo ricovero.
     * @param paziente il nuovo paziente da associare.
     */
    public void setPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

    /**
     * Restituisce il letto assegnato al paziente durante il ricovero.
     * @return l'oggetto {@link Letto} occupato dal paziente.
     */
    public Letto getLettoAssegnato() {
        return lettoAssegnato;
    }

    /**
     * Imposta o modifica il letto assegnato al paziente.
     * @param lettoAssegnato il nuovo posto letto da associare al ricovero, oppure null per azzerarlo.
     */
    public void setLettoAssegnato(Letto lettoAssegnato) {
        this.lettoAssegnato = lettoAssegnato;
    }

    /**
     * Restituisce una rappresentazione testuale dell'oggetto Ricovero.
     * @return una stringa contenente i dettagli del ricovero.
     */
    @Override
    public String toString() {
        return "Ricovero{" +
                "dataOraInizio=" + dataOraInizio +
                ", dataOraFinePrevista=" + dataOraFinePrevista +
                ", dataOraFineEffettiva=" + dataOraFineEffettiva +
                ", motivoDimissione='" + motivoDimissione + '\'' +
                ", giorniPrognosi=" + giorniPrognosi +
                ", paziente=" + (paziente != null ? paziente.getCf() : "null") +
                ", lettoAssegnato=" + (lettoAssegnato != null ? lettoAssegnato.getReparto() : "nessuno") +
                '}';
    }

}
