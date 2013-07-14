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

package hu.vmiklos.lightmaker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class LightMakerActivity extends Activity {
	
	class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY)
		{
			if (event1.getX() - event2.getX() > 120)
			{
				m_viewFlipper.showNext();
				return true;
			}
			else if (event2.getX() - event1.getX() > 120)
			{
				m_viewFlipper.showPrevious();
				return true;
			}
			return false;
		}
	}
	
	@Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // brightness
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 1;

        // set color
        //TextView tv = new TextView(this);
        //tv.setBackgroundColor(0xffffffff); // argb white
        
        // quit on touch
        /*tv.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				finish();
				return false;
			}
		});
        
        setContentView(tv);*/
        ImageView imageView1 = new ImageView(this);
        imageView1.setImageResource(R.drawable.test1);
        ImageView imageView2 = new ImageView(this);
        imageView2.setImageResource(R.drawable.test2);
        m_viewFlipper = new ViewFlipper(this);
        m_viewFlipper.addView(imageView1);
        m_viewFlipper.addView(imageView2);
        setContentView(m_viewFlipper);
        m_gestureDetector = new GestureDetector(this, new GestureListener());
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return m_gestureDetector.onTouchEvent(event);
	}
	
	public ViewFlipper m_viewFlipper;
	private GestureDetector m_gestureDetector;
}
