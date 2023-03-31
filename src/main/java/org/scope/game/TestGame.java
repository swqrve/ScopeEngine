package org.scope.game;

import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.camera.type.FreeFlyCamera;
import org.scope.light.types.DirectionalLight;
import org.scope.light.types.PointLight;
import org.scope.light.types.SpotLight;
import org.scope.logger.Debug;
import org.scope.manager.InputManager;
import org.scope.manager.LightManager;
import org.scope.render.struct.Material;
import org.scope.render.ShaderProgram;
import org.scope.render.Texture;
import org.scope.render.type.Cube;
import org.scope.render.type.Quad;
import org.scope.render.type.SkyBox;
import org.scope.scene.Scene;
import org.scope.util.FileUtil;


import static org.lwjgl.opengl.GL11C.*;

public class TestGame implements Scene {
    private FreeFlyCamera camera;

    private Quad quad;
    private Cube cube;
    private SkyBox skybox;

    private ShaderProgram defaultShader;

    private Texture senkuTexture;
    private Texture lampTexture;

    private final Matrix4f modelMatrix = new Matrix4f();

    private final Vector3f lightColor = new Vector3f(1.0f, 0.0f, 1.0f);
    private final Vector3f lightPosition = new Vector3f(-2.50f, 3.0f, 2.0f);

    private Material objectMaterial;
    private LightManager lightManager;

    @Override
    public void init() {
        // CREATE A CAMERA AND SET IT AS THE DEFAULT CAMERA
        camera = new FreeFlyCamera(0.0f, 0.0f, 0.5f, 45.0f);
        Camera.setCurrentCamera(camera); // This is for the input method on this custom camera to be called automatically and for skybox etc. to use its projection by default, will consider making this an List<>, but realistically you should only ever be calling input on one camera no?

        // CREATE THE TWO USED MODELS (COULD BE ABSTRACTED TO BE DEFAULT MODELS IN THE ENGINE ACCESSED FROM THE MODEL MANAGER)
        quad = new Quad();
        cube = new Cube();

        // CREATE THE TWO SHADERS
        defaultShader = new ShaderProgram(FileUtil.loadResource("shaders/vertex.glsl"), FileUtil.loadResource("shaders/fragment.glsl"));
        ShaderProgram skyBoxShader = new ShaderProgram(FileUtil.loadResource("shaders/skybox/skyboxvertex.glsl"), FileUtil.loadResource("shaders/skybox/skyboxfragment.glsl"));

        // CREATE SKYBOX MODEL/OBJECT
        skybox = new SkyBox(skyBoxShader, "textures/skybox");

        // CREATE TEXTURES
        senkuTexture = new Texture("textures/senku.png");
        lampTexture = new Texture("textures/lamp.png");

        // MATERIAL
        objectMaterial = new Material(senkuTexture, 32.0f);
        objectMaterial.createUniforms(defaultShader, "material");
        objectMaterial.setUniforms(defaultShader, "material");

        // LIGHTS
        lightManager = new LightManager(defaultShader, new Vector3f(1.0f, 1.0f, 1.0f), 32.0f);

        lightManager.addLight(new PointLight(lightColor, lightPosition, 1.0f));
        lightManager.addLight(new DirectionalLight(lightColor, new Vector3f(0.0f, 0.0f, 0.0f), 1.0f));
        lightManager.addLight(new SpotLight(lightColor, new Vector3f(0.0f, 5.0f, 0.0f), 50.0f, 0.0f, 0.0f, 1.0f, new Vector3f(0.0f, -1.0f, 0.0f), (float) Math.cos(Math.toRadians(15))));

        lightManager.createUniforms();
        lightManager.setUniforms();

        // CREATE STANDARD SHADER INFO
        createShaderUniforms(defaultShader);
        createShaderUniforms(skyBoxShader);

        // SET PROJECTION MATRIX, THE ONLY OF THE THREE MATRICES NOT UPDATED PER FRAME
        defaultShader.setMatrix4f("projection", camera.getCameraProjection());
        defaultShader.setMatrix4f("projection", camera.getCameraProjection());
    }



    long startTime = System.currentTimeMillis();
    float lightAngle;
    @Override
    public void update(double deltaTime) {
        if ((System.currentTimeMillis() - startTime) > 3000.0f) {
            startTime = System.currentTimeMillis();
            lightColor.set(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat());
            lightManager.getPointLights()[0].setUniforms(defaultShader, "pointLight[0]");
        }

        lightAngle += 50.0f * deltaTime;
        if (lightAngle > 90) {
            lightManager.getCurrentDirectionalLight().setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
            lightManager.getCurrentDirectionalLight().setIntensity(factor);
            lightManager.getCurrentDirectionalLight().getColor().y = Math.max(factor, 0.9f);
            lightManager.getCurrentDirectionalLight().getColor().z = Math.max(factor, 0.5f);
        } else {
            lightManager.getCurrentDirectionalLight().setIntensity(1);
            lightManager.getCurrentDirectionalLight().getColor().x = 1;
            lightManager.getCurrentDirectionalLight().getColor().y = 1;
            lightManager.getCurrentDirectionalLight().getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        lightManager.getCurrentDirectionalLight().getDirection().x = (float) Math.sin(angRad);
        lightManager.getCurrentDirectionalLight().getDirection().y = (float) Math.cos(angRad);

        lightManager.getCurrentDirectionalLight().setUniforms(defaultShader, "directionalLight");
    }

    @Override
    public void render() {
        // Render quad at -1.0f 0.0f 3.0f scaled 2x
        modelMatrix.identity().translate(-1.0f, 0.0f, 3.0f).scale(2.0f);
        defaultShader.setMatrix4f("model", modelMatrix);
        defaultShader.setBool("usesLighting", true);
        quad.render(camera, defaultShader, senkuTexture);

        // Render cube at 1.0f 0.0f 0.0f scaled 2x
        modelMatrix.identity().translate(1.0f, 0.0f, 0.0f).scale(2.0f);
        defaultShader.setMatrix4f("model", modelMatrix);
        defaultShader.setBool("usesLighting", true);
        cube.render(camera, defaultShader, senkuTexture);

        // Render cubes with lamp texture representing light positions
        for (int i = 0; i < lightManager.getNextFreeIndex(lightManager.getPointLights()); i++) {
            modelMatrix.identity().translate(lightManager.getPointLights()[i].getPosition()).scale(0.55f);
            defaultShader.setMatrix4f("model", modelMatrix);
            defaultShader.setBool("usesLighting", false);
            cube.render(camera, defaultShader, lampTexture);
        }


        for (int i = 0; i < lightManager.getNextFreeIndex(lightManager.getSpotLights()); i++) {
            modelMatrix.identity().translate(lightManager.getSpotLights()[i].getPosition()).scale(0.55f);
            defaultShader.setMatrix4f("model", modelMatrix);
            defaultShader.setBool("usesLighting", false);
            cube.render(camera, defaultShader, lampTexture);
        }

        // Render skybox
        skybox.render(camera);
    }


    long lastCreated = System.currentTimeMillis();
    @Override
    public void input(InputManager input) {
        // Basic input for closing the game and switch for wire frame mode
        if (input.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) ScopeEngine.getInstance().end();
        if (input.isKeyPressed(GLFW.GLFW_KEY_X)) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); else glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        if (input.isKeyPressed(GLFW.GLFW_KEY_SPACE) && System.currentTimeMillis() - lastCreated >= 3000f) {
            lightManager.addLight(new PointLight(lightColor, new Vector3f(camera.getCameraPosition()), 1.0f));
            lightManager.setUniforms();

            lastCreated = System.currentTimeMillis();
        }

        if (input.isKeyPressed(GLFW.GLFW_KEY_M) && System.currentTimeMillis() - lastCreated >= 3000f) {
            lightManager.addLight(new SpotLight(lightColor, new Vector3f(camera.getCameraPosition()), 1.0f,  new Vector3f(camera.getDirection()), (float) Math.cos(Math.toRadians(15))));
            lightManager.setUniforms();

            lastCreated = System.currentTimeMillis();
        }
    }

    private void createShaderUniforms(ShaderProgram shader) {
        shader.createUniform("projection");
        shader.createUniform("model");
        shader.createUniform("view"); // You don't manually set "view" other than in your own models render functions,
    }

    @Override
    public void cleanup() { // TODO: Fix weird java memory violation crash error going on in one of the cleanups?
/*
        lightAffectedObjectShader.cleanup();
        defaultShader.cleanup();
        skybox.getSkyboxShader().cleanup();

        quad.cleanup(); // TODO: Abstract it to ModelManager.getModels().cleanup() though this shouldn't be run in an actual game that has another screen that reuses models, this is just a single screen example
        cube.cleanup();
        skybox.cleanup();
*/
    }
}
