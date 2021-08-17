/*
 * Copyright (C) 2017 The Android Open Source Project
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
package androidx.constraintLayout.desktop.constraintRendering;

import androidx.constraintLayout.desktop.constraintRendering.draw.ColorSet;
import androidx.constraintLayout.desktop.constraintRendering.drawing.BlueprintColorSet;
import androidx.constraintLayout.desktop.utils.ScenePicker;
import java.awt.Rectangle;


/**
 * This provides the information for painting the related  such like transform from dp to screen space.
 */
public abstract class SceneContext {
  // Picker is used to record all graphics drawn to support selection
  private final ScenePicker myGraphicsPicker = new ScenePicker();
  private Object myFoundObject;
  private Long myTime;
  private int myMouseX = -1;
  private int myMouseY = -1;
  private boolean myShowOnlySelection = false;

  protected final Rectangle myRenderableBounds = new Rectangle();

  public SceneContext() {
    myTime = System.currentTimeMillis();
    myGraphicsPicker.setSelectListener((over, dist) -> myFoundObject = over);
  }

  public void setShowOnlySelection(boolean value) {
    myShowOnlySelection = value;
  }

  public long getTime() {
    return myTime;
  }

  public void setTime(long time) {
    myTime = time;
  }

  public void setMouseLocation( int x,  int y) {
    myMouseX = x;
    myMouseY = y;
  }

  /**
   * Get the X location of the mouse
   *
   * @return
   */
  
  public int getMouseX() {
    return myMouseX;
  }

  /**
   * Get the Y location of the mouse
   *
   * @return
   */
  
  public int getMouseY() {
    return myMouseY;
  }

  /**
   * Used to request Repaint
   */
  public void repaint() {
  }

  
  public int getSwingXDip(float x) {
    return (int) x;
  }

  
  public int getSwingYDip(float y) {
    return (int) y;
  }

  
  public int getSwingX( int x) {
    return x;
  }

  
  public int getSwingY( int y) {
    return y;
  }

  
  public int getSwingDimensionDip(float dim) {
    return (int) dim;
  }

  
  public int getSwingDimension( int dim) {
    return dim;
  }

  public abstract ColorSet getColorSet();

  public ScenePicker getScenePicker() {
    return myGraphicsPicker;
  }

  /**
   * Find objects drawn on the {}.
   * Objects drawn with this {@link SceneContext} can record there shapes
   * and this find can be used to detect them.
   * @param x
   * @param y
   * @return The clicked objects if exist, or null otherwise.
   */

  public Object findClickedGraphics( int x,  int y) {
    myFoundObject = null;
    myGraphicsPicker.find(x, y);
    return myFoundObject;
  }

  public double getScale() { return 1; }

  public final void setRenderableBounds( Rectangle bounds) {
    myRenderableBounds.setBounds(bounds);
  }

  
  
  public final Rectangle getRenderableBounds() {
    return myRenderableBounds;
  }

  private static SceneContext lazySingleton;

  /**
   * Provide an Identity transform used in testing
   */
 
  
  public static SceneContext get() {
    if (lazySingleton == null) {
      lazySingleton = new IdentitySceneContext();
    }
    return lazySingleton;
  }

  /**
   * Get a {@link SceneContext} for the given
   *
   */


  
  public float pxToDp( int px) {
    return px *1;
  }

  public boolean showOnlySelection() {
    return myShowOnlySelection;
  }

  /**
   * A {@link SceneContext} for testing purpose, which treat all coordinates system as the same one.
   */
  private static class IdentitySceneContext extends SceneContext {

    private ColorSet myColorSet = new BlueprintColorSet();

    @Override
    
    public ColorSet getColorSet() {
      return myColorSet;
    }
  }
}
