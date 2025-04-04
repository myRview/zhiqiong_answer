package com.zhiqiong.manager.oapi;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zhipu.oapi.service.v4.model.ModelData;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.enums.AppTypeEnum;
import com.zhiqiong.manager.oapi.ZhiPuAIManager;
import com.zhiqiong.model.entity.AppEntity;
import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.question.AIGeneratorRequestVO;
import com.zhiqiong.model.vo.question.OptionVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.service.AppService;
import com.zhiqiong.utils.ThrowExceptionUtil;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author huangkun
 * @date 2025/3/24 22:02
 */
@Service
@Slf4j
public class AIService {

    private final String SYSTEM_PROMPT = "你是一位严谨的出题专家，我会给你如下信息：\n" +
            "===\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "应用类别，\n" +
            "要生成的题目数，\n" +
            "每个题目的选项数\n" +
            "===\n" +
            "请你根据上述信息，按照以下步骤来出题：\n" +
            "1. 要求：题目和选项尽可能地短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复\n" +
            "2. 严格按照下面的 json 格式输出题目和选项\n" +
            "===\n" +
            "[{\"options\":[{\"value\":\"选项内容\",\"key\":\"A\",\"result\":\"\"},{\"value\":\"\",\"key\":\"B\",\"result\":\"\"}],\"title\":\"题目标题\"}]\n" +
            "===\n" +
            "title 是题目，options 是选项，每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，value 是选项内容\n" +
            "3. 检查题目是否包含序号，若包含序号则去除序号\n" +
            "4. 返回的题目列表格式必须为 JSON 数组\n" +
            "5. 如果是得分类的题目将正确结果的选项中的result设置为true,测评类的题目result设置为偏向等级，等级使用字母表示\n";

    private final static String ANSWER_PROMPT = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "===\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "【【应用类型】】"+
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "===\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "===\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "===\n" +
            "3. 返回格式必须为 JSON 对象";

    @Resource
    private ZhiPuAIManager zhiPuAIManager;
    @Resource
    private AppService appService;


    public List<TopicVO> generateQuestion(AIGeneratorRequestVO requestVO) {
        Integer questionNum = requestVO.getQuestionNum();
        Integer optionNum = requestVO.getOptionNum();
        if (questionNum == 0 || optionNum == 0) {
            return CollectionUtil.newArrayList();
        }
        String userMessage = getMessage(requestVO);
        String message = zhiPuAIManager.doSendStableMessage(SYSTEM_PROMPT, userMessage);
        if (StrUtil.isNotBlank(message)) {
            return JSONUtil.toList(message, TopicVO.class);
        }
        return null;
    }

    public Flowable<ModelData> generateQuestionStream(AIGeneratorRequestVO requestVO) {
        Integer questionNum = requestVO.getQuestionNum();
        Integer optionNum = requestVO.getOptionNum();
        if (questionNum == 0 || optionNum == 0) {
            return null;
        }
        String userMessage = getMessage(requestVO);
        if (StrUtil.isBlank(userMessage)) return null;
        return zhiPuAIManager.doSendStreamMessage(SYSTEM_PROMPT, userMessage);

    }

    public String aiGeneratorResult(List<TopicVO> topics, List<String> choices, AppVO appVO) {
        String userMessage = getAnswerMessage(topics, choices, appVO);
        return zhiPuAIManager.doSendStableMessage(ANSWER_PROMPT, userMessage);
    }

    public String generateImage(String prompt, String size) {
        return zhiPuAIManager.doView(prompt, size);
    }

    private String getAnswerMessage(List<TopicVO> topics, List<String> choices, AppVO appVO) {
        StringBuilder builder = new StringBuilder();
        builder.append(appVO.getAppName()).append("，\n");
        builder.append("【【【").append(appVO.getAppDesc()).append("】】】，\n");
        builder.append("【【【").append(AppTypeEnum.getEnumByValue(appVO.getAppType()).getDescription()).append("】】】，\n");
        builder.append("[");
        for (int i = 0; i < topics.size(); i++) {
            TopicVO topicVO = topics.get(i);
            String answer = choices.get(i);
            List<OptionVO> options = topicVO.getOptions();
            for (OptionVO optionVO : options) {
                if (optionVO.getKey().equals(answer)) {
                    builder.append("{")
                            .append("\"title\":\"").append(topicVO.getTitle())
                            .append("\",\"answer\":\"").append(optionVO.getValue())
                            .append("\"}");
                    break;
                }
            }
        }
        builder.append("]");
        return builder.toString();
    }

    private String getMessage(AIGeneratorRequestVO requestVO) {
        Long appId = requestVO.getAppId();
        Integer questionNum = requestVO.getQuestionNum();
        Integer optionNum = requestVO.getOptionNum();
        AppEntity app = appService.getById(appId);
        ThrowExceptionUtil.throwIf(app == null, ErrorCode.ERROR_PARAM, "应用不存在");
        AppTypeEnum typeEnum = AppTypeEnum.getEnumByValue(app.getAppType());
        ThrowExceptionUtil.throwIf(typeEnum == null, ErrorCode.ERROR_PARAM, "应用类型不存在");
        StringBuilder builder = new StringBuilder();
        builder.append(app.getAppName())
                .append("，\n")
                .append("【【【")
                .append(app.getAppDesc())
                .append("】】】，\n")
                .append(typeEnum.getDescription())
                .append("，\n")
                .append(questionNum)
                .append("，\n")
                .append(optionNum);
        return builder.toString();
    }


}
