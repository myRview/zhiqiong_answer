package com.zhiqiong.common.constant;

import org.redisson.api.RLock;

/**
 * @author huangkun
 * @date 2025/3/15 20:05
 */
public class CacheConstants {

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";
    public static final String SCORE_RESULT_KEY = "score_results:";
    public static final String AI_ANSWER_LOCK = "ai_answer_lock:";


}
