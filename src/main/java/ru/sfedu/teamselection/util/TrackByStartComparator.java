package ru.sfedu.teamselection.util;

import java.util.Comparator;
import ru.sfedu.teamselection.domain.Track;


public class TrackByStartComparator implements Comparator<Track> {
    @Override
    public int compare(Track o1, Track o2) {
        int result = o1.getStartDate().compareTo(o2.getStartDate());
        result = ((-1) * result);
        return result;
    }
}
