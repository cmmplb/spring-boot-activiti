package com.cmmplb.activiti.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author plb
 * @date 2020/6/12 9:58
 */

public class ListUtil {

    /**
     * list分页
     * @param list    数据集
     * @param current 当前页
     * @param size    每页条数
     * @param <T>     指定数据集类型
     * @return 分页结果数据
     */
    public static <T> List<T> startPage(List<T> list, int current, int size) {
        if (list == null) {
            return new ArrayList<>();
        }
        if (list.size() == 0) {
            return null;
        }
        // 记录总数
        int count = list.size();
        // 页数
        int pageCount;
        if (count % size == 0) {
            pageCount = count / size;
        } else {
            pageCount = count / size + 1;
        }
        // 开始索引
        int fromIndex;
        // 结束索引
        int toIndex;
        if (current != pageCount) {
            fromIndex = (current - 1) * size;
            toIndex = fromIndex + size;
        } else {
            fromIndex = (current - 1) * size;
            toIndex = count;
        }
        if (fromIndex >= count) {
            return null;
        }
        if (toIndex > count) {
            toIndex = count - 1;
        }
        return list.subList(fromIndex, toIndex);
    }

    /**
     * 获取总页数
     * @param list 数据集
     * @param size 每页条数
     * @param <T>  指定数据集类型
     * @return 总页数
     */
    public static <T> Integer getTotalPage(List<T> list, int size) {
        if (list == null) {
            return 0;
        }
        if (list.size() == 0) {
            return 0;
        }
        // 记录总数
        int count = list.size();
        // 页数
        int pageCount;
        if (count % size == 0) {
            pageCount = count / size;
        } else {
            pageCount = count / size + 1;
        }
        return pageCount;
    }

}













