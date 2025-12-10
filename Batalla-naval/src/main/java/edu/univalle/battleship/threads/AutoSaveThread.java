package edu.univalle.battleship.threads;

public class AutoSaveThread {
    Thread thread;
    public AutoSaveThread(Thread thread) {
        this.thread = thread;
    }

    // Hilo que guarda automáticamente la partida después de cada jugada.

// run(): bucle que escucha cambios en el juego y guarda.
// requestSave(): se llama desde GameController cuando hay jugada.
}
