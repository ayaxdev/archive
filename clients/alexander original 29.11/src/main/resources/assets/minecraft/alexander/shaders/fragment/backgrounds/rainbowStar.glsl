precision lowp float;uniform float time;uniform vec2 resolution;
#define _ uv=p;p-=.5;p.x*=r.x/r.y;z+=.1;l=length(p);uv+=p/l*(sin(z)+1.)*abs(sin(l*2.-z-z));c[i]=.01/length(mod(uv,.5)-.25);} gl_FragColor=vec4(c/l,x);//{_}
#define __ void main(){float x=time;vec3 c;vec2 r=resolution;vec4 g=gl_FragCoord;float l,z=x;for(int i=0;i<3;i++){vec2 uv,p=g.rg/r;_}//{[_=_]}
   //\    ______    o__   || .  __o
__//_\\__|__||__|   |     ||      |
 //___\\    ||      /\    ||     /\
//     \\__ || __P TENNIS (3Lines by speedhead/(B)/spatiosa)	