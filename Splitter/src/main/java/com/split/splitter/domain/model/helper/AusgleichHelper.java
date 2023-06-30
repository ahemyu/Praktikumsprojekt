package com.split.splitter.domain.model.helper;

import com.split.splitter.helper.Eur;
import com.split.splitter.helper.MoneyHelper;
import java.util.List;
import java.util.Map;
import org.javamoney.moneta.Money;



public class AusgleichHelper {

  public static void gleicheMaxMitMinAus(List<VonZuMitBetrag> fertigerAusgleich,
                                         Map<String, Money> mapFuerMinimal,
                                         PersonMitBetrag min, PersonMitBetrag max) {
    if (min.betrag().abs().isGreaterThan(max.betrag().abs())) {

      fertigerAusgleich.add(new VonZuMitBetrag(min.name(), max.name(), max.betrag()));

      mapFuerMinimal.remove(max.name());

      mapFuerMinimal.put(min.name(), min.betrag().add(max.betrag()));
    }

    if (min.betrag().abs().isLessThan(max.betrag().abs())) {

      fertigerAusgleich.add(new VonZuMitBetrag(min.name(), max.name(), (min.betrag().abs())));

      mapFuerMinimal.remove(min.name());

      mapFuerMinimal.put(max.name(), max.betrag().add(min.betrag()));
    }

    if (min.betrag().abs().isEqualTo(max.betrag().abs())) {

      fertigerAusgleich.add(new VonZuMitBetrag(min.name(), max.name(), min.betrag().abs()));

      mapFuerMinimal.remove(min.name());

      mapFuerMinimal.remove(max.name());
    }
  }

  public static void findePerfektenAusgleichFuerMin(List<PersonMitBetrag> addierteTransaktionen,
                                                    List<VonZuMitBetrag> fertigerAusgleich,
                                                    Map<String, Money> mapFuerMinimal,
                                                    PersonMitBetrag min) {
    if (mapFuerMinimal.size() > 2) {
      for (int j = 2; j < mapFuerMinimal.size(); j++) {
        Money teilBetragVonMin = MoneyHelper.round(min.betrag().divide(j));
        if (mapFuerMinimal.containsValue(teilBetragVonMin.multiply(-1))) {
          long anzahl = addierteTransaktionen.stream().filter(
                  e -> e.betrag().equals(teilBetragVonMin.multiply(-1))).count();
          if (anzahl == j) {
            List<PersonMitBetrag> listeVonAllenBetraegenDieOptimiert =
                    addierteTransaktionen.stream().filter(
                            e -> e.betrag().equals(teilBetragVonMin.multiply(-1))).toList();
            for (long k = 0; k < anzahl; k++) {
              PersonMitBetrag pbObjektVonOptimiert =
                      listeVonAllenBetraegenDieOptimiert.get((int) k);

              fertigerAusgleich.add(new VonZuMitBetrag(min.name(),
                      pbObjektVonOptimiert.name(),
                      teilBetragVonMin.abs()));

              mapFuerMinimal.remove(pbObjektVonOptimiert.name());

              addierteTransaktionen.remove(pbObjektVonOptimiert);

              mapFuerMinimal.put(
                      min.name(), min.betrag().add(
                              pbObjektVonOptimiert.betrag().multiply(k + 1)));

            }

            mapFuerMinimal.remove(min.name());
            addierteTransaktionen.remove(min);

          }
        }
      }
    }
  }

  public static void findePerfektenAusgleichFuerMax(List<PersonMitBetrag> addierteTransaktionen,
                                                    List<VonZuMitBetrag> fertigerAusgleich,
                                                    Map<String, Money> mapFuerMinimal,
                                                    PersonMitBetrag max) {
    if (mapFuerMinimal.size() > 2) {

      for (int j = 2; j < mapFuerMinimal.size(); j++) {

        Money teilBetragVonMax = MoneyHelper.round(max.betrag().divide(j));

        if (mapFuerMinimal.containsValue(teilBetragVonMax.multiply(-1))) {

          long anzahl = addierteTransaktionen.stream().filter(e -> e.betrag().equals(
                  teilBetragVonMax.multiply(-1))).count();

          if (anzahl == j) {

            List<PersonMitBetrag> listeVonAllenBetraegenDieOptimiert =
                    addierteTransaktionen.stream().filter(e -> e.betrag().equals(
                            teilBetragVonMax.multiply(-1))).toList();

            for (long k = 0; k < anzahl; k++) {

              PersonMitBetrag pbObjektVonOptimiert =
                      listeVonAllenBetraegenDieOptimiert.get((int) k);

              fertigerAusgleich.add(
                      new VonZuMitBetrag(
                              pbObjektVonOptimiert.name(), max.name(), teilBetragVonMax));

              mapFuerMinimal.remove(pbObjektVonOptimiert.name());

              addierteTransaktionen.remove(pbObjektVonOptimiert);

              mapFuerMinimal.put(
                      max.name(), max.betrag().add(
                              pbObjektVonOptimiert.betrag().multiply(k + 1)));

            }

            mapFuerMinimal.remove(max.name());
            addierteTransaktionen.remove(max);

          }
        }
      }
    }
  }


  public static boolean minMaxEquals0(PersonMitBetrag min, PersonMitBetrag max) {
    return min.betrag().equals(Eur.of(0)) && max.betrag().equals(Eur.of(0));
  }

}
