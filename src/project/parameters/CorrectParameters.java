package project.parameters;

import java.util.HashMap;

/**
 * Klasa umożliwiająca sprawdzenie poprawności parametrów wczytywanych z pliku.
 */
public class CorrectParameters {

    private static final CorrectParameters instancja = new CorrectParameters();

    private CorrectParameters() {
    }

    public static CorrectParameters getInstance() {
        return instancja;
    }

    private final String[] parametryInt = {"ile_tur", "pocz_ile_robów", "pocz_energia", "ile_daje_jedzenie",
            "ile_rośnie_jedzenie", "koszt_tury", "limit_powielania", "co_ile_wypisz"};

    private final String[] parametryDouble = {"pr_powielenia", "ułamek_energii_rodzica", "pr_usunięcia_instr",
            "pr_dodania_instr", "pr_zmiany_instr"};

    private final String[] parametryString = {"pocz_progr", "spis_instr"};

    /**
     * Znaki reprezentujące wszystkie dopuszczalne instrukcje.
     */
    private final char[] instrukcje = {'l', 'p', 'i', 'w', 'j'};

    /**
     * Sprawdza, czy parametr o poprawej nazwie jest prawidłowym parametrem typu {@code int}.
     *
     * @param x nazwa parametru
     */
    public boolean checkIntParameters(String x) {
        for (int i = 0; i < this.parametryInt.length; i++) {
            if (this.parametryInt[i].equals(x)) return true;
        }
        return false;
    }

    /**
     * Sprawdza, czy parametr o poprawej nazwie jest prawidłowym parametrem typu {@code double}.
     *
     * @param x nazwa parametru
     */
    public boolean checkDoubleParameters(String x) {
        for (int i = 0; i < this.parametryDouble.length; i++) {
            if (this.parametryDouble[i].equals(x)) return true;
        }
        return false;
    }

    /**
     * Sprawdza, czy parametr o poprawej nazwie jest prawidłowym napisem.
     *
     * @param x nazwa parametru
     */
    public boolean checkString(String x) {
        for (int i = 0; i < this.parametryString.length; i++) {
            if (this.parametryString[i].equals(x)) return true;
        }
        return false;
    }

    /**
     * Sprawdza, czy istnieje instrukcja reprezentowana przez znak {@code c}.
     */
    public boolean checkInstructionCorrectness(char c) {
        for (int i = 0; i < this.instrukcje.length; i++) {
            if (this.instrukcje[i] == c) return true;
        }
        return false;
    }

    public boolean checkValueRange(double d) {
        return d >= 0 && d <= 1;
    }

    public boolean checkValueRange(int x) {
        return x >= 0;
    }

    /**
     * Szuka nazwy parametru, którego nie ma w mapie {@code param}.
     *
     * @param param mapa, w której kluczem jest nazwa parametru
     * @return nazwa parametru, którego nie ma w {@code param}; jeśli nie ma takiego, zwraca {@code null}
     */
    public String findMissingParameter(HashMap<String, Object> param) {
        for (String s : this.parametryInt) {
            if (!param.containsKey(s)) return s;
        }

        for (String s : this.parametryDouble) {
            if (!param.containsKey(s)) return s;
        }

        for (String s : this.parametryString) {
            if (!param.containsKey(s)) return s;
        }
        return null;
    }
}
