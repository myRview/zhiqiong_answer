package com.zhiqiong.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 20231
 */
@Data
public class MBTIResult {

    // 类型代码（如 "INTJ"）
    private String typeCode;
    // 类型名称（如 "物流师"）
    private String typeName;
    // 类型描述
    private String description;


    private static final Map<String, Integer> DIMENSION_SCORES = new HashMap<>();

    private static final Map<String, String[]> MBTI_TYPE_MAP = new HashMap<>();

    static {

        //MBTI 类型名称和描述映射（可扩展为枚举或从数据库加载）
        // 物流师
        MBTI_TYPE_MAP.put("INTJ", new String[]{"物流师", "冷静的战略家，擅长规划和执行复杂系统"});
        // 逻辑学家
        MBTI_TYPE_MAP.put("INTP", new String[]{"逻辑学家", "创新的思考者，痴迷于理论和可能性"});
        // 指挥官
        MBTI_TYPE_MAP.put("ENTJ", new String[]{"指挥官", "果断的领导者，善于组织资源和达成目标"});
        // 辩论家
        MBTI_TYPE_MAP.put("ENTP", new String[]{"辩论家", "机智的探索者，喜欢挑战思想和创造新方案"});
        // 提倡者
        MBTI_TYPE_MAP.put("INFJ", new String[]{"提倡者", "理想主义的引路人，致力于帮助他人成长"});
        // 调解员
        MBTI_TYPE_MAP.put("INFP", new String[]{"调解员", "充满热情的利他主义者，追求和谐与意义"});
        // 主人公
        MBTI_TYPE_MAP.put("ENFJ", new String[]{"主人公", "富有魅力的导师，激励他人实现潜力"});
        // 竞选者
        MBTI_TYPE_MAP.put("ENFP", new String[]{"竞选者", "热情洋溢的发起人，充满好奇和创造力"});
        // 稽查员
        MBTI_TYPE_MAP.put("ISTJ", new String[]{"稽查员", "严谨的务实者，坚守秩序和责任感"});
        // 守护者
        MBTI_TYPE_MAP.put("ISFJ", new String[]{"守护者", "忠诚的保护者，细心照顾他人需求"});
        // 总经理
        MBTI_TYPE_MAP.put("ESTJ", new String[]{"总经理", "高效的执行者，擅长管理和维护规则"});
        // 执行官
        MBTI_TYPE_MAP.put("ESFJ", new String[]{"执政官", "温暖的协调者，致力于创造社会和谐"});
        // 鉴赏家
        MBTI_TYPE_MAP.put("ISTP", new String[]{"鉴赏家", "灵活的工匠，擅长动手解决实际问题"});
        // 探险家
        MBTI_TYPE_MAP.put("ISFP", new String[]{"探险家", "敏感的艺术家，追求自由和感官体验"});
        // 企业家
        MBTI_TYPE_MAP.put("ESTP", new String[]{"企业家", "机智的实干家，善于快速应对挑战"});
        // 表演者
        MBTI_TYPE_MAP.put("ESFP", new String[]{"表演者", "活力的娱乐者，热爱社交和即兴发挥"});

        // 各维度得分
        DIMENSION_SCORES.put("E", 5);
        DIMENSION_SCORES.put("I", 8);
        DIMENSION_SCORES.put("S", 3);
        DIMENSION_SCORES.put("N", 4);
        DIMENSION_SCORES.put("T", 6);
        DIMENSION_SCORES.put("F", 7);
    }

    public static Map<String, Integer> getDimensionScores() {
        return DIMENSION_SCORES;
    }

    public static MBTIResult getResult(String typeCode) {
        String[] strings = MBTI_TYPE_MAP.get(typeCode);
        if (strings == null) {
            return null;
        }
        MBTIResult mbtiResult = new MBTIResult();
        mbtiResult.setTypeCode(typeCode);
        mbtiResult.setTypeName(strings[0]);
        mbtiResult.setDescription(strings[1]);
        return mbtiResult;
    }
}