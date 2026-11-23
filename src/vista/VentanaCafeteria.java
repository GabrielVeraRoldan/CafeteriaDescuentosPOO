package vista;

import modelo.Pedido;
import modelo.PedidoDocente;
import modelo.PedidoEstudiante;
import modelo.PedidoGeneral;
import persistencia.ConexionBD;
import servicio.GestorDescuentos;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Interfaz gráfica de usuario (Swing) para la gestión de descuentos de la cafetería.
 * Integra JTextField, JButton, JTextArea y ActionListener con la capa funcional y JDBC.
 */
public class VentanaCafeteria extends JFrame {

    private JTextField txtCliente;
    private JTextField txtMonto;
    private JComboBox<String> cmbTipoCliente;
    private JButton btnCalcular;
    private JButton btnExportarCSV;
    private JButton btnLimpiar;
    private JTextArea txtResultado;

    private final GestorDescuentos gestorDescuentos;
    private final ConexionBD conexionBD;

    public VentanaCafeteria() {
        this.gestorDescuentos = new GestorDescuentos();
        this.conexionBD = new ConexionBD();

        configurarVentana();
        inicializarComponentes();
        iniciarConexionBD();
    }

    private void configurarVentana() {
        setTitle("Sistema de Descuentos - Cafetería Universitaria");
        setSize(580, 490);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // Panel superior con campos de entrada (JTextField) y botones (JButton)
        JPanel panelSuperior = new JPanel(new BorderLayout(8, 8));
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Registro y Cálculo de Pedido"));

        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 8, 8));
        panelCampos.add(new JLabel("Nombre del Cliente:"));
        txtCliente = new JTextField();
        panelCampos.add(txtCliente);

        panelCampos.add(new JLabel("Categoría del Cliente:"));
        cmbTipoCliente = new JComboBox<>(new String[]{"Estudiante", "Docente", "Cliente General"});
        panelCampos.add(cmbTipoCliente);

        panelCampos.add(new JLabel("Monto del Pedido ($):"));
        txtMonto = new JTextField();
        panelCampos.add(txtMonto);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnCalcular = new JButton("Calcular y Guardar");
        btnExportarCSV = new JButton("Exportar a CSV");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnCalcular);
        panelBotones.add(btnExportarCSV);
        panelBotones.add(btnLimpiar);

        panelSuperior.add(panelCampos, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        // Área de salida de resultados (JTextArea)
        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(txtResultado);
        scroll.setBorder(BorderFactory.createTitledBorder("Detalle de Descuentos y Estado de Persistencia"));
        add(scroll, BorderLayout.CENTER);

        // ActionListener principal: conecta el monto ingresado con la lógica Lambda y JDBC
        btnCalcular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarYGuardarPedido();
            }
        });

        // ActionListener para exportar el historial a CSV
        btnExportarCSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String resultadoCSV = conexionBD.exportarACSV("pedidos_cafeteria.csv");
                txtResultado.append("[CSV] " + resultadoCSV + "\n");
                txtResultado.append("----------------------------------------------------------\n");
                JOptionPane.showMessageDialog(VentanaCafeteria.this, resultadoCSV,
                        "Exportación CSV", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // ActionListener para limpiar formulario
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtCliente.setText("");
                txtMonto.setText("");
                cmbTipoCliente.setSelectedIndex(0);
                txtCliente.requestFocus();
            }
        });
    }

    private void iniciarConexionBD() {
        String estadoInicial = conexionBD.verificarYCrearTabla();
        txtResultado.append("[INICIO JDBC] " + estadoInicial + "\n");
        txtResultado.append("----------------------------------------------------------\n");
    }

    private void procesarYGuardarPedido() {
        try {
            String nombre = txtCliente.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese el nombre del cliente.",
                        "Dato requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double monto = Double.parseDouble(txtMonto.getText().trim());
            String categoria = (String) cmbTipoCliente.getSelectedItem();

            // Polimorfismo: instanciación según la categoría del cliente
            Pedido pedido;
            if ("Estudiante".equals(categoria)) {
                pedido = new PedidoEstudiante(nombre, monto);
            } else if ("Docente".equals(categoria)) {
                pedido = new PedidoDocente(nombre, monto);
            } else {
                pedido = new PedidoGeneral(nombre, monto);
            }

            // Aplicación de la expresión Lambda mediante GestorDescuentos
            gestorDescuentos.procesarPedido(pedido);

            // Persistencia en base de datos SQLite via JDBC
            String estadoBD = conexionBD.guardarPedido(pedido);

            // Despliegue claro en el JTextArea
            txtResultado.append(String.format(
                    "Cliente: %s | Categoría: %s\n"
                    + "Monto Base:         $%,.2f\n"
                    + "Descuento Aplicado: -$%,.2f\n"
                    + "Total a Pagar:      $%,.2f\n"
                    + "Base de Datos:      %s\n"
                    + "----------------------------------------------------------\n",
                    pedido.getCliente(),
                    pedido.getTipoCliente(),
                    pedido.getMontoBase(),
                    pedido.getDescuentoAplicado(),
                    pedido.getTotalPagar(),
                    estadoBD
            ));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un valor numérico válido en el monto.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validación de negocio", JOptionPane.WARNING_MESSAGE);
        }
    }
}