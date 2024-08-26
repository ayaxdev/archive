#extension GL_OES_standard_derivatives : enable

precision highp float;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;
uniform sampler2D texNoise;

float hash(vec2 p){
	p.x = dot(p,p+38.68);
	return fract(sin(p.x*534.63)*678.94*sin(p.y*687.354)*512.35);
}

float hash21(vec2 p){
	 p  = 50.0*fract( p*0.3083099 + vec2(0.71,0.113));
    	return -.1+fract( p.x*p.y*(p.x+p.y) );
}

float noise(vec2 p){
	vec2 i = floor(p);
	vec2 f = fract(p);
	
	vec2 u = f*f*3.0-2.0*f*f*f;
	
	return mix(mix(hash21(i),hash21(i+vec2(1.0,0.0)),u.x),
		mix(hash21(i+vec2(0.0,1.0)),hash21(i+vec2(1.0,1.0)),u.x),u.y);
}

float fbm(vec2 uv){
	float f = 0.0;
	f += 0.5000*noise( uv ); uv*=1.0;
	f += 0.2500*noise( uv ); uv*=2.5;
	f += 0.1250*noise( uv ); uv*=1.9;
	f += 0.0625*noise( uv ); uv*=2.2;
	return f;
}

mat2 rot(float a){
	return mat2(cos(a),sin(a),-sin(a),cos(a));
}

float hurb(vec3 p,vec3 s){
	p.z+=abs(p.x)*0.2;
	
	vec3 a = abs(p)-s;
	float d = max(a.x,max(a.y,a.z));//length(max(vec3(0),a))+min(0.,max(a.x,max(a.y,a.z)));
	return d;
}

float field(vec3 p,float r){
	vec2 id = floor(p.xz/r-.5);
	float a = hash21(id+.01);
	p.xz = (fract(p.xz/r-.5)-.5)*r;
	
	float noise = fbm(p.xz*.1);
	p.y-=noise*6.;
	p.yz *= rot(sin(time+p.x*2.)*0.2);
	p.y+=noise*6.;
	
	p.xz*=rot(a*5.3);
	float sx = a<0.2?a+0.5:a>0.8?a-0.2:a;
	return hurb(p,vec3(sin(p.y)*.9,3.,.03)+vec3(-sx*.8,-a*.4,a*0.05));
}

float mist(vec3 p){
	p+=sin(p.zxy*vec3(0.02,0.03,.1)+vec3(1.,.7,.2)*time)*1.;
	p.z+=time*5.;
	
	float repeat = 8.;
	p = (fract(p/repeat-.5)-.5)*repeat;
	float d = length(p)-.05;
	return d;
}
float mimi = 0.;
float mimi2 = 0.;

float grass(vec3 p){
	float d = 0.;
	float noise = fbm(p.xz*.1);
	p.y+=noise*6.;
	p.xz+=sin(p.zx*vec2(.1,0.02)+p.y*0.1+time*2.)*.8*max(0.,p.y*1.);
	d = field(p,2.);
	
	p.xz*=rot(.3);
	p.x+=73.68;
	d = min(d*.9,field(p,2.));
	
	p.xz*=rot(.68);
	p.x-=23.61;
	d = min(d*.9,field(p,2.));
	
	p.xz*=rot(.98);
	p.x-=12.61;
	d = min(d*.9,field(p,2.));
	
	d = min(d*.85,2.2-p.y);
	return d;
}

float map(vec3 p){
	float d = 0.;
	d = grass(p);
	d = min(d*.85,2.2-p.y);
	p.xy*=rot(.2);
	p.yz*=rot(.3);
	float mm = mist(p);
	mimi=0.1/(mm);
	d = min(d,mm);
	p.xz*=rot(.4);
	float mm2 = mist(p);
	mimi2 = 0.1/(mm2);
	d = min(d,mm2);
	return d;
}

float sss(vec3 p,vec3 l,float d){
	return smoothstep(0.,1.,map(p+l*d)/d);
}

void main( void ) {
	vec2 uv = ( gl_FragCoord.xy / resolution.xy )-.5;
	uv.x*=resolution.x/resolution.y;
	vec3 col = vec3(0.);
	float t = time*0.2;
	vec3 ro = vec3(0.,-21.,-10.);
	vec3 lookat = vec3(0.,-18.,0.);
	ro.xz*=rot(t);
	ro.z+=time*10.;
	ro.x+=time;
	lookat.x+=time;
	lookat.z+=time*10.;
	vec3 f = normalize(lookat-ro);
	vec3 r = normalize(cross(f,vec3(0,1,0)));
	vec3 u = normalize(cross(f,r));
	vec3 rd = normalize(uv.x*r+uv.y*u+f);
	
	float d;
	vec3 p;
	for(int i=0;i<120;i++){
		p = ro+rd*d;
		d+=map(p);
		if(d<0.001||d>200.) break;
	}
	
	vec3 light = vec3(-3.,-0.9,-1.2);
	vec3 l = normalize(light);
	float ground = p.y+fbm(p.xz*.1)*6.;
	float sub=0.;
	for(int i=1;i<20;i++){
		float dist = float(i)*5./20.;
		sub += sss(p,l,dist);
	}
	sub*=2./20.;
	sub *= clamp(-(p.y+ground)*.1+.7,0.,1.);
	float fog = 1.-clamp(length(p-ro)/150.,0.,1.);
	float noise = fbm(p.xy);
	float n = texture2D(texNoise,p.xz*0.01).x;
	col+=vec3(.8,.5,.6)*mimi+vec3(.8,.5,.6)*mimi2;
	col+=sub*vec3(.802,.8+noise*0.5,.2+noise*.2);
	col*=fog;
	
	vec3 sky = mix(max(vec3(0),vec3(.5,.6,1.)+rd.y*1.2),vec3(0.9,0.7,0.5)*10.,pow(max(0.,dot(rd,l)),10.));
	col +=pow(1.0-fog,3.)*1.1*sky;
	col = smoothstep(0.,1.,col);
	col = pow(col,vec3(0.4545));
	
	
	gl_FragColor = vec4(col, 1.0 );

}