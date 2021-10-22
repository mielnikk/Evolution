package project.simulation.universe.spatial;

import java.util.Random;

/**
 * Implementacja współrzędnych na planszy.
 *
 * @author Katarzyna Mielnik
 */
public class Coordinates {
    private final int xCoordinate;
    private final int yCoordinate;

    public Coordinates(int x, int y) {
        this.xCoordinate = x;
        this.yCoordinate = y;
    }

    public int X() {
        return xCoordinate;
    }

    public int Y() {
        return yCoordinate;
    }

    /**
     * Zwraca losowe współrzędne.
     * @param limX wartość, której współrzędna {@code x} nie może przekroczyć
     * @param limY wartość, której współrzędna {@code y} nie może przekroczyć
     */
    public static Coordinates getRandomCoordinates(int limX, int limY) {
        Random random= new Random();
        int x = random.nextInt(limX);
        int y = random.nextInt(limY);
        return new Coordinates(x, y);
    }
}
