import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class Main {
    private static int[][][][] setOfBlockArrays = { // [7][4][?][?]
            {
                    {
                            { 10, 10 },
                            { 10, 10 }
                    },
                    {
                            { 10, 10 },
                            { 10, 10 }
                    },
                    {
                            { 10, 10 },
                            { 10, 10 }
                    },
                    {
                            { 10, 10 },
                            { 10, 10 }
                    }
            },
            {
                    {
                            {0, 20, 0},
                            {20, 20, 20},
                            {0, 0, 0},
                    },
                    {
                            {0, 20, 0},
                            {0, 20, 20},
                            {0, 20, 0},
                    },
                    {
                            {0, 0, 0},
                            {20, 20, 20},
                            {0, 20, 0},
                    },
                    {
                            {0, 20, 0},
                            {20, 20, 0},
                            {0, 20, 0},
                    },
            },
            {
                    {
                            {30, 0, 0},
                            {30, 30, 30},
                            {0, 0, 0},
                    },
                    {
                            {0, 30, 30},
                            {0, 30, 0},
                            {0, 30, 0},
                    },
                    {
                            {0, 0, 0},
                            {30, 30, 30},
                            {0, 0, 30},
                    },
                    {
                            {0, 30, 0},
                            {0, 30, 0},
                            {30, 30, 0},
                    },
            },
            {
                    {
                            {0, 0, 40},
                            {40, 40, 40},
                            {0, 0, 0},
                    },
                    {
                            {0, 40, 0},
                            {0, 40, 0},
                            {0, 40, 40},
                    },
                    {
                            {0, 0, 0},
                            {40, 40, 40},
                            {40, 0, 0},
                    },
                    {
                            {40, 40, 0},
                            {0, 40, 0},
                            {0, 40, 0},
                    },
            },
            {
                    {
                            {0, 50, 0},
                            {50, 50, 0},
                            {50, 0, 0},
                    },
                    {
                            {50, 50, 0},
                            {0, 50, 50},
                            {0, 0, 0},
                    },
                    {
                            {0, 50, 0},
                            {50, 50, 0},
                            {50, 0, 0},
                    },
                    {
                            {50, 50, 0},
                            {0, 50, 50},
                            {0, 0, 0},
                    },
            },
            {
                    {
                            {0, 60, 0},
                            {0, 60, 60},
                            {0, 0, 60},
                    },
                    {
                            {0, 0, 0},
                            {0, 60, 60},
                            {60, 60, 0},
                    },
                    {
                            {0, 60, 0},
                            {0, 60, 60},
                            {0, 0, 60},
                    },
                    {
                            {0, 0, 0},
                            {0, 60, 60},
                            {60, 60, 0},
                    },
            },
            {
                    {
                            {0, 0, 0, 0},
                            {70, 70, 70, 70},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0},
                    },
                    {
                            {0, 70, 0, 0},
                            {0, 70, 0, 0},
                            {0, 70, 0, 0},
                            {0, 70, 0, 0},
                    },
                    {
                            {0, 0, 0, 0},
                            {70, 70, 70, 70},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0},
                    },
                    {
                            {0, 70, 0, 0},
                            {0, 70, 0, 0},
                            {0, 70, 0, 0},
                            {0, 70, 0, 0},
                    },
            },
    }; // end of setOfBlockArrays
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static String line = null;
    private static int nKeys = 0;
    private static char getKey() throws IOException {
        char ch;
        if (nKeys != 0) {
            ch = line.charAt(line.length() - nKeys);
            nKeys--;
            return ch;
        }
        do {
            line = br.readLine();
            nKeys = line.length();
        } while (nKeys == 0);
        ch = line.charAt(0);
        nKeys--;
        return ch;
    }
    public static void drawMatrix(Matrix m) {
        int dy = m.get_dy();
        int dx = m.get_dx();
        int array[][] = m.get_array();
        for (int y=0; y < dy; y++) {
            for (int x=0; x < dx; x++) {
                if (array[y][x] == 0) System.out.print("□ ");
                else if (array[y][x] == 30) System.out.print("■ ");
                else System.out.print("X ");
            }
            System.out.println();
        }
    }
    public static void drawCMatrix(Matrix m) {
        int dy = m.get_dy();
        int dx = m.get_dx();
        int array[][] = m.get_array();
        for (int y=0; y < dy; y++) {
            for (int x=0; x < dx; x++) {
                if (array[y][x] == 0) System.out.print("□ ");
                else if (array[y][x] == 10) System.out.print("♤ ");
                else if (array[y][x] == 20) System.out.print("♧ ");
                else if (array[y][x] == 30) System.out.print("♡ ");
                else if (array[y][x] == 40) System.out.print("♢ ");
                else if (array[y][x] == 50) System.out.print("♔ ");
                else if (array[y][x] == 60) System.out.print("♕ ");
                else if (array[y][x] == 70) System.out.print("♖ ");
                else System.out.print("X ");
            }
            System.out.println();
        }
    }
    public static void main(String[] args) throws Exception {
        char key;
        TetrisState state;
        CTetris.init(setOfBlockArrays);
        CTetris board = new CTetris(15, 10);
        Random random = new Random();
        key = (char) ('0' + random.nextInt(7));
        board.accept(key);
        drawCMatrix(board.get_oCScreen()); System.out.println();

        while ((key = getKey()) != 'q') {
            state = board.accept(key);
            drawCMatrix(board.get_oCScreen()); System.out.println();
            if (state == TetrisState.NewBlock) {
                key = (char) ('0' + random.nextInt(7));
                state = board.accept(key);
                drawCMatrix(board.get_oCScreen()); System.out.println();
                if (state == TetrisState.Finished) break; // Game Over!
            }
        }
        System.out.println("Program terminated!");
    }
}
