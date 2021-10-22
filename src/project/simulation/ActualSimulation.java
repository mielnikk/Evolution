package project.simulation;

import project.parameters.Configuration;
import project.simulation.rob.Rob;
import project.simulation.universe.Board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Rzeczywistość, w której odbywa się symulacja.
 *
 * @author Katarzyna Mielnik
 */
public class ActualSimulation {
    private final ArrayList<Rob> robs;
    private final Board board;
    private final Configuration configuration;
    private final Data statistics;

    public ActualSimulation(Configuration configuration, Board board) {
        this.configuration = configuration;
        this.board = board;
        this.robs = new ArrayList<>();
        for (int i = 0; i < configuration.initialRobsNumber(); i++) {
            this.robs.add(new Rob(configuration, board));
        }
        this.statistics = new Data(this.robs, board);
    }

    /**
     * Przeprowadza całą symulację określoną liczbę razy, lub do momentu wyginęcia wszystkich robów.
     */
    public void runSimulation() {
        for (int i = 1; i <= this.configuration.roundsNumber(); i++) {
            this.board.nextRound();
            Collections.shuffle(this.robs);
            ArrayList<Rob> offspring = new ArrayList<>();
            for (Rob rob : this.robs) {
                rob.newRound(this.board);
                if (rob.willMultiply()) {
                    Rob child = rob.multiply();
                    offspring.add(child);
                }
            }
            addNewRobs(offspring);
            removeDeadRobs();
            if (this.robs.size() == 0) {
                System.out.println("Tura " + i + ". Brak żyjących robów. Zakończenie symulacji.");
                break;
            }
            this.statistics.printStatistics(i);

            if (i % configuration.printingFrequence() == 0)
                this.statistics.printSimulationState();
        }
        // Jeśli statystyki nie zostały wypisane po ostatniej turze.
        if (configuration.roundsNumber() % configuration.printingFrequence() != 0)
            this.statistics.printSimulationState();
    }

    private void addNewRobs(ArrayList<Rob> newRobs) {
        this.robs.addAll(newRobs);
    }

    private void removeDeadRobs() {
        this.robs.removeAll(this.robs.stream().filter((rob) -> !rob.isAlive()).collect(Collectors.toList()));
    }


}
