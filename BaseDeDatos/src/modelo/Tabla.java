package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una tabla dentro del modelo de datos.
 */
public class Tabla implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private List<Columna> columnas;
    private int x;
    private int y;
    private int ancho;
    private int alto;

    public Tabla(String nombre, int x, int y) {
        this.nombre = nombre.toLowerCase();
        this.columnas = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.ancho = 160; 
        this.alto = 120;
    }

    public void agregarColumna(Columna columna) {
        this.columnas.add(columna);
    }

    public void eliminarColumna(Columna columna) {
        this.columnas.remove(columna);
    }

    public String generarCodigoSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(nombre).append(" (\n");
        
        for (int i = 0; i < columnas.size(); i++) {
            sb.append("    ").append(columnas.get(i).generarCodigoSQL());
            if (i < columnas.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(");");
        return sb.toString();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre.toLowerCase(); }
    public List<Columna> getColumnas() { return columnas; }
    public void setColumnas(List<Columna> columnas) { this.columnas = columnas; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
}