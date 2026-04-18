package com.shailahir.koha.circulation.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JacksonXmlRootElement
public class CirculationRuleDto {
    private String branchcode;
    private String categorycode;
    private String itemtype;
    private Map<String, Object> rules;
}

