package dao;

import java.util.ArrayList;

public interface AssenzaDAO {
    boolean aggiungiAssenza(String matricola, String dataInizio, String dataFine, String motivazione, boolean approvazione);
    
    ArrayList<String> getAssenza(String matricola, String dataInizio);
    ArrayList<ArrayList<String>> getAssenzeByMedico(String matricola);
    
    boolean aggiornaAssenza(String matricola, String dataInizio, String dataFine, String motivazione, boolean approvazione);
    boolean eliminaAssenza(String matricola, String dataInizio);
}