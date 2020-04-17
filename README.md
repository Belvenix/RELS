UPDATE 08/04/2020
Lab 1:
Aplikacja ma nieznaczne ilosci kodu który sprawdza wejcie uzytkownika lecz nie jest to bardzo zaawansowane.
Implementacyjnie jest to bazowy program, który posiada pewne uproszczenia (np. brak serwera SQL).
Dziala na Java13
Punkty zadania pierwszego:<br/>
	* 0. Podczas kompilacji i wykonywania nie wyswietla bledow.<br/>
	* 1. Hermetyzacja wg obiektowego programowania zostala zaimplementowana we wszystkich klasach (oprocz klasy MainApp i Rent).<br/>
	* 2. Definicja dostepu do pol i metod zostaly zaimplementowane we wszystkich klasach oprocz klasy MainApp i Rent.<br/>
		* 3.1 Przeciążenie konstruktora zostało zaimplementowane w klasie Seller oraz Buyer w celu usprawnienia tworzenia podstawowych instancji tych klas.<br/>
		* 3.2 Nadpisanie metody zostało zaimplementowane w klasie Rent w celu pokazanie w inny sposob danego obiektu.<br/>
	* 4. Polimorfizm jest zamprezentowany w metodzie main w klasie MainApp.<br/>
	* 5. Klasą abstrakcyjną jest klasa User (mozemy miec wiecej typow uzytkownikow), dziedzicza z niej dwie klasy jak na razie - Buyer oraz Seller. W przyszłości bedzie możliwosc trzymania uzytkownikow w polu uzytkownicy w klasie Server. Zostanie to prawdopodobnie zaimplementowane w przyszlosci.<br/><br/>
	
W kodzie zostały zaznaczone które punkty spełnia dana klasa za pomocą komentarza np.
w klasie User jest komentarz "// Abstract class for Seller and Buyer class \/5\/"
\/5\/ oznacza punkt piaty. 

---------------------------------------------------------------------------------------

Lab 2:
W aplikacji dodałem wymaganane elementy do laboratorium, czyli:
	1. Zaimplementowałem głębokie klonowanie wraz z wyświetlaną poprawnością (plik Seller.java druga połowa funckji main). Celem byłoby kopiowanie obiektu Seller bez kopiowania listy ofert.
		2.1 Kolekcja z wykorzystaniem interfejsu Comparable, aby ją posortować (plik Seller.java pierwsza połowa funkcji main). Celem byłaby funkcjonalność sortowania najbardziej prosperujących sprzedawców
		2.2 Kolekcja z wykorzystaniem klasy Comparator, aby ją posortować (plik Offer.java main). Celem byłaby funkcjonalność sortowania ofert po między innymi cenie jak i powierzchni
	3. Typ enumeryczny został zaimplementowany w klasie Offer. Celem było zmuszenie użytkownika do użycia tylko danych wartości (zakładamy że innych nie będzie).

W pliku źródłowym znajdują sie komentarze wskazujące na użycie poszczególnych elementów.

---------------------------------------------------------------------------------------

Lab 3:
W aplikacji zaimplementowałem wielowątkowość z której pomocą będę analizować dane, a dokładniej będę tworzył model liniowej regresji,
który będzie na podstawie powierzchni domu przewidywał cenę domu (bardzo podstawowe uczenie maszynowe).
	1. Został stworzony tzw. pipeline (potok), którego celem była analiza danych i stworzenie wcześniej wspomnianych modeli. Zostało to zaimplementowane w klasie StatisticCounter która korzysta z pomocniczej klasy ModelHandler. W StatisticCounter jest zaimplementowana petla która uczy nasz model jak i zlicza czas wykonania. Jako że czas wykonywania był bardzo krótki i pomimo najwiekszych staran nie udało się zrobić dłuższej operacji stwierdziłem że lepiej wtedy uśrednic czas wykonania.
	2. Strefa krytyczna została zaprezentowana w zmiennej OutlierCount do której odwołują się wątki synchronicznie. Ma ona na celu zliczyc ilość domów które kompletnie nie są przewidywane przez model co może sygnalizować że musimy poprawić nasz model.
	3. Raport w osobnym pliku .pdf

---------------------------------------------------------------------------------------

Lab 4:
Aplikacja została ulepszona o możliwość zapisywania plików w wielu formatach - plikach tekstowych, plikach binarnych oraz jako zserializowany obiekt.
Wiekszość elementow została zaimplementowana wewnatrz klasy FileHandler.
	1. Czytanie i pisanie do i z plików tekstowych i binarnych jest w metodach saveCLI, savetxt, savebin, loadtxt, loadbin. Większość nietypowych
	sytuacji podczas zapisywania plikow jest obsługiwana (niestety nie da sie przewidziec wszystkich czynności, które użytkownik może wykonać).
	2. Serializacja i deserializacja została zaimplementowana na obiekcie Offer poprzez dodanie "implements Serializable". Poprawność ładowania i 
	zapisywania została zaprezentowana wewnątrz metod saveser oraz loadser.
	3. Wieloprocesowy dostep do plików został rózwnież zaimplementowany. Dwa procesy nie mogą jednocześnie czytac badz wpisywać do tego samego pliku jednocześnie. Został wykorzystany mechanizm FileLock.tryLock coby wypisywać informacje w linii poleceń. Żeby przetestować aplikację trzeba wpisac
	odpowiednia komende na poczatku programu ('mta'). Aczkolwiek można pominąc pierwszy interfejs uzytkownika - w głównej funkcji nalezy zmienic "platform.runReadFiles();" na "platform.runReadFiles();".

