package vista;

import java.awt.*;
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
    
    // Componentes del JTree
    private JTree arbolEstructura;
    private DefaultTreeModel modeloArbol;
    private DefaultMutableTreeNode nodoRaiz;
    private JPopupMenu popupArbol;

    // Componentes mínimos de Hilos y GUI agregados
    private JLabel lblContadorTiempo;
    private JProgressBar progressAutoguardado;
    private GestorHilosUI gestorHilos;

    public VentanaPrincipal() {
        proyectoActual = new Proyecto("MiBaseDeDatos");
        initComponentes();
        actualizarArbolEstructura();
        
        // Inicializar e iniciar Gestor de Hilos
        gestorHilos = new GestorHilosUI(proyectoActual, lblContadorTiempo, progressAutoguardado);
        gestorHilos.iniciarHilos();

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

        // --- 2. BARRA DE HERRAMIENTAS SUPERIOR ---
        JToolBar barraHerramientas = new JToolBar();
        barraHerramientas.setFloatable(false);
        barraHerramientas.setBackground(new Color(230, 233, 238));
        
        JButton btnNuevaTabla = new JButton("Nueva Tabla");
        JButton btnNuevaColumna = new JButton("Agregar Columna");
        JButton btnVincular = new JButton("Vincular"); 
        JButton btnGuardar = new JButton("Guardar");             
        JButton btnAbrir = new JButton("Abrir");                 
        JButton btnGenerarSQL = new JButton("Generar SQL");
        
        // Elementos visuales del Contador y ProgressBar
        lblContadorTiempo = new JLabel(" Sesion: 00:00 ");
        lblContadorTiempo.setFont(new Font("Arial", Font.BOLD, 12));
        lblContadorTiempo.setForeground(new Color(41, 128, 185));

        progressAutoguardado = new JProgressBar();
        progressAutoguardado.setStringPainted(true);
        progressAutoguardado.setPreferredSize(new Dimension(170, 18));

        barraHerramientas.add(btnNuevaTabla);
        barraHerramientas.add(btnNuevaColumna);
        barraHerramientas.add(btnVincular); 
        barraHerramientas.addSeparator();
        barraHerramientas.add(btnGuardar);
        barraHerramientas.add(btnAbrir);
        barraHerramientas.addSeparator();
        barraHerramientas.add(btnGenerarSQL);
        barraHerramientas.addSeparator();
        barraHerramientas.add(lblContadorTiempo);
        barraHerramientas.add(progressAutoguardado);

        this.add(barraHerramientas, BorderLayout.NORTH);

        // --- 3. JTREE ESTRUCTURAL (LATERAL IZQUIERDO) ---
        nodoRaiz = new DefaultMutableTreeNode("Base de Datos: " + proyectoActual.getNombre());
        modeloArbol = new DefaultTreeModel(nodoRaiz);
        arbolEstructura = new JTree(modeloArbol);
        arbolEstructura.setFont(new Font("Arial", Font.PLAIN, 12));
        configurarPopupArbolInteractiva(); 
        
        JScrollPane scrollArbol = new JScrollPane(arbolEstructura);
        scrollArbol.setPreferredSize(new Dimension(260, 0));
        scrollArbol.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Estructura del Modelo", 2, 0, null, Color.DARK_GRAY));
        this.add(scrollArbol, BorderLayout.WEST);

        // --- 4. LIENZO GRÁFICO INTERACTIVO (CENTRO) ---
        lienzo = new Lienzo(proyectoActual) {
            @Override
            public void repaint() {
                super.repaint();
                if (proyectoActual != null) {
                    actualizarArbolEstructura();
                }
            }
        };
        configurarMenuContextualLienzo();

        JScrollPane scrollLienzo = new JScrollPane(lienzo);
        scrollLienzo.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));
        this.add(scrollLienzo, BorderLayout.CENTER);

        // --- 5. CONSOLA INFERIOR EDITABLE ---
        txtConsolaSQL = new JTextArea();
        txtConsolaSQL.setEditable(true);
        txtConsolaSQL.setBackground(new Color(30, 30, 30)); 
        txtConsolaSQL.setForeground(new Color(139, 233, 253)); 
        txtConsolaSQL.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        JScrollPane scrollConsola = new JScrollPane(txtConsolaSQL);
        scrollConsola.setPreferredSize(new Dimension(1000, 180));
        scrollConsola.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY), "Consola de Salida SQL / Consultas Explicadas", 2, 0, null, Color.GRAY));
        this.add(scrollConsola, BorderLayout.SOUTH);

        // --- ACCIONES DE MENÚ CONSULTAS ---
        itemSelect.addActionListener(e -> {
            List<Tabla> tablas = proyectoActual.getTablas();
            if (tablas.isEmpty()) {
                txtConsolaSQL.setText("-- [ERROR] No hay tablas en el modelo.");
            } else {
                Tabla primera = tablas.get(0);
                String col = primera.getColumnas().isEmpty() ? "id" : primera.getColumnas().get(0).getNombre();
                txtConsolaSQL.setText("-- ==========================================================\n" +
                                     "-- CONSULTA TRADUCIDA EN ESPANOL: LISTAR DATOS BASICOS\n" +
                                     "SELECT " + col + " \nFROM " + primera.getNombre() + ";");
            }
        });

        itemWhere.addActionListener(e -> {
            List<Tabla> tablas = proyectoActual.getTablas();
            if (tablas.isEmpty()) return;
            Tabla primera = tablas.get(0);
            String colName = primera.getColumnas().isEmpty() ? "id" : primera.getColumnas().get(0).getNombre();
            String valorBusqueda = JOptionPane.showInputDialog(null, "¿Que valor deseas buscar en la columna '" + colName + "'?:");
            if (valorBusqueda == null || valorBusqueda.trim().isEmpty()) valorBusqueda = "1";
            
            txtConsolaSQL.setText("-- ==========================================================\n" +
                                 "-- CONSULTA TRADUCIDA EN ESPANOL: BUSCAR POR FILTRO CONDICIONAL\n" +
                                 "SELECT * \nFROM " + primera.getNombre() + " \nWHERE " + colName + " = " + valorBusqueda.trim() + ";");
        });

        itemJoin.addActionListener(e -> {
            List<Relacion> relaciones = proyectoActual.getRelaciones();
            if (relaciones.isEmpty()) {
                txtConsolaSQL.setText("-- [ERROR] Vincula dos tablas con una linea verde primero para armar el JOIN.");
            } else {
                Relacion rel = relaciones.get(0);
                txtConsolaSQL.setText("-- ==========================================================\n" +
                                     "-- CONSULTA RELACIONAL AUTOMATICA TRADUCIDA (INNER JOIN)\n" +
                                     "SELECT * \nFROM " + rel.getTablaOrigen().getNombre() + " \nINNER JOIN " + rel.getTablaDestino().getNombre() + ";");
            }
        });

        // --- ACCIONES DE BOTONES SUPERIORES ---
        btnNuevaTabla.addActionListener(e -> ejecutarAltaTabla());
        btnNuevaColumna.addActionListener(e -> ejecutarAltaColumna());
        
        btnVincular.addActionListener(e -> {
            if (proyectoActual.getTablas().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay tablas en el modelo para establecer enlaces.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] nombresTablas = proyectoActual.getTablas().stream().map(Tabla::getNombre).toArray(String[]::new);
            String tOrigenStr = (String) JOptionPane.showInputDialog(null, "Selecciona la Tabla Origen (PK):", "Asistente de Vinculacion", JOptionPane.QUESTION_MESSAGE, null, nombresTablas, nombresTablas[0]);
            
            if (tOrigenStr != null) {
                Tabla tOrigen = proyectoActual.getTablas().stream().filter(x -> x.getNombre().equals(tOrigenStr)).findFirst().orElse(null);
                if (tOrigen != null) {
                    ejecutarAsistenteVinculacionDesdeTabla(tOrigen);
                }
            }
        });

        btnGuardar.addActionListener(e -> {
            javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
            if (selector.showSaveDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File file = selector.getSelectedFile();
                if (!file.getName().endsWith(".dat")) file = new java.io.File(file.getAbsolutePath() + ".dat");
                
                if (controlador.AdministradorFicheros.guardarProyecto(file, proyectoActual)) {
                    if (gestorHilos != null) gestorHilos.setArchivoActual(file); // Asignar ruta para activar autoguardado
                }
            }
        });

        btnAbrir.addActionListener(e -> {
            javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
            if (selector.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
                File archivoSel = selector.getSelectedFile();
                Proyecto cargado = controlador.AdministradorFicheros.cargarProyecto(archivoSel);
                if (cargado != null) {
                    proyectoActual = cargado;
                    lienzo.setProyecto(proyectoActual);
                    actualizarArbolEstructura();
                    
                    if (gestorHilos != null) {
                        gestorHilos.setProyecto(proyectoActual);
                        gestorHilos.setArchivoActual(archivoSel); // Asignar ruta para activar autoguardado
                    }
                }
            }
        });

        btnGenerarSQL.addActionListener(e -> txtConsolaSQL.setText(proyectoActual.generarScriptSQLCompleto()));
    }

    private void verificarCambiosEnRelaciones() {
        List<Relacion> relaciones = proyectoActual.getRelaciones();
        List<Relacion> aEliminar = new ArrayList<>();
        for (Relacion r : relaciones) {
            if (!proyectoActual.getTablas().contains(r.getTablaOrigen()) || !proyectoActual.getTablas().contains(r.getTablaDestino())) {
                aEliminar.add(r);
                continue;
            }
            boolean origenOk = r.getTablaOrigen().getColumnas().contains(r.getColumnaOrigen()) && r.getColumnaOrigen().isEsClavePrimaria();
            if (!origenOk) aEliminar.add(r);
        }
        for (Relacion r : aEliminar) proyectoActual.getRelaciones().remove(r);
    }

    // ==========================================
    // POPUP DEL JTREE TOTALMENTE CORREGIDO Y BLINDADO
    // ==========================================
    private void configurarPopupArbolInteractiva() {
        popupArbol = new JPopupMenu();
        
        arbolEstructura.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    evaluarYMostrarPopupArbol(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    evaluarYMostrarPopupArbol(e);
                }
            }
            
            private void evaluarYMostrarPopupArbol(MouseEvent e) {
                if (popupArbol.isVisible()) {
                    popupArbol.setVisible(false);
                }
                
                TreePath path = arbolEstructura.getPathForLocation(e.getX(), e.getY());
                
                if (path == null) {
                    int filaCercana = arbolEstructura.getClosestRowForLocation(e.getX(), e.getY());
                    if (filaCercana != -1) {
                        java.awt.Rectangle bounds = arbolEstructura.getRowBounds(filaCercana);
                        if (bounds != null && e.getY() >= bounds.y && e.getY() <= bounds.y + bounds.height) {
                            path = arbolEstructura.getPathForRow(filaCercana);
                        }
                    }
                }
                
                if (path == null) return;
                
                arbolEstructura.setSelectionPath(path);
                DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) path.getLastPathComponent();
                
                popupArbol.removeAll(); 
                int nivelJerarquia = path.getPathCount();
                
                if (nivelJerarquia == 3 && nodoSeleccionado.getParent() != null && 
                    nodoSeleccionado.getParent().toString().startsWith("Tablas")) { 
                    
                    String nombreTabla = nodoSeleccionado.toString();
                    Tabla tabla = buscarTablaPorNombre(nombreTabla);
                    
                    if (tabla != null) {
                        JMenuItem mAddCol = new JMenuItem("Anadir Columna a " + nombreTabla);
                        JMenuItem mRenombrar = new JMenuItem("Modificar Nombre de Tabla");
                        JMenuItem mDelete = new JMenuItem("Eliminar del Modelo");
                        
                        mAddCol.addActionListener(ae -> ejecutarAltaColumnaParaTabla(tabla));
                        mRenombrar.addActionListener(ae -> {
                            String nuevoNom = JOptionPane.showInputDialog(null, "Nuevo nombre para la tabla:", tabla.getNombre());
                            if (nuevoNom != null && !nuevoNom.trim().isEmpty()) {
                                tabla.setNombre(nuevoNom.trim());
                                lienzo.repaint();
                                actualizarArbolEstructura();
                            }
                        });
                        mDelete.addActionListener(ae -> {
                            proyectoActual.getTablas().remove(tabla);
                            verificarCambiosEnRelaciones();
                            lienzo.repaint();
                            actualizarArbolEstructura();
                        });
                        
                        popupArbol.add(mAddCol);
                        popupArbol.add(mRenombrar);
                        popupArbol.addSeparator();
                        popupArbol.add(mDelete);
                        popupArbol.show(arbolEstructura, e.getX(), e.getY());
                    }
                } 
                else if (nivelJerarquia == 4) { 
                    DefaultMutableTreeNode nodoPadreTabla = (DefaultMutableTreeNode) path.getPathComponent(2);
                    Tabla tablaPadre = buscarTablaPorNombre(nodoPadreTabla.toString());
                    
                    if (tablaPadre != null) {
                        String nodoStr = nodoSeleccionado.toString();
                        String limpio = nodoStr.replace("[PK] ", "").replace("[FK] ", "").replaceAll(" \\(.*\\)", "").trim();
                        Columna columna = tablaPadre.getColumnas().stream().filter(c -> c.getNombre().equals(limpio)).findFirst().orElse(null);
                        
                        if (columna != null) {
                            JMenuItem mModif = new JMenuItem("Modificar Atributo / PK");
                            JMenuItem mDeleteCol = new JMenuItem("Eliminar Columna");
                            
                            mModif.addActionListener(ae -> ejecutarModificacionColumnaEspecifica(tablaPadre, columna));
                            mDeleteCol.addActionListener(ae -> {
                                tablaPadre.getColumnas().remove(columna);
                                verificarCambiosEnRelaciones();
                                lienzo.repaint();
                                actualizarArbolEstructura();
                            });
                            
                            popupArbol.add(mModif);
                            popupArbol.add(mDeleteCol);
                            popupArbol.show(arbolEstructura, e.getX(), e.getY());
                        }
                    }
                }
                else if (nivelJerarquia == 3 && nodoSeleccionado.getParent() != null && 
                         nodoSeleccionado.getParent().toString().startsWith("Relaciones")) {
                    
                    String relStr = nodoSeleccionado.toString().replaceAll(" \\(.*\\)", "").trim();
                    Relacion relacion = proyectoActual.getRelaciones().stream().filter(r -> r.getNombreRestriccion().equals(relStr)).findFirst().orElse(null);
                    if (relacion != null) {
                        JMenuItem mDeleteRel = new JMenuItem("Eliminar Relacion");
                        mDeleteRel.addActionListener(ae -> {
                            proyectoActual.getRelaciones().remove(relacion);
                            lienzo.repaint();
                            actualizarArbolEstructura();
                        });
                        popupArbol.add(mDeleteRel);
                        popupArbol.show(arbolEstructura, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private Tabla buscarTablaPorNombre(String nombre) {
        return proyectoActual.getTablas().stream().filter(t -> t.getNombre().equals(nombre)).findFirst().orElse(null);
    }

    // ==========================================
    // MENÚ CONTEXTUAL DEL LIENZO
    // ==========================================
    private void configurarMenuContextualLienzo() {
        lienzo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) { if (SwingUtilities.isRightMouseButton(e)) desplegar(e); }
            @Override
            public void mousePressed(MouseEvent e) { if (SwingUtilities.isRightMouseButton(e)) desplegar(e); }
            
            private void desplegar(MouseEvent e) {
                Tabla t = buscarTablaEnCoordenada(e.getX(), e.getY());
                if (t != null) {
                    JPopupMenu menuTabla = new JPopupMenu();
                    JMenuItem mAltaCol = new JMenuItem("Añadir Columna");
                    JMenuItem mModifCol = new JMenuItem("Modificar Atributo / PK");
                    JMenuItem mVincular = new JMenuItem("Vincular con otra tabla"); 
                    JMenuItem mEliminar = new JMenuItem("Eliminar Tabla");
                    
                    menuTabla.add(mAltaCol);
                    menuTabla.add(mModifCol);
                    menuTabla.add(mVincular);
                    menuTabla.addSeparator();
                    menuTabla.add(mEliminar);
                    
                    mAltaCol.addActionListener(ae -> ejecutarAltaColumnaParaTabla(t));
                    mModifCol.addActionListener(ae -> ejecutarModificacionColumna(t));
                    mVincular.addActionListener(ae -> ejecutarAsistenteVinculacionDesdeTabla(t));
                    mEliminar.addActionListener(ae -> {
                        proyectoActual.getTablas().remove(t);
                        verificarCambiosEnRelaciones();
                        lienzo.repaint();
                    });
                    
                    menuTabla.show(lienzo, e.getX(), e.getY());
                }
            }
        });
    }

    private Tabla buscarTablaEnCoordenada(int x, int y) {
        for (Tabla t : proyectoActual.getTablas()) {
            if (x >= t.getX() && x <= t.getX() + 180 && y >= t.getY() && y <= t.getY() + 160) {
                return t;
            }
        }
        return null;
    }

    // ==========================================
    // ASISTENTES INTERNOS DE CONFIGURACIÓN
    // ==========================================
    private void ejecutarAsistenteVinculacionDesdeTabla(Tabla tOrigen) {
        List<Columna> pksOrigen = new ArrayList<>();
        for (Columna c : tOrigen.getColumnas()) { if (c.isEsClavePrimaria()) pksOrigen.add(c); }
        
        if (pksOrigen.isEmpty()) {
            JOptionPane.showMessageDialog(null, "La tabla origen '" + tOrigen.getNombre() + "' requiere una Clave Primaria (PK).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] colsPK = pksOrigen.stream().map(Columna::getNombre).toArray(String[]::new);
        String colOrigenStr = (String) JOptionPane.showInputDialog(null, "Selecciona la columna PK:", "Vincular", JOptionPane.QUESTION_MESSAGE, null, colsPK, colsPK[0]);
        if (colOrigenStr == null) return;
        Columna cOrigen = pksOrigen.stream().filter(x -> x.getNombre().equals(colOrigenStr)).findFirst().orElse(null);

        List<Tabla> destinos = new ArrayList<>();
        for (Tabla t : proyectoActual.getTablas()) { if (!t.equals(tOrigen)) destinos.add(t); }
        if (destinos.isEmpty()) return;
        
        String[] nombresDestino = destinos.stream().map(Tabla::getNombre).toArray(String[]::new);
        String tDestinoStr = (String) JOptionPane.showInputDialog(null, "Selecciona la Tabla Destino:", "Vincular", JOptionPane.QUESTION_MESSAGE, null, nombresDestino, nombresDestino[0]);
        if (tDestinoStr == null) return;
        Tabla tDestino = destinos.stream().filter(x -> x.getNombre().equals(tDestinoStr)).findFirst().orElse(null);

        List<Columna> normalesDestino = new ArrayList<>();
        for (Columna c : tDestino.getColumnas()) { if (!c.isEsClavePrimaria()) normalesDestino.add(c); }
        if (normalesDestino.isEmpty()) {
            JOptionPane.showMessageDialog(null, "La tabla destino requiere una columna libre (FK).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] colsFK = normalesDestino.stream().map(Columna::getNombre).toArray(String[]::new);
        String colDestinoStr = (String) JOptionPane.showInputDialog(null, "Selecciona la columna destino:", "Vincular", JOptionPane.QUESTION_MESSAGE, null, colsFK, colsFK[0]);
        if (colDestinoStr == null) return;
        Columna cDestino = normalesDestino.stream().filter(x -> x.getNombre().equals(colDestinoStr)).findFirst().orElse(null);

        String[] opciones = {"1 a Muchos (1 : \u221E)", "1 a 1 (1 : 1)"};
        String seleccionCard = (String) JOptionPane.showInputDialog(null, "Selecciona cardinalidad:", "Vincular", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        String cardDestino = (seleccionCard != null && seleccionCard.startsWith("1 a 1")) ? "1" : "\u221E";

        String nombreConstraint = "fk_" + tDestino.getNombre() + "_" + tOrigen.getNombre();
        Relacion nuevaRel = new Relacion(nombreConstraint, tOrigen, cOrigen, tDestino, cDestino, "1", cardDestino);
        proyectoActual.agregarRelacion(nuevaRel);
        lienzo.repaint();
    }

    private void ejecutarAltaTabla() {
        String nombre = JOptionPane.showInputDialog(null, "Nombre de la tabla:", "Nueva Tabla", JOptionPane.QUESTION_MESSAGE);
        if (nombre != null && !nombre.trim().isEmpty()) {
            proyectoActual.agregarTabla(new Tabla(nombre.trim(), 60, 60));
            lienzo.repaint();
        }
    }

    private void ejecutarAltaColumna() {
        if (proyectoActual.getTablas().isEmpty()) return;
        String[] nombres = proyectoActual.getTablas().stream().map(Tabla::getNombre).toArray(String[]::new);
        String seleccion = (String) JOptionPane.showInputDialog(null, "Tabla destino:", "Columna", JOptionPane.QUESTION_MESSAGE, null, nombres, nombres[0]);
        if (seleccion != null) {
            Tabla obj = proyectoActual.getTablas().stream().filter(x -> x.getNombre().equals(seleccion)).findFirst().orElse(null);
            if (obj != null) ejecutarAltaColumnaParaTabla(obj);
        }
    }

    private void ejecutarAltaColumnaParaTabla(Tabla objetivo) {
        String nombreCol = JOptionPane.showInputDialog(null, "Nombre columna:");
        if (nombreCol != null && !nombreCol.trim().isEmpty()) {
            String[] tipos = {"INT", "VARCHAR", "DATE", "DECIMAL", "BOOLEAN"};
            String tipoCol = (String) JOptionPane.showInputDialog(null, "Tipo de dato:", "Tipo", JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
            if (tipoCol != null) {
                int len = 0;
                if (tipoCol.equals("VARCHAR")) {
                    try { len = Integer.parseInt(JOptionPane.showInputDialog(null, "Longitud del VARCHAR:", "50")); } catch(Exception ex) { len = 50; }
                }
                int esPK = JOptionPane.showConfirmDialog(null, "¿Es Clave Primaria (PK)?", "Propiedades", JOptionPane.YES_NO_OPTION);
                boolean esPrimaria = (esPK == JOptionPane.YES_OPTION);
                
                objetivo.agregarColumna(new Columna(nombreCol.trim(), tipoCol, len, esPrimaria, esPrimaria));
                verificarCambiosEnRelaciones();
                lienzo.repaint();
            }
        }
    }

    private void ejecutarModificacionColumna(Tabla t) {
        if (t.getColumnas().isEmpty()) return;
        String[] columnas = t.getColumnas().stream().map(Columna::getNombre).toArray(String[]::new);
        String colSel = (String) JOptionPane.showInputDialog(null, "Atributo a editar:", "Modificar", JOptionPane.QUESTION_MESSAGE, null, columnas, columnas[0]);
        if (colSel != null) {
            Columna c = t.getColumnas().stream().filter(x -> x.getNombre().equals(colSel)).findFirst().orElse(null);
            if (c != null) {
                ejecutarModificacionColumnaEspecifica(t, c);
            }
        }
    }

    private void ejecutarModificacionColumnaEspecifica(Tabla t, Columna c) {
        String nom = JOptionPane.showInputDialog(null, "Modificar nombre de columna:", c.getNombre());
        if (nom != null && !nom.trim().isEmpty()) c.setNombre(nom.trim());
        
        String[] tipos = {"INT", "VARCHAR", "DATE", "DECIMAL", "BOOLEAN"};
        String nuevoTipo = (String) JOptionPane.showInputDialog(null, "Tipo de dato:", "Modificar Tipo", JOptionPane.QUESTION_MESSAGE, null, tipos, c.getTipoDato());
        if (nuevoTipo != null) c.setTipoDato(nuevoTipo);
        
        int esPK = JOptionPane.showConfirmDialog(null, "¿Será Clave Primaria (PK)?", "Modificar PK", JOptionPane.YES_NO_OPTION);
        c.setEsClavePrimaria(esPK == JOptionPane.YES_OPTION);
        
        verificarCambiosEnRelaciones();
        lienzo.repaint();
    }

    private void actualizarArbolEstructura() {
        if (nodoRaiz == null || modeloArbol == null) return;
        nodoRaiz.removeAllChildren();
        nodoRaiz.setUserObject("Base de Datos: " + proyectoActual.getNombre());

        DefaultMutableTreeNode nodoTablas = new DefaultMutableTreeNode("Tablas (" + proyectoActual.getTablas().size() + ")");
        for (Tabla t : proyectoActual.getTablas()) {
            DefaultMutableTreeNode nodoT = new DefaultMutableTreeNode(t.getNombre());
            for (Columna c : t.getColumnas()) {
                String tag = c.isEsClavePrimaria() ? "[PK] " : (c.isEsClaveForanea() ? "[FK] " : "");
                nodoT.add(new DefaultMutableTreeNode(tag + c.getNombre() + " (" + c.getTipoDato() + ")"));
            }
            nodoTablas.add(nodoT);
        }
        nodoRaiz.add(nodoTablas);

        DefaultMutableTreeNode nodoRelaciones = new DefaultMutableTreeNode("Relaciones (" + proyectoActual.getRelaciones().size() + ")");
        for (Relacion r : proyectoActual.getRelaciones()) {
            nodoRelaciones.add(new DefaultMutableTreeNode(r.getNombreRestriccion() + " (" + r.getCardinalidadOrigen() + ":" + r.getCardinalidadDestino() + ")"));
        }
        nodoRaiz.add(nodoRelaciones);
        
        modeloArbol.reload();
        for (int i = 0; i < arbolEstructura.getRowCount(); i++) arbolEstructura.expandRow(i);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
