package de.gemo.engine.units;

public class Vector2d {

    public double x;
    public double y;

    public Vector2d(double num1, double num2) {
        this.x = num1;
        this.y = num2;
    }

    public void add(double num) {
        x += num;
        y += num;
    }

    public void sub(double num) {
        x -= num;
        y -= num;
    }

    public void div(double num) {
        x /= num;
        y /= num;
    }

    public void mult(double num) {
        x *= num;
        y *= num;
    }

    public void add(Vector2d other) {
        x += other.x;
        y += other.y;
    }

    public void sub(Vector2d other) {
        x -= other.x;
        y -= other.y;
    }

    public void div(Vector2d other) {
        x /= other.x;
        y /= other.y;
    }

    public void mult(Vector2d other) {
        x *= other.x;
        y *= other.y;
    }
}
