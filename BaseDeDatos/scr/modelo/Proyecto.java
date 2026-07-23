package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contenedor global que agrupa tablas, relaciones y centraliza el script completo.
 */
public class Proyecto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private List<Tabla> tablas;
    private List<Relacion> relaciones;

    public Proyecto(String nombre) {
        this.nombre = nombre;
        this.tablas = new ArrayList<>();
        this.relaciones = new ArrayList<>();
    }

    public void agregarTabla(Tabla tabla) { this.tablas.add(tabla); }

    public void eliminarTabla(Tabla tabla) {
        this.tablas.remove(tabla);
        relaciones.removeIf(r -> r.getTablaOrigen().equals(tabla) || r.getTablaDestino().equals(tabla));
    }

    public void agregarRelacion(Relacion relacion) { this.relaciones.add(relacion); }

    public void eliminarRelacion(Relacion relacion) {
        relacion.getColumnaDestino().setEsClaveForanea(false);
        this.relaciones.remove(relacion);
    }

    public String generarScriptSQLCompleto() {
        StringBuilder script = new StringBuilder();
        script.append("-- =============================================\n")
              .append("-- SCRIPT GENERADO AUTOMÁTICAMENTE\n")
              .append("-- Proyecto: ").append(nombre).append("\n")
              .append("-- =============================================\n\n");

        script.append("-- --- CREACIÓN DE TABLAS ---\n");
        for (Tabla tabla : tablas) {
            script.append(tabla.generarCodigoSQL()).append("\n\n");
        }

        if (!relaciones.isEmpty()) {
            script.append("-- --- RESTRICCIONES (CLAVES FORÁNEAS) ---\n");
            for (Relacion relacion : relaciones) {
                script.append(relacion.generarCodigoSQL()).append("\n\n");
            }
        }
        return script.toString();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Tabla> getTablas() { return tablas; }
    public List<Relacion> getRelaciones() { return relaciones; }
}