package com.financeiro.pixkey.service;

import com.financeiro.pixkey.config.mapper.PixKeyMapper;
import com.financeiro.pixkey.entity.KeyTypeEnum;
import com.financeiro.pixkey.entity.PixKey;
import com.financeiro.pixkey.entity.dto.request.CreatePixKeyRequestDTO;
import com.financeiro.pixkey.entity.dto.response.PixKeyResponseDTO;
import com.financeiro.pixkey.entity.dto.response.ValidatePixKeyResponseDTO;
import com.financeiro.pixkey.exception.*;
import com.financeiro.pixkey.repository.PixKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PixKeyServiceTest {

    @Mock
    private PixKeyRepository pixKeyRepository;

    @Mock
    private CacheService cacheService;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private PixKeyMapper pixKeyMapper;

    @InjectMocks
    private PixKeyService pixKeyService;

    private PixKey pixKey;
    private PixKeyResponseDTO pixKeyResponseDTO;
    private CreatePixKeyRequestDTO createPixKeyRequestDTO;
    private ValidatePixKeyResponseDTO validatePixKeyResponseDTO;

    @BeforeEach
    void setUp() {
        pixKey = new PixKey(1L, 1L, KeyTypeEnum.CPF, "12345678901", true,
                OffsetDateTime.now(), null);

        pixKeyResponseDTO = new PixKeyResponseDTO(1L, 1L, "CPF", "12345678901",
                OffsetDateTime.now(), true);

        createPixKeyRequestDTO = new CreatePixKeyRequestDTO(1L, "CPF", "12345678901");

        validatePixKeyResponseDTO = new ValidatePixKeyResponseDTO(1L, "CPF", "12345678901");
    }

    @Test
    @DisplayName("Deve criar chave Pix com sucesso quando todos os dados forem válidos")
    void shouldCreatePixKeySuccessfully() {
        when(accountServiceClient.validateAccountExists(createPixKeyRequestDTO.accountId())).thenReturn(true);
        when(pixKeyRepository.countByAccountIdAndActiveTrue(createPixKeyRequestDTO.accountId())).thenReturn(0L);
        when(pixKeyRepository.existsByAccountIdAndKeyTypeAndActiveTrue(
                createPixKeyRequestDTO.accountId(), KeyTypeEnum.CPF)).thenReturn(false);
        when(pixKeyRepository.save(any(PixKey.class))).thenReturn(pixKey);
        when(pixKeyMapper.toResponseDTO(pixKey)).thenReturn(pixKeyResponseDTO);

        PixKeyResponseDTO result = pixKeyService.create(createPixKeyRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.accountId());
        assertEquals("CPF", result.keyType());
        assertEquals("12345678901", result.keyValue());
        assertTrue(result.active());

        verify(accountServiceClient, times(1)).validateAccountExists(1L);
        verify(pixKeyRepository, times(1)).countByAccountIdAndActiveTrue(1L);
        verify(pixKeyRepository, times(1)).existsByAccountIdAndKeyTypeAndActiveTrue(1L, KeyTypeEnum.CPF);
        verify(pixKeyRepository, times(1)).save(any(PixKey.class));
        verify(cacheService, times(1)).cachePixKey("12345678901", 1L);
        verify(pixKeyMapper, times(1)).toResponseDTO(pixKey);
    }

    @Test
    @DisplayName("Deve retornar 409 quando tentar criar segunda chave CPF da mesma conta")
    void shouldReturnConflictWhenTwoSameCpfKeyType() {
        when(accountServiceClient.validateAccountExists(createPixKeyRequestDTO.accountId())).thenReturn(true);
        when(pixKeyRepository.countByAccountIdAndActiveTrue(createPixKeyRequestDTO.accountId())).thenReturn(0L);
        when(pixKeyRepository.existsByAccountIdAndKeyTypeAndActiveTrue(
                createPixKeyRequestDTO.accountId(), KeyTypeEnum.CPF)).thenReturn(true);

        assertThrows(PixKeyAlreadyExistsException.class, () -> {
            pixKeyService.create(createPixKeyRequestDTO);
        });

        verify(accountServiceClient, times(1)).validateAccountExists(1L);
        verify(pixKeyRepository, times(1)).existsByAccountIdAndKeyTypeAndActiveTrue(1L, KeyTypeEnum.CPF);
        verify(pixKeyRepository, never()).save(any(PixKey.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando CPF for inválido")
    void shouldReturnBadRequestWhenInvalidCpf() {
        CreatePixKeyRequestDTO invalidCpfRequest = new CreatePixKeyRequestDTO(1L, "CPF", "123");

        when(accountServiceClient.validateAccountExists(invalidCpfRequest.accountId())).thenReturn(true);

        assertThrows(InvalidPixKeyFormatException.class, () -> {
            pixKeyService.create(invalidCpfRequest);
        });

        verify(accountServiceClient, times(1)).validateAccountExists(1L);
        verify(pixKeyRepository, never()).save(any(PixKey.class));
    }

    @Test
    @DisplayName("Deve deletar chave Pix com soft delete")
    void shouldDeletePixKeyWithSoftDelete() {
        when(pixKeyRepository.findById(1L)).thenReturn(Optional.of(pixKey));
        when(pixKeyRepository.save(pixKey)).thenReturn(pixKey);

        pixKeyService.delete(1L);

        assertFalse(pixKey.getActive());
        verify(pixKeyRepository, times(1)).findById(1L);
        verify(pixKeyRepository, times(1)).save(pixKey);
        verify(cacheService, times(1)).invalidatePixKey(pixKey.getKeyValue());
    }

    @Test
    @DisplayName("Deve validar chave Pix recuperando do cache com sucesso")
    void shouldValidatePixKeyFromCache() {
        when(cacheService.getPixKeyFromCache("12345678901")).thenReturn(Optional.of(1L));
        when(pixKeyRepository.findById(1L)).thenReturn(Optional.of(pixKey));

        ValidatePixKeyResponseDTO result = pixKeyService.validate("12345678901");

        assertNotNull(result);
        assertEquals(1L, result.accountId());
        assertEquals("CPF", result.keyType());
        assertEquals("12345678901", result.keyValue());

        verify(cacheService, times(1)).getPixKeyFromCache("12345678901");
        verify(pixKeyRepository, times(1)).findById(1L);
        verify(pixKeyRepository, never()).findByKeyValueAndActiveTrue("12345678901");
    }

}