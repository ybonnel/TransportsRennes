package fr.ybo.transportsbordeaux.activity.loading;

import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportscommun.activity.loading.AbstractLoadingActivity;

public class LoadingActivity extends AbstractLoadingActivity {

	@Override
	protected int getInfoChangementGtfs() {
		return R.string.infoChargementGtfs;
	}

	@Override
	protected int getChargementLigneFavori() {
		return R.string.chargementLigneFavori;
	}

	@Override
	protected Class<?> getRawClass() {
		return R.raw.class;
	}

	@Override
	protected int getErreurNoSpaceLeft() {
		return R.string.erreurNoSpaceLeft;
	}

	@Override
	protected int getLastUpdate() {
		return R.raw.last_update;
	}

	@Override
	protected int getPremierAccesLigne() {
		return R.string.premierAccesLigne;
	}

}
