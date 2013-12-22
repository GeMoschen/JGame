import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glStencilOp;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import de.gemo.game.physics.Shader;
import de.gemo.gameengine.units.Vector;

public class Main {

	public final int width = 800;
	public final int height = 600;

	public ArrayList<Light> lights = new ArrayList<Light>();
	public ArrayList<Block> blocks = new ArrayList<Block>();

	private Shader shader;

	private void render() {
		// CLEAR BACKGROUND
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

		// NORMAL RENDERPASS
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_BLEND);

		for (Block block : blocks) {
			float minDistance = Integer.MAX_VALUE;
			for (Light light : lights) {
				if (light.location.distanceTo(block.getVertices()[0]) / light.brightness < minDistance) {
					minDistance = (float) light.location.distanceTo(block.getVertices()[0]) / light.brightness;
				}
			}

			minDistance = (1f - (minDistance / 670f));
			minDistance = Math.max(minDistance, 0);

			glColor4f(minDistance, minDistance, minDistance, minDistance);
			glBegin(GL_QUADS);
			{
				for (Vector vertex : block.getVertices()) {
					vertex.render();
				}
			}
			glEnd();
		}

		// LIGHT-RENDERPASS
		for (Light light : lights) {
			light.brightness = 1f;
			glColorMask(false, false, false, false);
			glStencilFunc(GL_ALWAYS, 1, 1);
			glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);

			for (Block block : blocks) {
				Vector[] vertices = block.getVertices();
				for (int i = 0; i < vertices.length; i++) {
					Vector currentVertex = vertices[i];
					Vector nextVertex = vertices[(i + 1) % vertices.length];
					Vector edge = Vector.sub(nextVertex, currentVertex, null);
					Vector normal = new Vector(edge.getY(), -edge.getX());
					Vector lightToCurrent = Vector.sub(currentVertex, light.location, null);
					if (Vector.dot(normal, lightToCurrent) > 0) {
						Vector point1 = Vector.add(currentVertex, Vector.sub(currentVertex, light.location, null).scale(width), null);
						Vector point2 = Vector.add(nextVertex, Vector.sub(nextVertex, light.location, null).scale(width), null);

						glColor4f(1, 1, 1, 1f);
						glBegin(GL_QUADS);
						{
							glVertex2f(currentVertex.getX(), currentVertex.getY());
							glVertex2f(point1.getX(), point1.getY());
							glVertex2f(point2.getX(), point2.getY());
							glVertex2f(nextVertex.getX(), nextVertex.getY());
						}
						glEnd();
					}
				}
			}

			glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
			glStencilFunc(GL_EQUAL, 0, 1);
			glColorMask(true, true, true, true);

			this.shader.bind();
			glUniform1f(glGetUniformLocation(this.shader.getShaderProgramID(), "lightBrightness"), light.brightness);
			glUniform2f(glGetUniformLocation(this.shader.getShaderProgramID(), "lightLocation"), light.location.getX(), height - light.location.getY());
			glUniform3f(glGetUniformLocation(this.shader.getShaderProgramID(), "lightColor"), light.red, light.green, light.blue);
			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);

			glBegin(GL_QUADS);
			{
				glVertex2f(0, 0);
				glVertex2f(0, height);
				glVertex2f(width, height);
				glVertex2f(width, 0);
			}
			glEnd();

			glDisable(GL_BLEND);
			this.shader.unbind();
			glClear(GL_STENCIL_BUFFER_BIT);
		}

		Display.update();
		Display.sync(60);
	}

	private void setUpObjects() {
		int lightCount = 2 + (int) (Math.random() * 1);
		int blockCount = 15 + (int) (Math.random() * 1);

		for (int i = 1; i <= lightCount; i++) {
			Vector location = new Vector((float) Math.random() * width, (float) Math.random() * height);
			lights.add(new Light(location, (float) Math.random() * 10, (float) Math.random() * 10, (float) Math.random() * 10, 1));
		}

		for (int i = 1; i <= blockCount; i++) {
			int width = 25;
			int height = 25;
			int x = (int) (Math.random() * (this.width - width));
			int y = (int) (Math.random() * (this.height - height));
			blocks.add(new Block(x, y, width, height));
		}
	}

	private void initialize() {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle("2D Lighting");
			Display.create(new PixelFormat(0, 16, 1));
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		this.shader = new Shader();
		this.shader.loadPixelShader("resources\\shader\\lightshader.frag");

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, height, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);

		glEnable(GL_STENCIL_TEST);
		glClearColor(0, 0, 0, 0);
	}

	private void cleanup() {
		this.shader.cleanup();
		Display.destroy();
	}

	public static void main(String[] args) {
		Main main = new Main();

		main.setUpObjects();
		main.initialize();

		while (!Display.isCloseRequested()) {
			main.moveLights();
			main.render();
		}

		main.cleanup();
	}

	private void moveLights() {
		// for (Light light : this.lights) {
		// float moveX = 1;
		// if (Math.random() > 0.5) {
		// moveX = -1;
		// }
		// float moveY = 1;
		// if (Math.random() > 0.5) {
		// moveY = -1;
		// }
		// light.location.move(moveX, moveY);
		// }
		Light light = this.lights.get(0);
		light.location.set(Mouse.getX(), height - Mouse.getY());
	}
}
