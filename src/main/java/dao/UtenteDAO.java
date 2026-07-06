package dao;

import java.util.ArrayList;

public interface UtenteDAO {
    boolean checkLoginEsistente(String login);
    boolean aggiungiUtente(String matricola, String login, String password, String nome, String cognome, String ruolo);
    ArrayList<String> getUtenteByLoginAndPassword(String login, String password);
}