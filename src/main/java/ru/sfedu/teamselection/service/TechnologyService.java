package ru.sfedu.teamselection.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.repository.TechnologyRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TechnologyService {

    private final TechnologyRepository technologyRepository;
    private final TechnologyMapper technologyMapper;

    /**
     * Получить все технологии.
     */
    @Transactional(readOnly = true)
    public List<TechnologyDto> findAll() {
        List<Technology> technologies = technologyRepository.findAll();
        return technologyMapper.mapListToDto(technologies);
    }

    /**
     * Создать новую технологию.
     */
    @Transactional
    public TechnologyDto create(TechnologyDto dto) {
        Technology entity = technologyMapper.mapToEntity(dto);
        Technology saved = technologyRepository.save(entity);
        return technologyMapper.mapToDto(saved);
    }

    /**
     * Удалить технологию по идентификатору.
     * @throws NotFoundException если технология не найдена.
     */
    @Transactional
    public void delete(Long id) {
        Optional<Technology> technologyOpt = technologyRepository.findById(id);
        if (!technologyOpt.isPresent()) {
            throw new NotFoundException("Технология c id=" + id + " не найдена");
        }
        Technology technology = technologyOpt.get();
        technology.getStudents().clear();
        technologyRepository.save(technology);
        technologyRepository.deleteById(id);
    }
}
