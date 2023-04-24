package org.scope.entity;

import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class SDEntity {
    @Getter private Vector2f position = new Vector2f();

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    public void setPosition(Vector2f position) {
        this.position.set(position);
    }

    public void setX(float x) {
        this.position.x = x;
    }

    public void setY(float y) {
        this.position.y = y;
    }


    public float getX() {
        return this.position.x;
    }

    public float getY() {
        return this.position.y;
    }

}

