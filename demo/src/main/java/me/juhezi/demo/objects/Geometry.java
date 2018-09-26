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

        public Point translate(Vector vector) {
            return new Point(
                    x + vector.x,
                    y + vector.y,
                    z + vector.z);
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
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
            return new Circle(center, radius);
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

    public static class Vector {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }

        public Vector crossProduct(Vector vector) {
            return new Vector(
                    (y * vector.z) - (z * vector.y),
                    (z * vector.x) - (x * vector.z),
                    (x * vector.y) - (y - vector.x));
        }

        public float dotProduct(Vector vector) {
            return x * vector.x + y * vector.y + z * vector.z;
        }

        public Vector scale(float scale) {
            return new Vector(x * scale,
                    y * scale,
                    z * scale);
        }

        @Override
        public String toString() {
            return "Vector{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }

    public static class Ray {
        public final Point point;
        public final Vector vector;

        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }

        @Override
        public String toString() {
            return "Ray{" +
                    "point=" + point +
                    ", vector=" + vector +
                    '}';
        }
    }

    // 和木槌大小相当的包围球
    public static class Sphere {

        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }

        @Override
        public String toString() {
            return "Sphere{" +
                    "center=" + center +
                    ", radius=" + radius +
                    '}';
        }
    }

    public static class Plane {
        public final Point point;
        // 法向量和平面上的一个点
        public final Vector normal;

        public Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;
        }
    }

    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }

    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    // 计算球心和射线的距离
    private static float distanceBetween(Point point, Ray ray) {
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(ray.point, point);
        // 叉乘，得到第三个向量，垂直于前两个向量，向量的长度恰好是前两个向量定义的三角形的面积的两倍
        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();

        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }

    // 要计算出这个交点，需要计算出射线的向量要缩放多少才能刚好与平面相接触，这就是缩放因子
    // 接下来使用被缩放的向量平移射线的点来找出这个相交点。
    public static Point intersectionPoint(Ray ray, Plane plane) {
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);
        // 缩放因子
        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal)
                / ray.vector.dotProduct(plane.normal);
        Point intersectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
        return intersectionPoint;
    }

}
