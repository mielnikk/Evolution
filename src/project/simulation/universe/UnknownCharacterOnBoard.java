package project.simulation.universe;

/**
 * Wyjątek zgłaszany, gdy w pliku z planszą znajduje się znak, który nie reprezentuje żadnego pola.
 */
public class UnknownCharacterOnBoard extends Exception {


    public UnknownCharacterOnBoard(char c){
        super("Niepoprawny znak na planszy: " + c + ".");
    }
}
