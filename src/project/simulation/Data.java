package project.simulation;

import project.simulation.rob.Rob;
import project.simulation.universe.Board;

import java.util.ArrayList;

/**
 * Klasa stworzona w celu wypisywania danych o symulacji.
 *
 * @author Katarzyna Mielnik
 */
class Data {
    private final ArrayList<Rob> robs;
    private final Board board;

    public Data(ArrayList<Rob> robs, Board board) {
        this.robs = robs;
        this.board = board;
    }

    void printSimulationState() {
        System.out.println("* Stan symulacji.");
        for (Rob rob : this.robs) {
            System.out.println("* " + rob);
        }
    }

    void printStatistics(int roundNumber) {
        String food = "żyw: " + board.foodSquaresNumber();
        System.out.println(roundNumber + ", " + food + ", " + "roby: " + this.robs.size() + ", " + programStatistics() +
                ", " + energyStatistics() + ", " + ageStatistics());
    }

    String programStatistics() {
        String statProgram = "prog: ";
        if (this.robs.size() == 0)
            return statProgram += "0/0/0";
        double lenSum = 0, maxLen = 0, minLen = Double.MAX_VALUE;
        for (Rob rob : this.robs) {
            double programLength = rob.getProgramLength();
            lenSum += programLength;
            maxLen = Math.max(maxLen, programLength);
            minLen = Math.min(minLen, programLength);
        }
        double meanLength = lenSum / this.robs.size();
        statProgram += minLen+ "/" + String.format("%.2f", meanLength) + "/" + maxLen;
        return statProgram;
    }

    String energyStatistics() {
        if (this.robs.size() == 0)
            return "ener : 0/0/0";

        String statEnergy = "ener: ";
        double enerSum = 0, maxEner = 0, minEner = Double.MAX_VALUE;
        for (Rob rob : this.robs) {
            double energy = rob.getEnergyLevel();
            enerSum += energy;
            maxEner = Math.max(maxEner, energy);
            minEner = Math.min(minEner, energy);
        }
        double meanEnergyLevel = enerSum / this.robs.size();
        statEnergy += minEner + "/" + String.format("%.2f", meanEnergyLevel) + "/" + maxEner;
        return statEnergy;
    }

    String ageStatistics() {
        String statAge = "wiek: ";
        if (this.robs.size() == 0)
            return statAge += "0/0/0";
        double ageSum = 0, maxAge = 0, minAge = Double.MAX_VALUE;
        for (Rob rob : this.robs) {
            double robAge = rob.getAge();
            ageSum += robAge;
            maxAge = Math.max(maxAge, robAge);
            minAge = Math.min(minAge, robAge);
        }
        double meanAge = ageSum / this.robs.size();
        statAge += minAge + "/" + String.format("%.2f", meanAge) + "/" + maxAge;
        return statAge;
    }
}
