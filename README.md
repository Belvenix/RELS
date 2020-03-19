Aplikacja ma nieznaczne ilosci kodu który sprawdza wejcie uzytkownika lecz nie jest to bardzo zaawansowane.
Implementacyjnie jest to bazowy program, który posiada pewne uproszczenia (np. brak serwera SQL).
Dziala na Javie 13.
Punkty zadania pierwszego:
	0. Podczas kompilacji i wykonywania nie wyswietla bledow.
	1. Hermetyzacja wg obiektowego programowania zostala zaimplementowana we wszystkich klasach (oprocz klasy MainApp).
	2. Definicja dostepu do pol i metod zostaly zaimplementowane we wszystkich klasach oprocz klasy MainApp.
	3.1 Przeciążenie konstruktora zostało zaimplementowane w klasie Seller oraz Buyer w celu usprawnienia tworzenia podstawowych instancji tych klas.
	3.2 Nadpisanie metody zostało zaimplementowane w klasie Rent w celu pokazanie w inny sposob danego obiektu.
	4. Polimorfizm jest zamprezentowany w metodzie main w klasie MainApp.
	5. Klasą abstrakcyjną jest klasa User (mozemy miec wiecej typow uzytkownikow),
	dziedzicza z niej dwie klasy jak na razie - Buyer oraz Seller. W przyszłości bedzie możliwosc trzymania uzytkownikow w polu uzytkownicy w klasie Server. Zostanie to prawdopodobnie zaimplementowane w przyszlosci.