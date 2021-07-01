/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.constraintlayout.core.state;

import java.util.HashMap;

public class Registry {

    private static final Registry sRegistry = new Registry();

    public static Registry getInstance() {
        return sRegistry;
    }

    private HashMap<String, RegistryCallback> mCallbacks = new HashMap<>();

    public void register(String name, RegistryCallback callback) {
        mCallbacks.put(name, callback);
    }

    public void unregister(String name, RegistryCallback callback) {
        mCallbacks.remove(name);
    }

    public void updateContent(String name, String content) {
        RegistryCallback callback = mCallbacks.get(name);
        if (callback != null) {
            callback.onNewMotionScene(content);
        }
    }

    public void updateProgress(String name, float progress) {
        RegistryCallback callback = mCallbacks.get(name);
        if (callback != null) {
            callback.onProgress(progress);
        }
    }

    public String currentContent(String name) {
        RegistryCallback callback = mCallbacks.get(name);
        if (callback != null) {
            return callback.currentMotionScene();
        }
        return null;
    }

    public void setDrawDebug(String name, int debugMode) {
        RegistryCallback callback = mCallbacks.get(name);
        if (callback != null) {
            callback.setDrawDebug(debugMode);
        }
    }
}
