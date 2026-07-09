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
     * Restituisce la data di fine dell'assenza.
     * @return La data di fine.
     */
    public LocalDate getDataFineAssenza() {
        return dataFineAssenza;
    }

    /**
     * Restituisce la motivazione dell'assenza.
     * @return La motivazione.
     */
    public String getMotivoAssenza() {
        return motivoAssenza;
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

}
