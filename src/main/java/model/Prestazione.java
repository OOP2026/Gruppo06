package model;

public class Prestazione {
    public String tipologia_prestazione;
    public String descrizione_prestazione;
    public String esito_prestazione;
    public Medico medico;


    public Prestazione(String tipologia_prestazione, String descrizione_prestazione, String esito_prestazione, Medico medico) {
        this.descrizione_prestazione = descrizione_prestazione;
        this.esito_prestazione = esito_prestazione;
        this.medico = medico;
        this.tipologia_prestazione = tipologia_prestazione;
    }
}
