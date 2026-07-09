package dao;

import java.util.ArrayList;
import java.util.List;

public interface LettoDAO {
    boolean aggiungiLetto(String idLetto, String reparto);
    ArrayList<String> getLettoById(String idLetto, String reparto);
    ArrayList<ArrayList<String>> getAllLetti();
    boolean aggiornaStatoLetto(String idLetto, String reparto, boolean occupato);
    List<String> getAllReparti();
}