import java.text.Normalizer;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Laboratorio 1: Generador de slugs por composicion de funciones.
 * Asignatura: Programacion 1
 */
public class GeneradorSlugs {

    // Paso 1: Constantes de tipo UnaryOperator<String>
    // Recorta los espacios en blanco al inicio y al final usando referencia a metodo
    static final UnaryOperator<String> RECORTAR = String::strip;

    // Convierte todo el texto a minusculas usando referencia a metodo
    static final UnaryOperator<String> MINUSCULAS = String::toLowerCase;

    // Quita las tildes y acentos diacriticos descomponiendo los caracteres
    static final UnaryOperator<String> SIN_TILDES =
            s -> Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

    // Elimina cualquier caracter que no sea letra minuscula, numero, espacio o guion
    static final UnaryOperator<String> SOLO_VALIDOS =
            s -> s.replaceAll("[^a-z0-9\\s-]", "");

    // Reemplaza secuencias de espacios o guiones repetidos por un unico guion
    static final UnaryOperator<String> GUIONES =
            s -> s.strip().replaceAll("[\\s-]+", "-");

    /**
     * Paso 3: Metodo que recibe una lista de pasos y los compone usando andThen.
     * Inicia con la funcion identidad (no altera el texto inicial).
     */
    static Function<String, String> tuberia(List<? extends Function<String, String>> pasos) {
        Function<String, String> resultado = Function.identity();
        for (Function<String, String> paso : pasos) {
            resultado = resultado.andThen(paso);
        }
        return resultado;
    }

    public static void main(String[] args) {
        // Tubería en el orden normal solicitado
        Function<String, String> slug = tuberia(List.of(
                RECORTAR,
                MINUSCULAS,
                SIN_TILDES,
                SOLO_VALIDOS,
                GUIONES
        ));

        System.out.println("=== Salida Oficial del Laboratorio 1 ===");
        List<String> nombresPrueba = List.of(
                " Silla Ergonómica ",
                "Audífonos Bluetooth 5.3",
                "¡Oferta! Portátil i7 -- 16GB"
        );

        for (String nombre : nombresPrueba) {
            System.out.printf("%-32s -> %s%n", "\"" + nombre + "\"", slug.apply(nombre));
        }

        System.out.println();
        System.out.println("=== Experimento del Paso 4: Cambiando el orden de dos pasos ===");
        System.out.println("Cambiamos el orden de MINUSCULAS y SOLO_VALIDOS (primero filtrar caracteres, luego minusculas):");

        Function<String, String> tuberiaAlterada = tuberia(List.of(
                RECORTAR,
                SIN_TILDES,
                SOLO_VALIDOS, // Se evalua antes de pasar a minusculas
                MINUSCULAS,
                GUIONES
        ));

        for (String nombre : nombresPrueba) {
            System.out.println("Original:          \"" + nombre + "\"");
            System.out.println("Orden correcto:    " + slug.apply(nombre));
            System.out.println("Orden alterado:    " + tuberiaAlterada.apply(nombre));
            System.out.println("----------------------------------------------");
        }
    }
}
