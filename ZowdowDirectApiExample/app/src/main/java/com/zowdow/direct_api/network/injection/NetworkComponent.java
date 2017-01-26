package com.zowdow.direct_api.network.injection;

import com.zowdow.direct_api.ui.adapters.CardsAdapter;
import com.zowdow.direct_api.ui.adapters.SuggestionViewHolder;
import com.zowdow.direct_api.ui.HomeDemoActivity;
import com.zowdow.direct_api.ui.views.CardImageView;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = NetworkModule.class)
public interface NetworkComponent {
    void inject(HomeDemoActivity activity);
    void inject(SuggestionViewHolder viewHolder);
    void inject(CardsAdapter adapter);
    void inject(CardImageView view);
}
