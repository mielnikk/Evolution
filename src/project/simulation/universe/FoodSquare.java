package project.simulation.universe;

public class FoodSquare extends Square {
    private boolean isFoodRipe;
    private final int energyFromFood;
    private final int ripeningTime;
    private int foodRipeness;

    public FoodSquare(int energyFromFood, int ripeningTime) {
        this.energyFromFood = energyFromFood;
        this.foodRipeness = ripeningTime;
        this.ripeningTime = ripeningTime;
        this.isFoodRipe = true;
    }

    public void nextRound() {
        if (this.isFoodRipe) return;

        this.foodRipeness++;
        if (this.foodRipeness == this.ripeningTime)
            this.isFoodRipe = true;
    }

    public boolean containsFood() {
        return this.isFoodRipe;
    }

    public int eatFood() {
        if (!isFoodRipe)
            return 0;
        this.isFoodRipe = false;
        this.foodRipeness = 0;
        return this.energyFromFood;
    }
}
