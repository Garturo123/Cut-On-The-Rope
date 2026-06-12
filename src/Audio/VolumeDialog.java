
package Audio;

import javax.swing.*;
import java.awt.*;

public class VolumeDialog {
    
    /**
     * Muestra un diálogo modal para ajustar volumen
     * @param parent Ventana padre
     * @param titulo Título del diálogo
     * @param etiqueta Etiqueta del slider (ej: "SFX Volume")
     * @param valorInicial Valor inicial del volumen (0-100)
     * @param callback Callback que se ejecuta al cambiar el volumen
     */
    public static void mostrar(JFrame parent, String titulo, String etiqueta, 
                                int valorInicial, VolumeCallback callback) {
        JDialog dialog = new JDialog(parent, titulo, true);
        dialog.setSize(320, 140);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setResizable(false);
        
        JLabel label = new JLabel(etiqueta + ": " + valorInicial + "%", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JSlider slider = new JSlider(0, 100, valorInicial);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(false);
        
        JLabel valorLabel = new JLabel("Valor actual: " + valorInicial + "%", SwingConstants.CENTER);
        valorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valorLabel.setForeground(Color.GRAY);
        
        slider.addChangeListener(e -> {
            if (slider.getValueIsAdjusting()) {
                valorLabel.setText("Valor actual: " + slider.getValue() + "%");
            } else {
                int nuevoValor = slider.getValue();
                label.setText(etiqueta + ": " + nuevoValor + "%");
                valorLabel.setText("Valor actual: " + nuevoValor + "%");
                callback.onVolumeChanged(nuevoValor);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(e -> dialog.dispose());
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnOK);
        buttonPanel.add(btnCancelar);
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        centerPanel.add(slider, BorderLayout.CENTER);
        centerPanel.add(valorLabel, BorderLayout.SOUTH);
        
        dialog.add(label, BorderLayout.NORTH);
        dialog.add(centerPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    public static void mostrarSimple(JFrame parent, String titulo, String etiqueta, int valorInicial) {
        mostrar(parent, titulo, etiqueta, valorInicial, nuevoVolumen -> {});
    }
    
    @FunctionalInterface
    public interface VolumeCallback {
        void onVolumeChanged(int nuevoVolumen);
    }
}