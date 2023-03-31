package org.scope.render.struct;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4f;
import org.scope.framework.UniformConstructor;
import org.scope.render.ShaderProgram;
import org.scope.render.Texture;


public class Material implements UniformConstructor {
    @Getter @Setter private Vector4f ambient;
    @Getter @Setter private Vector4f diffuse;
    @Getter @Setter private Vector4f specular;

    @Getter @Setter private float reflectance;

    @Getter @Setter private Texture texture;

    public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, float reflectance, Texture texture) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;

        this.texture = texture;
        this.reflectance = reflectance;
    }

    public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, float reflectance) {
        this(ambient, diffuse, specular, reflectance, null);
    }

    public Material(StandardMaterial standard) {
        this.ambient = standard.getAmbient();
        this.diffuse = standard.getDiffuse();
        this.specular = standard.getSpecular();

        this.reflectance = standard.getReflectance();
    }

    public Material(Texture texture) {
        this(StandardMaterial.EMERALD); // This is just to set up the default values in the case the texture gets set to null, and for the setUniform to not throw errors

        this.texture = texture;
    }

    public Material(Texture texture, float reflectance) {
        this(StandardMaterial.EMERALD); // This is just to set up the default values in the case the texture gets set to null, and for the setUniform to not throw errors

        this.reflectance = reflectance;
        this.texture = texture;
    }

    public boolean hasTexture() {
        return texture != null;
    }

    @Override
    public void createUniforms(ShaderProgram shader, String uniformName) {
        shader.createUniform(uniformName + ".ambient");
        shader.createUniform(uniformName + ".diffuse");
        shader.createUniform(uniformName + ".specular");
        shader.createUniform(uniformName + ".reflectance");
        shader.createUniform(uniformName + ".hasTexture");
    }

    @Override
    public void setUniforms(ShaderProgram shader, String uniformName) {
        shader.setVec4(uniformName + ".ambient", getAmbient());
        shader.setVec4(uniformName + ".diffuse", getDiffuse());
        shader.setVec4(uniformName + ".specular", getSpecular());
        shader.setFloat(uniformName + ".reflectance", getReflectance());
        shader.setInt(uniformName + ".hasTexture", hasTexture() ? 1 : 0);
    }

    public enum StandardMaterial {

        EMERALD(new Vector4f(0.0215f, 0.1745f, 0.0215f, 1.0f), new Vector4f(0.07568f, 0.61424f, 0.07568f, 1.0f), new Vector4f(0.633f, 0.727811f, 0.633f, 1.0f), 0.6f),
        JADE(new Vector4f(0.135f, 0.2225f, 0.1575f, 1.0f), new Vector4f(0.54f, 0.89f, 0.63f, 1.0f), new Vector4f(0.316228f, 0.316228f, 0.316228f, 1.0f), 0.1f);
        // TODO: Add the rest of the OpenGL materials from Mark J Kilgards demo... http://devernay.free.fr/cours/opengl/materials.html
        @Getter private final Vector4f ambient;
        @Getter private final Vector4f diffuse;
        @Getter private final Vector4f specular;
        @Getter private final float reflectance;

        StandardMaterial(Vector4f ambient, Vector4f diffuse, Vector4f specular, float reflectance) {
            this.ambient = ambient;
            this.diffuse = diffuse;
            this.specular = specular;

            this.reflectance = reflectance;
        }
    }
}