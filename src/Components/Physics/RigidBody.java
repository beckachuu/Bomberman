package Components.Physics;

import Commons.Config;

public class RigidBody {

    private float _mass;            // m
    private float _gravityAccel;    // a(tt)
    private Vector2D _force;         // F
    private Vector2D _friction;      // Fr
    private Vector2D _displacement;  // s
    private Vector2D _velocity;      // v
    private Vector2D _accel;         // a(ht)

    public RigidBody() {
        _mass = Config.RIGID_BODY_DEFAULT_UNIT_MASS;
        _gravityAccel = Config.RIGID_BODY_DEFAULT_GRAVITY_ACCEL;

        _force = new Vector2D();
        _friction = new Vector2D();
        _displacement = new Vector2D();
        _velocity = new Vector2D();
        _accel = new Vector2D();
    }

    // Setter mass and gravitational acceleration
    public void setMass(float m) { _mass = m; }
    public void setGravityAccel(float a_ht) { _gravityAccel = a_ht;}

    // Setter for common force
    public void unsetForce() { _force.x = 0; _force.y = 0; }
    public void applyForce(Vector2D F) { _force = F; }
    public void applyForceXAxis(float Fx) { _force.x = Fx; }
    public void applyForceYAxis(float Fy) { _force.y = Fy; }

    // Setter for friction
    public void unsetFriction() { _friction.x = 0; _friction.y = 0; }
    public void applyFriction(Vector2D Fr) { _friction = Fr; }

    // Update the accel by X and Y axis
    // According to Newton's Second Law :
    //      (F + Fr)*(1/m) = a;
    public void move(float dt) {
        /** Explain :
         * The original formula is:
         *      aY = (P + Fy)/m
         * and   P = m * a
         * ==>    aY = a + Fy/m;
         */
        _accel.x = (_force.x + _friction.x)/ _mass;
        _accel.y = _gravityAccel + _force.y/ _mass;

        /** Explain :
         * According to Particle Kinematics, normal motion:
         * acceleration = differential of velocity in a time unit
         *           (a = dv/dt)
         *
         * We assume that all the character's motion are constant motion:
         *          acceleration = velocity / time
         *  ==>         velocity = acceleration * time;
         *                    (v = a * dt)
         */
        _velocity = _accel.multiply(dt);

        /** Simple formula:
         *      s = v * t
         */
        _displacement = _velocity.multiply(dt);
    }

    /** NOTICE :
     * If friction.x = 0.0f, mass = 1.0f
     * => accel.x ~ force.x
     *
     * velocity = accel*dt
     * => velocity ~ accel.x ~ force.x
     *
     * displacement = velocity * dt = accel * (dt^2)
     * => displacement ~ m_Velocity ~ m_Accel.x ~ force.x
     * and displacement ~ (dt)^2
     *
     * => Displacement proportional to Force and dt^2
     */

    // Getter for mass, displacement, velocity and acceleration
    public float getMass() { return _mass; }
    public Vector2D getDisplacement() { return _displacement; }
    public Vector2D getVelocity() { return _velocity; }
    public Vector2D getAccel() { return _accel; }
}
