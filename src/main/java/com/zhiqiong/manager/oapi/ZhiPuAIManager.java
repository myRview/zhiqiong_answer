package com.zhiqiong.manager.oapi;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.image.CreateImageRequest;
import com.zhipu.oapi.service.v4.image.ImageApiResponse;
import com.zhipu.oapi.service.v4.image.ImageResult;
import com.zhipu.oapi.service.v4.model.*;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.exception.BusinessException;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangkun
 * @date 2025/3/24 19:44
 */
@Component
@Slf4j
public class ZhiPuAIManager {
    @Resource
    private ClientV4 client;

    private final static float DEFAULT_TEMPERATURE = 0.95f;
    private final static float STABLE_TEMPERATURE = 0.05f;


    public String doSendStableMessage(String systemMessage, String userMessage) {
        return doSendMessage(systemMessage, userMessage, STABLE_TEMPERATURE);
    }

    public String doSendRandomMessage(String systemMessage, String userMessage) {
        return doSendMessage(systemMessage, userMessage, DEFAULT_TEMPERATURE);
    }


    /**
     * @param systemMessage 系统消息
     * @param userMessage   用户消息
     * @param temperature   温度
     * @return
     */
    public String doSendMessage(String systemMessage, String userMessage, Float temperature) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        messages.add(systemMsg);
        ChatMessage userMsg = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(userMsg);
        return doSendMessage(messages, Boolean.FALSE, temperature);
    }

    /**
     * @param messages    消息列表
     * @param stream      表示模型在生成所有内容后一次性返回所有内容。默认值为false。如果设置为true，模型将通过标准Event Stream逐块返回生成的内容
     * @param temperature 取值范围是：[0.0,1.0]， 默认值为 0.95，值越大，会使输出更随机，更具创造性；值越小，输出会更加稳定或确定
     */
    public String doSendMessage(List<ChatMessage> messages, Boolean stream, Float temperature) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
            List<Choice> choices = invokeModelApiResp.getData().getChoices();
            return choices.get(0).getMessage().getContent().toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("调用智谱AI接口异常", e);
            throw new BusinessException(ErrorCode.ERROR_SYSTEM, "调用智谱AI接口异常");
        }
    }


    /**
     * 流式输出
     *
     * @param systemMessage 系统消息
     * @param userMessage   用户消息
     * @return
     */
    public Flowable<ModelData> doSendStreamMessage(String systemMessage, String userMessage) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        messages.add(systemMsg);
        ChatMessage userMsg = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(userMsg);
        return doSendStreamMessage(messages, DEFAULT_TEMPERATURE);
    }


    /**
     * @param messages    消息列表
     * @param temperature 取值范围是：[0.0,1.0]， 默认值为 0.95，值越大，会使输出更随机，更具创造性；值越小，输出会更加稳定或确定
     */
    public Flowable<ModelData> doSendStreamMessage(List<ChatMessage> messages, Float temperature) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            ModelApiResponse sseModelApiResp = client.invokeModelApi(chatCompletionRequest);
            return sseModelApiResp.getFlowable();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("调用智谱AI接口异常", e);
            throw new BusinessException(ErrorCode.ERROR_SYSTEM, "调用智谱AI接口异常");
        }
    }

    /**
     * 生成图片
     *
     * @param prompt 图片描述
     * @param size   图片大小   格式：1344x768
     * @return
     */
    public String doView(String prompt, String size) {
        CreateImageRequest imageRequest = CreateImageRequest.builder().model(Constants.ModelCogView).prompt(prompt).size(size).build();
        String url = null;
        try {
            ImageApiResponse image = client.createImage(imageRequest);
            ImageResult data = image.getData();
            url = data.getData().get(0).getUrl();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("调用智谱AI接口异常", e);
            throw new BusinessException(ErrorCode.ERROR_SYSTEM, "调用智谱AI接口异常");
        }
        return url;
    }
}
