package modelo;

/**
 * Subclase que representa el pedido de un docente (10% de descuento base).
 */
public class PedidoDocente extends Pedido {

    public PedidoDocente(String cliente, double montoBase) {
        super(cliente, montoBase);
    }

    @Override
    public String getTipoCliente() {
        return "Docente";
    }

    @Override
    public double getPorcentajeBase() {
        return 0.10;
    }
}