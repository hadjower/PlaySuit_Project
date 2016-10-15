package com.shawasama.playsuit.support_view;


import android.widget.LinearLayout;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;


public class LinearLayoutTarget extends ViewGroupTarget<GlideDrawable>{
    public LinearLayoutTarget(LinearLayout linearLayout) {
        super(linearLayout);
    }

    @Override
    protected void setResource(GlideDrawable resource) {
        view.setBackground(resource);
    }
}
