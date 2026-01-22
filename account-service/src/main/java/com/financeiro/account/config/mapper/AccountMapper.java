package com.financeiro.account.config.mapper;

import com.financeiro.account.entity.Account;
import com.financeiro.account.entity.dto.request.CreateAccountRequestDTO;
import com.financeiro.account.entity.dto.request.UpdateAccountRequestDTO;
import com.financeiro.account.entity.dto.response.AccountResponseDTO;
import com.financeiro.account.entity.dto.response.AccountResumoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toEntity(CreateAccountRequestDTO createAccountRequestDTO);
    void updateEntity(UpdateAccountRequestDTO updateAccountRequestDTO, @MappingTarget Account account);
    AccountResponseDTO toResponseDTO(Account account);
    List<AccountResumoDTO> toListAccountResumoDTO(List<Account> account);
}
