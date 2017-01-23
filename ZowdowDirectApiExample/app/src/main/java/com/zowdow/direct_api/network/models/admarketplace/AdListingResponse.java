package com.zowdow.direct_api.network.models.admarketplace;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Response-wrapper for XML ad listings.
 */
@Root(name = "result", strict = false)
public class AdListingResponse {
    @ElementList(name = "adlistings")
    private List<AdListingDTO> adlistings;

    public List<AdListingDTO> getAdlistings() {
        return adlistings;
    }

    public void setAdlistings(List<AdListingDTO> adlistings) {
        this.adlistings = adlistings;
    }
}
