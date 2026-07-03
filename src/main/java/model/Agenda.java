package model;

import java.sql.Timestamp;

public class Agenda {
    private int idEvento;
    private String matricolaMedico;
    private String titolo;
    private String descrizione;
    private Timestamp dataOraInizio;
    private Timestamp dataOraFine;

    public Agenda(int idEvento, String matricolaMedico, String titolo, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine) {
        this.idEvento = idEvento;
        this.matricolaMedico = matricolaMedico;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.dataOraInizio = dataOraInizio;
        this.dataOraFine = dataOraFine;
    }

    // Getters
    public int getIdEvento() {
        return idEvento;
    }

    public String getMatricolaMedico() {
        return matricolaMedico;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Timestamp getDataOraInizio() {
        return dataOraInizio;
    }

    public Timestamp getDataOraFine() {
        return dataOraFine;
    }

    // Setters
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setDataOraInizio(Timestamp dataOraInizio) {
        this.dataOraInizio = dataOraInizio;
    }

    public void setDataOraFine(Timestamp dataOraFine) {
        this.dataOraFine = dataOraFine;
    }
}