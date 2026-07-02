package dao;

import java.util.ArrayList;

public interface UtenteDAO {
    boolean checkLoginEsistente(String login);
    boolean aggiungiUtente(String nome, String cognome, String login, String password, String matricola, String pin);
    ArrayList<String> getUtenteByLoginAndPassword(String login, String password);
}