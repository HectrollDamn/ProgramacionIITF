package controlador;

import java.io.*;
import modelo.Proyecto;

public class AdministradorFicheros {

    public static boolean guardarProyecto(File archivo, Proyecto proyecto) {
        try (FileOutputStream fos = new FileOutputStream(archivo);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(proyecto);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Proyecto cargarProyecto(File archivo) {
        try (FileInputStream fis = new FileInputStream(archivo);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object obj = ois.readObject();
            if (obj instanceof Proyecto) {
                return (Proyecto) obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}