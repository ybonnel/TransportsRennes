package fr.ybo.transportscommun.util;

import android.graphics.Color;
import fr.ybo.transportscommun.R;

public enum Theme {
	NOIR(Color.LTGRAY, R.style.Theme_Transports_black, R.drawable.actionbar_compat_background_black),

	BLANC(Color.BLACK, R.style.Theme_Transports, R.drawable.actionbar_compat_background);

	private int textColor;
	private int theme;
	private int actionBarBackground;

	private Theme(int textColor, int theme, int actionBarBackground) {
		this.textColor = textColor;
		this.theme = theme;
		this.actionBarBackground = actionBarBackground;
	}

	public int getTextColor() {
		return textColor;
	}

	public int getTheme() {
		return theme;
	}

	public int getActionBarBackground() {
		return actionBarBackground;
	}

}
