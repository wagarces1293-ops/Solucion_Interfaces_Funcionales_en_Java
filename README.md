# Solucion Guia Tecnica: Interfaces Funcionales en Java

**Asignatura:** Programacion 1  
**Nivel:** Primer Semestre Universitario  
**Entorno de desarrollo:** Java 21+ / IntelliJ IDEA  

---

## Descripcion General del Proyecto

Este repositorio contiene la solucion completa y detallada a la guia tecnica "Interfaces funcionales en Java: El contrato de un solo metodo que convirtio al comportamiento en un dato".

El trabajo fue desarrollado siguiendo los lineamientos de la asignatura Programacion 1, resolviendo cada ejercicio mediante conceptos fundamentales (variables, condicionales, ciclos, metodos y el uso introductorio de lambdas y las interfaces de la biblioteca estandar de Java).

El proyecto se encuentra organizado en carpetas independientes para cada seccion y laboratorio, incluyendo en cada una su respectivo codigo fuente en Java y un archivo explicativo en formato Markdown (`README.md`).

---

## Estructura de Carpetas y Contenido

```text
Solucion_Interfaces_Funcionales_en_Java/
├── punto_de_control/
│   ├── README.md               <- Respuestas justificadas a las 3 preguntas teóricas de la Sección 2
│   └── PuntoDeControlDemo.java  <- Código demostrativo ejecutable
├── laboratorio_1/
│   ├── README.md               <- Explicación del laboratorio y respuesta al cambio de orden (Paso 4)
│   └── GeneradorSlugs.java     <- Generador de slugs por composición funcional
├── laboratorio_2/
│   ├── README.md               <- Explicación del diseño genérico de Regla<T> y análisis de pruebas
│   └── MotorValidacion.java    <- Motor de validación componible para formularios
├── laboratorio_3/
│   ├── README.md               <- Explicación de mapa de estrategias vs switch (Paso 5)
│   └── CalculadoraEstrategias.java <- Calculadora basada en Map<String, DoubleBinaryOperator>
├── laboratorio_4/
│   ├── README.md               <- Explicación del proyecto integrador y análisis de ahorro
│   ├── Producto.java           <- Modelo de datos record Producto
│   ├── Catalogo.java           <- Catálogo con la lista de productos de muestra
│   └── MotorPromociones.java   <- Motor de selección de promociones más convenientes
├── autoevaluacion/
│   ├── README.md               <- Las 10 preguntas tipo test con respuestas y justificaciones
│   └── AutoevaluacionDemo.java <- Código práctico para verificar las respuestas del cuestionario
├── retos_profundizar/
│   ├── README.md               <- Explicación y análisis de los 4 retos opcionales de la guía
│   └── RetosProfundizar.java   <- Implementación en Java de los 4 retos de profundización
├── Solucion_Interfaces_Funcionales_en_Java.iml
└── README.md                   <- Este archivo (índice general del proyecto)
```

---

## Detalle de Cada Seccion

### 1. Punto de Control (`punto_de_control/`)
- **Archivo:** `PuntoDeControlDemo.java` y `README.md`
- **Contenido:**
  - Pregunta 1: Analisis de por que una interfaz con un metodo abstracto, dos `default` y el metodo `toString()` de `Object` sigue siendo una interfaz funcional valida.
  - Pregunta 2: Beneficios concretos y proteccion que ofrece la anotacion `@FunctionalInterface`.
  - Pregunta 3: Razones por las cuales una clase abstracta no puede recibir expresiones lambda en Java.

### 2. Laboratorio 1: Generador de Slugs (`laboratorio_1/`)
- **Archivo:** `GeneradorSlugs.java` y `README.md`
- **Contenido:**
  - Implementacion de los 5 pasos con `UnaryOperator<String>` (`RECORTAR`, `MINUSCULAS`, `SIN_TILDES`, `SOLO_VALIDOS`, `GUIONES`).
  - Metodo `tuberia` utilizando `Function.identity()` y composicion mediante `.andThen()`.
  - Pruebas con las tres cadenas solicitadas.
  - Experimento y explicacion del Paso 4: que ocurre si alteramos el orden de evaluacion entre `MINUSCULAS` y `SOLO_VALIDOS` (perdida de caracteres en mayusculas).

### 3. Laboratorio 2: Motor de Validacion Componible (`laboratorio_2/`)
- **Archivo:** `MotorValidacion.java` y `README.md`
- **Contenido:**
  - Diseno de la interfaz funcional generica `@FunctionalInterface interface Regla<T>`.
  - Metodo `default Regla<T> y(Regla<? super T> otra)` para unir reglas y combinar errores.
  - Fabricas estaticas `exigir` (para predicados) y `campo` (para extraer y validar atributos de objetos).
  - Validacion del modelo `Registro` para nombre de usuario, formato de correo y edad minima.

### 4. Laboratorio 3: Calculadora con Estrategias (`laboratorio_3/`)
- **Archivo:** `CalculadoraEstrategias.java` y `README.md`
- **Contenido:**
  - Sustitucion de una estructura `switch` rigida por un `Map<String, DoubleBinaryOperator>` utilizando `LinkedHashMap`.
  - Uso de referencias a metodos (`Double::sum`, `Math::pow`, `Math::max`) y lambdas para las operaciones aritmeticas.
  - Control de division por cero arrojando `ArithmeticException`.
  - Analisis comparativo (Paso 5): facilidad de extension al anadir un nuevo operador (como `%`) en el mapa frente a modificar un bloque `switch`.

### 5. Laboratorio 4: Motor de Promociones del Catalogo (`laboratorio_4/`)
- **Archivo:** `MotorPromociones.java`, `Producto.java`, `Catalogo.java` y `README.md`
- **Contenido:**
  - Modelado de promociones con el record `Promocion` usando `Predicate<Producto>` y `DoubleUnaryOperator`.
  - Fabricas de ajuste para porcentaje y monto fijo (garantizando no bajar de cero con `Math.max`).
  - Seleccion de la mejor oferta para el cliente usando `BinaryOperator.minBy(...)` y reduccion con un ciclo `for`.
  - Formato de impresion con `BiConsumer` y calculo del ahorro total ($429.250) con `ToDoubleBiFunction`.

### 6. Cuestionario de Autoevaluacion (`autoevaluacion/`)
- **Archivo:** `AutoevaluacionDemo.java` y `README.md`
- **Contenido:**
  - Resolucion completa de las 10 preguntas de opcion multiple de la Seccion C (paginas 32 y 33).
  - Justificacion tecnica de la opcion correcta y descarte de las opciones incorrectas.
  - Codigo Java demostrativo para verificar en ejecucion el comportamiento de cada concepto evaluado.

### 7. Retos para Profundizar (`retos_profundizar/`)
- **Archivo:** `RetosProfundizar.java` y `README.md`
- **Contenido:**
  - Reto 1: Currificacion con `TriFunction.curry()`.
  - Reto 2: Metodos `o(...)` y `cuando(...)` en el motor de reglas.
  - Reto 3: Memorizacion concurrente con `ConcurrentHashMap` y explicacion del riesgo de bloqueo en funciones recursivas.
  - Reto 4: Mecanismo de reintentos con `Reintentar.de(Supplier<T>, int)`.

---

## Como Ejecutar los Ejercicios

Desde la terminal en el directorio raiz del proyecto, se puede compilar y ejecutar cada archivo directamente con el comando `java`:

1. **Punto de Control:**
   ```bash
   java punto_de_control/PuntoDeControlDemo.java
   ```

2. **Laboratorio 1 (Generador de Slugs):**
   ```bash
   java laboratorio_1/GeneradorSlugs.java
   ```

3. **Laboratorio 2 (Motor de Validacion):**
   ```bash
   java laboratorio_2/MotorValidacion.java
   ```

4. **Laboratorio 3 (Calculadora con Estrategias):**
   ```bash
   java laboratorio_3/CalculadoraEstrategias.java
   ```

5. **Laboratorio 4 (Motor de Promociones):**
   ```bash
   javac laboratorio_4/Producto.java laboratorio_4/Catalogo.java laboratorio_4/MotorPromociones.java
   java -cp laboratorio_4 MotorPromociones
   ```

6. **Autoevaluacion:**
   ```bash
   java autoevaluacion/AutoevaluacionDemo.java
   ```

7. **Retos de Profundizacion:**
   ```bash
   java retos_profundizar/RetosProfundizar.java
   ```
