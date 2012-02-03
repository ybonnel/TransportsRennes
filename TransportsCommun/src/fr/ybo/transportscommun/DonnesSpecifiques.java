package fr.ybo.transportscommun;

import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;

public interface DonnesSpecifiques {

	public String getApplicationName();

	public int getCompactLogo();

	public Class<?> getDrawableClass();

	public int getIconeLigne();

	public Class<? extends BaseListActivity> getDetailArretClass();

}
