/* Copyright 2015 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * A copy of the license is available in the repository's
 * https://github.com/Esri/arcgis-runtime-demos-android/blob/master/license.txt
 *
 * For information about licensing your deployed app, see
 * https://developers.arcgis.com/android/guide/license-your-app.htm
 *
 */

package com.esri.arcgisruntime.wearcontrol;

import android.widget.Toast;

import com.esri.android.map.MapView;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;

public class MyoControlListener extends AbstractDeviceListener {
  // The arm that Myo is on is unknown until the arm recognized event is received.
  protected Arm mArm = Arm.UNKNOWN;

  protected MainActivity mActivity;
  protected MapView mMapView;
  protected XDirection mXDirection;

  private long mActivatedTime;

  public MyoControlListener(MainActivity activity, MapView mapView) {
    mActivity = activity;
    mMapView = mapView;
    mActivatedTime = System.currentTimeMillis();
  }

  // onArmRecognized() is called whenever Myo has recognized a setup gesture after someone has put it on their
  // arm. This lets Myo know which arm it's on and which way it's facing.
  @Override
  public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
    // Save the arm the Myo is on so that we can use it in the pose events.
    mArm = arm;
    mXDirection = xDirection;
  }

  // onArmLost() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
  // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
  // when Myo is moved around on the arm.
  @Override
  public void onArmUnsync(Myo myo, long timestamp) {
    mArm = Arm.UNKNOWN;
  }

  @Override
  public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
    if(Math.abs(gyro.x()) > 150 && (System.currentTimeMillis() - mActivatedTime >= 3000)) {
      myo.vibrate(Myo.VibrationType.SHORT);
      mActivity.switchMyoListener();
    }
  }

  protected void lock(final Myo myo) {
    showToast("Control locked!");
    myo.lock();
  }

  protected void unlock(final Myo myo) {
    showToast("Control unlocked!");
    myo.unlock(Myo.UnlockType.HOLD);
  }

  protected void showToast(final String message) {
    mActivity.runOnUiThread(new Runnable() {
      public void run() {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
      }
    });
  }

  public void prepareSwitch() {
    mActivatedTime = System.currentTimeMillis();
  }
}
