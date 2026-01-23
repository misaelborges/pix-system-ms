package com.financeiro.pixkey.service;

import com.financeiro.pixkey.config.mapper.PixKeyMapper;
import com.financeiro.pixkey.entity.KeyTypeEnum;
import com.financeiro.pixkey.entity.PixKey;
import com.financeiro.pixkey.entity.dto.request.CreatePixKeyRequestDTO;
import com.financeiro.pixkey.entity.dto.response.PixKeyResponseDTO;
import com.financeiro.pixkey.entity.dto.response.PixKeyResumoDTO;
import com.financeiro.pixkey.entity.dto.response.ValidatePixKeyResponseDTO;
import com.financeiro.pixkey.exception.*;
import com.financeiro.pixkey.repository.PixKeyRepository;
import com.financeiro.pixkey.validator.PixKeyValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PixKeyService {

    private final PixKeyValidator validator = new PixKeyValidator();
    private final PixKeyRepository pixKeyRepository;
    private final CacheService cacheService;
    private final AccountServiceClient accountServiceClient;
    private final PixKeyMapper pixKeyMapper;

    public PixKeyService(PixKeyRepository pixKeyRepository, CacheService cacheService, AccountServiceClient accountServiceClient,
                         PixKeyMapper pixKeyMapper) {

        this.pixKeyRepository = pixKeyRepository;
        this.cacheService = cacheService;
        this.accountServiceClient = accountServiceClient;
        this.pixKeyMapper = pixKeyMapper;
    }

    @Transactional
    public PixKeyResponseDTO create(CreatePixKeyRequestDTO request) {
        findAccount(request.accountId());

        validatePixKeyFormat(request.keyType(), request.keyValue());

        if (pixKeyRepository.countByAccountIdAndActiveTrue(request.accountId()) >= 5) {
            throw new MaxPixKeysLimitException("Já existe 5 chaves pix cadastrada nessa conta");
        }

        KeyTypeEnum keyTypeEnum = KeyTypeEnum.valueOf(request.keyType().toUpperCase());  // ← Converte aqui

        if (pixKeyRepository.existsByAccountIdAndKeyTypeAndActiveTrue(request.accountId(), keyTypeEnum)) {
            throw new PixKeyAlreadyExistsException("Já existe esse tipo de chave cadastrado na conta");
        }

        String keyValue = request.keyValue();
        if (KeyTypeEnum.RANDOM.equals(keyTypeEnum)) {
            keyValue = generatePixKeyRandom().toString();
        }

        PixKey pixKey = new PixKey();
        pixKey.setAccountId(request.accountId());
        pixKey.setKeyType(keyTypeEnum);
        pixKey.setKeyValue(keyValue);
        pixKey.setActive(true);

        pixKey = pixKeyRepository.save(pixKey);

        cacheService.cachePixKey(pixKey.getKeyValue(), pixKey.getAccountId());

        return pixKeyMapper.toResponseDTO(pixKey);
    }

    public List<PixKeyResumoDTO> listByAccountId(Long accountId) {
        findAccount(accountId);
        List<PixKey> pixKeyList = pixKeyRepository.findByAccountIdAndActiveTrue(accountId);
        return pixKeyMapper.toListResumoDTO(pixKeyList);
    }

    @Transactional
    public void delete(Long pixKeyId) {
        PixKey pixKey = pixKeyRepository.findById(pixKeyId)
                .orElseThrow(() -> new PixKeyNotFoundException("Chave PIX não existe."));

        pixKey.setActive(false);
        pixKeyRepository.save(pixKey);
        cacheService.invalidatePixKey(pixKey.getKeyValue());
    }

    public ValidatePixKeyResponseDTO validate(String pixKeyValue) {
        Optional<Long> pixKeyFromCache = cacheService.getPixKeyFromCache(pixKeyValue);
        if (pixKeyFromCache.isPresent()) {
            Optional<PixKey> pixKey = pixKeyRepository.findById(pixKeyFromCache.get());
            if (pixKey.isPresent() && pixKey.get().getActive()) {
                return new ValidatePixKeyResponseDTO(
                        pixKey.get().getAccountId(),
                        pixKey.get().getKeyType().getType(),
                        pixKey.get().getKeyValue());
            }
        }

        PixKey pixKey = pixKeyRepository.findByKeyValueAndActiveTrue(pixKeyValue)
                .orElseThrow(() -> new PixKeyNotFoundException("Chave Pix não existe"));

        cacheService.cachePixKey(pixKey.getKeyValue(), pixKey.getAccountId());

        return new ValidatePixKeyResponseDTO(
                pixKey.getAccountId(),
                pixKey.getKeyType().getType(),
                pixKey.getKeyValue());
    }

    public boolean validatePixKeyExists(String pixKeyValue) {
        Optional<PixKey> pixKey = pixKeyRepository.findByKeyValueAndActiveTrue(pixKeyValue);
        return pixKey.isPresent();
    }

    private void findAccount(Long accountId)  {
        if (!accountServiceClient.validateAccountExists(accountId)) {
            throw new AccountNotFoundException("Não existe uma conta cadastrada com esse id");
        }
    }

    private void validatePixKeyFormat(String keyType, String keyValue) {
        String normalizedKeyType = keyType.toUpperCase();  // ← Adiciona isso
        switch (normalizedKeyType) {
            case "CPF" -> validator.validateCpf(keyValue);
            case "CNPJ" -> validator.validateCnpj(keyValue);
            case "EMAIL" -> validator.validateEmail(keyValue);
            case "PHONE" -> validator.validatePhone(keyValue);
            case "RANDOM" -> {}
            default -> throw new InvalidPixKeyFormatException("Tipo de chave inválido");
        }
    }

    private UUID generatePixKeyRandom() {
        return UUID.randomUUID();
    }
}
