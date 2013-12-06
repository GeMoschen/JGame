uniform vec2 lightLocation;
uniform vec3 lightColor;
uniform float screenHeight;
uniform float lightBrightness;

void main() {
	float distance = length(lightLocation - gl_FragCoord.xy)* 2;
	float attenuation = lightBrightness / distance * 2;
	vec4 color = vec4(attenuation, attenuation, attenuation, pow(attenuation, lightBrightness)) * vec4(lightColor, 1);
	gl_FragColor = color;
}