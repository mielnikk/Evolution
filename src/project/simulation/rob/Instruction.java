package project.simulation.rob;

import project.parameters.CorrectParameters;
import project.simulation.universe.Board;

/**
 * Reprezentacja instrukcji. <p>Każda wartość enumeratora odpowiada poprawnej instrukcji, która może pojawić się w
 * spisie instrukcji. </p>
 *
 * @see CorrectParameters
 * @see Rob
 */
public enum Instruction {
    GO('i'),
    EAT('j'),
    SNIFF('w'),
    LEFT('l'),
    RIGHT('p');

    private final char assignedCharacter;

    Instruction(char c) {
        this.assignedCharacter = c;
    }

    /**
     * Zwraca instrukcję oznaczaną danym znakiem. Jeśli taka nie istnieje, zwraca {@code null}.
     */
    public static Instruction getInstruction(char c) {
        for (Instruction instr : Instruction.values()) {
            if (instr.assignedCharacter == c)
                return instr;
        }
        return null;
    }

    void executeInstruction(Rob rob, Board board) {
        switch (this) {
            case GO:
                rob.go(board);
                break;
            case SNIFF:
                rob.sniff(board);
                break;
            case EAT:
                rob.eat(board);
                break;
            case RIGHT:
                rob.turnRight();
                break;
            case LEFT:
                rob.turnLeft();
                break;
        }
    }
}
