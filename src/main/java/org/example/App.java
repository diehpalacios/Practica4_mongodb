package org.example;


import util.MongoConnection;
import vista.MainWindow;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // 1. Aplicar el aspecto visual nativo del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Si falla, Swing utilizará el tema por defecto sin interrumpir la ejecución
        }

        // 2. Registrar cierre limpio de la conexión con MongoDB al salir de la aplicación
        Runtime.getRuntime().addShutdownHook(new Thread(MongoConnection::closeConnection));

        // 3. Iniciar la interfaz gráfica en el hilo de eventos de Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Verificar que la conexión con el servidor MongoDB responde antes de abrir la ventana
                if (MongoConnection.getDatabase() == null) {
                    return;
                }

                MainWindow window = new MainWindow();
                window.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error crítico al iniciar la aplicación: " + e.getMessage(),
                        "Error de Arranque",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}