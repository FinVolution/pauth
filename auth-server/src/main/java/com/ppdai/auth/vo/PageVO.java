package com.ppdai.auth.vo;

import lombok.Data;

import java.util.List;

/**
 * page view object
 *
 */

@Data
public class PageVO<T> {
    private List<T> content;
    private Long totalElements;
}
