package project.parameters;

/**
 * Wyjątek zgłaszany, gdy w pliku z parametrami nie ma wszystkich wymaganych parametrów.
 * @see CorrectParameters
 */
public class MissingParameters extends Exception{

    public MissingParameters() {
        super("Nie wczytano wszystkich parametrów.");
    }
}
