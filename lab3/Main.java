import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class Main {
    static Random random = new Random(); // Random 객체 생성
//    private static int idxBlockType = random.nextInt(7); // 0~6 정수 랜덤 선택

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

    // 일자 블록 (임시 테스트용)
//    static int[][] arrayBlk = {
//            { 0, 0, 1, 0 },
//            { 0, 0, 1, 0 },
//            { 0, 0, 1, 0 },
//            { 0, 0, 1, 0 },
//    };
    private static int iScreenDy = 15;
    private static int iScreenDx = 10;
    private static int iScreenDw = 4; // large enough to cover the largest block
    private static int[][] createArrayScreen(int dy, int dx, int dw) {
        int y, x;
        int[][] array = new int[dy + dw][dx + 2*dw];
        for (y = 0; y < array.length; y++)
            for (x = 0; x < dw; x++)
                array[y][x] = 1;
        for (y = 0; y < array.length; y++)
            for (x = dw + dx; x < array[0].length; x++)
                array[y][x] = 1;
        for (y = dy; y < array.length; y++)
            for (x = 0; x < array[0].length; x++)
                array[y][x] = 1;
        return array;
    }
    public static void drawMatrix(Matrix m) {
        int dy = m.get_dy();
        int dx = m.get_dx();
        int array[][] = m.get_array();
        for (int y=0; y < dy; y++) {
            for (int x=0; x < dx; x++) {
                if (array[y][x] == 0) System.out.print("□ ");
                else if (array[y][x] == 1) System.out.print("■ ");
                else System.out.print("X ");
            }
            System.out.println();
        }
    }
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
    public static void main(String[] args) throws Exception {
        // Rotate 키 처리 (v9)
        boolean rotateNeeded = false; // rotate 키가 입력 되었을 경우 true
        int idxBlockDegree = 0; // 초기 degree의 값은 0으로 설정 (0도 회전한 모양)

        // 7가지 블록 무작위 선택 (v8)
        int idxBlockType = random.nextInt(7); // 0~6 정수 랜덤 선택
        int[][] arrayBlk = setOfBlockArrays[idxBlockType][idxBlockDegree]; // 랜덤하게 블록 선택 (블록의 모양(degree)은 첫 번째 모양으로 선택)




        boolean newBlockNeeded = false;
        int top = 0;
        int left = iScreenDw + iScreenDx/2 - 2;
        int[][] arrayScreen = createArrayScreen(iScreenDy, iScreenDx, iScreenDw);
        char key;



        Matrix iScreen = new Matrix(arrayScreen);
        Matrix currBlk = new Matrix(arrayBlk);
        Matrix tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
        tempBlk = tempBlk.add(currBlk);
        Matrix oScreen = new Matrix(iScreen);
        oScreen.paste(tempBlk, top, left);
        drawMatrix(oScreen); System.out.println();
        while ((key = getKey()) != 'q') {
            switch(key) {
                case 'a': left--; break; // move left
                case 'd': left++; break; // move right
                case 's': top++; break; // move down
                case 'w': rotateNeeded = true; break; // rotate the block clockwise
                case ' ': break; // drop the block
                default: System.out.println("unknown key!");
            }

            // w키를 입력한 경우 90도 회전된 모양으로 변경
            if (rotateNeeded == true) {
                rotateNeeded = false; // rotateNeeded 변수값 다시 false로 변경
                idxBlockDegree = (idxBlockDegree + 1) % 4; // 90도 회전 (0~3 범위 안에서 인덱스 1증가)
                arrayBlk = setOfBlockArrays[idxBlockType][idxBlockDegree]; // 블록 다시 선택
                currBlk =  new Matrix(arrayBlk); // currBlk 객체 다시 할당
            }


            tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
            tempBlk = tempBlk.add(currBlk);
            if (tempBlk.anyGreaterThan(1)) {
                switch(key) {
                    case 'a': left++; break; // undo: move right
                    case 'd': left--; break; // undo: move left
                    case 's': top--; newBlockNeeded = true; break; // undo: move up
                    case 'w': break; // undo: rotate the block counter-clockwise
                    case ' ': break; // undo: move up
                }
                tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
                tempBlk = tempBlk.add(currBlk);
            }
            oScreen = new Matrix(iScreen);
            oScreen.paste(tempBlk, top, left);
            drawMatrix(oScreen); System.out.println();
            if (newBlockNeeded) {
                // 7가지 블록 무작위 선택 (v8)
                idxBlockType = random.nextInt(7); // 0~6 정수 랜덤 선택
                arrayBlk = setOfBlockArrays[idxBlockType][idxBlockDegree]; // 랜덤하게 블록 선택 (블록의 모양(degree)은 첫 번째 모양으로 선택)

                iScreen = new Matrix(oScreen);
                top = 0; left = iScreenDw + iScreenDx/2 - 2;
                newBlockNeeded = false;
                currBlk = new Matrix(arrayBlk);
                tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
                tempBlk = tempBlk.add(currBlk);
                if (tempBlk.anyGreaterThan(1)) {
                    System.out.println("Game Over!");
                    System.exit(0);
                }
                oScreen = new Matrix(iScreen);
                oScreen.paste(tempBlk, top, left);
                drawMatrix(oScreen); System.out.println();
            }
        }
    }
}
