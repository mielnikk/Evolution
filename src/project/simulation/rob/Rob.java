package project.simulation.rob;

import project.parameters.Configuration;
import project.simulation.universe.spatial.Coordinates;
import project.simulation.universe.spatial.Direction;
import project.simulation.universe.Board;

import java.util.ArrayList;
import java.util.Random;

public class Rob {
    /**
     * Liczba tur, w których rob był aktywny. Rob, który powstał w danej turze ma wiek 0.
     */
    private int age;
    private int energy;
    private boolean willMultiply;
    private final ArrayList<Instruction> program;
    private Direction direction;
    private Coordinates coordinates;

    private final int singleRoundEnergyCost;
    private final double partOfParentEnergy;
    private final int multiplicationLimit;
    private final double multiplicationOdds;

    private final double instructionRemovalOdds;
    private final double instructionChangeOdds;
    private final double instructionAddingOdds;
    private final ArrayList<Instruction> instructionSet;


    public Rob(Configuration configuration, Board board) {
        this.age = 0;
        this.energy = configuration.initialEnergy();
        this.program = configuration.initialProgram();
        this.singleRoundEnergyCost = configuration.singleRoundEnergyCost();
        this.partOfParentEnergy = configuration.partOfParentEnergy();
        this.multiplicationLimit = configuration.multiplicationLimit();
        this.multiplicationOdds = configuration.multiplicationOdds();
        this.instructionRemovalOdds = configuration.instructionRemovalOdds();
        this.instructionChangeOdds = configuration.instructionChangeOdds();
        this.instructionAddingOdds = configuration.instructionAddingOdds();
        this.instructionSet = configuration.instructionList();
        this.willMultiply = drawMultiplicationLots();
        // Początkowa pozycja oraz kierunek są losowe.
        this.coordinates = board.getRandomPosition();
        this.direction = Direction.getRandomDirection();
    }

    /**
     * Tworzy nowego roba na podstawie przekazanych parametrów.
     * <p> Podczas powielania rob - rodzic tworzy nowego roba przekazując mu wartości atrybutów.</p>
     *
     * @see #multiply
     */
    private Rob(int energy, ArrayList<Instruction> program, Direction direction, Coordinates coordinates, int singleRoundEnergyCost,
                double partOfParentEnergy, int multiplicationLimit, double multiplicationOdds, double instructionRemovalOdds,
                double instructionChangeOdds, double instructionAddingOdds, ArrayList<Instruction> instructionSet) {
        this.age = 0;
        this.energy = energy;
        this.program = program;
        this.coordinates = coordinates;
        this.direction = direction;
        this.singleRoundEnergyCost = singleRoundEnergyCost;
        this.partOfParentEnergy = partOfParentEnergy;
        this.multiplicationLimit = multiplicationLimit;
        this.multiplicationOdds = multiplicationOdds;
        this.instructionRemovalOdds = instructionRemovalOdds;
        this.instructionChangeOdds = instructionChangeOdds;
        this.instructionAddingOdds = instructionAddingOdds;
        this.instructionSet = instructionSet;
    }


    public boolean isAlive() {
        return this.energy >= 0;
    }

    /**
     * Wykonuje instrukcję z programu roba ({@code program}) zapisaną pod indeksem {@code index}.
     *
     * @param index indeks instrukcji
     * @param board plansza, na której znajduje się rob
     */
    public void executeInstruction(int index, Board board) {
        this.program.get(index).executeInstruction(this, board);
    }

    /**
     * Zjada jedzenie z obecnego pola.
     *
     * @param board plansza, na której znajduje się rob
     */
    private void eatFromCurrentSquare(Board board) {
        this.energy += board.eatFoodFromSquare(this.coordinates);
    }

    /**
     * Przechodzi o jedno pole zgodnie ze swoim kierunkiem oraz zjada jedzenie z nowego pola.
     *
     * @param board plansza, na której znajduje się rob
     */
    void go(Board board) {
        this.coordinates = board.calculateCoordinates(this.coordinates, this.direction);
        if (board.foodAtCoordinates(this.coordinates))
            this.eatFromCurrentSquare(board);
    }

    /**
     * Szuka jedzenia na czterech najbliższych polach obok oraz po przekątnej. Przechodzi na pole z jedzeniem
     * i zjada z niego jedzenie.
     *
     * @param board plansza, na której znajduje się rob
     */
    void eat(Board board) {
        Coordinates coordinates = null;
        boolean foundFood = false;
        loop:
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;
                coordinates = board.calculateCoordinates(this.coordinates.X() + i, this.coordinates.Y() + j);
                if (board.foodAtCoordinates(coordinates)) {
                    foundFood = true;
                    break loop;
                }
            }
        }
        if (foundFood) {
            this.coordinates = coordinates;
            this.eatFromCurrentSquare(board);
        }
    }

    /**
     * Obraca roba w kierunku jednego z czterech sąsiednich pól, jeśli znajduje się na nim jedzenie.
     *
     * @param board plansza, na której znajduje się rob
     */
    void sniff(Board board) {
        for (int i = 0; i < 4; i++) {
            this.direction = this.direction.turnRight();
            Coordinates coordinates = board.calculateCoordinates(this.coordinates, this.direction);
            if (board.foodAtCoordinates(coordinates))
                break;
        }
    }

    /**
     * Obraca roba w lewo o 90 stopni.
     */
    void turnLeft() {
        this.direction = this.direction.turnLeft();
    }

    /**
     * Obraca roba w prawo o 90 stopni.
     */
    void turnRight() {
        this.direction = this.direction.turnRight();
    }

    /**
     * Wykonuje program roba do końca, lub do momentu, w którym zabraknie mu energii.
     *
     * @param board plansza, na której znajduje się rob
     */
    public void executeProgram(Board board) {
        int index = 0;
        while (this.energy >= 0 && this.program.size() > index) {
            executeInstruction(index, board);
            this.energy--;
            index++;
        }
    }

    /**
     * Zmienia stan roba wynikający z rozpoczęcia nowej tury. Rozpoczyna wykonywanie programu roba.
     *
     * @param board plansza, na której znajduje się rob
     */
    public void newRound(Board board) {
        this.age++;
        this.energy = this.energy - this.singleRoundEnergyCost;
        executeProgram(board);
        this.willMultiply = drawMultiplicationLots() && this.energy >= this.multiplicationLimit;
    }

    private boolean drawMultiplicationLots() {
        Random random = new Random();
        return random.nextDouble() <= this.multiplicationOdds;
    }

    /**
     * Zmienia (mutuje) program nowego roba z ustalonym prawdopodobieństwem.
     *
     * @param program program nowego roba
     */
    private void mutateProgram(ArrayList<Instruction> program) {
        Random random = new Random();

        if (random.nextDouble() <= this.instructionRemovalOdds && program.size() > 0)
            program.remove(program.size() - 1);

        if (random.nextDouble() <= this.instructionAddingOdds) {
            Instruction randomInstruction = this.instructionSet.get(random.nextInt(this.instructionSet.size()));
            program.add(randomInstruction);
        }

        if (random.nextDouble() <= this.instructionChangeOdds && program.size() > 0) {
            int randomPosition = random.nextInt(program.size());
            program.set(randomPosition, this.instructionSet.get(random.nextInt(this.instructionSet.size())));
        }
    }

    /**
     * Tworzy program nowego roba.
     */
    @SuppressWarnings("unchecked")
    private ArrayList<Instruction> createChildProgram() {
        ArrayList<Instruction> childProgram = (ArrayList<Instruction>) this.program.clone();
        mutateProgram(childProgram);
        return childProgram;
    }

    /**
     * Powiela roba.
     * <p>Nowy rob dostaje zmutowaną kopię programu rodzica oraz część energii rodzica. Atrybuty wynikające z
     * konfiguracji są takie same u obu robów. </p>
     *
     * @return nowy rob
     */
    public Rob multiply() {
        if (!willMultiply || this.energy < this.multiplicationLimit) return null;

        ArrayList<Instruction> childProgram = createChildProgram();
        int childEnergy = (int) (((double) this.energy) * this.partOfParentEnergy);
        this.energy -= childEnergy;
        return new Rob(childEnergy, childProgram, this.direction.getOpposite(), this.coordinates, this.singleRoundEnergyCost,
                this.partOfParentEnergy, this.multiplicationLimit, this.multiplicationOdds, this.instructionRemovalOdds,
                this.instructionChangeOdds, this.instructionAddingOdds, this.instructionSet);
    }

    /**
     * Sprawdza, czy w obecnej turze rob powieli się.
     */
    public boolean willMultiply() {
        return this.willMultiply;
    }

    public int getProgramLength() {
        return this.program.size();
    }

    public int getAge() {
        return this.age;
    }

    public int getEnergyLevel() {
        return this.energy;
    }

    /**
     * Tworzy opis stanu roba
     */
    @Override
    public String toString() {
        return "Rob: " + "wiek: " + this.age + ", " + "energia: " + this.energy + ", " +
                "pozycja " + "(" + this.coordinates.X() + ", " + this.coordinates.Y() + ")";
    }
}
