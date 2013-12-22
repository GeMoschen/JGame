package de.gemo.gameengine.renderer;

public interface IRenderable {

	/**
	 * Add this IRenderable to the pipeline of the {@link Renderer}
	 */
	public void addToRenderPipeline();

	/**
	 * Render this IRenderable.
	 */
	public void render();

	/**
	 * Debugrendering of this IRenderable.
	 */
	public void debugRender();
}
