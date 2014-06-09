package de.gemo.game.fov.units;

import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

public class Shader {

    private int shaderProgramID, vertexShaderID, pixelShaderID;
    private boolean useAnyShader = false, useVertexShader = false, usePixelShader = false;

    private ByteBuffer getByteCode(String fileName) {
        try {
            // load bytes from file
            byte[] shaderCode = null;
            InputStream inputStream = new FileInputStream(new File(fileName));
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            dataInputStream.readFully(shaderCode = new byte[inputStream.available()]);
            dataInputStream.close();
            inputStream.close();

            // create buffer
            ByteBuffer shader = BufferUtils.createByteBuffer(shaderCode.length);
            shader.put(shaderCode);
            shader.flip();

            return shader;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void loadPixelShader(String fileName) {
        ByteBuffer buffer = this.getByteCode(fileName);
        if (buffer == null) {
            this.useAnyShader = false;
            this.usePixelShader = false;
            return;
        }

        this.compilePixelShader(buffer);
        this.recompileShaderProgramm();
    }

    public void loadVertexShader(String fileName) {
        ByteBuffer buffer = this.getByteCode(fileName);
        if (buffer == null) {
            this.useAnyShader = false;
            this.useVertexShader = false;
            return;
        }

        this.compileVertexShader(buffer);
        this.recompileShaderProgramm();
    }

    private void recompileShaderProgramm() {
        if (!this.usePixelShader && !this.useVertexShader) {
            this.useAnyShader = false;
            return;
        }

        this.shaderProgramID = ARBShaderObjects.glCreateProgramObjectARB();
        if (this.useVertexShader) {
            ARBShaderObjects.glAttachObjectARB(shaderProgramID, vertexShaderID);
        }
        if (this.usePixelShader) {
            ARBShaderObjects.glAttachObjectARB(shaderProgramID, pixelShaderID);
        }
        ARBShaderObjects.glLinkProgramARB(shaderProgramID);
        glValidateProgram(shaderProgramID);
        this.useAnyShader = true;
    }

    private void compileVertexShader(ByteBuffer buffer) {
        if (this.useVertexShader) {
            glDeleteShader(this.vertexShaderID);
            this.useVertexShader = false;
        }
        this.vertexShaderID = ARBShaderObjects.glCreateShaderObjectARB(ARBVertexShader.GL_VERTEX_SHADER_ARB);
        ARBShaderObjects.glShaderSourceARB(vertexShaderID, buffer);
        ARBShaderObjects.glCompileShaderARB(vertexShaderID);

        if (ARBShaderObjects.glGetObjectParameteriARB(pixelShaderID, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
            System.out.println("Error creating shader: " + getLogInfo(vertexShaderID));
            // throw new RuntimeException("Error creating shader: " +
            // getLogInfo(vertexShaderID));
        }

        this.useVertexShader = true;
    }

    private void compilePixelShader(ByteBuffer buffer) {
        if (this.usePixelShader) {
            glDeleteShader(this.pixelShaderID);
            this.usePixelShader = false;
        }
        this.pixelShaderID = ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
        ARBShaderObjects.glShaderSourceARB(pixelShaderID, buffer);
        ARBShaderObjects.glCompileShaderARB(pixelShaderID);

        if (ARBShaderObjects.glGetObjectParameteriARB(pixelShaderID, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
            System.out.println("Error creating shader: " + getLogInfo(pixelShaderID));
            // throw new RuntimeException("Error creating shader: " +
            // getLogInfo(pixelShaderID));
        }

        this.usePixelShader = true;
    }

    private static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    public void bind() {
        if (this.useAnyShader) {
            ARBShaderObjects.glUseProgramObjectARB(shaderProgramID);
        }
    }

    public void unbind() {
        if (this.useAnyShader) {
            ARBShaderObjects.glUseProgramObjectARB(0);
        }
    }

    public void cleanup() {
        if (!this.useAnyShader) {
            return;
        }
        if (this.usePixelShader) {
            glDeleteShader(this.pixelShaderID);
            this.usePixelShader = false;
        }
        if (this.useVertexShader) {
            glDeleteShader(this.vertexShaderID);
            this.useVertexShader = false;
        }
        glDeleteProgram(this.shaderProgramID);
        this.useAnyShader = false;
    }

    public int getID() {
        return shaderProgramID;
    }
}
