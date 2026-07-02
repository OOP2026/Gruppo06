package dao;

import java.util.ArrayList;

public interface LettoDAO {
    boolean aggiungiLetto(String idLetto, String reparto);
    ArrayList<String> getLettoById(String idLetto);
    ArrayList<ArrayList<String>> getAllLetti();
    boolean aggiornaStatoLetto(String idLetto, boolean occupato);
}