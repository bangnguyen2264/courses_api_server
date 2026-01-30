package com.example.course.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springdoc.core.annotations.ParameterObject;

@Data
@ToString(callSuper=true)
@Schema(allOf = {BaseFilter.class})
@ParameterObject
@EqualsAndHashCode(callSuper = true)
public class SubjectFilter extends BaseFilter{
    private String name;
    private Boolean status;

}
