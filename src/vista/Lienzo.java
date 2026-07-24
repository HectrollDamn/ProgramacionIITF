package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import modelo.*;

/**
 * Lienzo Operativo para clicks y arrastres limpios.
 */
public class Lienzo extends JPanel {
    
    private Proyecto proyecto;
    private Tabla tablaSeleccionada;
    private int difX, difY;

    private final int ANCHO_TABLA = 160;
    private final int ALTO_TABLA = 120;

    public Lienzo(Proyecto proyecto) {
        this.proyecto = proyecto;
        this.setBackground(new Color(245, 245, 245));
        this.setPreferredSize(new Dimension(2000, 2000));
        initMecanicaMouse();
    }

    private void initMecanicaMouse() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                tablaSeleccionada = null;
                if (SwingUtilities.isRightMouseButton(e) || e.isPopupTrigger()) { return; }
                
                for (int i = proyecto.getTablas().size() - 1; i >= 0; i--) {
                    Tabla t = proyecto.getTablas().get(i);
                    if (e.getX() >= t.getX() && e.getX() <= (t.getX() + ANCHO_TABLA) &&
                        e.getY() >= t.getY() && e.getY() <= (t.getY() + ALTO_TABLA)) {
                        tablaSeleccionada = t;
                        difX = e.getX() - t.getX();
                        difY = e.getY() - t.getY();
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        break;
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    boolean sobreTabla = false;
                    for (Tabla t : proyecto.getTablas()) {
                        if (e.getX() >= t.getX() && e.getX() <= (t.getX() + ANCHO_TABLA) &&
                            e.getY() >= t.getY() && e.getY() <= (t.getY() + ALTO_TABLA)) {
                            sobreTabla = true;
                            ejecutarAsistenteNuevaColumnaRapida(t);
                            break;
                        }
                    }
                    if (!sobreTabla) {
                        String nombre = JOptionPane.showInputDialog(null, "Nombre de la nueva tabla:", "Nueva Tabla", JOptionPane.QUESTION_MESSAGE);
                        if (nombre != null && !nombre.trim().isEmpty()) {
                            Tabla nueva = new Tabla(nombre.trim(), e.getX(), e.getY());
                            proyecto.agregarTabla(nueva);
                            repaint();
                        }
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (tablaSeleccionada != null && SwingUtilities.isLeftMouseButton(e)) {
                    int nuevoX = e.getX() - difX;
                    int nuevoY = e.getY() - difY;
                    tablaSeleccionada.setX(Math.max(0, nuevoX));
                    tablaSeleccionada.setY(Math.max(0, nuevoY));
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (tablaSeleccionada != null) {
                    tablaSeleccionada = null;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }

    private void ejecutarAsistenteNuevaColumnaRapida(Tabla t) {
        String nombreCol = JOptionPane.showInputDialog(null, "Nueva columna para '" + t.getNombre() + "':");
        if (nombreCol != null && !nombreCol.trim().isEmpty()) {
            String[] tipos = {"INT", "VARCHAR", "DATE", "DECIMAL", "BOOLEAN"};
            String tipoCol = (String) JOptionPane.showInputDialog(null, "Tipo de dato:", "Tipo", JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
            if (tipoCol != null) {
                int longitud = 0;
                if (tipoCol.equals("VARCHAR")) {
                    String lenStr = JOptionPane.showInputDialog(null, "Longitud:", "50");
                    try { longitud = Integer.parseInt(lenStr); } catch(Exception ex) { longitud = 50; }
                }
                int esPK = JOptionPane.showConfirmDialog(null, "¿Es Clave Primaria (PK)?", "Propiedades", JOptionPane.YES_NO_OPTION);
                t.agregarColumna(new Columna(nombreCol.trim(), tipoCol, longitud, (esPK == JOptionPane.YES_OPTION), false));
                repaint();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2d.setColor(new Color(46, 204, 113));
        g2d.setStroke(new java.awt.BasicStroke(2));
        
        for (Relacion rel : proyecto.getRelaciones()) {
            int x1 = rel.getTablaOrigen().getX() + (ANCHO_TABLA / 2);
            int y1 = rel.getTablaOrigen().getY() + (ALTO_TABLA / 2);
            int x2 = rel.getTablaDestino().getX() + (ANCHO_TABLA / 2);
            int y2 = rel.getTablaDestino().getY() + (ALTO_TABLA / 2);
            
            g2d.drawLine(x1, y1, x2, y2);
            g2d.fillOval(x2 - 5, y2 - 5, 10, 10);
            
            g2d.setColor(new Color(44, 62, 80));
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            
            double distanciaTotal = Math.hypot(x2 - x1, y2 - y1);
            if (distanciaTotal > 0) {
                double dx = (x2 - x1) / distanciaTotal;
                double dy = (y2 - y1) / distanciaTotal;
                int margenBorde = 95;
                if (distanciaTotal < margenBorde * 2) margenBorde = (int) (distanciaTotal * 0.3);
                
                int xText1 = (int) (x1 + (dx * margenBorde));
                int yText1 = (int) (y1 + (dy * margenBorde)) - 5;
                int xText2 = (int) (x2 - (dx * margenBorde));
                int yText2 = (int) (y2 - (dy * margenBorde)) - 5;
                
                g2d.drawString(rel.getCardinalidadOrigen(), xText1, yText1);
                g2d.drawString(rel.getCardinalidadDestino(), xText2, yText2);
            }
            g2d.setColor(new Color(46, 204, 113));
        }
        
        for (Tabla tabla : proyecto.getTablas()) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(tabla.getX(), tabla.getY(), ANCHO_TABLA, ALTO_TABLA);
            g2d.setColor(new Color(180, 180, 180));
            g2d.drawRect(tabla.getX(), tabla.getY(), ANCHO_TABLA, ALTO_TABLA);
            
            g2d.setColor(new Color(41, 128, 185));
            g2d.fillRect(tabla.getX(), tabla.getY(), ANCHO_TABLA, 25);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(tabla.getNombre(), tabla.getX() + 10, tabla.getY() + 18);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            int despliegueY = tabla.getY() + 42;
            
            if (tabla.getColumnas() != null) {
                for (Columna col : tabla.getColumnas()) {
                    String prefijo = "      ";
                    if (col.isEsClavePrimaria()) {
                        g2d.setColor(new Color(230, 126, 34));
                        prefijo = "PK  ";
                    } else if (col.isEsClaveForanea()) {
                        g2d.setColor(new Color(46, 204, 113));
                        prefijo = "FK  ";
                    } else {
                        g2d.setColor(Color.DARK_GRAY);
                    }
                    g2d.drawString(prefijo + col.getNombre() + " : " + col.getTipoDato(), tabla.getX() + 8, despliegueY);
                    despliegueY += 20;
                }
            }
        }
    }
    
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; repaint(); }
}