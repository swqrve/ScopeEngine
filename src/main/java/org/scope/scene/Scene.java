package org.scope.scene;

import org.scope.manager.InputManager;
import org.scope.framework.Cleanable;

public interface Scene extends Cleanable {
    void init();
    void input(InputManager input);
    void render();

    void update(double deltaTime);
}
