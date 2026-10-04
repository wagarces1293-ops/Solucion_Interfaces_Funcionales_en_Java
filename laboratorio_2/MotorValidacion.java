import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Laboratorio 2: Motor de validacion componible.
 * Asignatura: Programacion 1
 */
public class MotorValidacion {

    /**
     * Paso 1: Interfaz funcional generica Regla<T>.
     * Valida un objeto de tipo T y devuelve una lista de errores encontrados.
     * Si no hay errores, la lista estara vacia.
     */
    @FunctionalInterface
    public interface Regla<T> {
        List<String> validar(T valor);

        /**
         * Paso 2: Metodo default para encadenar reglas con logica "Y".
         * Ejecuta la regla actual y la otra regla, uniendo todos los mensajes de error.
         */
        default Regla<T> y(Regla<? super T> otra) {
            return valor -> {
                List<String> errores = new ArrayList<>(validar(valor));
                errores.addAll(otra.validar(valor));
                return errores;
            };
        }

        /**
         * Paso 3: Fabrica estatica para crear una regla basada en una condicion (Predicate).
         * Si cumple la condicion no genera error; si no la cumple, devuelve el mensaje de error.
         */
        static <T> Regla<T> exigir(Predicate<? super T> condicion, String mensaje) {
            return valor -> condicion.test(valor) ? List.of() : List.of(mensaje);
        }

        /**
         * Paso 4: Fabrica estatica que aplica una regla sobre un atributo especifico de un objeto.
         * Usa un extractor (Function) para obtener el campo y luego le aplica la regla correspondiente.
         */
        static <T, C> Regla<T> campo(Function<? super T, ? extends C> extractor, Regla<? super C> regla) {
            return valor -> regla.validar(extractor.apply(valor));
        }
    }

    /**
     * Paso 5: Modelo de datos para el formulario de registro.
     */
    public record Registro(String usuario, String correo, int edad) { }

    public static void main(String[] args) {
        System.out.println("=== Salida Oficial del Laboratorio 2 ===");

        // Reglas para el texto del usuario: que no este vacio y que tenga minimo 4 caracteres
        Regla<String> usuarioTexto = Regla.<String>exigir(s -> !s.isBlank(), "usuario: obligatorio")
                .y(Regla.exigir(s -> s.length() >= 4, "usuario: mínimo 4 caracteres"));

        // Adaptamos la regla de texto para que se aplique al campo 'usuario' del Registro
        Regla<Registro> usuarioValido = Regla.campo(Registro::usuario, usuarioTexto);

        // Regla para el formato del correo electronico
        Regla<Registro> correoValido = Regla.exigir(
                r -> r.correo().matches("[^@\\s]+@[^@\\s]+\\.[a-z]{2,}"),
                "correo: formato inválido"
        );

        // Regla para validar la edad minima de 14 anos
        Regla<Registro> edadValida = Regla.exigir(r -> r.edad() >= 14, "edad: mínimo 14 años");

        // Combinamos todas las reglas en una sola regla maestra
        Regla<Registro> todas = usuarioValido.y(correoValido).y(edadValida);

        // Lista de registros de prueba solicitados
        List<Registro> registros = List.of(
                new Registro("camila_r", "camila@correo.co", 19),
                new Registro("", "sin-arroba", 12),
                new Registro("ana", "ana@correo", 15)
        );

        // Evaluacion e impresion de resultados con formato
        for (Registro r : registros) {
            List<String> errores = todas.validar(r);
            String nombreUsuario = r.usuario().isEmpty() ? "(vacío)" : r.usuario();
            String estado = errores.isEmpty() ? "VÁLIDO" : "INVÁLIDO";

            System.out.printf("%-9s %-10s %s%n", estado, nombreUsuario, errores);
        }
    }
}
