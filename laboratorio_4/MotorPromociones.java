import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.BiConsumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;

/**
 * Laboratorio 4 (Proyecto Integrador): Motor de promociones del catalogo.
 * Asignatura: Programacion 1
 */
public class MotorPromociones {

    /**
     * Paso 1: Record Promocion.
     * Modela una promocion con su nombre, la condicion que debe cumplir el producto (aplicaA)
     * y la funcion de transformacion de precio (ajuste).
     */
    public record Promocion(String nombre, Predicate<Producto> aplicaA, DoubleUnaryOperator ajuste) {
        /**
         * Calcula el precio del producto aplicando la promocion si cumple el criterio;
         * si no aplica, devuelve el precio original sin modificaciones.
         */
        public double precioCon(Producto p) {
            return aplicaA.test(p) ? ajuste.applyAsDouble(p.precio()) : p.precio();
        }
    }

    /**
     * Paso 2: Fabrica de ajuste por porcentaje de descuento.
     */
    static DoubleUnaryOperator porcentaje(double pct) {
        return precio -> precio * (1.0 - pct / 100.0);
    }

    /**
     * Paso 2: Fabrica de ajuste por monto fijo de descuento (evitando valores negativos).
     */
    static DoubleUnaryOperator montoFijo(double monto) {
        return precio -> Math.max(0.0, precio - monto);
    }

    public static void main(String[] args) {
        System.out.println("=== Salida Oficial del Laboratorio 4 ===");

        // Paso 3: Definir las tres promociones de la tienda
        List<Promocion> promociones = List.of(
                new Promocion("Tecno 10 %", p -> p.categoria().equals("tecnología"), porcentaje(10)),
                new Promocion("Bono $50.000", p -> p.precio() >= 200_000, montoFijo(50_000)),
                new Promocion("Liquidación", p -> p.stock() > 100, porcentaje(30))
        );

        // Paso 4: Supplier para la opcion "Sin promocion" con ajuste identidad
        Supplier<Promocion> sinPromocion =
                () -> new Promocion("Sin promoción", p -> true, DoubleUnaryOperator.identity());

        // Paso 6: BiConsumer para imprimir la fila de cada producto con formato
        BiConsumer<Producto, Promocion> imprimir = (p, promo) -> System.out.printf(
                "%-17s %-14s $%,11.0f -> $%,11.0f%n",
                p.nombre(), promo.nombre(), p.precio(), promo.precioCon(p)
        );

        // ToDoubleBiFunction para calcular la diferencia de precio (el ahorro)
        ToDoubleBiFunction<Producto, Promocion> ahorro =
                (p, promo) -> p.precio() - promo.precioCon(p);

        double ahorroTotal = 0.0;

        // Recorremos el catalogo de productos de muestra
        for (Producto p : Catalogo.muestra()) {
            // Si el producto no esta disponible (stock == 0), lo saltamos
            if (!p.disponible()) {
                continue;
            }

            // Paso 5: Elegir la mejor promocion (la que deje el menor precio final para el cliente)
            BinaryOperator<Promocion> masConveniente =
                    BinaryOperator.minBy(Comparator.comparingDouble(promo -> promo.precioCon(p)));

            // Comenzamos asumiendo que no tiene promocion aplicada
            Promocion mejor = sinPromocion.get();

            // Reducimos la lista de promociones para encontrar la mas favorable
            for (Promocion promo : promociones) {
                mejor = masConveniente.apply(mejor, promo);
            }

            // Imprimimos la fila del producto
            imprimir.accept(p, mejor);

            // Acumulamos el ahorro obtenido
            ahorroTotal += ahorro.applyAsDouble(p, mejor);
        }

        // Mostramos el total ahorrado por el cliente
        System.out.printf("Ahorro total para el cliente: $%,.0f%n", ahorroTotal);
    }
}
