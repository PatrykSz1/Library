package com.test.testtechnicalsystem.mapper;

import com.test.testtechnicalsystem.model.MonitorMethodModel;
import com.test.testtechnicalsystem.model.MonitorMethodModelDto;
import org.mapstruct.Mapper;

@Mapper
public interface MonitorMethodModelMapper {

    MonitorMethodModelDto toDto(MonitorMethodModel monitorMethodModel);

    MonitorMethodModel toEntity(MonitorMethodModelDto monitorMethodModelDto);


}