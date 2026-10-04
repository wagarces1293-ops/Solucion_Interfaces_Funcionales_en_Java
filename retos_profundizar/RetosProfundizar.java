import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Solucion a los 4 retos para profundizar propuestos en la pagina 33 de la guia.
 * Asignatura: Programacion 1
 */
public class RetosProfundizar {

    // =========================================================================
    // Reto 1: TriFunction con curry()
    // =========================================================================
    @FunctionalInterface
    public interface TriFunction<A, B, C, R> {
        R aplicar(A a, B b, C c);

        /**
         * Transforma una funcion de tres parametros en una secuencia de funciones
         * encadenadas de un solo parametro (currificacion).
         */
        default Function<A, Function<B, Function<C, R>>> curry() {
            return a -> b -> c -> aplicar(a, b, c);
        }
    }

    // =========================================================================
    // Reto 2: Metodos o(...) y cuando(...) en Regla<T>
    // =========================================================================
    @FunctionalInterface
    public interface ReglaAvanzada<T> {
        List<String> validar(T valor);

        /**
         * Operador logico "O" (disyuncion):
         * Si cualquiera de las dos reglas se cumple (es decir, devuelve lista de errores vacia),
         * el dato se considera valido. Si ambas fallan, se reportan los errores combinados.
         */
        default ReglaAvanzada<T> o(ReglaAvanzada<? super T> otra) {
            return valor -> {
                List<String> errores1 = validar(valor);
                if (errores1.isEmpty()) {
                    return List.of();
                }
                List<String> errores2 = otra.validar(valor);
                if (errores2.isEmpty()) {
                    return List.of();
                }
                List<String> ambosErrores = new ArrayList<>(errores1);
                ambosErrores.addAll(errores2);
                return ambosErrores;
            };
        }

        /**
         * Aplica la regla solo si se cumple una condicion previa (Predicate).
         * Si la condicion no se cumple, la regla se ignora (se considera valido / sin errores).
         */
        default ReglaAvanzada<T> cuando(Predicate<T> condicion) {
            return valor -> condicion.test(valor) ? validar(valor) : List.of();
        }
    }

    // =========================================================================
    // Reto 3: Memorizacion segura para hilos con ConcurrentHashMap
    // =========================================================================
    public static <T, R> Function<T, R> memorizarSeguro(Function<T, R> funcion) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return entrada -> cache.computeIfAbsent(entrada, funcion);
    }

    // =========================================================================
    // Reto 4: Mecanismo de reintento Reintentar.de(Supplier<T>, int)
    // =========================================================================
    public static class Reintentar {
        public static <T> T de(Supplier<T> proveedor, int intentos) {
            int restantes = intentos;
            RuntimeException ultimoError = null;

            while (restantes > 0) {
                try {
                    return proveedor.get();
                } catch (RuntimeException ex) {
                    restantes--;
                    ultimoError = ex;
                    System.out.println("   [Reintento] Ocurrio un fallo: " + ex.getMessage()
                            + ". Intentos restantes: " + restantes);
                }
            }

            throw new RuntimeException("Se agotaron los " + intentos + " intentos permitidos.", ultimoError);
        }
    }

    // =========================================================================
    // Demostracion en el metodo main
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("=== Demostracion de Retos para Profundizar ===");

        // Prueba Reto 1: TriFunction currificada
        System.out.println("\n--- Reto 1: Currificacion de TriFunction ---");
        TriFunction<String, Integer, Double, String> liquidacion =
                (cliente, dias, tarifaDiaria) -> cliente + " total: $" + (dias * tarifaDiaria);

        var liquidacionCurry = liquidacion.curry();
        // Podemos ir aplicando los parametros uno por uno
        var paraCarlos = liquidacionCurry.apply("Carlos");
        var paraCarlosCincoDias = paraCarlos.apply(5);
        String resultadoFinal = paraCarlosCincoDias.apply(50000.0);
        System.out.println("Resultado currificado paso a paso: " + resultadoFinal);

        // Prueba Reto 2: Metodos o(...) y cuando(...)
        System.out.println("\n--- Reto 2: Reglas con 'o' y 'cuando' ---");
        ReglaAvanzada<String> terminaEnCom = s -> s.endsWith(".com") ? List.of() : List.of("no termina en .com");
        ReglaAvanzada<String> terminaEnCo = s -> s.endsWith(".co") ? List.of() : List.of("no termina en .co");

        // Regla con 'o': es valido si termina en .com O en .co
        ReglaAvanzada<String> dominioValido = terminaEnCom.o(terminaEnCo);
        System.out.println("Validando 'tienda.co' con regla O: " + dominioValido.validar("tienda.co"));
        System.out.println("Validando 'tienda.org' con regla O: " + dominioValido.validar("tienda.org"));

        // Regla con 'cuando': exige minimo 5 caracteres SOLO cuando la palabra no esta vacia
        ReglaAvanzada<String> longitudMinima = s -> s.length() >= 5 ? List.of() : List.of("demasiado corto");
        ReglaAvanzada<String> condicional = longitudMinima.cuando(s -> !s.isEmpty());
        System.out.println("Validando texto vacio \"\" con regla cuando: " + condicional.validar(""));
        System.out.println("Validando texto corto \"abc\" con regla cuando: " + condicional.validar("abc"));

        // Prueba Reto 3: Memorizacion segura con hilos
        System.out.println("\n--- Reto 3: Memorizacion segura para hilos ---");
        Function<Integer, Integer> calculoDoble = memorizarSeguro(n -> {
            System.out.println("   [Calculando en origen para: " + n + "]");
            return n * 2;
        });

        System.out.println("Primera llamada con 10: " + calculoDoble.apply(10));
        System.out.println("Segunda llamada con 10 (desde cache): " + calculoDoble.apply(10));

        // Prueba Reto 4: Mecanismo de reintentos
        System.out.println("\n--- Reto 4: Mecanismo de Reintentos ---");
        int[] intentosSimulados = {0};

        Supplier<String> conexionSimulada = () -> {
            intentosSimulados[0]++;
            if (intentosSimulados[0] < 3) {
                throw new RuntimeException("Error temporal de conexion");
            }
            return "Conexion establecida exitosamente en el intento " + intentosSimulados[0];
        };

        String resultadoConexion = Reintentar.de(conexionSimulada, 4);
        System.out.println("Resultado final: " + resultadoConexion);

        System.out.println("\n=== Todos los retos fueron completados y verificados ===");
    }
}
