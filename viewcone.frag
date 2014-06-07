uniform vec2 lightLocation;
uniform vec3 lightColorOne;
uniform vec3 lightColorTwo;
uniform float intensity;

void main() {
	float distance = length(lightLocation - gl_FragCoord.xy);
	if(distance < 125.0) {
		vec4 color = vec4(lightColorOne, 1);
		// pow(attenuation, 1)) * vec4(lightColorOne, 1
		gl_FragColor = color;
	} else if(distance < 270.0) {
		vec4 color = vec4(lightColorTwo, 1);
		gl_FragColor = color;
	}	
}