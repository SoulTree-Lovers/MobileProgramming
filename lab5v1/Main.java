import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws Exception { main2(args); }
    private static int[][][][] setOfBlockArrays = { // [7][4][?][?]
            {
                    {
                            { 1, 1 },
                            { 1, 1 }
                    },
                    {
                            { 1, 1 },
                            { 1, 1 }
                    },
                    {
                            { 1, 1 },
                            { 1, 1 }
                    },
                    {
                            { 1, 1 },
                            { 1, 1 }
                    }
            },
            {
                    {
                            {0, 1, 0},
                            {1, 1, 1},
                            {0, 0, 0},
                    },
                    {
                            {0, 1, 0},
                            {0, 1, 1},
                            {0, 1, 0},
                    },
                    {
                            {0, 0, 0},
                            {1, 1, 1},
                            {0, 1, 0},
                    },
                    {
                            {0, 1, 0},
                            {1, 1, 0},
                            {0, 1, 0},
                    },
            },
            {
                    {
                            {1, 0, 0},
                            {1, 1, 1},
                            {0, 0, 0},
                    },
                    {
                            {0, 1, 1},
                            {0, 1, 0},
                            {0, 1, 0},
                    },
                    {
                            {0, 0, 0},
                            {1, 1, 1},
                            {0, 0, 1},
                    },
                    {
                            {0, 1, 0},
                            {0, 1, 0},
                            {1, 1, 0},
                    },
            },
            {
                    {
                            {0, 0, 1},
                            {1, 1, 1},
                            {0, 0, 0},
                    },
                    {
                            {0, 1, 0},
                            {0, 1, 0},
                            {0, 1, 1},
                    },
                    {
                            {0, 0, 0},
                            {1, 1, 1},
                            {1, 0, 0},
                    },
                    {
                            {1, 1, 0},
                            {0, 1, 0},
                            {0, 1, 0},
                    },
            },
            {
                    {
                            {0, 1, 0},
                            {1, 1, 0},
                            {1, 0, 0},
                    },
                    {
                            {1, 1, 0},
                            {0, 1, 1},
                            {0, 0, 0},
                    },
                    {
                            {0, 1, 0},
                            {1, 1, 0},
                            {1, 0, 0},
                    },
                    {
                            {1, 1, 0},
                            {0, 1, 1},
                            {0, 0, 0},
                    },
            },
            {
                    {
                            {0, 1, 0},
                            {0, 1, 1},
                            {0, 0, 1},
                    },
                    {
                            {0, 0, 0},
                            {0, 1, 1},
                            {1, 1, 0},
                    },
                    {
                            {0, 1, 0},
                            {0, 1, 1},
                            {0, 0, 1},
                    },
                    {
                            {0, 0, 0},
                            {0, 1, 1},
                            {1, 1, 0},
                    },
            },
            {
                    {
                            {0, 0, 0, 0},
                            {1, 1, 1, 1},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0},
                    },
                    {
                            {0, 1, 0, 0},
                            {0, 1, 0, 0},
                            {0, 1, 0, 0},
                            {0, 1, 0, 0},
                    },
                    {
                            {0, 0, 0, 0},
                            {1, 1, 1, 1},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0},
                    },
                    {
                            {0, 1, 0, 0},
                            {0, 1, 0, 0},
                            {0, 1, 0, 0},
                            {0, 1, 0, 0},
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
    public static void main2(String[] args) throws Exception {
        char key;
        TetrisState state;
        Tetris.init(setOfBlockArrays);

        // myOn~~ 객체를 새로 만들어서 플러그인 구조로 설계
        OnLeft myOnLeft = new OnLeft() {
            public void run(Tetris t, char key) throws Exception { t.left = t.left - 1; }
        };
        OnRight myOnRight = new OnRight() {
            public void run(Tetris t, char key) throws Exception { t.left = t.left + 1; }
        };
        OnDown myOnDown = new OnDown() {
            public void run(Tetris t, char key) throws Exception { t.top = t.top + 1; }
        };
        OnUp myOnUp = new OnUp() {
            public void run(Tetris t, char key) throws Exception { t.top = t.top - 1; }
        };
        OnCw myOnCw = new OnCw() {
            public void run(Tetris t, char key) throws Exception {
                t.idxBlockDegree = (t.idxBlockDegree+1)%t.nBlockDegrees;
                t.currBlk = t.setOfBlockObjects[t.idxBlockType][t.idxBlockDegree];
            }
        };
        OnCcw myOnCcw = new OnCcw() {
            public void run(Tetris t, char key) throws Exception {
                t.idxBlockDegree = (t.idxBlockDegree+t.nBlockDegrees-1)%t.nBlockDegrees;
                t.currBlk = t.setOfBlockObjects[t.idxBlockType][t.idxBlockDegree];
            }
        };
        OnNewBlock myOnNewBlock = new OnNewBlock() {
            public void run(Tetris t, char key) throws Exception {
                t.oScreen = deleteFullLines(t.oScreen, t.currBlk, t.top, t.iScreenDy, t.iScreenDx, t.iScreenDw);
                t.iScreen.paste(t.oScreen, 0, 0);
                t.idxBlockType = key - '0';
                t.idxBlockDegree = 0;
                t.currBlk = t.setOfBlockObjects[t.idxBlockType][t.idxBlockDegree];
                t.top = 0;
                t.left = t.iScreenDw + t.iScreenDx/2 - (t.currBlk.get_dx()+1)/2;
            }
            private Matrix deleteFullLines(Matrix screen, Matrix blk, int top, int dy, int dx, int dw) throws Exception {
                Matrix line, zero, temp;
                if (blk == null) return screen;
                int cy, y, nDeleted = 0,nScanned = blk.get_dy();
//                System.out.println("top, dy, dx, blk.get_dy() "+ top + ", " + dy+ ", " + dx + ", "+ blk.get_dy());

                // 블록의 공백 부분이 바닥 벽을 침범했을 경우, 그 부분은 스캔하지 않도록 하기 위한 코드
                if (top + blk.get_dy() - 1 >= dy) {
//                    System.out.println("전 nScanned: " + nScanned);
                    nScanned -= (top + blk.get_dy() - dy);
//                    System.out.println("후 nScanned: " + nScanned);
                }

                zero = new Matrix(1, dx);
                for (y = nScanned - 1; y >= 0 ; y--) {
                    cy = top + y + nDeleted; // 현재 스캔할 y좌표
                    line = screen.clip(cy, 0, cy + 1, screen.get_dx()); // 현재 y좌표를 기준으로 아래로 한 줄 자르기
                    if (line.sum() == screen.get_dx()) { // 만약 줄이 꽉 찼다면, 해당 줄 제거
                        temp = screen.clip(0, 0, cy, screen.get_dx());
                        screen.paste(temp, 1, 0);
                        screen.paste(zero, 0, dw);
                        nDeleted++;
                    }
                }
                return screen;
            }
        };
        OnFinished myOnFinished = new OnFinished() {
            public void run(Tetris t, char key) throws Exception {
                System.out.println("OnFinished.run() called");
            }
        };

        // myOn~~를 Tetris 클래스 메소드를 통해 플러그인 (run method의 body 교체)
        Tetris.setOnLeftListener(myOnLeft);
        Tetris.setOnRightListener(myOnRight);
        Tetris.setOnDownListener(myOnDown);
        Tetris.setOnUpListener(myOnUp);
        Tetris.setOnCwListener(myOnCw);
        Tetris.setOnCcwListener(myOnCcw);
        Tetris.setOnNewBlockListener(myOnNewBlock);
        Tetris.setOnFinishedListener(myOnFinished);

        Tetris board = new Tetris(15, 10);
        Random random = new Random();
        key = (char) ('0' + random.nextInt(7));
        board.accept(key);
        board.printScreen(); System.out.println();

        while ((key = getKey()) != 'q') {
            state = board.accept(key);
            board.printScreen(); System.out.println();
            if (state == TetrisState.NewBlock) {
                key = (char) ('0' + random.nextInt(7));
                state = board.accept(key);
                board.printScreen(); System.out.println();
                if (state == TetrisState.Finished) break; // Game Over!
            }
        }
        System.out.println("Program terminated!");
    }
}
