uniform mat4 u_Matrix;

attribute vec3 a_Position;

varying vec3 v_Position;

void main() {
    v_Position = a_Position;
    // z 分量反转
    // 把世界的右手坐标空间转换为天空盒所期望的左手坐标空间
    v_Position.z = -v_Position.z;
    gl_Position = u_Matrix * vec4(a_Position, 1.0);
    // 确保天空盒的每一部分都将位于归一化设备坐标的远平面上以及场景中的其他一切后面
    gl_Position = gl_Position.xyww;
}
