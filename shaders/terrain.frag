uniform int terrain[256][256];

void main() {
highp int x = int(gl_FragCoord.x);
highp int y = int(gl_FragCoord.y);
	if(terrain[x][y] == 1) {
    	gl_FragColor = vec4(1,0,0,1);
	} else {
		gl_FragColor = vec4(0,0,0,0);
	}
}