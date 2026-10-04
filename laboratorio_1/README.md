# Laboratorio 1 - Generador de Slugs por Composicion

En este laboratorio el objetivo fue construir una funcion para transformar nombres de productos en "slugs" limpios para direcciones URL (por ejemplo: convertir "Silla Ergonomica" en "silla-ergonomica"). En lugar de escribir un solo metodo gigante lleno de instrucciones mezcladas, dividimos el problema en transformaciones pequenas y las unimos usando composicion de funciones.

---

### Pasos desarrollados

1. **Definicion de los 5 pasos independientes como `UnaryOperator<String>`:**
   - `RECORTAR`: Se uso la referencia a metodo `String::strip` para quitar espacios sobrantes al principio y al final.
   - `MINUSCULAS`: Se uso `String::toLowerCase` para estandarizar todo el texto a letras minusculas.
   - `SIN_TILDES`: Se uso `Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "")` para descomponer las vocales con tilde y remover las marcas de acento.
   - `SOLO_VALIDOS`: Mediante la expresion regular `[^a-z0-9\\s-]`, se borran simbolos especiales, signos de admiracion, puntos, etc., dejando unicamente letras minusculas, numeros, espacios y guiones.
   - `GUIONES`: Mediante `s.strip().replaceAll("[\\s-]+", "-")`, cualquier grupo de espacios o guiones consecutivos se convierte en un solo guion medio.

2. **Creacion del metodo `tuberia`:**
   Este metodo recibe una lista de funciones de tipo `List<? extends Function<String, String>>`.
   - Se inicia con `Function.identity()`, que es el elemento neutro (devuelve el mismo texto que recibe).
   - Luego, con un ciclo `for` basico, se va encadenando cada paso con el metodo `.andThen(paso)`.
   - Cada `.andThen()` garantiza que la salida de un paso sea la entrada del siguiente, en el orden exacto en que estan en la lista.

3. **Pruebas con los datos solicitados:**
   - `" Silla Ergonomica "` produce `silla-ergonomica`
   - `"Audifonos Bluetooth 5.3"` produce `audifonos-bluetooth-53`
   - `"¡Oferta! Portatil i7 -- 16GB"` produce `oferta-portatil-i7-16gb`

---

### Respuesta al Paso 4: ¿Que ocurre al cambiar el orden de dos pasos?

Para responder esta pregunta del laboratorio, hicimos el experimento de cambiar el orden entre `MINUSCULAS` y `SOLO_VALIDOS`. En el orden alterado, primero se ejecuta `SOLO_VALIDOS` y despues `MINUSCULAS`.

**Resultado observado:**
- En la prueba con `" Silla Ergonomica "`:
  - Orden correcto: `silla-ergonomica`
  - Orden alterado: `illa-rgonomica`
- En `"Audifonos Bluetooth 5.3"`:
  - Orden correcto: `audifonos-bluetooth-53`
  - Orden alterado: `udifonos-luetooth-53`
- En `"¡Oferta! Portatil i7 -- 16GB"`:
  - Orden correcto: `oferta-portatil-i7-16gb`
  - Orden alterado: `ferta-ortatil-i7-16`

**Explicacion:**
El resultado cambio de forma drastica y perdimos informacion. La razon es que la regla de `SOLO_VALIDOS` utiliza la expresion regular `[^a-z0-9\\s-]`, la cual solo permite letras minusculas de la 'a' a la 'z'.

Si pasamos las palabras antes de convertirlas a minusculas, el filtro considera que letras mayusculas como la 'S', la 'E', la 'A', la 'B' o la palabra 'GB' son caracteres no permitidos y las borra por completo. Por eso, en "Silla" se borra la 'S' y queda "illa".

**Conclusion sobre composicion de funciones:**
La composicion de funciones con `andThen` no es conmutativa: `f.andThen(g)` no produce lo mismo que `g.andThen(f)`. El orden en que se aplican los filtros es crucial para que el flujo de datos sea correcto.
