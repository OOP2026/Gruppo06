package model;

import java.util.Date;

public class Ricovero {
    public Date dataOraInizio;
    public Date DataOraFinePrevista;
    public Date DataOraFineEffettiva;
    public String motivoDimissione;
    public int giorniPrognosi;

    //Richiamo classi
    public Paziente paziente;

    public Ricovero(Date dataOraInizio, Date DataOraFinePrevista, Date DataOraFineEffettiva, String motivoDimissione, int giorniPrognosi, Paziente paziente){
        this.dataOraInizio = dataOraInizio;
        this.DataOraFinePrevista = DataOraFinePrevista;
        this.DataOraFineEffettiva = DataOraFineEffettiva;
        this.motivoDimissione = motivoDimissione;
        this.giorniPrognosi = giorniPrognosi;
        this.paziente = paziente;
    }

    public void checkRicovero(){
        //System.out.println("Controllo ricovero...");
    }
}
