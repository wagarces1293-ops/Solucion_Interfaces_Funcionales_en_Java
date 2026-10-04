# Laboratorio 2 - Motor de Validacion Componible

En este laboratorio aprendimos a disenar nuestra propia interfaz funcional generica (`Regla<T>`) para construir un validador de datos para formularios. Lo interesante de este ejercicio es que permite unir varias reglas pequenas mediante un metodo de composicion (`y`), logrando que una regla compleja se exprese de forma muy limpia: `usuarioValido.y(correoValido).y(edadValida)`.

---

### Componentes de la solucion

1. **Interfaz funcional `Regla<T>`:**
   Tiene un unico metodo abstracto:
   `List<String> validar(T valor);`
   Si el dato cumple con la regla, devuelve una lista vacia `List.of()`. Si no cumple, devuelve una lista con los mensajes de error encontrados.

2. **Metodo default de composicion `y`:**
   Permite unir la regla actual con otra regla (`otra`).
   Al validar un valor, ejecuta la primera regla, luego ejecuta la segunda regla, y une todos los mensajes de error en una sola lista mediante `errores.addAll(...)`. Si ninguna de las dos reglas detecto errores, la lista resultante queda vacia.

3. **Fabrica estatica `exigir`:**
   Convierte cualquier condicion booleana (`Predicate<? super T>`) en una regla:
   - Si la condicion se cumple (`condicion.test(valor)` es verdadero), devuelve lista vacia.
   - Si no se cumple, devuelve una lista con el mensaje de error especificado.

4. **Fabrica estatica `campo`:**
   Permite validar un campo especifico de un objeto compuesto.
   Recibe una funcion `extractor` (como `Registro::usuario`) para sacar el valor del atributo y luego le aplica una regla disenada para ese tipo de dato. Esto es util porque podemos reutilizar reglas generales de texto (`Regla<String>`) directamente sobre objetos complejos (`Registro`).

5. **Modelo `Registro` y validaciones aplicadas:**
   Se creo un `record Registro(String usuario, String correo, int edad)` y se validaron los siguientes criterios:
   - `usuario`: Obligatorio (no debe estar en blanco) y tener al menos 4 caracteres.
   - `correo`: Debe tener formato valido con arroba y dominio (usando expresion regular).
   - `edad`: Debe ser mayor o igual a 14 anos.

---

### Analisis de los casos de prueba

Al ejecutar el programa con los tres registros de prueba, obtuvimos exactamente los resultados esperados:

1. **`Registro("camila_r", "camila@correo.co", 19)`:**
   - Usuario valido (no vacio, 8 caracteres).
   - Correo valido.
   - Edad valida (19 anos).
   - Resultado: **VALIDO** (lista de errores vacia `[]`).

2. **`Registro("", "sin-arroba", 12)`:**
   - Falla en usuario obligatorio.
   - Falla en longitud minima de usuario.
   - Falla en formato de correo (no contiene arroba ni dominio valido).
   - Falla en edad (12 anos es menor a 14).
   - Resultado: **INVALIDO** con los 4 errores acumulados.

3. **`Registro("ana", "ana@correo", 15)`:**
   - Cumple en usuario obligatorio, pero falla por tener solo 3 caracteres (minimo 4).
   - Falla en correo (falta el dominio con punto, ej: `.com` o `.co`).
   - Cumple con la edad (15 anos).
   - Resultado: **INVALIDO** con los 2 errores acumulados.

---

### Conclusion del laboratorio
En lugar de escribir multiples condicionales `if-else` anidados dentro de una clase controladora, las interfaces funcionales nos permitieron separar cada criterio de validacion como una pieza independiente y reutilizable. Ademas, gracias a que el metodo `y` acumula los errores, el usuario puede ver todos los fallos de una sola vez en lugar de corregir uno por uno.
