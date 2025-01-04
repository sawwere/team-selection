package ru.sfedu.teamselection.service;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.repository.TrackRepository;


@RequiredArgsConstructor
@Service
public class TrackService {

    private final TrackRepository trackRepository;


    /**
     * Find Team entity by id
     * @param id track id
     * @return entity with given id
     * @throws NoSuchElementException in case there is no track with such id
     */
    public Track findByIdOrElseThrow(Long id) throws NoSuchElementException {
        return trackRepository.findById(id).orElseThrow();
    }

    public List<Track> findAllByType(String type) {
        throw new NotImplementedException();
    }

    //TODO deleteById

    //TODO create



}
