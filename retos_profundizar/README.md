# Retos para Profundizar - Seccion C (Pagina 33)

En esta carpeta se encuentra la solucion a los 4 retos opcionales de profundizacion propuestos al final de la guia tecnica. Estos ejercicios ponen a prueba conceptos mas avanzados de funciones de orden superior, manejo de concurrencia y tolerancia a fallos.

---

### Reto 1: Currificacion de `TriFunction`
**Objetivo:** Escribir un metodo `curry()` en una interfaz `TriFunction<A, B, C, R>` para convertirla en una cadena de funciones de un solo parametro: `Function<A, Function<B, Function<C, R>>>`.

**Solucion implementada:**
```java
@FunctionalInterface
public interface TriFunction<A, B, C, R> {
    R aplicar(A a, B b, C c);

    default Function<A, Function<B, Function<C, R>>> curry() {
        return a -> b -> c -> aplicar(a, b, c);
    }
}
```

**Explicacion:**
La currificacion consiste en transformar una funcion que recibe varios argumentos juntos en una secuencia de funciones que reciben los argumentos de a uno por vez.
- La primera funcion recibe el argumento `a` y devuelve una segunda funcion.
- La segunda funcion recibe el argumento `b` y devuelve una tercera funcion.
- La tercera funcion recibe el argumento `c` y finalmente calcula el resultado llamando a `aplicar(a, b, c)`.
Esto es util porque nos permite fijar o precargar parametros paso a paso (aplicacion parcial).

---

### Reto 2: Metodos `o(...)` y `cuando(...)` en `Regla<T>`
**Objetivo:** Enriquecer el motor de validacion del Laboratorio 2 anadiendo disyuncion logica (`o`) y validacion condicional (`cuando`).

**Solucion implementada:**
1. **Metodo `o(Regla<? super T> otra)`:**
   - Evalua la regla actual. Si no produce errores (`errores1.isEmpty()`), el dato es valido y retorna inmediatamente una lista vacia sin necesidad de evaluar la otra regla (hace cortocircuito booleano).
   - Si la primera falla, evalua la segunda regla. Si esta segunda no tiene errores, el dato tambien se considera valido.
   - Si ambas fallan, une y reporta los errores de ambas.
2. **Metodo `cuando(Predicate<T> condicion)`:**
   - Evalua primero el predicado de condicion `condicion.test(valor)`.
   - Si la condicion se cumple, aplica la regla normal `validar(valor)`.
   - Si la condicion no se cumple, la regla se desactiva y devuelve lista vacia (no genera error). Esto es muy util para campos opcionales en formularios (por ejemplo, validar formato de telefono fijo unicamente si el usuario escribio algo).

---

### Reto 3: Memorizacion segura para hilos y funciones recursivas
**Objetivo:** Implementar una funcion `memorizarSeguro` usando `ConcurrentHashMap` y explicar que ocurre si la funcion que se intenta memorizar es recursiva.

**Solucion implementada:**
```java
public static <T, R> Function<T, R> memorizarSeguro(Function<T, R> funcion) {
    Map<T, R> cache = new ConcurrentHashMap<>();
    return entrada -> cache.computeIfAbsent(entrada, funcion);
}
```

**Analisis de que ocurre si la funcion es recursiva:**
Si intentamos memorizar una funcion recursiva directa (por ejemplo, un calculo de Fibonacci que se llama a si mismo pasando por la cache), **el programa fallara o se bloqueara**:
1. En `ConcurrentHashMap`, el metodo `computeIfAbsent` bloquea internamente la celda o nodo (bucket) correspondiente a la clave mientras calcula el valor para garantizar que dos hilos no calculen lo mismo a la vez.
2. Si la funcion de calculo vuelve a llamar a la misma cache mientras esta dentro del calculo, puede intentar modificar o leer el mismo nodo que ya esta bloqueado por el mismo hilo.
3. Esto provoca en tiempo de ejecucion una excepcion `IllegalStateException` ("Recursive update") o un interbloqueo (deadlock) donde el hilo se queda congelado esperandose a si mismo de manera infinita.
4. Por esta razon, la guia advierte que las funciones que se memoricen con `computeIfAbsent` no deben ser auto-referenciales ni modificar el mapa durante su ejecucion.

---

### Reto 4: Mecanismo de reintentos `Reintentar.de(Supplier<T>, int)`
**Objetivo:** Crear una clase con un metodo estatico que reciba un `Supplier<T>` y un numero de intentos maximos, volviendo a ejecutar el proveedor si este lanza una excepcion.

**Solucion implementada:**
```java
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
```

**Explicacion:**
- Se utiliza un ciclo `while` basico para controlar la cantidad de intentos restantes.
- En cada iteracion, se intenta obtener el resultado con `proveedor.get()`.
- Si se ejecuta de forma exitosa, retorna el valor de inmediato.
- Si se produce una excepcion en tiempo de ejecucion (`RuntimeException`), se captura en el bloque `catch`, se decrementa el contador y se guarda la excepcion para no perder el rastro del error original.
- Si se agotan todos los intentos sin exito, se lanza una nueva excepcion informando el problema y encadenando la causa original (`ultimoError`).
