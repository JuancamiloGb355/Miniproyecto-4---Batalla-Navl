# Battleship - Juego de Hundir la Flota en Java

Este es un proyecto de **Battleship (Hundir la Flota)** implementado en **Java** utilizando **JavaFX** para la interfaz gráfica. El proyecto sigue principios de **programación orientada a objetos**, emplea **patrones de diseño** como **Strategy** para la IA, y permite guardar/cargar partidas.

---

## Características Principales

- **Juego para un jugador contra la máquina**
  - Coloca tus barcos manualmente en un tablero de 10x10.
  - Dispara a la flota enemiga haciendo clic en las celdas del tablero.
  
- **IA inteligente**
  - Implementa una estrategia de disparo **Hunt & Target**.
  - El modo “hunt” dispara aleatoriamente hasta encontrar un barco.
  - El modo “target” dispara alrededor de un `hit` para hundir barcos de manera eficiente.
  - La IA ahora intenta deducir la orientación del barco para optimizar los disparos.

- **Guardado y carga de partidas**
  - Permite guardar el estado actual del juego y continuar más tarde.
  - Se guarda la posición de los barcos y los disparos realizados.

- **Interfaz gráfica con JavaFX**
  - Tablero interactivo para el jugador y previsualización del tablero del enemigo.
  - Drag & drop para colocar los barcos.
  - Indicadores visuales para `hit`, `miss` y barcos hundidos.

- **Patrones de diseño**
  - **Strategy:** permite definir diferentes estrategias de disparo para la IA.
  - **Singleton:** `GameManager` controla el flujo del juego.

---

## Estructura del Proyecto

src/
└─ edu/univalle/battleship/
├─ controller/ # Controladores de JavaFX
├─ designpatterns/ # Estrategias de disparo (IA)
├─ model/ # Clases de lógica de juego (Player, Ship, Board)
└─ resources/
└─ images/ # Imágenes de barcos, hits, misses, sink


- **`StartController.java`**: ventana inicial del juego (nuevo/continuar partida).  
- **`PositionController.java`**: permite al jugador colocar sus barcos en el tablero.  
- **`OpponentController.java`**: controla el tablero de la máquina y la interacción del jugador.  
- **`HuntTargetShootingStrategy.java`**: IA de disparo con Hunt & Target.  
- **`RandomShootingStrategy.java`**: IA básica de disparo aleatorio.  
- **`GameManager.java`**: singleton que gestiona el estado global del juego.  

---

## Cómo Ejecutar

1. Clonar el repositorio:
```bash git clone https://github.com/tu_usuario/battleship-java.git```

2. Abrir el proyecto en IntelliJ IDEA o Eclipse.
   
3. Compilar y ejecutar la clase principal:
java -jar Battleship.jar

o ejecutar desde la clase que lanza la aplicación JavaFX (según tu configuración).

## Uso

Inicia el juego desde la ventana principal.
Coloca tus barcos arrastrando y soltando desde la flota al tablero.
Cambia la orientación de los barcos con el botón Horizontal/Vertical.
Haz clic en el tablero enemigo para disparar.
Observa los indicadores visuales de hit, miss o sink.
Puedes guardar y salir con el botón Guardar y Salir para continuar más tarde.

## Tecnologías Utilizadas

Java 17+
JavaFX
Diseño orientado a objetos
Patrones de diseño (Strategy, Singleton)

## Licencia

Este proyecto está bajo la licencia MIT. Puedes usarlo y modificarlo libremente con fines educativos.
