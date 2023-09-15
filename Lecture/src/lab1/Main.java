import java.util.Arrays; // for main3

public class Main {
    public static void main(String[] args) {
        main1(args);
        main2(args);
        main3(args);
        main4(args);
        main5(args);
    }

    public static void main1(String[] args) {
        String s1 = "Hello, Java!";
        System.out.print("s1="); System.out.println(s1);
        String s2 = String.format("%s, %s!", "Hello", "Java");
        System.out.print("s2="); System.out.println(s2);

        int len1 = s1.length();
        int len2 = s2.length();
        System.out.println("len1=" + len1);
        System.out.println("len2=" + len2);

        boolean b1 = s1.equals(s1); // compare s1 and s2 (in content)
        boolean b2 = s1.equals(s2); // compare s1 and s2 (in content)
        boolean b3 = (s1 == s2); // compare s1 and s2 (in objects)

        System.out.println("s1.equals(s1)=" + b1);
        System.out.println("s1.equals(s2)=" + b2);
        System.out.println("(s1==s2)=" +  b3);
    }

    public static void main2(String[] args) {
        String istr = "1234";
        String dstr = "12.34";

        int ival = Integer.parseInt(istr); // string -> integer value
        double dval = Double.parseDouble(dstr); // string -> double value
        System.out.println("before : " + ival + ", " + dval);

        ival = ival + 1111;
        dval = dval + 11.11;
        String istr2 = String.valueOf(ival); // integer value -> string
        String dstr2 = String.valueOf(dval); // double value -> string

        System.out.println("after  : " + istr2 + ", " + dstr2);
    }

    public static void main3(String[] args) {
        int A1[] = null;
        int A2[] = {1, 2, 3, 4, 5};
        int[] A3 = new int[5];
        int A4[] = new int[]{1, 2, 3, 4, 5};

        System.out.print("A1:");
        printArray(A1);
        System.out.print("A2:");
        printArray(A2);
        System.out.print("A3:");
        printArray(A3);
        System.out.print("A4:");
        printArray(A4);
        System.out.println("A2.equals(A3)=" + A2.equals(A3));
        System.out.println("A2.equals(A4)=" + A2.equals(A4));
        System.out.println("Arrays.equals(A2, A3)=" + Arrays.equals(A2, A3));
        System.out.println("Arrays.equals(A2, A4)=" + Arrays.equals(A2, A4));
    }

    public static void printArray(int a[]) {
        if (a != null) {
            for (int i = 0; i < a.length; i++)
                System.out.print(a[i] + " ");
        }
        System.out.println();
    }

    public static void main4(String[] args) {
        Nested m1 = new Nested(1, 2);
        Nested m2 = new Nested(3, 4);
        System.out.println("m1.get_dy()=" + m1.get_dy() + ", m1.get_dx()=" + m1.get_dx());
        System.out.println("m2.get_dy()=" + m2.get_dy() + ", m2.get_dx()=" + m2.get_dx());
        Nested.InnerD d1 = m1.new InnerD();
        Nested.InnerS s1 = new Nested.InnerS();
        Nested.InnerD d2  = m2.new InnerD();
        Nested.InnerS s2 = new Nested.InnerS();
        System.out.println("d1.get_dy()=" + d1.get_dy() + ", s1.get_dx()=" + s1.get_dx() + ", d1.sum()=" + d1.sum());
        System.out.println("d2.get_dy()=" + d2.get_dy() + ", s2.get_dx()=" + s2.get_dx() + ", d2.sum()=" + d2.sum());
    }

    public static void main5(String[] args) {
        Matrix m1 = new Matrix(3,3);
        m1.print(); System.out.println();
        int A[][] = { { 0, 1, 0, }, { 1, 1, 1, }, { 0, 0, 0, } };
        Matrix m2 = new Matrix(A);
        m2.print(); System.out.println();
        MyMatrix m3 = new MyMatrix(3,3);
        m3.print(); System.out.println();
        MyMatrix m4 = new MyMatrix(A);
        m4.print(); System.out.println();
        m2 = m4; // polymorphism: Matrix covers MyMatrix!!
        m2.print(); System.out.println();
    }
}

class Nested {
    private int dy; // dynamic variable
    private static int dx; // static variable
    public int get_dy() { return dy; }
    public static int get_dx() { return dx; } // can be declared 'dynamic'
    public Nested(int cy, int cx) { dy = cy; dx = cx; }
    public class InnerD {
        public int get_dy() { return dy; }
        public int sum() { return dy+dx; }
    }
    public static class InnerS {  public int get_dx() { return dx; }  }
}

class MyMatrix extends Matrix {
    public MyMatrix() { super(); }
    public MyMatrix(int cy, int cx) { super(cy, cx); }
    public MyMatrix(int[][] a) { super(a); }
    public void print() {
        int dy = get_dy();
        int dx = get_dx();
        int array[][] = get_array();
        for (int y=0; y < dy; y++) {
            for (int x=0; x < dx; x++) {
                if (array[y][x] == 0) System.out.print("□ ");
                else if (get_array()[y][x] == 1) System.out.print("■ ");
                else System.out.print("X ");
            }
            System.out.println();
        }
    }
}
