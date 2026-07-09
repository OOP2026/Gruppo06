package model;

import java.util.Date;

/**
 * Rappresenta il ricovero di un paziente.
 * Contiene informazioni sulle date, la prognosi e il motivo della dimissione.
 */
public class Ricovero {
    public Date dataOraInizio;
    public Date DataOraFinePrevista;
    public Date DataOraFineEffettiva;
    public String motivoDimissione;
    public int giorniPrognosi;

    /**
     * Costruisce un nuovo oggetto Ricovero.
     *
     * @param dataOraInizio         La data di inizio del ricovero.
     * @param DataOraFinePrevista   La data di fine prevista.
     * @param DataOraFineEffettiva  La data di fine effettiva.
     * @param motivoDimissione      Il motivo della dimissione.
     * @param giorniPrognosi        I giorni di prognosi.
     * @param paziente              Il paziente ricoverato.
     */
    public Ricovero(Date dataOraInizio, Date DataOraFinePrevista, Date DataOraFineEffettiva, String motivoDimissione, int giorniPrognosi, Paziente paziente){
        this.dataOraInizio = dataOraInizio;
        this.DataOraFinePrevista = DataOraFinePrevista;
        this.DataOraFineEffettiva = DataOraFineEffettiva;
        this.motivoDimissione = motivoDimissione;
        this.giorniPrognosi = giorniPrognosi;
    }

}
