package dao;

import model.Agenda;

import java.util.ArrayList;

public interface AgendaDAO {

    ArrayList<Agenda> getEventiByMedico(String matricolaMedico);

    boolean addEvento(Agenda evento);

    boolean updateEvento(Agenda evento);

    boolean deleteEvento(int idEvento);
}