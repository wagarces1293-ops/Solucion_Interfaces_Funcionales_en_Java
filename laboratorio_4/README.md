# Laboratorio 4 - Proyecto Integrador: Motor de Promociones del Catalogo

En este proyecto integrador unimos todos los conceptos vistos a lo largo de la guia (lambdas, referencias a metodos, la biblioteca `java.util.function`, composicion y reduccion) para solucionar un problema real de comercio electronico: aplicar la promocion mas conveniente para cada producto de una tienda sin tocar el codigo que procesa el catalogo.

---

### Arquitectura y diseno funcional

1. **Modelo de datos `Producto` y catalogo:**
   - Se utilizo el record `Producto(String nombre, String categoria, double precio, int stock)` con el metodo `disponible()`, el cual valida si hay inventario disponible (`stock > 0`).
   - El cuaderno escolar tiene stock 0, por lo que el algoritmo lo omite directamente mediante `if (!p.disponible()) continue;`.

2. **Record `Promocion`:**
   En lugar de crear una clase para cada promocion, modelamos una promocion usando interfaces funcionales:
   - `String nombre`: El nombre descriptivo de la promocion.
   - `Predicate<Producto> aplicaA`: Condicion booleana que decide si el producto califica para la promocion.
   - `DoubleUnaryOperator ajuste`: Funcion matematica que toma el precio actual del producto y calcula el nuevo precio rebajado.
   - Metodo `precioCon(Producto p)`: Si la condicion `aplicaA.test(p)` es verdadera, aplica la rebaja con `ajuste.applyAsDouble(p.precio())`. Si no califica, devuelve el precio original.

3. **Fabricas de ajustes (`DoubleUnaryOperator`):**
   - `porcentaje(double pct)`: Devuelve una lambda que aplica un porcentaje de descuento: `precio -> precio * (1.0 - pct / 100.0)`.
   - `montoFijo(double monto)`: Devuelve una lambda que resta una cantidad fija en dinero, usando `Math.max(0.0, precio - monto)` para garantizar que un descuento jamas deje un precio negativo.

4. **Las tres promociones del negocio:**
   - `Tecno 10 %`: Aplica a productos con categoria `"tecnología"`, descuento del 10%.
   - `Bono $50.000`: Aplica a productos de precio igual o superior a $200.000, resta $50.000 fijos.
   - `Liquidación`: Aplica a productos con mas de 100 unidades en bodega (`stock > 100`), descuento del 30%.

5. **Opcion por defecto con `Supplier<Promocion>`:**
   - Representa la opcion "Sin promocion".
   - Aplica a cualquier producto (`p -> true`) y usa la funcion identidad `DoubleUnaryOperator.identity()`, que deja el precio intacto.
   - Se obtiene de manera diferida mediante `sinPromocion.get()`.

6. **Seleccion de la promocion mas conveniente (`minBy`):**
   - Para cada producto, el cliente debe recibir el descuento maximo (es decir, el menor precio final posible).
   - Usamos un comparador de numeros: `Comparator.comparingDouble(promo -> promo.precioCon(p))`.
   - Se pasa al selector `BinaryOperator.minBy(...)`, que entre dos opciones siempre escoge la que deje el precio mas bajo.
   - Mediante un ciclo `for`, se evalua la promocion ganadora contra todas las disponibles.

7. **Impresion y acumulacion de ahorros:**
   - `BiConsumer<Producto, Promocion> imprimir`: Recibe el producto y la promocion ganadora y formatea la salida en consola.
   - `ToDoubleBiFunction<Producto, Promocion> ahorro`: Calcula el ahorro individual (`precioOriginal - precioConDescuento`).
   - Se acumula el ahorro total con una variable `double` sumatoria.

---

### Analisis de resultados obtenidos

Al ejecutar el programa, la salida en consola es:

```text
Portatil          Tecno 10 %     $  3.200.000 -> $  2.880.000
Mouse             Tecno 10 %     $     85.000 -> $     76.500
Audifonos         Bono $50.000   $    240.000 -> $    190.000
Lapiz             Liquidacion    $      2.500 -> $      1.750
Silla ergonomica  Bono $50.000   $    890.000 -> $    840.000
Ahorro total para el cliente: $429.250
```

**Por que se escogio cada promocion:**
- **Portatil ($3.200.000):** Aplica a Tecno 10% (ahorro de $320.000) y a Bono $50.000 (ahorro de $50.000). El algoritmo eligio Tecno 10% porque deja el precio en $2.880.000, siendo mucho mas conveniente para el cliente.
- **Mouse ($85.000):** Solo aplica a Tecno 10% (ahorro de $8.500, precio final $76.500). No califica para Bono $50.000 por costar menos de $200.000 ni para liquidacion por tener solo 25 unidades.
- **Cuaderno ($12.000):** Fue ignorado porque su stock es 0 (no disponible).
- **Audifonos ($240.000):** Aplica a Tecno 10% (descuento de $24.000, precio $216.000) y a Bono $50.000 (descuento de $50.000, precio $190.000). El algoritmo escogio el Bono $50.000 por ser el mas favorable.
- **Lapiz ($2.500):** Tiene 120 unidades en bodega, por lo que aplica a Liquidacion 30% (descuento de $750, precio final $1.750).
- **Silla ergonomica ($890.000):** No es de tecnologia, pero supera los $200.000, por lo que recibe el Bono de $50.000 (precio final $840.000).

Ahorro total acumulado: $320.000 + $8.500 + $50.000 + $750 + $50.000 = **$429.250**.

---

### Conclusion del laboratorio
Este laboratorio demuestra la potencia real de las interfaces funcionales: el motor de calculo principal no conoce ninguna promocion fija en su codigo. Si manana la tienda agrega una promocion del dia sin IVA o una promocion 2x1, solo hay que agregar un elemento nuevo a la lista `promociones` sin modificar una sola linea del algoritmo de evaluacion ni de la logica de facturacion.
