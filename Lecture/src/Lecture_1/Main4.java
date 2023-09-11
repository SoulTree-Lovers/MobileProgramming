package Lecture_1;

public class Main4 {
    public static void main(String[] args) {
        Nested m1 = new Nested(1, 2);
        Nested m2 = new Nested(3, 4);

        System.out.println("m1.get_dy() = " + m1.get_dy() + ", m1.get_dx() = " + m1.get_dx());
        System.out.println("m2.get_dy() = " + m2.get_dy() + ", m2.get_dx() = " + m2.get_dx());

        Nested.InnerD d1 = m1.new InnerD();
        Nested.InnerS s1 = new Nested.InnerS();

        Nested.InnerD d2 = m2.new InnerD();
        Nested.InnerS s2 = new Nested.InnerS();

        System.out.println("d1.get_dy() = " + d1.get_dy() + ", s1.get_dx() = " + s1.get_dx() + ", d1.sum() = " + d1.sum());
        System.out.println("d2.get_dy() = " + d2.get_dy() + ", s2.get_dx() = " + s2.get_dx() + ", d2.sum() = " + d2.sum());

    }
}

class Nested {
    private int dy;
    private static int dx;

    public int get_dy() {
        return dy;
    }

    public static int get_dx() {
        return dx;
    }

    public Nested (int cy, int cx) {
        dy = cy;
        dx = cx;
    }

    public class InnerD {
        public int get_dy() {
            return dy;
        }
        public int sum() {
            return dy+dx;
        }
    }

    public static class InnerS {
        public int get_dx() {
            return dx;
        }
    }
}