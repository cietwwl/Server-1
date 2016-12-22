package com.gm.multipletimeshotfix;

import java.net.URL;
import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import com.playerdata.activity.growthFund.data.ActivityGrowthFundItemHolder;

public class ReHFActivityGrowthFund implements Callable<Void>{

	@Override
	public Void call() throws Exception {
		
		Field instanceField = ActivityGrowthFundItemHolder.class.getDeclaredField("instance");
		instanceField.setAccessible(true);
		instanceField.set(ActivityGrowthFundItemHolder.getInstance(), new ActivityGrowthFundItemHolderReHF());
		
		URL url = ClassLoader.getSystemResource("com/gm/multipletimeshotfix");
		File packageFile = new File(url.getFile());
		File[] files = packageFile.listFiles();
		File temp;
		for (int i = 0; i < files.length; i++) {
			temp = files[i];
			if (temp.getName().equals("ActivityGrowthFundItemHolderHotfix.class") 
					|| temp.getName().equals("HotFixActivityGrowthFund.class")) {
				files[i].delete();
			}
		}
		return null;
	}

}
