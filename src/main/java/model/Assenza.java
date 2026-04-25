package model;

import java.util.Date;

public class Assenza {
    public Date data_inizio_assenza;
    public Date data_fine_assenza;
    public String motivo_assenza;
    public boolean approvazione;
    public Medico medico;
    public Turno_Lavoro turno_lavoro;


    public Assenza(Date data_inizio_assenza, Date data_fine_assenza, String motivo_assenza, boolean approvazione, Medico medico, Turno_Lavoro turno_lavoro){
        this.data_inizio_assenza = data_inizio_assenza;
        this.data_fine_assenza = data_fine_assenza;
        this.motivo_assenza = motivo_assenza;
        this.approvazione = approvazione;
        this.medico = medico;
        this.turno_lavoro = turno_lavoro;
    }
}
