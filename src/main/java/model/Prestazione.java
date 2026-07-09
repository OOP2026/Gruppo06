package model;

/**
 * Rappresenta una prestazione medica eseguita da un medico.
 */
public class Prestazione {
    private String tipologiaPrestazione;
    private String descrizionePrestazione;
    private String esitoPrestazione;
    private Medico medico;

    /**
     * Costruisce un nuovo oggetto Prestazione.
     * @param tipologiaPrestazione La tipologia della prestazione.
     * @param descrizionePrestazione La descrizione della prestazione.
     * @param esitoPrestazione L'esito della prestazione.
     * @param medico Il medico che ha eseguito la prestazione.
     */
    public Prestazione(String tipologiaPrestazione, String descrizionePrestazione, String esitoPrestazione, Medico medico) {
        this.tipologiaPrestazione = tipologiaPrestazione;
        this.descrizionePrestazione = descrizionePrestazione;
        this.esitoPrestazione = esitoPrestazione;
        this.medico = medico;
    }

    /**
     * Restituisce la tipologia della prestazione.
     * @return la tipologia della prestazione.
     */
    public String getTipologiaPrestazione() {
        return tipologiaPrestazione;
    }

    /**
     * Imposta la tipologia della prestazione.
     * @param tipologiaPrestazione la nuova tipologia della prestazione.
     */
    public void setTipologiaPrestazione(String tipologiaPrestazione) {
        this.tipologiaPrestazione = tipologiaPrestazione;
    }

    /**
     * Restituisce la descrizione della prestazione.
     * @return la descrizione della prestazione.
     */
    public String getDescrizionePrestazione() {
        return descrizionePrestazione;
    }

    /**
     * Imposta la descrizione della prestazione.
     * @param descrizionePrestazione la nuova descrizione della prestazione.
     */
    public void setDescrizionePrestazione(String descrizionePrestazione) {
        this.descrizionePrestazione = descrizionePrestazione;
    }

    /**
     * Restituisce l'esito della prestazione.
     * @return l'esito della prestazione.
     */
    public String getEsitoPrestazione() {
        return esitoPrestazione;
    }

    /**
     * Imposta l'esito della prestazione.
     * @param esitoPrestazione il nuovo esito della prestazione.
     */
    public void setEsitoPrestazione(String esitoPrestazione) {
        this.esitoPrestazione = esitoPrestazione;
    }

    /**
     * Restituisce il medico che ha eseguito la prestazione.
     * @return l'oggetto Medico associato.
     */
    public Medico getMedico() {
        return medico;
    }

    /**
     * Imposta il medico che ha eseguito la prestazione.
     * @param medico il nuovo medico da associare.
     */
    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    /**
     * Restituisce una rappresentazione testuale dell'oggetto Prestazione.
     * @return una stringa contenente i dettagli della prestazione.
     */
    @Override
    public String toString() {
        return "Prestazione{" +
                "tipologiaPrestazione='" + tipologiaPrestazione + '\'' +
                ", descrizionePrestazione='" + descrizionePrestazione + '\'' +
                ", esitoPrestazione='" + esitoPrestazione + '\'' +
                ", medico=" + (medico != null ? medico.getMatricola() : "null") +
                '}';
    }
}
