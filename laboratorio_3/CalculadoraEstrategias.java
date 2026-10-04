import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;

/**
 * Laboratorio 3: Calculadora con patron estrategia usando interfaces funcionales.
 * Asignatura: Programacion 1
 */
public class CalculadoraEstrategias {

    // Paso 1: Mapa que asocia cada simbolo de operacion con su respectiva funcion
    // Se usa LinkedHashMap para mantener el orden exacto de insercion de las operaciones
    static final Map<String, DoubleBinaryOperator> OPERACIONES = new LinkedHashMap<>();

    static {
        // Operaciones usando referencias a metodos donde es posible
        OPERACIONES.put("+", Double::sum);
        OPERACIONES.put("-", (a, b) -> a - b);
        OPERACIONES.put("*", (a, b) -> a * b);

        // Paso 2: La division valida que el divisor no sea cero
        OPERACIONES.put("/", (a, b) -> {
            if (b == 0) {
                throw new ArithmeticException("división por cero");
            }
            return a / b;
        });

        OPERACIONES.put("^", Math::pow);
        OPERACIONES.put("max", Math::max);
    }

    /**
     * Paso 3: Metodo para evaluar una expresion en texto en formato "operando1 operador operando2".
     * Separa el texto por espacios en blanco, busca la funcion en el mapa y la ejecuta.
     */
    static double evaluar(String expresion) {
        String[] partes = expresion.strip().split("\\s+");

        if (partes.length != 3) {
            throw new IllegalArgumentException("formato esperado: a op b");
        }

        String simboloOperador = partes[1];
        DoubleBinaryOperator operacion = OPERACIONES.get(simboloOperador);

        if (operacion == null) {
            throw new IllegalArgumentException("operador desconocido: " + simboloOperador);
        }

        double operando1 = Double.parseDouble(partes[0]);
        double operando2 = Double.parseDouble(partes[2]);

        return operacion.applyAsDouble(operando1, operando2);
    }

    public static void main(String[] args) {
        System.out.println("=== Salida Oficial del Laboratorio 3 ===");

        // Paso 4: Evaluar las expresiones de prueba capturando las excepciones esperadas
        List<String> expresiones = List.of(
                "12 + 30",
                "2 ^ 10",
                "7 / 2",
                "9 max 4",
                "5 / 0",
                "3 % 2"
        );

        for (String exp : expresiones) {
            try {
                double resultado = evaluar(exp);
                System.out.printf("%-8s = %s%n", exp, resultado);
            } catch (RuntimeException ex) {
                System.out.printf("%-8s -> error: %s%n", exp, ex.getMessage());
            }
        }

        // Mostrar los operadores registrados en el mapa
        System.out.println("Operadores disponibles: " + OPERACIONES.keySet());
    }
}
