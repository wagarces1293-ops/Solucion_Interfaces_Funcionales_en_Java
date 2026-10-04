/**
 * Demostracion practica de los conceptos analizados en el Punto de Control.
 * Asignatura: Programacion 1
 */
public class PuntoDeControlDemo {

    // Interfaz para comprobar la Pregunta 1:
    // Tiene un metodo abstracto, dos metodos default y declara toString() de Object.
    @FunctionalInterface
    interface OperacionPersonalizada {
        // Metodo abstracto unico (SAM)
        int operar(int a, int b);

        // Metodo default 1: no cuenta como abstracto porque tiene cuerpo
        default int duplicarPrimerNumero(int a) {
            return a * 2;
        }

        // Metodo default 2: tampoco cuenta
        default void imprimirMensaje(String msg) {
            System.out.println("Mensaje: " + msg);
        }

        // Metodo de Object: no cuenta como abstracto porque toda clase hereda Object
        @Override
        String toString();
    }

    public static void main(String[] args) {
        System.out.println("=== Demostracion del Punto de Control ===");

        // Comprobacion Pregunta 1: Podemos asignarle una expresion lambda directamente
        OperacionPersonalizada suma = (x, y) -> x + y;

        int resultado = suma.operar(15, 25);
        System.out.println("Resultado de la operacion (15 + 25): " + resultado);

        // Uso de los metodos default
        System.out.println("Duplicado de 15: " + suma.duplicarPrimerNumero(15));
        suma.imprimirMensaje("La interfaz funciona correctamente con lambdas.");

        System.out.println("Comprobacion exitosa: la interfaz es completamente funcional.");
    }
}
