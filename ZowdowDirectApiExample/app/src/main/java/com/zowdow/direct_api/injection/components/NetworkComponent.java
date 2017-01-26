package com.zowdow.direct_api.injection.components;

import com.zowdow.direct_api.injection.modules.NetworkModule;
import com.zowdow.direct_api.ui.adapters.CardsAdapter;
import com.zowdow.direct_api.ui.adapters.SuggestionViewHolder;
import com.zowdow.direct_api.ui.sections.home.HomeDemoActivity;
import com.zowdow.direct_api.ui.views.ZowdowImageView;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = NetworkModule.class)
public interface NetworkComponent {
    void inject(HomeDemoActivity activity);
    void inject(SuggestionViewHolder viewHolder);
    void inject(CardsAdapter adapter);
    void inject(ZowdowImageView view);
}
