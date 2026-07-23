package controlador;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import modelo.Proyecto;

public class GestorHilosUI {

    private Proyecto proyecto;
    private File archivoActual; 
    
    private JLabel lblContadorTiempo;
    private JProgressBar progressBar;
    
    private volatile boolean ejecutando = true;
    private final int SEGUNDOS_INTERVALO = 30;

    public GestorHilosUI(Proyecto proyecto, JLabel lblContadorTiempo, JProgressBar progressBar) {
        this.proyecto = proyecto;
        this.lblContadorTiempo = lblContadorTiempo;
        this.progressBar = progressBar;
        
        if (this.progressBar != null) {
            this.progressBar.setMaximum(SEGUNDOS_INTERVALO);
            this.progressBar.setValue(0);
            this.progressBar.setString("Pausado (Sin archivo)");
        }
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public void setArchivoActual(File archivoActual) {
        this.archivoActual = archivoActual;
    }

    public void iniciarHilos() {
        iniciarHiloContador();
        iniciarHiloAutoguardado();
    }

    public void detenerHilos() {
        this.ejecutando = false;
    }

    private void iniciarHiloContador() {
        Thread hilo = new Thread(() -> {
            int segundosTotales = 0;
            while (ejecutando) {
                try {
                    Thread.sleep(1000);
                    segundosTotales++;

                    int mins = segundosTotales / 60;
                    int segs = segundosTotales % 60;
                    String tiempoStr = String.format(" Sesion: %02d:%02d ", mins, segs);

                    if (lblContadorTiempo != null) {
                        SwingUtilities.invokeLater(() -> lblContadorTiempo.setText(tiempoStr));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    private void iniciarHiloAutoguardado() {
        Thread hilo = new Thread(() -> {
            int segundosTranscurridos = 0;

            while (ejecutando) {
                try {
                    Thread.sleep(1000);

                    if (archivoActual == null) {
                        segundosTranscurridos = 0;
                        if (progressBar != null) {
                            SwingUtilities.invokeLater(() -> {
                                progressBar.setValue(0);
                                progressBar.setString("Pausado (Sin archivo)");
                            });
                        }
                        continue;
                    }

                    segundosTranscurridos++;
                    final int segs = segundosTranscurridos;

                    if (progressBar != null) {
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue(segs);
                            progressBar.setString("Autoguardado en: " + (SEGUNDOS_INTERVALO - segs) + "s");
                        });
                    }

                    if (segundosTranscurridos >= SEGUNDOS_INTERVALO) {
                        segundosTranscurridos = 0;

                        if (progressBar != null) {
                            SwingUtilities.invokeLater(() -> progressBar.setString("Guardando..."));
                        }

                        boolean ok = AdministradorFicheros.guardarProyecto(archivoActual, proyecto);

                        if (progressBar != null) {
                            String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                            String msg = ok ? " Guardado (" + hora + ")" : " Error al guardar";
                            SwingUtilities.invokeLater(() -> {
                                progressBar.setValue(0);
                                progressBar.setString(msg);
                            });
                        }
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        hilo.setDaemon(true);
        hilo.start();
    }
}