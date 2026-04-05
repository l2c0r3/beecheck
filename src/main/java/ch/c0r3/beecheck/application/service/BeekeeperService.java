package ch.c0r3.beecheck.application.service;

import ch.c0r3.beecheck.domain.model.Beekeeper;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@ApplicationScoped
public class BeekeeperService {

    private final List<Beekeeper> beekeepers = new ArrayList<>();

    public void add(Beekeeper beekeeper) {
        beekeepers.add(beekeeper);
    }

    public List<Beekeeper> getBeekeepers() {
        return List.copyOf(beekeepers);
    }

    public List<Beekeeper> queryByFirstNameOrLastName(String text) {
        Predicate<Beekeeper> hasFirstname = b -> b.firstname().contains(text);
        Predicate<Beekeeper> hasLastname = b -> b.lastname().contains(text);

        return beekeepers.stream()
                .filter(hasFirstname.or(hasLastname))
                .toList();

    }
}
