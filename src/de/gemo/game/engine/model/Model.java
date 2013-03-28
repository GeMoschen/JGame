package de.gemo.game.engine.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

/** @author Oskar */
public class Model {

    public final List<Vector3f> vertices = new ArrayList<Vector3f>();
    public final List<Vector3f> normals = new ArrayList<Vector3f>();
    public final List<Face> faces = new ArrayList<Face>();

    public static Model fromFile(File file) throws IOException {
        Model model = OBJLoader.loadModel(file);
        model.setupDisplayList();
        return model;
    }

    private int vboVertexHandle;
    private int vboNormalHandle;

    public void render() {
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);
        glNormalPointer(GL_FLOAT, 0, 0L);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);
        glMaterialf(GL_FRONT, GL_SHININESS, 10f);
        glDrawArrays(GL_TRIANGLES, 0, this.faces.size() * 3);
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);
    }

    private void setupDisplayList() {
        int[] vbos = OBJLoader.createVBO(this);
        vboVertexHandle = vbos[0];
        vboNormalHandle = vbos[1];
    }
}