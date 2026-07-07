package model;

import java.util.Date;

public class Turno_Lavoro{
    public Date data_turno;
    public Date ora_inizio;
    public Date ora_fine;
    public Medico medico;

    public Turno_Lavoro(Date data_turno, Date ora_inizio, Date ora_fine){
        this.data_turno = data_turno;
        this.ora_inizio = ora_inizio;
        this.ora_fine = ora_fine;
    }

    public void checkTurno(){
        System.out.println("Controllo turno...");
    }
}
