package com.view.drop;

import android.app.Activity;
import android.os.Bundle;

public class BigDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WaterDrop wd=(WaterDrop)findViewById(R.id.drop);
        wd.setText("3");
        CoverManager.getInstance().init(this);
        CoverManager.getInstance().setMaxDragDistance(250);
 
    }

}
