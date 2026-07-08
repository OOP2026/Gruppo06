package dao;

import java.util.ArrayList;

public interface MedicoDAO {
    boolean aggiungiMedico(String nome, String cognome, String matricola, String login, String password, String iscrizioneAlbo, String specializzazione, String reparto);

    ArrayList<String> getMedicoByMatricola(String matricola);
    ArrayList<ArrayList<String>> getAllMedici();
    
    boolean aggiornaMedico(String nome, String cognome, String matricola, String iscrizioneAlbo, String specializzazione, String reparto);
    boolean eliminaMedico(String matricola);
    ArrayList<String> getMedicoByLoginAndPassword(String login, String password);
    boolean checkLoginEsistente(String login);
}