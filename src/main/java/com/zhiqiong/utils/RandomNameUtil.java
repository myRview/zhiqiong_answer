package com.zhiqiong.utils;

import cn.hutool.core.util.RandomUtil;

/**
 * @author huangkun
 * @date 2024/12/27 18:56
 */
public class RandomNameUtil {

    // 生成一个包含3个字的中文名称
    public static String generateName() {
        StringBuilder name = new StringBuilder();
        name.append("uid");
        int[] ints = RandomUtil.randomInts(8);
        for (int anInt : ints) {
            name.append(anInt);
        }
        return name.toString();
    }

    public static void main(String[] args) {
        System.out.println(generateName());
    }
}
