package com.zhiqiong.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhipu.oapi.service.v4.model.ModelData;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.common.ResponseResult;
import com.zhiqiong.exception.BusinessException;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.question.AIGeneratorRequestVO;
import com.zhiqiong.model.vo.question.AddQuestionVO;
import com.zhiqiong.model.vo.question.QuestionPageVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.manager.oapi.AIService;
import com.zhiqiong.service.QuestionService;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 题目 前端控制器
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/question")
//@Api(tags = "题目管理")
public class QuestionController {

    @Resource
    private QuestionService questionService;
    @Resource
    private AIService aiService;


    @GetMapping("/id")
    @ApiOperation(value = "获取题目详情")
    public ResponseResult<TopicVO> selectQuestionInfo(@RequestParam(value = "id") Long id) {
        TopicVO topicVO = questionService.selectTopicInfo(id);
        return ResponseResult.success(topicVO);
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取题目列表")
    public ResponseResult<AddQuestionVO> selectQuestionList(@RequestParam(value = "appId", required = false) Long appId) {
        AddQuestionVO questionVOList = questionService.selectQuestionList(appId);
        return ResponseResult.success(questionVOList);
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加题目")
    public ResponseResult<Boolean> addQuestion(@RequestBody AddQuestionVO addQuestionVO) {
        boolean add = questionService.addQuestion(addQuestionVO);
        return ResponseResult.success(add);
    }

    @PostMapping("/page")
    @ApiOperation(value = "获取题目列表")
    public ResponseResult<Page<TopicVO>> selectTopicPage(@RequestBody QuestionPageVO questionPageVO) {
        Page<TopicVO> page = questionService.selectTopicPage(questionPageVO);
        return ResponseResult.success(page);
    }


    @DeleteMapping("/delete")
    @ApiOperation(value = "删除题目")
    public ResponseResult<Boolean> deleteQuestion(@RequestBody IdVO idVO) {
        Long id = idVO.getId();
        boolean del = questionService.removeById(id);
        return ResponseResult.success(del);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新题目")
    public ResponseResult<Boolean> updateQuestion(@RequestBody TopicVO topicVO) {
        boolean update = questionService.updateTopic(topicVO);
        return ResponseResult.success(update);
    }


    @PostMapping("/generator")
    @ApiOperation(value = "AI生成题目")
    public ResponseResult<List<TopicVO>> generatorQuestion(@RequestBody AIGeneratorRequestVO requestVO) {
        List<TopicVO> topics = aiService.generateQuestion(requestVO);
        return ResponseResult.success(topics);
    }

    @GetMapping("/sse/generator")
    @ApiOperation(value = "AI生成题目(流式)")
    public SseEmitter generateQuestionStream(AIGeneratorRequestVO requestVO) {
        Flowable<ModelData> flowable = aiService.generateQuestionStream(requestVO);
        if (flowable == null) {
            throw new BusinessException(ErrorCode.ERROR_SYSTEM, "生成失败");
        }
        SseEmitter flowableEmitter = new SseEmitter();
        StringBuilder contentBuilder = new StringBuilder();
        AtomicInteger flag = new AtomicInteger(0);
        flowable
                // 异步线程池执行
                .observeOn(Schedulers.io())
                .map(chunk -> chunk.getChoices().get(0).getDelta().getContent())
                .map(message -> message.replaceAll("\\s", ""))
                .filter(StrUtil::isNotBlank)
                .flatMap(message -> {
                    // 将字符串转换为 List<Character>
                    List<Character> charList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        charList.add(c);
                    }
                    return Flowable.fromIterable(charList);
                })
                .doOnNext(c -> {
                    {
                        // 识别第一个 [ 表示开始 AI 传输 json 数据，打开 flag 开始拼接 json 数组
                        if (c == '{') {
                            flag.addAndGet(1);
                        }
                        if (flag.get() > 0) {
                            contentBuilder.append(c);
                        }
                        if (c == '}') {
                            flag.addAndGet(-1);
                            if (flag.get() == 0) {
                                // 累积单套题目满足 json 格式后，sse 推送至前端
                                // sse 需要压缩成当行 json，sse 无法识别换行
                                flowableEmitter.send(JSONUtil.toJsonStr(contentBuilder.toString()));
                                // 清空 StringBuilder
                                contentBuilder.setLength(0);
                            }
                        }
                    }
                }).doOnComplete(flowableEmitter::complete).subscribe();
        return flowableEmitter;
    }
}
