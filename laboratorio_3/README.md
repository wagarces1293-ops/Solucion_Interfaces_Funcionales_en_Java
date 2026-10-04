# Laboratorio 3 - Calculadora con Estrategias

En este laboratorio aprendimos a implementar el patron de diseno Estrategia utilizando un mapa y la interfaz funcional `DoubleBinaryOperator`. Tradicionalmente en Programacion 1, cuando queremos evaluar una operacion matematica a partir de un texto, recurrimos a una estructura condicional `switch` o a multiples `if-else`. En este ejercicio vimos una forma mucho mas limpia y escalable.

---

### Explicacion de la solucion

1. **Uso de `DoubleBinaryOperator` y `LinkedHashMap`:**
   - La interfaz funcional `DoubleBinaryOperator` pertenece al paquete `java.util.function` y esta especializada en tipos primitivos: recibe dos valores de tipo `double` y devuelve un resultado de tipo `double` (`applyAsDouble(double, double)`). De esta forma no se produce autoboxing innecesario a objetos `Double`.
   - Utilizamos un `LinkedHashMap<String, DoubleBinaryOperator>` como diccionario de operaciones. La ventaja de `LinkedHashMap` frente a un `HashMap` comun es que conserva el orden en que fuimos insertando los operadores, lo que permite que al consultar `OPERACIONES.keySet()` salgan en orden: `[+, -, *, /, ^, max]`.

2. **Referencias a metodos y lambdas:**
   - Para las operaciones ya existentes en el lenguaje o en la clase `Math`, usamos referencias a metodos:
     - Suma: `Double::sum`
     - Potencia: `Math::pow`
     - Maximo: `Math::max`
   - Para las operaciones personalizadas, usamos expresiones lambda:
     - Resta: `(a, b) -> a - b`
     - Multiplicacion: `(a, b) -> a * b`
     - Division: `(a, b) -> { if (b == 0) throw new ArithmeticException("división por cero"); return a / b; }`

3. **Metodo `evaluar` y manejo de excepciones:**
   - El metodo `evaluar(String expresion)` limpia los extremos con `.strip()` y divide la cadena por espacios en blanco usando `.split("\\s+")`.
   - Se valida que la expresion tenga exactamente 3 partes: operando 1, operador y operando 2.
   - Se busca el operador en el mapa `OPERACIONES.get(partes[1])`. Si no existe, lanza de inmediato `IllegalArgumentException`.
   - En el `main`, usamos un bloque `try-catch (RuntimeException ex)` para capturar tanto los errores de formato y operador desconocido, como el error de division por cero, mostrando mensajes claros y evitando que el programa se caiga.

---

### Reflexion del Paso 5: Agregar el operador modulo `%` (Mapa vs Switch)

La guia nos pide reflexionar sobre lo que tendriamos que hacer para anadir la operacion modulo (`%`) en esta version basada en mapa frente a una version tradicional basada en `switch`:

#### En la solucion con Mapa de Estrategias:
Para agregar el operador `%`, solo tenemos que agregar una linea en el mapa:
```java
OPERACIONES.put("%", (a, b) -> a % b);
```
- No tocamos para nada el metodo `evaluar`.
- No modificamos ninguna de las otras operaciones existentes.
- El metodo `evaluar` sigue funcionando exactamente igual porque busca la operacion en el mapa de forma dinamica.
- Si consultamos la lista de operadores disponibles, `%` aparece automaticamente.
- Cumple con el principio de diseno "Abierto a la extension, cerrado a la modificacion" (Open/Closed Principle).

#### En una version basada en `switch`:
En una calculadora tradicional con `switch(operador)`:
```java
switch (operador) {
    case "+": return a + b;
    case "-": return a - b;
    case "*": return a * b;
    case "/":
        if (b == 0) throw new ArithmeticException("división por cero");
        return a / b;
    case "^": return Math.pow(a, b);
    case "max": return Math.max(a, b);
    case "%": return a % b; // Aqui tendriamos que abrir el metodo a modificarlo
    default: throw new IllegalArgumentException("operador desconocido");
}
```
- Nos obligaria a buscar y modificar el codigo fuente interno del metodo `evaluar`.
- Si tenemos que agregar 15 o 20 operaciones nuevas (raiz cuadrada, seno, coseno, etc.), el bloque `switch` se convertiria en una funcion gigante ("metodo Dios") muy dificil de leer y mantener.
- Existe el riesgo de tocar accidentalmente otro `case` y danar operaciones que ya funcionaban bien.
- No hay forma sencilla de consultar programaticamente la lista de operadores disponibles sin tener que duplicar un arreglo o lista con los nombres.

**Conclusion:**
El diseno con interfaces funcionales y mapas de estrategias nos da un codigo mucho mas modular, mantenible y profesional que las estructuras condicionales rigidas.
