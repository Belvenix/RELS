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
