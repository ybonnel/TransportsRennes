package fr.ybo.transportscommun;

import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;

public interface DonnesSpecifiques {

	String getApplicationName();

	int getCompactLogo();

	Class<?> getDrawableClass();

	int getIconeLigne();

	Class<? extends BaseListActivity> getDetailArretClass();

}
