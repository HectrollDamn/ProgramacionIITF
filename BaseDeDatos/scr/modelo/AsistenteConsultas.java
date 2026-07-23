package modelo;

import java.util.List;

/**
 * Se encarga puramente de generar los strings explicativos de SQL básico.
 */
public class AsistenteConsultas {

    public static String generarSelectBasico(Proyecto proyecto) {
        List<Tabla> tablas = proyecto.getTablas();
        if (tablas.isEmpty()) {
            return "-- [ERROR] No hay tablas en el modelo.";
        }
        Tabla primera = tablas.get(0);
        String col = primera.getColumnas().isEmpty() ? "id" : primera.getColumnas().get(0).getNombre();
        return "-- ==========================================================\n" +
               "-- CONSULTA TRADUCIDA EN ESPANOL: LISTAR DATOS BASICOS\n" +
               "SELECT " + col + " \nFROM " + primera.getNombre() + ";";
    }

    public static String generarWhereFiltro(Tabla tabla, String valorBusqueda) {
        String colName = tabla.getColumnas().isEmpty() ? "id" : tabla.getColumnas().get(0).getNombre();
        return "-- ==========================================================\n" +
               "-- CONSULTA TRADUCIDA EN ESPANOL: BUSCAR POR FILTRO CONDICIONAL\n" +
               "SELECT * \nFROM " + tabla.getNombre() + " \nWHERE " + colName + " = " + valorBusqueda + ";";
    }

    public static String generarInnerJoin(Proyecto proyecto) {
        List<Relacion> relaciones = proyecto.getRelaciones();
        if (relaciones.isEmpty()) {
            return "-- [ERROR] Vincula dos tablas con una linea verde primero para armar el JOIN.";
        }
        Relacion rel = relaciones.get(0);
        return "-- ==========================================================\n" +
               "-- CONSULTA RELACIONAL AUTOMATICA TRADUCIDA (INNER JOIN)\n" +
               "SELECT * \nFROM " + rel.getTablaOrigen().getNombre() + " \nINNER JOIN " + rel.getTablaDestino().getNombre() + ";";
    }
}