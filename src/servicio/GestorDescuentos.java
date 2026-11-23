package servicio;

import modelo.Pedido;

/**
 * Capa de servicio encargada de aplicar la lógica funcional (expresiones Lambda)
 * para calcular el descuento y el total a pagar de cualquier tipo de pedido.
 */
public class GestorDescuentos {

    // Expresión Lambda 1: Descuento estándar según el porcentaje base del tipo de cliente
    private final CalculadoraDescuento descuentoEstandar = (monto, pct) -> monto * pct;

    // Expresión Lambda 2: Descuento con bonificación adicional del 5% para compras >= $50,000
    private final CalculadoraDescuento descuentoPorVolumen = (monto, pct) ->
            (monto >= 50000) ? (monto * (pct + 0.05)) : (monto * pct);

    /**
     * Procesa el pedido seleccionando dinámicamente la expresión Lambda correspondiente
     * y actualiza el descuento aplicado y el total final.
     */
    public void procesarPedido(Pedido pedido) {
        CalculadoraDescuento estrategia = (pedido.getMontoBase() >= 50000)
                ? descuentoPorVolumen
                : descuentoEstandar;

        double descuento = estrategia.calcular(pedido.getMontoBase(), pedido.getPorcentajeBase());
        double total = pedido.getMontoBase() - descuento;

        pedido.setDescuentoAplicado(descuento);
        pedido.setTotalPagar(total);
    }
}
