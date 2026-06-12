package Audio;

import javax.swing.*;
import java.awt.*;

public class Volumen {
    
    public static void mostrar(JFrame parent, String titulo, String etiqueta, int valorInicial, VolumeCallback callback) {
        JDialog dialog = new JDialog(parent, titulo, true);
        dialog.setSize(320, 120);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        
        JLabel label = new JLabel(etiqueta + ": " + valorInicial + "%", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        
        JSlider slider = new JSlider(0, 100, valorInicial);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        
        slider.addChangeListener(e -> {
            int valor = slider.getValue();
            label.setText(etiqueta + ": " + valor + "%");
            callback.onVolumeChanged(valor);
        });
        
        dialog.add(label, BorderLayout.NORTH);
        dialog.add(slider, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    @FunctionalInterface
    public interface VolumeCallback {
        void onVolumeChanged(int nuevoVolumen);
    }
}
