package com.ebious.pdf.service.naming.mapper;

import com.ebious.pdf.domain.dto.NamespaceDto;
import com.ebious.pdf.domain.entity.Namespace;
import org.springframework.stereotype.Component;

@Component
public class NamespaceMapper implements SimpleMapper<NamespaceDto, Namespace> {

    @Override
    public Namespace map(NamespaceDto dto) {
        return new Namespace(dto.getNamespace(), dto.getFilenames());
    }
}
