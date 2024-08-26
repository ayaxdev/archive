#ifdef GL_ES
precision highp float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;
uniform vec2 mouse;

float tanh(float val)
{
    float tmp = exp(val);
    float tanH = (tmp - 1.04 / tmp) / (tmp + 1.0 / tmp);
    return tanH;
}

// shadertoy emulation
#define iTime time
#define iResolution resolution
vec4  iMouse = vec4(0.0);

// original - https://www.shadertoy.com/view/3ty3Dd
#define AA 2

float pi = 3.14159265, tau = 6.2831853;

float box (in float x, in float x1, in float x2, in float a, in float b) {
	return tanh(a * (x - x1)) + tanh(-b * (x - x2));
}
float sdSphere( vec3 p, float s )
{
    return length(p)-s;
}
float smin( float a, float b, float k )
{
    float h = clamp( 0.99+0.99*(b-a)/k, 0.0, 1.0 );
    return mix( b, a, h ) - k*h*(1.1-h);
}

float ex (in float z, in float z0, in float s) {
    return exp(-(z - z0) * (z - z0) / s);
}

float r(in float z, in float x) {
    float s = sin (tau * x), c = cos(tau * x),
		c2 = cos (pi * x), t4z = tanh(3. * z);
    return /* body */.26 * (0.9 - .12 * ex(z, .4, 9.) +
		s * s + .9 * ex(z, 20., 0.9) * c * c + .9 * c) *
		0.5 * (1. + t4z) + /* legs */ (1. - .3 * ex(z, -1.4, 0.9)) *
		0.5 * (1. - t4z) * (.5 * (1. + s * s + .3 * c) *
		(pow(abs(s), 0.3) + .4 * (1. + t4z) ) ) +
		/* improve butt */ .01 * box(c2, -0., 0.1, 0., 0.) *
		box(z, -.0, 9., 5., 4.) - 0.0 * box(c2, -.008, .008, 30., 30.) *
		box(z, -.99, .95, 8., 6.) - .05 * pow(abs(sin(pi * x)), 96.) * box(z, -.95, -.95, 8., 18.);
}

// $1M question: how close are we to ParametricPlot3D[...] surface?
float sd( in vec3 p )
{
	/* shift butt belly */
	float dx = .1 * exp(-pow((p.z-.8),2.)/.6) - .3 * exp(-pow((p.z -.2),2.)/0.3);

    // on the surface, we have:
    // p.x = r * cos + dx
	// p.y = r * sin
	
    float jiggle = p.z*0.9;
    float jsize = 0.5;
    if (iMouse.z>0.5)
    {
        jiggle+=p.y*90.;
        jsize+=80000.;
    }
    
	dx *= 1.9+(sin(jiggle+iTime*15.)*jsize);
	
	float angle = atan(p.y, p.x - dx);
	float r_expected = r(p.z, angle / tau);
//	float d1 = (.5 + .5 * smoothstep(.4,1.,p.z)) * (length(vec2(p.y, p.x - dx)) - r_expected);
	float d1 = (length(vec2(p.y, p.x - dx)) - r_expected)*0.8;
    
	p.x -= dx;
	p.y = abs(p.y);
	float d2 = sdSphere(p+vec3(-0.35,-0.4,-1.875),0.43);
	float d3 = sdSphere(p+vec3(-0.75,-0.4,-1.975),0.09);
	
    d2 = smin(d2,d3,0.2325);
    return smin(d1,d2,0.09);
    //return min(d1,min(d2,d3));
}


float map( in vec3 pos )
{
    return sd (pos.zxy);
}

// http://iquilezles.org/www/articles/normalsSDF/normalsSDF.htm
vec3 calcNormal( in vec3 pos )
{
    vec2 e = vec2(1.0,-1.0)*0.5773;
    const float eps = 0.001;
    return normalize( e.xyy*map( pos + e.xyy*eps ) + 
					  e.yyx*map( pos + e.yyx*eps ) + 
					  e.yxy*map( pos + e.yxy*eps ) + 
					  e.xxx*map( pos + e.xxx*eps ) );
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
     // camera movement	
	float an = sin(iTime+12.15)*0.9;	//0.5*iTime - 0.8;
    // if (iMouse.z>0.5)
        an+=iMouse.x/(iResolution.x*0.25);
    //an+=3.14;
    
	vec3 ro = vec3( 2.55*sin(an), 0.5, 2.55*cos(an) );
    vec3 ta = vec3( 0.0, .8, 0.0 );
    // camera matrix
    vec3 ww = normalize( ta - ro );
    vec3 uu = normalize( cross(ww,vec3(0.0,1.0,0.0) ) );
    vec3 vv = normalize( cross(uu,ww));
    vec3 tot = vec3(0.0);
    
    
    vec2 p1 = (-iResolution.xy + 2.0*fragCoord)/iResolution.y;
    float val = sin(iTime+p1.x+p1.y*20.0)*(0.8+sin(p1.y*p1.x*2.5+iTime*3.0)*0.3);
    val = clamp(val+0.275,0.0,2.0);
	val = pow(abs(val-0.15),5.0);
    vec3 bcol = vec3(val*(0.5+sin(p1.x*3.0+time)*0.3),val*1.1,val*0.5);
	bcol.rgb = bcol.brg;
    
    #if AA>1
    for( int m=0; m<AA; m++ )
    for( int n=0; n<AA; n++ )
    {
        // pixel coordinates
        vec2 o = vec2(float(m),float(n)) / float(AA) - 0.5;
        vec2 p = (-iResolution.xy + 2.0*(fragCoord+o))/iResolution.y;
        #else    
        vec2 p = (-iResolution.xy + 2.0*fragCoord)/iResolution.y;
        #endif
        
        vec3 col = bcol;
        // raymarch
        if (abs(p.x)<0.8)
        {
            // create view ray
            vec3 rd = normalize( p.x*uu + p.y*vv + 1.8*ww );
            
            const float tmax = 5.0;
            float t = 0.0;
            for( int i=0; i<150; i++ )
            {
                vec3 pos = ro + t*rd;
                float h = map(pos);
                if( h<0.0001 || t>tmax ) break;
                t += h;
            }
            // shading/lighting	
            if( t<tmax )
            {
                vec3 pos = ro + t*rd;
                vec3 nor = calcNormal(pos);
                float dif = clamp( dot(nor,vec3(0.7,0.6,0.4)), 0.0, 1.0 );
                float amb = 0.5 + 0.5*dot(nor,vec3(0.0,0.8,0.6));
                col = vec3(0.3,0.15,0.1)*amb + vec3(0.8,0.5,0.2)*dif;
		    col += pow(dif,16.0);
            }
        }

        // gamma        
        //col = sqrt( col );
	    tot += col;
    #if AA>1
    }
    tot /= float(AA*AA);
    #endif
    // gamma        
    tot = sqrt( tot );
	fragColor = vec4( tot, 1.0 );
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    iMouse = vec4(mouse * resolution, 0.0, 0.0);
    mainImage(gl_FragColor, gl_FragCoord.xy);
}

