package com.zhiqiong.manager;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.enums.AppTypeEnum;
import com.zhiqiong.manager.oapi.ZhiPuAIManager;
import com.zhiqiong.model.entity.AppEntity;
import com.zhiqiong.model.vo.question.AIGeneratorRequestVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.service.AppService;
import com.zhiqiong.utils.ThrowExceptionUtil;
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
            "4. 返回的题目列表格式必须为 JSON 数组\n"+
            "5. 如果是得分类的题目将正确结果的选项中的result设置为true,测评类的题目result设置为偏向等级，等级使用字母表示\n";


    @Resource
    private ZhiPuAIManager zhiPuAIManager;
    @Resource
    private AppService appService;


    public List<TopicVO> generateQuestion(AIGeneratorRequestVO requestVO) {
        String userMessage = getMessage(requestVO);
        String message = zhiPuAIManager.doSendStableMessage(SYSTEM_PROMPT, userMessage);
        if (StrUtil.isNotBlank(message)) {
            return JSONUtil.toList(message, TopicVO.class);
        }
        return null;
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
//        return "java面试题" + "，\n" +
//                "【【【java面试题】】】，\n" +
//                "得分类，\n" +
//                questionNum + "，\n" +
//                optionNum;
//        return "MBTI 性格测试，\n" +
//                "【【【快来测测你的 MBTI 性格】】】，\n" +
//                "测评类，\n" +
//                "3，\n" +
//                "3\n";
    }


}
