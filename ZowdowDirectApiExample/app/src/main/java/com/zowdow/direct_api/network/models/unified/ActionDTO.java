package com.zowdow.direct_api.network.models.unified;

import com.google.gson.annotations.SerializedName;

/**
 * Represents Card's action
 */
public class ActionDTO {
    @SerializedName("action_target") private String mActionTarget;
    @SerializedName("action_type") private String mActionType;

    public ActionDTO(String actionTarget, String actionType) {
        mActionTarget = actionTarget;
        mActionType = actionType;
    }

    public String getActionTarget() {
        return mActionTarget;
    }

    public String getActionType() {
        return mActionType;
    }
}
