package vista;

import java.awt.*;
import javax.swing.*;

import org.w3c.dom.events.MouseEvent;

import modelo.*;

public class Lienzo extends JPanel{
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
}
