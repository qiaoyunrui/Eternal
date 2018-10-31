//uniform mat4 u_Matrix;
//uniform vec3 u_VectorToLight;   // 存储指向方向光源的归一化向量
//
//attribute vec3 a_Normal;
//attribute vec3 a_Position;
//
//varying vec3 v_Color;
//
//void main() {
//    // mix 函数用来在两个不同的颜色间做平滑插值
//    v_Color = mix(vec3(0.180, 0.467, 0.153),    // A dark grenn
//            vec3(0.660, 0.670, 0.680),      // A stony gray
//            a_Position.y);
//
//    vec3 scaledNormal = a_Normal;
//    scaledNormal.y *= 10.0;
//    scaledNormal = normalize(scaledNormal);
//
//    // 计算朗伯体反射
//    // 要计算表面与光线的夹角的余弦值，要计算指向光源的向量与表面法线的点积
//    float diffuse = max(dot(scaledNormal,u_VectorToLight), 0.0);
//    v_Color *= diffuse;
//
//    float ambient = 0.2;    // 环境光
//    v_Color += ambient;
//
//    gl_Position = u_Matrix * vec4(a_Position, 1.0);
//}



uniform mat4 u_MVMatrix;    // 模型视图矩阵
uniform mat4 u_IT_MVMatrix; // 倒置矩阵的转置
uniform mat4 u_MVPMatrix;   // 合并后的模型视图投影矩阵

uniform vec3 u_VectorToLight;   // In eye space
uniform vec4 u_PointLightPositions[3];  // In eye space
uniform vec3 u_PointLightColors[3];

attribute vec4 a_Position;
attribute vec3 a_Normal;

varying vec3 v_Color;

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

    // 在眼空间中计算当前的位置和法线
    eyeSpacePosition = u_MVMatrix * a_Position;
    eyeSpaceNormal = normalize(vec3(u_IT_MVMatrix * vec4(a_Normal, 0.0)));

    // 计算每一种光的类型，并把其结果颜色累加到 v_Color 上
    v_Color = getAmbientLighting();
    v_Color += getDirectionalLighting();
    v_Color += getPointLighting();

    gl_Position = u_MVPMatrix * a_Position;
}

// 计算环境光
vec3 getAmbientLighting() {
    return materialColor * 0.1;
}

// 计算方向光
// 朗伯体反射
// 要计算表面与光线的夹角的余弦值，要计算指向光源的向量与表面法线的点积
vec3 getDirectionalLighting() {
    return materialColor * 0.3
        * max(dot(eyeSpaceNormal, u_VectorToLight), 0.0);
}


vec3 getPointLighting() {
    vec3 lightingSum = vec3(0.0);

    for (int i = 0; i < 3;i++) {
        vec3 toPointLight = vec3(u_PointLightPositions[i])
        - vec3(eyeSpacePosition);
        float distance = length(toPointLight);
        toPointLight = normalize(toPointLight);

        float cosine = max(dot(eyeSpaceNormal,toPointLight), 0.0);
        lightingSum += (materialColor * u_PointLightColors[i] * 5.0 * cosine)
                / distance;
    }
    return lightingSum;
}
