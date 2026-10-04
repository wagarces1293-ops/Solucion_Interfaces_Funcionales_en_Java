# Punto de Control - Seccion 2

En esta seccion de la guia se plantean tres preguntas fundamentales sobre la definicion de interfaces funcionales en Java y el uso de la anotacion @FunctionalInterface. A continuacion presento mis respuestas explicadas paso a paso con los conceptos que hemos aprendido en clase.

---

### Pregunta 1
**Enunciado:**
Una interfaz tiene un metodo abstracto, dos metodos default y declara `String toString()`. ¿Es funcional?

**Respuesta:**
Si, si es una interfaz funcional.

**Explicacion:**
Para que una interfaz sea funcional en Java, la condicion indispensable es que tenga exactamente un solo metodo abstracto (lo que se conoce como SAM o Single Abstract Method).

Al analizar los metodos que tiene esta interfaz encontramos lo siguiente:
1. Los dos metodos `default` ya traen su propio cuerpo de codigo (su implementacion), por lo tanto no son abstractos y no cuentan para el limite de un solo metodo abstracto.
2. La declaracion `String toString()` es un metodo abstracto, pero coincide con la firma de un metodo publico de la clase `java.lang.Object`. En Java, todas las clases heredan obligatoriamente de `Object`, por lo que cualquier clase que implemente la interfaz ya tendra garantizada una implementacion de `toString()`. La regla del lenguaje dice explicitamente que los metodos publicos de `Object` declarados en una interfaz no cuentan como metodos abstractos para la regla de la interfaz funcional.
3. Como resultado, solo queda el metodo abstracto inicial. Al tener exactamente un metodo abstracto pendiente por implementar, la interfaz cumple perfectamente el contrato de una interfaz funcional y puede recibir lambdas.

---

### Pregunta 2
**Enunciado:**
¿Que ventaja concreta aporta `@FunctionalInterface` si la interfaz ya es funcional sin ella?

**Respuesta:**
La ventaja principal es que le pide al compilador de Java (`javac`) que verifique y garantice que la interfaz realmente cumple las reglas de una interfaz funcional.

**Explicacion:**
En Java, una interfaz con un solo metodo abstracto ya es funcional por definicion y funciona con lambdas aunque no tenga la anotacion. Sin embargo, colocar `@FunctionalInterface` aporta dos beneficios concretos:
1. **Deteccion temprana de errores:** Si en el futuro otro companero de equipo o nosotros mismos agregamos por descuido un segundo metodo abstracto a la interfaz, el compilador marcara un error inmediatamente en la declaracion de la propia interfaz (avisando que no es una interfaz funcional valida). Si no tuviesemos la anotacion, la interfaz compilaria sin queja, pero el error apareceria despues en todos los lugares distantes del programa donde intentamos asignarle expresiones lambda.
2. **Claridad e intencion en el diseno:** Funciona como documentacion directa para quien lea el codigo, indicando con claridad que esa interfaz fue creada con el proposito de usarse mediante expresiones lambda o referencias a metodos.

---

### Pregunta 3
**Enunciado:**
¿Por que una clase abstracta con un solo metodo abstracto no puede recibir una lambda?

**Respuesta:**
Porque la especificacion del lenguaje Java restringe de manera estricta que el tipo destino (target type) de una expresion lambda debe ser unicamente una interfaz funcional, nunca una clase.

**Explicacion:**
Aunque una clase abstracta tenga un solo metodo abstracto, tiene diferencias estructurales profundas frente a una interfaz:
1. **Manejo de estado y constructores:** Las clases abstractas pueden tener constructores con logica de inicializacion y variables de instancia (estado mutable propio). Las lambdas estan pensadas para ser funciones puras y ligeras sin estado propio ni constructores.
2. **Jerarquia de herencia simple:** En Java una clase solo puede heredar de una clase padre (`extends`), mientras que puede implementar multiples interfaces. Si las lambdas pudieran implementar clases abstractas, se generarian conflictos con el modelo de herencia simple del lenguaje y con la forma en que la maquina virtual de Java (JVM) crea las funciones en memoria mediante `invokedynamic`.
3. **Diseno del lenguaje:** Los creadores de Java decidieron deliberadamente que las lambdas son implementaciones compactas de contratos de interfaz (SAM), no de clases.
