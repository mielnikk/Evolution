package project.simulation.universe.spatial;

import java.util.Random;

/**
 * Implementacja kierunku na planszy.
 *
 * @author Katarzyna Mielnik
 */
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    RIGHT(1, 0),
    LEFT(-1, 0);

    private final int xDirection;
    private final int yDirection;

    Direction(int x, int y) {
        this.xDirection = x;
        this.yDirection = y;
    }

    public int xCoordinate() {
        return this.xDirection;
    }

    public int yCoordinate() {
        return this.yDirection;
    }

    private Direction directionFromCoordinates(int x, int y) {
        for (Direction k : Direction.values()) {
            if (k.xCoordinate() == x && k.yCoordinate() == y)
                return k;
        }
        return null;
    }

    public Direction turnRight() {
        return directionFromCoordinates(this.yCoordinate() * (-1), this.xCoordinate());
    }

    public Direction turnLeft() {
        return directionFromCoordinates(this.yCoordinate(), this.xCoordinate() * (-1));
    }

    public Direction getOpposite() {
        return directionFromCoordinates(this.xDirection * (-1), this.yDirection * (-1));
    }

    public static Direction getRandomDirection() {
        Random random = new Random();
        return Direction.values()[random.nextInt(4)];
    }

}
