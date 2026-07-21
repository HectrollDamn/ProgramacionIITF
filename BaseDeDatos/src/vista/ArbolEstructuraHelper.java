package vista;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import modelo.*;

public class ArbolEstructuraHelper {
    public static void rellenarArbol(DefaultMutableTreeNode nodoRaiz, DefaultTreeModel modeloArbol, JTree arbol, Proyecto proyecto) {
        if (nodoRaiz == null || modeloArbol == null) return;
        
        nodoRaiz.removeAllChildren();
        nodoRaiz.setUserObject("Base de Datos: " + proyecto.getNombre());

        DefaultMutableTreeNode nodoTablas = new DefaultMutableTreeNode("Tablas (" + proyecto.getTablas().size() + ")");
        for (Tabla t : proyecto.getTablas()) {
            DefaultMutableTreeNode nodoT = new DefaultMutableTreeNode(t.getNombre());
            for (Columna c : t.getColumnas()) {
                String tag = c.isEsClavePrimaria() ? "[PK] " : (c.isEsClaveForanea() ? "[FK] " : "");
                nodoT.add(new DefaultMutableTreeNode(tag + c.getNombre() + " (" + c.getTipoDato() + ")"));
            }
            nodoTablas.add(nodoT);
        }
        nodoRaiz.add(nodoTablas);

        DefaultMutableTreeNode nodoRelaciones = new DefaultMutableTreeNode("Relaciones (" + proyecto.getRelaciones().size() + ")");
        for (Relacion r : proyecto.getRelaciones()) {
            nodoRelaciones.add(new DefaultMutableTreeNode(r.getNombreRestriccion() + " (" + r.getCardinalidadOrigen() + ":" + r.getCardinalidadDestino() + ")"));
        }
        nodoRaiz.add(nodoRelaciones);
        
        modeloArbol.reload();
        for (int i = 0; i < arbol.getRowCount(); i++) arbol.expandRow(i);
    }
}
