package gui;

import javax.swing.*;
import javax.swing.table.*;

public class Medici extends JFrame {
    public JPanel mainPanel;
    private JTextField nomeField;
    private JTextField codiceField;
    private JButton resetButton;
    private JButton cercaButton;
    private JTable mediciTable;
    private JButton newmedicoButton;

    private JList<String> specializzazioneList;
    private JList<String> repartoList;
    private JButton assenzaButton;
    private JButton modificamedicoButton;
    private JRadioButton attivoRadioButton;
    private JRadioButton assenteRadioButton;
    private JRadioButton occupatoRadioButton;
    private JRadioButton tuttiRadioButton;

    private static final String[] COLONNE = {
            "Matricola", "Cognome e Nome", "Specializzazione",
            "Reparto Assegnato", "Stato"
    };

    private static final String[] SPECIALIZZAZIONI_DATA = {
            "Chirurgia Generale", "Cardiologia", "Neurologia",
            "Anestesia", "Chirurgia Toracica", "Ematologia", "Otorinolaringoiatria"
    };

    private static final String[] REPARTI_DATA = {
            "Blocco Operatorio", "Terapia Intensiva", "Neuroradiologia",
            "Chirurgia Toracica", "Laboratorio Analisi", "Pronto Soccorso"
    };
    
    private Object[][] datiMedici = new Object[0][0];
    
    public Medici() {
        initComponents();
        setupStyles();
		if (specializzazioneList != null) {
			specializzazioneList.setListData(SPECIALIZZAZIONI_DATA);
		}

		if (repartoList != null) {
			repartoList.setListData(REPARTI_DATA);
		}

		if (tuttiRadioButton != null && attivoRadioButton != null && assenteRadioButton != null && occupatoRadioButton != null) {
			ButtonGroup statoGroup = new ButtonGroup();
			statoGroup.add(tuttiRadioButton);
			statoGroup.add(attivoRadioButton);
			statoGroup.add(assenteRadioButton);
			statoGroup.add(occupatoRadioButton);
			tuttiRadioButton.setSelected(true);
		}

		if (mediciTable != null) {
			DefaultTableModel model = new DefaultTableModel(COLONNE, 0) {
				@Override public boolean isCellEditable(int row, int column) { return false; }
			};
			mediciTable.setModel(model);
		}
        setupListeners();
        if (mediciTable != null) {
            loadTableData(null, null, null, null);
        }
    }

    public void aggiornaTabella(Object[][] dati) {
        this.datiMedici = dati != null ? dati : new Object[0][0];
        loadTableData(null, null, null, null);
    }

    public void addNuovoMedicoListener(java.awt.event.ActionListener listener) {
        newmedicoButton.addActionListener(listener);
    }

    public void addModificaMedicoListener(java.awt.event.ActionListener listener) {
        modificamedicoButton.addActionListener(listener);
    }

    public void addAssenzaListener(java.awt.event.ActionListener listener) {
        assenzaButton.addActionListener(listener);
    }

    public String getMatricolaMedicoSelezionato() {
        int selectedRow = mediciTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        return (String) mediciTable.getValueAt(selectedRow, 0);
    }

    private void initComponents() {
        // Questo metodo è mantenuto per compatibilità con il GUI Designer, ma la logica è stata spostata.
    }
    //Setup degli stili visivi per le componenti GUI
    private void setupStyles() {
        Login.styleList(specializzazioneList);
        Login.styleList(repartoList);
        Login.setupTableStyle(mediciTable);
        Login.applicaStilePulsantiCentrali(cercaButton);
        Login.applicaStilePulsantiCentrali(resetButton);
        Login.applicaStilePulsantiCentrali(modificamedicoButton);
        Login.applicaStilePulsantiCentrali(newmedicoButton);
        Login.applicaStilePulsantiCentrali(assenzaButton);
    }

    private void setupListeners() {
        cercaButton.addActionListener(e -> {
            String nome = nomeField.getText().toLowerCase().trim();
            String matricola = codiceField.getText().toLowerCase().trim();//codiceField viene usato per il filtraggio
            String specializzazione = specializzazioneList.getSelectedValue();
            String reparto = repartoList.getSelectedValue();

            loadTableData(nome, matricola, specializzazione, reparto);
        });

        resetButton.addActionListener(e -> {
            nomeField.setText("");
            codiceField.setText("");
            specializzazioneList.clearSelection();
            repartoList.clearSelection();
            tuttiRadioButton.setSelected(true);

            loadTableData(null, null, null, null);
        });
    }

    private void loadTableData(String filtroNome, String filtroMatricola, String filtroSpec, String filtroReparto) {
        DefaultTableModel m = (DefaultTableModel) mediciTable.getModel();
        m.setRowCount(0);

        String filtroStato = null;
        if (tuttiRadioButton.isSelected()) {
            filtroStato = null; // Nessun filtro
        } else if (attivoRadioButton.isSelected()) {
            filtroStato = "attivo";
        } else if (assenteRadioButton.isSelected()) {
            filtroStato = "assente";
        } else if (occupatoRadioButton.isSelected()) {
            filtroStato = "occupato";
        }

        for (Object[] row : datiMedici) {
            String rMatricola = ((String) row[0]).toLowerCase();
            String rNome = ((String) row[1]).toLowerCase();
            String rSpec = (String) row[2];
            String rReparto = (String) row[3];
            String rStato = ((String) row[4]).toLowerCase();

            boolean matchNome = (filtroNome == null || filtroNome.isEmpty() || rNome.contains(filtroNome));
            boolean matchMatricola = (filtroMatricola == null || filtroMatricola.isEmpty() || rMatricola.contains(filtroMatricola));

            boolean matchSpec = (filtroSpec == null || rSpec.equals(filtroSpec));
            boolean matchReparto = (filtroReparto == null || rReparto.equals(filtroReparto));
            
            boolean matchStato = (filtroStato == null || rStato.equals(filtroStato));

            if (matchNome && matchMatricola && matchSpec && matchReparto && matchStato) {
                m.addRow(row);
            }
        }
    }

}