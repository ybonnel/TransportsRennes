package fr.ybo.transportscommun.activity.commun;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.ubikod.capptain.android.sdk.CapptainAgent;
import com.ubikod.capptain.android.sdk.CapptainAgentUtils;

public abstract class CapptainFragmentActivity extends FragmentActivity {
	private CapptainAgent mCapptainAgent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCapptainAgent = CapptainAgent.getInstance(this);
	}

	@Override
	protected void onResume() {
		mCapptainAgent.startActivity(this, getCapptainActivityName(), getCapptainActivityExtra());
		super.onResume();
	}

	@Override
	protected void onPause() {
		mCapptainAgent.endActivity();
		super.onPause();
	}

	/**
	 * Get the Capptain agent attached to this activity.
	 * 
	 * @return the Capptain agent
	 */
	public final CapptainAgent getCapptainAgent() {
		return mCapptainAgent;
	}

	/**
	 * Override this to specify the name reported by your activity. The default
	 * implementation returns the simple name of the class and removes the
	 * "Activity" suffix if any (e.g. "com.mycompany.MainActivity" -> "Main").
	 * 
	 * @return the activity name reported by the Capptain service.
	 */
	protected String getCapptainActivityName() {
		return CapptainAgentUtils.buildCapptainActivityName(getClass());
	}

	/**
	 * Override this to attach extra information to your activity. The default
	 * implementation attaches no extra information (i.e. return null).
	 * 
	 * @return activity extra information, null or empty if no extra.
	 */
	protected Bundle getCapptainActivityExtra() {
		return null;
	}
}
