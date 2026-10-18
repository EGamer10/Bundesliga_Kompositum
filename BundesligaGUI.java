import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BundesligaGUI extends JFrame {
    private static final Color BG_DARK = new Color(18, 20, 26);
    private static final Color BG_PANEL = new Color(28, 31, 40);
    private static final Color BG_ROW_ALT = new Color(34, 38, 49);
    private static final Color TEXT_LIGHT = new Color(235, 238, 245);
    private static final Color ACCENT_RED = new Color(210, 5, 21);

    private final LigaVerwaltung verwaltung;
    private final KompositumTabellenModell tableModel;
    private final JTable table;
    private final JComboBox<String> teamSelector;
    private final JTextArea logArea;
    private boolean isSyncingSelection = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LigaVerwaltung verwaltung = new LigaVerwaltung();
            new BundesligaGUI(verwaltung).setVisible(true);
        });
    }

    public BundesligaGUI(LigaVerwaltung verwaltung) {
        this.verwaltung = verwaltung;
        this.tableModel = new KompositumTabellenModell(verwaltung);
        this.table = new JTable(tableModel);
        this.teamSelector = new JComboBox<>();
        this.logArea = new JTextArea(7, 25);

        setTitle("Bundesliga Manager — Kompositum-Listen Edition (2-Listen-Architektur)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1060, 680);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(16, 16));

        add(createHeader(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);
        add(createControlSidebar(), BorderLayout.EAST);

        refreshTeamDropdown();
        log("Stammliste & SortierteListe (Kompositum) initialisiert.");
        log("Länge der Liste (LaengeGeben): " + verwaltung.SortierteListeGeben().LaengeGeben() + " Vereine.");
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, ACCENT_RED),
                new EmptyBorder(14, 20, 14, 20)
        ));

        JLabel title = new JLabel("BUNDESLIGA MANAGER (KOMPOSITUM: 2-LISTEN-SYSTEM)");
        title.setFont(new Font("SansSerif", Font.BOLD, 19));
        title.setForeground(TEXT_LIGHT);

        JLabel subtitle = new JLabel("Liste 1: Stammliste (Suchen/Speichern) | Liste 2: SortierteListe (SortiertEinfuegen)");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(160, 168, 185));

        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);
        return header;
    }

    private JScrollPane createTableSection() {
        table.setBackground(BG_PANEL);
        table.setForeground(TEXT_LIGHT);
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setGridColor(new Color(45, 50, 62));
        table.setSelectionBackground(new Color(65, 75, 105));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);

        table.getTableHeader().setBackground(new Color(14, 16, 20));
        table.getTableHeader().setForeground(TEXT_LIGHT);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        int[] widths = {55, 260, 60, 60, 60, 60, 85};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Klick in die Tabelle wählt den Verein im Dropdown aus
        table.getSelectionModel().addListSelectionListener(e -> {
            if (isSyncingSelection || e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row >= 0) {
                Datenelement d = verwaltung.SortierteListeGeben().DatenelementAnIndexGeben(row);
                if (d instanceof Mannschaft) {
                    isSyncingSelection = true;
                    teamSelector.setSelectedItem(((Mannschaft) d).NameGeben());
                    isSyncingSelection = false;
                }
            }
        });

        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean isSel, boolean hasFoc, int row, int col) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                cell.setHorizontalAlignment(col == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);
                cell.setBorder(new EmptyBorder(0, 8, 0, 8));

                if (!isSel) {
                    cell.setBackground(row % 2 == 0 ? BG_PANEL : BG_ROW_ALT);
                    cell.setForeground(TEXT_LIGHT);
                }

                if (col == 0) {
                    cell.setFont(new Font("SansSerif", Font.BOLD, 13));
                    if (row < 4) cell.setForeground(new Color(46, 204, 113));
                    else if (row == 4) cell.setForeground(new Color(52, 152, 219));
                    else if (row == 5) cell.setForeground(new Color(155, 89, 182));
                    else if (row == 15) cell.setForeground(new Color(241, 196, 15));
                    else if (row >= 16) cell.setForeground(new Color(231, 76, 60));
                } else if (col == 6) {
                    cell.setFont(new Font("SansSerif", Font.BOLD, 15));
                }
                return cell;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.setBorder(new EmptyBorder(0, 16, 16, 0));
        scroll.setBackground(BG_DARK);
        return scroll;
    }

    private JPanel createControlSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_PANEL);
        sidebar.setBorder(new EmptyBorder(16, 18, 16, 18));
        sidebar.setPreferredSize(new Dimension(330, 0));

        JLabel section1 = new JLabel("Einzelergebnis in Stammliste buchen");
        section1.setFont(new Font("SansSerif", Font.BOLD, 15));
        section1.setForeground(TEXT_LIGHT);
        section1.setAlignmentX(Component.LEFT_ALIGNMENT);

        teamSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        teamSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        teamSelector.addActionListener(e -> {
            if (isSyncingSelection) return;
            String selected = (String) teamSelector.getSelectedItem();
            if (selected != null) {
                highlightTeamInTable(selected);
            }
        });

        JButton btnWin = createStyledButton("Sieg buchen (+3 Punkte)", new Color(39, 174, 96));
        JButton btnDraw = createStyledButton("Unentschieden (+1 Punkt)", new Color(211, 84, 0));
        JButton btnLoss = createStyledButton("Niederlage (+0 Punkte)", new Color(127, 140, 141));

        btnWin.addActionListener(e -> bookSelectedTeam(3, "Sieg (+3 Pkt.)"));
        btnDraw.addActionListener(e -> bookSelectedTeam(1, "Unentschieden (+1 Pkt.)"));
        btnLoss.addActionListener(e -> bookSelectedTeam(0, "Niederlage (+0 Pkt.)"));

        JLabel section2 = new JLabel("Listen-Aktionen & Simulation");
        section2.setFont(new Font("SansSerif", Font.BOLD, 15));
        section2.setForeground(TEXT_LIGHT);
        section2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSim = createStyledButton("Kompletten Spieltag simulieren", new Color(41, 128, 185));
        JButton btnConsole = createStyledButton("InformationAusgeben() (Konsole)", new Color(142, 68, 173));
        JButton btnSave = createStyledButton("Stammliste als CSV speichern", new Color(52, 73, 94));
        JButton btnReset = createStyledButton("Saison auf 0 zurücksetzen", new Color(192, 57, 43));

        btnSim.addActionListener(e -> {
            verwaltung.SpieltagSimulieren();
            refreshUI((String) teamSelector.getSelectedItem());
            log("Spieltag simuliert -> SortierteListe per SortiertEinfuegen() neu aufgebaut.");
        });

        btnConsole.addActionListener(e -> {
            System.out.println("=== AUSGABE DER SORTIERTEN LISTE (InformationAusgeben) ===");
            verwaltung.SortierteListeGeben().InformationAusgeben();
            log("InformationAusgeben() rekursiv auf der Konsole ausgeführt.");
        });

        btnSave.addActionListener(e -> {
            try {
                verwaltung.AufFestplatteSpeichern();
                log("Stammliste gespeichert in: bundesliga_save.csv");
            } catch (IOException ex) {
                log("Fehler beim Speichern: " + ex.getMessage());
            }
        });

        btnReset.addActionListener(e -> {
            verwaltung.BundesligaVorlageLaden();
            refreshTeamDropdown();
            refreshUI(null);
            log("Beide Listen zurückgesetzt (18 Vereine mit 0 Punkten).");
        });

        logArea.setEditable(false);
        logArea.setBackground(new Color(14, 16, 20));
        logArea.setForeground(new Color(140, 210, 160));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        logScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 65, 80)),
                " Kompositum Audit-Log ", 0, 0, new Font("SansSerif", Font.BOLD, 12), TEXT_LIGHT));

        sidebar.add(section1);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(teamSelector);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnWin);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnDraw);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnLoss);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(section2);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnSim);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnConsole);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnSave);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnReset);
        sidebar.add(Box.createVerticalStrut(16));
        sidebar.add(logScroll);

        return sidebar;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void bookSelectedTeam(int punkteArt, String label) {
        String selectedName = (String) teamSelector.getSelectedItem();
        if (selectedName == null && table.getSelectedRow() >= 0) {
            Datenelement d = verwaltung.SortierteListeGeben().DatenelementAnIndexGeben(table.getSelectedRow());
            if (d instanceof Mannschaft) {
                selectedName = ((Mannschaft) d).NameGeben();
            }
        }
        if (selectedName == null) return;

        Mannschaft m = verwaltung.ErgebnisBuchen(selectedName, punkteArt);
        if (m != null) {
            refreshUI(m.NameGeben());
            log("Suchen('" + m.NameGeben() + "') -> " + label + " (Neu: " + m.PunkteGeben() + " Pkt.)");
        }
    }

    private void refreshTeamDropdown() {
        isSyncingSelection = true;
        String current = (String) teamSelector.getSelectedItem();
        teamSelector.removeAllItems();
        Liste stamm = verwaltung.StammListeGeben();
        int anzahl = stamm.LaengeGeben();
        for (int i = 0; i < anzahl; i++) {
            Datenelement d = stamm.DatenelementAnIndexGeben(i);
            if (d instanceof Mannschaft) {
                teamSelector.addItem(((Mannschaft) d).NameGeben());
            }
        }
        if (current != null) {
            teamSelector.setSelectedItem(current);
        }
        isSyncingSelection = false;
    }

    private void highlightTeamInTable(String teamName) {
        isSyncingSelection = true;
        Liste sortiert = verwaltung.SortierteListeGeben();
        int anzahl = sortiert.LaengeGeben();
        for (int i = 0; i < anzahl; i++) {
            Datenelement d = sortiert.DatenelementAnIndexGeben(i);
            if (d instanceof Mannschaft && ((Mannschaft) d).NameGeben().equals(teamName)) {
                table.setRowSelectionInterval(i, i);
                break;
            }
        }
        isSyncingSelection = false;
    }

    private void refreshUI(String highlightTeamName) {
        isSyncingSelection = true;
        tableModel.fireTableDataChanged();
        if (highlightTeamName != null) {
            teamSelector.setSelectedItem(highlightTeamName);
            Liste sortiert = verwaltung.SortierteListeGeben();
            int anzahl = sortiert.LaengeGeben();
            for (int i = 0; i < anzahl; i++) {
                Datenelement d = sortiert.DatenelementAnIndexGeben(i);
                if (d instanceof Mannschaft && ((Mannschaft) d).NameGeben().equals(highlightTeamName)) {
                    table.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
        isSyncingSelection = false;
    }

    private void log(String msg) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        logArea.append("[" + time + "] " + msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // Tabellen-Modell greift direkt rekursiv auf die SortierteListe (Liste 2) zu
    private static class KompositumTabellenModell extends AbstractTableModel {
        private final String[] columns = {"Platz", "Verein", "Sp.", "S", "U", "N", "Punkte"};
        private final LigaVerwaltung verwaltung;

        public KompositumTabellenModell(LigaVerwaltung verwaltung) {
            this.verwaltung = verwaltung;
        }

        @Override
        public int getRowCount() {
            return verwaltung.SortierteListeGeben().LaengeGeben();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int col) {
            return columns[col];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Datenelement d = verwaltung.SortierteListeGeben().DatenelementAnIndexGeben(rowIndex);
            if (!(d instanceof Mannschaft)) return "";
            Mannschaft m = (Mannschaft) d;
            return switch (columnIndex) {
                case 0 -> rowIndex + 1;
                case 1 -> m.NameGeben();
                case 2 -> m.SpieleGeben();
                case 3 -> m.SiegeGeben();
                case 4 -> m.UnentschiedenGeben();
                case 5 -> m.NiederlagenGeben();
                case 6 -> m.PunkteGeben();
                default -> "";
            };
        }
    }
}