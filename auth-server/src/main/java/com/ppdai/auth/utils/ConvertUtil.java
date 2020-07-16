package com.ppdai.auth.utils;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.exception.BaseException;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The utils to convert object between class/types
 *
 */
public class ConvertUtil {

    public static <S, T> T convert(S s, Class<T> tClass) {
        try {
            T t = tClass.newInstance();
            BeanUtils.copyProperties(s, t);
            return t;
        } catch (Exception e) {
            throw BaseException.newException(MessageType.ERROR, "convert error");
        }
    }

    public static <S, T> T convert(S s, T t) {
        try {
            BeanUtils.copyProperties(s, t);
            return t;
        } catch (Exception e) {
            throw BaseException.newException(MessageType.ERROR, "convert error");
        }
    }

    public static <S, T> List<T> convert(Iterable<S> iterable, Class<T> tClass) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(s -> ConvertUtil.convert(s, tClass)).collect(Collectors.toList());
    }

    public static <S, T> List<T> convert(Iterable<S> iterable, Function<? super S, ? extends T> mapper) {
        return StreamSupport.stream(iterable.spliterator(), false).map(mapper).collect(Collectors.toList());
    }

}
