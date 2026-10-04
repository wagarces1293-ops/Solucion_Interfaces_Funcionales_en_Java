/**
 * Modelo de datos Producto compartido por los ejemplos de la guia (Listado 1).
 * Asignatura: Programacion 1
 */
public record Producto(String nombre, String categoria, double precio, int stock) {

    /**
     * Un producto esta disponible si cuenta con al menos una unidad en stock.
     */
    public boolean disponible() {
        return stock > 0;
    }
}
