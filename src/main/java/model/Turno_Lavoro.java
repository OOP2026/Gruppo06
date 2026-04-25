package model;

import java.util.Date;

public class Turno_Lavoro{
    public Date data;
    public Date inizio_turno;
    public Date fine_turno;
    public Medico medico;

    public Turno_Lavoro(Date data, Date inizio_turno, Date fine_turno){
        this.data = data;
        this.inizio_turno = fine_turno;
        this.fine_turno = fine_turno;
    }

    public void checkTurno(){
        System.out.println("Controllo turno...");
    }
}
