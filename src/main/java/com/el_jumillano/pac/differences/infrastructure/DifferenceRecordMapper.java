package com.el_jumillano.pac.differences.infrastructure;

import com.el_jumillano.pac.differences.domain.DifferenceRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DifferenceRecordMapper {

    DifferenceRecord toDomain(DifferenceRecordJpaEntity entity);

    DifferenceRecordJpaEntity toEntity(DifferenceRecord domain);
}
