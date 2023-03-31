#version 330 core
out vec4 fragColor;

in vec2 texCoords;
in vec3 normal;
in vec3 fragPos;

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;

    int hasTexture;
    float reflectance;
};

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct PointLight {
    vec3 color;
    vec3 position;
    float intensity;
    Attenuation att;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct SpotLight {
    vec3 color;
    vec3 position;
    float intensity;
    Attenuation att;
    vec3 coneDir;
    float cutOff;
};

uniform sampler2D aTexture;

uniform vec3 ambientLight;
uniform float specularPower;

uniform Material material;

uniform PointLight pointLight[MAX_POINT_LIGHTS];
uniform SpotLight spotLight[MAX_SPOT_LIGHTS];

uniform DirectionalLight directionalLight;

uniform int usesLighting;


uniform vec3 viewPos;

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal);
vec4 calcLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDir, vec3 normal);
void setupColors(Material material, vec2 texCoord);
vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal);
vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal);

void main() {
    setupColors(material, texCoords);

    if (usesLighting == 1) {
        vec4 diffuseSpecularComp;

        if (directionalLight.intensity > 0) diffuseSpecularComp = calcDirectionalLight(directionalLight, fragPos, normal);
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) if (pointLight[i].intensity > 0) diffuseSpecularComp += calcPointLight(pointLight[i], fragPos, normal);
        for (int i = 0; i < MAX_SPOT_LIGHTS; i++) if (spotLight[i].intensity > 0) diffuseSpecularComp += calcSpotLight(spotLight[i], fragPos, normal);

        fragColor = ambientC * vec4(ambientLight, 1.0) + diffuseSpecularComp;

        return;
    }

    if (material.hasTexture == 1) {
        fragColor = ambientC;
        return;
    }
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal) {
    vec3 lightDir = light.position - position;
    vec3 toLightDir = normalize(lightDir);
    vec3 fromLightDir = -toLightDir;
    float spot_alfa = dot(fromLightDir, normalize(light.coneDir));

    PointLight point;
    point.position = light.position;
    point.intensity = light.intensity;
    point.color = light.color;
    point.att = light.att;

    vec4 color = vec4(0.0f, 0.0f, 0.0f, 0.0f);
    if (spot_alfa > light.cutOff) {
        color = calcPointLight(point, position, normal);
        color *= (1.0f - (1.0 - spot_alfa) / (1.0  - light.cutOff));
    }

    return color;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.position - position;
    vec3 toLightDir  = normalize(lightDirection);
    vec4 lightColor = calcLightColor(light.color, light.intensity, position, toLightDir, normal);

    // Apply Attenuation
    float distance = length(lightDirection);
    float attenuationInv = light.att.constant + light.att.linear * distance +
    light.att.exponent * distance * distance;
    return lightColor / attenuationInv;
}


vec4 calcLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDir, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specColour = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, toLightDir), 0.0);
    diffuseColor = diffuseC * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;

    // Specular Light
    vec3 cameraDirection = normalize(viewPos - position);
    vec3 fromLightDir = -toLightDir;
    vec3 reflectedLight = normalize(reflect(fromLightDir , normal));
    float specularFactor = max( dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColour = speculrC * lightIntensity  * specularFactor * material.reflectance * vec4(lightColor, 1.0);

    return (diffuseColor + specColour);
}

void setupColors(Material material, vec2 texCoord) {
    if (material.hasTexture == 1) {
        ambientC = texture(aTexture, texCoord);
        diffuseC = ambientC;
        speculrC = ambientC;
        return;
    }

    ambientC = material.ambient;
    diffuseC = material.diffuse;
    speculrC = material.specular;
}
