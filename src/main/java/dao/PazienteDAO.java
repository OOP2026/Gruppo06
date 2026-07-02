package dao;

import java.util.ArrayList;

public interface PazienteDAO {
    boolean aggiungiPaziente(String cf, String nome, String cognome, String dataNascita, String sesso, String residenza, String diagnosi);
    ArrayList<String> getPazienteByCf(String cf);
    ArrayList<ArrayList<String>> getAllPazienti();
    boolean aggiornaPaziente(String cf, String nome, String cognome, String dataNascita, String sesso, String residenza, String diagnosi);
    boolean eliminaPaziente(String cf);
}