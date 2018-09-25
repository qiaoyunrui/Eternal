package me.juhezi.demo.objects;

public class Geometry {

    public static class Point {

        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * 沿着 Y 轴平移
         *
         * @param distance
         * @return
         */
        public Point translateY(float distance) {
            return new Point(x, y + distance, z);
        }

    }

    public static class Circle {

        public final Point center;
        public final float radius;

        public Circle(Point center, float redius) {
            this.center = center;
            this.radius = redius;
        }

        public Circle scale(float scale) {
            return new Circle(center,radius);
        }

    }

    /**
     * 圆柱体
     */
    public static class Cylinder {
        public final Point center;
        public final float radius;
        public final float height;

        public Cylinder(Point center, float radius, float height) {
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }

}
