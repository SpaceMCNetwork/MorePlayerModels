#version 150

#moj_import <fog.glsl>

in vec3 Position;
in vec2 UV0;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

out vec2 texCoord0;
out float vertexDistance;
out vec4 normal;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    texCoord0 = UV0;
    // The legacy shader's fog helper accepted a model-view matrix.  In 1.21.1
    // it takes an already transformed position, and this shader does not use
    // the distance in its fragment stage.
    vertexDistance = 0.0;
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
