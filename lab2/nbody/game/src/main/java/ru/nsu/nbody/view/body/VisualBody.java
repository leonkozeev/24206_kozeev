package ru.nsu.nbody.view.body;


import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

import ru.nsu.nbody.simulation.Body;

public class VisualBody {
    private Geometry geom;
    private Body body;
    private ColorRGBA color = ColorRGBA.randomColor();
    private TrajectoryRenderer trajectory;
    private float radius = 10;
    private Vector3f pos;
    private Vector3f vel;
    private Vector3f a;

    public VisualBody(int id, Body body, Node rootNode, AssetManager assetManager) {
        this.body = body;
        pos = body.r().toVector3f();
        vel = body.v().toVector3f();
        a = Vector3f.ZERO;
        Sphere sphere = new Sphere(32, 32, radius);
        geom = new Geometry("planet_" + id, sphere);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", color);
        geom.setMaterial(material);
        rootNode.attachChild(geom);

        Mesh traceMesh = new Mesh();
        Geometry traceGeom = new Geometry("trace_" + id, traceMesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        traceGeom.setMaterial(mat);
        rootNode.attachChild(traceGeom);
        trajectory = new TrajectoryRenderer(500, 4, traceGeom);
    }

    public void update(float tpf) {
        pos.addLocal(vel.mult(tpf));
        vel.addLocal(a.mult(tpf));
        // System.out.println(tpf + " " + pos);
        geom.setLocalTranslation(pos);
        trajectory.updateMesh(pos);
    }

    public void cleanup(Node rootNode) {
        rootNode.detachChild(geom);
    }

    public void set(Body body, float lastTimeDelta) {
        pos = body.r().toVector3f();
        vel = body.v().toVector3f();
        a = vel.subtract(this.body.v().toVector3f()).multLocal(1 / lastTimeDelta);
        this.body = body;
        trajectory.addControlPoint(pos);
    }

    public Vector3f pos() {
        return pos;
    }

    public float radius() {
        return radius;
    }

    public Body body() {
        return body;
    }
}