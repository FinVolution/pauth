package com.ppdai.auth.vo;

import lombok.Data;

/**
 * validation result
 *
 */
@Data
public class ValidityVO {

    Boolean isValid;

    public ValidityVO() {
        this.isValid = Boolean.FALSE;
    }

}
