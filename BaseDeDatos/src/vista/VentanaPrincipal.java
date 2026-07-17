package vista;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import modelo.*;
import controlador.*;

public class VentanaPrincipal extends JFrame {

    private Proyecto proyectoActual;
    private Lienzo lienzo;
    private JTextArea txtConsolaSQL;
    
    private JTree arbolEstructura;
    private DefaultTreeModel modeloArbol;
    private DefaultMutableTreeNode nodoRaiz;
    private JPopupMenu popupArbol;

    public VentanaPrincipal() {
        proyectoActual = new Proyecto("MiBaseDeDatos");
        initComponentes();
        actualizarArbolEstructura();
        
        this.setTitle("Diseñador de Base de Datos Interactiva - SQL Generator");
        this.setSize(1200, 780);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private void initComponentes() {
        this.setLayout(new BorderLayout());

        // --- 1. MENÚ SUPERIOR ---
        JMenuBar menuBar = new JMenuBar();
        JMenu menuConsultas = new JMenu("Consultas Rapidas");
        JMenuItem itemSelect = new JMenuItem("Estructura SELECT Basica");
        JMenuItem itemWhere = new JMenuItem("Filtro con WHERE (Asistente)");
        JMenuItem itemJoin = new JMenuItem("Estructura INNER JOIN");
        
        menuConsultas.add(itemSelect); menuConsultas.add(itemWhere);
        menuConsultas.addSeparator(); menuConsultas.add(itemJoin);
        menuBar.add(menuConsultas); this.setJMenuBar(menuBar);

        // --- 2. BARRA DE HERRAMIENTAS ---
        JToolBar barraHerramientas = new JToolBar(); barraHerramientas.setFloatable(false);
        barraHerramientas.setBackground(new Color(230, 233, 238));
        
        JButton btnNuevaTabla = new JButton("Nueva Tabla");
        JButton btnNuevaColumna = new JButton("Agregar Columna");
        JButton btnVincular = new JButton("Vincular"); 
        JButton btnGuardar = new JButton("Guardar");
        JButton btnAbrir = new JButton("Abrir");
        JButton btnGenerarSQL = new JButton("Generar SQL");
        
        barraHerramientas.add(btnNuevaTabla); barraHerramientas.add(btnNuevaColumna); barraHerramientas.add(btnVincular);
        barraHerramientas.addSeparator(); barraHerramientas.add(btnGuardar); barraHerramientas.add(btnAbrir);
        barraHerramientas.addSeparator(); barraHerramientas.add(btnGenerarSQL);
        this.add(barraHerramientas, BorderLayout.NORTH);

        // --- 3. JTREE (IZQUIERDA) ---
        nodoRaiz = new DefaultMutableTreeNode("Base de Datos");
        modeloArbol = new DefaultTreeModel(nodoRaiz);
        arbolEstructura = new JTree(modeloArbol);
        arbolEstructura.setFont(new Font("Arial", Font.PLAIN, 12));
        configurarPopupArbolInteractiva();
        
        JScrollPane scrollArbol = new JScrollPane(arbolEstructura);
        scrollArbol.setPreferredSize(new Dimension(260, 0));
        scrollArbol.setBorder(BorderFactory.createTitledBorder("Estructura del Modelo"));
        this.add(scrollArbol, BorderLayout.WEST);
    }
}