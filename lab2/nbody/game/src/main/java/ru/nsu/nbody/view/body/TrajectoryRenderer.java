package ru.nsu.nbody.view.body;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

public class TrajectoryRenderer {
    private final int maxControlPoints;
    private final int segmentsPerSection;
    private final List<Vector3f> controlPoints = new ArrayList<>();

    private final Geometry geometry;
    private final Mesh mesh;
    
    private final FloatBuffer posBuffer;
    private final ShortBuffer indexBuffer;
    
    private int lastVertexCount = -1;

    public TrajectoryRenderer(int maxControlPoints, int segmentsPerSection, Geometry geometry) {
        this.maxControlPoints = maxControlPoints;
        this.segmentsPerSection = segmentsPerSection;
        this.geometry = geometry;
        this.mesh = geometry.getMesh();

        int totalMaxVertices = (maxControlPoints + 2) * segmentsPerSection + 1;
        
        this.posBuffer = BufferUtils.createFloatBuffer(totalMaxVertices * 3);
        this.indexBuffer = BufferUtils.createShortBuffer(totalMaxVertices);

        this.mesh.setMode(Mesh.Mode.LineStrip);
        this.mesh.setDynamic();
        
        this.mesh.setBuffer(VertexBuffer.Type.Position, 3, posBuffer);
        this.mesh.setBuffer(VertexBuffer.Type.Index, 2, indexBuffer);
    }

    public void addControlPoint(Vector3f point) {
        controlPoints.add(point.clone());
        if (controlPoints.size() > maxControlPoints) {
            controlPoints.remove(0);
        }
    }

    public void updateMesh(Vector3f currentVisualPos) {
        if (controlPoints.isEmpty()) return;

        posBuffer.rewind();
        int vertexCount = 0;

        if (controlPoints.size() == 1) {
            putToBuffer(controlPoints.get(0));
            putToBuffer(currentVisualPos);
            vertexCount = 2;
        } else {
            for (int i = 0; i < controlPoints.size(); i++) {
                vertexCount += fillCatmullRom(i, currentVisualPos);
            }
        }

        posBuffer.rewind();
        mesh.getBuffer(VertexBuffer.Type.Position).updateData(posBuffer);
        
        updateIndices(vertexCount);
        
        geometry.updateModelBound();
    }

    private int fillCatmullRom(int index, Vector3f headPos) {
        Vector3f p1 = controlPoints.get(index);
        boolean isLastPoint = (index == controlPoints.size() - 1);
        
        Vector3f p2 = isLastPoint ? headPos : controlPoints.get(index + 1);
        Vector3f p0 = (index > 0) ? controlPoints.get(index - 1) : p1;
        
        Vector3f p3;
        if (isLastPoint) {
            p3 = headPos.add(headPos.subtract(p1)); 
        } else if (index + 2 < controlPoints.size()) {
            p3 = controlPoints.get(index + 2);
        } else {
            p3 = headPos;
        }

        for (int i = 0; i < segmentsPerSection; i++) {
            float t = i / (float) segmentsPerSection;
            interpolateToBuffer(p0, p1, p2, p3, t);
        }
        
        if (isLastPoint) {
            putToBuffer(headPos);
            return segmentsPerSection + 1;
        }
        
        return segmentsPerSection;
    }

    private void interpolateToBuffer(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, float t) {
        float t2 = t * t;
        float t3 = t2 * t;

        float f1 = -0.5f * t3 + t2 - 0.5f * t;
        float f2 =  1.5f * t3 - 2.5f * t2 + 1.0f;
        float f3 = -1.5f * t3 + 2.0f * t2 + 0.5f * t;
        float f4 =  0.5f * t3 - 0.5f * t2;

        float x = p0.x * f1 + p1.x * f2 + p2.x * f3 + p3.x * f4;
        float y = p0.y * f1 + p1.y * f2 + p2.y * f3 + p3.y * f4;
        float z = p0.z * f1 + p1.z * f2 + p2.z * f3 + p3.z * f4;

        posBuffer.put(x).put(y).put(z);
    }

    private void putToBuffer(Vector3f v) {
        posBuffer.put(v.x).put(v.y).put(v.z);
    }

    private void updateIndices(int vertexCount) {
        if (vertexCount == lastVertexCount) return; 

        indexBuffer.rewind();
        
        short lastValidIndex = 0;
        for (short i = 0; i < vertexCount; i++) {
            lastValidIndex = i;
            indexBuffer.put(i);
        }

        while (indexBuffer.hasRemaining()) {
            indexBuffer.put(lastValidIndex);
        }

        indexBuffer.rewind();
        mesh.getBuffer(VertexBuffer.Type.Index).updateData(indexBuffer);
        
        lastVertexCount = vertexCount;
    }

    public Geometry getGeometry() {
        return geometry;
    }
    
    public void clear() {
        controlPoints.clear();
        lastVertexCount = -1;
    }
}