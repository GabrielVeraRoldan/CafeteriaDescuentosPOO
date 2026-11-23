import vista.VentanaCafeteria;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaCafeteria ventana = new VentanaCafeteria();
            ventana.setVisible(true);
        });
    }
}