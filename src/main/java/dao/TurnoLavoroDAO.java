package dao;

import java.util.ArrayList;

public interface TurnoLavoroDAO {
    boolean aggiungiTurno(String matricola, String data, String inizioTurno, String fineTurno, String id_agenda);
    
    ArrayList<String> getTurno(String matricola, String data, String inizioTurno);
    ArrayList<ArrayList<String>> getTurniByMedico(String matricola);
    
    boolean aggiornaTurno(String matricola, String data, String vecchioInizio, String nuovoInizio, String nuovaFine);
    boolean eliminaTurno(String matricola, String data, String inizioTurno);

    boolean aggiornaMedicoTurno(int idTurno, String nuovaMatricola);
}