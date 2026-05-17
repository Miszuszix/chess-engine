---
apply: always
---

# Plan Projektu i Postępy: Chess Engine (1000-1400 ELO)

**Legenda Statusów:**
🟢 **DONE** - Zakończone, przetestowane i udokumentowane.
🟡 **IN PROGRESS** - Aktualnie nad tym pracujemy.
🔴 **NOT STARTED** - Czeka na realizację.

---

## Faza 1: Reprezentacja Planszy i Ruchy 🟢 (ZAKOŃCZONO)
*Fundamenty silnika. Stworzenie struktur danych zdolnych do przetrzymywania stanu gry, z zachowaniem ekstremalnej wydajności i bez alokowania pamięci.*

- 🟢 **Słownik stałych:** Plik `Constants.kt` z mapowaniem figur, kolorów i indeksów pól (0-63).
- 🟢 **Bitboard API:** Plik `Bitboard.kt` ze statycznymi, bezpiecznymi i zoptymalizowanymi (ULong) metodami `setBit`, `clearBit`, `getBit`.
- 🟢 **Kodowanie Ruchu (32-bit Int):** Plik `Move.kt` realizujący kompresję ruchu do `Int` z użyciem akumulatora i masek walidacyjnych zamiast `if`-ów.
- 🟢 **Stan Historii (Irreversible State):** Klasa `StateInfo.kt` do przechowywania praw do roszady, ep, licznika 50 ruchów i zbitej figury.
- 🟢 **Główna Klasa Planszy:** Plik `Board.kt` zawierający tablice bitboardów (`pieces`, `colors`), mechanizm zarządzania `ply` oraz funkcje `setPiece` i `removePiece`.

---

## Faza 2: Generator Ruchów "Pseudo-Legal" i Perft 🟡 (W TRAKCIE)
*Magia szachowa. Generowanie wszystkich możliwych ruchów na planszy. Na tym etapie silnik uczy się "jak poruszają się figury".*

- 🟢 **Ataki Skoczka:** Plik `KnightAttacks.kt` - Pre-computed tablica ataków z uwzględnieniem masek chroniących przed "wrap-around".
- 🟢 **Ataki Króla:** Plik `KingAttacks.kt` - Pre-computed tablica ataków z maskami brzegowymi.
- 🟢 **Ataki Pionów:** Pre-computing białych i czarnych ataków na ukos (`PawnAttacks.kt`).
- 🟢 **Figury Liniowe (Ray Casting):** Implementacja promieni dla Wieży, Gońca i Hetmana.
- 🟢 **Funkcja `isSquareAttacked`:** Ocenianie, czy dane pole jest aktualnie atakowane przez określony kolor.
- 🔴 **Generator Ruchów (Główny):** Właściwa logika tworząca listy zakodowanych ruchów (z użyciem obiektu `Move`) dla danej pozycji w oparciu o stan `Board.kt`.
- 🔴 **Mechanizm Make / Unmake:** Funkcje `makeMove` i `unmakeMove` w klasie `Board`, zmieniające stan bitboardów i zarządzające stosem `StateInfo`.
- 🔴 **Weryfikacja PERFT:** Test jednostkowy przechodzący głębokie węzły na pozycjach testowych (wymagane w 100% zgodności ze standardami przed pójściem dalej).

---

## Faza 3: Protokół UCI i Pierwsza Gra 🔴 (NIE ROZPOCZĘTO)
*Komunikacja ze światem zewnętrznym i umożliwienie rozgrywki przez GUI (np. CuteChess).*

- 🔴 **Pętla UCI:** Implementacja nasłuchiwania w konsoli na komendy `uci`, `isready`, `position`, `go`.
- 🔴 **Budowanie z FEN:** Funkcja umożliwiająca zbudowanie stanu `Board` na podstawie tekstowego stringa FEN.
- 🔴 **Podstawowy Minimax:** Szkielet algorytmu przeszukującego w głąb w celu znalezienia jakiegokolwiek logicznego ruchu.
- 🔴 **Kamień Milowy:** Podpięcie skompilowanego Fat JAR pod CuteChess i rozegranie pełnej (nawet bardzo słabej) partii szachowej na silniku.

---

## Faza 4: Ewaluacja i Usprawnienie Przeszukiwania 🔴 (NIE ROZPOCZĘTO)
*Nadanie silnikowi "rozumu" poprzez stworzenie funkcji oceniającej (heurystyki) oraz przyspieszenie szukania ruchów.*

- 🔴 **Ocena Materiału:** Obliczanie bilansu punktowego pozycji.
- 🔴 **Piece-Square Tables (PST):** Tablice nagradzające figury za zajmowanie dobrych pól.
- 🔴 **Rozróżnienie Midgame / Endgame:** Interpolacja wartości tablic w zależności od materiału na planszy.
- 🔴 **Odcięcia Alpha-Beta (Alpha-Beta Pruning):** Dodanie matematyki odcinającej złe warianty w drzewie.
- 🔴 **Sortowanie Ruchów (Move Ordering):** Wdrażanie MVV-LVA (najcenniejsza ofiara, najmniej wartościowy atakujący), co ekstremalnie przyspieszy Alpha-Betę.
- 🔴 **Quiescence Search:** Przeszukiwanie spoczynkowe likwidujące "Efekt Horyzontu" po gwałtownych wymianach.

---

## Faza 5: Optymalizacja Turniejowa i Książki 🔴 (NIE ROZPOCZĘTO)
*Spełnienie ostatecznych wymogów wyciśnięcia maksymalnej wydajności (pod procesor i RAM z zasad turnieju) i szlify końcowe.*

- 🔴 **Iterative Deepening:** Zmiana sterowania czasem - wyszukiwanie na głębokościach 1, 2, 3... dopóki czas pozwala.
- 🔴 **Książka Otwarć Polyglot (.bin):** Parsowanie bajtów książki (nice to have, ale priorytetowe dla startu).
- 🔴 **Transposition Tables (TT):** Wdrożenie kluczy Zobrista (Zobrist Hashing) i zbudowanie tablicy cache ograniczającej użycie do 64-128 MB RAM.
- 🔴 **Clean Code & Review:** Ostatni przegląd architektury przed oddaniem projektu prowadzącemu.