/*
 * Version: MPL 1.1 / GPLv3+ / LGPLv3+
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License or as specified alternatively below. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Major Contributor(s):
 * Copyright (C) 2012 Miklos Vajna <vmiklos@vmiklos.hu> (initial developer) 
 *
 * All Rights Reserved.
 *
 * For minor contributions see the git repository.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 3 or later (the "GPLv3+"), or
 * the GNU Lesser General Public License Version 3 or later (the "LGPLv3+"),
 * in which case the provisions of the GPLv3+ or the LGPLv3+ are applicable
 * instead of those above.
 */

package hu.vmiklos.lightmakerzoom;

import hu.vmiklos.lightmakerzoom.R;
import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;

public class LightMakerActivity extends Activity {
	
	class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY)
		{
			if (m_scaleDetector.inZoom())
				return false;
			if (event1.getX() - event2.getX() > 120)
			{
				m_scaleDetector.reset();
				m_viewFlipper.showNext();
				return true;
			}
			else if (event2.getX() - event1.getX() > 120)
			{
				m_scaleDetector.reset();
				m_viewFlipper.showPrevious();
				return true;
			}
			return false;
		}
	}
	
	class ScaleListener implements OnTouchListener
	{
		public ScaleListener()
		{
			reset();
		}
		
		public boolean onTouch(View v, MotionEvent event)
		{
			ImageView view = (ImageView) v;
			if (view.getScaleType() == ScaleType.FIT_CENTER)
			{
				origValues = new float[9];
				view.getImageMatrix().getValues(origValues);
				matrix.setValues(origValues);
				view.setScaleType(ScaleType.MATRIX);
			}

			switch (event.getAction() & MotionEvent.ACTION_MASK)
			{
			case MotionEvent.ACTION_DOWN:
				if (inZoom())
				{
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
				}
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				float[] values = new float[9];
				view.getImageMatrix().getValues(values);
				currentScaleX = values[Matrix.MSCALE_X];
				currentScaleY = values[Matrix.MSCALE_Y];
				if (currentScaleX < origValues[Matrix.MSCALE_X])
					matrix.setValues(origValues);
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
				}
				else if (mode == ZOOM) {
					float newDist = spacing(event);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = newDist / oldDist;
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
				break;
			}

			view.setImageMatrix(matrix);
			return true;
		}

		private float spacing(MotionEvent event)
		{
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}

		private void midPoint(PointF point, MotionEvent event)
		{
			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}
		
		public void reset()
		{
			((ImageView)m_viewFlipper.getCurrentView()).setScaleType(ScaleType.FIT_CENTER);
			matrix = new Matrix();
			savedMatrix = new Matrix();
			mode = NONE;
			start = new PointF();
			mid = new PointF();
			oldDist = 1f;
			origValues = null;
			currentScaleX = 0;
			currentScaleY = 0;
		}
		
		public boolean inZoom()
		{
			return origValues != null && origValues[Matrix.MSCALE_X] < currentScaleX;
		}
		
		Matrix matrix; // Matrix of the current view
		Matrix savedMatrix; // Matrix when the user started the touch.

		static final int NONE = 0;
		static final int DRAG = 1;
		static final int ZOOM = 2;
		int mode;

		PointF start;
		PointF mid;
		float oldDist;

		float[] origValues;
		float currentScaleX;
		float currentScaleY;
	}
	
	@Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ImageView imageView1 = new ImageView(this);
        imageView1.setImageResource(R.drawable.test1);
        ImageView imageView2 = new ImageView(this);
        imageView2.setImageResource(R.drawable.test2);
        m_viewFlipper = new ViewFlipper(this);
        m_viewFlipper.addView(imageView1);
        m_viewFlipper.addView(imageView2);
        setContentView(m_viewFlipper);
        m_gestureDetector = new GestureDetector(this, new GestureListener());
        m_scaleDetector = new ScaleListener();
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (m_gestureDetector.onTouchEvent(event))
			return true;
		if (m_scaleDetector.onTouch(m_viewFlipper.getCurrentView(), event))
			return true;
		return false;
	}
	
	public ViewFlipper m_viewFlipper;
	private GestureDetector m_gestureDetector;
	public ScaleListener m_scaleDetector;
}
