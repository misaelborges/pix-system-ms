package com.financeiro.pixkey.config.mapper;

import com.financeiro.pixkey.entity.PixKey;
import com.financeiro.pixkey.entity.dto.request.CreatePixKeyRequestDTO;
import com.financeiro.pixkey.entity.dto.response.PixKeyResponseDTO;
import com.financeiro.pixkey.entity.dto.response.PixKeyResumoDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PixKeyMapper {

    PixKey toEntity(CreatePixKeyRequestDTO createPixKeyRequestDTO);
    PixKeyResponseDTO toResponseDTO(PixKey pixKey);
    List<PixKeyResumoDTO> toListResumoDTO(List<PixKey> pixKeyList);
}
