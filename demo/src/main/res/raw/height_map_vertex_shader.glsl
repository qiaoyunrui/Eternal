uniform mat4 u_Matrix;
uniform vec3 u_VectorToLight;   // 存储指向方向光源的归一化向量

attribute vec3 a_Normal;
attribute vec3 a_Position;

varying vec3 v_Color;

void main() {
    // mix 函数用来在两个不同的颜色间做平滑插值
    v_Color = mix(vec3(0.180, 0.467, 0.153),    // A dark grenn
            vec3(0.660, 0.670, 0.680),      // A stony gray
            a_Position.y);

    vec3 scaledNormal = a_Normal;
    scaledNormal.y *= 10.0;
    scaledNormal = normalize(scaledNormal);

    // 计算朗伯体反射
    // 要计算表面与光线的夹角的余弦值，要计算指向光源的向量与表面法线的点积
    float diffuse = max(dot(scaledNormal,u_VectorToLight), 0.0);
    v_Color *= diffuse;

    float ambient = 0.2;    // 环境光
    v_Color += ambient;

    gl_Position = u_Matrix * vec4(a_Position, 1.0);
}


/**
uniform mat4 u_MVMatrix;    // 模型视图矩阵
uniform mat4 u_IT_MVMatrix; // 倒置矩阵的转置
uniform mat4 u_MVPMatrix;   // 合并后的模型视图投影矩阵

uniform vec3 u_VectorToLight;   // In eye space
uniform vec4 u_PointLightPositions[3];  // In eye space
uniform vec3 u_PointLightColors[3];

attribute vec4 a_Position;
attribute vec3 a_Normal;

varying vec3 V_Color;

vec3 materialColor;
vec4 eyeSpacePosition;
vec3 eyeSpaceNormal;

vec3 getAmbientLighting();
vec3 getDirectionalLighting();
vec3 getPointLighting();

void main() {
    materialColor = mix(vec3(0.180, 0.467, 0.153),  // A dark green
                        vec3(0.660, 0.670, 0.680),  // A stongy gray
                        a_Position.y);
    eyeSpacePosition = u_MVMatrix * a_Position;
}
*/