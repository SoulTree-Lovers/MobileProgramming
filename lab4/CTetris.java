public class CTetris extends Tetris {
    private Matrix currCBlk;
    private Matrix iCScreen;
    private Matrix oCScreen;
    private static Matrix[][] setOfCBlockObjects;


    public CTetris(int cy, int cx) throws Exception {
        super(cy, cx);
        int[][] arrayScreen = createArrayScreen(iScreenDy, iScreenDx, iScreenDw);
        iCScreen = new Matrix(arrayScreen);
        oCScreen = new Matrix(iCScreen);
    }

    public Matrix get_oCScreen() {
        return oCScreen;
    }
    public static void init(int[][][][] setOfBlockArrays) throws Exception {
        Tetris.init(setOfBlockArrays);
        setOfCBlockObjects = Tetris.createSetOfBlocks(setOfBlockArrays, true);
    }

    private Matrix deleteColorFullLines(Matrix oCScreen, Matrix currCBlk, int top, int iScreenDw) throws Exception {
        return deleteFullLines(oCScreen, currCBlk, top, iScreenDy, iScreenDx, iScreenDw);
    }

    public TetrisState accept(char key) throws Exception {
        Matrix tempBlk, tempBlk2;
        TetrisState _state = super.accept(key);
        currCBlk = setOfCBlockObjects[idxBlockType][idxBlockDegree];
        tempBlk = iCScreen.clip(top, left, top + currCBlk.get_dy(), left + currCBlk.get_dx());
        tempBlk2 = tempBlk.add(currCBlk);
        oCScreen.paste(iCScreen, 0, 0);
        oCScreen.paste(tempBlk2, top, left);
        if (_state == TetrisState.NewBlock) {
            oCScreen = deleteColorFullLines(oCScreen, currCBlk, top, iScreenDw);
            iCScreen.paste(oCScreen, 0, 0);
        }
        return _state;
    }
}

