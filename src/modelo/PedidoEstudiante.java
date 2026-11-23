package modelo;

/**
 * Subclase que representa el pedido de un estudiante (15% de descuento base).
 */
public class PedidoEstudiante extends Pedido {

    public PedidoEstudiante(String cliente, double montoBase) {
        super(cliente, montoBase);
    }

    @Override
    public String getTipoCliente() {
        return "Estudiante";
    }

    @Override
    public double getPorcentajeBase() {
        return 0.15;
    }
}
