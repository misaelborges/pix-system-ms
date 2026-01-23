package com.financeiro.pixkey.controller;

import com.financeiro.pixkey.entity.dto.request.CreatePixKeyRequestDTO;
import com.financeiro.pixkey.entity.dto.response.PixKeyResponseDTO;
import com.financeiro.pixkey.entity.dto.response.PixKeyResumoDTO;
import com.financeiro.pixkey.entity.dto.response.ValidatePixKeyResponseDTO;
import com.financeiro.pixkey.service.PixKeyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PixKeyController {

    private final PixKeyService pixKeyService;

    public PixKeyController(PixKeyService pixKeyService) {
        this.pixKeyService = pixKeyService;
    }

    @PostMapping("/pix-keys")
    public ResponseEntity<PixKeyResponseDTO> create(@RequestBody @Valid CreatePixKeyRequestDTO createPixKeyRequestDTO) {
        PixKeyResponseDTO pixKeyResponseDTO = pixKeyService.create(createPixKeyRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pixKeyResponseDTO);
    }

    @GetMapping("/pix-keys/account/{accountId}")
    public ResponseEntity<List<PixKeyResumoDTO>> listByAccountId(@PathVariable Long accountId) {
        List<PixKeyResumoDTO> pixKeyResumoDTOList = pixKeyService.listByAccountId(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(pixKeyResumoDTOList);
    }

    @DeleteMapping("/pix-keys/{pixKeyId}")
    public ResponseEntity<?> delete(@PathVariable Long pixKeyId) {
        pixKeyService.delete(pixKeyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/pix-keys/validate/{pixKeyValue}")
    public ResponseEntity<ValidatePixKeyResponseDTO> validate(@PathVariable String pixKeyValue) {
        ValidatePixKeyResponseDTO validatePixKeyResponseDTO = pixKeyService.validate(pixKeyValue);
        return ResponseEntity.status(HttpStatus.OK).body(validatePixKeyResponseDTO);
    }

    @PostMapping("/pix-keys/internal/validate/{pixKeyValue}")
    public ResponseEntity<Boolean> validateInternal(@PathVariable String pixKeyValue) {
        boolean validatePixKeyExists = pixKeyService.validatePixKeyExists(pixKeyValue);
        return ResponseEntity.status(HttpStatus.OK).body(validatePixKeyExists);
    }

}
