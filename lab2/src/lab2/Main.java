package lab2;

public class Main {
    public static void main(String[] args) throws Exception { // why "throws Exception"???
        main3(args);
    }

    public static void main1(String[] args) throws Exception { // why "throws Exception"???
        Matrix m = new Matrix(10, 10);;
        for (int i=0; i<999; i++)
            m = new Matrix(10, 10);
        System.gc();
        System.out.println("nAlloc=" + m.get_nAlloc());
        System.out.println("nFree=" + m.get_nFree());
    }

    public static void main2(String[] args) { // why no "throws Exception"???
        int[][] arrayBlk = {
                { 0, 1, 0 },
                { 1, 1, 1 },
                { 0, 0, 0 }
        };
        try {
            Matrix currBlk = new Matrix(arrayBlk);
            //Matrix tempBlk = new Matrix(5,5);
            Matrix tempBlk = new Matrix(-1,-1); // falls into the second catch
            tempBlk = tempBlk.add(currBlk); // falls into the first catch
        } catch(MismatchedMatrixException e) {
            e.printStackTrace();
            System.out.println("at first catch: " + e.getMessage());
        } catch(MatrixException e) {
            e.printStackTrace();
            System.out.println("at second catch: " + e.getMessage());
        }
    }

    public static void main3(String[] args) throws Exception { // why "throws Exception"???
        int[][] arrayScreen = {
                { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
                { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
                { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
                { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
                { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
                { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
        };
        int[][] arrayBlk = {
                { 0, 1, 0 },
                { 1, 1, 1 },
                { 0, 0, 0 },
        };
        Matrix oScreen = new Matrix(arrayScreen);
        System.out.println("oScreen:");
        drawMatrix(oScreen); System.out.println();

        Matrix currBlk = new Matrix(arrayBlk);
        System.out.println("currBlk:");
        drawMatrix(currBlk); System.out.println();

        int top = 0;
        int left = 4;
        Matrix tempBlk = oScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
        System.out.println("tempBlk (after clip):");
        drawMatrix(tempBlk); System.out.println();

        tempBlk = tempBlk.add(currBlk);
        System.out.println("tempBlk (after add):");
        drawMatrix(tempBlk); System.out.println();

        oScreen.paste(tempBlk, top, left);
        System.out.println("oScreen (after paste):");
        drawMatrix(oScreen); System.out.println();

        System.out.println("currBlk.sum()=" + currBlk.sum());
        System.out.println();

        tempBlk.mulc(2);
        System.out.println("tempBlk (after mulc):");
        tempBlk.print(); System.out.println();

        System.out.println("currBlk.anyGreaterThan(1)=" + currBlk.anyGreaterThan(1));
        System.out.println("tempBlk.anyGreaterThan(1)=" + tempBlk.anyGreaterThan(1));
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
}
