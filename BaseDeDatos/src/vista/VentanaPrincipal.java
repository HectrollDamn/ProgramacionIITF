package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

public class VentanaPrincipal extends JFrame {

    private JTextArea txtConsolaSQL;
    private JTree arbolEstructura;
    private DefaultTreeModel modeloArbol;
    private DefaultMutableTreeNode nodoRaiz;

    public VentanaPrincipal() {
        initComponentes();
        
        this.setTitle("Diseñador de Base de Datos Interactiva - SQL Generator");
        this.setSize(1200, 780);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private void initComponentes() {
        this.setLayout(new BorderLayout());

        // --- 1. MENÚ SUPERIOR DE CONSULTAS RÁPIDAS ---
        JMenuBar menuBar = new JMenuBar();
        JMenu menuConsultas = new JMenu("Consultas Rapidas");
        JMenuItem itemSelect = new JMenuItem("Estructura SELECT Basica");
        JMenuItem itemWhere = new JMenuItem("Filtro con WHERE (Asistente)");
        JMenuItem itemJoin = new JMenuItem("Estructura INNER JOIN");
        
        menuConsultas.add(itemSelect);
        menuConsultas.add(itemWhere);
        menuConsultas.addSeparator();
        menuConsultas.add(itemJoin);
        menuBar.add(menuConsultas);
        this.setJMenuBar(menuBar);
    }
}