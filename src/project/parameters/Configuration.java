package project.parameters;

import project.simulation.rob.Instruction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Klasa zawierająca parametry symulacji wczytane z pliku.
 * <p> Aby móc korzystać z parametów należy, oprócz stworzenia obiektu klasy {@code Konfiguracja}, wywołać funkcję
 * {@code przetwarzajDane}, która odczytuje oraz zapisuje parametry z pliku {@code plik} do atrybutów. Jeśli dane
 * nie zostały przetworzone, gettery nie zwrócą poprawnych dla symulacji danych.</p>
 *
 * @author Katarzyna Mielnik
 */
public class Configuration {

    private boolean parsedFile;
    private final File file;
    private final HashMap<String, Object> parameters;


    public Configuration(File file) {
        this.file = file;
        this.parsedFile = false;
        this.parameters = new HashMap<>();
    }

    /**
     * Sprawdza, czy plik z parametrami został przetworzony, a parametry zostały zapisane w konfiguracji.
     *
     * @return prawda, wtedy i tylko wtedy, gdy {@code plik} został poprawnie przetworzony
     */
    public boolean czyWczytanoDane() {
        return this.parsedFile;
    }

    /**
     * Przetwarza {@code plik} i zapisuje parametry w mapie {@code parameters}.
     *
     * @throws FileNotFoundException brak pliku
     * @throws IncorrectData            dane są niezgodne z wymaganiami
     * @throws MissingParameters    nie wczytano wszystkich wymaganych parametrów
     */
    @SuppressWarnings("unchecked")
    public void parseData() throws FileNotFoundException, IncorrectData, MissingParameters {
        if (this.parsedFile)
            return;

        Scanner scanner = new Scanner(this.file);
        int lineNumber = 0;

        while (scanner.hasNextLine()) {
            try {
                parseLine(++lineNumber, scanner.nextLine());
            }
            catch (IncorrectData e) {
                scanner.close();
                throw e;
            }
        }

        scanner.close();
        if (!checkProgramCorectness((ArrayList<Instruction>) this.parameters.get("pocz_progr"),
                (ArrayList<Instruction>) this.parameters.get("spis_instr")))
            throw new IncorrectData(lineNumber, "Program zawiera instrukcje spoza spisu.");

        if (!allParametersRead())
            throw new MissingParameters();

        this.parsedFile = true;
    }

    /**
     * Sprawdza, czy wszystkie parametry potrzebne do przeprowadzenia symulacji zostały wczytane do mapy {@code
     * parameters}.
     * Zakłada, że w mapie nie ma kluczy o niepoprawnych nazwach.
     *
     * @return prawda, wtedy i tylko wtedy, gdy wczytano wszystkie niezbędne parametry
     */
    private boolean allParametersRead() {
        String s = CorrectParameters.getInstance().findMissingParameter(this.parameters);
        return s == null;
    }

    /**
     * Zapisuje parametr z pojedynczej linii.
     *
     * @param lineNumber numer linii, która jest przetwarzana
     * @param line   linia, która jest przetwarzana
     * @throws IncorrectData jeśli nazwa parametru powtarza się lub jest niepoprawna, jeśli w linii znajdują się dodatkowe
     *                    znaki
     */
    private void parseLine(int lineNumber, String line) throws IncorrectData {
        Scanner lineScanner = new Scanner(line);
        String parameterName = lineScanner.next();

        if (this.parameters.containsKey(parameterName)) {
            throw new IncorrectData(lineNumber, "Powtarzający się parametr");
        }

        // Działanie w zależności od nazwy parametru.
        try {
            if (CorrectParameters.getInstance().checkIntParameters(parameterName))
                readInt(parameterName, lineScanner, lineNumber);

            else if (CorrectParameters.getInstance().checkDoubleParameters(parameterName))
                readDouble(parameterName, lineScanner, lineNumber);

            else if (CorrectParameters.getInstance().checkString(parameterName))
                this.parameters.put(parameterName, createInstructionList(lineScanner.next()));

            else
                throw new IncorrectData(lineNumber, "Niepoprawna nazwa parametru.");
        }
        catch (IncorrectData e) {
            lineScanner.close();
            throw e;
        }

        boolean tooManyArguments = lineScanner.hasNext();
        lineScanner.close();
        // W przypadku, gdy w linii znajduje się coś jeszcze, a nie powinno.
        if (tooManyArguments)
            throw new IncorrectData(lineNumber, "Nieprawidłowa liczba parametrów w linii.");
    }

    /**
     * Wczytuje parametr typu {@code int}.
     *
     * @param parameter nazwa parametru
     * @param sc       obiekt klasy {@code Scanner}
     * @param lineNumber  numer linii, z której parametr jest wczytywany
     * @throws IncorrectData kiedy wartość parametru nie jest typu {@code int} lub nie mieści się w poprawnym zakresie
     */
    private void readInt(String parameter, Scanner sc, int lineNumber) throws IncorrectData {
        if (!sc.hasNextInt())
            throw new IncorrectData(lineNumber, "Niepoprawny typ.");

        int value = sc.nextInt();
        if (!CorrectParameters.getInstance().checkValueRange(value))
            throw new IncorrectData(lineNumber, "Wartość wykracza poza zakres.");

        this.parameters.put(parameter, value);
    }

    /**
     * Wczytuje parametr typu {@code double}.
     *
     * @param parameter nazwa parametru
     * @param sc       obiekt klasy {@code Scanner}
     * @param lineNumber  numer linii, z której parametr jest wczytywany
     * @throws IncorrectData kiedy wartość parametru nie jest typu {@code double} lub nie mieści się w poprawnym zakresie
     */
    private void readDouble(String parameter, Scanner sc, int lineNumber) throws IncorrectData {
        if (!sc.hasNextDouble())
            throw new IncorrectData(lineNumber, "Niepoprawny typ.");

        double value = sc.nextDouble();
        if (!CorrectParameters.getInstance().checkValueRange(value))
            throw new IncorrectData(lineNumber, "Wartość wykracza poza zakres.");

        this.parameters.put(parameter, value);
    }

    /**
     * Tworzy listę znaków reprezentujących instrukcje.
     *
     * @param s ciąg znaków
     * @return lista znaków
     * @throws IncorrectData pewien znak nie reprezentuje żadnej instrukcji.
     */
    private ArrayList<Instruction> createInstructionList(String s) throws IncorrectData {
        ArrayList<Instruction> instructions = new ArrayList<>();

        for (int i = 0; i < s.length(); i++) {
            if (CorrectParameters.getInstance().checkInstructionCorrectness(s.charAt(i)))
                instructions.add(Instruction.getInstruction(s.charAt(i)));
            else
                throw new IncorrectData(s.charAt(i));
        }
        return instructions;
    }

    /**
     * Sprawdza, czy początkowy program zawiera instrukcje wyłącznie ze spisu instrukcji.
     *
     * @param program początkowy program robów
     * @param list    spis instrukcji dopuszczalnych w symulacji
     * @return prawda, wtedy i tylko wtedy, gdy każdy znak z {@code program} zawiera się w {@code list}
     */
    private boolean checkProgramCorectness(ArrayList<Instruction> program, ArrayList<Instruction> list) {
        for (Instruction i : program) {
            if (!list.contains(i))
                return false;
        }
        return true;
    }


    public int roundsNumber() {
        if (!this.parsedFile) return -1;
        return (int) this.parameters.get("ile_tur");
    }

    public int initialRobsNumber() {
        if (!this.parsedFile) return -1;
        return (int) this.parameters.get("pocz_ile_robów");
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Instruction> initialProgram() {
        if (!this.parsedFile) return null;
        return (ArrayList<Instruction>) this.parameters.get("pocz_progr");
    }

    public int initialEnergy() {
        if (!this.parsedFile) return -1;
        return (int) this.parameters.get("pocz_energia");
    }

    public int energyFromFood() {
        if (!this.parsedFile) return -1;
        return (int) this.parameters.get("ile_daje_jedzenie");
    }

    public int foodRipeningTime() {
        if (!this.parsedFile) return -1;
        return (int) this.parameters.get("ile_rośnie_jedzenie");
    }

    public double multiplicationOdds() {
        if (!this.parsedFile) return -1;
        return (double) this.parameters.get("pr_powielenia");
    }

    public int singleRoundEnergyCost() {
        if (!this.parsedFile) return -1;
        return (int) this.parameters.get("koszt_tury");
    }

    public double partOfParentEnergy() {
        if (!this.parsedFile) return -1;
        return (double) this.parameters.get("ułamek_energii_rodzica");
    }

    public int multiplicationLimit() {
        if (!this.parsedFile) return -1;
        return (int) this.parameters.get("limit_powielania");
    }

    public double instructionRemovalOdds() {
        if (!this.parsedFile) return -1;
        return (double) this.parameters.get("pr_usunięcia_instr");
    }

    public double instructionAddingOdds() {
        if (!this.parsedFile) return -1;
        return (double) this.parameters.get("pr_dodania_instr");
    }

    public double instructionChangeOdds() {
        if (!this.parsedFile) return -1;
        return (double) this.parameters.get("pr_zmiany_instr");
    }

    public int printingFrequence() {
        if (!this.parsedFile) return -1;
        return (int) this.parameters.get("co_ile_wypisz");
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Instruction> instructionList() {
        if (!this.parsedFile) return null;
        return (ArrayList<Instruction>) this.parameters.get("spis_instr");
    }
}
