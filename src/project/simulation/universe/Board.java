package project.simulation.universe;

import project.parameters.Configuration;
import project.simulation.universe.spatial.Direction;
import project.simulation.universe.spatial.Coordinates;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Implementacja planszy, na której odbywa się symulacja.
 * <p> Odnośnie współrzędnych na planszy : pole w wierszu wyżej ma mniejszą współrzędną {@code y}. Współrzędna {@code
 * x} rośnie od lewej do prawej strony planszy.
 * </p>
 *
 * @author Katarzyna Mielnik
 */
public class Board {
    private final int sizeX;
    private final int sizeY;
    private int foodSquaresNumber;
    // Do pola o współrzędnych (x, y) należy odwoływać się pola[y][x].
    // Aby przejść do góry (odpowiednio na dół) należy odwołać się do mniejszej (większej) współrzędnej y
    private final Square[][] squares;


    private Board(Square[][] squares) {
        this.squares = squares;
        this.sizeY = squares.length;
        this.sizeX = squares[0].length;
        this.foodSquaresNumber = 0;
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                if (squares[i][j].containsFood())
                    this.foodSquaresNumber++;
            }
        }
    }

    /**
     * Tworzy planszę symulacji na podstawie pliku {@code file}.
     *
     * @param file         plik z planszą
     * @param configuration konfiguracja symulacji
     * @return plansza utworzona na podstawie pliku
     * @throws FileNotFoundException    nie znaleziono pliku
     * @throws UnknownCharacterOnBoard na planszy znajduje się niepoprawny znak
     * @throws UnevenRows          wiersze w pliku nie są równej długości
     */
    public static Board createBoard(File file, Configuration configuration)
            throws FileNotFoundException, UnknownCharacterOnBoard, UnevenRows {
        Scanner scanner = new Scanner(file);
        Square[][] squares = new Square[0][]; // Tablica wierszy.
        int arraySize = 0;
        int lastLineLength = 0;
        int lineNumber = 0;
        while (scanner.hasNextLine()) {
            lineNumber++;
            String s = scanner.nextLine();
            Square[] line;

            // Próbuje utworzyć kolejny wiersz planszy.
            try {
                line = createBoardRow(s, configuration);
            }
            catch (UnknownCharacterOnBoard n) {
                scanner.close();
                throw n;
            }

            if (lineNumber != 1 && lastLineLength != s.length()) {
                scanner.close();
                throw new UnevenRows();
            }
            lastLineLength = s.length();

            if (lineNumber - 1 == arraySize) {
                arraySize = arraySize * 2 + 1;
                squares = Arrays.copyOf(squares, arraySize);
            }

            squares[lineNumber - 1] = line;
        }
        scanner.close();
        // Resize tablicy, aby nie było pustych pól.
        squares = Arrays.copyOf(squares, lineNumber);
        return new Board(squares);
    }

    /**
     * Tworzy wiersz pól.
     *
     * @param s            zapis wiersza w postaci {@code String}
     * @param configuration konfiguracja symulacji
     * @return tablica typu {@code Square}
     * @throws UnknownCharacterOnBoard w wierszu znajduje się niepoprawny znak
     * @see Square
     */
    private static Square[] createBoardRow(String s, Configuration configuration) throws UnknownCharacterOnBoard {
        Square[] row = new Square[s.length()];
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ')
                row[i] = new EmptySquare();
            else if (s.charAt(i) == 'x')
                row[i] = new FoodSquare(configuration.energyFromFood(), configuration.foodRipeningTime());
            else
                throw new UnknownCharacterOnBoard(s.charAt(0));
        }
        return row;
    }

    /**
     * Zwraca współrzędne losowej pozycji na planszy.
     * @see Coordinates
     */
    public Coordinates getRandomPosition() {
        return Coordinates.getRandomCoordinates(this.sizeX, this.sizeY);
    }

    /**
     * Oblicza współrzędne sąsiedniego pola, na które wskazuje {@code direction}.
     *
     * @param coord współrzędne obecnego pola
     * @param direction kierunek
     * @return współrzędne pola, które leży w kierunku {@code direction} od obecnego pola
     */
    public Coordinates calculateCoordinates(Coordinates coord, Direction direction) {
        return calculateCoordinates(coord.X() + direction.xCoordinate(), coord.Y() + direction.yCoordinate());
    }

    /**
     * Oblicza współrzędne na planszy na podstawie pozycji w dwóch wymiarach.
     * Współrzędne są obliczane tak, aby nie wskazywały na nieistniejące pole poza planszą.
     * @param posX pozycja w wierszu
     * @param posY pozycja w kolumnie
     * @return współrzędne wynikające z pozycji, uwzględniające wymiary planszy
     */
    public Coordinates calculateCoordinates(int posX, int posY) {
        int nowaPozX = Math.floorMod(posX, this.sizeX);
        int nowaPozY = Math.floorMod(posY, this.sizeY);
        return new Coordinates(nowaPozX, nowaPozY);
    }

    /**
     * Sprawdza, czy na polu o współrzędnych {@code coordinates} znajduje się jedzenie.
     *
     * @param coordinates współrzędne pola
     * @return prawda wtedy i tylko wtedy, gdy na polu znajduje się jedzenie
     */
    public boolean foodAtCoordinates(Coordinates coordinates) {
        return this.squares[coordinates.Y()][coordinates.X()].containsFood();
    }

    /**
     * Wprowadza na planszy zmiany wynikające z przejścia do nowej tury.
     * Aktualizuje {@code liczbaPólZJedzeniem} o liczbę pól, na których zregenerowało się jedzenie.
     */
    public void nextRound() {
        for (int i = 0; i < this.squares.length; i++) {
            for (int j = 0; j < this.squares[i].length; j++) {
                boolean previousState = this.squares[i][j].containsFood();
                this.squares[i][j].nextRound();
                if (this.squares[i][j].containsFood() != previousState) {
                    this.foodSquaresNumber++;
                }
            }
        }
    }

    /**
     * Przeprowadza procedurę spożycia jedzenia na polu o określonych współrzędnych.
     *
     * @param squareCoordinates współrzędne pola, na którym rob zje jedzenie
     * @return wartość energii, którą daje zjedzone jedzenie
     */
    public int eatFoodFromSquare(Coordinates squareCoordinates) {
        if (this.squares[squareCoordinates.Y()][squareCoordinates.X()].containsFood()) {
            this.foodSquaresNumber--;
            return ((FoodSquare) this.squares[squareCoordinates.Y()][squareCoordinates.X()]).eatFood();
        }
        else return 0;
    }

    /**
     * Sprawdza liczbę pól z jedzeniem. Dla celów statystyczych.
     *
     * @return liczba pól zawierających jedzenie
     */
    public int foodSquaresNumber() {
        return foodSquaresNumber;
    }
}
