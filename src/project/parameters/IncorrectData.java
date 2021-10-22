package project.parameters;

/**
 * Wyjątek zgłaszany, gdy w pliku z parametrami występują nieprawidłowe dane.
 * Parametr może mieć nieznaną nazwę, niepoprawny typ lub wartość.
 *
 * @see CorrectParameters
 */
public class IncorrectData extends Exception {

    /**
     * Sytuacja, w której początkowy program zawiera instrukcje spoza spisu lub spis zawiera nieznane instrukcje.
     * @param c niepoprawny znak
     */
    public IncorrectData(char c) {
        super("Nieprawidłowa instrukcja: " + c);
    }

    /**
     * @param nrLinii numer linii pliku z parametrami, w której znajduje się błąd
     * @param message wiadomość dotycząca błędu
     */
    public IncorrectData(int nrLinii, String message) {
        super("Linia " + nrLinii + ". " + message);
    }
}
