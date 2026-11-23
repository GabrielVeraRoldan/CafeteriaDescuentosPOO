package modelo;

/**
 * Clase abstracta base que representa un pedido genérico en la cafetería.
 * Aplica los principios SOLID de Responsabilidad Única (SRP) y Abierto/Cerrado (OCP),
 * permitiendo extender nuevos tipos de clientes sin modificar la lógica existente.
 */
public abstract class Pedido {
    private String cliente;
    private double montoBase;
    private double descuentoAplicado;
    private double totalPagar;

    public Pedido(String cliente, double montoBase) {
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        if (montoBase <= 0) {
            throw new IllegalArgumentException("El monto del pedido debe ser mayor a cero.");
        }
        this.cliente = cliente.trim();
        this.montoBase = montoBase;
    }

    // Métodos abstractos polimórficos que cada subclase implementa según su perfil
    public abstract String getTipoCliente();
    public abstract double getPorcentajeBase();

    // Getters y Setters (Encapsulamiento)
    public String getCliente() {
        return cliente;
    }

    public double getMontoBase() {
        return montoBase;
    }

    public double getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public void setDescuentoAplicado(double descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }

    public double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
    }
}