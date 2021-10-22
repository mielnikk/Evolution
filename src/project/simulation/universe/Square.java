package project.simulation.universe;

/**
 * Pole - pojedyncza składowa planszy.
 */
public abstract class Square {
    /**
     * Zmienia stan pola wynikający z przejścia do nowej tury.
     */
    public abstract void nextRound();

    /**
     * Sprawdza, czy w tym momencie pole posiada jedzenie.
     */
    public abstract boolean containsFood();
}
