package model;

import java.time.LocalDate;

public class Assenza {
    private LocalDate dataInizioAssenza;
    private LocalDate dataFineAssenza;
    private String motivoAssenza;
    private boolean approvazione;

    //Richiamo classi
    private Medico medico;
    private Turno_Lavoro turnoLavoro;


    public Assenza(LocalDate dataInizioAssenza, LocalDate dataFineAssenza, String motivoAssenza, boolean approvazione, Medico medico, Turno_Lavoro turnoLavoro){
        this.dataInizioAssenza = dataInizioAssenza;
        this.dataFineAssenza = dataFineAssenza;
        this.motivoAssenza = motivoAssenza;
        this.approvazione = approvazione;
        this.medico = medico;
        this.turnoLavoro = turnoLavoro;
    }

    public LocalDate getDataInizioAssenza() {
        return dataInizioAssenza;
    }

    public void setDataInizioAssenza(LocalDate dataInizioAssenza) {
        this.dataInizioAssenza = dataInizioAssenza;
    }

    public LocalDate getDataFineAssenza() {
        return dataFineAssenza;
    }

    public void setDataFineAssenza(LocalDate dataFineAssenza) {
        this.dataFineAssenza = dataFineAssenza;
    }

    public String getMotivoAssenza() {
        return motivoAssenza;
    }

    public void setMotivoAssenza(String motivoAssenza) {
        this.motivoAssenza = motivoAssenza;
    }

    public boolean isApprovazione() {
        return approvazione;
    }

    public void setApprovazione(boolean approvazione) {
        this.approvazione = approvazione;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Turno_Lavoro getTurnoLavoro() {
        return turnoLavoro;
    }

    public void setTurnoLavoro(Turno_Lavoro turnoLavoro) {
        this.turnoLavoro = turnoLavoro;
    }
}
