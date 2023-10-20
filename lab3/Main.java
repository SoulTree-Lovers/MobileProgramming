import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class Main {
    static Random random = new Random(); // Random 객체 생성 (초기 한 번만 선언)

    private static int[][][][] setOfBlockArrays = { // [7][4][?][?]
            {
                // 네모 블록
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
                // ㅗ자 블록
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
                // ㄴ자 블록 1
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
                // ㄴ자 블록 2
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
                // ㄹ자 블록 1
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
                // ㄹ자 블록 2
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
                // ㅡ자 블록
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
        // 벽 두께가 1칸만 보이도록 변경
        int dy = m.get_dy();
        int dx = m.get_dx();
        int dw = iScreenDw;
        int array[][] = m.get_array();
        for (int y=0; y < dy-dw+1; y++) {
            for (int x=dw-1; x < dx-dw+1; x++) {
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
        // nKeys != 0 이라는 뜻은, 버퍼에 읽을 값이 있다는 뜻이다.
        // 만일, 이전에 사용자가 입력한 문자의 길이가 2이상이라면, 버퍼에 문자가 남아있을 것이다.
        // 그러면, line의 길이는 그대로지만 nKeys는 줄어들기 때문에 남은 문자들을 하나씩 읽어올 수 있는 것이다.
        if (nKeys != 0) {
            ch = line.charAt(line.length() - nKeys);
            nKeys--;
            return ch;
        }

        do {
            line = br.readLine(); // 사용자로부터 문자열을 입력받는다.
            nKeys = line.length(); // nKeys를 문자열의 길이로 지정한다.
        } while (nKeys == 0); // 버퍼에 읽을 문자가 없다면(nKeys == 0) 또 다시 do를 실행한다. (사용자가 공백을 입력한 경우)

        ch = line.charAt(0);
        nKeys--;

        return ch;
    }

    // [v11]까지 구현
    public static void main(String[] args) throws Exception {
        // Rotate 키 처리: 블록 회전 (v9)
        boolean rotateNeeded = false; // rotate(w) 키가 입력되었을 경우 true
        int idxBlockDegree = 0; // 초기 degree의 값은 0으로 설정 (0도 회전한 모양)

        // 7가지 블록 무작위 선택 (v8)
        int idxBlockType = random.nextInt(7); // 0~6 정수 랜덤 선택
        int[][] arrayBlk = setOfBlockArrays[idxBlockType][idxBlockDegree]; // 랜덤하게 블록 선택 (블록의 모양(degree)은 첫 번째 모양으로 선택)

        // Space 키 처리: 블록 추락 (v10)
        boolean inputSpace = false; // space 키가 입력되었을 경우 true

        // Full line 삭제 처리 (v11)
        boolean checkFullLine = false; // down (s 혹은 space) 키가 입력되었을 경우 true

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
                case ' ': inputSpace = true; break; // drop the block
                default: System.out.println("unknown key!");
            }

            // w키를 입력한 경우 90도 회전된 모양으로 변경 (v9)
            if (rotateNeeded == true) {
                rotateNeeded = false; // rotateNeeded 변수값 다시 false로 변경
                int tempDegree = idxBlockDegree; // 원래 degree 값을 임시로 저장
                idxBlockDegree = (idxBlockDegree + 1) % 4; // 90도 회전 (0~3 범위 안에서 인덱스 1증가)
                arrayBlk = setOfBlockArrays[idxBlockType][idxBlockDegree]; // 블록 다시 선택
                currBlk =  new Matrix(arrayBlk); // currBlk 객체 다시 할당

                tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
                tempBlk = tempBlk.add(currBlk);

                // 회전한 결과 충돌이 발생한 경우 되돌리기
                if (tempBlk.anyGreaterThan(1)) {
                    idxBlockDegree = tempDegree; // 원래 블록 모양으로 -90도 회전 (0~3 범위 안에서 인덱스 1증가)
                    arrayBlk = setOfBlockArrays[idxBlockType][idxBlockDegree]; // 블록 다시 선택
                    currBlk =  new Matrix(arrayBlk); // currBlk 객체 다시 할당
                }
            }

            // space(' ')키를 입력한 경우 블록 추락 (v10)
            if (inputSpace == true) {
                inputSpace = false;

                // 충돌이 발생할 때까지 블록 한 칸 아래로 이동
                while (true) {
                    if (tempBlk.anyGreaterThan(1)) {
                        checkFullLine = true;
                        newBlockNeeded = true;
                        top--;
                        break;
                    }
                    top++;
                    tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
                    tempBlk = tempBlk.add(currBlk);
                }
            }

            tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
            tempBlk = tempBlk.add(currBlk);



            // 충돌이 발생한 경우
            if (tempBlk.anyGreaterThan(1)) {
                switch(key) {
                    case 'a': left++; break; // undo: move right
                    case 'd': left--; break; // undo: move left
                    case 's': top--; newBlockNeeded = true; checkFullLine = true; break; // undo: move up
                    case 'w': break; // undo: rotate the block counter-clockwise
                    case ' ': checkFullLine = true; break; // undo: move up
                }

                tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
                tempBlk = tempBlk.add(currBlk);
            }

            oScreen = new Matrix(iScreen);
            oScreen.paste(tempBlk, top, left);

            // down 키(s 혹은 space) 입력 후 충돌이 발생했을 경우 full line 체크 (v11)
            if (checkFullLine == true) {
//                System.out.println("checkFullLine == true");

                // tempBlk(currBlk)이 영향을 준 라인부터 검사 (맨 아랫 줄부터 검사)
                for (int i=iScreenDy-1; i>=top; i--) {
                    // tempBlk(currBlk)이 영향을 준 라인 잘라내기
                    Matrix fullLineBlk = oScreen.clip(i, iScreenDw, i+1, iScreenDx+iScreenDw);

//                    System.out.println("clip(" + i + ", " + 4 + ", " +((int)i+1) + ", " + (4+(int)iScreenDx) + ")");
//                    System.out.print("Line (" + i + ") : " + fullLineBlk.sum());

                    if (fullLineBlk.sum() == 10) {
                        System.out.println(" [ Remove Full Line ] ");
                        Matrix removedBlk = oScreen.clip(0, iScreenDw, i, iScreenDx+iScreenDw); // 지워질 부분 위부터 자르기
                        oScreen.paste(removedBlk, 1, iScreenDw); // 지워진 블록을 다시 oScreen에 붙여넣기
                        i++; // 한 줄이 삭제되었으므로 i는 다시 복구
                    }
                }

                checkFullLine = false;
            }

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
