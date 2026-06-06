package ru.nsu.nbody.util;

public class Vector3dM {
    private double x, y, z;

    public double x() { return x; }
    public double y() { return y; }
    public double z() { return z; }

    public Vector3dM(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vector3dM fromVector3d(Vector3d v) {
        return new Vector3dM(v.x(), v.y(), v.z());
    }

    public Vector3d toVector3d() {
        return new Vector3d(x, y, z);
    }

    public double getComponent(int d) {
        switch (d) {
            case 0 -> {
                return x;
            }
            case 1 -> {
                return y;
            }
            case 2 -> {
                return z;
            }
            default -> throw new IndexOutOfBoundsException("d must be in [0, 2]!");
        }
    }

    public Vector3dM radd(Vector3dM other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public void radd(double x, double y, double z) {
        this.x += x; this.y += y; this.z += z;
    }

    public Vector3dM rsub(Vector3dM other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    public Vector3dM rmul(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    public Vector3dM raddmul(Vector3dM other, double scalar) {
        this.x += other.x * scalar;
        this.y += other.y * scalar;
        this.z += other.z * scalar;
        return this;
    }

    public double dot(Vector3dM other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3dM radd(Vector3d other) {
        this.x += other.x();
        this.y += other.y();
        this.z += other.z();
        return this;
    }

    public Vector3dM rsub(Vector3d other) {
        this.x -= other.x();
        this.y -= other.y();
        this.z -= other.z();
        return this;
    }

    public Vector3dM raddmul(Vector3d other, double scalar) {
        this.x += other.x() * scalar;
        this.y += other.y() * scalar;
        this.z += other.z() * scalar;
        return this;
    }

    public double dot(Vector3d other) {
        return x * other.x() + y * other.y() + z * other.z();
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3dM copy() {
        return new Vector3dM(x, y, z);
    }
}