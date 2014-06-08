uniform vec2 origin;
uniform vec2 inner;
uniform float angle;
uniform float inner_intensity;
uniform float outer_intensity;

varying vec2 pos;

void main()
{
    float a = acos(dot(normalize(pos - origin), normalize(inner))) / angle;
    a = (outer_intensity - inner_intensity) * a + inner_intensity;
    a = 1.0 / (1.0 + exp(-(a*12.0 - 6.0)));
    gl_FragColor = vec4(a, a, a, a);
}