package org.scope.scene;

import org.scope.framework.Cleanable;
import org.scope.input.InputManager;

public interface Scene extends Cleanable {
    void init();

    void input(InputManager input);

    void render(double deltaTime);

    void update(double deltaTime);
}
