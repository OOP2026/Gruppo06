package model;

import java.util.Date;

/**
 * Rappresenta un turno di lavoro di un medico.
 * Definisce l'intervallo temporale in cui un medico è di turno.
 */
public class Turno_Lavoro{
    public Date data_turno;
    public Date ora_inizio;
    public Date ora_fine;

    /**
     * Costruisce un nuovo oggetto Turno_Lavoro.
     *
     * @param data_turno La data del turno.
     * @param ora_inizio L'ora di inizio del turno.
     * @param ora_fine   L'ora di fine del turno.
     */
    public Turno_Lavoro(Date data_turno, Date ora_inizio, Date ora_fine){
        this.data_turno = data_turno;
        this.ora_inizio = ora_inizio;
        this.ora_fine = ora_fine;
    }
}
