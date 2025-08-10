# TicTacToeFun

Juego de Tic Tac Toe (tres en raya) en consola con IA en tres niveles (EASY / NORMAL / IMPOSSIBLE), mensajes dinámicos y tablero grande ANSI.

## Características
- IA EASY: movimientos aleatorios y comentarios divertidos.
- IA NORMAL: intenta ganar o bloquear, prioriza centro y esquinas.
- IA IMPOSSIBLE: algoritmo Minimax con poda alpha-beta (no pierde).
- Tablero "grande" con bordes Unicode y colores ANSI (se puede desactivar en código).
- Animación de "pensando" para la IA.
- Rejuego sin reiniciar el proceso.
- Entrada tipo keypad numérico (7 8 9 / 4 5 6 / 1 2 3).

## Demostración rápida
```
╔═══════════════════════════════╗
║        TIC TAC TOE FUN        ║
║   Humano (X) vs IA (O)        ║
╚═══════════════════════════════╝
┏━━━━━━━━━┳━━━━━━━━━┳━━━━━━━━━┓
┃    7    ┃    8    ┃    9    ┃
┠─────────┼─────────┼─────────┨
┃    4    ┃    5    ┃    6    ┃
┠─────────┼─────────┼─────────┨
┃    1    ┃    2    ┃    3    ┃
┗━━━━━━━━━┻━━━━━━━━━┻━━━━━━━━━┛
```

## Estructura
```
src/main/java/com/kryxuss/tictactoefun/TicTacToeFun.java
```

## Requisitos
- Java 11+ (probado con OpenJDK 17)

## Compilación y ejecución
```bash
javac src/main/java/com/kryxuss/tictactoefun/TicTacToeFun.java -d out
java -cp out com.kryxuss.tictactoefun.TicTacToeFun
```

## Cómo jugar
1. Elige la dificultad (1/2/3).
2. Decide quién empieza (s/n).
3. Introduce posiciones usando el keypad numérico.
4. Gana haciendo una línea de 3 o provoca un empate.

## Notas técnicas
- Minimax retorna puntuaciones según profundidad para favorecer victorias rápidas y derrotas tardías.
- Limpieza de pantalla mediante secuencias ANSI (fallback si no están soportadas).
- El centrado de símbolos ignora códigos ANSI al calcular ancho visible.

## Posibles mejoras futuras
- Toggle de colores vía argumento.
- Exportar histórico de partidas a JSON/CSV.
- Añadir tests unitarios para minimax y detección de ganador.
- Modo silencioso (sin taunts ni animaciones).
- Empaquetado con Maven/Gradle y publicación como artefacto.

## Autor
Creado por **Kryxuss** por diversión. Si te gusta, ponle ⭐ en el repo.

## Licencia
Este proyecto se distribuye bajo la licencia MIT. Ver `LICENSE`.
