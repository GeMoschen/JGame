varying vec2 pos;

void main()
{
    pos = gl_Vertex.xy;
    gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_Vertex.xy, 0.0, 1.0);
}