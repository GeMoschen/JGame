varying vec2 pos;
uniform vec4 color;

void main() {
    float t = 1.0 - sqrt(pos.x*pos.x + pos.y*pos.y);
    // Enable this line if you want sigmoid function on the light interpolation
    //t = 1.0 / (1.0 + exp(-(t*12.0 - 6.0)));
    gl_FragColor = vec4(color.r, color.g, color.b, color.a) * t;
}