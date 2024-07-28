package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AiManager {
    @Resource
    private YuCongMingClient client;

    /**
     *
     * @return
     */
    public String getAiGenRes(Long modelId, String message){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);

        BaseResponse<DevChatResponse> response = null;
        try {
            response = client.doChat(devChatRequest);
        } catch (Exception e) {
            ThrowUtils.throwIf(ObjectUtils.isEmpty(response), ErrorCode.OPERATION_ERROR, e + "请求接口失败");
        }
        return response.getData().getContent();
    }
}
