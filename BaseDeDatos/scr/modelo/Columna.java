package modelo;

import java.io.Serializable;

/**
 * Representa una columna o atributo dentro de una tabla de base de datos relacional.
 */
public class Columna implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String tipoDato; // VARCHAR, INT, DATE, etc.
    private int longitud;     // Para tipos como VARCHAR(50)
    private boolean esClavePrimaria;
    private boolean esClaveForanea;
    private boolean noNulo;

    public Columna(String nombre, String tipoDato, int longitud, boolean esClavePrimaria, boolean noNulo) {
        this.nombre = nombre;
        this.tipoDato = tipoDato.toUpperCase();
        this.longitud = longitud;
        this.esClavePrimaria = esClavePrimaria;
        this.esClaveForanea = false; 
        this.noNulo = noNulo;
    }

    public String generarCodigoSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombre).append(" ").append(tipoDato);
        
        if (longitud > 0 && (tipoDato.equals("VARCHAR") || tipoDato.equals("CHAR"))) {
            sb.append("(").append(longitud).append(")");
        }
        if (noNulo && !esClavePrimaria) {
            sb.append(" NOT NULL");
        }
        if (esClavePrimaria) {
            sb.append(" PRIMARY KEY");
        }
        return sb.toString();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTipoDato() { return tipoDato; }
    public void setTipoDato(String tipoDato) { this.tipoDato = tipoDato.toUpperCase(); }
    public int getLongitud() { return longitud; }
    public void setLongitud(int longitud) { this.longitud = longitud; }
    public boolean isEsClavePrimaria() { return esClavePrimaria; }
    public void setEsClavePrimaria(boolean esClavePrimaria) { this.esClavePrimaria = esClavePrimaria; }
    public boolean isEsClaveForanea() { return esClaveForanea; }
    public void setEsClaveForanea(boolean esClaveForanea) { this.esClaveForanea = esClaveForanea; }
    public boolean isNoNulo() { return noNulo; }
    public void setNoNulo(boolean noNulo) { this.noNulo = noNulo; }

    @Override
    public String toString() {
        return nombre + " : " + tipoDato + (longitud > 0 ? "(" + longitud + ")" : "") 
                + (esClavePrimaria ? " [PK]" : "") + (esClaveForanea ? " [FK]" : "");
    }
}