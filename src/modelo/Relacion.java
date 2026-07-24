package modelo;

import java.io.Serializable;

public class Relacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreRestriccion;
    private Tabla tablaOrigen;
    private Columna columnaOrigen;
    private Tabla tablaDestino;
    private Columna columnaDestino;
    private String cardinalidadOrigen;
    private String cardinalidadDestino;

    public Relacion(String nombreRestriccion, Tabla tablaOrigen, Columna columnaOrigen, Tabla tablaDestino, Columna columnaDestino, String cardinalidadOrigen, String cardinalidadDestino) {
        this.nombreRestriccion = nombreRestriccion.toLowerCase();
        this.tablaOrigen = tablaOrigen;
        this.columnaOrigen = columnaOrigen;
        this.tablaDestino = tablaDestino;
        this.columnaDestino = columnaDestino;
        this.cardinalidadOrigen = cardinalidadOrigen;
        this.cardinalidadDestino = cardinalidadDestino;
        
        this.columnaDestino.setEsClaveForanea(true);
    }

    public String generarCodigoSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(tablaDestino.getNombre()).append("\n")
          .append("ADD CONSTRAINT ").append(nombreRestriccion).append("\n")
          .append("FOREIGN KEY (").append(columnaDestino.getNombre()).append(")\n")
          .append("REFERENCES ").append(tablaOrigen.getNombre()).append("(").append(columnaOrigen.getNombre()).append(");");
        return sb.toString();
    }

    public String getNombreRestriccion() { return nombreRestriccion; }
    public Tabla getTablaOrigen() { return tablaOrigen; }
    public Columna getColumnaOrigen() { return columnaOrigen; }
    public Tabla getTablaDestino() { return tablaDestino; }
    public Columna getColumnaDestino() { return columnaDestino; }
    public String getCardinalidadOrigen() { return cardinalidadOrigen; }
    public String getCardinalidadDestino() { return cardinalidadDestino; }
}