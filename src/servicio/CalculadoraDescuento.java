package servicio;

/**
 * Interfaz funcional (@FunctionalInterface) que define el contrato
 * para calcular el descuento mediante expresiones Lambda.
 */
@FunctionalInterface
public interface CalculadoraDescuento {
    double calcular(double montoBase, double porcentaje);
}
