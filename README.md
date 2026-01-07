# PUJ_2_Dino_Devedzic
Projekat step by step je rađen na master branchu i tu su vidljive sve izmjene kao i push commitovi funkcionalnosti. Za jednostavniji pristup pushao sam citav projekat na main branch via files koji je isti kao i na master branchu ali svakako sam ga postavio. 

Nakon instanciranja baze PUJ2DB i konektovanja na istu (singleton) fetchaju se podaci iz baze kolekcije users. 
Korisnik se može logovati u sistem ako postoji njegov nalog. Hard coded je jedan admin user iz razloga što sam otkrio bug da se baza neće nikako instancirati ako ne unesemo u nju nešto ručno. 
Ako korisnik nema nalog može ga kreirati u register formi te se registrovati kao admin ili korisnik. Nakon toga se register forme .dispose() te vraća na login formu. 
Nakon logina otvara se MainMenuWindow gdje korisnik dobija welcome message sa njegovim imenom i rolom. Tu se nalaze svi trackeri (finnance tracker, to do, sleep schedule i meal planner) kao i profile window korisnika. 
Funkcionalnosti su raspoređene sa if uslovom odnosno nisu sve funkcionalnosti dostupne korisniku koje su i adminu. 
