package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Rappresenta un turno di lavoro di un medico.
 * Definisce l'intervallo temporale in cui un medico è di turno.
 */
public class TurnoLavoro{
    private LocalDate dataTurno;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private Medico medico;

    /**
     * Costruisce un nuovo oggetto Turno_Lavoro.
     *
     * @param dataTurno La data del turno.
     * @param oraInizio L'ora di inizio del turno.
     * @param oraFine   L'ora di fine del turno.
     * @param medico    Il medico assegnato al turno.
     */
    public TurnoLavoro(LocalDate dataTurno, LocalTime oraInizio, LocalTime oraFine, Medico medico){
        this.dataTurno = dataTurno;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.medico = medico;
    }

    /**
     * Restituisce la data del turno.
     * @return La data del turno.
     */
    public LocalDate getDataTurno() {
        return dataTurno;
    }

    /**
     * Imposta la data del turno.
     * @param dataTurno La nuova data del turno.
     */
    public void setDataTurno(LocalDate dataTurno) {
        this.dataTurno = dataTurno;
    }

    /**
     * Restituisce l'ora di inizio del turno.
     * @return L'ora di inizio.
     */
    public LocalTime getOraInizio() {
        return oraInizio;
    }

    /**
     * Imposta l'ora di inizio del turno.
     * @param oraInizio La nuova ora di inizio.
     */
    public void setOraInizio(LocalTime oraInizio) {
        this.oraInizio = oraInizio;
    }

    /**
     * Restituisce l'ora di fine del turno.
     * @return L'ora di fine.
     */
    public LocalTime getOraFine() {
        return oraFine;
    }

    /**
     * Imposta l'ora di fine del turno.
     * @param oraFine La nuova ora di fine.
     */
    public void setOraFine(LocalTime oraFine) {
        this.oraFine = oraFine;
    }

    /**
     * Restituisce il medico assegnato al turno.
     * @return Il medico.
     */
    public Medico getMedico() {
        return medico;
    }

    /**
     * Imposta il medico assegnato al turno.
     * @param medico Il nuovo medico.
     */
    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    @Override
    public String toString() {
        return "Turno_Lavoro{" +
                "dataTurno=" + dataTurno +
                ", oraInizio=" + oraInizio +
                ", oraFine=" + oraFine +
                ", medico=" + (medico != null ? medico.getMatricola() : "null") +
                '}';
    }
}
