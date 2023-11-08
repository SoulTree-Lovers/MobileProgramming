enum TetrisState {
    Running(0), NewBlock(1), Finished(2);
    private final int value;
    private TetrisState(int value) { this.value = value; }
    public int value() { return value; }
}

public class Tetris {
    protected static int iScreenDw;		// large enough to cover the largest block
    protected static int nBlockTypes;		// number of block types (typically 7)
    protected static int nBlockDegrees;	// number of block degrees (typically 4)
    protected static Matrix[][] setOfBlockObjects;	// Matrix object arrays of all blocks
    protected static Matrix[][] createSetOfBlocks(int[][][][] setOfArrays, boolean color) throws Exception {
        int ntypes = setOfArrays.length;
        int ndegrees = setOfArrays[0].length;
        Matrix[][] setOfObjects = new Matrix[nBlockTypes][nBlockDegrees];
        for (int t = 0; t < ntypes; t++) {
            for (int d = 0; d < ndegrees; d++) {
                setOfObjects[t][d] = new Matrix(setOfArrays[t][d]);
                if (color == false) {
                    setOfObjects[t][d] = setOfObjects[t][d].int2bool();
                }
            }
        }
        return setOfObjects;
    }
    protected static int max(int a, int b) { return (a > b ? a : b); }
    protected static int findLargestBlockSize(int[][][][] setOfArrays) {
        int size, max_size = 0;
        for (int t = 0; t < nBlockTypes; t++) {
            for (int d = 0; d < nBlockDegrees; d++) {
                size = setOfArrays[t][d].length;
                max_size = max(max_size, size);
            }
        }
        //System.out.println("max_size = "+max_size);
        return max_size;
    }
    public static void init(int[][][][] setOfBlockArrays) throws Exception { // initialize static variables
        nBlockTypes = setOfBlockArrays.length;
        nBlockDegrees = setOfBlockArrays[0].length;
        setOfBlockObjects = createSetOfBlocks(setOfBlockArrays, false);
        iScreenDw = findLargestBlockSize(setOfBlockArrays);
    }
    protected int iScreenDy;	// height of the background screen (excluding walls)
    protected int iScreenDx;  // width of the background screen (excluding walls)
    protected TetrisState state;		// game state
    protected int top;		// y of the top left corner of the current block
    protected int left;		// x of the top left corner of the current block
    protected Matrix iScreen;	// input screen (as background)
    protected Matrix oScreen;	// output screen
	public Matrix get_oScreen() {
		return oScreen;
	}
    protected Matrix currBlk;	// current block
    protected int idxBlockType;	// index for the current block type
    protected int idxBlockDegree; // index for the current block degree
    protected int[][] createArrayScreen(int dy, int dx, int dw) {
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
    private void printMatrix(Matrix blk) { // for debugging purposes
        int dy = blk.get_dy();
        int dx = blk.get_dx();
        int array[][] = blk.get_array();
        for (int y=0; y < dy; y++) {
            for (int x=0; x < dx; x++) {
                if (array[y][x] == 0) System.out.print("□ ");
                else if (array[y][x] == 1) System.out.print("■ ");
                else System.out.print("XX ");
            }
            System.out.println();
        }
    }
    protected Matrix deleteFullLines(Matrix screen, Matrix blk, int top, int dy, int dx, int dw) throws Exception {
        Matrix line, zero, temp;
        if (blk == null) // called right after the game starts.
            return screen; // no lines to be deleted
        int cy, y, nDeleted = 0, nScanned = blk.get_dy();
        zero = new Matrix(1, dx);

        if (top + blk.get_dy() - 1 >= dy)
            nScanned -= (top + blk.get_dy() - dy);

        for (y = nScanned - 1; y >= 0; y--) {
            cy = top + y + nDeleted;
            line = screen.clip(cy, 0, cy + 1, screen.get_dx());
            if (line.int2bool().sum() == screen.get_dx()) {
                temp = screen.clip(0, 0, cy, screen.get_dx());
                screen.paste(temp, 1, 0);
                screen.paste(zero, 0, dw);
                nDeleted++;
            }
        }
        return screen;
    }
    public void printScreen() {	Matrix screen = oScreen; // copied from oScreen
        int dy = screen.get_dy();
        int dx = screen.get_dx();
        int dw = iScreenDw;
        int array[][] = screen.get_array();
        for (int y = 0; y < dy - dw + 1; y++) {
            for (int x = dw - 1; x < dx - dw + 1; x++) {
                if (array[y][x] == 0) System.out.print("□ ");
                else if (array[y][x] == 1) System.out.print("■ ");
                else System.out.print("XX ");
            }
            System.out.println();
        }
    }
    public Tetris(int cy, int cx) throws Exception { // initialize dynamic variables
        if (cy < iScreenDw || cx < iScreenDw)
            throw new TetrisException("too small screen");
        iScreenDy = cy;
        iScreenDx = cx;
        int[][] arrayScreen = createArrayScreen(iScreenDy, iScreenDx, iScreenDw);
        state = TetrisState.NewBlock;	// The game should start with a new block needed!
        iScreen = new Matrix(arrayScreen);
        oScreen = new Matrix(iScreen);
    }
    public TetrisState accept(char key) throws Exception {
        Matrix tempBlk = new Matrix();
        if (state == TetrisState.NewBlock) {
            oScreen = deleteFullLines(oScreen, currBlk, top, iScreenDy, iScreenDx, iScreenDw);
            iScreen.paste(oScreen, 0, 0);
            state = TetrisState.Running;
            idxBlockType = key - '0'; // copied from key
            idxBlockDegree = 0;
            currBlk = setOfBlockObjects[idxBlockType][idxBlockDegree];
            top = 0;
            left = iScreenDw + iScreenDx / 2 - (currBlk.get_dx()+1) / 2;
            tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
            tempBlk = tempBlk.add(currBlk);
            oScreen.paste(iScreen, 0, 0);
            oScreen.paste(tempBlk, top, left); System.out.println();
            if (tempBlk.anyGreaterThan(1)) {
                state = TetrisState.Finished;	// System.out.println("Game Over!");
                return state;	// System.exit(0);
            }
            return state;		// should require a key input
        }
        // while ((key = getKey()) != 'q') {
        switch(key) {
            case 'a': left--; break; // move left
            case 'd': left++; break; // move right
            case 's': top++; break; // move down
            case 'w': rotateCW(); break; // rotateCW
            case ' ': drop(tempBlk); break; // drop the block
            default: System.out.println("unknown key!");
        }
        tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
        tempBlk = tempBlk.add(currBlk);
        if (tempBlk.anyGreaterThan(1)) {
            switch(key) {
                case 'a': left++; break; // undo: move right
                case 'd': left--; break; // undo: move left
                case 's': top--; state = TetrisState.NewBlock; break; // undo: move up
                case 'w': rotateCCW(); break; // undo: rotateCCW
                case ' ': unDoDrop(); break; // undo: move up
            }
            tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
            tempBlk = tempBlk.add(currBlk);
        }
        oScreen.paste(iScreen, 0, 0);
        oScreen.paste(tempBlk, top, left);
        // printScreen(oScreen); System.out.println();
        return state;
        // if (newBlockNeeded) { ... }
        // } end of while
    }

    private void rotateCW() {
        idxBlockDegree = (idxBlockDegree + 1) % nBlockDegrees;
        currBlk = setOfBlockObjects[idxBlockType][idxBlockDegree];
    }

    private void rotateCCW() {
        idxBlockDegree = (idxBlockDegree + nBlockDegrees - 1) % nBlockDegrees;
        currBlk = setOfBlockObjects[idxBlockType][idxBlockDegree];
    }

    private void drop(Matrix tempBlk) throws Exception {
        do {
            top++;
            tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
            tempBlk = tempBlk.add(currBlk);
        } while (tempBlk.anyGreaterThan(1) == false);
    }

    private void unDoDrop() {
        top--;
        state = TetrisState.NewBlock;
    }
}

class TetrisException extends Exception {
    public TetrisException() { super("Tetris Exception"); }
    public TetrisException(String msg) { super(msg); }
}
