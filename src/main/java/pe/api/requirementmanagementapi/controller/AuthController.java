package pe.api.requirementmanagementapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.api.requirementmanagementapi.dto.request.LoginRequest;
import pe.api.requirementmanagementapi.dto.response.AuthResponse;
import pe.api.requirementmanagementapi.dto.response.UsuarioResponse;
import pe.api.requirementmanagementapi.model.Usuario;
import pe.api.requirementmanagementapi.repository.UsuarioRepository;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(request.getUsername());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // En este momento las contraseñas están en texto plano (TODO: BCrypt)
        if (!usuario.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }
        
        if (!usuario.getActivo()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario inactivo");
        }

        // Simulación de generación de token JWT temporal para que el frontend pueda avanzar
        String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI" + usuario.getUsername() + "In0.mockSignature123";
        
        AuthResponse response = AuthResponse.builder()
                .token(mockToken)
                .expiresIn(3600000) // 1 hora
                .user(UsuarioResponse.fromEntity(usuario))
                .build();

        return ResponseEntity.ok(response);
    }
}
