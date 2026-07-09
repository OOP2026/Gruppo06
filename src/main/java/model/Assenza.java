package model;

import java.time.LocalDate;

/**
 * Rappresenta il periodo di assenza di un medico.
 * Contiene le date di inizio e fine, la motivazione e lo stato di approvazione.
 */
public class Assenza {
    private LocalDate dataInizioAssenza;
    private LocalDate dataFineAssenza;
    private String motivoAssenza;
    private boolean approvazione;

    //Richiamo classi
    private Medico medico;
    private Turno_Lavoro turnoLavoro;

    /**
     * Costruisce un nuovo oggetto Assenza.
     *
     * @param dataInizioAssenza La data di inizio del periodo di assenza.
     * @param dataFineAssenza   La data di fine del periodo di assenza.
     * @param motivoAssenza     La motivazione dell'assenza.
     * @param approvazione      Lo stato di approvazione dell'assenza.
     * @param medico            Il medico a cui si riferisce l'assenza.
     * @param turnoLavoro       Il turno di lavoro impattato dall'assenza.
     */
    public Assenza(LocalDate dataInizioAssenza, LocalDate dataFineAssenza, String motivoAssenza, boolean approvazione, Medico medico, Turno_Lavoro turnoLavoro){
        this.dataInizioAssenza = dataInizioAssenza;
        this.dataFineAssenza = dataFineAssenza;
        this.motivoAssenza = motivoAssenza;
        this.approvazione = approvazione;
        this.medico = medico;
        this.turnoLavoro = turnoLavoro;
    }

    /**
     * Restituisce la data di inizio dell'assenza.
     * @return La data di inizio.
     */
    public LocalDate getDataInizioAssenza() {
        return dataInizioAssenza;
    }

    /**
     * Imposta la data di inizio dell'assenza.
     * @param dataInizioAssenza La nuova data di inizio.
     */
    public void setDataInizioAssenza(LocalDate dataInizioAssenza) {
        this.dataInizioAssenza = dataInizioAssenza;
    }

    /**
     * Restituisce la data di fine dell'assenza.
     * @return La data di fine.
     */
    public LocalDate getDataFineAssenza() {
        return dataFineAssenza;
    }

    /**
     * Imposta la data di fine dell'assenza.
     * @param dataFineAssenza La nuova data di fine.
     */
    public void setDataFineAssenza(LocalDate dataFineAssenza) {
        this.dataFineAssenza = dataFineAssenza;
    }

    /**
     * Restituisce la motivazione dell'assenza.
     * @return La motivazione.
     */
    public String getMotivoAssenza() {
        return motivoAssenza;
    }

    /**
     * Imposta la motivazione dell'assenza.
     * @param motivoAssenza La nuova motivazione.
     */
    public void setMotivoAssenza(String motivoAssenza) {
        this.motivoAssenza = motivoAssenza;
    }

    /**
     * Verifica se l'assenza è stata approvata.
     * @return {@code true} se approvata, altrimenti {@code false}.
     */
    public boolean isApprovazione() {
        return approvazione;
    }

    /**
     * Imposta lo stato di approvazione dell'assenza.
     * @param approvazione {@code true} per approvare, {@code false} altrimenti.
     */
    public void setApprovazione(boolean approvazione) {
        this.approvazione = approvazione;
    }

    /**
     * Restituisce l'oggetto Medico associato all'assenza.
     * @return Il medico.
     */
    public Medico getMedico() {
        return medico;
    }

    /**
     * Associa un nuovo medico a questa assenza.
     * @param medico Il nuovo medico.
     */
    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    /**
     * Restituisce il turno di lavoro associato all'assenza.
     * @return Il turno di lavoro.
     */
    public Turno_Lavoro getTurnoLavoro() {
        return turnoLavoro;
    }

    /**
     * Associa un nuovo turno di lavoro a questa assenza.
     * @param turnoLavoro Il nuovo turno di lavoro.
     */
    public void setTurnoLavoro(Turno_Lavoro turnoLavoro) {
        this.turnoLavoro = turnoLavoro;
    }
}
