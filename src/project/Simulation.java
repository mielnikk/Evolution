package project;

import project.parameters.MissingParameters;
import project.parameters.IncorrectData;
import project.parameters.Configuration;
import project.simulation.ActualSimulation;
import project.simulation.universe.UnknownCharacterOnBoard;
import project.simulation.universe.UnevenRows;
import project.simulation.universe.Board;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Zadanie 1: Ewolucja, czyli niech programy piszą się same.
 * <p>Tworzy nową symulowaną rzeczywistość na podstawie konfiguracji i planszy. Przeprowadza symulację. </p>
 *
 * @author Katarzyna Mielnik
 */
public class Simulation {

    public static void main(String[] args) throws FileNotFoundException {
        Configuration configuration;
        Board board;

        if (args.length < 2) {
            System.out.println("Two files are required.");
            return;
        }

        File parametersFile = new File(args[0]);
        try {
            configuration = new Configuration(parametersFile);
            configuration.parseData();
        }
        catch (FileNotFoundException | IncorrectData | MissingParameters e) {
            System.out.println(e.getMessage());
            return;
        }

        File boardFile = new File(args[1]);
        try {
            board = Board.createBoard(boardFile, configuration);
        }
        catch (UnknownCharacterOnBoard | UnevenRows e) {
            System.out.println(e.getMessage());
            return;
        }

        ActualSimulation s = new ActualSimulation(configuration, board);
        s.runSimulation();
    }
}
