package Components.Physics;

public class Vector2D {

    public float x;
    public float y;

    public Vector2D() {
        x = 0.0f;
        y = 0.0f;
    }

    public Vector2D(float X, float Y) {
        x = X;
        y = Y;
    }

    public Vector2D add(Vector2D v2) {
        x += v2.x;
        y += v2.y;
        return this;
    }

    public Vector2D subtract(Vector2D v2) {
        x -= v2.x;
        y -= v2.y;
        return this;
    }

    public Vector2D multiply(float scaler) {
        x *= scaler;
        y *= scaler;
        return this;
    }

    public void printMessage() {
        System.out.println("(x, y) = (" + x + ", " + y + ")");
    }
}
