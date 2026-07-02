package dao;

import java.util.ArrayList;

public interface Turno_LavoroDAO {
    boolean aggiungiTurno(String matricola, String data, String inizioTurno, String fineTurno);
    
    ArrayList<String> getTurno(String matricola, String data, String inizioTurno);
    ArrayList<ArrayList<String>> getTurniByMedico(String matricola);
    
    boolean aggiornaTurno(String matricola, String data, String inizioTurno, String fineTurno);
    boolean eliminaTurno(String matricola, String data, String inizioTurno);
}