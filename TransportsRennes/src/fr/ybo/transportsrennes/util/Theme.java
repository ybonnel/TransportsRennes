package fr.ybo.transportsrennes.util;

import android.graphics.Color;
import fr.ybo.transportsrennes.R;

public enum Theme {
	NOIR(Color.LTGRAY, R.style.Theme_TransportsRennes_black), BLANC(Color.BLACK, R.style.Theme_TransportsRennes);

	private int textColor;
	private int theme;

	private Theme(int textColor, int theme) {
		this.textColor = textColor;
		this.theme = theme;
	}

	public int getTextColor() {
		return textColor;
	}

	public int getTheme() {
		return theme;
	}

}
