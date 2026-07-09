package model;

/**
 * Rappresenta una prestazione medica eseguita da un medico.
 */
public class Prestazione {
    public String tipologia_prestazione;
    public String descrizione_prestazione;
    public String esito_prestazione;


    /**
     * Costruisce un nuovo oggetto Prestazione.
     * @param tipologia_prestazione La tipologia della prestazione.
     * @param descrizione_prestazione La descrizione della prestazione.
     * @param esito_prestazione L'esito della prestazione.
     * @param medico Il medico che ha eseguito la prestazione.
     */
    public Prestazione(String tipologia_prestazione, String descrizione_prestazione, String esito_prestazione, Medico medico) {
        this.descrizione_prestazione = descrizione_prestazione;
        this.esito_prestazione = esito_prestazione;
        this.tipologia_prestazione = tipologia_prestazione;
    }
}
