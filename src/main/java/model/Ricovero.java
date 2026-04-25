package model;

import java.util.Date;

public class Ricovero {
    public Date dataOraInizio;
    public Date DataOraFinePrevista;
    public Date DataOraFineEffettiva;
    public Paziente paziente;

    public Ricovero(Date dataOraInizio, Date DataOraFinePrevista, Date getDataOraFineEffettiva, Paziente paziente){
        this.dataOraInizio = dataOraInizio;
        this.DataOraFinePrevista = DataOraFinePrevista;
        this.DataOraFineEffettiva = DataOraFineEffettiva;
        this.paziente = paziente;
    }

    public void checkRicovero(){}
        //System.out.println("Controllo ricovero...");
    }
