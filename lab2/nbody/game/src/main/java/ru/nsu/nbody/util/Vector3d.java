package ru.nsu.nbody.util;

import com.jme3.math.Vector3f;

public record Vector3d(double x, double y, double z) {
    public static Vector3d fromVector3f(Vector3f v) {
        return new Vector3d((double) v.x, (double) v.y, (double) v.z);
    }

    public Vector3d add(Vector3d other) {
        return new Vector3d(x + other.x, y + other.y, z + other.z);
    }

    public Vector3d sub(Vector3d other) {
        return new Vector3d(x - other.x, y - other.y, z - other.z);
    }

    public Vector3d mul(double scalar) {
        return new Vector3d(x * scalar, y * scalar, z * scalar);
    }

    public Vector3d addmul(Vector3d other, double scalar) {
        return new Vector3d(x + other.x * scalar, y + other.y * scalar, z + other.z * scalar);
    }

    public double dot(Vector3d other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3f toVector3f() {
        return new Vector3f((float) x, (float) y, (float) z);
    }
}
