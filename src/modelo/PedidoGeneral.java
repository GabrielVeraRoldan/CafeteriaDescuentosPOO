package modelo;

/**
 * Subclase que representa el pedido de un cliente externo o general (5% de descuento base).
 */
public class PedidoGeneral extends Pedido {

    public PedidoGeneral(String cliente, double montoBase) {
        super(cliente, montoBase);
    }

    @Override
    public String getTipoCliente() {
        return "Cliente General";
    }

    @Override
    public double getPorcentajeBase() {
        return 0.05;
    }
}
