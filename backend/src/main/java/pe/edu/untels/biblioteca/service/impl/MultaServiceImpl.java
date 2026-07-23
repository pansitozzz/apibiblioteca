package pe.edu.untels.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.untels.biblioteca.dto.response.MultaResponse;
import pe.edu.untels.biblioteca.dto.response.PageResponse;
import pe.edu.untels.biblioteca.entity.Multa;
import pe.edu.untels.biblioteca.entity.Rol;
import pe.edu.untels.biblioteca.entity.Usuario;
import pe.edu.untels.biblioteca.exception.BusinessRuleException;
import pe.edu.untels.biblioteca.exception.ResourceNotFoundException;
import pe.edu.untels.biblioteca.mapper.MultaMapper;
import pe.edu.untels.biblioteca.repository.MultaRepository;
import pe.edu.untels.biblioteca.repository.UsuarioRepository;
import pe.edu.untels.biblioteca.service.MultaService;

@Service
@RequiredArgsConstructor
@Transactional
public class MultaServiceImpl implements MultaService {

    private final MultaRepository multaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MultaMapper multaMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MultaResponse> listar(String emailSolicitante, Pageable pageable) {
        Usuario solicitante = usuarioRepository.findByEmail(emailSolicitante)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + emailSolicitante));

        Page<Multa> pagina = (solicitante.getRol() == Rol.ESTUDIANTE)
                ? multaRepository.findByPrestamoUsuarioId(solicitante.getId(), pageable)
                : multaRepository.findAll(pageable);

        return PageResponse.from(pagina.map(multaMapper::toResponse));
    }

    @Override
    public MultaResponse pagar(Long id) {
        Multa multa = multaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Multa no encontrada con id: " + id));

        if (multa.isPagada()) {
            throw new BusinessRuleException("Esta multa ya fue pagada");
        }

        multa.setPagada(true);
        return multaMapper.toResponse(multaRepository.save(multa));
    }
}
