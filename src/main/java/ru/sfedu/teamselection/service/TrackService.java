package ru.sfedu.teamselection.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.Track;


@RequiredArgsConstructor
@Service
public class TrackService {

    public List<Track> findAllByType(String type) {
        throw new NotImplementedException();
    }
}
