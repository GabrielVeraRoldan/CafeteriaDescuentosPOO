package persistencia;

import modelo.Pedido;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Capa de persistencia encargada de la conexión JDBC con SQLite,
 * el registro de pedidos procesados y la exportación a formato CSV.
 */
public class ConexionBD {

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String URL = "jdbc:sqlite:cafeteria_pedidos.db";

    /**
     * Intenta establecer la conexión JDBC al iniciar la aplicación y crea
     * la tabla historial_pedidos si aún no existe.
     */
    public String verificarYCrearTabla() {
        String sqlTabla = "CREATE TABLE IF NOT EXISTS historial_pedidos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "cliente TEXT NOT NULL,"
                + "tipo_cliente TEXT NOT NULL,"
                + "monto_base REAL NOT NULL,"
                + "descuento REAL NOT NULL,"
                + "total_pagar REAL NOT NULL,"
                + "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try {
            // Carga explícita del controlador JDBC
            Class.forName(DRIVER);

            // Uso de try-with-resources para garantizar el cierre seguro de Connection y Statement
            try (Connection conn = DriverManager.getConnection(URL);
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sqlTabla);
                return "Conexión JDBC exitosa: Base de datos SQLite operativa.";
            }
        } catch (ClassNotFoundException e) {
            return "Error crítico JDBC: No se encontró el driver (" + e.getMessage() + ").";
        } catch (SQLException e) {
            return "Error SQLException al inicializar la base de datos: " + e.getMessage();
        }
    }

    /**
     * Inserta un pedido procesado en la base de datos utilizando PreparedStatement
     * y try-with-resources.
     */
    public String guardarPedido(Pedido pedido) {
        String sqlInsert = "INSERT INTO historial_pedidos (cliente, tipo_cliente, monto_base, descuento, total_pagar) "
                + "VALUES (?, ?, ?, ?, ?)";

        try {
            Class.forName(DRIVER);

            try (Connection conn = DriverManager.getConnection(URL);
                 PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

                pstmt.setString(1, pedido.getCliente());
                pstmt.setString(2, pedido.getTipoCliente());
                pstmt.setDouble(3, pedido.getMontoBase());
                pstmt.setDouble(4, pedido.getDescuentoAplicado());
                pstmt.setDouble(5, pedido.getTotalPagar());
                pstmt.executeUpdate();

                return "Pedido almacenado correctamente en SQLite.";
            }
        } catch (ClassNotFoundException e) {
            return "Error ClassNotFoundException al guardar: " + e.getMessage();
        } catch (SQLException e) {
            return "Error SQLException al guardar el pedido: " + e.getMessage();
        }
    }

    /**
     * Exporta todos los registros almacenados en la base de datos a un archivo CSV
     * para cumplir con los entregables de la actividad.
     */
    public String exportarACSV(String rutaArchivo) {
        String sqlSelect = "SELECT id, cliente, tipo_cliente, monto_base, descuento, total_pagar, fecha FROM historial_pedidos";

        try {
            Class.forName(DRIVER);

            try (Connection conn = DriverManager.getConnection(URL);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlSelect);
                 PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {

                writer.println("ID,Cliente,Tipo_Cliente,Monto_Base,Descuento,Total_Pagar,Fecha");

                while (rs.next()) {
                    writer.printf("%d,\"%s\",\"%s\",%.2f,%.2f,%.2f,%s%n",
                            rs.getInt("id"),
                            rs.getString("cliente"),
                            rs.getString("tipo_cliente"),
                            rs.getDouble("monto_base"),
                            rs.getDouble("descuento"),
                            rs.getDouble("total_pagar"),
                            rs.getString("fecha"));
                }
                return "Archivo CSV exportado exitosamente en: " + rutaArchivo;
            }
        } catch (ClassNotFoundException e) {
            return "Error de Driver al exportar CSV: " + e.getMessage();
        } catch (SQLException e) {
            return "Error SQL al leer registros para CSV: " + e.getMessage();
        } catch (IOException e) {
            return "Error de escritura al generar el archivo CSV: " + e.getMessage();
        }
    }
}