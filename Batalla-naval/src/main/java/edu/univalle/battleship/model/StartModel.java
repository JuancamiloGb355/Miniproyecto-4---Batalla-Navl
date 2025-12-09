package edu.univalle.battleship.model;

public class StartModel {

    private boolean continueGameAvailable;

    public StartModel() {
        // En un caso real, revisarías si existe partida guardada
        this.continueGameAvailable = false;
    }

    public boolean isContinueGameAvailable() {
        return continueGameAvailable;
    }

    public void setContinueGameAvailable(boolean continueGameAvailable) {
        this.continueGameAvailable = continueGameAvailable;
    }

    public void startNewGame() {
        System.out.println("Iniciando nueva partida...");
        // Lógica real del juego aquí
    }
}
