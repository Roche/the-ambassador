package pl.filipowm.innersource.ambassador.model.stats;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.stream.Collectors;

class TimeMap {


    public static void main(String[] args) {
        var m = new HashMap<LocalDate, Integer>();
        /*
        czemu sprzedaje? kto byl pierwszym wlascicielem?
        kiedy wymiana turbo i regeneracja egr?
        co jest na wyposazeniu auta?

        gdzie auto bylo serwisowane?

        MDoradca
        co bylo naprawiane w samochodzie wedlug pana wiedzy?
        co bylo wymieniane? jaki oleej byl lany?
        co nalezy w najblizszym czasie zrobic / wymienic w samochodzie?

        jaki jest przebieg? czy wpisze Pan to w umowe?
        czy nie bedzie problemu jak pojedziemy z autem na serwis?

        czy nie bedzie problemu jak przyjade z mechanikiem?

        NIE --> jaka jest forma sprzedazy?
        czy moge prosic o dodatkowe zdjecia wraz z usszkodzeniami i dwiema szkodami parkingowymi?

        drugi wlasciciel - w kraju, za granca?
        jak dlugo jest pan wlascicielem samochodu?
        70k 2.5 roku


        czy posiada ksiazke serwisowa?
        autospecjal serwis
         */
        m.put(LocalDate.now(), 2);
        m.put(LocalDate.now().minusDays(1), 7);
        m.put(LocalDate.now().minusDays(13), 3);
        m.put(LocalDate.now().minusDays(26), 5);
        m.put(LocalDate.now().minusDays(29), 9);
        m.put(LocalDate.now().minusDays(100), 7);
        m.put(LocalDate.now().minusDays(101), 7);

        System.out.println(m);

        var z = m.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        it -> it.getKey().with(DayOfWeek.MONDAY),
//                        it ->  it.getKey().withDayOfMonth(1),
                        it -> it.getValue(),
                        ( a, b ) -> a + b
                ));
        System.out.println(z);
    }
}
